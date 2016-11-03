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

package OptimizationTests.ConstantCalculationSinking.DoWhileLoopIntLong_002;

/**
*
* Minor variation of simple do-while loop:
* Iterator is not 1
* It's not so clear if optimization should be applied if the expression is not in the form v += x but v = v + x or v = x + v + y;
* No sinking expected for expression that doesn't follow accumulation style
* 1 sinking expected for correct expressions
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 11000;

    public int testLoopAddIntNonAccumulationStyleExpr(int n) {
        int testVar = 0x2000;
        int additionalVar = 0;
        int setToConstantVar = -10;

        int i = 0;
        do {
            additionalVar += i/3 + n;
            testVar = testVar - (setToConstantVar-11)*3 + 5;
    	    i = i + 10;
        } while (i < iterations);
        
        testVar += testVar + additionalVar;

        return testVar;
    }

    public int testLoopAddInt() {
        int testVar = 0x2000;
        double additionalVar = 0.0d;
        int setToConstantVar = -10;

        int i = 0;
        do {
            additionalVar += i/3.14;
            testVar = testVar + ((setToConstantVar-11)*3 - 5);
    	    i = i + 10;
        } while (i < iterations);
        
        testVar = testVar + (int)Math.round(additionalVar);

        return testVar;
    }


    public int testLoopSubInt() {
        int testVar = 0x2000;
        double additionalVar = 0.0d;
        int setToConstantVar = -10;

        int i = 0;
        do {
            additionalVar += i/3.14;
            testVar = testVar - ((setToConstantVar-11)*3 + 5);
    	    i = i + 10;
        } while (i < iterations);
        
        testVar = testVar + (int)Math.round(additionalVar);

        return testVar;
    }


    public long testLoopSubLong() {
        long testVar = 0x2000;
        double additionalVar = 0.0d;
        long setToConstantVar = -10;

        long i = 0;
        do {
            additionalVar += i/3.14;
            testVar = testVar - ((setToConstantVar-11)*3 + 5);
    	    i = i + 10;
        } while (i < iterations);
        
        testVar = testVar + Math.round(additionalVar);

        return testVar;
    }


    public int testLoopMulInt(int n) {
        int testVar = 1;
        double additionalVar = 0.0d;
        int setToConstantVar = -10;

        int i = 0;
        do {
            additionalVar += i/3.14 + n + i%2 + i%3 + i%4 + i%5 +i%6 + i%7 + i%8 +i%9 + i%10;
            testVar = testVar*((setToConstantVar + 11)*3);
    	    i = i + 10;
        } while (i < 18);
        
        testVar = testVar + (int)Math.round(additionalVar);

        return testVar;
    }


    public long testLoopMulLong(long n) {
        long testVar = 1;
        double additionalVar = 0.0d;
        long setToConstantVar = -10;

        long i = 0;
        do {
            additionalVar += i/3.14 + n + i%2 + i%3 + i%4 + i%5 +i%6 + i%7 + i%8 +i%9 + i%10;
            testVar = testVar*((setToConstantVar+11)*3L);
    	    i = i + 10;
        } while (i < 18);
        
        testVar = testVar + Math.round(additionalVar);

        return testVar;
    }


    public int testLoopDivInt() {
        int testVar = 0x2000;
        double additionalVar = 0.0d;
        int setToConstantVar = -10;

        int i = 0;
        do {
            additionalVar += i/3.14;
            testVar = testVar/((setToConstantVar-11)/3);
    	    i = i + 10;
        } while (i < iterations);
        
        testVar = testVar + (int)Math.round(additionalVar);

        return testVar;
    }


    public long testLoopDivLong() {
        long testVar = 0x2000;
        double additionalVar = 0.0d;
        long setToConstantVar = -10;

        long i = 0;
        do {
            additionalVar += i/3.14;
            testVar = testVar/((setToConstantVar-11)/3);
    	    i = i + 10;
        } while (i < iterations);
        
        testVar = testVar + Math.round(additionalVar);

        return testVar;
    }


    public int testLoopRemInt(int n) {
        int testVar = 0x2000;
        double additionalVar = 0.0d;
        int setToConstantVar = -12;

        int i = 0;
        do {
            additionalVar += i/3.14 + n;
            testVar = testVar%((setToConstantVar-11)%5);
    	    i = i + 10;
        } while (i < iterations);
        
        testVar = testVar +(int) Math.round(additionalVar);

        return testVar;
    }


    public long testLoopRemLong(long n) {
        long testVar = 0x2000;
        double additionalVar = 0.0d;
        long setToConstantVar = -12;

        long i = 0;
        do {
            additionalVar += i/3.14 + n;
            testVar = testVar%((long)(setToConstantVar-11)%5L);
    	    i = i + 10;
        } while (i < iterations);
        
        testVar = testVar + Math.round(additionalVar);

        return testVar;
    }


    public long testLoopAddLong() {
        long testVar = 0x2000;
        double additionalVar = 0.0d;
        long setToConstantVar = -10;

        long i = 0;
        do {
            additionalVar += i/3.14;
            testVar = testVar - ((setToConstantVar-11)*3 - 5);
    	    i = i + 10;
        } while (i < iterations);
        
        testVar = testVar + Math.round(additionalVar);

        return testVar;
    }


    public static void main(String[] args)
    {
System.out.println(new Main().testLoopAddIntNonAccumulationStyleExpr(10));
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

