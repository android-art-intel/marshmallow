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
#include "induction_variable.h"
#include "loop_information.h"
#include "loop_iterators.h"
#include "optimization_x86.h"

namespace art {

/**
 * @details Add takes a new LoopInformation and determines if info is nested_ with
 *   this instance or not.
 * If it is nested_ in this instance, we fill our nested_ information with it
 * Otherwise, we are a sibling, this is due to how Add is being called,
 *  it supposes that info has less or equal number of BasicBlocks.
 */
void HLoopInformation_X86::Add(HLoopInformation_X86* other) {
  // Check if there is anything to do.
  if (other == nullptr) {
    return;
  }

  // Clear siblings and nested in case.
  other->sibling_next_ = nullptr;
  other->sibling_previous_ = nullptr;
  other->outer_ = nullptr;

  // Do we include the current loop?
  if (other->IsIn(*this)) {
    // We contain them, so they should not contain us.
    DCHECK(!IsIn(*other));

    // Search in the children if anybody includes them.
    if (inner_ == nullptr) {
      inner_ = other;
      inner_->outer_ = this;
    } else {
      // Continue down the link.
      inner_->Add(other);
    }
  } else {
    if (sibling_next_ == nullptr) {
      other->sibling_previous_ = this;
      sibling_next_ = other;
      other->outer_ = outer_;
    } else {
      sibling_next_->Add(other);
    }
  }
}

void HLoopInformation_X86::SetDepth(int depth) {
  HLoopInformation_X86* info = this;
  while (info != nullptr) {
    info->depth_ = depth;

    if (info->inner_ != nullptr) {
      info->inner_->SetDepth(depth + 1);
    }
    info = info->sibling_next_;
  }
}

void HLoopInformation_X86::Dump(int max_depth) const {
  char buffer[256];
  char* ptr = buffer;
  int i = 0;

  for (i = 0; i < depth_ && i < max_depth; i++) {
    *ptr = '\t';
    ptr++;
  }

  snprintf(ptr, sizeof(buffer) - i, "%p: Loop depth %d with header %d, # blocks %d, contains: (inner %p, sibling %p)",
      this, depth_, GetHeader()->GetBlockId(), NumberOfBlocks(), inner_, sibling_next_);
  LOG(INFO) << buffer;

  if (inner_ != nullptr) {
    inner_->Dump();
  }

  if (sibling_next_ != nullptr) {
    sibling_next_->Dump();
  }
}

bool HLoopInformation_X86::ExecutedPerIteration(HBasicBlock* bb) const {
  DCHECK(bb != nullptr);
  // First is the instruction in the loop?
  if (!Contains(*bb)) {
    return false;
  }

  size_t size = GetBackEdges().Size();
  for (size_t idx = 0; idx < size; ++idx) {
    HBasicBlock* back_edge = GetBackEdges().Get(idx);

    if (!bb->Dominates(back_edge)) {
      return false;
    }
  }

  return true;
}

bool HLoopInformation_X86::ExecutedPerIteration(HInstruction* candidate) const {
  if (candidate == nullptr) {
    return false;
  }

  return ExecutedPerIteration(candidate->GetBlock());
}

bool HLoopInformation_X86::ComputeBoundInformation() {
  // Always start off with reinitializing the bound information.
  bound_info_ = HLoopBoundInformation();

  // Check that the loop has only one exit.
  HBasicBlock* exit_block = GetExitBlock();
  if (exit_block == nullptr) {
    return false;
  }

  // Check that we exit by IF instruction.
  HInstruction* branch = exit_block->GetPredecessors().Get(0)->GetLastInstruction();
  if (!branch->IsIf()) {
    return false;
  }

  // Go from the if to the compare.
  HInstruction* insn_to_check = branch->InputAt(0);

  // We want a condition here.
  if (!insn_to_check->IsCondition()) {
    return false;
  }

  IfCondition comparison_condition = insn_to_check->AsCondition()->GetCondition();
  bool is_fp = false;
  bool is_wide = false;

  // This could be a comparison on the result of a Compare instruction.
  insn_to_check = GetCompareInstruction(insn_to_check);

  // Get the first element of the compare.
  HInstruction* first_element = insn_to_check->InputAt(0);
  DCHECK(first_element != nullptr);

  // Skip the type conversion if any. It is safe to do so because if we find
  // an IV, we have checked the bounds of the conversion already.
  if (first_element->IsTypeConversion()) {
    first_element = first_element->InputAt(0);
  }

  // Get its type.
  Primitive::Type type = first_element->GetType();

  switch (type) {
    case Primitive::kPrimDouble:
      is_wide = true;
      FALLTHROUGH_INTENDED;
    case Primitive::kPrimFloat:
      is_fp = true;
      break;
    case Primitive::kPrimLong:
      is_wide = true;
      FALLTHROUGH_INTENDED;
    case Primitive::kPrimInt:
      is_fp = false;
      break;
    default:
      // The insn is not a comparison.
      return false;
  }

  // Determine if the first use is possibly an IV.
  bool is_iv_second_use = false;

  // Determine if the first use is possibly an IV.
  HInductionVariable* iv_info = GetInductionVariable(first_element);
  HInstruction* second_element = insn_to_check->InputAt(1);

  if (iv_info == nullptr) {
    // Did not find the IV in first argument. Try second.
    if (second_element->IsTypeConversion()) {
      second_element = second_element->InputAt(0);
    }

    iv_info = GetInductionVariable(second_element);

    if (iv_info == nullptr) {
      // Did not find IV in second argument either.
      return false;
    } else {
      is_iv_second_use = true;
    }
  }

  if (!iv_info->IsBasic()) {
    // The IV is not basic.
    return false;
  }

  bound_info_.loop_biv_ = iv_info;

  // We have the IV, the other must be a constant.
  HInstruction* cst = is_iv_second_use ? first_element : second_element;
  HConstant* constant = cst->AsConstant();

  if (constant == nullptr) {
    return false;
  }

  bound_info_.constant_bound_ = constant;
  bound_info_.is_bound_constant_ = true;

  // If the IV is the second use, flip the opcode so we treat it as if it is first.
  if (is_iv_second_use) {
    comparison_condition = FlipConditionForOperandSwap(comparison_condition);
  }

  // If the taken block is the exit block, negate the condition so that we deal with.
  if (branch->AsIf()->IfTrueSuccessor() == exit_block) {
    comparison_condition = NegateCondition(comparison_condition);
  }

  // Now we are at point where we know we have form: BIV cond INV.
  if (bound_info_.loop_biv_->IsIncrementPositive()) {
    // This is a simple count up loop.
    bound_info_.is_simple_count_up_ = true;
    bound_info_.comparison_condition_ = comparison_condition;
  } else if (bound_info_.loop_biv_->IsIncrementNegative()) {
    // This is a simple count down loop.
    bound_info_.is_simple_count_down_ = true;
    bound_info_.comparison_condition_ = comparison_condition;
  }

  // If this is a simple count up loop and we know the upper bound, see if we can determine
  // the number of iterations.
  HPhi* phi = bound_info_.loop_biv_->GetPhiInsn();
  HConstant* entry_value = FindBIVEntrySSA(phi);

  // Did we find a constant?
  if (entry_value != nullptr) {
    if (!is_fp) {
      int64_t increment = bound_info_.loop_biv_->GetIncrement();
      DCHECK_NE(increment, 0);

      int64_t start_value = 0, end_value = 0;

      // Try to get the border values.
      if (!GetIntConstantValue(entry_value, start_value)) {
        return false;
      }

      if (!GetIntConstantValue(bound_info_.constant_bound_, end_value)) {
        return false;
      }

      const int64_t min_value = is_wide ?
        std::numeric_limits<int64_t>::min() :
        std::numeric_limits<int32_t>::min();

      const int64_t max_value = is_wide ?
        std::numeric_limits<int64_t>::max() :
        std::numeric_limits<int32_t>::max();

      // Normalize conditions to get rid of 'equal' conditions.
      if (bound_info_.comparison_condition_ == kCondLE) {
        if (end_value == max_value) {
          // i <= MAX_VALUE is an infinite loop.
          return false;
        } else {
          // Normalize the end value to represent less than "<"
          // i <= 14 is equivalent to i < 15.
          end_value += 1;
        }
        bound_info_.comparison_condition_ = kCondLT;
      } else if (bound_info_.comparison_condition_ == kCondGE) {
        if (end_value == min_value) {
          // i >= MIN_VALUE is an infinite loop.
          return false;
        } else {
          // Normalize the end value to represent greater than ">"
          // i >= 14 is equivalent to i > 13.
          end_value -= 1;
        }
        bound_info_.comparison_condition_ = kCondGT;
      }

      if (bound_info_.is_simple_count_up_) {
        // Verify condition.
        if (bound_info_.comparison_condition_ != kCondLT) {
          return false;
        }

        // Now the borders are normalized. Make sure that the loop isn't empty.
        if (start_value >= end_value) {
          return false;
        }

        // Wouldn't we overflow? Consider the situation:
        // for (int i = 0; i < 2E+9 + 1; i += 1E+9) {...}
        // Condition will be true for i = 0, i = 1E+9 and 2E+9.
        // When we add 1E+9 to 2E+9, we overflow int. And the
        // formula for number of iterations cannot be applied.
        if ((max_value - increment + 1) < end_value) {
          return false;
        }
      } else if (bound_info_.is_simple_count_down_) {
        // The same logic is for count down loops.
        if (bound_info_.comparison_condition_ != kCondGT) {
          return false;
        }

        if (start_value <= end_value) {
          return false;
        }

        if (end_value < (min_value - increment - 1)) {
          return false;
        }
      } else {
        // Should never come here.
        DCHECK(false);
        return false;
      }

      int64_t num_iterations = (end_value - start_value) / increment;
      if (((end_value - start_value) % increment) != 0) {
        // If the mod is non-zero, then we will execute this one more iteration.
        num_iterations++;
      }

      // Fill the structure.
      bound_info_.biv_start_value_ = start_value;
      bound_info_.biv_end_value_ = end_value;
      bound_info_.num_iterations_ = num_iterations;
    } else {
      // Floating point is handled here.
      return FillFloatingPointBound(entry_value, is_wide);
    }
  } else {
    // Bail, we did not find the entry value.
    return false;
  }

  return true;
}

int64_t HLoopInformation_X86::GetNumIterations(HBasicBlock* bb) const {
  // Paranoia.
  DCHECK(bb != nullptr);
  DCHECK(ExecutedPerIteration(bb));

  int64_t number_of_biv_increments = bound_info_.num_iterations_;

  if (number_of_biv_increments == -1) {
    // The number of iterations for this loop is unknown.
    return -1;
  }

  HInductionVariable* iv = GetBasicIV();
  if (iv == nullptr || iv->linear_insn_ == nullptr) {
    return -1;
  }

  HBasicBlock* biv_increment_bb = iv->linear_insn_->GetBlock();
  DCHECK(biv_increment_bb != nullptr);
  HBasicBlock* exit_block = GetExitBlock();

  if (exit_block == nullptr) {
    return -1;
  }

  bool biv_dominates_exit = biv_increment_bb->Dominates(exit_block);
  bool bb_dominates_exit = bb->Dominates(exit_block);

  if (biv_dominates_exit == bb_dominates_exit) {
    // Both BIV increment and BB lie above or below the IF. And executed the
    // same number of times since they both are executed per iteration.
    return number_of_biv_increments;
  }

  // There can be two possible cases:
  //    CASE 1:                 CASE 2:
  //     ----------------        ----------------
  //     |              |        |              |
  //     V              |        V              |
  // |---------|        |    |---------|        |
  // | biv_inc |        |    |   bb    |        |
  // |---------|        |    |---------|        |
  //      |             |         |             |
  //    (IF)------> OUT |       (IF)------> OUT |
  //      |             |         |             |
  //      V             |         V             |
  // |---------|        |    |---------|        |
  // |   bb    |---------    | biv_inc |---------
  // |---------|             |---------|
  if (biv_dominates_exit) {
    // We are in CASE 1 and BIV increment is executed 1 time more than BB.
    return number_of_biv_increments - 1;
  } else {
    // We are in CASE 2 and BIV increment is executed 1 time less than BB.
    return number_of_biv_increments + 1;
  }
}

bool HLoopInformation_X86::FillFloatingPointBound(HConstant* entry_value, bool is_double) {
  double increment = bound_info_.loop_biv_->GetFPIncrement();
  DCHECK(increment != 0.0);

  // Get the start and end values with error checking.
  double start_value = 0, end_value = 0;

  if (!GetFPConstantValue(entry_value, start_value)) {
    return false;
  }

  if (!GetFPConstantValue(bound_info_.constant_bound_, end_value)) {
    return false;
  }

  // Deconstruct doubles into parts.
  int32_t sign_start, sign_end, sign_inc;
  int64_t mantissa_start, mantissa_end, mantissa_inc;
  int32_t power_start, power_end, power_inc;

  DeconstructFP(start_value, is_double, true, sign_start, power_start, mantissa_start);
  DeconstructFP(end_value, is_double, true, sign_end, power_end, mantissa_end);
  DeconstructFP(increment, is_double, true, sign_inc, power_inc, mantissa_inc);

  // For now, consider only case of the same sign. In this case the power cannot decrease.
  if (sign_start != sign_end || sign_start != sign_inc || power_start > power_end) {
    // TODO: This case can be handled by splitting the interval into two intervals:
    // for (x = start; x < 0; x += step) {...}; for(; x < end; x += step)
    // and calculate the number of iterations for every case. Now we consider only the case
    // where the absolute value of x always increases.
    return false;
  }

  // For count up loop, check that the loop is not empty.
  if ((sign_inc == 0) && (start_value >= end_value)) {
    return false;
  }

  // For count down loop, check that the loop is not empty.
  if ((sign_inc == 1) && (start_value <= end_value)) {
    return false;
  }
  // Consider that we iterate from start_value to end_value, and
  // abs(start_value) < abs(end_value). They are represented in
  // FP as follows below:
  //
  // start_value = [sign_start][power_start][mantissa_start]
  // end_value   = [ sign_end ][ power_end ][ mantissa_end ]
  // increment   = [ sign_inc ][ power_inc ][ mantissa_inc ]
  //
  // We want to guarantee that all intermediate values of x in loop
  //   for (float x = start; x < end; x += step) {...}
  // are representable in fp without the loss of precision.
  //
  // How can we get an non-representable value if we add two representable
  // values? Let us add x and y. First, we should normalize them to the
  // greater power. Without loss of generality, let exp_x be >= exp_y.
  //     [ ... mantissa of x ... ]
  //          [E mantissa of y ... 1] <-- One gone out of grid and was lost.
  //     |       grid of FP      |
  //
  // Here E denominates the implicit 1 that is not stored in mantissa, but
  // should be considered in adjustment if y != 0.
  // The number of bits of y that didn't fit into the grid is equal to
  // K = (exp_x - exp_y). If there was at least one non-zero bit among the
  // K last bits of y, we lose precision and cannot estimate the number of
  // iterations correctly.
  //
  // Let adjusted_pow = max(power_start, power_inc).
  // While iterating from start_value to end_value, the summary number of
  // increases of power (and bits that go out of grid) is N = (end_power - start_power).
  // Now we can formulate the required and sufficient conditions of representability
  // of all intermediate values over our iteration:
  // 1. mantissa_start has at least z_1 = (adjusted_pow - power_start) zeros in the end;
  // 2. mantissa_step has at least z_2 = (adjusted_pow - power_inc) zeros in the end;
  // 3. They both have at least N zeros in the end: min(z1, z2) >= N.
  //
  // If all three conditions are met, all intermediate numbers are representable.

  // Adjust start and step.
  const int32_t adjusted_pow = std::max(power_start, power_inc);
  const int32_t zeros_start = CountEndZerosInMantissa(mantissa_start, is_double);
  const int32_t zeros_inc = CountEndZerosInMantissa(mantissa_inc, is_double);

  // Ensure that mantissae can be adjusted to this value.
  if (zeros_start < (adjusted_pow - power_start)) {
    return false;
  }

  if (zeros_inc < (adjusted_pow - power_inc)) {
    return false;
  }

  // Estimate the number of allowed increments of power.
  const int32_t allowed_increments = std::min(zeros_start, zeros_inc);

  if ((power_end - power_start) > allowed_increments) {
    // We need to do more increments than we are allowed.
    return false;
  }

  // We have proved that all values of our IV within our loop are
  // representable in floating-point grid.
  // It means that adjusted extended mantissae (i.e. with implicit ones)
  // of these values behave as usual integers. We don't overflow here
  // because all extended mantissae do not exceed 2^53.
  const int max_power = std::max(adjusted_pow, power_end);
  mantissa_start >>= (max_power - power_start);
  mantissa_end >>= (max_power - power_end);
  mantissa_inc >>= (max_power - power_inc);

  // Now we can estimate the number of iterations.
  int64_t num_iterations = (mantissa_end - mantissa_start) / mantissa_inc;
  if ((mantissa_end - mantissa_start) % mantissa_inc != 0) {
    num_iterations++;
  }

  bound_info_.biv_start_value_ = start_value;
  bound_info_.biv_end_value_ = end_value;

  // Check for overflows and compute the number of iterations for this loop.
  bound_info_.num_iterations_ = num_iterations;
  return true;
}

HInductionVariable* HLoopInformation_X86::GetInductionVariable(HInstruction* insn) const {
  // The instruction can either be the phi node or the linear instrction.
  //   Thus check for both.

  // Now walk the induction variables.
  size_t max = iv_list_.Size();
  for (size_t i = 0; i < max; i++) {
    HInductionVariable* iv = iv_list_.Get(i);
    DCHECK(iv != nullptr);

    if (iv->GetLinearInsn() == insn) {
      return iv;
    }

    if (iv->GetPhiInsn() == insn) {
      return iv;
    }
  }

  // Not found: return nullptr.
  return nullptr;
}

HConstant* HLoopInformation_X86::FindBIVEntrySSA(HPhi* phi) const {
  size_t max = phi->InputCount();
  for (size_t i = 0; i < max; i++) {
    HInstruction* insn = phi->InputAt(i);
    HConstant* constant = insn->AsConstant();
    if (constant != nullptr) {
      return constant;
    }
  }
  return nullptr;
}

bool HLoopInformation_X86::HasOneExitEdge() const {
  // We call GetExitBlock because we know that will bail as soon
  // as it finds two exits.
  bool has_one_exit = GetExitBlock() != nullptr;
  DCHECK_EQ(has_one_exit, GetLoopExitCount() == 1u);
  return has_one_exit;
}

bool HLoopInformation_X86::IsVerySimple(bool require_at_most_two_blocks) const {
  // Loop is very simple if it is innermost.
  if (!IsInner()) {
    return false;
  }

  if (require_at_most_two_blocks) {
    // Refuse anything that has more than two blocks.
    if (NumberOfBlocks() > 2) {
      return false;
    }
  }

  // Finally, there are complex opcodes we don't consider as simple.
  if (ContainsComplexInstructions()) {
    return false;
  }

  return true;
}

HBasicBlock* HLoopInformation_X86::GetExitBlock() const {
  HBasicBlock* exit_block = nullptr;
  for (HBlocksInLoopIterator it_loop(*this); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* loop_block = it_loop.Current();
    DCHECK(loop_block != nullptr);  // Paranoid.
    // Look for a successor that isn't in the loop.
    const GrowableArray<HBasicBlock*>& successors = loop_block->GetSuccessors();
    for (size_t i = 0, e = successors.Size(); i < e; ++i) {
      HBasicBlock* successor = successors.Get(i);
      if (!Contains(*successor)) {
        if (exit_block == nullptr) {
          // This is the first exit edge we found.
          // Note, we do not allow multiple edges to same block.
          exit_block = successor;
        } else {
          // More than one different exit blocks.
          return nullptr;
        }
      }
    }
  }

  return exit_block;
}

size_t HLoopInformation_X86::GetLoopExitCount() const {
  size_t count = 0u;

  for (HBlocksInLoopIterator it_loop(*this); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* block = it_loop.Current();

    for (size_t idx = 0; idx < block->GetSuccessors().Size(); ++idx) {
      HBasicBlock* successor = block->GetSuccessors().Get(idx);
      if (!this->Contains(*successor)) {
        count++;
      }
    }
  }
  return count;
}

bool HLoopInformation_X86::CanSideExit(bool ignore_suspends) const {
  for (HBlocksInLoopIterator bb_it(*this); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* bb = bb_it.Current();
    for (HInstructionIterator insn_it(bb->GetInstructions()); !insn_it.Done(); insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();
      if (insn->HasEnvironment()) {
        if (ignore_suspends && (insn->IsSuspendCheck() || insn->IsSuspend())) {
          continue;
        }
        return true;
      }
    }
  }

  // Nothing has an environment.
  return false;
}

bool HLoopInformation_X86::GetLoopCost(uint64_t* cost) const {
  DCHECK(cost != nullptr);
  uint64_t local_cost = 0;
  for (HBlocksInLoopIterator bb_it(*this); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* bb = bb_it.Current();
    for (HInstructionIterator insn_it(bb->GetInstructions()); !insn_it.Done(); insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();
      switch (insn->GetKind()) {
        case HInstruction::kDiv:
        case HInstruction::kRem:
          local_cost += 50;
          break;
        case HInstruction::kMul:
          local_cost += 5;
          break;
        default:
          local_cost += 1;
          break;
      }
    }
  }

  *cost = local_cost;
  return true;
}

bool HLoopInformation_X86::InsertInstructionInSuspendBlock(HInstruction* instruction) {
  // Have we already split the SuspendCheck into two pieces?
  bool added_split_block = false;
  if (!HasTestSuspend()) {
    SplitSuspendCheck();
    added_split_block = true;
  }

  DCHECK(HasTestSuspend());
  DCHECK(HasSuspend());
  HSuspend* suspend = GetSuspend();

  // Insert the actual instruction before the HSuspend. That will ensure that
  // successive calls insert in order.
  suspend->GetBlock()->InsertInstructionBefore(instruction, suspend);

  return added_split_block;
}

void HLoopInformation_X86::SplitSuspendCheck() {
  // Have we already been split?
  if (HasTestSuspend()) {
    // We must have got here already.
    DCHECK(!HasSuspendCheck());
    return;
  }

  // We better have an HSuspendCheck to split!
  DCHECK(HasSuspendCheck());

  // Replace the SuspendCheck with a TestSuspend.
  HSuspendCheck* check = GetSuspendCheck();

  // The suspend check must be in the header.
  HBasicBlock* header = GetHeader();
  DCHECK(header == check->GetBlock());
  DCHECK(header->GetLoopInformation() == this);
  HGraph* graph = header->GetGraph();
  ArenaAllocator* arena = graph->GetArena();

  // Special case single block loops.
  if (GetBackEdges().Get(0) == header) {
    // Clear the back edges now, in order to prevent 2 edges after the split.
    // It will be rebuilt later.
    ClearBackEdges();
  }

  // Create a new block to hold the TestSuspend. This avoids a 'CriticalEdge' with
  // multiple predecessors and successors.
  HBasicBlock* test_suspend_block = new(arena) HBasicBlock(graph, header->GetDexPc());
  graph->AddBlock(test_suspend_block);

  // Add an HTestSuspend into this block.
  HTestSuspend* test_suspend = new(arena) HTestSuspend;
  test_suspend_block->AddInstruction(test_suspend);
  SetTestSuspend(test_suspend);

  // Create a dummy block just for the suspend to return to,
  // in order to avoid creating a critical edge.
  HBasicBlock* dummy_block = new(arena) HBasicBlock(graph, header->GetDexPc());
  graph->AddBlock(dummy_block);
  dummy_block->AddInstruction(new(arena) HGoto());

  // We need a second dummy block to avoid another critical edge, to handle
  // the non-suspend case.
  HBasicBlock* dummy_block2 = new(arena) HBasicBlock(graph, header->GetDexPc());
  graph->AddBlock(dummy_block2);
  dummy_block2->AddInstruction(new(arena) HGoto());
  dummy_block2->AddSuccessor(dummy_block);
  test_suspend_block->AddSuccessor(dummy_block2);

  // Create a new block to hold the code after the TestSuspend.
  HBasicBlock* rest_of_header = header->SplitAfter(check);

  // Function SplitAfter() divides a block into two halves but it
  // doesn't add the bottom half into reverse_post_order_. And, this
  // may cause a trouble. When HGraph::ClearDominanceInformation() is
  // called, it doesn't clear dominated_blocks_ for the bottom half
  // because the half is absent in the reverse_post_order_. Next,
  // HGraph::ComputeDominanceInformation() adds blocks into
  // dominated_blocks_ but it doesn't check for duplicate values. So,
  // it may populate dominated_blocks_ with same blocks. This is not
  // normal and violates DCHECK conditions.
  HBasicBlock* dom = rest_of_header->GetDominator();
  rest_of_header->ClearDominanceInformation();
  rest_of_header->SetDominator(dom);

  graph->AddBlock(rest_of_header);

  // Now add a Goto to the end of the header block.
  header->AddInstruction(new(arena) HGoto());
  header->AddSuccessor(test_suspend_block);

  // And link in the dummy block.
  dummy_block->AddSuccessor(rest_of_header);

  // Create a new block to hold the Suspend.
  HBasicBlock* suspend_block = new(arena) HBasicBlock(graph, header->GetDexPc());
  graph->AddBlock(suspend_block);

  // Create the new suspend instruction with the original environment.
  HSuspend* suspend = new(arena) HSuspend(check->GetDexPc());
  SetSuspend(suspend);
  suspend_block->AddInstruction(suspend);
  suspend->CopyEnvironmentFrom(check->GetEnvironment());
  suspend_block->AddInstruction(new(arena) HGoto());

  // Remove the HSuspendCheck.
  header->RemoveInstruction(check);
  SetSuspendCheck(nullptr);

  // Link the new block into the loop.
  test_suspend_block->AddSuccessor(suspend_block);
  suspend_block->AddSuccessor(dummy_block);

  // Set loop information for new blocks.
  AddToAll(suspend_block);
  AddToAll(test_suspend_block);
  AddToAll(dummy_block);
  AddToAll(dummy_block2);
  AddToAll(rest_of_header);
}

void HLoopInformation_X86::AddToAll(HBasicBlock* block) {
  block->SetLoopInformation(this);
  for (HLoopInformation_X86* current = this; current != nullptr; current = current->GetParent()) {
    static_cast<HLoopInformation*>(current)->Add(block);
  }
}

bool HLoopInformation_X86::HasCatchHandler() const {
  // Walk through the loop's blocks to find a catch handler.
  for (HBlocksInLoopIterator it_loop(*this); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* block = it_loop.Current();
    if (block->IsCatchBlock()) {
      return true;
    }
  }

  // No catch handler found.
  return false;
}

bool HLoopInformation_X86::IsPeelable(HOptimization_X86* optim) const {
  if (!IsInner()) {
    // Peeling an iteration of an outer loop, it means that in the peel we would have
    // the inner loop. For now reject because it is unlikely to desire this behavior
    // and we would need to make sure to update all loop structures to reflect the
    // loops. Also, the peeling removes suspend checks and this is also not safe
    // if inside the peel there is loop.
    PRINT_PASS_OSTREAM_MESSAGE(optim, "Peeling failed because the loop is not an inner loop.");
    return false;
  }

  if (HasCatchHandler()) {
    // Catch information is stored specially and all data structures around this would
    // need updated if we peeled this. Since this case is unlikely to be desired, reject
    // for now and support only when needed.
    PRINT_PASS_OSTREAM_MESSAGE(optim, "Peeling failed because the loop has a catch handler.");
    return false;
  }

  if (!HasOneExitEdge()) {
    // The peeler does not properly update phi nodes post loop if there is more than one exit.
    // There is a lot of messiness around phi construction and that is why it is not handled.
    // If ever desired in future, the cleanest approach to enable this is to allow entire graph
    // phi regeneration instead of just localized regeneration.
    PRINT_PASS_OSTREAM_MESSAGE(optim, "Peeling failed because the loop has multiple exits.");
    return false;
  }

  HBasicBlock* header = GetHeader();
  for (HInstructionIterator it(header->GetPhis()); !it.Done(); it.Advance()) {
    HInstruction* phi = it.Current();
    // We expect that loop phi nodes have two inputs:
    // 1) From the original definition that is coming into the loop.
    // 2) The output of the loop that is used in the loop after that initial iteration:
    // The optimizing compiler does not hold the above true because of lack of move nodes,
    // and therefore we explicitly check it now to avoid dealing with messiness.
    if (phi->IsLoopHeaderPhi()) {
      if (phi->InputCount() != 2u) {
        PRINT_PASS_OSTREAM_MESSAGE(optim,
          "Peeling failed because the loop phi does not have 2 inputs.");
        return false;
      }

      HInstruction* first_input = phi->InputAt(0);
      HInstruction* second_input = phi->InputAt(1);
      bool first_from_loop = Contains(*(first_input->GetBlock()));
      bool second_from_loop = Contains(*(second_input->GetBlock()));

      if (first_from_loop == second_from_loop) {
        PRINT_PASS_OSTREAM_MESSAGE(optim,
          "Peeling failed because the loop phi doesn't have expected form.");
        return false;
      }
    }
  }

  // Since peeling requires cloning, walk through loop to check for any instructions
  // that cannot be cloned. We do not enable cloning since we simply want to visit.
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(this->graph_);
  constexpr bool enable_cloning = false;
  HInstructionCloner cloner(graph, nullptr, enable_cloning);
  for (HBlocksInLoopIterator it_loop(*this); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* block = it_loop.Current();

    // Check if there is a non-loop phi. We reject in this case because this can happen
    // when there is a control flow in graph. This is not really a problem BUT phis expected
    // that if their inputs come from predecessor, the index of predecessor is index of
    // use in phi. Thus a repair phase is needed after phi duplication to fix those inputs.
    if (block != header) {
      if (!block->GetPhis().IsEmpty()) {
        PRINT_PASS_OSTREAM_MESSAGE(optim,
          "Peeling failed because loop has control flow with phi merges.");
        return false;
      }
    }

    cloner.VisitBasicBlock(block);

    // Found an instruction which cannot be cloned.
    if (!cloner.AllOkay()) {
      PRINT_PASS_OSTREAM_MESSAGE(optim,
        "Peeling failed because found instruction which cannot be cloned: " <<
            cloner.GetDebugNameForFailedClone());
      return false;
    }
  }

  // As far as we know, there is nothing blocking us from peeling.
  return true;
}

/**
 * @brief Used to add new phis to the exits.
 * @details The new phis are added because the peeled iteration copies all edges
 * to the exits. Therefore, a merge of values has to be created for the peel define
 * and the original define.
 * @param graph The graph in which all the instructions reside.
 * @param loop The loop currently being peeled.
 * @param loop_exits Set of the exits of loop currently being peeled.
 * @param env_to_instr The map of all environments in graph to the instruction that
 * encompasses them.
 * @param orig The original instruction in loop before the peel.
 * @param clone The cloned instruction in the peel.
 */
static void AddExitPhisAfterPeel(const HGraph_X86* graph, const HLoopInformation_X86* loop,
                                 const std::set<HBasicBlock*>& loop_exits,
                                 const SafeMap<HEnvironment*, HInstruction*>& env_to_instr,
                                 HInstruction* orig, HInstruction* clone) {
  // Note that the logic here that handles insertion of phis, makes assumption that there is
  // a single exit. That way we do not have to check which definition was live at which exit.
  DCHECK_EQ(loop_exits.size(), 1u);
  HBasicBlock* exit_bb = *(loop_exits.begin());

  // Attempt to give a valid phi number to the new created phi. This is not important,
  // but helps with debugging of graph.
  HPhi* new_phi = nullptr;
  uint32_t reg_number = kNoRegNumber;
  if (orig->IsLoopHeaderPhi()) {
    reg_number = orig->AsPhi()->GetRegNumber();
  }

  for (HUseIterator<HInstruction*> use_it(orig->GetUses()); !use_it.Done(); use_it.Advance()) {
    HInstruction* user = use_it.Current()->GetUser();
    // We do not want to add phi nodes for uses inside the loop.
    if (!loop->Contains(*(user->GetBlock()))) {
      if (user->IsPhi() && user->GetBlock() == exit_bb) {
        // Be careful here, only add if not already a user.
        HPhi* existing_phi = user->AsPhi();
        bool found_input = false;
        for (size_t input_idx = 0u; input_idx != existing_phi->InputCount(); input_idx++) {
          if (existing_phi->InputAt(input_idx) == clone) {
            found_input = true;
            break;
          }
        }
        if (!found_input) {
          // Only add the clone because this phi already is using the original output.
          DCHECK_EQ(clone->GetType(), existing_phi->GetType());
          existing_phi->AddInput(clone);
        }
      } else {
        if (new_phi == nullptr) {
          new_phi = new (graph->GetArena()) HPhi(graph->GetArena(), reg_number,
              0, HPhi::ToPhiType(orig->GetType()));
          exit_bb->AddPhi(new_phi);
          new_phi->AddInput(orig);
          new_phi->AddInput(clone);
        }

        size_t input_index = use_it.Current()->GetIndex();
        user->ReplaceInput(new_phi, input_index);
      }
    }
  }

  for (HUseIterator<HEnvironment*> use_it(orig->GetEnvUses()); !use_it.Done(); use_it.Advance()) {
    HEnvironment* user = use_it.Current()->GetUser();
    // We do not want to add phi nodes for uses inside the loop.
    auto it = env_to_instr.find(user);
    DCHECK(it != env_to_instr.end());
    if (!loop->Contains(*(it->second->GetBlock()))) {
      if (new_phi == nullptr) {
        new_phi = new (graph->GetArena()) HPhi(graph->GetArena(), reg_number,
            0, HPhi::ToPhiType(orig->GetType()));
        exit_bb->AddPhi(new_phi);
        new_phi->AddInput(orig);
        new_phi->AddInput(clone);
      }

      size_t input_index = use_it.Current()->GetIndex();
      user->RemoveAsUserOfInput(input_index);
      user->SetRawEnvAt(input_index, new_phi);
      new_phi->AddEnvUseAt(user, input_index);
    }
  }
}

/**
 * @brief Used to fix the inputs of loop phis to come from peel.
 * @param header The loop header.
 * @param cloner The instruction cloner that is being used to add instructions to peel.
 */
static void FixLoopPhis(const HBasicBlock* header, const HInstructionCloner& cloner) {
  // During the peeling we might generate Phi duplicate:
  // If two Phi nodes has a different pre-header input but the same from loop input
  // then after peeling they will be identical and we can do a clean-up.
  std::map<int, HPhi*> duplicates;
  std::map<HPhi*, std::vector<HPhi*>> replacement;
  for (HInstructionIterator it(header->GetPhis()); !it.Done(); it.Advance()) {
    HInstruction* phi = it.Current();
    if (phi->IsLoopHeaderPhi()) {
      DCHECK_EQ(phi->InputCount(), 2u);
      HInstruction* loop_input = phi->InputAt(1);

      // Get clone of the loop's input.
      HInstruction* clone = cloner.GetClone(loop_input);
      if (clone != nullptr) {
        phi->ReplaceInput(clone, 0);
        // We replaced the pre-header input with a clone of from loop input.
        // If we did before the same with other Phi node then we
        // created a duplicate => eliminate it.
        auto dup = duplicates.find(loop_input->GetId());
        if (dup != duplicates.end()) {
          // We cannot eliminate it now because duplicates may have different
          // clones and if there is other Phi node which is not handled yet
          // then it will use the incorrect clone. So for now just remember
          // what we should eliminate and do it later.
          replacement[dup->second].push_back(phi->AsPhi());
        } else {
          // Remember our change.
          duplicates[loop_input->GetId()] = phi->AsPhi();
        }
      }
    }
  }

  // Now eliminate duplicates if there are any.
  for (auto it1 : replacement) {
    for (auto it2 : it1.second) {
      it2->ReplaceWith(it1.first);
      it2->GetBlock()->RemovePhi(it2);
    }
  }
}

/**
 * @brief Used to add cloned instructions to the cloned blocks.
 * @param graph The graph in which all the instructions reside.
 * @param loop The loop currently being peeled.
 * @param loop_exits Set of the exits of loop currently being peeled.
 * @param cloner The instruction cloner that is being used to add instructions to peel.
 * @param old_to_new_bbs The mapping between original blocks and the cloned blocks.
 * @param env_to_instr The map of all environments in graph to the instruction that
 * encompasses them.
 */
static void AddClonedInstructions(const HGraph_X86* graph,
                                  const HLoopInformation_X86* loop,
                                  const std::set<HBasicBlock*>& loop_exits,
                                  const HInstructionCloner& cloner,
                                  const SafeMap<HBasicBlock*, HBasicBlock*>& old_to_new_bbs,
                                  const SafeMap<HEnvironment*, HInstruction*>& env_to_instr) {
  for (HBlocksInLoopReversePostOrderIterator it_loop(*loop);
       !it_loop.Done();
       it_loop.Advance()) {
    HBasicBlock* original_bb = it_loop.Current();
    HBasicBlock* copy_bb = old_to_new_bbs.Get(original_bb);

    for (HInstructionIterator it(original_bb->GetPhis()); !it.Done(); it.Advance()) {
      HInstruction* orig = it.Current();
      HInstruction* clone = cloner.GetClone(orig);

      // Sometimes the clone is manually added for phi nodes to represent original input.
      // Because of this, we are careful to not add a non-phi node to the phi list.
      if (clone != nullptr && clone->GetBlock() == nullptr) {
        if (clone->IsPhi()) {
          HPhi* phi_clone = clone->AsPhi();
          copy_bb->AddPhi(phi_clone);
        } else {
          // We should not have a clone without a block if it is not equivalent
          // to the type that it was cloned from - which is phi in this case.
          DCHECK(false) << "Unreachable: Found non-phi clone for phi with no block.";
        }
      }

      if (clone != nullptr && clone->GetBlock() != nullptr) {
        // Now add appropriate phi nodes after loop.
        AddExitPhisAfterPeel(graph, loop, loop_exits, env_to_instr, orig, clone);
      }
    }
    for (HInstructionIterator it(original_bb->GetInstructions()); !it.Done(); it.Advance()) {
      HInstruction* orig = it.Current();
      HInstruction* clone = cloner.GetClone(orig);
      if (clone != nullptr && clone->GetBlock() == nullptr) {
        copy_bb->AddInstruction(clone);
      }

      if (clone != nullptr && clone->GetBlock() != nullptr) {
        // Now add appropriate phi nodes after loop.
        AddExitPhisAfterPeel(graph, loop, loop_exits, env_to_instr, orig, clone);
      }
    }
  }
}

/**
 * @brief Used to clone instruction from main loop to use for peel.
 * @details Suspend checks and loop phis are treated specially.
 * @param loop The loop being peeled.
 * @param cloner The instance of instruction cloner.
 */
static void CloneInstructionsForPeel(const HLoopInformation_X86* loop,
                                     HInstructionCloner* cloner) {
  // Walk in reverse post-order so that cloning works correctly in using cloned inputs.
  for (HBlocksInLoopReversePostOrderIterator it_loop(*loop);
       !it_loop.Done();
       it_loop.Advance()) {
    HBasicBlock* original_bb = it_loop.Current();

    for (HInstructionIterator it(original_bb->GetPhis()); !it.Done(); it.Advance()) {
      HInstruction* phi = it.Current();
      if (phi->IsLoopHeaderPhi()) {
        // It is a requirement that loop has a single preheader so it must
        // be the case that the phi has only two inputs. We make the assumption
        // of this in the logic above so recheck it now.
        DCHECK_EQ(phi->InputCount(), 2u);

        // Loop phis can be skipped because the peel is no longer a loop.
        // However, in the mapping table we want to make it so that the input to phi
        // from define outside of loop is the actual mapping.
        HInstruction* out_of_loop_input = phi->InputAt(0);
        cloner->AddCloneManually(phi, out_of_loop_input);
      } else {
        phi->Accept(cloner);
      }
    }

    for (HInstructionIterator it(original_bb->GetInstructions()); !it.Done(); it.Advance()) {
      HInstruction* instruction = it.Current();
      if (instruction->IsSuspendCheck()) {
        // No need to copy suspend checks in the peel.
        continue;
      } else {
        instruction->Accept(cloner);
      }
    }
  }
}

bool HLoopInformation_X86::PeelHelperHead() {
  SafeMap<HBasicBlock*, HBasicBlock*> old_to_new_bbs;
  SafeMap<HEnvironment*, HInstruction*> env_to_instr;
  std::set<HBasicBlock*> loop_exits;

  // Make a copy of each block.
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(this->graph_);

  for (HBlocksInLoopReversePostOrderIterator it_loop(*this);
       !it_loop.Done();
       it_loop.Advance()) {
    HBasicBlock* original = it_loop.Current();
    HBasicBlock* copy = graph->CreateNewBasicBlock(original->GetDexPc());
    DCHECK(copy != nullptr);
    old_to_new_bbs.Put(original, copy);
    peeled_blocks_.Insert(copy->GetBlockId());
  }

  // The loop's old preheader must now go to the copied header.
  HBasicBlock* header = this->GetHeader();
  HBasicBlock* preheader = this->GetPreHeader();
  HLoopInformation_X86* outer_loop_info = LOOPINFO_TO_LOOPINFO_X86(preheader->GetLoopInformation());

  // Fix the predecessors and successors by substituting links with the copy.
  for (HBlocksInLoopIterator it_loop(*this); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* original = it_loop.Current();
    HBasicBlock* copy = old_to_new_bbs.Get(original);
    for (size_t idx = 0; idx < original->GetSuccessors().Size(); ++idx) {
      HBasicBlock* orig_successor = original->GetSuccessors().Get(idx);

      if (orig_successor == header) {
        // Handle case where it goes to header - since this is peeled version,
        // we must go to the old header.
        // Use predecessor replacement because it preserves index of predecessor,
        // which is important for phi nodes.
        header->ReplacePredecessor(preheader, copy);
      } else if (old_to_new_bbs.count(orig_successor) == 0) {
        // Handle the case where this is an exit from loop.
        copy->AddSuccessor(orig_successor);
        // We are creating two critical edges, so split them here.
        graph->SplitCriticalEdgeAndUpdateLoopInformation(copy, orig_successor);
        graph->SplitCriticalEdgeAndUpdateLoopInformation(original, orig_successor);
        loop_exits.insert(orig_successor);
      } else {
        // Handle the normal case where we must link to the copy.
        HBasicBlock* copy_successor = old_to_new_bbs.Get(orig_successor);
        copy->AddSuccessor(copy_successor);
      }
    }

    // At same time, fix the loop information of outer loop to contain the peeled blocks.
    if (outer_loop_info != nullptr) {
      // AddToAll will set loop info for our block.
      outer_loop_info->AddToAll(copy);
    }
  }

  // Attach the peeled blocks to old pre-header.
  preheader->AddSuccessor(old_to_new_bbs.Get(header));

  // We will need to insert phi nodes later in loop exits and update all users,
  // including environment. Since the environment does not have mapping back
  // to the instruction that contains it, we get that mapping now.
  graph->FillEnvironmentToInstruction(&env_to_instr);

  // Copy the instructions without adding them to any block yet.
  HInstructionCloner cloner(graph, &env_to_instr);
  CloneInstructionsForPeel(this, &cloner);
  DCHECK(cloner.AllOkay());

  // Now that instructions are copied, we must add them to their appropriate blocks.
  AddClonedInstructions(graph, this, loop_exits, cloner, old_to_new_bbs, env_to_instr);

  // Finally fix all of the phi inputs to actual loop. This is because the original
  // definition that was incoming to the loop will be coming from the peel now.
  FixLoopPhis(header, cloner);

  // Fix up the domination information now.
  // We would save on compile time if we did it incrementally, but doing this
  // way allows fewer mistakes to be made.
  graph->RebuildDomination();

  return true;
}

bool HLoopInformation_X86::HasInvokes() const {
  for (HBlocksInLoopIterator it_loop(*this); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* loop_block = it_loop.Current();

    // Go through each of the BB's instructions to check if there is at least one invoke.
    for (HInstructionIterator insn_it(loop_block->GetInstructions()); !insn_it.Done(); insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();

      if (insn->IsInvoke()) {
        return true;
      }
    }
  }

  return false;
}

bool HLoopInformation_X86::ContainsComplexInstructions() const {
  for (HBlocksInLoopIterator bb_it(*this); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* bb = bb_it.Current();
    for (HInstructionIterator insn_it(bb->GetInstructions()); !insn_it.Done(); insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();

      if (insn->IsReturn() ||
          insn->IsNewInstance() ||
          insn->IsNewArray() ||
          insn->IsArrayLength() ||
          insn->IsThrow() ||
          insn->IsInvoke() ||
          insn->IsLoadException()) {
        return true;
      }
    }
  }
  return false;
}

bool HLoopInformation_X86::RemoveFromGraph() {
  // The method currently supports innermost-level loops only.
  if (!IsInner()) {
    return false;
  }

  // First, we want to retrieve the loop pre-header and exit blocks.
  HBasicBlock* pre_header = GetPreHeader();
  HBasicBlock* exit_block = GetExitBlock();

  // If we can't take either of these blocks, we can't go any further.
  if (pre_header == nullptr || exit_block == nullptr) {
    return false;
  }

  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);

  // We want to temporarily hold the loop blocks in order to not upset the loop iterator.
  GrowableArray<HBasicBlock*> blocks_in_loop(graph->GetArena(), graph->GetBlocks().Size());
  for (HBlocksInLoopIterator it_loop(*this); !it_loop.Done(); it_loop.Advance()) {
    HBasicBlock* loop_block = it_loop.Current();
    DCHECK(loop_block->IsInLoop());  // Paranoid.
    DCHECK(loop_block->GetLoopInformation() == this);  // Paranoid.
    blocks_in_loop.Add(loop_block);
  }

  // Change the successor to the preheader to the exit block.
  DCHECK_EQ(pre_header->GetSuccessors().Size(), 1u);

  // Update the pre_header successor with the exit block.
  HBasicBlock* loop_header = GetHeader();
  DCHECK_EQ(loop_header, pre_header->GetSuccessors().Get(0));
  pre_header->ReplaceSuccessor(loop_header, exit_block);
  pre_header->ReplaceDominatedBlock(loop_header, exit_block);

  // Effective deletion of the loop blocks.
  for (size_t i = 0, e = blocks_in_loop.Size(); i < e; ++i) {
    HBasicBlock* loop_block = blocks_in_loop.Get(i);
    graph->DeleteBlock(loop_block);
  }

  // We want to remove the innermost loop from the parent (if any).
  HLoopInformation_X86* parent = GetParent();
  HLoopInformation_X86* prev_sibling = GetPrevSibling();
  HLoopInformation_X86* next_sibling = GetNextSibling();

  // Update the next sibling link.
  if (next_sibling != nullptr) {
    next_sibling->sibling_previous_ = prev_sibling;
  }

  // Update the previous sibling link.
  if (prev_sibling != nullptr) {
    prev_sibling->sibling_next_ = next_sibling;
  }

  // Update the parent link if this loop was the first of the parent's inner loops.
  if (parent != nullptr && parent->inner_ == this) {
    parent->inner_ = next_sibling;
  }

  // Update the graph's loop information if necessary, by either the next sibling or the parent.
  if (graph->GetLoopInformation() == this) {
    if (next_sibling == nullptr) {
      graph->SetLoopInformation(parent);
    } else {
      graph->SetLoopInformation(next_sibling);
    }
  }

  HLoopInformation_X86* tmp = this;
  // Then for each nested level, we also want to remove all of the old loop's blocks.
  while (tmp != nullptr) {
    for (size_t i = 0, e = blocks_in_loop.Size(); i < e; ++i) {
      tmp->Remove(blocks_in_loop.Get(i));
    }
    tmp = tmp->GetParent();
  }

  return true;
}

bool HLoopInformation_X86::CanThrow() const {
  for (HBlocksInLoopIterator bb_it(*this); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* bb = bb_it.Current();
    for (HInstructionIterator insn_it(bb->GetInstructions()); !insn_it.Done(); insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();

      if (insn->CanThrow()) {
        return true;
      }
    }
  }

  return false;
}

uint64_t HLoopInformation_X86::CountInstructionsInBody(bool skip_suspend_checks) const {
  uint64_t nb_instructions = 0u;

  for (HBlocksInLoopIterator bb_it(*this); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* bb = bb_it.Current();
    for (HInstructionIterator insn_it(bb->GetInstructions());
         !insn_it.Done();
         insn_it.Advance()) {
      HInstruction::InstructionKind kind = insn_it.Current()->GetKind();
      if (skip_suspend_checks) {
        switch (kind) {
          case HInstruction::kSuspendCheck:
          case HInstruction::kTestSuspend:
          case HInstruction::kSuspend:
            continue;
          default:
            break;
        }
      }
      nb_instructions++;
    }
  }

  return nb_instructions;
}

HInstruction* HLoopInformation_X86::PhiInput(HPhi* phi, bool inside_of_loop) {
  DCHECK(phi != nullptr);
  DCHECK_EQ(phi->InputCount(), 2u);
  DCHECK(phi->GetBlock()->GetLoopInformation() == this);

  // This method assumes that the first predecessor of the loop header is
  // the loop pre-header.
  uint32_t input_id = inside_of_loop ? 1u : 0u;
  return phi->InputAt(input_id);
}

}  // namespace art
