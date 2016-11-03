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

package OptimizationTests.regression.test111518_1;

public class Main {

public static final int N = 100;

    public static void main(String[] args) {
        int ex_count = 0;
        long n, b = 0;
        int k = 0;

        long divisors[] = new long[N];

        for(k = 0; k < N; k++) {
            divisors[k] = k;
        }
        divisors[N-1] = 0;

        for (n = 0; n < N * 1000 * 1000; n++) {
            k = (int) n % N;
            try {
                b+= n % divisors[k];
            } catch (ArithmeticException a_e) {
                if((++ex_count%100000) == 0) {
                    System.out.println(a_e+" " + ex_count);
                }
            }
        }
        System.out.println("b = " + b);
    }
}
