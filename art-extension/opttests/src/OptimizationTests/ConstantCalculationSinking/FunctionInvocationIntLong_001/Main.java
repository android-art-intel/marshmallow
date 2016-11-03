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

package OptimizationTests.ConstantCalculationSinking.FunctionInvocationIntLong_001;

import java.util.Random;

/**
*
* L: Function invocation (if not inlined) breaks one basic block limitation of
* L: Constant Calculation Sinking optimization
* L: Expected result: no sinking expected
* M: No limitations on basic blocks number, function invocation allowed
* M: Expected result: 1 sinking for each method (if var to be sunk is not passed to function as parameter)
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 100;

    public int foo(long n) {
        int s = 0;
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            s += rand.nextInt(10);

        }
        return s;
    }

    public int testLoopAddInt() {
        int testVar = 10000;
        int additionalVar = 0;

        for (int i = 0; i < iterations; i++) {
            testVar += 5;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public int testLoopSubInt() {
        int testVar = 10000;
        int additionalVar = 0;

        for (int i = 0; i < iterations; i++) {
            testVar -= 5;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public long testLoopSubLong() {
        long testVar = 10000;
        long additionalVar = 0;

        for (long i = 0; i < iterations; i++) {
            testVar -= 5;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public int testLoopMulInt() {
        int testVar = 1;
        int additionalVar = 0;

        for (int i = 0; i < 18; i++) {
            testVar *= 3;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public long testLoopMulLong() {
        long testVar = 1;
        long additionalVar = 0;

        for (long i = 0; i < 18; i++) {
            testVar *= 3;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public int testLoopDivInt() {
        int testVar = 100000;
        int additionalVar = 0;

        for (int i = 0; i < 25; i++) {
            testVar /= 2;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public long testLoopDivLong() {
        long testVar = 100000;
        long additionalVar = 0;

        for (long i = 0; i < 25; i++) {
            testVar /= 2;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public int testLoopRemInt() {
        int testVar = 10000;
        int additionalVar = 0;

        for (int i = 0; i < iterations; i++) {
            testVar %= 5;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public long testLoopRemLong() {
        long testVar = 10000;
        long additionalVar = 0;

        for (long i = 0; i < iterations; i++) {
            testVar %= 5;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }

    public long testLoopAddLong() {
        long testVar = 10000;
        long additionalVar = 0;

        for (long i = 0; i < iterations; i++) {
            testVar += 5;
            additionalVar += foo(i);
        }

        return testVar + additionalVar/(iterations*iterations*10*2) ;
    }


    private static int largest(int... numbers) {
        int currentLargest = numbers[0];
        for (int number : numbers) {
            if (number > currentLargest) {
                currentLargest = number;
            }
        }
        return currentLargest;
    }


    public int testLoopVarargs() {
        int testVar = 100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = -10; i < iterations; i++) {
            testVar += 5;
            additionalVar += (i*2)%5 + largest(0, i, 1, i, additionalVar, testVar);
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
System.out.println(new Main().testLoopMulLong());
System.out.println(new Main().testLoopMulInt());
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
System.out.println(new Main().testLoopVarargs());
    }

}  

