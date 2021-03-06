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

// Generated by Dalvik Fuzzer tool (3.4.001). Sun Nov  9 00:42:42 2014
package OptimizationTests.regression.test839_1;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static double gx = 81.5;

    public static long wzh_check_sum = 0;
    public static long i_check_sum = 0;

    public static double wzh(byte u) {

        long gl4 = 7222767079411174918L, wiz[] = new long[N];
        double ctr = -14.1007;
        short r = -32113;
        int p1k = -64501, cdd = 44828, q66 = 47006, uhc[] = new int[N];
        float h1 = 27.851F;
        boolean bv = false, fy[] = new boolean[N];

        init(uhc, 38847);
        init(fy, true);
        init(wiz, -2924198573805687245L);

        for (p1k = 4; p1k < 97; ++p1k) {
            cdd = 1;
            while (++cdd < p1k) {
                q66 = (q66++);
                h1 += (-48119 * ((r - gl4) - (uhc[cdd - 1] <<= r)));
                q66 -= p1k;
                fy[cdd - 1] = (fy[p1k - 1] = bv);
                q66 = (int) (wiz[cdd]++);
                r <<= (short) cdd;
                q66 |= (cdd + cdd);
                ctr = cdd;
            }
            r = (short) (((u << cdd) + (-(q66++))) - (uhc[p1k + 1] + ctr));
            wiz[p1k - 1] *= gl4;
        }
        long meth_res = u + p1k + cdd + q66 + Float.floatToIntBits(h1) + r
                + gl4 + (bv ? 1 : 0) + Double.doubleToLongBits(ctr)
                + checkSum(uhc) + checkSum(fy) + checkSum(wiz);
        wzh_check_sum += meth_res;
        return (double) meth_res;
    }

    public static void i(int ci4) {

        long lf = -6244299518957506187L;
        byte p = -78;
        int k = -44567, px9 = 44852, yed = 50738, mi[] = new int[N];
        float vcr[] = new float[N];
        boolean m = false;

        init(vcr, 114.962F);
        init(mi, -7744);

        k = 1;
        do {
            lf = (long) gx;
            ci4 = (int) (lf++);
            ci4 += 11706;
        } while (++k < 84);
        for (px9 = 5; px9 < 88; px9++) {
            m = (((58852 - (ci4 - px9)) * (lf % (k | 1))) >= (ci4++));
            ci4 = (int) (ci4 - (--gx));
        }
        yed = 1;
        do {
            vcr[yed] = k;
            ci4 *= (int) (7252989760940893349L + yed);
        } while (++yed < 97);
        ci4 = ((++mi[(63222 >>> 1) % N]) + (-(k + ci4)));
        lf = (38035 * ((ci4 + p) - lf));
        i_check_sum += ci4 + k + lf + px9 + (m ? 1 : 0) + yed + p
                + Double.doubleToLongBits(checkSum(vcr)) + checkSum(mi);
    }

    public static void main(String[] ju) {

        long zo = -4959481146965571550L, fh[] = new long[N];
        byte ki3 = -104;
        double tu = 119.725;
        short s = 24918;
        int dlo = 39830, q5d = 2090, btb = 14084, f7 = 58193, j[] = new int[N];
        float cva = -39.896F, r3z[] = new float[N];
        boolean g4 = false, b01[] = new boolean[N];

        init(j, -30027);
        init(fh, -9112426965622366808L);
        init(r3z, 10.748F);
        init(b01, true);

        if (((q5d * q5d) - ((--r3z[(btb >>> 1) % N]) - (dlo + dlo))) != ((--q5d) * ((zo + f7) + (q5d - zo)))) {
            for (int iy1 : j) {
                iy1 = (int) (zo + ((zo + zo) + tu));
            }
            for (int c : j) {
                fh[(26734 >>> 1) % N] += (c--);
                cva = (-8889669372277134468L + ((c - c) | (ki3 - c)));
            }
        } else if (g4 && ((++ki3) >= (q5d * (j[(btb >>> 1) % N]--)))) {
            for (dlo = 1; dlo < 89; dlo++) {
                q5d = (int) (((dlo * zo) - wzh(ki3)) / ((q5d--) | 1));
                zo -= (long) (((zo % ((long) tu | 1)) * (dlo * -30953)) - ((q5d--) - q5d));
                cva *= cva;
                r3z[dlo + 1] = (q5d ^= (int) (-(ki3 - cva)));
                q5d += dlo;
                q5d <<= (int) (--zo);
                q5d -= (int) (((tu + q5d) - dlo) + (++q5d));
                zo = (long) (((ki3 + -8586739265073413919L) * fh[dlo - 1]) / ((long) ((q5d - zo) + (cva - q5d)) | 1));
                q5d += (int) ((-(-41018 + (53932 * (zo + zo)))) - (q5d++));
                switch ((dlo % 1) + 112) {
                case 112:
                    if (((r3z[dlo] += (float) (tu + 62263)) <= ((zo + dlo) - (++q5d)))
                            | (b01[dlo + 1] && g4))
                        continue;
                    for (btb = 67; btb > 4; --btb) {
                        zo >>= (btb * zo);
                        q5d += (int) cva;
                    }
                    tu *= (s * dlo);
                    i((int) ((zo - (--q5d)) + dlo));
                }
            }
        } else {
            i(f7);
        }
        i(btb);

        System.out
                .println("zo tu cva = " + zo + ","
                        + Double.doubleToLongBits(tu) + ","
                        + Float.floatToIntBits(cva));
        System.out.println("ki3 dlo q5d = " + ki3 + "," + dlo + "," + q5d);
        System.out.println("g4 btb s = " + (g4 ? 1 : 0) + "," + btb + "," + s);
        System.out.println("f7 j fh = " + f7 + "," + checkSum(j) + ","
                + checkSum(fh));
        System.out.println("r3z b01 = "
                + Double.doubleToLongBits(checkSum(r3z)) + "," + checkSum(b01));

        System.out.println("gx = " + Double.doubleToLongBits(gx));

        System.out.println("wzh_check_sum: " + wzh_check_sum);
        System.out.println("i_check_sum: " + i_check_sum);
    }
}
