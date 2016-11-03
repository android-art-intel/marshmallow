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

package OptimizationTests.ConstantCalculationSinking.OperationNotSupportedIntLong_002;

/**
  *
  * Expected result: no sinking, operation is not supported
  *
  **/

public class Main {                                                                                                                                                   
    final int iterations = 5;

    public int testLoopInt(int n) {
        int testVar = 2;
        int additionalVar = 0;
        
        for (int i = 0; i < iterations; i++) {
              testVar >>= 2;
              additionalVar += (i*2)%5 + i%3 + i%4 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public long testLoopLong(long n) {
        long testVar = 2;
        long additionalVar = 0;
        
        for (long i = 0; i < iterations; i++) {
              testVar >>= 2;
              additionalVar += (i*2)%5 + i%3 + i%4 + n;
        }
        testVar += additionalVar;
        return testVar;
    }


    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopInt(10));
         System.out.println(new Main().testLoopLong(10));
    }

}  

