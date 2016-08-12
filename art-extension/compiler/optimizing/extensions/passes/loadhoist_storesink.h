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

#ifndef ART_COMPILER_OPTIMIZING_LOADHOIST_STORESINK_H_
#define ART_COMPILER_OPTIMIZING_LOADHOIST_STORESINK_H_

#include "cloning.h"
#include "ext_alias.h"
#include "graph_x86.h"
#include "loop_information.h"
#include "nodes.h"
#include "optimization_x86.h"

#include <map>
#include <set>

namespace art {

typedef std::map<HInstruction*, HInstruction*> GetSetToHoistSink;
typedef std::set<HInstruction*> SetsToSink;

class LoadHoistStoreSink : public HOptimization_X86 {
 public:
  explicit LoadHoistStoreSink(HGraph* graph, const DexCompilationUnit& c_unit,
                              OptimizingCompilerStats* stats = nullptr)
      : HOptimization_X86(graph, true, kLoadHoistStoreSinkPassName, stats), c_unit_(c_unit) {}

  void Run() OVERRIDE;

 private:
  static constexpr const char* kLoadHoistStoreSinkPassName = "loadhoist_storesink";

  /*
   * @brief Find load/store matches and check for dangerous instructions.
   * @param loop Loop information for current loop.
   * @param get_to_set matching get/sets are added to this parameter.
   * @param sets_to_sink unmatched candidate sets are added to this parameter.
   * @return 'true' if no dangerous instructions were found in the loop.
   */
  bool FindLoadStoreCouples(HLoopInformation_X86* loop,
                            GetSetToHoistSink& get_to_set,
                            SetsToSink& sets_to_sink);

  bool LoopGate(HLoopInformation_X86* loop) const;

  /*
   * @brief Find load/store matches and check for dangerous instructions.
   * @param loop Loop information for current loop.
   * @param get_to_set matching get/sets to be hoisted/sunk.
   * @param sets_to_sink unmatched sets to be sunk.
   * @return 'true' if at least one instruction was added to the suspend block.
   */
  bool DoLoadHoistStoreSink(HLoopInformation_X86* loop,
                            GetSetToHoistSink& get_to_set,
                            SetsToSink& sets_to_sink);


  AliasCheck alias_;
  const DexCompilationUnit& c_unit_;

  DISALLOW_COPY_AND_ASSIGN(LoadHoistStoreSink);
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_LOADHOIST_STORESINK_H_
