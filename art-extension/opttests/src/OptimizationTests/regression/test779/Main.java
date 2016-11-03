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

// Generated by Dalvik Fuzzer tool (3.4.001). Sat Oct 25 16:06:20 2014
package OptimizationTests.regression.test779;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static float ild = 86.772F;
    public static int l41 = -5008;

    public static long kcm_check_sum = 0;
    public static long q0_check_sum = 0;

    public static void kcm() {

        byte mu = -113;
        double k = -78.949;
        boolean w0 = true;
        short z0y = 198, z = -1237;
        float z3 = 73.1006F;
        long n5 = 7954752234893944233L;
        int v = 60239, g = 2601, a5i = 31832, awb = -44828, xdk = 32588, ci[] = new int[N];

        init(ci, 46788);

        for (v = 1; v < 94; v++) {
            k += ((v + v) - ((v + mu) / (v | 1)));
            for (g = 1; g < v; g += 2) {
                w0 = (w0 = (w0 | w0));
                n5 += (long) (((v - z3) * -55584) - ((-9127608826412543872L - (g - 22901)) + n5));
                a5i = 1;
                do {
                    awb += (int) (((v - a5i) - (ci[v] += (int) n5)) - ((n5 - v) + (n5 + k)));
                    n5 = (((xdk + z0y) * n5) + (--xdk));
                    z = (short) k;
                    xdk += ((long) a5i | (long) awb);
                    awb *= (int) (n5 + (--xdk));
                    awb = (int) ((ci[g]++) - ((n5 - v) - k));
                } while (++a5i < 7);
            }
        }
        kcm_check_sum += v + Double.doubleToLongBits(k) + mu + g + (w0 ? 1 : 0)
                + n5 + Float.floatToIntBits(z3) + a5i + awb + xdk + z0y + z
                + checkSum(ci);
    }

    public static void q0(byte t98, float x) {

        byte nz[] = new byte[N];
        double q = 28.513;
        boolean e = true;
        short gwu = 21350;
        long bw5 = 6267085363621676199L, ut4[] = new long[N];
        int n = -24784, yj = 17660, s = -36005;

        init(nz, (byte) -43);
        init(ut4, 1641394962853766931L);

        for (n = 90; n > 4; n -= 3) {
            for (yj = 1; yj < n; ++yj) {
                s -= (((--s) - (s = 48389)) * (++s));
            }
            s += (n * s);
            bw5 <<= ((s++) * (nz[n] >> (bw5 - yj)));
            ut4[n + 1] = (-((yj + t98) - -10342));
            e = (((q - n) + (-51406 - t98)) != (s--));
            for (l41 = 2; l41 < 36; ++l41) {
                if ((-(42858 - (yj * -1835377629403452079L))) != (gwu--))
                    break;
                gwu <<= (short) (++s);
                e = e;
                ut4[l41] -= (bw5--);
            }
        }
        q0_check_sum += t98 + Float.floatToIntBits(x) + n + yj + s + bw5
                + (e ? 1 : 0) + Double.doubleToLongBits(q) + gwu + checkSum(nz)
                + checkSum(ut4);
    }

    public static void main(String[] j0b) {

        double jgb = -116.71;
        byte a = 0;
        boolean glt = true;
        long g5m = 3838306552258289635L, yq[][] = new long[N][N];
        float de = 107.276F;
        int umt = 13269, i = 13637, iuf = 3202, agc = 4199, oij = -44169, x5s = -15499, sqn = -43945, b2m = 18066, w[] = new int[N];

        init(yq, -3158246917823171844L);
        init(w, -45297);

        umt = 1;
        while (++umt < 92) {
            i = (int) (yq[umt + 1][umt] += (long) (umt * (-50426 - de)));
            iuf &= (w[umt] + ((-(a += (byte) i)) + (umt - -9175)));
            iuf = (int) de;
            kcm();
            g5m -= ((iuf++) * ((g5m * 6318) * (i = 14116)));
            try {
                i = (umt % -1881023246);
                iuf = (40770 % iuf);
                i = (iuf % iuf);
            } catch (ArithmeticException a_e) {
            }
        }
        for (agc = 3; agc < 80; ++agc) {
            jgb *= (--yq[agc][agc]);
            if (glt || ((glt = (umt < i)) ^ (glt = glt)))
                continue;
            i = (w[agc + 1]++);
            g5m = (--i);
            iuf += (int) ((++w[agc]) + ((-(agc * g5m)) - i));
            de = ((-iuf) * (++i));
        }
        for (oij = 6; oij < 97; oij++) {
            q0((byte) ((jgb - (-7006219955015471602L >>> (long) (-(21571 - (ild * umt))))) + (--iuf)),
                    ((--i) | (g5m * iuf)) - ((x5s * x5s) - ild));
            iuf = (int) (x5s = (int) (x5s -= (int) (g5m - x5s)));
            try {
                x5s += (int) g5m;
                w[oij + 1] <<= a;
                x5s += oij;
                iuf = (int) (((g5m + agc) + (i = (int) 4247940346848514900L)) * x5s);
                for (sqn = oij; sqn < 88; sqn++) {
                    w[sqn - 1] -= (int) (((-(35.156 + (7127337906807912629L - (a + g5m)))) + b2m) - (++g5m));
                    de = (++w[oij]);
                }
            } catch (NegativeArraySizeException t) {
                w[oij + 1] = (int) ((18170 + w[oij]) + (-(-(jgb - jgb))));
            }
        }

        System.out.println("umt i de = " + umt + "," + i + ","
                + Float.floatToIntBits(de));
        System.out.println("iuf a g5m = " + iuf + "," + a + "," + g5m);
        System.out.println("agc jgb glt = " + agc + ","
                + Double.doubleToLongBits(jgb) + "," + (glt ? 1 : 0));
        System.out.println("oij x5s sqn = " + oij + "," + x5s + "," + sqn);
        System.out.println("b2m yq w = " + b2m + "," + checkSum(yq) + ","
                + checkSum(w));

        System.out
                .println("l41 ild = " + l41 + "," + Float.floatToIntBits(ild));

        System.out.println("kcm_check_sum: " + kcm_check_sum);
        System.out.println("q0_check_sum: " + q0_check_sum);
    }
}
