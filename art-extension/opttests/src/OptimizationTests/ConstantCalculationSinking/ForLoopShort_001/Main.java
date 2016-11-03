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

package OptimizationTests.ConstantCalculationSinking.ForLoopShort_001;

/**
*
* Sinking occurs only if SinkCasts sinks int-to-short instruction
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 10;

    public short testLoopAdd(short n) {
        short testVar = 0;
        short additionalVar = 0;
        for (short i = -10; i < iterations; i++) {
              testVar += 3;
              additionalVar += (i*2)%5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public short testLoopSub(short n) {
        short testVar = 0;
        short additionalVar = 0;
        for (short i = -10; i < iterations; i++) {
              testVar -= 3;
              additionalVar += (i*2)%5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public short testLoopMul(short n) {
        short testVar = 1;
        short additionalVar = 0;
        for (short i = 5; i < iterations; i++) {
              testVar *= 3;
              additionalVar += (i*2)%5 + n + i%2 + n%3 + i%4 + n%5 + i%6 + n%7 + i%8 + n%9;
        }
        testVar += additionalVar;
        return testVar;
    }

    public short testLoopDiv(short n) {
        short testVar = 100;
        short additionalVar = 0;
        for (short i = -5; i < iterations; i++) {
              testVar /= 3;
              additionalVar += (i*2)%5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public short testLoopRem(short n) {
        short testVar = 100;
        short additionalVar = 0;
        for (short i = -10; i < iterations; i++) {
              testVar %= 3;
              additionalVar += (i*2)%5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }


    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAdd((short)10));
         System.out.println(new Main().testLoopSub((short)10));
         System.out.println(new Main().testLoopMul((short)10));
         System.out.println(new Main().testLoopDiv((short)10));
         System.out.println(new Main().testLoopRem((short)10));
    }

}  

