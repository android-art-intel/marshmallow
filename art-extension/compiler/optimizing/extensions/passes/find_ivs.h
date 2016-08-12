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


#ifndef VENDOR_INTEL_ART_EXTENSION_PASSES_FIND_IVS_H_
#define VENDOR_INTEL_ART_EXTENSION_PASSES_FIND_IVS_H_

#include "induction_variable.h"
#include "optimization_x86.h"

namespace art {

// Forward declarations.
class HLoopInformation_X86;

/**
 * Find the Induction Variables of the loop.
 */
class HFindInductionVariables : public HOptimization_X86 {
 protected:
  void DetectAndInitializeBasicIV(HLoopInformation_X86* info, HPhi* phi);
  void FindInductionVariablesHelper(HLoopInformation_X86* info);
  HIf* FindLoopIf(HLoopInformation_X86* loop);
  bool FindLoopUpperBound(HLoopInformation_X86* info, int64_t& upper_bound);
  bool IsValidCastForIV(HInstruction* candidate, HLoopInformation_X86* loop);

 public:
  HFindInductionVariables(HGraph* graph, const DexCompilationUnit* c_unit,
                          OptimizingCompilerStats* stats = nullptr)
    : HOptimization_X86(graph, true, "find_ivs", stats), c_unit_(c_unit) {}

  void Run() OVERRIDE;

 private:
  const DexCompilationUnit* c_unit_;
};
}  // namespace art

#endif  // VENDOR_INTEL_ART_EXTENSION_PASSES_FIND_IVS_H_
