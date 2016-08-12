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

#ifndef ART_OPT_PASSES_LOOP_FORMATION_H_
#define ART_OPT_PASSES_LOOP_FORMATION_H_

#include "nodes.h"
#include "optimization_x86.h"

namespace art {

/**
 * @brief Implements optimizations specific to each instruction.
 */
class HLoopFormation : public HOptimization_X86 {
 public:
  explicit HLoopFormation(HGraph* graph, const char* name = kLoopFormationPassName)
    : HOptimization_X86(graph, true, name, nullptr) {}

  static constexpr const char* kLoopFormationPassName = "loop_formation";

  void Run() OVERRIDE;
};

}  // namespace art

#endif  // ART_OPT_PASSES_LOOP_FORMATION_H_
