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
#include "graph_x86.h"
#include "gvn_after_fbl.h"
#include "gvn.h"
#include "loop_iterators.h"
#include "side_effects_analysis.h"

namespace art {

void GVNAfterFormBottomLoops::Run() {
  HGraph_X86* graph = GRAPH_TO_GRAPH_X86(graph_);
  HLoopInformation_X86* loop = graph->GetLoopInformation();

  // Go through each loop and check whether there is at least one bottom tested
  // loop. If so, launch the GVN pass.
  bool do_gvn = false;
  for (HOutToInLoopIterator iter(loop); !iter.Done(); iter.Advance()) {
    HLoopInformation_X86* l = iter.Current();

    DCHECK(l != nullptr);  // Paranoid.
    if (l->IsBottomTested()) {
      do_gvn = true;
      break;
    }
  }

  // If there are no bottom tested loops, give up now.
  if (!do_gvn) {
    return;
  }

  // We have seen a bottom tested loop.  Rerun GVN.
  SideEffectsAnalysis side_effects(graph_);
  side_effects.Run();
  GVNOptimization gvn(graph_, side_effects);
  gvn.Run();
}

}  // namespace art
