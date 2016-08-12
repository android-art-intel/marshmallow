/*
 * Copyright (C) 2015 The Android Open Source Project
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
 *
 * Modified by Intel Corporation
 *
 */

#ifndef ART_COMPILER_OPTIMIZING_GENERATE_SELECTS_H_
#define ART_COMPILER_OPTIMIZING_GENERATE_SELECTS_H_

#include "optimization_x86.h"

namespace art {

class DexCompilationUnit;

class HGenerateSelects : public HOptimization_X86 {
 public:
  HGenerateSelects(HGraph* graph,
                   const DexCompilationUnit& c_unit,
                   OptimizingCompilerStats* stats)
    : HOptimization_X86(graph, true, kGenerateSelectsPassName, stats), c_unit_(c_unit) {}

  void Run() OVERRIDE;

  static constexpr const char* kGenerateSelectsPassName = "generate_selects";

 private:
  void TryGeneratingSelects(HBasicBlock* block);
  const DexCompilationUnit& c_unit_;

  DISALLOW_COPY_AND_ASSIGN(HGenerateSelects);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_GENERATE_SELECTS_H_
