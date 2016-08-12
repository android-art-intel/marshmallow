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

ifndef ART_ANDROID_COMMON_BUILD_VENDOR_MK
ART_ANDROID_COMMON_BUILD_VENDOR_MK = true

VENDOR_COMPILER_INCLUDES := \
  $(VENDOR_ART_PATH)/compiler \
  $(VENDOR_ART_PATH)/compiler/optimizing \
  $(VENDOR_ART_PATH)/compiler/optimizing/extensions \
  $(VENDOR_ART_PATH)/compiler/optimizing/extensions/infrastructure \
  $(VENDOR_ART_PATH)/compiler/optimizing/extensions/passes

# It is a bit of a sin to polute general includes with compiler includes
# but ART uses ART_C_INCLUDES everywhere. For sake of reducing conflicts,
# simply add all vendor compiler includes in the general includes.
ART_C_INCLUDES += $(VENDOR_COMPILER_INCLUDES)

TIP_ID = $(shell cd art-extension && git log | grep '^\s\+Change-Id:' \
  | head -1 | sed -r 's/^\s+Change-Id:\s*//' | head -c 6)

# To understand what weekly build is base for current build, we get all
# unique weekly build ids and number of their entries which corresponds to
# actual number of patches from weekly.
WWs = $(shell cd art-extension && git log | grep 'Extension-Weekly:' \
  | sed -r 's/^\s+Extension-Weekly:\s*//' | sort -r | uniq -c | sed -r 's/^\s*//' \
  | sed -r 's/\s/-/')

# Here we compare the actual number of patches from weekly builds current
# build contains with total number of patches in weekly. If these
# numbers are equal we know which weekly build is the base for current build.
WW=$(shell for val in ${WWs}; \
  do real_ww_patches="`sed -r 's/^([[:digit:]]+).*/\1/' <<< $$val`"; \
  supposed_ww_patches="`sed -r 's/[^.]*\.([[:digit:]]+)/\1/' <<< $$val`"; \
  get_week="`sed -r 's/[^-]*-([[:digit:]]+[a-z]*)\..*/\1/' <<< $$val`"; \
  if [[ $$real_ww_patches = $$supposed_ww_patches ]]; then echo $$get_week; break; fi done)

# Handle unexpected case.
ifeq (${WW},)
ART_EXTENSION_VERSION = UNKNOWN.${TIP_ID}
else
# Calculate number of patches were added to weekly build with number WW.
NUMBER_OF_PATCHES_ABOVE_WEEKLY = $(shell cd art-extension && git log \
  | sed -r '/^\s+Extension-Weekly:\s*${WW}/q' | grep '^\s\+Change-Id:' | head -n -1 | wc -l)

ART_EXTENSION_VERSION = \
  ${WW}.${NUMBER_OF_PATCHES_ABOVE_WEEKLY}.${TIP_ID}
endif

ART_HOST_CFLAGS += -DART_EXTENSION_VERSION=$(ART_EXTENSION_VERSION)
ART_TARGET_CFLAGS += -DART_EXTENSION_VERSION=$(ART_EXTENSION_VERSION)

endif
