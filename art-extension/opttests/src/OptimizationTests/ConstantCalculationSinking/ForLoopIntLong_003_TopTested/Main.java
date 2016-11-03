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

package OptimizationTests.ConstantCalculationSinking.ForLoopIntLong_003_TopTested;


/**
*
* LE (less or equal) as loop condition should be supported
* Class instance/static field and local variables set to constant values in the right side of instruction <operation>= should be supported (treated as constant)
* Expected result: 1 sinking for each testcase
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;
    final int constInstanceField = -1;
    final static int constStaticField = -2;

    public int testLoopAddInt() {
        int testVar = 100;
        int setToConstantVar = 5;
        int additionalVar = 0;
        for (int i = 0;  iterations >= i; i++) {
              testVar += setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopSubInt() {
        int testVar = 100;
        int setToConstantVar = 5;
        int additionalVar = 0;
        for (int i = 0;  iterations >= i; i++) {
              testVar -= setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = 100;
        long setToConstantVar = 5;
        long additionalVar = 0;
        for (int i = 0;  iterations >= i; i++) {
              testVar -= setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int setToConstantVar = 6;
        int additionalVar = 0;
        for (int i = 0;  17 >= i; i++) {
              testVar *= setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5 + n + i%6 + i%4 + i%2 + i%3 + i%5 +i%7 + i%9;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long setToConstantVar = 6;
        long additionalVar = 0;
        for (int i = 0;  25 >= i; i++) {
              testVar *= setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5 + n;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopDivInt() {
        int testVar = 100;
        int setToConstantVar = 5;
        int additionalVar = 0;
        for (int i = 0;  iterations >= i; i++) {
              testVar /= setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = 100;
        long setToConstantVar = 5;
        long additionalVar = 0;
        for (int i = 0;  iterations >= i; i++) {
              testVar /= setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public int testLoopRemInt() {
        int testVar = 100;
        int setToConstantVar = 5;
        int additionalVar = 0;
        for (int i = 0;  iterations >= i; i++) {
              testVar %= setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopRemLong() {
        long testVar = 100;
        long setToConstantVar = 5;
        long additionalVar = 0;
        for (int i = 0;  iterations >= i; i++) {
              testVar %= setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public long testLoopAddLong() {
        long testVar = 100;
        long setToConstantVar = 5;
        long additionalVar = 0;
        for (int i = 0;  iterations >= i ; i++) {
              testVar += setToConstantVar + constInstanceField + constStaticField;
              additionalVar += (i*2)%5;
        }
        testVar = testVar + additionalVar;
        return testVar;
    }

    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddInt());
System.out.println(new Main().testLoopAddLong());
System.out.println(new Main().testLoopRemLong());
System.out.println(new Main().testLoopRemInt());
System.out.println(new Main().testLoopDivLong());
System.out.println(new Main().testLoopDivInt());
System.out.println(new Main().testLoopMulLong(10));
System.out.println(new Main().testLoopMulInt(10));
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

