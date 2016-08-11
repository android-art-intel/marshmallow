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
 *
 * Modified by Intel Corporation
 *
 */

#ifndef ART_COMPILER_OPTIMIZING_CODE_GENERATOR_UTILS_H_
#define ART_COMPILER_OPTIMIZING_CODE_GENERATOR_UTILS_H_

#include <cstdint>

namespace art {

class Label;
class HCondition;

// Computes the magic number and the shift needed in the div/rem by constant algorithm, as out
// arguments `magic` and `shift`
void CalculateMagicAndShiftForDivRem(int64_t divisor, bool is_long, int64_t* magic, int* shift);

// Is it valid to reverse the condition? Uses the values supplied to
// GenerateTestAndBranch() in instruction generators.
bool CanReverseCondition(Label* always_true_target,
                         Label* false_target,
                         HCondition* condition = nullptr);

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_CODE_GENERATOR_UTILS_H_
