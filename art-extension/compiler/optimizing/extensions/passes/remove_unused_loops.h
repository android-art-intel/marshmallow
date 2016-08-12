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

#ifndef ART_COMPILER_OPTIMIZING_REMOVE_UNUSED_LOOPS_H_
#define ART_COMPILER_OPTIMIZING_REMOVE_UNUSED_LOOPS_H_

#include "nodes.h"
#include "optimization_x86.h"

namespace art {

class HRemoveUnusedLoops : public HOptimization_X86 {
 public:
  explicit HRemoveUnusedLoops(HGraph* graph,
                              OptimizingCompilerStats* stats = nullptr)
      : HOptimization_X86(graph, true, kRemoveUnusedLoopsPassName, stats) {}

  void Run() OVERRIDE;

 private:
  static constexpr const char* kRemoveUnusedLoopsPassName = "remove_unused_loops";

  bool CheckInstructionsInBlock(HLoopInformation_X86* loop_info, HBasicBlock* loop_block);
  bool CheckPhisInBlock(HLoopInformation_X86* loop_info, HBasicBlock* loop_block);
  void RemoveLoop(HLoopInformation_X86* loop_info,
                  HBasicBlock*pre_header,
                  HBasicBlock* exit_block);
  void UpdateExternalPhis();

  std::set<HPhi*> external_loop_phis_;

  DISALLOW_COPY_AND_ASSIGN(HRemoveUnusedLoops);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_REMOVE_UNUSED_LOOPS_H_
