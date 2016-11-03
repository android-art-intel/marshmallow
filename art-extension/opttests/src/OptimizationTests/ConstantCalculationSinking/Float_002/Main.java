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

package OptimizationTests.ConstantCalculationSinking.Float_002;

/**
*
* Expected result: 
* in nested loops sinking occurs:
* 1. addition/subtraction: if result overflows
* 2. multiplication: if (overflows or result is 0) and (constant operand cst >= 0)
* 3. division : if (overflows or result is 0) and (constant operand cst > 0 )
* 4. reminder, always sink
*
**/

public class Main {                                                                                                                                                   
    final int iterationsLessThanThreshold = 50;
    final int iterationsGreaterThanThreshold = 70;

    public float testLoopAddLess(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
            for (int i = -10; i < iterationsLessThanThreshold; i++) {
                testVar += 3.5f;
                additionalVar += (i*2)/5 + n;
            }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopAddGreater(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
            for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
                testVar += 3.5f;
                additionalVar += (i*2)/5 + n;
            }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopAddInfinity(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
            for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
                testVar += Float.MAX_VALUE/3.5f;
                additionalVar += (i*2)/5 + n;
            }
        }
        testVar += additionalVar;
        return testVar;
    }


    public float testLoopSubLess(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        float f = -1.2345f;
        do {
            for (int i = -10; i < iterationsLessThanThreshold; i++) {
                testVar -= 3.5f;
                additionalVar += (i*2)/5 + n;
                f += 0.2134f;
            }
        } while (f < 50.3);
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopSubGreater(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;

        float f = -1.2345f;
        do {
            for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
                testVar -= 3.5f;
                additionalVar += (i*2)/5 + n;
                f += 0.2134f;
            }
        } while (f < 50.3);
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopSubInfinity(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        float f = -1.2345f;
        do {
            for (int i = -10; i < iterationsGreaterThanThreshold; i += 1) {
                testVar -= Float.MAX_VALUE/3.5f;
                additionalVar += (i*2)/5 + n;
                f += 0.2134f;
            }
        } while (f < 50.3);
        testVar += additionalVar;
        return testVar;
    }


 public float testLoopMulLess(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
            for (int i = -10; i < iterationsLessThanThreshold; i++) {
                testVar *= 1.1f;
                additionalVar += (i*2)/5 + n;
            }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopMulGreater(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= 1.1f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopMulInfinity(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= Float.MAX_VALUE/3.5f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopMulInfinityNeg(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= -Float.MAX_VALUE/3.5f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }



   public float testLoopMulZero(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        float smallVar = 3.5f/Float.MAX_VALUE;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= smallVar;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

   public float testLoopMulZeroNeg(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        float smallVar = 3.5f/Float.MAX_VALUE;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= -smallVar;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }




 public float testLoopDivLess(float n) {
        float testVar = 10000000.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < 10; i++) {
              testVar /= 3.5f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopDivGreater(float n) {
        float testVar = 1000000.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= 1.1f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopDivZero(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= 30.5f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopDivZeroNeg(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= -30.5f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopDivInfinity(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        float smallVar = 10/Float.MAX_VALUE;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= smallVar;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopDivInfinityNeg(float n) {
        float testVar = 1.0f;
        float additionalVar = 0;
        float smallVar = 10/Float.MAX_VALUE;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= -smallVar;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }




    public float testLoopRemLess(float n) {
        float testVar = 100.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsLessThanThreshold; i++) {
              testVar %= -0.5f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopRemGreater(float n) {
        float testVar = 100.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar %= 0.5f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }

    public float testLoopRemZero(float n) {
        float testVar = 10.1f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar %= 10.1f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }


    public float testLoopNaN(float n) {
        float testVar = 0.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= 0.0f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;

    }

    public float testLoopInfinity1(float n) {
        float testVar = 1.0f/0.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar += 1.0f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;

    }

    public float testLoopInfinity2(float n) {
        float testVar = 1.0f/0.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= -11.0f/0f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;

    }

    public float testLoopInfinity3(float n) {
        float testVar = -1.0f/0.0f;
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= 11.0f/0f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;

    }


 public float testLoopUnkownStart(float testVar, float n) {
        float additionalVar = 0;
        for (int j = 0; j < 1100; j++) {
        for (int i = -10; i < iterationsLessThanThreshold; i++) {
              testVar += 3.5f;
              additionalVar += (i*2)/5 + n;
        }
        }
        testVar += additionalVar;
        return testVar;
    }


    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddLess(1.2f));
         System.out.println(new Main().testLoopAddGreater(1.2f));
         System.out.println(new Main().testLoopAddInfinity(1.2f));

         System.out.println(new Main().testLoopSubLess(1.2f));
         System.out.println(new Main().testLoopSubGreater(1.2f));
         System.out.println(new Main().testLoopSubInfinity(1.2f));


         System.out.println(new Main().testLoopMulLess(1.2f));
         System.out.println(new Main().testLoopMulGreater(1.2f));
         System.out.println(new Main().testLoopMulZero(1.2f));
         System.out.println(new Main().testLoopMulZeroNeg(1.2f));
         System.out.println(new Main().testLoopMulInfinity(1.2f));
         System.out.println(new Main().testLoopMulInfinityNeg(1.2f));

         System.out.println(new Main().testLoopDivLess(1.2f));
         System.out.println(new Main().testLoopDivGreater(1.2f));
         System.out.println(new Main().testLoopDivZero(1.2f));
         System.out.println(new Main().testLoopDivZeroNeg(1.2f));
         System.out.println(new Main().testLoopDivInfinity(1.2f));
         System.out.println(new Main().testLoopDivInfinityNeg(1.2f));

         System.out.println(new Main().testLoopRemLess(1.2f));
         System.out.println(new Main().testLoopRemGreater(1.2f));
         System.out.println(new Main().testLoopRemZero(1.2f));

         System.out.println(new Main().testLoopNaN(1.2f));
         System.out.println(new Main().testLoopInfinity1(1.2f));
         System.out.println(new Main().testLoopInfinity2(1.2f));
         System.out.println(new Main().testLoopInfinity3(1.2f));
         
         System.out.println(new Main().testLoopUnkownStart(1.2f, 3.5f));

    }

}  

