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

#include "base/macros.h"
#include "base/logging.h"
#include "compiler_driver.h"
#include "system_load_metric.h"

namespace art {

const char* InstantLoadMetric::kLoadavgFilename = "/proc/loadavg";

ThreadNumberAction InstantLoadMetric::GetAction(size_t working_tasks_count ATTRIBUTE_UNUSED,
                                                size_t active_working_tasks_count) {
  int32_t threads_in_queue = GetQueueLength();
  double other_threads = threads_in_queue - static_cast<int32_t>(active_working_tasks_count);
  double active_tasks_target = work_units_ - other_threads;
  active_tasks_virtual_ += (active_tasks_target - active_tasks_virtual_) *
      kReactionFactor * GetUpdateIntervalMs() / 1000;
  active_tasks_virtual_ = std::max(active_tasks_virtual_, 1.0);
  active_tasks_virtual_ = std::min(active_tasks_virtual_, static_cast<double>(work_units_));
  size_t active_tasks_virtual_rounded = static_cast<size_t>(round(active_tasks_virtual_));
  if (active_tasks_virtual_rounded == active_working_tasks_count) {
    return kDoNotChangeThreadNumber;
  } else if (active_tasks_virtual_rounded > active_working_tasks_count) {
    return kIncreaseThreadNumber;
  } else {
    return kDecreaseThreadNumber;
  }
}

bool InstantLoadMetric::Initialize() {
  if (!loadavg_file_.is_open()) {
    loadavg_file_.open(kLoadavgFilename, std::ios::in);
  }
  if (!loadavg_file_.is_open()) {
    return false;
  }
  active_tasks_virtual_ = active_tasks_virtual_init_;
  return true;
}

int32_t InstantLoadMetric::GetQueueLength() {
  DCHECK(loadavg_file_.is_open());
  // Shift to file beginning.
  loadavg_file_.seekg(0);
  // Skip values for average system load for last 1, 5, and 15 minutes.
  std::string skip_string;
  for (int i = 0; i < 3; i++) {
    loadavg_file_ >> skip_string;
  }
  // Get instant system load.
  size_t result = 0;
  loadavg_file_ >> result;
  DCHECK_GT(result, 0u);
  // We should subtract 1 from queue length,
  // because the thread that access to this value is always counted in it.
  return result - 1;
}

}  // namespace art.
