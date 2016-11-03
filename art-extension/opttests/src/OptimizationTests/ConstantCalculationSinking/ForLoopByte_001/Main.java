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

package OptimizationTests.ConstantCalculationSinking.ForLoopByte_001;

/**
*
* L: Sinking occurs only if SinkCasts sinks int-to-byte instruction
* M: No sinking expected since SinkCast optimization is not implemented now
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 10;

    public byte testLoopAdd(byte n) {
        byte testVar = 0;
        byte additionalVar = 0;
        for (byte i = -10; i < iterations; i++) {
              testVar += 3;
              additionalVar += (i*2)%5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public byte testLoopSub(byte n) {
        byte testVar = 0;
        byte additionalVar = 0;
        for (byte i = -10; i < iterations; i++) {
              testVar -= 3;
              additionalVar += (i*2)%5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public byte testLoopMul(byte n) {
        byte testVar = 1;
        byte additionalVar = 0;
        for (byte i = -10; i < -3; i++) {
              testVar *= 3;
              additionalVar += (i*2)%5 +i%2 + i%3 +i%4 + i%5 + + i%6 + i%7 + i%8 + i%9 + n;
        }
        testVar += additionalVar%10;
        return testVar;
    }

    public byte testLoopDiv(byte n) {
        byte testVar = 100;
        byte additionalVar = 0;
        for (byte i = -10; i < -3; i++) {
              testVar /= 2;
              additionalVar += (i*2)%5 +i%2 + i%3 +i%4 + i%5 + i%5 + + i%6 + i%7 + i%8 + i%9 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public byte testLoopRem(byte n) {
        byte testVar = 100;
        byte additionalVar = 0;
        for (byte i = -10; i < iterations; i++) {
              testVar %= 3;
              additionalVar += (i*2)%5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }


    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAdd((byte)-1));
         System.out.println(new Main().testLoopSub((byte)-1));
         System.out.println(new Main().testLoopMul((byte)-1));
         System.out.println(new Main().testLoopDiv((byte)-1));
         System.out.println(new Main().testLoopRem((byte)-1));
    }

}  

