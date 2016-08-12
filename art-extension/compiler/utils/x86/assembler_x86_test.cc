/*
 * Copyright (C) 2011 The Android Open Source Project
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

#include "assembler_x86.h"

#include "base/stl_util.h"
#include "utils/assembler_test.h"

namespace art {

TEST(AssemblerX86, CreateBuffer) {
  AssemblerBuffer buffer;
  AssemblerBuffer::EnsureCapacity ensured(&buffer);
  buffer.Emit<uint8_t>(0x42);
  ASSERT_EQ(static_cast<size_t>(1), buffer.Size());
  buffer.Emit<int32_t>(42);
  ASSERT_EQ(static_cast<size_t>(5), buffer.Size());
}

class AssemblerX86Test : public AssemblerTest<x86::X86Assembler, x86::Register,
                                              x86::XmmRegister, x86::Immediate> {
 protected:
  std::string GetArchitectureString() OVERRIDE {
    return "x86";
  }

  std::string GetAssemblerParameters() OVERRIDE {
    return " --32";
  }

  std::string GetDisassembleParameters() OVERRIDE {
    return " -D -bbinary -mi386 --no-show-raw-insn";
  }

  void SetUpHelpers() OVERRIDE {
    if (registers_.size() == 0) {
      registers_.insert(end(registers_),
                        {  // NOLINT(whitespace/braces)
                          new x86::Register(x86::EAX),
                          new x86::Register(x86::EBX),
                          new x86::Register(x86::ECX),
                          new x86::Register(x86::EDX),
                          new x86::Register(x86::EBP),
                          new x86::Register(x86::ESP),
                          new x86::Register(x86::ESI),
                          new x86::Register(x86::EDI)
                        });
    }

    if (fp_registers_.size() == 0) {
      fp_registers_.insert(end(fp_registers_),
                           {  // NOLINT(whitespace/braces)
                             new x86::XmmRegister(x86::XMM0),
                             new x86::XmmRegister(x86::XMM1),
                             new x86::XmmRegister(x86::XMM2),
                             new x86::XmmRegister(x86::XMM3),
                             new x86::XmmRegister(x86::XMM4),
                             new x86::XmmRegister(x86::XMM5),
                             new x86::XmmRegister(x86::XMM6),
                             new x86::XmmRegister(x86::XMM7)
                           });
    }
  }

  void TearDown() OVERRIDE {
    AssemblerTest::TearDown();
    STLDeleteElements(&registers_);
    STLDeleteElements(&fp_registers_);
  }

  std::vector<x86::Register*> GetRegisters() OVERRIDE {
    return registers_;
  }

  std::vector<x86::XmmRegister*> GetFPRegisters() OVERRIDE {
    return fp_registers_;
  }

  x86::Immediate CreateImmediate(int64_t imm_value) OVERRIDE {
    return x86::Immediate(imm_value);
  }

 private:
  std::vector<x86::Register*> registers_;
  std::vector<x86::XmmRegister*> fp_registers_;
};


TEST_F(AssemblerX86Test, Movl) {
  GetAssembler()->movl(x86::EAX, x86::EBX);
  const char* expected = "mov %ebx, %eax\n";
  DriverStr(expected, "movl");
}

TEST_F(AssemblerX86Test, psrlq) {
  GetAssembler()->psrlq(x86::XMM0, CreateImmediate(32));
  const char* expected = "psrlq $0x20, %xmm0\n";
  DriverStr(expected, "psrlq");
}

TEST_F(AssemblerX86Test, punpckldq) {
  GetAssembler()->punpckldq(x86::XMM0, x86::XMM1);
  const char* expected = "punpckldq %xmm1, %xmm0\n";
  DriverStr(expected, "punpckldq");
}

TEST_F(AssemblerX86Test, LoadLongConstant) {
  GetAssembler()->LoadLongConstant(x86::XMM0, 51);
  const char* expected =
      "push $0x0\n"
      "push $0x33\n"
      "movsd 0(%esp), %xmm0\n"
      "add $8, %esp\n";
  DriverStr(expected, "LoadLongConstant");
}

TEST_F(AssemblerX86Test, LockCmpxchgl) {
  GetAssembler()->LockCmpxchgl(x86::Address(
        x86::Register(x86::EDI), x86::Register(x86::EBX), x86::TIMES_4, 12),
      x86::Register(x86::ESI));
  GetAssembler()->LockCmpxchgl(x86::Address(
        x86::Register(x86::EDI), x86::Register(x86::ESI), x86::TIMES_4, 12),
      x86::Register(x86::ESI));
  GetAssembler()->LockCmpxchgl(x86::Address(
        x86::Register(x86::EDI), x86::Register(x86::ESI), x86::TIMES_4, 12),
      x86::Register(x86::EDI));
  GetAssembler()->LockCmpxchgl(x86::Address(
      x86::Register(x86::EBP), 0), x86::Register(x86::ESI));
  GetAssembler()->LockCmpxchgl(x86::Address(
        x86::Register(x86::EBP), x86::Register(x86::ESI), x86::TIMES_1, 0),
      x86::Register(x86::ESI));
  const char* expected =
    "lock cmpxchgl %ESI, 0xc(%EDI,%EBX,4)\n"
    "lock cmpxchgl %ESI, 0xc(%EDI,%ESI,4)\n"
    "lock cmpxchgl %EDI, 0xc(%EDI,%ESI,4)\n"
    "lock cmpxchgl %ESI, (%EBP)\n"
    "lock cmpxchgl %ESI, (%EBP,%ESI,1)\n";

  DriverStr(expected, "lock_cmpxchgl");
}

TEST_F(AssemblerX86Test, LockCmpxchg8b) {
  GetAssembler()->LockCmpxchg8b(x86::Address(
      x86::Register(x86::EDI), x86::Register(x86::EBX), x86::TIMES_4, 12));
  GetAssembler()->LockCmpxchg8b(x86::Address(
      x86::Register(x86::EDI), x86::Register(x86::ESI), x86::TIMES_4, 12));
  GetAssembler()->LockCmpxchg8b(x86::Address(
      x86::Register(x86::EDI), x86::Register(x86::ESI), x86::TIMES_4, 12));
  GetAssembler()->LockCmpxchg8b(x86::Address(x86::Register(x86::EBP), 0));
  GetAssembler()->LockCmpxchg8b(x86::Address(
      x86::Register(x86::EBP), x86::Register(x86::ESI), x86::TIMES_1, 0));
  const char* expected =
    "lock cmpxchg8b 0xc(%EDI,%EBX,4)\n"
    "lock cmpxchg8b 0xc(%EDI,%ESI,4)\n"
    "lock cmpxchg8b 0xc(%EDI,%ESI,4)\n"
    "lock cmpxchg8b (%EBP)\n"
    "lock cmpxchg8b (%EBP,%ESI,1)\n";

  DriverStr(expected, "lock_cmpxchg8b");
}

TEST_F(AssemblerX86Test, FPUIntegerLoad) {
  GetAssembler()->filds(x86::Address(x86::Register(x86::ESP), 4));
  GetAssembler()->fildl(x86::Address(x86::Register(x86::ESP), 12));
  const char* expected =
      "fildl 0x4(%ESP)\n"
      "fildll 0xc(%ESP)\n";
  DriverStr(expected, "FPUIntegerLoad");
}

TEST_F(AssemblerX86Test, FPUIntegerStore) {
  GetAssembler()->fistps(x86::Address(x86::Register(x86::ESP), 16));
  GetAssembler()->fistpl(x86::Address(x86::Register(x86::ESP), 24));
  const char* expected =
      "fistpl 0x10(%ESP)\n"
      "fistpll 0x18(%ESP)\n";
  DriverStr(expected, "FPUIntegerStore");
}

TEST_F(AssemblerX86Test, AddlAddrImm) {
  GetAssembler()->addl(x86::Address(x86::EAX, 0), x86::Immediate(100));
  GetAssembler()->addl(x86::Address(x86::EDI, 0), x86::Immediate(101));
  const char* expected = "addl $100, 0(%eax)\n"
                         "addl $101, 0(%edi)\n";
  DriverStr(expected, "addl_addr_imm");
}


TEST_F(AssemblerX86Test, AdclAddrImm) {
  GetAssembler()->adcl(x86::Address(x86::EAX, 0), x86::Immediate(100));
  GetAssembler()->adcl(x86::Address(x86::EDI, 0), x86::Immediate(101));
  const char* expected = "adcl $100, 0(%eax)\n"
                         "adcl $101, 0(%edi)\n";
  DriverStr(expected, "adcl_addr_imm");
}


TEST_F(AssemblerX86Test, SublAddrImm) {
  GetAssembler()->subl(x86::Address(x86::EAX, 0), x86::Immediate(100));
  GetAssembler()->subl(x86::Address(x86::EDI, 0), x86::Immediate(101));
  const char* expected = "subl $100, 0(%eax)\n"
                         "subl $101, 0(%edi)\n";
  DriverStr(expected, "subl_addr_imm");
}


TEST_F(AssemblerX86Test, SbblAddrImm) {
  GetAssembler()->sbbl(x86::Address(x86::EAX, 0), x86::Immediate(100));
  GetAssembler()->sbbl(x86::Address(x86::EDI, 0), x86::Immediate(101));
  const char* expected = "sbbl $100, 0(%eax)\n"
                         "sbbl $101, 0(%edi)\n";
  DriverStr(expected, "sbbl_addr_imm");
}


TEST_F(AssemblerX86Test, AddlAddrReg) {
  GetAssembler()->addl(x86::Address(x86::EAX, 0), x86::EDI);
  GetAssembler()->addl(x86::Address(x86::EDI, 0), x86::EDI);
  const char* expected = "addl %edi, 0(%eax)\n"
                         "addl %edi, 0(%edi)\n";
  DriverStr(expected, "addl_addr_reg");
}


TEST_F(AssemblerX86Test, AdclAddrReg) {
  GetAssembler()->adcl(x86::Address(x86::EAX, 0), x86::EDI);
  GetAssembler()->adcl(x86::Address(x86::EDI, 0), x86::EDI);
  const char* expected = "adcl %edi, 0(%eax)\n"
                         "adcl %edi, 0(%edi)\n";
  DriverStr(expected, "adcl_addr_reg");
}


TEST_F(AssemblerX86Test, SublAddrReg) {
  GetAssembler()->subl(x86::Address(x86::EAX, 0), x86::EDI);
  GetAssembler()->subl(x86::Address(x86::EDI, 0), x86::EDI);
  const char* expected = "subl %edi, 0(%eax)\n"
                         "subl %edi, 0(%edi)\n";
  DriverStr(expected, "subl_addr_reg");
}


TEST_F(AssemblerX86Test, SbblAddrReg) {
  GetAssembler()->sbbl(x86::Address(x86::EAX, 0), x86::EDI);
  GetAssembler()->sbbl(x86::Address(x86::EDI, 0), x86::EDI);
  const char* expected = "sbbl %edi, 0(%eax)\n"
                         "sbbl %edi, 0(%edi)\n";
  DriverStr(expected, "sbbl_addr_reg");
}


/////////////////
// Near labels //
/////////////////

TEST_F(AssemblerX86Test, Jecxz) {
  x86::NearLabel target;
  GetAssembler()->jecxz(&target);
  GetAssembler()->addl(x86::EDI, x86::Address(x86::ESP, 4));
  GetAssembler()->Bind(&target);
  const char* expected =
    "jecxz 1f\n"
    "addl 4(%ESP),%EDI\n"
    "1:\n";

  DriverStr(expected, "jecxz");
}

TEST_F(AssemblerX86Test, NearLabel) {
  // Test both forward and backward branches.
  x86::NearLabel start, target;
  GetAssembler()->Bind(&start);
  GetAssembler()->j(x86::kEqual, &target);
  GetAssembler()->jmp(&target);
  GetAssembler()->jecxz(&target);
  GetAssembler()->addl(x86::EDI, x86::Address(x86::ESP, 4));
  GetAssembler()->Bind(&target);
  GetAssembler()->j(x86::kNotEqual, &start);
  GetAssembler()->jmp(&start);
  const char* expected =
    "1: je 2f\n"
    "jmp 2f\n"
    "jecxz 2f\n"
    "addl 4(%ESP),%EDI\n"
    "2: jne 1b\n"
    "jmp 1b\n";

  DriverStr(expected, "near_label");
}

}  // namespace art
