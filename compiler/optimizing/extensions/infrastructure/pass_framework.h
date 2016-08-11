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

#ifndef ART_OPT_INFRASTRUCTURE_PASS_FRAMEWORK_H_
#define ART_OPT_INFRASTRUCTURE_PASS_FRAMEWORK_H_

#include "driver/dex_compilation_unit.h"
#include "nodes.h"

namespace art {

// Forward declarations.
class HOptimization;
class PassInfoPrinter;

/**
 * @brief Print the passes for debugging information.
 * @param opts the optimization vector.
 * @param post_opts the post-optimization vectors.
 */
void PrintPasses(ArenaVector<HOptimization*>& opts,
                 ArenaVector<HOptimization*>& post_opts);

/**
 * @brief Print the passes for debugging information, if their
 *        printing is enabled by --print-pass-names option and
 *        they have not been printed by this method before.
 * @param opts the optimization vector.
 * @param post_opts the post-optimization vectors.
 * @param driver the compilation driver.
 * @return true, if the passes have been printed by this method call.
 *         false, if the pass printing is not enabled by the option
 *                or they have already been printed before.
 */
bool PrintPassesOnlyOnce(ArenaVector<HOptimization*>& opts,
                         ArenaVector<HOptimization*>& post_opts,
                         CompilerDriver* driver);

void RunOptimizationsX86(HGraph* graph,
                         CompilerDriver* driver,
                         OptimizingCompilerStats* stats,
                         HOptimization* optimizations[],
                         size_t length,
                         PassInfoPrinter* pass_info_printer,
                         const DexCompilationUnit& c_unit);

}  // namespace art

#endif  // ART_OPT_INFRASTRUCTURE_GRAPH_X86_H_
