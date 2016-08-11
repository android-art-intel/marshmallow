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

#ifndef ART_OPT_INFRASTRUCTURE_INSTRUMENTATION_H_
#define ART_OPT_INFRASTRUCTURE_INSTRUMENTATION_H_

#include "nodes.h"
#include "optimization.h"
#include "perf_analysis_metrics.h"

namespace art {

/**
 * @brief Structure holding the instrumentation of the Perf. Analysis Infrastructure.
 * It holds the metrics to be used, and provides methods to start and stop analyses and collections,
 * as well as dumping the results of the metrics once the instrumentation is done.
 */
class PerfAnalysisInfrastructure : public ArenaObject<kArenaAllocMisc> {
  public:
    PerfAnalysisInfrastructure(ArenaAllocator* arena = nullptr,
                               const PassManagerOptions* pass_options = nullptr) :
    arena_(arena),
    timing_(),
    tsclock_(),
    malloc_stats_(),
    arena_bytes_allocated_(),
    arena_num_allocations_(),
    pass_stats_() {
      if (pass_options != nullptr) {
        SetOptions(pass_options);
      }
    }

    ~PerfAnalysisInfrastructure() {
      if (arena_ != nullptr) {
        arena_->SetInstrumentOff();
      }
    }

    /**
     * @brief Sets the list of metrics according to the options provided by the command line.
     * @details Calling this method on an instance is required before starting an Analysis or
     * a Collection, otherwise no metric will be set.
     * @param options The options data structure holding the list of user requested metrics.
     */
    void SetOptions(const PassManagerOptions* options);

    /**
     * @brief Some metrics require an arena allocator pointer in order to work.
     * This method should be called before SetOptions in case we want to enable
     * arena related metrics (memory-consumption).
     * @param arena The arena that will be analyzed by the Perf. Analysis Infrastructure.
     */
    void SetArena(ArenaAllocator* arena) {
      arena_ = arena;
      arena_->SetInstrumentOn();
      arena_bytes_allocated_.SetArena(arena);
      arena_num_allocations_.SetArena(arena);
    }

    bool ShouldInstrument() const {
      return metrics_.size() > 0u;
    }

    /**
     * @brief Starts an analysis. An analysis will be represented as an output file, holding
     * one or several metric collections. It should be stopped with StopAnalysis.
     * @param analysis_name The name of the analysis. This name will be used to identify
     * the analysis for later use.
     * @sa StopAnalysis.
     */
    void StartAnalysis(const std::string& analysis_name);

    /**
     * @brief Starts a metric collection. A collection represents an instrumentation of one or
     * several metrics. It should be stopped with StopCollection.
     * @details A collection must be part of an analysis, therefore at least one call to
     * StartAnalysis should be done before calling this method.
     * @param analysis_name The identifier of the analysis corresponding to this collection.
     * @param collection_name The name of this metric collection. It must be unique throughout
     * the analysis.
     * @param data The optional data we want to pass to the collection. By default, it is nullptr.
     */
    void StartCollection(const std::string& analysis_name,
                         const std::string& collection_name,
                         void* data = nullptr);

    /**
     * @brief Stops a metric collection. It must be preceded by a StartAnalysis and a
     * StartCollection calls.
     * @param analysis_name The identifier of the analysis corresponding to this collection.
     * @param collection_name The name of the metric collection that was started. It must be
     * unique throughout the analysis.
     * @param data The optional data we want to pass to the collection. By default, it is nullptr.
     */
    void StopCollection(const std::string& analysis_name,
                         const std::string& collection_name,
                         void* data = nullptr);

    /**
     * @brief Stops an analysis. This call must be preceded by a StartAnalysis with the
     * corresponding @p analysis_name. This call will dump a file with the collected data.
     * @details The file will not be dumped if there has been no collections, or no metrics
     * selected.
     * @param analysis_name The name of the analysis that needs to be stopped.
     * @param hash_name Whether the output file name needs to be hashed. This is handy when the
     * analysis name does not respect standards file-system restrictions, such as special
     * characters, or filename length.
     */
    void StopAnalysis(const std::string& analysis_name, bool hash_name);

    /**
     * @brief Facility to perform an Analysis. It is scope-based, which means that the analysis
     * starts when the instance is created, and it is stopped (and possibly dumped, see
     * StopAnalysis) as soon as the scope of the instance ends.
     */
    class ScopedAnalysis {
      public:
        /**
         * @brief Initiates an analysis.
         * @param name The name of the analysis.
         * @param hash_name Whether we want to hash the output file name (see StopAnalysis)
         * @param perf_analysis An instance to the parent PerfAnalysisInfrastructure class
         * needs to be attached to the instance. If it is nullptr, the ScopedAnalysis won't
         * perform an analysis.
         */
        ScopedAnalysis(const char* name, bool hash_name, PerfAnalysisInfrastructure* perf_analysis)
        : name_(name), hash_name_(hash_name), perf_analysis_(perf_analysis) {
          if (perf_analysis_ != nullptr) {
            perf_analysis_->StartAnalysis(name_);
          }
        }

        /**
         * @brief Ends the analysis. If the PerfAnalysisInfrastructure instance provided during the
         * construction of the instance is nullptr, it won't do anything.
         */
        ~ScopedAnalysis() {
          if (perf_analysis_ != nullptr) {
            perf_analysis_->StopAnalysis(name_, hash_name_);
          }
        }

      private:
        const char* name_;
        bool hash_name_;
        PerfAnalysisInfrastructure* perf_analysis_;
    };

    /**
     * @brief Facility to perform a metric collection. It is scope-based, which means the
     * collection starts when the instance is created, and it is stopped as soon as the scope
     * of the instance ends.
     */
    class ScopedCollection {
      public:
        /**
         * @brief Initiates a metric collection.
         * @param analysis_name This instance requires an already-started analysis name. This
         * represents the analysis identifier of the corresponding analysis.
         * @param collection_name This is the name of the collection performed by this instance.
         * @param perf_analysis An instance to the parent PerfAnalysisInfrastructure class
         * needs to be attached to the instance. If it is nullptr, the ScopedCollection won't
         * perform a metric collection.
         * @param data The optional data we can pass to the metrics. By default, it is nullptr.
         */
        ScopedCollection(const char* analysis_name, const char* collection_name,
               PerfAnalysisInfrastructure* perf_analysis, void* data = nullptr) :
        analysis_name_(analysis_name),
        collection_name_(collection_name),
        optional_data_(data),
        perf_analysis_(perf_analysis) {
          if (perf_analysis_ != nullptr) {
            perf_analysis_->StartCollection(analysis_name_,
                                            collection_name_,
                                            optional_data_);
          }
        }

        /**
         * @brief Ends the metric collection. If the PerfAnalysisInfrastructure
         * instance provided during the construction of the instance is
         * nullptr, it won't do anything.
         */
        ~ScopedCollection() {
          if (perf_analysis_ != nullptr) {
            perf_analysis_->StopCollection(analysis_name_, collection_name_, optional_data_);
          }
        }

      private:
        const char* analysis_name_;
        const char* collection_name_;
        void* optional_data_;
        PerfAnalysisInfrastructure* perf_analysis_;
    };

  private:
    /**
     * @brief This data structure holds the metrics data of an analysis, as well
     * as the order of the performed collections.
     */
    struct AnalysisData {
      std::unordered_map<std::string, std::vector<PassData<uint64_t>>> collections_;
      std::vector<std::string> collection_order_;
    };

    /**
     * @brief Allocate space in the AnalysisData structure to hold metrics collection data.
     * @param data The data structure which corresponds to the new allocated space.
     * @param collection_name The name of the collection we want to prepare.
     * @return Returns the allocated vector.
     */
    std::vector<PassData<uint64_t>>&
    PrepareCollection(AnalysisData& data, const std::string& collection_name);

    /**
     * @brief Stores the results of the metrics in a file in a csv format.
     * @details This method should be called only after EndPassInstrumentation is called.
     * @param analysis_name The name of the analysis to dump.
     * @param hash_name Whether we want to hash the name. See StopAnalysis for more information.
     * @sa StopAnalysis.
     */
    void Dump(const std::string& analysis_name, bool hash_name);

    ArenaAllocator* arena_;

    // Compile-time related metrics.
    Timing timing_;
    TSClock tsclock_;

    // Memory-consumption related metrics.
    MallocStats malloc_stats_;
    ArenaBytesAllocated arena_bytes_allocated_;
    ArenaNumAllocations arena_num_allocations_;
    const PassManagerOptions* pass_options_;

    // Pass-usage related metric.
    PassStats pass_stats_;

    std::vector<Metric<uint64_t>*> metrics_;  // Holds the activated metrics.

    // The pool of currently started analyses. There can be one or more analyses started at once.
    std::unordered_map<std::string, AnalysisData> analyses_pool_;
};
}  // namespace art.

#endif  // ART_OPT_INFRASTRUCTURE_INSTRUMENTATION_H_

#endif  // PERF_ANALYSIS_INFRASTRUCTURE
