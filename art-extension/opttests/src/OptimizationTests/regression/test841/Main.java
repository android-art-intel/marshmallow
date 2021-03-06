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

// Generated by Dalvik Fuzzer tool (3.4.001). Sun Nov  9 11:44:46 2014
package OptimizationTests.regression.test841;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static byte rm = 0;
    public static double j9 = 70.182;

    public static long x_check_sum = 0;
    public static long oni_check_sum = 0;

    public static int x() {

        long mdg = 2145480762340946134L;
        int mzm = -34843, t1 = -56967, j = -51498, mdb = 25965, f[] = new int[N];
        float z = -82.616F;
        double wb = 79.993;

        init(f, -20939);

        for (mzm = 1; mzm < 87; ++mzm) {
            wb = mdg;
        }
        for (t1 = 5; t1 < 94; t1++) {
            wb *= (-mdg);
            j = (int) (rm + (++mdg));
            j = (int) (-(mdg++));
            j &= (int) ((++z) + ((2235418075691667163L + wb) + j));
            z *= (f[t1]++);
        }
        for (mdb = 2; mdb < 85; ++mdb) {
            z -= j;
            wb = (-5679871793758537847L * ((j - j) + (++j)));
        }
        j = (int) (z - (mdg += (long) (mdg * wb)));
        long meth_res = mzm + Double.doubleToLongBits(wb) + mdg + t1 + j
                + Float.floatToIntBits(z) + mdb + checkSum(f);
        x_check_sum += meth_res;
        return (int) meth_res;
    }

    public static double oni() {

        long n6 = -8507920823997900340L, p[] = new long[N];
        int vns = -39305, suv = -47258, c1h = 50769, wt4[] = new int[N];
        short xy9[] = new short[N];
        double u3a[] = new double[N];
        float wi3 = -82.215F;

        init(wt4, 13989);
        init(u3a, 53.13);
        init(p, 8548576764006706711L);
        init(xy9, (short) -31307);

        for (vns = 2; vns < 82; vns++) {
            n6 = (long) ((--wi3) + (-(vns - 32616)));
            suv -= (int) (n6 + vns);
            suv = (int) ((j9--) + (n6 += (wt4[vns + 1]++)));
        }
        c1h = 1;
        do {
            suv -= (int) j9;
        } while (++c1h < 80);
        for (double ix : u3a) {
            suv *= (int) p[(suv >>> 1) % N];
            n6 += -3483086988293713764L;
            suv -= (int) (n6--);
            xy9[(20987 >>> 1) % N] = (short) vns;
            wt4[(c1h >>> 1) % N] = (int) n6;
        }
        long meth_res = vns + n6 + Float.floatToIntBits(wi3) + suv + c1h
                + checkSum(wt4) + Double.doubleToLongBits(checkSum(u3a))
                + checkSum(p) + checkSum(xy9);
        oni_check_sum += meth_res;
        return (double) meth_res;
    }

    public static void main(String[] vy5) {

        long zxy = 7945928051618626975L, c2s = -8217591962427947742L;
        int gi = -61821, o7 = 22097, b = -39393, yhe = 18735, d = -24296, ly = -12992, lh = 29515, dhb[] = new int[N];
        short w = 31089;
        float n = -45.232F;
        boolean zas = true, igd[] = new boolean[N];

        init(igd, false);
        init(dhb, 21346);

        switch (((x() >>> 1) % 2) + 21) {
        case 21:
            for (gi = 3; gi < 98; gi++) {
                for (o7 = 1; o7 < 27; ++o7) {
                    if (igd[gi - 1])
                        break;
                    zxy -= (--b);
                    dhb[o7] *= (int) (b + zxy);
                    dhb[o7 - 1] -= dhb[gi];
                }
                dhb[gi] ^= (((o7 - b) * (++b)) + (b++));
                b -= w;
                dhb[gi + 1] = (int) zxy;
                if ((b--) < (-2746270021440587212L - gi))
                    continue;
                n -= (dhb[gi] - ((o7 - -40337) * dhb[gi]));
                b *= (int) oni();
                for (yhe = 1; yhe < 84; yhe++) {
                    rm -= (byte) (n -= (float) (b -= (int) (-3233162796505834835L * (o7 * -57.522))));
                    for (d = 3; d < 68; d++) {
                        b = (int) ((++b) - ((-(ly + b)) - (lh - zxy)));
                        ly -= (int) ((--lh) + (n + (ly - o7)));
                        j9 += (((w - yhe) - zxy) + yhe);
                    }
                    ly >>= (int) ((--dhb[yhe - 1]) * ((o7 + o7) - (--j9)));
                    ly -= (--ly);
                    if (zas || zas) {
                        dhb[yhe] = -4340;
                        zxy -= (long) (n--);
                        lh = (lh++);
                    } else if (false) {
                        c2s += (((yhe * lh) + rm) - lh);
                    }
                }
            }
            break;
        case 22:
            ly = (dhb[(yhe >>> 1) % N]--);
        }

        System.out.println("gi o7 zxy = " + gi + "," + o7 + "," + zxy);
        System.out.println("b w n = " + b + "," + w + ","
                + Float.floatToIntBits(n));
        System.out.println("yhe d ly = " + yhe + "," + d + "," + ly);
        System.out.println("lh c2s zas = " + lh + "," + c2s + ","
                + (zas ? 1 : 0));
        System.out.println("igd dhb = " + checkSum(igd) + "," + checkSum(dhb));

        System.out.println("rm j9 = " + rm + "," + Double.doubleToLongBits(j9));

        System.out.println("x_check_sum: " + x_check_sum);
        System.out.println("oni_check_sum: " + oni_check_sum);
    }
}
