/*
 * Copyright (C) 2014 The Android Open Source Project
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
 *
 * Modified by Intel Corporation
 *
 */

#include "code_generator_x86_64.h"

#include "art_method.h"
#include "code_generator_utils.h"
#include "compiled_method.h"
#include "entrypoints/quick/quick_entrypoints.h"
#include "ext_alias.h"
#include "gc/accounting/card_table.h"
#include "intrinsics.h"
#include "intrinsics_x86_64.h"
#include "mirror/array-inl.h"
#include "mirror/class-inl.h"
#include "mirror/object_reference.h"
#include "thread.h"
#include "utils/assembler.h"
#include "utils/stack_checks.h"
#include "utils/x86_64/assembler_x86_64.h"
#include "utils/x86_64/managed_register_x86_64.h"

namespace art {

namespace x86_64 {

// Some x86_64 instructions require a register to be available as temp.
static constexpr Register TMP = R11;

static constexpr int kCurrentMethodStackOffset = 0;
static constexpr Register kMethodRegisterArgument = RDI;

static constexpr Register kCoreCalleeSaves[] = { RBX, RBP, R12, R13, R14, R15 };
static constexpr FloatRegister kFpuCalleeSaves[] = { XMM12, XMM13, XMM14, XMM15 };

static constexpr int kC2ConditionMask = 0x400;


#define __ reinterpret_cast<X86_64Assembler*>(codegen->GetAssembler())->

class NullCheckSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  explicit NullCheckSlowPathX86_64(HNullCheck* instruction) : instruction_(instruction) {}

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    __ Bind(GetEntryLabel());
    __ gs()->call(
        Address::Absolute(QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pThrowNullPointer), true));
    RecordPcInfo(codegen, instruction_, instruction_->GetDexPc());
  }

 private:
  HNullCheck* const instruction_;
  DISALLOW_COPY_AND_ASSIGN(NullCheckSlowPathX86_64);
};

class DivZeroCheckSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  explicit DivZeroCheckSlowPathX86_64(HDivZeroCheck* instruction) : instruction_(instruction) {}

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    __ Bind(GetEntryLabel());
    __ gs()->call(
        Address::Absolute(QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pThrowDivZero), true));
    RecordPcInfo(codegen, instruction_, instruction_->GetDexPc());
  }

 private:
  HDivZeroCheck* const instruction_;
  DISALLOW_COPY_AND_ASSIGN(DivZeroCheckSlowPathX86_64);
};

class DivRemMinusOneSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  explicit DivRemMinusOneSlowPathX86_64(Register reg, Primitive::Type type, bool is_div)
      : cpu_reg_(CpuRegister(reg)), type_(type), is_div_(is_div) {}

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    __ Bind(GetEntryLabel());
    if (type_ == Primitive::kPrimInt) {
      if (is_div_) {
        __ negl(cpu_reg_);
      } else {
        __ xorl(cpu_reg_, cpu_reg_);
      }

    } else {
      DCHECK_EQ(Primitive::kPrimLong, type_);
      if (is_div_) {
        __ negq(cpu_reg_);
      } else {
        __ xorl(cpu_reg_, cpu_reg_);
      }
    }
    __ jmp(GetExitLabel());
  }

 private:
  const CpuRegister cpu_reg_;
  const Primitive::Type type_;
  const bool is_div_;
  DISALLOW_COPY_AND_ASSIGN(DivRemMinusOneSlowPathX86_64);
};

class SuspendCheckSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  explicit SuspendCheckSlowPathX86_64(HSuspendCheck* instruction, HBasicBlock* successor)
      : instruction_(instruction),
        locations_(instruction->GetLocations()),
        dex_pc_(instruction->GetDexPc()),
        successor_(successor) {}

  explicit SuspendCheckSlowPathX86_64(HSuspend* instruction, HBasicBlock* successor)
      : instruction_(instruction),
        locations_(instruction->GetLocations()),
        dex_pc_(instruction->GetDexPc()),
        successor_(successor) {}

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    CodeGeneratorX86_64* x64_codegen = down_cast<CodeGeneratorX86_64*>(codegen);
    __ Bind(GetEntryLabel());
    SaveLiveRegisters(codegen, locations_);
    __ gs()->call(Address::Absolute(QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pTestSuspend), true));
    RecordPcInfo(codegen, instruction_, dex_pc_);
    RestoreLiveRegisters(codegen, locations_);
    if (successor_ == nullptr) {
      __ jmp(GetReturnLabel());
    } else {
      __ jmp(x64_codegen->GetLabelOf(successor_));
    }
  }

  Label* GetReturnLabel() {
    DCHECK(successor_ == nullptr);
    return &return_label_;
  }

  HBasicBlock* GetSuccessor() const {
    return successor_;
  }

 private:
  HInstruction* instruction_;
  LocationSummary* locations_;
  uint32_t dex_pc_;
  HBasicBlock* const successor_;
  Label return_label_;

  DISALLOW_COPY_AND_ASSIGN(SuspendCheckSlowPathX86_64);
};

class BoundsCheckSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  BoundsCheckSlowPathX86_64(HBoundsCheck* instruction,
                            Location index_location,
                            Location length_or_base_location,
                            Location temp_length)
      : instruction_(instruction),
        index_location_(index_location),
        length_or_base_location_(length_or_base_location),
        temp_length_(temp_length) {}

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    __ Bind(GetEntryLabel());
    // Load the length if we need to do so.
    if (temp_length_.IsValid()) {
      CpuRegister len = temp_length_.AsRegister<CpuRegister>();
      CpuRegister base = length_or_base_location_.AsRegister<CpuRegister>();
      uint32_t offset = mirror::Array::LengthOffset().Uint32Value();
      __ movl(len, Address(base, offset));
      length_or_base_location_ = temp_length_;
    }

    // We're moving two locations to locations that could overlap, so we need a parallel
    // move resolver.
    InvokeRuntimeCallingConvention calling_convention;
    codegen->EmitParallelMoves(
        index_location_,
        Location::RegisterLocation(calling_convention.GetRegisterAt(0)),
        Primitive::kPrimInt,
        length_or_base_location_,
        Location::RegisterLocation(calling_convention.GetRegisterAt(1)),
        Primitive::kPrimInt);
    __ gs()->call(Address::Absolute(
        QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pThrowArrayBounds), true));
    RecordPcInfo(codegen, instruction_, instruction_->GetDexPc());
  }

 private:
  HBoundsCheck* const instruction_;
  const Location index_location_;
  Location length_or_base_location_;
  const Location temp_length_;

  DISALLOW_COPY_AND_ASSIGN(BoundsCheckSlowPathX86_64);
};

class LoadClassSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  LoadClassSlowPathX86_64(HLoadClass* cls,
                          HInstruction* at,
                          uint32_t dex_pc,
                          bool do_clinit)
      : cls_(cls), at_(at), dex_pc_(dex_pc), do_clinit_(do_clinit) {
    DCHECK(at->IsLoadClass() || at->IsClinitCheck());
  }

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    LocationSummary* locations = at_->GetLocations();
    CodeGeneratorX86_64* x64_codegen = down_cast<CodeGeneratorX86_64*>(codegen);
    __ Bind(GetEntryLabel());

    SaveLiveRegisters(codegen, locations);

    InvokeRuntimeCallingConvention calling_convention;
    __ movl(CpuRegister(calling_convention.GetRegisterAt(0)), Immediate(cls_->GetTypeIndex()));
    __ gs()->call(Address::Absolute((do_clinit_
          ? QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pInitializeStaticStorage)
          : QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pInitializeType)) , true));
    RecordPcInfo(codegen, at_, dex_pc_);

    Location out = locations->Out();
    // Move the class to the desired location.
    if (out.IsValid()) {
      DCHECK(out.IsRegister() && !locations->GetLiveRegisters()->ContainsCoreRegister(out.reg()));
      x64_codegen->Move(out, Location::RegisterLocation(RAX));
    }

    RestoreLiveRegisters(codegen, locations);
    __ jmp(GetExitLabel());
  }

 private:
  // The class this slow path will load.
  HLoadClass* const cls_;

  // The instruction where this slow path is happening.
  // (Might be the load class or an initialization check).
  HInstruction* const at_;

  // The dex PC of `at_`.
  const uint32_t dex_pc_;

  // Whether to initialize the class.
  const bool do_clinit_;

  DISALLOW_COPY_AND_ASSIGN(LoadClassSlowPathX86_64);
};

class LoadStringSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  explicit LoadStringSlowPathX86_64(HLoadString* instruction) : instruction_(instruction) {}

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    LocationSummary* locations = instruction_->GetLocations();
    DCHECK(!locations->GetLiveRegisters()->ContainsCoreRegister(locations->Out().reg()));

    CodeGeneratorX86_64* x64_codegen = down_cast<CodeGeneratorX86_64*>(codegen);
    __ Bind(GetEntryLabel());
    SaveLiveRegisters(codegen, locations);

    InvokeRuntimeCallingConvention calling_convention;
    __ movl(CpuRegister(calling_convention.GetRegisterAt(0)),
            Immediate(instruction_->GetStringIndex()));
    __ gs()->call(Address::Absolute(
        QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pResolveString), true));
    RecordPcInfo(codegen, instruction_, instruction_->GetDexPc());
    x64_codegen->Move(locations->Out(), Location::RegisterLocation(RAX));
    RestoreLiveRegisters(codegen, locations);
    __ jmp(GetExitLabel());
  }

 private:
  HLoadString* const instruction_;

  DISALLOW_COPY_AND_ASSIGN(LoadStringSlowPathX86_64);
};

class TypeCheckSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  TypeCheckSlowPathX86_64(HInstruction* instruction,
                          Location class_to_check,
                          Location object_class,
                          uint32_t dex_pc)
      : instruction_(instruction),
        class_to_check_(class_to_check),
        object_class_(object_class),
        dex_pc_(dex_pc) {}

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    LocationSummary* locations = instruction_->GetLocations();
    DCHECK(instruction_->IsCheckCast()
           || !locations->GetLiveRegisters()->ContainsCoreRegister(locations->Out().reg()));

    CodeGeneratorX86_64* x64_codegen = down_cast<CodeGeneratorX86_64*>(codegen);
    __ Bind(GetEntryLabel());
    SaveLiveRegisters(codegen, locations);

    // We're moving two locations to locations that could overlap, so we need a parallel
    // move resolver.
    InvokeRuntimeCallingConvention calling_convention;
    codegen->EmitParallelMoves(
        class_to_check_,
        Location::RegisterLocation(calling_convention.GetRegisterAt(0)),
        Primitive::kPrimNot,
        object_class_,
        Location::RegisterLocation(calling_convention.GetRegisterAt(1)),
        Primitive::kPrimNot);

    if (instruction_->IsInstanceOf()) {
      __ gs()->call(
          Address::Absolute(QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pInstanceofNonTrivial), true));
    } else {
      DCHECK(instruction_->IsCheckCast());
      __ gs()->call(
          Address::Absolute(QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pCheckCast), true));
    }
    RecordPcInfo(codegen, instruction_, dex_pc_);

    if (instruction_->IsInstanceOf()) {
      x64_codegen->Move(locations->Out(), Location::RegisterLocation(RAX));
    }

    RestoreLiveRegisters(codegen, locations);
    __ jmp(GetExitLabel());
  }

 private:
  HInstruction* const instruction_;
  const Location class_to_check_;
  const Location object_class_;
  const uint32_t dex_pc_;

  DISALLOW_COPY_AND_ASSIGN(TypeCheckSlowPathX86_64);
};

class DeoptimizationSlowPathX86_64 : public SlowPathCodeX86_64 {
 public:
  explicit DeoptimizationSlowPathX86_64(HInstruction* instruction)
      : instruction_(instruction) {}

  void EmitNativeCode(CodeGenerator* codegen) OVERRIDE {
    __ Bind(GetEntryLabel());
    SaveLiveRegisters(codegen, instruction_->GetLocations());
    __ gs()->call(
        Address::Absolute(QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pDeoptimize), true));
    DCHECK(instruction_->IsDeoptimize());
    HDeoptimize* deoptimize = instruction_->AsDeoptimize();
    uint32_t dex_pc = deoptimize->GetDexPc();
    codegen->RecordPcInfo(instruction_, dex_pc, this);
  }

 private:
  HInstruction* const instruction_;
  DISALLOW_COPY_AND_ASSIGN(DeoptimizationSlowPathX86_64);
};

#undef __
#define __ reinterpret_cast<X86_64Assembler*>(GetAssembler())->

inline Condition X86_64Condition(IfCondition cond) {
  switch (cond) {
    case kCondEQ: return kEqual;
    case kCondNE: return kNotEqual;
    case kCondLT: return kLess;
    case kCondLE: return kLessEqual;
    case kCondGT: return kGreater;
    case kCondGE: return kGreaterEqual;
    default:
      LOG(FATAL) << "Unknown if condition";
  }
  return kEqual;
}

void CodeGeneratorX86_64::GenerateStaticOrDirectCall(HInvokeStaticOrDirect* invoke,
                                                     Location temp) {
  // All registers are assumed to be correctly set up.

  Location callee_method = temp;  // For all kinds except kRecursive, callee will be in temp.
  switch (invoke->GetMethodLoadKind()) {
    case HInvokeStaticOrDirect::MethodLoadKind::kStringInit:
      // temp = thread->string_init_entrypoint
      __ gs()->movl(temp.AsRegister<CpuRegister>(),
                    Address::Absolute(invoke->GetStringInitOffset(), true));
      break;
    case HInvokeStaticOrDirect::MethodLoadKind::kRecursive:
      __ movq(temp.AsRegister<CpuRegister>(),
              Address(CpuRegister(RSP), kCurrentMethodStackOffset));
      break;
    case HInvokeStaticOrDirect::MethodLoadKind::kDirectAddress:
      __ movq(temp.AsRegister<CpuRegister>(), Immediate(invoke->GetMethodAddress()));
      break;
    case HInvokeStaticOrDirect::MethodLoadKind::kDirectAddressWithFixup:
      __ movl(temp.AsRegister<CpuRegister>(), Immediate(0));  // Placeholder.
      method_patches_.emplace_back(invoke->GetTargetMethod());
      __ Bind(&method_patches_.back().label);  // Bind the label at the end of the "movl" insn.
      break;
    case HInvokeStaticOrDirect::MethodLoadKind::kDexCachePcRelative:
      pc_rel_dex_cache_patches_.emplace_back(*invoke->GetTargetMethod().dex_file,
                                             invoke->GetDexCacheArrayOffset());
      __ movq(temp.AsRegister<CpuRegister>(),
              Address::Absolute(kDummy32BitOffset, false /* no_rip */));
      // Bind the label at the end of the "movl" insn.
      __ Bind(&pc_rel_dex_cache_patches_.back().label);
      break;
    case HInvokeStaticOrDirect::MethodLoadKind::kDexCacheViaMethod: {
      CpuRegister reg = temp.AsRegister<CpuRegister>();
      __ movq(reg, Address(CpuRegister(RSP), kCurrentMethodStackOffset));
      // temp = temp->dex_cache_resolved_methods_;
      __ movl(reg,
              Address(CpuRegister(reg),
                      ArtMethod::DexCacheResolvedMethodsOffset().SizeValue()));
      // temp = temp[index_in_cache]
      uint32_t index_in_cache = invoke->GetTargetMethod().dex_method_index;
      __ movq(reg, Address(reg, CodeGenerator::GetCachePointerOffset(index_in_cache)));
      break;
    }
  }

  switch (invoke->GetCodePtrLocation()) {
    case HInvokeStaticOrDirect::CodePtrLocation::kCallSelf:
      __ call(&frame_entry_label_);
      break;
    case HInvokeStaticOrDirect::CodePtrLocation::kCallPCRelative: {
      relative_call_patches_.emplace_back(invoke->GetTargetMethod());
      Label* label = &relative_call_patches_.back().label;
      __ call(label);  // Bind to the patch label, override at link time.
      __ Bind(label);  // Bind the label at the end of the "call" insn.
      break;
    }
    case HInvokeStaticOrDirect::CodePtrLocation::kCallDirectWithFixup:
    case HInvokeStaticOrDirect::CodePtrLocation::kCallDirect:
      // For direct code, we actually prefer to call via the code pointer from ArtMethod*.
      // TODO Implement patchable direct call here instead of just call indirectly via ArtMethod*
      FALLTHROUGH_INTENDED;
    case HInvokeStaticOrDirect::CodePtrLocation::kCallArtMethod:
      // (callee_method + offset_of_quick_compiled_code)()
      __ call(Address(callee_method.AsRegister<CpuRegister>(),
                      ArtMethod::EntryPointFromQuickCompiledCodeOffset(
                          kX86_64WordSize).SizeValue()));
      break;
  }

  DCHECK(!IsLeafMethod());
}

void CodeGeneratorX86_64::EmitLinkerPatches(ArenaVector<LinkerPatch>* linker_patches) {
  DCHECK(linker_patches->empty());
  size_t size =
      method_patches_.size() + relative_call_patches_.size() + pc_rel_dex_cache_patches_.size();
  linker_patches->reserve(size);
  for (const MethodPatchInfo<Label>& info : method_patches_) {
    // The label points to the end of the "movl" instruction but the literal offset for method
    // patch x86 needs to point to the embedded constant which occupies the last 4 bytes.
    uint32_t literal_offset = info.label.Position() - 4;
    linker_patches->push_back(LinkerPatch::MethodPatch(literal_offset,
                                                       info.target_method.dex_file,
                                                       info.target_method.dex_method_index));
  }
  for (const MethodPatchInfo<Label>& info : relative_call_patches_) {
    // The label points to the end of the "call" instruction but the literal offset for method
    // patch x86 needs to point to the embedded constant which occupies the last 4 bytes.
    uint32_t literal_offset = info.label.Position() - 4;
    linker_patches->push_back(LinkerPatch::RelativeCodePatch(literal_offset,
                                                             info.target_method.dex_file,
                                                             info.target_method.dex_method_index));
  }
  for (const PcRelativeDexCacheAccessInfo& info : pc_rel_dex_cache_patches_) {
    // The label points to the end of the "mov" instruction but the literal offset for method
    // patch x86 needs to point to the embedded constant which occupies the last 4 bytes.
    uint32_t literal_offset = info.label.Position() - 4;
    linker_patches->push_back(LinkerPatch::DexCacheArrayPatch(literal_offset,
                                                              &info.target_dex_file,
                                                              info.label.Position(),
                                                              info.element_offset));
  }
}

void CodeGeneratorX86_64::DumpCoreRegister(std::ostream& stream, int reg) const {
  stream << X86_64ManagedRegister::FromCpuRegister(Register(reg));
}

void CodeGeneratorX86_64::DumpFloatingPointRegister(std::ostream& stream, int reg) const {
  stream << X86_64ManagedRegister::FromXmmRegister(FloatRegister(reg));
}

size_t CodeGeneratorX86_64::SaveCoreRegister(size_t stack_index, uint32_t reg_id) {
  __ movq(Address(CpuRegister(RSP), stack_index), CpuRegister(reg_id));
  return kX86_64WordSize;
}

size_t CodeGeneratorX86_64::RestoreCoreRegister(size_t stack_index, uint32_t reg_id) {
  __ movq(CpuRegister(reg_id), Address(CpuRegister(RSP), stack_index));
  return kX86_64WordSize;
}

size_t CodeGeneratorX86_64::SaveFloatingPointRegister(size_t stack_index, uint32_t reg_id) {
  __ movsd(Address(CpuRegister(RSP), stack_index), XmmRegister(reg_id));
  return kX86_64WordSize;
}

size_t CodeGeneratorX86_64::RestoreFloatingPointRegister(size_t stack_index, uint32_t reg_id) {
  __ movsd(XmmRegister(reg_id), Address(CpuRegister(RSP), stack_index));
  return kX86_64WordSize;
}

static constexpr int kNumberOfCpuRegisterPairs = 0;
// Use a fake return address register to mimic Quick.
static constexpr Register kFakeReturnRegister = Register(kLastCpuRegister + 1);
CodeGeneratorX86_64::CodeGeneratorX86_64(HGraph* graph,
                const X86_64InstructionSetFeatures& isa_features,
                const CompilerOptions& compiler_options)
      : CodeGenerator(graph,
                      kNumberOfCpuRegisters,
                      kNumberOfFloatRegisters,
                      kNumberOfCpuRegisterPairs,
                      ComputeRegisterMask(reinterpret_cast<const int*>(kCoreCalleeSaves),
                                          arraysize(kCoreCalleeSaves))
                          | (1 << kFakeReturnRegister),
                      ComputeRegisterMask(reinterpret_cast<const int*>(kFpuCalleeSaves),
                                          arraysize(kFpuCalleeSaves)),
                      compiler_options),
        block_labels_(graph->GetArena(), 0),
        location_builder_(graph, this),
        instruction_visitor_(graph, this),
        move_resolver_(graph->GetArena(), this),
        isa_features_(isa_features),
        constant_area_start_(0),
        jump_table_fixups_(graph->GetArena(), 0),
        method_patches_(graph->GetArena()->Adapter()),
        relative_call_patches_(graph->GetArena()->Adapter()),
        pc_rel_dex_cache_patches_(graph->GetArena()->Adapter()) {
  AddAllocatedRegister(Location::RegisterLocation(kFakeReturnRegister));
}

InstructionCodeGeneratorX86_64::InstructionCodeGeneratorX86_64(HGraph* graph,
                                                               CodeGeneratorX86_64* codegen)
      : HGraphVisitor(graph),
        assembler_(codegen->GetAssembler()),
        codegen_(codegen) {}

Location CodeGeneratorX86_64::AllocateFreeRegister(Primitive::Type type) const {
  switch (type) {
    case Primitive::kPrimLong:
    case Primitive::kPrimByte:
    case Primitive::kPrimBoolean:
    case Primitive::kPrimChar:
    case Primitive::kPrimShort:
    case Primitive::kPrimInt:
    case Primitive::kPrimNot: {
      size_t reg = FindFreeEntry(blocked_core_registers_, kNumberOfCpuRegisters);
      return Location::RegisterLocation(reg);
    }

    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble: {
      size_t reg = FindFreeEntry(blocked_fpu_registers_, kNumberOfFloatRegisters);
      return Location::FpuRegisterLocation(reg);
    }

    case Primitive::kPrimVoid:
      LOG(FATAL) << "Unreachable type " << type;
  }

  return Location();
}

void CodeGeneratorX86_64::SetupBlockedRegisters(bool is_baseline) const {
  // Stack register is always reserved.
  blocked_core_registers_[RSP] = true;

  // Block the register used as TMP.
  blocked_core_registers_[TMP] = true;

  if (is_baseline) {
    for (size_t i = 0; i < arraysize(kCoreCalleeSaves); ++i) {
      blocked_core_registers_[kCoreCalleeSaves[i]] = true;
    }
    for (size_t i = 0; i < arraysize(kFpuCalleeSaves); ++i) {
      blocked_fpu_registers_[kFpuCalleeSaves[i]] = true;
    }
  }
}

static dwarf::Reg DWARFReg(Register reg) {
  return dwarf::Reg::X86_64Core(static_cast<int>(reg));
}

static dwarf::Reg DWARFReg(FloatRegister reg) {
  return dwarf::Reg::X86_64Fp(static_cast<int>(reg));
}

void CodeGeneratorX86_64::GenerateFrameEntry() {
  __ cfi().SetCurrentCFAOffset(kX86_64WordSize);  // return address
  __ Bind(&frame_entry_label_);
  bool skip_overflow_check = IsLeafMethod()
      && !FrameNeedsStackCheck(GetFrameSize(), InstructionSet::kX86_64);
  DCHECK(GetCompilerOptions().GetImplicitStackOverflowChecks());

  if (!skip_overflow_check) {
    __ testq(CpuRegister(RAX), Address(
        CpuRegister(RSP), -static_cast<int32_t>(GetStackOverflowReservedBytes(kX86_64))));
    RecordPcInfo(nullptr, 0);
  }

  if (HasEmptyFrame()) {
    return;
  }

  for (int i = arraysize(kCoreCalleeSaves) - 1; i >= 0; --i) {
    Register reg = kCoreCalleeSaves[i];
    if (allocated_registers_.ContainsCoreRegister(reg)) {
      __ pushq(CpuRegister(reg));
      __ cfi().AdjustCFAOffset(kX86_64WordSize);
      __ cfi().RelOffset(DWARFReg(reg), 0);
    }
  }

  int adjust = GetFrameSize() - GetCoreSpillSize();
  __ subq(CpuRegister(RSP), Immediate(adjust));
  __ cfi().AdjustCFAOffset(adjust);
  uint32_t xmm_spill_location = GetFpuSpillStart();
  size_t xmm_spill_slot_size = GetFloatingPointSpillSlotSize();

  for (int i = arraysize(kFpuCalleeSaves) - 1; i >= 0; --i) {
    if (allocated_registers_.ContainsFloatingPointRegister(kFpuCalleeSaves[i])) {
      int offset = xmm_spill_location + (xmm_spill_slot_size * i);
      __ movsd(Address(CpuRegister(RSP), offset), XmmRegister(kFpuCalleeSaves[i]));
      __ cfi().RelOffset(DWARFReg(kFpuCalleeSaves[i]), offset);
    }
  }

  __ movq(Address(CpuRegister(RSP), kCurrentMethodStackOffset), CpuRegister(RDI));
}

void CodeGeneratorX86_64::GenerateFrameExit() {
  __ cfi().RememberState();
  if (!HasEmptyFrame()) {
    uint32_t xmm_spill_location = GetFpuSpillStart();
    size_t xmm_spill_slot_size = GetFloatingPointSpillSlotSize();
    for (size_t i = 0; i < arraysize(kFpuCalleeSaves); ++i) {
      if (allocated_registers_.ContainsFloatingPointRegister(kFpuCalleeSaves[i])) {
        int offset = xmm_spill_location + (xmm_spill_slot_size * i);
        __ movsd(XmmRegister(kFpuCalleeSaves[i]), Address(CpuRegister(RSP), offset));
        __ cfi().Restore(DWARFReg(kFpuCalleeSaves[i]));
      }
    }

    int adjust = GetFrameSize() - GetCoreSpillSize();
    __ addq(CpuRegister(RSP), Immediate(adjust));
    __ cfi().AdjustCFAOffset(-adjust);

    for (size_t i = 0; i < arraysize(kCoreCalleeSaves); ++i) {
      Register reg = kCoreCalleeSaves[i];
      if (allocated_registers_.ContainsCoreRegister(reg)) {
        __ popq(CpuRegister(reg));
        __ cfi().AdjustCFAOffset(-static_cast<int>(kX86_64WordSize));
        __ cfi().Restore(DWARFReg(reg));
      }
    }
  }
  __ ret();
  __ cfi().RestoreState();
  __ cfi().DefCFAOffset(GetFrameSize());
}

void CodeGeneratorX86_64::Bind(HBasicBlock* block) {
  __ Bind(GetLabelOf(block));
}

void CodeGeneratorX86_64::LoadCurrentMethod(CpuRegister reg) {
  DCHECK(RequiresCurrentMethod());
  __ movq(reg, Address(CpuRegister(RSP), kCurrentMethodStackOffset));
}

Location CodeGeneratorX86_64::GetStackLocation(HLoadLocal* load) const {
  switch (load->GetType()) {
    case Primitive::kPrimLong:
    case Primitive::kPrimDouble:
      return Location::DoubleStackSlot(GetStackSlot(load->GetLocal()));

    case Primitive::kPrimInt:
    case Primitive::kPrimNot:
    case Primitive::kPrimFloat:
      return Location::StackSlot(GetStackSlot(load->GetLocal()));

    case Primitive::kPrimBoolean:
    case Primitive::kPrimByte:
    case Primitive::kPrimChar:
    case Primitive::kPrimShort:
    case Primitive::kPrimVoid:
      LOG(FATAL) << "Unexpected type " << load->GetType();
      UNREACHABLE();
  }

  LOG(FATAL) << "Unreachable";
  UNREACHABLE();
}

void CodeGeneratorX86_64::Move(Location destination, Location source) {
  if (source.Equals(destination)) {
    return;
  }
  if (destination.IsRegister()) {
    if (source.IsRegister()) {
      __ movq(destination.AsRegister<CpuRegister>(), source.AsRegister<CpuRegister>());
    } else if (source.IsFpuRegister()) {
      __ movd(destination.AsRegister<CpuRegister>(), source.AsFpuRegister<XmmRegister>());
    } else if (source.IsStackSlot()) {
      __ movl(destination.AsRegister<CpuRegister>(),
              Address(CpuRegister(RSP), source.GetStackIndex()));
    } else {
      DCHECK(source.IsDoubleStackSlot());
      __ movq(destination.AsRegister<CpuRegister>(),
              Address(CpuRegister(RSP), source.GetStackIndex()));
    }
  } else if (destination.IsFpuRegister()) {
    if (source.IsRegister()) {
      __ movd(destination.AsFpuRegister<XmmRegister>(), source.AsRegister<CpuRegister>());
    } else if (source.IsFpuRegister()) {
      __ movaps(destination.AsFpuRegister<XmmRegister>(), source.AsFpuRegister<XmmRegister>());
    } else if (source.IsStackSlot()) {
      __ movss(destination.AsFpuRegister<XmmRegister>(),
              Address(CpuRegister(RSP), source.GetStackIndex()));
    } else {
      DCHECK(source.IsDoubleStackSlot());
      __ movsd(destination.AsFpuRegister<XmmRegister>(),
               Address(CpuRegister(RSP), source.GetStackIndex()));
    }
  } else if (destination.IsStackSlot()) {
    if (source.IsRegister()) {
      __ movl(Address(CpuRegister(RSP), destination.GetStackIndex()),
              source.AsRegister<CpuRegister>());
    } else if (source.IsFpuRegister()) {
      __ movss(Address(CpuRegister(RSP), destination.GetStackIndex()),
               source.AsFpuRegister<XmmRegister>());
    } else if (source.IsConstant()) {
      HConstant* constant = source.GetConstant();
      int32_t value = GetInt32ValueOf(constant);
      __ movl(Address(CpuRegister(RSP), destination.GetStackIndex()), Immediate(value));
    } else {
      DCHECK(source.IsStackSlot()) << source;
      __ movl(CpuRegister(TMP), Address(CpuRegister(RSP), source.GetStackIndex()));
      __ movl(Address(CpuRegister(RSP), destination.GetStackIndex()), CpuRegister(TMP));
    }
  } else {
    DCHECK(destination.IsDoubleStackSlot());
    if (source.IsRegister()) {
      __ movq(Address(CpuRegister(RSP), destination.GetStackIndex()),
              source.AsRegister<CpuRegister>());
    } else if (source.IsFpuRegister()) {
      __ movsd(Address(CpuRegister(RSP), destination.GetStackIndex()),
               source.AsFpuRegister<XmmRegister>());
    } else if (source.IsConstant()) {
      HConstant* constant = source.GetConstant();
      int64_t value;
      if (constant->IsDoubleConstant()) {
        value = bit_cast<int64_t, double>(constant->AsDoubleConstant()->GetValue());
      } else {
        DCHECK(constant->IsLongConstant());
        value = constant->AsLongConstant()->GetValue();
      }
      Store64BitValueToStack(destination, value);
    } else {
      DCHECK(source.IsDoubleStackSlot());
      __ movq(CpuRegister(TMP), Address(CpuRegister(RSP), source.GetStackIndex()));
      __ movq(Address(CpuRegister(RSP), destination.GetStackIndex()), CpuRegister(TMP));
    }
  }
}

void CodeGeneratorX86_64::Move(HInstruction* instruction,
                               Location location,
                               HInstruction* move_for) {
  LocationSummary* locations = instruction->GetLocations();
  if (locations != nullptr && locations->Out().Equals(location)) {
    return;
  }

  if (locations != nullptr && locations->Out().IsConstant()) {
    HConstant* const_to_move = locations->Out().GetConstant();
    if (const_to_move->IsIntConstant() || const_to_move->IsNullConstant()) {
      Immediate imm(GetInt32ValueOf(const_to_move));
      if (location.IsRegister()) {
        __ movl(location.AsRegister<CpuRegister>(), imm);
      } else if (location.IsStackSlot()) {
        __ movl(Address(CpuRegister(RSP), location.GetStackIndex()), imm);
      } else {
        DCHECK(location.IsConstant());
        DCHECK_EQ(location.GetConstant(), const_to_move);
      }
    } else if (const_to_move->IsLongConstant()) {
      int64_t value = const_to_move->AsLongConstant()->GetValue();
      if (location.IsRegister()) {
        Load64BitValue(location.AsRegister<CpuRegister>(), value);
      } else if (location.IsDoubleStackSlot()) {
        Store64BitValueToStack(location, value);
      } else {
        DCHECK(location.IsConstant());
        DCHECK_EQ(location.GetConstant(), const_to_move);
      }
    }
  } else if (instruction->IsLoadLocal()) {
    switch (instruction->GetType()) {
      case Primitive::kPrimBoolean:
      case Primitive::kPrimByte:
      case Primitive::kPrimChar:
      case Primitive::kPrimShort:
      case Primitive::kPrimInt:
      case Primitive::kPrimNot:
      case Primitive::kPrimFloat:
        Move(location, Location::StackSlot(GetStackSlot(instruction->AsLoadLocal()->GetLocal())));
        break;

      case Primitive::kPrimLong:
      case Primitive::kPrimDouble:
        Move(location,
             Location::DoubleStackSlot(GetStackSlot(instruction->AsLoadLocal()->GetLocal())));
        break;

      default:
        LOG(FATAL) << "Unexpected local type " << instruction->GetType();
    }
  } else if (instruction->IsTemporary()) {
    Location temp_location = GetTemporaryLocation(instruction->AsTemporary());
    Move(location, temp_location);
  } else {
    DCHECK((instruction->GetNext() == move_for) || instruction->GetNext()->IsTemporary());
    switch (instruction->GetType()) {
      case Primitive::kPrimBoolean:
      case Primitive::kPrimByte:
      case Primitive::kPrimChar:
      case Primitive::kPrimShort:
      case Primitive::kPrimInt:
      case Primitive::kPrimNot:
      case Primitive::kPrimLong:
      case Primitive::kPrimFloat:
      case Primitive::kPrimDouble:
        Move(location, locations->Out());
        break;

      default:
        LOG(FATAL) << "Unexpected type " << instruction->GetType();
    }
  }
}

void LocationsBuilderX86_64::VisitGoto(HGoto* got) {
  got->SetLocations(nullptr);
}

void InstructionCodeGeneratorX86_64::VisitGoto(HGoto* got) {
  HBasicBlock* successor = got->GetSuccessor();
  DCHECK(!successor->IsExitBlock());

  HBasicBlock* block = got->GetBlock();
  HInstruction* previous = got->GetPrevious();

  HLoopInformation* info = block->GetLoopInformation();
  if (info != nullptr && info->IsBackEdge(*block) && info->HasSuspendCheck()) {
    GenerateSuspendCheck(info->GetSuspendCheck(), successor);
    return;
  }

  if (block->IsEntryBlock() && (previous != nullptr) && previous->IsSuspendCheck()) {
    GenerateSuspendCheck(previous->AsSuspendCheck(), nullptr);
  }
  if (!codegen_->GoesToNextBlock(got->GetBlock(), successor)) {
    __ jmp(codegen_->GetLabelOf(successor));
  }
}

void LocationsBuilderX86_64::VisitExit(HExit* exit) {
  exit->SetLocations(nullptr);
}

void InstructionCodeGeneratorX86_64::VisitExit(HExit* exit) {
  UNUSED(exit);
}

void LocationsBuilderX86_64::VisitX86SelectValue(HX86SelectValue* instr) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instr, LocationSummary::kNoCall);
  // The values must both be in registers for CMOV.
  locations->SetInAt(0, Location::RequiresRegister());
  locations->SetInAt(1, Location::RequiresRegister());

  // The comparision is just like a condition.  We only handle integers for now.
  DCHECK(!Primitive::IsFloatingPointType(instr->InputAt(2)->GetType()));
  locations->SetInAt(2, Location::RequiresRegister());
  locations->SetInAt(3, Location::Any());
  locations->SetOut(Location::SameAsFirstInput());
}

void InstructionCodeGeneratorX86_64::VisitX86SelectValue(HX86SelectValue* instr) {
  LocationSummary* locations = instr->GetLocations();
  CpuRegister value_lhs = locations->InAt(0).AsRegister<CpuRegister>();
  CpuRegister value_rhs = locations->InAt(1).AsRegister<CpuRegister>();
  CpuRegister cond_lhs = locations->InAt(2).AsRegister<CpuRegister>();
  Location cond_rhs = locations->InAt(3);
  CpuRegister out = locations->Out().AsRegister<CpuRegister>();
  DCHECK_EQ(out.AsRegister(), value_lhs.AsRegister());

  switch (instr->GetCompareLeft()->GetType()) {
    default:
      // Integer case.
      if (cond_rhs.IsRegister()) {
        __ cmpl(cond_lhs, cond_rhs.AsRegister<CpuRegister>());
      } else if (cond_rhs.IsConstant()) {
        int32_t constant = CodeGenerator::GetInt32ValueOf(cond_rhs.GetConstant());
        if (constant == 0) {
          __ testl(cond_lhs, cond_lhs);
        } else {
          __ cmpl(cond_lhs, Immediate(constant));
        }
      } else {
        __ cmpl(cond_lhs, Address(CpuRegister(RSP), cond_rhs.GetStackIndex()));
      }
      break;

    case Primitive::kPrimLong:
      if (cond_rhs.IsRegister()) {
        __ cmpq(cond_lhs, cond_rhs.AsRegister<CpuRegister>());
      } else if (cond_rhs.IsConstant()) {
        int64_t value = cond_rhs.GetConstant()->AsLongConstant()->GetValue();
        if (IsInt<32>(value)) {
          if (value == 0) {
            __ testq(cond_lhs, cond_lhs);
          } else {
            __ cmpq(cond_lhs, Immediate(static_cast<int32_t>(value)));
          }
        } else {
          // Value won't fit in an int.
          __ cmpq(cond_lhs, codegen_->LiteralInt64Address(value));
        }
      } else {
        __ cmpq(cond_lhs, Address(CpuRegister(RSP), cond_rhs.GetStackIndex()));
      }
      break;
  }

  // The condition code has now been set. Use a CMOV to get the right value into
  // the output (which is the same as the input).  The LHS is already in the output
  // register, so we only need to move on the opposite condition.
  Condition opposite_cond = kEqual;
  switch (instr->GetCondition()) {
    case kCondEQ:
      opposite_cond = kNotEqual;
      break;
    case kCondNE:
      opposite_cond = kEqual;
      break;
    case kCondLT:
      opposite_cond = kGreaterEqual;
      break;
    case kCondLE:
      opposite_cond = kGreater;
      break;
    case kCondGT:
      opposite_cond = kLessEqual;
      break;
    case kCondGE:
      opposite_cond = kLess;
      break;
  }
  __ cmov(opposite_cond, out, value_rhs, instr->GetType() == Primitive::kPrimLong);
}

void LocationsBuilderX86_64::VisitSwitch(HSwitch* switch_instr) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(switch_instr, LocationSummary::kNoCall);
  locations->SetInAt(0, Location::RequiresRegister());
  locations->AddTemp(Location::RequiresRegister());
  locations->AddTemp(Location::RequiresRegister());
}

void InstructionCodeGeneratorX86_64::VisitSwitch(HSwitch* switch_instr) {
  int32_t lower_bound = switch_instr->GetStartValue();
  int32_t num_entries = switch_instr->GetNumEntries();
  LocationSummary* locations = switch_instr->GetLocations();
  CpuRegister value_reg_in = locations->InAt(0).AsRegister<CpuRegister>();
  CpuRegister temp_reg = locations->GetTemp(0).AsRegister<CpuRegister>();
  CpuRegister base_reg = locations->GetTemp(1).AsRegister<CpuRegister>();

  // Remove the bias, if needed. CpuRegister can't be assigned (boo!).
  Register value_reg_out = value_reg_in.AsRegister();
  if (lower_bound != 0) {
    __ leal(temp_reg, Address(value_reg_in, -lower_bound));
    value_reg_out = temp_reg.AsRegister();
  }
  CpuRegister value_reg(value_reg_out);

  // Is the value in range?
  HBasicBlock* default_block = switch_instr->GetDefaultBlock();
  __ cmpl(value_reg, Immediate(num_entries - 1));
  __ j(kAbove, codegen_->GetLabelOf(default_block));

  // We are in the range of the table.
  // Load the address of the jump table in the constant area.
  __ leaq(base_reg, codegen_->LiteralCaseTable(switch_instr));

  // Load the (signed) offset from the jump table.
  __ movsxd(temp_reg, Address(base_reg, value_reg, TIMES_4, 0));

  // Add the offset to the address of the table base.
  __ addq(temp_reg, base_reg);

  // And jump.
  __ jmp(temp_reg);
}

static Condition ReverseX86Condition(Condition cond) {
  switch (cond) {
    case kEqual:
      return kNotEqual;
    case kNotEqual:
      return kEqual;
    case kLess:
      return kGreaterEqual;
    case kLessEqual:
      return kGreater;
    case kGreater:
      return kLessEqual;
    case kGreaterEqual:
      return kLess;
    case kAboveEqual:
      return kBelow;
    case kAbove:
      return kBelowEqual;
    case kBelow:
      return kAboveEqual;
    case kBelowEqual:
      return kAbove;
    default:
      LOG(FATAL) << "Unknown if condition";
  }
  return cond;
}

void InstructionCodeGeneratorX86_64::GenerateFPJumps(HCondition* cond,
                                                     Label* true_label,
                                                     Label* false_label,
                                                     bool can_jump_to_false) {
  bool gt_bias = cond->IsGtBias();
  IfCondition if_cond = cond->GetCondition();
  Condition ccode = kOverflow;
  /* unordered result sets flags to: cf = 1, zf = 1, pf = 1 */
  switch (if_cond) {
    case kCondEQ:
      __ j(kParityEven, false_label); /* pf == 1 */
      ccode = kEqual; /* zf == 1 */
      break;
    case kCondNE:
      __ j(kParityEven, true_label); /* pf == 1 */
      ccode = kNotEqual; /* zf == 0 */
      break;
    case kCondLT:
      if (gt_bias) {
        __ j(kParityEven, false_label); /* pf == 1 */
      }
      ccode = kBelow; /* cf == 1 */
      break;
    case kCondLE:
      if (gt_bias) {
        __ j(kParityEven, false_label); /* pf == 1 */
      }
      ccode = kBelowEqual; /* cf == 1 || zf == 0 */
      break;
    case kCondGT:
      if (gt_bias) {
        __ j(kParityEven, true_label); /* pf == 1 */
      }
      ccode = kAbove; /* cf == 0 && zf == 0 */
      break;
    case kCondGE:
      if (gt_bias) {
        __ j(kParityEven, true_label); /* pf == 1 */
      }
      ccode = kAboveEqual; /* cf == 0 */
      break;
  }
  DCHECK(ccode != kOverflow);
  if (can_jump_to_false) {
    __ j(ReverseX86Condition(ccode), false_label);
  } else {
    __ j(ccode, true_label);
  }
}

void InstructionCodeGeneratorX86_64::GenerateCompareTestAndBranch(HIf* if_instr,
                                                                  HCondition* condition,
                                                                  Label* true_target,
                                                                  Label* false_target,
                                                                  Label* always_true_target) {
  LocationSummary* locations = condition->GetLocations();
  Location left = locations->InAt(0);
  Location right = locations->InAt(1);

  // We don't want true_target as a nullptr.
  if (true_target == nullptr) {
    true_target = always_true_target;
  }
  bool falls_through = (false_target == nullptr);

  // FP compares don't like null false_targets.
  if (false_target == nullptr) {
    false_target = codegen_->GetLabelOf(if_instr->IfFalseSuccessor());
  }

  Primitive::Type type = condition->InputAt(0)->GetType();
  bool can_jump_to_false = CanReverseCondition(always_true_target, false_target);
  switch (type) {
    case Primitive::kPrimLong: {
      CpuRegister left_reg = left.AsRegister<CpuRegister>();
      if (right.IsConstant()) {
        int64_t value = right.GetConstant()->AsLongConstant()->GetValue();
        if (IsInt<32>(value)) {
          if (value == 0) {
            __ testq(left_reg, left_reg);
          } else {
            __ cmpq(left_reg, Immediate(static_cast<int32_t>(value)));
          }
        } else {
          // Value won't fit in an 32-bit integer.
          __ cmpq(left_reg, codegen_->LiteralInt64Address(value));
        }
      } else if (right.IsDoubleStackSlot()) {
        __ cmpq(left_reg, Address(CpuRegister(RSP), right.GetStackIndex()));
      } else {
        __ cmpq(left_reg, right.AsRegister<CpuRegister>());
      }
      if (can_jump_to_false) {
        __ j(X86_64Condition(condition->GetOppositeCondition()), false_target);
        return;
      }
      __ j(X86_64Condition(condition->GetCondition()), true_target);
      break;
    }
    case Primitive::kPrimFloat: {
      if (right.IsFpuRegister()) {
        __ ucomiss(left.AsFpuRegister<XmmRegister>(), right.AsFpuRegister<XmmRegister>());
      } else if (right.IsConstant()) {
        __ ucomiss(left.AsFpuRegister<XmmRegister>(),
                   codegen_->LiteralFloatAddress(
                     right.GetConstant()->AsFloatConstant()->GetValue()));
      } else {
        DCHECK(right.IsStackSlot());
        __ ucomiss(left.AsFpuRegister<XmmRegister>(),
                   Address(CpuRegister(RSP), right.GetStackIndex()));
      }
      GenerateFPJumps(condition, true_target, false_target, can_jump_to_false);
      break;
    }
    case Primitive::kPrimDouble: {
      if (right.IsFpuRegister()) {
        __ ucomisd(left.AsFpuRegister<XmmRegister>(), right.AsFpuRegister<XmmRegister>());
      } else if (right.IsConstant()) {
        __ ucomisd(left.AsFpuRegister<XmmRegister>(),
                   codegen_->LiteralDoubleAddress(
                     right.GetConstant()->AsDoubleConstant()->GetValue()));
      } else {
        DCHECK(right.IsDoubleStackSlot());
        __ ucomisd(left.AsFpuRegister<XmmRegister>(),
                   Address(CpuRegister(RSP), right.GetStackIndex()));
      }
      GenerateFPJumps(condition, true_target, false_target, can_jump_to_false);
      break;
    }
    default:
      LOG(FATAL) << "Unexpected condition type " << type;
  }

  if (!falls_through && !can_jump_to_false) {
    __ jmp(false_target);
  }
}

void InstructionCodeGeneratorX86_64::GenerateTestAndBranch(HInstruction* instruction,
                                                           Label* true_target,
                                                           Label* false_target,
                                                           Label* always_true_target) {
  HInstruction* cond = instruction->InputAt(0);
  if (cond->IsIntConstant()) {
    // Constant condition, statically compared against 1.
    int32_t cond_value = cond->AsIntConstant()->GetValue();
    if (cond_value == 1) {
      if (always_true_target != nullptr) {
        __ jmp(always_true_target);
      }
      return;
    } else {
      DCHECK_EQ(cond_value, 0);
    }
  } else {
    bool materialized =
        !cond->IsCondition() || cond->AsCondition()->NeedsMaterialization();
    // Moves do not affect the eflags register, so if the condition is
    // evaluated just before the if, we don't need to evaluate it
    // again.  We can't use the eflags on FP conditions if they are
    // materialized due to the complex branching.
    HCondition* condition = cond->AsCondition();
    Primitive::Type type = condition ? cond->InputAt(0)->GetType() : Primitive::kPrimInt;
    bool eflags_set = cond->IsCondition()
        && cond->AsCondition()->IsBeforeWhenDisregardMoves(instruction)
        && !Primitive::IsFloatingPointType(type);
    // Can we optimize the jump if we know that the next block is the true case?
    bool can_jump_to_false = CanReverseCondition(always_true_target, false_target);

    if (materialized) {
      if (!eflags_set) {
        // Materialized condition, compare against 0.
        Location lhs = instruction->GetLocations()->InAt(0);
        if (lhs.IsRegister()) {
          __ testl(lhs.AsRegister<CpuRegister>(), lhs.AsRegister<CpuRegister>());
        } else {
          __ cmpl(Address(CpuRegister(RSP), lhs.GetStackIndex()),
                  Immediate(0));
        }
        if (can_jump_to_false) {
          __ j(kEqual, false_target);
          return;
        }
        __ j(kNotEqual, true_target);
      } else {
        if (can_jump_to_false) {
          __ j(X86_64Condition(condition->GetOppositeCondition()), false_target);
          return;
        }
        __ j(X86_64Condition(condition->GetCondition()), true_target);
      }
    } else {
      // Is this a long or FP comparison that has been folded into the HCondition?
      if (type == Primitive::kPrimLong || Primitive::IsFloatingPointType(type)) {
        // Generate the comparison directly
        GenerateCompareTestAndBranch(instruction->AsIf(), cond->AsCondition(),
                                     true_target, false_target, always_true_target);
        return;
      }
      Location lhs = cond->GetLocations()->InAt(0);
      Location rhs = cond->GetLocations()->InAt(1);
      if (rhs.IsRegister()) {
        __ cmpl(lhs.AsRegister<CpuRegister>(), rhs.AsRegister<CpuRegister>());
      } else if (rhs.IsConstant()) {
        int32_t constant = CodeGenerator::GetInt32ValueOf(rhs.GetConstant());
        if (constant == 0) {
          __ testl(lhs.AsRegister<CpuRegister>(), lhs.AsRegister<CpuRegister>());
        } else {
          __ cmpl(lhs.AsRegister<CpuRegister>(), Immediate(constant));
        }
      } else {
        __ cmpl(lhs.AsRegister<CpuRegister>(),
                Address(CpuRegister(RSP), rhs.GetStackIndex()));
      }

      if (can_jump_to_false) {
        __ j(X86_64Condition(condition->GetOppositeCondition()), false_target);
        return;
      }

      __ j(X86_64Condition(condition->GetCondition()), true_target);
    }
  }
  if (false_target != nullptr) {
    __ jmp(false_target);
  }
}

void LocationsBuilderX86_64::VisitIf(HIf* if_instr) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(if_instr, LocationSummary::kNoCall);
  HInstruction* cond = if_instr->InputAt(0);
  if (!cond->IsCondition() || cond->AsCondition()->NeedsMaterialization()) {
    locations->SetInAt(0, Location::Any());
  }
}

void InstructionCodeGeneratorX86_64::VisitIf(HIf* if_instr) {
  Label* true_target = codegen_->GetLabelOf(if_instr->IfTrueSuccessor());
  Label* false_target = codegen_->GetLabelOf(if_instr->IfFalseSuccessor());
  Label* always_true_target = true_target;
  if (codegen_->GoesToNextBlock(if_instr->GetBlock(),
                                if_instr->IfTrueSuccessor())) {
    always_true_target = nullptr;
  }
  if (codegen_->GoesToNextBlock(if_instr->GetBlock(),
                                if_instr->IfFalseSuccessor())) {
    false_target = nullptr;
  }
  GenerateTestAndBranch(if_instr, true_target, false_target, always_true_target);
}

void LocationsBuilderX86_64::VisitDeoptimize(HDeoptimize* deoptimize) {
  LocationSummary* locations = new (GetGraph()->GetArena())
      LocationSummary(deoptimize, LocationSummary::kCallOnSlowPath);
  HInstruction* cond = deoptimize->InputAt(0);
  DCHECK(cond->IsCondition());
  if (cond->AsCondition()->NeedsMaterialization()) {
    locations->SetInAt(0, Location::Any());
  }
}

void InstructionCodeGeneratorX86_64::VisitDeoptimize(HDeoptimize* deoptimize) {
  SlowPathCodeX86_64* slow_path = new (GetGraph()->GetArena())
      DeoptimizationSlowPathX86_64(deoptimize);
  codegen_->AddSlowPath(slow_path);
  Label* slow_path_entry = slow_path->GetEntryLabel();
  GenerateTestAndBranch(deoptimize, slow_path_entry, nullptr, slow_path_entry);
}

void LocationsBuilderX86_64::VisitLocal(HLocal* local) {
  local->SetLocations(nullptr);
}

void InstructionCodeGeneratorX86_64::VisitLocal(HLocal* local) {
  DCHECK_EQ(local->GetBlock(), GetGraph()->GetEntryBlock());
}

void LocationsBuilderX86_64::VisitLoadLocal(HLoadLocal* local) {
  local->SetLocations(nullptr);
}

void InstructionCodeGeneratorX86_64::VisitLoadLocal(HLoadLocal* load) {
  // Nothing to do, this is driven by the code generator.
  UNUSED(load);
}

void LocationsBuilderX86_64::VisitStoreLocal(HStoreLocal* store) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(store, LocationSummary::kNoCall);
  switch (store->InputAt(1)->GetType()) {
    case Primitive::kPrimBoolean:
    case Primitive::kPrimByte:
    case Primitive::kPrimChar:
    case Primitive::kPrimShort:
    case Primitive::kPrimInt:
    case Primitive::kPrimNot:
    case Primitive::kPrimFloat:
      locations->SetInAt(1, Location::StackSlot(codegen_->GetStackSlot(store->GetLocal())));
      break;

    case Primitive::kPrimLong:
    case Primitive::kPrimDouble:
      locations->SetInAt(1, Location::DoubleStackSlot(codegen_->GetStackSlot(store->GetLocal())));
      break;

    default:
      LOG(FATAL) << "Unexpected local type " << store->InputAt(1)->GetType();
  }
}

void InstructionCodeGeneratorX86_64::VisitStoreLocal(HStoreLocal* store) {
  UNUSED(store);
}

void LocationsBuilderX86_64::VisitCondition(HCondition* cond) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(cond, LocationSummary::kNoCall);
  // Handle the long/FP comparisons made in instruction simplification.
  switch (cond->InputAt(0)->GetType()) {
    case Primitive::kPrimLong:
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetInAt(1, Location::Any());
      break;
    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble:
      locations->SetInAt(0, Location::RequiresFpuRegister());
      locations->SetInAt(1, Location::Any());
      break;
    default:
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetInAt(1, Location::Any());
      break;
  }
  if (cond->NeedsMaterialization()) {
    locations->SetOut(Location::RequiresRegister());
  }
}

void InstructionCodeGeneratorX86_64::VisitCondition(HCondition* cond) {
  if (!cond->NeedsMaterialization()) {
    return;
  }

  LocationSummary* locations = cond->GetLocations();
  Location lhs = locations->InAt(0);
  Location rhs = locations->InAt(1);
  CpuRegister reg = locations->Out().AsRegister<CpuRegister>();
  Label true_label, false_label;

  switch (cond->InputAt(0)->GetType()) {
    default:
      // Integer case.

      // Clear output register: setcc only sets the low byte.
      __ xorl(reg, reg);

      if (rhs.IsRegister()) {
        __ cmpl(lhs.AsRegister<CpuRegister>(), rhs.AsRegister<CpuRegister>());
      } else if (rhs.IsConstant()) {
        int32_t constant = CodeGenerator::GetInt32ValueOf(rhs.GetConstant());
        if (constant == 0) {
          __ testl(lhs.AsRegister<CpuRegister>(), lhs.AsRegister<CpuRegister>());
        } else {
          __ cmpl(lhs.AsRegister<CpuRegister>(), Immediate(constant));
        }
      } else {
        __ cmpl(lhs.AsRegister<CpuRegister>(), Address(CpuRegister(RSP), rhs.GetStackIndex()));
      }
      __ setcc(X86_64Condition(cond->GetCondition()), reg);
      return;
    case Primitive::kPrimLong:
      // Clear output register: setcc only sets the low byte.
      __ xorl(reg, reg);

      if (rhs.IsRegister()) {
        __ cmpq(lhs.AsRegister<CpuRegister>(), rhs.AsRegister<CpuRegister>());
      } else if (rhs.IsConstant()) {
        int64_t value = rhs.GetConstant()->AsLongConstant()->GetValue();
        if (IsInt<32>(value)) {
          if (value == 0) {
            __ testq(lhs.AsRegister<CpuRegister>(), lhs.AsRegister<CpuRegister>());
          } else {
            __ cmpq(lhs.AsRegister<CpuRegister>(), Immediate(static_cast<int32_t>(value)));
          }
        } else {
          // Value won't fit in an int.
          __ cmpq(lhs.AsRegister<CpuRegister>(), codegen_->LiteralInt64Address(value));
        }
      } else {
        __ cmpq(lhs.AsRegister<CpuRegister>(), Address(CpuRegister(RSP), rhs.GetStackIndex()));
      }
      __ setcc(X86_64Condition(cond->GetCondition()), reg);
      return;
    case Primitive::kPrimFloat: {
      XmmRegister lhs_reg = lhs.AsFpuRegister<XmmRegister>();
      if (rhs.IsConstant()) {
        float value = rhs.GetConstant()->AsFloatConstant()->GetValue();
        __ ucomiss(lhs_reg, codegen_->LiteralFloatAddress(value));
      } else if (rhs.IsStackSlot()) {
        __ ucomiss(lhs_reg, Address(CpuRegister(RSP), rhs.GetStackIndex()));
      } else {
        __ ucomiss(lhs_reg, rhs.AsFpuRegister<XmmRegister>());
      }
      GenerateFPJumps(cond, &true_label, &false_label);
      break;
    }
    case Primitive::kPrimDouble: {
      XmmRegister lhs_reg = lhs.AsFpuRegister<XmmRegister>();
      if (rhs.IsConstant()) {
        double value = rhs.GetConstant()->AsDoubleConstant()->GetValue();
        __ ucomisd(lhs_reg, codegen_->LiteralDoubleAddress(value));
      } else if (rhs.IsDoubleStackSlot()) {
        __ ucomisd(lhs_reg, Address(CpuRegister(RSP), rhs.GetStackIndex()));
      } else {
        __ ucomisd(lhs_reg, rhs.AsFpuRegister<XmmRegister>());
      }
      GenerateFPJumps(cond, &true_label, &false_label);
      break;
    }
  }

  // Convert the jumps into the result.
  NearLabel done_label;

  // false case: result = 0;
  __ Bind(&false_label);
  __ xorl(reg, reg);
  __ jmp(&done_label);

  // True case: result = 1
  __ Bind(&true_label);
  __ movl(reg, Immediate(1));
  __ Bind(&done_label);
}

void LocationsBuilderX86_64::VisitEqual(HEqual* comp) {
  VisitCondition(comp);
}

void InstructionCodeGeneratorX86_64::VisitEqual(HEqual* comp) {
  VisitCondition(comp);
}

void LocationsBuilderX86_64::VisitNotEqual(HNotEqual* comp) {
  VisitCondition(comp);
}

void InstructionCodeGeneratorX86_64::VisitNotEqual(HNotEqual* comp) {
  VisitCondition(comp);
}

void LocationsBuilderX86_64::VisitLessThan(HLessThan* comp) {
  VisitCondition(comp);
}

void InstructionCodeGeneratorX86_64::VisitLessThan(HLessThan* comp) {
  VisitCondition(comp);
}

void LocationsBuilderX86_64::VisitLessThanOrEqual(HLessThanOrEqual* comp) {
  VisitCondition(comp);
}

void InstructionCodeGeneratorX86_64::VisitLessThanOrEqual(HLessThanOrEqual* comp) {
  VisitCondition(comp);
}

void LocationsBuilderX86_64::VisitGreaterThan(HGreaterThan* comp) {
  VisitCondition(comp);
}

void InstructionCodeGeneratorX86_64::VisitGreaterThan(HGreaterThan* comp) {
  VisitCondition(comp);
}

void LocationsBuilderX86_64::VisitGreaterThanOrEqual(HGreaterThanOrEqual* comp) {
  VisitCondition(comp);
}

void InstructionCodeGeneratorX86_64::VisitGreaterThanOrEqual(HGreaterThanOrEqual* comp) {
  VisitCondition(comp);
}

void LocationsBuilderX86_64::VisitCompare(HCompare* compare) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(compare, LocationSummary::kNoCall);
  switch (compare->InputAt(0)->GetType()) {
    case Primitive::kPrimLong: {
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetInAt(1, Location::Any());
      locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
      break;
    }
    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble: {
      locations->SetInAt(0, Location::RequiresFpuRegister());
      locations->SetInAt(1, Location::Any());
      locations->SetOut(Location::RequiresRegister());
      break;
    }
    default:
      LOG(FATAL) << "Unexpected type for compare operation " << compare->InputAt(0)->GetType();
  }
}

void InstructionCodeGeneratorX86_64::VisitCompare(HCompare* compare) {
  LocationSummary* locations = compare->GetLocations();
  CpuRegister out = locations->Out().AsRegister<CpuRegister>();
  Location left = locations->InAt(0);
  Location right = locations->InAt(1);

  Label less, greater, done;
  Primitive::Type type = compare->InputAt(0)->GetType();
  switch (type) {
    case Primitive::kPrimLong: {
      CpuRegister left_reg = left.AsRegister<CpuRegister>();
      if (right.IsConstant()) {
        int64_t value = right.GetConstant()->AsLongConstant()->GetValue();
        if (IsInt<32>(value)) {
          if (value == 0) {
            __ testq(left_reg, left_reg);
          } else {
            __ cmpq(left_reg, Immediate(static_cast<int32_t>(value)));
          }
        } else {
          // Value won't fit in an int.
          __ cmpq(left_reg, codegen_->LiteralInt64Address(value));
        }
      } else if (right.IsDoubleStackSlot()) {
        __ cmpq(left_reg, Address(CpuRegister(RSP), right.GetStackIndex()));
      } else {
        __ cmpq(left_reg, right.AsRegister<CpuRegister>());
      }
      break;
    }
    case Primitive::kPrimFloat: {
      XmmRegister left_reg = left.AsFpuRegister<XmmRegister>();
      if (right.IsConstant()) {
        float value = right.GetConstant()->AsFloatConstant()->GetValue();
        __ ucomiss(left_reg, codegen_->LiteralFloatAddress(value));
      } else if (right.IsStackSlot()) {
        __ ucomiss(left_reg, Address(CpuRegister(RSP), right.GetStackIndex()));
      } else {
        __ ucomiss(left_reg, right.AsFpuRegister<XmmRegister>());
      }
      __ j(kUnordered, compare->IsGtBias() ? &greater : &less);
      break;
    }
    case Primitive::kPrimDouble: {
      XmmRegister left_reg = left.AsFpuRegister<XmmRegister>();
      if (right.IsConstant()) {
        double value = right.GetConstant()->AsDoubleConstant()->GetValue();
        __ ucomisd(left_reg, codegen_->LiteralDoubleAddress(value));
      } else if (right.IsDoubleStackSlot()) {
        __ ucomisd(left_reg, Address(CpuRegister(RSP), right.GetStackIndex()));
      } else {
        __ ucomisd(left_reg, right.AsFpuRegister<XmmRegister>());
      }
      __ j(kUnordered, compare->IsGtBias() ? &greater : &less);
      break;
    }
    default:
      LOG(FATAL) << "Unexpected compare type " << type;
  }
  __ movl(out, Immediate(0));
  __ j(kEqual, &done);
  __ j(type == Primitive::kPrimLong ? kLess : kBelow, &less);  //  ucomis{s,d} sets CF (kBelow)

  __ Bind(&greater);
  __ movl(out, Immediate(1));
  __ jmp(&done);

  __ Bind(&less);
  __ movl(out, Immediate(-1));

  __ Bind(&done);
}

void LocationsBuilderX86_64::VisitIntConstant(HIntConstant* constant) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(constant, LocationSummary::kNoCall);
  locations->SetOut(Location::ConstantLocation(constant));
}

void InstructionCodeGeneratorX86_64::VisitIntConstant(HIntConstant* constant) {
  // Will be generated at use site.
  UNUSED(constant);
}

void LocationsBuilderX86_64::VisitNullConstant(HNullConstant* constant) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(constant, LocationSummary::kNoCall);
  locations->SetOut(Location::ConstantLocation(constant));
}

void InstructionCodeGeneratorX86_64::VisitNullConstant(HNullConstant* constant) {
  // Will be generated at use site.
  UNUSED(constant);
}

void LocationsBuilderX86_64::VisitLongConstant(HLongConstant* constant) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(constant, LocationSummary::kNoCall);
  locations->SetOut(Location::ConstantLocation(constant));
}

void InstructionCodeGeneratorX86_64::VisitLongConstant(HLongConstant* constant) {
  // Will be generated at use site.
  UNUSED(constant);
}

void LocationsBuilderX86_64::VisitFloatConstant(HFloatConstant* constant) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(constant, LocationSummary::kNoCall);
  locations->SetOut(Location::ConstantLocation(constant));
}

void InstructionCodeGeneratorX86_64::VisitFloatConstant(HFloatConstant* constant) {
  // Will be generated at use site.
  UNUSED(constant);
}

void LocationsBuilderX86_64::VisitDoubleConstant(HDoubleConstant* constant) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(constant, LocationSummary::kNoCall);
  locations->SetOut(Location::ConstantLocation(constant));
}

void InstructionCodeGeneratorX86_64::VisitDoubleConstant(HDoubleConstant* constant) {
  // Will be generated at use site.
  UNUSED(constant);
}

void LocationsBuilderX86_64::VisitMemoryBarrier(HMemoryBarrier* memory_barrier) {
  memory_barrier->SetLocations(nullptr);
}

void InstructionCodeGeneratorX86_64::VisitMemoryBarrier(HMemoryBarrier* memory_barrier) {
  GenerateMemoryBarrier(memory_barrier->GetBarrierKind());
}

void LocationsBuilderX86_64::VisitReturnVoid(HReturnVoid* ret) {
  ret->SetLocations(nullptr);
}

void InstructionCodeGeneratorX86_64::VisitReturnVoid(HReturnVoid* ret) {
  UNUSED(ret);
  codegen_->GenerateFrameExit();
}

void LocationsBuilderX86_64::VisitReturn(HReturn* ret) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(ret, LocationSummary::kNoCall);
  switch (ret->InputAt(0)->GetType()) {
    case Primitive::kPrimBoolean:
    case Primitive::kPrimByte:
    case Primitive::kPrimChar:
    case Primitive::kPrimShort:
    case Primitive::kPrimInt:
    case Primitive::kPrimNot:
    case Primitive::kPrimLong:
      locations->SetInAt(0, Location::RegisterLocation(RAX));
      break;

    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble:
      locations->SetInAt(0, Location::FpuRegisterLocation(XMM0));
      break;

    default:
      LOG(FATAL) << "Unexpected return type " << ret->InputAt(0)->GetType();
  }
}

void InstructionCodeGeneratorX86_64::VisitReturn(HReturn* ret) {
  if (kIsDebugBuild) {
    switch (ret->InputAt(0)->GetType()) {
      case Primitive::kPrimBoolean:
      case Primitive::kPrimByte:
      case Primitive::kPrimChar:
      case Primitive::kPrimShort:
      case Primitive::kPrimInt:
      case Primitive::kPrimNot:
      case Primitive::kPrimLong:
        DCHECK_EQ(ret->GetLocations()->InAt(0).AsRegister<CpuRegister>().AsRegister(), RAX);
        break;

      case Primitive::kPrimFloat:
      case Primitive::kPrimDouble:
        DCHECK_EQ(ret->GetLocations()->InAt(0).AsFpuRegister<XmmRegister>().AsFloatRegister(),
                  XMM0);
        break;

      default:
        LOG(FATAL) << "Unexpected return type " << ret->InputAt(0)->GetType();
    }
  }
  codegen_->GenerateFrameExit();
}

Location InvokeDexCallingConventionVisitorX86_64::GetReturnLocation(Primitive::Type type) const {
  switch (type) {
    case Primitive::kPrimBoolean:
    case Primitive::kPrimByte:
    case Primitive::kPrimChar:
    case Primitive::kPrimShort:
    case Primitive::kPrimInt:
    case Primitive::kPrimNot:
    case Primitive::kPrimLong:
      return Location::RegisterLocation(RAX);

    case Primitive::kPrimVoid:
      return Location::NoLocation();

    case Primitive::kPrimDouble:
    case Primitive::kPrimFloat:
      return Location::FpuRegisterLocation(XMM0);
  }

  UNREACHABLE();
}

Location InvokeDexCallingConventionVisitorX86_64::GetMethodLocation() const {
  return Location::RegisterLocation(kMethodRegisterArgument);
}

Location InvokeDexCallingConventionVisitorX86_64::GetNextLocation(Primitive::Type type) {
  switch (type) {
    case Primitive::kPrimBoolean:
    case Primitive::kPrimByte:
    case Primitive::kPrimChar:
    case Primitive::kPrimShort:
    case Primitive::kPrimInt:
    case Primitive::kPrimNot: {
      uint32_t index = gp_index_++;
      stack_index_++;
      if (index < calling_convention.GetNumberOfRegisters()) {
        return Location::RegisterLocation(calling_convention.GetRegisterAt(index));
      } else {
        return Location::StackSlot(calling_convention.GetStackOffsetOf(stack_index_ - 1));
      }
    }

    case Primitive::kPrimLong: {
      uint32_t index = gp_index_;
      stack_index_ += 2;
      if (index < calling_convention.GetNumberOfRegisters()) {
        gp_index_ += 1;
        return Location::RegisterLocation(calling_convention.GetRegisterAt(index));
      } else {
        gp_index_ += 2;
        return Location::DoubleStackSlot(calling_convention.GetStackOffsetOf(stack_index_ - 2));
      }
    }

    case Primitive::kPrimFloat: {
      uint32_t index = float_index_++;
      stack_index_++;
      if (index < calling_convention.GetNumberOfFpuRegisters()) {
        return Location::FpuRegisterLocation(calling_convention.GetFpuRegisterAt(index));
      } else {
        return Location::StackSlot(calling_convention.GetStackOffsetOf(stack_index_ - 1));
      }
    }

    case Primitive::kPrimDouble: {
      uint32_t index = float_index_++;
      stack_index_ += 2;
      if (index < calling_convention.GetNumberOfFpuRegisters()) {
        return Location::FpuRegisterLocation(calling_convention.GetFpuRegisterAt(index));
      } else {
        return Location::DoubleStackSlot(calling_convention.GetStackOffsetOf(stack_index_ - 2));
      }
    }

    case Primitive::kPrimVoid:
      LOG(FATAL) << "Unexpected parameter type " << type;
      break;
  }
  return Location();
}

void LocationsBuilderX86_64::VisitInvokeStaticOrDirect(HInvokeStaticOrDirect* invoke) {
  // When we do not run baseline, explicit clinit checks triggered by static
  // invokes must have been pruned by art::PrepareForRegisterAllocation.
  DCHECK(codegen_->IsBaseline() || !invoke->IsStaticWithExplicitClinitCheck());

  IntrinsicLocationsBuilderX86_64 intrinsic(codegen_);
  if (intrinsic.TryDispatch(invoke)) {
    return;
  }

  HandleInvoke(invoke);
}

static bool TryGenerateIntrinsicCode(HInvoke* invoke, CodeGeneratorX86_64* codegen) {
  if (invoke->GetLocations()->Intrinsified()) {
    IntrinsicCodeGeneratorX86_64 intrinsic(codegen);
    intrinsic.Dispatch(invoke);
    return true;
  }
  return false;
}

void InstructionCodeGeneratorX86_64::VisitInvokeStaticOrDirect(HInvokeStaticOrDirect* invoke) {
  // When we do not run baseline, explicit clinit checks triggered by static
  // invokes must have been pruned by art::PrepareForRegisterAllocation.
  DCHECK(codegen_->IsBaseline() || !invoke->IsStaticWithExplicitClinitCheck());

  if (TryGenerateIntrinsicCode(invoke, codegen_)) {
    return;
  }

  LocationSummary* locations = invoke->GetLocations();
  codegen_->GenerateStaticOrDirectCall(
      invoke, locations->HasTemps() ? locations->GetTemp(0) : Location::NoLocation());
  codegen_->RecordPcInfo(invoke, invoke->GetDexPc());
}

void LocationsBuilderX86_64::HandleInvoke(HInvoke* invoke) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(invoke, LocationSummary::kCall);

  InvokeDexCallingConventionVisitorX86_64 calling_convention_visitor;
  locations->AddTemp(calling_convention_visitor.GetMethodLocation());
  for (size_t i = 0; i < invoke->GetNumberOfArguments(); i++) {
    HInstruction* input = invoke->InputAt(i);
    locations->SetInAt(i, calling_convention_visitor.GetNextLocation(input->GetType()));
  }

  switch (invoke->GetType()) {
    case Primitive::kPrimBoolean:
    case Primitive::kPrimByte:
    case Primitive::kPrimChar:
    case Primitive::kPrimShort:
    case Primitive::kPrimInt:
    case Primitive::kPrimNot:
    case Primitive::kPrimLong:
      locations->SetOut(Location::RegisterLocation(RAX));
      break;

    case Primitive::kPrimVoid:
      break;

    case Primitive::kPrimDouble:
    case Primitive::kPrimFloat:
      locations->SetOut(Location::FpuRegisterLocation(XMM0));
      break;
  }

  invoke->SetLocations(locations);
}

void LocationsBuilderX86_64::VisitInvokeVirtual(HInvokeVirtual* invoke) {
  IntrinsicLocationsBuilderX86_64 intrinsic(codegen_);
  if (intrinsic.TryDispatch(invoke)) {
    return;
  }

  HandleInvoke(invoke);
}

void InstructionCodeGeneratorX86_64::VisitInvokeVirtual(HInvokeVirtual* invoke) {
  if (TryGenerateIntrinsicCode(invoke, codegen_)) {
    return;
  }

  CpuRegister temp = invoke->GetLocations()->GetTemp(0).AsRegister<CpuRegister>();
  size_t method_offset = mirror::Class::EmbeddedVTableEntryOffset(
      invoke->GetVTableIndex(), kX86_64PointerSize).SizeValue();
  LocationSummary* locations = invoke->GetLocations();
  Location receiver = locations->InAt(0);
  size_t class_offset = mirror::Object::ClassOffset().SizeValue();
  // temp = object->GetClass();
  if (receiver.IsStackSlot()) {
    __ movl(temp, Address(CpuRegister(RSP), receiver.GetStackIndex()));
    __ movl(temp, Address(temp, class_offset));
  } else {
    __ movl(temp, Address(receiver.AsRegister<CpuRegister>(), class_offset));
  }
  codegen_->MaybeRecordImplicitNullCheck(invoke);
  // temp = temp->GetMethodAt(method_offset);
  __ movq(temp, Address(temp, method_offset));
  // call temp->GetEntryPoint();
  __ call(Address(temp, ArtMethod::EntryPointFromQuickCompiledCodeOffset(
      kX86_64WordSize).SizeValue()));

  DCHECK(!codegen_->IsLeafMethod());
  codegen_->RecordPcInfo(invoke, invoke->GetDexPc());
}

void LocationsBuilderX86_64::VisitInvokeInterface(HInvokeInterface* invoke) {
  HandleInvoke(invoke);
  // Add the hidden argument.
  invoke->GetLocations()->AddTemp(Location::RegisterLocation(RAX));
}

void InstructionCodeGeneratorX86_64::VisitInvokeInterface(HInvokeInterface* invoke) {
  // TODO: b/18116999, our IMTs can miss an IncompatibleClassChangeError.
  CpuRegister temp = invoke->GetLocations()->GetTemp(0).AsRegister<CpuRegister>();
  uint32_t method_offset = mirror::Class::EmbeddedImTableEntryOffset(
      invoke->GetImtIndex() % mirror::Class::kImtSize, kX86_64PointerSize).Uint32Value();
  LocationSummary* locations = invoke->GetLocations();
  Location receiver = locations->InAt(0);
  size_t class_offset = mirror::Object::ClassOffset().SizeValue();

  // Set the hidden argument.
  CpuRegister hidden_reg = invoke->GetLocations()->GetTemp(1).AsRegister<CpuRegister>();
  codegen_->Load64BitValue(hidden_reg, invoke->GetDexMethodIndex());

  // temp = object->GetClass();
  if (receiver.IsStackSlot()) {
    __ movl(temp, Address(CpuRegister(RSP), receiver.GetStackIndex()));
    __ movl(temp, Address(temp, class_offset));
  } else {
    __ movl(temp, Address(receiver.AsRegister<CpuRegister>(), class_offset));
  }
  codegen_->MaybeRecordImplicitNullCheck(invoke);
  // temp = temp->GetImtEntryAt(method_offset);
  __ movq(temp, Address(temp, method_offset));
  // call temp->GetEntryPoint();
  __ call(Address(temp, ArtMethod::EntryPointFromQuickCompiledCodeOffset(
      kX86_64WordSize).SizeValue()));

  DCHECK(!codegen_->IsLeafMethod());
  codegen_->RecordPcInfo(invoke, invoke->GetDexPc());
}

void LocationsBuilderX86_64::VisitNeg(HNeg* neg) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(neg, LocationSummary::kNoCall);
  switch (neg->GetResultType()) {
    case Primitive::kPrimInt:
    case Primitive::kPrimLong:
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetOut(Location::SameAsFirstInput());
      break;

    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble:
      locations->SetInAt(0, Location::RequiresFpuRegister());
      locations->SetOut(Location::SameAsFirstInput());
      locations->AddTemp(Location::RequiresFpuRegister());
      break;

    default:
      LOG(FATAL) << "Unexpected neg type " << neg->GetResultType();
  }
}

void InstructionCodeGeneratorX86_64::VisitNeg(HNeg* neg) {
  LocationSummary* locations = neg->GetLocations();
  Location out = locations->Out();
  Location in = locations->InAt(0);
  switch (neg->GetResultType()) {
    case Primitive::kPrimInt:
      DCHECK(in.IsRegister());
      DCHECK(in.Equals(out));
      __ negl(out.AsRegister<CpuRegister>());
      break;

    case Primitive::kPrimLong:
      DCHECK(in.IsRegister());
      DCHECK(in.Equals(out));
      __ negq(out.AsRegister<CpuRegister>());
      break;

    case Primitive::kPrimFloat: {
      DCHECK(in.Equals(out));
      XmmRegister mask = locations->GetTemp(0).AsFpuRegister<XmmRegister>();
      // Implement float negation with an exclusive or with value
      // 0x80000000 (mask for bit 31, representing the sign of a
      // single-precision floating-point number).
      __ movss(mask, codegen_->LiteralInt32Address(0x80000000));
      __ xorps(out.AsFpuRegister<XmmRegister>(), mask);
      break;
    }

    case Primitive::kPrimDouble: {
      DCHECK(in.Equals(out));
      XmmRegister mask = locations->GetTemp(0).AsFpuRegister<XmmRegister>();
      // Implement double negation with an exclusive or with value
      // 0x8000000000000000 (mask for bit 63, representing the sign of
      // a double-precision floating-point number).
      __ movsd(mask, codegen_->LiteralInt64Address(INT64_C(0x8000000000000000)));
      __ xorpd(out.AsFpuRegister<XmmRegister>(), mask);
      break;
    }

    default:
      LOG(FATAL) << "Unexpected neg type " << neg->GetResultType();
  }
}

void LocationsBuilderX86_64::VisitTypeConversion(HTypeConversion* conversion) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(conversion, LocationSummary::kNoCall);
  Primitive::Type result_type = conversion->GetResultType();
  Primitive::Type input_type = conversion->GetInputType();
  DCHECK_NE(result_type, input_type);

  // The Java language does not allow treating boolean as an integral type but
  // our bit representation makes it safe.

  switch (result_type) {
    case Primitive::kPrimByte:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-byte' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimShort:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-short' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimInt:
      switch (input_type) {
        case Primitive::kPrimLong:
          // Processing a Dex `long-to-int' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
          break;

        case Primitive::kPrimFloat:
          // Processing a Dex `float-to-int' instruction.
          locations->SetInAt(0, Location::RequiresFpuRegister());
          locations->SetOut(Location::RequiresRegister());
          break;

        case Primitive::kPrimDouble:
          // Processing a Dex `double-to-int' instruction.
          locations->SetInAt(0, Location::RequiresFpuRegister());
          locations->SetOut(Location::RequiresRegister());
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimLong:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-long' instruction.
          // TODO: We would benefit from a (to-be-implemented)
          // Location::RegisterOrStackSlot requirement for this input.
          locations->SetInAt(0, Location::RequiresRegister());
          locations->SetOut(Location::RequiresRegister());
          break;

        case Primitive::kPrimFloat:
          // Processing a Dex `float-to-long' instruction.
          locations->SetInAt(0, Location::RequiresFpuRegister());
          locations->SetOut(Location::RequiresRegister());
          break;

        case Primitive::kPrimDouble:
          // Processing a Dex `double-to-long' instruction.
          locations->SetInAt(0, Location::RequiresFpuRegister());
          locations->SetOut(Location::RequiresRegister());
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimChar:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
          // Processing a Dex `int-to-char' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimFloat:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-float' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresFpuRegister());
          break;

        case Primitive::kPrimLong:
          // Processing a Dex `long-to-float' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresFpuRegister());
          break;

        case Primitive::kPrimDouble:
          // Processing a Dex `double-to-float' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresFpuRegister(), Location::kNoOutputOverlap);
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      };
      break;

    case Primitive::kPrimDouble:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-double' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresFpuRegister());
          break;

        case Primitive::kPrimLong:
          // Processing a Dex `long-to-double' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresFpuRegister());
          break;

        case Primitive::kPrimFloat:
          // Processing a Dex `float-to-double' instruction.
          locations->SetInAt(0, Location::Any());
          locations->SetOut(Location::RequiresFpuRegister(), Location::kNoOutputOverlap);
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    default:
      LOG(FATAL) << "Unexpected type conversion from " << input_type
                 << " to " << result_type;
  }
}

void InstructionCodeGeneratorX86_64::VisitTypeConversion(HTypeConversion* conversion) {
  LocationSummary* locations = conversion->GetLocations();
  Location out = locations->Out();
  Location in = locations->InAt(0);
  Primitive::Type result_type = conversion->GetResultType();
  Primitive::Type input_type = conversion->GetInputType();
  DCHECK_NE(result_type, input_type);
  switch (result_type) {
    case Primitive::kPrimByte:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-byte' instruction.
          if (in.IsRegister()) {
            __ movsxb(out.AsRegister<CpuRegister>(), in.AsRegister<CpuRegister>());
          } else if (in.IsStackSlot()) {
            __ movsxb(out.AsRegister<CpuRegister>(),
                      Address(CpuRegister(RSP), in.GetStackIndex()));
          } else {
            DCHECK(in.GetConstant()->IsIntConstant());
            __ movl(out.AsRegister<CpuRegister>(),
                    Immediate(static_cast<int8_t>(in.GetConstant()->AsIntConstant()->GetValue())));
          }
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimShort:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-short' instruction.
          if (in.IsRegister()) {
            __ movsxw(out.AsRegister<CpuRegister>(), in.AsRegister<CpuRegister>());
          } else if (in.IsStackSlot()) {
            __ movsxw(out.AsRegister<CpuRegister>(),
                      Address(CpuRegister(RSP), in.GetStackIndex()));
          } else {
            DCHECK(in.GetConstant()->IsIntConstant());
            __ movl(out.AsRegister<CpuRegister>(),
                    Immediate(static_cast<int16_t>(in.GetConstant()->AsIntConstant()->GetValue())));
          }
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimInt:
      switch (input_type) {
        case Primitive::kPrimLong:
          // Processing a Dex `long-to-int' instruction.
          if (in.IsRegister()) {
            __ movl(out.AsRegister<CpuRegister>(), in.AsRegister<CpuRegister>());
          } else if (in.IsDoubleStackSlot()) {
            __ movl(out.AsRegister<CpuRegister>(),
                    Address(CpuRegister(RSP), in.GetStackIndex()));
          } else {
            DCHECK(in.IsConstant());
            DCHECK(in.GetConstant()->IsLongConstant());
            int64_t value = in.GetConstant()->AsLongConstant()->GetValue();
            __ movl(out.AsRegister<CpuRegister>(), Immediate(static_cast<int32_t>(value)));
          }
          break;

        case Primitive::kPrimFloat: {
          // Processing a Dex `float-to-int' instruction.
          XmmRegister input = in.AsFpuRegister<XmmRegister>();
          CpuRegister output = out.AsRegister<CpuRegister>();
          NearLabel done, nan;

          __ movl(output, Immediate(kPrimIntMax));
          // if input >= (float)INT_MAX goto done
          __ comiss(input, codegen_->LiteralFloatAddress(kPrimIntMax));
          __ j(kAboveEqual, &done);
          // if input == NaN goto nan
          __ j(kUnordered, &nan);
          // output = float-to-int-truncate(input)
          __ cvttss2si(output, input, false);
          __ jmp(&done);
          __ Bind(&nan);
          //  output = 0
          __ xorl(output, output);
          __ Bind(&done);
          break;
        }

        case Primitive::kPrimDouble: {
          // Processing a Dex `double-to-int' instruction.
          XmmRegister input = in.AsFpuRegister<XmmRegister>();
          CpuRegister output = out.AsRegister<CpuRegister>();
          NearLabel done, nan;

          __ movl(output, Immediate(kPrimIntMax));
          // if input >= (double)INT_MAX goto done
          __ comisd(input, codegen_->LiteralDoubleAddress(kPrimIntMax));
          __ j(kAboveEqual, &done);
          // if input == NaN goto nan
          __ j(kUnordered, &nan);
          // output = double-to-int-truncate(input)
          __ cvttsd2si(output, input);
          __ jmp(&done);
          __ Bind(&nan);
          //  output = 0
          __ xorl(output, output);
          __ Bind(&done);
          break;
        }

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimLong:
      switch (input_type) {
        DCHECK(out.IsRegister());
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-long' instruction.
          DCHECK(in.IsRegister());
          __ movsxd(out.AsRegister<CpuRegister>(), in.AsRegister<CpuRegister>());
          break;

        case Primitive::kPrimFloat: {
          // Processing a Dex `float-to-long' instruction.
          XmmRegister input = in.AsFpuRegister<XmmRegister>();
          CpuRegister output = out.AsRegister<CpuRegister>();
          NearLabel done, nan;

          codegen_->Load64BitValue(output, kPrimLongMax);
          // if input >= (float)LONG_MAX goto done
          __ comiss(input, codegen_->LiteralFloatAddress(kPrimLongMax));
          __ j(kAboveEqual, &done);
          // if input == NaN goto nan
          __ j(kUnordered, &nan);
          // output = float-to-long-truncate(input)
          __ cvttss2si(output, input, true);
          __ jmp(&done);
          __ Bind(&nan);
          //  output = 0
          __ xorl(output, output);
          __ Bind(&done);
          break;
        }

        case Primitive::kPrimDouble: {
          // Processing a Dex `double-to-long' instruction.
          XmmRegister input = in.AsFpuRegister<XmmRegister>();
          CpuRegister output = out.AsRegister<CpuRegister>();
          NearLabel done, nan;

          codegen_->Load64BitValue(output, kPrimLongMax);
          // if input >= (double)LONG_MAX goto done
          __ comisd(input, codegen_->LiteralDoubleAddress(kPrimLongMax));
          __ j(kAboveEqual, &done);
          // if input == NaN goto nan
          __ j(kUnordered, &nan);
          // output = double-to-long-truncate(input)
          __ cvttsd2si(output, input, true);
          __ jmp(&done);
          __ Bind(&nan);
          //  output = 0
          __ xorl(output, output);
          __ Bind(&done);
          break;
        }

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimChar:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
          // Processing a Dex `int-to-char' instruction.
          if (in.IsRegister()) {
            __ movzxw(out.AsRegister<CpuRegister>(), in.AsRegister<CpuRegister>());
          } else if (in.IsStackSlot()) {
            __ movzxw(out.AsRegister<CpuRegister>(),
                      Address(CpuRegister(RSP), in.GetStackIndex()));
          } else {
            DCHECK(in.GetConstant()->IsIntConstant());
            __ movl(out.AsRegister<CpuRegister>(),
                    Immediate(static_cast<uint16_t>(in.GetConstant()->AsIntConstant()->GetValue())));
          }
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      }
      break;

    case Primitive::kPrimFloat:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-float' instruction.
          if (in.IsRegister()) {
            __ cvtsi2ss(out.AsFpuRegister<XmmRegister>(), in.AsRegister<CpuRegister>(), false);
          } else if (in.IsConstant()) {
            int32_t v = in.GetConstant()->AsIntConstant()->GetValue();
            XmmRegister dest = out.AsFpuRegister<XmmRegister>();
            if (v == 0) {
              __ xorps(dest, dest);
            } else {
              __ movss(dest, codegen_->LiteralFloatAddress(static_cast<float>(v)));
            }
          } else {
            __ cvtsi2ss(out.AsFpuRegister<XmmRegister>(),
                        Address(CpuRegister(RSP), in.GetStackIndex()), false);
          }
          break;

        case Primitive::kPrimLong:
          // Processing a Dex `long-to-float' instruction.
          if (in.IsRegister()) {
            __ cvtsi2ss(out.AsFpuRegister<XmmRegister>(), in.AsRegister<CpuRegister>(), true);
          } else if (in.IsConstant()) {
            int64_t v = in.GetConstant()->AsLongConstant()->GetValue();
            XmmRegister dest = out.AsFpuRegister<XmmRegister>();
            if (v == 0) {
              __ xorps(dest, dest);
            } else {
              __ movss(dest, codegen_->LiteralFloatAddress(static_cast<float>(v)));
            }
          } else {
            __ cvtsi2ss(out.AsFpuRegister<XmmRegister>(),
                        Address(CpuRegister(RSP), in.GetStackIndex()), true);
          }
          break;

        case Primitive::kPrimDouble:
          // Processing a Dex `double-to-float' instruction.
          if (in.IsFpuRegister()) {
            __ cvtsd2ss(out.AsFpuRegister<XmmRegister>(), in.AsFpuRegister<XmmRegister>());
          } else if (in.IsConstant()) {
            double v = in.GetConstant()->AsDoubleConstant()->GetValue();
            XmmRegister dest = out.AsFpuRegister<XmmRegister>();
            if (bit_cast<int64_t, double>(v) == 0) {
              __ xorps(dest, dest);
            } else {
              __ movss(dest, codegen_->LiteralFloatAddress(static_cast<float>(v)));
            }
          } else {
            __ cvtsd2ss(out.AsFpuRegister<XmmRegister>(),
                        Address(CpuRegister(RSP), in.GetStackIndex()));
          }
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      };
      break;

    case Primitive::kPrimDouble:
      switch (input_type) {
        case Primitive::kPrimBoolean:
          // Boolean input is a result of code transformations.
        case Primitive::kPrimByte:
        case Primitive::kPrimShort:
        case Primitive::kPrimInt:
        case Primitive::kPrimChar:
          // Processing a Dex `int-to-double' instruction.
          if (in.IsRegister()) {
            __ cvtsi2sd(out.AsFpuRegister<XmmRegister>(), in.AsRegister<CpuRegister>(), false);
          } else if (in.IsConstant()) {
            int32_t v = in.GetConstant()->AsIntConstant()->GetValue();
            XmmRegister dest = out.AsFpuRegister<XmmRegister>();
            if (v == 0) {
              __ xorpd(dest, dest);
            } else {
              __ movsd(dest, codegen_->LiteralDoubleAddress(static_cast<double>(v)));
            }
          } else {
            __ cvtsi2sd(out.AsFpuRegister<XmmRegister>(),
                        Address(CpuRegister(RSP), in.GetStackIndex()), false);
          }
          break;

        case Primitive::kPrimLong:
          // Processing a Dex `long-to-double' instruction.
          if (in.IsRegister()) {
            __ cvtsi2sd(out.AsFpuRegister<XmmRegister>(), in.AsRegister<CpuRegister>(), true);
          } else if (in.IsConstant()) {
            int64_t v = in.GetConstant()->AsLongConstant()->GetValue();
            XmmRegister dest = out.AsFpuRegister<XmmRegister>();
            if (v == 0) {
              __ xorpd(dest, dest);
            } else {
              __ movsd(dest, codegen_->LiteralDoubleAddress(static_cast<double>(v)));
            }
          } else {
            __ cvtsi2sd(out.AsFpuRegister<XmmRegister>(),
                        Address(CpuRegister(RSP), in.GetStackIndex()), true);
          }
          break;

        case Primitive::kPrimFloat:
          // Processing a Dex `float-to-double' instruction.
          if (in.IsFpuRegister()) {
            __ cvtss2sd(out.AsFpuRegister<XmmRegister>(), in.AsFpuRegister<XmmRegister>());
          } else if (in.IsConstant()) {
            float v = in.GetConstant()->AsFloatConstant()->GetValue();
            XmmRegister dest = out.AsFpuRegister<XmmRegister>();
            if (bit_cast<int32_t, float>(v) == 0) {
              __ xorpd(dest, dest);
            } else {
              __ movsd(dest, codegen_->LiteralDoubleAddress(static_cast<double>(v)));
            }
          } else {
            __ cvtss2sd(out.AsFpuRegister<XmmRegister>(),
                        Address(CpuRegister(RSP), in.GetStackIndex()));
          }
          break;

        default:
          LOG(FATAL) << "Unexpected type conversion from " << input_type
                     << " to " << result_type;
      };
      break;

    default:
      LOG(FATAL) << "Unexpected type conversion from " << input_type
                 << " to " << result_type;
  }
}

void LocationsBuilderX86_64::VisitAdd(HAdd* add) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(add, LocationSummary::kNoCall);
  switch (add->GetResultType()) {
    case Primitive::kPrimInt: {
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetInAt(1, Location::RegisterOrConstant(add->InputAt(1)));
      locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
      break;
    }

    case Primitive::kPrimLong: {
      locations->SetInAt(0, Location::RequiresRegister());
      // We can use a leaq or addq if the constant can fit in an immediate.
      locations->SetInAt(1, Location::RegisterOrInt32LongConstant(add->InputAt(1)));
      locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
      break;
    }

    case Primitive::kPrimDouble:
    case Primitive::kPrimFloat: {
      locations->SetInAt(0, Location::RequiresFpuRegister());
      locations->SetInAt(1, Location::Any());
      locations->SetOut(Location::SameAsFirstInput());
      break;
    }

    default:
      LOG(FATAL) << "Unexpected add type " << add->GetResultType();
  }
}

void InstructionCodeGeneratorX86_64::VisitAdd(HAdd* add) {
  LocationSummary* locations = add->GetLocations();
  Location first = locations->InAt(0);
  Location second = locations->InAt(1);
  Location out = locations->Out();

  switch (add->GetResultType()) {
    case Primitive::kPrimInt: {
      if (second.IsRegister()) {
        if (out.AsRegister<Register>() == first.AsRegister<Register>()) {
          __ addl(out.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
        } else {
          __ leal(out.AsRegister<CpuRegister>(), Address(
              first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>(), TIMES_1, 0));
        }
      } else if (second.IsConstant()) {
        if (out.AsRegister<Register>() == first.AsRegister<Register>()) {
          __ addl(out.AsRegister<CpuRegister>(),
                  Immediate(second.GetConstant()->AsIntConstant()->GetValue()));
        } else {
          __ leal(out.AsRegister<CpuRegister>(), Address(
              first.AsRegister<CpuRegister>(), second.GetConstant()->AsIntConstant()->GetValue()));
        }
      } else {
        DCHECK(first.Equals(locations->Out()));
        __ addl(first.AsRegister<CpuRegister>(), Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    case Primitive::kPrimLong: {
      if (second.IsRegister()) {
        if (out.AsRegister<Register>() == first.AsRegister<Register>()) {
          __ addq(out.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
        } else {
          __ leaq(out.AsRegister<CpuRegister>(), Address(
              first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>(), TIMES_1, 0));
        }
      } else {
        DCHECK(second.IsConstant());
        int64_t value = second.GetConstant()->AsLongConstant()->GetValue();
        int32_t int32_value = Low32Bits(value);
        DCHECK_EQ(int32_value, value);
        if (out.AsRegister<Register>() == first.AsRegister<Register>()) {
          __ addq(out.AsRegister<CpuRegister>(), Immediate(int32_value));
        } else {
          __ leaq(out.AsRegister<CpuRegister>(), Address(
              first.AsRegister<CpuRegister>(), int32_value));
        }
      }
      break;
    }

    case Primitive::kPrimFloat: {
      if (second.IsFpuRegister()) {
        __ addss(first.AsFpuRegister<XmmRegister>(), second.AsFpuRegister<XmmRegister>());
      } else if (second.IsConstant()) {
        __ addss(first.AsFpuRegister<XmmRegister>(),
                 codegen_->LiteralFloatAddress(second.GetConstant()->AsFloatConstant()->GetValue()));
      } else {
        DCHECK(second.IsStackSlot());
        __ addss(first.AsFpuRegister<XmmRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    case Primitive::kPrimDouble: {
      if (second.IsFpuRegister()) {
        __ addsd(first.AsFpuRegister<XmmRegister>(), second.AsFpuRegister<XmmRegister>());
      } else if (second.IsConstant()) {
        __ addsd(first.AsFpuRegister<XmmRegister>(),
                 codegen_->LiteralDoubleAddress(second.GetConstant()->AsDoubleConstant()->GetValue()));
      } else {
        DCHECK(second.IsDoubleStackSlot());
        __ addsd(first.AsFpuRegister<XmmRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    default:
      LOG(FATAL) << "Unexpected add type " << add->GetResultType();
  }
}

void LocationsBuilderX86_64::VisitSub(HSub* sub) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(sub, LocationSummary::kNoCall);
  switch (sub->GetResultType()) {
    case Primitive::kPrimInt: {
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetInAt(1, Location::Any());
      locations->SetOut(Location::SameAsFirstInput());
      break;
    }
    case Primitive::kPrimLong: {
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetInAt(1, Location::RegisterOrInt32LongConstant(sub->InputAt(1)));
      locations->SetOut(Location::SameAsFirstInput());
      break;
    }
    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble: {
      locations->SetInAt(0, Location::RequiresFpuRegister());
      locations->SetInAt(1, Location::Any());
      locations->SetOut(Location::SameAsFirstInput());
      break;
    }
    default:
      LOG(FATAL) << "Unexpected sub type " << sub->GetResultType();
  }
}

void InstructionCodeGeneratorX86_64::VisitSub(HSub* sub) {
  LocationSummary* locations = sub->GetLocations();
  Location first = locations->InAt(0);
  Location second = locations->InAt(1);
  DCHECK(first.Equals(locations->Out()));
  switch (sub->GetResultType()) {
    case Primitive::kPrimInt: {
      if (second.IsRegister()) {
        __ subl(first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
      } else if (second.IsConstant()) {
        Immediate imm(second.GetConstant()->AsIntConstant()->GetValue());
        __ subl(first.AsRegister<CpuRegister>(), imm);
      } else {
        __ subl(first.AsRegister<CpuRegister>(), Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }
    case Primitive::kPrimLong: {
      if (second.IsConstant()) {
        int64_t value = second.GetConstant()->AsLongConstant()->GetValue();
        DCHECK(IsInt<32>(value));
        __ subq(first.AsRegister<CpuRegister>(), Immediate(static_cast<int32_t>(value)));
      } else {
        __ subq(first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
      }
      break;
    }

    case Primitive::kPrimFloat: {
      if (second.IsFpuRegister()) {
        __ subss(first.AsFpuRegister<XmmRegister>(), second.AsFpuRegister<XmmRegister>());
      } else if (second.IsConstant()) {
        __ subss(first.AsFpuRegister<XmmRegister>(),
                 codegen_->LiteralFloatAddress(second.GetConstant()->AsFloatConstant()->GetValue()));
      } else {
        DCHECK(second.IsStackSlot());
        __ subss(first.AsFpuRegister<XmmRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    case Primitive::kPrimDouble: {
      if (second.IsFpuRegister()) {
        __ subsd(first.AsFpuRegister<XmmRegister>(), second.AsFpuRegister<XmmRegister>());
      } else if (second.IsConstant()) {
        __ subsd(first.AsFpuRegister<XmmRegister>(),
                 codegen_->LiteralDoubleAddress(second.GetConstant()->AsDoubleConstant()->GetValue()));
      } else {
        DCHECK(second.IsDoubleStackSlot());
        __ subsd(first.AsFpuRegister<XmmRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    default:
      LOG(FATAL) << "Unexpected sub type " << sub->GetResultType();
  }
}

void LocationsBuilderX86_64::VisitMul(HMul* mul) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(mul, LocationSummary::kNoCall);
  switch (mul->GetResultType()) {
    case Primitive::kPrimInt: {
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetInAt(1, Location::Any());
      if (mul->InputAt(1)->IsIntConstant()) {
        // Can use 3 operand multiply.
        locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
      } else {
        locations->SetOut(Location::SameAsFirstInput());
      }
      break;
    }
    case Primitive::kPrimLong: {
      locations->SetInAt(0, Location::RequiresRegister());
      locations->SetInAt(1, Location::Any());
      if (mul->InputAt(1)->IsLongConstant() &&
          IsInt<32>(mul->InputAt(1)->AsLongConstant()->GetValue())) {
        // Can use 3 operand multiply.
        locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
      } else {
        locations->SetOut(Location::SameAsFirstInput());
      }
      break;
    }
    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble: {
      locations->SetInAt(0, Location::RequiresFpuRegister());
      locations->SetInAt(1, Location::Any());
      locations->SetOut(Location::SameAsFirstInput());
      break;
    }

    default:
      LOG(FATAL) << "Unexpected mul type " << mul->GetResultType();
  }
}

void InstructionCodeGeneratorX86_64::VisitMul(HMul* mul) {
  LocationSummary* locations = mul->GetLocations();
  Location first = locations->InAt(0);
  Location second = locations->InAt(1);
  Location out = locations->Out();
  switch (mul->GetResultType()) {
    case Primitive::kPrimInt:
      // The constant may have ended up in a register, so test explicitly to avoid
      // problems where the output may not be the same as the first operand.
      if (mul->InputAt(1)->IsIntConstant()) {
        Immediate imm(mul->InputAt(1)->AsIntConstant()->GetValue());
        __ imull(out.AsRegister<CpuRegister>(), first.AsRegister<CpuRegister>(), imm);
      } else if (second.IsRegister()) {
        DCHECK(first.Equals(out));
        __ imull(first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
      } else {
        DCHECK(first.Equals(out));
        DCHECK(second.IsStackSlot());
        __ imull(first.AsRegister<CpuRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    case Primitive::kPrimLong: {
      // The constant may have ended up in a register, so test explicitly to avoid
      // problems where the output may not be the same as the first operand.
      if (mul->InputAt(1)->IsLongConstant()) {
        int64_t value = mul->InputAt(1)->AsLongConstant()->GetValue();
        if (IsInt<32>(value)) {
          __ imulq(out.AsRegister<CpuRegister>(), first.AsRegister<CpuRegister>(),
                   Immediate(static_cast<int32_t>(value)));
        } else {
          // Have to use the constant area.
          DCHECK(first.Equals(out));
          __ imulq(first.AsRegister<CpuRegister>(), codegen_->LiteralInt64Address(value));
        }
      } else if (second.IsRegister()) {
        DCHECK(first.Equals(out));
        __ imulq(first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
      } else {
        DCHECK(second.IsDoubleStackSlot());
        DCHECK(first.Equals(out));
        __ imulq(first.AsRegister<CpuRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    case Primitive::kPrimFloat: {
      DCHECK(first.Equals(out));
      if (second.IsFpuRegister()) {
        __ mulss(first.AsFpuRegister<XmmRegister>(), second.AsFpuRegister<XmmRegister>());
      } else if (second.IsConstant()) {
        __ mulss(first.AsFpuRegister<XmmRegister>(),
                 codegen_->LiteralFloatAddress(second.GetConstant()->AsFloatConstant()->GetValue()));
      } else {
        DCHECK(second.IsStackSlot());
        __ mulss(first.AsFpuRegister<XmmRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    case Primitive::kPrimDouble: {
      DCHECK(first.Equals(out));
      if (second.IsFpuRegister()) {
        __ mulsd(first.AsFpuRegister<XmmRegister>(), second.AsFpuRegister<XmmRegister>());
      } else if (second.IsConstant()) {
        __ mulsd(first.AsFpuRegister<XmmRegister>(),
                 codegen_->LiteralDoubleAddress(second.GetConstant()->AsDoubleConstant()->GetValue()));
      } else {
        DCHECK(second.IsDoubleStackSlot());
        __ mulsd(first.AsFpuRegister<XmmRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    default:
      LOG(FATAL) << "Unexpected mul type " << mul->GetResultType();
  }
}

void InstructionCodeGeneratorX86_64::PushOntoFPStack(Location source, uint32_t temp_offset,
                                                     uint32_t stack_adjustment, bool is_float) {
  if (source.IsStackSlot()) {
    DCHECK(is_float);
    __ flds(Address(CpuRegister(RSP), source.GetStackIndex() + stack_adjustment));
  } else if (source.IsDoubleStackSlot()) {
    DCHECK(!is_float);
    __ fldl(Address(CpuRegister(RSP), source.GetStackIndex() + stack_adjustment));
  } else {
    // Write the value to the temporary location on the stack and load to FP stack.
    if (is_float) {
      Location stack_temp = Location::StackSlot(temp_offset);
      codegen_->Move(stack_temp, source);
      __ flds(Address(CpuRegister(RSP), temp_offset));
    } else {
      Location stack_temp = Location::DoubleStackSlot(temp_offset);
      codegen_->Move(stack_temp, source);
      __ fldl(Address(CpuRegister(RSP), temp_offset));
    }
  }
}

void InstructionCodeGeneratorX86_64::GenerateRemFP(HRem *rem) {
  Primitive::Type type = rem->GetResultType();
  bool is_float = type == Primitive::kPrimFloat;
  size_t elem_size = Primitive::ComponentSize(type);
  LocationSummary* locations = rem->GetLocations();
  Location first = locations->InAt(0);
  Location second = locations->InAt(1);
  Location out = locations->Out();

  // Create stack space for 2 elements.
  // TODO: enhance register allocator to ask for stack temporaries.
  __ subq(CpuRegister(RSP), Immediate(2 * elem_size));

  // Load the values to the FP stack in reverse order, using temporaries if needed.
  PushOntoFPStack(second, elem_size, 2 * elem_size, is_float);
  PushOntoFPStack(first, 0, 2 * elem_size, is_float);

  // Loop doing FPREM until we stabilize.
  NearLabel retry;
  __ Bind(&retry);
  __ fprem();

  // Move FP status to AX.
  __ fstsw();

  // And see if the argument reduction is complete. This is signaled by the
  // C2 FPU flag bit set to 0.
  __ andl(CpuRegister(RAX), Immediate(kC2ConditionMask));
  __ j(kNotEqual, &retry);

  // We have settled on the final value. Retrieve it into an XMM register.
  // Store FP top of stack to real stack.
  if (is_float) {
    __ fsts(Address(CpuRegister(RSP), 0));
  } else {
    __ fstl(Address(CpuRegister(RSP), 0));
  }

  // Pop the 2 items from the FP stack.
  __ fucompp();

  // Load the value from the stack into an XMM register.
  DCHECK(out.IsFpuRegister()) << out;
  if (is_float) {
    __ movss(out.AsFpuRegister<XmmRegister>(), Address(CpuRegister(RSP), 0));
  } else {
    __ movsd(out.AsFpuRegister<XmmRegister>(), Address(CpuRegister(RSP), 0));
  }

  // And remove the temporary stack space we allocated.
  __ addq(CpuRegister(RSP), Immediate(2 * elem_size));
}

void InstructionCodeGeneratorX86_64::DivRemOneOrMinusOne(HBinaryOperation* instruction) {
  DCHECK(instruction->IsDiv() || instruction->IsRem());

  LocationSummary* locations = instruction->GetLocations();
  Location second = locations->InAt(1);
  DCHECK(second.IsConstant());

  CpuRegister output_register = locations->Out().AsRegister<CpuRegister>();
  CpuRegister input_register = locations->InAt(0).AsRegister<CpuRegister>();
  int64_t imm = Int64FromConstant(second.GetConstant());

  DCHECK(imm == 1 || imm == -1);

  switch (instruction->GetResultType()) {
    case Primitive::kPrimInt: {
      if (instruction->IsRem()) {
        __ xorl(output_register, output_register);
      } else {
        __ movl(output_register, input_register);
        if (imm == -1) {
          __ negl(output_register);
        }
      }
      break;
    }

    case Primitive::kPrimLong: {
      if (instruction->IsRem()) {
        __ xorl(output_register, output_register);
      } else {
        __ movq(output_register, input_register);
        if (imm == -1) {
          __ negq(output_register);
        }
      }
      break;
    }

    default:
      LOG(FATAL) << "Unexpected type for div by (-)1 " << instruction->GetResultType();
  }
}

void InstructionCodeGeneratorX86_64::DivByPowerOfTwo(HDiv* instruction) {
  LocationSummary* locations = instruction->GetLocations();
  Location second = locations->InAt(1);

  CpuRegister output_register = locations->Out().AsRegister<CpuRegister>();
  CpuRegister numerator = locations->InAt(0).AsRegister<CpuRegister>();

  int64_t imm = Int64FromConstant(second.GetConstant());

  DCHECK(IsPowerOfTwo(std::abs(imm)));

  CpuRegister tmp = locations->GetTemp(0).AsRegister<CpuRegister>();

  if (instruction->GetResultType() == Primitive::kPrimInt) {
    __ leal(tmp, Address(numerator, std::abs(imm) - 1));
    __ testl(numerator, numerator);
    __ cmov(kGreaterEqual, tmp, numerator);
    int shift = CTZ(imm);
    __ sarl(tmp, Immediate(shift));

    if (imm < 0) {
      __ negl(tmp);
    }

    __ movl(output_register, tmp);
  } else {
    DCHECK_EQ(instruction->GetResultType(), Primitive::kPrimLong);
    CpuRegister rdx = locations->GetTemp(0).AsRegister<CpuRegister>();

    codegen_->Load64BitValue(rdx, std::abs(imm) - 1);
    __ addq(rdx, numerator);
    __ testq(numerator, numerator);
    __ cmov(kGreaterEqual, rdx, numerator);
    int shift = CTZ(imm);
    __ sarq(rdx, Immediate(shift));

    if (imm < 0) {
      __ negq(rdx);
    }

    __ movq(output_register, rdx);
  }
}

void InstructionCodeGeneratorX86_64::GenerateDivRemWithAnyConstant(HBinaryOperation* instruction) {
  DCHECK(instruction->IsDiv() || instruction->IsRem());

  LocationSummary* locations = instruction->GetLocations();
  Location second = locations->InAt(1);

  CpuRegister numerator = instruction->IsDiv() ? locations->GetTemp(1).AsRegister<CpuRegister>()
      : locations->GetTemp(0).AsRegister<CpuRegister>();
  CpuRegister eax = locations->InAt(0).AsRegister<CpuRegister>();
  CpuRegister edx = instruction->IsDiv() ? locations->GetTemp(0).AsRegister<CpuRegister>()
      : locations->Out().AsRegister<CpuRegister>();
  CpuRegister out = locations->Out().AsRegister<CpuRegister>();

  DCHECK_EQ(RAX, eax.AsRegister());
  DCHECK_EQ(RDX, edx.AsRegister());
  if (instruction->IsDiv()) {
    DCHECK_EQ(RAX, out.AsRegister());
  } else {
    DCHECK_EQ(RDX, out.AsRegister());
  }

  int64_t magic;
  int shift;

  // TODO: can these branches be written as one?
  if (instruction->GetResultType() == Primitive::kPrimInt) {
    int imm = second.GetConstant()->AsIntConstant()->GetValue();

    CalculateMagicAndShiftForDivRem(imm, false /* is_long */, &magic, &shift);

    __ movl(numerator, eax);

    NearLabel no_div;
    NearLabel end;
    __ testl(eax, eax);
    __ j(kNotEqual, &no_div);

    __ xorl(out, out);
    __ jmp(&end);

    __ Bind(&no_div);

    __ movl(eax, Immediate(magic));
    __ imull(numerator);

    if (imm > 0 && magic < 0) {
      __ addl(edx, numerator);
    } else if (imm < 0 && magic > 0) {
      __ subl(edx, numerator);
    }

    if (shift != 0) {
      __ sarl(edx, Immediate(shift));
    }

    __ movl(eax, edx);
    __ shrl(edx, Immediate(31));
    __ addl(edx, eax);

    if (instruction->IsRem()) {
      __ movl(eax, numerator);
      __ imull(edx, Immediate(imm));
      __ subl(eax, edx);
      __ movl(edx, eax);
    } else {
      __ movl(eax, edx);
    }
    __ Bind(&end);
  } else {
    int64_t imm = second.GetConstant()->AsLongConstant()->GetValue();

    DCHECK_EQ(instruction->GetResultType(), Primitive::kPrimLong);

    CpuRegister rax = eax;
    CpuRegister rdx = edx;

    CalculateMagicAndShiftForDivRem(imm, true /* is_long */, &magic, &shift);

    // Save the numerator.
    __ movq(numerator, rax);

    // RAX = magic
    codegen_->Load64BitValue(rax, magic);

    // RDX:RAX = magic * numerator
    __ imulq(numerator);

    if (imm > 0 && magic < 0) {
      // RDX += numerator
      __ addq(rdx, numerator);
    } else if (imm < 0 && magic > 0) {
      // RDX -= numerator
      __ subq(rdx, numerator);
    }

    // Shift if needed.
    if (shift != 0) {
      __ sarq(rdx, Immediate(shift));
    }

    // RDX += 1 if RDX < 0
    __ movq(rax, rdx);
    __ shrq(rdx, Immediate(63));
    __ addq(rdx, rax);

    if (instruction->IsRem()) {
      __ movq(rax, numerator);

      if (IsInt<32>(imm)) {
        __ imulq(rdx, Immediate(static_cast<int32_t>(imm)));
      } else {
        __ imulq(rdx, codegen_->LiteralInt64Address(imm));
      }

      __ subq(rax, rdx);
      __ movq(rdx, rax);
    } else {
      __ movq(rax, rdx);
    }
  }
}

void InstructionCodeGeneratorX86_64::GenerateDivRemIntegral(HBinaryOperation* instruction) {
  DCHECK(instruction->IsDiv() || instruction->IsRem());
  Primitive::Type type = instruction->GetResultType();
  DCHECK(type == Primitive::kPrimInt || Primitive::kPrimLong);

  bool is_div = instruction->IsDiv();
  LocationSummary* locations = instruction->GetLocations();

  CpuRegister out = locations->Out().AsRegister<CpuRegister>();
  Location second = locations->InAt(1);

  DCHECK_EQ(RAX, locations->InAt(0).AsRegister<CpuRegister>().AsRegister());
  DCHECK_EQ(is_div ? RAX : RDX, out.AsRegister());

  if (second.IsConstant()) {
    int64_t imm = Int64FromConstant(second.GetConstant());

    if (imm == 0) {
      // Do not generate anything. DivZeroCheck would prevent any code to be executed.
    } else if (imm == 1 || imm == -1) {
      DivRemOneOrMinusOne(instruction);
    } else if (instruction->IsDiv() && IsPowerOfTwo(std::abs(imm))) {
      DivByPowerOfTwo(instruction->AsDiv());
    } else {
      DCHECK(imm <= -2 || imm >= 2);
      GenerateDivRemWithAnyConstant(instruction);
    }
  } else {
    SlowPathCodeX86_64* slow_path =
        new (GetGraph()->GetArena()) DivRemMinusOneSlowPathX86_64(
            out.AsRegister(), type, is_div);
    codegen_->AddSlowPath(slow_path);

    CpuRegister second_reg = second.AsRegister<CpuRegister>();
    // 0x80000000(00000000)/-1 triggers an arithmetic exception!
    // Dividing by -1 is actually negation and -0x800000000(00000000) = 0x80000000(00000000)
    // so it's safe to just use negl instead of more complex comparisons.
    if (type == Primitive::kPrimInt) {
      __ cmpl(second_reg, Immediate(-1));
      __ j(kEqual, slow_path->GetEntryLabel());
      // edx:eax <- sign-extended of eax
      __ cdq();
      // eax = quotient, edx = remainder
      __ idivl(second_reg);
    } else {
      __ cmpq(second_reg, Immediate(-1));
      __ j(kEqual, slow_path->GetEntryLabel());
      // rdx:rax <- sign-extended of rax
      __ cqo();
      // rax = quotient, rdx = remainder
      __ idivq(second_reg);
    }
    __ Bind(slow_path->GetExitLabel());
  }
}

void LocationsBuilderX86_64::VisitDiv(HDiv* div) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(div, LocationSummary::kNoCall);
  switch (div->GetResultType()) {
    case Primitive::kPrimInt:
    case Primitive::kPrimLong: {
      locations->SetInAt(0, Location::RegisterLocation(RAX));
      locations->SetInAt(1, Location::RegisterOrConstant(div->InputAt(1)));
      locations->SetOut(Location::SameAsFirstInput());
      // Intel uses edx:eax as the dividend.
      locations->AddTemp(Location::RegisterLocation(RDX));
      // We need to save the numerator while we tweak rax and rdx. As we are using imul in a way
      // which enforces results to be in RAX and RDX, things are simpler if we use RDX also as
      // output and request another temp.
      if (div->InputAt(1)->IsConstant()) {
        locations->AddTemp(Location::RequiresRegister());
      }
      break;
    }

    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble: {
      locations->SetInAt(0, Location::RequiresFpuRegister());
      locations->SetInAt(1, Location::Any());
      locations->SetOut(Location::SameAsFirstInput());
      break;
    }

    default:
      LOG(FATAL) << "Unexpected div type " << div->GetResultType();
  }
}

void InstructionCodeGeneratorX86_64::VisitDiv(HDiv* div) {
  LocationSummary* locations = div->GetLocations();
  Location first = locations->InAt(0);
  Location second = locations->InAt(1);
  DCHECK(first.Equals(locations->Out()));

  Primitive::Type type = div->GetResultType();
  switch (type) {
    case Primitive::kPrimInt:
    case Primitive::kPrimLong: {
      GenerateDivRemIntegral(div);
      break;
    }

    case Primitive::kPrimFloat: {
      if (second.IsFpuRegister()) {
        __ divss(first.AsFpuRegister<XmmRegister>(), second.AsFpuRegister<XmmRegister>());
      } else if (second.IsConstant()) {
        __ divss(first.AsFpuRegister<XmmRegister>(),
                 codegen_->LiteralFloatAddress(second.GetConstant()->AsFloatConstant()->GetValue()));
      } else {
        DCHECK(second.IsStackSlot());
        __ divss(first.AsFpuRegister<XmmRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    case Primitive::kPrimDouble: {
      if (second.IsFpuRegister()) {
        __ divsd(first.AsFpuRegister<XmmRegister>(), second.AsFpuRegister<XmmRegister>());
      } else if (second.IsConstant()) {
        __ divsd(first.AsFpuRegister<XmmRegister>(),
                 codegen_->LiteralDoubleAddress(second.GetConstant()->AsDoubleConstant()->GetValue()));
      } else {
        DCHECK(second.IsDoubleStackSlot());
        __ divsd(first.AsFpuRegister<XmmRegister>(),
                 Address(CpuRegister(RSP), second.GetStackIndex()));
      }
      break;
    }

    default:
      LOG(FATAL) << "Unexpected div type " << div->GetResultType();
  }
}

void LocationsBuilderX86_64::VisitRem(HRem* rem) {
  Primitive::Type type = rem->GetResultType();
  LocationSummary* locations =
    new (GetGraph()->GetArena()) LocationSummary(rem, LocationSummary::kNoCall);

  switch (type) {
    case Primitive::kPrimInt:
    case Primitive::kPrimLong: {
      locations->SetInAt(0, Location::RegisterLocation(RAX));
      locations->SetInAt(1, Location::RegisterOrConstant(rem->InputAt(1)));
      // Intel uses rdx:rax as the dividend and puts the remainder in rdx
      locations->SetOut(Location::RegisterLocation(RDX));
      // We need to save the numerator while we tweak eax and edx. As we are using imul in a way
      // which enforces results to be in RAX and RDX, things are simpler if we use EAX also as
      // output and request another temp.
      if (rem->InputAt(1)->IsConstant()) {
        locations->AddTemp(Location::RequiresRegister());
      }
      break;
    }

    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble: {
      locations->SetInAt(0, Location::Any());
      locations->SetInAt(1, Location::Any());
      locations->SetOut(Location::RequiresFpuRegister());
      locations->AddTemp(Location::RegisterLocation(RAX));
      break;
    }

    default:
      LOG(FATAL) << "Unexpected rem type " << type;
  }
}

void InstructionCodeGeneratorX86_64::VisitRem(HRem* rem) {
  Primitive::Type type = rem->GetResultType();
  switch (type) {
    case Primitive::kPrimInt:
    case Primitive::kPrimLong: {
      GenerateDivRemIntegral(rem);
      break;
    }
    case Primitive::kPrimFloat:
    case Primitive::kPrimDouble: {
      GenerateRemFP(rem);
      break;
    }
    default:
      LOG(FATAL) << "Unexpected rem type " << rem->GetResultType();
  }
}

void LocationsBuilderX86_64::VisitDivZeroCheck(HDivZeroCheck* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  locations->SetInAt(0, Location::Any());
  if (instruction->HasUses()) {
    locations->SetOut(Location::SameAsFirstInput());
  }
}

void InstructionCodeGeneratorX86_64::VisitDivZeroCheck(HDivZeroCheck* instruction) {
  SlowPathCodeX86_64* slow_path =
      new (GetGraph()->GetArena()) DivZeroCheckSlowPathX86_64(instruction);
  codegen_->AddSlowPath(slow_path);

  LocationSummary* locations = instruction->GetLocations();
  Location value = locations->InAt(0);

  switch (instruction->GetType()) {
    case Primitive::kPrimByte:
    case Primitive::kPrimChar:
    case Primitive::kPrimShort:
    case Primitive::kPrimInt: {
      if (value.IsRegister()) {
        __ testl(value.AsRegister<CpuRegister>(), value.AsRegister<CpuRegister>());
        __ j(kEqual, slow_path->GetEntryLabel());
      } else if (value.IsStackSlot()) {
        __ cmpl(Address(CpuRegister(RSP), value.GetStackIndex()), Immediate(0));
        __ j(kEqual, slow_path->GetEntryLabel());
      } else {
        DCHECK(value.IsConstant()) << value;
        if (value.GetConstant()->AsIntConstant()->GetValue() == 0) {
        __ jmp(slow_path->GetEntryLabel());
        }
      }
      break;
    }
    case Primitive::kPrimLong: {
      if (value.IsRegister()) {
        __ testq(value.AsRegister<CpuRegister>(), value.AsRegister<CpuRegister>());
        __ j(kEqual, slow_path->GetEntryLabel());
      } else if (value.IsDoubleStackSlot()) {
        __ cmpq(Address(CpuRegister(RSP), value.GetStackIndex()), Immediate(0));
        __ j(kEqual, slow_path->GetEntryLabel());
      } else {
        DCHECK(value.IsConstant()) << value;
        if (value.GetConstant()->AsLongConstant()->GetValue() == 0) {
        __ jmp(slow_path->GetEntryLabel());
        }
      }
      break;
    }
    default:
      LOG(FATAL) << "Unexpected type for HDivZeroCheck " << instruction->GetType();
  }
}

void LocationsBuilderX86_64::HandleShift(HBinaryOperation* op) {
  DCHECK(op->IsShl() || op->IsShr() || op->IsUShr());

  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(op, LocationSummary::kNoCall);

  switch (op->GetResultType()) {
    case Primitive::kPrimInt:
    case Primitive::kPrimLong: {
      locations->SetInAt(0, Location::RequiresRegister());
      // The shift count needs to be in CL.
      locations->SetInAt(1, Location::ByteRegisterOrConstant(RCX, op->InputAt(1)));
      locations->SetOut(Location::SameAsFirstInput());
      break;
    }
    default:
      LOG(FATAL) << "Unexpected operation type " << op->GetResultType();
  }
}

void InstructionCodeGeneratorX86_64::HandleShift(HBinaryOperation* op) {
  DCHECK(op->IsShl() || op->IsShr() || op->IsUShr());

  LocationSummary* locations = op->GetLocations();
  CpuRegister first_reg = locations->InAt(0).AsRegister<CpuRegister>();
  Location second = locations->InAt(1);

  switch (op->GetResultType()) {
    case Primitive::kPrimInt: {
      if (second.IsRegister()) {
        CpuRegister second_reg = second.AsRegister<CpuRegister>();
        if (op->IsShl()) {
          __ shll(first_reg, second_reg);
        } else if (op->IsShr()) {
          __ sarl(first_reg, second_reg);
        } else {
          __ shrl(first_reg, second_reg);
        }
      } else {
        Immediate imm(second.GetConstant()->AsIntConstant()->GetValue() & kMaxIntShiftValue);
        if (op->IsShl()) {
          __ shll(first_reg, imm);
        } else if (op->IsShr()) {
          __ sarl(first_reg, imm);
        } else {
          __ shrl(first_reg, imm);
        }
      }
      break;
    }
    case Primitive::kPrimLong: {
      if (second.IsRegister()) {
        CpuRegister second_reg = second.AsRegister<CpuRegister>();
        if (op->IsShl()) {
          __ shlq(first_reg, second_reg);
        } else if (op->IsShr()) {
          __ sarq(first_reg, second_reg);
        } else {
          __ shrq(first_reg, second_reg);
        }
      } else {
        Immediate imm(second.GetConstant()->AsIntConstant()->GetValue() & kMaxLongShiftValue);
        if (op->IsShl()) {
          __ shlq(first_reg, imm);
        } else if (op->IsShr()) {
          __ sarq(first_reg, imm);
        } else {
          __ shrq(first_reg, imm);
        }
      }
      break;
    }
    default:
      LOG(FATAL) << "Unexpected operation type " << op->GetResultType();
  }
}

void LocationsBuilderX86_64::VisitShl(HShl* shl) {
  HandleShift(shl);
}

void InstructionCodeGeneratorX86_64::VisitShl(HShl* shl) {
  HandleShift(shl);
}

void LocationsBuilderX86_64::VisitShr(HShr* shr) {
  HandleShift(shr);
}

void InstructionCodeGeneratorX86_64::VisitShr(HShr* shr) {
  HandleShift(shr);
}

void LocationsBuilderX86_64::VisitUShr(HUShr* ushr) {
  HandleShift(ushr);
}

void InstructionCodeGeneratorX86_64::VisitUShr(HUShr* ushr) {
  HandleShift(ushr);
}

void LocationsBuilderX86_64::VisitNewInstance(HNewInstance* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kCall);
  InvokeRuntimeCallingConvention calling_convention;
  locations->AddTemp(Location::RegisterLocation(calling_convention.GetRegisterAt(0)));
  locations->AddTemp(Location::RegisterLocation(calling_convention.GetRegisterAt(1)));
  locations->SetOut(Location::RegisterLocation(RAX));
}

void InstructionCodeGeneratorX86_64::VisitNewInstance(HNewInstance* instruction) {
  InvokeRuntimeCallingConvention calling_convention;
  codegen_->LoadCurrentMethod(CpuRegister(calling_convention.GetRegisterAt(1)));
  codegen_->Load64BitValue(CpuRegister(calling_convention.GetRegisterAt(0)),
                           instruction->GetTypeIndex());
  __ gs()->call(
      Address::Absolute(GetThreadOffset<kX86_64WordSize>(instruction->GetEntrypoint()), true));

  DCHECK(!codegen_->IsLeafMethod());
  codegen_->RecordPcInfo(instruction, instruction->GetDexPc());
}

void LocationsBuilderX86_64::VisitNewArray(HNewArray* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kCall);
  InvokeRuntimeCallingConvention calling_convention;
  locations->AddTemp(Location::RegisterLocation(calling_convention.GetRegisterAt(0)));
  locations->AddTemp(Location::RegisterLocation(calling_convention.GetRegisterAt(2)));
  locations->SetOut(Location::RegisterLocation(RAX));
  locations->SetInAt(0, Location::RegisterLocation(calling_convention.GetRegisterAt(1)));
}

void InstructionCodeGeneratorX86_64::VisitNewArray(HNewArray* instruction) {
  InvokeRuntimeCallingConvention calling_convention;
  codegen_->LoadCurrentMethod(CpuRegister(calling_convention.GetRegisterAt(2)));
  codegen_->Load64BitValue(CpuRegister(calling_convention.GetRegisterAt(0)),
                           instruction->GetTypeIndex());

  __ gs()->call(
      Address::Absolute(GetThreadOffset<kX86_64WordSize>(instruction->GetEntrypoint()), true));

  DCHECK(!codegen_->IsLeafMethod());
  codegen_->RecordPcInfo(instruction, instruction->GetDexPc());
}

void LocationsBuilderX86_64::VisitParameterValue(HParameterValue* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  Location location = parameter_visitor_.GetNextLocation(instruction->GetType());
  if (location.IsStackSlot()) {
    location = Location::StackSlot(location.GetStackIndex() + codegen_->GetFrameSize());
  } else if (location.IsDoubleStackSlot()) {
    location = Location::DoubleStackSlot(location.GetStackIndex() + codegen_->GetFrameSize());
  }
  locations->SetOut(location);
}

void InstructionCodeGeneratorX86_64::VisitParameterValue(HParameterValue* instruction) {
  // Nothing to do, the parameter is already at its location.
  UNUSED(instruction);
}

void LocationsBuilderX86_64::VisitNot(HNot* not_) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(not_, LocationSummary::kNoCall);
  locations->SetInAt(0, Location::RequiresRegister());
  locations->SetOut(Location::SameAsFirstInput());
}

void InstructionCodeGeneratorX86_64::VisitNot(HNot* not_) {
  LocationSummary* locations = not_->GetLocations();
  DCHECK_EQ(locations->InAt(0).AsRegister<CpuRegister>().AsRegister(),
            locations->Out().AsRegister<CpuRegister>().AsRegister());
  Location out = locations->Out();
  switch (not_->GetResultType()) {
    case Primitive::kPrimInt:
      __ notl(out.AsRegister<CpuRegister>());
      break;

    case Primitive::kPrimLong:
      __ notq(out.AsRegister<CpuRegister>());
      break;

    default:
      LOG(FATAL) << "Unimplemented type for not operation " << not_->GetResultType();
  }
}

void LocationsBuilderX86_64::VisitBooleanNot(HBooleanNot* bool_not) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(bool_not, LocationSummary::kNoCall);
  locations->SetInAt(0, Location::RequiresRegister());
  locations->SetOut(Location::SameAsFirstInput());
}

void InstructionCodeGeneratorX86_64::VisitBooleanNot(HBooleanNot* bool_not) {
  LocationSummary* locations = bool_not->GetLocations();
  DCHECK_EQ(locations->InAt(0).AsRegister<CpuRegister>().AsRegister(),
            locations->Out().AsRegister<CpuRegister>().AsRegister());
  Location out = locations->Out();
  __ xorl(out.AsRegister<CpuRegister>(), Immediate(1));
}

void LocationsBuilderX86_64::VisitPhi(HPhi* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  for (size_t i = 0, e = instruction->InputCount(); i < e; ++i) {
    locations->SetInAt(i, Location::Any());
  }
  locations->SetOut(Location::Any());
}

void InstructionCodeGeneratorX86_64::VisitPhi(HPhi* instruction) {
  UNUSED(instruction);
  LOG(FATAL) << "Unimplemented";
}

void InstructionCodeGeneratorX86_64::GenerateMemoryBarrier(MemBarrierKind kind) {
  /*
   * According to the JSR-133 Cookbook, for x86 only StoreLoad/AnyAny barriers need memory fence.
   * All other barriers (LoadAny, AnyStore, StoreStore) are nops due to the x86 memory model.
   * For those cases, all we need to ensure is that there is a scheduling barrier in place.
   */
  switch (kind) {
    case MemBarrierKind::kAnyAny: {
      codegen_->MemoryFence();
      break;
    }
    case MemBarrierKind::kAnyStore:
    case MemBarrierKind::kLoadAny:
    case MemBarrierKind::kStoreStore: {
      // nop
      break;
    }
    default:
      LOG(FATAL) << "Unexpected memory barier " << kind;
  }
}

void LocationsBuilderX86_64::HandleFieldGet(HInstruction* instruction) {
  DCHECK(instruction->IsInstanceFieldGet() || instruction->IsStaticFieldGet());

  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  locations->SetInAt(0, Location::RequiresRegister());
  if (Primitive::IsFloatingPointType(instruction->GetType())) {
    locations->SetOut(Location::RequiresFpuRegister());
  } else {
    locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
  }
}

void InstructionCodeGeneratorX86_64::HandleFieldGet(HInstruction* instruction,
                                                    const FieldInfo& field_info) {
  DCHECK(instruction->IsInstanceFieldGet() || instruction->IsStaticFieldGet());

  LocationSummary* locations = instruction->GetLocations();
  CpuRegister base = locations->InAt(0).AsRegister<CpuRegister>();
  Location out = locations->Out();
  bool is_volatile = field_info.IsVolatile();
  Primitive::Type field_type = field_info.GetFieldType();
  uint32_t offset = field_info.GetFieldOffset().Uint32Value();

  switch (field_type) {
    case Primitive::kPrimBoolean: {
      __ movzxb(out.AsRegister<CpuRegister>(), Address(base, offset));
      break;
    }

    case Primitive::kPrimByte: {
      __ movsxb(out.AsRegister<CpuRegister>(), Address(base, offset));
      break;
    }

    case Primitive::kPrimShort: {
      __ movsxw(out.AsRegister<CpuRegister>(), Address(base, offset));
      break;
    }

    case Primitive::kPrimChar: {
      __ movzxw(out.AsRegister<CpuRegister>(), Address(base, offset));
      break;
    }

    case Primitive::kPrimInt:
    case Primitive::kPrimNot: {
      __ movl(out.AsRegister<CpuRegister>(), Address(base, offset));
      break;
    }

    case Primitive::kPrimLong: {
      __ movq(out.AsRegister<CpuRegister>(), Address(base, offset));
      break;
    }

    case Primitive::kPrimFloat: {
      __ movss(out.AsFpuRegister<XmmRegister>(), Address(base, offset));
      break;
    }

    case Primitive::kPrimDouble: {
      __ movsd(out.AsFpuRegister<XmmRegister>(), Address(base, offset));
      break;
    }

    case Primitive::kPrimVoid:
      LOG(FATAL) << "Unreachable type " << field_type;
      UNREACHABLE();
  }

  codegen_->MaybeRecordImplicitNullCheck(instruction);

  if (is_volatile) {
    GenerateMemoryBarrier(MemBarrierKind::kLoadAny);
  }
}

void LocationsBuilderX86_64::HandleFieldSet(HInstruction* instruction,
                                            const FieldInfo& field_info) {
  DCHECK(instruction->IsInstanceFieldSet() || instruction->IsStaticFieldSet());

  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  bool is_volatile = field_info.IsVolatile();
  bool needs_write_barrier =
      CodeGenerator::StoreNeedsWriteBarrier(field_info.GetFieldType(), instruction->InputAt(1));

  locations->SetInAt(0, Location::RequiresRegister());
  if (Primitive::IsFloatingPointType(instruction->InputAt(1)->GetType())) {
    if (is_volatile) {
      locations->SetInAt(1, Location::RequiresFpuRegister());
    } else {
      locations->SetInAt(1, Location::FpuRegisterOrConstant(instruction->InputAt(1)));
    }
  } else {
    if (is_volatile) {
      locations->SetInAt(1, Location::RequiresRegister());
    } else {
      locations->SetInAt(1, Location::RegisterOrConstant(instruction->InputAt(1)));
    }
  }
  if (needs_write_barrier) {
    // Temporary registers for the write barrier.
    locations->AddTemp(Location::RequiresRegister());
    locations->AddTemp(Location::RequiresRegister());
  }
}

void InstructionCodeGeneratorX86_64::HandleFieldSet(HInstruction* instruction,
                                                    const FieldInfo& field_info) {
  DCHECK(instruction->IsInstanceFieldSet() || instruction->IsStaticFieldSet());

  LocationSummary* locations = instruction->GetLocations();
  CpuRegister base = locations->InAt(0).AsRegister<CpuRegister>();
  Location value = locations->InAt(1);
  bool is_volatile = field_info.IsVolatile();
  Primitive::Type field_type = field_info.GetFieldType();
  uint32_t offset = field_info.GetFieldOffset().Uint32Value();

  if (is_volatile) {
    GenerateMemoryBarrier(MemBarrierKind::kAnyStore);
  }

  bool maybe_record_implicit_null_check_done = false;

  switch (field_type) {
    case Primitive::kPrimBoolean:
    case Primitive::kPrimByte: {
      if (value.IsConstant()) {
        int8_t v = CodeGenerator::GetInt32ValueOf(value.GetConstant());
        __ movb(Address(base, offset), Immediate(v));
      } else {
        __ movb(Address(base, offset), value.AsRegister<CpuRegister>());
      }
      break;
    }

    case Primitive::kPrimShort:
    case Primitive::kPrimChar: {
      if (value.IsConstant()) {
        int16_t v = CodeGenerator::GetInt32ValueOf(value.GetConstant());
        __ movw(Address(base, offset), Immediate(v));
      } else {
        __ movw(Address(base, offset), value.AsRegister<CpuRegister>());
      }
      break;
    }

    case Primitive::kPrimInt:
    case Primitive::kPrimNot: {
      if (value.IsConstant()) {
        int32_t v = CodeGenerator::GetInt32ValueOf(value.GetConstant());
        __ movl(Address(base, offset), Immediate(v));
      } else {
        __ movl(Address(base, offset), value.AsRegister<CpuRegister>());
      }
      break;
    }

    case Primitive::kPrimLong: {
      if (value.IsConstant()) {
        int64_t v = value.GetConstant()->AsLongConstant()->GetValue();
        codegen_->MoveInt64ToAddress(Address(base, offset),
                                     Address(base, offset + sizeof(int32_t)),
                                     v,
                                     instruction);
        maybe_record_implicit_null_check_done = true;
      } else {
        __ movq(Address(base, offset), value.AsRegister<CpuRegister>());
      }
      break;
    }

    case Primitive::kPrimFloat: {
      if (value.IsConstant()) {
        int32_t v =
            bit_cast<int32_t, float>(value.GetConstant()->AsFloatConstant()->GetValue());
        __ movl(Address(base, offset), Immediate(v));
      } else {
        __ movss(Address(base, offset), value.AsFpuRegister<XmmRegister>());
      }
      break;
    }

    case Primitive::kPrimDouble: {
      if (value.IsConstant()) {
        int64_t v =
            bit_cast<int64_t, double>(value.GetConstant()->AsDoubleConstant()->GetValue());
        codegen_->MoveInt64ToAddress(Address(base, offset),
                                     Address(base, offset + sizeof(int32_t)),
                                     v,
                                     instruction);
        maybe_record_implicit_null_check_done = true;
      } else {
        __ movsd(Address(base, offset), value.AsFpuRegister<XmmRegister>());
      }
      break;
    }

    case Primitive::kPrimVoid:
      LOG(FATAL) << "Unreachable type " << field_type;
      UNREACHABLE();
  }

  if (!maybe_record_implicit_null_check_done) {
    codegen_->MaybeRecordImplicitNullCheck(instruction);
  }

  if (CodeGenerator::StoreNeedsWriteBarrier(field_type, instruction->InputAt(1))) {
    CpuRegister temp = locations->GetTemp(0).AsRegister<CpuRegister>();
    CpuRegister card = locations->GetTemp(1).AsRegister<CpuRegister>();
    codegen_->MarkGCCard(temp, card, base, value.AsRegister<CpuRegister>());
  }

  if (is_volatile) {
    GenerateMemoryBarrier(MemBarrierKind::kAnyAny);
  }
}

void LocationsBuilderX86_64::VisitInstanceFieldSet(HInstanceFieldSet* instruction) {
  HandleFieldSet(instruction, instruction->GetFieldInfo());
}

void InstructionCodeGeneratorX86_64::VisitInstanceFieldSet(HInstanceFieldSet* instruction) {
  HandleFieldSet(instruction, instruction->GetFieldInfo());
}

void LocationsBuilderX86_64::VisitInstanceFieldGet(HInstanceFieldGet* instruction) {
  HandleFieldGet(instruction);
}

void InstructionCodeGeneratorX86_64::VisitInstanceFieldGet(HInstanceFieldGet* instruction) {
  HandleFieldGet(instruction, instruction->GetFieldInfo());
}

void LocationsBuilderX86_64::VisitStaticFieldGet(HStaticFieldGet* instruction) {
  HandleFieldGet(instruction);
}

void InstructionCodeGeneratorX86_64::VisitStaticFieldGet(HStaticFieldGet* instruction) {
  HandleFieldGet(instruction, instruction->GetFieldInfo());
}

void LocationsBuilderX86_64::VisitStaticFieldSet(HStaticFieldSet* instruction) {
  HandleFieldSet(instruction, instruction->GetFieldInfo());
}

void InstructionCodeGeneratorX86_64::VisitStaticFieldSet(HStaticFieldSet* instruction) {
  HandleFieldSet(instruction, instruction->GetFieldInfo());
}

void LocationsBuilderX86_64::VisitNullCheck(HNullCheck* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  Location loc = codegen_->GetCompilerOptions().GetImplicitNullChecks()
      ? Location::RequiresRegister()
      : Location::Any();
  locations->SetInAt(0, loc);
  if (instruction->HasUses()) {
    locations->SetOut(Location::SameAsFirstInput());
  }
}

void InstructionCodeGeneratorX86_64::GenerateImplicitNullCheck(HNullCheck* instruction) {
  if (codegen_->CanMoveNullCheckToUser(instruction)) {
    return;
  }
  LocationSummary* locations = instruction->GetLocations();
  Location obj = locations->InAt(0);

  __ testl(CpuRegister(RAX), Address(obj.AsRegister<CpuRegister>(), 0));
  codegen_->RecordPcInfo(instruction, instruction->GetDexPc());
}

void InstructionCodeGeneratorX86_64::GenerateExplicitNullCheck(HNullCheck* instruction) {
  SlowPathCodeX86_64* slow_path = new (GetGraph()->GetArena()) NullCheckSlowPathX86_64(instruction);
  codegen_->AddSlowPath(slow_path);

  LocationSummary* locations = instruction->GetLocations();
  Location obj = locations->InAt(0);

  if (obj.IsRegister()) {
    __ testl(obj.AsRegister<CpuRegister>(), obj.AsRegister<CpuRegister>());
  } else if (obj.IsStackSlot()) {
    __ cmpl(Address(CpuRegister(RSP), obj.GetStackIndex()), Immediate(0));
  } else {
    DCHECK(obj.IsConstant()) << obj;
    DCHECK_EQ(obj.GetConstant()->AsIntConstant()->GetValue(), 0);
    __ jmp(slow_path->GetEntryLabel());
    return;
  }
  __ j(kEqual, slow_path->GetEntryLabel());
}

void InstructionCodeGeneratorX86_64::VisitNullCheck(HNullCheck* instruction) {
  if (codegen_->GetCompilerOptions().GetImplicitNullChecks()) {
    GenerateImplicitNullCheck(instruction);
  } else {
    GenerateExplicitNullCheck(instruction);
  }
}

void LocationsBuilderX86_64::VisitArrayGet(HArrayGet* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  locations->SetInAt(0, Location::RequiresRegister());
  locations->SetInAt(1, Location::RegisterOrConstant(instruction->InputAt(1)));
  if (Primitive::IsFloatingPointType(instruction->GetType())) {
    locations->SetOut(Location::RequiresFpuRegister(), Location::kNoOutputOverlap);
  } else {
    locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
  }
}

void InstructionCodeGeneratorX86_64::VisitArrayGet(HArrayGet* instruction) {
  LocationSummary* locations = instruction->GetLocations();
  CpuRegister obj = locations->InAt(0).AsRegister<CpuRegister>();
  Location index = locations->InAt(1);

  switch (instruction->GetType()) {
    case Primitive::kPrimBoolean: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(uint8_t)).Uint32Value();
      CpuRegister out = locations->Out().AsRegister<CpuRegister>();
      if (index.IsConstant()) {
        __ movzxb(out, Address(obj,
            (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_1) + data_offset));
      } else {
        __ movzxb(out, Address(obj, index.AsRegister<CpuRegister>(), TIMES_1, data_offset));
      }
      break;
    }

    case Primitive::kPrimByte: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(int8_t)).Uint32Value();
      CpuRegister out = locations->Out().AsRegister<CpuRegister>();
      if (index.IsConstant()) {
        __ movsxb(out, Address(obj,
            (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_1) + data_offset));
      } else {
        __ movsxb(out, Address(obj, index.AsRegister<CpuRegister>(), TIMES_1, data_offset));
      }
      break;
    }

    case Primitive::kPrimShort: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(int16_t)).Uint32Value();
      CpuRegister out = locations->Out().AsRegister<CpuRegister>();
      if (index.IsConstant()) {
        __ movsxw(out, Address(obj,
            (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_2) + data_offset));
      } else {
        __ movsxw(out, Address(obj, index.AsRegister<CpuRegister>(), TIMES_2, data_offset));
      }
      break;
    }

    case Primitive::kPrimChar: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(uint16_t)).Uint32Value();
      CpuRegister out = locations->Out().AsRegister<CpuRegister>();
      if (index.IsConstant()) {
        __ movzxw(out, Address(obj,
            (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_2) + data_offset));
      } else {
        __ movzxw(out, Address(obj, index.AsRegister<CpuRegister>(), TIMES_2, data_offset));
      }
      break;
    }

    case Primitive::kPrimInt:
    case Primitive::kPrimNot: {
      DCHECK_EQ(sizeof(mirror::HeapReference<mirror::Object>), sizeof(int32_t));
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(int32_t)).Uint32Value();
      CpuRegister out = locations->Out().AsRegister<CpuRegister>();
      if (index.IsConstant()) {
        __ movl(out, Address(obj,
            (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_4) + data_offset));
      } else {
        __ movl(out, Address(obj, index.AsRegister<CpuRegister>(), TIMES_4, data_offset));
      }
      break;
    }

    case Primitive::kPrimLong: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(int64_t)).Uint32Value();
      CpuRegister out = locations->Out().AsRegister<CpuRegister>();
      if (index.IsConstant()) {
        __ movq(out, Address(obj,
            (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_8) + data_offset));
      } else {
        __ movq(out, Address(obj, index.AsRegister<CpuRegister>(), TIMES_8, data_offset));
      }
      break;
    }

    case Primitive::kPrimFloat: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(float)).Uint32Value();
      XmmRegister out = locations->Out().AsFpuRegister<XmmRegister>();
      if (index.IsConstant()) {
        __ movss(out, Address(obj,
            (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_4) + data_offset));
      } else {
        __ movss(out, Address(obj, index.AsRegister<CpuRegister>(), TIMES_4, data_offset));
      }
      break;
    }

    case Primitive::kPrimDouble: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(double)).Uint32Value();
      XmmRegister out = locations->Out().AsFpuRegister<XmmRegister>();
      if (index.IsConstant()) {
        __ movsd(out, Address(obj,
            (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_8) + data_offset));
      } else {
        __ movsd(out, Address(obj, index.AsRegister<CpuRegister>(), TIMES_8, data_offset));
      }
      break;
    }

    case Primitive::kPrimVoid:
      LOG(FATAL) << "Unreachable type " << instruction->GetType();
      UNREACHABLE();
  }
  codegen_->MaybeRecordImplicitNullCheck(instruction);
}

void LocationsBuilderX86_64::VisitArraySet(HArraySet* instruction) {
  Primitive::Type value_type = instruction->GetComponentType();

  bool needs_write_barrier =
      CodeGenerator::StoreNeedsWriteBarrier(value_type, instruction->GetValue());
  bool needs_runtime_call = instruction->NeedsTypeCheck();

  LocationSummary* locations = new (GetGraph()->GetArena()) LocationSummary(
      instruction, needs_runtime_call ? LocationSummary::kCall : LocationSummary::kNoCall);
  if (needs_runtime_call) {
    InvokeRuntimeCallingConvention calling_convention;
    locations->SetInAt(0, Location::RegisterLocation(calling_convention.GetRegisterAt(0)));
    locations->SetInAt(1, Location::RegisterLocation(calling_convention.GetRegisterAt(1)));
    locations->SetInAt(2, Location::RegisterLocation(calling_convention.GetRegisterAt(2)));
  } else {
    locations->SetInAt(0, Location::RequiresRegister());
    locations->SetInAt(
        1, Location::RegisterOrConstant(instruction->InputAt(1)));
    locations->SetInAt(2, Location::RequiresRegister());
    if (value_type == Primitive::kPrimFloat || value_type == Primitive::kPrimDouble) {
      locations->SetInAt(2, Location::FpuRegisterOrConstant(instruction->InputAt(2)));
    } else if (instruction->GetUseNonTemporalMove() &&
                (value_type == Primitive::kPrimInt ||
                 value_type == Primitive::kPrimNot ||
                 value_type == Primitive::kPrimLong)) {
      // Non-temporal move needs a register src.
      locations->SetInAt(2, Location::RequiresRegister());
    } else {
      locations->SetInAt(2, Location::RegisterOrConstant(instruction->InputAt(2)));
    }

    if (needs_write_barrier) {
      // Temporary registers for the write barrier.
      locations->AddTemp(Location::RequiresRegister());
      locations->AddTemp(Location::RequiresRegister());
    }
  }
}

void InstructionCodeGeneratorX86_64::VisitArraySet(HArraySet* instruction) {
  LocationSummary* locations = instruction->GetLocations();
  CpuRegister obj = locations->InAt(0).AsRegister<CpuRegister>();
  Location index = locations->InAt(1);
  Location value = locations->InAt(2);
  Primitive::Type value_type = instruction->GetComponentType();
  bool needs_runtime_call = locations->WillCall();
  bool needs_write_barrier =
      CodeGenerator::StoreNeedsWriteBarrier(value_type, instruction->GetValue());
  bool use_non_temporal = instruction->GetUseNonTemporalMove();

  switch (value_type) {
    case Primitive::kPrimBoolean:
    case Primitive::kPrimByte: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(uint8_t)).Uint32Value();
      if (index.IsConstant()) {
        size_t offset = (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_1) + data_offset;
        if (value.IsRegister()) {
          __ movb(Address(obj, offset), value.AsRegister<CpuRegister>());
        } else {
          __ movb(Address(obj, offset),
                  Immediate(value.GetConstant()->AsIntConstant()->GetValue()));
        }
      } else {
        if (value.IsRegister()) {
          __ movb(Address(obj, index.AsRegister<CpuRegister>(), TIMES_1, data_offset),
                  value.AsRegister<CpuRegister>());
        } else {
          __ movb(Address(obj, index.AsRegister<CpuRegister>(), TIMES_1, data_offset),
                  Immediate(value.GetConstant()->AsIntConstant()->GetValue()));
        }
      }
      codegen_->MaybeRecordImplicitNullCheck(instruction);
      break;
    }

    case Primitive::kPrimShort:
    case Primitive::kPrimChar: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(uint16_t)).Uint32Value();
      if (index.IsConstant()) {
        size_t offset = (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_2) + data_offset;
        if (value.IsRegister()) {
          __ movw(Address(obj, offset), value.AsRegister<CpuRegister>());
        } else {
          DCHECK(value.IsConstant()) << value;
          __ movw(Address(obj, offset),
                  Immediate(value.GetConstant()->AsIntConstant()->GetValue()));
        }
      } else {
        DCHECK(index.IsRegister()) << index;
        if (value.IsRegister()) {
          __ movw(Address(obj, index.AsRegister<CpuRegister>(), TIMES_2, data_offset),
                  value.AsRegister<CpuRegister>());
        } else {
          DCHECK(value.IsConstant()) << value;
          __ movw(Address(obj, index.AsRegister<CpuRegister>(), TIMES_2, data_offset),
                  Immediate(value.GetConstant()->AsIntConstant()->GetValue()));
        }
      }
      codegen_->MaybeRecordImplicitNullCheck(instruction);
      break;
    }

    case Primitive::kPrimInt:
    case Primitive::kPrimNot: {
      if (!needs_runtime_call) {
        uint32_t data_offset = mirror::Array::DataOffset(sizeof(int32_t)).Uint32Value();
        if (index.IsConstant()) {
          size_t offset =
              (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_4) + data_offset;
          if (value.IsRegister()) {
            __ movl(Address(obj, offset), value.AsRegister<CpuRegister>());
          } else {
            DCHECK(value.IsConstant()) << value;
            int32_t v = CodeGenerator::GetInt32ValueOf(value.GetConstant());
            __ movl(Address(obj, offset), Immediate(v));
          }
        } else {
          DCHECK(index.IsRegister()) << index;
          if (use_non_temporal && value.IsRegister()) {
            // Generate the non-temporal move instead.
            __ movntl(Address(obj, index.AsRegister<CpuRegister>(), TIMES_4, data_offset),
                      value.AsRegister<CpuRegister>());
          } else if (value.IsRegister()) {
            __ movl(Address(obj, index.AsRegister<CpuRegister>(), TIMES_4, data_offset),
                    value.AsRegister<CpuRegister>());
          } else {
            DCHECK(value.IsConstant()) << value;
            int32_t v = CodeGenerator::GetInt32ValueOf(value.GetConstant());
            __ movl(Address(obj, index.AsRegister<CpuRegister>(), TIMES_4, data_offset),
                    Immediate(v));
          }
        }
        codegen_->MaybeRecordImplicitNullCheck(instruction);
        if (needs_write_barrier) {
          DCHECK_EQ(value_type, Primitive::kPrimNot);
          CpuRegister temp = locations->GetTemp(0).AsRegister<CpuRegister>();
          CpuRegister card = locations->GetTemp(1).AsRegister<CpuRegister>();
          codegen_->MarkGCCard(temp, card, obj, value.AsRegister<CpuRegister>());
        }
      } else {
        DCHECK_EQ(value_type, Primitive::kPrimNot);
        __ gs()->call(Address::Absolute(QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pAputObject),
                                        true));
        DCHECK(!codegen_->IsLeafMethod());
        codegen_->RecordPcInfo(instruction, instruction->GetDexPc());
      }
      break;
    }

    case Primitive::kPrimLong: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(int64_t)).Uint32Value();
      if (index.IsConstant()) {
        size_t offset = (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_8) + data_offset;
        if (value.IsRegister()) {
          __ movq(Address(obj, offset), value.AsRegister<CpuRegister>());
          codegen_->MaybeRecordImplicitNullCheck(instruction);
        } else {
          int64_t v = value.GetConstant()->AsLongConstant()->GetValue();
          codegen_->MoveInt64ToAddress(Address(obj, offset),
                                       Address(obj, offset + sizeof(int32_t)),
                                       v,
                                       instruction);
        }
      } else {
        CpuRegister index_reg = index.AsRegister<CpuRegister>();
        if (use_non_temporal && value.IsRegister()) {
          // Generate the non-temporal move instead.
          __ movntq(Address(obj, index_reg, TIMES_8, data_offset),
                    value.AsRegister<CpuRegister>());
          codegen_->MaybeRecordImplicitNullCheck(instruction);
        } else if (value.IsRegister()) {
          __ movq(Address(obj, index_reg, TIMES_8, data_offset),
                  value.AsRegister<CpuRegister>());
          codegen_->MaybeRecordImplicitNullCheck(instruction);
        } else {
          int64_t v = value.GetConstant()->AsLongConstant()->GetValue();
          codegen_->MoveInt64ToAddress(Address(obj, index_reg, TIMES_8, data_offset),
                                       Address(obj, index_reg, TIMES_8, data_offset +
                                               sizeof(uint32_t)),
                                       v,
                                       instruction);
        }
      }
      break;
    }

    case Primitive::kPrimFloat: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(float)).Uint32Value();
      if (index.IsConstant()) {
        size_t offset = (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_4) + data_offset;
        if (value.IsFpuRegister()) {
          __ movss(Address(obj, offset), value.AsFpuRegister<XmmRegister>());
        } else {
          DCHECK(value.IsConstant());
          int32_t v =
              bit_cast<int32_t, float>(value.GetConstant()->AsFloatConstant()->GetValue());
          __ movl(Address(obj, offset), Immediate(v));
        }
      } else {
        if (value.IsFpuRegister()) {
          __ movss(Address(obj, index.AsRegister<CpuRegister>(), TIMES_4, data_offset),
                  value.AsFpuRegister<XmmRegister>());
        } else {
          DCHECK(value.IsConstant());
          int32_t v =
              bit_cast<int32_t, float>(value.GetConstant()->AsFloatConstant()->GetValue());
          __ movl(Address(obj, index.AsRegister<CpuRegister>(), TIMES_4, data_offset),
                  Immediate(v));
        }
      }
      codegen_->MaybeRecordImplicitNullCheck(instruction);
      break;
    }

    case Primitive::kPrimDouble: {
      uint32_t data_offset = mirror::Array::DataOffset(sizeof(double)).Uint32Value();
      if (index.IsConstant()) {
        size_t offset = (index.GetConstant()->AsIntConstant()->GetValue() << TIMES_8) + data_offset;
        if (value.IsFpuRegister()) {
          __ movsd(Address(obj, offset), value.AsFpuRegister<XmmRegister>());
          codegen_->MaybeRecordImplicitNullCheck(instruction);
        } else {
          DCHECK(value.IsConstant());
          int64_t v =
              bit_cast<int64_t, double>(value.GetConstant()->AsDoubleConstant()->GetValue());
          codegen_->MoveInt64ToAddress(Address(obj, offset),
                                       Address(obj, offset + sizeof(int32_t)),
                                       v,
                                       instruction);
        }
      } else {
        CpuRegister index_reg = index.AsRegister<CpuRegister>();
        if (value.IsFpuRegister()) {
          __ movsd(Address(obj, index_reg, TIMES_8, data_offset), value.AsFpuRegister<XmmRegister>());
          codegen_->MaybeRecordImplicitNullCheck(instruction);
        } else {
          DCHECK(value.IsConstant());
          int64_t v =
              bit_cast<int64_t, double>(value.GetConstant()->AsDoubleConstant()->GetValue());
          codegen_->MoveInt64ToAddress(Address(obj, index_reg, TIMES_8, data_offset),
                                       Address(obj, index_reg, TIMES_8, data_offset +
                                               sizeof(int32_t)),
                                       v,
                                       instruction);
        }
      }
      break;
    }

    case Primitive::kPrimVoid:
      LOG(FATAL) << "Unreachable type " << instruction->GetType();
      UNREACHABLE();
  }
}

void LocationsBuilderX86_64::VisitArrayLength(HArrayLength* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  locations->SetInAt(0, Location::RequiresRegister());
  locations->SetOut(Location::RequiresRegister(), Location::kNoOutputOverlap);
}

void InstructionCodeGeneratorX86_64::VisitArrayLength(HArrayLength* instruction) {
  LocationSummary* locations = instruction->GetLocations();
  uint32_t offset = mirror::Array::LengthOffset().Uint32Value();
  CpuRegister obj = locations->InAt(0).AsRegister<CpuRegister>();
  CpuRegister out = locations->Out().AsRegister<CpuRegister>();
  __ movl(out, Address(obj, offset));
  codegen_->MaybeRecordImplicitNullCheck(instruction);
}

void LocationsBuilderX86_64::VisitBoundsCheck(HBoundsCheck* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  locations->SetInAt(0, Location::RegisterOrConstant(instruction->InputAt(0)));
  locations->SetInAt(1, Location::RegisterOrConstant(instruction->InputAt(1)));
  if (instruction->HasUses()) {
    locations->SetOut(Location::SameAsFirstInput());
  }
  // We may need a temporary for the length if we have passed in the array base.
  if (instruction->InputAt(1)->GetType() == Primitive::kPrimNot) {
    locations->AddTemp(Location::RequiresRegister());
  }
}

void InstructionCodeGeneratorX86_64::VisitBoundsCheck(HBoundsCheck* instruction) {
  LocationSummary* locations = instruction->GetLocations();
  Location index_loc = locations->InAt(0);
  Location length_loc = locations->InAt(1);
  Location temp_length;
  bool has_array = instruction->InputAt(1)->GetType() == Primitive::kPrimNot;
  if (has_array) {
    temp_length = locations->GetTemp(0);
  }
  SlowPathCodeX86_64* slow_path =
    new (GetGraph()->GetArena()) BoundsCheckSlowPathX86_64(instruction, index_loc, length_loc, temp_length);

  if (length_loc.IsConstant()) {
    int32_t length = CodeGenerator::GetInt32ValueOf(length_loc.GetConstant());
    if (index_loc.IsConstant()) {
      // BCE will remove the bounds check if we are guarenteed to pass.
      int32_t index = CodeGenerator::GetInt32ValueOf(index_loc.GetConstant());
      if (index < 0 || index >= length) {
        codegen_->AddSlowPath(slow_path);
        __ jmp(slow_path->GetEntryLabel());
      } else {
        // Some optimization after BCE may have generated this, and we should not
        // generate a bounds check if it is a valid range.
      }
      return;
    }

    // We have to reverse the jump condition because the length is the constant.
    CpuRegister index_reg = index_loc.AsRegister<CpuRegister>();
    __ cmpl(index_reg, Immediate(length));
    codegen_->AddSlowPath(slow_path);
    __ j(kAboveEqual, slow_path->GetEntryLabel());
  } else {
    // Is the second input the array, rather than the length?
    if (has_array) {
      // Compare the length directly to the array descriptor.
      CpuRegister array_base = length_loc.AsRegister<CpuRegister>();
      uint32_t offset = mirror::Array::LengthOffset().Uint32Value();
      if (index_loc.IsConstant()) {
        int32_t value = CodeGenerator::GetInt32ValueOf(index_loc.GetConstant());
        __ cmpl(Address(array_base, offset), Immediate(value));
      } else {
        __ cmpl(Address(array_base, offset), index_loc.AsRegister<CpuRegister>());
      }
      if (codegen_->GetCompilerOptions().GetImplicitNullChecks()) {
        DCHECK(instruction->CanDoImplicitNullCheckOn(instruction->InputAt(1)));
        // Find the first previous instruction which is not a move.
        HInstruction* first_prev_not_move = instruction->GetPreviousDisregardingMoves();

        // If the instruction is a null check it means that `instruction` is the first user
        // and needs to record the pc.
        if (first_prev_not_move != nullptr && first_prev_not_move->IsNullCheck()) {
          HNullCheck* null_check = first_prev_not_move->AsNullCheck();
          // TODO: The parallel moves modify the environment. Their changes need to be reverted
          // otherwise the stack maps at the throw point will not be correct.
          codegen_->RecordPcInfo(null_check, null_check->GetDexPc());
        }
      }
    } else {
      CpuRegister length = length_loc.AsRegister<CpuRegister>();
      if (index_loc.IsConstant()) {
        int32_t value = CodeGenerator::GetInt32ValueOf(index_loc.GetConstant());
        __ cmpl(length, Immediate(value));
      } else {
        __ cmpl(length, index_loc.AsRegister<CpuRegister>());
      }
    }
    codegen_->AddSlowPath(slow_path);
    __ j(kBelowEqual, slow_path->GetEntryLabel());
  }
}

void CodeGeneratorX86_64::MarkGCCard(CpuRegister temp,
                                     CpuRegister card,
                                     CpuRegister object,
                                     CpuRegister value) {
  NearLabel is_null;
  __ testl(value, value);
  __ j(kEqual, &is_null);
  __ gs()->movq(card, Address::Absolute(
      Thread::CardTableOffset<kX86_64WordSize>().Int32Value(), true));
  __ movq(temp, object);
  __ shrq(temp, Immediate(gc::accounting::CardTable::kCardShift));
  __ movb(Address(temp, card, TIMES_1, 0),  card);
  __ Bind(&is_null);
}

void LocationsBuilderX86_64::VisitTemporary(HTemporary* temp) {
  temp->SetLocations(nullptr);
}

void InstructionCodeGeneratorX86_64::VisitTemporary(HTemporary* temp) {
  // Nothing to do, this is driven by the code generator.
  UNUSED(temp);
}

void LocationsBuilderX86_64::VisitParallelMove(HParallelMove* instruction) {
  UNUSED(instruction);
  LOG(FATAL) << "Unimplemented";
}

void InstructionCodeGeneratorX86_64::VisitParallelMove(HParallelMove* instruction) {
  codegen_->GetMoveResolver()->EmitNativeCode(instruction);
}

void LocationsBuilderX86_64::VisitTestSuspend(HTestSuspend* instruction) {
  UNUSED(instruction);
}

void InstructionCodeGeneratorX86_64::VisitTestSuspend(HTestSuspend* test_suspend) {
  __ gs()->cmpw(Address::Absolute(
      Thread::ThreadFlagsOffset<kX86_64WordSize>().Int32Value(), true), Immediate(0));
  HBasicBlock* suspend_block =
    codegen_->FirstNonEmptyBlock(test_suspend->SuspendSuccessor());
  HBasicBlock* no_suspend_block =
    codegen_->FirstNonEmptyBlock(test_suspend->NoSuspendSuccessor());

  // Generate the best jump we can.
  if (codegen_->GoesToNextBlock(test_suspend->GetBlock(), no_suspend_block)) {
    // We fall through to the no-suspend case.
    __ j(kNotEqual, codegen_->GetLabelOf(suspend_block));
  } else if (codegen_->GoesToNextBlock(test_suspend->GetBlock(), suspend_block)) {
    __ j(kEqual, codegen_->GetLabelOf(no_suspend_block));
  } else {
    // neither is fall through.
    __ j(kNotEqual, codegen_->GetLabelOf(suspend_block));
    __ jmp(codegen_->GetLabelOf(no_suspend_block));
  }
}

void LocationsBuilderX86_64::VisitSuspend(HSuspend* instruction) {
  UNUSED(instruction);
  new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kCallOnSlowPath);
}

void InstructionCodeGeneratorX86_64::VisitSuspend(HSuspend* suspend) {
  HBasicBlock* successor = suspend->GetSuccessor();
  if (!suspend->GetNext()->IsGoto()) {
    // Ensure that we execute any instructions after the Suspend.
    successor = nullptr;
  }
  SuspendCheckSlowPathX86_64* slow_path =
      new (GetGraph()->GetArena()) SuspendCheckSlowPathX86_64(suspend, successor);
  codegen_->AddSlowPath(slow_path);
  __ jmp(slow_path->GetEntryLabel());
  if (successor == nullptr) {
    // Come back here to execute the non-goto code.
    __ Bind(slow_path->GetReturnLabel());
  }
}

void LocationsBuilderX86_64::VisitSuspendCheck(HSuspendCheck* instruction) {
  new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kCallOnSlowPath);
}

void InstructionCodeGeneratorX86_64::VisitSuspendCheck(HSuspendCheck* instruction) {
  HBasicBlock* block = instruction->GetBlock();
  if (block->GetLoopInformation() != nullptr) {
    DCHECK(block->GetLoopInformation()->GetSuspendCheck() == instruction);
    // The back edge will generate the suspend check.
    return;
  }
  if (block->IsEntryBlock() && instruction->GetNext()->IsGoto()) {
    // The goto will generate the suspend check.
    return;
  }
  GenerateSuspendCheck(instruction, nullptr);
}

void InstructionCodeGeneratorX86_64::GenerateSuspendCheck(HSuspendCheck* instruction,
                                                          HBasicBlock* successor) {
  SuspendCheckSlowPathX86_64* slow_path =
      down_cast<SuspendCheckSlowPathX86_64*>(instruction->GetSlowPath());
  if (slow_path == nullptr) {
    slow_path = new (GetGraph()->GetArena()) SuspendCheckSlowPathX86_64(instruction, successor);
    instruction->SetSlowPath(slow_path);
    codegen_->AddSlowPath(slow_path);
    if (successor != nullptr) {
      DCHECK(successor->IsLoopHeader());
      codegen_->ClearSpillSlotsFromLoopPhisInStackMap(instruction);
    }
    codegen_->MaybeRecordImplicitNullCheck(instruction);
  } else {
    DCHECK_EQ(slow_path->GetSuccessor(), successor);
  }

  __ gs()->cmpw(Address::Absolute(
      Thread::ThreadFlagsOffset<kX86_64WordSize>().Int32Value(), true), Immediate(0));
  if (successor == nullptr) {
    __ j(kNotEqual, slow_path->GetEntryLabel());
    __ Bind(slow_path->GetReturnLabel());
  } else {
    __ j(kEqual, codegen_->GetLabelOf(successor));
    __ jmp(slow_path->GetEntryLabel());
  }
}

X86_64Assembler* ParallelMoveResolverX86_64::GetAssembler() const {
  return codegen_->GetAssembler();
}

void ParallelMoveResolverX86_64::EmitMove(size_t index) {
  MoveOperands* move = moves_.Get(index);
  Location source = move->GetSource();
  Location destination = move->GetDestination();

  if (source.IsRegister()) {
    if (destination.IsRegister()) {
      __ movq(destination.AsRegister<CpuRegister>(), source.AsRegister<CpuRegister>());
    } else if (destination.IsStackSlot()) {
      __ movl(Address(CpuRegister(RSP), destination.GetStackIndex()),
              source.AsRegister<CpuRegister>());
    } else {
      DCHECK(destination.IsDoubleStackSlot());
      __ movq(Address(CpuRegister(RSP), destination.GetStackIndex()),
              source.AsRegister<CpuRegister>());
    }
  } else if (source.IsStackSlot()) {
    if (destination.IsRegister()) {
      __ movl(destination.AsRegister<CpuRegister>(),
              Address(CpuRegister(RSP), source.GetStackIndex()));
    } else if (destination.IsFpuRegister()) {
      __ movss(destination.AsFpuRegister<XmmRegister>(),
              Address(CpuRegister(RSP), source.GetStackIndex()));
    } else {
      DCHECK(destination.IsStackSlot());
      __ movl(CpuRegister(TMP), Address(CpuRegister(RSP), source.GetStackIndex()));
      __ movl(Address(CpuRegister(RSP), destination.GetStackIndex()), CpuRegister(TMP));
    }
  } else if (source.IsDoubleStackSlot()) {
    if (destination.IsRegister()) {
      __ movq(destination.AsRegister<CpuRegister>(),
              Address(CpuRegister(RSP), source.GetStackIndex()));
    } else if (destination.IsFpuRegister()) {
      __ movsd(destination.AsFpuRegister<XmmRegister>(),
               Address(CpuRegister(RSP), source.GetStackIndex()));
    } else {
      DCHECK(destination.IsDoubleStackSlot()) << destination;
      __ movq(CpuRegister(TMP), Address(CpuRegister(RSP), source.GetStackIndex()));
      __ movq(Address(CpuRegister(RSP), destination.GetStackIndex()), CpuRegister(TMP));
    }
  } else if (source.IsConstant()) {
    HConstant* constant = source.GetConstant();
    if (constant->IsIntConstant() || constant->IsNullConstant()) {
      int32_t value = CodeGenerator::GetInt32ValueOf(constant);
      if (destination.IsRegister()) {
        if (value == 0) {
          __ xorl(destination.AsRegister<CpuRegister>(), destination.AsRegister<CpuRegister>());
        } else {
          __ movl(destination.AsRegister<CpuRegister>(), Immediate(value));
        }
      } else {
        DCHECK(destination.IsStackSlot()) << destination;
        __ movl(Address(CpuRegister(RSP), destination.GetStackIndex()), Immediate(value));
      }
    } else if (constant->IsLongConstant()) {
      int64_t value = constant->AsLongConstant()->GetValue();
      if (destination.IsRegister()) {
        codegen_->Load64BitValue(destination.AsRegister<CpuRegister>(), value);
      } else {
        DCHECK(destination.IsDoubleStackSlot()) << destination;
        codegen_->Store64BitValueToStack(destination, value);
      }
    } else if (constant->IsFloatConstant()) {
      float fp_value = constant->AsFloatConstant()->GetValue();
      int32_t value = bit_cast<int32_t, float>(fp_value);
      if (destination.IsFpuRegister()) {
        XmmRegister dest = destination.AsFpuRegister<XmmRegister>();
        if (value == 0) {
          // easy FP 0.0.
          __ xorps(dest, dest);
        } else {
          __ movss(dest, codegen_->LiteralFloatAddress(fp_value));
        }
      } else {
        DCHECK(destination.IsStackSlot()) << destination;
        Immediate imm(value);
        __ movl(Address(CpuRegister(RSP), destination.GetStackIndex()), imm);
      }
    } else {
      DCHECK(constant->IsDoubleConstant()) << constant->DebugName();
      double fp_value =  constant->AsDoubleConstant()->GetValue();
      int64_t value = bit_cast<int64_t, double>(fp_value);
      if (destination.IsFpuRegister()) {
        XmmRegister dest = destination.AsFpuRegister<XmmRegister>();
        if (value == 0) {
          __ xorpd(dest, dest);
        } else {
          __ movsd(dest, codegen_->LiteralDoubleAddress(fp_value));
        }
      } else {
        DCHECK(destination.IsDoubleStackSlot()) << destination;
        codegen_->Store64BitValueToStack(destination, value);
      }
    }
  } else if (source.IsFpuRegister()) {
    if (destination.IsFpuRegister()) {
      __ movaps(destination.AsFpuRegister<XmmRegister>(), source.AsFpuRegister<XmmRegister>());
    } else if (destination.IsStackSlot()) {
      __ movss(Address(CpuRegister(RSP), destination.GetStackIndex()),
               source.AsFpuRegister<XmmRegister>());
    } else {
      DCHECK(destination.IsDoubleStackSlot()) << destination;
      __ movsd(Address(CpuRegister(RSP), destination.GetStackIndex()),
               source.AsFpuRegister<XmmRegister>());
    }
  }
}

void ParallelMoveResolverX86_64::Exchange32(CpuRegister reg, int mem) {
  __ movl(CpuRegister(TMP), Address(CpuRegister(RSP), mem));
  __ movl(Address(CpuRegister(RSP), mem), reg);
  __ movl(reg, CpuRegister(TMP));
}

void ParallelMoveResolverX86_64::Exchange32(int mem1, int mem2) {
  ScratchRegisterScope ensure_scratch(
      this, TMP, RAX, codegen_->GetNumberOfCoreRegisters());

  int stack_offset = ensure_scratch.IsSpilled() ? kX86_64WordSize : 0;
  __ movl(CpuRegister(TMP), Address(CpuRegister(RSP), mem1 + stack_offset));
  __ movl(CpuRegister(ensure_scratch.GetRegister()),
          Address(CpuRegister(RSP), mem2 + stack_offset));
  __ movl(Address(CpuRegister(RSP), mem2 + stack_offset), CpuRegister(TMP));
  __ movl(Address(CpuRegister(RSP), mem1 + stack_offset),
          CpuRegister(ensure_scratch.GetRegister()));
}

void ParallelMoveResolverX86_64::Exchange64(CpuRegister reg1, CpuRegister reg2) {
  __ movq(CpuRegister(TMP), reg1);
  __ movq(reg1, reg2);
  __ movq(reg2, CpuRegister(TMP));
}

void ParallelMoveResolverX86_64::Exchange64(CpuRegister reg, int mem) {
  __ movq(CpuRegister(TMP), Address(CpuRegister(RSP), mem));
  __ movq(Address(CpuRegister(RSP), mem), reg);
  __ movq(reg, CpuRegister(TMP));
}

void ParallelMoveResolverX86_64::Exchange64(int mem1, int mem2) {
  ScratchRegisterScope ensure_scratch(
      this, TMP, RAX, codegen_->GetNumberOfCoreRegisters());

  int stack_offset = ensure_scratch.IsSpilled() ? kX86_64WordSize : 0;
  __ movq(CpuRegister(TMP), Address(CpuRegister(RSP), mem1 + stack_offset));
  __ movq(CpuRegister(ensure_scratch.GetRegister()),
          Address(CpuRegister(RSP), mem2 + stack_offset));
  __ movq(Address(CpuRegister(RSP), mem2 + stack_offset), CpuRegister(TMP));
  __ movq(Address(CpuRegister(RSP), mem1 + stack_offset),
          CpuRegister(ensure_scratch.GetRegister()));
}

void ParallelMoveResolverX86_64::Exchange32(XmmRegister reg, int mem) {
  __ movl(CpuRegister(TMP), Address(CpuRegister(RSP), mem));
  __ movss(Address(CpuRegister(RSP), mem), reg);
  __ movd(reg, CpuRegister(TMP));
}

void ParallelMoveResolverX86_64::Exchange64(XmmRegister reg, int mem) {
  __ movq(CpuRegister(TMP), Address(CpuRegister(RSP), mem));
  __ movsd(Address(CpuRegister(RSP), mem), reg);
  __ movd(reg, CpuRegister(TMP));
}

void ParallelMoveResolverX86_64::EmitSwap(size_t index) {
  MoveOperands* move = moves_.Get(index);
  Location source = move->GetSource();
  Location destination = move->GetDestination();

  if (source.IsRegister() && destination.IsRegister()) {
    Exchange64(source.AsRegister<CpuRegister>(), destination.AsRegister<CpuRegister>());
  } else if (source.IsRegister() && destination.IsStackSlot()) {
    Exchange32(source.AsRegister<CpuRegister>(), destination.GetStackIndex());
  } else if (source.IsStackSlot() && destination.IsRegister()) {
    Exchange32(destination.AsRegister<CpuRegister>(), source.GetStackIndex());
  } else if (source.IsStackSlot() && destination.IsStackSlot()) {
    Exchange32(destination.GetStackIndex(), source.GetStackIndex());
  } else if (source.IsRegister() && destination.IsDoubleStackSlot()) {
    Exchange64(source.AsRegister<CpuRegister>(), destination.GetStackIndex());
  } else if (source.IsDoubleStackSlot() && destination.IsRegister()) {
    Exchange64(destination.AsRegister<CpuRegister>(), source.GetStackIndex());
  } else if (source.IsDoubleStackSlot() && destination.IsDoubleStackSlot()) {
    Exchange64(destination.GetStackIndex(), source.GetStackIndex());
  } else if (source.IsFpuRegister() && destination.IsFpuRegister()) {
    __ movd(CpuRegister(TMP), source.AsFpuRegister<XmmRegister>());
    __ movaps(source.AsFpuRegister<XmmRegister>(), destination.AsFpuRegister<XmmRegister>());
    __ movd(destination.AsFpuRegister<XmmRegister>(), CpuRegister(TMP));
  } else if (source.IsFpuRegister() && destination.IsStackSlot()) {
    Exchange32(source.AsFpuRegister<XmmRegister>(), destination.GetStackIndex());
  } else if (source.IsStackSlot() && destination.IsFpuRegister()) {
    Exchange32(destination.AsFpuRegister<XmmRegister>(), source.GetStackIndex());
  } else if (source.IsFpuRegister() && destination.IsDoubleStackSlot()) {
    Exchange64(source.AsFpuRegister<XmmRegister>(), destination.GetStackIndex());
  } else if (source.IsDoubleStackSlot() && destination.IsFpuRegister()) {
    Exchange64(destination.AsFpuRegister<XmmRegister>(), source.GetStackIndex());
  } else {
    LOG(FATAL) << "Unimplemented swap between " << source << " and " << destination;
  }
}


void ParallelMoveResolverX86_64::SpillScratch(int reg) {
  __ pushq(CpuRegister(reg));
}


void ParallelMoveResolverX86_64::RestoreScratch(int reg) {
  __ popq(CpuRegister(reg));
}

void InstructionCodeGeneratorX86_64::GenerateClassInitializationCheck(
    SlowPathCodeX86_64* slow_path, CpuRegister class_reg) {
  __ cmpl(Address(class_reg,  mirror::Class::StatusOffset().Int32Value()),
          Immediate(mirror::Class::kStatusInitialized));
  __ j(kLess, slow_path->GetEntryLabel());
  __ Bind(slow_path->GetExitLabel());
  // No need for memory fence, thanks to the X86_64 memory model.
}

void LocationsBuilderX86_64::VisitLoadClass(HLoadClass* cls) {
  LocationSummary::CallKind call_kind = cls->CanCallRuntime()
      ? LocationSummary::kCallOnSlowPath
      : LocationSummary::kNoCall;
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(cls, call_kind);
  locations->SetOut(Location::RequiresRegister());
}

void InstructionCodeGeneratorX86_64::VisitLoadClass(HLoadClass* cls) {
  CpuRegister out = cls->GetLocations()->Out().AsRegister<CpuRegister>();
  if (cls->IsReferrersClass()) {
    DCHECK(!cls->CanCallRuntime());
    DCHECK(!cls->MustGenerateClinitCheck());
    codegen_->LoadCurrentMethod(out);
    __ movl(out, Address(out, ArtMethod::DeclaringClassOffset().Int32Value()));
  } else {
    DCHECK(cls->CanCallRuntime());
    codegen_->LoadCurrentMethod(out);
    __ movl(out, Address(out, ArtMethod::DexCacheResolvedTypesOffset().Int32Value()));
    __ movl(out, Address(out, CodeGenerator::GetCacheOffset(cls->GetTypeIndex())));
    SlowPathCodeX86_64* slow_path = new (GetGraph()->GetArena()) LoadClassSlowPathX86_64(
        cls, cls, cls->GetDexPc(), cls->MustGenerateClinitCheck());
    codegen_->AddSlowPath(slow_path);
    __ testl(out, out);
    __ j(kEqual, slow_path->GetEntryLabel());
    if (cls->MustGenerateClinitCheck()) {
      GenerateClassInitializationCheck(slow_path, out);
    } else {
      __ Bind(slow_path->GetExitLabel());
    }
  }
}

void LocationsBuilderX86_64::VisitClinitCheck(HClinitCheck* check) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(check, LocationSummary::kCallOnSlowPath);
  locations->SetInAt(0, Location::RequiresRegister());
  if (check->HasUses()) {
    locations->SetOut(Location::SameAsFirstInput());
  }
}

void InstructionCodeGeneratorX86_64::VisitClinitCheck(HClinitCheck* check) {
  // We assume the class to not be null.
  SlowPathCodeX86_64* slow_path = new (GetGraph()->GetArena()) LoadClassSlowPathX86_64(
      check->GetLoadClass(), check, check->GetDexPc(), true);
  codegen_->AddSlowPath(slow_path);
  GenerateClassInitializationCheck(slow_path,
                                   check->GetLocations()->InAt(0).AsRegister<CpuRegister>());
}

void LocationsBuilderX86_64::VisitLoadString(HLoadString* load) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(load, LocationSummary::kCallOnSlowPath);
  locations->SetOut(Location::RequiresRegister());
}

void InstructionCodeGeneratorX86_64::VisitLoadString(HLoadString* load) {
  SlowPathCodeX86_64* slow_path = new (GetGraph()->GetArena()) LoadStringSlowPathX86_64(load);
  codegen_->AddSlowPath(slow_path);

  CpuRegister out = load->GetLocations()->Out().AsRegister<CpuRegister>();
  codegen_->LoadCurrentMethod(CpuRegister(out));
  __ movl(out, Address(out, ArtMethod::DeclaringClassOffset().Int32Value()));
  __ movl(out, Address(out, mirror::Class::DexCacheStringsOffset().Int32Value()));
  __ movl(out, Address(out, CodeGenerator::GetCacheOffset(load->GetStringIndex())));
  __ testl(out, out);
  __ j(kEqual, slow_path->GetEntryLabel());
  __ Bind(slow_path->GetExitLabel());
}

void LocationsBuilderX86_64::VisitLoadException(HLoadException* load) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(load, LocationSummary::kNoCall);
  locations->SetOut(Location::RequiresRegister());
}

void InstructionCodeGeneratorX86_64::VisitLoadException(HLoadException* load) {
  Address address = Address::Absolute(
      Thread::ExceptionOffset<kX86_64WordSize>().Int32Value(), true);
  __ gs()->movl(load->GetLocations()->Out().AsRegister<CpuRegister>(), address);
  __ gs()->movl(address, Immediate(0));
}

void LocationsBuilderX86_64::VisitThrow(HThrow* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kCall);
  InvokeRuntimeCallingConvention calling_convention;
  locations->SetInAt(0, Location::RegisterLocation(calling_convention.GetRegisterAt(0)));
}

void InstructionCodeGeneratorX86_64::VisitThrow(HThrow* instruction) {
  __ gs()->call(
        Address::Absolute(QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pDeliverException), true));
  codegen_->RecordPcInfo(instruction, instruction->GetDexPc());
}

void LocationsBuilderX86_64::VisitInstanceOf(HInstanceOf* instruction) {
  LocationSummary::CallKind call_kind = instruction->IsClassFinal()
      ? LocationSummary::kNoCall
      : LocationSummary::kCallOnSlowPath;
  LocationSummary* locations = new (GetGraph()->GetArena()) LocationSummary(instruction, call_kind);
  locations->SetInAt(0, Location::RequiresRegister());
  locations->SetInAt(1, Location::Any());
  locations->SetOut(Location::RequiresRegister());
}

void InstructionCodeGeneratorX86_64::VisitInstanceOf(HInstanceOf* instruction) {
  LocationSummary* locations = instruction->GetLocations();
  CpuRegister obj = locations->InAt(0).AsRegister<CpuRegister>();
  Location cls = locations->InAt(1);
  CpuRegister out = locations->Out().AsRegister<CpuRegister>();
  uint32_t class_offset = mirror::Object::ClassOffset().Int32Value();
  NearLabel done, zero;
  SlowPathCodeX86_64* slow_path = nullptr;

  // Return 0 if `obj` is null.
  // Avoid null check if we know obj is not null.
  if (instruction->MustDoNullCheck()) {
    __ testl(obj, obj);
    __ j(kEqual, &zero);
  }
  // Compare the class of `obj` with `cls`.
  __ movl(out, Address(obj, class_offset));
  if (cls.IsRegister()) {
    __ cmpl(out, cls.AsRegister<CpuRegister>());
  } else {
    DCHECK(cls.IsStackSlot()) << cls;
    __ cmpl(out, Address(CpuRegister(RSP), cls.GetStackIndex()));
  }
  if (instruction->IsClassFinal()) {
    // Classes must be equal for the instanceof to succeed.
    __ j(kNotEqual, &zero);
    __ movl(out, Immediate(1));
    __ jmp(&done);
  } else {
    // If the classes are not equal, we go into a slow path.
    DCHECK(locations->OnlyCallsOnSlowPath());
    slow_path = new (GetGraph()->GetArena()) TypeCheckSlowPathX86_64(
        instruction, locations->InAt(1), locations->Out(), instruction->GetDexPc());
    codegen_->AddSlowPath(slow_path);
    __ j(kNotEqual, slow_path->GetEntryLabel());
    __ movl(out, Immediate(1));
    __ jmp(&done);
  }

  if (instruction->MustDoNullCheck() || instruction->IsClassFinal()) {
    __ Bind(&zero);
    __ movl(out, Immediate(0));
  }

  if (slow_path != nullptr) {
    __ Bind(slow_path->GetExitLabel());
  }
  __ Bind(&done);
}

void LocationsBuilderX86_64::VisitCheckCast(HCheckCast* instruction) {
  LocationSummary* locations = new (GetGraph()->GetArena()) LocationSummary(
      instruction, LocationSummary::kCallOnSlowPath);
  locations->SetInAt(0, Location::RequiresRegister());
  locations->SetInAt(1, Location::Any());
  locations->AddTemp(Location::RequiresRegister());
}

void InstructionCodeGeneratorX86_64::VisitCheckCast(HCheckCast* instruction) {
  LocationSummary* locations = instruction->GetLocations();
  CpuRegister obj = locations->InAt(0).AsRegister<CpuRegister>();
  Location cls = locations->InAt(1);
  CpuRegister temp = locations->GetTemp(0).AsRegister<CpuRegister>();
  uint32_t class_offset = mirror::Object::ClassOffset().Int32Value();
  SlowPathCodeX86_64* slow_path = new (GetGraph()->GetArena()) TypeCheckSlowPathX86_64(
      instruction, locations->InAt(1), locations->GetTemp(0), instruction->GetDexPc());
  codegen_->AddSlowPath(slow_path);

  // Avoid null check if we know obj is not null.
  if (instruction->MustDoNullCheck()) {
    __ testl(obj, obj);
    __ j(kEqual, slow_path->GetExitLabel());
  }
  // Compare the class of `obj` with `cls`.
  __ movl(temp, Address(obj, class_offset));
  if (cls.IsRegister()) {
    __ cmpl(temp, cls.AsRegister<CpuRegister>());
  } else {
    DCHECK(cls.IsStackSlot()) << cls;
    __ cmpl(temp, Address(CpuRegister(RSP), cls.GetStackIndex()));
  }
  // Classes must be equal for the checkcast to succeed.
  __ j(kNotEqual, slow_path->GetEntryLabel());
  __ Bind(slow_path->GetExitLabel());
}

void LocationsBuilderX86_64::VisitMonitorOperation(HMonitorOperation* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kCall);
  InvokeRuntimeCallingConvention calling_convention;
  locations->SetInAt(0, Location::RegisterLocation(calling_convention.GetRegisterAt(0)));
}

void InstructionCodeGeneratorX86_64::VisitMonitorOperation(HMonitorOperation* instruction) {
  __ gs()->call(Address::Absolute(instruction->IsEnter()
        ? QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pLockObject)
        : QUICK_ENTRYPOINT_OFFSET(kX86_64WordSize, pUnlockObject),
      true));
  codegen_->RecordPcInfo(instruction, instruction->GetDexPc());
}

void LocationsBuilderX86_64::VisitAnd(HAnd* instruction) { HandleBitwiseOperation(instruction); }
void LocationsBuilderX86_64::VisitOr(HOr* instruction) { HandleBitwiseOperation(instruction); }
void LocationsBuilderX86_64::VisitXor(HXor* instruction) { HandleBitwiseOperation(instruction); }

void LocationsBuilderX86_64::HandleBitwiseOperation(HBinaryOperation* instruction) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(instruction, LocationSummary::kNoCall);
  DCHECK(instruction->GetResultType() == Primitive::kPrimInt
         || instruction->GetResultType() == Primitive::kPrimLong);
  locations->SetInAt(0, Location::RequiresRegister());
  locations->SetInAt(1, Location::Any());
  locations->SetOut(Location::SameAsFirstInput());
}

void InstructionCodeGeneratorX86_64::VisitAnd(HAnd* instruction) {
  HandleBitwiseOperation(instruction);
}

void InstructionCodeGeneratorX86_64::VisitOr(HOr* instruction) {
  HandleBitwiseOperation(instruction);
}

void InstructionCodeGeneratorX86_64::VisitXor(HXor* instruction) {
  HandleBitwiseOperation(instruction);
}

void InstructionCodeGeneratorX86_64::HandleBitwiseOperation(HBinaryOperation* instruction) {
  LocationSummary* locations = instruction->GetLocations();
  Location first = locations->InAt(0);
  Location second = locations->InAt(1);
  DCHECK(first.Equals(locations->Out()));

  if (instruction->GetResultType() == Primitive::kPrimInt) {
    if (second.IsRegister()) {
      if (instruction->IsAnd()) {
        __ andl(first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
      } else if (instruction->IsOr()) {
        __ orl(first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
      } else {
        DCHECK(instruction->IsXor());
        __ xorl(first.AsRegister<CpuRegister>(), second.AsRegister<CpuRegister>());
      }
    } else if (second.IsConstant()) {
      Immediate imm(second.GetConstant()->AsIntConstant()->GetValue());
      if (instruction->IsAnd()) {
        __ andl(first.AsRegister<CpuRegister>(), imm);
      } else if (instruction->IsOr()) {
        __ orl(first.AsRegister<CpuRegister>(), imm);
      } else {
        DCHECK(instruction->IsXor());
        __ xorl(first.AsRegister<CpuRegister>(), imm);
      }
    } else {
      Address address(CpuRegister(RSP), second.GetStackIndex());
      if (instruction->IsAnd()) {
        __ andl(first.AsRegister<CpuRegister>(), address);
      } else if (instruction->IsOr()) {
        __ orl(first.AsRegister<CpuRegister>(), address);
      } else {
        DCHECK(instruction->IsXor());
        __ xorl(first.AsRegister<CpuRegister>(), address);
      }
    }
  } else {
    DCHECK_EQ(instruction->GetResultType(), Primitive::kPrimLong);
    CpuRegister first_reg = first.AsRegister<CpuRegister>();
    bool second_is_constant = false;
    int64_t value = 0;
    if (second.IsConstant()) {
      second_is_constant = true;
      value = second.GetConstant()->AsLongConstant()->GetValue();
    }
    bool is_int32_value = IsInt<32>(value);

    if (instruction->IsAnd()) {
      if (second_is_constant) {
        if (is_int32_value) {
          __ andq(first_reg, Immediate(static_cast<int32_t>(value)));
        } else {
          __ andq(first_reg, codegen_->LiteralInt64Address(value));
        }
      } else if (second.IsDoubleStackSlot()) {
        __ andq(first_reg, Address(CpuRegister(RSP), second.GetStackIndex()));
      } else {
        __ andq(first_reg, second.AsRegister<CpuRegister>());
      }
    } else if (instruction->IsOr()) {
      if (second_is_constant) {
        if (is_int32_value) {
          __ orq(first_reg, Immediate(static_cast<int32_t>(value)));
        } else {
          __ orq(first_reg, codegen_->LiteralInt64Address(value));
        }
      } else if (second.IsDoubleStackSlot()) {
        __ orq(first_reg, Address(CpuRegister(RSP), second.GetStackIndex()));
      } else {
        __ orq(first_reg, second.AsRegister<CpuRegister>());
      }
    } else {
      DCHECK(instruction->IsXor());
      if (second_is_constant) {
        if (is_int32_value) {
          __ xorq(first_reg, Immediate(static_cast<int32_t>(value)));
        } else {
          __ xorq(first_reg, codegen_->LiteralInt64Address(value));
        }
      } else if (second.IsDoubleStackSlot()) {
        __ xorq(first_reg, Address(CpuRegister(RSP), second.GetStackIndex()));
      } else {
        __ xorq(first_reg, second.AsRegister<CpuRegister>());
      }
    }
  }
}

void LocationsBuilderX86_64::VisitBoundType(HBoundType* instruction) {
  // Nothing to do, this should be removed during prepare for register allocator.
  UNUSED(instruction);
  LOG(FATAL) << "Unreachable";
}

void InstructionCodeGeneratorX86_64::VisitBoundType(HBoundType* instruction) {
  // Nothing to do, this should be removed during prepare for register allocator.
  UNUSED(instruction);
  LOG(FATAL) << "Unreachable";
}

static void FillRHSMemoryLocation(LocationSummary* locations, HInstructionRHSMemory* insn) {
  switch (insn->GetType()) {
    case Primitive::kPrimInt:
    case Primitive::kPrimLong:
      // Allow constants for the LHS to reduce register pressure.
      if (insn->InputAt(0)->IsConstant()) {
        locations->SetInAt(0, Location::RegisterOrConstant(insn->InputAt(0)));
        locations->SetOut(Location::RequiresRegister(), Location::kOutputOverlap);
      } else {
        locations->SetInAt(0, Location::RequiresRegister());
        locations->SetOut(Location::SameAsFirstInput());
      }
      locations->SetInAt(1, Location::RequiresRegister());
      if (insn->InputCount() == 3) {
        // Index.
        locations->SetInAt(2, Location::RequiresRegister());
      }
      break;

    case Primitive::kPrimDouble:
    case Primitive::kPrimFloat:
      locations->SetInAt(0, Location::RequiresFpuRegister());
      locations->SetInAt(1, Location::RequiresRegister());
      if (insn->InputCount() == 3) {
        // Index.
        locations->SetInAt(2, Location::RequiresRegister());
      }
      locations->SetOut(Location::SameAsFirstInput());
      break;

    default:
      LOG(FATAL) << "Unexpected insn type " << insn->GetType();
  }
}

void LocationsBuilderX86_64::VisitAddRHSMemory(HAddRHSMemory* add) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(add, LocationSummary::kNoCall);
  FillRHSMemoryLocation(locations, add);
}

void InstructionCodeGeneratorX86_64::VisitAddRHSMemory(HAddRHSMemory* add) {
  LocationSummary* locations = add->GetLocations();
  Location out = locations->Out();
  Location first = locations->InAt(0);
  Location base = locations->InAt(1);
  Location index;
  if (add->InputCount() == 3) {
    index = locations->InAt(2);
  }
  size_t offset = add->GetOffset();

  switch (add->GetType()) {
    case Primitive::kPrimInt:
      if (add->InputAt(0)->IsConstant()) {
        // We have to ensure that Out is set to the LHS constant value.
        int64_t v = codegen_->GetInt64ValueOf(add->InputAt(0)->AsConstant());
        DCHECK(IsInt<32>(v));
        __ movl(out.AsRegister<CpuRegister>(), Immediate(v));
      } else {
        DCHECK(first.Equals(out));
      }

      if (index.IsValid()) {
        __ addl(out.AsRegister<CpuRegister>(),
                Address(base.AsRegister<CpuRegister>(),
                        index.AsRegister<CpuRegister>(),
                        TIMES_4,
                        offset));
      } else {
        __ addl(out.AsRegister<CpuRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimLong:
      if (add->InputAt(0)->IsConstant()) {
        // We have to ensure that Out is set to the LHS constant value.
        codegen_->Load64BitValue(out.AsRegister<CpuRegister>(),
                                 codegen_->GetInt64ValueOf(add->InputAt(0)->AsConstant()));
      } else {
        DCHECK(first.Equals(out));
      }

      if (index.IsValid()) {
        __ addq(out.AsRegister<CpuRegister>(),
                Address(base.AsRegister<CpuRegister>(),
                        index.AsRegister<CpuRegister>(),
                        TIMES_8,
                        offset));
      } else {
        __ addq(out.AsRegister<CpuRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimFloat:
      DCHECK(first.Equals(out));
      if (index.IsValid()) {
        __ addss(out.AsFpuRegister<XmmRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_4,
                         offset));
      } else {
        __ addss(out.AsFpuRegister<XmmRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimDouble:
      DCHECK(first.Equals(out));
      if (index.IsValid()) {
        __ addsd(first.AsFpuRegister<XmmRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_8,
                         offset));
      } else {
        __ addsd(out.AsFpuRegister<XmmRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    default:
      LOG(FATAL) << "Unexpected add type " << add->GetType();
      break;
  }
  codegen_->MaybeRecordImplicitNullCheck(add);
}

static void FillLHSMemoryLocation(LocationSummary* locations, HInstructionLHSMemory* insn) {
  bool is_int = insn->GetType() == Primitive::kPrimInt;
  DCHECK(is_int || insn->GetType() == Primitive::kPrimLong);

  // Base must be in a register.
  locations->SetInAt(0, Location::RequiresRegister());
  uint32_t num_inputs = insn->InputCount();
  if (num_inputs == 3) {
    // Index must also be in a register.
    locations->SetInAt(1, Location::RequiresRegister());
  }
  uint32_t val_index = num_inputs - 1;
  if (is_int) {
    locations->SetInAt(val_index, Location::RegisterOrConstant(insn->InputAt(val_index)));
  } else {
    locations->SetInAt(val_index, Location::RegisterOrInt32LongConstant(insn->InputAt(val_index)));
  }
}

void LocationsBuilderX86_64::VisitAddLHSMemory(HAddLHSMemory* add) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(add, LocationSummary::kNoCall);
  FillLHSMemoryLocation(locations, add);
}

void InstructionCodeGeneratorX86_64::VisitAddLHSMemory(HAddLHSMemory* add) {
  LocationSummary* locations = add->GetLocations();
  uint32_t num_ops = add->InputCount();
  Location base = locations->InAt(0);
  Location index;
  Location rhs = locations->InAt(num_ops - 1);
  if (num_ops == 3) {
    index = locations->InAt(1);
  }
  size_t offset = add->GetOffset();

  if (add->GetType() == Primitive::kPrimInt) {
    Address addr(index.IsValid() ?
        Address(base.AsRegister<CpuRegister>(), index.AsRegister<CpuRegister>(), TIMES_4, offset) :
        Address(base.AsRegister<CpuRegister>(), offset));
    if (rhs.IsConstant()) {
      Immediate value(CodeGenerator::GetInt32ValueOf(rhs.GetConstant()));
      __ addl(addr, value);
    } else {
      __ addl(addr, rhs.AsRegister<CpuRegister>());
      }
  } else {
    DCHECK(add->GetType() == Primitive::kPrimLong);
    Address addr(index.IsValid() ?
        Address(base.AsRegister<CpuRegister>(), index.AsRegister<CpuRegister>(), TIMES_8, offset) :
        Address(base.AsRegister<CpuRegister>(), offset));
    if (rhs.IsConstant()) {
      int64_t v = CodeGenerator::GetInt64ValueOf(rhs.GetConstant());
      DCHECK(IsInt<32>(v));
      Immediate value(v);
      __ addq(addr, value);
    } else {
      __ addq(addr, rhs.AsRegister<CpuRegister>());
    }
  }
  codegen_->MaybeRecordImplicitNullCheck(add);
}

void LocationsBuilderX86_64::VisitSubRHSMemory(HSubRHSMemory* sub) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(sub, LocationSummary::kNoCall);
  FillRHSMemoryLocation(locations, sub);
}

void InstructionCodeGeneratorX86_64::VisitSubRHSMemory(HSubRHSMemory* sub) {
  LocationSummary* locations = sub->GetLocations();
  Location out = locations->Out();
  Location first = locations->InAt(0);
  Location base = locations->InAt(1);
  Location index;
  if (sub->InputCount() == 3) {
    index = locations->InAt(2);
  }
  size_t offset = sub->GetOffset();

  switch (sub->GetType()) {
    case Primitive::kPrimInt:
      if (sub->InputAt(0)->IsConstant()) {
        // We have to ensure that Out is set to the LHS constant value.
        int64_t v = codegen_->GetInt64ValueOf(sub->InputAt(0)->AsConstant());
        DCHECK(IsInt<32>(v));
        __ movl(out.AsRegister<CpuRegister>(), Immediate(v));
      } else {
        DCHECK(first.Equals(out));
      }

      if (index.IsValid()) {
        __ subl(out.AsRegister<CpuRegister>(),
                Address(base.AsRegister<CpuRegister>(),
                        index.AsRegister<CpuRegister>(),
                        TIMES_4,
                        offset));
      } else {
        __ subl(out.AsRegister<CpuRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimLong:
      if (sub->InputAt(0)->IsConstant()) {
        // We have to ensure that Out is set to the LHS constant value.
        codegen_->Load64BitValue(out.AsRegister<CpuRegister>(),
                                 codegen_->GetInt64ValueOf(sub->InputAt(0)->AsConstant()));
      } else {
        DCHECK(first.Equals(out));
      }

      if (index.IsValid()) {
        __ subq(out.AsRegister<CpuRegister>(),
                Address(base.AsRegister<CpuRegister>(),
                        index.AsRegister<CpuRegister>(),
                        TIMES_8,
                        offset));
      } else {
        __ subq(out.AsRegister<CpuRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimFloat:
      DCHECK(first.Equals(out));
      if (index.IsValid()) {
        __ subss(out.AsFpuRegister<XmmRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_4,
                         offset));
      } else {
        __ subss(out.AsFpuRegister<XmmRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimDouble:
      DCHECK(first.Equals(out));
      if (index.IsValid()) {
        __ subsd(out.AsFpuRegister<XmmRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_8,
                         offset));
      } else {
        __ subsd(out.AsFpuRegister<XmmRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    default:
      LOG(FATAL) << "Unexpected sub type " << sub->GetType();
      break;
  }
  codegen_->MaybeRecordImplicitNullCheck(sub);
}

void LocationsBuilderX86_64::VisitMulRHSMemory(HMulRHSMemory* mul) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(mul, LocationSummary::kNoCall);
  FillRHSMemoryLocation(locations, mul);
}

void InstructionCodeGeneratorX86_64::VisitMulRHSMemory(HMulRHSMemory* mul) {
  LocationSummary* locations = mul->GetLocations();
  Location out = locations->Out();
  Location first = locations->InAt(0);
  Location base = locations->InAt(1);
  Location index;
  if (mul->InputCount() == 3) {
    index = locations->InAt(2);
  }
  size_t offset = mul->GetOffset();

  switch (mul->GetType()) {
    case Primitive::kPrimInt:
      if (mul->InputAt(0)->IsConstant()) {
        // We have to ensure that Out is set to the LHS constant value.
        int64_t v = codegen_->GetInt64ValueOf(mul->InputAt(0)->AsConstant());
        DCHECK(IsInt<32>(v));
        __ movl(out.AsRegister<CpuRegister>(), Immediate(v));
      } else {
        DCHECK(first.Equals(out));
      }

      if (index.IsValid()) {
        __ imull(out.AsRegister<CpuRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_4,
                         offset));
      } else {
        __ imull(out.AsRegister<CpuRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimLong:
      if (mul->InputAt(0)->IsConstant()) {
        // We have to ensure that Out is set to the LHS constant value.
        codegen_->Load64BitValue(out.AsRegister<CpuRegister>(),
                                 codegen_->GetInt64ValueOf(mul->InputAt(0)->AsConstant()));
      } else {
        DCHECK(first.Equals(out));
      }

      if (index.IsValid()) {
        __ imulq(out.AsRegister<CpuRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_8,
                         offset));
      } else {
        __ imulq(out.AsRegister<CpuRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimFloat:
      DCHECK(first.Equals(out));
      if (index.IsValid()) {
        __ mulss(out.AsFpuRegister<XmmRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_4,
                         offset));
      } else {
        __ mulss(out.AsFpuRegister<XmmRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimDouble:
      DCHECK(first.Equals(out));
      if (index.IsValid()) {
        __ mulsd(out.AsFpuRegister<XmmRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_8,
                         offset));
      } else {
        __ mulsd(out.AsFpuRegister<XmmRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    default:
      LOG(FATAL) << "Unexpected mul type " << mul->GetType();
      break;
  }
  codegen_->MaybeRecordImplicitNullCheck(mul);
}

void LocationsBuilderX86_64::VisitDivRHSMemory(HDivRHSMemory* div) {
  LocationSummary* locations =
      new (GetGraph()->GetArena()) LocationSummary(div, LocationSummary::kNoCall);
  FillRHSMemoryLocation(locations, div);
}

void InstructionCodeGeneratorX86_64::VisitDivRHSMemory(HDivRHSMemory* div) {
  LocationSummary* locations = div->GetLocations();
  Location out = locations->Out();
  Location first = locations->InAt(0);
  DCHECK(first.Equals(out));
  Location base = locations->InAt(1);
  Location index;
  if (div->InputCount() == 3) {
    index = locations->InAt(2);
  }
  size_t offset = div->GetOffset();

  switch (div->GetType()) {
    case Primitive::kPrimFloat:
      if (index.IsValid()) {
        __ divss(out.AsFpuRegister<XmmRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_4,
                         offset));
      } else {
        __ divss(out.AsFpuRegister<XmmRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    case Primitive::kPrimDouble:
      if (index.IsValid()) {
        __ divsd(out.AsFpuRegister<XmmRegister>(),
                 Address(base.AsRegister<CpuRegister>(),
                         index.AsRegister<CpuRegister>(),
                         TIMES_8,
                         offset));
      } else {
        __ divsd(out.AsFpuRegister<XmmRegister>(), Address(base.AsRegister<CpuRegister>(), offset));
      }
      break;

    default:
      LOG(FATAL) << "Unexpected div type " << div->GetType();
      break;
  }
  codegen_->MaybeRecordImplicitNullCheck(div);
}

void CodeGeneratorX86_64::Load64BitValue(CpuRegister dest, int64_t value) {
  if (value == 0) {
    __ xorl(dest, dest);
  } else if (value > 0 && IsInt<32>(value)) {
    // We can use a 32 bit move, as it will zero-extend and is one byte shorter.
    __ movl(dest, Immediate(static_cast<int32_t>(value)));
  } else {
    __ movq(dest, Immediate(value));
  }
}

void CodeGeneratorX86_64::Store64BitValueToStack(Location dest, int64_t value) {
  DCHECK(dest.IsDoubleStackSlot());
  if (IsInt<32>(value)) {
    // Can move directly as an int32 constant.
    __ movq(Address(CpuRegister(RSP), dest.GetStackIndex()),
            Immediate(static_cast<int32_t>(value)));
  } else {
    Load64BitValue(CpuRegister(TMP), value);
    __ movq(Address(CpuRegister(RSP), dest.GetStackIndex()), CpuRegister(TMP));
  }
}

/**
 * Class to handle late fixup of offsets into constant area.
 */
class RIPFixup : public AssemblerFixup, public ArenaObject<kArenaAllocMisc> {
  public:
    RIPFixup(const CodeGeneratorX86_64& codegen, int offset)
      : codegen_(codegen), offset_into_constant_area_(offset) {}

    void AddOffset(int delta) { offset_into_constant_area_ += delta; }

  private:
    void Process(const MemoryRegion& region, int pos) OVERRIDE {
      // Patch the correct offset for the instruction.  We use the address of the
      // 'next' instruction, which is 'pos' (patch the 4 bytes before).
      int constant_offset = codegen_.ConstantAreaStart() + offset_into_constant_area_;
      int relative_position = constant_offset - pos;

      // Patch in the right value.
      region.StoreUnaligned<int32_t>(pos - 4, relative_position);
    }

    const CodeGeneratorX86_64& codegen_;

    // Location in constant area that the fixup refers to.
    int offset_into_constant_area_;
};

void CodeGeneratorX86_64::Finalize(CodeAllocator* allocator) {
  // Generate the constant area if needed.
  X86_64Assembler* assembler = GetAssembler();
  if (!assembler->IsConstantAreaEmpty()) {
    // Align to 4 byte boundary to reduce cache misses, as the data is 4 and 8
    // byte values.  If used for vectors at a later time, this will need to be
    // updated to 16 bytes with the appropriate offset.
    assembler->Align(4, 0);
    constant_area_start_ = assembler->CodeSize();
    assembler->AddConstantArea();

    // Fixup offsets to jump tables, if needed.
    size_t const_init_size = assembler->GetInitializedConstantAreaSize();
    if (const_init_size != 0 && jump_table_fixups_.Size() > 0) {
      for (size_t i = 0, e = jump_table_fixups_.Size(); i < e; i++) {
        jump_table_fixups_.Get(i)->AddOffset(const_init_size);
      }
    }
  }

  // And finish up.
  CodeGenerator::Finalize(allocator);
}

Address CodeGeneratorX86_64::LiteralDoubleAddress(double v) {
  AssemblerFixup* fixup = new (GetGraph()->GetArena()) RIPFixup(*this, __ AddDouble(v));
  return Address::RIP(fixup);
}

Address CodeGeneratorX86_64::LiteralFloatAddress(float v) {
  AssemblerFixup* fixup = new (GetGraph()->GetArena()) RIPFixup(*this, __ AddFloat(v));
  return Address::RIP(fixup);
}

Address CodeGeneratorX86_64::LiteralInt32Address(int32_t v) {
  AssemblerFixup* fixup = new (GetGraph()->GetArena()) RIPFixup(*this, __ AddInt32(v));
  return Address::RIP(fixup);
}

Address CodeGeneratorX86_64::LiteralInt64Address(int64_t v) {
  AssemblerFixup* fixup = new (GetGraph()->GetArena()) RIPFixup(*this, __ AddInt64(v));
  return Address::RIP(fixup);
}

/**
 * Class to handle late fixup of jump tables in constant area.
 */
class JumpTableFixup : public AssemblerFixup, public ArenaObject<kArenaAllocMisc> {
 public:
  JumpTableFixup(CodeGeneratorX86_64& codegen, HSwitch* switch_instr)
    : codegen_(codegen), switch_instr_(switch_instr) {}

 private:
  void Process(const MemoryRegion& region, int pos) OVERRIDE;

  CodeGeneratorX86_64& codegen_;
  HSwitch* switch_instr_;
};

void JumpTableFixup::Process(const MemoryRegion& region, int pos) {
  int32_t num_entries = switch_instr_->GetNumEntries();
  HBasicBlock* block = switch_instr_->GetBlock();
  const GrowableArray<HBasicBlock*>& successors = block->GetSuccessors();
  // The value that we want is the target offset - the position of the table.
  for (int i = 0; i < num_entries; i++) {
    HBasicBlock* b = successors.Get(i);
    Label* l = codegen_.GetLabelOf(b);
    DCHECK(l->IsBound());
    int32_t offset_to_block = l->Position() - pos;
    region.StoreUnaligned<int32_t>(pos + i * 4, offset_to_block);
  }
}

Address CodeGeneratorX86_64::LiteralCaseTable(HSwitch* switch_instr) {
  // Create a fixup to be used to patch the table.
  AssemblerFixup* table_fixup = new (GetGraph()->GetArena()) JumpTableFixup(*this, switch_instr);
  __ AddConstantAreaFixup(table_fixup);

  // Create the table itself in the constant area.
  int table_offset = __ AllocateConstantAreaWords(switch_instr->GetNumEntries());

  // Now the fixup to the table itself.
  RIPFixup* fixup = new (GetGraph()->GetArena()) RIPFixup(*this, table_offset);

  // We may have to fix the offset to account for the initialized data.
  jump_table_fixups_.Insert(fixup);
  return Address::RIP(fixup);
}

/**
 * Changes the input to BoundsCheck from the length to the array if possible.
 */
class BackendVisitor : public HGraphVisitor {
 public:
  explicit BackendVisitor(HGraph* graph) : HGraphVisitor(graph) {}

 private:
  void VisitBoundsCheck(HBoundsCheck* check) OVERRIDE {
    // Replace the length by the array itself, so that we can do compares to memory.
    HArrayLength* array_len = check->InputAt(1)->AsArrayLength();
    // Handle baseline code, which will load from a local here.
    if (array_len != nullptr) {
      HInstruction* array = array_len->InputAt(0);
      DCHECK_EQ(array->GetType(), Primitive::kPrimNot);

      // Don't apply this optimization when the array is nullptr.
      if (array->IsConstant() ||
          (array->IsNullCheck() && array->InputAt(0)->IsConstant())) {
        return;
      }

      // Is there a null check that could be an implicit check?
      if (array->IsNullCheck()) {
        CodeGenerator* codegen = GetGraph()->GetCodeGenerator();
        if (codegen->GetCompilerOptions().GetImplicitNullChecks()) {
          // The ArrayLen may generate the implicit null check.  Can the
          // bounds check do so as well?
          if (array_len->GetNextDisregardingMoves() != check) {
            // No, it won't.  Leave as is.
            return;
          }
        }
      }

      // Can we remove the ArrayLength?
      if (array_len->HasOnlyOneNonEnvironmentUse() &&
          check->InputAt(0)->GetType() == Primitive::kPrimInt) {
        HBoundsCheck* new_check =
          new (GetGraph()->GetArena()) HBoundsCheck(check->InputAt(0), array, check->GetDexPc());
        check->GetBlock()->InsertInstructionBefore(new_check, check);
        check->ReplaceWith(new_check);
        DCHECK(check->GetEnvironment() != nullptr);
        new_check->CopyEnvironmentFrom(check->GetEnvironment());
        check->GetBlock()->RemoveInstruction(check);
        array_len->GetBlock()->RemoveInstruction(array_len);
      }
    }
  }

  void VisitAdd(HAdd* add) OVERRIDE {
    VisitMathOp(add, false);
  }

  void VisitSub(HSub* sub) OVERRIDE {
    VisitMathOp(sub, false);
  }

  void VisitMul(HMul* mul) OVERRIDE {
    VisitMathOp(mul, false);
  }

  void VisitDiv(HDiv* div) OVERRIDE {
    VisitMathOp(div, true);
  }

  void TryForLHSMemory(HInstruction* instruction) {
    HInstruction* previous = instruction->GetPrevious();
    if (previous) {
      HInstructionRHSMemory* rhs_mem = previous->AsInstructionRHSMemory();
      if (rhs_mem) {
        TryMemoryOperation(instruction, rhs_mem);
      }
    }
  }

  void VisitInstanceFieldSet(HInstanceFieldSet* instruction) OVERRIDE {
    // Can we combine this with a preceding RHSMemory operation?
    TryForLHSMemory(instruction);
  }

  void VisitStaticFieldSet(HStaticFieldSet* instruction) OVERRIDE {
    // Can we combine this with a preceding RHSMemory operation?
    TryForLHSMemory(instruction);
  }

  void VisitArraySet(HArraySet* instruction) OVERRIDE {
    // Can we combine this with a preceding RHSMemory operation?
    TryForLHSMemory(instruction);
  }

  void VisitMathOp(HBinaryOperation* bin_op, bool float_only) {
    // Can we convert to a HInstructionRHSMemory variant?
    HInstruction* rhs = bin_op->GetRight();
    HInstruction* lhs = bin_op->GetLeft();
    if (TryMemoryOperation(bin_op, lhs, rhs, float_only)) {
      // We did it.
      return;
    }

    // We didn't use the RHS. Can we use the LHS?
    if (bin_op->IsCommutative()) {
      if (!lhs->HasOnlyOneNonEnvironmentUse()) {
        return;
      }
      TryMemoryOperation(bin_op, rhs, lhs, float_only);
    }
  }

  bool IsSafeToReplaceWithMemOp(HInstruction* rhs, HBinaryOperation* bin_op) {
    // There is a case when we can't convert an op to mem variant.
    // Let's say we have the following instructions:
    // a = ArrayGet b, c
    // e = ArraySet b, c, f
    // g = Mul a, f
    // h = ArraySet b, c, g
    //
    // If we convert it into
    // e = ArraySet b, c, f
    // g = MulRHSMemory b, c, f
    // h = ArraySet b, c, g
    // then we violate flow dependency.
    // There is a similar case, when he have FieldGet/FieldSet
    // instead of ArrayGet/ArraySet.
    //
    if (rhs->GetBlock() != bin_op->GetBlock()) {
      // For analysis simplification we want to need
      // rhs and binary op to be in the same block.
      return false;
    }

    for (HInstruction* instruction = rhs->GetNext();
         instruction != nullptr && instruction != bin_op;
         instruction = instruction->GetNext()) {
      switch (instruction->GetKind()) {
        case HInstruction::kArrayGet:
        case HInstruction::kInstanceFieldGet:
        case HInstruction::kStaticFieldGet:
          // A read is harmless, even if it is to the same address.
          break;
        default:
          if (alias_checker_.Alias(instruction, rhs) != AliasCheck::kNoAlias) {
            // This instruction can alias with the load.  Don't generate an
            // operation from memory.
            return false;
          }
          break;
      }
    }
    // No instruction between rhs and bin_op aliased with rhs.  It is safe to
    // combine the instructions into one.
    return true;
  }

  bool TryMemoryOperation(HBinaryOperation* bin_op,
                          HInstruction* lhs,
                          HInstruction* rhs,
                          bool float_only) {
    HInstruction::InstructionKind mem_kind = rhs->GetKind();
    switch (mem_kind) {
      case HInstruction::kInstanceFieldGet:
      case HInstruction::kStaticFieldGet: {
        if (!rhs->HasOnlyOneNonEnvironmentUse()) {
          break;
        }
        const FieldInfo& field_info = (mem_kind == HInstruction::kStaticFieldGet) ?
                                        rhs->AsStaticFieldGet()->GetFieldInfo() :
                                        rhs->AsInstanceFieldGet()->GetFieldInfo();
        if (field_info.IsVolatile()) {
          // Ignore volatiles.
          break;
        }

        if (!IsSafeToReplaceWithMemOp(rhs, bin_op)) {
          // Conversion into memory operation is not permitted.
          break;
        }

        Primitive::Type field_type = field_info.GetFieldType();
        uint32_t offset = field_info.GetFieldOffset().Uint32Value();

        HInstructionRHSMemory* new_insn = nullptr;
        HInstruction* base = nullptr;
        switch (field_type) {
          case Primitive::kPrimInt:
          case Primitive::kPrimLong:
            if (!float_only) {
              base = rhs->InputAt(0);
              new_insn = GetRHSMemory(bin_op, rhs, lhs, base, nullptr, offset);
            }
            break;
          case Primitive::kPrimFloat:
          case Primitive::kPrimDouble:
            base = rhs->InputAt(0);
            new_insn = GetRHSMemory(bin_op, rhs, lhs, base, nullptr, offset);
            break;
          default:
            // Unsupported type.
            break;
        }
        if (new_insn) {
          if (mem_kind == HInstruction::kStaticFieldGet) {
            new_insn->SetFromStatic();
          }
          bin_op->GetBlock()->ReplaceAndRemoveInstructionWith(bin_op, new_insn);
          rhs->GetBlock()->RemoveInstruction(rhs);
          DCHECK(base != nullptr);
          if (rhs->HasEnvironment()) {
            new_insn->CopyEnvironmentFrom(rhs->GetEnvironment());
          }
          return true;
        }
        break;
      }
      case HInstruction::kArrayGet: {
        if (!rhs->HasOnlyOneNonEnvironmentUse()) {
          break;
        }

        if (!IsSafeToReplaceWithMemOp(rhs, bin_op)) {
          // Conversion into memory operation is not permitted.
          break;
        }

        HArrayGet* get = rhs->AsArrayGet();
        Primitive::Type type = get->GetType();
        uint32_t data_offset = GetArrayOffset(type);
        HInstruction* new_insn = nullptr;
        HInstruction* base = nullptr;

        switch (type) {
          case Primitive::kPrimInt:
          case Primitive::kPrimLong:
            if (!float_only) {
              base = get->GetArray();
              new_insn = GetRHSMemory(bin_op, rhs, lhs, base, get->GetIndex(), data_offset);
            }
            break;
          case Primitive::kPrimFloat:
          case Primitive::kPrimDouble:
            base = get->GetArray();
            new_insn = GetRHSMemory(bin_op, rhs, lhs, base, get->GetIndex(), data_offset);
            break;
          default:
            // Unsupported type.
            break;
        }
        if (new_insn) {
          bin_op->GetBlock()->ReplaceAndRemoveInstructionWith(bin_op, new_insn);
          rhs->GetBlock()->RemoveInstruction(rhs);
          DCHECK(base != nullptr);
          if (rhs->HasEnvironment()) {
            new_insn->CopyEnvironmentFrom(rhs->GetEnvironment());
          }
          return true;
        }
        break;
      }
      default:
        break;
    }

    // Failed to convert to a memory operation.
    return false;
  }

  uint32_t GetArrayOffset(Primitive::Type type) {
    switch (type) {
      case Primitive::kPrimInt:
        return mirror::Array::DataOffset(sizeof(int32_t)).Uint32Value();
      case Primitive::kPrimLong:
        return mirror::Array::DataOffset(sizeof(int64_t)).Uint32Value();
      case Primitive::kPrimFloat:
        return mirror::Array::DataOffset(sizeof(float)).Uint32Value();
      case Primitive::kPrimDouble:
        return mirror::Array::DataOffset(sizeof(double)).Uint32Value();
      default:
        // Unsupported type.
        return 0;
    }
  }

  static void TryConvertConstantIndexToOffset(Primitive::Type type,
                                              HInstruction*& index,
                                              uint32_t& data_offset) {
    HInstruction* temp_index = index;
    if (temp_index->IsBoundsCheck()) {
      temp_index = temp_index->InputAt(0);
    }
    if (!temp_index->IsIntConstant()) {
      return;
    }
    int32_t index_value = temp_index->AsIntConstant()->GetValue();

    // Scale the index by the type.
    switch (type) {
      case Primitive::kPrimInt:
        index_value *= sizeof(int32_t);
        break;
      case Primitive::kPrimFloat:
        index_value *= sizeof(float);
        break;
      case Primitive::kPrimLong:
        index_value *= sizeof(int64_t);
        break;
      case Primitive::kPrimDouble:
        index_value *= sizeof(double);
        break;
      default:
        break;
    }

    // Replace the index with adjusted offset.
    index = nullptr;
    data_offset += index_value;
  }

  HInstructionRHSMemory* GetRHSMemory(HBinaryOperation* bin_op, HInstruction* get,
                                      HInstruction* lhs, HInstruction* base,
                                      HInstruction* index, uint32_t data_offset) {
    ArenaAllocator* arena = GetGraph()->GetArena();
    if (index != nullptr) {
      TryConvertConstantIndexToOffset(bin_op->GetType(), index, data_offset);
    }

    // Is this valid to do a null check on?
    if (base->IsNullCheck() && get->IsInstanceFieldGet() && data_offset >= kPageSize) {
      // We can't use an implicit null check. Just use the original instruction.
      return nullptr;
    }

    HInstructionRHSMemory* result = nullptr;
    switch (bin_op->GetKind()) {
      case HInstruction::kAdd:
        result = new (arena) HAddRHSMemory(arena, bin_op->GetType(), lhs, base, index, data_offset,
                                           base->GetDexPc());
        break;
      case HInstruction::kSub:
        result = new (arena) HSubRHSMemory(arena, bin_op->GetType(), lhs, base, index, data_offset,
                                           base->GetDexPc());
        break;
      case HInstruction::kMul:
        result = new (arena) HMulRHSMemory(arena, bin_op->GetType(), lhs, base, index, data_offset,
                                           base->GetDexPc());
        break;
      case HInstruction::kDiv:
        result = new (arena) HDivRHSMemory(arena, bin_op->GetType(), lhs, base, index, data_offset,
                                           base->GetDexPc());
        break;
      default:
        LOG(FATAL) << "Unexpected type " << bin_op->GetType();
        break;
    }

    return result;
  }

  void TryMemoryOperation(HInstruction* instruction, HInstructionRHSMemory* rhs_mem_op) {
    // Does this instruction use the result of the rhs_mem_op?
    HInstruction* input = instruction->InputAt(instruction->InputCount() - 1);
    if (input != rhs_mem_op) {
      // The result of the RHS mem op is not the input to the Set.
      return;
    }

    // We can't support FP operations to memory.
    if (Primitive::IsFloatingPointType(rhs_mem_op->InputAt(0)->GetType())) {
      return;
    }

    // We only support add to memory.
    if (!rhs_mem_op->IsAddRHSMemory()) {
      return;
    }

    // Are these instructions compatible?
    if (HInstanceFieldSet* i_set = instruction->AsInstanceFieldSet()) {
      // The the memory operation can't have an index.
      if (rhs_mem_op->InputCount() != 2) {
        return;
      }
      // We need to match on base and offset.
      if (i_set->InputAt(0) != rhs_mem_op->InputAt(1) ||
          i_set->GetFieldOffset().Uint32Value() != rhs_mem_op->GetOffset()) {
        return;
      }
    } else if (HStaticFieldSet* a_set = instruction->AsStaticFieldSet()) {
      // The the memory operation can't have an index.
      if (rhs_mem_op->InputCount() != 2) {
        return;
      }
      // We need to match on base and offset.
      if (a_set->InputAt(0) != rhs_mem_op->InputAt(1) ||
          a_set->GetFieldOffset().Uint32Value() != rhs_mem_op->GetOffset()) {
        return;
      }
    } else {
      DCHECK(instruction->IsArraySet());
      // Does the memory operation have an index?
      if (rhs_mem_op->InputCount() != 3) {
        return;
      }
      // We need to match on base and index.
      HArraySet* set = instruction->AsArraySet();
      if (set->GetArray() != rhs_mem_op->InputAt(1) ||
          set->GetIndex() != rhs_mem_op->InputAt(2)) {
        return;
      }
    }

    HInstruction* new_rhs = rhs_mem_op->InputAt(0);
    ArenaAllocator* arena = GetGraph()->GetArena();
    HInstructionLHSMemory* new_insn =
        new (arena) HAddLHSMemory(arena, rhs_mem_op, new_rhs, rhs_mem_op->GetDexPc());

    // Go ahead and do the replacement.
    HBasicBlock* block = instruction->GetBlock();
    block->ReplaceAndRemoveInstructionWith(instruction, new_insn);

    // Remove the old RHS memory op if there is no other use of it.
    if (!rhs_mem_op->HasUses()) {
      if (rhs_mem_op->HasEnvironment()) {
        new_insn->CopyEnvironmentFrom(rhs_mem_op->GetEnvironment());
      }
      block->RemoveInstruction(rhs_mem_op);
    }
  }

  // Alias checker for RHSMemory generation.
  AliasCheck alias_checker_;
};

void CodeGeneratorX86_64::RunBackendOptimization(HGraph* graph) {
  BackendVisitor visitor(graph);
  visitor.VisitInsertionOrder();
}

void CodeGeneratorX86_64::MoveInt64ToAddress(const Address& addr_low,
                                             const Address& addr_high,
                                             int64_t v,
                                             HInstruction* instruction) {
  if (IsInt<32>(v)) {
    int32_t v_32 = v;
    __ movq(addr_low, Immediate(v_32));
    MaybeRecordImplicitNullCheck(instruction);
  } else {
    // Didn't fit in a register.  Do it in pieces.
    int32_t low_v = Low32Bits(v);
    int32_t high_v = High32Bits(v);
    __ movl(addr_low, Immediate(low_v));
    MaybeRecordImplicitNullCheck(instruction);
    __ movl(addr_high, Immediate(high_v));
  }
}

}  // namespace x86_64
}  // namespace art
