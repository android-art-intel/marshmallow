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

package OptimizationTests.ConstantCalculationSinking.UnknownBoundsForLoopIntLong_002;

import java.util.Random;

/**
 *
 * Bounds unknown at compile time
 * No sinking expected
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 100;

    public int testLoopAddInt() {
        Random rand = new Random();  
        int n = rand.nextInt(40) + 1;       
        int testVar = 5;
        int additionalVar = 3;
        for (int i = 0; i < n; i++) {
            testVar += 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public int testLoopSubInt() {
        Random rand = new Random();  
        int n = rand.nextInt(40) + 1;       
        int testVar = 5;
        int additionalVar = 3;
        for (int i = 0; i < n; i++) {
            testVar -= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public long testLoopSubLong() {
        Random rand = new Random();  
        long n = rand.nextInt(40) + 1;       
        long testVar = 5;
        long additionalVar = 3;
        for (long i = 0; i < n; i++) {
            testVar -= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public int testLoopMulInt() {
        Random rand = new Random();  
        int n = rand.nextInt(40) + 1;       
        int testVar = 1;
        int additionalVar = 3;
        for (int i = 0; i < n/2; i++) {
            testVar *= 2;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public long testLoopMulLong() {
        Random rand = new Random();  
        long n = rand.nextInt(40) + 1;       
        long testVar = 1;
        long additionalVar = 3;
        for (long i = 0; i < n/2; i++) {
            testVar *= 2;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public int testLoopDivInt() {
        Random rand = new Random();  
        int n = rand.nextInt(40) + 1;       
        int testVar = 5;
        int additionalVar = 3;
        for (int i = 0; i < n; i++) {
            testVar /= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public long testLoopDivLong() {
        Random rand = new Random();  
        long n = rand.nextInt(40) + 1;       
        long testVar = 5;
        long additionalVar = 3;
        for (long i = 0; i < n; i++) {
            testVar /= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public int testLoopRemInt() {
        Random rand = new Random();  
        int n = rand.nextInt(40) + 1;       
        int testVar = 5;
        int additionalVar = 3;
        for (int i = 0; i < n; i++) {
            testVar %= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public long testLoopRemLong() {
        Random rand = new Random();  
        long n = rand.nextInt(40) + 1;       
        long testVar = 5;
        long additionalVar = 3;
        for (long i = 0; i < n; i++) {
            testVar %= 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
    }

    public long testLoopAddLong() {
        Random rand = new Random();  
        long n = rand.nextInt(40) + 1;       
        long testVar = 5;
        long additionalVar = 3;
        for (long i = 0; i < n; i++) {
            testVar += 5;
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6;
        }

        return testVar/Integer.MAX_VALUE;//always 0
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
    }

}  

