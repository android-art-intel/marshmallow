/*
 * Copyright (C) 2015 The Android Open Source Project
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

public class Main {

  /**
   * Remove unused loop optimization.
   */

  // CHECK-START: void Main.unused_loop() remove_unused_loops (before)
  // CHECK-DAG:     [[Const0:i\d+]]  IntConstant 0
  // CHECK-DAG:     [[Const1:i\d+]]  IntConstant 1
  // CHECK-DAG:     [[Phi:i\d+]]     Phi 1 [ [[Const0]]
  // CHECK-DAG:     [[Add:i\d+]]     Add [ [[Phi]] [[Const1]]

  // CHECK-START: void Main.unused_loop() remove_unused_loops (after)
  // CHECK-NOT:                        Add
  // CHECK-NOT:                        Phi

  public static void unused_loop() {
    for (int i = 0; i < 2000; i++) {
      long l = 200;
      l++;
    }
  }

  // CHECK-START: void Main.argument_loop(int) remove_unused_loops (before)
  // CHECK-DAG:     [[Const0:i\d+]]  IntConstant 0
  // CHECK-DAG:     [[Const1:i\d+]]  IntConstant 1
  // CHECK-DAG:     [[Phi:i\d+]]     Phi 1 [ [[Const0]]
  // CHECK-DAG:     [[Add:i\d+]]     Add [ [[Phi]] [[Const1]]

  // CHECK-START: void Main.argument_loop(int) remove_unused_loops (after)
  // CHECK-DAG:     [[Const0:i\d+]]  IntConstant 0
  // CHECK-DAG:     [[Const1:i\d+]]  IntConstant 1
  // CHECK-DAG:     [[Phi:i\d+]]     Phi 1 [ [[Const0]]
  // CHECK-DAG:     [[Add:i\d+]]     Add [ [[Phi]] [[Const1]]

  public static void argument_loop(int upper) {
    // We can't remove this loop because it is not a countable loop.
    for (int i = 0; i < upper; i++) {
      long l = 200;
      l++;
    }
  }

  public static void main(String[] args) {
    unused_loop();
    argument_loop(200);
  }
}
