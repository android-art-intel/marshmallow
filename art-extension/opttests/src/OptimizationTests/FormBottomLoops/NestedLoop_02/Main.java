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

package OptimizationTests.FormBottomLoops.NestedLoop_02;

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



    /* Negative: ourer loop -- already bottom tested, inner loop --  at most 2 HPhi instructions at the top of the loop (More than 2 loop phis) */
    public int test13(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 0;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;

        do {
            additionalVar1++;
            for (int i = 0; i < iterations; i += 1) {
                testVar += 5 -  n/2 + i/2;
                additionalVar1 += i%2 + n%3;
                additionalVar2 += i*n;
                additionalVar3 += i - n;
                //        additionalVar4 += i%additionalVar5;

            }
            additionalVar3--;
        } while (additionalVar1 < 100);
        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5;
    }

    /* Positive: same iterator in inner and outer loop, several assignments of same var inside loop */
    public int test14(int n) {
        int testVar = 0;

        int i = 0;
        while (i < iterations) {
            for (; i < 100; i++) {
                testVar += 5 + n/2 + i/2;
                testVar -= n*i/100;
                testVar++;
                testVar--;
            }
            i++;
        }

        return testVar;
    }


    /* Positive: outer loop with float iterator, inner loop: many sotres to class fields, but only 2 HPhi instructions at the top of the loop */
    public int test15(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 57;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;

        MyClass c = new MyClass(100);

        float f = 0.12345f;
        additionalVar1++;

        while (f < iterations) {
            for (int i = c.value; i < iterations; ) {
                testVar += 5 -  n/2 + c.value/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
                c.value +=  additionalVar1;
                c.value1 = c.value + n%3 - additionalVar2;
                c.value2 = c.value*n + additionalVar3;
                c.value3 = c.value - n + additionalVar4 + i++;
                //            additionalVar4 = i/7;

            }
            f *= additionalVar3;
        }
        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }


    /* Positive: outer loop is already bottom tested, call to function with try-catch in loop body */
    public int test16(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 57;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;

        MyClass c = new MyClass(100);

        additionalVar1++;
        do {
            for (int i = 0; i < c.getArrSideEffect(n).length; i += 1) {
                testVar += tryDiv(5, (i-1)) -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
                c.value +=  i + additionalVar1;

            }
        } while (c.value1*2 < tryDiv(iterations, c.value1*2));
        additionalVar3--;
        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }

    

    /* Negative (function call in loop header -> unable to clone): outer loop is already bottom tested, call to function with try-catch in loop body*/
    /* Update: Positive */
    public int test16a(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 57;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;

        MyClass c = new MyClass(100);

        additionalVar1++;
        do {
            for (int i = 0; i < c.modifyAndGetArr().length; i += 1) {
                testVar += tryDiv(5, (i-1)) -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
                c.value +=  i + additionalVar1;

            }
        } while (c.value < tryDiv(iterations, c.value));
        additionalVar3--;
        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }





    /* Positive: outer loop is bottom tested, two inner loops on different levels */
    public int test17(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 57;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;
        float f = -1.0001f;

        MyClass c = new MyClass(100);
        


        additionalVar1++;
        do {
            for (int i = 0; i < iterations; i += 1) {
                testVar += 5 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
                c.value +=  i + testVar;

            }
            additionalVar3--;

            while (testVar < 100) {
                for (int i = 0; i < iterations; i += 1) {
                    testVar += 3 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;

                    c.value +=  i + additionalVar2;

                }
            }
            f =  f / -0.12345f;
        } while (f < iterations);
        testVar = testVar - 7;

        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }


    /* Positive: several inner loops, some of them are inside if-else, some of them are bottom tested */
    public int test18(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 57;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;

        MyClass c = new MyClass(100);
        MyClass c1 = new MyClass(200);

        additionalVar1++;
        for (int i = 0; i < iterations; i += 1) {
            testVar += 5 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
            c.value +=  i + testVar;

        }
        additionalVar3 = additionalVar3 + n;
        if (additionalVar3 < 100) {
            int i = 0 ;
             do {
                testVar += 3 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3;
                c.value +=  i++ + additionalVar1;

            } while (i < iterations);

             while (i >=0 ) {
                 i -=10;
                 c.value += i/2;
             }
        } else {
            for (int i = 0; i < iterations; i += 1) {
                testVar += 3 -  n/2 + i/2 + additionalVar2 + additionalVar3;
                c.value +=  i + additionalVar1 + foo(i);
                c1.value = c.value;

            }
        }
        testVar = testVar - 7 - c.value;;

        for (int i = foo(n/10)*foo(n/2); i < iterations; i += 1) {
            testVar += 5 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
            c.value +=  i + foo(testVar);

        }


        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5 + c.arr[0] + c.value2 - c.value3 + c1.value/2;
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

