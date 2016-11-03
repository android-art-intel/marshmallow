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

// Generated by Dalvik Fuzzer tool (3.2.001). Tue Apr  1 14:43:20 2014
package OptimizationTests.regression.test183965_1;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 500;

    public static void main(String[] jm) {

        boolean i9 = true, d[] = new boolean[N];
        int ee = 22528, zf = 24894, kfy = -55580, ps0 = 60972, mc = -50855, z2f = -55334, k = 22113, t5 = -6318, zqd = 52265, bms = 28754, qa = 35187, gm = 3134, yt = 36005, j8o = 36577, oxa[] = new int[N];
        byte f4u = 99;
        double uby = -83.861;
        short hxq = 17510;
        float fp = -1.944F, g[] = new float[N];
        long e = 2072592355714437567L, mr = 8116729533969702041L, a = -3348959753919211095L, vms = -9218508487718528001L, v = -5231542616158830562L, c[] = new long[N];

        init(g, -94.197F);
        init(oxa, -45968);
        init(d, true);
        init(c, -1714774377468303638L);

        ee = (int) (((-83.1020F * (-61540 + (-50.545 + ee))) + e) - (g[(ee >>> 1)
                % N] + -29630));
        zf >>= ee;
        for (kfy = 391; kfy > 22; kfy--) {
            ps0 = (int) (((fp - -42516) + (--ee)) - ((-(++mc)) * oxa[kfy - 1]));
            d[kfy] = ((i9 = (zf >= 28866)) ^ (d[kfy + 1] = (hxq != 0.258F)));
            zf -= (int) ((c[kfy + 1] *= (mr + z2f)) + (k - (++uby)));
            a = (-(-(hxq + zf)));
            ps0 += (int) (ps0 + ((--g[kfy + 1]) - (z2f * mr)));
        }
        for (t5 = 28; t5 < 465; ++t5) {
            oxa[t5 - 1] >>= (int) ((-(8635159116200379957L - (vms - 28316))) % 8716418643030740973L);
            zqd = bms;
            for (qa = 5; qa < 248; qa++) {
                f4u += (byte) zf;
            }
        }
        mc = gm;
        fp = (((--c[(yt >>> 1) % N]) + (hxq + 53307)) * (v - j8o));

        System.out.println("ee e zf = " + ee + "," + e + "," + zf);
        System.out.println("kfy ps0 fp = " + kfy + "," + ps0 + ","
                + Float.floatToIntBits(fp));
        System.out.println("mc i9 hxq = " + mc + "," + i9 + "," + hxq);
        System.out.println("mr z2f k = " + mr + "," + z2f + "," + k);
        System.out.println("uby a t5 = " + Double.doubleToLongBits(uby) + ","
                + a + "," + t5);
        System.out.println("vms zqd bms = " + vms + "," + zqd + "," + bms);
        System.out.println("qa f4u gm = " + qa + "," + f4u + "," + gm);
        System.out.println("yt v j8o = " + yt + "," + v + "," + j8o);
        System.out.println("g oxa d = " + Double.doubleToLongBits(checkSum(g))
                + "," + checkSum(oxa) + "," + checkSum(d));
        System.out.println("c = " + checkSum(c));

    }
}