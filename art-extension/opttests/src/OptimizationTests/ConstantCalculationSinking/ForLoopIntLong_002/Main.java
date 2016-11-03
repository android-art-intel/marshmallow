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

package OptimizationTests.ConstantCalculationSinking.ForLoopIntLong_002;


/**
*
* Variable set to a constant in the right side of instruction <operation>= should be supported (treated as constant)
* Starting loop IV with value other than 0 shouldn't prevent the optimization from being applied
*  Expected result: 1 sinking for each test case
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar = 100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = -10; i < iterations; i++) {
              testVar += setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public int testLoopSubInt() {
        int testVar = 100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = -10; i < iterations; i++) {
              testVar -= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar -= additionalVar;
        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = 100;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = -10; i < iterations; i++) {
              testVar -= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar -= additionalVar;
        return testVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar1 = 1;
        int additionalVar2 = 2;
        int additionalVar3 = 3;
        int additionalVar4 = 4;
        int additionalVar5 = 5;
        int setToConstantVar = 3;
        for (int i = -10; i < 2; i++) {
              testVar *= setToConstantVar;
              additionalVar1 += (i*2)%5;
              additionalVar2 += (i*4)%5;
              additionalVar3 += (i*6)%5;
              additionalVar4 += (i*8)%5;
              additionalVar5 += (i*2)%5 + n;
        }
        testVar += additionalVar1 + additionalVar2 + additionalVar3 + additionalVar4 + additionalVar5;
        return testVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar1 = 1;
        long additionalVar2 = 2;
        long additionalVar3 = 3;
        long additionalVar4 = 4;
        long additionalVar5 = 5;
        long setToConstantVar = 3;
        for (int i = -10; i < 2; i++) {
              testVar *= setToConstantVar;
              additionalVar1 += (i*2)%5;
              additionalVar2 += (i*4)%5;
              additionalVar3 += (i*6)%5;
              additionalVar4 += (i*8)%5;
              additionalVar5 += (i*2)%5 + n;
        }
        testVar += additionalVar1 + additionalVar2 + additionalVar3 + additionalVar4 + additionalVar5;
        return testVar;

    }

    public int testLoopDivInt() {
        int testVar = 100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = -10; i < iterations; i++) {
              testVar /= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = 100;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = -10; i < iterations; i++) {
              testVar /= setToConstantVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public int testLoopRemInt(int n) {
        int testVar = 2001;
        int additionalVar1 = 1;
        int additionalVar2 = 2;
        int additionalVar3 = 3;
        int additionalVar4 = 4;
        int additionalVar5 = 5;
        int setToConstantVar = 3;
        for (int i = -10; i < 0 ; i++) {
              testVar %= setToConstantVar;
              additionalVar1 += (i*2)%5 + n*i;
              additionalVar2 += (i*4)%5 + n*i;
              additionalVar3 += (i*6)%5 + n*i;
              additionalVar4 += (i*8)%5 + n*i;
              additionalVar5 += (i*2)%5 + n*i;

        }
        testVar += additionalVar1 + additionalVar2 + additionalVar3 + additionalVar4 + additionalVar5;
        return testVar;
    }

    public long testLoopRemLong(long n) {
        long testVar = 2001;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = -10; i < -1; i++) {
              testVar %= setToConstantVar;
              additionalVar += (i*2)%5 + i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + n*i;
        }
        testVar += additionalVar;
        return testVar;
    }

    public long testLoopAddLong() {
        long testVar = 100;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = -10; i < iterations; i++) {
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
System.out.println(new Main().testLoopRemLong(10));
System.out.println(new Main().testLoopRemInt(10));
System.out.println(new Main().testLoopDivLong());
System.out.println(new Main().testLoopDivInt());
System.out.println(new Main().testLoopMulLong(10));
System.out.println(new Main().testLoopMulInt(10));
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

