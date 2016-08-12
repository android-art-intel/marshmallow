#
# Copyright (C) 2011 The Android Open Source Project
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
# Modified by Intel Corporation
#
#

include $(VENDOR_ART_PATH)/build/Android.common_build.mk

ART_CPPLINT := $(VENDOR_ART_PATH)/tools/cpplint.py
ART_CPPLINT_FILTER := --filter=-whitespace/line_length,-build/include,-readability/function,-readability/streams,-readability/todo,-runtime/references,-runtime/sizeof,-runtime/threadsafe_fn,-runtime/printf,-build/header_guard
ART_CPPLINT_SRC := $(shell find $(VENDOR_ART_PATH) -name "*.h" -o -name "*$(ART_CPP_EXTENSION)" | grep -v compiler/llvm/generated/ | grep -v runtime/elf\.h | grep -v compiler/dex/quick/extension/)

# "mm cpplint-art" to verify we aren't regressing
.PHONY: cpplint-art
cpplint-art:
	$(ART_CPPLINT) $(ART_CPPLINT_FILTER) $(ART_CPPLINT_SRC)

# "mm cpplint-art-all" to see all warnings
.PHONY: cpplint-art-all
cpplint-art-all:
	$(ART_CPPLINT) $(ART_CPPLINT_SRC)

OUT_CPPLINT := $(TARGET_COMMON_OUT_ROOT)/cpplint

ART_CPPLINT_TARGETS :=

define declare-art-cpplint-target
art_cpplint_file := $(1)
art_cpplint_touch := $$(OUT_CPPLINT)/$$(subst /,__,$$(art_cpplint_file))

$$(art_cpplint_touch): $$(art_cpplint_file) $(ART_CPPLINT) $(VENDOR_ART_PATH)/build/Android.cpplint.mk
	$(hide) $(ART_CPPLINT) $(ART_CPPLINT_FILTER) $$<
	@mkdir -p $$(dir $$@)
	$(hide) touch $$@

ART_CPPLINT_TARGETS += $$(art_cpplint_touch)
endef

$(foreach file, $(ART_CPPLINT_SRC), $(eval $(call declare-art-cpplint-target,$(file))))
#$(info $(call declare-art-cpplint-target,$(firstword $(ART_CPPLINT_SRC))))

include $(CLEAR_VARS)
LOCAL_MODULE := cpplint-art-phony
LOCAL_MODULE_TAGS := optional
LOCAL_ADDITIONAL_DEPENDENCIES := $(ART_CPPLINT_TARGETS)
include $(BUILD_PHONY_PACKAGE)
