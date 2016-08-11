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

#ifndef ART_COMPILER_OPTIMIZING_NON_TEMPORAL_MOVE_H_
#define ART_COMPILER_OPTIMIZING_NON_TEMPORAL_MOVE_H_

#include "nodes.h"
#include "optimization_x86.h"

namespace art {

class DexCompilationUnit;

class HNonTemporalMove : public HOptimization_X86 {
 public:
  explicit HNonTemporalMove(HGraph* graph, const DexCompilationUnit& c_unit,
                            OptimizingCompilerStats* stats = nullptr)
      : HOptimization_X86(graph, true, kNonTemporalMovePassName, stats), c_unit_(c_unit) {}

  void Run() OVERRIDE;

  static constexpr int kMinNonTemporalIterations = 131072;

 private:
  static constexpr const char* kNonTemporalMovePassName = "non_temporal_move";

  typedef std::set<HArraySet*> ArraySets;

  /*
   * @brief Is this loop a candidate for non-temporal-move replacement?
   * @param loop_info Candidate loop information.
   * @param array_sets ArraySet instructions found in the loop are added to array_sets.
   * @returns 'true' if the loop should use non-temporal moves.
   */
  bool Gate(HLoopInformation_X86* loop_info, ArraySets& array_sets);

  const DexCompilationUnit& c_unit_;

  DISALLOW_COPY_AND_ASSIGN(HNonTemporalMove);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_NON_TEMPORAL_MOVE_H_
