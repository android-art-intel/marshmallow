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

#include "find_ivs.h"
#include "loop_formation.h"
#include "loop_information.h"
#include "loop_iterators.h"
#include "optimizing_unit_test.h"

#include "gtest/gtest.h"

namespace art {
  static void TestIVBounds(const uint16_t* data, ArenaAllocator* allocator,
                           int nbr_ivs,
                           int lower, int upper,
                           int inc) {
    // Build the graph.
    HGraph_X86* graph = CreateGraph_X86_for_test(allocator);
    HGraphBuilder builder(graph);
    const DexFile::CodeItem* item = reinterpret_cast<const DexFile::CodeItem*>(data);
    builder.BuildGraph(*item);
    graph->TryBuildingSsa();

    // Loop formation is needed to get loop hierarchy in place.
    HLoopFormation formation(graph);
    formation.Run();

    // The IV pass is also needed.
    HFindInductionVariables iv_pass(graph, nullptr, nullptr);
    iv_pass.Run();

    HOnlyInnerLoopIterator inner_iter(GRAPH_TO_GRAPH_X86(graph)->GetLoopInformation());
    HLoopInformation_X86* inner_loop = inner_iter.Current();
    const HLoopBoundInformation& bound_info = inner_loop->GetBoundInformation();

    ASSERT_EQ(bound_info.biv_start_value_, lower);
    ASSERT_EQ(bound_info.biv_end_value_, upper);
    ASSERT_EQ(bound_info.loop_biv_->GetIncrement(), inc);

    GrowableArray<HInductionVariable*>& list = inner_loop->GetInductionVariables();
    ASSERT_EQ(static_cast<int>(list.Size()), nbr_ivs);
  }

  static void TestFPIVBounds(const uint16_t* data, ArenaAllocator* allocator,
                           int nbr_ivs,
                           int lower, int upper,
                           float inc) {
    // Build the graph.
    HGraph_X86* graph = CreateGraph_X86_for_test(allocator);
    HGraphBuilder builder(graph);
    const DexFile::CodeItem* item = reinterpret_cast<const DexFile::CodeItem*>(data);
    builder.BuildGraph(*item);
    graph->TryBuildingSsa();

    // Loop formation is needed to get loop hierarchy in place.
    HLoopFormation formation(graph);
    formation.Run();

    // The IV pass is also needed.
    HFindInductionVariables iv_pass(graph, nullptr, nullptr);
    iv_pass.Run();

    HOnlyInnerLoopIterator inner_iter(GRAPH_TO_GRAPH_X86(graph)->GetLoopInformation());
    HLoopInformation_X86* inner_loop = inner_iter.Current();
    const HLoopBoundInformation& bound_info = inner_loop->GetBoundInformation();

    ASSERT_EQ(bound_info.biv_start_value_, lower);
    ASSERT_EQ(bound_info.biv_end_value_, upper);
    ASSERT_LT(fabs(bound_info.loop_biv_->GetFPIncrement() - inc), 0.001);

    GrowableArray<HInductionVariable*>& list = inner_loop->GetInductionVariables();
    ASSERT_EQ(static_cast<int>(list.Size()), nbr_ivs);
  }

  TEST(LoopIVBoundTest, Simple) {
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
        Instruction::CONST_16 | 2 << 8, 1 << 10,
        Instruction::IF_GE | 0 << 8 | 2 << 12, 0x6,
        Instruction::ADD_INT_2ADDR | 1 << 8,
        Instruction::ADD_INT_LIT8 | 0, 1 << 8,
        Instruction::GOTO | 0xF9 << 8,
        Instruction::RETURN | 1 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestIVBounds(data, &allocator, 1, 0, 1024, 1);
  }

  TEST(LoopIVBoundTest, SimpleDoWhile) {
    /*
     *  Simple while loop:
     *  int result = 0;
     *  int i = 0;
     *  do {
     *    result += i;
     *    i++;
     *  } while (i < 42);
     *  return result;
     */

    const uint16_t data[] = THREE_REGISTERS_CODE_ITEM(
        Instruction::CONST_4 | 0,
        Instruction::MOVE | 1 << 8,
        Instruction::ADD_INT_2ADDR | 1 << 8,
        Instruction::ADD_INT_LIT8 | 0, 2 << 8,
        Instruction::CONST_16 | 2 << 8, 42,
        Instruction::IF_LT | 0 << 8 | 2 << 12, 0xfffb,
        Instruction::RETURN | 1 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestIVBounds(data, &allocator, 1, 0, 42, 2);
  }

  TEST(LoopIVBoundTest, SimpleDoWhileIncBeforeUse) {
    /*
     *  Simple while loop:
     *  int result = 0;
     *  int i = 0;
     *  do {
     *    i++;
     *    result += i;
     *  } while (i < 126);
     *  return result;
     */

    const uint16_t data[] = THREE_REGISTERS_CODE_ITEM(
        Instruction::CONST_4 | 0,
        Instruction::MOVE | 1 << 8,
        Instruction::ADD_INT_LIT8, 4 << 8,
        Instruction::ADD_INT_2ADDR | 1 << 8,
        Instruction::CONST_16 | 2 << 8, 126,
        Instruction::IF_LT | 0 << 8 | 2 << 12, 0xfffb,
        Instruction::RETURN | 1 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestIVBounds(data, &allocator, 1, 0, 126, 4);
  }

  TEST(LoopIVBoundTest, Two) {
    /*
     *  Two IVs for loop:
     *  int result = 0;
     *  int j = 0;
     *  for (int i = 0; i < 10; i++, j++) {
     *    result += i + j;
     *  }
     *  return result;
     */

    const uint16_t data[] = FOUR_REGISTERS_CODE_ITEM(
        Instruction::CONST_4,
        Instruction::MOVE | 1 << 8,
        Instruction::MOVE | 2 << 8,
        Instruction::CONST_16 | 3 << 8, 4 << 8,
        Instruction::IF_GE | 0 << 8 | 3 << 12, 0xa,
        Instruction::ADD_INT | 3 << 8, 1 << 8,
        Instruction::ADD_INT_2ADDR | 2 << 8 | 3 << 12,
        Instruction::ADD_INT_LIT8, 1 << 8,
        Instruction::ADD_INT_LIT8 | 1 << 8, (1 << 8) | 1,
        Instruction::GOTO | 0xF5 << 8,
        Instruction::RETURN | 2 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestIVBounds(data, &allocator, 2, 0, 1024, 1);
  }

  TEST(LoopIVBoundTest, TwoDoWhile) {
    /*
     *  Simple while loop:
     *  int result = 0;
     *  int i = 0;
     *  do {
     *    result += i;
     *    i++;
     *  } while (i < 42);
     *  return result;
     */
    const uint16_t data[] = FOUR_REGISTERS_CODE_ITEM(
        Instruction::CONST_4 | 0,
        Instruction::MOVE | 1 << 8,
        Instruction::MOVE | 2 << 8,
        Instruction::ADD_INT | 3 << 8, 1 << 8,
        Instruction::ADD_INT_2ADDR | 2 << 8 | 3 << 12,
        Instruction::ADD_INT_LIT8, 1 << 8,
        Instruction::ADD_INT_LIT8 | 1 << 8, (1 << 8) | 1,
        Instruction::CONST_16 | 3 << 8, 84,
        Instruction::IF_LT | 0 << 8 | 3 << 12, 0xfff7,
        Instruction::RETURN | 1 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestIVBounds(data, &allocator, 2, 0, 84, 1);
  }

  // Now do floating point ones.
  TEST(LoopIVBoundTest, FloatSimple) {
    /*
     *  Simple for loop:
     *  float result = 0;
     *  for (float i = 0; i < 10; i++) {
     *    result += i;
     *  }
     *  return result;
     */

    const uint16_t data[] = THREE_REGISTERS_CODE_ITEM(
        Instruction::CONST_4 | 0,
        Instruction::MOVE | 1 << 8,
        Instruction::CONST_HIGH16 | 2 << 8, 0x44 << 8 | 0x80,
        Instruction::CMPG_FLOAT | 2 << 8, 2 << 8,
        Instruction::IF_GEZ | 2 << 8, 0x7,
        Instruction::ADD_FLOAT_2ADDR | 1 << 8 | 0 << 12,
        Instruction::CONST_HIGH16 | 2 << 8, 0x3f << 8 | 0x80,
        Instruction::ADD_FLOAT_2ADDR | 0 << 8 | 2 << 12,
        Instruction::GOTO | 0xF6 << 8,
        Instruction::RETURN | 1 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestFPIVBounds(data, &allocator, 1, 0.0, 1024.0, 1.0);
  }

  TEST(LoopIVBoundTest, FloatSimpleDoWhile) {
    /*
     *  Simple while loop:
     *  float result = 0;
     *  float i = 0;
     *  do {
     *    result += i;
     *    i++;
     *  } while (i < 42);
     *  return result;
     */

    const uint16_t data[] = THREE_REGISTERS_CODE_ITEM(
        Instruction::CONST_4 | 0,
        Instruction::MOVE | 1 << 8,
        Instruction::ADD_FLOAT_2ADDR | 1 << 8,
        Instruction::CONST_HIGH16 | 2 << 8, 0x3f << 8 | 0x80,
        Instruction::ADD_FLOAT_2ADDR | 0 << 8 | 2 << 12,
        Instruction::CONST_HIGH16 | 2 << 8, 0x42 << 8 | 0x28,
        Instruction::CMPG_FLOAT | 2 << 8, 2 << 8,
        Instruction::IF_LTZ | 2 << 8, 0xff << 8 | 0xf8,
        Instruction::RETURN | 1 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestFPIVBounds(data, &allocator, 1, 0.0, 42.0, 1.0);
  }

  TEST(LoopIVBoundTest, FloatSimpleDoWhileIncBeforeUse) {
    /*
     *  Simple while loop:
     *  float result = 0;
     *  float i = 0;
     *  do {
     *    i++;
     *    result += i;
     *  } while (i < 126);
     *  return result;
     */

    const uint16_t data[] = THREE_REGISTERS_CODE_ITEM(
        Instruction::CONST_4 | 0,
        Instruction::MOVE | 1 << 8,
        Instruction::CONST_HIGH16 | 2 << 8, 0x3f << 8 | 0x80,
        Instruction::ADD_FLOAT_2ADDR | 0 << 8 | 2 << 12,
        Instruction::ADD_FLOAT_2ADDR | 1 << 8,
        Instruction::CONST_HIGH16 | 2 << 8, 0x42 << 8 | 0xfc,
        Instruction::CMPG_FLOAT | 2 << 8, 2 << 8,
        Instruction::IF_LTZ | 2 << 8, 0xff << 8 | 0xf8,
        Instruction::RETURN | 1 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestFPIVBounds(data, &allocator, 1, 0.0, 126.0, 1.0);
  }

  TEST(LoopIVBoundTest, FloatTwo) {
    /*
     *  Two IVs for loop:
     *  float result = 0;
     *  float j = 0;
     *  for (float i = 0; i < 10; i++, j++) {
     *    result += i + j;
     *  }
     *  return result;
     */

    const uint16_t data[] = FIVE_REGISTERS_CODE_ITEM(
        Instruction::CONST_HIGH16 | 4 << 8, 0x3f << 8 | 0x80,
        Instruction::CONST_4 | 0,
        Instruction::MOVE | 1 << 8,
        Instruction::MOVE | 2 << 8,
        Instruction::CONST_HIGH16 | 3 << 8, 0x44 << 8 | 0x80,
        Instruction::CMPG_FLOAT | 3 << 8, 3 << 8,
        Instruction::IF_GEZ | 3 << 8, 0x8,
        Instruction::ADD_FLOAT | 3 << 8, 1 << 8,
        Instruction::ADD_FLOAT_2ADDR | 2 << 8 | 3 << 12,
        Instruction::ADD_FLOAT_2ADDR | 4 << 12,
        Instruction::ADD_FLOAT_2ADDR | 1 << 8 | 4 << 12,
        Instruction::GOTO | 0xF5 << 8,
        Instruction::RETURN | 2 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestFPIVBounds(data, &allocator, 2, 0.0, 1024.0, 1.0);
  }

  TEST(LoopIVBoundTest, FloatTwoDoWhile) {
    /*
     *  Simple while loop:
     *  float result = 0;
     *  float i = 0;
     *  do {
     *    result += i;
     *    i++;
     *  } while (i < 42);
     *  return result;
     */
    const uint16_t data[] = FIVE_REGISTERS_CODE_ITEM(
        Instruction::CONST_HIGH16 | 4 << 8, 0x3f << 8 | 0x80,
        Instruction::CONST_4 | 0,
        Instruction::MOVE | 1 << 8,
        Instruction::MOVE | 2 << 8,
        Instruction::ADD_FLOAT | 3 << 8, 1 << 8,
        Instruction::ADD_FLOAT_2ADDR | 2 << 8 | 3 << 12,
        Instruction::ADD_FLOAT_2ADDR | 4 << 12,
        Instruction::ADD_FLOAT_2ADDR | 1 << 8 | 4 << 12,
        Instruction::CONST_HIGH16 | 3 << 8, 0x42 << 8 | 0xa8,
        Instruction::CMPG_FLOAT | 3 << 8, 3 << 8,
        Instruction::IF_LTZ | 3 << 8, 0xf7 | 0xff << 8,
        Instruction::RETURN | 2 << 8);

    ArenaPool arena;
    ArenaAllocator allocator(&arena);
    TestFPIVBounds(data, &allocator, 2, 0, 84, 1);
  }
}  // namespace art
