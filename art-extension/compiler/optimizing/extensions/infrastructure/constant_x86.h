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


#ifndef ART_COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_CONSTANT_X86_H_
#define ART_COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_CONSTANT_X86_H_

#include "nodes.h"

namespace art {

class HConstant_X86 : public ArenaObject<kArenaAllocMisc> {
 protected:
  bool is_wide_;
  bool is_fp_;
  HConstant* constant_;

 public:
  HConstant_X86(HConstant* constant, bool is_wide, bool is_fp) :
      is_wide_(is_wide), is_fp_(is_fp), constant_(constant) {
        // Do not allow the creation of a nullptr constant...
        DCHECK(constant != nullptr);
  }

  bool IsWide() const {
    return (constant_->IsDoubleConstant() || constant_->IsLongConstant());
  }

  bool IsFP() const {
    return (constant_->IsDoubleConstant() || constant_->IsFloatConstant());
  }

  /**
   * @brief Used to Get the loop increment (BIV must be non-FP typed).
   * @return Returns the increment.
   */
  int64_t GetValue() const {
    DCHECK(IsFP() == false);
    if (IsWide()) {
      HLongConstant* tmp = constant_->AsLongConstant();
      DCHECK(tmp != nullptr);
      return tmp->GetValue();
    } else {
      HIntConstant* tmp = constant_->AsIntConstant();
      DCHECK(tmp != nullptr);
      return tmp->GetValue();
    }
  }

  /**
   * @brief Used to Get the FP loop increment(BIV must be FP typed).
   * @return Returns the increment.
   */
  double GetFPValue() const {
    DCHECK(IsFP());
    if (IsWide()) {
      HDoubleConstant* tmp = constant_->AsDoubleConstant();
      DCHECK(tmp != nullptr);
      return tmp->GetValue();
    } else {
      HFloatConstant* tmp = constant_->AsFloatConstant();
      DCHECK(tmp != nullptr);
      return tmp->GetValue();
    }
  }

  bool IsOne() const {
    return constant_->IsOne();
  }

  bool IsDoubleConstant() const {
    return constant_->IsDoubleConstant();
  }

  bool IsIntConstant() const {
    return constant_->IsIntConstant();
  }

  bool IsLongConstant() const {
    return constant_->IsLongConstant();
  }

  bool IsFloatConstant() const {
    return constant_->IsFloatConstant();
  }
};

}  // namespace art
#endif  // ART_COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_CONSTANT_X86_H_

