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

#ifndef ART_OPT_INFRASTRUCTURE_OPTIMIZATION_X86_H_
#define ART_OPT_INFRASTRUCTURE_OPTIMIZATION_X86_H_

#include "optimization.h"

namespace art {

/**
 * @class HOptimization_X86
 * @brief Abstraction to implement an Intel-specific optimization pass.
 */
class HOptimization_X86 : public HOptimization {
 public:
  HOptimization_X86(HGraph* graph,
                    bool is_in_ssa_form,
                    const char* pass_name,
                    OptimizingCompilerStats* stats = nullptr,
                    bool verbose = false) :
    HOptimization(graph, is_in_ssa_form, pass_name, stats), verbose_(verbose) {}

  /**
   * @brief Is this pass verbose?
   * @return true, if this optimization pass is verbose.
   */
  bool IsVerbose() const {
    return verbose_;
  }

  /**
   * @brief Sets verbosity of the pass.
   * @param verbose the new verbosity.
   */
  void SetVerbose(bool verbose) {
    verbose_ = verbose;
  }

 private:
  bool verbose_;
};

}  // namespace art

#endif  // ART_OPT_INFRASTRUCTURE_OPTIMIZATION_X86_H_
