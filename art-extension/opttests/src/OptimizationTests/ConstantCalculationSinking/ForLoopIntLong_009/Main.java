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

package OptimizationTests.ConstantCalculationSinking.ForLoopIntLong_009;

/**
*
* Count-down loops should be supported, now fails (L: CAR-876)
* Expected result: 1 sinking
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar = 0x100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = iterations - 1; i>=0; --i) {
              testVar += setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public int testLoopSubInt() {
        int testVar = 0x100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = iterations - 1; i>=0; --i) {
              testVar -= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar -= additionalVar;
        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = 0x100;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = iterations - 1; i>=0; --i) {
              testVar -= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar -= additionalVar;
        return testVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = 15; i>=0; --i) {
              testVar *= setToConstantVar;
              additionalVar += (i*2)%5 + n + i%2 - n%3 + i%4 - n%5 + i%6 - n%7 + i%8 - n%9;
        }
        testVar += additionalVar;
        return testVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = 15; i>=0; --i) {
              testVar *= setToConstantVar;
              additionalVar += (i*2)%5 + n + i%2 - n%3 + i%4 - n%5 + i%6 - n%7 + i%8 - n%9;
        }
        testVar += additionalVar;
        return testVar;
    }

    public int testLoopDivInt() {
        int testVar = 0x100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = iterations - 1; i>=0; --i) {
              testVar /= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar /= additionalVar;
        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = 0x100;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = iterations - 1; i>=0; --i) {
              testVar /= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar /= additionalVar;
        return testVar;
    }

    public int testLoopRemInt() {
        int testVar = 0x100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = iterations - 1; i>=0; --i) {
              testVar %= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public long testLoopRemLong() {
        long testVar = 0x100;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = iterations - 1; i>=0; --i) {
              testVar %= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public long testLoopAddLong() {
        long testVar = 0x100;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = iterations - 1; i>=0; --i) {
              testVar += setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
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

