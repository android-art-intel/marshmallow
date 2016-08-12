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

#include "dex/pass_manager.h"
#include "perf_analysis.h"
#include "pretty_printer.h"

#include <fstream>
#include <functional>
#include <pwd.h>
#include <sstream>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

namespace art {

void PerfAnalysisInfrastructure::SetOptions(const PassManagerOptions* options) {
  DCHECK(options != nullptr);
  pass_options_ = options;

  metrics_.clear();

  // Compile time metrics.
  if (options->InstrumentCompileTime()) {
    metrics_.push_back(&timing_);
    metrics_.push_back(&tsclock_);
  }

  // Pass usage metrics.
  if (options->InstrumentPassUsage()) {
    metrics_.push_back(&pass_stats_);
  }

  // Memory consumption metrics.
  if (options->InstrumentMemoryConsumption()) {
    if (arena_ != nullptr) {
      metrics_.push_back(&arena_bytes_allocated_);
      metrics_.push_back(&arena_num_allocations_);
    }
    metrics_.push_back(&malloc_stats_);
  }
}

std::vector<PassData<uint64_t>>&
PerfAnalysisInfrastructure::PrepareCollection(AnalysisData& data, const std::string& collection_name) {
  std::vector<PassData<uint64_t>>& collection_data = data.collections_[collection_name];
  collection_data.resize(metrics_.size());

  return collection_data;
}

void PerfAnalysisInfrastructure::StartAnalysis(const std::string& analysis_name) {
  // Paranoid: we should not have an already-started analysis with the same name.
  DCHECK(analyses_pool_.find(analysis_name) == analyses_pool_.end());

  // Then, we add an entry in our analyses pool.
  analyses_pool_[analysis_name] = AnalysisData();
}

void PerfAnalysisInfrastructure::StartCollection(const std::string& analysis_name,
                                                 const std::string& collection_name,
                                                 void* optional_data) {
  if (!ShouldInstrument()) {
    return;
  }

  // We need to find the analysis corresponding to the provided identifier.
  DCHECK(analyses_pool_.find(analysis_name) != analyses_pool_.end()) << analysis_name;

  // Retrieve the data structure corresponding to the analysis.
  AnalysisData& data = analyses_pool_[analysis_name];

  // We must not have a collection with the same name in our analysis.
  DCHECK(data.collections_.find(collection_name) == data.collections_.end()) << collection_name;

  // Save the order of the collection to use it when dumping, because we want to do it in order.
  data.collection_order_.push_back(collection_name);

  // Allocate the metrics we need for measurement.
  auto& collection_data = PrepareCollection(data, collection_name);

  // Start the metrics.
  const uint32_t metrics_size = metrics_.size();
  for (size_t i = 0; i < metrics_size; i++) {
    auto metric = metrics_[i];
    metric->Start(collection_data[i], collection_name.c_str(), optional_data);
  }
}

void PerfAnalysisInfrastructure::StopCollection(const std::string& analysis_name,
                                                const std::string& collection_name,
                                                void* optional_data) {
  if (!ShouldInstrument()) {
    return;
  }

  // there must be an analysis started with the provided identifier.
  DCHECK(analyses_pool_.find(analysis_name) != analyses_pool_.end());

  // Retrieve the data structure corresponding to the analysis.
  AnalysisData& data = analyses_pool_[analysis_name];

  // We must have a collection with the same name in our analysis, since we are stopping it.
  DCHECK(data.collections_.find(collection_name) != data.collections_.end());

  // Retrieve the collection data that was started before.
  auto& collection_data = data.collections_[collection_name];

  // Stop the metrics in the reverse order.
  const int metrics_size = metrics_.size();
  for (int i = metrics_size - 1; i >= 0; i--) {
    auto metric = metrics_[i];
    metric->Stop(collection_data[i], collection_name.c_str(), optional_data);
  }
}

void PerfAnalysisInfrastructure::StopAnalysis(const std::string& analysis_name,
                                             bool hash_name) {
  if (!ShouldInstrument()) {
    return;
  }

  // We need to stop an existing analysis.
  DCHECK(analyses_pool_.find(analysis_name) != analyses_pool_.end()) << analysis_name;

  // Retrieve the data.
  AnalysisData& data = analyses_pool_[analysis_name];

  // If we have something to dump, then dump it.
  if (data.collections_.size() != 0u && metrics_.size() != 0u) {
    Dump(analysis_name, hash_name);
  }

  // It is safe to remove this entry now.
  analyses_pool_.erase(analysis_name);
}

void PerfAnalysisInfrastructure::Dump(const std::string& analysis_name, bool hash_name) {
  if (!ShouldInstrument()) {
    return;
  }

  DCHECK(analyses_pool_.find(analysis_name) != analyses_pool_.end());

  // Fetch the data from the pool.
  AnalysisData& data = analyses_pool_[analysis_name];

  const size_t num_collections = data.collections_.size();
  const size_t num_metrics = metrics_.size();

  // If there is no collection for this analysis, we don't need to dump.
  if (num_collections == 0u) {
    return;
  }

  // We might want to hash the filename in case it's too complex or too long for the FS.
  std::stringstream output_filename;
  if (hash_name) {
    std::hash<std::string> hasher;
    std::string to_hash(analysis_name);
    output_filename << hasher(to_hash) << ".hash";
  } else {
    output_filename << analysis_name;
  }

  std::ostringstream oss;

  // Write the CSV header.
  oss << "\"" << analysis_name << "\",";
  for (size_t i = 0; i < num_metrics; i++) {
    auto metric = metrics_[i];
    oss << "\"" << metric->GetName() << "\", ";
  }
  oss << std::endl;

  // Write the CSV data.
  for (size_t coll_id = 0; coll_id < num_collections; coll_id++) {
    auto collection_name = data.collection_order_[coll_id];
    oss << "Pass \"" << collection_name << "\", ";
    DCHECK(data.collections_.find(collection_name) != data.collections_.end());
    auto& collection_data = data.collections_[collection_name];
    for (size_t j = 0; j < num_metrics; j++) {
      metrics_[j]->Print(oss, collection_data[j]);
      oss << ", ";
    }
    oss << std::endl;
  }

  // We want to open the file according to whether we are building on host or target.
  std::ostringstream folder, full_path;
  if (kIsTargetBuild) {
    folder << "/data/art_perf/";
  } else {
    folder << "/tmp/art_perf/";
  }

  // Create output directory if needed.
  mkdir(folder.str().c_str(), S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);

  full_path << folder.str() << output_filename.str() << ".csv";

  // Open the output file.
  std::ofstream fd(full_path.str());

  if (!fd.is_open()) {
    LOG(INFO) << "Error: Could not open file " << full_path.str().c_str();
    return;
  }

  // Effective dump.
  fd << oss.str();

  fd.close();
}

}  // namespace art.

#endif  // PERF_ANALYSIS_INFRASTRUCTURE
