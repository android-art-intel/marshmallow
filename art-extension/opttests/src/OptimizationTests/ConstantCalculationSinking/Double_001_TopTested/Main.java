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

package OptimizationTests.ConstantCalculationSinking.Double_001_TopTested;

/**
*
* Expected result: sinking occurs if number of iterations is less than 65
* or if we get to Infinity for Add/Sub/Mul or to 0 for Div/Rem
*
**/

public class Main {                                                                                                                                                   
    final int iterationsLessThanThreshold = 50;
    final int iterationsGreaterThanThreshold = 70;

    public double testLoopAddLess(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsLessThanThreshold; i++) {
              testVar += 3.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopAddGreater(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar += 3.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopAddInfinity(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar += Double.MAX_VALUE/3.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }


 public double testLoopSubLess(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsLessThanThreshold; i++) {
              testVar -= 3.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopSubGreater(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar -= 3.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopSubInfinity(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar -= Double.MAX_VALUE/3.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }


 public double testLoopMulLess(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsLessThanThreshold; i++) {
              testVar *= 1.1d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopMulGreater(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= 1.1d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopMulInfinity(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= Double.MAX_VALUE/3.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

   public double testLoopMulZero(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        double smallVar = 3.5d/Double.MAX_VALUE;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= smallVar;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }



 public double testLoopDivLess(double n) {
        double testVar = 10000000.0d;
        double additionalVar = 0;
        for (int i = -10; i < 10; i++) {
              testVar /= 3.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopDivGreater(double n) {
        double testVar = 1000000.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= 1.1d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopDivZero(double n) {
        double testVar = 1000.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= Double.MAX_VALUE;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopDivInfinity(double n) {
        double testVar = 1.0d;
        double additionalVar = 0;
        double smallVar = 10/Double.MAX_VALUE;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= smallVar;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }




    public double testLoopRemLess(double n) {
        double testVar = 100.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsLessThanThreshold; i++) {
              testVar %= 0.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopRemGreater(double n) {
        double testVar = 100.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar %= 0.5d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }

    public double testLoopRemZero(double n) {
        double testVar = 10.1d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar %= 10.1d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;
    }


    public double testLoopNaN(double n) {
        double testVar = 0.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar /= 0.0d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;

    }

    public double testLoopInfinity1(double n) {
        double testVar = 1.0d/0.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar += 1.0d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;

    }

    public double testLoopInfinity2(double n) {
        double testVar = 1.0d/0.0d;
        double additionalVar = 0;
        for (int i = -10; i < iterationsGreaterThanThreshold; i++) {
              testVar *= -11.0d/0d;
              additionalVar += (i*2)/5 + n;
        }
        testVar += additionalVar;
        return testVar;

    }



    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddLess(1.2d));
         System.out.println(new Main().testLoopAddGreater(1.2d));
         System.out.println(new Main().testLoopAddInfinity(1.2d));

         System.out.println(new Main().testLoopSubLess(1.2d));
         System.out.println(new Main().testLoopSubGreater(1.2d));
         System.out.println(new Main().testLoopSubInfinity(1.2d));


         System.out.println(new Main().testLoopMulLess(1.2d));
         System.out.println(new Main().testLoopMulGreater(1.2d));
         System.out.println(new Main().testLoopMulZero(1.2d));
         System.out.println(new Main().testLoopMulInfinity(1.2d));

         System.out.println(new Main().testLoopDivLess(1.2d));
         System.out.println(new Main().testLoopDivGreater(1.2d));
         System.out.println(new Main().testLoopDivZero(1.2d));
         System.out.println(new Main().testLoopDivInfinity(1.2d));

         System.out.println(new Main().testLoopRemLess(1.2d));
         System.out.println(new Main().testLoopRemGreater(1.2d));
         System.out.println(new Main().testLoopRemZero(1.2d));

         System.out.println(new Main().testLoopNaN(1.2d));
         System.out.println(new Main().testLoopInfinity1(1.2d));
         System.out.println(new Main().testLoopInfinity2(1.2d));

    }

}  

