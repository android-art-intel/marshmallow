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

package OptimizationTests.ConstantCalculationSinking.ForLoopIntLong_006_TopTested;

/**
*
* testLoop1: Handling sinking multiple variables with different operations and types
* Expected result: 4 sinkings for testLoop1
* testLoop2: for loop with no IV
* Expeted result: no sinking for testLoop2
* testLoop3: x = 2*x + 1 should not be supported
* Expeted result: no sinking for testLoop3
* testLoop4: v += v + x;
* Expeted result: no sinking for testLoop4
* testLoop5Int: '100>i' instead of 'i<100'
* Expected result: 1 sinking
* testLoop5Long: '100>i' instead of 'i<100'
* Expected result: 1 sinking
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public float  testLoop1() {
        int testVar1 = 10;
        int testVar2 = 20;
        long testVar3 = 30;
        float testVar4 = 40.123f;
        //int testVar3 = 30;
        //int testVar4 = 40;

        int additionalVar1 = 100;
        int additionalVar2 = 200;
        long additionalVar3 = 3L;
        float additionalVar4 = 4.12345f;
        for (int i = -10; i < iterations; i++) {
            int temp = testVar1++;
            temp = 1;
            additionalVar1 += (i*2)%5 + temp;
            testVar2 -= temp;
            additionalVar2 += temp + i;
            testVar3 /= additionalVar3; 
            testVar4 *= additionalVar4;
        }
        if (testVar4 == Float.POSITIVE_INFINITY) {
            testVar4 = -0.0f;
        }
        return testVar1 + testVar2 + testVar3 + testVar4+ additionalVar1 + additionalVar2 + additionalVar3 + additionalVar4;
    }

    public int testLoop2() {
        int testVar = 10;
        int additionalVar1 = 100;
        int additionalVar2 = 200;
        int additionalVar3 = 3;
        int additionalVar4 = 4;
        for ( ; testVar < iterations; ) {
            testVar *= 2;
            int temp = 1;
            additionalVar1 += (temp*2)%5 + temp;
            additionalVar2 = 2*additionalVar2 + additionalVar3*additionalVar4;
        }
        return testVar + additionalVar1 + additionalVar2 + additionalVar3 + additionalVar4;
    }

    public int testLoop3Int() {
        int testVar = 10;
        int additionalVar1 = 100;
        for (int i = 0; i < iterations; i++) {
            testVar = testVar*2 + 1;
            additionalVar1 += (i*2)%5 + i;
        }
        return testVar + additionalVar1;
    }

    public long testLoop3Long() {
        long testVar = 10;
        long additionalVar1 = 100;
        for (long i = 0; i < iterations; i++) {
            testVar = testVar*2 + 1;
            additionalVar1 += (i*2)%5 + i;
        }
        return testVar + additionalVar1;
    }

    public int testLoop4Int() {
        int testVar = 10;
        int additionalVar1 = 100;
        for (int i = 0; i < iterations; i++) {
            testVar += testVar + 5;
            additionalVar1 += (i*2)%5 + i;
        }
        return testVar + additionalVar1;
    }

    public long testLoop4Long() {
        long testVar = 10;
        long additionalVar1 = 100;
        for (long i = 0; i < iterations; i++) {
            testVar += testVar + 5;
            additionalVar1 += (i*2)%5 + i;
        }
        return testVar + additionalVar1;
    }

    public int testLoop5Int() {
        int testVar = 2001;
        int setToConstantVar = 2;
        for (int i = 0; iterations > i ; i++) {
            testVar += setToConstantVar;

        }
        return testVar;
    }

    public long testLoop5Long() {
        long testVar = 2001;
        long setToConstantVar = 3L;
        //long setToConstantVar = 3;
        for (long i = 0; iterations > i ; i++) {
            testVar += setToConstantVar;

        }
        return testVar;
    }


    public static void main(String[] args)
    {
        System.out.println(new Main().testLoop1());
        System.out.println(new Main().testLoop2());
         System.out.println(new Main().testLoop3Int());
         System.out.println(new Main().testLoop3Long());
         System.out.println(new Main().testLoop4Int());
         System.out.println(new Main().testLoop4Long());
         System.out.println(new Main().testLoop5Int());
         System.out.println(new Main().testLoop5Long());
    }

}  

