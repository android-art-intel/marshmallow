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

package OptimizationTests.FormBottomLoops.WhileLoop_01;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * Simplest case when optimization should be applied: while loop
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
        int[] getArr() {
            return arr;
        }

        int[] modifyAndGetArr() {
            arr[0] = 100;
            return arr;
        }

    }

    public int foo(int x) {
        for (int i = 0; i < iterations; i++) {
            x = x + i/2;
        }
         return x;
    }

    public int sideEffectFunc(int x) {
        count++;
        return x + count;

    }

     public int tryDiv(int x, int y) {
        int res = 0;
        try {
            res = x/y;
        } catch (Exception e) {
            System.out.println("Exception when dividing " + x + " by " + y);
            res = x/(y+1);
        }
        return res;

    }

    /* Positive: simple test, loop is not affected by other optimization */
    public int test1(int n) {
        int testVar = 0;
        int i = 0; 

        while( i < iterations) {
            testVar += 5 + n/2 + i/2;
            i++;
        }

        return testVar;
    }


    /* Positive: iterator start value is unkown */
    public int test2(int n) {
        int testVar = 0;
        int k = n;
        while ( k < iterations) {
            testVar += 5 + n/2;
            k++;
        }

        return testVar + n + k;
    }


    /* Positive: loop with 2 iterations, peeling */
    public int test3(int n) {
        int testVar = 0;
        int[] arr =  new int[n];

        for (int i = 0; i < 2; i++) {
            testVar += 5 + n/2 + i/2;
            arr[i] = 5*i/n;
        }
        return testVar + n;
    }


    /* Positive: iterator overflows */
    public int test4(int n) {
        int testVar = 0;
        int additionalVar = 1;

        int i = 0;    
        while (i < iterations) {
            testVar += 5 -  n/2 + i/2;
             i += Integer.MAX_VALUE;
//            additionalVar += i - n;
        }
        return testVar + additionalVar;
    }


    /* Positive: IV is different than 1, lower bound is negative, <= */
    public int test5(int n) {
        int testVar = 0;
        MyClass c = new MyClass(100);

        int i = -100;   
        while ( i <= iterations) {
            testVar += 5 + n/2 + i/2 + c.arr[0];
             i = i + 3;
            
        }

        return testVar + n;
    }

    /* Positive: not a linear increment: no IV? */
    public int test6(int n) {
        int testVar = 0;
        MyClass c = new MyClass(iterations);

        int i = 1;
        while ( i < c.value) {
            testVar += 5 + n/2 + i/2;
            i = i*3;
        }

        return testVar + n;
    }


    /* Positive: IV is modified both in loop header and body loop; loop is executed only once */
    public int test7(int n) {
        int testVar = 0;

        int i = 0;
        while (i < n) {
            i += 1 + n/2 + (++i)/2;
            n = -1;
            i++;
        }

        return testVar + n;
    }


    /* Negative: The header of the loop must not create values that are used outside the loop (Result is used by) */
    public int test8(int n) {
        int testVar = 0;
        int i = 0;

        while (i++ < iterations) {
            testVar += 5 + n/2 + i/2;
        }

        return testVar + n;
    }


    /* Positive: loop body is empty: should probably be modified when DCE is implemented */
    public int test9(int n) {
        int testVar = 0;

        int i = 0;
        while (i < iterations){
            i++;
        }
        

        return testVar;
    }

    /* Negative: at most 2 HPhi instructions at the top of the loop (More than 2 loop phis) */
    public int test10(int n) {
        int testVar = 0;
        
        if (n > 0) {
            testVar = 1;
        }
        

        int i = 0;
        int k = 1;
        while ( i < iterations - (k*=2)) {
            testVar -=  n/2;
            i++;
        }

        return testVar + i + k;
    }

    /* Negative:  The header of the loop must not create values that are used outside the loop  */
    public int test11(int n) {
        int testVar = 0;
        int additionalVar = 1;

        int i = -1;
        while (i < iterations - additionalVar++) {
            testVar -= i/2 + n/2;
            i++;
        }

        return testVar + additionalVar;
    }

    /* Negative: at most 2 HPhi instructions at the top of the loop (More than 2 loop phis) */
    public int test12(int n) {
        int testVar = 0;

        int i = 0;
        while (i < iterations - testVar++) {
            testVar -= i/2 + n/2;
            n--;
            i++;

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

