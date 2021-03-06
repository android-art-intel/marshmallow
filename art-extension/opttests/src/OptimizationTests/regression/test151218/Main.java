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

// Generated by Dalvik Fuzzer tool (2.1.003). Sun Nov 10 12:39:28 2013
package OptimizationTests.regression.test151218;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 30;

    public static long u8_check_sum = 0;

    public static long u8(int x1, int vg, long o2, short g5b) {

        long ba0[] = new long[N];
        int h = -55633, f = -52110, cl = -3561, n[] = new int[N];

        init(n, 32609);
        init(ba0, -8336083535082777051L);

        if (n[13] != ba0[24]) {
            for (h = 1; h < 21; ++h) {
                x1 -= (int) (++f);
            }
            for (cl = 1; cl < 21; cl++) {
                int zf = 11;
                do {
                    int u = 11;
                    do {
                        vg += (int) (ba0[cl]);
                    } while (--u > 0);
                } while (--zf > 0);
            }
            return (long) (x1 + vg + o2 + g5b + h + f + cl + checkSum(n) + checkSum(ba0));
        }
        long meth_res = x1 + vg + o2 + g5b + h + f + cl + checkSum(n)
                + checkSum(ba0);
        u8_check_sum += meth_res;
        return (long) meth_res;
    }

    public static void main(String[] rfm) {

        short o7i[] = new short[N];
        char xcn[] = new char[N];
        long hf[] = new long[N], aov[][] = new long[N][N], wp0[] = new long[N];
        int nt = -49138, nv0 = -19864, ag3 = 51514, i6x = 10575, p = -57372, hj = -22023, w = 3946, bo3 = -59274, y3 = 52525, k8 = -39921, e = 40083, z94[][] = new int[N][N], t43[][] = new int[N][N], n2[] = new int[N], zbk[] = new int[N], c4l[] = new int[N];

        init(xcn, (char) 43384);
        init(z94, 55814);
        init(t43, 21216);
        init(n2, -53714);
        init(hf, -1289766965484199734L);
        init(zbk, -53396);
        init(c4l, -26793);
        init(aov, 3708325538228655950L);
        init(o7i, (short) 32397);
        init(wp0, -6113058638161746228L);

        int d = 11;
        while (--d > 0) {
            int i = 11;
            while (--i > 0) {
                for (nt = 1; nt < 8; nt++) {
                    nv0 -= (int) (xcn[nt - 1]);
                    nv0 -= (int) (z94[nt][nt]);
                    t43[nt + 1][nt - 1] *= (int) (n2[nt]);
                    hf[nt] = (long) (u8((int) (nv0 - (ag3++)),
                            (int) ((zbk[nt]) + (t43[nt][nt])),
                            (long) (c4l[nt]),
                            (short) ((xcn[nt]) + (xcn[nt - 1]))));
                    i6x -= (int) ((n2[nt]) + (z94[nt][nt + 1]));
                }
            }
        }

        for (p = 1; p < 22; ++p) {
            for (hj = p; hj < 6; hj++) {
                for (w = 4; w > p; w--) {
                    aov[p + 1][w] -= (long) (--bo3);
                }
            }
            y3 *= (int) (w - (zbk[p - 1]));
        }
        for (k8 = 1; k8 < 22; k8++) {
            bo3 *= (int) (wp0[k8]);
        }

        System.out.println("long: " + checkSum(hf) + "," + checkSum(aov) + ","
                + checkSum(wp0));
    }
}
