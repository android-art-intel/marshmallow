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

#ifdef PERF_ANALYSIS_INFRASTRUCTURE

#ifndef ART_OPT_INFRASTRUCTURE_INSTRUMENTATION_METRICS_H_
#define ART_OPT_INFRASTRUCTURE_INSTRUMENTATION_METRICS_H_

#include "nodes.h"
#include "optimization.h"
#include "base/time_utils.h"

#include <fstream>
#include <time.h>
#include <vector>
#include <malloc.h>

namespace art {

/**
 * @brief Data structures holding the metric data.
 */
template <typename T>
struct PassData {
  T start_, result_;
  PassData() : start_(0), result_(0) {}
};

/**
 * @brief Base class representing a metric of the Perf. Analysis Infrastructure.
 */
template <typename T>
class Metric {
  public:
    explicit Metric(const char* name) : name_(name) {}
    virtual ~Metric() {}

    /**
     * @brief Starts the collection of data.
     * @param values The data structure holding the results of the metric.
     * @param instru_name The name of the collection.
     * @param data The optional data pointer that a metric may use.
     * @return Returns true if the Start process was successful, or false otherwise.
     */
    virtual bool Start(PassData<T>& values, const char* instru_name, void* data) = 0;

    /**
     * @brief Stops the collection of data.
     * @param values The data structure holding the results of the metric.
     * @param instru_name The name of the collection.
     * @param data The optional data pointer that a metric may use.
     * @return Returns true if the Stop process was successful, or false otherwise.
     */
    virtual bool Stop(PassData<T>& values, const char* instru_name, void* data) = 0;

    /**
     * @brief Prints the collection of data. This method should be called only once the collection
     * is completed (that is, when Start *and* stop method have been called).
     * @param out The output stream to print the collection data to.
     * @param values The data structure holding the results of the metric.
     * @return Returns true if the data was successfully appended to out, or false otherwise.
     */
    virtual bool Print(std::ostream& out, const PassData<T>& values) const = 0;

    /**
     * @return The name of the metric, as specified when constructed.
     */
    virtual const char* GetName() const {
      return name_;
    }

  protected:
    const char* name_;
};

/**
 * @brief: Timing metrics: get nanosecond timestamp.
 */
class Timing : public Metric<uint64_t> {
  public:
    explicit Timing() : Metric("Time (ns)") {}

    bool Start(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      values.start_ = NanoTime();
      return true;
    }

    bool Stop(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      values.result_ = NanoTime() - values.start_;
      return true;
    }

    bool Print(std::ostream& oss, const PassData<uint64_t>& data) const OVERRIDE {
      oss << data.result_;
      return true;
    }
};

/**
 * @brief Metric holding malloc memory leaks. It basically checks the amount
 * of allocated bytes at different points in time and compute the difference.
 */
class MallocStats : public Metric<uint64_t> {
  public:
    explicit MallocStats() : Metric("STL Memory leaks (bytes)") {}

    bool Start(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      struct mallinfo malloc_stats = mallinfo();
      values.start_ = malloc_stats.hblkhd;
      return true;
    }

    bool Stop(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      struct mallinfo malloc_stats = mallinfo();
      uint64_t end = malloc_stats.hblkhd;
      values.result_ = end - values.start_;
      return true;
    }

    bool Print(std::ostream& oss, const PassData<uint64_t>& data) const OVERRIDE {
      oss << data.result_;
      return true;
    }
};

/**
 * @brief Uses ArenaAllocator's instrumentation to get the number of bytes
 * allocated between two points in the code.
 */
class ArenaBytesAllocated : public Metric<uint64_t> {
  public:
    explicit ArenaBytesAllocated() :
      Metric("Arena allocations (bytes)"),
      arena_(nullptr) {}

    void SetArena(ArenaAllocator* arena) {
      arena_ = arena;
    }

    bool Start(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      if (arena_ == nullptr) {
        return false;
      }

      const PerfInstrument& perf_instru = arena_->GetPerfInstrumentation();
      values.start_ = perf_instru.bytes_allocated_;
      return true;
    }

    bool Stop(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      if (arena_ == nullptr) {
        return false;
      }

      const PerfInstrument& perf_instru = arena_->GetPerfInstrumentation();
      values.result_ = perf_instru.bytes_allocated_ - values.start_;
      return true;
    }

    bool Print(std::ostream& oss, const PassData<uint64_t>& data) const OVERRIDE {
      oss << data.result_;
      return true;
    }

  private:
    ArenaAllocator* arena_;
};

/**
 * @brief Uses ArenaAllocator's instrumentation to get the number of calls to
 * the arena allocator.
 */
class ArenaNumAllocations : public Metric<uint64_t> {
  public:
    explicit ArenaNumAllocations() :
      Metric("Arena allocations (# allocations)"),
      arena_(nullptr) {}

    void SetArena(ArenaAllocator* arena) {
      arena_ = arena;
    }

    bool Start(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      if (arena_ == nullptr) {
        return false;
      }

      const PerfInstrument& perf_instru = arena_->GetPerfInstrumentation();
      values.start_ = perf_instru.num_allocations_;
      return true;
    }

    bool Stop(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      if (arena_ == nullptr) {
        return false;
      }

      const PerfInstrument& perf_instru = arena_->GetPerfInstrumentation();
      values.result_ = perf_instru.num_allocations_ - values.start_;
      return true;
    }

    bool Print(std::ostream& oss, const PassData<uint64_t>& data) const OVERRIDE {
      oss << data.result_;
      return true;
    }

  private:
    ArenaAllocator* arena_;
};

/**
 * @brief Computes the difference of values of the timestamp counter (read with
 * the rdtsc instruction) between two points in the code.
 */
class TSClock : public Metric<uint64_t> {
  public:
    explicit TSClock() : Metric("TSCounter (cycles)") {}

    bool Start(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      values.start_ = TSClock::rdtsc();
      return true;
    }

    bool Stop(PassData<uint64_t>& values, const char* instru_name, void* data) OVERRIDE {
      UNUSED(instru_name);
      UNUSED(data);
      values.result_ = TSClock::rdtsc() - values.start_;
      return true;
    }

    bool Print(std::ostream& oss, const PassData<uint64_t>& data) const OVERRIDE {
      oss << data.result_;
      return true;
    }

  private:
  /**
   * @brief Defines the rdtsc function. This method reads the timestamp counter from the hardware.
   * If not available, clock_gettime provides a similar counter.
   * @return The current timestamp counter value.
   */
  static uint64_t rdtsc() {
  #if defined(__i386__) || defined(__x86_64__)
    uint32_t hi, lo;
    __asm__ __volatile__("rdtsc" : "=a"(lo), "=d"(hi));
    return static_cast<uint64_t>((static_cast<uint64_t>(lo) | (static_cast<uint64_t>(hi) << 32)));
  #else
    return ThreadCpuNanoTime();
  #endif
  }
};

/**
 * @brief Gathers the number of successful applications of the provided pass.
 */
class PassStats : public Metric<uint64_t> {
  public:
    explicit PassStats() : Metric("Pass stats (# successful executions)") {}

    bool Start(PassData<uint64_t>& values, const char* pass_name, void* data) OVERRIDE {
      HOptimization* optim = static_cast<HOptimization*>(data);
      if (data == nullptr) {
        return false;
      }

      values.start_ = GetStat(pass_name, optim);
      return true;
    }

    bool Stop(PassData<uint64_t>& values, const char* pass_name, void* data) OVERRIDE {
      HOptimization* optim = static_cast<HOptimization*>(data);
      if (data == nullptr) {
        return false;
      }

      int32_t value = GetStat(pass_name, optim);

      if (value == -1) {
        values.result_ = static_cast<uint64_t>(-1);
      } else {
        values.result_ = value - values.start_;
      }

      return true;
    }

    bool Print(std::ostream& oss, const PassData<uint64_t>& data) const OVERRIDE {
      if (data.result_ == static_cast<uint64_t>(-1)) {
        oss << "Not supported";
      } else {
        oss << data.result_;
      }

      return true;
    }

 private:
  int32_t GetStat(const char* pass_name, HOptimization* optim) const {
    DCHECK(pass_name != nullptr);
    int32_t value;

    if (strcmp("constant_calculation_sinking", pass_name) == 0) {
      value = optim->GetStat(kIntelCCS);
    } else if (strcmp("find_ivs", pass_name) == 0) {
      value = optim->GetStat(kIntelBIVFound);
    } else if (strcmp("remove_loop_suspend_checks", pass_name) == 0) {
      value = optim->GetStat(kIntelRemoveSuspendCheck);
    } else if (strcmp("trivial_loop_evaluator", pass_name) == 0) {
      value = optim->GetStat(kIntelRemoveTrivialLoops);
    } else if (strcmp("non_temporal_move", pass_name) == 0) {
      value = optim->GetStat(kIntelNonTemporalMove);
    } else if (strcmp("form_bottom_loops", pass_name) == 0) {
      value = optim->GetStat(kIntelFormBottomLoop);
    } else if (strcmp("loadhoist_storesink", pass_name) == 0) {
      value = optim->GetStat(kIntelLHSS) + optim->GetStat(kIntelStoreSink);
    } else if (strcmp("loop_peeling", pass_name) == 0) {
      value = optim->GetStat(kIntelLoopPeeled);
    } else if (strcmp("inliner", pass_name) == 0) {
      value = optim->GetStat(kInlinedInvoke);
    } else if (strcmp("instruction_simplifier", pass_name) == 0) {
      value = optim->GetStat(kInstructionSimplifications);
    } else {
      value = -1;
    }

    return value;
  }
};

}  // namespace art.

#endif  // ART_OPT_INFRASTRUCTURE_INSTRUMENTATION_METRICS_H_

#endif  // PERF_ANALYSIS_INFRASTRUCTURE

