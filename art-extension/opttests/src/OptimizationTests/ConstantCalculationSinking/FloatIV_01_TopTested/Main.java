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

package OptimizationTests.ConstantCalculationSinking.FloatIV_01_TopTested;

/**
*
* Test with float point induction variable
* Expected result: sinking is expected if induction variable is presented as (N + M/2^K) in each iteration
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

  
    public int testLoopAddInt() {
        int testVar = 0xAAA;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1.2345f;
        double additionalVar3 = 6.7899d;
        int additionalVar4 = 0xB;
        for (float f = 0 ; f < iterations; f += 0.25f) {
            additionalVar1 = 2*additionalVar1/100 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
        }
        return testVar + (int)additionalVar1;
    }


    public int testLoopSubInt() {
        int testVar = 0xAAA;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        int additionalVar4 = 0xBBB;
        for (float f = 0 ; f < iterations; f += 0.26f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar -=  additionalVar4;
        }
        return testVar + (int)additionalVar1;
    }

    public long testLoopSubLong(int n) {
        long testVar = 0xAAA;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (float f = 0.000001f ; f < iterations ; f += 0.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
            testVar -=  additionalVar4;
        }
        return testVar + (long)additionalVar1;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1f;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (float f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar *=  additionalVar4;
        }
        return testVar + (int)additionalVar1;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1f;
        double additionalVar3 = 1d;
        long additionalVar4 = 3;
        for (float f = 0 ; f < iterations; f += 99.30f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar *=  additionalVar4;
        }
        return testVar + (long)additionalVar1;
    }

    public int testLoopDivInt() {
        int testVar = 0xAAA;
        long additionalVar1 = 0x10;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        int additionalVar4 = 0xBB;
        for (float f = 0 ; f < iterations; f += 0.75f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar /=  additionalVar4;
        }
        return testVar + (int)additionalVar1;
    }

    public long testLoopDivLong() {
        long testVar = 0xAAA;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (double d = 0 ; d < iterations; d += 0.1d) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)d/3;
            testVar /=  additionalVar4;
        }
        return testVar + (long)additionalVar1;
    }

    public int testLoopRemInt() {
        int testVar = 0xAAA;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        int additionalVar4 = 0xBB;
       for (float f = 0 ; f < iterations; f += 1.00390625f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar %=  additionalVar4;
        }
        return testVar + (int)additionalVar1;
    }

    public long testLoopRemLong() {
        long testVar = 0xAAA;
        long additionalVar1 = 5;
        float additionalVar2 = 123.2345f;
        double additionalVar3 = 678.89d;
        long additionalVar4 = 0xBBB;
        for (float f = 0 ; f < 1; f += 0.0000152587890625f) {
            additionalVar1 = additionalVar1/2 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar %=  additionalVar4;
        }
        return testVar + (long)additionalVar1;
    }

    public long testLoopAddLong() {
        long testVar = 0xAAA;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (double d = 0 ; d < iterations; d += 0.00390625d) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)d/3;
            testVar +=  additionalVar4;
        }
        return testVar + (long)additionalVar1;
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
System.out.println(new Main().testLoopSubLong(10));
System.out.println(new Main().testLoopSubInt());
    }

}  

