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

package OptimizationTests.FormBottomLoops.ForLoop_02;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * Simplest case when optimization should be applied: for loop
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


    /* Negative: at most 2 HPhi instructions at the top of the loop (More than 2 loop phis) */
    public int test13(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 0;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;
        
        additionalVar1++;
        for (int i = 0; i < iterations; i += 1) {
            testVar += 5 -  n/2 + i/2;
            additionalVar1 += i%2 + n%3;
            additionalVar2 += i*n;
            additionalVar3 += i - n;
    //        additionalVar4 += i%additionalVar5;

        }
        additionalVar3--;
        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5;
    }

    /* Positive: iterator of long type, loop upper bound is < Integer.MAX_VALUE, several assignments of same var inside loop */
    public int test14(int n) {
        int testVar = 0;

        for (long i = 0L; i < Integer.MAX_VALUE + 10; i++) {
            testVar += 5 + n/2 + (int)i/2;
            testVar -=(int)n*i/100;
            testVar++;
            testVar--;
        }

        return testVar;
    }


    /* Positive: many sotres to class fields, but only 2 HPhi instructions at the top of the loop */
    public int test15(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 57;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;

        MyClass c = new MyClass(100);

        additionalVar1++;
        for (int i = 0; i < iterations; i += 1) {
            testVar += 5 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
            c.value +=  i + additionalVar1;
            c.value1 = i%2 + n%3 - additionalVar2;
            c.value2 = i*n + additionalVar3;
            c.value3 = i - n + additionalVar4;
//            additionalVar4 = i/7;

        }
        additionalVar3--;
        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }



    /* Positive: call to function with try-catch in loop header and body*/
    public int test16(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 57;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;

        MyClass c = new MyClass(100);

        additionalVar1++;
        for (int i = tryDiv(iterations, n - 10); i < iterations; i += 1) {
            testVar += tryDiv(5, (i-1)) -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
            c.value +=  i + additionalVar1;

        }
        additionalVar3--;
        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }

    /* Positive: two loops, long type iterator in one of the loops */
    public int test17(int n) {
        int testVar = 0;
        int additionalVar1 = 1;
        int additionalVar2 = -1;
        int additionalVar3 = 57;
        int additionalVar4 = 20000;
        int additionalVar5 = -123456;

        MyClass c = new MyClass(100);

        additionalVar1++;
        for (int i = 0; i < iterations; i += 1) {
            testVar += 5 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
            c.value +=  i + testVar;

        }
        additionalVar3--;
        for (long i = 0L; i < iterations; i += 1) {
            testVar += 3 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3 - additionalVar4;
            c.value +=  i + additionalVar2;

        }
        testVar = testVar - 7;

        return testVar + additionalVar1 + additionalVar2 - additionalVar3 + additionalVar4 - additionalVar5 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }

    /* Positive: several loops, some of them are inside if-else */
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
            for (int i = 0; i < iterations; i += 1) {
                testVar += 3 -  n/2 + i/2 + additionalVar1 - additionalVar2 + additionalVar3;
                c.value +=  i + additionalVar1;

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

