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

#ifndef ART_COMPILER_OPTIMIZING_CONSTANT_CALCULATION_SINKING_H_
#define ART_COMPILER_OPTIMIZING_CONSTANT_CALCULATION_SINKING_H_

#include "induction_variable.h"
#include "nodes.h"
#include "optimization_x86.h"

namespace art {

class HConstantCalculationSinking : public HOptimization_X86 {
 public:
  explicit HConstantCalculationSinking(HGraph* graph,
                                       OptimizingCompilerStats* stats = nullptr)
      : HOptimization_X86(graph, true, kConstantCalculationSinkingPassName, stats) {
  }

  static constexpr const char* kConstantCalculationSinkingPassName = "constant_calculation_sinking";

  void Run() OVERRIDE;

 private:
  /**
   * @class AccumulatorAssociation
   * @brief AccumulatorAssociation is an association with all information needed
   * to sink an constant.
   */
  struct AccumulatorAssociation {
    AccumulatorAssociation() :
      linear_instruction(nullptr), const_instruction(nullptr),
      initial_constant(nullptr), phi(nullptr), constant(nullptr),
      finalized_calculation(false), is_zero(false), is_inf(false) {
    }

    HInstruction* linear_instruction;
    HInstruction* const_instruction;
    HInstruction* initial_constant;
    HPhi* phi;
    HConstant* constant;
    bool finalized_calculation;
    bool is_zero;
    bool is_inf;
  };

  void DeletePhiAndUsers(HPhi* phi) const;

  bool HasNoDependenciesWithinLoop(HInstruction* candidate, HLoopInformation_X86* loop_info,
          HInstruction* phi) const;

  void FillAccumulator(HLoopInformation_X86* loop_info, HInductionVariable* choosen_iv,
          std::vector<AccumulatorAssociation>& accumulator_list) const;

  bool FindOrigin(AccumulatorAssociation& accum_assoc, bool& seen_base_twice) const;

  bool EvaluateFloatOperation(
         const HInstruction::InstructionKind& instruction_kind,
         int64_t iterations, AccumulatorAssociation& accum_assoc,
         bool is_double, bool seen_base_twice) const;

  bool EvaluateIntegerOperation(
        const HInstruction::InstructionKind& instruction_kind,
        int64_t iterations,
        AccumulatorAssociation& accum_assoc) const;

  bool SafeForFloatOperation(
        const HInstruction::InstructionKind& instruction_kind,
        AccumulatorAssociation& accum_assoc) const;

  void DoConstantCalculation(const std::vector<AccumulatorAssociation>& accumulator_list,
          HLoopInformation_X86* loop_info,
          std::vector<AccumulatorAssociation>& to_sink) const;

  bool HandleCalculation(const HInstruction::InstructionKind& instruction_kind,
          int64_t iterations,
          AccumulatorAssociation& accum_assoc) const;

  HInductionVariable* GetBasicInductionVariable(HLoopInformation_X86* loop_info) const;

  bool IsLoopGoodForCCS(HLoopInformation_X86* loop_info) const;

  void DoConstantSinking(const std::vector<AccumulatorAssociation>& to_sink,
          HLoopInformation_X86* loop_info,
          std::vector<AccumulatorAssociation>& to_remove) const;

  void RemoveDeadCode(const std::vector<AccumulatorAssociation>& to_remove) const;

  void HandleLoop(HLoopInformation_X86* loop_info) const;

  // This is 65 because after 65 iterations, we will have overflowed in 64 bits.
  static constexpr int64_t kMaximumEvaluationIterations = 65;
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_CONSTANT_CALCULATION_SINKING_H_
