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

package OptimizationTests.NonTemporalMove.WhileLoopObject_001;

// 1 non temporal move expected

public class Main {                                                                                                                                                   
    final int iterations = 0x40000;


    class C {
        int value = 0;
        public C(int v) {
            value = v;
        }
    }

    public long checkSum(C[] tab, int n) {
        long s = 0;
        for (int i = 0; i < n ; i++) {
            if (tab[i] != null) {
                s = s + tab[i].value;
            }
                
        }
        return s;
    }

    public long testLoop(C[] tab) {

        int i = 0;


        while (i < iterations) {
            tab[i] = tab[iterations/2];
            i++;
        }
 
        i = 10;

        tab[100] = new C(10);

        while (i < iterations) {
            tab[i] = tab[iterations/2];
            i++;
        }

        int k = 0;
        while (k < 2) {
            i = 0;
            while ( i < iterations) {
                tab[i] = tab[iterations/2];
                i++;
            }
            k++;
        }



        return checkSum(tab, iterations) + tab[iterations/2].value;
    }

    public void test()
    {
        C[] tab = new C [iterations];
        C c = new C(100);        
        for (int i = 0; i < iterations; i++) {
            tab[i] = c;
        }
 
        System.out.println(testLoop(tab));
    }

    public static void main(String[] args)
    {
        new Main().test();
    }

}  

