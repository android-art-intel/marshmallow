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

package OptimizationTests.ConstantCalculationSinking.ForLoopIntLong_005;

/**
*
* Handling multiple loops: should be supported
* Expected result: 4 sinkings
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar1 = 10;
        int testVar2 = 20;
        int testVar3 = 30;
        int testVar4 = 40;
        long additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
              testVar1 += 1;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar2 += 2;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar3 += 3;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar4 += 4;
              additionalVar = ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public long testLoopAddLong() {
        long testVar1 = 10;
        long testVar2 = 20;
        long testVar3 = 30;
        long testVar4 = 40;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
              testVar1 += 1;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar2 += 2;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar3 += 3;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar4 += 4;
              additionalVar = ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public int testLoopSubInt() {
        int testVar1 = 10;
        int testVar2 = 20;
        int testVar3 = 30;
        int testVar4 = 40;
        long additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
              testVar1 -= 1;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar2 -= 2;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar3 -= 3;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar4 -= 4;
              additionalVar = ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public long testLoopSubLong() {
        long testVar1 = 10;
        long testVar2 = 20;
        long testVar3 = 30;
        long testVar4 = 40;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
              testVar1 -= 1;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar2 -= 2;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar3 -= 3;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar4 -= 4;
              additionalVar = ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public int testLoopMulInt(int n) {
        int testVar1 = 1;
        int testVar2 = 1;
        int testVar3 = 2;
        int testVar4 = 2;
        long additionalVar = 0;
        for (int i = 0; i < 18; i++) {
              testVar1 *= 3;
              additionalVar = ((i)*2)%5 + n + n*i + i%2 + i%3 + i%4;
        }
        for (int i = 0; i < 18; i++) {
              testVar2 *= 3;
              additionalVar = ((i)*2)%5 + n + n*i + i%4 + i%5 + i%6;
        }
        for (int i = 0; i < 18; i++) {
              testVar3 *= 3;
              additionalVar = ((i)*2)%5 + n + n*i + i%7 + i%8 + i%9;
        }
        for (int i = 0; i < 18; i++) {
              testVar4 *= 3;
              additionalVar = ((i)*2)%5 + n + n*i + i%2 + i%5 + i%8;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public long testLoopMulLong(long n) {
        long testVar1 = 1;
        long testVar2 = 1;
        long testVar3 = 2;
        long testVar4 = 2;
        long additionalVar = 0;
        for (long i = 0; i < 25; i++) {
              testVar1 *= 3;
              additionalVar = ((i)*2)%5 + n;
        }
        for (long i = 0; i < 25; i++) {
              testVar2 *= 3;
              additionalVar = ((i)*2)%5 + n;
        }
        for (long i = 0; i < 25; i++) {
              testVar3 *= 3;
              additionalVar = ((i)*2)%5 + n;
        }
        for (long i = 0; i < 25; i++) {
              testVar4 *= 3;
              additionalVar = ((i)*2)%5 + n;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public int testLoopDivInt() {
        int testVar1 = 1000;
        int testVar2 = 2000;
        int testVar3 = 3000;
        int testVar4 = 4000;
        long additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
              testVar1 /= 2;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar2 /= 2;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar3 /= 3;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar4 /= 4;
              additionalVar = ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public long testLoopDivLong() {
        long testVar1 = 1000;
        long testVar2 = 2000;
        long testVar3 = 3000;
        long testVar4 = 4000;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
              testVar1 /= 2;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar2 /= 2;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar3 /= 3;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar4 /= 4;
              additionalVar = ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public int testLoopRemInt() {
        int testVar1 = 1000;
        int testVar2 = 2000;
        int testVar3 = 3000;
        int testVar4 = 4000;
        long additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
              testVar1 %= 2;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar2 %= 2;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar3 %= 3;
              additionalVar = ((i)*2)%5;
        }
        for (int i = 0; i < iterations; i++) {
              testVar4 %= 4;
              additionalVar = ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public long testLoopRemLong() {
        long testVar1 = 1000;
        long testVar2 = 2000;
        long testVar3 = 3000;
        long testVar4 = 4000;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
              testVar1 %= 2;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar2 %= 2;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar3 %= 3;
              additionalVar = ((i)*2)%5;
        }
        for (long i = 0; i < iterations; i++) {
              testVar4 %= 4;
              additionalVar = ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
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

