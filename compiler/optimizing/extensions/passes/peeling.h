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

#ifndef ART_COMPILER_OPTIMIZING_EXTENSIONS_PASSES_PEELING_H_
#define ART_COMPILER_OPTIMIZING_EXTENSIONS_PASSES_PEELING_H_

#include "nodes.h"
#include "optimization_x86.h"

namespace art {

/**
 * @brief This is an optimization pass that peels some loops.
 * @details The peeling applies always to the loops on which the heuristics pass.
 */
class HLoopPeeling : public HOptimization_X86 {
 public:
  explicit HLoopPeeling(HGraph* graph, OptimizingCompilerStats* stats = nullptr)
    : HOptimization_X86(graph, true, "loop_peeling", stats) {}

  void Run() OVERRIDE;

  bool ShouldPeel(HLoopInformation_X86* loop);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_EXTENSIONS_PASSES_PEELING_H_
