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

package OptimizationTests.regression.test83708;

public class Main {

    public static final int N = 100;

    private static void init(int[] a, int n, int seed) {
        for (int j = 0; j < n; j++) {
            a[j] = ((j % 2 == 0) ? seed + j : seed - j) % 65535;
        }
    }

    private static int checkSum(int[] a, int n) {
        int sum = 0;
        for (int j = 0; j < n; j++) {
            sum += ((j % 2 == 0) ? a[j] : -a[j]);
        }
        return sum;
    }

    public static void main(String[] args) {
        int kk = 19, ki = -60211, b2 = 18254, res = 31923, n9 = 0;

        int wu[] = new int[N], we[] = new int[N], ch5[] = new int[N], ea[] = new int[N], j5[] = new int[N];

        init(wu, N, 34952);
        init(we, N, 18862);
        init(ch5, N, 45138);
        init(ea, N, 44695);
        init(j5, N, 4473);

        for (ki = kk; ki < 29; ++ki) {
            try {
                int[] exc_1 = null;
                try {
                    we[ki] += wu[ki];
                    b2 = exc_1.length;

                } catch (NullPointerException exc_14) {
                }
                n9 -= (ea[ki])
                        * ((ch5[ki]) - (-28756 * (644 / ((j5[ki] != 0) ? j5[ki]
                                : 1))));
            } catch (ArrayIndexOutOfBoundsException exc_15) {
            }
        }

        res = b2 + n9;
        res += checkSum(wu, N) - checkSum(we, N) + checkSum(ch5, N)
                + checkSum(ea, N) - checkSum(j5, N);

        System.out.println("res = " + res);
    }
}
