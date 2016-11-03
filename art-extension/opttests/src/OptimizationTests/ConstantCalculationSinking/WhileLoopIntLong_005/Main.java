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

package OptimizationTests.ConstantCalculationSinking.WhileLoopIntLong_005;

/**
 *
 * While loop with IV other than 1 and condition that breaks limitation
 * one operation per iteration
 * Expected result: no sinking
 *
 **/


public class Main {                                                                                                                                                   
    final int iterations = 3300;
//    final boolean condition = false;

    public int testLoopAddInt(boolean condition) {
        int testVar = -3017;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = 0;
        while (i < iterations ) {
            if (condition) {
                testVar += setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
            additionalVar += i;
            i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopSubInt(boolean condition) {
        int testVar = -3017;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = 0;
        while (i < iterations ) {
            if (condition) {
                testVar -= setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
              additionalVar += i;
              i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopSubLong(boolean condition) {
        long testVar = -3017;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = 0;
        while (i < iterations ) {
            if (condition) {
                testVar -= setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
              additionalVar += i;
              i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopMulInt(int n, boolean condition) {
        int testVar = -1;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = 0;
        while (i < 55 ) {
            if (condition) {
                testVar *= setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
            additionalVar += i + n;
            i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopMulLong(long n, boolean condition) {
        long testVar = -1;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = 0;
        while (i < 55 ) {
            if (condition) {
                testVar *= setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
              additionalVar += i + n;
              i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopDivInt(boolean condition) {
        int testVar = -3017;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = 0;
        while (i < iterations ) {
            if (condition) {
                testVar /= setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
              additionalVar += i;
              i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopDivLong(boolean condition) {
        long testVar = -3017;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = 0;
        while (i < iterations ) {
            if (condition) {
                testVar /= setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
              additionalVar += i;
              i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopRemInt(boolean condition) {
        int testVar = -3017;
        int additionalVar = 0;
        int setToConstantVar = 3;
        int i = 0;
        while (i < iterations ) {
            if (condition) {
                testVar %= setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
              additionalVar += i;
              i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopRemLong(boolean condition) {
        long testVar = -3017;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = 0;
        while (i < iterations ) {
            if (condition) {
                testVar %= setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
            additionalVar += i;
            i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopAddLong(boolean condition) {
        long testVar = -3017;
        long additionalVar = 0;
        long setToConstantVar = 3;
        long i = 0;
        while (i < iterations ) {
           if (condition) {
                testVar += setToConstantVar;
            }
            if (i%2 != 0) {
                condition = !condition;
            }
              additionalVar += i;
              i = i + 3;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddInt(false));
System.out.println(new Main().testLoopAddLong(false));
System.out.println(new Main().testLoopRemLong(false));
System.out.println(new Main().testLoopRemInt(false));
System.out.println(new Main().testLoopDivLong(true));
System.out.println(new Main().testLoopDivInt(true));
System.out.println(new Main().testLoopMulLong(10, false));
System.out.println(new Main().testLoopMulInt(10, false));
System.out.println(new Main().testLoopSubLong(true));
System.out.println(new Main().testLoopSubInt(true));
    }

}  

