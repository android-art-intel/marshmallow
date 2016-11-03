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

package OptimizationTests.ConstantCalculationSinking.StaticFieldForLoopIntLong_001;

/**
  *
  * No sinking expected: according to optimization document only local variable can be sunk
  *
  **/

public class Main {                                                                                                                                                   
    final int iterations = 100;
    static int testVar = 0;

    public int testLoopAddInt() {
        testVar = 1;
        int additionalVar = 10;
        for (int i = 0; i < iterations; i++) {
            testVar += 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11;
        }

        return testVar;
    }

    public int testLoopSubInt() {
        testVar = 1;
        int additionalVar = 10;
        for (int i = 0; i < iterations; i++) {
            testVar -= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11;
        }

        return testVar;
    }

    public long testLoopSubLong() {
        testVar = 1;
        long additionalVar = 10;
        for (long i = 0; i < iterations; i++) {
            testVar -= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11;
        }

        return testVar;
    }

    public int testLoopMulInt(int n) {
        testVar = 1;
        int additionalVar = 10;
        for (int i = 0; i < 10; i++) {
            testVar *= 3;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11 + n;
        }

        return testVar;
    }

    public long testLoopMulLong(long n) {
        testVar = 1;
        long additionalVar = 10;
        for (long i = 0; i < 10; i++) {
            testVar *= 3;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11 + n;
        }

        return testVar;
    }

    public int testLoopDivInt() {
        testVar = 1;
        int additionalVar = 10;
        for (int i = 0; i < iterations; i++) {
            testVar /= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11;
        }

        return testVar;
    }

    public long testLoopDivLong() {
        testVar = 1;
        long additionalVar = 10;
        for (long i = 0; i < iterations; i++) {
            testVar /= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11;
        }

        return testVar;
    }

    public int testLoopRemInt() {
        testVar = 1;
        int additionalVar = 10;
        for (int i = 0; i < iterations; i++) {
            testVar %= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11;
        }

        return testVar;
    }

    public long testLoopRemLong() {
        testVar = 1;
        long additionalVar = 10;
        for (long i = 0; i < iterations; i++) {
            testVar %= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11;
        }

        return testVar;
    }

    public long testLoopAddLong() {
        testVar = 1;
        long additionalVar = 10;
        for (long i = 0; i < iterations; i++) {
            testVar += 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + 4*(i+5) + 11;
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

