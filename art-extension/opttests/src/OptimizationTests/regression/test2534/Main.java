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

// Generated by Android Runtime Fuzzer tool (3.5.002). Fri Aug 21 18:13:30 2015
// srand = 308430354394905729687367056635911484759

package OptimizationTests.regression.test2534;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static float fFld=1.36F;

    public static long bMeth_check_sum = 0;

public static boolean bMeth(int i4, long l2, long l3) {

    int i5=-10, i6=-52683, i7=-63024, iArr1[]=new int[N];
    double d1=-37.105082, dArr1[][]=new double[N][N];
    long l4=-4L, lArr1[]=new long[N];
    float f1=102.953F, fArr[][]=new float[N][N];

    init(iArr1, 9089);
    init(fArr, -1.375F);
    init(lArr1, -12L);
    init(dArr1, -2.69282);

    for (i5 = 96; i5 > 5; i5 -= 3) {
        i4 -= (i4 = (--iArr1[i5]));
        i6 = 1;
        do {
            fArr[i5 - 1][i5] += (l2 * iArr1[i5 - 1]);
            lArr1[i5 - 1] = (long)(fFld++);
            d1 = (l4 * (l3--));
            i4 = i6;
        } while (++i6 < 88);
        for (i7 = i5; i7 < 46; ++i7) {
            for (f1 = 3; f1 < 73; f1 += 3) {
                dArr1[i5 + 1][i7 - 1] += ((--iArr1[(int)(f1 + 1)]) * iArr1[i7 - 1]);
                fFld -= (float)(((l4 - l4) + (i5 * f1)) + ((d1 - i7) - (i5 + i6)));
                fFld %= (i6 | 1);
            }
        }
    }
    long meth_res = i4 + l2 + l3 + i5 + i6 + Double.doubleToLongBits(d1) + l4 + i7 + Float.floatToIntBits(f1) +
        checkSum(iArr1) + Double.doubleToLongBits(checkSum(fArr)) + checkSum(lArr1) +
        Double.doubleToLongBits(checkSum(dArr1));
    bMeth_check_sum += meth_res;
    return meth_res % 2 > 0;
}

public static void main(String[] strArr) {

    int i=-8, i1=-32942, i2=-95, i3=2, i8=1, iArr[]=new int[N];
    byte by=-101;
    long l=-2879388771L, l1=-258852634911775623L, lArr[]=new long[N];
    double d=-2.111206, dArr[]=new double[N];
    float f=-2.393F;
    boolean b=false;
    short s=-30960;

    init(iArr, -56658);
    init(dArr, -1.28616);
    init(lArr, 2479325406823178397L);

    i = 98;
    while (--i > 0) {
        i1 -= (int)(by - l);
        d -= (i1++);
        i1 += (int)(((l + i1) - l) + (++iArr[i + 1]));
        i1 = (int)((-106 * (l + f)) * i1);
        for (i2 = 1; i2 < 6; i2++) {
            l += (long)(((-(dArr[i + 1]++)) + (--l)) + ((i2 - i) + (--i1)));
            if ((--i1) != (i1--)) {
            } else {
                i1 -= (int)(f * d);
                l >>>= ((lArr[i]++) - (i1++));
                d -= ((i1++) + (--l));
            }
            b = (b = (b = b));
            d = ((~iArr[i]) - ((--i1) * (d--)));
            l1 += (long)d;
        }
        i1 *= (int)Math.max(-1L, l - (iArr[i + 1]--));
    }
    for (i3 = 4; i3 < 84; i3++) {
        i1 &= (int)((--d) + (i1++));
        i1 += (i3 | i);
        b = (bMeth(i1 = i3, l, Math.max(l1, l1)) == b);
        i1 >>= ((iArr[i3] - (i1 = i1)) - i3);
        for (double d2 : dArr) {
            l1 = (((s - i1) + s) * i3);
            i1 = (i1 = iArr[i3]);
            i1 -= (i8 - ((++s) - i));
            i8 += (i3 | l);
        }
    }

    System.out.println("i i1 by = " + i + "," + i1 + "," + by);
    System.out.println("l d f = " + l + "," + Double.doubleToLongBits(d) + "," + Float.floatToIntBits(f));
    System.out.println("i2 b l1 = " + i2 + "," + (b ? 1 : 0) + "," + l1);
    System.out.println("i3 s i8 = " + i3 + "," + s + "," + i8);
    System.out.println("iArr dArr lArr = " + checkSum(iArr) + "," + Double.doubleToLongBits(checkSum(dArr)) + "," +
        checkSum(lArr));

    System.out.println("fFld = " + Float.floatToIntBits(fFld));

    System.out.println("bMeth_check_sum: " + bMeth_check_sum);
}
}