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


#ifndef ART_OPT_INFRASTRUCTURE_COMPILER_OPTIONS_H_
#define ART_OPT_INFRASTRUCTURE_COMPILER_OPTIONS_H_

#include "driver/compiler_options.h"

namespace art {

#define COMPILER_OPTIONS_X86(X) static_cast<const CompilerOptions_X86*>(X)

class PassManagerOptions;

class CompilerOptions_X86 : public CompilerOptions {
 public:
  CompilerOptions_X86(CompilerOptions::CompilerFilter compiler_filter,
                      size_t huge_method_threshold,
                      size_t large_method_threshold,
                      size_t small_method_threshold,
                      size_t tiny_method_threshold,
                      size_t num_dex_methods_threshold,
                      size_t inline_depth_limit,
                      size_t inline_max_code_units,
                      size_t stop_compiling_after,
                      size_t stop_optimizing_after,
                      size_t quick_disable_opt_mask,
                      bool cond_compilation,
                      bool include_patch_information,
                      double top_k_profile_threshold,
                      bool debuggable,
                      bool generate_debug_info,
                      bool implicit_null_checks,
                      bool implicit_so_checks,
                      bool implicit_suspend_checks,
                      bool compile_pic,
                      const std::vector<std::string>* verbose_methods,
                      PassManagerOptions* pass_manager_options,
                      std::ostream* init_failure_output,
                      bool abort_on_hard_verifier_failure,
                      bool aggressive_non_debuggable = false) :
      CompilerOptions(compiler_filter,
                      huge_method_threshold,
                      large_method_threshold,
                      small_method_threshold,
                      tiny_method_threshold,
                      num_dex_methods_threshold,
                      inline_depth_limit,
                      inline_max_code_units,
                      stop_compiling_after,
                      stop_optimizing_after,
                      quick_disable_opt_mask,
                      cond_compilation,
                      include_patch_information,
                      top_k_profile_threshold,
                      debuggable,
                      generate_debug_info,
                      implicit_null_checks,
                      implicit_so_checks,
                      implicit_suspend_checks,
                      compile_pic, verbose_methods,
                      pass_manager_options,
                      init_failure_output,
                      abort_on_hard_verifier_failure),
                      aggressive_non_debuggable_(aggressive_non_debuggable) {
  }

  bool GetAggressiveNonDebuggable() const {
    return aggressive_non_debuggable_;
  }

 private:
  bool aggressive_non_debuggable_;

  DISALLOW_COPY_AND_ASSIGN(CompilerOptions_X86);
};

}  // namespace art

#endif  // ART_OPT_INFRASTRUCTURE_COMPILER_OPTIONS_H_
