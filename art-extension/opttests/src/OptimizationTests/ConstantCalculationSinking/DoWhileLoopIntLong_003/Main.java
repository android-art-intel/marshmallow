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

package OptimizationTests.ConstantCalculationSinking.DoWhileLoopIntLong_003;

/**
*
* Count-down do-while loop
* 1 sinking expected
* L: fails due to CAR-876
* M: count-down loops are not enabled now
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt() {
        int testVar = 100;
        int additionalVar = 1;
        int setToConstantVar = -10;

        int i = iterations - 1;
        do {
            testVar += setToConstantVar;
            additionalVar += i/3;
	        i = i - 1;
        } while (i > -1);
        
        testVar = testVar + additionalVar;

        return testVar;
    }

    public int testLoopSubInt() {
        int testVar = 100;
        double additionalVar = 0.0d;
        int setToConstantVar = -10;

        int i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar -= setToConstantVar;
	        i = i - 10;
        } while (i > -1);
        
        testVar = testVar + (int)Math.round(additionalVar);

        return testVar;
    }

    public long testLoopSubLong() {
        long testVar = 100;
        double additionalVar = 0.0d;
        long setToConstantVar = -10;

        long i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar -= setToConstantVar;
	        i = i - 10;
        } while (i > -1);
        
        testVar = testVar + (long)Math.round(additionalVar);

        return testVar;
    }

    public int testLoopMulInt() {
        int testVar = 1;
        double additionalVar = 0.0d;
        int setToConstantVar = -3;

        int i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar *= setToConstantVar;
	        i = i - 5;
        } while (i > -1);
        
        testVar = testVar + (int)Math.round(additionalVar);

        return testVar;
    }

    public long testLoopMulLong() {
        long testVar = 1;
        double additionalVar = 0.0d;
        long setToConstantVar = -3;

        long i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar *= setToConstantVar;
	        i = i - 5;
        } while (i > -1);
        
        testVar = testVar + (long)Math.round(additionalVar);

        return testVar;
    }

    public int testLoopDivInt() {
        int testVar = 100;
        double additionalVar = 0.0d;
        int setToConstantVar = -10;

        int i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar /= setToConstantVar;
	        i = i - 10;
        } while (i > -1);
        
        testVar = testVar + (int)Math.round(additionalVar);

        return testVar;
    }

    public long testLoopDivLong() {
        long testVar = 100;
        double additionalVar = 0.0d;
        long setToConstantVar = -10;

        long i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar /= setToConstantVar;
	        i = i - 10;
        } while (i > -1);
        
        testVar = testVar + (long)Math.round(additionalVar);

        return testVar;
    }

    public int testLoopRemInt() {
        int testVar = 100;
        double additionalVar = 0.0d;
        int setToConstantVar = -10;

        int i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar %= setToConstantVar;
	        i = i - 10;
        } while (i > -1);
        
        testVar = testVar + (int)Math.round(additionalVar);

        return testVar;
    }

    public long testLoopRemLong() {
        long testVar = 100;
        double additionalVar = 0.0d;
        long setToConstantVar = -10;

        long i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar %= setToConstantVar;
	        i = i - 10;
        } while (i > -1);
        
        testVar = testVar + (long)Math.round(additionalVar);

        return testVar;
    }

    public long testLoopAddLong() {
        long testVar = 100;
        double additionalVar = 0.0d;
        long setToConstantVar = -10;

        long i = iterations - 1;
        do {
            additionalVar += i/3.14;
            testVar += setToConstantVar;
	        i = i - 10;
        } while (i > -1);
        
        testVar = testVar + (long)Math.round(additionalVar);

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
System.out.println(new Main().testLoopMulLong());
System.out.println(new Main().testLoopMulInt());
System.out.println(new Main().testLoopSubLong());
System.out.println(new Main().testLoopSubInt());
    }

}  

