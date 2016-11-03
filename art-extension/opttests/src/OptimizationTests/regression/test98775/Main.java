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

package OptimizationTests.regression.test98775;

public class Main {

    public static final int N = 1000;

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

    public static void test() {
        int vm5 = -10345, k = -10160, nu = 59847, nb = -60456, jk = 407, res = -52503;

        int qj[] = new int[N], aq0[] = new int[N], cu2[] = new int[N], h2[] = new int[N], q4[] = new int[N], aa2[] = new int[N], am[] = new int[N], e[] = new int[N], c0[] = new int[N], qi[] = new int[N];

        init(qj, N, 5225);
        init(aq0, N, 3774);
        init(cu2, N, 58273);
        init(h2, N, 20546);
        init(q4, N, 2162);
        init(aa2, N, 26015);
        init(am, N, 49297);
        init(e, N, 7810);
        init(c0, N, 29000);
        init(qi, N, 31417);

        vm5 += qj[N - 5];
        int contr_1 = 11;
        while (--contr_1 > 0) {
            qj[N-1] -= qj[N-7];
            int contr_2 = 11;
            do {
                for (k = 1; k < N-5; ++k) {
                    qj[k] *= qj[k + 1];
                    for (int contr_3 : qj) {
                        qj[k] = vm5 - ((nu--) + (qj[k - 1]));
                        nu = -62683 - -45183;
                        if (qj[k - 1] > 0)
                            break;
                    }
                    qj[k + 1] -= qj[k];
                    qj[k] -= (qj[k])
                            - (((qj[k]) + ((qj[k]) + (17040 - (--nb)))) * (qj[k]));
                }
            } while (--contr_2 > 0);
        }

        res = vm5 + nu - nb;
        res += checkSum(qj, N) + checkSum(aq0, N) - checkSum(cu2, N)
                + checkSum(h2, N) - checkSum(q4, N) + checkSum(aa2, N)
                - checkSum(am, N) + checkSum(e, N) - checkSum(c0, N)
                + checkSum(qi, N);

        System.out.println("res = " + res);
    }

    public static void main(String[] args) {
            new Main().test();
    }
}
