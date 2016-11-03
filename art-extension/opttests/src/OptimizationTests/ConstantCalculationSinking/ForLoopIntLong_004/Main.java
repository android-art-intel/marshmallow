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

package OptimizationTests.ConstantCalculationSinking.ForLoopIntLong_004;


/**
*
* Handling multiple variables within one loop: should be supported
* Expected result: 4 sinkings for each test case
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar1 = 10;
        int testVar2 = 20;
        int testVar3 = 30;
        int testVar4 = 40;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
              testVar1 += 1;
              testVar2 += 2;
              testVar3 += 3;
              testVar4 += 4;
              additionalVar += ((i)*2)%5;
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
              testVar2 += 2;
              testVar3 += 3;
              testVar4 += 4;
              additionalVar += ((i)*2)%5;
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
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
              testVar1 -= 1;
              testVar2 -= 2;
              testVar3 -= 3;
              testVar4 -= 4;
              additionalVar += ((i)*2)%5;
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
              testVar2 -= 2;
              testVar3 -= 3;
              testVar4 -= 4;
              additionalVar += ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public int testLoopMulInt(int n) {
        int testVar1 = 6;
        int testVar2 = 5;
        int testVar3 = 3;
        int testVar4 = 4;
        int additionalVar = 0;
        for (int i = 0; i < 6; i++) {
              testVar1 *= 3;
              testVar2 *= 3;
              testVar3 *= 3;
              testVar4 *= 3;
              additionalVar += ((i)*2)%5 + i%2 + i%4 + i%5 + i%6 + i*(i-4) + i*(i+1) + i/3 + i/5 + n;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public long testLoopMulLong(long n) {
        long testVar1 = 6;
        long testVar2 = 5;
        long testVar3 = 3;
        long testVar4 = 4;
        long additionalVar = 0;
        for (long i = 0; i < 25; i++) {
              testVar1 *= 3;
              testVar2 *= 3;
              testVar3 *= 3;
              testVar4 *= 3;
              additionalVar += ((i)*2)%5 + n;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public int testLoopDivInt() {
        int testVar1 = 100;
        int testVar2 = 200;
        int testVar3 = 300;
        int testVar4 = 400;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
              testVar1 /= 2;
              testVar2 /= 2;
              testVar3 /= 3;
              testVar4 /= 4;
              additionalVar += ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public long testLoopDivLong() {
        long testVar1 = 100;
        long testVar2 = 200;
        long testVar3 = 300;
        long testVar4 = 400;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
              testVar1 /= 2;
              testVar2 /= 2;
              testVar3 /= 3;
              testVar4 /= 4;
              additionalVar += ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public int testLoopRemInt() {
        int testVar1 = 100;
        int testVar2 = 200;
        int testVar3 = 300;
        int testVar4 = 400;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
              testVar1 %= 2;
              testVar2 %= 2;
              testVar3 %= 3;
              testVar4 %= 4;
              additionalVar += ((i)*2)%5;
        }
        testVar1 += additionalVar;
        testVar2 += additionalVar;
        testVar3 += additionalVar;
        testVar4 += additionalVar;
        return testVar1 + testVar2 + testVar3 + testVar4;
    }

    public long testLoopRemLong() {
        long testVar1 = 100;
        long testVar2 = 200;
        long testVar3 = 300;
        long testVar4 = 400;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
              testVar1 %= 2;
              testVar2 %= 2;
              testVar3 %= 3;
              testVar4 %= 4;
              additionalVar += ((i)*2)%5;
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

