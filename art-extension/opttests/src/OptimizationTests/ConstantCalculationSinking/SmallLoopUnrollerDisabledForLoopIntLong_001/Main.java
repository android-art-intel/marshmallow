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

package OptimizationTests.ConstantCalculationSinking.SmallLoopUnrollerDisabledForLoopIntLong_001;

/**
  *
  * 10 sinking expected: LoopFullUnroller disabled
  * M: no LoopFullUnroller so far, test should be updated if it is implemented
  *
  **/

public class Main {                                                                                                                                                   

   public int testLoop0(int n) {
        int testVar = 1;
        int iterations = 0;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar += 5; 
        }
        return testVar + n;
    }



   public int testLoop1(int n) {
        int testVar = 1;
        int iterations = 1;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar -= 5;
        }
        return testVar + n;
    }

   public int testLoop2(int n) {
        int testVar = 1;
        int iterations = 2;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar *= 5;
        }
        return testVar + n;
    }

   public int testLoop3(int n) {
        int testVar = 1;
        int iterations = 3;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar /= 5;
        }
        return testVar + n;
    }



   public int testLoop4(int n) {
        int testVar = 6;
        int iterations = 4;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar %=5 ;
        }
        return testVar + n;
    }

   public long testLoop5(int n) {
        long testVar = 1;
        int iterations = 5;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar += 5;
        }
        return testVar + n;
    }

   public long testLoop6(int n) {
        long testVar = 1;
        int iterations = 6;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar -= 5;
        }
        return testVar + n;
    }

   public long testLoop7(int n) {
        long testVar = 1;
        int iterations = 7;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar *= 5;
        }
        return testVar + n;
    }

   public long testLoop8(int n) {
        long testVar = 1;
        int iterations = 8;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar /= 5;
        }
        return testVar + n;
    }

   public long testLoop9(int n) {
        long testVar = 0;
        int iterations = 9;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar %= 5;
        }
        return testVar + n;
    }

   public int testLoop10(int n) {
        int testVar = 0;
        int iterations = 10;
        for (int i = 0; i < iterations; i++) { n += i%2 + 1; 
            testVar += 5;
        }
        return testVar + n;
    }




    public static void main(String[] args)
    {
        Main m = new Main();
        System.out.println(m.testLoop0(10) + m.testLoop1(10) + m.testLoop2(10) + m.testLoop3(10) + m.testLoop4(10) + m.testLoop5(10) + m.testLoop6(10) + m.testLoop7(10) + m.testLoop8(10) + m.testLoop9(10) + m.testLoop10(10));
    }

}  

