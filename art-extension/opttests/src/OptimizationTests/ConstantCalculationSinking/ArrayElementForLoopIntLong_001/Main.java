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

package OptimizationTests.ConstantCalculationSinking.ArrayElementForLoopIntLong_001;

/**
*
* No sinking expected: according to update from Jc only local variable can be sunk
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 100;

    public int testLoopAddInt() {
        int[] testVar = new int[] {100, 200, 300};

        for (int i = 0; i < iterations; i++) {
            testVar[0] += 5;
        }

        return testVar[0] + testVar[1] + testVar[2];
    }

    public int testLoopSubInt() {
        int[] testVar = new int[] {100, 200, 300};

        for (int i = 0; i < iterations; i++) {
            testVar[0] -= 5;
        }

        return testVar[0] + testVar[1] + testVar[2];
    }

    public long testLoopSubLong() {
        long[] testVar = new long[] {100, 200, 300};

        for (long i = 0; i < iterations; i++) {
            testVar[0] -= 5;
        }

        return testVar[0] + testVar[1] + testVar[2];
    }

    public int testLoopMulInt() {
        int[] testVar = new int[] {1, 200, 300};
        int additionalVar = 0;
        for (int i = 0; i < 25; i++) {
            testVar[0] *= 2;
            additionalVar += i/2;
        }

        return testVar[0] + testVar[1] + testVar[2] + additionalVar;
    }

    public long testLoopMulLong() {
        long[] testVar = new long[] {1, 200, 300};
        int additionalVar = 0;
        for (long i = 0; i < 25; i++) {
            testVar[0] *= 2;
            additionalVar += i/2;
        }

        return testVar[0] + testVar[1] + testVar[2] + additionalVar;
    }

    public int testLoopDivInt() {
        int[] testVar = new int[] {100000, 200, 300};

        for (int i = 0; i < iterations; i++) {
            testVar[0] /= 5;
        }

        return testVar[0] + testVar[1] + testVar[2];
    }

    public long testLoopDivLong() {
        long[] testVar = new long[] {100000, 200, 300};

        for (long i = 0; i < iterations; i++) {
            testVar[0] /= 5;
        }

        return testVar[0] + testVar[1] + testVar[2];
    }

    public int testLoopRemInt() {
        int[] testVar = new int[] {100, 200, 300};

        for (int i = 0; i < iterations; i++) {
            testVar[0] %= 5;
        }

        return testVar[0] + testVar[1] + testVar[2];
    }

    public long testLoopRemLong() {
        long[] testVar = new long[] {100, 200, 300};

        for (long i = 0; i < iterations; i++) {
            testVar[0] %= 5;
        }

        return testVar[0] + testVar[1] + testVar[2];
    }

    public long testLoopAddLong() {
        long[] testVar = new long[] {100, 200, 300};

        for (long i = 0; i < iterations; i++) {
            testVar[0] += 5;
        }

        return testVar[0] + testVar[1] + testVar[2];
    }

    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddInt());
System.out.println(new Main().testLoopAddLong());
System.out.println(new Main().testLoopRemLong());
System.out.println(new Main().testLoopRemInt());
System.out.println(new Main().testLoopDivLong());
System.out.println(new Main().testLoopDivInt());
System.out.println(new Main().testLoopMulLong());
System.out.println(new Main().testLoopMulInt());
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

