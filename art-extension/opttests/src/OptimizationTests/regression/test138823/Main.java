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

// Generated by Dalvik Fuzzer tool (3.1.001). Mon Sep 16 14:09:40 2013

package OptimizationTests.regression.test138823;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 30;

    public static void main(String[] o) {

        int ni = -60588, n7 = -3868, b = -10036, n = 39227, z = -55356, k4 = 45448, id = -38839, h = -21214, y = 10027, l = -5206, t = 3332, rpl = 61828, yc = 28349, i = -37884, a = -11879, q[][] = new int[N][N], d[] = new int[N], fs[] = new int[N];
        byte k19 = 16;
        double b1 = -120.135;
        long hts = 3074214686910170135L, e = -3517839364383407767L, r = 884309550005320613L, kf5[] = new long[N];
        float yz = -12.820F, hx[][] = new float[N][N];

        init(q, -43246);
        init(kf5, 4940656269842873271L);
        init(d, -43900);
        init(hx, -87.391F);
        init(fs, -2112);

        yz = 23;
        while (--yz > 0) {
            q[(ni >>> 1) % N][(ni >>> 1) % N] = (int) (hts - ((ni + e) + b1));
            ni <<= (int) ((q[(ni >>> 1) % N][(n7 >>> 1) % N]++) + (++kf5[(ni >>> 1)
                    % N]));
            for (int ed : d) {
                b = 15;
                while (--b > 0) {
                    hts -= (((n7 << n) - (z * k4)) + ((id - h) + (z - h)));
                    if (false)
                        continue;
                    n7 = (-h);
                    kf5[(4317 >>> 1) % N] >>= (-(e--));
                }
                // Test integer division optimization
                try {
                    id = (z % q[(y >>> 1) % N][(l >>> 1) % N]);
                    y = (22 / h);
                    l = (28067 % t);
                } catch (ArithmeticException a_e) {
                }
                rpl = n7;
                yc = (int) ((i * e) + (--d[(12491 >>> 1) % N]));
            }
            for (a = 13; a > 1; a -= 2) {
                if ((-((-(y - id)) % (((long) hx[a - 1][a + 1] >>> 1) + 1))) != ni)
                    break;
                if (((-33422 * (r - y)) >> hts) != (fs[a - 1] * (k19--)))
                    continue;
            }
        }

        System.out.println("int: " + ni + "," + n7 + "," + b + "," + n + ","
                + z + "," + k4 + "," + id + "," + h + "," + y + "," + l + ","
                + t + "," + rpl + "," + yc + "," + i + "," + a + ","
                + checkSum(q) + "," + checkSum(d) + "," + checkSum(fs));
        System.out.println("byte: " + k19);
        System.out.println("double: " + b1);
        System.out.println("long: " + hts + "," + e + "," + r + ","
                + checkSum(kf5));
        System.out.println("float: " + yz + "," + checkSum(hx));

    }
}
