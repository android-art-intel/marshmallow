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


#include "base/stringprintf.h"
#include "ext_utility.h"
#include "graph_x86.h"
#include "loop_iterators.h"
#include "nodes.h"
#include "trivial_loop_evaluator.h"

namespace art {

void TrivialLoopEvaluator::Run() {
  // Build the list of loops belonging to the CFG.
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86* loop_start = graph->GetLoopInformation();
  bool graph_updated = false;
  PRINT_PASS_OSTREAM_MESSAGE(this, "TrivialLoopEvaluator: Begin " << GetMethodName(c_unit_));

  // For each inner loop in the graph, we will try to apply TLE.
  for (HOnlyInnerLoopIterator it(loop_start); !it.Done(); it.Advance()) {
    HLoopInformation_X86* loop = it.Current();

    // First, we need to check that the loop is a valid candidate for TLE.
    if (LoopGate(loop) == false) {
      continue;
    }

    // This container will hold all the evaluated values of the loop.
    ValuesHolder values_holder((std::less<HInstruction*>()));

    // Then, we will evaluate the loop if possible.
    if (!EvaluateLoop(loop, values_holder)) {
      continue;
    }

    // Now that the loop is evaluated, we want to know which values to write back
    // to the graph. to_update is a subset of values_holder.
    ValuesToUpdate to_update;
    SelectRegistersToUpdate(loop, values_holder, to_update);

    PRINT_PASS_OSTREAM_MESSAGE(this, to_update.size() << " ssa registers need to be updated.");

    // The last step is to write back the values to the registers using the evaluated values.
    UpdateRegisters(loop, to_update);

    // At this point, we have updated the graph at least once while executing this method.
    graph_updated = true;

    // Finally, we want to remove the now-useless loop from the graph.
    if (!loop->RemoveFromGraph()) {
      LOG(FATAL) << "TrivialLoopEvaluator: Could not remove loop from the graph.";
    } else {
      MaybeRecordStat(MethodCompilationStat::kIntelRemoveTrivialLoops);
      PRINT_PASS_OSTREAM_MESSAGE(this, "TrivialLoopEvaluator: Loop \"" << loop << "\" of method \""
                << GetMethodName(c_unit_) << "\" has been statically evaluated by TLE.");
    }
  }

  if (graph_updated) {
    graph->RebuildDomination();
  }
  PRINT_PASS_OSTREAM_MESSAGE(this, "TrivialLoopEvaluator: End " << GetMethodName(c_unit_));
}

static bool StorePhiConstantValue(ValuesHolder& values_holder, HPhi* phi, HInstruction* input) {
  DCHECK(phi != nullptr);
  DCHECK(input != nullptr);

  // We don't want to store the constant if the input already exists in the values_holder
  // because it is not necessary.
  if (values_holder.find(input) != values_holder.end() ||
      values_holder.find(phi) != values_holder.end()) {
    return false;
  }

  // We need to make sure the input is constant of primitve type before trying to store it.
  if (!input->IsConstant() || input->IsNullConstant()) {
    return false;
  }

  values_holder.Overwrite(phi, ConstantValue(input->AsConstant()));

  return true;
}

static bool StoreConstantValue(ValuesHolder& values_holder, HInstruction* input) {
  DCHECK(input != nullptr);

  // We don't want to store a constant if the input already exists in the values_holder
  // because it is not necessary.
  if (values_holder.find(input) != values_holder.end()) {
    return true;
  }

  // We need to make sure the input is constant of primitve type before trying to store it.
  if (!input->IsConstant() || input->IsNullConstant()) {
    return false;
  }

  values_holder.Overwrite(input, ConstantValue(input->AsConstant()));

  return true;
}

static bool UpdatePhis(HLoopInformation_X86* loop,
                       ValuesHolder& values_holder, HBasicBlock* new_bb) {
  DCHECK(new_bb != nullptr);


  // We need to handle phi nodes to update their values.
  // Note that we have to execute the phis as if they were being evaluated in
  // parallel to get the correct values.
  std::map<HPhi*, ConstantValue> new_phi_values;
  for (HInstructionIterator phi_it(new_bb->GetPhis()); !phi_it.Done(); phi_it.Advance()) {
    HInstruction* insn = phi_it.Current();
    HPhi* phi = insn->AsPhi();

    DCHECK(values_holder.find(insn) != values_holder.end());
    size_t input_count = phi->InputCount();

    // We don't support phi nodes with more than two inputs for now, because it requires
    // extra logic we don't require for now.
    if (input_count > 2) {
      return false;
    }

    bool phi_is_updated = false;
    for (size_t input_id = 0; input_id < input_count; input_id++) {
      HInstruction* input = phi->InputAt(input_id);

      if (!loop->Contains(*input->GetBlock())) {
        // We want to deal with the use coming from inside of the loop.
        continue;
      }

      if (values_holder.find(input) == values_holder.end()) {
        // We don't want to update the phi if we don't have the inside-of-loop value.
        // If we get here it means we are starting the first iteration.
        continue;
      }

      // Remember this for updating in parallel.
      new_phi_values[phi] = values_holder.Get(input);
      phi_is_updated = true;
    }

    // There must be at least one phi input coming from inside of the loop.
    if (!phi_is_updated) {
      return false;
    }
  }

  // Now update all the phi values as if in parallel.
  for (auto it : new_phi_values) {
    // Copy the value back to the phi ssa reg.
    values_holder.Overwrite(it.first, it.second);
  }

  return true;
}

bool TrivialLoopEvaluator::EvaluateLoop(HLoopInformation_X86* loop, ValuesHolder& values_holder) {
  unsigned int loop_iterations = loop->GetNumIterations(loop->GetHeader());

  // First of all, we need to handle Phi nodes to initialize the values_holder map correctly.
  // It may hold values from outside of the loop and we need to initialize them accordingly.
  // They will be used later in the loop evaluation.
  for (HBlocksInLoopIterator bb_it(*loop); !bb_it.Done(); bb_it.Advance()) {
    HBasicBlock* bb = bb_it.Current();

    // Go through each phi instruction in the loop.
    for (HInstructionIterator phi_it(bb->GetPhis()); !phi_it.Done(); phi_it.Advance()) {
      HInstruction* insn = phi_it.Current();
      HPhi* phi = insn->AsPhi();
      size_t input_count = phi->InputCount();

      // We don't support phis nodes with more than two inputs for now, because it requires
      // extra logic we don't require for now.
      if (input_count > 2) {
        return false;
      }

      // For each input of the phi.
      bool phi_is_handled = false;
      for (size_t input_id = 0; input_id < phi->InputCount(); input_id++) {
        HInstruction* input = phi->InputAt(input_id);

        if (loop->Contains(*input->GetBlock())) {
          // We want to deal with the use coming from outside of the loop.
          continue;
        }

        // Store the value if it is constant, otherwise evaluation cannot start.
        if (!StorePhiConstantValue(values_holder, phi, input)) {
          PRINT_PASS_OSTREAM_MESSAGE(this,
            "Input \"" << input << "\" of phi \"" << insn << "\" is not constant.");
          return false;
        }
        phi_is_handled = true;
      }

      if (!phi_is_handled) {
        // We don't support phi nodes with intra-loop inputs only for now, because it requires
        // extra logic we don't require for now.
        PRINT_PASS_OSTREAM_MESSAGE(this,
            "The phi \"" << insn << "\" only has intra-loop inputs only,"
            << " and is not supported for now.");
        return false;
      }
    }
  }

  // For each iteration of the loop, execute its sequence of instructions.
  uint64_t current_iter = 0;
  HBasicBlock* first_bb = loop->GetHeader();
  HBasicBlock* current_block = first_bb;

  while (current_iter < loop_iterations) {
    DCHECK(loop->Contains(*current_block));  // Paranoid.

    // Now, we can go through each instruction within the basic block to evaluate them.
    for (HInstructionIterator insn_it(current_block->GetInstructions());
         !insn_it.Done();
         insn_it.Advance()) {
      HInstruction* insn = insn_it.Current();

      // We can ignore suspend checks for this optimization.
      if (insn->IsSuspendCheck()) {
        continue;
      }

      if (insn->IsConstant()) {
        // Instruction is a constant therefore we simply have to store it.
        bool overwritten = StoreConstantValue(values_holder, insn);
        // We must have the entry since we checked the insn is constant.
        DCHECK(overwritten);
      } else if (insn->IsTypeConversion()) {
        // Instruction is a cast operation: we have to be careful to store
        // correctly the types since the input has a different type than the result.
        HInstruction* input = insn->InputAt(0);

        // Insert the input in the values_holder if necessary.
        if (!StoreConstantValue(values_holder, input)) {
          PRINT_PASS_OSTREAM_MESSAGE(this,
              "Input \"" << input << "\" of unary instruction \""
              << insn << "\" is not constant");
          return false;
        }

        // Evaluate the cast operation.
        ConstantValue result;
        if (!Evaluate(insn, values_holder.Get(input), result)) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "Could not evaluate cast operation " << insn);
          return false;
        }

        // Write it back in the values holder.
        values_holder.Overwrite(insn, result);
      } else if (insn->IsUnaryOperation()) {
        // Instruction is an unary operation, therefore we have to evaluate the instruction.
        HInstruction* input = insn->InputAt(0);

        // Store the input if necessary.
        if (!StoreConstantValue(values_holder, input)) {
          PRINT_PASS_OSTREAM_MESSAGE(this,
              "Input \"" << input << "\" of unary instruction \""
              << insn << "\" is not constant");
          return false;
        }

        // Evaluate the unary operation.
        ConstantValue result;
        if (!Evaluate(insn, values_holder.Get(input), result)) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "Could not evaluate unary operation " << insn);
          return false;
        }

        // Write it back in the values holder.
        values_holder.Overwrite(insn, result);
      } else if (insn->IsBinaryOperation()) {
        // Instruction is a binary operation.
        HBinaryOperation* bop = insn->AsBinaryOperation();

        // Get the two operands of the instruction.
        HInstruction* left = bop->GetLeft();
        HInstruction* right = bop->GetRight();

        // Make sure both operands are available in the values_holder first.
        if (!StoreConstantValue(values_holder, left)) {
          PRINT_PASS_OSTREAM_MESSAGE(this,
              "Left input \"" << left << "\" of instruction \""
              << insn << "\" is not constant");
          return false;
        }

        if (!StoreConstantValue(values_holder, right)) {
          PRINT_PASS_OSTREAM_MESSAGE(this,
              "Right input \"" << right << "\" of instruction \""
              << insn << "\" is not constant");
          return false;
        }

        // Evaluate the binary operation.
        ConstantValue result;
        if (!Evaluate(insn->AsBinaryOperation(),
                      values_holder.Get(left),
                      values_holder.Get(right), result)) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "Could not evaluate binary operation " << insn);
          return false;
        }

        // Write it back in the values holder.
        values_holder.Overwrite(insn, result);
      } else if (insn->IsIf()) {
        // Instruction is a condition. We need to evaluate it to get the next
        // block to be evaluated.
        DCHECK_EQ(insn->InputCount(), 1u);
        HInstruction* input = insn->InputAt(0);
        HIf* if_insn = insn->AsIf();

        // Try to store the input if not already in values_holder.
        if (!StoreConstantValue(values_holder, input)) {
          PRINT_PASS_OSTREAM_MESSAGE(this,
              "If input \"" << input << "\" of instruction \"" << insn
              << "\" is not constant");
          return false;
        }

        // We get the constant corresponding to the input.
        const ConstantValue& result = values_holder.Get(input);

        DCHECK_EQ(result.type, Primitive::kPrimInt);

        // We overwrite the if in the values_holder in case we need its result.
        values_holder.Overwrite(insn, result);

        // We update the current block.
        current_block = result.value.i == 1 ?
                          if_insn->IfTrueSuccessor() :
                          if_insn->IfFalseSuccessor();
        // We will automatically exit this loop from here.
      } else if (insn->IsGoto()) {
        // Instruction is a goto, therefore we just need to update the current_block
        // to be the goto's successor block.
        current_block = insn->AsGoto()->GetSuccessor();
      } else {
        PRINT_PASS_OSTREAM_MESSAGE(this,
            "Could not evaluate instruction \"" << insn << "\".");
        return false;
      }
    }

    // Paranoid.
    DCHECK(current_block != nullptr);

    // We finished an iteration, because we either looped back to the first_bb, or
    // because we jumped out of the loop. Therefore, we need to:
    // - Increment the iteration count.
    // - Update the phi values of the previous iteration for the next one.
    if (current_block == first_bb || !loop->Contains(*current_block)) {
      current_iter++;
      if (!UpdatePhis(loop, values_holder, current_block)) {
        PRINT_PASS_OSTREAM_MESSAGE(this,
            "Could not update phi nodes properly, therefore loop evaluation"
            << " cannot be done.");
        return false;
      }
    }
  }
  // Paranoid check: we must have evaluated all our iterations. Otherwise, it means either bound
  // computation is broken, or we did not evaluate the loop properly.
  DCHECK_EQ(current_iter, loop_iterations);

  return true;
}

void TrivialLoopEvaluator::SelectRegistersToUpdate(HLoopInformation_X86* loop,
        const ValuesHolder& values_holder,
        ValuesToUpdate& to_update) {
  // From here, what we want to do is decide which values should be written back.
  // Not all of them should be: only the ones that are live after the end of the loop
  // should be written back.
  for (auto value : values_holder) {
    HInstruction* insn = value.first;
    // 1. Check that the value belongs to the loop. If they don't, we don't need to
    // write them back, because we haven't changed them as we only evaluated
    // the loop. We don't care about nesting here since we made sure to only
    // handle innermost loops.
    if (!loop->Contains(*insn->GetBlock())) {
      continue;
    }

    // 2. Check that the value is live after the loop.
    for (HUseIterator<HInstruction*> uses_it(insn->GetUses()); !uses_it.Done();
         uses_it.Advance()) {
      HInstruction* use = uses_it.Current()->GetUser();

      if (!loop->Contains(*use->GetBlock())) {
        // Here we are in a situation where the use has to be written back.
        // We do two things here:
        // - We save the constants for future use.
        // - We add them in the graph (in the ToHConstant method).
        to_update.Overwrite(value.first, value.second.ToHConstant(graph_));
      }
    }

    // We need to save the environment live outs as well, because we could reach
    // a debug point which needs them.
    if (insn->HasEnvironmentUses()) {
      for (HUseIterator<HEnvironment*> env_it(insn->GetEnvUses());
           !env_it.Done();
           env_it.Advance()) {
        HEnvironment* env = env_it.Current()->GetUser();
        DCHECK(env != nullptr);  // Paranoid.
        for (size_t input = 0; input < env->Size(); input++) {
          HInstruction* env_insn = env->GetInstructionAt(input);

          if (env_insn == nullptr) {
            continue;
          }

          if (values_holder.find(env_insn) != values_holder.end()) {
            to_update.Overwrite(value.first, value.second.ToHConstant(graph_));
          }
        }
      }
    }
  }
}

void TrivialLoopEvaluator::UpdateRegisters(HLoopInformation_X86* loop,
        const ValuesToUpdate& to_update) {
  DCHECK(loop != nullptr);

  // If there is nothing to update in the first place, we can return now.
  if (to_update.size() == 0u) {
    return;
  }

  // We want to find all the users of the values we need to write back.
  // Then, we replace the corresponding input by the HConstant.
  for (auto value : to_update) {
    HInstruction* insn = value.first;
    HConstant* constant = value.second;
    DCHECK(insn != nullptr);  // Paranoid.
    DCHECK(constant != nullptr);  // Paranoid.

    PRINT_PASS_OSTREAM_MESSAGE(this, "Start Replacing computed value: " << insn);

    HPhi* phi = insn->AsPhi();

    // We need to take care of the environment. Values might need to be alive for
    // debugging purposes, so we need to update them with the evaluated values too.
    for (HUseIterator<HEnvironment*> env_it(insn->GetEnvUses());
         !env_it.Done();
         env_it.Advance()) {
      HUseListNode<HEnvironment*>* user_node = env_it.Current();
      HEnvironment* env_user = user_node->GetUser();
      PRINT_PASS_OSTREAM_MESSAGE(this, "  Replace env use in: " << env_user);
      env_user->RemoveAsUserOfInput(user_node->GetIndex());
      if (phi != nullptr) {
        // For Phis that will be removed, set the environment to nullptr.
        env_user->SetRawEnvAt(user_node->GetIndex(), nullptr);
      } else {
        env_user->SetRawEnvAt(user_node->GetIndex(), constant);
        constant->AddEnvUseAt(env_user, user_node->GetIndex());
      }
    }

    // Go through each of the instruction's users.
    for (HUseIterator<HInstruction*> uses_it(insn->GetUses());
         !uses_it.Done();
         uses_it.Advance()) {
      HUseListNode<HInstruction*>* use_node = uses_it.Current();
      HInstruction* user = use_node->GetUser();

      PRINT_PASS_OSTREAM_MESSAGE(this, "  Replace use in: " << user);
      user->ReplaceInput(constant, use_node->GetIndex());
    }

    // Remove a Phi here, before deleting the blocks.
    if (phi != nullptr) {
      for (size_t i = 0, e = phi->InputCount(); i < e; ++i) {
        phi->RemoveAsUserOfInput(i);
      }
      phi->GetBlock()->RemovePhi(phi, false);
    }
  }
}

bool TrivialLoopEvaluator::LoopGate(HLoopInformation_X86* loop) {
  DCHECK(loop != nullptr);

  unsigned int num_blocks = loop->NumberOfBlocks();
  // Loops are not normalized. Therefore, we want to skip the first BB.
  if (num_blocks > 2) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Cannot apply TLE: loop has " << num_blocks << " blocks.");
    return false;
  }

  // We currently support only innermost loops.
  if (!loop->IsInner()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop is not innermost.");
    return false;
  }

  // The loop should not side exit because it would make the evaluation harder.
  if (!loop->HasOneExitEdge()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop has more than one exit block.");
    return false;
  }

  // We want to consider very simple loops only.
  if (!loop->IsVerySimple(false)) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop is not very simple.");
    return false;
  }

  // The loop should not have invokes since they are complex to evaluate.
  if (loop->HasInvokes()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop has invokes.");
    return false;
  }

  // The loop must have a known number of iterations in order to evaluate it.
  if (!loop->HasKnownNumIterations()) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Loop does not have a known number of iterations.");
    return false;
  }

  // The iteration count must be smaller than the threshold we fixed in order to
  // prevent increasing compile time too much.
  if (loop->GetNumIterations(loop->GetHeader()) > kLoopEvalMaxIter) {
    PRINT_PASS_OSTREAM_MESSAGE(this,
        "Loop has too many iterations (max supported : " << kLoopEvalMaxIter << ").");
    return false;
  }

  return true;
}

}  // namespace art.
