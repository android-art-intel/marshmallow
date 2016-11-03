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

package OptimizationTests.ConstantCalculationSinking.Division_001;

/**
*
*  Different testa with division to be sunk
*  M: sinking is currently disabled for non-finalized division
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    //not to zero
    public int testLoop1(int n) {
        int testVar = Integer.MAX_VALUE;
        int additionalVar = 0;

        for (int i = 0; i < 15; i++) {
            additionalVar += i/2 + n/2;
            testVar /= 3;
        }

        return testVar + additionalVar;
    }

    //goes to zero
    public int testLoop2(int n) {
        int testVar = 1024;
        int additionalVar = 0;

        for (int i = 0; i < 15; i++) {
            additionalVar += i/2 + n/2;
            testVar /= -3;
        }
        System.out.println("additionalVar = " + additionalVar);
        return testVar + additionalVar;
    }
    
    //not to zero
    public float testLoop3(int n) {
        float testVar = Float.MAX_VALUE;
        int additionalVar = 0;

        for (int i = 0; i < 30; i++) {
            additionalVar += i/2 + n/2;
            testVar /= 3.1f;
        }

        return testVar + additionalVar;
    }

    //Overflow to -Infinity
    public float testLoop4(int n) {
        float testVar = Float.MAX_VALUE - 1;
        int additionalVar = 0;

        for (int i = 0; i < 25; i++) {
            additionalVar += i/2 + n/2;
            testVar /= -Float.MIN_VALUE;
        }

        return testVar + additionalVar;
    }

     //goes to zero
    public float testLoop5(int n) {
        float testVar = Float.MIN_VALUE;
        int additionalVar = 0;

        for (int i = 0; i < 25; i++) {
            additionalVar += i/2 + n/2;
            testVar /= -Float.MAX_VALUE;
        }

        return testVar + additionalVar;
    }



    public static void main(String[] args)
    {
         System.out.println(new Main().testLoop1(10));
         System.out.println(new Main().testLoop2(10));
         System.out.println(new Main().testLoop3(10));
         System.out.println(new Main().testLoop4(10));
         System.out.println(new Main().testLoop5(10));
    }

}  

