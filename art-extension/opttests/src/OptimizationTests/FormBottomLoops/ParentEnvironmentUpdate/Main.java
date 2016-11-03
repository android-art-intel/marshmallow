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

package OptimizationTests.FormBottomLoops.ParentEnvironmentUpdate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * Specific test case with parent environment update: regression test for CAR-2664
 *
 **/

public class Main {                                                                                                                                                   
    final int iterations = 1100;


    C c_field = new C(iterations);

    class C {
        boolean[] arr;
        int count = 0;
        public C(int n) {
            int arr_length = 0;
            if (n > 0) {
                arr_length = n;
            } else {
                arr_length = 0;
            }
            arr = new boolean[arr_length];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = false;
            }
        }
        int bar (int x) {
            return arr[x] ? x : 0;
        }

        /* inlined */
        boolean[] getArr1() {
            return arr;
        }

        /* not inlined */
        boolean[] getArrNotInlined(int x) {
            arr[x] = false;
            return arr;
        }


        /* inlined */
        boolean[] getArrInlined(int x) {
            count = count/x;
            return arr;
        }




    }


    boolean[] inline_arr(boolean[] bb) {
       return bb;
    } 

    /* Positive: Reproduces CAR-2664: parent environment update occurs */
    public int test1(int n) {
        int testVar = 0;

        C c = new C(n);
        C c1 = new C(n*2);

        boolean[] b = new boolean[n];

        int m = n - 1;
        for (int i = 0; i < c.getArrInlined(m).length; i++) {
            i++;
            b[i] = true;
            c1 = new C(c1.arr.length);
        }

        m++;
        n++;
        testVar++;
        
        if (b[0] || c1.arr[0]) {
            return n;
        } else {
            return n + 1;
        }

    }


    /* Negative (function call in loop header): similar to test1 with minor variation */
    /* Update: Positive */
    public int test1a(int n) {
        int testVar = 0;

        C c = new C(n);
        C c1 = new C(n*2);

        boolean[] b = new boolean[n];

        int m = n - 1;
        for (int i = 1; i < c.getArrNotInlined(m).length-1; i++) {
            i++;
            b[i] = true;
            c1 = new C(c1.arr.length);
        }

        m++;
        n++;
        testVar++;
        
        if (b[0] || c1.arr[0]) {
            return n;
        } else {
            return n + 1;
        }

    }



    /* Positive: Same as test1 + minor changes */
    public int test2(int n) {



        int testVar = 0;

        C c = new C(n);
        C c1 = new C(n*2);

        boolean[] b = new boolean[n];
        int[] iarr = new int[n];

        int m = n - 1;
        for (int i = 0; i < c.getArrInlined(m).length; i++) {
            i++;
            b[i] = true;
            c1 = new C(c1.arr.length);
            iarr[i] = i/2;
        }

        m++;
        n++;
        testVar++;
        
        if (b[0] || c1.arr[0]) {
            return n;
        } else {
            return n + 1;
        }

    }
    
    /* Negative (function call in loop header): Same as test1 + minor changes */
     /* Update: Positive */
    public int test2a(int n) {



        int testVar = 0;

        C c = new C(n);
        C c1 = new C(n*2);

        boolean[] b = new boolean[n];
        int[] iarr = new int[n];

        int m = n - 1;
        for (int i = 0; i < c.getArrNotInlined(m).length; i++) {
            i++;
            b[i] = true;
            c1 = new C(c1.arr.length);
            iarr[i] = i/2;
        }

        m++;
        n++;
        testVar++;
        
        if (b[0] || c1.arr[0]) {
            return n;
        } else {
            return n + 1;
        }

    }
    



    /* Positive: Another test with similar loop condition */
    public int test3(int n) {
        int testVar = 0;

        C c = new C(n);

        boolean[] b = new boolean[n];


        for (int i = 0; i < c.getArr1().length/2; i++) {
            b[i] = true ;
        }

        
        if (b[0]) {
            return n;
        } else {
            return n+1;
        }

    }


    /* Positive: Another test with similar loop condition */
    public int test4(int n) {
        int testVar = 0;

        C c = new C(n);

        boolean[] b = new boolean[n];


        for (int i = 0; i < c.getArrInlined(n-1).length; i++) {
            b[i] = true ;
        }

        
        if (b[0]) {
            return n;
        } else {
            return n+1;
        }

    }
    
    /* Negative (function call in loop header): Another test with similar loop condition */
     /* Update: Positive */
    public int test4a(int n) {
        int testVar = 0;

        C c = new C(n);

        boolean[] b = new boolean[n];


        for (int i = 0; i < c.getArrNotInlined(n-1).length; i++) {
            b[i] = true ;
        }

        
        if (b[0]) {
            return n;
        } else {
            return n+1;
        }

    }
    


    /* Positive */
    public int test5(int n) {
        int testVar = 0;

        C c = new C(n);
        C c1 = new C(n*2);

        boolean[] b = new boolean[n];

        int m = n - 1;
        for (int i = c.getArrInlined(m).length - 1; i>=0; i--) {
            i--;
            b[i] = true;
            c1 = new C(c1.arr.length);
        }

        m++;
        n++;
        testVar++;
        
        if (b[0] || c1.arr[0]) {
            return n;
        } else {
            return n + 1;
        }

    }

     /* Positive  */
    public int test5a(int n) {
        int testVar = 0;

        C c = new C(n);
        C c1 = new C(n*2);

        boolean[] b = new boolean[n];

        int m = n - 1;
        for (int i = c.getArrNotInlined(m).length - 1; i>=0; i--) {
            i--;
            b[i] = true;
            c1 = new C(c1.arr.length);
        }

        m++;
        n++;
        testVar++;
        
        if (b[0] || c1.arr[0]) {
            return n;
        } else {
            return n + 1;
        }

    }

    

    /* Positive: Bottom tested version of test1 (CAR-2664) */
    public int test6(int n) {
        int testVar = 0;

        C c = new C(n);
        C c1 = new C(n*2);

        boolean[] b = new boolean[n];

        int m = n - 1;
        int i = 0;
        do {
            i++;
            b[i] = true;
            c1 = new C(c1.arr.length);
            i++;
        } while (i < c.getArrInlined(m).length);

        m++;
        n++;
        testVar++;
        
        if (b[0] || c1.arr[0]) {
            return n;
        } else {
            return n + 1;
        }

    }

    /* Negative (function call in loop header): Bottom tested version of test1 (CAR-2664) */
    public int test6a(int n) {
        int testVar = 0;

        C c = new C(n);
        C c1 = new C(n*2);

        boolean[] b = new boolean[n];

        int m = n - 1;
        int i = 0;
        do {
            i++;
            b[i] = true;
            c1 = new C(c1.arr.length);
            i++;
        } while (i < c.getArrNotInlined(m).length);

        m++;
        n++;
        testVar++;
        
        if (b[0] || c1.arr[0]) {
            return n;
        } else {
            return n + 1;
        }

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

