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

#ifndef COMPILE_HEADER_H_
#define COMPILE_HEADER_H_

#include "key_values.h"

class CompileHeader {
  protected:
    KeysValues key_values;

  public:
    bool HasName() {
      return key_values.HasKey("name");
    }

    bool HasMethod() {
      return key_values.HasKey("method");
    }

    const char* GetName() {
      const std::vector<Value>& values = key_values.GetValues("name");

      // Suppose we have a single one.
      if (values.size() == 1) {
        const Value& value = values[0];

        if (value.type != String || value.type != Identifier) {
          return nullptr;
        } else {
          return values[0].element.name;
        }
      } else {
        return 0;
      }
    }
};
#endif
