/*
 * Copyright (C) 2014 The Android Open Source Project
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

#ifndef ART_COMPILER_OPTIMIZING_CONSTANT_FOLDING_X86_H_
#define ART_COMPILER_OPTIMIZING_CONSTANT_FOLDING_X86_H_

#include "constant_folding.h"
#include "optimization_x86.h"

namespace art {

class HConstantFolding_X86 : public HOptimization_X86 {
 public:
  explicit HConstantFolding_X86(HGraph* graph, OptimizingCompilerStats* stats = nullptr,
                                const char* name = kConstantFoldingPassName)
      : HOptimization_X86(graph, true, name, stats), fold(graph, stats, name) {}

  void Run() OVERRIDE;

 private:
  static constexpr const char* kConstantFoldingPassName = "constant_folding_x86";

  HConstantFolding fold;

  DISALLOW_COPY_AND_ASSIGN(HConstantFolding_X86);
};

}  // namespace art

#endif /* ART_COMPILER_OPTIMIZING_CONSTANT_FOLDING_X86_H_ */
