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

// Generated by Dalvik Fuzzer tool (3.2.001). Sun Apr 27 11:01:41 2014
package OptimizationTests.regression.test191288;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

public static final int N = 500;

    public static float uec=89.724F;
    public static int rk9=-32345, vt=29241, qiz=52796;
    public static long bku=7805736938003134481L;

public static void main(String[] vd) {

    short l=-19688, cc=18770;
    float v=22.655F;
    long e=-5478173715339659732L, kl=7624130954824923079L, fl5=7724321976154487021L, w=-3053074689403075916L,
        ol2=-230840791220203597L, jle=6294641318197848749L, h[]=new long[N];
    int r=-57093, y=59190, x7=-23192, w0=-63947, n1=54900, mnx=-42511, m=-43255, a1=65256, gn=26129, pun=49771,
        b3m=-57986, q=31914, ur=292, p=-29604, ut=-41965, yq=-34379, z6=11465, su4[]=new int[N], xu5[][]=new int[N][N];

    init(su4, -29246);
    init(xu5, 4161);
    init(h, 3285677830042697904L);

    for (int b : su4) {
        r *= (int)((2939357386392042364L >> y) + ((-(-42161 * (uec + -2078))) + b));
        for (x7 = 442; x7 > 16; x7--) {
            try {
                r = (b / r);
                r = (w0 / -202);
                mnx = (r / n1);
            } catch (ArithmeticException a_e) {}
            e *= (long)(((-(-95.354 + kl)) + m) + (-kl));
        }
        bku = (long)(((r - v) + (e - mnx)) * (a1 - gn));
        for (pun = 15; pun < 377; ++pun) {
            mnx += (int)fl5;
            for (b3m = 4; b3m > 1; --b3m) {
                l *= (short)(q - (fl5 / ((ur - w) | 1)));
                rk9 *= ((++w0) * (-(p * n1)));
                try {
                    a1 = (mnx % ur);
                    rk9 = (-357050267 / ut);
                    xu5[b3m + 1][b3m] = (yq / vt);
                } catch (ArithmeticException a_e) {}
                h[pun + 1] >>= -1012;
            }
            z6 += (int)(((cc %= (short)(ol2 | 1)) * (jle - pun)) | (-(-9439 - (y >> b))));
            su4[pun - 1] ^= (-qiz);
        }
    }

    System.out.println("r y x7 = " + r + "," + y + "," + x7);
    System.out.println("w0 n1 mnx = " + w0 + "," + n1 + "," + mnx);
    System.out.println("e kl m = " + e + "," + kl + "," + m);
    System.out.println("v a1 gn = " + Float.floatToIntBits(v) + "," + a1 + "," + gn);
    System.out.println("pun fl5 b3m = " + pun + "," + fl5 + "," + b3m);
    System.out.println("l q ur = " + l + "," + q + "," + ur);
    System.out.println("w p ut = " + w + "," + p + "," + ut);
    System.out.println("yq z6 cc = " + yq + "," + z6 + "," + cc);
    System.out.println("ol2 jle su4 = " + ol2 + "," + jle + "," + checkSum(su4));
    System.out.println("xu5 h = " + checkSum(xu5) + "," + checkSum(h));
    
    System.out.println("uec bku rk9 = " + Float.floatToIntBits(uec) + "," + bku + "," + rk9);
    System.out.println("vt qiz = " + vt + "," + qiz);
}
}
