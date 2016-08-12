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

#include <queue>

#include "graph_x86.h"
#include "loop_iterators.h"
#include "loop_formation.h"

namespace art {


/**
 * @class CompareLoops
 * @brief A comparison between loops.
 */
class HCompareLoops {
 public:
  /**
   * @brief Compare both loops.
   * @param l1 the first loop.
   * @param l2 the second loop.
   * @return does l1 contain less number of BasicBlocks than l2?
   */
  bool operator()(HLoopInformation_X86* l1, HLoopInformation_X86* l2) {
    return l1->NumberOfBlocks() < l2->NumberOfBlocks();
  }
};

void HLoopFormation::Run() {
  /**
   * The algorithm of this method is to actually create all the LoopInformation for the loops of the method.
   *  Put the loops in a priority queue that will have everything sorted in an inverse way:
   *    ie. the loops with the most BBs are first.
   *  This means that when adding the loops:
   *    They are either sibling or nested, they cannot contain a previously added loop.
   */
  std::priority_queue<HLoopInformation_X86*, std::vector<HLoopInformation_X86*>, HCompareLoops> queue;
  std::set<HLoopInformation_X86*> info_set;

  // Post order visit to visit inner loops before outer loops.
  GRAPH_TO_GRAPH_X86(graph_)->ClearLoopInformation();
  for (HPostOrderIterator it(*graph_); !it.Done(); it.Advance()) {
    HBasicBlock* block = it.Current();
    if (block->IsLoopHeader()) {
      HLoopInformation_X86* info = LOOPINFO_TO_LOOPINFO_X86(block->GetLoopInformation());

      // If we do not have it, add it.
      if (info_set.find(info) == info_set.end()) {
        queue.push(info);
        info_set.insert(info);
      }
    }
  }

  // Now clear data structures to make way for initialization.
  HGraph_X86* graph_x86 = GRAPH_TO_GRAPH_X86(graph_);
  graph_x86->ClearLoopInformation();
  for (auto loop : info_set) {
    loop->ResetRelationships();
  }

  // Now add them together.
  while (queue.empty() == false) {
    HLoopInformation_X86* info = queue.top();
    graph_x86->AddLoopInformation(info);
    queue.pop();
  }

  // Finally, now that we are done, set the depths right.
  HLoopInformation_X86* outer = graph_x86->GetLoopInformation();
  if (outer != nullptr) {
    outer->SetDepth(0);
  }
}

}  // namespace art

