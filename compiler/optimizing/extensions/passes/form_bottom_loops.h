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

#ifndef ART_COMPILER_OPTIMIZING_FORM_BOTTOM_LOOPS_H_
#define ART_COMPILER_OPTIMIZING_FORM_BOTTOM_LOOPS_H_

#include "driver/dex_compilation_unit.h"
#include "nodes.h"
#include "optimization_x86.h"

namespace art {

class HInstructionCloner;

class HFormBottomLoops : public HOptimization_X86 {
 public:
  explicit HFormBottomLoops(HGraph* graph, const DexCompilationUnit& c_unit,
                            OptimizingCompilerStats* stats = nullptr)
      : HOptimization_X86(graph, true, kFormBottomLoopsPassName, stats), c_unit_(c_unit) {}

  void Run() OVERRIDE;

 private:
  static constexpr const char* kFormBottomLoopsPassName = "form_bottom_loops";

  // Mapping from Environment to instruction containing the environment.
  typedef SafeMap<HEnvironment*, HInstruction*> EnvToInstrMap;

  /**
   * @brief Should this loop be rewritten as a bottom tested loop?
   * @param loop_info The current loop.
   * @param loop_header The top of the loop.
   * @param exit_block The exit from the loop.
   * @returns 'true' if the loop should be rewritten.
   */
  bool ShouldTransformLoop(HLoopInformation_X86* loop_info,
                           HBasicBlock* loop_header,
                           HBasicBlock* exit_block);

  /**
   * @brief Is the loop header block safe to rewrite?
   * @param loop_info The current loop.
   * @param loop_block The top of the loop.
   * @returns 'true' if all instructions in the loop are safe to rewrite.
   */
  bool CheckLoopHeader(HLoopInformation_X86* loop_info, HBasicBlock* loop_block);

  /**
   * @brief Rewrite the loop as a bottom tested loop.
   * @param loop_info The current loop.
   * @param env_to_instr Map from Environment to instruction to aid in environment fixups.
   * @param loop_header The top of the loop.
   * @param exit_block The exit from the loop.
   */
  void RewriteLoop(HLoopInformation_X86* loop_info,
                   EnvToInstrMap& env_to_instr,
                   HBasicBlock* loop_header,
                   HBasicBlock* exit_block);

  /**
   * @brief Fix up any references to the old loop header Phis that are used
   * in a following instruction.
   * @param loop_info The current loop.
   * @param env_to_instr Map from Environment to instruction to aid in environment fixups.
   * @param first_block The future loop header.
   * @param exit_block The block in which to insert a new Phi.
   * @param phi The phi to fix up.
   * @param computed_in_header Whether our Phi node is computed in header.
   * @param outside_value The phi input that comes from outside the loop.
   * @param inside_value The phi input that comes from inside the loop.
   * @param exit_phis set of Phis created in the exit block.
   */
  void FixPhiUses(HLoopInformation_X86* loop_info,
                  EnvToInstrMap& env_to_instr,
                  HBasicBlock* first_block,
                  HBasicBlock* exit_block,
                  HPhi* phi,
                  bool computed_in_header,
                  HInstruction* outside_value,
                  HInstruction* inside_value,
                  std::set<HPhi*>& exit_phis);

  const DexCompilationUnit& c_unit_;

  std::set<HInstruction*> header_values_used_in_exit_block;

  DISALLOW_COPY_AND_ASSIGN(HFormBottomLoops);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_FORM_BOTTOM_LOOPS_H_
