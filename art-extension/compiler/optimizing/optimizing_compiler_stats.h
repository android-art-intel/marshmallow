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

#ifndef ART_COMPILER_OPTIMIZING_OPTIMIZING_COMPILER_STATS_H_
#define ART_COMPILER_OPTIMIZING_OPTIMIZING_COMPILER_STATS_H_

#include <sstream>
#include <string>

#include "atomic.h"

namespace art {

enum MethodCompilationStat {
  kAttemptCompilation = 0,
  kCompiledBaseline,
  kCompiledOptimized,
  kCompiledQuick,
  kInlinedInvoke,
  kInstructionSimplifications,
  kNotCompiledBranchOutsideMethodCode,
  kNotCompiledCannotBuildSSA,
  kNotCompiledCantAccesType,
  kNotCompiledClassNotVerified,
  kNotCompiledHugeMethod,
  kNotCompiledLargeMethodNoBranches,
  kNotCompiledMalformedOpcode,
  kNotCompiledNoCodegen,
  kNotCompiledNonSequentialRegPair,
  kNotCompiledPathological,
  kNotCompiledSpaceFilter,
  kNotCompiledUnhandledInstruction,
  kNotCompiledUnresolvedField,
  kNotCompiledUnresolvedMethod,
  kNotCompiledUnsupportedIsa,
  kNotCompiledVerifyAtRuntime,
  kNotOptimizedDisabled,
  kNotOptimizedRegisterAllocator,
  kNotOptimizedTryCatch,
  kRemovedCheckedCast,
  kRemovedDeadInstruction,
  kRemovedNullCheck,
  kConstantFolding,
  kBooleanSimplifier,
  kReferenceTypePropagation,
  kIntrinsicsRecognizer,
  kLICM,
  kIntelLoopPeeled,
  kIntelCCS,
  kIntelBIVFound,
  kIntelRemoveSuspendCheck,
  kIntelRemoveUnusedLoops,
  kIntelRemoveTrivialLoops,
  kIntelNonTemporalMove,
  kIntelFormBottomLoop,
  kIntelLHSS,
  kIntelStoreSink,
  kIntelSelect,
  kIntelValuePropagationThroughHeap,
  kIntelPureDirectCallDeleted,
  kIntelPureStaticCallDeleted,
  kIntelUselessNullCheckDeleted,
  kIntelLoopFullyUnrolled,
  kLastStat
};

class OptimizingCompilerStats {
 public:
  OptimizingCompilerStats() {}

  void RecordStat(MethodCompilationStat stat, size_t count = 1) {
    compile_stats_[stat] += count;
  }

  void Log() const {
    if (compile_stats_[kAttemptCompilation] == 0) {
      LOG(INFO) << "Did not compile any method.";
    } else {
      size_t unoptimized_percent =
          compile_stats_[kCompiledBaseline] * 100 / compile_stats_[kAttemptCompilation];
      size_t optimized_percent =
          compile_stats_[kCompiledOptimized] * 100 / compile_stats_[kAttemptCompilation];
      size_t quick_percent =
          compile_stats_[kCompiledQuick] * 100 / compile_stats_[kAttemptCompilation];
      std::ostringstream oss;
      oss << "Attempted compilation of " << compile_stats_[kAttemptCompilation] << " methods: ";

      oss << unoptimized_percent << "% (" << compile_stats_[kCompiledBaseline] << ") unoptimized, ";
      oss << optimized_percent << "% (" << compile_stats_[kCompiledOptimized] << ") optimized, ";
      oss << quick_percent << "% (" << compile_stats_[kCompiledQuick] << ") quick.";

      LOG(INFO) << oss.str();

      for (int i = 0; i < kLastStat; i++) {
        if (compile_stats_[i] != 0) {
          LOG(INFO) << PrintMethodCompilationStat(i) << ": " << compile_stats_[i];
        }
      }
    }
  }

  int32_t GetStat(MethodCompilationStat compilation_stat) const {
    DCHECK(compilation_stat < kLastStat);
    return compile_stats_[compilation_stat];
  }

 private:
  std::string PrintMethodCompilationStat(int stat) const {
    switch (stat) {
      case kAttemptCompilation : return "kAttemptCompilation";
      case kCompiledBaseline : return "kCompiledBaseline";
      case kCompiledOptimized : return "kCompiledOptimized";
      case kCompiledQuick : return "kCompiledQuick";
      case kInlinedInvoke : return "kInlinedInvoke";
      case kInstructionSimplifications: return "kInstructionSimplifications";
      case kNotCompiledBranchOutsideMethodCode: return "kNotCompiledBranchOutsideMethodCode";
      case kNotCompiledCannotBuildSSA : return "kNotCompiledCannotBuildSSA";
      case kNotCompiledCantAccesType : return "kNotCompiledCantAccesType";
      case kNotCompiledClassNotVerified : return "kNotCompiledClassNotVerified";
      case kNotCompiledHugeMethod : return "kNotCompiledHugeMethod";
      case kNotCompiledLargeMethodNoBranches : return "kNotCompiledLargeMethodNoBranches";
      case kNotCompiledMalformedOpcode : return "kNotCompiledMalformedOpcode";
      case kNotCompiledNoCodegen : return "kNotCompiledNoCodegen";
      case kNotCompiledNonSequentialRegPair : return "kNotCompiledNonSequentialRegPair";
      case kNotCompiledPathological : return "kNotCompiledPathological";
      case kNotCompiledSpaceFilter : return "kNotCompiledSpaceFilter";
      case kNotCompiledUnhandledInstruction : return "kNotCompiledUnhandledInstruction";
      case kNotCompiledUnresolvedField : return "kNotCompiledUnresolvedField";
      case kNotCompiledUnresolvedMethod : return "kNotCompiledUnresolvedMethod";
      case kNotCompiledUnsupportedIsa : return "kNotCompiledUnsupportedIsa";
      case kNotCompiledVerifyAtRuntime : return "kNotCompiledVerifyAtRuntime";
      case kNotOptimizedDisabled : return "kNotOptimizedDisabled";
      case kNotOptimizedRegisterAllocator : return "kNotOptimizedRegisterAllocator";
      case kNotOptimizedTryCatch : return "kNotOptimizedTryCatch";
      case kRemovedCheckedCast: return "kRemovedCheckedCast";
      case kRemovedDeadInstruction: return "kRemovedDeadInstruction";
      case kRemovedNullCheck: return "kRemovedNullCheck";
      case kConstantFolding: return "kConstantFolding";
      case kBooleanSimplifier: return "kBooleanSimplifier";
      case kReferenceTypePropagation: return "kReferenceTypePropagation";
      case kIntrinsicsRecognizer: return "kIntrinsicsRecognizer";
      case kLICM: return "kLICM";
      case kIntelLoopPeeled: return "kIntelLoopPeeled";
      case kIntelCCS: return "kIntelCCS";
      case kIntelBIVFound: return "kIntelBIVFound";
      case kIntelRemoveSuspendCheck: return "kIntelRemoveSuspendCheck";
      case kIntelRemoveUnusedLoops: return "kIntelRemoveUnusedLoops";
      case kIntelRemoveTrivialLoops: return "kIntelRemoveTrivialLoops";
      case kIntelNonTemporalMove: return "kIntelNonTemporalMove";
      case kIntelFormBottomLoop: return "kIntelFormBottomLoop";
      case kIntelLHSS: return "kIntelLHSS";
      case kIntelStoreSink: return "kIntelStoreSink";
      case kIntelSelect: return "kIntelSelect";
      case kIntelValuePropagationThroughHeap: return "kIntelValuePropagationThroughHeap";
      case kIntelPureDirectCallDeleted: return "kIntelPureDirectCallDeleted";
      case kIntelPureStaticCallDeleted: return "kIntelPureStaticCallDeleted";
      case kIntelUselessNullCheckDeleted: return "kIntelUselessNullCheckDeleted";
      case kIntelLoopFullyUnrolled: return "kIntelLoopFullyUnrolled";
      default: LOG(FATAL) << "invalid stat";
    }
    return "";
  }

  AtomicInteger compile_stats_[kLastStat];

  DISALLOW_COPY_AND_ASSIGN(OptimizingCompilerStats);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_OPTIMIZING_COMPILER_STATS_H_
