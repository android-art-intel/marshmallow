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

package OptimizationTests.ConstantCalculationSinking.VolatileFieldForLoopIntLong_001;

/**
 *
 * No sinking expected: only local variable can be sunk
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 1100;
    volatile int testVar = 0;

    public int testLoopAddInt() {
        testVar = 10005;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
            testVar += 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9;
        }

        return testVar + additionalVar;
    }

    public int testLoopSubInt() {
        testVar = 10005;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
            testVar -= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9;
        }

        return testVar + additionalVar;
    }

    public long testLoopSubLong() {
        testVar = 10005;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
            testVar -= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9;
        }

        return testVar + additionalVar;
    }

    public int testLoopMulInt(int n) {
        testVar = 1;
        int additionalVar = 0;
        for (int i = 0; i < 18; i++) {
            testVar *= 3;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9 + n;
        }

        return testVar + additionalVar;
    }

    public long testLoopMulLong(long n) {
        testVar = 1;
        long additionalVar = 0;
        for (long i = 0; i < 18; i++) {
            testVar *= 3;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9 + n;
        }

        return testVar + additionalVar;
    }

    public int testLoopDivInt() {
        testVar = 10005;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
            testVar /= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9;
        }

        return testVar + additionalVar;
    }

    public long testLoopDivLong() {
        testVar = 10005;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
            testVar /= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9;
        }

        return testVar + additionalVar;
    }

    public int testLoopRemInt() {
        testVar = 10005;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
            testVar %= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9;
        }

        return testVar + additionalVar;
    }

    public long testLoopRemLong() {
        testVar = 10005;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
            testVar %= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9;
        }

        return testVar + additionalVar;
    }

    public long testLoopAddLong() {
        testVar = 10005;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
            testVar += 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 +i%9;
        }

        return testVar + additionalVar;
    }

    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddInt());
System.out.println(new Main().testLoopAddLong());
System.out.println(new Main().testLoopRemLong());
System.out.println(new Main().testLoopRemInt());
System.out.println(new Main().testLoopDivLong());
System.out.println(new Main().testLoopDivInt());
System.out.println(new Main().testLoopMulLong(10L));
System.out.println(new Main().testLoopMulInt(10));
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

