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

package OptimizationTests.ConstantCalculationSinking.Misc_001;

/**
*
* Different cases not covered by other tests
* See comments about expected behavior below
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    //1 sinking expected
    public int testLoop1(int testVar) {
        int additionalVar1 = 100;
        int additionalVar2 = 200;
        for (int i = 0; i < iterations; i++) {
            additionalVar1 += i % 2;
            testVar += 5;
            additionalVar2 += i % 4;
        }

        return testVar + additionalVar1 + additionalVar2;
    }

    //no sinking expected: can't calculate bound information because we increment loop counter twice
    public int testLoop2(int testVar) {
        int additionalVar1 = 100;
        int additionalVar2 = 200;
        for (int i = 0; i < iterations; i++) {
            additionalVar1 += i % 2;
            testVar += 5;
            additionalVar2 += ++i % 4;

        }

        return testVar + additionalVar1 + additionalVar2;
    }

    //no sinking expected: can't calculate bound information
    public double testLoop3(double testVar) {
        double additionalVar1 = 100d;
        double additionalVar2 = 200d;
        double d = 1d;
        while ( d < iterations) {
            additionalVar1 += d / 2;
            testVar /=1.001d;
            additionalVar2 += d / 4;
            d *= 2d;

        }

        return testVar + additionalVar1 + additionalVar2;
    }

    //5 sinkings expected
    public long testLoop4(long testVar) {
        long additionalVar1 = 100;
        long additionalVar2 = 200;
        int k = 0;
        for (int i = 0; i < iterations; i++) {
            for (int j = 0; j < iterations; j++) {
                additionalVar1 += j%3 + i%3;
                testVar +=3;
            }
            k = 0;
            while ( k < 50) {
                additionalVar1 += ++k % 2 + i*2;
                testVar += 5;
            }

            additionalVar2 += i % 4;
            int m = 50;
            while ( m < 150) {
                additionalVar1 += ++m % 2 + i/2;
                testVar += 5;
                
            }
            testVar++;
            float f = 1f;
            for ( ; f < 30 ; f += 0.25f) {
                additionalVar1 += (int)f*2 + i;
                testVar -= 3; 
            }
            int l = 150;
            do {
                testVar %= -31;
                l += 10;
                additionalVar1 += l % 2 + i/3 + (l - 1)/2 + l%3 + l*4 - l/2 + i%2 + i*3 - (i + 7)/2 + l%6 + l%7 + l%8 + l %9 - l%10 + l%11 -l%12 + l%13 - l%14 + l%15 + l%16;
            } while (l < 170);
            additionalVar1++;


        }


        return testVar + additionalVar1 + additionalVar2;
    }


    public static void main(String[] args)
    {
         System.out.println(new Main().testLoop1(10));
         System.out.println(new Main().testLoop2(10));
         System.out.println(new Main().testLoop3(10));
         System.out.println(new Main().testLoop4(10));
    }

}  

