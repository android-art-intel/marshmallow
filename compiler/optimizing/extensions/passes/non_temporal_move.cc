/*
 * Copyright (C) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "non_temporal_move.h"

#include "ext_utility.h"
#include "graph_x86.h"
#include "induction_variable.h"
#include "loop_formation.h"
#include "loop_iterators.h"

namespace art {

void HNonTemporalMove::Run() {
  PRINT_PASS_OSTREAM_MESSAGE(this, "Begin: " << GetMethodName(c_unit_));
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86* graph_loop_info = graph->GetLoopInformation();

  // Walk all the inner loops in the graph.
  bool graph_updated = false;
  for (HOnlyInnerLoopIterator it(graph_loop_info); !it.Done(); it.Advance()) {
    HLoopInformation_X86* loop_info = it.Current();
    PRINT_PASS_OSTREAM_MESSAGE(this, "Visit " << loop_info->GetHeader()->GetBlockId());
    ArraySets array_sets;
    if (!Gate(loop_info, array_sets)) {
      // Debug message printed in Gate().
      continue;
    }

    // Mark all the ArraySets as 'non_temporal_move'.
    DCHECK_GT(array_sets.size(), 0u);
    for (auto array_set : array_sets) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Add non-temporal to " << array_set);
      array_set->SetUseNonTemporalMove();
    }
    MaybeRecordStat(MethodCompilationStat::kIntelNonTemporalMove, array_sets.size());

    // Add the needed barrier to the exit.
    HBasicBlock* exit_block = loop_info->GetExitBlock();
    CHECK(exit_block != nullptr);
    HMemoryBarrier* mb = new(graph->GetArena()) HMemoryBarrier(MemBarrierKind::kAnyAny);
    PRINT_PASS_OSTREAM_MESSAGE(this, "Add memory barrier to exit block");
    exit_block->InsertInstructionBefore(mb, exit_block->GetFirstInstruction());

    // Add the needed barrier to suspend block if needed.
    if (loop_info->HasSuspendCheck() || loop_info->HasTestSuspend()) {
      mb = new(graph->GetArena()) HMemoryBarrier(MemBarrierKind::kAnyAny);
      PRINT_PASS_OSTREAM_MESSAGE(this, "Add memory barrier to suspend block");
      graph_updated |= loop_info->InsertInstructionInSuspendBlock(mb);
    }
  }

  if (graph_updated) {
    // Only rebuild the dominators if we added a new node for a suspend.
    graph->RebuildDomination();
  }
}

bool HNonTemporalMove::Gate(HLoopInformation_X86* loop_info, ArraySets& array_sets) {
  // This must be a countable loop.
  if (!loop_info->HasKnownNumIterations()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop is not countable");
    return false;
  }

  // The number of iterations must be large enough.
  if (loop_info->GetNumIterations(loop_info->GetHeader()) < kMinNonTemporalIterations) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop has " << loop_info->GetNumIterations(loop_info->GetHeader())
                                     << " iterations; needs at least "
                                     << kMinNonTemporalIterations);
    return false;
  }

  // The loop must be a simple count up loop.
  const HLoopBoundInformation& bound_info = loop_info->GetBoundInformation();
  if (!bound_info.is_simple_count_up_) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop is not a simple count up loop");
    return false;
  }

  // The IV increment must be 1.
  HInductionVariable* iv = bound_info.loop_biv_;
  DCHECK(iv != nullptr);
  if (!iv->IsBasicAndIncrementOf1()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Not a basic IV with increment 1");
    return false;
  }

  // The IV increment must not be FP.
  if (iv->IsFP()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "IV increment is FP");
    return false;
  }

  HPhi* iv_variable = iv->GetPhiInsn();
  DCHECK(iv_variable != nullptr);
  PRINT_PASS_OSTREAM_MESSAGE(this, "IV is " << iv_variable);

  // Walk through the blocks in the loop.
  // - There must be no array gets in the loop.
  // - No side exits from the loop.
  for (HBlocksInLoopIterator it_loop(*loop_info); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* loop_block = it_loop.Current();
    for (HInstructionIterator inst_it(loop_block->GetInstructions());
         !inst_it.Done();
         inst_it.Advance()) {
      HInstruction* instruction = inst_it.Current();
      PRINT_PASS_OSTREAM_MESSAGE(this, "Look at: " << instruction);

      // - There must be no array gets in the loop.
      if (instruction->IsArrayGet()) {
        PRINT_PASS_OSTREAM_MESSAGE(this, "Loop contains an array get");
        return false;
      }

      // - No side exits from the loop.
      if (instruction->HasEnvironment()) {
        switch (instruction->GetKind()) {
          case HInstruction::kSuspendCheck:
          case HInstruction::kSuspend:
            // We can ignore these, as we will add a fence if needed.
            break;
          case HInstruction::kArraySet:
            if (instruction->AsArraySet()->NeedsTypeCheck()) {
              // Code generator will generate a runtime call, so no chance
              // to generate non-temporal move.
              PRINT_PASS_OSTREAM_MESSAGE(this, "Array set may side exit loop");
              return false;
            }
            break;
          default:
            PRINT_PASS_OSTREAM_MESSAGE(this, "Loop may side exit loop");
            return false;
        }
      }

      // Remember any ArraySets that have the IV as the index.
      HArraySet* array_set = instruction->AsArraySet();
      if (array_set != nullptr) {
        // Is the array index the loop IV?
        if (array_set->InputAt(1) != iv_variable) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "ArraySet index is not IV");
          return false;
        }

        // Is the array set a supported x86 movnti type?
        Primitive::Type set_type = array_set->GetComponentType();
        const char *type_name = nullptr;
        switch (set_type) {
          case Primitive::kPrimInt:
            type_name = "int";
            break;
          case Primitive::kPrimLong:
            type_name = "long";
            break;
          case Primitive::kPrimNot:
            type_name = "object";
            break;
          default:
            PRINT_PASS_OSTREAM_MESSAGE(this, "ArraySet type is not int/long/object");
            return false;
          }

        PRINT_PASS_OSTREAM_MESSAGE(this, "Mark NonTemporal ArraySet (" << type_name
                                         << ") " << array_set);
        array_sets.insert(array_set);
      }
    }
  }

  // Everything is copacetic.  Was there anything found?
  return array_sets.size() > 0;
}

}  // namespace art
