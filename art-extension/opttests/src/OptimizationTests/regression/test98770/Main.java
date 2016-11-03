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

package OptimizationTests.regression.test98770;

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
        int k = -45333, o = 56479, rx = 32031, i6 = 33455, kk = -35551, j5 = 16756, f = 14177, nf = 12809, nd = 50244, ii6 = -20155, j = 14507, nt = -61920, nh = -13187, n5 = 33010, nk7 = 40311, sh = -39918, nc = 23947, jr = -23131, kv2 = -42249, k4 = 53749, nv = -27978, nv2 = 16614, nu7 = 25774, nq4 = 59599, mj = -31648, wc7 = -35176, uw = -29319, jo = 62595, res = 53596;

        int n[] = new int[N], i[] = new int[N], t6[] = new int[N], y[] = new int[N], qg5[][] = new int[N][N], b[][] = new int[N][N], yk[] = new int[N], w[][] = new int[N][N], r[] = new int[N], y5[][] = new int[N][N], ox0[][] = new int[N][N], en[] = new int[N], co7[][] = new int[N][N], ec[][] = new int[N][N], z[][] = new int[N][N], ls[][] = new int[N][N], yd[] = new int[N], g[][] = new int[N][N], kg[] = new int[N], oz6[] = new int[N], mr[] = new int[N], d8[][] = new int[N][N], hg6[][] = new int[N][N], mc[] = new int[N], i1[] = new int[N], yl5[] = new int[N], yo[] = new int[N], ag8[] = new int[N], qa7[] = new int[N], ay[] = new int[N];

        init(n, N, 51557);
        init(i, N, 3548);
        init(t6, N, 15264);
        init(y, N, 52502);
        init(qg5, N * N, 46312);
        init(b, N * N, 65128);
        init(yk, N, 22412);
        init(w, N * N, 55026);
        init(r, N, 25693);
        init(y5, N * N, 61754);
        init(ox0, N * N, 46923);
        init(en, N, 65058);
        init(co7, N * N, 33253);
        init(ec, N * N, 60691);
        init(z, N * N, 49556);
        init(ls, N * N, 763);
        init(yd, N, 34848);
        init(g, N * N, 63666);
        init(kg, N, 41682);
        init(oz6, N, 5252);
        init(mr, N, 13903);
        init(d8, N * N, 23262);
        init(hg6, N * N, 21305);
        init(mc, N, 22731);
        init(i1, N, 2089);
        init(yl5, N, 32666);
        init(yo, N, 56553);
        init(ag8, N, 36444);
        init(qa7, N, 11734);
        init(ay, N, 35479);

        for (k = 94; k > 2; --k) {
            n[k - 1] = n[k];
        }

        o += n[20];
        rx = i[41];
        i6 -= i[5];
        int exc_1 = 0;

        try {
            for (kk = 1; kk < 22; kk++) {
                for (j5 = 1; j5 < 2; j5++) {
                    f -= t6[j5 - 1];
                    t6[j5] += (nf--) - 44780;
                    try {
                        try {
                            i[j5] *= n[j5 + 1];
                            i[102] = i[j5 - 1];
                            t6[j5] -= ++nd;
                            for (ii6 = 9; ii6 > 1; ii6--) {
                                for (j = 9; j > 1; j--) {
                                    try {
                                        i[j + 1] *= i[j + 1];
                                        y[j] *= qg5[ii6 - 1][ii6 + 1];
                                        i[j] -= y[j];
                                    } catch (ArrayIndexOutOfBoundsException exc_4) {
                                        y[j] = -((i[j]) / (((nd + (i[j + 1]))
                                                / ((k != 0) ? k : 1) != 0) ? (nd + (i[j + 1]))
                                                / ((k != 0) ? k : 1)
                                                : 1));
                                    } catch (NullPointerException exc_5) {
                                        qg5[ii6][j + 1] = (n[j + 1])
                                                * ((b[ii6][j + 1]) / ((yk[j] != 0) ? yk[j]
                                                        : 1));
                                        try {
                                            yk[j] *= (((58568 / ((yk[j + 1] != 0) ? yk[j + 1]
                                                    : 1)) % ((t6[j] != 0) ? t6[j]
                                                    : 1)) - (kk - (nf / ((b[ii6 + 1][j] != 0) ? b[ii6 + 1][j]
                                                    : 1))))
                                                    / (((b[j + 1][ii6 - 1])
                                                            - (yk[j + 1]) != 0) ? (b[j + 1][ii6 - 1])
                                                            - (yk[j + 1])
                                                            : 1);
                                            y[j - 1] = ++nt;
                                        } catch (ArithmeticException exc_6) {
                                            o += nh++;
                                            nf = f - (qg5[ii6 - 1][ii6 - 1]);
                                        } catch (ArrayIndexOutOfBoundsException exc_7) {
                                            nt = --nk7;
                                            sh *= ((i6 + (n[j + 1])) / ((w[j][ii6 + 1] != 0) ? w[j][ii6 + 1]
                                                    : 1))
                                                    % ((yk[j] != 0) ? yk[j] : 1);
                                        } finally {
                                            w[j - 1][ii6 + 1] *= ((++nc) + (yk[j - 1]))
                                                    * -38081;
                                        }
                                    }
                                }
                                for (jr = 9; jr > 1; jr--) {
                                }
                            }
                            for (kv2 = 9; kv2 > 1; --kv2) {
                            }
                        } catch (ArrayIndexOutOfBoundsException exc_8) {
                            for (k4 = kk; k4 > j5; k4--) {
                            }
                            nk7 = n[j5 - 1];
                        } catch (NullPointerException exc_9) {
                            t6[j5] *= (((-16797 / ((k4
                                    + ((y5[j5 + 1][kk - 1]) - (64949 + 45423)) != 0) ? k4
                                    + ((y5[j5 + 1][kk - 1]) - (64949 + 45423))
                                        : 1)) % ((kk != 0) ? kk : 1)) - (ox0[kk][j5]))
                                    * (nv--);
                            n5 += --nv2;
                        } catch (ArithmeticException exc_10) {
                            w[j5 - 1][kk] -= r[j5 + 1];
                        } finally {
                            co7[kk][kk - 1] = (yd[j5])
                                    * ((((g[j5 - 1][j5]) / (((kg[j5])
                                            / ((-24638
                                                    / ((nd - (oz6[j5]) != 0) ? nd
                                                            - (oz6[j5])
                                                            : 1) != 0) ? -24638
                                                    / ((nd - (oz6[j5]) != 0) ? nd
                                                            - (oz6[j5])
                                                            : 1)
                                                    : 1) != 0) ? (kg[j5])
                                            / ((-24638
                                                    / ((nd - (oz6[j5]) != 0) ? nd
                                                            - (oz6[j5])
                                                            : 1) != 0) ? -24638
                                                    / ((nd - (oz6[j5]) != 0) ? nd
                                                            - (oz6[j5])
                                                            : 1)
                                                    : 1)
                                                : 1)) + (((mr[j5 - 1]) / ((yk[j5 + 1] != 0) ? yk[j5 + 1]
                                            : 1)) - jr)) - (d8[kk - 1][kk]));
                        }
                        hg6[kk + 1][kk] *= nq4++;
                    } catch (NullPointerException exc_11) {
                        ec[kk - 1][j5 + 1] -= mc[j5];
                        nu7 += (mc[j5 - 1]) + ii6;
                    }
                }
            }
            mj = wc7 / exc_1;
        } catch (ArithmeticException exc_12) {
            uw *= ((i1[85]) - nh)
                    * ((((((62195 % ((yl5[94] != 0) ? yl5[94] : 1)) + (yo[13])) % ((nh != 0) ? nh
                            : 1)) / ((nu7 != 0) ? nu7 : 1)) + 35950) % ((ag8[66] != 0) ? ag8[66]
                            : 1));
        } finally {
            rx -= ((((qa7[35]) + (y[74])) % ((ay[13] != 0) ? ay[13] : 1)) + -4415)
                    % ((-57137 != 0) ? -57137 : 1);
            for (jo = 1; jo < 48; jo += 3) {
            }
        }

        res = o + rx - i6 + kk - f + nf - nd + nt - nh + n5 - nk7 + sh - nc
                + nv - nv2 + nu7 - nq4 + mj - wc7 + uw;
        res += checkSum(n, N) + checkSum(i, N) - checkSum(t6, N)
                + checkSum(y, N) - checkSum(qg5, N * N) + checkSum(b, N * N)
                - checkSum(yk, N) + checkSum(w, N * N) - checkSum(r, N)
                + checkSum(y5, N * N) - checkSum(ox0, N * N) + checkSum(en, N)
                - checkSum(co7, N * N) + checkSum(ec, N * N)
                - checkSum(z, N * N) + checkSum(ls, N * N) - checkSum(yd, N)
                + checkSum(g, N * N) - checkSum(kg, N) + checkSum(oz6, N)
                - checkSum(mr, N) + checkSum(d8, N * N) - checkSum(hg6, N * N)
                + checkSum(mc, N) - checkSum(i1, N) + checkSum(yl5, N)
                - checkSum(yo, N) + checkSum(ag8, N) - checkSum(qa7, N)
                + checkSum(ay, N);

        System.out.println("res = " + res);
    }

}
