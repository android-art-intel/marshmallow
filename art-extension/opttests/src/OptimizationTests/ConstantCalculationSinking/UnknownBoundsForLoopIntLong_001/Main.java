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

package OptimizationTests.ConstantCalculationSinking.UnknownBoundsForLoopIntLong_001;

import java.util.Random;

/**
  *
  * Bounds unknown at compile time
  * No sinking expected
  *
  **/

public class Main {                                                                                                                                                   
    final int iterations = 100;

    public int testLoopAddInt(int n) {
        int testVar = 11;
        int additionalVar = 1;
        for (int i = 0; i < n; i++) {
            testVar += 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/30000;//always 0
    }

    public int testLoopSubInt(int n) {
        int testVar = 11;
        int additionalVar = 1;
        for (int i = 0; i < n; i++) {
            testVar -= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/30000;//always 0
    }

    public long testLoopSubLong(long n) {
        long testVar = 11;
        long additionalVar = 1;
        for (long i = 0; i < n; i++) {
            testVar -= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/30000;//always 0
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar = 1;
        for (int i = 0; i < n/2; i++) {
            testVar *= 2;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/Integer.MAX_VALUE;//always 0
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar = 1;
        for (long i = 0; i < n/2; i++) {
            testVar *= 2;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/Integer.MAX_VALUE;//always 0
    }

    public int testLoopDivInt(int n) {
        int testVar = 11;
        int additionalVar = 1;
        for (int i = 0; i < n; i++) {
            testVar /= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/30000;//always 0
    }

    public long testLoopDivLong(long n) {
        long testVar = 11;
        long additionalVar = 1;
        for (long i = 0; i < n; i++) {
            testVar /= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/30000;//always 0
    }

    public int testLoopRemInt(int n) {
        int testVar = 11;
        int additionalVar = 1;
        for (int i = 0; i < n; i++) {
            testVar %= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/30000;//always 0
    }

    public long testLoopRemLong(long n) {
        long testVar = 11;
        long additionalVar = 1;
        for (long i = 0; i < n; i++) {
            testVar %= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/30000;//always 0
    }

    public long testLoopAddLong(long n) {
        long testVar = 11;
        long additionalVar = 1;
        for (long i = 0; i < n; i++) {
            testVar += 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return (testVar + additionalVar)/30000;//always 0
    }

    public static void main(String[] args)
    {
        Random rand = new Random();  
        int n = rand.nextInt(40) + 1;       
        System.out.println(new Main().testLoopAddInt(n));
System.out.println(new Main().testLoopAddLong(n));
System.out.println(new Main().testLoopRemLong(n));
System.out.println(new Main().testLoopRemInt(n));
System.out.println(new Main().testLoopDivLong(n));
System.out.println(new Main().testLoopDivInt(n));
System.out.println(new Main().testLoopMulLong(n));
System.out.println(new Main().testLoopMulInt(n));
System.out.println(new Main().testLoopSubLong(n));
System.out.println(new Main().testLoopSubInt(n));
    }

}  

