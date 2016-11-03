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

package OptimizationTests.ConstantCalculationSinking.VarUsedAgainForLoopIntLong_002;

/**
 *
 * No sinking expected because variable is used again in the loop
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar = 1000;
        int additionalVar = 0;
        int a = 2;
        
        for (int i = 0; i < iterations; i++) {
            a += testVar;
            testVar += 5;
            additionalVar += (i*2)%5;
        }

        return testVar + a + additionalVar ;
    }

    public int testLoopSubInt() {
        int testVar = 1000;
        int additionalVar = 0;
        int a = 2;
        
        for (int i = 0; i < iterations; i++) {
            a += testVar;
            testVar -= 5;
            additionalVar += (i*2)%5;
        }

        return testVar + a + additionalVar ;
    }

    public long testLoopSubLong() {
        long testVar = 1000;
        long additionalVar = 0;
        long a = 2;
        
        for (long i = 0; i < iterations; i++) {
            a += testVar;
            testVar -= 5;
            additionalVar += (i*2)%5;
        }

        return testVar + a + additionalVar ;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar = 0;
        int a = 2;
        
        for (int i = 0; i < 18; i++) {
            a += testVar;
            testVar *= 3;
            additionalVar += (i*2)%5 + n;
        }

        return testVar + a + additionalVar ;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar = 0;
        long a = 2;
        
        for (long i = 0; i < 18; i++) {
            a += testVar;
            testVar *= 3;
            additionalVar += (i*2)%5 + n;
        }

        return testVar + a + additionalVar ;
    }

    public int testLoopDivInt() {
        int testVar = 1000;
        int additionalVar = 0;
        int a = 2;
        
        for (int i = 0; i < iterations; i++) {
            a += testVar;
            testVar /= 5;
            additionalVar += (i*2)%5;
        }

        return testVar + a + additionalVar ;
    }

    public long testLoopDivLong() {
        long testVar = 1000;
        long additionalVar = 0;
        long a = 2;
        
        for (long i = 0; i < iterations; i++) {
            a += testVar;
            testVar /= 5;
            additionalVar += (i*2)%5;
        }

        return testVar + a + additionalVar ;
    }

    public int testLoopRemInt() {
        int testVar = 1000;
        int additionalVar = 0;
        int a = 2;
        
        for (int i = 0; i < iterations; i++) {
            a += testVar;
            testVar %= 5;
            additionalVar += (i*2)%5;
        }

        return testVar + a + additionalVar ;
    }

    public long testLoopRemLong() {
        long testVar = 1000;
        long additionalVar = 0;
        long a = 2;
        
        for (long i = 0; i < iterations; i++) {
            a += testVar;
            testVar %= 5;
            additionalVar += (i*2)%5;
        }

        return testVar + a + additionalVar ;
    }

    public long testLoopAddLong() {
        long testVar = 1000;
        long additionalVar = 0;
        long a = 2;
        
        for (long i = 0; i < iterations; i++) {
            a += testVar;
            testVar += 5;
            additionalVar += (i*2)%5;
        }

        return testVar + a + additionalVar ;
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

