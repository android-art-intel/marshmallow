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

package OptimizationTests.NonTemporalMove.NonSinlgeBasicBlockInt_003;

// No non temporal move expected: breaking single basic block limitation

public class Main {                                                                                                                                                   
    final int iterations = 0x40000;

    public long checkSum(int[] tab, int n) {
        long s = 0;
        for (int i = 0; i < n ; i++) {
            s = s + tab[i];
        }
        return s;
    }

    public long testLoop(int[] tab, int n) {
        
        for (int i = 0; i < iterations; i++) {
            switch (n) {
                case 0: tab[i] = 0;
                        break;
                case 1: tab[i] = 1;
                        break;
                default:
                        tab[i] = i;
                        break;
            }
        }
        
        return checkSum(tab, iterations);
    }

    public void test()
    {
        int[] tab = new int [iterations];
        System.out.println(testLoop(tab,0));
        System.out.println(testLoop(tab,1));
        System.out.println(testLoop(tab,2));
    }

    public static void main(String[] args)
    {
        new Main().test();
    }

}  

