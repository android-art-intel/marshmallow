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

package OptimizationTests.ConstantCalculationSinking.Switch_001;

/**
 *
 * Branching is not allowed: switch breaks optimization's limitations
 * Expected result: no sinking because of if-else statement in a loop
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoop1() {
        int testVar = 100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = -10; i < iterations; i++) {
              additionalVar += (i*2)%5;
          switch (additionalVar) {
              case 0: testVar += setToConstantVar;
            		  additionalVar++;
                      break;
              case 10: testVar -= setToConstantVar;
            		  additionalVar++;
                      break;
              case 15: testVar *= setToConstantVar;
            		  additionalVar++;
                      break;
              case 100: 
            		  additionalVar = --testVar;
                      break;

	      }
        }
        return testVar + additionalVar;
    }

    public int testLoop2() {
        int testVar = 100;
        int additionalVar = 0;
        int setToConstantVar = 3;
        for (int i = -10; i < iterations; i++) {
              additionalVar += (i*2)%5;
          switch (additionalVar) {
              case 0:
            		  additionalVar += i*2;
                      break;
              case 10: 
            		  additionalVar += i/2;
                      break;
              case 15: 
            		  additionalVar++;
                      break;
              case 100: 
            		  additionalVar = --testVar;
                      break;
              default:
            		  additionalVar += i;
                      break;

	      }
        }
        return testVar + additionalVar;
    }

    public float testLoop3() {
        float testVar = Float.POSITIVE_INFINITY;
        float additionalVar = 1.5f;
        float setToConstantVar = 3.1f;
        for (int i = -10; i < iterations; i++) {
              additionalVar += (i*2)%5;
          switch (i) {
              case 0:
            		  additionalVar += i*2;
                      break;
              case 10: 
            		  additionalVar += i/2;
                      break;
              case 15: 
            		  additionalVar++;
                      break;
              case 100: 
            		  additionalVar = --testVar;
                      break;
              case 200: 
            		  additionalVar += i/2;
                      testVar *= -1;
                      break;
              default:
            		  additionalVar += i;
                      break;

	      }
        }
        return testVar + additionalVar;
    }

    public float testLoop4(int n) {
        float testVar = -10.0625f;
        float additionalVar = 1.5f;
        float setToConstantVar = 3.1f;
        for (int i = -10; i < 71; i = i + 1) {
            additionalVar += (i*2)%5 + n%4;
            for (int k = 0; k < 53 ; k++) {
                additionalVar += n%2 + k%3;
                testVar *= -12.25f;
            }
            switch ((int)i) {
                case 0:
                    additionalVar += i*2;
                    break;
                case 10: 
                    additionalVar += i/2;
                    break;
                case 15: 
                    additionalVar++;
                      break;
              case 100: 
                      testVar++;
                      break;
              case 50: 
            		  additionalVar += i/2;
                      testVar *= -1;
                      break;
              default:
            		  additionalVar += i;
                      break;

	      }
        }
        return testVar + additionalVar;
    }

   public long testLoop5() {
        long testVar = 100L;
        long additionalVar = 0L;
        long setToConstantVar = 3L;
        for (int i = -10; i < iterations; i++) {
              additionalVar += (i*2)%5;
          switch (i) {
              case 0:
            		  additionalVar++;
                      break;
              case 10: 
            		  additionalVar++;
            		  additionalVar += i%2;
                      break;
              case 15: 
            		  additionalVar++;
            		  additionalVar -= i%3;

                      break;
              case 100: 
            		  additionalVar += i%5;
                      break;
              default:
                      break;

	      }
          testVar +=setToConstantVar;

        }
        return testVar + additionalVar;
    }


    public static void main(String[] args)
    {
         System.out.println(new Main().testLoop1());
         System.out.println(new Main().testLoop2());
         System.out.println(new Main().testLoop3());
         System.out.println(new Main().testLoop4(10));
         System.out.println(new Main().testLoop5());

    }

}  

