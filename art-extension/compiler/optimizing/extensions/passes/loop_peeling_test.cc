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

#include "base/arena_allocator.h"
#include "builder.h"
#include "dex_file.h"
#include "dex_instruction.h"
#include "graph_x86.h"
#include "nodes.h"
#include "loop_information.h"
#include "loop_iterators.h"
#include "loop_formation.h"
#include "optimizing_unit_test.h"

#include "gtest/gtest.h"

namespace art {

static void TestPeeling(const uint16_t* data, ArenaAllocator* allocator, size_t num_peeled_blocks) {
  // Build the graph.
  HGraph_X86* graph = CreateGraph_X86_for_test(allocator);
  HGraphBuilder builder(graph);
  const DexFile::CodeItem* item = reinterpret_cast<const DexFile::CodeItem*>(data);
  builder.BuildGraph(*item);
  graph->TryBuildingSsa();

  // Loop formation is needed to get loop hierarchy in place for peeling.
  HLoopFormation formation(graph);
  formation.Run();

  HOnlyInnerLoopIterator inner_iter(GRAPH_TO_GRAPH_X86(graph)->GetLoopInformation());
  while (!inner_iter.Done()) {
    HLoopInformation_X86* inner_loop = inner_iter.Current();
    if (inner_loop->IsPeelable(&formation)) {
      inner_loop->PeelHead(&formation);
    }
    ASSERT_EQ(inner_loop->GetPeeledBlockIds().Size(), num_peeled_blocks);
    inner_iter.Advance();
  }
}

TEST(LoopPeelingTest, ForLoop) {
  /*
   *  Simple for loop:
   *  int result = 0;
   *  for (int i = 0; i < 10; i++) {
   *    result += i;
   *  }
   *  return result;
   */

  const uint16_t data[] = THREE_REGISTERS_CODE_ITEM(
    Instruction::CONST_4 | 0,
    Instruction::MOVE | 1 << 8,
    Instruction::CONST_16 | 2 << 8, 0xa,
    Instruction::IF_GE | 0x20 << 8, 0x6,
    Instruction::ADD_INT_2ADDR | 1 << 8,
    Instruction::ADD_INT_LIT8 | 0, 0x100,
    Instruction::GOTO | 0xF9 << 8,
    Instruction::RETURN | 1 << 8);

  ArenaPool arena;
  ArenaAllocator allocator(&arena);
  // The loop above will generate two blocks - thus peel must also be two.
  TestPeeling(data, &allocator, 2u);
}
}  // namespace art
