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

// Generated by Dalvik Fuzzer tool (3.4.001). Sat Dec 20 06:20:05 2014
package OptimizationTests.regression.test934_2;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long u9s = -4167806156642576249L;
    public static double pj2 = 88.734;
    public static int g3 = -26299, i1a[] = new int[N];

    static {
        init(i1a, -15660);
    }

    public static long m9_check_sum = 0;
    public static long r_check_sum = 0;

    public static int m9(long hrm, short m) {

        long yq[][] = new long[N][N];
        byte ufd = -29;
        boolean ys = true;
        int nhr = -14886, ihv = -42798, rac = -27002, wb1 = 32779, bh = -29668, g = 9907, su[] = new int[N];

        init(su, -13470);
        init(yq, 5599280183647557633L);

        for (nhr = 1; nhr < 82; ++nhr) {
            ihv -= ((rac++) - (++su[(nhr >>> 1) % N]));
            ihv = (ihv++);
            rac = (int) (((hrm * -12205) - (29331 - (--ihv))) * (--su[nhr + 1]));
            for (wb1 = nhr; wb1 < 61; ++wb1) {
                pj2 = -94.678F;
                if (ys)
                    continue;
            }
            for (bh = 67; bh > nhr; bh--) {
                for (g = 1; g < 2; ++g) {
                    su[bh] += (ihv--);
                    su[bh] -= (int) (((-49434 - (54308 - (ufd - hrm))) - rac) - ((g3--) * wb1));
                    yq[g + 1][g] = rac;
                }
            }
        }
        long meth_res = hrm + m + nhr + ihv + rac + wb1 + (ys ? 1 : 0) + bh + g
                + ufd + checkSum(su) + checkSum(yq);
        m9_check_sum += meth_res;
        return (int) meth_res;
    }

    public static void r() {

        byte oq[] = new byte[N];
        long c[] = new long[N];
        int foi = 1145, hbt = 14691, h = 8371;
        short m4 = 16748;
        float sza = 30.203F;

        init(c, 8491241130948124587L);
        init(oq, (byte) -71);

        for (sza = 2; sza < 95; ++sza) {
            i1a[(int) (sza + 1)] *= (int) (-((g3 - sza) + sza));
            c[(int) (sza - 1)] &= oq[(-46562 >>> 1) % N];
            g3 += (int) (sza + m4);
        }
        foi = 1;
        while (++foi < 94) {
            i1a[foi] = g3;
            hbt = 1;
            while ((hbt += 2) < foi) {
                g3 = g3;
                g3 >>= (int) (pj2 * (-hbt));
                i1a[hbt] = (int) (((foi * m4) - u9s) - ((-7253643181052554146L + (--h)) + (m4 - g3)));
                pj2 *= g3;
                u9s += (m4 = (short) u9s);
            }
        }
        r_check_sum += Float.floatToIntBits(sza) + m4 + foi + hbt + h
                + checkSum(c) + checkSum(oq);
    }

    public static void main(String[] kn) {

        byte k = 114;
        long twj = 7649518289083536121L, x8s = 5439791634333313257L, fp = -5011546681196821321L, py[] = new long[N];
        int xd3 = 15659, e8 = 4829, jq6 = -7668, rj = -40531, e = -2758, n1w[] = new int[N];
        float qi1 = 60.237F;
        short i16 = -19073, eo[] = new short[N];

        init(n1w, 26048);
        init(eo, (short) -20047);
        init(py, -2458708468133671504L);

        for (xd3 = 5; xd3 < 89; ++xd3) {
            e8 += (xd3 - twj);
        }
        for (jq6 = 2; jq6 < 82; ++jq6) {
            e8 *= (int) (((-(e8--)) * m9(twj, i16)) - qi1);
            for (rj = jq6; rj < jq6; ++rj) {
                e8 += (int) (rj + (e8 = (int) (twj >> e8)));
                pj2 %= ((twj--) | 1);
            }
            qi1 = ((n1w[jq6] = (int) (jq6 - qi1)) + eo[jq6 - 1]);
            e8 = ((xd3 + (g3 + rj)) - g3);
            e = 1;
            while (++e < 68) {
                n1w[e + 1] = (int) ((g3 >>= (int) (--twj)) - -62);
                twj = (--g3);
                g3 *= (e8--);
                twj <<= (((twj - twj) - (jq6 - g3)) * (--py[jq6 - 1]));
                twj = (long) (g3 + (qi1 * (++g3)));
                k = (byte) (k - ((twj - qi1) + (rj * twj)));
                e8 += (e * e);
                twj >>= (long) (twj - -127.976);
                g3 += (e8--);
            }
            g3 = (int) x8s;
            fp >>= xd3;
            n1w[jq6 - 1] += e8;
            g3 = (int) ((g3++) * (fp + twj));
        }
        r();
        r();

        System.out.println("xd3 e8 twj = " + xd3 + "," + e8 + "," + twj);
        System.out.println("jq6 i16 qi1 = " + jq6 + "," + i16 + ","
                + Float.floatToIntBits(qi1));
        System.out.println("rj e k = " + rj + "," + e + "," + k);
        System.out.println("x8s fp n1w = " + x8s + "," + fp + ","
                + checkSum(n1w));
        System.out.println("eo py = " + checkSum(eo) + "," + checkSum(py));

        System.out.println("pj2 g3 u9s = " + Double.doubleToLongBits(pj2) + ","
                + g3 + "," + u9s);
        System.out.println("i1a = " + checkSum(i1a));

        System.out.println("m9_check_sum: " + m9_check_sum);
        System.out.println("r_check_sum: " + r_check_sum);
    }
}
