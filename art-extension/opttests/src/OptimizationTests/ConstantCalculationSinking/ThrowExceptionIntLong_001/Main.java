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

package OptimizationTests.ConstantCalculationSinking.ThrowExceptionIntLong_001;

/**
*
* No sinking expected: loop shouldn't throw exception
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    public int testLoopAddInt(int n) {
        int testVar = 10;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
            testVar += 5;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                System.out.println("Exception " + ae);
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public int testLoopSubInt(int n) {
        int testVar = 10;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
            testVar -= 5;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public long testLoopSubLong(long n) {
        long testVar = 10;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
            testVar -= 5;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public int testLoopMulInt(int n) {
        int testVar = 1;
        int additionalVar = 0;
        for (int i = 0; i < 18; i++) {
            testVar *= 3;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public long testLoopMulLong(long n) {
        long testVar = 1;
        long additionalVar = 0;
        for (long i = 0; i < 18; i++) {
            testVar *= 3;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public int testLoopDivInt(int n) {
        int testVar = 10;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
            testVar /= 5;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public long testLoopDivLong(long n) {
        long testVar = 10;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
            testVar /= 5;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public int testLoopRemInt(int n) {
        int testVar = 10;
        int additionalVar = 0;
        for (int i = 0; i < iterations; i++) {
            testVar %= 5;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public long testLoopRemLong(long n) {
        long testVar = 10;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
            testVar %= 5;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public long testLoopAddLong(long n) {
        long testVar = 10;
        long additionalVar = 0;
        for (long i = 0; i < iterations; i++) {
            testVar += 5;
            try {
               throw new java.lang.ArithmeticException();
            } catch (java.lang.ArithmeticException ae) {
//                ae.printStackTrace();
            }
            additionalVar += i%2 + i%3 + i%4 + i%5 + i%6 + i%7 + i%8 + i%9 + i%10 + n;
        }

        return testVar + additionalVar;
    }

    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddInt(10));
System.out.println(new Main().testLoopAddLong(10L));
System.out.println(new Main().testLoopRemLong(10L));
System.out.println(new Main().testLoopRemInt(10));
System.out.println(new Main().testLoopDivLong(10L));
System.out.println(new Main().testLoopDivInt(10));
System.out.println(new Main().testLoopMulLong(10L));
System.out.println(new Main().testLoopMulInt(10));
System.out.println(new Main().testLoopSubLong(10L));
System.out.println(new Main().testLoopSubInt(10));

    }

}  

