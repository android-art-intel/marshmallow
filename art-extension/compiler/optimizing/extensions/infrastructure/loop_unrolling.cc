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

#include "cloning.h"
#include "ext_utility.h"
#include "graph_x86.h"
#include "loop_unrolling.h"
#include "optimization_x86.h"

namespace art {

HLoopUnrolling::HLoopUnrolling(HLoopInformation_X86* loop, HOptimization_X86* optim)
    : loop_(loop),
      graph_(GRAPH_TO_GRAPH_X86(loop_->GetGraph())),
      cloner_(graph_, &env_to_instr_, true, true, true),
      optim_(optim) {}

static bool IsUselessInstruction(HInstruction* insn) {
  switch (insn->GetKind()) {
    case HInstruction::kTestSuspend:
    case HInstruction::kSuspendCheck:
    case HInstruction::kSuspend:
      return true;
    default:
      break;
  }

  return false;
}

void HLoopUnrolling::CreateBasicBlocks(LoopBody& dst_body, uint64_t iteration_count) {
  // The loop information should be the same as the loop parent of the original basic block.
  HLoopInformation_X86* parent_loop_info =
        LOOPINFO_TO_LOOPINFO_X86(loop_->GetPreHeader()->GetLoopInformation());
  HBasicBlock* last_bb = nullptr;

  for (HBlocksInLoopReversePostOrderIterator bb_it(*loop_); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* src_bb = bb_it.Current();

    // No need to copy an empty basic block.
    if (src_bb->IsSingleGoto()) {
      continue;
    }

    if (loop_->GetNumIterations(src_bb) <= static_cast<int64_t>(iteration_count)) {
      continue;
    }

    // Create a new basic block.
    HBasicBlock* bb_copy = graph_->CreateNewBasicBlock(src_bb->GetDexPc());
    DCHECK(bb_copy != nullptr);

    // Add it to the old to copy basic block map.
    if (old_to_new_bbs_.find(src_bb) != old_to_new_bbs_.end()) {
      old_to_new_bbs_.Overwrite(src_bb, bb_copy);
    } else {
      old_to_new_bbs_.Put(src_bb, bb_copy);
    }

    // Add it to the destination body.
    dst_body.block_list_.insert(bb_copy);

    parent_loop_info->AddToAll(bb_copy);

    const auto& predecessors = src_bb->GetPredecessors();

    // We want to fix the predecessors / successors links. We only fix the predecessors since
    // they are the only basic blocks that we know we have copied so far, since we are going
    // through the basic blocks in a reverse post-order manner.
    for (unsigned int pred_id = 0u; pred_id < predecessors.Size(); pred_id++) {
      HBasicBlock* pred_bb = predecessors.Get(pred_id);

      // We want to handle in-loop links only. The out-of-loop links will be handled later.
      // We also don't want to handle back-edges since we are removing the loop.
      if (!loop_->Contains(*pred_bb) || loop_->IsBackEdge(*pred_bb)) {
        continue;
      }

      // Each predecessor must have been copied at this point.
      DCHECK(old_to_new_bbs_.find(pred_bb) != old_to_new_bbs_.end())
        << "BB #" << pred_bb->GetBlockId();
      HBasicBlock* pred_bb_copy = old_to_new_bbs_.Get(pred_bb);

      // Link the two copies.
      bb_copy->AddPredecessor(pred_bb_copy);
    }
    last_bb = src_bb;
  }

  // Update the dst body with new entry and tail blocks.
  dst_body.entry_block_ = old_to_new_bbs_.Get(loop_->GetHeader());
  if (last_bb == nullptr) {
    dst_body.tail_block_ = nullptr;
  } else {
    dst_body.tail_block_ = old_to_new_bbs_.Get(last_bb);
  }
}

void HLoopUnrolling::CloneInstructions(HInstructionCloner& cloner, uint64_t iteration_count) const {
  // For each BB of the source body, clone the instructions and copy them to the dest.
  for (HBlocksInLoopIterator it_loop(*loop_); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* bb = it_loop.Current();

    // There is nothing to copy for this basic block.
    if (bb->IsSingleGoto()) {
      continue;
    }

    if (loop_->GetNumIterations(bb) <= static_cast<int64_t>(iteration_count)) {
      continue;
    }

    // At this point, we don't need to handle loop header phi nodes because we already
    // handled them and they should not appear in the unrolled body.
    if (bb != loop_->GetHeader()) {
      for (HInstructionIterator it(bb->GetPhis()); !it.Done(); it.Advance()) {
        HInstruction* phi = it.Current();
        phi->Accept(&cloner);
      }
    }

    // Let's copy the loop instructions.
    for (HInstructionIterator it(bb->GetInstructions()); !it.Done(); it.Advance()) {
      HInstruction* insn = it.Current();

      // We want to filter insns that we do not want to copy.
      if (IsUselessInstruction(insn) || IsLoopConditionInstruction(insn)) {
        continue;
      }

      // Otherwise, clone the insn.
      insn->Accept(&cloner);
    }
  }
}

void HLoopUnrolling::CopyBody(LoopBody& dst, uint64_t iteration_count) {
  // First, create the basic blocks for the destination.
  CreateBasicBlocks(dst, iteration_count);

  // Then, we clone all the instructions.
  CloneInstructions(cloner_, iteration_count);
}

void HLoopUnrolling::ListLoopConditionInstructions() {
  // Last instruction should be the loop control flow, and we do not need it.
  HInstruction* control_flow = loop_->GetExitBlock()->GetPredecessors().Get(0)->GetLastInstruction();
  loop_condition_instructions_.insert(control_flow);
  for (HInputIterator inputs(control_flow); !inputs.Done(); inputs.Advance()) {
    HInstruction* input = inputs.Current();
    // If control_flow is the only use of its input, then we consider it useless.
    if (input->HasOnlyOneNonEnvironmentUse()) {
      loop_condition_instructions_.insert(input);
    }
  }
}

bool HLoopUnrolling::IsLoopConditionInstruction(HInstruction* insn) const {
  return loop_condition_instructions_.find(insn) != loop_condition_instructions_.end();
}

bool HLoopUnrolling::MapOutOfLoopPhiNodes() {
  HBasicBlock* loop_header = loop_->GetHeader();
  // We go through the loop header phi nodes only, because they are the only nodes
  // we need to take care of initially.
  for (HInstructionIterator it(loop_header->GetPhis()); !it.Done(); it.Advance()) {
    HPhi* phi = it.Current()->AsPhi();
    DCHECK(phi->IsLoopHeaderPhi());

    // It is a requirement that loop has a single preheader so it must
    // be the case that the phi has only two inputs. We make the assumption
    // of this in the logic above so recheck it now.
    DCHECK_EQ(phi->InputCount(), 2u);

    // Loop phis can be skipped because this is full unrolling.
    // However, in the mapping table we want to make it so that the input to phi
    // from define outside of loop is the actual mapping.
    HInstruction* out_of_loop_input = loop_->PhiInput(phi, false);

    // Then, we map the phi node to the clone.
    cloner_.AddCloneManually(phi, out_of_loop_input);
  }

  return true;
}

bool HLoopUnrolling::UpdateLoopPhiNodesMap() {
  HBasicBlock* loop_header = loop_->GetHeader();
  // The phi nodes are updated virtually "in parallel". That means that if one loop header phi
  // depends on another, we should not introduce an ordering dependency between them, but take
  // their old values instead. This temp mapping takes care of that.
  SafeMap<HInstruction*, HInstruction*> temp_mapping;

  // We want to update the loop header phi nodes to use their latest in-loop inputs.
  for (HInstructionIterator it(loop_header->GetPhis()); !it.Done(); it.Advance()) {
    HPhi* phi = it.Current()->AsPhi();
    DCHECK(phi->IsLoopHeaderPhi());
    DCHECK_EQ(phi->InputCount(), 2u);

    HInstruction* in_loop_input = loop_->PhiInput(phi, true);

    // We map it to the latest cloned instruction.
    HInstruction* to_update = cloner_.GetClone(in_loop_input);

    // Save it in the temporary mapping (if any clone).
    if (to_update != nullptr) {
      temp_mapping.Put(phi, to_update);
    } else {
      // Otherwise, no clone means the in-loop input is not in the loop body;
      // So we need to map the phi node to this in-loop input.
      temp_mapping.Put(phi, in_loop_input);
    }
  }

  // Now that temporary mapping is done, we go through the list again to update the final
  // mapping that will be used for the loop instructions.
  for (HInstructionIterator it(loop_header->GetPhis()); !it.Done(); it.Advance()) {
    HInstruction* phi = it.Current();

    // At this point, we should have at least one mapping per loop header phi.
    DCHECK(temp_mapping.find(phi) != temp_mapping.end()) << phi;
    HInstruction* map_insn = temp_mapping.Get(phi);

    // We map the original phi node to the latest clone.
    cloner_.AddOrUpdateCloneManually(phi, map_insn);
  }

  return true;
}

void HLoopUnrolling::AppendInstructions(const LoopBody& new_body, uint64_t iteration_count) {
  for (HBlocksInLoopReversePostOrderIterator bb_it(*loop_); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* orig_bb = bb_it.Current();

    // Skip empty basic blocks.
    if (orig_bb->IsSingleGoto()) {
      continue;
    }

    // We need to skip the unrolling of some basic blocks during the last iteration because
    // some of them are executed one less times. This is the case for top tested loops:
    // The loop header enters and exits the loop, thus it is executed one more time
    // than the other basic blocks.
    // However, we still need to duplicate the loop header, because there might be important
    // instructions there. An example is:
    //
    // while (++i < n);
    //
    // In this case, the loop header will contain the phi nodes and the loop condition, but also
    // the i incrementation, which needs to be duplicated an extra time.
    if (loop_->GetNumIterations(orig_bb) <= static_cast<int64_t>(iteration_count)) {
      continue;
    }

    // Retrieve the new copy of the original loop basic block.
    DCHECK(old_to_new_bbs_.find(orig_bb) != old_to_new_bbs_.end());
    HBasicBlock* copy_bb = old_to_new_bbs_.Get(orig_bb);

    // Let's add phi nodes. We don't need to copy loop header phi nodes because we are removing
    // the loop.
    for (HInstructionIterator phi_it(orig_bb->GetPhis()); !phi_it.Done(); phi_it.Advance()) {
      HPhi* phi = phi_it.Current()->AsPhi();
      DCHECK(phi != nullptr);

      if (!phi->IsLoopHeaderPhi()) {
        HInstruction* cloned_insn = cloner_.GetClone(phi);
        if (cloned_insn != nullptr) {
          HPhi* cloned_phi = cloned_insn->AsPhi();
          if (cloned_phi != nullptr) {
            copy_bb->AddPhi(cloned_phi);
          }
        }
      }
    }

    // Add the instructions to the new basic block, except the ones we don't want to.
    for (HInstructionIterator insn_it(orig_bb->GetInstructions());
         !insn_it.Done();
         insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();

      if (IsUselessInstruction(insn) || IsLoopConditionInstruction(insn)) {
        continue;
      }

      // Add the instruction to the new basic block.
      HInstruction* toAdd = cloner_.GetClone(insn);
      if (toAdd != nullptr && toAdd->GetBlock() == nullptr) {
        // We do it only if it is a clone not if it already exists.
        copy_bb->AddInstruction(toAdd);
      }
    }
    if (copy_bb->GetLastInstruction() == nullptr ||
        !copy_bb->GetLastInstruction()->IsGoto()) {
      // Finally, add a goto instruction at the end of the basic block.
      copy_bb->AddInstruction(new (graph_->GetArena()) HGoto());
    }
  }

  // Append the new bbs to the unrolled body list.
  unrolled_body_.block_list_.insert(new_body.block_list_.begin(), new_body.block_list_.end());

  // If there is no entry block to the unrolled body yet, let's add the first copied basic block.
  if (unrolled_body_.entry_block_ == nullptr) {
    unrolled_body_.entry_block_ = new_body.entry_block_;
  }
  if (unrolled_body_.tail_block_ != nullptr) {
    DCHECK(new_body.entry_block_ != nullptr);
    // If unrolling has already started, link the previous tail to the new body entry.
    unrolled_body_.tail_block_->AddSuccessor(new_body.entry_block_);
  }
  // Finally, update the tail to the tail of the new body.
  unrolled_body_.tail_block_ = new_body.tail_block_;
}

void HLoopUnrolling::FixLoopUsers() {
  for (HBlocksInLoopReversePostOrderIterator bb_it(*loop_); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* orig_bb = bb_it.Current();

    if (orig_bb->IsSingleGoto()) {
      continue;
    }

    // First, let's replace the loop phi nodes users.
    for (HInstructionIterator phi_it(orig_bb->GetPhis()); !phi_it.Done(); phi_it.Advance()) {
      HPhi* phi = phi_it.Current()->AsPhi();
      HInstruction* last_replacement = cloner_.GetClone(phi);
      DCHECK(last_replacement != nullptr);
      phi->ReplaceWith(last_replacement);
    }

    // Then, let's replace the loop instructions users.
    for (HInstructionIterator insn_it(orig_bb->GetInstructions());
         !insn_it.Done();
         insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();

      // Skip the instructions we did not copy.
      if (IsUselessInstruction(insn) || IsLoopConditionInstruction(insn)) {
        continue;
      }

      HInstruction* last_replacement = cloner_.GetClone(insn);
      DCHECK(last_replacement != nullptr);
      insn->ReplaceWith(last_replacement);
    }
  }
}

bool HLoopUnrolling::UnrollBody(uint64_t unrolling_factor) {
  // Save the out-of-loop phi nodes values in the instruction cloner map.
  if (!MapOutOfLoopPhiNodes()) {
    PRINT_PASS_OSTREAM_MESSAGE(optim_, "Could not map out phi nodes.");
    return false;
  }

  // We want to list the condition and all of the inputs to prevent copying them
  // during the unrolling.
  ListLoopConditionInstructions();

  // For each unrolling factor, let's unroll the loop.
  for (uint64_t i = 0; i < unrolling_factor; i++) {
    LoopBody tmp_copy;
    // Copy the instruction body.
    CopyBody(tmp_copy, i);

    // Make sure that the instruction cloner successfully cloned all the instructions in the loop.
    DCHECK(cloner_.AllOkay()) << "Could not copy the loop body successfully.";

    // Update the loop phi nodes map with the newly cloned in-loop inputs, unless it is the last
    // iteration, because in this case it is not necessary.
    if ((i != unrolling_factor - 1) && !UpdateLoopPhiNodesMap()) {
      PRINT_PASS_OSTREAM_MESSAGE(optim_, "Could not update phi nodes map.");
      return false;
    }

    // Append instructions to the unrolled body.
    AppendInstructions(tmp_copy, i);
  }

  // We need to fix the users of the loop instructions to use the instructions cloned during
  // the last unrolling iteration.
  FixLoopUsers();

  return true;
}

void HLoopUnrolling::UpdateGraph() {
  HBasicBlock* loop_pre_header = loop_->GetPreHeader();
  HBasicBlock* exit_block = loop_->GetExitBlock();
  DCHECK(exit_block != nullptr);
  DCHECK(unrolled_body_.entry_block_ != nullptr);
  DCHECK(unrolled_body_.tail_block_ != nullptr);

  // Remove the loop from the graph.
  loop_->RemoveFromGraph();

  // Fix the links of the pre-header and the exit block.
  loop_pre_header->ReplaceSuccessor(exit_block, unrolled_body_.entry_block_);
  exit_block->AddPredecessor(unrolled_body_.tail_block_);
}

bool HLoopUnrolling::FullUnroll() {
  uint64_t num_iterations = loop_->GetNumIterations(loop_->GetHeader());

  // Unroll the loop body.
  if (!UnrollBody(num_iterations)) {
    return false;
  }

  // Finally, we want to update the loop's graph to insert the unrolled body.
  UpdateGraph();

  // Before finishing, we rebuild the domination graph to handle the new structure of the graph.
  graph_->RebuildDomination();

  return true;
}

bool HLoopUnrolling::Gate(uint64_t max_unrolled_instructions) const {
  if (loop_ == nullptr) {
    PRINT_PASS_OSTREAM_MESSAGE(optim_, "Loop is nullptr");
    return false;
  }

  if (!loop_->HasKnownNumIterations()) {
    PRINT_PASS_OSTREAM_MESSAGE(optim_, "Loop must have a known number of iterations.");
    return false;
  }

  uint64_t num_iterations = loop_->GetNumIterations(loop_->GetHeader());

  if ((loop_->IsBottomTested() && num_iterations < 2u) ||
      (!loop_->IsBottomTested() && num_iterations < 3u)) {
    PRINT_PASS_OSTREAM_MESSAGE(optim_, "Loop has one or less iterations: "
      "loop unrolling is useless.");
    return false;
  }

  if (!loop_->HasOneExitEdge()) {
    PRINT_PASS_OSTREAM_MESSAGE(optim_, "Loop must have one exit edge.");
    return false;
  }

  if (loop_->GetBackEdges().Size() > 1u) {
    PRINT_PASS_OSTREAM_MESSAGE(optim_, "Loop must have one back edge.");
    return false;
  }

  // TODO: As future work, we can remove this limitation.
  if (loop_->NumberOfBlocks() > 2) {
    PRINT_PASS_OSTREAM_MESSAGE(optim_, "Loop must have less than three basic blocks.");
    return false;
  }

  uint64_t nb_instructions = loop_->CountInstructionsInBody(true);
  uint64_t nb_unrolled_instructions = num_iterations * nb_instructions;
  if (nb_unrolled_instructions > max_unrolled_instructions) {
    PRINT_PASS_OSTREAM_MESSAGE(optim_, "Number of unrolled instructions ("
      << nb_unrolled_instructions << ") is too large (max: " << max_unrolled_instructions << ")");
    return false;
  }

  // Verify that the instructions can be all cloned by the instruction cloner.
  // Return false if it was not successful.
  {
    HInstructionCloner instruction_verifier(graph_, nullptr, false);
    CloneInstructions(instruction_verifier, 0u);
    if (!instruction_verifier.AllOkay()) {
      PRINT_PASS_OSTREAM_MESSAGE(optim_, "The loop body cannot be copied entirely."
        " because of instruction: " << instruction_verifier.GetDebugNameForFailedClone());
      return false;
    }
  }

  return true;
}
}  // namespace art
