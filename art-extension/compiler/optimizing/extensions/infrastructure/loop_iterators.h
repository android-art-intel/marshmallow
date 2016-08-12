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

#ifndef ART_OPT_INFRASTRUCTURE_LOOP_ITERATORS_H_
#define ART_OPT_INFRASTRUCTURE_LOOP_ITERATORS_H_

#include <queue>

#include "loop_information.h"

namespace art {

/**
 * @class HOutToInLoopIterator
 * @brief The Loop Iterator that goes from outer loops to inner loop.
 */
class HOutToInLoopIterator : public ValueObject {
 public:
  explicit HOutToInLoopIterator(HLoopInformation_X86* loop) : loop_(loop) {
  }

  void Advance() {
    // First handle inner loop.
    HLoopInformation_X86* tmp = loop_->GetInner();
    if (tmp != nullptr) {
      loop_ = tmp;
      return;
    }

    // Now is more complex, we are looking for a sibling or a parent with a sibling.
    HLoopInformation_X86* current = loop_;
    while (current->GetNextSibling() == nullptr && current->GetParent() != nullptr) {
      current = current->GetParent();
    }

    // Are we all on top and have no sibling?
    if (current->GetNextSibling() != nullptr) {
      loop_ = current->GetNextSibling();
      return;
    }

    // We are done if we arrive here.
    loop_ = nullptr;
  }

  bool Done() const {
    return loop_ == nullptr;
  }

  HLoopInformation_X86* Current() const {
    return loop_;
  }

 protected:
  HLoopInformation_X86* loop_;

 private:
  DISALLOW_COPY_AND_ASSIGN(HOutToInLoopIterator);
};

/**
 * @class HInToOutLoopIterator
 * @brief The Loop Iterator that goes from inner loops to outer loop.
 */
class HInToOutLoopIterator : public ValueObject {
 public:
  explicit HInToOutLoopIterator(HLoopInformation_X86* loop) {
    Populate(loop);
  }

  void Populate(HLoopInformation_X86* loop) {
    for (HOutToInLoopIterator iter(loop); iter.Done() == false; iter.Advance()) {
      HLoopInformation_X86* current = iter.Current();
      list.push(current);
    }
  }

  bool Done() const {
    return list.empty();
  }

  HLoopInformation_X86* Current() const {
    return list.top();
  }

  void Advance() {
    list.pop();
  }

 protected:
  std::stack<HLoopInformation_X86*> list;

 private:
  DISALLOW_COPY_AND_ASSIGN(HInToOutLoopIterator);
};

/**
 * @class HOnlyInnerLoopIterator
 * @brief The Loop Iterator that only iterates on inner loops.
 */
class HOnlyInnerLoopIterator : public ValueObject {
 public:
  explicit HOnlyInnerLoopIterator(HLoopInformation_X86* loop) {
    Populate(loop);
  }

  void Populate(HLoopInformation_X86* loop) {
    for (HOutToInLoopIterator iter(loop); iter.Done() == false; iter.Advance()) {
      HLoopInformation_X86* current = iter.Current();
      if (current->IsInner()) {
        list.push(current);
      }
    }
  }

  bool Done() const {
    return list.empty();
  }

  HLoopInformation_X86* Current() const {
    return list.front();
  }

  void Advance() {
    list.pop();
  }

 protected:
  std::queue<HLoopInformation_X86*> list;

 private:
  DISALLOW_COPY_AND_ASSIGN(HOnlyInnerLoopIterator);
};

}  // namespace art

#endif  // ART_OPT_INFRASTRUCTURE_LOOP_ITERATORS_H_
