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

package OptimizationTests.ConstantCalculationSinking.ForLoopIntLong_011_TopTested;

/**
*
* Float-point induction variable
* 1 sinking expected
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar = 0xC1C1C1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < iterations; f = f + 1) {
            testVar += 5;
            additionalVar = (additionalVar + f)/2;
        }

        return testVar + Math.round(additionalVar);
    }

    public int testLoopSubInt() {
        int testVar = 0xC1C1C1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < iterations; f = f + 1) {
            testVar -= 5;
            additionalVar = (additionalVar + f)/2;
        }

        return testVar + Math.round(additionalVar);
    }

    public long testLoopSubLong() {
        long testVar = 0xC1C1C1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < iterations; f = f + 1) {
            testVar -= 5;
            additionalVar = (additionalVar + f)/2;
        }

        return testVar + Math.round(additionalVar);
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < 18f; f = f + 1) {
            testVar *= 3;
            additionalVar = (additionalVar + f)/2 + n + f*n + f*2.5f - f*3.14f + f*1.01f - f*2.12345f + f/5.2f + f*2.1f;
        }

        return testVar + Math.round(additionalVar);
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < 25f; f = f + 1) {
            testVar *= 3;
            additionalVar = (additionalVar + f)/2 + n;
        }

        return testVar + Math.round(additionalVar);
    }

    public int testLoopDivInt() {
        int testVar = 0xC1C1C1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < iterations; f = f + 1) {
            testVar /= 5;
            additionalVar = (additionalVar + f)/2;
        }

        return testVar + Math.round(additionalVar);
    }

    public long testLoopDivLong() {
        long testVar = 0xC1C1C1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < iterations; f = f + 1) {
            testVar /= 5;
            additionalVar = (additionalVar + f)/2;
        }

        return testVar + Math.round(additionalVar);
    }

    public int testLoopRemInt() {
        int testVar = 0xC1C1C1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < iterations; f = f + 1) {
            testVar %= 5;
            additionalVar = (additionalVar + f)/2;
        }

        return testVar + Math.round(additionalVar);
    }

    public long testLoopRemLong() {
        long testVar = 0xC1C1C1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < iterations; f = f + 1) {
            testVar %= 5;
            additionalVar = (additionalVar + f)/2;
        }

        return testVar + Math.round(additionalVar);
    }

    public long testLoopAddLong() {
        long testVar = 0xC1C1C1;
        float additionalVar = -1.0f;

        for (float f = 0.0f; f < iterations; f = f + 1) {
            testVar += 5;
            additionalVar = (additionalVar + f)/2;
        }

        return testVar + Math.round(additionalVar);
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

