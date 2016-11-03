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

package OptimizationTests.NonTemporalMove.NestedLoopLong_005;

// 2 non temporal move expected

public class Main {                                                                                                                                                   

    final int iterations = 133000;

    public long checkSum(long[] tab, int n) {
        long s = 0;
        for (int i = 0; i < n ; i++) {
            s = s + tab[i];
        }
        return s;
    }

    public long testLoop(long[] tab1, long[] tab2) {
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < iterations; i++) {
                tab1[i] = i;
            }
            for (int i = 0; i < iterations; i++) {
                tab2[i] = i;
            }
        }
        return checkSum(tab1, iterations) - checkSum(tab2, iterations);
    }

    public void test()
    {
        long[] tab1 = new long [iterations];
        long[] tab2 = new long [iterations];
        System.out.println(testLoop(tab1, tab2));
    }

    public static void main(String[] args)
    {
        new Main().test();
    }

}  

