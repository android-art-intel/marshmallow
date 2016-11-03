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

// Generated by Dalvik Fuzzer tool (3.4.001). Sun Nov 16 07:28:26 2014
package OptimizationTests.regression.test858_2;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static byte o4=32;

public static void main(String[] sj) {

    short w=-1809, z=-3637, h=12590;
    long nsu=4112878558749541423L, m5i[]=new long[N];
    int f=-57687, zz=-40252, fz=7553, xeu=-29181, bk=12507, l=11015, q37[]=new int[N];
    float si3=16.536F;
    boolean phv=false, k[]=new boolean[N];
    double e=-104.775;

    init(q37, -26895);
    init(m5i, 883591948700042850L);
    init(k, true);

    for (f = 5; f < 81; ++f) {
        for (fz = 4; fz < 87; fz++) {
            if (k[f - 1]) {
                xeu = 1;
                while (++xeu < f) {
//                System.out.println("  : "+f+" "+fz+" "+xeu+" "+m5i+" "+m5i.length);
                    try {
                        zz = (131 / zz);
                        q37[f + 1] = (170 / zz);
                        q37[f + 1] = (-45463 % f);
                    } catch (ArithmeticException a_e) {
                    }
                    zz += (int)(--m5i[f - 1]); // here AIOBE
                }
            }
        }
    }

//    System.out.println("f zz nsu = " + f + "," + zz + "," + nsu);
//    System.out.println("w si3 e = " + w + "," + Float.floatToIntBits(si3) + "," + Double.doubleToLongBits(e));
//    System.out.println("fz z h = " + fz + "," + z + "," + h);
//    System.out.println("xeu bk l = " + xeu + "," + bk + "," + l);
//    System.out.println("phv q37 m5i = " + (phv ? 1 : 0) + "," + checkSum(q37) + "," + checkSum(m5i));
//    System.out.println("k = " + checkSum(k));

//    System.out.println("o4 = " + o4);
}
}
