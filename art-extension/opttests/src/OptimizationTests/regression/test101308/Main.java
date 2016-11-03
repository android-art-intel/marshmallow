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

package OptimizationTests.regression.test101308;

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
        int i = 26910, y = 27615, v8 = 16045, am = -26751, q7 = -4270, j = -55901, op = -53634, j8 = -34099, tu2 = 50408, j3 = 120, j5 = -53374, res = -46089;

        int gn[] = new int[N], rr0[] = new int[N], oe[] = new int[N], n[] = new int[N], z[] = new int[N], o[] = new int[N], v[] = new int[N], a3[] = new int[N], k[] = new int[N];

        init(gn, N, 6456);
        init(rr0, N, 13161);
        init(oe, N, 10390);
        init(n, N, 26176);
        init(z, N, 59889);
        init(o, N, 39556);
        init(v, N, 616);
        init(a3, N, 4957);
        init(k, N, 15670);

        for (int contr_1 : gn) {
            for (i = 31; i > 1; --i) {
                gn[i] = 16281;
            }
        }

        for (j8 = 2; j8 < 75; ++j8) {
            int contr_2 = 11;
            while (--contr_2 > 0) {
                z[j8] = o[j8];
                tu2 += o[j8];
                for (j3 = 41; j3 > 1; --j3) {
                    v[j3 + 1] = a3[j3] * o[j3];
                }
            }
            for (j5 = 30; j5 > 1; --j5) {
                k[j5] = -z[j5 + 1];
                rr0[j5] = j3;
            }
        }

        res = y + v8 - am + q7 - op + j8 - tu2;
        res += checkSum(gn, N) + checkSum(rr0, N) - checkSum(oe, N)
                + checkSum(n, N) - checkSum(z, N) + checkSum(o, N)
                - checkSum(v, N) + checkSum(a3, N) - checkSum(k, N);

        System.out.println("res = " + res);
    }
}
