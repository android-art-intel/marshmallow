/*
 * Copyright (C) 2014 The Android Open Source Project
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
 *
 * Modified by Intel Corporation
 *
 */

#include "inliner.h"

#include "art_method-inl.h"
#include "builder.h"
#include "class_linker.h"
#include "constant_folding.h"
#include "dead_code_elimination.h"
#include "driver/compiler_driver-inl.h"
#include "driver/compiler_options.h"
#include "driver/dex_compilation_unit.h"
#include "instruction_simplifier.h"
#include "mirror/class_loader.h"
#include "mirror/dex_cache.h"
#include "nodes.h"
#include "optimizing_compiler.h"
#include "register_allocator.h"
#include "ssa_phi_elimination.h"
#include "scoped_thread_state_change.h"
#include "thread.h"
#include "dex/verified_method.h"
#include "dex/verification_results.h"

#include "graph_x86.h"

namespace art {

void HInliner::Run() {
  const CompilerOptions& compiler_options = compiler_driver_->GetCompilerOptions();
  if ((compiler_options.GetInlineDepthLimit() == 0)
      || (compiler_options.GetInlineMaxCodeUnits() == 0)) {
    return;
  }
  if (graph_->IsDebuggable()) {
    // For simplicity, we currently never inline when the graph is debuggable. This avoids
    // doing some logic in the runtime to discover if a method could have been inlined.
    return;
  }
  const GrowableArray<HBasicBlock*>& blocks = graph_->GetReversePostOrder();
  for (size_t i = 0; i < blocks.Size(); ++i) {
    HBasicBlock* block = blocks.Get(i);
    for (HInstruction* instruction = block->GetFirstInstruction(); instruction != nullptr;) {
      HInstruction* next = instruction->GetNext();
      HInvokeStaticOrDirect* call = instruction->AsInvokeStaticOrDirect();
      // As long as the call is not intrinsified, it is worth trying to inline.
      if (call != nullptr && call->GetIntrinsic() == Intrinsics::kNone) {
        // We use the original invoke type to ensure the resolution of the called method
        // works properly.
        if (!TryInline(call, call->GetDexMethodIndex())) {
          if (kIsDebugBuild && IsCompilingWithCoreImage()) {
            std::string callee_name =
                PrettyMethod(call->GetDexMethodIndex(), *outer_compilation_unit_.GetDexFile());
            bool should_inline = callee_name.find("$inline$") != std::string::npos;
            CHECK(!should_inline) << "Could not inline " << callee_name;
          }
        }
      }
      instruction = next;
    }
  }
}

bool HInliner::TryInline(HInvoke* invoke_instruction, uint32_t method_index) const {
  ScopedObjectAccess soa(Thread::Current());
  const DexFile& caller_dex_file = *caller_compilation_unit_.GetDexFile();
  VLOG(compiler) << "Try inlining " << PrettyMethod(method_index, caller_dex_file);

  ClassLinker* class_linker = caller_compilation_unit_.GetClassLinker();
  // We can query the dex cache directly. The verifier has populated it already.
  ArtMethod* resolved_method;
  if (invoke_instruction->IsInvokeStaticOrDirect()) {
    MethodReference ref = invoke_instruction->AsInvokeStaticOrDirect()->GetTargetMethod();
    resolved_method = class_linker->FindDexCache(*ref.dex_file)->GetResolvedMethod(
        ref.dex_method_index, class_linker->GetImagePointerSize());
  } else {
    resolved_method = class_linker->FindDexCache(caller_dex_file)->GetResolvedMethod(
        method_index, class_linker->GetImagePointerSize());
  }

  if (resolved_method == nullptr) {
    // Method cannot be resolved if it is in another dex file we do not have access to.
    VLOG(compiler) << "Method cannot be resolved " << PrettyMethod(method_index, caller_dex_file);
    return false;
  }

  if (resolved_method->ShouldNotInline()) {
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " was already flagged as non inlineable";
    return false;
  }

  const DexFile::CodeItem* code_item = resolved_method->GetCodeItem();

  if (code_item == nullptr) {
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " is not inlined because it is native";
    resolved_method->SetShouldNotInline();
    return false;
  }

  if (code_item->insns_size_in_code_units_ == 1) {
    const Instruction& instruction = *Instruction::At(code_item->insns_);
    if (instruction.Opcode() == Instruction::RETURN_VOID) {
      // Remove the invoke from the caller.
      invoke_instruction->GetBlock()->RemoveInstruction(invoke_instruction);
      VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                     << " contains only 'return void' instruction, so the invoke has been removed";
      MaybeRecordStat(kInlinedInvoke);
      return true;
    }
  }

  size_t inline_max_code_units = compiler_driver_->GetCompilerOptions().GetInlineMaxCodeUnits();
  if (code_item->insns_size_in_code_units_ > inline_max_code_units) {
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " is too big to inline";
    resolved_method->SetShouldNotInline();
    return false;
  }

  if (code_item->tries_size_ != 0) {
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " is not inlined because of try block";
    resolved_method->SetShouldNotInline();
    return false;
  }

  uint16_t class_def_idx = resolved_method->GetDeclaringClass()->GetDexClassDefIndex();
  if (!compiler_driver_->IsMethodVerifiedWithoutFailures(
        resolved_method->GetDexMethodIndex(), class_def_idx, *resolved_method->GetDexFile())) {
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " couldn't be verified, so it cannot be inlined";
    resolved_method->SetShouldNotInline();
    return false;
  }

  if (invoke_instruction->IsInvokeStaticOrDirect() &&
      invoke_instruction->AsInvokeStaticOrDirect()->IsStaticWithImplicitClinitCheck()) {
    // Case of a static method that cannot be inlined because it implicitly
    // requires an initialization check of its declaring class.
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " is not inlined because it is static and requires a clinit"
                   << " check that cannot be emitted due to Dex cache limitations";
    resolved_method->SetShouldNotInline();
    return false;
  }

  bool same_dex_file =
      IsSameDexFile(*outer_compilation_unit_.GetDexFile(), *resolved_method->GetDexFile());

  if (!TryBuildAndInline(resolved_method, invoke_instruction, method_index, same_dex_file)) {
    return false;
  }

  VLOG(compiler) << "Successfully inlined " << PrettyMethod(method_index, caller_dex_file);
  MaybeRecordStat(kInlinedInvoke);
  return true;
}

bool HInliner::TryBuildAndInline(ArtMethod* resolved_method,
                                 HInvoke* invoke_instruction,
                                 uint32_t method_index,
                                 bool can_use_dex_cache) const {
  ScopedObjectAccess soa(Thread::Current());
  const DexFile::CodeItem* code_item = resolved_method->GetCodeItem();
  const DexFile& caller_dex_file = *caller_compilation_unit_.GetDexFile();

  DexCompilationUnit dex_compilation_unit(
    nullptr,
    caller_compilation_unit_.GetClassLoader(),
    caller_compilation_unit_.GetClassLinker(),
    *resolved_method->GetDexFile(),
    code_item,
    resolved_method->GetDeclaringClass()->GetDexClassDefIndex(),
    resolved_method->GetDexMethodIndex(),
    resolved_method->GetAccessFlags(),
    nullptr);

  HGraph* callee_graph = new (graph_->GetArena()) HGraph_X86(
      graph_->GetArena(),
      caller_dex_file,
      method_index,
      compiler_driver_->GetInstructionSet(),
      graph_->IsDebuggable(),
      graph_->GetCurrentInstructionId());
  callee_graph->SetCodeGenerator(graph_->GetCodeGenerator());

  OptimizingCompilerStats inline_stats;
  HGraphBuilder builder(callee_graph,
                        &dex_compilation_unit,
                        &outer_compilation_unit_,
                        resolved_method->GetDexFile(),
                        compiler_driver_,
                        &inline_stats);

  if (!builder.BuildGraph(*code_item)) {
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " could not be built, so cannot be inlined";
    // There could be multiple reasons why the graph could not be built, including
    // unaccessible methods/fields due to using a different dex cache. We do not mark
    // the method as non-inlineable so that other callers can still try to inline it.
    return false;
  }

  if (!RegisterAllocator::CanAllocateRegistersFor(*callee_graph,
                                                  compiler_driver_->GetInstructionSet())) {
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " cannot be inlined because of the register allocator";
    resolved_method->SetShouldNotInline();
    return false;
  }

  if (!callee_graph->TryBuildingSsa()) {
    VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                   << " could not be transformed to SSA";
    resolved_method->SetShouldNotInline();
    return false;
  }

  // Run simple optimizations on the graph.
  HDeadCodeElimination dce(callee_graph, stats_);
  HConstantFolding fold(callee_graph, stats_);
  InstructionSimplifier simplify(callee_graph, stats_);

  HOptimization* optimizations[] = {
    &dce,
    &fold,
    &simplify,
  };

  for (size_t i = 0; i < arraysize(optimizations); ++i) {
    HOptimization* optimization = optimizations[i];
    optimization->Run();
  }

  if (depth_ + 1 < compiler_driver_->GetCompilerOptions().GetInlineDepthLimit()) {
    HInliner inliner(callee_graph,
                     outer_compilation_unit_,
                     dex_compilation_unit,
                     compiler_driver_,
                     stats_,
                     depth_ + 1);
    inliner.Run();
  }

  HReversePostOrderIterator it(*callee_graph);
  it.Advance();  // Past the entry block, it does not contain instructions that prevent inlining.
  for (; !it.Done(); it.Advance()) {
    HBasicBlock* block = it.Current();
    if (block->IsLoopHeader()) {
      VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                     << " could not be inlined because it contains a loop";
      resolved_method->SetShouldNotInline();
      return false;
    }

    for (HInstructionIterator instr_it(block->GetInstructions());
         !instr_it.Done();
         instr_it.Advance()) {
      HInstruction* current = instr_it.Current();
      if (current->IsSuspendCheck()) {
        continue;
      }

      // We only do this on the target. We still want deterministic inlining on the host.
      constexpr bool kInliningMustBeDeterministic = !kIsTargetBuild;

      if (current->CanThrow()) {
        if (GRAPH_TO_GRAPH_X86(graph_)->CanInlineThrower(callee_graph,
            invoke_instruction, current)) {
          continue;
        }

        VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                       << " could not be inlined because " << current->DebugName()
                       << " can throw";
        if (!kInliningMustBeDeterministic) {
          resolved_method->SetShouldNotInline();
        }
        return false;
      }

      if (current->NeedsEnvironment()) {
        VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                       << " could not be inlined because " << current->DebugName()
                       << " needs an environment";
        if (!kInliningMustBeDeterministic) {
          resolved_method->SetShouldNotInline();
        }
        return false;
      }

      if (!can_use_dex_cache && current->NeedsDexCache()) {
        VLOG(compiler) << "Method " << PrettyMethod(method_index, caller_dex_file)
                       << " could not be inlined because " << current->DebugName()
                       << " it is in a different dex file and requires access to the dex cache";
        // Do not flag the method as not-inlineable. A caller within the same
        // dex file could still successfully inline it.
        return false;
      }
    }
  }

  GRAPH_TO_GRAPH_X86(callee_graph)->InlineIntoExtended(graph_, invoke_instruction);

  if (callee_graph->HasBoundsChecks()) {
    graph_->SetHasBoundsChecks(true);
  }

  return true;
}

}  // namespace art
