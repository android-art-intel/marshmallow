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

// Generated by Dalvik Fuzzer tool (3.4.001). Wed Nov 26 20:09:33 2014
package OptimizationTests.regression.test953;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


    public static long aiw_check_sum = 0;

public static int aiw(int w) {

    int t9p=-27807, the=28504, bqy=-704, ou[][]=new int[N][N];
    byte qd7=-65;
    long s=8069660286854491172L;
    float t=41.164F;
    double s8=91.133;

    init(ou, -29571);

    for (t9p = 92; t9p > 1; --t9p) {
        s8 = w;
        the = 1;
        while (++the < 34) {
            w *= the;
            for (bqy = the; bqy < 80; ++bqy) {
                switch ((((the >>> 1) % 1) * 5) + 27) {
                case 29:
                    s |= (long)((s++) + s8);
                    break;
                }
            }
            ou[the + 1][the - 1] <<= bqy;
            w += (((the * t) + t9p) - w);
            w = (int)((qd7 - bqy) - ((w++) << (s * the)));
            w *= bqy;
            s = (s - t9p);
        }
    }
    long meth_res = w + t9p + Double.doubleToLongBits(s8) + the + bqy + s + Float.floatToIntBits(t) + qd7 +
        checkSum(ou);
    aiw_check_sum += meth_res;
    return (int)meth_res;
}

public static void main(String[] fj) {

    int n=33731, v=4349, f2=16923, sw6=-55390, lg=7631, lqf=-10716, e[][]=new int[N][N];
    long sv0=-4679480526547010196L, v1[]=new long[N];
    short isa=16751;
    float bat=14.5F, h[]=new float[N];
    double q2=121.307;

    init(v1, -5829833984736884229L);
    init(e, -41061);
    init(h, 27.896F);

    for (n = 2; n < 86; n++) {
        try {
            bat = ((sv0--) - (v--));
            f2 = 1;
            while (++f2 < 64) {
                bat *= (-8075362290537792408L + (--v));
            }
            isa <<= (short)((v += aiw(v)) * v);
            v += (--v);
            v >>= (int)((++sv0) * (v--));
            bat = ((v1[n + 1] - (sv0 >> n)) + v);
        }
        catch (ArithmeticException hdw) {
            v <<= (v = ((isa + 27142) + sw6));
            try {
                v = (-24 / e[n + 1][n]);
                v = (100 % sw6);
                v = (-952 / n);
            } catch (ArithmeticException a_e) {}
            e[n][n] = (int)(((bat - n) - (n - n)) + (--v));
            v -= (int)(((n - f2) - (28801 + (v = sw6))) - ((sw6 >>> sv0) + (sw6 * lg)));
        }
        catch (NegativeArraySizeException tj) {
            v += e[n][n - 1];
            for (sw6 = 74; sw6 > 4; --sw6) {
                sv0 -= (v += (int)((-(sv0++)) - (v + f2)));
            }
            e[n + 1][n - 1] *= 27745;
        }
        q2 = (-(lg--));
        e[n + 1][n] -= (int)(h[n] - 2722612725077018129L);
        v *= (int)(((v % (f2 | 1)) + (n - 6823227731109175187L)) - ((f2 - sw6) - v1[n + 1]));
        lg -= (int)bat;
        lg *= (int)h[n - 1];
        for (lqf = 1; lqf < 1; ++lqf) {
            lg *= (int)(--sv0);
        }
        bat = lg;
    }

    System.out.println("n bat sv0 = " + n + "," + Float.floatToIntBits(bat) + "," + sv0);
    System.out.println("v f2 isa = " + v + "," + f2 + "," + isa);
    System.out.println("sw6 lg q2 = " + sw6 + "," + lg + "," + Double.doubleToLongBits(q2));
    System.out.println("lqf v1 e = " + lqf + "," + checkSum(v1) + "," + checkSum(e));
    System.out.println("h = " + Double.doubleToLongBits(checkSum(h)));


    System.out.println("aiw_check_sum: " + aiw_check_sum);
}
}