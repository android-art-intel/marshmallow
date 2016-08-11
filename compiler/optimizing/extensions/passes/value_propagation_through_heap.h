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

#ifndef ART_COMPILER_OPTIMIZING_VALUE_PROPAGATION_THROUGHHEAP_H_
#define ART_COMPILER_OPTIMIZING_VALUE_PROPAGATION_THROUGHHEAP_H_

#include "ext_alias.h"
#include "nodes.h"
#include "optimization_x86.h"

#include <set>

namespace art {

class HValuePropagationThroughHeap : public HOptimization_X86 {
 public:
  explicit HValuePropagationThroughHeap(HGraph* graph, OptimizingCompilerStats* stats)
      : HOptimization_X86(graph, true, kValuePropagationThroughHeap, stats) {}

  void Run() OVERRIDE;

 private:
  static constexpr const char* kValuePropagationThroughHeap = "value_propagation_through_heap";

 /*
  * @brief Check if loop instructions is safe for value propagation through heap optimization.
  * @param loop the loop information.
  * @return true true if loop is valid candidate for this optimization, false otherwise.
  */
  bool Gate(HLoopInformation_X86* loop) const;

  /*
   * @brief Get candidate setter instructions where invariant value is stored in the loop preheader.
   * @param loop the loop information.
   * @param setters_set the set that stores the candidate setter instructions.
   * @return return true when candidates setter found.
   */
  bool GetCandidateSetters(HLoopInformation_X86* loop, std::set<HInstruction*>& setters_set);


  /*
   * @brief Propagate values in candidate setters to the corresponding getter instructions.
   * @param loop the loop information.
   * @param setters_set the set that stores the candidate setter instructions.
   */
  void PropagateValueToGetters(HLoopInformation_X86* loop, std::set<HInstruction*> setters__set);

  AliasCheck alias_;

  // The maximum basic block numbers in the loop for this optimization to apply
  // to save compilation time.
  static constexpr int64_t kMaximumBasicBlockNumbers = 11;

  DISALLOW_COPY_AND_ASSIGN(HValuePropagationThroughHeap);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_VALUE_PROPAGATION_THROUGH_HEAP_H_

