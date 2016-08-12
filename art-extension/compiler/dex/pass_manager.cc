/*
 * Copyright (C) 2015 The Android Open Source Project
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

#include <sstream>

#include "pass_manager.h"

#include "base/stl_util.h"
#include "pass_me.h"

namespace art {

#ifdef PERF_ANALYSIS_INFRASTRUCTURE
bool PassManagerOptions::SetInstrumentOptions(const std::string& options) {
  bool uses_pass_usage = false;
  instrument_options_ = 0u;
  const char* options_str = options.c_str();

  if ((strstr(options_str, "timings") != nullptr)) {
    instrument_options_ |= InstrumentOptions::kCompileTime;
  }

  if ((strstr(options_str, "memory-consumption") != nullptr)) {
    instrument_options_ |= InstrumentOptions::kMemoryConsumption;
  }

  if ((strstr(options_str, "pass-usage") != nullptr)) {
    instrument_options_ |= InstrumentOptions::kPassUsage;
    uses_pass_usage = true;
  }

  return uses_pass_usage;
}

void PassManagerOptions::SetInstrumentMethodFilter(const std::string& options) {
  // We want to store each method filter in our structure for later use.
  std::stringstream method_filters_stream(options.c_str());
  std::string filter;
  while (std::getline(method_filters_stream, filter, ',')) {
    instrument_method_filters_.insert(filter.c_str());
  }
}
#endif


PassManager::PassManager(const PassManagerOptions& options) : options_(options) {
}

PassManager::~PassManager() {
  STLDeleteElements(&passes_);
}

void PassManager::CreateDefaultPassList() {
  default_pass_list_.clear();
  // Add each pass which isn't disabled into default_pass_list_.
  for (const auto* pass : passes_) {
    if (options_.GetDisablePassList().find(pass->GetName()) != std::string::npos) {
      VLOG(compiler) << "Skipping disabled pass " << pass->GetName();
    } else {
      default_pass_list_.push_back(pass);
    }
  }
}

void PassManager::PrintPassNames() const {
  LOG(INFO) << "Loop Passes are:";
  for (const Pass* cur_pass : default_pass_list_) {
    LOG(INFO) << "\t-" << cur_pass->GetName();
  }
}

}  // namespace art
