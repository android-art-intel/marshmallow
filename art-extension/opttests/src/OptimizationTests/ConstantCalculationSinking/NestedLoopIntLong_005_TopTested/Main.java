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

package OptimizationTests.ConstantCalculationSinking.NestedLoopIntLong_005_TopTested;

/**
 *
 * Optimization should be applied to the inner loop only
 * 1 sinking excepted for each testcase
 * L: fails due to CAR-961/CAR-800
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 60;

    public int testLoopAddInt() {
        int testVar = 100;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            testVar += 1;
            for (int k = 0; k < iterations; k++) {
                for (int j = 0; j < iterations; j++) {
                    additionalVar += j;
                    testVar += 3;
                    for (int l = 0; l <= iterations; l++) {
                        testVar += 4;
                        additionalVar += j;
                    }
                }
                testVar += 2;
                additionalVar += k;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopSubInt() {
        int testVar = 100;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            testVar -= 1;
            for (int k = 0; k < iterations; k++) {
                for (int j = 0; j < iterations; j++) {
                    additionalVar += j;
                    testVar -= 3;
                    for (int l = 0; l <= iterations; l++) {
                        testVar -= 4;
                        additionalVar += j;
                    }
                }
                testVar -= 2;
                additionalVar += k;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopSubLong() {
        long testVar = 100;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            testVar -= 1;
            for (long k = 0; k < iterations; k++) {
                for (long j = 0; j < iterations; j++) {
                    additionalVar += j;
                    testVar -= 3;
                    for (long l = 0; l <= iterations; l++) {
                        testVar -= 4;
                        additionalVar += j;
                    }
                }
                testVar -= 2;
                additionalVar += k;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar = 10;

        for (int i = 0; i < 2; i++) {
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + i*i - 4*(i+5) + 11 + n;
            testVar *= 3;
            for (int k = 0; k < 2; k++) {
                for (int j = 0; j < 1; j++) {
                    additionalVar += j%2 + j%3 + j%4 + j%5 + j%6 + j%7 + j%8 + j%9 + j%10 + j*j - 4*(j+5) + 11 + n;
                    testVar *= 3;
                    for (int l = 0; l <= 1; l++) {
                        testVar *= 3;
                        additionalVar += l%2 + l%3 + l%4 + l%5 + l%6 + l%7 + l%8 + l%9 + l%10 + l*l - 4*(l+5) + 11 + n;
                    }
                }
                testVar *= 3;
                additionalVar += k%2 + k%3 + k%4 + k%5 + k%6 + k%7 + k%8 + k%9 + k%10 + k*k - 4*(k+5) + 11 + n;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1L;
        long additionalVar = 10;

        for (long i = 0; i < 2; i++) {
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + i*i - 4*(i+5) + 11 + n;
            testVar *= 3;
            for (long k = 0; k < 2; k++) {
                for (long j = 0; j < 1; j++) {
                    additionalVar += j%2 + j%3 + j%4 + j%5 + j%6 + j%7 + j%8 + j%9 + j%10 + j*j - 4*(j+5) + 11 + n;
                    testVar *= 3;
                    for (long l = 0; l <= 1; l++) {
                        testVar *= 3;
                        additionalVar += l%2 + l%3 + l%4 + l%5 + l%6 + l%7 + l%8 + l%9 + l%10 + l*l - 4*(l+5) + 11 + n;
                    }
                }
                testVar *= 3;
                additionalVar += k%2 + k%3 + k%4 + k%5 + k%6 + k%7 + k%8 + k%9 + k%10 + k*k - 4*(k+5) + 11 + n;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopDivInt() {
        int testVar = 100;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            testVar /= 1;
            for (int k = 0; k < iterations; k++) {
                for (int j = 0; j < iterations; j++) {
                    additionalVar += j;
                    testVar /= 3;
                    for (int l = 0; l <= iterations; l++) {
                        testVar /= 4;
                        additionalVar += j;
                    }
                }
                testVar /= 2;
                additionalVar += k;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopDivLong() {
        long testVar = 100;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            testVar /= 1;
            for (long k = 0; k < iterations; k++) {
                for (long j = 0; j < iterations; j++) {
                    additionalVar += j;
                    testVar /= 3;
                    for (long l = 0; l <= iterations; l++) {
                        testVar /= 4;
                        additionalVar += j;
                    }
                }
                testVar /= 2;
                additionalVar += k;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopRemInt() {
        int testVar = 100;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            testVar %= 1;
            for (int k = 0; k < iterations; k++) {
                for (int j = 0; j < iterations; j++) {
                    additionalVar += j;
                    testVar %= 3;
                    for (int l = 0; l <= iterations; l++) {
                        testVar %= 4;
                        additionalVar += j;
                    }
                }
                testVar %= 2;
                additionalVar += k;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopRemLong() {
        long testVar = 100;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            testVar %= 1;
            for (long k = 0; k < iterations; k++) {
                for (long j = 0; j < iterations; j++) {
                    additionalVar += j;
                    testVar %= 3;
                    for (long l = 0; l <= iterations; l++) {
                        testVar %= 4;
                        additionalVar += j;
                    }
                }
                testVar %= 2;
                additionalVar += k;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopAddLong() {
        long testVar = 100;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            testVar += 1;
            for (long k = 0; k < iterations; k++) {
                for (long j = 0; j < iterations; j++) {
                    additionalVar += j;
                    testVar += 3;
                    for (long l = 0; l <= iterations; l++) {
                        testVar += 4;
                        additionalVar += j;
                    }
                }
                testVar += 2;
                additionalVar += k;
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

