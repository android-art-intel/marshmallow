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

#include "form_bottom_loops.h"

#include "cloning.h"
#include "ext_utility.h"
#include "graph_x86.h"
#include "loop_iterators.h"

// For debugging purposes.
#ifdef HAVE_ANDROID_OS
#include "cutils/properties.h"
#endif

namespace art {

void HFormBottomLoops::Run() {
  PRINT_PASS_OSTREAM_MESSAGE(this, "Begin: " << GetMethodName(c_unit_));
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86* graph_loop_info = graph->GetLoopInformation();

  EnvToInstrMap env_to_instr;
  bool env_to_instr_initialized = false;

  // Walk all the inner loops in the graph.
  bool changed = false;
  for (HOnlyInnerLoopIterator it(graph_loop_info); !it.Done(); it.Advance()) {
    HLoopInformation_X86* loop_info = it.Current();
    HBasicBlock* pre_header = loop_info->GetPreHeader();

    PRINT_PASS_OSTREAM_MESSAGE(this, "Visit " << loop_info->GetHeader()->GetBlockId()
                                     << ", preheader = " << pre_header->GetBlockId());

    // The exit block from the loop.
    HBasicBlock* loop_header = loop_info->GetHeader();
    HBasicBlock* exit_block = loop_info->GetExitBlock();

    if (!ShouldTransformLoop(loop_info, loop_header, exit_block)) {
      // Loop is already correct, or not valid for rewrite.
      continue;
    }

    if (!env_to_instr_initialized) {
      graph->FillEnvironmentToInstruction(&env_to_instr);
      env_to_instr_initialized = true;
    }
    RewriteLoop(loop_info, env_to_instr, loop_header, exit_block);
    MaybeRecordStat(MethodCompilationStat::kIntelFormBottomLoop);
    changed = true;
  }

  if (changed) {
    // Rebuild the loop data structures.
    graph->RebuildDomination();
  }
  PRINT_PASS_OSTREAM_MESSAGE(this, "End: " << GetMethodName(c_unit_));
}

static bool IsSingleGotoBackEdge(HBasicBlock* bb) {
  HLoopInformation* loop_info = bb->GetLoopInformation();
  return bb->GetFirstInstruction() != nullptr
         && bb->GetPhis().IsEmpty()
         && bb->GetFirstInstruction() == bb->GetLastInstruction()
         && bb->GetLastInstruction()->IsGoto()
         && (loop_info != nullptr && loop_info->IsBackEdge(*bb));
}

bool HFormBottomLoops::ShouldTransformLoop(HLoopInformation_X86* loop_info,
                                           HBasicBlock* loop_header,
                                           HBasicBlock* exit_block) {
  if (exit_block == nullptr) {
    // We need exactly 1 exit block from the loop.
    PRINT_PASS_MESSAGE(this, "Too many or too few exit blocks");
    return false;
  }

  // Exit block is alone and it always has one predecessor due to
  // critical edge elimination.
  DCHECK_EQ(exit_block->GetPredecessors().Size(), static_cast<size_t>(1));
  HBasicBlock* loop_to_exit = exit_block->GetPredecessors().Get(0);

  HBasicBlock* first_back_edge = loop_info->GetBackEdges().Get(0);

  // Is this a top tested loop?
  HInstruction* last_insn = loop_header->GetLastInstruction();
  DCHECK(last_insn != nullptr);
  if (!last_insn->IsIf()) {
    PRINT_PASS_MESSAGE(this, "Loop header doesn't end with HIf");

    // Perhaps this loop is already bottom tested.
    // We must ensure that:
    // 1) loop_to_exit ends up with if.
    // 2) There is only one back edge.
    // 3) back edge is a successor of loop_to_exit.
    // 4) back edge is single goto.
    bool is_bottom_tested =
      (loop_to_exit->GetLastInstruction()->IsIf()) &&
      (loop_info->NumberOfBackEdges() == 1) &&
      (first_back_edge->GetPredecessorIndexOf(loop_to_exit) != static_cast<size_t>(-1)) &&
      (IsSingleGotoBackEdge(first_back_edge));

    if (is_bottom_tested) {
      PRINT_PASS_MESSAGE(this, "Loop is already bottom tested");
      loop_info->SetBottomTested(true);
    }

    return false;
  }

  // We don't know how to rewrite a loop with multiple back edges at this time.
  // TODO: Support multiple back edges if a need arises.
  if (loop_info->NumberOfBackEdges() != 1) {
    PRINT_PASS_MESSAGE(this, "More than one back edge");
    return false;
  }

  // Does the loop header exit the loop, making it top tested?
  if (loop_to_exit == loop_header) {
    // Perhaps this loop is already bottom tested.
    // We must ensure that:
    // 1) loop_to_exit ends up with if - already done.
    // 2) There is only one back edge - checked above.
    // 3) back edge is a successor of loop_to_exit.
    // 4) back edge is single goto.
    bool is_bottom_tested =
        (first_back_edge->GetPredecessorIndexOf(loop_to_exit) != static_cast<size_t>(-1)) &&
        (IsSingleGotoBackEdge(first_back_edge));
    if (is_bottom_tested) {
      // More complicated single block loop.
      PRINT_PASS_MESSAGE(this, "Complex Loop is already bottom tested (after gotos)");
      loop_info->SetBottomTested(true);
      return false;
    }
  } else {
    PRINT_PASS_MESSAGE(this, "Loop header doesn't exit the loop");
    return false;
  }

  // Check that there aren't more than two Loop phis in the loop header,
  // in order to prevent too much register pressure from values live across the
  // loop.
  size_t num_phis = loop_header->GetPhis().CountSize();
  if (num_phis > 2) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "More than 2 loop phis: " << num_phis);
    return false;
  }

  // If there are lots of blocks in the loop, there may also be more register pressure.
  // Check that we don't have too many blocks in the loop.
  int num_blocks = loop_info->NumberOfBlocks();
  if (num_blocks > 5) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "More than 5 blocks in loop: " << num_blocks);
    return false;
  }

  // Are the instructions in the header okay?
  if (!CheckLoopHeader(loop_info, loop_header)) {
    return false;
  }

  // Leave support for debugging the transformation in debug builds only.
  // Only works with -j1.
  if (kIsDebugBuild) {
    static int max = -1;
    static int count = 0;
    if (max == -1) {
#ifdef HAVE_ANDROID_OS
      char buff[PROPERTY_VALUE_MAX];
      if (property_get("dex2oat.bottom.max", buff, "1000000") > 0) {
        max = atoi(buff);
      }
#else
      char* p = getenv("BOTTOM_MAX");
      max = p ? atoi(p) : 1000000;
#endif
    }
    if (++count > max) {
      PRINT_PASS_MESSAGE(this, "MAX transform count exceeded");
      return false;
    }
  }

  // Looks fine to go.
  return true;
}

void HFormBottomLoops::RewriteLoop(HLoopInformation_X86* loop_info,
                                   EnvToInstrMap& env_to_instr,
                                   HBasicBlock* loop_header,
                                   HBasicBlock* exit_block) {
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HBasicBlock* pre_header = loop_info->GetPreHeader();
  PRINT_PASS_OSTREAM_MESSAGE(this, "Rewrite loop " << loop_header->GetBlockId()
                                    << ", preheader = " << pre_header->GetBlockId());
  PRINT_PASS_OSTREAM_MESSAGE(this, "Exit block = " << exit_block->GetBlockId());

  // Find the first block in the loop after the loop header.  There must be
  // one due to our pre-checks.
  DCHECK(loop_header->GetLastInstruction() != nullptr);
  HIf* if_insn = loop_header->GetLastInstruction()->AsIf();
  DCHECK(if_insn != nullptr);
  bool first_successor_is_exit = if_insn->IfTrueSuccessor() == exit_block;
  HBasicBlock* first_block = first_successor_is_exit
                               ? if_insn->IfFalseSuccessor()
                               : if_insn->IfTrueSuccessor();

  // Map from phis to the inside and output inputs
  // while moving the Phis from the loop_header to the first block.
  std::map<HInstruction*, HInstruction*> phi_to_inside_input;
  std::map<HInstruction*, HInstruction*> phi_to_outside_input;
  bool first_predecessor_is_preheader = loop_header->GetPredecessors().Get(0) == pre_header;
  int outside_phi_index = first_predecessor_is_preheader ? 0 : 1;
  int inside_phi_index = 1 - outside_phi_index;
  std::set<HPhi*> exit_phis;
  HLoopInformation_X86* pre_header_loop_info =
    LOOPINFO_TO_LOOPINFO_X86(pre_header->GetLoopInformation());

  for (HInstructionIterator phi_it(loop_header->GetPhis()); !phi_it.Done(); phi_it.Advance()) {
    HPhi* phi = phi_it.Current()->AsPhi();
    DCHECK_EQ(phi->InputCount(), 2u);
    HInstruction* inside_value = phi->InputAt(inside_phi_index);
    HInstruction* outside_value = phi->InputAt(outside_phi_index);

    PRINT_PASS_OSTREAM_MESSAGE(this, "Process Phi " << phi << " inside input "
                               << inside_value << " outside input " << outside_value);

    if (kIsDebugBuild) {
      // Is the inside input a Phi we have already processed?
      auto it = phi_to_inside_input.find(inside_value);
      if (it != phi_to_inside_input.end()) {
        // This is a reference to a Phi in the header.
        PRINT_PASS_OSTREAM_MESSAGE(this, "Phi " << phi << " has Phi inside input");
      }
    }

    phi_to_inside_input[phi] = inside_value;
    phi_to_outside_input[phi] = outside_value;
    // If the Phi inside input is computed in the loop header, we want external
    // references to use the Phi.  If it is computed in the rest of the loop, we
    // want to use that computed value or the value computed on previous iteration.
    bool computed_in_header = (inside_value->GetBlock() == loop_header && inside_value != phi);
    FixPhiUses(loop_info, env_to_instr, first_block, exit_block, phi, computed_in_header,
               outside_value, inside_value, exit_phis);

    if (computed_in_header) {
      // We are moving the Phi down past the header.  In this case, we want to have
      // the outside input be the value computed in the header.
      phi->ReplaceInput(inside_value, outside_phi_index);
      PRINT_PASS_OSTREAM_MESSAGE(this, "Replace the outside input with the inside value: " << phi);
    }
    graph->MovePhi(phi, first_block);
  }

  // Move the SuspendCheck from the loop header to the first block.
  HSuspendCheck* suspend_check = loop_info->GetSuspendCheck();
  if (suspend_check != nullptr) {
    suspend_check->MoveBefore(first_block->GetFirstInstruction());
  }

  // Now clone the instructions in the loop_header to the bottom of each of the back
  // edges.
  const GrowableArray<HBasicBlock*>& back_edges = loop_info->GetBackEdges();
  std::map<HInstruction*, HInstruction*> cloned_instructions;
  for (size_t i = 0, e = back_edges.Size(); i < e; i++) {
    HBasicBlock* back_block = back_edges.Get(i);
    PRINT_PASS_OSTREAM_MESSAGE(this, "Back block = " << back_block->GetBlockId());

    // We need a fresh cloner for each back edge, as we want different copies of
    // the loop header instructions.
    HInstructionCloner cloner(graph, &env_to_instr);

    // We have to decide what value to use for the Phi for the cloned code, which
    // will be cloned to the back branches.
    // If the instruction is defined in the current loop header, any reference to
    // the Phi needs to stay the Phi.
    // If the instruction is generated elsewhere in the loop, any reference to the
    // Phi should use the generated inside value.
    for (auto it : phi_to_inside_input) {
      HBasicBlock* inside_block = it.second->GetBlock();
      if (inside_block == loop_header) {
        // We want to use the Phi itself.
        PRINT_PASS_OSTREAM_MESSAGE(this, "Use Phi as is : " << it.first);
      } else {
        PRINT_PASS_OSTREAM_MESSAGE(this, "Add Phi manually orig: " << it.first <<
                                         " replacement " << it.second);
        cloner.AddCloneManually(it.first, it.second);
      }
    }

    // Remove the Goto at the end of the block.
    HInstruction* goto_insn = back_block->GetLastInstruction();
    DCHECK(goto_insn != nullptr);
    DCHECK(goto_insn->IsGoto());
    DCHECK_EQ(back_block->GetSuccessors().Size(), 1u);
    DCHECK(back_block->GetSuccessors().Get(0) == loop_header);

    // Create the cloned instructions.
    for (HInstructionIterator inst_it(loop_header->GetInstructions());
                              !inst_it.Done();
                              inst_it.Advance()) {
      HInstruction* insn = inst_it.Current();
      // Clone the instruction.
      insn->Accept(&cloner);
      HInstruction* cloned_insn = cloner.GetClone(insn);

      // Add the cloned instruction to the back block.
      DCHECK(cloned_insn != nullptr);
      if (cloned_insn->GetBlock() == nullptr) {
        back_block->AddInstruction(cloned_insn);
      }
      PRINT_PASS_OSTREAM_MESSAGE(this, "Clone:" << insn << " to " << cloned_insn);
      cloned_instructions[insn] = cloned_insn;
    }

    // Remove the Goto at the end of the block.
    back_block->RemoveInstruction(goto_insn);

    // Fix the successors of the block.
    if (first_successor_is_exit) {
      back_block->ReplaceSuccessor(loop_header, exit_block);
      back_block->AddSuccessor(first_block);
    } else {
      back_block->ReplaceSuccessor(loop_header, first_block);
      back_block->AddSuccessor(exit_block);
    }

    // Ensure that the exit block doesn't get messed up by SplitCriticalEdge.
    HBasicBlock* split_exit_block = graph->CreateNewBasicBlock();
    PRINT_PASS_OSTREAM_MESSAGE(this, "New loop exit block " << split_exit_block->GetBlockId());
    split_exit_block->AddInstruction(new(graph->GetArena()) HGoto());
    if (pre_header_loop_info != nullptr) {
      pre_header_loop_info->AddToAll(split_exit_block);
    }
    split_exit_block->InsertBetween(back_block, exit_block);

    // We also need to ensure that the branch around the loop isn't a critical edge.
    HBasicBlock* around_block = graph->CreateNewBasicBlock();
    PRINT_PASS_OSTREAM_MESSAGE(this, "New around to exit block " << around_block->GetBlockId());
    around_block->AddInstruction(new(graph->GetArena()) HGoto());
    if (pre_header_loop_info != nullptr) {
      pre_header_loop_info->AddToAll(around_block);
    }
    around_block->InsertBetween(loop_header, exit_block);

    // We also need to ensure that the branch to the top of the loop isn't a critical edge.
    HBasicBlock* top_block = graph->CreateNewBasicBlock();
    PRINT_PASS_OSTREAM_MESSAGE(this, "New around to top block " << top_block->GetBlockId());
    top_block->AddInstruction(new(graph->GetArena()) HGoto());
    loop_info->AddToAll(top_block);
    top_block->InsertBetween(back_block, first_block);
    loop_info->ReplaceBackEdge(back_block, top_block);
  }

  // Walk the instructions in the loop header replacing references to the phi with
  // the outside input.
  for (HInstructionIterator insn_it(loop_header->GetInstructions());
       !insn_it.Done();
       insn_it.Advance()) {
    HInstruction* insn = insn_it.Current();

    for (size_t i = 0, e = insn->InputCount(); i < e; i++) {
      HInstruction* input = insn->InputAt(i);
      auto it = phi_to_outside_input.find(input);
      if (it != phi_to_outside_input.end()) {
        // Replace with the correct input.
        PRINT_PASS_OSTREAM_MESSAGE(this, "Replace " << it->first << " with "
                                         << it->second << " in " << insn);
        insn->ReplaceInput(it->second, i);
      }
    }

    // Walk the environment uses.
    if (insn->HasEnvironment()) {
      for (HEnvironment* env = insn->GetEnvironment(); env != nullptr; env = env->GetParent()) {
        for (size_t i = 0, e = env->Size(); i < e; i++) {
          HInstruction* env_use = env->GetInstructionAt(i);
          if (env_use == nullptr) {
            continue;
          }
          auto it = phi_to_outside_input.find(env_use);
          if (it != phi_to_outside_input.end()) {
            // Replace with the correct input.
            env->RemoveAsUserOfInput(i);
            env->SetRawEnvAt(i, it->second);
            PRINT_PASS_OSTREAM_MESSAGE(this, "Replace " << it->first << " with "
                                              << it->second << " in environment("
                                              << i << ") of " << insn);
            it->second->AddEnvUseAt(env, i);
          }
        }
      }
    }
  }


  // Handle Phi inputs that were generated in the old loop header (cloned to back edge).
  for (HInstructionIterator phi_it(first_block->GetPhis()); !phi_it.Done(); phi_it.Advance()) {
    HPhi* phi = phi_it.Current()->AsPhi();
    auto it = cloned_instructions.find(phi->InputAt(inside_phi_index));
    if (it != cloned_instructions.end()) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Examine loop Phi: " << phi);
      phi->ReplaceInput(it->second, inside_phi_index);
      PRINT_PASS_OSTREAM_MESSAGE(this, "Replaced by: " << phi);
    }
  }

  // Fix up the exit phis as well.
  for (auto phi : exit_phis) {
    auto clone_it = cloned_instructions.find(phi->InputAt(inside_phi_index));
    if (clone_it != cloned_instructions.end()) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Fix exit loop phi: " << phi);
      // Replace the inside reference with the clone of the inside value.
      phi->ReplaceInput(clone_it->second, inside_phi_index);
      PRINT_PASS_OSTREAM_MESSAGE(this, "Replaced by: " << phi);
    }
  }

  // Handle header uses in exit blocks.
  for (auto insn : header_values_used_in_exit_block) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Fixup header insn used in exit block: " << insn);
    auto clone_it = cloned_instructions.find(insn);
    DCHECK(clone_it != cloned_instructions.end());
    PRINT_PASS_OSTREAM_MESSAGE(this, "Clone used: " << clone_it->second);
    // We have to create a new phi in the exit block, and fix up the uses.
    HPhi* new_phi = new (graph_->GetArena())
      HPhi(graph_->GetArena(), -1, 0, HPhi::ToPhiType(insn->GetType()));
    exit_block->AddPhi(new_phi);

    // Replace the uses in the exit block with the Phi we just made.
    for (HUseIterator<HInstruction*> it(insn->GetUses()); !it.Done(); it.Advance()) {
      HInstruction* use = it.Current()->GetUser();
      HBasicBlock* use_block = use->GetBlock();
      if (!loop_info->Contains(*use_block)) {
        size_t index = it.Current()->GetIndex();
        PRINT_PASS_OSTREAM_MESSAGE(this, "Replace input " << index << " in " << use);
        use->ReplaceInput(new_phi, index);
      }
    }

    // Add the Phi inputs in the correct order.
    DCHECK_EQ(exit_block->GetPredecessors().Size(), 2u);
    // Find the new block leading to the exit block from the header.
    HBasicBlock* new_around_to_exit =
      loop_header->GetSuccessors().Get(first_successor_is_exit ? 0 : 1);
    if (exit_block->GetPredecessors().Get(0) == new_around_to_exit) {
      new_phi->AddInput(insn);
      new_phi->AddInput(clone_it->second);
    } else {
      DCHECK(exit_block->GetPredecessors().Get(1) == new_around_to_exit);
      new_phi->AddInput(clone_it->second);
      new_phi->AddInput(insn);
    }
    PRINT_PASS_OSTREAM_MESSAGE(this, "Created Exit block fixup Phi: " << new_phi);
  }

  // Ensure that a new block loop header doesn't get messed up by SplitCriticalEdge.
  if (loop_header->GetSuccessors().Size() > 1 &&
      first_block->GetPredecessors().Size() > 1) {
    HBasicBlock* new_block = graph->CreateNewBasicBlock();
    PRINT_PASS_OSTREAM_MESSAGE(this, "Fixing up loop pre-header for " <<
                                     first_block->GetBlockId() <<
                                     ", new block = " << new_block->GetBlockId());
    new_block->AddInstruction(new(graph->GetArena()) HGoto());
    if (pre_header_loop_info != nullptr) {
      pre_header_loop_info->AddToAll(new_block);
    }
    new_block->InsertBetween(loop_header, first_block);
  }

  // Fix up the loop_info.
  loop_info->SetHeader(first_block);
  loop_info->Remove(loop_header);
  loop_header->SetLoopInformation(pre_header_loop_info);
  loop_info->SetBottomTested(true);
}

void HFormBottomLoops::FixPhiUses(HLoopInformation_X86* loop_info,
                                  EnvToInstrMap& env_to_instr,
                                  HBasicBlock* first_block,
                                  HBasicBlock* exit_block,
                                  HPhi* phi,
                                  bool computed_in_header,
                                  HInstruction* outside_value,
                                  HInstruction* inside_value,
                                  std::set<HPhi*>& exit_phis) {
  // We may need phi for the merging the values on exit.
  HPhi* new_phi = nullptr;
  // The loop
  //    (Phi3, def, If, use)
  // will be transformed to
  //    Phi1 == outside_value, def1, If, (Phi2, use, Phi3 == inside_value, def2, If)
  // Phi1 and Phi3 will be eliminated but they show that *use* should
  // have the value of Phi1 or Phi3. Phi3 is an inside value but it does not
  // correspond to Phi2 inside value which will be equal to def2.
  // So for this case we will need a new Phi merging outside_value and def2
  // but on previous iteration. In other words we may need Phi (outside_value, Phi2).
  HPhi* new_pre_phi = nullptr;

  // Create Phi utility.
  auto create_phi_node = [&] (int reg_num, HBasicBlock* block, HInstruction* inside_input) {
    HPhi* res_phi = new (graph_->GetArena())
      HPhi(graph_->GetArena(), reg_num, 0, HPhi::ToPhiType(phi->GetType()));
    block->AddPhi(res_phi);
    res_phi->AddInput(outside_value);
    res_phi->AddInput(inside_input);
    PRINT_PASS_OSTREAM_MESSAGE(this, "Created Fixup Phi: " << res_phi);
    return res_phi;
  };

  HBasicBlock* loop_header = loop_info->GetHeader();
  // Find (or create if not created yet) the Phi node to use in
  // substitution. Returns null if no update is required.
  auto find_phi_to_replace = [&] (HBasicBlock* user_block) {
    HPhi* result = nullptr;
    if (loop_info->Contains(*user_block)) {
      // If use is inside loop and Phi was computed in header
      // then use after the header should use the value on
      // the previous iteration than Phi node will represent.
      if (computed_in_header && user_block != loop_header) {
        if (new_pre_phi == nullptr) {
          // We cannot use Phi register number because basic block
          // cannot contains the Phi nodes coressponding to the
          // same register number. first_block will contain both
          // phi node and phi we are generating now.
          new_pre_phi = create_phi_node(-1, first_block, phi);
        }
        result = new_pre_phi;
      }
    } else {
      // If use is outside of the loop we should merge the value
      // of cloned instruction and instruction moved to old back edge.
      if (new_phi == nullptr) {
        new_phi = create_phi_node(phi->GetRegNumber(), exit_block,
                                  computed_in_header ? phi : inside_value);
        exit_phis.insert(new_phi);
      }
      result = new_phi;
    }
    return result;
  };

  // Walk through the instruction uses of the original Phi.
  for (HUseIterator<HInstruction*> it(phi->GetUses()); !it.Done(); it.Advance()) {
    HInstruction* user = it.Current()->GetUser();

    HPhi* phi_to_replace_input = find_phi_to_replace(user->GetBlock());

    if (phi_to_replace_input != nullptr) {
      size_t index = it.Current()->GetIndex();
      user->ReplaceInput(phi_to_replace_input, index);
    }
  }

  // Walk through the environment uses of the original Phi.
  for (HUseIterator<HEnvironment*> it(phi->GetEnvUses()); !it.Done(); it.Advance()) {
    HEnvironment* user = it.Current()->GetUser();

    HPhi* phi_to_replace_input = find_phi_to_replace(env_to_instr.Get(user)->GetBlock());

    if (phi_to_replace_input != nullptr) {
      size_t input_index = it.Current()->GetIndex();
      user->RemoveAsUserOfInput(input_index);
      user->SetRawEnvAt(input_index, phi_to_replace_input);
      phi_to_replace_input->AddEnvUseAt(user, input_index);
    }
  }
}

bool HFormBottomLoops::CheckLoopHeader(HLoopInformation_X86* loop_info, HBasicBlock* loop_header) {
  // Check for Phis with inputs that are other Phis further down this block.
  // If so, we can't successfully move those Phis to the exit block.
  std::set<HPhi*> seen_phis;
  for (HInstructionIterator inst_it(loop_header->GetPhis());
       !inst_it.Done();
       inst_it.Advance()) {
    HPhi* phi = inst_it.Current()->AsPhi();
    for (size_t i = 1, e = phi->InputCount(); i < e; i++) {
      HPhi* input = phi->InputAt(i)->AsPhi();
      if (input != nullptr && input->GetBlock() == loop_header) {
        // Is this a Phi from later in the block?
        if (seen_phis.find(input) == seen_phis.end()) {
          // Reference to another Phi in this block that we haven't processed yet.
          PRINT_PASS_OSTREAM_MESSAGE(this, "Forward Phi reference: " << phi);
          return false;
        }
        // We also want to disable the case if our input is Phi and there is an
        // use outside of the loop.
        for (HUseIterator<HInstruction*> it(phi->GetUses()); !it.Done(); it.Advance()) {
          HInstruction* user = it.Current()->GetUser();
          if (!loop_info->Contains(*user->GetBlock())) {
            PRINT_PASS_OSTREAM_MESSAGE(this, "Outside complex Phi usage: " << phi);
            return false;
          }
        }
      }
      seen_phis.insert(phi);
    }
  }

  // Walk through the instructions in the loop, looking for instructions that aren't
  // clonable, or that have results that are used outside of this block.
  HInstructionCloner cloner(GRAPH_TO_GRAPH_X86(graph_), nullptr, false);
  header_values_used_in_exit_block.clear();
  for (HInstructionIterator inst_it(loop_header->GetInstructions());
       !inst_it.Done();
       inst_it.Advance()) {
    HInstruction* insn = inst_it.Current();

    PRINT_PASS_OSTREAM_MESSAGE(this, "Look at: " << insn);

    switch (insn->GetKind()) {
      case HInstruction::kSuspendCheck:
        // Special case SuspendCheck.  We don't care about it.
        continue;
      case HInstruction::kIf:
        // If the instruction is HIf, then we must have checked it in the gate.
        continue;
      default:
        if (insn->IsControlFlow() && !insn->CanThrow()) {
          // We can't handle any control flow except an HIf.
          PRINT_PASS_MESSAGE(this, "Instruction has control flow");
          return false;
        }

        // All the uses of the instruction must be within this block.
        if (insn->GetType() != Primitive::kPrimVoid) {
          for (HUseIterator<HInstruction*> it2(insn->GetUses()); !it2.Done(); it2.Advance()) {
            HInstruction* use = it2.Current()->GetUser();
            HBasicBlock* use_block = use->GetBlock();
            if (!loop_info->Contains(*use_block)) {
              // We can fix this up later.
              PRINT_PASS_OSTREAM_MESSAGE(this, "Result is used by: "
                                                << use << " in/after exit block "
                                                << use_block->GetBlockId());
              if (insn->HasEnvironmentUses()) {
                PRINT_PASS_OSTREAM_MESSAGE(this, "Instruction used in/after exit block has environment uses");
                return false;
              }
              header_values_used_in_exit_block.insert(insn);
            } else if (use_block != loop_header) {
              PRINT_PASS_OSTREAM_MESSAGE(this, "Result is used by: "
                                                << use << " in block "
                                                << use_block->GetBlockId());
              return false;
            }
          }
        }

        // Can we clone this instruction?
        insn->Accept(&cloner);
        if (!cloner.AllOkay()) {
          PRINT_PASS_MESSAGE(this, "Unable to clone");
          return false;
        }
        break;
    }
  }

  // All instructions are okay.
  return true;
}

}  // namespace art
