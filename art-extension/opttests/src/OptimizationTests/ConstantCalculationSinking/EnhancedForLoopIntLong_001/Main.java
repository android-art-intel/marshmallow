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

package OptimizationTests.ConstantCalculationSinking.EnhancedForLoopIntLong_001;

/**
*
* L: Enhanced for loop is not supported, 0 sinking expected
* M: Enhanced for loop is supported, 1 sinking expected
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 100;

    public int testLoopAddInt() {
        int testVar = 100;
        int additionalVar = 1;
        int[] iterator = new int [iterations];
        for (int i : iterator ) {
            additionalVar += i;
            testVar += 5;
        }

        return testVar;
    }

    public int testLoopSubInt() {
        int testVar = 100;
        int additionalVar = 1;
        int[] iterator = new int [iterations];
        for (int i : iterator ) {
            additionalVar += i;
            testVar -= 5;
        }

        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = 100;
        long additionalVar = 1;
        long[] iterator = new long [iterations];
        for (long i : iterator ) {
            additionalVar += i;
            testVar -= 5;
        }

        return testVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar = 1;
        int[] iterator = new int [18];
        for (int i : iterator ) {
            additionalVar += i + n%2 + i%3 + n%4 + i%5 + n%6 + i%7 + n%8 + i%9;
            testVar *= 3;
        }

        return testVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar = 1;
        long[] iterator = new long [18];
        for (long i : iterator ) {
            additionalVar += i + n%2 + i%3 + n%4 + i%5 + n%6 + i%7 + n%8 + i%9;
            testVar *= 3L;
        }

        return testVar;
    }

    public int testLoopDivInt() {
        int testVar = 100;
        int additionalVar = 1;
        int[] iterator = new int [iterations];
        for (int i : iterator ) {
            additionalVar += i;
            testVar /= 5;
        }

        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = 100;
        long additionalVar = 1;
        long[] iterator = new long [iterations];
        for (long i : iterator ) {
            additionalVar += i;
            testVar /= 5;
        }

        return testVar;
    }

    public int testLoopRemInt() {
        int testVar = 100;
        int additionalVar = 1;
        int[] iterator = new int [iterations];
        for (int i : iterator ) {
            additionalVar += i;
            testVar %= 5;
        }

        return testVar;
    }

    public long testLoopRemLong() {
        long testVar = 100;
        long additionalVar = 1;
        long[] iterator = new long [iterations];
        for (long i : iterator ) {
            additionalVar += i;
            testVar %= 5;
        }

        return testVar;
    }

    public long testLoopAddLong() {
        long testVar = 100;
        long additionalVar = 1;
        long[] iterator = new long [iterations];
        for (long i : iterator ) {
            additionalVar += i;
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
System.out.println(new Main().testLoopMulLong(10L));
System.out.println(new Main().testLoopMulInt(10));
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

