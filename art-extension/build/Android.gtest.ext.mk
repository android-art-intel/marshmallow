#
# Copyright (C) 2015 Intel Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

ifeq ($(findstring loop_hierarchy_test,$(COMPILER_GTEST_COMMON_SRC_FILES)),)

  VENDOR_EXTENSIONS_FOLDER := compiler/optimizing/extensions

  COMPILER_GTEST_EXTENSION_SRC_FILES := \
    $(VENDOR_EXTENSIONS_FOLDER)/passes/loop_hierarchy_test.cc \
    $(VENDOR_EXTENSIONS_FOLDER)/passes/loop_peeling_test.cc \
    $(VENDOR_EXTENSIONS_FOLDER)/infrastructure/loops_iv_bounds_test.cc

  COMPILER_GTEST_COMMON_SRC_FILES += \
    $(COMPILER_GTEST_EXTENSION_SRC_FILES)

  # Although we added to the common files, add them to target and host as well because
  # the point where extensions makefile is included is after those get initialized
  # from the common list of files.
  TMP := $(COMPILER_GTEST_TARGET_SRC_FILES)
  COMPILER_GTEST_TARGET_SRC_FILES := \
    $(TMP) \
    $(COMPILER_GTEST_EXTENSION_SRC_FILES)

  TMP := $(COMPILER_GTEST_HOST_SRC_FILES)
  COMPILER_GTEST_HOST_SRC_FILES := \
    $(TMP) \
    $(COMPILER_GTEST_EXTENSION_SRC_FILES)

endif
