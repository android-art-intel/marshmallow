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

package OptimizationTests.ConstantCalculationSinking.VarUsedAgainForLoopIntLong_005;

/**
 *
 * No sinking expected because variable is used again in the loop
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopInt() {
        int testVar = 387;

        for (int i = 0; i < iterations; i++) {
            testVar += 118;
            testVar /= 31;
            testVar *= 30;
            testVar -= -10;
//            testVar %= 2;
        }

        return testVar;
    }

    public long testLoopLong() {
        long testVar = 387;

        for (int i = 0; i < iterations; i++) {
            testVar += 118;
            testVar /= 31;
            testVar *= 30;
            testVar -= -10;
//            testVar %= 2;
        }

        return testVar;
    }

 
    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopInt());
         System.out.println(new Main().testLoopLong());
    }

}  

