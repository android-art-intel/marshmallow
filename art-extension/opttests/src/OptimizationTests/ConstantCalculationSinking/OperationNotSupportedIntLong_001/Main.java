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

package OptimizationTests.ConstantCalculationSinking.OperationNotSupportedIntLong_001;

/**
  *
  * Expected result: no sinking, case v = x - v, v = x/v is not supported
  *
  **/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopSubInt() {
        int testVar = 0;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = 0; i < iterations; i++) {
              testVar = setToConstantVar - testVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = 0;
        long additionalVar = 0;
        long setToConstantVar = 3;
        for (long i = 0; i < iterations; i++) {
              testVar = setToConstantVar - testVar;
              additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public int testLoopDivInt() {
        int testVar = 2;
        int additionalVar = 0;
        int setToConstantVar = 2000000;
        for (int i = 0; i < iterations; i++) {
            testVar = setToConstantVar/testVar;
            additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = 2;
        long additionalVar = 0;
        long setToConstantVar = 2000000;
        for (long i = 0; i < iterations; i++) {
            testVar = setToConstantVar/testVar;
            additionalVar += (i*2)%5;
        }
        testVar += additionalVar;
        return testVar;
    }

    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopSubInt());
         System.out.println(new Main().testLoopSubLong());
         System.out.println(new Main().testLoopDivInt());
         System.out.println(new Main().testLoopDivLong());
    }

}  

