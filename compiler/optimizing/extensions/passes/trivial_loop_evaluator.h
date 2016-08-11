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


#ifndef ART_COMPILER_OPTIMIZING_TRIVIAL_LOOP_EVALUATOR_H_
#define ART_COMPILER_OPTIMIZING_TRIVIAL_LOOP_EVALUATOR_H_

#include "driver/dex_compilation_unit.h"
#include "ext_utility.h"
#include "nodes.h"
#include "optimization_x86.h"

#include <iostream>

namespace art {

/** Structure mapping the instructions to their evaluated value. */
typedef SafeMap<HInstruction*, ConstantValue> ValuesHolder;

/** Structure mapping the instructions to the constant to write back to the graph. */
typedef SafeMap<HInstruction*, HConstant*> ValuesToUpdate;

/**
 * @brief Trivial Loop Evaluator is an optimization pass which tries to evaluate the loops of
 * a HGraph when possible. If so, it removes the loop of the graph, writes back the evaluated
 * results and updates their users.
 */
class TrivialLoopEvaluator : public HOptimization_X86 {
  public:
    explicit TrivialLoopEvaluator(HGraph* graph, const DexCompilationUnit& c_unit,
                                  OptimizingCompilerStats* stats = nullptr)
      : HOptimization_X86(graph, true, "trivial_loop_evaluator", stats), c_unit_(c_unit) {}
    virtual ~TrivialLoopEvaluator() {}

    void Run() OVERRIDE;

  private:
    /**
     * @brief Narrows down the scope of application of TLE to the loops having
     * specific properties.
     * @param loop The HLoopInformation_X86 loop this method will check.
     * @return True if the loop is a valid candidate for TLE, or false otherwise.
     */
    bool LoopGate(HLoopInformation_X86* loop);

    /**
     * @brief Tries to statically evaluate the given loop.
     * @param loop The HLoopInformation_X86 loop this method will try to evaluate
     * statically.
     * @param values_holder The structure which will hold all the writes resulting of the
     * evaluation of the loop. It should be empty when calling this method.
     * @return True if TLE successfully evaluated the loop, or false otherwise.
     */
    bool EvaluateLoop(HLoopInformation_X86* loop, ValuesHolder& values_holder);

    /**
     * @brief This method decides which values we should write back to the graph from the evaluation.
     * @param loop The HLoopInformation_X86 structure corresponding to the loop which has been
     * evaluated.
     * @param values_holder The structure holding all the results of the evaluation of @p loop.
     * @param to_update The result structure holding HConstant values we need
     * to write back. This structure should be empty when calling this method.
     */
    void SelectRegistersToUpdate(HLoopInformation_X86* loop, const ValuesHolder& values_holder,
                                 ValuesToUpdate& to_update);

    /**
     * @brief This method deletes all the instructions in the loop's only BB,
     * it replaces them by the constant values previously evaluated by TLE, and deletes
     * the backedge of the given loop.
     * @param loop The HLoopInformation_X86 loop previously evaluated successfully by TLE.
     * @param to_update The result structure holding HConstant values we need to write back.
     */
    void UpdateRegisters(HLoopInformation_X86* loop, const ValuesToUpdate& to_update);

    /** Maximum number of iterations in an evaluable loop. Beyond this limits, loops are considered
    * too costly to be statically evaluated. */
    static constexpr int64_t kLoopEvalMaxIter = 1000;

    const DexCompilationUnit& c_unit_;

    /** Copy and assignment are not allowed. */
    DISALLOW_COPY_AND_ASSIGN(TrivialLoopEvaluator);
};

}  // namespace art.

#endif  // ART_COMPILER_OPTIMIZING_TRIVIAL_LOOP_EVALUATOR_H_

