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

#include "ext_utility.h"
#include "graph_x86.h"
#include "loop_iterators.h"
#include "pretty_printer.h"

namespace art {

void HGraph_X86::Dump() {
  for (size_t i = 0; i < blocks_.Size(); ++i) {
    HBasicBlock* block = blocks_.Get(i);
    LOG(INFO) << "Block " << block->GetBlockId() << " has LoopInformation " << block->GetLoopInformation();
  }

  StringPrettyPrinter printer(this);
  printer.VisitInsertionOrder();
  const std::string& print_string = printer.str();

  LOG(INFO) << print_string;
}

void HGraph_X86::DeleteBlock(HBasicBlock* block) {
  // Remove all Phis.
  for (HInstructionIterator it2(block->GetPhis()); !it2.Done(); it2.Advance()) {
    HInstruction* insn = it2.Current();
    RemoveAsUser(insn);
    RemoveFromEnvironmentUsers(insn);
    block->RemovePhi(insn->AsPhi(), false);
  }

  // Remove the rest of the instructions.
  for (HInstructionIterator it2(block->GetInstructions()); !it2.Done(); it2.Advance()) {
    HInstruction* insn = it2.Current();
    RemoveAsUser(insn);
    RemoveFromEnvironmentUsers(insn);
    block->RemoveInstruction(insn, false);
  }

  // Remove all successors from the block.
  const GrowableArray<HBasicBlock*>& successors = block->GetSuccessors();
  for (size_t j = successors.Size(); j > 0; j--) {
    HBasicBlock* successor = successors.Get(j - 1);
    if (successor->GetPredecessorIndexOf(block) != static_cast<size_t>(-1)) {
      successor->RemovePredecessor(block);
    }
    block->RemoveSuccessor(successor);
  }

  // Remove all predecessors.
  block->ClearAllPredecessors();

  // Remove all data structures pointing to the block.
  blocks_.Put(block->GetBlockId(), nullptr);
  reverse_post_order_.Delete(block);
  if (linear_order_.Size() > 0) {
    linear_order_.Delete(block);
  }
}

void HGraph_X86::SplitCriticalEdgeAndUpdateLoopInformation(HBasicBlock* from, HBasicBlock* to) {
  // Remember index, to find a new added splitter.
  size_t index = to->GetPredecessorIndexOf(from);

  // First split.
  SplitCriticalEdge(from, to);

  // Find splitter.
  HBasicBlock* splitter = to->GetPredecessors().Get(index);

  // Set loop information for splitter.
  HLoopInformation* loop_information = to->IsLoopHeader() ?
          from->GetLoopInformation() : to->GetLoopInformation();
  if (loop_information != nullptr) {
    LOOPINFO_TO_LOOPINFO_X86(loop_information)->AddToAll(splitter);
  }
}

void HGraph_X86::RebuildDomination() {
  ClearDominanceInformation();
  ComputeDominanceInformation();
}

bool HGraph_X86::CanInlineThrower(HGraph* callee_graph, HInvoke* invoke,
                                  HInstruction* instruction) {
  // Handle the case where this method is called with a non-thrower.
  if (!instruction->CanThrow()) {
    VLOG(compiler) << "Method " << GetMethodName(callee_graph)
                   << " is called with a non-thrower, so cannot be inlined";
    return false;
  }

  // This is a simple conservative approximation of throwers that can be supported.
  bool ends_callee_context = instruction->IsNullCheck() ||
      instruction->IsBoundsCheck() ||
      instruction->IsDivZeroCheck();

  // Do the complete check to see if we can handle it. The following conditions are checked:
  // #) Thrower ends callee context.
  // #) Outer graph does not have catch for invoke.
  // #) Inner graph does not have catch for thrower.
  if (ends_callee_context &&
      !HasCatchHandler(invoke->GetId()) &&
      !GRAPH_TO_GRAPH_X86(callee_graph)->HasCatchHandler(instruction->GetId())) {
    // This thrower passed criteria, so allow it to be inlined.
    return true;
  }

  VLOG(compiler) << "The thrower did not pass criteria, so the method "
                 << GetMethodName(callee_graph)
                 << " cannot be inlined";
  return false;
}

void HGraph_X86::InlineIntoExtended(HGraph* outer_graph, HInvoke* invoke) {
  // This method does the following things:
  // #) Fixes the environment for throwers that end callee context.
  // #) Calls the super to finish the inlining logic.
  // #) Updates outer graph with catch information.

  HReversePostOrderIterator it(*this);
  it.Advance();  // Skip the entry block, we do not need to update the entry's suspend check.
  for (; !it.Done(); it.Advance()) {
    HBasicBlock* block = it.Current();
    for (HInstructionIterator instr_it(block->GetInstructions());
          !instr_it.Done();
          instr_it.Advance()) {
       HInstruction* current = instr_it.Current();
      if (current->NeedsEnvironment()) {
        current->GetEnvironment()->SetAndCopyParentChain(
            outer_graph->GetArena(), invoke->GetEnvironment());
      }
    }
  }

  if (HasBoundsChecks()) {
    outer_graph->SetHasBoundsChecks(true);
  }

  // Now call the super method to finish the inlining.
  InlineInto(outer_graph, invoke);

  // Make sure to update that caller also has catch handlers now.
  if (this->HasCatchHandlers()) {
    GRAPH_TO_GRAPH_X86(outer_graph)->SetHasCatchHandlers(true);
  }
}

void HGraph_X86::MovePhi(HPhi* phi, HBasicBlock* to_block) {
  DCHECK(phi != nullptr);
  HBasicBlock* from_block = phi->GetBlock();
  if (from_block != to_block) {
    from_block->phis_.RemoveInstruction(phi);
    to_block->phis_.AddInstruction(phi);
    phi->SetBlock(to_block);
  }
}

void HGraph_X86::MoveInstructionBefore(HInstruction* instr, HInstruction* cursor) {
  DCHECK(instr != nullptr);
  HBasicBlock* from_block = instr->GetBlock();
  DCHECK(cursor != nullptr);
  DCHECK(!cursor->IsPhi());
  HBasicBlock* to_block = cursor->GetBlock();
  DCHECK(from_block != to_block);

  // Disconnect from the old block.
  from_block->RemoveInstruction(instr, false);

  // Connect up to the new block.
  DCHECK_NE(instr->GetId(), -1);
  DCHECK_NE(cursor->GetId(), -1);
  DCHECK(!instr->IsControlFlow());
  instr->SetBlock(to_block);
  to_block->instructions_.InsertInstructionBefore(instr, cursor);
}

void HGraph_X86::FillEnvironmentToInstruction(SafeMap<HEnvironment*, HInstruction*>* env_to_instr) {
  for (HReversePostOrderIterator block_it(*this); !block_it.Done(); block_it.Advance()) {
    HBasicBlock* block = block_it.Current();
    for (HInstructionIterator it(block->GetInstructions()); !it.Done(); it.Advance()) {
      HInstruction* instruction = it.Current();

      if (instruction->HasEnvironment()) {
        for (HEnvironment* env = instruction->GetEnvironment();
             env != nullptr; env = env->GetParent()) {
          env_to_instr->Put(env, instruction);
        }
      }
    }
  }
}

}  // namespace art
