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

// Generated by Dalvik Fuzzer tool (3.3.001). Sun Jul  6 09:49:57 2014
package OptimizationTests.regression.test209438;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

public static final int N = 300;

    public static int oj=-59402;
    public static long a=-1897223401519710181L;

public static void main(String[] t) {

    boolean z=true;
    int l37=-23596, fs=29411, clu=12158, y9=-25177, nla=-38515, h=-10153, is=-58428, y6s=32839, pwg[]=new int[N],
        l[]=new int[N];
    double j=-7.610;
    float f=89.468F;
    short w7=-29951;
    long vi[][]=new long[N][N];

    init(pwg, -37248);
    init(l, 43279);
    init(vi, -5202536919024285207L);

    for (l37 = 7; l37 < 259; l37++) {
        if (z) {
            j -= l37;
            for (fs = 152; fs > 6; fs -= 2) {
                a = ((fs * (29344 + w7)) + (l37 - (40482 * (l37 + 8241988973044946355L))));
                clu -= (int)f;
            }
            for (y9 = 92; y9 > l37; --y9) {
                clu = 35221;
            }
            clu &= (int)(f * a);
        } else {
            nla >>= (int)(a * pwg[l37]);
        }
        j = ((-pwg[l37]) * y9);
        clu = pwg[l37 + 1];
        for (int dd : pwg) {
            clu = l37;
            l[l37] = (int)a;
        }
        clu += (int)(--j);
        vi[l37][l37] = (-(nla - (-(l37 - y9))));
        nla ^= (int)((vi[l37 + 1][l37] - (9116 * (124 + (--nla)))) + ((52.88F + y9) + (fs * clu)));
    }
    for (h = 9; h < 278; h += 2) {
        a += (nla++);
        f *= (((y9 - 25833) * 11245) - pwg[h]);
        for (is = 26; is > 1; is--) {
            y6s = h;
            while ( --y6s > 0 ) {
                nla -= oj;
                oj = (++pwg[is]);
            }
        }
    }

    System.out.println("l37 j fs = " + l37 + "," + Double.doubleToLongBits(j) + "," + fs);
    System.out.println("w7 clu f = " + w7 + "," + clu + "," + Float.floatToIntBits(f));
    System.out.println("y9 z nla = " + y9 + "," + z + "," + nla);
    System.out.println("h is y6s = " + h + "," + is + "," + y6s);
    System.out.println("pwg l vi = " + checkSum(pwg) + "," + checkSum(l) + "," + checkSum(vi));
    
    System.out.println("a oj = " + a + "," + oj);
}
}
