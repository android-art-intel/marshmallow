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
#include "find_ivs.h"
#include "graph_x86.h"
#include "induction_variable.h"
#include "loop_iterators.h"

namespace art {

void HFindInductionVariables::FindInductionVariablesHelper(HLoopInformation_X86* info) {
  // Get the loop entry BB.
  HBasicBlock* entry = info->GetHeader();

  // Paranoid.
  DCHECK(entry != nullptr);

  // Clear the induction variable information and inter-iteration variables.
  GrowableArray<HInductionVariable*>& iv_list = info->GetInductionVariables();
  iv_list.Reset();

  // Clear inter iteration variables.
  info->ClearInterIterationVariables();

  // We could do a visitor here but we'd use up compile time for the non phi instructions.
  // Let us just visit it with a non C++ iterator.
  for (HInstructionIterator it(entry->GetPhis()); !it.Done(); it.Advance()) {
    HPhi* phi = static_cast<HPhi*> (it.Current());
    DetectAndInitializeBasicIV(info, phi);

    // Also add it as a inter-iteration variable.
    info->AddInterIterationVariable(phi->GetRegNumber());
  }

  // Set is it a count up loop.
  if (iv_list.Size() == 1) {
    HInductionVariable* iv = iv_list.Get(0);

    DCHECK(iv != nullptr);

    bool is_count_up = (iv->IsBasic() && iv->IsIncrementPositive());
    info->SetCountUpLoop(is_count_up);
  }
}

HIf* HFindInductionVariables::FindLoopIf(HLoopInformation_X86* loop) {
  DCHECK(loop != nullptr);

  // Get the only exit block.
  HBasicBlock* exit_block = loop->GetExitBlock();
  DCHECK(exit_block != nullptr);

  // exit_block always has only one predecessor and it comes from the loop.
  DCHECK_EQ(exit_block->GetPredecessors().Size(), 1u);
  HBasicBlock* loop_block = exit_block->GetPredecessors().Get(0);
  DCHECK(loop_block != nullptr);

  // Get last instruction.
  HInstruction* last_insn = loop_block->GetLastInstruction();
  DCHECK(last_insn != nullptr);

  // return last instruction if it is an if or nullptr.
  return last_insn->AsIf();
}

bool HFindInductionVariables::FindLoopUpperBound(HLoopInformation_X86* loop, int64_t& upper_bound) {
  DCHECK(loop != nullptr);

  if (!loop->HasOneExitEdge()) {
    return false;
  }

  const GrowableArray<HBasicBlock*>& back_edges = loop->GetBackEdges();
  size_t back_edges_size = back_edges.Size();

  if (back_edges_size != 1u) {
    return false;
  }

  HInstruction* if_insn = FindLoopIf(loop);

  if (if_insn == nullptr) {
    return false;
  }

  // The input to the if should be our condition.
  HInstruction* condition = if_insn->InputAt(0);

  if (!condition->IsCondition()) {
    return false;
  }

  // We are only supporting GreaterOrEqual at this point.
  switch (condition->GetKind()) {
    case HInstruction::kLessThan:
      FALLTHROUGH_INTENDED;
    case HInstruction::kEqual:
      return false;
    default:
      break;
  };

  DCHECK_EQ(condition->InputCount(), 2u);

  // Find the out-of-loop value to get the upper-bound.
  bool upper_bound_found = false;
  for (size_t input_id = 0, e = condition->InputCount(); input_id < e; input_id++) {
    HInstruction* input = condition->InputAt(input_id);

    HConstant* cst = input->AsConstant();
    if (cst != nullptr) {
      // All constants are defined in block 0, and are therefore out of the loop.
      DCHECK(!loop->Contains(*cst->GetBlock()));
      if (cst->IsIntConstant()) {
        upper_bound = static_cast<int64_t>(cst->AsIntConstant()->GetValue());
        upper_bound_found = true;
      } else if (cst->IsLongConstant()) {
        upper_bound = static_cast<int64_t>(cst->AsLongConstant()->GetValue());
        upper_bound_found = true;
      }
    }
  }

  return upper_bound_found;
}

bool HFindInductionVariables::IsValidCastForIV(HInstruction* candidate, HLoopInformation_X86* loop) {
  DCHECK(candidate != nullptr);
  DCHECK(loop != nullptr);

  HTypeConversion* cast = candidate->AsTypeConversion();

  // If the operation is not a cast, there is no point to go further.
  if (cast == nullptr) {
    return false;
  }

  Primitive::Type result = cast->GetResultType();
  Primitive::Type input = cast->InputAt(0)->GetType();

  bool int_to_short = (result == Primitive::kPrimShort && input == Primitive::kPrimInt);
  bool int_to_byte = (result == Primitive::kPrimByte && input == Primitive::kPrimInt);

  // We only support int-to-byte and int-to-short for now.
  if (!int_to_short && !int_to_byte) {
    return false;
  }

  // Fetch the upper bound of the loop to check it is within the range of the converted type.
  int64_t upper_bound = 0;
  if (!FindLoopUpperBound(loop, upper_bound)) {
    return false;
  }

  constexpr int64_t max_byte = std::numeric_limits<char>::max();
  constexpr int64_t min_byte = std::numeric_limits<char>::min();
  constexpr int64_t max_short = std::numeric_limits<int16_t>::max();
  constexpr int64_t min_short = std::numeric_limits<int16_t>::min();

  // We have to make sure the loop bound are within the type limits.
  if (int_to_byte && (upper_bound > max_byte || upper_bound < min_byte)) {
    return false;
  } else if (int_to_short && (upper_bound > max_short || upper_bound < min_short)) {
    return false;
  }

  return true;
}

void HFindInductionVariables::DetectAndInitializeBasicIV(HLoopInformation_X86* info, HPhi* phi) {
  size_t input_count = phi->InputCount();

  // For now accept only PHI nodes that have two uses and one define.
  if (input_count != 2) {
    return;
  }

  // Keep track of HInstruction that is candidate for identifying simple IV.
  HInstruction* candidate = nullptr;

  // Go through each of the PHI's uses.
  for (size_t use = 0; use < input_count; use++) {
    // Get HInstruction that defines the use.
    candidate = phi->InputAt(use);

    // If we cannot find the definition, then just continue.
    if (candidate == nullptr) {
      continue;
    }

    // If candidate is executed per iteration of current loop, then we
    // can keep it and run it through the IV detection criteria.
    if (info->ExecutedPerIteration(candidate)) {
      break;
    }

    // If we get here it means that the candidate doesn't have to be
    // executed per iteration and thus it cannot be an IV. Therefore,
    // we reset the candidate now.
    candidate = nullptr;
  }

  // If we found a candidate, check that it matches criteria for basic IV.
  if (candidate != nullptr) {
    // The type conversions might occur after the add operation, therefore we want to trim it.
    if (candidate->IsTypeConversion()) {
      if (IsValidCastForIV(candidate, info)) {
        candidate = candidate->InputAt(0);
      } else {
        return;
      }
    }
    // We want the right side of the instruction.
    // Currently we only look at additions, future work consists of handling negative increments.
    if (candidate->IsAdd()) {
      HBinaryOperation* binary = candidate->AsBinaryOperation();

      if (binary != nullptr) {
        // If neither side goes back to the phi node, we are not a basic IV.
        if (binary->GetRight() != phi && binary->GetLeft() != phi) {
          return;
        }

        // Get constant right is cool because it will look to the right but,
        //  if right is not a constant, will look to the left for a constant.
        HInstruction* right = binary->GetConstantRight();

        if (right != nullptr) {
          bool is_positive_or_zero = false;
          bool is_wide = false;
          bool is_fp = false;
          if (right->IsFloatConstant()) {
            is_positive_or_zero = right->AsFloatConstant()->GetValue() >= 0.0;
            is_fp = true;
          } else if (right->IsDoubleConstant()) {
            is_wide = true;
            is_fp = true;
            is_positive_or_zero = right->AsDoubleConstant()->GetValue() >= 0;
          } else if (right->IsIntConstant()) {
            is_positive_or_zero = right->AsIntConstant()->GetValue() >= 0;
          } else if (right->IsLongConstant()) {
            is_wide = true;
            is_positive_or_zero = right->AsLongConstant()->GetValue() >= 0;
          } else {
            return;
          }

          // Only accept this IV if the delta_value is positive for now.
          if (is_positive_or_zero) {
            ArenaAllocator* arena = graph_->GetArena();
            GrowableArray<HInductionVariable*>& iv_list = info->GetInductionVariables();

            int ssa_reg = phi->GetId();

            HInductionVariable* iv_info = new (arena) HInductionVariable(ssa_reg, right->AsConstant(), is_wide, is_fp, candidate, phi);
            DCHECK(iv_info != nullptr);

            iv_list.Insert(iv_info);

            MaybeRecordStat(MethodCompilationStat::kIntelBIVFound);
            if (IsVerbose()) {
              iv_info->Dump();
            }
          }
        }
      }
    }
  }
}

void HFindInductionVariables::Run() {
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86* loop_info = graph->GetLoopInformation();
  if (c_unit_) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Find IVs: Begin " << GetMethodName(*c_unit_));
  }
  for (HOutToInLoopIterator loop_iter(loop_info); !loop_iter.Done(); loop_iter.Advance()) {
    HLoopInformation_X86* current = loop_iter.Current();
    // Two things are done here:
    FindInductionVariablesHelper(current);
    // And also calculate the loop bounds.
    current->ComputeBoundInformation();
  }
  if (c_unit_) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Find IVs: End " << GetMethodName(*c_unit_));
  }
}
}  // namespace art

