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

#include "cloning.h"
#include "ext_utility.h"
#include "graph_x86.h"
#include "loop_iterators.h"
#include "loop_full_unrolling.h"
#include "loop_unrolling.h"

namespace art {

void HLoopFullUnrolling::Run() {
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86* loop_start = graph->GetLoopInformation();
  bool graph_updated = false;
  PRINT_PASS_OSTREAM_MESSAGE(this, "Begin " << GetMethodName(graph));

  // For each inner loop in the graph, we will try to apply Loop Unrolling.
  for (HOnlyInnerLoopIterator it(loop_start); !it.Done(); it.Advance()) {
    HLoopInformation_X86* loop = it.Current();
    HLoopUnrolling loop_unrolling(loop, this);
    HBasicBlock* loop_header = loop->GetHeader();
    uint64_t num_iterations = loop->GetNumIterations(loop_header);

    if (!Gate(&loop_unrolling)) {
      continue;
    }

    if (!loop_unrolling.FullUnroll()) {
      continue;
    }

    MaybeRecordStat(MethodCompilationStat::kIntelLoopFullyUnrolled);
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop #" << loop_header->GetBlockId()
      << " of method " << GetMethodName(graph)
      << " has been successfully fully unrolled by factor "
      << num_iterations);
  }

  if (graph_updated) {
    graph->RebuildDomination();
  }

  PRINT_PASS_OSTREAM_MESSAGE(this, "End " << GetMethodName(graph));
}

bool HLoopFullUnrolling::Gate(HLoopUnrolling* loop_unrolling) const {
  DCHECK(loop_unrolling != nullptr);

  // Get the user option for max unrolled instructions limit.
  uint64_t max_unrolled_instructions = GetOption("MaxInstructionsUnrolled").AsInt();
  bool pass_enabled = (GetOption("Enabled").AsInt() == 1);

  if (pass_enabled == false) {
    return false;
  }

  if (!loop_unrolling->Gate(max_unrolled_instructions)) {
    return false;
  }

  return true;
}

}  // namespace art
