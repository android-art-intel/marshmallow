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

package OptimizationTests.FormBottomLoops.NestedLoop_03;

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


    /* Positive: 7 levels of nested loops, initial IV value uses function invocation with side effect*/
    public int test19(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);

        for (int i = sideEffectFunc(n); i < iterations; i += count) {

            while ( i++ < iterations/5 ) {
                testVar += 5 -  n/2 + i/2 + additionalVar1;
                while (i++ < iterations/6) {
                    testVar += 5 -  n/2 + i/2 + additionalVar1;
                    while (i++ < iterations/7) {
                        testVar += 5 -  n/2 + i/2 + additionalVar1;
                        while (i++ < iterations/8) {
                            testVar += 5 -  n/2 + i/2 + additionalVar1;
                            while (i++ < iterations/9) {
                                while (i < iterations/10) {
                                    c.value +=  foo(testVar);
                                    i++;
                                }
                            }
                        }
                    }
                }
            }

        }

        return testVar + additionalVar1 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    } 

    /* Positive:  IV increment value uses function invocation with side effect*/
    public int test20(int n) {
        int testVar = 0;
        int additionalVar1 = 1;


        MyClass[] cc = new MyClass[iterations];

        for (int i = sideEffectFunc(n); i < iterations; i += sideEffectFunc(count)) {
            cc[i] = new MyClass(i);
            cc[i].value = i;
            cc[i].arr[0] = i/2;
            testVar += 5 -  n/2 + i/2 + cc[i].value1;

        }

        return testVar + additionalVar1 + cc[n+2].arr[0] + cc[n+2].value2 - cc[n+2].value3 + cc[n+2].value1/2;
    } 

    /* Positive: integer overflow within loop */
    public int test21(int n) {
        int testVar = 3;
        int additionalVar = Integer.MAX_VALUE/2;


        int k = 1;
        MyClass c = new MyClass(1);
        do {

            c = new MyClass(k);
            for (int i = 0; i < Integer.MAX_VALUE/2; i++) {
                testVar *= (5 +  n/2 + i/3 + additionalVar);
                c.value1 = i%k;
            }
            k++;
        } while (c.getArr().length < 2);
        return testVar + additionalVar + c.getArr().length + c.value1;
    }

/* Positive: unkown number of iteration at compile time, no iterations will occur with n=10*/
    public int test22(int n) {


        int testVar = 0;
        int additionalVar1 = 1;


        MyClass[] cc = new MyClass[iterations];
        cc[0] = new MyClass(100);

        int k = 0;
        while (cc[k].getArr().length < n ) {
            if (cc[k].getArr()[0] != 1) {
                for (int i = sideEffectFunc(n); i < n; i += sideEffectFunc(count)) {
                    cc[i] = new MyClass(i);
                    cc[i].value = i + k;
                    cc[0].value = i - 1;
                    cc[i].arr[0] = i/2;
                    testVar += 5 -  n/2 + i/2 + cc[i].value1;

                }
            }
            k += 2;
        }

        return testVar + additionalVar1 + cc[0].arr[0] + cc[0].value2 - cc[0].value3 + cc[0].value1/2;


    }



    /* Positive:  count down loop */
    public int test23(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);



        while (true) {
            for (int i = iterations; i >0; i--) {
                testVar += 5 -  n/2 + i/2 + additionalVar1;
                c.value +=  i + foo(testVar);

            }
            if (testVar > 1000) break;
        }

        return testVar + additionalVar1 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }

    /* Positive: count down loop */
    public int test24(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);

        for (int i = foo(-n); i >=  -iterations - count -c.arr[2]; i = i - c.arr[1]) {
            for (i = foo(-n); i >=  -iterations - count -c.arr[2]; i = i - c.arr[1]) {
                testVar += 5 -  n/2 + i/2 + additionalVar1;
                c.value +=  i + foo(testVar);

            }
        }

        return testVar + additionalVar1 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
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

