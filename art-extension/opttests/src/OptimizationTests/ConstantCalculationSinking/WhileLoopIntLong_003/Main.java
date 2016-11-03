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

package OptimizationTests.ConstantCalculationSinking.WhileLoopIntLong_003;

/**
 *
 * While loop with count down  IV other than 1
 * L: Expected result: 1 sinking, currently fail due to CAR-876
 * M: Expected result: 1 sinking, currently fail
 *
 **/


public class Main {                                                                                                                                                   
    final int iterations = 3300;

    public int testLoopAddInt() {
        int testVar = -3017;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = iterations - 1;
        while (i >= 0 ) {
              testVar += setToConstantVar;
              additionalVar += i;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopSubInt() {
        int testVar = -3017;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = iterations - 1;
        while (i >= 0 ) {
              testVar -= setToConstantVar;
              additionalVar += i;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = -3017;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = iterations - 1;
        while (i >= 0 ) {
              testVar -= setToConstantVar;
              additionalVar += i;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = -1;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = 55;
        while (i >= 0 ) {
              testVar *= setToConstantVar;
              additionalVar += i + n;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = -1;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = 55;
        while (i >= 0 ) {
              testVar *= setToConstantVar;
              additionalVar += i + n;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopDivInt() {
        int testVar = -3017;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = iterations - 1;
        while (i >= 0 ) {
              testVar /= setToConstantVar;
              additionalVar += i;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = -3017;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = iterations - 1;
        while (i >= 0 ) {
              testVar /= setToConstantVar;
              additionalVar += i;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopRemInt() {
        int testVar = -3017;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = iterations - 1;
        while (i >= 0 ) {
              testVar %= setToConstantVar;
              additionalVar += i;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopRemLong() {
        long testVar = -3017;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = iterations - 1;
        while (i >= 0 ) {
              testVar %= setToConstantVar;
              additionalVar += i;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopAddLong() {
        long testVar = -3017;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = iterations - 1;
        while (i >= 0 ) {
              testVar += setToConstantVar;
              additionalVar += i;
              i = i - 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddInt());
System.out.println(new Main().testLoopAddLong());
System.out.println(new Main().testLoopRemLong());
System.out.println(new Main().testLoopRemInt());
System.out.println(new Main().testLoopDivLong());
System.out.println(new Main().testLoopDivInt());
System.out.println(new Main().testLoopMulLong(10));
System.out.println(new Main().testLoopMulInt(10));
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

