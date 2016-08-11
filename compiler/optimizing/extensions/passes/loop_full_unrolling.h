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

#ifndef ART_COMPILER_OPTIMIZING_EXTENSIONS_PASSES_LOOP_FULL_UNROLLING_H_
#define ART_COMPILER_OPTIMIZING_EXTENSIONS_PASSES_LOOP_FULL_UNROLLING_H_

#include "nodes.h"
#include "optimization_x86.h"

namespace art {

// Forward declaration.
class HLoopUnrolling;

/**
 * @brief Full Unrolling is an optimization pass which copies the loop body a given amount
 * of times in order to remove the loop. It aims at optimizing the generated code by removing
 * the cost of the loop structure.
 */
class HLoopFullUnrolling : public HOptimization_X86 {
 public:
  HLoopFullUnrolling(HGraph* graph, OptimizingCompilerStats* stats = nullptr)
    : HOptimization_X86(graph, true, kHLoopFullUnrollingPassName, stats) {
      // This default value is legacy from L. Is it still the best value now?
      DefineOption("MaxInstructionsUnrolled", OptionContent(60));
      DefineOption("Enabled", OptionContent(0));
    }

  void Run() OVERRIDE;

 private:
  bool Gate(HLoopUnrolling* loop_unrolling) const;

  static constexpr const char* kHLoopFullUnrollingPassName = "loop_full_unrolling";
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_EXTENSIONS_PASSES_LOOP_FULL_UNROLLING_H_
