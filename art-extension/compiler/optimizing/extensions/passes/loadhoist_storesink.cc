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


#include "ext_alias.h"
#include "ext_utility.h"
#include "loadhoist_storesink.h"
#include "loop_iterators.h"
#include "nodes.h"
#include "ssa_builder.h"
#include <unordered_set>

namespace art {

void LoadHoistStoreSink::Run() {
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86* loop_start = graph->GetLoopInformation();
  // For each inner loop in the graph.
  bool graph_changed = false;
  for (HOnlyInnerLoopIterator it(loop_start); !it.Done(); it.Advance()) {
    HLoopInformation_X86* loop = it.Current();

    // First, we need to check that the loop respect some criteria.
    if (LoopGate(loop) == false) {
      continue;
    }

    GetSetToHoistSink get_to_set;
    SetsToSink sets_to_sink;
    if (!FindLoadStoreCouples(loop, get_to_set, sets_to_sink)) {
      // We found a dangerous instruction.  Ignore this loop.
      continue;
    }

    PRINT_PASS_OSTREAM_MESSAGE(this, "There are " << get_to_set.size()
                                      << " get/set pairs and "
                                      << sets_to_sink.size()
                                      << " sets to sink out of the loop.");

    graph_changed |= DoLoadHoistStoreSink(loop, get_to_set, sets_to_sink);
  }

  if (graph_changed) {
    graph->RebuildDomination();
  }
}

static HInstruction* GetValueToSet(HInstruction* set) {
  DCHECK(set != nullptr);

  if (set->IsArraySet()) {
    return set->InputAt(2);
  } else {
    return set->InputAt(1);
  }
}

static void ReplaceValueToSet(HInstruction* set, HInstruction* value) {
  DCHECK(set != nullptr);

  if (set->IsArraySet()) {
    set->ReplaceInput(value, 2);
  } else {
    set->ReplaceInput(value, 1);
  }
}

static bool IsSet(HInstruction* insn) {
  DCHECK(insn != nullptr);
  HInstruction::InstructionKind kind = insn->GetKind();
  return kind == HInstruction::kStaticFieldSet ||
         kind == HInstruction::kInstanceFieldSet ||
         kind == HInstruction::kArraySet;
}

static bool CopySetInSuspendBlock(HInstruction* set, HLoopInformation_X86* loop,
                                  HInstruction* value_to_store,
                                  ArenaAllocator* arena) {
  DCHECK(set != nullptr);
  DCHECK(loop != nullptr);
  DCHECK(!set->HasEnvironment());

  HInstruction* to_copy = nullptr;

  if (set->IsStaticFieldSet()) {
    HStaticFieldSet* sfs = set->AsStaticFieldSet();

    to_copy = new (arena) HStaticFieldSet(sfs->InputAt(0),
                                  value_to_store, sfs->GetFieldType(),
                                  sfs->GetFieldOffset(), sfs->IsVolatile());
  } else if (set->IsInstanceFieldSet()) {
    HInstanceFieldSet* ifs = set->AsInstanceFieldSet();

    to_copy = new (arena) HInstanceFieldSet(ifs->InputAt(0),
                                   value_to_store, ifs->GetFieldType(),
                                   ifs->GetFieldOffset(), ifs->IsVolatile());
  } else if (set->IsArraySet()) {
    HArraySet* as = set->AsArraySet();

    to_copy = new (arena) HArraySet(as->GetArray(), as->GetIndex(), value_to_store,
                                    as->GetComponentType(), as->GetDexPc());
  } else {
    return false;
  }

  // Now that we have our copy, let's add it to the loop suspend block.
  return loop->InsertInstructionInSuspendBlock(to_copy);
}

static bool HasSuspendPoint(HLoopInformation_X86* loop) {
  DCHECK(loop != nullptr);

  return loop->HasSuspendCheck() || loop->HasSuspend();
}

bool LoadHoistStoreSink::DoLoadHoistStoreSink(HLoopInformation_X86* loop,
                                              GetSetToHoistSink& get_to_set,
                                              SetsToSink& sets_to_sink) {
  DCHECK(loop != nullptr);

  HBasicBlock* pre_header = loop->GetPreHeader();
  HBasicBlock* loop_header = loop->GetHeader();
  HBasicBlock* exit_block = loop->GetExitBlock();
  ArenaAllocator* arena = graph_->GetArena();
  bool has_suspend = HasSuspendPoint(loop);
  bool suspend_block_created = false;

  // If there is nothing to hoist/sink, we can exit now.
  if (get_to_set.size() == 0u && sets_to_sink.size() == 0u) {
    return false;
  }

  // Paranoid: Loop must have a pre-header.
  DCHECK(pre_header != nullptr);

  // Paranoid.
  DCHECK(loop_header != nullptr);

  // Paranoid: There should be an exit block at this point since there is exactly one exit edge.
  CHECK(exit_block != nullptr);

  // The exit block must be the right place to put the stores.  Ensure that it isn't
  // shared with the branch around the loop.
  DCHECK_EQ(exit_block->GetPredecessors().Size(), 1u);

  // Hoist the load to the pre-header and sink the store to the exit block.
  for (auto load_store = get_to_set.rbegin();
       load_store != get_to_set.rend();
       load_store++) {
    HInstruction* get = load_store->first;
    HInstruction* set = load_store->second;

    HInstruction* value_to_set = GetValueToSet(set);
    // First, check whether we can create a Phi node.
    // We should be careful here. Phi node must have the inputs of the same type.
    // However set is not typed if the value has not been obtained from some computation.
    // It is possible that get is of type fp while
    // set for constant will be of integral type. To avoid problems with Phi node we should
    // handle this case.
    if (HPhi::ToPhiType(get->GetType()) != HPhi::ToPhiType(value_to_set->GetType())) {
      if ((value_to_set->IsLongConstant() && get->GetType() == Primitive::kPrimDouble) ||
          (value_to_set->IsIntConstant() && get->GetType() == Primitive::kPrimFloat)) {
        value_to_set =
            SsaBuilder::GetFloatOrDoubleEquivalent(nullptr, value_to_set, value_to_set->GetType());
        ReplaceValueToSet(set, value_to_set);
      } else {
        PRINT_PASS_OSTREAM_MESSAGE(this, "We cannot set Phi node with inputs of different types"
                                         << Primitive::PrettyDescriptor(get->GetType())
                                         << " and "
                                         << Primitive::PrettyDescriptor(value_to_set->GetType()));
        continue;
      }
    }

    // Now, we move the load at the end of the loop pre-header.
    get->MoveBefore(pre_header->GetLastInstruction());

    // Then, we need to update the loop users of the load.
    // We create a new phi node and we update the users accordingly.
    HPhi* phi = new (arena) HPhi(arena, graph_->GetNextInstructionId(),
                                 2u, HPhi::ToPhiType(get->GetType()));

    // We need to attach a basic block to the phi temporarily because AddUseAt uses the arena
    // from the instruction's basic block in "ReplaceWith".
    phi->SetBlock(loop_header);

    // Replace the users of the get by those of the phi node.
    get->ReplaceWith(phi);

    // Set the new phi its inputs.
    phi->SetRawInputAt(0, get);  // First input of the phi is the get.

    phi->SetRawInputAt(1, value_to_set);  // Second input is the input defined in the loop.

    // We need to detach the phi temporarily from the basic block because "HBasicBlock::AddPhi"
    // requires the phi to be detached from a basic block.
    phi->SetBlock(nullptr);

    // Finally, add the phi node to the loop pre-header.
    loop_header->AddPhi(phi);

    if (has_suspend) {
      suspend_block_created |= CopySetInSuspendBlock(set, loop,
                                                     phi, graph_->GetArena());
    }

    // Then, we can move the store instruction to the new exit block.
    set->MoveBefore(exit_block->GetFirstInstruction());

    PRINT_PASS_OSTREAM_MESSAGE(this, "Set/Get pair (\"" << set << "\" / \""
                                     << get << "\")"
                                     << " has been hoisted/sunk out of the loop #"
                                     << loop_header->GetBlockId() << " to exit block #"
                                     << exit_block->GetBlockId()
                                     << " successfully!");
  }
  MaybeRecordStat(MethodCompilationStat::kIntelLHSS, get_to_set.size());

  // Sink the invariant stores to the pre-exit block.
  for (auto set_it = sets_to_sink.rbegin(); set_it != sets_to_sink.rend(); set_it++) {
    HInstruction* set = *set_it;
    if (has_suspend) {
      suspend_block_created |= CopySetInSuspendBlock(set, loop, GetValueToSet(set),
                                                     graph_->GetArena());
    }

    set->MoveBefore(exit_block->GetFirstInstruction());
    PRINT_PASS_OSTREAM_MESSAGE(this, "Set \"" << set << "\" has been successfully sunk out"
                                      << " of the loop #" << loop_header->GetBlockId()
                                      << " in block #" << exit_block->GetBlockId()
                                      << " successfully!");
  }
  MaybeRecordStat(MethodCompilationStat::kIntelStoreSink, sets_to_sink.size());

  // If we added a suspend block, we need to rebuild the dominators.
  return suspend_block_created;
}

static bool IsVolatile(HInstruction* insn) {
  DCHECK(insn != nullptr);

  switch (insn->GetKind()) {
    case HInstruction::kStaticFieldSet:
      return insn->AsStaticFieldSet()->IsVolatile();
    case HInstruction::kInstanceFieldSet:
      return insn->AsInstanceFieldSet()->IsVolatile();
    case HInstruction::kStaticFieldGet:
      return insn->AsStaticFieldGet()->IsVolatile();
    case HInstruction::kInstanceFieldGet:
      return insn->AsInstanceFieldGet()->IsVolatile();
    default:
      return false;
  };
}

static bool LoopHeaderOrInvariant(HLoopInformation_X86* loop, HInstruction* set,
                                  bool check_is_phi) {
  DCHECK(loop != nullptr);
  DCHECK(set != nullptr);

  // If the loop has a suspend point, the set has to be duplicated in the suspend block,
  // which is at the beginning of the loop. For this reason, we can't sink a set if
  // the value is not defined at this point, meaning that it restricts the sinking to
  // set instructions having inputs that are in the loop header or invariant values.
  HInstruction* value_to_set = GetValueToSet(set);
  if (HasSuspendPoint(loop)) {
    if (value_to_set->GetBlock() != loop->GetHeader()
        && loop->Contains(*value_to_set->GetBlock())) {
      return false;
    }

    if (check_is_phi && !value_to_set->IsPhi()) {
      return false;
    }
  }

  return true;
}

bool LoadHoistStoreSink::FindLoadStoreCouples(HLoopInformation_X86* loop,
                                              GetSetToHoistSink& get_to_set,
                                              SetsToSink& sets_to_sink) {
  DCHECK(loop != nullptr);

  // We will need this as a gate for reference sinks.
  bool has_suspend = HasSuspendPoint(loop);

  // Container of gets in the loop.
  std::unordered_set<HInstruction*> gets;
  // Container of sets in the loop.
  std::unordered_set<HInstruction*> sets;
  // Maps saving ordering of get/sets in the loop.
  std::map<HInstruction*, uint32_t> get_to_idx;
  std::map<HInstruction*, uint32_t> set_to_idx;

  // Current index for ordering get/sets in the loop.
  uint32_t current_index = 0;

  // Container of side effects instructions (include sets) in the loop.
  std::unordered_set<HInstruction*> has_side_effects;

  HBasicBlock* exit_block = loop->GetExitBlock();
  CHECK(exit_block != nullptr);  // Paranoid: we made sure there is one in the gate.

  // Get the list of get, set and instructions that have side effects within the loop.
  for (HBlocksInLoopIterator bb_it(*loop); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* current_block = bb_it.Current();
    for (HInstructionIterator insn_it(current_block->GetInstructions());
         !insn_it.Done(); insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();
      HInstruction::InstructionKind insn_type = insn->GetKind();

      // Is this an instruction that can side exit?
      if (insn->HasEnvironment() && !(insn->IsSuspendCheck() || insn->IsSuspend())) {
        PRINT_PASS_OSTREAM_MESSAGE(this, "Instruction can side exit: " << insn);
        return false;
      }

      // Is the instruction a set or a get?
      bool is_set = false, is_get = false, is_array = false;
      switch (insn_type) {
        case HInstruction::kArrayGet:
          is_array = true;
          FALLTHROUGH_INTENDED;
        case HInstruction::kInstanceFieldGet:
        case HInstruction::kStaticFieldGet:
          is_get = true;
          break;
        case HInstruction::kArraySet:
          is_array = true;
          FALLTHROUGH_INTENDED;
        case HInstruction::kInstanceFieldSet:
        case HInstruction::kStaticFieldSet:
          is_set = true;
          break;
        default:
          break;
      };

      // If the instruction is a set or a get, check whether its inputs are invariant.
      // If they are, they should be candidates for load/store hoisting/sinking.
      if (is_set || is_get) {
        // We only care about the address ; the store has a second input, but
        // this can be variant as long as it updates the same memory location.
        HInstruction* address = insn->InputAt(0);
        bool invariant_address = !loop->Contains(*address->GetBlock());

        if (is_array) {
          // If it is an array, we also want to check that its index is invariant.
          HInstruction* index = insn->InputAt(1);
          if (loop->Contains(*index->GetBlock())) {
            invariant_address = false;
          }
        }

        // Valid candidates so far are:
        // - Field memory accesses that have an invariant address
        // - Array memory accesses that have an invariant address and an invariant index.
        if (invariant_address) {
          if (is_get) {
            gets.insert(insn);
            get_to_idx[insn] = current_index++;
          } else if (is_set) {
            sets.insert(insn);
            set_to_idx[insn] = current_index++;
          }
        }
      }

      // Finally, we need to consider instructions that have side effects to prevent
      // hoisting or sinking instructions that might alias with them.
      if (is_get || is_set || alias_.HasSideEffects(insn)) {
        has_side_effects.insert(insn);
      }
    }
  }

  PRINT_PASS_OSTREAM_MESSAGE(this, "Gets and sets for method \"" << GetMethodName(c_unit_) << "\"");

  // For each get, we want to know if there is a single set that must alias with it.
  // We also need to make sure that the get does not alias with any other instruction
  // that has side effects.
  for (auto get : gets) {
    uint32_t nb_must_alias = 0;
    HInstruction* set_candidate = nullptr;
    bool valid_candidate = true;
    for (auto se : has_side_effects) {
      // If the side-effect instruction is the get, skip the tests.
      if (se == get) {
        continue;
      }

      // Get the aliasing of the two instructions.
      AliasCheck::AliasKind alias_kind = alias_.Alias(se, get);

      // If the instruction is a set that must alias with the get.
      if (alias_kind == AliasCheck::kMustAlias && IsSet(se)) {
        if (nb_must_alias > 0) {
          // We need to have exactly one set corresponding to the get in the loop.
          set_candidate = nullptr;
          break;
        } else {
          // We get the set corresponding to the get.
          set_candidate = se;
        }
        nb_must_alias++;
      } else if (alias_kind != AliasCheck::kNoAlias) {
        // We found an instruction in the loop that may alias with the get, therefore
        // we can stop now.
        valid_candidate = false;
        break;
      }
    }

    // Conditions to be a valid Load/Store pair:
    // 1. Get must be placed before the set in the loop.
    // 2. Get and set should not be volatile.
    // 3. Get MUST ONLY alias with the set.
    // 4. Get MUST NOT alias with anything other instruction.
    // 5. Get and Set MUST be in the same basic block.
    // 6. Get and Set MUST execute every loop iteration. Can test just get since
    //    they are both in the same block.
    if (nb_must_alias == 1 && valid_candidate && set_candidate != nullptr) {
      HBasicBlock* get_bb = get->GetBlock();
      bool ls_couple_is_valid = true;

      if (IsVolatile(get)) {
        // The get and the set should not be volatile.
        DCHECK(IsVolatile(set_candidate));  // Paranoid.
        PRINT_PASS_OSTREAM_MESSAGE(this, "Get and set instructions should not be volatile.");
        return false;
      } else if (get_to_idx[get] > set_to_idx[set_candidate]) {
        // The get instruction must be before the set.
        PRINT_PASS_OSTREAM_MESSAGE(this, "Get instruction must be before the set instruction.");
        ls_couple_is_valid = false;
      } else if (get_bb != set_candidate->GetBlock()) {
        // The get and set must be in the same basic block.
        PRINT_PASS_OSTREAM_MESSAGE(this, get << " and " << set_candidate <<
                                   " are in different blocks");
        ls_couple_is_valid = false;
      } else if (!loop->ExecutedPerIteration(get)) {
        // Get and set instructions must be executed at every iteration.
        PRINT_PASS_OSTREAM_MESSAGE(this, get << " and " << set_candidate <<
                                   " may not be executed every loop iteration");
        ls_couple_is_valid = false;
      } else if (!get_bb->Dominates(exit_block)) {
        // Their basic block must dominate the exit block in order for the set sinking to be valid.
        PRINT_PASS_OSTREAM_MESSAGE(this, "Block #" << get_bb->GetBlockId() << " does not dominate"
                                         << " exit block #" << exit_block->GetBlockId());
        ls_couple_is_valid = false;
      } else if (has_suspend && get->GetType() == Primitive::kPrimNot) {
        // TODO: support reference types with suspend.
        // If we have a reference type and suspend check in the loop
        // we will need to add our generated Phi node into suspend environment.
        // However there is no a register for this Phi and we will need to
        // extend suspend environment to add this new value.
        // For now we simply reject this case.
        PRINT_PASS_OSTREAM_MESSAGE(this, "Get \"" << get
            << "\" has been skipped because it is a reference");
        ls_couple_is_valid = false;
      } else if (!LoopHeaderOrInvariant(loop, set_candidate, false)) {
        PRINT_PASS_OSTREAM_MESSAGE(this, "Set " << set_candidate << " is contained in a loop with suspend"
                                   << " point, and its input"
                                   << " is neither loop header phi nor invariant.");
        ls_couple_is_valid = false;
      }

      if (ls_couple_is_valid) {
        DCHECK(!IsVolatile(set_candidate));  // Paranoid.
        // Here it means we have a valid (Load:get,Store:set) candidate.
        get_to_set[get] = set_candidate;
      }

      // Remove it from the list of sets in order to avoid picking them up for store
      // sinking (see below).
      sets.erase(set_candidate);
    }
  }

  // We also want to know the store that we can sink out of the loop if their definitions
  // are coming from outside of the loop.
  for (auto set : sets) {
    // Set should not be volatile.
    if (IsVolatile(set)) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Set instruction " << set << " is volatile.");
      return false;
    }

    // Set basic block must dominate the exit block.
    HBasicBlock* set_block = set->GetBlock();
    if (set_block == nullptr) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Block is NULL for the set " << set);
      continue;
    }

    if (!set_block->Dominates(exit_block)) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Block #" << set_block->GetBlockId() << " of set "
                                 << set << " does not dominate the exit block.");
      continue;
    }

    // Address of the set must be invariant.
    if (loop->Contains(*set->InputAt(0)->GetBlock())) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Address of set " << set << " is not invariant");
      continue;
    }

    if (set->IsArraySet() && loop->Contains(*set->InputAt(1)->GetBlock())) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Index of array set " << set << " is not invariant.");
      continue;
    }

    if (!LoopHeaderOrInvariant(loop, set, true)) {
      PRINT_PASS_OSTREAM_MESSAGE(this, "Set " << set << " is contained in a loop with suspend"
                                 << " point, and its input"
                                 << " is neither loop header phi nor invariant.");
      continue;
    }

    // Set must not alias with:
    // - Any other side effects instruction (includes other sets)
    // - Any other get instruction.
    bool set_aliases = false;
    HInstruction* alias_insn = nullptr;
    for (auto se_insn : has_side_effects) {
      if (set != se_insn && alias_.Alias(set, se_insn) != AliasCheck::kNoAlias) {
        set_aliases = true;
        alias_insn = se_insn;
        break;
      }
    }

    if (!set_aliases) {
      for (auto get : gets) {
        DCHECK(set != get);
        if (alias_.Alias(set, get) != AliasCheck::kNoAlias) {
          set_aliases = true;
          alias_insn = get;
          break;
        }
      }
    }

    // If there is any aliasing, we discard the candidate.
    if (set_aliases) {
      DCHECK(alias_insn != nullptr);
      PRINT_PASS_OSTREAM_MESSAGE(this, "Set " << set << " aliases with instruction "
                                 << alias_insn << ".");
      continue;
    }

    // All good.
    sets_to_sink.insert(set);
  }

  // All okay.
  return true;
}

bool LoadHoistStoreSink::LoopGate(HLoopInformation_X86* loop) const {
  DCHECK(loop != nullptr);

  // The loop must be innermost.
  DCHECK(loop->IsInner());

  // Loop should have only one exit block where to sink the stores.
  if (!loop->HasOneExitEdge()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop #" << loop->GetHeader()->GetBlockId()
                               << " has more than one exit block.");
    return false;
  }

  // We currently support one back edge since new phi node has only two inputs.
  if (loop->NumberOfBackEdges() != 1u) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop #" << loop->GetHeader()->GetBlockId()
                               << " has more than one back edge.");
    return false;
  }

  // We cannot hoist or sink memory accesses if the loop has invokes or can side exit.
  if (loop->CanSideExit()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop #" << loop->GetHeader()->GetBlockId()
                                      << " can side exit.");
    return false;
  }

  return true;
}

}  // namespace art
