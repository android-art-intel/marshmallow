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
 */

#ifndef ART_RUNTIME_GC_GCPROFILER_H_
#define ART_RUNTIME_GC_GCPROFILER_H_

#include "gc/heap.h"

namespace art {

namespace gc {

/* Allocation Fail until the following phases.
* E.g. kFailUntilGCConcurrent means allocation succeed after Concurrent GC.
*/
enum AllocFailPhase {
  kFailUntilGCConcurrent,                     // Background GC.
  kFailUntilGCForAlloc,                       // GC for alloc.
  kFailUntilGCForAllocClearRef,               // GC clear references.

  kFailUntilGCForAllocWithFragment,           // With Fragmentation.
  kFailUntilGCConcurrentWithFragment,         // With Fragmentation.
  kFailUntilGCForAllocClearRefWithFragment,   // With Fragmentation.

  kFailUntilAllocGrowHeap,                    // Heap grown.
  kFailThrowGCOOM,                            // Throw OOM.
  kFailNull,                                  // Not fail.
};

enum RecordType {
  kRecordTypeGC,
  kRecordTypeSucc,
  kRecordTypeFail,
  kRecordTypeLarge,
  kRecordTypeAlloc,
};

std::ostream& operator<<(std::ostream& os, const AllocFailPhase& alloc_fail_phase);
std::ostream& operator<<(std::ostream& os, const RecordType& record_type);

// Base class of all records that GC Profiler dump.
class ProfileRecord {
 public:
  virtual void DumpRecord(std::ofstream& os) { UNUSED(os); }
  virtual ~ProfileRecord() {}
  // Convert time from ns to ms.
  void ConvertTimeToMs(uint64_t& time) {
    time = NsToMs(time);
  }
  // Convert size from bytes to Mb.
  void ConvertSizeToMb(uint32_t& size) {
    size = size / (1024 * 1024);
  }
  // Convert throughput from npns/bpns to npms/bpms.
  void ConvertThroughputToMs(double& throughput_ns) {
    throughput_ns *= 1000 * 1000;
  }
  // Used for InaccuratyMode.
  virtual void ConvertDataUnits() { }
};

// Record for GC info.
class GCRecord : public ProfileRecord {
 public:
  // File Basic info, used when generating record.
  void FillBasicInfo(uint32_t id,
                     GcCause gc_cause,
                     collector::GcType gc_type,
                     uint64_t gc_start_time_ns,
                     uint32_t footprint,
                     uint32_t bytes_allocated,
                     uint32_t main_space_size,
                     uint32_t los_space_size);

  // Fill fields.
  void FillFields(collector::GarbageCollector* collector,
                      uint32_t max_allowed_footprint,
                      uint32_t concurrent_start_bytes,
                      uint32_t alloc_stack_size,
                      uint32_t total_memory,
                      uint32_t bytes_allocated,
                      uint32_t main_space_size,
                      uint32_t los_size);

  // Update the max waiting time and blocking time.
  void UpdateWaitAndBlockTime(uint64_t wait_time,
                              bool update_wait_time = true,
                              bool update_block_time = true) {
    if (update_wait_time) {
      max_wait_time_ = std::max(wait_time, max_wait_time_);
    }
    if (update_block_time) {
      blocking_time_ = std::max(wait_time, blocking_time_);
    }
  }

  // Sum up the time exhausted in unnecessary WaitForGcComplete.
  void UpdateWastedWaitTime(uint64_t wait_time) {
    wasted_wait_time_after_gc_ += wait_time;
  }

  // Update time records.
  void UpdateGCTimes(uint64_t pause, uint64_t mark, uint64_t sweep) {
       pause_time_max_ = pause;
       mark_time_ = mark;
       sweep_time_ = sweep;
  }

  void DumpRecord(std::ofstream& os);
  void ConvertDataUnits();
  uint32_t GetGcId() { return id_; }

 private:
  uint32_t id_;  // GC id.
  GcCause reason_;  // Gc Cause.
  uint64_t pause_time_max_;  // Max pause time.
  uint64_t mark_time_;  // Max mark time.
  uint64_t sweep_time_;  // Max sweep time.
  uint64_t gc_time_;  // GC duration.
  uint32_t free_bytes_;
  uint32_t free_object_count_;
  uint32_t free_large_object_count_;
  uint32_t free_large_object_bytes_;
  uint64_t max_wait_time_;  // Max time wait for this GC.
  uint64_t timestamp_;
  collector::GcType type_;  // Type of GC.
  uint32_t max_allowed_footprint_;
  uint32_t concurrent_start_bytes_;
  uint32_t allocated_size_before_gc_;
  uint32_t allocated_size_after_gc_;  // Total bytes allocated.
  uint32_t total_object_count_in_alloc_stack_during_gc_;
  double gc_throughput_bpns_;
  double gc_throughput_npns_;
  uint64_t blocking_time_;  // Max blocking time caused by this GC.
  uint64_t wasted_wait_time_after_gc_;  // Time exhausted in WaitForGcComplete after this GC and before next GC.
  uint32_t main_space_size_before_gc_;
  uint32_t main_space_size_after_gc_;
  uint32_t los_space_size_before_gc_;
  uint32_t los_space_size_after_gc_;
  uint32_t footprint_size_before_gc_;
  uint32_t footprint_size_after_gc_;  // Footprint.
};

// Successfully allocation record.
class SuccAllocRecord : public ProfileRecord {
 private:
  uint32_t gc_id_;
  uint32_t total_size_;
  uint32_t size_dist_[12];

 public:
  // Insert the size distribution to allocinfo.
  void InsertSizeDist(uint32_t size);
  void DumpRecord(std::ofstream& os);
  void FillFields(uint32_t byte_count);
  void SetGcId(uint32_t id) { gc_id_ = id; }
  uint32_t GetGcId() { return gc_id_; }
  void ConvertDataUnits();
};

// Fail allocation record.
class FailAllocRecord : public ProfileRecord {
 private:
  uint32_t gc_id_;
  uint32_t size_;
  AllocFailPhase phase_;
  collector::GcType last_gc_type_;
  char type_[256];

 public:
  void DumpRecord(std::ofstream& os);
  void ConvertDataUnits();
  void FillFields(mirror::Class* klass,
                  uint32_t bytes_allocated,
                  uint32_t max_allowed_footprint,
                  uint32_t alloc_size,
                  uint32_t gc_id,
                  collector::GcType gc_type,
                  AllocFailPhase fail_phase);
};

// Large object record.
class LargeObjAllocRecord : public ProfileRecord {
 private:
  uint32_t gc_id_;
  uint32_t size_;
  char type_[256];

 public:
  void DumpRecord(std::ofstream& os);
  void ConvertDataUnits();
  void FillFields(uint32_t gc_id, uint32_t byte_count, mirror::Class* klass);
};

// Allocation info.
class AllocInfoRecord : public ProfileRecord {
 private:
  Atomic<uint32_t> number_bytes_alloc_atomic_;
  // Used for data output and unit conversion.
  uint32_t number_bytes_alloc_;
  Atomic<uint32_t> number_objects_alloc_;
  double throughput_npns_;
  double throughput_bpns_;
  uint64_t duration_;

 public:
  // Add allocation info.
  void AddAllocInfo(uint32_t bytes_allocated);
  void DumpRecord(std::ofstream& os);
  void ConvertDataUnits();
  void CalculateAllocThroughput(uint64_t duration);
  AllocInfoRecord() : number_bytes_alloc_atomic_(0),
                      number_bytes_alloc_(0),
                      number_objects_alloc_(0),
                      throughput_npns_(0.0),
                      throughput_bpns_(0.0),
                      duration_(0) { }
};

// Class of record list.
class RecordList {
 public:
  void DumpRecords(std::ofstream& os);
  void InsertRecord(ProfileRecord* record);
  ProfileRecord* GetLastRecord();
  void ReleaseRecordsAndClear();
  uint32_t Size() { return record_list_.size(); }
  void SetRecordType(RecordType record_type) {
    record_type_ = record_type;
  }

  RecordType GetRecordType() const {
    return record_type_;
  }

  void DumpHeader(std::ofstream& os);

 private:
  std::vector<ProfileRecord*> record_list_;
  RecordType record_type_;
};

class GcProfiler {
 public:
  // Start the profiling.
  void Start();
  // Stop the profiling.
  void Stop(bool dropResult);
  // Update the max waiting time and blocking time.
  void UpdateMaxWaitForGcTimeAndBlockingTime(uint64_t wait_time, bool update_wait_time = true,
                                             bool update_block_time = true);
  // Sumup the time exhausted in unnecessary WaitForGcComplete.
  void UpdateWastedWaitTime(uint64_t wait_time);
  // Create new GCRecord and add to list.
  void InsertNewGcRecord(const GcCause gc_cause,
                         const collector::GcType gc_type,
                         uint64_t gc_start_time_ns,
                         uint32_t bytes_allocated,
                         uint32_t footprint,
                         uint32_t main_space_size,
                         uint32_t los_space_size);
  // Fill GCRecord fields.
  void FillGcRecordInfo(collector::GarbageCollector* collector,
                        uint32_t max_allowed_footprint,
                        uint32_t concurrent_start_bytes_,
                        uint32_t alloc_stack_size,
                        uint32_t total_memory,
                        uint32_t bytes_allocated,
                        uint32_t main_space_size,
                        uint32_t los_size);
  // Create a new FailRecord.
  void CreateFailRecord(mirror::Class* klass,
                        uint32_t bytes_allocated,
                        uint32_t max_allowed_footprint,
                        uint32_t alloc_size,
                        collector::GcType gc_type,
                        AllocFailPhase fail_phase);
  // Update profiler's heap information.
  void UpdateHeapUsageInfo(uint32_t bytes_allocated, uint32_t max_allowed_footprint_);

  // Only one GcProfiler instance exist.
  static  GcProfiler* GetInstance() {
    return &s_instance;
  }
  // Add allocation info.
  void AddAllocInfo(uint32_t bytes_allocated);
  // Update GcProfiler's GC times.
  void SetGCTimes(uint64_t pause, uint64_t mark, uint64_t sweep);
  // Update GcProfiler's data dump dir path.
  void SetDir(std::string dir) {
    data_dir_ = dir;
  }
  ~GcProfiler();
  void InsertSuccAllocRecord(uint32_t byte_count, mirror::Class* klass)
      SHARED_LOCKS_REQUIRED(Locks::mutator_lock_);

  void EnableSuccAllocProfile(bool enable) {
    prof_succ_allocation_ = enable;
  }

  bool ProfileSuccAllocInfo() const {
    return prof_succ_allocation_;
  }

  bool IsRunning() const {
    return gc_prof_running_;
  }

  // By default dump .csv file, leave binary options here for extension.
  static bool DumpBinary() {
    return dump_binary_format_;
  }

  // By default, inaccurate mode dump size as MB, time as ms.
  // This can lose accuracy, leave options here for extension.
  static bool InaccurateMode() {
    return inaccurate_mode_;
  }

 private:
  std::unique_ptr<std::ofstream> out_;
  // Duration of the profiling, used for calculate allocation throughput.
  int64_t profile_duration_;
  uint32_t gc_id_;
  bool gc_prof_running_;
  bool prof_succ_allocation_;
  static GcProfiler s_instance;
  std::string data_dir_;
  GcProfiler();
  GcProfiler(const GcProfiler&);
  GcProfiler& operator=(const GcProfiler&);
  // Lock for start/stop gc profiling.
  Mutex* gc_profiler_lock_;
  // Lock for record success allocation.
  Mutex* succ_record_lock_;
  // Lock for record fail allocation.
  Mutex* fail_record_lock_;
  // List of GCRecord.
  std::vector<GCRecord*> GCRecordList;

  RecordList gc_record_list_;
  RecordList succ_record_list_;
  RecordList fail_record_list_;
  RecordList large_object_alloc_record_list_;
  RecordList alloc_info_record_list_;
  std::vector<RecordList*> record_lists_;
  // Dump result as .csv file.
  static constexpr bool dump_binary_format_ = false;
  // Dump time in ms and size in MB.
  static constexpr bool inaccurate_mode_ = true;
  // Dump title line for csv file.
  void DumpTitleLine(std::ofstream& out);
  // Dump all Record info.
  void DumpRecordLists();
  void ClearAndReleaseAllRecords();
  void CalculateAllocThroughput(uint64_t profile_duration);
  void CreateSuccAllocRecord(uint32_t gc_id);
  void InsertLargeObjAllocRecord(uint32_t gc_id, uint32_t byte_count, mirror::Class* klass);
  void CreateAllocInfoRecord();
  uint32_t GetCurrentGcId();
};
}   // namespace gc
}   // namespace art

#endif  // ART_RUNTIME_GC_GCPROFILER_H_
