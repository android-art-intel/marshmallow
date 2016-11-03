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

package OptimizationTests.FormBottomLoops.NestedLoop_01;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * Simplest cases when optimization should be applied: nested loops
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

        int[] getArrSideEffect(int x) {
            value = value/x;
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


    /* Positive: simple test, optimization applied to inner loop only (to one loop)*/
    public int test1(int n) {
        int testVar = 0;

        for (int j = 0; j < 100; j++) {
            for (int i = 0; i < 100; i++) {
                testVar += 5 + n/2 + i/2 + j/2;
            }
                testVar += 5 + n/2 + j/2;
        }

        return testVar;
    }


    /* Positive: inner loop's iterator start value is unkown, outer loop is while */
    public int test2(int n) {
        int testVar = 0;
        int k = n;
        int i = 0;
        while ( i < iterations) {
            testVar  += new MyClass(i++).arr.length;
            for ( k = n; k < 100; k++) {
                testVar += 5 + n/2 - k/3;
                k++;
            }
        }

        return testVar + n + k + i;
    }


    /* Positive: inner loop with 2 iterations, do-while outer loop, integer overflow */
    public int test3(int n) {
        int testVar = 0;

        int k = n;
        do {
            for (int i = 0; i < 2; i++) {
                testVar += 5 + n/2 + i/2;
            }
            k +=2;
            testVar += n - k + Integer.MAX_VALUE/3;

        } while (k <= iterations);
        return testVar + n;
    }


    /* Positive: nested loops (3 levels), iterator overflows */
    public int test4(int n) {
        int testVar = 0;
        int additionalVar = 1;

        for (int j = iterations; j >= -1; j--) {
                int k = j + 1;
                while (k > 0) {
                    testVar += k - j + n/2 ;
                    for (int i = 0; i < 100; i += Integer.MAX_VALUE) {
                        testVar += 5 -  n/2 + i/2 + j/2 + k/3;
                    }
                    k -= 2;
                }
                testVar += j/5;
                additionalVar += j/6;

        }
        return testVar + additionalVar;
    }


    /* Positive: 3 inner loops, one of them breaks limitation(>1 exits),  outer loop is count-down - 2 "rewrite loop" expected */
    public int test5(int n) {
        int testVar = 0;
        MyClass c = new MyClass(100);


        for (int j = c.value; j >= -10; j = j - 8) {
           

            for (int i = c.value + iterations; i >= 1; i = i - 7 ){
                c.arr[i%100] += 5 + n/2 + i/5 + c.arr[0] + c.value2;
            }
            c.value1 = j + foo(n);

            /* no form bottom loops: more than 1 exit */
            for (int i = 1; (i <= iterations && i != iterations + 5 ) ; i = i + 3) {
                c.arr[i%100] += 5 + n/2 + i/2 + c.arr[0] + c.value;
            }
            for (int i = c.value + iterations; i >= 0; i = i - 11 ){
                c.arr[i%100] += 5 + n/2 + i/2 + c.arr[0] + c.value1;
            }
            
 
        }


        return testVar + n;
    }

    /* Positive: not a linear increment, outer for, inner while with float iterator*/
    public int test6(int n) {
        float testVar = 0.1f;
        MyClass c = new MyClass(iterations);

        for (int i = 1; i < c.value; i = i*3) {
            float k = i*1.33f;
            while ( k < c.value/10) {
                testVar += 5 + n/2 + i/2 + (k++)/3;
                k = foo((int)k/2);
            }

        }

        return (int)testVar + n;
    }


    /* Positive: outer loop breaks limitation (> 2 Phis), inner loop should be optimized */
    public int test7(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = 2;
        int additionalVar3 = 3;

        while (testVar < iterations) {
            additionalVar1 += testVar++;
            for (int i = 0; i < n + testVar; i++) {
                i += 1 + n/2 + (++i)/2;
                n = additionalVar2 + testVar/2;
                
            }
            additionalVar3 += testVar + n;

        }

        return testVar + n + additionalVar1 + additionalVar2 + additionalVar3;
    }


    /* Negative: outer loop uses break, inner loop: The header of the loop must not create values that are used outside the loop (Result is used by) */
    public int test8(int n) {
        int testVar = 0;
//        int i = 0;

        while (true) {
            for ( int i = 0; i++ < iterations; ) {
                testVar += 5 + n/2 + i/2 ;
            }
            if (testVar > iterations) {
                break;
            }
        }

        return testVar + n;// + i;
    }


    /* Positive: outer loop ises long type iterator, inner loop body is empty: should probably be modified when DCE is implemented */
    public int test9(int n) {
        int testVar = 0;

        for (long j = 0L; j < iterations ; j++) {
            for (int i = 0; i < iterations; i++) {
            }
        }
        

        return testVar;
    }

    /* Negative: 3 outer loops, 1 inner loop that breaks limitation: at most 2 HPhi instructions at the top of the loop (More than 2 loop phis) */
    public int test10(int n) {
        int testVar = 0;
        
        if (n > 0) {
            testVar = 1;
        }


        int i = 0;
        int k = 1;


        for (int m = 0; m < n; m++) {
            testVar += k-- + m;
            for (int j = 0; j < n; j++) {
                while ( k < n) {
                    k++;
                    testVar += k/2 + n%2;
                    for ( ; i < iterations - (k*=2) ; i++) {
                        testVar -=  n/2;
                    }
                }
                testVar += k++ + j + m;
            }
        }

        return testVar + i + k;
    }

    /* Negative:  The header of the loop must not create values that are used outside the loop  */
    public int test11(int n) {
        int testVar = 0;
        int additionalVar = 1;

        while (additionalVar < iterations*10) {
            for (float i = 0.1f; i < iterations - additionalVar++; i++) {
                testVar -= i/2 + n/2;
            }
            additionalVar+=100;
        }

        return testVar + additionalVar;
    }

    /* Negative: inner loop: at most 2 HPhi instructions at the top of the loop (More than 2 loop phis) */
    public int test12(int n) {
        int testVar = 0;

        while (true) {
            for (int i = 0; i < iterations - testVar++; i++) {
                testVar -= i/2 + n/2;
                n--;

            }
            if (n < -100) {
                break;
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

