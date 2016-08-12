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

#ifndef ART_COMPILER_DRIVER_SYSTEM_LOAD_METRIC_H_
#define ART_COMPILER_DRIVER_SYSTEM_LOAD_METRIC_H_

#include <cmath>
#include <fstream>

namespace art {

class ParallelCompilationManager;

enum ThreadNumberAction {
  kDecreaseThreadNumber,
  kDoNotChangeThreadNumber,
  kIncreaseThreadNumber
};

class SystemLoadMetric {
 public:
  explicit SystemLoadMetric(uint64_t update_interval_ms)
      : update_interval_ms_(update_interval_ms) {}
  virtual ~SystemLoadMetric() {}
  virtual ThreadNumberAction GetAction(size_t working_tasks_count,
                                       size_t active_working_tasks_count) = 0;

  // Returns false if the metric is not ready for work (can't open file etc).
  virtual bool Initialize() {
    return true;
  };

  uint64_t GetUpdateIntervalMs() {
    return update_interval_ms_;
  }

 private:
  const uint64_t update_interval_ms_;
};

// Metric which uses instant value of the processor queue length.
class InstantLoadMetric : public SystemLoadMetric {
 public:
  InstantLoadMetric(size_t work_units,
                    uint64_t update_interval_ms = 100u)
      : InstantLoadMetric(work_units, work_units, update_interval_ms) {}


  InstantLoadMetric(size_t work_units,
                    double initial_task_count,
                    uint64_t update_interval_ms = 100u)
      : SystemLoadMetric(update_interval_ms),
        work_units_(static_cast<int32_t>(work_units)),
        active_tasks_virtual_(initial_task_count),
        active_tasks_virtual_init_(active_tasks_virtual_) {}

  ~InstantLoadMetric() {
    if (loadavg_file_.is_open()) {
      loadavg_file_.close();
    }
  }

  virtual ThreadNumberAction GetAction(size_t working_tasks_count,
                                       size_t active_working_tasks_count);
  virtual bool Initialize();

 private:
  int32_t GetQueueLength();

  std::fstream loadavg_file_;
  int32_t work_units_;
  double active_tasks_virtual_;

  const double active_tasks_virtual_init_;

  static constexpr double kReactionFactor = 0.5;
  static const char* kLoadavgFilename;
};

}  // namespace art.

#endif
