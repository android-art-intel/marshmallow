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

// Generated by Dalvik Fuzzer tool (3.2.001). Sun Apr 20 04:14:27 2014
package OptimizationTests.regression.test201247;

import OptimizationTests.regression.shared.*;

class UserDefinedException extends RuntimeException {
    public int field;
}

public class Main extends FuzzerUtils {

public static final int N = 500;


public static void main(String[] hz) {

    short j=16429;
    double jb1=-113.277, s=39.814;
    boolean ik=true, xjj=false, o=true;
    float t=60.487F;
    int wg=-47721, vu=-31687, l=31340, x2s=-57173, q=-22370, mf6=65373, cfj=5026, tr=33495, tf[]=new int[N], is6[]=new
        int[N], orw[][]=new int[N][N];
    byte i3=-16;
    long p=7151895967341780132L, son=7239323276793971903L, k[]=new long[N];

    init(tf, 55483);
    init(is6, 22199);
    init(k, -2531580670683874610L);
    init(orw, -14759);

    for (wg = 448; wg > 19; --wg) {
        ik = ((xjj = (-49318 < (49974 - (wg - p)))) || (ik == (xjj = o)));
        vu -= (int)(((p + p) * (wg % 44256)) % -50376);
        tf[wg + 1] += (int)-7941563228624288903L;
    }
    l = 471;
    while ( --l > 0 ) {
        for (x2s = 4; x2s < 67; x2s++) {
            q = x2s;
            do {
                try {
                    vu = ((~(l << mf6)) + (++cfj));
                    j *= (short)(is6[x2s - 1] * (2.323F + (tr * p)));
                }
                catch (UserDefinedException yk) {
                    p *= (((3376048187124959509L * (-(++k[q]))) - (57484 >>> cfj)) * (--vu));
                    son += i3;
                    jb1 /= (vu | 1);
                    s = (((-orw[x2s][q]) * (vu + 33624)) - (-(--mf6)));
                }
            } while ( --q > 0 );
            cfj *= (int)(t--);
        }
    }

    System.out.println("wg ik xjj = " + wg + "," + ik + "," + xjj);
    System.out.println("p o vu = " + p + "," + o + "," + vu);
    System.out.println("l x2s q = " + l + "," + x2s + "," + q);
    System.out.println("mf6 cfj j = " + mf6 + "," + cfj + "," + j);
    System.out.println("tr son i3 = " + tr + "," + son + "," + i3);
    System.out.println("jb1 s t = " + Double.doubleToLongBits(jb1) + "," + Double.doubleToLongBits(s) + "," +
        Float.floatToIntBits(t));
    System.out.println("tf is6 k = " + checkSum(tf) + "," + checkSum(is6) + "," + checkSum(k));
    System.out.println("orw = " + checkSum(orw));
    
}
}