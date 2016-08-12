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

#ifndef ART_OPT_INFRASTRUCTURE_GRAPH_X86_H_
#define ART_OPT_INFRASTRUCTURE_GRAPH_X86_H_

#include "nodes.h"
#include "loop_information.h"
#include "optimizing_compiler_stats.h"

namespace art {

#ifndef NDEBUG
#define GRAPH_MAGIC 0xdeadcafe
#define GRAPH_TO_GRAPH_X86(X) HGraph_X86::DownCast(X)
#else
#define GRAPH_TO_GRAPH_X86(X) static_cast<HGraph_X86*>(X)
#endif

class HGraph_X86 : public HGraph {
 public:
  HGraph_X86(ArenaAllocator* arena, const DexFile& dex_file, uint32_t method_idx, InstructionSet instruction_set, bool debuggable = false, int start_instruction_id = 0) :
      HGraph(arena, dex_file, method_idx, instruction_set, debuggable, start_instruction_id),
      loop_information_(nullptr),
      has_catch_handlers_(false) {
#ifndef NDEBUG
        down_cast_checker_ = GRAPH_MAGIC;
#endif
  }

  /**
   * @brief Return the graph's loop information.
   * @return the outermost loop in the graph.
   */
  HLoopInformation_X86* GetLoopInformation() const {
    return loop_information_;
  }

  void SetLoopInformation(HLoopInformation_X86* loop) {
    loop_information_ = loop;
  }

  /**
   * @brief Clear the loop information.
   */
  void ClearLoopInformation() {
    loop_information_ = nullptr;
  }

  /**
   * @brief Add a loop to the graph.
   * @param information The loop information to be added.
   */
  void AddLoopInformation(HLoopInformation_X86* information) {
    // Due to the way this method is being called,
    //   the first call is one of the outer loops.

    // Ensure we are only adding a single loop in.
    information->ResetLinks();
    if (loop_information_ == nullptr) {
      loop_information_ = information;
    } else {
      loop_information_->Add(information);
    }
  }

  /**
   * @brief Delete a block, cleaning up all the loose ends such as
   * successors, predecessors, etc.
   * @param block The HBasicBlock to delete from the graph.
   */
  void DeleteBlock(HBasicBlock* block);

  void Dump();

#ifndef NDEBUG
  static HGraph_X86* DownCast(HGraph* graph) {
    HGraph_X86* res = static_cast<HGraph_X86*>(graph);

    if (res != nullptr) {
      DCHECK_EQ(res->down_cast_checker_, GRAPH_MAGIC);
    }
    return res;
  }
#endif

  /**
   * @brief Used to create a new basic that is added to graph.
   * @param dex_pc The dex pc of this block (optional).
   * @return Returns the newly created block.
   */
  HBasicBlock* CreateNewBasicBlock(uint32_t dex_pc = kNoDexPc) {
    HBasicBlock* new_block = new (arena_) HBasicBlock(this, dex_pc);
    AddBlock(new_block);
    return new_block;
  }

  /**
   * @brief Split critial edge and set loop information splitter.
   * @param from input of new critical edge.
   * @param to output of new critical edge.
   */
  void SplitCriticalEdgeAndUpdateLoopInformation(HBasicBlock* from, HBasicBlock* to);

  /**
   * @brief Called after an optimization pass in order to rebuild domination
   * information and ordering.
   * @details This also has effect of cleaning up graph and normalizing loops
   * (depending on what Google overloaded it to do).
   */
  void RebuildDomination();

  /**
   * @brief Used to check if the method has any catch handlers.
   * @return Returns true if the method indeed has catches.
   */
  bool HasCatchHandlers() const {
    return has_catch_handlers_;
  }

  /**
   * @brief Used to update the information in graph that it does have catch handler.
   * @param val This is passed in as true if method has catch handlers and false if
   * it does not.
   */
  void SetHasCatchHandlers(bool val) {
    has_catch_handlers_ = val;
  }

  /**
   * @brief Used to check whether a specific instruction is inside try/catch block.
   * @param instruction_id The id of the instruction to check.
   * @return Returns true if instruction is in try block. Also returns true if method
   * has catch blocks but it is unknown whether this instruction resides in try block.
   * Return false if instruction is definitely not in try block.
   */
  bool HasCatchHandler(int32_t instruction_id ATTRIBUTE_UNUSED) const {
    // TODO: Since we do not store information about which instructions are in catch, simply
    // assume that if the method has catch handlers the current instruction is in it.
    return HasCatchHandlers();
  }

  /**
   * @brief This is a gate used by inlining pass to check if thrower can be inlined.
   * @details Ideally this kind of method would be part of inliner gate, but it was
   * placed here since this is an extension of common code. The instance on which
   * this method is called should be the caller graph.
   * @param callee_graph The graph of the callee.
   * @param invoke The invoke instruction whose target is being inlined.
   * @param instruction The thrower to check if it can be inlined without caring for
   * stack trace.
   * @return Returns true if inlining can be done with no issue on this instruction.
   */
  bool CanInlineThrower(HGraph* callee_graph, HInvoke* invoke, HInstruction* instruction);

  /**
   * @brief This is the extended implementation to inline callee into caller.
   * @details This is used in conjunction with overridden InlineInto but provides additional
   * logic to deal with cases like inlining throwers. The instance for which this method
   * should be called is callee graph.
   * @see CanInlineThrower
   * @param outer_graph The graph of the caller.
   * @param invoke The invoke instruction whose target is being inlined.
   */
  void InlineIntoExtended(HGraph* outer_graph, HInvoke* invoke);

  /*
   * @brief Move a Phi from one block to another block.
   * @param phi Phi to move.
   * @param to_block Block to which to move the Phi.
   */
  void MovePhi(HPhi* phi, HBasicBlock* to_block);

  /*
   * @brief Move an from one block to another block.
   * @param instr Instruction to move.
   * @param cursor The target instruction.
   * @note The instruction to move must be in a different block from the cursor.
   */
  void MoveInstructionBefore(HInstruction* instr, HInstruction* cursor);

  /**
   * @brief Used to figure out a mapping between environments and instructions that contain them.
   * @param env_to_instr The map that gets update to have the mapping of every environment in graph
   * to the instruction that contains it.
   */
  void FillEnvironmentToInstruction(SafeMap<HEnvironment*, HInstruction*>* env_to_instr);

 protected:
#ifndef NDEBUG
  uint32_t down_cast_checker_;
#endif

  HLoopInformation_X86* loop_information_;

  /**
   * @brief Used to record whether the graph has catch handlers.
   * @details This is equivalent to checking the entire graph whether there are blocks
   * marked as being catch blocks.
   */
  bool has_catch_handlers_;
};

/**
 * @brief Helper function to create a HGraph_X86 for use in standalone tests.
 * @param allocator ArenaAllocator to use to allocate the HGraph_X86.
 * @returns the newly created HGraph_X86.
 */
inline HGraph_X86* CreateGraph_X86_for_test(ArenaAllocator* allocator) {
  return new (allocator) HGraph_X86(
      allocator, *reinterpret_cast<DexFile*>(allocator->Alloc(sizeof(DexFile))), -1, kRuntimeISA,
      false);
}

}  // namespace art

#endif  // ART_OPT_INFRASTRUCTURE_GRAPH_X86_H_
