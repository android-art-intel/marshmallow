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

package OptimizationTests.FormBottomLoops.FloatPointIterator_01;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * Simplest cases with float-point type iterator
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 1100;

    int count = 0;

    class MyClass {
        int value = 0;
        int value1 = 1;
        int value2 = 2;
        int value3 = 3;
        int[] arr;
        public MyClass(int x) {
            value = x;
            value1 = x/2;
            value2 = x*3;
            value3 = x%4;
            arr = new int[x];
            for (int i = 0; i < x; i++) {
                arr[i] = i*x/2;
            }

        }
    }

    public double foo(double x) {
        for (double i = 0; i < iterations; i++) {
            x = x + i/2;
        }
        return x;
    }

    public float sideEffectFunc(float x) {
        count++;
        return x + count;

    }

    public double tryDiv(double x, double y) {
        long res = 0;
        try {
            res = (long)x/(long)y;
        } catch (Exception e) {
            System.out.println("Exception when dividing " + x + " by " + y);
            res = (long)x/2;
        }
        return res;

    }


    /* Positive: simple test, loop is not affected by other optimization */
    public double test1(int n) {
        float testVar = 0.11f;

        for (float i = 0.1f; i < iterations + 0.5f; i = i + 1.75f) {
            testVar += 5 + n/2 + i/2;
        }

        return (int)testVar;
    }


    /* Positive: iterator start value is unkown */
    public double test2(int n) {
        double testVar = 0;
        double k = n;
        for ( k = n/3; k < iterations/3; k = k*0.0000013456789e6) {
            testVar += 1.234e2 + n/2;
            k += 1/3;
        }

        return testVar + n + k;
    }


    /* Positive: loop with 2 iterations and possible peeling */
    public double test3(int n) {
        int testVar = 0;


        float i = 0.1f;
        while (i < 0.2f) { 
            i += 0.06;
            testVar += 5 + n/2 + i/n;

        }
        return testVar + n;
    }


    /* Positive: iterator overflows */
    public double test4(int n) {
        float testVar = 0;

        for (float i = 1.2345f; i < Float.MAX_VALUE; i *= Float.MAX_VALUE/3 + 0.12345f) {
            testVar += 5 -  n/2 + i/2;

        }
        return testVar;
    }


    /* Positive: iterator of double type, IV is sqrt(2), lower bound is negative, <= */
    public double test5(int n) {
        int testVar = 0;
        MyClass c = new MyClass(100);

        for (double i = -100.12; i <= iterations*1.23456789; i+= Math.sqrt(2)) {


            testVar += 5 + n/2 + (int)i/2 + c.arr[0];

        }

        return testVar + n;
    }

    /* Positive: float iterator type, IV is increased by integer */
    public double test6(int n) {
        int testVar = 0;
        MyClass c = new MyClass(iterations);

        for (float i = 1.5f; i < c.value; i = i + 3) {
            testVar += 5 + n/2 + (int)i/2;
        }

        return testVar + n;
    }


    /* Positive: float type iterator, upper loop boud = Float.MAX_VALUE, IV is modified both in loop header and body loop;  */
    public double test7(int n) {
        double testVar = 0;

        float i = 0.5f; 
        while (i < Float.MAX_VALUE ) {
            i++;
            i *= 1 + n/2 + (++i)/2;
            testVar += i;
            //          n = -1;
        }

        return testVar + n;
    }


    /* Negative: The header of the loop must not create values that are used outside the loop (Result is used by) */
    public double test8(int n) {
        int testVar = 0;
        double i = 0.5;

        for ( ; i++ < iterations; ) {
            testVar += 5 + n/2 + i/2;
        }

        return testVar + n;
    }


    /* Positive: loop body is empty: should probably be modified when DCE is implemented */
    public double test9(int n) {
        int testVar = 0;

        for (double i = 0.1; i < iterations; i += 1.23456789) {
        }


        return testVar;
    }

    /* Negative: at most 2 HPhi instructions at the top of the loop (More than 2 loop phis) */
    public double test10(int n) {
        double testVar = 0;

        if (n > 0) {
            testVar = 1;
        }


        double i = 0.3;
        double k = 1.4;
        for ( ; i < iterations - (k*=2.1) ; i += 1.5) {
            testVar -=  n/2;
        }

        return testVar + i + k;
    }

    /* Negative:  The header of the loop must not create values that are used outside the loop  */
    public double test11(int n) {
        double testVar = 0;
        int additionalVar = 1;

        for (float i = 0.1f; i < iterations - additionalVar++; i += 1.3f) {
            testVar -= i/2 + n/2;
        }

        return testVar + additionalVar;
    }

    /* Negative: at most 2 HPhi instructions at the top of the loop (More than 2 loop phis) */
    public double test12(int n) {
        double testVar = 0;

        float i = -0.999f; 
        while (i < iterations - testVar++) {
            i += 1.999f;
            testVar -= i/2 + n/2;
            n--;

        }

        return testVar;
    }




    public void runTests() {
        Class c = new Main().getClass();
        Method[] methods = c.getDeclaredMethods();
        Method tempMethod;
        for (int i = 1; i < methods.length; i++) {
            int j = i;
            while (j > 0 &&  methods[j-1].getName().compareTo(methods[j].getName()) > 0) {
                tempMethod = methods[j];
                methods[j] = methods[j-1];
                methods[j-1] = tempMethod;
                j = j - 1;
            }
        }

        Object[] arr = {null};
        for (Method m: methods){
            if (m.getName().startsWith("test")){
                try {
                    String names[] = c.getPackage().getName().split("\\.");
                    String testName = names[names.length-1];
                    System.out.println("Test "+testName+"; Subtest "+m.getName()+"; Result: "+ m.invoke(this, 10));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    public static void main(String[] args)
    {
        new Main().runTests();
    }


}  

