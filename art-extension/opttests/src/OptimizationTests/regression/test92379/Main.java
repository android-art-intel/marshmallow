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

package OptimizationTests.regression.test92379;

public class Main {

    public static final int N = 100;

    private static void init(int[] a, int n, int seed) {
        for (int j = 0; j < n; j++) {
            a[j] = ((j % 2 == 0) ? seed + j : seed - j) % 65535;
        }
    }

    private static int checkSum(int[] a, int n) {
        int j, sum = 0;
        for (j = 0; j < n; j++) {
            sum += ((j % 2 == 0) ? a[j] : -a[j]);
        }
        return sum;
    }

    public static void main(String[] args) {
        int jq = 2746, i = -40406, m = -33738, k = -43340, kl = 15815, kz4 = 4455, iz = 23071, r = -17122, nt = 52938, nd = -63838, jb2 = 29535, jq4 = -37388, t = 4944, k2 = 17113, kn5 = -12085, res = 59988;

        int yn4[] = new int[N], cf[] = new int[N], j[] = new int[N], jt[] = new int[N], xa[] = new int[N], go8[] = new int[N], qt[] = new int[N], fw[] = new int[N], ix[] = new int[N], o[] = new int[N], oq6[] = new int[N], gw3[] = new int[N], yw[] = new int[N];

        init(yn4, N, 7151);
        init(cf, N, 11272);
        init(xa, N, 23238);
        init(go8, N, 35596);
        init(j, N, 50869);
        init(o, N, 45333);
        init(oq6, N, 59414);
        init(yw, N, 3835);
        init(qt, N, 47699);
        init(fw, N, 40758);
        init(ix, N, 31481);
        init(gw3, N, 53375);
        init(jt, N, 41287);

        for (k = 1; k < 75; k++) {
            for (kl = 13; kl > 1; kl--) {
                for (kz4 = kl; kz4 < kl; ++kz4) {
                    jt[kl - 1] -= ((yn4[kz4]) + (((cf[kz4 - 1]) - (((jq * k) - jq) / (((cf[kz4 - 1])
                            + -23075 != 0) ? (cf[kz4 - 1]) + -23075 : 1))) / (((yn4[kz4 - 1])
                            * i != 0) ? (yn4[kz4 - 1]) * i : 1)))
                            - kz4;
                }
            }

            if (k != 15) {
                try {
                    try {
                        j[k - 1] = 9674 + (m - (yn4[k]));
                        go8[k - 1] += (j[k]) % ((i != 0) ? i : 1);
                        for (iz = 21; iz > 1; --iz) {
                            r = (j[iz - 1]) % ((kz4 != 0) ? kz4 : 1);
                            try {
                                int contr_1 = 11;
                                do {
                                    int contr_2 = 11;
                                    do {
                                        try {
                                            if (yw[iz + 1] < 65536)
                                                throw new ArithmeticException();
                                        } catch (ArithmeticException exc_3) {
                                            qt[iz + 1] += ((cf[iz - 1]) + (fw[iz]))
                                                    * (((ix[iz]) * (o[iz - 1])) / ((iz != 0) ? iz
                                                            : 1));
                                            jt[iz] *= (j[iz - 1]) - 50219;
                                        } finally {
                                            yn4[iz - 1] += k
                                                    / ((i != 0) ? i : 1);
                                        }
                                    } while (--contr_2 > 0);
                                } while (--contr_1 > 0);
                            } catch (ArithmeticException exc_9) {
                                int contr_3 = 11;
                                do {
                                    j[iz] -= (o[iz - 1])
                                            - ((jt[iz + 1]) + (gw3[iz - 1]));
                                } while (--contr_3 > 0);
                            } finally {
                                nd -= fw[iz - 1];
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException exc_11) {
                        qt[k] = gw3[k];
                    } catch (NullPointerException exc_12) {
                        qt[k + 1] = (((j[k]) % ((4997 != 0) ? 4997 : 1)) + (yn4[k - 1]))
                                * jq4;
                        t += (o[k - 1])
                                - ((gw3[k]) % ((oq6[k] != 0) ? oq6[k] : 1));
                    } finally {
                        for (k2 = 1; k2 < 8; ++k2) {
                            qt[k2] = -cf[k2 - 1];
                            yn4[k2] = qt[k2 + 1] - j[k2 + 1];
                        }
                    }
                } finally {
                    int contr_8 = 11;
                    do {
                        oq6[k + 1] = qt[k - 1];
                    } while (--contr_8 > 0);
                }
            }
        }

        res = m + k - kl + kz4 - r + nt - nd + jq4 - t;
        res += checkSum(yn4, N) - checkSum(cf, N) + checkSum(xa, N)
                - checkSum(go8, N) + checkSum(j, N) + checkSum(o, N)
                - checkSum(oq6, N) - checkSum(yw, N) + checkSum(qt, N)
                - checkSum(fw, N) + checkSum(ix, N) - checkSum(gw3, N)
                + checkSum(jt, N);

        System.out.println("res = " + res);
    }
}
