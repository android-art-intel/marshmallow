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


#ifndef ART_COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_CLONING_H_
#define ART_COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_CLONING_H_

#include "graph_x86.h"
#include "nodes.h"

namespace art {

/**
 * @brief Used to clone instructions.
 * @details Note that this does not copy users from source. Thus, instructions
 * are not valid until proper inputs are added.
 */
class HInstructionCloner : public HGraphVisitor {
 public:
  // Mapping from Environment to instruction containing the environment.
  typedef SafeMap<HEnvironment*, HInstruction*> EnvToInstrMap;

  /**
   * Create an instruction cloner.
   * @param graph Graph containing the instructions to clone.
   * @param env_to_instr Optional mapping of environments->instructions that
   * will be updated for cloned instructions.  Pass 'nullptr' if not needed.
   * @param enable_cloning 'false' to see if cloning is possible, 'true' to clone.
   * @param use_cloned_inputs 'true' if cloned instructions should use already
   * cloned inputs.
   * @param allow_overwrite Allow one instruction to have multiple clones. It is deactivated
   * by default because it is safer to keep one single mapping, to prevent potential memory
   * leaks. However, there are certain situations where allowing such feature becomes handy.
   * It is the case, for example, of Loop Unrolling.
   */
  HInstructionCloner(HGraph_X86* graph,
                     EnvToInstrMap* env_to_instr,
                     bool enable_cloning = true,
                     bool use_cloned_inputs = true,
                     bool allow_overwrite = false) :
      HGraphVisitor(graph),
      cloning_enabled_(enable_cloning),
      all_cloned_okay_(true),
      use_cloned_inputs_(use_cloned_inputs),
      allow_overwrite_(allow_overwrite),
      arena_(graph->GetArena()),
      debug_name_failed_clone_(nullptr),
      env_to_instr_(env_to_instr) {}

  void VisitInstruction(HInstruction* instruction) OVERRIDE {
    // If this instruction does not have a visitor, assume that it was not cloned correctly.
    all_cloned_okay_ = false;
    debug_name_failed_clone_ = instruction->DebugName();
  }

  void AddOrUpdateCloneManually(HInstruction* original, HInstruction* clone) {
    DCHECK(original != nullptr);
    DCHECK(clone != nullptr);
    orig_to_clone_.Overwrite(original, clone);
  }

  void AddCloneManually(HInstruction* original, HInstruction* clone) {
    DCHECK(original != nullptr);
    DCHECK(clone != nullptr);
    orig_to_clone_.Put(original, clone);
  }

  HInstruction* GetClone(HInstruction* source) const {
    if (orig_to_clone_.count(source) != 0) {
      HInstruction* clone = orig_to_clone_.Get(source);
      DCHECK(clone != nullptr);
      return clone;
    } else {
      return nullptr;
    }
  }

  void OverwriteAllowanceCheck(HInstruction* instr) const {
    if (!allow_overwrite_) {
      DCHECK(orig_to_clone_.find(instr) == orig_to_clone_.end());
    }
  }

  bool AllOkay() const;

  const char* GetDebugNameForFailedClone() const {
    DCHECK(!all_cloned_okay_);
    return debug_name_failed_clone_;
  }

  HEnvironment* CloneEnvironment(HEnvironment* env);

  void VisitAdd(HAdd* instr) OVERRIDE;
  void VisitAnd(HAnd* instr) OVERRIDE;
  void VisitArrayGet(HArrayGet* instr) OVERRIDE;
  void VisitArrayLength(HArrayLength* instr) OVERRIDE;
  void VisitArraySet(HArraySet* instr) OVERRIDE;
  void VisitBooleanNot(HBooleanNot* instr) OVERRIDE;
  void VisitBoundsCheck(HBoundsCheck* instr) OVERRIDE;
  void VisitBoundType(HBoundType* instr) OVERRIDE;
  void VisitCheckCast(HCheckCast* instr) OVERRIDE;
  void VisitClinitCheck(HClinitCheck* instr) OVERRIDE;
  void VisitCompare(HCompare* instr) OVERRIDE;
  void VisitCondition(HCondition* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitDeoptimize(HDeoptimize* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitDiv(HDiv* instr) OVERRIDE;
  void VisitDivZeroCheck(HDivZeroCheck* instr) OVERRIDE;
  void VisitDoubleConstant(HDoubleConstant* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitEqual(HEqual* instr) OVERRIDE;
  void VisitExit(HExit* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitFloatConstant(HFloatConstant* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitGoto(HGoto* instr) OVERRIDE;
  void VisitGreaterThan(HGreaterThan* instr) OVERRIDE;
  void VisitGreaterThanOrEqual(HGreaterThanOrEqual* instr) OVERRIDE;
  void VisitIf(HIf* instr) OVERRIDE;
  void VisitInstanceFieldGet(HInstanceFieldGet* instr) OVERRIDE;
  void VisitInstanceFieldSet(HInstanceFieldSet* instr) OVERRIDE;
  void VisitInstanceOf(HInstanceOf* instr) OVERRIDE;
  void VisitIntConstant(HIntConstant* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitInvokeInterface(HInvokeInterface* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitInvokeStaticOrDirect(HInvokeStaticOrDirect* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitInvokeVirtual(HInvokeVirtual* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitLessThan(HLessThan* instr) OVERRIDE;
  void VisitLessThanOrEqual(HLessThanOrEqual* instr) OVERRIDE;
  void VisitLoadClass(HLoadClass* instr) OVERRIDE;
  void VisitLoadException(HLoadException* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitLoadLocal(HLoadLocal* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitLoadString(HLoadString* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitLocal(HLocal* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitLongConstant(HLongConstant* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitMemoryBarrier(HMemoryBarrier* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitMonitorOperation(HMonitorOperation* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitMul(HMul* instr) OVERRIDE;
  void VisitNeg(HNeg* instr) OVERRIDE;
  void VisitNewArray(HNewArray* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitNewInstance(HNewInstance* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitNot(HNot* instr) OVERRIDE;
  void VisitNotEqual(HNotEqual* instr) OVERRIDE;
  void VisitNullConstant(HNullConstant* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitNullCheck(HNullCheck* instr) OVERRIDE;
  void VisitOr(HOr* instr) OVERRIDE;
  void VisitParallelMove(HParallelMove* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitParameterValue(HParameterValue* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitPhi(HPhi* instr) OVERRIDE;
  void VisitRem(HRem* instr) OVERRIDE;
  void VisitReturn(HReturn* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitReturnVoid(HReturnVoid* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitShl(HShl* instr) OVERRIDE;
  void VisitShr(HShr* instr) OVERRIDE;
  void VisitStaticFieldGet(HStaticFieldGet* instr) OVERRIDE;
  void VisitStaticFieldSet(HStaticFieldSet* instr) OVERRIDE;
  void VisitStoreLocal(HStoreLocal* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitSub(HSub* instr) OVERRIDE;
  void VisitSuspend(HSuspend* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitSuspendCheck(HSuspendCheck* instr) OVERRIDE;
  void VisitTemporary(HTemporary* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitTestSuspend(HTestSuspend* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitThrow(HThrow* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitTypeConversion(HTypeConversion* instr) OVERRIDE;
  void VisitUShr(HUShr* instr) OVERRIDE;
  void VisitXor(HXor* instr) OVERRIDE;
  void VisitConstant(HConstant* instr) OVERRIDE { VisitInstruction(instr); }
  void VisitX86SelectValue(HX86SelectValue* instr) OVERRIDE;

 private:
  void GetInputsForUnary(HInstruction* instr, HInstruction** input_ptr) const;
  void GetInputsForBinary(HInstruction* instr,
                          HInstruction** lhs_ptr,
                          HInstruction** rhs_ptr) const;
  void GetInputsForTernary(HInstruction* instr,
                           HInstruction** input0_ptr,
                           HInstruction** input1_ptr,
                           HInstruction** input2_ptr) const;
  void GetInputsForQuaternary(HInstruction* instr,
                              HInstruction** input0_ptr,
                              HInstruction** input1_ptr,
                              HInstruction** input2_ptr,
                              HInstruction** input3_ptr) const;
  void CloneEnvironment(HInstruction* instr, HInstruction* clone);

  const bool cloning_enabled_;
  bool all_cloned_okay_;
  const bool use_cloned_inputs_;
  const bool allow_overwrite_;
  SafeMap<HInstruction*, HInstruction*> orig_to_clone_;
  ArenaAllocator* arena_;
  const char* debug_name_failed_clone_;
  EnvToInstrMap* env_to_instr_;
};

}  // namespace art

#endif  // ART_COMPILER_OPTIMIZING_EXTENSIONS_INFRASTRUCTURE_CLONING_H_
