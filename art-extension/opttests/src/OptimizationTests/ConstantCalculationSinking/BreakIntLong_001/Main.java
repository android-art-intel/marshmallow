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

package OptimizationTests.ConstantCalculationSinking.BreakIntLong_001;

/**
*
* L: Break operator doesn't break one basic block limitation for int, 
* for some reasons (most probably not a bug), there are two basic blocks for long type,
* 1 sinking expected for int, 0 for long.
* M: no limitations on basic blocks number, 1 constant calculation sinking expected for
* each method
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar = -1000;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            additionalVar += i;
         for (int k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar += 5;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopSubInt() {
        int testVar = -1000;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            additionalVar += i;
         for (int k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar -= 5;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopSubLong() {
        long testVar = -1000;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            additionalVar += i;
         for (long k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar -= 5;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = -3;
        int additionalVar = 10;

        for (int i = 0; i < 3; i++) {
            additionalVar += i + i%2 + i%3 + i%4 + i%5 + i%6 +i%7 + i%8 + i%9 + i%10 + i%11 + i%12 - i*2 + i*4 - i*(i + 1) + n*2;

            for (int k = 0; k < 5; k++) {
                additionalVar += k + k%2 + k%3 + k%4 + k%5 + k%6 +k%7 + k%8 + k%9 + k%10 + k%11 + k%12 - k*2 + k*3 - k*4 + k*(k+1)*(k-1) + n%2;
                testVar *= 3;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = 2;
        long additionalVar = 10;

        for (long i = 0; i < 5; i++) {
            additionalVar += i+ i%2 + i%3 + i%4 + i%5 + i%6 +i%7 + i%8 + i%9 + i%10 + i%11 + i%12 + n*2;
             for (long k = 0; k < 5; k++) {
                    additionalVar += k + k%2 + k%3 + k%4 + k%5 + k%6 +k%7 + k%8 + k%9 + k%10 + k%11 + k%12 + n%2;
                testVar *= 3;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopDivInt() {
        int testVar = -1000;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            additionalVar += i;
         for (int k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar /= 5;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopDivLong() {
        long testVar = -1000;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            additionalVar += i;
         for (long k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar /= 5;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public int testLoopRemInt() {
        int testVar = -1000;
        int additionalVar = 10;

        for (int i = 0; i < iterations; i++) {
            additionalVar += i;
         for (int k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar %= 5;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopRemLong() {
        long testVar = -1000;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            additionalVar += i;
         for (long k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar %= 5;
	            break;
            }
        }

        return testVar + additionalVar;
    }

    public long testLoopAddLong() {
        long testVar = -1000;
        long additionalVar = 10;

        for (long i = 0; i < iterations; i++) {
            additionalVar += i;
         for (long k = 0; k < iterations; k++) {
                additionalVar += k;
                testVar += 5;
	            break;
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
System.out.println(new Main().testLoopMulLong(10));
System.out.println(new Main().testLoopMulInt(10));
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

