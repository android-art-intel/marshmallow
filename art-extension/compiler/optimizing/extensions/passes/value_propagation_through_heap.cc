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

#include "ext_utility.h"
#include "graph_x86.h"
#include "loop_iterators.h"
#include "loop_formation.h"
#include "value_propagation_through_heap.h"
#include <set>

namespace art {

static bool IsNotSafeForVPThroughHeap(HInstruction* insn) {
  if (insn->IsInvoke()) {
    return true;
  } else if (HInstanceFieldGet* ifg = insn->AsInstanceFieldGet()) {
    return ifg->IsVolatile();
  } else if (HInstanceFieldSet* ifs = insn->AsInstanceFieldSet()) {
    return ifs->IsVolatile();
  } else if (HStaticFieldGet* sfg = insn->AsStaticFieldGet()) {
    return sfg->IsVolatile();
  } else if (HStaticFieldSet* sfs = insn->AsStaticFieldSet()) {
    return sfs->IsVolatile();
  } else if (insn->IsMonitorOperation()) {
    return true;
  } else {
    return false;
  }
}

bool HValuePropagationThroughHeap::GetCandidateSetters(HLoopInformation_X86* loop,
                                      std::set<HInstruction*>& setters_set) {
  DCHECK(loop != nullptr);

  HBasicBlock* preheader = loop->GetPreHeader();
  DCHECK(preheader != nullptr);

  // Go through each instruction in the preheader in reverse order to simplify the checks.
  for (HBackwardInstructionIterator it(preheader->GetInstructions()); !it.Done(); it.Advance()) {
    HInstruction* inst = it.Current();

    if (IsNotSafeForVPThroughHeap(inst)) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Found not safe instruction " << inst <<
                                 " and stop setter search");
      break;
    }

    // If instruction is setter.
    HInstruction::InstructionKind inst_type = inst->GetKind();
    if (inst_type == HInstruction::kInstanceFieldSet ||
        inst_type == HInstruction::kStaticFieldSet) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Found candidate setter " << inst);
      // We must ensure that there is no already found setter to the same location.
      bool is_good = true;
      for (auto setter : setters_set) {
        AliasCheck::AliasKind alias_kind = alias_.Alias(setter, inst);
        if (alias_kind != AliasCheck::kNoAlias) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "Found the similar setter " << setter
                                     << " after us");
          is_good = false;
          break;
        }
      }
      if (is_good) {
        setters_set.insert(inst);
      }
    }
  }
  return !setters_set.empty();
}


void HValuePropagationThroughHeap::PropagateValueToGetters(HLoopInformation_X86* loop,
                                                  std::set<HInstruction*> setters_set) {
  std::set<HInstruction*> loop_getters;
  std::set<HInstruction*> loop_setters;

  // Walk through each insns in the loop.
  for (HBlocksInLoopIterator it(*loop); !it.Done(); it.Advance()) {
    HBasicBlock* current_block = it.Current();
    for (HInstructionIterator insn_it(current_block->GetInstructions());
         !insn_it.Done();
         insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();
      HInstruction::InstructionKind insn_type = insn->GetKind();

      // Record setter, getters or the instruction which has side effects.
      if (insn_type == HInstruction::kInstanceFieldSet ||
          insn_type == HInstruction::kStaticFieldSet ||
          alias_.HasSideEffects(insn)) {
        loop_setters.insert(insn);
      } else if (insn_type == HInstruction::kInstanceFieldGet ||
                 insn_type == HInstruction::kStaticFieldGet) {
        loop_getters.insert(insn);
      }
    }
  }

  for (auto setter : setters_set) {
    bool is_noalias_setter = true;
    std::set<HInstruction*> candidate_getters;

    for (auto loop_setter : loop_setters) {
      // Get the aliasing of the two instructions.
      AliasCheck::AliasKind alias_kind = alias_.Alias(setter, loop_setter);

      // If current candidate setter is aliased with loop setter,
      // we should not propagate the value since the heap content might change.
      if (alias_kind != AliasCheck::kNoAlias) {
          is_noalias_setter = false;
          PRINT_PASS_OSTREAM_MESSAGE(this, "Skipping candidiate setter " << setter <<
                "due to it alias with setter or side effects instruction in loop " << loop_setter);
          break;
      }
    }

    // if it is valid to propagate the constant in the loop.
    if (is_noalias_setter) {
      for (auto loop_getter : loop_getters) {
        AliasCheck::AliasKind alias_kind = alias_.Alias(setter, loop_getter);

        // If the loop getter must alias with the candidate setter,
        // we found the corresponding getter for the candidate setter.
        if (alias_kind == AliasCheck::kMustAlias) {
          candidate_getters.insert(loop_getter);
        }
      }

      // Remove the getter and replace the uses of the getter with setter's constant or invariant.
      HInstruction* value = setter->InputAt(1);
      for (auto candidate_getter : candidate_getters) {
        candidate_getter->ReplaceWith(value);
        candidate_getter->GetBlock()->RemoveInstruction(candidate_getter);
        PRINT_PASS_OSTREAM_MESSAGE(this, "Successfully replaced use of getter " << candidate_getter <<
                          " with value " << value << " stored in setter " << setter);
      }
      MaybeRecordStat(MethodCompilationStat::kIntelValuePropagationThroughHeap,
                      candidate_getters.size());
    }
  }
}

bool HValuePropagationThroughHeap::Gate(HLoopInformation_X86* loop) const {
  DCHECK(loop->GetPreHeader() != nullptr);

  // To save the compilation time, limit the loop basic blocks.
  if (loop->NumberOfBlocks() > kMaximumBasicBlockNumbers) {
    return false;
  }
  // For the safety of memory operations, do not allow invokes, volatile or monitor
  // instructions in the loop.
  for (HBlocksInLoopIterator it(*loop); !it.Done(); it.Advance()) {
    HBasicBlock* current_block = it.Current();
    for (HInstructionIterator insn_it(current_block->GetInstructions());
         !insn_it.Done();
         insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();
      if (IsNotSafeForVPThroughHeap(insn)) {
        return false;
      }
    }
  }
  return true;
}

void HValuePropagationThroughHeap::Run() {
  PRINT_PASS_OSTREAM_MESSAGE(this, "Try to optimize : " << GetMethodName(graph_));
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86* loop_info = graph->GetLoopInformation();

  // Walk through loops from outer to inner.
  for (HOutToInLoopIterator it_loop(loop_info); !it_loop.Done(); it_loop.Advance()) {
    HLoopInformation_X86* loop = it_loop.Current();
    PRINT_PASS_OSTREAM_MESSAGE(this, "For loop at " << loop->GetHeader()->GetBlockId());

    // Check if the loop pass the gate.
    if (!Gate(loop)) {
      continue;
    }
    std::set<HInstruction*> setters_set;

    // If we find any valid invariant value in the stores, propagate the value to the use of getters.
    if (GetCandidateSetters(loop, setters_set)) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Found " << setters_set.size() << " invariant values in setters");
      PropagateValueToGetters(loop, setters_set);
    }
  }
}

}  // namespace art
