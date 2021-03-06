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

LOCAL_PATH := $(call my-dir)

include $(VENDOR_ART_PATH)/build/Android.executable.mk

DEX2OAT_SRC_FILES := \
	dex2oat.cc

# TODO: Remove this when the framework (installd) supports pushing the
# right instruction-set parameter for the primary architecture.
ifneq ($(filter ro.zygote=zygote64,$(PRODUCT_DEFAULT_PROPERTY_OVERRIDES)),)
  dex2oat_target_arch := 64
else
  dex2oat_target_arch := 32
endif

# We need to explcitly give the arch, as giving 'both' will make the
# build-art-executable rule compile dex2oat for 64bits.
ifeq ($(HOST_PREFER_32_BIT),true)
  dex2oat_host_arch := 32
else
  dex2oat_host_arch := both
endif

ifeq ($(ART_BUILD_TARGET_NDEBUG),true)
  $(eval $(call build-art-executable,dex2oat,$(DEX2OAT_SRC_FILES),libcutils libart-compiler,$(VENDOR_ART_PATH)/compiler,target,ndebug,$(dex2oat_target_arch)))
endif
ifeq ($(ART_BUILD_TARGET_DEBUG),true)
  $(eval $(call build-art-executable,dex2oat,$(DEX2OAT_SRC_FILES),libcutils libartd-compiler,$(VENDOR_ART_PATH)/compiler,target,debug,$(dex2oat_target_arch)))
endif

# We always build dex2oat and dependencies, even if the host build is otherwise disabled, since they are used to cross compile for the target.
ifeq ($(ART_BUILD_HOST_NDEBUG),true)
  $(eval $(call build-art-executable,dex2oat,$(DEX2OAT_SRC_FILES),libcutils libart-compiler libziparchive-host,$(VENDOR_ART_PATH)/compiler,host,ndebug,$(dex2oat_host_arch)))
endif
ifeq ($(ART_BUILD_HOST_DEBUG),true)
  $(eval $(call build-art-executable,dex2oat,$(DEX2OAT_SRC_FILES),libcutils libartd-compiler libziparchive-host,$(VENDOR_ART_PATH)/compiler,host,debug,$(dex2oat_host_arch)))
endif
