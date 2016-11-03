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

package OptimizationTests.ConstantCalculationSinking.NonConstantRightSideForLoopIntLong_001;

/**
  *
  * Non-constant right side is not supported even if it's invariant
  * Expected result: no sinking
  **/

public class Main {                                                                                                                                                   
    final int iterations = 100;

    public int testLoopAddInt(int additionalVar) {
        int testVar = 1000;
        for (int i = 0; i < iterations; i++) {
              testVar += additionalVar;
        }
        return testVar;
    }

    public int testLoopSubInt(int additionalVar) {
        int testVar = 1000;
        for (int i = 0; i < iterations; i++) {
              testVar -= additionalVar;
        }
        return testVar;
    }

    public long testLoopSubLong(long additionalVar) {
        long testVar = 1000;
        for (long i = 0; i < iterations; i++) {
              testVar -= additionalVar;
        }
        return testVar;
    }

    public int testLoopMulInt(int additionalVar) {
        int testVar = 1;
        int s = 0;
        for (int i = 0; i < 18; i++) {
              testVar *= additionalVar;
              s += i%2 + i%3 + i%4 + i%5;
        }
        return testVar + s;
    }

    public long testLoopMulLong(long additionalVar) {
        long testVar = 1;
        int s = 0;
        for (long i = 0; i < 18; i++) {
              testVar *= additionalVar;
              s += i%2 + i%3 + i%4 + i%5;
        }
        return testVar + s;
    }

    public int testLoopDivInt(int additionalVar) {
        int testVar = 1000;
        for (int i = 0; i < iterations; i++) {
              testVar /= additionalVar;
        }
        return testVar;
    }

    public long testLoopDivLong(long additionalVar) {
        long testVar = 1000;
        for (long i = 0; i < iterations; i++) {
              testVar /= additionalVar;
        }
        return testVar;
    }

    public int testLoopRemInt(int additionalVar) {
        int testVar = 1000;
        for (int i = 0; i < iterations; i++) {
              testVar %= additionalVar;
        }
        return testVar;
    }

    public long testLoopRemLong(long additionalVar) {
        long testVar = 1000;
        for (long i = 0; i < iterations; i++) {
              testVar %= additionalVar;
        }
        return testVar;
    }

    public long testLoopAddLong(long additionalVar) {
        long testVar = 1000;
        for (long i = 0; i < iterations; i++) {
              testVar += additionalVar;
        }
        return testVar;
    }

    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddInt(5));
System.out.println(new Main().testLoopAddLong(5));
System.out.println(new Main().testLoopRemLong(5));
System.out.println(new Main().testLoopRemInt(5));
System.out.println(new Main().testLoopDivLong(5));
System.out.println(new Main().testLoopDivInt(5));
System.out.println(new Main().testLoopMulLong(3));
System.out.println(new Main().testLoopMulInt(3));
System.out.println(new Main().testLoopSubLong(5));
System.out.println(new Main().testLoopSubInt(5));
    }

}  

