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

package OptimizationTests.regression.test101315;

class UserDefinedException extends RuntimeException {
    public int field;
}

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
        int j = 20147, j8 = -25954, c = 6163, n = 36264, nm = 5815, ny = 47554, nl = 55365, nn8 = -50910, jt = -40548, jz = -42772, no = 31595, xc = 57200, nb4 = 53120, ng2 = 45318, ja = 57376, iq = -12754, nx1 = -31322, ns9 = -55327, ng = 41496, nz = -30984, nv = -27709, kj8 = -25800, q = -40252, nn = -7412, np4 = 51126, np = 58811, nx7 = 4702, nc8 = 64048, nf = 25802, nh8 = 64368, res = 50822;

        int jm[] = new int[N], t[][] = new int[N][N], rk[] = new int[N], og[][] = new int[N][N], la[] = new int[N], de1[] = new int[N], xz[] = new int[N], oe[][] = new int[N][N], xq[] = new int[N], p[][] = new int[N][N], u[] = new int[N], hd[] = new int[N], xv[] = new int[N], ms[][] = new int[N][N], hs[] = new int[N], kt[][] = new int[N][N], zd[] = new int[N], z[][] = new int[N][N], sy[] = new int[N], vk3[] = new int[N], x[][] = new int[N][N], rl[] = new int[N], fd[] = new int[N], sc1[][] = new int[N][N], e8[][] = new int[N][N], l[][] = new int[N][N], si[] = new int[N], i[][] = new int[N][N], b3[] = new int[N], cz1[] = new int[N], wl[] = new int[N], ou[][] = new int[N][N], zv[][] = new int[N][N], dl[] = new int[N], v[] = new int[N], r[] = new int[N], f[][] = new int[N][N], ef[][] = new int[N][N], zq[] = new int[N], d[][] = new int[N][N], yu9[][] = new int[N][N], kh[] = new int[N], dc1[] = new int[N], cj4[][] = new int[N][N], w[][] = new int[N][N];

        init(jm, N, 20887);
        init(t, N * N, 18469);
        init(rk, N, 43463);
        init(og, N * N, 5713);
        init(la, N, 23354);
        init(de1, N, 17250);
        init(xz, N, 20341);
        init(oe, N * N, 39585);
        init(xq, N, 41734);
        init(p, N * N, 53701);
        init(u, N, 48115);
        init(hd, N, 39211);
        init(xv, N, 17030);
        init(ms, N * N, 42644);
        init(hs, N, 61732);
        init(kt, N * N, 10244);
        init(zd, N, 38860);
        init(z, N * N, 30318);
        init(sy, N, 28902);
        init(vk3, N, 54832);
        init(x, N * N, 61657);
        init(rl, N, 56234);
        init(fd, N, 36800);
        init(sc1, N * N, 7775);
        init(e8, N * N, 39679);
        init(l, N * N, 50062);
        init(si, N, 37635);
        init(i, N * N, 24927);
        init(b3, N, 27491);
        init(cz1, N, 29465);
        init(wl, N, 18888);
        init(ou, N * N, 33826);
        init(zv, N * N, 36375);
        init(dl, N, 20136);
        init(v, N, 1393);
        init(r, N, 62446);
        init(f, N * N, 14620);
        init(ef, N * N, 50959);
        init(zq, N, 4047);
        init(d, N * N, 49326);
        init(yu9, N * N, 2234);
        init(kh, N, 31200);
        init(dc1, N, 52457);
        init(cj4, N * N, 49139);
        init(w, N * N, 19213);

        for (j = 68; j > 1; j--) {

            for (j8 = 36; j8 > 1; j8--) {
                jm[j8] = jm[j8];
                t[j - 1][j8 + 1] += (jm[j8]) / ((rk[j8] != 0) ? rk[j8] : 1);

                try {
                    rk[j8] += t[j - 1][j + 1];
                    c -= n--;
                    jm[j8 - 1] -= nm++;
                } catch (UserDefinedException exc_1) {
                    int[] exc_2;
                    int exc_3 = -10;

                    try {
                        if ((og[j8 - 1][j8 + 1]) % ((rk[j8] != 0) ? rk[j8] : 1) < (t[j8][j8])
                                - (t[j8 - 1][j8 + 1])) {

                            for (jt = 8; jt > 1; --jt) {
                                for (jz = jt; jz < j8; jz++) {
                                    oe[jz - 1][jt - 1] += de1[jz + 1];
                                }
                            }

                            try {
                                int[] exc_4;
                                int exc_5 = -10;
                                try {
                                    xc -= (xc - (jm[j8 + 1]))
                                            % ((rk[j8] != 0) ? rk[j8] : 1);
                                    exc_4 = new int[exc_5];
                                    int[] exc_6;
                                    int exc_7 = -10;
                                    try {
                                        if ((--nb4) + ny < jm[j8 - 1]) {
                                            de1[j8 + 1] -= --ng2;
                                            for (ja = j; ja < j8; ja++) {
                                                for (iq = 1; iq < j; iq++) {
                                                    oe[iq + 1][ja + 1] = nx1--;
                                                }
                                            }
                                        } else {
                                            int exc_8 = 0;
                                            try {
                                                ns9 = nb4 / exc_8;
                                            } catch (ArrayIndexOutOfBoundsException exc_9) {
                                                de1[j8 - 1] = ++ng;
                                            } catch (ArithmeticException exc_10) {
                                                og[j + 1][j - 1] = (u[j8 + 1])
                                                        * (((hd[j8 + 1]) / ((ja != 0) ? ja
                                                                : 1)) + ng);
                                                u[j8] -= de1[j8];
                                            } finally {
                                                xv[j8 - 1] *= nz--;
                                            }
                                        }
                                        exc_6 = new int[exc_7];
                                    } catch (ArithmeticException exc_11) {
                                        jm[j8 + 1] -= (xz[j8 - 1])
                                                / ((hd[j8 - 1] != 0) ? hd[j8 - 1]
                                                        : 1);
                                        ms[j8][j - 1] = de1[j8 + 1];
                                    } catch (NullPointerException exc_12) {
                                        hs[j8 + 1] = hd[j8];
                                        kt[j8 - 1][j + 1] = ++nv;
                                    } catch (NegativeArraySizeException exc_13) {
                                        t[j8][j + 1] -= oe[j8 + 1][j8 - 1];
                                    }
                                } catch (ArrayIndexOutOfBoundsException exc_14) {
                                    jm[j8 - 1] = ((zd[j8]) + 34506)
                                            + (og[j8 - 1][j8]);
                                    for (kj8 = 1; kj8 < 18; ++kj8) {
                                        oe[kj8][j8] += ((xq[kj8]) * -20974)
                                                / ((zd[kj8] != 0) ? zd[kj8] : 1);
                                    }
                                } catch (NegativeArraySizeException exc_15) {
                                    rl[j8 - 1] += xz[j8 - 1];
                                } catch (NullPointerException exc_16) {
                                    hd[j8 + 1] *= ++np4;
                                }
                                if (la[j8 - 1] < 65536)
                                    throw new UserDefinedException();
                                xv[j8 - 1] += la[j8 - 1];
                            } catch (UserDefinedException exc_17) {
                                e8[j + 1][j8] = ((n + (zd[j8 - 1])) + (rl[j8 - 1]))
                                        % ((nn8 != 0) ? nn8 : 1);
                                l[j8][j] *= -58916
                                        / (((si[j8 + 1]) + jt != 0) ? (si[j8 + 1])
                                                + jt
                                                : 1);
                            } finally {
                                ng2 = (la[j8 + 1]) - -45740;
                                hs[j8 - 1] += rl[j8];
                            }
                        }
                        exc_2 = new int[exc_3];
                    } catch (ArrayIndexOutOfBoundsException exc_18) {
                        i[j][j + 1] = (b3[j8])
                                * ((t[j][j - 1]) % (((cz1[j8 + 1])
                                        % ((wl[j8 - 1] != 0) ? wl[j8 - 1] : 1) != 0) ? (cz1[j8 + 1])
                                        % ((wl[j8 - 1] != 0) ? wl[j8 - 1] : 1)
                                            : 1));
                    } catch (ArithmeticException exc_19) {
                        zq[j8] += nx1 + ny;
                        ef[j8][j8] += si[j8];
                    } catch (NegativeArraySizeException exc_20) {
                        p[j - 1][j8] = zq[j8 - 1];
                        z[j][j] -= iq + -26537;
                    } finally {
                        n -= i[j][j8 - 1];
                    }
                    de1[j8 - 1] = ((nx7++) * (++nc8))
                            - ((++nf) * ((nh8--) + (kh[j8 + 1])));
                } finally {
                    dc1[j8] -= xz[j8 - 1];
                    v[j8 - 1] -= ((w[j + 1][j8 - 1]) % ((rl[j8 - 1] != 0) ? rl[j8 - 1]
                            : 1)) - 42555;
                }
            }
        }

        res = j + c - n + nm - ny + nl - nn8 + no - xc + nb4 - ng2 + ja - nx1
                + ns9 - ng + nz - nv + q - nn + np4 - np + nx7 - nc8 + nf - nh8;
        res += checkSum(jm, N) + checkSum(t, N * N) - checkSum(rk, N)
                + checkSum(og, N * N) - checkSum(la, N) + checkSum(de1, N)
                - checkSum(xz, N) + checkSum(oe, N * N) - checkSum(xq, N)
                + checkSum(p, N * N) - checkSum(u, N) + checkSum(hd, N)
                - checkSum(xv, N) + checkSum(ms, N * N) - checkSum(hs, N)
                + checkSum(kt, N * N) - checkSum(zd, N) + checkSum(z, N * N)
                - checkSum(sy, N) + checkSum(vk3, N) - checkSum(x, N * N)
                + checkSum(rl, N) - checkSum(fd, N) + checkSum(sc1, N * N)
                - checkSum(e8, N * N) + checkSum(l, N * N) - checkSum(si, N)
                + checkSum(i, N * N) - checkSum(b3, N) + checkSum(cz1, N)
                - checkSum(wl, N) + checkSum(ou, N * N) - checkSum(zv, N * N)
                + checkSum(dl, N) - checkSum(v, N) + checkSum(r, N)
                - checkSum(f, N * N) + checkSum(ef, N * N) - checkSum(zq, N)
                + checkSum(d, N * N) - checkSum(yu9, N * N) + checkSum(kh, N)
                - checkSum(dc1, N) + checkSum(cj4, N * N) - checkSum(w, N * N);

        System.out.println("res = " + res);
    }
}
