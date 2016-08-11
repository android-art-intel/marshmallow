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


#ifndef VENDOR_INTEL_ART_EXTENSION_OPT_INFRASTRUCTURE_OPTION_CONTENT_H_
#define VENDOR_INTEL_ART_EXTENSION_OPT_INFRASTRUCTURE_OPTION_CONTENT_H_

namespace art {



/**
 * Structure abstracting pass option values, which can be of type string or integer.
 */
class OptionContent {
 public:
  explicit OptionContent(const std::string& value) :
    type(kString), s(value) {}

  explicit OptionContent(int64_t value) :
    type(kInteger), i(value) {}

  /**
   * Allows for a transparent display of the option content.
   */
  friend std::ostream& operator<<(std::ostream& out, const OptionContent& option) {
    if (option.type == kString) {
      out << option.s;
    } else {
      out << option.i;
    }

    return out;
  }

  const std::string& AsString() const {
    DCHECK(type == kString);
    return s;
  }

  int64_t AsInt() const {
    DCHECK(type == kInteger);
    return i;
  }

  /**
   * Describes the type of parameters allowed as option values.
   */
  enum OptionType {
    kString = 0,
    kInteger
  };

 private:
  OptionType type;
  std::string s;
  int64_t i;
  static constexpr size_t kOptionStringMaxLength = 2048u;
};
}  // namespace art

#endif  // VENDOR_INTEL_ART_EXTENSION_OPT_INFRASTRUCTURE_OPTION_CONTENT_H_
