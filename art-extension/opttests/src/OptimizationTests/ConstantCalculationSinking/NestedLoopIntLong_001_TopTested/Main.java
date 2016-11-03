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

package OptimizationTests.ConstantCalculationSinking.NestedLoopIntLong_001_TopTested;

/**
 *
 * Optimization should be applied to the inner loop only
 * 1 sinking excepted for each testcase
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 100;

    public int testLoopAddInt() {
        int testVar = 100;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            additionalVar += i;
            for (int k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar += 5;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopSubInt() {
        int testVar = 100;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            additionalVar += i;
            for (int k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar -= 5;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopSubLong() {
        long testVar = 100;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            additionalVar += i;
            for (long k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar -= 5;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar = 10;

        for (int i = 0; i < 3; i++) {
            additionalVar += i;
            for (int k = 0; k < 6; k++) {
                additionalVar += k + n%2 + k%3 + n%4 + k%5 + n%6 + k%7 + n%8 + i%9;
                testVar *= 3;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar = 10;

        for (long i = 0; i < 3; i++) {
            additionalVar += i;
            for (long k = 0; k < 6; k++) {
                additionalVar += k + n%2 + k%3 + n%4 + k%5 + n%6 + k%7 + n%8 + i%9;
                testVar *= 3;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopDivInt() {
        int testVar = 100;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            additionalVar += i;
            for (int k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar /= 5;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopDivLong() {
        long testVar = 100;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            additionalVar += i;
            for (long k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar /= 5;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopRemInt() {
        int testVar = 100;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            additionalVar += i;
            for (int k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar %= 5;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopRemLong() {
        long testVar = 100;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            additionalVar += i;
            for (long k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar %= 5;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopAddLong() {
        long testVar = 100;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            additionalVar += i;
            for (long k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar += 5;
            }
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

