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

package OptimizationTests.ConstantCalculationSinking.NonTemporalMoveForLoopIntLong_001;

/**
  *
  * Checking the combination with NonTemporalMove optimization:
  * 
  * L: No sinking expected for Add/Sub: NonTemporalMove will not occur because of multiple BIV,
  * L: then aput is not marked as no_rangecheck, then accessing array element may lead to Exception,
  * L: so we except both ConstantCalculationSinking and NonTemporalMove to occur for Mul/Div/Rem only
  * M: no sinking for multiplication because of overflow
  *
  **/

public class Main {                                                                                                                                                   
    final int iterations = 133000; // threshold for NonTemporalMove passed

    public int testLoopAddInt() {
        int testVar = 100;
        int[] tab = new int[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar += 2;
        }
        return testVar + tab[10];
    }

    public int testLoopSubInt() {
        int testVar = 100;
        int[] tab = new int[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar -= 2;
        }
        return testVar + tab[10];
    }

    public long testLoopSubLong() {
        long testVar = 100;
        long[] tab = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar -= 2;
        }
        return testVar + tab[10];
    }

    public int testLoopMulInt() {
        int testVar = 100;
        int[] tab = new int[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar *= 3;
        }
        return testVar + tab[10];
    }

    public long testLoopMulLong() {
        long testVar = 100;
        long[] tab = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar *= 3;
        }
        return testVar + tab[10];
    }

    public int testLoopDivInt() {
        int testVar = 100;
        int[] tab = new int[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar /= 2;
        }
        return testVar + tab[10];
    }

    public long testLoopDivLong() {
        long testVar = 100;
        long[] tab = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar /= 2;
        }
        return testVar + tab[10];
    }

    public int testLoopRemInt() {
        int testVar = 100;
        int[] tab = new int[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar %= 2;
        }
        return testVar + tab[10];
    }

    public long testLoopRemLong() {
        long testVar = 100;
        long[] tab = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar %= 2;
        }
        return testVar + tab[10];
    }

    public long testLoopAddLong() {
        long testVar = 100;
        long[] tab = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            tab[i] = i;
            testVar += 2;
        }
        return testVar + tab[10];
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

