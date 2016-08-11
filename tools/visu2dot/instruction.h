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

#ifndef INSTRUCTION_H_
#define INSTRUCTION_H_

#include <cassert>
#include <sstream>

#include <iostream>
#include <vector>
#include <string>
#include <cassert>
#include <fstream>
#include <sstream>

#include "key_values.h"

class Instruction {
  int64_t int64_teger1;
  int64_t int64_teger2;
  std::string identifier;
  std::string instruction;
  std::vector<const char*> registers;
  Value* value;

  public:
  Instruction(int64_t pint64_teger1, int64_t pint64_teger2, const std::string& pidentifier,
      const std::string& pinstruction, Value* v = nullptr) :
    int64_teger1(pint64_teger1), int64_teger2(pint64_teger2), identifier(pidentifier),
    instruction(pinstruction), value(v) {}

  std::string Dump(bool last) const {
    std::ostringstream separator;
    std::ostringstream reg_str;
    std::ostringstream str;
    std::ostringstream value_str;

    if (last) {
      separator << " | ";
    }

    for (int64_t i = 0; i < registers.size(); i++) {
      reg_str << registers[i] << ' ';
    }

    if (value) {
      value_str << ' ';
      value->FillString(value_str);
    }

    str << "    {" << int64_teger1 << ' ' << int64_teger2 << ' ' << identifier
        << ' ' << instruction << value_str.str() << ' ' << reg_str.str()
        << "              \\l}" << separator.str() << "\\\n";

    return str.str();
  }

  void AddRegister(const char* reg) {
    registers.push_back(reg);
  }

  const char* GetRegister(int64_t idx) const {
    assert(idx < registers.size());
    return registers[idx];
  }

  int64_t GetNumRegisters() const {
    return registers.size();
  }

  bool IsExit() const {
    return instruction == "Exit";
  };
};

class BasicBlock {
  std::string name;
  std::vector<Instruction*> insns;
  std::vector<const char*> next;

  public:
  BasicBlock(const char* pname, const std::vector<Instruction*>& pinsns)
    : name(pname), insns(pinsns) {}

  void AddNext(const char* bb_name) {
    next.push_back(bb_name);
  }

  const char* GetNext(int64_t idx) const {
    assert(idx < next.size());
    return next[idx];
  }

  int64_t GetNumNext() const {
    return next.size();
  }

  const char* GetName() const {
    return name.c_str();
  }

  void AddInsn(Instruction* insn) {
    insns.push_back(insn);
  }

  Instruction* GetInsn(int64_t index) {
    assert(index < insns.size());
    return insns[index];
  }

  int64_t GetNumInsn() const {
    return insns.size();
  }
};

class Graph {
  std::string name;
  std::vector<BasicBlock*> bbs;

 public:
  Graph(const char* pname, const std::vector<BasicBlock*>& pbbs)
    : name(pname), bbs(pbbs) {}

  void AddBB(BasicBlock* bb) {
    bbs.push_back(bb);
  }

  BasicBlock* GetBB(int64_t index) {
    assert(index < bbs.size());
    return bbs[index];
  }

  int64_t GetNumBB() const {
    return bbs.size();
  }

  void DumpGraph(const char* filename) {
    std::ostringstream fname;
    fname << filename << name << ".dot";
    std::ofstream ofs(fname.str().c_str(), std::ofstream::out);

    if (!ofs.is_open()) {
      fprintf(stderr, "Error: Cannot open file %s.\n", filename);
      return;
    }

    // Do the header.
    ofs << "digraph G {\n\trankdir=TB\n\tentry_1 [shape=Mdiamond];\n";

    int64_t num_bbs = GetNumBB();

    if (num_bbs != 0) {
      BasicBlock* bb = GetBB(0);
      ofs << "\tentry_1:s -> block_" << bb->GetName() << ":n\n\n\texit_2 [shape=Mdiamond];\n\n";

      for (int64_t i = 0; i < num_bbs; i++) {
        bb = GetBB(i);
        int64_t num_insns = bb->GetNumInsn();
        ofs << "  block_" << bb->GetName() << " [shape=record,label = \"{ \\\n";
        ofs << "    {block id " << bb->GetName() << "\\l} | \\\n";
        bool saw_exit = false;
        for (int64_t j = 0; j < num_insns; j++) {
          Instruction* insn = bb->GetInsn(j);
          ofs << insn->Dump((j + 1) < num_insns);
          if (insn->IsExit()) {
            saw_exit = true;
          }
        }
        ofs << "  }\"];\n\n";

        int64_t num_next = bb->GetNumNext();
        for (int64_t k = 0; k < num_next; k++) {
          ofs << "  block_" << bb->GetName() << ":s -> block_" << bb->GetNext(k) << ":n";
          if (num_next == 2 && k == 0) {
            ofs << " [style=dotted]";
          }
          ofs << "\n";
        }

        if (saw_exit) {
          ofs << "  block_" << bb->GetName() << ":s -> exit_2:n\n";
        }
      }
    }

    // Do the footer.
    ofs << "}\n";
    ofs.close();
  }
};

#endif
