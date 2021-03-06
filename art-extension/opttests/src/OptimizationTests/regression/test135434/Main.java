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

// Generated by Dalvik Fuzzer tool (3.1.000). Sun Sep  1 05:36:57 2013
package OptimizationTests.regression.test135434;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 1000;

    public static void main(String[] hc) {

        int a = -21604, q87 = -20411, j3a = -25675, w = -2242, o = -22905, g = -13265, knw = 52078, r2u = 46873, mlw = 25457, fpy = 11162, ftz = -36287, b = 56871, e8y = -35807, ho5 = 33835, rq = 51490, dhm = 56612, m[] = new int[N], yu[] = new int[N], f[] = new int[N];
        byte cq = 98, p12 = 89;
        long u = -8656929635064000890L, ir4 = -496347478322859776L, fs = 4066900560633851317L, jn[][] = new long[N][N];

        init(m, -8445);
        init(yu, -3827);
        init(jn, 6766895225571290311L);
        init(f, -61571);

        for (int n : m) {
            a = (int) (((-(q87 / ((j3a >>> 1) + 1))) - 12308) >> (yu[(a >>> 1)
                    % N] & (w + u)));
            for (o = 1; o < 5; o++) {
                a = (int) ((q87 - (o + j3a)) - ((u << ir4) / ((u >>> 1) + 1)));
                for (g = 1; g < o; ++g) {
                    knw = cq;
                    knw = a;
                    w += (int) ((r2u - (u - p12)) - ((-28854 << (u + r2u)) - (855 | (q87--))));
                }
                m[o] += (knw + w);
            }
        }
        for (mlw = 1; mlw < N - 19; ++mlw) {
            j3a -= (int) (jn[mlw + 1][mlw + 1] <<= fpy);
            f[mlw] -= (int) (u << ((a -= cq) + (ftz - q87)));
            b *= (--fpy);
        }
        e8y = (int) (-(-(q87 = (int) fs)));
        knw = (((ho5 - -51987) - (a - rq)) << dhm);

        System.out.println("int: " + a + "," + q87 + "," + j3a + "," + w + ","
                + o + "," + g + "," + knw + "," + r2u + "," + mlw + "," + fpy
                + "," + ftz + "," + b + "," + e8y + "," + ho5 + "," + rq + ","
                + dhm + "," + checkSum(m) + "," + checkSum(yu) + ","
                + checkSum(f));
        System.out.println("byte: " + cq + "," + p12);
        System.out.println("long: " + u + "," + ir4 + "," + fs + ","
                + checkSum(jn));

    }
}
