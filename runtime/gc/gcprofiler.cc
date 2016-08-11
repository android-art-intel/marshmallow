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

#include <fcntl.h>
#include <fstream>
#include <limits>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <vector>
#include "base/histogram-inl.h"
#include "base/stl_util.h"
#include "common_throws.h"
#include "cutils/sched_policy.h"
#include "debugger.h"
#include "gc/accounting/atomic_stack.h"
#include "gc/accounting/card_table-inl.h"
#include "gc/accounting/heap_bitmap-inl.h"
#include "gc/accounting/mod_union_table.h"
#include "gc/accounting/mod_union_table-inl.h"
#include "gc/accounting/remembered_set.h"
#include "gc/accounting/space_bitmap-inl.h"
#include "gc/collector/concurrent_copying.h"
#include "gc/collector/mark_sweep-inl.h"
#include "gc/collector/partial_mark_sweep.h"
#include "gc/collector/semi_space.h"
#include "gc/collector/sticky_mark_sweep.h"
#include "gc/reference_processor.h"
#include "gc/space/bump_pointer_space.h"
#include "gc/space/dlmalloc_space-inl.h"
#include "gc/space/image_space.h"
#include "gc/space/large_object_space.h"
#include "gc/space/rosalloc_space-inl.h"
#include "gc/space/space-inl.h"
#include "gc/space/zygote_space.h"
#include "entrypoints/quick/quick_alloc_entrypoints.h"
#include "heap-inl.h"
#include "image.h"
#include "art_field-inl.h"
#include "mirror/class-inl.h"
#include "mirror/object.h"
#include "mirror/object-inl.h"
#include "mirror/object_array-inl.h"
#include "mirror/reference-inl.h"
#include "os.h"
#include "reflection.h"
#include "runtime.h"
#include "ScopedLocalRef.h"
#include "scoped_thread_state_change.h"
#include "thread_list.h"
#include "UniquePtr.h"
#include "well_known_classes.h"
#include "gc/gcprofiler.h"

namespace art {

namespace gc {
// Number of regions divided for size distribution.
static constexpr size_t kNumSizeDistRegions = 11;
static constexpr size_t kConstToFragmentPhase = 4;
// Dump Record Header based on record type.
void RecordList::DumpHeader(std::ofstream& os) {
  switch (record_type_) {
    case kRecordTypeGC: {
      os << "GC Message, ID, Timestamp, reason, max pausetime, mark time, sweep time, "
         << "GC duration, number objects freed, freed bytes, number large obj freed, large obj freed bytes, "
         << "max wait time, GC type, max allowed footprint, concurrent start bytes, blocking time, "
         << "allocated size before gc, allocated size after gc, alloc stack size after gc, GC throughput, "
         << "GC throughput, footprint before gc, footprint after gc, main space size before gc, "
         << "main space size after gc, los space size before gc, los space size after gc" << std::endl;
      break;
    }
    case kRecordTypeSucc: {
      os << "Succeeded Allocation Messages,GC_Id, total_size, [1-16], [17-32],"
         << " [33-64], [65-128], [129-256], [257-512], [513-1024], [1025-2048], [2049-4096],"
         << " [4097-8192], [8193-12288], [12288-]" << std::endl;
     break;
    }
    case kRecordTypeFail: {
      os << "Failed Allocation Messages, GC_Id, size, phase, last_gc_type, type" << std::endl;
      break;
    }
    case kRecordTypeLarge: {
      os << "Large Object Messages, GC_Id, size, type" << std::endl;
      break;
    }
    case kRecordTypeAlloc: {
      os << "Allocation Info, duration(ms), total_bytes_allocated, "
         << "total_objects_allocated, Alloc_ThroughPut, Alloc_ThroughPut" << std::endl;
      break;
    }
    default: {
      os << "Not Supported Info!!" << record_type_ << std::endl;
      break;
    }
  }
}

// Dump Records in RecordList.
void RecordList::DumpRecords(std::ofstream& os) {
  for (auto iter = record_list_.begin();
       iter != record_list_.end(); iter++) {
    // Dump Record Data.
    (*iter)->DumpRecord(os);
  }
}

// Release Records in list and clear the list.
void RecordList::ReleaseRecordsAndClear() {
  for (auto iter = record_list_.begin();
        iter != record_list_.end(); iter++) {
    delete (*iter);
  }
  record_list_.clear();
}

// Create new record and insert to list.
void RecordList::InsertRecord(ProfileRecord* record) {
  record_list_.push_back(record);
}

// Get the last Record in list, it should be the currently using one.
ProfileRecord* RecordList::GetLastRecord() {
  if (record_list_.size() == 0) {
    return nullptr;
  }
  return record_list_.back();
}

// Fill basic info, used when creating new record.
void GCRecord::FillBasicInfo(uint32_t id,
                             GcCause gc_cause,
                             collector::GcType gc_type,
                             uint64_t gc_start_time_ns,
                             uint32_t footprint,
                             uint32_t bytes_allocated,
                             uint32_t main_space_size,
                             uint32_t los_space_size) {
  id_ = id;
  reason_ = gc_cause;
  type_ = gc_type;
  timestamp_ = gc_start_time_ns;
  footprint_size_before_gc_ = footprint;
  allocated_size_before_gc_ = bytes_allocated;
  main_space_size_before_gc_ = main_space_size;
  los_space_size_before_gc_ = los_space_size;
}

// Fill fields of GCRecord.
void GCRecord::FillFields(collector::GarbageCollector* collector,
                       uint32_t max_allowed_footprint,
                       uint32_t concurrent_start_bytes,
                       uint32_t alloc_stack_size,
                       uint32_t total_memory,
                       uint32_t bytes_allocated,
                       uint32_t main_space_size,
                       uint32_t los_size) {
    free_bytes_ = collector->GetCurrentIteration()->GetFreedBytes();
    free_object_count_ = collector->GetCurrentIteration()->GetFreedObjects();
    free_large_object_count_ = collector->GetCurrentIteration()->GetFreedLargeObjects();
    free_large_object_bytes_ = collector->GetCurrentIteration()->GetFreedLargeObjectBytes();
    gc_time_ = collector->GetCurrentIteration()->GetDurationNs();
    footprint_size_after_gc_ = total_memory;
    allocated_size_after_gc_ = bytes_allocated;
    max_allowed_footprint_ = max_allowed_footprint;
    concurrent_start_bytes_ = concurrent_start_bytes;
    total_object_count_in_alloc_stack_during_gc_ = alloc_stack_size;
    main_space_size_after_gc_ = main_space_size;
    los_space_size_after_gc_ = los_size;

    if (gc_time_ != 0) {
      gc_throughput_bpns_ = static_cast<double>(free_bytes_ + free_large_object_bytes_) / static_cast<double>(gc_time_);
      gc_throughput_npns_ = static_cast<double>(free_object_count_ + free_large_object_count_) / static_cast<double>(gc_time_);
    } else {
      gc_throughput_bpns_ = 0.0;
      gc_throughput_npns_ = 0.0;
    }
}

void GCRecord::ConvertDataUnits() {
  // Only convert in inaccurate mode.
  if (GcProfiler::InaccurateMode()) {
    // Convert time.
    ConvertTimeToMs(timestamp_);
    ConvertTimeToMs(pause_time_max_);
    ConvertTimeToMs(mark_time_);
    ConvertTimeToMs(sweep_time_);
    ConvertTimeToMs(gc_time_);
    ConvertTimeToMs(max_wait_time_);
    ConvertTimeToMs(blocking_time_);
    // Convert size.
    ConvertSizeToMb(free_bytes_);
    ConvertSizeToMb(free_large_object_bytes_);
    ConvertSizeToMb(max_allowed_footprint_);
    ConvertSizeToMb(concurrent_start_bytes_);
    ConvertSizeToMb(allocated_size_before_gc_);
    ConvertSizeToMb(allocated_size_after_gc_);
    ConvertSizeToMb(footprint_size_before_gc_);
    ConvertSizeToMb(footprint_size_after_gc_);
    ConvertSizeToMb(main_space_size_before_gc_);
    ConvertSizeToMb(main_space_size_after_gc_);
    ConvertSizeToMb(los_space_size_before_gc_);
    ConvertSizeToMb(los_space_size_after_gc_);
    // Convert throughput bpns/npns to bpms/npms.
    ConvertThroughputToMs(gc_throughput_bpns_);
    ConvertThroughputToMs(gc_throughput_npns_);
  }
}

// Dump GC records.
void GCRecord::DumpRecord(std::ofstream& os) {
  if (GcProfiler::DumpBinary()) {
    // Dump binary raw data.
    os.write(reinterpret_cast<char*>(this), sizeof(GCRecord));
  } else {
    ConvertDataUnits();
    // Dump .csv format data.
    os << "," << id_ << "," << timestamp_ << "," << reason_ << "," << pause_time_max_ << "," << mark_time_
       << "," << sweep_time_ << "," << gc_time_ << "," << free_object_count_ << "," << free_bytes_
       << "," << free_large_object_count_ << "," << free_large_object_bytes_ << "," << max_wait_time_
       << "," << type_ << "," << max_allowed_footprint_ << "," << concurrent_start_bytes_
       << "," << blocking_time_ << "," << allocated_size_before_gc_ << "," << allocated_size_after_gc_
       << "," << total_object_count_in_alloc_stack_during_gc_ << "," << gc_throughput_bpns_
       << "," << gc_throughput_npns_ << "," << footprint_size_before_gc_ << "," << footprint_size_after_gc_
       << "," << main_space_size_before_gc_ << "," << main_space_size_after_gc_
       << "," << los_space_size_before_gc_ << "," << los_space_size_after_gc_ << std::endl;
  }
  os.flush();
}

// Convert the data unit for inaccurate mode.
void SuccAllocRecord::ConvertDataUnits() {
  if (GcProfiler::InaccurateMode()) {
    // Convert bytes to Mb.
    ConvertSizeToMb(total_size_);
  }
}

// Dump succeed allocation info.
void SuccAllocRecord::DumpRecord(std::ofstream& os) {
  if (GcProfiler::DumpBinary()) {
    os.write(reinterpret_cast<char*>(this), sizeof(SuccAllocRecord));
  } else {
    ConvertDataUnits();
    os << " ," << gc_id_ << " ," << total_size_;
    for (int i = 0; i < 12; i++) {
      os << " ," << size_dist_[i];
    }
    os << std::endl;
  }
  os.flush();
}
// Insert the size to distribution in allocation info record.
// The size_dist_[] divided the object size into kNumSizeDistRegions.
// It records the count of objects whose size is in coressponding region.
void SuccAllocRecord::InsertSizeDist(uint32_t size) {
  if (size >= Heap::kDefaultLargeObjectThreshold) {
    size_dist_[kNumSizeDistRegions]++;
  } else {
    uint32_t t = (size - 1) >> 4;
    uint32_t i = 0;
    while (t > 0) {
      i++;
      t = t >> 1;
    }
    size_dist_[i]++;
  }
}

// Fill success allocation info fields.
void SuccAllocRecord::FillFields(uint32_t byte_count) {
  total_size_ += byte_count;
  InsertSizeDist(byte_count);
}

// Convert the data unit for inaccurate mode.
void FailAllocRecord::ConvertDataUnits() {
  if (GcProfiler::InaccurateMode()) {
    // Convert bytes to Mb.
    ConvertSizeToMb(size_);
  }
}

// Dump Record of fail allocation info.
void FailAllocRecord::DumpRecord(std::ofstream& os) {
  if (GcProfiler::DumpBinary()) {
    os.write(reinterpret_cast<char*>(this), sizeof(FailAllocRecord));
  } else {
    ConvertDataUnits();
    os << ", " << gc_id_ << ", " << size_ << ", " << phase_ << ", " << last_gc_type_ << ", " << type_ << std::endl;
  }
  os.flush();
}

// Fill fail allocation info fields.
void FailAllocRecord::FillFields(mirror::Class* klass,
                                 uint32_t bytes_allocated,
                                 uint32_t max_allowed_footprint,
                                 uint32_t alloc_size,
                                 uint32_t gc_id,
                                 collector::GcType gc_type,
                                 AllocFailPhase fail_phase) {
  uint32_t free_size = max_allowed_footprint - bytes_allocated;
  gc_id_ = gc_id;
  size_ = alloc_size;
  last_gc_type_ = gc_type;
  std::string class_desc = Runtime::Current()->GetHeap()->SafeGetClassDescriptor(klass);
  int length = class_desc.size() + 1;
  length = length <= 255 ? length : 255;
  strncpy(type_, class_desc.c_str(), length);
  // If there is segmentation, set corresponding phase.
  if (free_size > alloc_size && fail_phase <= kFailUntilGCForAllocClearRef) {
    phase_ = (AllocFailPhase)(fail_phase + kConstToFragmentPhase);
  } else {
    phase_ = fail_phase;
  }
}

// Convert the data unit for inaccurate mode.
void LargeObjAllocRecord::ConvertDataUnits() {
  if (GcProfiler::InaccurateMode()) {
    // Convert bytes to Mb.
    ConvertSizeToMb(size_);
  }
}

// Dump large object allocation info.
void LargeObjAllocRecord::DumpRecord(std::ofstream& os) {
  if (GcProfiler::DumpBinary()) {
    os.write(reinterpret_cast<char*>(this), sizeof(LargeObjAllocRecord));
  } else {
    ConvertDataUnits();
    os << " ," << gc_id_ << " ," << size_ << " ," << type_ << std::endl;
  }
  os.flush();
}

// Fill large object allocation info.
void LargeObjAllocRecord::FillFields(uint32_t gc_id, uint32_t byte_count, mirror::Class* klass) {
  gc_id_ = gc_id;
  size_ = byte_count;
  std::string class_desc = Runtime::Current()->GetHeap()->SafeGetClassDescriptor(klass);
  int length = class_desc.size() + 1;
  length = length <= 255 ? length : 255;
  strncpy(type_, class_desc.c_str(), length);
}

// Sum up the number of bytes and counts of allocted objects.
void AllocInfoRecord::AddAllocInfo(uint32_t bytes) {
  number_bytes_alloc_atomic_.FetchAndAddSequentiallyConsistent(bytes);
  number_objects_alloc_++;
}

// Calculate allocation throughput.
void AllocInfoRecord::CalculateAllocThroughput(uint64_t duration) {
  if (duration != 0) {
    throughput_bpns_ = static_cast<double>(number_bytes_alloc_atomic_.LoadSequentiallyConsistent())
        / static_cast<double>(duration);
    throughput_npns_ = static_cast<double>(number_objects_alloc_.LoadSequentiallyConsistent())
        / static_cast<double>(duration);
  } else {
    // If profile_duration is too small, throughputs are set to 0.
    throughput_bpns_ = 0.0;
    throughput_npns_ = 0.0;
  }
  duration_ = duration;
}

// Convert the data unit for inaccurate mode.
void AllocInfoRecord::ConvertDataUnits() {
  if (GcProfiler::InaccurateMode()) {
    // Convert bytes to MB.
    number_bytes_alloc_ = RoundUp(number_bytes_alloc_atomic_.LoadSequentiallyConsistent() / MB, MB);
    // Convert throughput.
    ConvertThroughputToMs(throughput_bpns_);
    ConvertThroughputToMs(throughput_npns_);
  } else {
    // For accurate mode, load the value for output.
    number_bytes_alloc_ = number_bytes_alloc_atomic_.LoadSequentiallyConsistent();
  }
}

// Dump allocation record.
void AllocInfoRecord::DumpRecord(std::ofstream& os) {
  if (GcProfiler::DumpBinary()) {
    os.write(reinterpret_cast<char*>(this), sizeof(AllocInfoRecord));
  } else {
    ConvertDataUnits();
    os << ", " << duration_ << ", " << number_bytes_alloc_ << ", " << number_objects_alloc_
       << ", " << throughput_bpns_ << ", " << throughput_npns_ << std::endl;
  }
  os.flush();
}

GcProfiler GcProfiler::s_instance;

GcProfiler::GcProfiler()
          : profile_duration_(0),
            gc_id_(0),
            gc_prof_running_(false),
            prof_succ_allocation_(false),
            data_dir_("data/local/tmp/gcprofile/") {
  gc_profiler_lock_ = new Mutex("Gcprofiling lock");
  succ_record_lock_ = new Mutex("Successfull allocation record lock");
  fail_record_lock_ = new Mutex("Fail allocation record lock");
  // Build up the record lists for dump iteration.
  record_lists_.push_back(&gc_record_list_);
  record_lists_.push_back(&succ_record_list_);
  record_lists_.push_back(&fail_record_list_);
  record_lists_.push_back(&large_object_alloc_record_list_);
  record_lists_.push_back(&alloc_info_record_list_);
  // Set the record list type.
  gc_record_list_.SetRecordType(kRecordTypeGC);
  succ_record_list_.SetRecordType(kRecordTypeSucc);
  fail_record_list_.SetRecordType(kRecordTypeFail);
  large_object_alloc_record_list_.SetRecordType(kRecordTypeLarge);
  alloc_info_record_list_.SetRecordType(kRecordTypeAlloc);
}

GcProfiler::~GcProfiler() {
  delete succ_record_lock_;
  delete fail_record_lock_;
}

// Start GC Profiler, init related variables.
void GcProfiler::Start() {
  LOG(INFO) << "GCProfile: Start";
  Thread* self = Thread::Current();
  // In case there are multi-starts.
  MutexLock mu(self, *gc_profiler_lock_);
  // If profile already start, return.
  if (gc_prof_running_) {
    return;
  }
  profile_duration_ = NsToMs(NanoTime());
  gc_prof_running_ = true;

  // Create allocation info record.
  CreateAllocInfoRecord();
  CreateSuccAllocRecord(0);
}

// Stop GC profiling.
void GcProfiler::Stop(bool drop_result) {
  Thread* self = Thread::Current();
  MutexLock mu(self, *gc_profiler_lock_);
  // Return if gc profiling not running.
  if (!gc_prof_running_) {
    return;
  }
  // Don't need the result.
  if (drop_result) {
    ClearAndReleaseAllRecords();
    gc_prof_running_ = false;
    return;
  }
  // Profile duration is used for calculating allocation throughput.
  profile_duration_ = NsToMs(NanoTime()) - profile_duration_;
  // Calculate throughput.
  CalculateAllocThroughput(profile_duration_);
  DumpRecordLists();
  ClearAndReleaseAllRecords();
  LOG(INFO) << "GCProfile: Finish!";
  gc_prof_running_ = false;
}

// Dump title line that contains data unit.
void GcProfiler::DumpTitleLine(std::ofstream& out) {
  std::string time_unit = "nanoseconds";
  std::string size_unit = "bytes";
  std::string throughput_b_unit = "bytes/nanosecond";
  std::string throughput_n_unit = "count/nanosecond";
  // Only need title in csv file
  if (DumpBinary()) {
    return;
  }
  // Change data unit for inaccurate mode.
  if (InaccurateMode()) {
    time_unit = "milliseconds";
    size_unit = "mega-bytes";
    throughput_b_unit = "bytes/milliseconds";
    throughput_n_unit = "count/milliseconds";
  }

  out << "GcProfile Data, Unit: Time(" << time_unit <<") Size (" << size_unit << ") Throughput ("
      << throughput_b_unit << " ; " << throughput_n_unit << ")" << std::endl;
  out.flush();
}

// Iterate all list in record_lists and dump.
void GcProfiler::DumpRecordLists() {
  // Create the dump file.
  char file_name[256];
  std::string suffix = "csv";
  int tail = 0;
  int err = 0;
  bool tried_data_path = false;
  struct stat buf;
  // Using different suffix for different format.
  if (DumpBinary()) {
    suffix = "b";
  }
  // Find the file path to save profile data. if data_dir not work, using app's private data path.
  do {
    // Try create output files.
    for (tail = 0; ; tail++) {
      snprintf(file_name, sizeof(file_name), "%s/alloc_free_log_%d_%d.%s", data_dir_.c_str(), getpid(), tail, suffix.c_str());
      err = stat(file_name, &buf);
      if (err != 0) {
        LOG(INFO) << file_name << " will be used.";
        break;
      }
    }
    // Open profile log file.
    out_.reset(new std::ofstream(file_name, std::ios::binary));
    if (!out_->good()) {
      LOG(WARNING) << "GCProfile: cannot open " << file_name << " " << strerror(errno);
      // Process data dir, use app's private data path, if can not use the pre-assigned one.
      if (!tried_data_path) {
        // Get process name from /proc/self/cmdline.
        std::ifstream in("/proc/self/cmdline");
        std::string proc_name;
        std::string app_data_dir = "/data/data/";
        if (!in.is_open()) {
          LOG(ERROR) << "GCProfile: cannot dump data!";
          return;
        }
        getline(in, proc_name);
        data_dir_ = app_data_dir + proc_name + "/";
        LOG(WARNING) << "GCProfile: using app data dir: " << data_dir_;
        tried_data_path = true;
      } else {
        LOG(ERROR) << "GCProfile: cannot dump data!";
        return;
      }
    } else {
      break;
    }
  } while (true);

  // Dump title which contains data unit.
  DumpTitleLine(*out_);

  for (auto it = record_lists_.begin();
       it != record_lists_.end(); it++) {
    if (DumpBinary()) {
      // Dump record type and list size when dump binary format.
      uint32_t count = (*it)->Size();
      if (count <= 0) {
        continue;
      }
      RecordType record_type = (*it)->GetRecordType();
      out_->write(reinterpret_cast<char*>(&count), sizeof(uint32_t));
      out_->write(reinterpret_cast<char*>(&record_type), sizeof(uint32_t));
      out_->flush();
    } else {
      // Dump Headers for record.
      (*it)->DumpHeader(*out_);
    }
    // Dump Records.
    (*it)->DumpRecords(*out_);
  }
  out_->close();
}

// Clear and release all records.
void GcProfiler::ClearAndReleaseAllRecords() {
  for (auto it = record_lists_.begin();
       it != record_lists_.end(); it++) {
    (*it)->ReleaseRecordsAndClear();
  }
  // Clear all counters.
  gc_id_ = 0;
  profile_duration_ = 0;
}

// Update max wait time and blocking time in GC record.
void GcProfiler::UpdateMaxWaitForGcTimeAndBlockingTime(uint64_t wait_time,
                                                       bool update_wait_time,
                                                       bool update_block_time) {
  if (gc_prof_running_) {
    GCRecord* record = reinterpret_cast<GCRecord*>(gc_record_list_.GetLastRecord());
    if (record != nullptr) {
      record->UpdateWaitAndBlockTime(wait_time, update_wait_time, update_block_time);
    }
  }
}

// Sum up the time wasted in WaitForGcComplete duration two GCs.
void GcProfiler::UpdateWastedWaitTime(uint64_t wait_time) {
  if (gc_prof_running_) {
    GCRecord* record = reinterpret_cast<GCRecord*>(gc_record_list_.GetLastRecord());
    if (record != nullptr) {
      record->UpdateWastedWaitTime(wait_time);
    }
  }
}

// Create allocation info record.
void GcProfiler::CreateAllocInfoRecord() {
  if (gc_prof_running_) {
    AllocInfoRecord *record = new AllocInfoRecord();
    if (record != nullptr) {
      alloc_info_record_list_.InsertRecord(reinterpret_cast<ProfileRecord*>(record));
    }
  }
}

// Insert new GC record into record list.
void GcProfiler::InsertNewGcRecord(const GcCause gc_cause, const collector::GcType gc_type,
                                   uint64_t gc_start_time_ns, uint32_t bytes_allocated, uint32_t footprint,
                                   uint32_t main_space_size, uint32_t los_space_size) {
  if (gc_prof_running_) {
    GCRecord* record = new GCRecord();
    if (record != nullptr) {
      record->FillBasicInfo(gc_id_++, gc_cause, gc_type, gc_start_time_ns, footprint,
                            bytes_allocated, main_space_size, los_space_size);
      gc_record_list_.InsertRecord(reinterpret_cast<ProfileRecord*>(record));
      // Create success allocation record if necessory.
      if (prof_succ_allocation_) {
        CreateSuccAllocRecord(record->GetGcId());
      }
    }
  }
}

// Get gc id of current gc record.
uint32_t GcProfiler::GetCurrentGcId() {
  if (gc_prof_running_) {
    GCRecord* record = reinterpret_cast<GCRecord*>(gc_record_list_.GetLastRecord());
    if (record != nullptr) {
      return record->GetGcId();
    }
  }
  return 0;
}

// Fill record info for the GC.
void GcProfiler::FillGcRecordInfo(collector::GarbageCollector* collector,
                                  uint32_t max_allowed_footprint,
                                  uint32_t concurrent_start_bytes,
                                  uint32_t alloc_stack_size,
                                  uint32_t total_memory,
                                  uint32_t bytes_allocated,
                                  uint32_t main_space_size,
                                  uint32_t los_space_size) {
  if (gc_prof_running_) {
    GCRecord* record = reinterpret_cast<GCRecord*>(gc_record_list_.GetLastRecord());
    if (record != nullptr) {
      record->FillFields(collector, max_allowed_footprint, concurrent_start_bytes,
                           alloc_stack_size, total_memory, bytes_allocated, main_space_size, los_space_size);
    }
  }
}

// Create success allocation record.
void GcProfiler::CreateSuccAllocRecord(uint32_t gc_id) {
  if (gc_prof_running_) {
    SuccAllocRecord* record = new SuccAllocRecord();
    if (record != nullptr) {
      record->SetGcId(gc_id);
      succ_record_list_.InsertRecord(reinterpret_cast<ProfileRecord*>(record));
    }
  }
}

// Insert succ alloc record info in to current record.
void GcProfiler::InsertSuccAllocRecord(uint32_t byte_count, mirror::Class* klass) {
  if (gc_prof_running_) {
    if (prof_succ_allocation_ == false) {
      return;
    }
    MutexLock mu(Thread::Current(), *succ_record_lock_);
    // If no record in list, create one with gc id set to zero.
    if (succ_record_list_.Size() == 0) {
      CreateSuccAllocRecord(0);
    }
    SuccAllocRecord* record = reinterpret_cast<SuccAllocRecord*>(succ_record_list_.GetLastRecord());
    if (record != nullptr) {
      record->FillFields(byte_count);
      if (byte_count >= Heap::kDefaultLargeObjectThreshold && klass->IsPrimitiveArray()) {
        InsertLargeObjAllocRecord(record->GetGcId(), byte_count, klass);
      }
    }
  }
}

// Insert large object alloction info.
void GcProfiler::InsertLargeObjAllocRecord(uint32_t gc_id, uint32_t byte_count, mirror::Class* klass) {
  if (gc_prof_running_) {
    LargeObjAllocRecord *record = new LargeObjAllocRecord();
    if (record != nullptr) {
      record->FillFields(gc_id, byte_count, klass);
      large_object_alloc_record_list_.InsertRecord(reinterpret_cast<ProfileRecord*>(record));
    }
  }
}

// Calaculate alloc throughput with duration.
void GcProfiler::CalculateAllocThroughput(uint64_t duration) {
  AllocInfoRecord* record = reinterpret_cast<AllocInfoRecord*>(alloc_info_record_list_.GetLastRecord());
  if (record != nullptr) {
    record->CalculateAllocThroughput(duration);
  }
}

// Create fail allocation result.
void GcProfiler::CreateFailRecord(mirror::Class* klass,
                                  uint32_t bytes_allocated,
                                  uint32_t max_allowed_footprint,
                                  uint32_t alloc_size, collector::GcType gc_type,
                                  AllocFailPhase fail_phase) {
  if (gc_prof_running_) {
    Thread* self = Thread::Current();
    MutexLock mu(self, *fail_record_lock_);
    uint32_t gc_id = GetCurrentGcId();
    FailAllocRecord* record = new FailAllocRecord();
    if (record != nullptr) {
      record->FillFields(klass, bytes_allocated, max_allowed_footprint, alloc_size, gc_id, gc_type, fail_phase);
      fail_record_list_.InsertRecord(reinterpret_cast<ProfileRecord*>(record));
    }
  }
}

// Set max pause time, mark time and sweep time.
void GcProfiler::SetGCTimes(uint64_t pause, uint64_t mark, uint64_t sweep) {
  if (gc_prof_running_) {
    GCRecord* record = reinterpret_cast<GCRecord*>(gc_record_list_.GetLastRecord());
    if (record != nullptr) {
      record->UpdateGCTimes(pause, mark, sweep);
    }
  }
}

// Add alloc info.
void GcProfiler::AddAllocInfo(uint32_t bytes_allocated) {
  if (gc_prof_running_) {
    AllocInfoRecord* record = reinterpret_cast<AllocInfoRecord*>(alloc_info_record_list_.GetLastRecord());
    if (record != nullptr) {
      record->AddAllocInfo(bytes_allocated);
    }
  }
}

}  // namespace gc
}  // namespace art
