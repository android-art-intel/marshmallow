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

package OptimizationTests.ConstantCalculationSinking.ForLoopIntLong_001;

/**
*
* Simplest case when optimization should be applied
*  1 sinking expected
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar = 0;

        for (int i = 0; i < iterations; i++) {
            testVar += 5;
        }

        return testVar;
    }

    public int testLoopSubInt() {
        int testVar = 0;

        for (int i = 0; i < iterations; i++) {
            testVar -= 5;
        }

        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = 0;

        for (long i = 0; i < iterations; i++) {
            testVar -= 5;
        }

        return testVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar1 = 100;
        int additionalVar2 = 200;
        int additionalVar3 = 300;
        int additionalVar4 = 400;
        int additionalVar5 = 500;

        for (int i = 0; i < 12; i++) {
            testVar *= 3;
            additionalVar1 += i % 2;
            additionalVar2 += i % 4;
            additionalVar3 += i % 6;
            additionalVar4 += i % 8;
            additionalVar5 += i % 10 + n;
        }

        return testVar + additionalVar1 + additionalVar2 + additionalVar3 + additionalVar4 + additionalVar5;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar1 = 100;
        long additionalVar2 = 200;
        long additionalVar3 = 300;
        long additionalVar4 = 400;
        long additionalVar5 = 500;

        for (int i = 0; i < 12; i++) {
            testVar *= 3;
            additionalVar1 += i % 2;
            additionalVar2 += i % 4;
            additionalVar3 += i % 6;
            additionalVar4 += i % 8;
            additionalVar5 += i % 10 + n;
        }

        return testVar + additionalVar1 + additionalVar2 + additionalVar3 + additionalVar4 + additionalVar5;

    }

    public int testLoopDivInt() {
        int testVar = 100;

        for (int i = 0; i < iterations; i++) {
            testVar /= 5;
        }

        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = 100;

        for (long i = 0; i < iterations; i++) {
            testVar /= 5;
        }

        return testVar;
    }

    public int testLoopRemInt() {
        int testVar = 100;

        for (int i = 0; i < iterations; i++) {
            testVar %= 5;
        }

        return testVar;
    }

    public long testLoopRemLong() {
        long testVar = 100;

        for (long i = 0; i < iterations; i++) {
            testVar %= 5;
        }

        return testVar;
    }

    public long testLoopAddLong() {
        long testVar = 0;

        for (long i = 0; i < iterations; i++) {
            testVar += 5;
        }

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

