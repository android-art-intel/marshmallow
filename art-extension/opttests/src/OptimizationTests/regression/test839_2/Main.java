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

// Generated by Dalvik Fuzzer tool (3.4.001). Sun Nov  9 10:51:10 2014
package OptimizationTests.regression.test839_2;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static byte n[][] = new byte[N][N];
    public static int z = -65474;

    static {
        init(n, (byte) -34);
    }

    public static long gtx_check_sum = 0;
    public static long ln_check_sum = 0;

    public static int gtx(float u6j, float iur, int vzl) {

        long sr = -1851411503728056671L;
        float f7 = -36.601F;
        double v8n = 85.435;
        byte s7 = -86;
        int v6x = -28397, m = 62225, ee = 62172, kq = -55402, r[] = new int[N];

        init(r, 15040);

        if (((vzl + (iur--)) - (-32312 + m)) > (++s7)) {
            v6x = 1;
            while (++v6x < 80) {
                try {
                    m -= (v6x - (m--));
                    f7 = 1;
                    do {
                        m -= (int) (m -= (int) ((-(--sr)) * (sr + vzl)));
                    } while (++f7 < 77);
                } catch (ArithmeticException wt) {
                    for (ee = 1; ee < v6x; ++ee) {
                        vzl = ee;
                        r[v6x] = (int) (++iur);
                        iur = (float) ((v6x + (u6j - m)) - v8n);
                        n[v6x - 1][v6x] = (byte) (-m);
                        m = (int) (u6j - (++r[(m >>> 1) % N]));
                    }
                } finally {
                    r[v6x] += (m--);
                }
            }
        } else if ((-(-(iur--))) > (-(++r[(kq >>> 1) % N]))) {
            v8n -= (((v6x % (ee | 1)) - (sr--)) - (-34182 - (++m)));
        }
        long meth_res = Float.floatToIntBits(u6j) + Float.floatToIntBits(iur)
                + vzl + v6x + m + Float.floatToIntBits(f7) + sr + ee
                + Double.doubleToLongBits(v8n) + s7 + kq + checkSum(r);
        gtx_check_sum += meth_res;
        return (int) meth_res;
    }

    public static void ln(int fb, float un8, int ypt) {

        long gu = -6428976282862159226L, j[] = new long[N];
        float b6 = -86.239F, ax[] = new float[N];
        boolean t = false;
        double o = -61.75, u8a = 120.646;
        int zz = 52237, b[] = new int[N];

        init(j, -5039020716538540701L);
        init(b, 35724);
        init(ax, 55.254F);

        zz = 1;
        do {
            for (b6 = zz; b6 > 1; b6 -= 2) {
                j[zz - 1] += (++ypt);
                ypt *= (int) (-((ypt--) * (zz * o)));
                z = z;
                ypt = ((fb--) - (z++));
                t = ((-b[(int) (b6)]) <= ((b6 * zz) - (o + gu)));
                o -= gu;
                if (t = (t = t))
                    continue;
                for (u8a = 2; u8a < 36; ++u8a) {
                    z *= (int) ((83 - ypt) - (ax[(int) (b6 - 1)] -= ypt));
                    b[(int) (u8a)] = (int) (((++z) >> z) - ((u8a - 13873) + (gu - ypt)));
                }
            }
        } while (++zz < 89);
        ln_check_sum += fb + Float.floatToIntBits(un8) + ypt + zz
                + Float.floatToIntBits(b6) + Double.doubleToLongBits(o)
                + (t ? 1 : 0) + gu + Double.doubleToLongBits(u8a) + checkSum(j)
                + checkSum(b) + Double.doubleToLongBits(checkSum(ax));
    }

    public static void main(String[] pqq) {

        short uih = 8306;
        long nhh = 5206160457177580763L, bo8[] = new long[N];
        float q = 64.812F, o5 = -108.925F, b8t[] = new float[N];
        double mx = -41.109;
        byte jc = -87;
        int kk = 17514, ox = 10758, id8 = 57148, y = 64590, w2y[] = new int[N];

        init(w2y, 49443);
        init(bo8, -8888186518941430108L);
        init(b8t, -20.210F);

        gtx((float) (mx - z),
                (w2y[(z >>> 1) % N]++) + (--w2y[(z >>> 1) % N]),
                (int) (((z + nhh) - (50522 + nhh)) + ((42211 + nhh) * (z *= -43256))));
        kk = 1;
        while (++kk < 78) {
            q += (--w2y[kk]);
            mx -= (((kk * kk) + (z - z)) + bo8[kk + 1]);
            q += (float) ((-(mx++)) - (z = (19109 + w2y[kk])));
            z = w2y[kk];
            z = uih;
            z += kk;
            nhh += (-50989 + (kk * kk));
            z = (int) (kk * (nhh + (-124.491F * (q + z))));
            ln((int) (nhh * z), (++ox) * ox, (int) (mx - (-w2y[kk - 1])));
        }
        for (id8 = 93; id8 > 4; --id8) {
            nhh |= ((-(kk + ox)) * ((nhh - kk) + (-z)));
            if ((nhh = (w2y[id8 + 1] % (z | 1))) > ((z -= (y - z)) * mx)) {
                uih = (short) (nhh * z);
                z -= (int) ((jc * b8t[id8 - 1]) - (-(nhh * id8)));
                y = 32;
                while (--y > 0) {
                    ox >>>= (int) (w2y[y] + ((--nhh) - (mx * -48162)));
                    z = (int) nhh;
                    q = -13790;
                    z = (int) (((long) (z - mx) >>> -33284) * ((y - q) + w2y[y]));
                    o5 = 1;
                    do {
                        w2y[id8 + 1] += (int) (((ox * -33016) - (nhh = nhh)) + ((z = kk) | (nhh - y)));
                    } while (++o5 < 23);
                }
            } else {
                z <<= (int) ((-854645989884522935L + nhh) - (20124 + z));
                nhh -= (((-(ox |= (int) 5167042838505078421L)) + (nhh + z)) - ((-(8581780587469045221L * (++z))) + (uih * y)));
            }
        }

        System.out.println("mx nhh kk = " + Double.doubleToLongBits(mx) + ","
                + nhh + "," + kk);
        System.out.println("q uih ox = " + Float.floatToIntBits(q) + "," + uih
                + "," + ox);
        System.out.println("id8 jc y = " + id8 + "," + jc + "," + y);
        System.out.println("o5 w2y bo8 = " + Float.floatToIntBits(o5) + ","
                + checkSum(w2y) + "," + checkSum(bo8));
        System.out.println("b8t = " + Double.doubleToLongBits(checkSum(b8t)));

        System.out.println("z n = " + z + "," + checkSum(n));

        System.out.println("gtx_check_sum: " + gtx_check_sum);
        System.out.println("ln_check_sum: " + ln_check_sum);
    }
}
