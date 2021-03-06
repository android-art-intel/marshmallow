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

// Generated by Dalvik Fuzzer tool (3.3.001). Sun Jul 27 07:48:13 2014
package OptimizationTests.regression.test213602;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 150;

    public static void main(String[] zpl) {

        byte r = 86, u = -11;
        long jfi = 8077777659578695855L, sn4[] = new long[N];
        boolean ah3 = true, xd = true;
        int n2 = 47901, a = -50912, ak = 7000, e8 = 31434, d0d = -12109, g[] = new int[N];
        double us = 33.622;
        short zmz = -2206, ii8[] = new short[N];
        float dpm = -60.453F;

        init(sn4, -1521927862609082822L);
        init(g, 18435);
        init(ii8, (short) 29838);

        for (n2 = 5; n2 < 127; ++n2) {
            a >>>= (a--);
            for (ak = 91; ak > 2; ak--) {
                a = (int) (++sn4[n2]);
                for (e8 = 1; e8 < ak; e8++) {
                    try {
                        a = (e8 / -65460);
                        a = (-63502 / e8);
                        a = (-1700228551 / ak);
                    } catch (ArithmeticException a_e) {
                    }
                    jfi *= (long) (((jfi - r) + (a - 115.132F)) - jfi);
                    dpm *= (float) (us + (++us));
                    jfi <<= g[ak - 1];
                    jfi |= (a++);
                    g[ak + 1] += (int) (((zmz - dpm) * -71) / ((long) ((e8 + dpm) + (ak - jfi)) | 1));
                    zmz += (short) ((dpm *= (float) (jfi + 16.971)) + jfi);
                }
                for (int n : g) {
                    sn4[n2 + 1] = (((a--) + sn4[n2]) * (jfi = (n + jfi)));
                    a = (int) ((a = (int) (a + us)) * (-(dpm + a)));
                    us -= (dpm = e8);
                    u -= (byte) (g[n2 - 1] * ((dpm * e8) + (a * n)));
                    zmz += (short) n;
                    a *= g[n2];
                    jfi <<= (long) ((-(us + u)) - (a--));
                    r &= (byte) (a--);
                }
                sn4[n2] %= (g[n2 - 1] | 1);
                ah3 = (((n2 != -5856173265517684476L) && (ah3 = xd)) && ((jfi + a) <= (ak * n2)));
                a -= (int) (jfi - ((-(us + e8)) - (a + jfi)));
                ii8[n2 + 1] = (short) (jfi--);
                dpm -= (float) (g[ak] - ((--us) - (-us)));
                us = (--sn4[n2]);
                for (d0d = 1; d0d < 43; ++d0d) {
                    dpm += e8;
                    dpm += -63;
                }
            }
        }

        System.out.println("n2 a ak = " + n2 + "," + a + "," + ak);
        System.out.println("e8 jfi r = " + e8 + "," + jfi + "," + r);
        System.out.println("dpm us zmz = " + Float.floatToIntBits(dpm) + ","
                + Double.doubleToLongBits(us) + "," + zmz);
        System.out.println("u ah3 xd = " + u + "," + ah3 + "," + xd);
        System.out.println("d0d sn4 g = " + d0d + "," + checkSum(sn4) + ","
                + checkSum(g));
        System.out.println("ii8 = " + checkSum(ii8));

    }
}
