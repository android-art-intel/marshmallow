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

package OptimizationTests.FormBottomLoops.ConditionIf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 *
 * Simplest cases whith if-else statements
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
        
        int[] zeroAndReturn(int x) {
            arr[x] = 0;
            return arr;
        }

        int[] getArrSideEffect(int x) {
            value = value/x;
            return arr;
        }



        int bar (int x) {
            return arr[x] > 0 ? x : 0;
        }

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

    public int foo(int x) {
        for (int i = 0; i < iterations; i++) {
            x = x + i/2;
        }
         return x;
    }


        boolean cond(int x) {
            return (x % 2 == 0)?true:false;
        }


    public int sideEffectFunc(int x) {
        count++;
        return x + count;

    }


    public int randomInt(int min, int max) {
            Random rand = new Random();
            int result = rand.nextInt((max - min) + 1) + min;
                return result;
    }


 
    /* Positive:  if inside loop */
    public int test1(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);

        for (int i = 0; i < iterations; i++) {
            testVar += 5 -  n/2 + i/2 + additionalVar1;
            if (testVar > 50) {
                c.value +=  i + foo(testVar);
            } else {
                c.value +=  i - foo(testVar);
            }

        }

        return testVar + additionalVar1 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    } 

    /* Positive: if inside loop, count down loop */
    public int test2(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);

        for (int i = foo(-n); i >=  -iterations - count -c.arr[2]; i = i - c.arr[1]) {
            testVar += 5 -  n/2 + i/2 + additionalVar1;
             if (testVar > 50) {
                testVar +=  i + foo(testVar);
             }
           c.value +=  i + foo(testVar);

        }

        return testVar + additionalVar1 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    } 

    /* Positive:  if inside loop, i is modified inside if */
    public int test3(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);

        int i = 0; 
        while (i < iterations) {
            testVar += 5 -  n/2 + i/2 + additionalVar1;
            if (testVar > n) {
                c.value +=  i + foo(testVar);
                i++;
                testVar += 3;
            } else {
                c.value +=  i - foo(testVar);
                i = i + additionalVar1;
            }
            i++;

        }

        return testVar + additionalVar1 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }

    /* Positive:  if inside loop, i is modified inside if , reference type in if */
    public int test4(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(10*n);
        MyClass c1 = new MyClass(n);

        boolean[] bb = new boolean[n];

        for (int i = 0; i < c.getArrSideEffect(n).length; i++) {
            if (new MyClass(i+1).arr[i/2] > 2) {
                c1 = new MyClass(i++);
            } else {
                c1 = new MyClass(i+2);
            }
            c1.value +=  i - foo(testVar);
            i = i + additionalVar1;
            bb[i%bb.length] = false;

        }

        return testVar + additionalVar1 + c1.arr[0] + c1.value2 - c1.value3 + c1.value1/2;
    }

    /* Negative because of function call (unable to clone):  if inside loop, i is modified inside if , reference type in if */
    /* Update: Positive */
    public int test4a(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(10*n);
        MyClass c1 = new MyClass(n);

        boolean[] bb = new boolean[n];

        for (int i = 0; i < c.zeroAndReturn(n).length; i++) {
            if (new MyClass(i+1).arr[i/2] > 2) {
                c1 = new MyClass(i++);
            } else {
                c1 = new MyClass(i+2);
            }
            c1.value +=  i - foo(testVar);
            i = i + additionalVar1;
            bb[i%bb.length] = false;

        }

        return testVar + additionalVar1 + c1.arr[0] + c1.value2 - c1.value3 + c1.value1/2;
    }


    /* Negative:  nested if results in more than 5 basic blocks in the loop: optimization limitation */
    public int test5(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(n);
        MyClass c1 = new MyClass(n);

        boolean[] bb = new boolean[n];

        int i = 0; 
        while (i < c.zeroAndReturn(n-1).length) {
            if (i++ < 3) {
                if (c.arr[i] > 10) {
                    //        c1 = new MyClass(i++);
                    bb[i%bb.length] = true;
                    c1.value++;
                } else {

                        c1.value +=  i - foo(testVar);
                        i = i + additionalVar1;
                        bb[i%bb.length] = false;
                    }
                }
             i++;
            }


        return testVar + additionalVar1 + c1.arr[0] + c1.value2 - c1.value3 + c1.value1/2;
    }

    /* Negative:  complex if condition results in more than 5 basic blocks in the loop: optimization limitation */
    public int test7(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(n);
        MyClass c1 = new MyClass(n);

        boolean[] bb = new boolean[n];

        for (int i = 0; i < c.zeroAndReturn(n-1).length; i++) {
            if (i++ < 3 || sideEffectFunc(i) == 11) {
                if (c.arr[i] > 10) {
                    //        c1 = new MyClass(i++);
                    bb[i%bb.length] = true;
                    c1.value += count;
                } else {

                        c1.value +=  i - foo(testVar);
                        i = i + additionalVar1;
                        bb[i%bb.length] = false;
                    }
                }
            }


        return testVar + additionalVar1 + c1.arr[0] + c1.value2 - c1.value3 + c1.value1/2 + count;
    }


    /* Positive:  if outside loop */
    public int test6(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(10*n);
        MyClass c1 = new MyClass(n);

        boolean[] bb = new boolean[n];

        if (c1.arr[n-1] > 1 ) {
            for (int i = 0; i < c.getArrSideEffect(n).length; i++) {
                c1 = new MyClass(i++);
                c1.value +=  i - foo(testVar);
                i = i + additionalVar1;
                bb[i%bb.length] = (c1.value == foo(i));

            }
        }

        return testVar + additionalVar1 + c1.arr[0] + c1.value2 - c1.value3 + c1.value1/2;
    }


    /* Negative (function call in loop header -> unable to clone):  if outside loop */
    /* Update: Positive */
    public int test6a(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(10*n);
        MyClass c1 = new MyClass(n);

        boolean[] bb = new boolean[n];

        if (c1.arr[n-1] > 1 ) {
            for (int i = 0; i < c.zeroAndReturn(n).length; i++) {
                c1 = new MyClass(i++);
                c1.value +=  i - foo(testVar);
                i = i + additionalVar1;
                bb[i%bb.length] = (c1.value == foo(i));

            }
        }

        return testVar + additionalVar1 + c1.arr[0] + c1.value2 - c1.value3 + c1.value1/2;
    }





/* Negative:  if inside loop, more than one back edge */
    public int test8(int n) {
        int testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);

        int i = 0; 
        while (i < iterations) {
            i++;
            testVar += 5 -  n/2 + i/2 + additionalVar1;
            if (cond(testVar)) {
                c.value +=  i + foo(testVar);
                i++;
                testVar += 3;
            } else {
                c.value +=  i - foo(testVar);
                i = i + additionalVar1;
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

