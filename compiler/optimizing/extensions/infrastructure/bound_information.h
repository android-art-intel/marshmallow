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


#ifndef VENDOR_INTEL_ART_EXTENSION_OPT_INFRASTRUCTURE_BOUND_INFORMATION_H_
#define VENDOR_INTEL_ART_EXTENSION_OPT_INFRASTRUCTURE_BOUND_INFORMATION_H_

namespace art {

// Forward declarations.
class HConstant;
class HInductionVariable;

/**
 * @brief This structure is used to keep bound information.
 * @see ComputeBoundInformation which outlines the algorithm used to compute this.
 * @details This is not guaranteed to be filled completely.
 * - loop_biv may be null.
 * - constant_bound is only valid if is_bound_constant is true.
 * - comparison_condition is only valid if is_simple_count_up or is_simple_count_down are true.
 * - num_iterations, biv_start_value, and biv_end_value are only valid if knowIterations is true.
 */
struct HLoopBoundInformation {
  HLoopBoundInformation() :
    loop_biv_(nullptr), is_bound_constant_(false), constant_bound_(nullptr),
    is_simple_count_up_(false), is_simple_count_down_(false),
    comparison_condition_(0), num_iterations_(-1), biv_start_value_(0),
    biv_end_value_(0) {
    }

  /**
   * @brief Used to store the loop's BIV.
   * @details In order for this to exist, the loop must have only one backedge and exit.
   * Additionally this BIV must be used in the condition that iterates the loop.
   */
  HInductionVariable* loop_biv_;

  /**
   * @brief Whether the loop's bound is constant.
   */
  bool is_bound_constant_;

  /**
   * @brief The constant of the loop bound. Only valid if is_bound_constant is true.
   */
  HConstant* constant_bound_;

  /**
   * @brief Whether the loop is a simple count up loop.
   * @details The IV must increment in positive manner, the loop condition must use BIV
   * and constant/invariant.
   */
  bool is_simple_count_up_;

  /**
   * @brief Whether the loop is a simple count down loop.
   * @details The IV must increment in negative manner, the loop condition must use BIV
   * and constant/invariant.
   */
  bool is_simple_count_down_;

  /**
   * @brief Contains the comparison condition as an OP_IF bytecode.
   * @details Only valid if is_simple_count_up or is_simple_count_down are true.
   */
  int comparison_condition_;

  /**
   * @brief The number of iterations through the loop.
   * @details If this value is -1, then number of iterations is not known.
   */
  int64_t num_iterations_;

  /**
   * @brief The start value of the induction variable.
   */
  int64_t biv_start_value_;

  /**
   * @brief The end value of the induction variable.
   */
  int64_t biv_end_value_;
};
}  // namespace art

#endif  // VENDOR_INTEL_ART_EXTENSION_OPT_INFRASTRUCTURE_BOUND_INFORMATION_H_
