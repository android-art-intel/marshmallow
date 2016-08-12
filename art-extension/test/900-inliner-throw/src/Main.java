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

  public static int inlinePossibleNPE(Main m) {
    return foo(m);
  }

  static int foo(Main m) {
    return m.field;
  }

  int field = 42;

  public static int inlinePossibleOOBE(int[] arr) {
    return bar(arr);
  }

  static int bar(int[] arr) {
    return arr[1];
  }

  public static int inlinePossibleAE(int denominator) {
    return baz(denominator);
  }

  static int baz(int denominator) {
    return 10 / denominator;
  }

  public static void main(String[] args) {
    if (inlinePossibleNPE(new Main()) != 42) {
      throw new Error("Expected 42");
    }

    try {
      inlinePossibleNPE(null);
      throw new Error("Did not expect to reach here");
    } catch (Exception ex) {
      if (!(ex instanceof NullPointerException)) {
        throw new Error("Expected NullPointerException");
      }
    }

    int[] arr1 = new int[2];
    int[] arr2 = new int[1];
    arr1[1] = 42;

    if (inlinePossibleOOBE(arr1) != 42) {
      throw new Error("Expected 42");
    }

    try {
      inlinePossibleOOBE(arr2);
      throw new Error("Did not expect to reach here");
    } catch (Exception ex) {
      if (!(ex instanceof ArrayIndexOutOfBoundsException)) {
        throw new Error("Expected ArrayIndexOutOfBoundsException");
      }
    }

    if (inlinePossibleAE(5) != 2) {
      throw new Error("Expected 2");
    }

    try {
      inlinePossibleAE(0);
      throw new Error("Did not expect to reach here");
    } catch (Exception ex) {
      if (!(ex instanceof ArithmeticException)) {
        throw new Error("Expected ArithmeticException");
      }
    }
  }
}
