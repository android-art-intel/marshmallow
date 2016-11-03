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

package OptimizationTests.ConstantCalculationSinking.NestedLoopIntLong_006;

/**
 *
 * Optimization should be applied to the inner loop only
 * 1 sinking excepted for each testcase
 * L: ails due to CAR-961/CAR-800
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 100;

    public int testLoopAddInt() {
        int testVar1 = 100;
        int testVar2 = 200;
        int additionalVar1 = 10;
        int additionalVar2 = 20;

        int i = 1;

        while (i < iterations) {
            int j = iterations;
            while (j < iterations + iterations) {
                testVar2 += 5;
                additionalVar2 += j;
                j = j + 2;
            }
            additionalVar1 += i;
            testVar1 += -1;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public int testLoopSubInt() {
        int testVar1 = 100;
        int testVar2 = 200;
        int additionalVar1 = 10;
        int additionalVar2 = 20;

        int i = 1;

        while (i < iterations) {
            int j = iterations;
            while (j < iterations + iterations) {
                testVar2 -= 5;
                additionalVar2 += j;
                j = j + 2;
            }
            additionalVar1 += i;
            testVar1 -= -1;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public long testLoopSubLong() {
        long testVar1 = 100;
        long testVar2 = 200;
        long additionalVar1 = 10;
        long additionalVar2 = 20;

        long i = 1;

        while (i < iterations) {
            long j = iterations;
            while (j < iterations + iterations) {
                testVar2 -= 5;
                additionalVar2 += j;
                j = j + 2;
            }
            additionalVar1 += i;
            testVar1 -= -1;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public int testLoopMulInt(int n) {
        int testVar1 = 1;
        int testVar2 = 2;
        int additionalVar1 = 10;
        int additionalVar2 = 20;

        int i = 1;

        while (i < 3) {
            int j = 5;
            while (j < 10) {
                testVar2 *= 3;
                additionalVar2 += j%2 + j%3 + j%4 + j%5 + j%6 + j%7 + j%8 + j%9 + j%10 + n;
                j = j + 2;
            }
            additionalVar1 += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
            testVar1 *= -3;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public long testLoopMulLong(long n) {
        long testVar1 = 1L;
        long testVar2 = 2L;
        long additionalVar1 = 10;
        long additionalVar2 = 20;

        long i = 1;

        while (i < 3) {
            long j = 5;
            while (j < 10) {
                testVar2 *= 3;
                additionalVar2 += j%2 + j%3 + j%4 + j%5 + j%6 + j%7 + j%8 + j%9 + j%10 + n;
                j = j + 2;
            }
            additionalVar1 += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
            testVar1 *= -3;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public int testLoopDivInt() {
        int testVar1 = 100;
        int testVar2 = 200;
        int additionalVar1 = 10;
        int additionalVar2 = 20;

        int i = 1;

        while (i < iterations) {
            int j = iterations;
            while (j < iterations + iterations) {
                testVar2 /= 5;
                additionalVar2 += j;
                j = j + 2;
            }
            additionalVar1 += i;
            testVar1 /= -1;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public long testLoopDivLong() {
        long testVar1 = 100;
        long testVar2 = 200;
        long additionalVar1 = 10;
        long additionalVar2 = 20;

        long i = 1;

        while (i < iterations) {
            long j = iterations;
            while (j < iterations + iterations) {
                testVar2 /= 5;
                additionalVar2 += j;
                j = j + 2;
            }
            additionalVar1 += i;
            testVar1 /= -1;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public int testLoopRemInt() {
        int testVar1 = 100;
        int testVar2 = 200;
        int additionalVar1 = 10;
        int additionalVar2 = 20;

        int i = 1;

        while (i < iterations) {
            int j = iterations;
            while (j < iterations + iterations) {
                testVar2 %= 5;
                additionalVar2 += j;
                j = j + 2;
            }
            additionalVar1 += i;
            testVar1 %= -1;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public long testLoopRemLong() {
        long testVar1 = 100;
        long testVar2 = 200;
        long additionalVar1 = 10;
        long additionalVar2 = 20;

        long i = 1;

        while (i < iterations) {
            long j = iterations;
            while (j < iterations + iterations) {
                testVar2 %= 5;
                additionalVar2 += j;
                j = j + 2;
            }
            additionalVar1 += i;
            testVar1 %= -1;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
    }

    public long testLoopAddLong() {
        long testVar1 = 100;
        long testVar2 = 200;
        long additionalVar1 = 10;
        long additionalVar2 = 20;

        long i = 1;

        while (i < iterations) {
            long j = iterations;
            while (j < iterations + iterations) {
                testVar2 += 5;
                additionalVar2 += j;
                j = j + 2;
            }
            additionalVar1 += i;
            testVar1 += -1;
            i++;
        }

        return testVar1 + testVar2 + additionalVar1 + additionalVar2;
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

