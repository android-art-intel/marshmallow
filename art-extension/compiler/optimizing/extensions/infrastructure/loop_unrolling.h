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

#ifndef ART_OPT_INFRASTRUCTURE_LOOP_UNROLLING_H_
#define ART_OPT_INFRASTRUCTURE_LOOP_UNROLLING_H_

#include "nodes.h"

namespace art {

// Forward declaration.
class HInstructionCloner;

/**
 * @brief This data structure represents the body of the loop. It is used in the methods,
 * which implement the loop unrolling optimization.
 */
struct LoopBody {
  LoopBody() : entry_block_(nullptr), tail_block_(nullptr) {}
  std::unordered_set<HBasicBlock*> block_list_;
  HBasicBlock* entry_block_;
  HBasicBlock* tail_block_;
};

class HLoopUnrolling {
 public:
  HLoopUnrolling(HLoopInformation_X86* loop, HOptimization_X86* optim);

  /**
   * @brief Fully unrolls the loop by the provided factor if possible. The user
   * must check the feasability of the unrolling before with a call to Gate().
   * @return Returns true if the unrolling was successful, or false otherwise.
   * @sa Gate.
   */
  bool FullUnroll();

  /**
   * @brief Makes sure the provided loop complies with the restrictions of loop unrolling.
   * @param max_unrolled_instructions The maximum amount of instructions tolerated for unrolling.
   * @return Returns true if the loop complies with the unrolling restrictions, or false otherwise.
   */
  bool Gate(uint64_t max_unrolled_instructions) const;

 private:
  /**
   * @brief Allocates copies of the loop basic blocks to their destination. This facility also
   * makes sure the successors / predecessors links of the new basic blocks are identical
   * to the loop's originals, except from the back edges and out-of-loop links.
   * @param dst_body The structure which will hold the new basic blocks.
   * @param iteration_count The current iteration count of the unrolling process.
   */
  void CreateBasicBlocks(LoopBody& dst_body, uint64_t iteration_count);

  /**
   * @brief CopyBody Facility to copy the original loop body to the provided body. It will
   * allocate the basic blocks, clone and attach the instructions to the new body.
   * @param dst The body structure holding the copy of the original loop body.
   * @param iteration_count The current iteration count of the unrolling process.
   */
  void CopyBody(LoopBody& dst, uint64_t iteration_count);

  /**
   * @brief List the loop instructions that are used for computing the exit condition. It is used
   * to avoid copying them during the unrolling process.
   * @details This method will store the list in an internal data structure.
   */
  void ListLoopConditionInstructions();

  /**
   * @return Returns whether the instruction is the condition of the loop being unrolled.
   * @param insn The instruction to check.
   */
  bool IsLoopConditionInstruction(HInstruction* insn) const;

  /**
   * @brief During the first unroll iteration, we need to take the out-of-loop values into account
   * to map the body instructions to their correct values. This facility will map them accordingly.
   * @return Returns true if the mapping was successful, or false otherwise.
   */
  bool MapOutOfLoopPhiNodes();

  /**
   * @brief During the unrolling iterations (except the very first one), we need to take the in-loop
   * phi inputs into account when copying instructions using loop phi nodes. This mapping is computed
   * when this method is called.
   * @return Returns true if the update was successful, or false otherwise.
   */
  bool UpdateLoopPhiNodesMap();

  /**
   * This method attaches all the cloned instructions to their corresponding cloned basic block.
   * @param new_body The body the method will attach the new instructions to.
   * @param iteration_count The current iteration count of the unrolling process.
   */
  void AppendInstructions(const LoopBody& new_body, uint64_t iteration_count);

  /**
   * @brief Debug facility to print out a loop body.
   * @param body The loop body we want to print out.
   */
  void DumpBody(const LoopBody& body);

  /**
   * @brief This method fixes the users of the loop instructions to use the instructions cloned
   * during the last unrolling iteration.
   */
  void FixLoopUsers();

  /**
   * @brief Executes the loop unrolling, given an unrolling factor.
   * @param unrolling_factor The unrolling factor we want to apply to the loop.
   */
  bool UnrollBody(uint64_t unrolling_factor);

  /**
   * @brief Updates the loop's graph by attaching the unrolled body in place of the loop.
   * This method removes the graph of the loop.
   * TODO: If we want to do generic unrolling, we will need to update the unrolling algorithm to
   * attach the unrolled body inside of the loop instead of outside.
   */
  void UpdateGraph();

  /**
   * @brief Clones the loop instructions and saves the clones in the provided cloner. The
   * instructions stores in the should_not_copy_ structure will not be cloned, neither will
   * the loop header phi nodes.
   * @param cloner The Instruction cloner to use to clone the loop instructions.
   * @param iteration_count The current iteration count of the unrolling process.
   */
  void CloneInstructions(HInstructionCloner& cloner, uint64_t iteration_count) const;

  // The loop that needs to be unrolled.
  HLoopInformation_X86* loop_;

  // The graph the loop belongs to.
  HGraph_X86* graph_;

  // Structure holding the unrolled basic blocks. We will append it to the graph later.
  LoopBody unrolled_body_;

  // List of instructions that we should not clone.
  std::unordered_set<HInstruction*> loop_condition_instructions_;

  // Mapping of original -> copies of basic blocks.
  SafeMap<HBasicBlock*, HBasicBlock*> old_to_new_bbs_;

  // The map internally used by the instruction cloner.
  HInstructionCloner::EnvToInstrMap env_to_instr_;

  // The instruction cloner is used to duplicate loop instructions.
  HInstructionCloner cloner_;

  // The optional optimization the loop unrolling is attached to.
  HOptimization_X86* optim_;
};

}  // namespace art

#endif
