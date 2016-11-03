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

package OptimizationTests.FormBottomLoops.BottomTested_01;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * Simplest cases for loops that are already bottom tested
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

        int[] zeroAndReturn(int x) {
            arr[x] = 0;
            return arr;
        }


    }

    public int foo(int x) {
        for (int i = 0; i < iterations; i++) {
            x = x + i/2;
        }
         return x;
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

    public int sideEffectFunc(int x) {
        count++;
        return x + count;

    }



    /* Positive: Complex Loop is already bottom tested (after gotos)  */
    public int test1(int n) {
        int testVar = 0;
        int i = 0;
        do {
            testVar += 5 + n/2 + i/2;
            i++;
        } while (i < iterations);

        return testVar;
    }


    /* Positive: Complex Loop is already bottom tested (after gotos)  */
    public int test2(int n) {
        int testVar = 0;
        int k = n;
        for ( ; ; ) {
            testVar += 5 + n/2 + k;
            k++;
            if (k > iterations) {
                break;
            }
        }

        return testVar + n + k;
    }


    /* Positive: Complex Loop is already bottom tested (after gotos)  */
    public int test3(int n) {
        int testVar = 0;
        int i = -1;
        while (true) {
            testVar += 5 + n/2 + i/2;
            if (i > iterations || testVar > iterations) {
                break;
            }
        }
        return testVar + n;
    }


    /* Negative: loop shouldn't be marked as bottom tested because of multiple exits  */
    public int test4(int n) {
        int testVar = 0;
        int additionalVar = 1;

        int i = 0; 
        do {
            testVar += 5 +  n/2 + i/2;

            if (n < 20) break;
            i += Integer.MAX_VALUE - 5;
        } while (testVar < iterations);

        return testVar + additionalVar;
    }


    /* Positive: inner bottom tested loop, same iterator in inner and outer loop, lower bound is negative, <= */
    public int test5(int n) {
        int testVar = 0;
        MyClass c = new MyClass(100);

        for (int i = -c.arr.length ; i <= iterations; i = i + 3) {
            do {
                testVar += 5 + n/2 + i/2 + c.arr[0];
                i++;
            } while (i < c.value * 10);
            
        }

        return testVar + n;
    }

    /* Positive: also should be bottom tested */
    public int test6(int n) {
        int testVar = 0;
        MyClass c = new MyClass(iterations);

        for (int i = 1; ; ) {
            testVar += 5 + n/2 + i/2;
            i++;
            if (i >= foo(c.value)) break;
            
        }

        return testVar + n;
    }


    /* Positive: bottom tested count-down loop with float iterator */
    public int test7(int n) {
        int testVar = 0;

        for (float f = 1234.56789f; ; ) {
            f /=123.456789f;
            testVar += f;
            if (f < n ) break;
        }
        return testVar + n;
    }


    /* Negative: The header of the loop must not create values that are used outside the loop (Result is used by) */
    public int test8(int n) {
        int testVar = 0;
        int i = 0;

        for ( ; ; i++) {
            testVar += 5 + n/2 + i/2;
            if (i > iterations) break;
        }

        return testVar + n;
    }


    /* Positive: loop body is empty: should probably be modified when DCE is implemented */
    public int test9(int n) {
        int testVar = 0;

        do {
        } while (testVar++ < iterations);
        
        

        return testVar;
    }

    /* Positive: 2 HPhi instructions at the top of the loop are ok for already bottom tested loop */
    public int test10(int n) {
        int testVar = 0;
        int additionalVar = 1;
        
        if (n > 0) {
            testVar = 1;
        }
        

        int i = 0;
        int k = 1;
        do {
            i++;
            testVar -=  n/2;
            k*=2;
            additionalVar += i/2 + k/2;
        } while (i < foo(iterations) - k);

        return testVar + i + k + additionalVar;
    }










    /* Positive:   */
    public int test11(int n) {
        int testVar = 0;
        int additionalVar = 1;

   MyClass c = new MyClass(n);
        int i = -n;

        do {
            testVar -= i/2 + n/2 + additionalVar--;

            if (testVar < 5) {
                testVar++;
            } else {
                testVar += 2;
            }
/*            if (foo(testVar) < 100) {
                testVar = testVar*2;
            } else {
                testVar += 3;
            }
*/
            
        } while ( (i=foo(i)) <= iterations + c.value + additionalVar++);
        


        return testVar + additionalVar + c.value + i;
    }

    /* Positive: already bottom tested loop: inner with break to outer loop label */
    public int test12(int n) {
        int testVar = 0;
        long i = -1L;

mylabel:for (; ; ) {
            for (; ; ) {
                testVar -= i/2 + n/2;
                n--;
                i++;
                if (i >= iterations - testVar++) break mylabel;
            }
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

