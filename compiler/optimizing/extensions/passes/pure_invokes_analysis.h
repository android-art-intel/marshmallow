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

#ifndef ART_COMPILER_OPTIMIZING_PURE_INVOKES_ANALYSIS_H_
#define ART_COMPILER_OPTIMIZING_PURE_INVOKES_ANALYSIS_H_

#include "driver/dex_compilation_unit.h"
#include "graph_x86.h"
#include "ext_utility.h"
#include "nodes.h"
#include "optimization_x86.h"

#include <unordered_set>
#include <vector>

namespace art {

typedef std::vector<HInvokeStaticOrDirect*> ResolvedInvokes;

class HPureInvokesAnalysis : public HOptimization_X86 {
 public:
  HPureInvokesAnalysis(HGraph* graph,
                       OptimizingCompilerStats* stats = nullptr)
  : HOptimization_X86(graph, true, kPureInvokesHoistingPassName, stats) {}

  void Run() OVERRIDE;

 private:
  /**
   * @brief Finds candidates for pure invokes hoisting.
   * @details This method makes primary part of work. It emilinates pure
   *          invokes for which we have proved that it is legal, and also
   *          accumulates those that can potentially be hoisted to the vector.
   * @param hoisting_candidates The vector where candidates for hoisting are accumulated.
   * @return true, if we found at least one candidate for hoisting.
   */
  bool ProcessPureInvokes(ResolvedInvokes& hoisting_candidates);

  bool CalledNotPureMethodInChain(HInstruction* insn,
                                  std::unordered_set<HInstruction*>& objects_with_not_pure_invokes);

  bool HoistPureInvokes(ResolvedInvokes& hoisting_candidates);

  /**
   * @brief Checks whether the result of the instuction can be null.
   * @param insn The instuction to check.
   * @return true, if we proved that insn cannot return null.
   */
  bool CanReturnNull(HInstruction* insn);

  /**
   * @brief Checks whether the call is an invoke of pure method.
   * @param call The call to check.
   * @return true, if call invokes a pure method.
   */
  bool IsPureMethodInvoke(HInvokeStaticOrDirect* call);

  /**
   * @brief Pessimistically checks whether the invoke can return null.
   * @param call The invoke.
   * @return false, if we proved that the call never returns null.
   *         true, otherwise.
   */
  bool IsInvokeThatCanReturnNull(HInvokeStaticOrDirect* call);

  static constexpr const char* kPureInvokesHoistingPassName = "pure_invokes_analysis";

  // Method reference -> known answer for this method.
  SafeMap<const MethodReference, bool, MethodReferenceComparator> pure_invokes_;
  SafeMap<const MethodReference, bool, MethodReferenceComparator> null_invokes_;

  DISALLOW_COPY_AND_ASSIGN(HPureInvokesAnalysis);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_PURE_INVOKES_ANALYSIS_H_
