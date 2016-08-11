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

#include "base/dumpable.h"
#include "base/timing_logger.h"
#include "code_generator.h"
#include "constant_calculation_sinking.h"
#include "constant_folding_x86.h"
#include "dex/pass_manager.h"
#include "driver/compiler_driver.h"
#include "form_bottom_loops.h"
#include "find_ivs.h"
#include "generate_selects.h"
#include "graph_visualizer.h"
#include "gvn_after_fbl.h"
#include "loadhoist_storesink.h"
#include "loop_formation.h"
#include "loop_full_unrolling.h"
#ifndef SOFIA
#include "non_temporal_move.h"
#endif
#include "optimization.h"
#include "optimizing_compiler_stats.h"
#include "option_content.h"
#include "pass_framework.h"
#include "pass_info_printer.h"
#include "peeling.h"
#include "perf_analysis.h"
#include "pure_invokes_analysis.h"
#include "remove_suspend.h"
#include "remove_unused_loops.h"
#include "scoped_thread_state_change.h"
#include "thread.h"
#include "trivial_loop_evaluator.h"
#include "value_propagation_through_heap.h"


namespace art {

// Enumeration defining possible commands to be applied to each pass.
enum PassInstrumentation {
  kPassInsertBefore,
  kPassInsertAfter,
  kPassReplace,
};

/**
 * @brief Used to provide directive for custom pass placement.
 */
struct HCustomPassPlacement {
  const char* pass_to_place;      /**!< The pass that has a custom location. */
  const char* pass_relative_to;   /**!< The pass for which the new pass is relative to. */
  PassInstrumentation directive;  /**!< How to do the pass insert (before, after, etc). */
};

/**
 * @brief Static array holding information about custom placements.
 */
static HCustomPassPlacement kPassCustomPlacement[] = {
  { "loop_formation", "instruction_simplifier_after_types", kPassInsertAfter },
  { "find_ivs", "loop_formation", kPassInsertAfter },
  { "remove_loop_suspend_checks", "find_ivs", kPassInsertAfter },
  { "loadhoist_storesink", "remove_loop_suspend_checks", kPassInsertAfter },
  { "remove_unused_loops", "loadhoist_storesink", kPassInsertAfter },
  { "loop_peeling", "inliner", kPassInsertAfter },
  { "loop_formation_before_peeling", "loop_peeling", kPassInsertBefore },
  { "constant_calculation_sinking", "find_ivs", kPassInsertAfter },
  { "form_bottom_loops", "BCE", kPassInsertAfter },
  { "generate_selects", "boolean_simplifier", kPassInsertAfter },
  { "GVN_after_form_bottom_loops", "form_bottom_loops", kPassInsertAfter },
  { "value_propagation_through_heap", "GVN_after_form_bottom_loops", kPassInsertAfter },
  { "constant_folding_after_vph", "value_propagation_through_heap", kPassInsertAfter },
  { "loop_formation_before_bottom_loops", "form_bottom_loops", kPassInsertBefore },
  { "pure_invokes_analysis", "remove_loop_suspend_checks", kPassInsertAfter },
  { "non_temporal_move", "trivial_loop_evaluator", kPassInsertAfter},
  { "trivial_loop_evaluator", "find_ivs", kPassInsertAfter},
  { "loop_full_unrolling", "constant_calculation_sinking", kPassInsertAfter},
};

/**
 * @brief Static array holding names of passes that need removed.
 * @details This is done in cases where common code pass ordering and
 * existing passes are not appropriate or compatible with extension.
 */
static const char* kPassRemoval[] = {
  nullptr,
};

static void AddX86Optimization(HOptimization* optimization,
                               ArenaVector<HOptimization*>& list,
                               ArenaSafeMap<const char*, HCustomPassPlacement*> &placements) {
  ArenaSafeMap<const char*, HCustomPassPlacement*>::iterator iter = placements.find(optimization->GetPassName());

  if (iter == placements.end()) {
    return;
  }

  HCustomPassPlacement* placement = iter->second;

  // Find the right pass to change now.
  size_t len = list.size();
  size_t idx;
  for (idx = 0; idx < len; idx++) {
    if (strcmp(list[idx]->GetPassName(), placement->pass_relative_to) == 0) {
      switch (placement->directive) {
        case kPassReplace:
          list[idx] = optimization;
          break;
        case kPassInsertBefore:
        case kPassInsertAfter:
          // Add an empty element.
          list.push_back(nullptr);

          // Find the start, is it idx or idx + 1?
          size_t start = idx;

          if (placement->directive == kPassInsertAfter) {
            start++;
          }

          // Push elements backwards.
          DCHECK_NE(len, list.size());
          for (size_t idx2 = len; idx2 >= start; idx2--) {
            list[idx2] = list[idx2 - 1];
          }

          // Place the new element.
          list[start] = optimization;
          break;
      }
      // Done here.
      break;
    }
  }
  // It must be the case that the custom placement was found.
  DCHECK_NE(len, idx);
}

static void FillCustomPlacement(ArenaSafeMap<const char*, HCustomPassPlacement*>& placements) {
  size_t len = arraysize(kPassCustomPlacement);

  for (size_t i = 0; i < len; i++) {
    placements.Overwrite(kPassCustomPlacement[i].pass_to_place, kPassCustomPlacement + i);
  }
}

static void FillOptimizationList(HGraph* graph,
                                 ArenaVector<HOptimization*>& list,
                                 HOptimization* optimizations[],
                                 size_t opts_length,
                                 HOptimization_X86* optimizations_x86[],
                                 size_t opts_x86_length) {
  // Start by copying the passes from Google.
  for (size_t i = 0; i < opts_length; i++) {
    list.push_back(optimizations[i]);
  }

  // Get the custom placements for our passes.
  ArenaSafeMap<const char*, HCustomPassPlacement*> custom_placement(
      std::less<const char*>(),
      graph->GetArena()->Adapter(kArenaAllocMisc));
  FillCustomPlacement(custom_placement);

  for (size_t i = 0; i < opts_x86_length; i++) {
    HOptimization_X86* opt = optimizations_x86[i];
    if (opt != nullptr) {
      AddX86Optimization(opt, list, custom_placement);
    }
  }
}

/**
 * @brief Remove the passes in the optimization list.
 * @param opts the optimization vector.
 * @param driver the compilation driver.
 */
static void RemoveOptimizations(ArenaVector<HOptimization*>& opts,
                                CompilerDriver* driver) {
  std::unordered_set<std::string> disabled_passes;

  SplitStringIntoSet(driver->GetCompilerOptions().
                       GetPassManagerOptions()->GetDisablePassList(),
                     ',',
                     disabled_passes);

  // Add elements from kPassRemoval.
  for (size_t i = 0, len = arraysize(kPassRemoval); i < len; i++) {
    if (kPassRemoval[i] != nullptr) {
      disabled_passes.insert(std::string(kPassRemoval[i]));
    }
  }

  // If there are no disabled passes, bail.
  if (disabled_passes.empty()) {
    return;
  }

  size_t opts_len = opts.size();

  // We replace the opts with nullptr if we find a match.
  //   This is cheaper than rearranging the vectors.
  for (size_t opts_idx = 0; opts_idx < opts_len; opts_idx++) {
    HOptimization* opt = opts[opts_idx];
    if (disabled_passes.find(opt->GetPassName()) != disabled_passes.end()) {
      opts[opts_idx] = nullptr;
    }
  }
}

void PrintPasses(ArenaVector<HOptimization*>& opts) {
  size_t opts_len = opts.size();

  // We replace the opts with nullptr if we find a match.
  //   This is cheaper than rearranging the vectors.
  LOG(INFO) << "Pass List:";
  if (opts_len == 0) {
    LOG(INFO) << "\t<Empty>";
  }

  for (size_t opts_idx = 0; opts_idx < opts_len; opts_idx++) {
    HOptimization* opt = opts[opts_idx];
    if (opt != nullptr) {
      LOG(INFO) << "\t- " << opt->GetPassName();
    }
  }
}

void FillUserPassOptions(ArenaVector<HOptimization*>& opts,
                         CompilerDriver* driver) {
  const PassManagerOptions* pass_options = driver->GetCompilerOptions().GetPassManagerOptions();
  const std::string& options = pass_options->GetOverriddenPassOptions();
  const size_t options_len = options.size();

  // Before anything, check if we care about anything right now.
  if (options_len == 0) {
    return;
  }

  for (auto optimization : opts) {
    if (optimization == nullptr) {
      continue;
    }

    const char* pass_name = optimization->GetPassName();
    const size_t pass_name_len = strlen(pass_name);
    const size_t min_option_size = 4;  // 2 delimiters, 1 option name, 1 option.
    size_t search_pos = 0;

    // If there is no room for pass options, exit early.
    if (options_len < pass_name_len + min_option_size) {
      continue;
    }

    do {
      search_pos = options.find(pass_name, search_pos);

      // Check if we found this pass name in rest of string.
      if (search_pos == std::string::npos) {
        // No more options for this pass.
        break;
      }

      // The string contains the pass name. Now check that there is
      // room for the options: at least one char for option name,
      // two chars for two delimiter, and at least one char for option.
      if (search_pos + pass_name_len + min_option_size >= options_len) {
        // No more options for this pass.
        break;
      }

      // Update the current search position to not include the pass name.
      search_pos += pass_name_len;

      // The format must be "PassName:SettingName:#" where # is the option.
      // Thus look for the first ":" which must exist.
      if (options[search_pos] != ':') {
        // Missing delimiter right after pass name.
        continue;
      } else {
        search_pos += 1;
      }

      // Now look for the actual option by finding the next ":" delimiter.
      const size_t option_name_pos = search_pos;
      size_t option_pos = options.find(':', option_name_pos);

      if (option_pos == std::string::npos) {
        // Missing a delimiter that would capture where option starts.
        continue;
      } else if (option_pos == option_name_pos) {
        // Missing option thus did not move from option name.
        continue;
      } else {
        // Skip the delimiter.
        option_pos += 1;
      }

      // Look for the terminating delimiter which must be a comma.
      size_t next_configuration_separator = options.find(',', option_pos);
      if (next_configuration_separator == std::string::npos) {
        next_configuration_separator = options_len;
      }

      // Prevent end of string errors.
      if (next_configuration_separator == option_pos) {
          continue;
      }

      // Get the actual option itself.
      std::string option_string =
          options.substr(option_pos, next_configuration_separator - option_pos);

      std::string option_name =
          options.substr(option_name_pos, option_pos - option_name_pos - 1);

      // We attempt to convert the option value to integer. Strtoll is being used to
      // convert because it is exception safe.
      char* end_ptr = nullptr;
      const char* option_ptr = option_string.c_str();
      DCHECK(option_ptr != nullptr);  // Paranoid: option_ptr must be a valid pointer.
      int64_t int_value = strtoll(option_ptr, &end_ptr, 0);
      DCHECK(end_ptr != nullptr);  // Paranoid: end_ptr must be set by the strtoll call.

      // If strtoll call succeeded, the option is now considered as integer.
      if (*option_ptr != '\0' && end_ptr != option_ptr && *end_ptr == '\0') {
        optimization->DefineOption(option_name, OptionContent(int_value));
      } else {
        // Otherwise, it is considered as a string.
        optimization->DefineOption(option_name, OptionContent(option_string.c_str()));
      }
      search_pos = next_configuration_separator;
    } while (true);
  }
}

bool PrintPassesOnlyOnce(ArenaVector<HOptimization*>& opts,
                         CompilerDriver* driver) {
  bool need_print = driver->GetCompilerOptions().
                            GetPassManagerOptions()->GetPrintPassNames();

  if (!need_print) {
    return false;
  }

  // Flags that we have already printed the pass name list.
  static volatile bool pass_names_already_printed_ = false;

  // Have we already printed the names?
  if (!pass_names_already_printed_) {
    // Double-check it under the lock.
    ScopedObjectAccess soa(Thread::Current());
    if (!pass_names_already_printed_) {
      pass_names_already_printed_ = true;
    } else {
      need_print = false;
    }
  } else {
    need_print = false;
  }

  if (!need_print) {
    return false;
  }

  PrintPasses(opts);
  return true;
}

/**
 * @brief Sets verbosity for passes.
 * @param optimizations the optimization array.
 * @param opts_len the length of optimizations array.
 * @param driver the compilation driver.
 */
void FillVerbose(HOptimization_X86* optimizations[],
                 size_t opts_len,
                 CompilerDriver* driver) {
  std::unordered_set<std::string> print_passes;
  const bool print_all_passes = driver->GetCompilerOptions().
                                GetPassManagerOptions()->GetPrintAllPasses();
  if (!print_all_passes) {
    // If we don't print all passes, we need to check the list.
    SplitStringIntoSet(driver->GetCompilerOptions().
                         GetPassManagerOptions()->GetPrintPassList(),
                       ',',
                       print_passes);

    // Are there any passes to print?
    if (print_passes.empty()) {
      return;
    }
  }

  for (size_t opts_idx = 0; opts_idx < opts_len; opts_idx++) {
    HOptimization* opt = optimizations[opts_idx];
    if (opt != nullptr) {
      if (print_all_passes ||
          print_passes.find(opt->GetPassName()) != print_passes.end()) {
        optimizations[opts_idx]->SetVerbose(true);
      }
    }
  }
}

void RunOptimizationsX86(HGraph* graph,
                         CompilerDriver* driver,
                         OptimizingCompilerStats* stats,
                         HOptimization* optimizations[],
                         size_t length,
                         PassInfoPrinter* pass_info_printer,
                         const DexCompilationUnit& c_unit) {
  // We want our own list of passes with our own vector.
  ArenaAllocator* arena = graph->GetArena();
  ArenaVector<HOptimization*> opt_list(arena->Adapter(kArenaAllocMisc));
#ifdef PERF_ANALYSIS_INFRASTRUCTURE
  const PassManagerOptions* pass_options = driver->GetCompilerOptions().GetPassManagerOptions();
  const std::string& method_name = PrettyMethod(c_unit.GetDexMethodIndex(),
                                                *c_unit.GetDexFile());
  PerfAnalysisInfrastructure* perf_analysis = nullptr;
  if (pass_options->ShouldInstrument(method_name.c_str())) {
    perf_analysis = new (arena) PerfAnalysisInfrastructure(arena, pass_options);
  }
#endif

  // Create the array for the opts.
  HLoopFormation* loop_formation = new (arena) HLoopFormation(graph);
  HFindInductionVariables* find_ivs = new (arena) HFindInductionVariables(graph, &c_unit, stats);
  HRemoveLoopSuspendChecks* remove_suspends = new (arena) HRemoveLoopSuspendChecks(graph, c_unit, stats);
  HConstantCalculationSinking* ccs = new (arena) HConstantCalculationSinking(graph, stats);
  HRemoveUnusedLoops* remove_unused_loops = new (arena) HRemoveUnusedLoops(graph, stats);
  TrivialLoopEvaluator* tle = new (arena) TrivialLoopEvaluator(graph, c_unit, stats);
#ifndef SOFIA
  HNonTemporalMove* non_temporal_move = new (arena) HNonTemporalMove(graph, c_unit, stats);
#endif
  LoadHoistStoreSink* lhss = new (arena) LoadHoistStoreSink(graph, c_unit, stats);
  HValuePropagationThroughHeap* value_propagation_through_heap = new (arena) HValuePropagationThroughHeap(graph, stats);
  HLoopFormation* formation_before_peeling = new (arena) HLoopFormation(graph, "loop_formation_before_peeling");
  HLoopPeeling* peeling = new (arena) HLoopPeeling(graph, stats);
  HPureInvokesAnalysis* pure_invokes_analysis = new (arena) HPureInvokesAnalysis(graph, stats);
  HLoopFormation* formation_before_bottom_loops = new (arena) HLoopFormation(graph, "loop_formation_before_bottom_loops");
  HFormBottomLoops* form_bottom_loops = new (arena) HFormBottomLoops(graph, c_unit, stats);
  GVNAfterFormBottomLoops* gvn_after_fbl = new (arena) GVNAfterFormBottomLoops(graph);
  HGenerateSelects* generate_selects = new (arena) HGenerateSelects(graph, c_unit, stats);
  HConstantFolding_X86* constant_folding = new (arena) HConstantFolding_X86(graph, stats, "constant_folding_after_vph");
  HLoopFullUnrolling* loop_full_unrolling = new (arena) HLoopFullUnrolling(graph, stats);

  HOptimization_X86* opt_array[] = {
    loop_formation,
    find_ivs,
    remove_suspends,
    ccs,
    lhss,
    remove_unused_loops,
    peeling,
    pure_invokes_analysis,
    formation_before_peeling,
    tle,
#ifndef SOFIA
    // Non-temporal move can cause the software modem to crash on SoFIA implementations.
    non_temporal_move,
#endif
    form_bottom_loops,
    gvn_after_fbl,
    value_propagation_through_heap,
    constant_folding,
    formation_before_bottom_loops,
    generate_selects,
    loop_full_unrolling
  };

  // Fill verbose flags where we need it.
  FillVerbose(opt_array, arraysize(opt_array),
              driver);

  // Create the vector for the optimizations.
  FillOptimizationList(graph, opt_list, optimizations, length,
                                  opt_array, arraysize(opt_array));

  // Finish by removing the ones we do not want.
  RemoveOptimizations(opt_list, driver);

  // Print the pass list, if needed.
  PrintPassesOnlyOnce(opt_list, driver);

  FillUserPassOptions(opt_list, driver);

  {
#ifdef PERF_ANALYSIS_INFRASTRUCTURE
    PerfAnalysisInfrastructure::ScopedAnalysis sa(method_name.c_str(), true, perf_analysis);
#endif

    // Now execute the optimizations.
    uint32_t phase_id = 0;
    for (auto optimization : opt_list) {
      if (optimization != nullptr) {
        const char* name = optimization->GetPassName();
        // if debug option --stop-optimizing-after is passed
        // then check whether we need to stop optimization.
        if (driver->GetCompilerOptions().IsConditionalCompilation()) {
          if (driver->GetCompilerOptions().GetStopOptimizingAfter() < phase_id ||
              driver->GetCompilerOptions().GetStopOptimizingAfter() ==
              std::numeric_limits<uint32_t>::max()) {
            break;
          }
          VLOG(compiler) << "Applying " << name << ", phase_id = " << phase_id;
        }

        {
          PassInfo pass_info(name, pass_info_printer);
#ifdef PERF_ANALYSIS_INFRASTRUCTURE
          PerfAnalysisInfrastructure::ScopedCollection sc(method_name.c_str(),
                                                          name,
                                                          perf_analysis,
                                                          optimization);
#endif

          // Effective optimization launch.
          optimization->Run();
        }

        {
#ifdef PERF_ANALYSIS_INFRASTRUCTURE
          char check_str[256];
          snprintf(check_str, sizeof(check_str), "Optimization-Check #%u", phase_id);
          PerfAnalysisInfrastructure::ScopedCollection sc(method_name.c_str(),
                                                       check_str,
                                                       perf_analysis);
#endif
          optimization->Check();
        }
      }
      phase_id++;
    }
  }
}

}  // namespace art
