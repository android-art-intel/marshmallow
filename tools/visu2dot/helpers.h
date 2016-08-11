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

#ifndef HELPERS_H_
#define HELPERS_H_

#include "key_values.h"

class CompleteInsn {
  protected:
    std::string opcode;
    Value* value;
    std::vector<std::string>* registers;

  public:
    CompleteInsn(const std::string& n, Value* v = nullptr) :
      opcode(n), value(v), registers(nullptr) {
    }

    Value* GetValue() const {
      return value;
    }

    const std::string& GetOpcode() {
      return opcode;
    }

    void AddRegisterList(std::vector<std::string>* list) {
      registers = list;
    }

    std::vector<std::string>* GetRegisters() {
      return registers;
    };
};

void ReformatName(char* name) {
  while (*name) {
    if (!isalnum(*name)) {
      *name = '_';
    }
    name++;
  }
}

#endif
