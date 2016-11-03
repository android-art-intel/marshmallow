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

package OptimizationTests.FormBottomLoops.FloatPointIterator_03;

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



    /* Positive: initial IV value uses function invocation with side effect, NaN inside loop on first iteration*/
    public double test19(int n) {
        float testVar = 0.123f;
        float additionalVar1 = -1f;

        MyClass c = new MyClass(100);

        for (float i = sideEffectFunc(n); i < iterations; i += count) {
            testVar += 5 -  n/(i - 11) + i/2 + additionalVar1;
            c.value +=  i + foo(testVar);

        }

        return testVar + additionalVar1 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    } 

    /* Positive:  IV increment value uses function invocation with side effect, NaN in loop header*/
    public double test20(int n) {
        float testVar = 0f;
        int additionalVar1 = 1;


        MyClass[] cc = new MyClass[iterations];

        for (float i = sideEffectFunc(n); i < (n-10)*(iterations - 0.1f )/(n - 10); i += sideEffectFunc(count)) {
            cc[(int)i] = new MyClass((int)i);
            cc[(int)i].value = (int)i;
            cc[(int)i].arr[0] = (int)i/2;
            testVar += 5 -  n/2 + i/2 + cc[(int)i].value1;

        }

        return testVar + additionalVar1 ;
    } 

    /* Positive: overflow within loop */
    public double test21(int n) {
        float testVar = 3.3f;
        float additionalVar = Float.MAX_VALUE/2;

        for (float i = 0.1f; i < iterations; i++) {
            testVar *= -(5 +  n/2 + i/3 + additionalVar);
        }
        return testVar + additionalVar;
    }

    /* Positive: unkown number of iteration at compile time, no iterations will occur with n=10*/
    public double test22(int n) {


        int testVar = 0;
        int additionalVar1 = 1;


        MyClass[] cc = new MyClass[iterations];
        cc[0] = new MyClass(100);

        for (float i = sideEffectFunc(n); i < n; i += sideEffectFunc(count)) {
            cc[(int)i] = new MyClass((int)i);
            cc[(int)i].value = (int)i;
            cc[0].value = (int)i - 1;
            cc[(int)i].arr[0] = (int)i/2;
            testVar += 5 -  n/2 + i/2 + cc[(int)i].value1;

        }

        return testVar + additionalVar1 + cc[0].arr[0] + cc[0].value2 - cc[0].value3 + cc[0].value1/2;


    }



    /* Positive:  count down loop, float type iterator */
    public double test23(int n) {
        float testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);

        for (float i = (float)iterations/1.33f; i >0.1f; i -= 1.1f) {
            testVar += 5 -  n/2 + i/2 + additionalVar1;
            c.value += (int) (i + foo(testVar));

        }

        return testVar + additionalVar1 + c.arr[0] + c.value2 - c.value3 + c.value1/2;
    }

    /* Positive: count down loop */
    public double test24(int n) {
        double testVar = 0;
        int additionalVar1 = 1;

        MyClass c = new MyClass(100);

        for (double i = foo(-n); i >=  -iterations - count -c.arr[2]; i = i - c.arr[1]) {
            testVar += 5 -  n/2 + i/2 + additionalVar1;
            c.value += (int) (i + foo(testVar));

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

