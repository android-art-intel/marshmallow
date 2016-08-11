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

#include "loop_iterators.h"
#include "pure_invokes_analysis.h"

namespace art {

void HPureInvokesAnalysis::Run() {
  // Reduce the scope to method with loops only to not hurt compile time.
  HOnlyInnerLoopIterator loop_iter(GRAPH_TO_GRAPH_X86(graph_)->GetLoopInformation());
  if (loop_iter.Done()) {
    // TODO: This limitation can be removed when the purity checks gets cheaper.
    PRINT_PASS_OSTREAM_MESSAGE(this, "Skip the method " << GetMethodName(graph_)
                                     << " because it has no loops");
    return;
  }
  PRINT_PASS_OSTREAM_MESSAGE(this, "Start " << GetMethodName(graph_));
  ResolvedInvokes hoisting_candidates;

  if (ProcessPureInvokes(hoisting_candidates)) {
    PRINT_PASS_OSTREAM_MESSAGE(this, "Found " << hoisting_candidates.size()
                                     << " invoke candidates for hoisting");
    HoistPureInvokes(hoisting_candidates);
  } else {
    PRINT_PASS_MESSAGE(this, "Found no candidates for pure invokes hoisting");
  }

  PRINT_PASS_OSTREAM_MESSAGE(this, "End " << GetMethodName(graph_));
}

bool HPureInvokesAnalysis::CanReturnNull(HInstruction* insn) {
  if (insn->IsNewInstance()) {
    // Constructors never return null.
    return false;
  }

  if (insn->IsNullCheck()) {
    // Only if we proved that the arg of the NullCheck is not null.
    return CanReturnNull(insn->InputAt(0));
  }

  if (insn->IsInvokeStaticOrDirect()) {
    // Check the non-null method whitelist for this.
    return IsInvokeThatCanReturnNull(insn->AsInvokeStaticOrDirect());
  }

  // We can guarantee nothing for other instructions.
  return true;
}

bool HPureInvokesAnalysis::IsPureMethodInvoke(HInvokeStaticOrDirect* call) {
  DCHECK(call != nullptr);
  const MethodReference target_method = call->GetTargetMethod();
  // Do we already know the answer for this method?
  auto it_pure = pure_invokes_.find(target_method);
  if (it_pure != pure_invokes_.end()) {
    return it_pure->second;
  }

  // The whitelist contains known pure methods.
  const char* whitelist[] = {
    // String methods.
    "bool java.lang.String.isEmpty()",
    "int java.lang.String.indexOf(int)",
    "int java.lang.String.indexOf(int, int)",
    "int java.lang.String.indexOf(java.lang.String)",
    "int java.lang.String.indexOf(java.lang.String, int)",
    "int java.lang.String.lastIndexOf(int)",
    "int java.lang.String.lastIndexOf(int, int)",
    "int java.lang.String.lastIndexOf(java.lang.String)",
    "int java.lang.String.lastIndexOf(java.lang.String, int)",
    "int java.lang.String.length()",
    "java.lang.String java.lang.String.toString()",
    // StringBuffer methods.
    "bool java.lang.StringBuffer.isEmpty()",
    "int java.lang.StringBuffer.indexOf(int)",
    "int java.lang.StringBuffer.indexOf(int, int)",
    "int java.lang.StringBuffer.indexOf(java.lang.String)",
    "int java.lang.StringBuffer.indexOf(java.lang.String, int)",
    "int java.lang.StringBuffer.lastIndexOf(int)",
    "int java.lang.StringBuffer.lastIndexOf(int, int)",
    "int java.lang.StringBuffer.lastIndexOf(java.lang.String)",
    "int java.lang.StringBuffer.lastIndexOf(java.lang.String, int)",
    "int java.lang.StringBuffer.length()",
    "java.lang.String java.lang.StringBuffer.toString()",
    // StringBuilder methods.
    "bool java.lang.StringBuilder.isEmpty()",
    "int java.lang.StringBuilder.indexOf(int)",
    "int java.lang.StringBuilder.indexOf(int, int)",
    "int java.lang.StringBuilder.indexOf(java.lang.String)",
    "int java.lang.StringBuilder.indexOf(java.lang.String, int)",
    "int java.lang.StringBuilder.lastIndexOf(int)",
    "int java.lang.StringBuilder.lastIndexOf(int, int)",
    "int java.lang.StringBuilder.lastIndexOf(java.lang.String)",
    "int java.lang.StringBuilder.lastIndexOf(java.lang.String, int)",
    "int java.lang.StringBuilder.length()",
    "java.lang.String java.lang.StringBuilder.toString()",
    // Math methods.
    "int java.lang.Math.abs(int)",
    "long java.lang.Math.abs(long)",
    "float java.lang.Math.abs(float)",
    "double java.lang.Math.abs(double)",
    "double java.lang.Math.asin(double)",
    "double java.lang.Math.acos(double)",
    "double java.lang.Math.atan(double)",
    "double java.lang.Math.atan2(double, double)",
    "double java.lang.Math.cbrt(double)",
    "double java.lang.Math.ceil(double)",
    "double java.lang.Math.copySign(double, double)",
    "double java.lang.Math.cos(double)",
    "double java.lang.Math.cosh(double)",
    "double java.lang.Math.exp(double)",
    "double java.lang.Math.expm1(double)",
    "double java.lang.Math.floor(double)",
    "int java.lang.Math.getExponent(double)",
    "int java.lang.Math.getExponent(float)",
    "double java.lang.Math.hypot(double, double)",
    "double java.lang.Math.log(double)",
    "double java.lang.Math.log10(double)",
    "double java.lang.Math.max(double, double)",
    "float java.lang.Math.max(float, float)",
    "int java.lang.Math.max(int, int)",
    "long java.lang.Math.max(long, long)",
    "double java.lang.Math.min(double, double)",
    "float java.lang.Math.min(float, float)",
    "int java.lang.Math.min(int, int)",
    "long java.lang.Math.min(long, long)",
    "double java.lang.Math.nextAfter(double, double)",
    "float java.lang.Math.nextAfter(float, double)",
    "double java.lang.Math.nextUp(double)",
    "float java.lang.Math.nextUp(float)",
    "double java.lang.Math.pow(double, double)",
    "double java.lang.Math.rint(double)",
    "long java.lang.Math.round(double)",
    "int java.lang.Math.round(float)",
    "double java.lang.Math.scalb(double, int)",
    "float java.lang.Math.scalb(float, int)",
    "double java.lang.Math.signum(double)",
    "float java.lang.Math.signum(float)",
    "double java.lang.Math.sin(double)",
    "double java.lang.Math.sinh(double)",
    "double java.lang.Math.sqrt(double)",
    "double java.lang.Math.tan(double)",
    "double java.lang.Math.tanh(double)",
    "double java.lang.Math.toDigrees(double)",
    "double java.lang.Math.toRadians(double)",
    "double java.lang.Math.ulp(double)",
    "float java.lang.Math.ulp(float)",
  };
  const size_t len = arraysize(whitelist);
  const std::string method_name = PrettyMethod(target_method.dex_method_index,
                                               *target_method.dex_file);
  const char* char_name = method_name.c_str();
  for (size_t i = 0; i < len; i++) {
    if (strcmp(char_name, whitelist[i]) == 0) {
        pure_invokes_.Put(target_method, true);
        // Remember that this one is pure.
        return true;
    }
  }
  // Remember that this one is not pure.
  pure_invokes_.Put(target_method, false);
  return false;
}

bool HPureInvokesAnalysis::IsInvokeThatCanReturnNull(HInvokeStaticOrDirect* call) {
  DCHECK(call != nullptr);
  const MethodReference target_method = call->GetTargetMethod();
  // Do we already know the answer for this method?
  auto it_null = null_invokes_.find(target_method);
  if (it_null != null_invokes_.end()) {
    return it_null->second;
  }

  // We assume that all invokes except the whitelist can return null.
  const char* whitelist[] = {
    // String methods.
    "java.lang.String java.lang.String.toString()",
    // StringBuffer methods.
    "java.lang.String java.lang.StringBuffer.toString()",
    // StringBuilder methods.
    "java.lang.String java.lang.StringBuilder.toString()",
  };
  const size_t len = arraysize(whitelist);
  const std::string method_name = PrettyMethod(target_method.dex_method_index,
                                               *target_method.dex_file);
  const char* char_name = method_name.c_str();
  for (size_t i = 0; i < len; i++) {
    if (strcmp(char_name, whitelist[i]) == 0) {
        null_invokes_.Put(target_method, false);
        return false;
    }
  }
  null_invokes_.Put(target_method, true);
  return true;
}

bool HPureInvokesAnalysis::ProcessPureInvokes(ResolvedInvokes& hoisting_candidates) {
  DCHECK(hoisting_candidates.empty());
  for (HPostOrderIterator block_iter(*graph_); !block_iter.Done(); block_iter.Advance()) {
    HBasicBlock* block = block_iter.Current();
    auto loop = LOOPINFO_TO_LOOPINFO_X86(block->GetLoopInformation());
    bool can_hoist_from_block = loop != nullptr &&
                                loop->IsInner() &&
                                loop->IsBottomTested() &&
                                loop->GetParent() == nullptr;

    for (HBackwardInstructionIterator insn_iter(block->GetInstructions());
         !insn_iter.Done(); insn_iter.Advance()) {
      HInstruction* insn = insn_iter.Current();
      // Paranoid.
      DCHECK(insn != nullptr);

      if (insn->IsNullCheck()) {
        if (!insn->HasEnvironmentUses() && !CanReturnNull(insn->InputAt(0))) {
          insn->ReplaceWith(insn->InputAt(0));
          PRINT_PASS_OSTREAM_MESSAGE(this, "Eliminated nullcheck " << insn
                                           << " because its argument is guaranteed to be not null");
          MaybeRecordStat(MethodCompilationStat::kIntelUselessNullCheckDeleted);
          block->RemoveInstruction(insn);
        }
        continue;
      }

      auto call = insn->AsInvokeStaticOrDirect();
      if (call == nullptr) {
        // We are interested in static/direct invokes only.
        continue;
      }
      if (!IsPureMethodInvoke(call)) {
        // We can optimize away only invokes with no side effects.
        continue;
      }

      if (call->IsStatic()) {
        // For static pure invoke, it is enough to check that the result is unused.
        if (!call->HasUses()) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "Eliminated static invoke " << call
                                           << " of pure method "
                                           << CalledMethodName(call));
          MaybeRecordStat(MethodCompilationStat::kIntelPureStaticCallDeleted);
          block->RemoveInstruction(call);
        } else if (can_hoist_from_block) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "Found candidate for pure static"
                                           << " invoke hoisting: " << call);
          hoisting_candidates.push_back(call);
        }
      } else {
        // The argument should not be null to prove that this invoke cannot throw
        // NullPointerException.
        HInstruction* callee_object = call->InputAt(0);
        if (CanReturnNull(callee_object)) {
          continue;
        }

        if (!call->HasUses()) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "Eliminated direct invoke " << call
                                           << " of pure method "
                                           << CalledMethodName(call)
                                           << " because it is called for not-null object "
                                           << callee_object);
          MaybeRecordStat(MethodCompilationStat::kIntelPureDirectCallDeleted);
          block->RemoveInstruction(call);
        } else if (can_hoist_from_block) {
          PRINT_PASS_OSTREAM_MESSAGE(this, "Found candidate for pure direct"
                                           << " invoke hoisting: " << call);
          // This invoke is a potential candidate for hoisting.
          hoisting_candidates.push_back(call);
        }
      }
    }
  }

  return !hoisting_candidates.empty();
}

bool HPureInvokesAnalysis::CalledNotPureMethodInChain(HInstruction* insn,
  std::unordered_set<HInstruction*>& objects_with_not_pure_invokes) {
  if (objects_with_not_pure_invokes.find(insn) != objects_with_not_pure_invokes.end()) {
    return true;
  } else if (insn->IsInvokeStaticOrDirect()) {
    auto obj = insn->InputAt(0);
    return CalledNotPureMethodInChain(obj, objects_with_not_pure_invokes);
  } else {
    return false;
  }
}

bool HPureInvokesAnalysis::HoistPureInvokes(ResolvedInvokes& hoisting_candidates) {
  DCHECK(!hoisting_candidates.empty());
  return false;
}

}  // namespace art
