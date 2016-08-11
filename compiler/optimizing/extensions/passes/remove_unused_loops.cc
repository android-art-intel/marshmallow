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

#include "remove_unused_loops.h"

#include "ext_utility.h"
#include "graph_x86.h"
#include "loop_formation.h"
#include "loop_iterators.h"
#include "base/stringprintf.h"

namespace art {

void HRemoveUnusedLoops::Run() {
  PRINT_PASS_OSTREAM_MESSAGE(this, "start " << GetMethodName(graph_));

  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86 *graph_loop_info = graph->GetLoopInformation();

  // Walk all the inner loops in the graph.
  bool changed = false;
  for (HOnlyInnerLoopIterator it(graph_loop_info); !it.Done(); it.Advance()) {
    HLoopInformation_X86* loop_info = it.Current();
    HBasicBlock* pre_header = loop_info->GetPreHeader();

    PRINT_PASS_OSTREAM_MESSAGE(this, "Visit " << loop_info->GetHeader()->GetBlockId()
                                     << ", preheader = " << pre_header->GetBlockId());

    // The exit block from the loop.
    HBasicBlock* exit_block = loop_info->GetExitBlock();

    if (exit_block == nullptr) {
      // We need exactly 1 exit block from the loop.
      PRINT_PASS_MESSAGE(this, "Too many or too few exit blocks");
      continue;
    }

    // This must be a countable loop.
    if (!loop_info->HasKnownNumIterations()) {
      PRINT_PASS_MESSAGE(this, "Loop is not countable");
      continue;
    }

    // Walk through the blocks in the loop.
    bool loop_is_empty = true;
    external_loop_phis_.clear();
    for (HBlocksInLoopIterator it_loop(*loop_info); !it_loop.Done(); it_loop.Advance()) {
      HBasicBlock* loop_block = it_loop.Current();

      if (CheckPhisInBlock(loop_info, loop_block) == false) {
        // Phi result is used outside the loop.
        loop_is_empty = false;
        break;
      }

      if (CheckInstructionsInBlock(loop_info, loop_block) == false) {
        // Instruction result is used outside the loop.
        loop_is_empty = false;
        break;
      }
    }

    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop end: is_empty = " << (loop_is_empty ? "true" : "false"));

    if (loop_is_empty) {
      UpdateExternalPhis();
      RemoveLoop(loop_info, pre_header, exit_block);
      MaybeRecordStat(MethodCompilationStat::kIntelRemoveUnusedLoops);
      changed = true;
    }
  }
  PRINT_PASS_OSTREAM_MESSAGE(this, "end " << GetMethodName(graph_));
  if (changed) {
    // We have to rebuild our loops properly, now that we have removed loops.
    HLoopFormation form_loops(graph_);
    form_loops.Run();
  }
}

bool HRemoveUnusedLoops::CheckInstructionsInBlock(HLoopInformation_X86* loop_info,
                                                 HBasicBlock* loop_block) {
  // Walk through the instructions in the loop, and see if the instruction
  // has any side effect, or it can throw.  If neither, see if the result is used
  // outside the loop.
  for (HInstructionIterator inst_it(loop_block->GetInstructions());
       !inst_it.Done();
       inst_it.Advance()) {
    HInstruction* instruction = inst_it.Current();
    PRINT_PASS_OSTREAM_MESSAGE(this, "Look at: " << instruction
                    << (instruction->HasSideEffects() ? " <has side effects>" : "")
                    << (!instruction->CanBeMoved() ? " <can't be moved>" : "")
                    << (instruction->CanThrow() ? " <can throw>" : "")
                    << (instruction->IsControlFlow() ? " <is control flow>" : ""));

    // Special case SuspendCheck.  We don't care about it.
    if (instruction->IsSuspendCheck()) {
      continue;
    }

    // Special case ControlFlow.  If it exits the loop, we would catch it above.
    // Throw is a special case.
    if (instruction->IsControlFlow() && !instruction->CanThrow()) {
      continue;
    }

    if (instruction->HasSideEffects() || instruction->CanThrow() ||
        !instruction->CanBeMoved()) {
      // Not an empty loop.
      PRINT_PASS_MESSAGE(this, "need this instruction");
      return false;
    }

    // Check that all the uses of the instruction are used in the loop.
    if (instruction->GetType() == Primitive::kPrimVoid) {
      // Isn't used by anyone.
      continue;
    }

    bool no_side_effects = true;  // All instructions are okay.
    std::unordered_set<HPhi*> worklist;
    for (HUseIterator<HInstruction*> it2(instruction->GetUses()); !it2.Done(); it2.Advance()) {
      HInstruction* insn = it2.Current()->GetUser();
      HBasicBlock* insn_block = insn->GetBlock();
      HLoopInformation* li = insn_block->GetLoopInformation();
      PRINT_PASS_OSTREAM_MESSAGE(this, "Result is used by: " << insn);
      if (li != loop_info) {
        // We are being used in a different loop.  Is it REALLY used?
        // Only special case Phis for this check.
        HPhi* insn_as_phi = insn->AsPhi();
        if (insn_as_phi != nullptr && !insn->HasUses()) {
          PRINT_PASS_MESSAGE(this, "Used by Phi in different loop -- has no uses (removing)");
          worklist.insert(insn_as_phi);
        } else {
          PRINT_PASS_MESSAGE(this, "Used in different loop");
          no_side_effects = false;
        }
      }
    }
    for (auto insn_as_phi : worklist) {
      insn_as_phi->GetBlock()->RemovePhi(insn_as_phi);
    }
    if (!no_side_effects) {
      return false;  // Other insn may be skipped.
    }
  }

  // All instructions are okay.
  return true;
}

static bool BothInputsAreFromOutsideInnerLoop(HPhi* phi,
                                              HLoopInformation_X86* loop_info) {
  // Only handle 2 input Phis.
  if (phi->InputCount() != 2) {
    return false;
  }

  // Check that the inputs are not within the loop.  Since we only handle
  // inner loops, any input which has a different loop information is outside.
  HLoopInformation* li = phi->InputAt(0)->GetBlock()->GetLoopInformation();
  if (li == loop_info) {
    return false;
  }
  li = phi->InputAt(1)->GetBlock()->GetLoopInformation();
  return li != loop_info;
}

bool HRemoveUnusedLoops::CheckPhisInBlock(HLoopInformation_X86* loop_info,
                                         HBasicBlock* loop_block) {
  // We are only looking at inner loops.
  DCHECK(loop_info->IsInner());

  // Walk through the Phis in the loop, and see if the result
  // is used outside the loop.
  for (HInstructionIterator inst_it(loop_block->GetPhis());
       !inst_it.Done();
       inst_it.Advance()) {
    HPhi* phi = inst_it.Current()->AsPhi();
    DCHECK(phi != nullptr);
    PRINT_PASS_OSTREAM_MESSAGE(this, "Look at: " << phi);
    // Special case the case where both inputs are from outside the loop.
    // Only valid in loop header.
    if (phi->IsLoopHeaderPhi() && BothInputsAreFromOutsideInnerLoop(phi, loop_info)) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Phi has 2 external inputs: "
                                        << phi->InputAt(0) << ' ' << phi->InputAt(1));
      external_loop_phis_.insert(phi);
      continue;
    }

    for (HUseIterator<HInstruction*> it2(phi->GetUses()); !it2.Done(); it2.Advance()) {
      HInstruction* insn = it2.Current()->GetUser();
      HBasicBlock* insn_block = insn->GetBlock();
      HLoopInformation* li = insn_block->GetLoopInformation();
      PRINT_PASS_OSTREAM_MESSAGE(this, "Result is used by: " << insn);
      if (li != loop_info) {
        // We are being used in a different loop (could be out of the loop).
        PRINT_PASS_MESSAGE(this, "Used in different loop");
        return false;
      }
    }
  }

  // All okay.
  return true;
}

void HRemoveUnusedLoops::RemoveLoop(HLoopInformation_X86* loop_info,
                                   HBasicBlock* pre_header,
                                   HBasicBlock* exit_block) {
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HBasicBlock* loop_header = loop_info->GetHeader();
  PRINT_PASS_OSTREAM_MESSAGE(this, "Remove loop blocks: "
                                   << loop_header->GetBlockId()
                                   << ", preheader = " << pre_header->GetBlockId());

  GrowableArray<HBasicBlock*> blocks_in_loop(graph->GetArena(), graph->GetBlocks().Size());
  for (HBlocksInLoopIterator it_loop(*loop_info); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* loop_block = it_loop.Current();
    DCHECK(loop_block->IsInLoop());
    DCHECK(loop_block->GetLoopInformation() == loop_info);
    blocks_in_loop.Add(loop_block);
  }

  // Change the successor to the preheader to the exit block.
  DCHECK_EQ(pre_header->GetSuccessors().Size(), 1u);
  PRINT_PASS_OSTREAM_MESSAGE(this, "Set preheader to successor " << exit_block->GetBlockId());
  pre_header->ReplaceSuccessor(loop_header, exit_block);
  pre_header->ReplaceDominatedBlock(loop_header, exit_block);
  exit_block->SetDominator(pre_header);

  for (size_t i = 0, e = blocks_in_loop.Size(); i < e; ++i) {
    HBasicBlock* loop_block = blocks_in_loop.Get(i);
    PRINT_PASS_OSTREAM_MESSAGE(this, "Remove block " << loop_block->GetBlockId());
    graph->DeleteBlock(loop_block);
  }

  HLoopInformation_X86* tmp = loop_info;
  while (tmp != nullptr) {
    for (size_t i = 0, e = blocks_in_loop.Size(); i < e; ++i) {
      tmp->Remove(blocks_in_loop.Get(i));
    }
    tmp = tmp->GetParent();
  }
}

void HRemoveUnusedLoops::UpdateExternalPhis() {
  for (auto it : external_loop_phis_) {
    // Replace each phi with the value computed in the loop.
    PRINT_PASS_OSTREAM_MESSAGE(this, "Replace Phi " << it << " with " << it->InputAt(1));
    it->ReplaceWith(it->InputAt(1));
  }
}

}  // namespace art
