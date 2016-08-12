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
#include "peeling.h"

namespace art {

static constexpr size_t kLeastRequiredCandidateCount = 1u;

bool HLoopPeeling::ShouldPeel(HLoopInformation_X86* loop) {
  DCHECK(loop->IsInner());

  size_t num_candidate_instr = 0u;
  if (loop->GetBlocks().NumSetBits() <= 2u) {
    // Check to see if peeling may be worth it - these are cases where GVN may do a better job
    // by eliminating throwers/getters inside of a loop.
    for (HBlocksInLoopIterator it_loop(*loop); !it_loop.Done(); it_loop.Advance()) {
      HBasicBlock* block = it_loop.Current();
      for (HInstruction* instruction = block->GetFirstInstruction();
          instruction != nullptr;
          instruction = instruction->GetNext()) {
        // Check if thrower or heap mutator/reader first.
        // We also handle LoadString and LoadClass specially because they may not fall
        // in either category but in reality are useful to hoist since they have no
        // IR inputs and will reload same thing over and over.
        if (instruction->CanThrow() || instruction->HasSideEffects() ||
            instruction->GetSideEffects().HasDependencies() ||
            instruction->IsLoadClass() || instruction->IsLoadString()) {
          bool all_inputs_from_outside = true;

          // Now check that all inputs are from outside.
          for (size_t i = 0, e = instruction->InputCount(); i < e; ++i) {
            HInstruction* input = instruction->InputAt(i);
            if (loop->Contains(*input->GetBlock())) {
              all_inputs_from_outside = false;
              break;
            }
          }

          // If all inputs from outside, count that we found a candidate which makes
          // peeling worth it.
          if (all_inputs_from_outside) {
            num_candidate_instr++;
            if (num_candidate_instr >= kLeastRequiredCandidateCount) {
              return true;
            }
          }
        }
      }
    }
  }

  return false;
}

void HLoopPeeling::Run() {
  HOnlyInnerLoopIterator inner_iter(GRAPH_TO_GRAPH_X86(graph_)->GetLoopInformation());
  PRINT_PASS_OSTREAM_MESSAGE(this, "Start " << GetMethodName(graph_));
  while (!inner_iter.Done()) {
    HLoopInformation_X86* inner_loop = inner_iter.Current();
    // If the loop should be peeled, make an attempt to do it.
    if (ShouldPeel(inner_loop)) {
      if (inner_loop->IsPeelable(this)) {
        inner_loop->PeelHead(this);
        MaybeRecordStat(MethodCompilationStat::kIntelLoopPeeled);
        PRINT_PASS_OSTREAM_MESSAGE(this, "Successfully peeled loop with header block " <<
                                         inner_loop->GetHeader()->GetBlockId() << '.');
      }
    }
    inner_iter.Advance();
  }
  PRINT_PASS_OSTREAM_MESSAGE(this, "End " << GetMethodName(graph_));
}

}  // namespace art
