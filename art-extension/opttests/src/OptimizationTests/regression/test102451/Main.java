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

package OptimizationTests.regression.test102451;

public class Main {

    public static final int N = 100;

    private static void init(int[] a, int n, int seed) {
        for (int j = 0; j < n; j++) {
            a[j] = ((j % 2 == 0) ? seed + j : seed - j) % 65535;
        }
    }

    private static void init(int[][] a, int n, int seed) {
        for (int j = 0; j < a.length; j++) {
            init(a[j], a[j].length, seed);
        }
    }

    private static int checkSum(int[] a, int n) {
        int sum = 0;
        for (int j = 0; j < n; j++) {
            sum += ((j % 2 == 0) ? a[j] : -a[j]);
        }
        return sum;
    }

    private static int checkSum(int[][] a, int n) {
        int sum = 0;
        for (int j = 0; j < a.length; j++) {
            sum += checkSum(a[j], a[j].length);
        }
        return sum;
    }

    public static void main(String[] args) {
        int co = -5562, j = -53750, n1 = 22750, x8 = 54702, k3 = -19989, k4 = -49759, k1 = -26784, go = 7540, res = -13711;
        int ip[] = new int[N], yp[] = new int[N], xx[][] = new int[N][N], e[] = new int[N], mc[] = new int[N];

        init(ip, N, 57762);
        init(yp, N, 7861);
        init(xx, N * N, 59413);
        init(e, N, 50996);
        init(mc, N, 5094);

        for (int contr_2 : ip) {

            for (j = 1; j < 45; j++) {
                co += n1--;
                x8 = (yp[j - 1]) / 28037;

                for (k3 = 1; k3 < 17; k3++) {

                    for (k4 = 16; k4 > k3; --k4) {
                        yp[k4] = yp[k4 - 1];
                        if (n1 > 0)
                            continue;
                        ip[k4 + 1] += (xx[k3 - 1][k3 + 1])
                                / (((xx[k3][k3 + 1]) - co != 0) ? (xx[k3][k3 + 1])
                                        - co
                                        : 1);
                    }
                    co -= ip[k3];
                }

                ip[j] -= e[j];

                for (k1 = 41; k1 > 1; k1--) {
                    go = k4 / ((k4 != 0) ? k4 : 1);
                    int contr_3 = 11;
                    while (--contr_3 > 0) {

                        if (-62581 + co < -((mc[k1 - 1]) % ((-51136 != 0) ? -51136
                                : 1))) {
                            if (yp[k1] > 0)
                                break;

                            xx[k1][j] = e[k1];
                        }
                    }
                }
            }
        }

        res = co + n1 - x8 + go;
        res += checkSum(ip, N) + checkSum(yp, N) - checkSum(xx, N * N)
                + checkSum(e, N) - checkSum(mc, N);

        System.out.println("res = " + res);
    }

}
