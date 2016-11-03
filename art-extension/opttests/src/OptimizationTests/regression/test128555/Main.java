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

// Generated by Dalvik Fuzzer tool (2.1.003). Sat Aug  3 18:16:30 2013
package OptimizationTests.regression.test128555;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 30;

    public static long q_check_sum = 0;

    public static int q(int rp, long ir, long q7, int cv) {

        int f8p = 39846, v = -41169, tl = 36012;
        long ra = -7731227427443751092L, u[][] = new long[N][N], p[] = new long[N];

        init(u, 3811754949291432803L);
        init(p, -1307793973989041469L);

        for (f8p = 1; f8p < 10; f8p++) {
            u[f8p][f8p + 1] = (long) (p[f8p + 1]);
        }
        for (v = 1; v < 22; ++v) {

            for (tl = 6; tl > 1; --tl) {
                if (ra > 0) {
                    return (int) (rp + ir + q7 + cv + f8p + v + tl + ra
                            + checkSum(u) + checkSum(p));
                }
                int jk = 11;
                do {
                    p[tl] += (long) (p[v]);
                } while (--jk > 0);
            }
        }

        long meth_res = rp + ir + q7 + cv + f8p + v + tl + ra + checkSum(u)
                + checkSum(p);
        q_check_sum += meth_res;
        return (int) meth_res;
    }

    public static void main(String[] sl) {

        int xnh = -32849, of = 10732, ojs = 28314, qng = -38322, vsv = 42823, r = -7313, uq = -38986, be[][] = new int[N][N], i[] = new int[N], rg[][] = new int[N][N], d[][] = new int[N][N], y[] = new int[N], r8i[] = new int[N], xr[][] = new int[N][N], y5q[] = new int[N], gj9[][] = new int[N][N], m[] = new int[N], npd[][] = new int[N][N];
        short krg[] = new short[N];
        byte tu[] = new byte[N], uw[] = new byte[N];
        long t = -6286128324357981898L, cab[][] = new long[N][N];

        init(be, -40318);
        init(i, 22080);
        init(rg, 53401);
        init(d, -20757);
        init(y, 3071);
        init(r8i, -53936);
        init(tu, (byte) 97);
        init(xr, -8668);
        init(y5q, 49379);
        init(gj9, -53561);
        init(m, 11761);
        init(npd, 10653);
        init(uw, (byte) -36);
        init(cab, 2309843958107815615L);
        init(krg, (short) -5558);

        for (xnh = 1; xnh < 7; xnh += 2) {
            if ((be[xnh + 1][xnh])
                    * (q((int) ((6961197543202237031L) - (i[xnh - 1])),
                            (long) (i[xnh]), (long) (23005 - (-40320)),
                            (int) (-(rg[xnh][xnh])))) < d[xnh - 1][xnh]) {
                y[xnh - 1] += (int) ((i[xnh + 1]) - (q((int) (of++),
                        (long) (r8i[xnh - 1]), (long) ((of) + -18602),
                        (int) (tu[xnh]))));
                t = (long) (xr[xnh + 1][xnh + 1]);
                t = (long) (((rg[xnh - 1][xnh]) * t) - (r8i[xnh]));
            }
        }

        for (ojs = 0; ojs > 1; --ojs) {
            t *= (long) (rg[ojs][ojs - 1]);
        }

        int e = 11;
        while (--e > 0) {
            for (qng = 1; qng < 11; qng++) {
                of = (int) (ojs * (q(
                        (int) ((y5q[qng + 1]) - (gj9[qng - 1][qng - 1])),
                        (long) (rg[qng - 1][qng + 1]), (long) (tu[qng - 1]),
                        (int) ((m[qng + 1]) * (npd[qng - 1][qng + 1])))));
                xr[qng][qng - 1] += (int) (-(uw[qng + 1]));
                r8i[qng] += (int) (3943 * ojs);
                cab[qng + 1][qng - 1] += (long) (d[qng][qng - 1]);
            }
        }

        for (vsv = 1; vsv < 2; ++vsv) {
            for (r = 1; r < vsv; ++r) {
                t *= (long) ((krg[vsv]) - (--uq));
            }
        }

        System.out.println("int: " + checkSum(y));
        System.out.println("long: " + t + "," + checkSum(cab));
        System.out.println("q_check_sum: " + q_check_sum);
    }
}