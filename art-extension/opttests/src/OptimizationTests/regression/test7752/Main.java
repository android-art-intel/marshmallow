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

// Generated by Dalvik Fuzzer tool (3.5.001). Fri Apr  3 12:11:38 2015
package OptimizationTests.regression.test7752;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


    public static long vMeth_check_sum = 0;

public static void vMeth(int i3, float f1) {

    byte by=-26;
    double d1=0.125239;
    int i4=-14, i5=-37955, i6=96, i7=-2, iArr1[]=new int[N];
    long l1=499508477085670715L;

    init(iArr1, -9);

    for (i4 = 89; i4 > 4; i4--) {
        i3 = (int)(((--i3) - (i4 - i3)) + (167 - l1));
        d1 += (i4 + d1);
        iArr1[i4 - 1] -= (i3--);
        i3 = (int)(i3 * l1);
        l1 ^= i4;
    }
    for (i5 = 4; i5 < 87; i5++) {
        i3 >>= i3;
    }
    for (i6 = 94; i6 > 5; i6 -= 2) {
        for (i7 = 1; i7 < 61; ++i7) {
            i3 += (int)d1;
            i3 = by;
        }
    }
    vMeth_check_sum += i3 + Float.floatToIntBits(f1) + i4 + l1 + Double.doubleToLongBits(d1) + i5 + i6 + i7 + by +
        checkSum(iArr1);
}

public static void main(String[] strArr) {

    boolean b=false;
    short sArr[]=new short[N];
    float f=-2.461F, fArr[][]=new float[N][N];
    double d=-64.18222;
    int i=-62575, i1=104, i2=-5, iArr[]=new int[N];
    long l=-44L, lArr[]=new long[N];

    init(sArr, (short)1241);
    init(lArr, -1255699469860659491L);
    init(iArr, 6);
    init(fArr, 94.201F);

    for (short s : sArr) {
        i *= i;
        i1 = 1;
        do {
            i += i1;
            lArr[i1 - 1] = ((~(-(i * l))) - i);
        } while (++i1 < 97);
        i = ((iArr[(i1 >>> 1) % N]--) + (s - (-(i * s))));
        l += (long)(((i % (i | 1)) + (i1 + i)) - (fArr[(i >>> 1) % N][(-157 >>> 1) % N]++));
        l += ((iArr[(-247 >>> 1) % N] - iArr[(i2 >>> 1) % N]) + (i++));
        iArr[(i2 >>> 1) % N] >>= (((i - i) + i2) * iArr[(i2 >>> 1) % N]);
        f -= 4796;
        b = (d == (--i2));
    }
    vMeth(i, (float)(d * (iArr[(i1 >>> 1) % N]++)));

    System.out.println("i i1 l = " + i + "," + i1 + "," + l);
    System.out.println("i2 f b = " + i2 + "," + Float.floatToIntBits(f) + "," + (b ? 1 : 0));
    System.out.println("d sArr lArr = " + Double.doubleToLongBits(d) + "," + checkSum(sArr) + "," + checkSum(lArr));
    System.out.println("iArr fArr = " + checkSum(iArr) + "," + Double.doubleToLongBits(checkSum(fArr)));


    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
}
}
