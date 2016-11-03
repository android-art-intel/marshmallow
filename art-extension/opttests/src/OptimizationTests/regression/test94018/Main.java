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

package OptimizationTests.regression.test94018;

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
        int k = -347, kv4 = -30554, tl7 = 39629, a = -48425, a7 = 46157, res = 16753;

        int m8[] = new int[N], r[] = new int[N], i3[] = new int[N], c[] = new int[N];
        int xq[][] = new int[N][N];

        init(m8, N, 21222);
        init(r, N, 53867);
        init(i3, N, 5691);
        init(c, N, 47204);

        for (k = 60; k > 1; --k) {
            if (-47532 - (m8[k - 1]) <= r[k + 1]) {
                if (i3[k - 1] > 0)
                    break;
                for (kv4 = k; kv4 > 1; kv4--) {
                    int contr_1 = 11;
                    while (--contr_1 > 0) {
                        try {
                            xq[kv4 - 1][k] = c[kv4 - 1] % 476536715 / kv4;
                        } catch (ArithmeticException a_e) {
                        }
                    }
                }
            }
        }

        res = tl7 + a - a7;
        res += checkSum(m8, N) + checkSum(r, N) - checkSum(i3, N)
                + checkSum(c, N);

        System.out.println("res = " + res);
    }
}
