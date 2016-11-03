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

package OptimizationTests.UnusedLoopRemoval.Inlining_I_03;

public class Main {

    // Three loops with inlining to be removed

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


   public static int foo(int x) {
       return x;
   }


    public static int getValue() {
        return 5;
    }

    public int loop(int n) {
        int res = 0;
        for (int i = 0; i < 10000; i++) {
            res = foo(getValue()) * 2;
        }

        res += 1;
        for (int i = 0; i < 10000; i++) {
            res += foo(getValue() * 2);
        }
        res += 1;


        int testVar = 0;

        C c = new C(10);
        C c1 = new C(20);

        boolean[] b = new boolean[n];

        int m = n - 1;
       
       
        int j = 0;
        do 
        {
            res = c.getArr1().length;
            m = 5;
            j++;
        } while (j < 10000);
    

        res += 1;

        if (b[0] || c1.arr[0]) {
            return n + res;
        } else {
            return n + 1 + res;
        }

    }

    public static void main(String[] args) {
        int res = new Main().loop(10);
        System.out.println(new Main().loop(10));
    }
}
