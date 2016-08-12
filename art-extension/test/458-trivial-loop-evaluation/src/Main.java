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
   * Trivial Loop Evaluator optimization.
   */

  // CHECK-START: void Main.trivial_loop() trivial_loop_evaluator (before)
  // CHECK-DAG:     [[Const0:i\d+]]  IntConstant 0
  // CHECK-DAG:     [[Const1:i\d+]]  IntConstant 1
  // CHECK-DAG:     [[Phi:i\d+]]     Phi 1 [ [[Const0]]
  // CHECK-DAG:     [[Add:i\d+]]     Add [ [[Phi]] [[Const1]]

  // CHECK-START: void Main.trivial_loop() trivial_loop_evaluator (after)
  // CHECK-NOT:                        Add
  // CHECK-NOT:                        Phi

  public static void trivial_loop() {
    short sum = 0;
    for (short i = 0; i < 500; i++) {
      sum += i;
    }
  }

  public static void main(String[] args) {
    trivial_loop();
  }
}
