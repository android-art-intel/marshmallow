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

#ifndef KEY_VALUES_H_
#define KEY_VALUES_H_

#include <sstream>
#include <iostream>
#include <string.h>
#include <vector>
#include <map>

enum ValueType {
  String,
  Integer,
  Float,
  Identifier,
};

struct Value {
  union {
    char* name;
    int64_t i;
    double f;
  } element;
  ValueType type;

  explicit Value(char* n) : type(String) {
    element.name = n;
  }

  explicit Value(int64_t i) : type(Integer) {
    element.i = i;
  }

  explicit Value(double f) : type(Float) {
    element.f = f;
  }

  void FillString(std::ostringstream& oss) const {
    switch (type) {
      case Integer:
        oss << element.i;
        break;
      case Float:
        oss << element.f;
        break;
      case Identifier:
      case String:
        oss << element.name;
        break;
    }
  }
};

struct KeyValues {
  std::string name;
  std::vector<Value> values;

  explicit KeyValues(char* s) :
    name(s) {
  }

  KeyValues(char* s, const Value& v) :
    name(s) {
      values.push_back(v);
  }

  KeyValues(char* s, const std::vector<Value>& v) :
    name(s), values(v) {
  }

  KeyValues(char* s, const std::vector<Value>* v) :
    name(s) {
      size_t size = v->size();
      for (size_t i = 0; i < size; i++) {
        Value value = v->at(i);
        values.push_back(value);
      }
  }
};

class KeysValues {
  protected:
    std::map<std::string, std::vector<Value>> key_values;

  public:
    void PushValue(const std::string& key, const Value& v) {
      key_values[key].push_back(v);
    }

    bool HasKey(const std::string& key) {
      return key_values.find(key) != key_values.end();
    }

    const std::vector<Value>& GetValues(const std::string& name) {
      return key_values[name];
    }

    void PushValue(const KeyValues* key_value) {
      const std::vector<Value>& new_values = key_value->values;
      const std::string& name = key_value->name;
      std::vector<Value>& values = key_values[name];
      for (std::vector<Value>::const_iterator it = new_values.begin();
                                              it != new_values.end();
                                              it++) {
        const Value& value = *it;
        values.push_back(value);
      }
    }
};
#endif
