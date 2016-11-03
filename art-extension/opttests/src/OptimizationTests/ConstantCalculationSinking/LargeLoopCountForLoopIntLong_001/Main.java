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

package OptimizationTests.ConstantCalculationSinking.LargeLoopCountForLoopIntLong_001;


/**
*
* 1 sinking expected: basic test case, large loop count
*
**/

public class Main {                                                                                                                                                   
    final int iterations = Integer.MAX_VALUE;

    public int testLoopAddInt() {
        int testVar = Integer.MIN_VALUE;

        for (int i = 0; i < iterations; i++) {
            testVar += 2;
        }

        return testVar;
    }

    public int testLoopSubInt() {
        int testVar = Integer.MIN_VALUE;

        for (int i = 0; i < iterations; i++) {
            testVar -= 2;
        }

        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = Long.MAX_VALUE;

        for (long i = 0; i < iterations; i++) {
            testVar -= 2;
        }

        return testVar;
    }

    /*
     * can't check multiplication with large loop count
     *
    public int testLoopMulInt() {
        int testVar = Integer.MIN_VALUE;

        for (int i = 0; i < iterations; i++) {
            testVar *= 2;
        }

        return testVar;
    }

    public long testLoopMulLong() {
        long testVar = Integer.MIN_VALUE;

        for (long i = 0; i < iterations; i++) {
            testVar *= 2;
        }

        return testVar;
    }
    *
    */

    public int testLoopDivInt() {
        int testVar = Integer.MIN_VALUE;

        for (int i = 0; i < iterations; i++) {
            testVar /= 2;
        }

        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = Long.MIN_VALUE;

        for (long i = 0; i < iterations; i++) {
            testVar /= 2;
        }

        return testVar;
    }

    public int testLoopRemInt() {
        int testVar = Integer.MIN_VALUE;

        for (int i = 0; i < iterations; i++) {
            testVar %= 2;
        }

        return testVar;
    }

    public long testLoopRemLong() {
        long testVar = Long.MIN_VALUE;

        for (long i = 0; i < iterations; i++) {
            testVar %= 2;
        }

        return testVar;
    }

    public long testLoopAddLong() {
        long testVar = Long.MIN_VALUE;

        for (long i = 0; i < iterations; i++) {
            testVar += 2;
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
//System.out.println(new Main().testLoopMulLong());
//System.out.println(new Main().testLoopMulInt());
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

