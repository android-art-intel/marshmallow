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

// Generated by Dalvik Fuzzer tool (2.1.003). Mon Aug  4 19:39:41 2014
package OptimizationTests.regression.test215192;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

public static final int N = 30;

public static long bo_check_sum = 0;

public static int bo(int j) {

    int q2m=51772, hfy=-55667, nt=9566, kmx[][]=new int[N][N], ylj[][]=new int[N][N], hnh[]=new int[N], bj0[]=new
        int[N];
    long shz=-5113507479248767367L;

    init(kmx, -60695);
    init(ylj, 12159);
    init(hnh, -15789);
    init(bj0, 58481);

    int in = 11;
    while ( --in > 0 ) {
        for (q2m = 22; q2m > 1; --q2m) {
            for (hfy = q2m; hfy < 11; hfy++) {
                kmx[hfy-1][hfy] -= (int)(ylj[q2m][q2m]);
            }
        }
        int fgb = 11;
        do {
            for (nt = 1; nt < 16; ++nt) {
                hnh[nt+1] = (int)(-hnh[nt]);
                hnh[nt-1] = (int)(10001);
            }
            int v = 11;
            do {
                shz -= (long)(bj0[27]);
            } while ( --v > 0 );
        } while ( --fgb > 0 );
    }

    long meth_res = j + q2m + hfy + nt + shz + checkSum(kmx) + checkSum(ylj) + checkSum(hnh) + checkSum(bj0);
    bo_check_sum += meth_res;
    return (int)meth_res;
}

public static void main(String[] myb) {

    int po=-15969, gd4=13009, mn8=43380, ry=12754, g=-8950, lr=-6313, i=51776, o2l=-15612, zs=53514, x15=3282,
        adq[]=new int[N], zf2[]=new int[N], xvb[][]=new int[N][N], gt4[]=new int[N], xct[][]=new int[N][N], k46[][]=new
        int[N][N], sp[][]=new int[N][N];
    short l=-21699, rv[]=new short[N];
    long c0=47469887051361283L, tgb[][]=new long[N][N];

    init(adq, 9998);
    init(rv, (short)231);
    init(zf2, 47337);
    init(xvb, 41580);
    init(gt4, 49902);
    init(tgb, 7018806382136071028L);
    init(xct, -24984);
    init(k46, 27080);
    init(sp, 51701);

    for (po = 25; po > 1; --po) {
        adq[po+1] = (int)(31220);
    }
    for (gd4 = 22; gd4 > 1; gd4--) {
        for (mn8 = gd4; mn8 > 1; --mn8) {
            adq[gd4+1] *= (int)(-rv[gd4-1]);
        }
        int nth = 11;
        do {
            rv[gd4+1] *= (short)((bo((int)((zf2[gd4-1]) / ((mn8 != 0) ? mn8 : 1)))) * (xvb[gd4+1][gd4-1]));
            for (ry = gd4; ry < 12; ry++) {
                zf2[ry+1] = (int)(gt4[gd4+1] - gt4[gd4-1]);
            }
        } while ( --nth > 0 );
        int cze = 11;
        while ( --cze > 0 ) {
            rv[gd4+1] -= (short)(adq[gd4-1]);
        }
    }

    System.out.println("bo_check_sum: " + bo_check_sum);
}
}
