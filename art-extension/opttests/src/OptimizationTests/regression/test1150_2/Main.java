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

// Generated by Dalvik Fuzzer tool (3.5.001). Mon Mar 23 00:43:32 2015
package OptimizationTests.regression.test1150_2;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static int iFld=219;
    public static float fFld=2.723F;

    public static long fMeth_check_sum = 0;

public static float fMeth(int i1) {

    int i2=-16219, i3=-43797, i4=-247, iArr[]=new int[N];
    short s=-3333;
    byte by=-118;
    double d=2.89244;
    float f=10.477F;
    long l1=5L, lArr[]=new long[N];

    init(lArr, -5L);
    init(iArr, -46380);

    for (i2 = 4; i2 < 95; ++i2) {
        i1 = (int)(((l1 - i1) << 11) - (s + lArr[i2]));
        i1 = (int)(i2 - ((2L % ((i1++) | 1)) - (-7L + (l1 + i1))));
        try {
            i1 = (i2 / 56066);
            i1 = (i2 % i2);
            iArr[i2 - 1] = (i2 % i2);
        } catch (ArithmeticException a_e) {}
        for (i3 = 1; i3 < 25; ++i3) {
            for (i4 = 3; i4 < 57; i4++) {
                if ((iArr[i3 + 1] * (i1 * (i3 - f))) >= (i1++)) continue;
            }
            f *= (float)(d--);
            l1 *= (long)d;
            i1 = (int)(((-202 * i1) * (i4 * i2)) + f);
            l1 <<= (((-31725L - i4) + (by - i2)) - ((l1 - 39336) - (-(i4 + 30118))));
        }
        i1 = (i2 + iArr[i2 + 1]);
    }
    long meth_res = i1 + i2 + l1 + s + i3 + i4 + Float.floatToIntBits(f) + Double.doubleToLongBits(d) + by +
        checkSum(lArr) + checkSum(iArr);
    fMeth_check_sum += meth_res;
    return (float)meth_res;
}

public static void main(String[] strArr) {

    int i=-195, i5=0, i6=3, i7=148, i8=69, i9=40409, i10=-34681, iArr1[]=new int[N];
    short s1=15670;
    byte by1=-127;
    float f1=-12.736F;
    double d1=40.66483;
    long l=141L, l2=5L, l3=-5L, lArr1[]=new long[N];

    init(lArr1, 8L);
    init(iArr1, -10);

    i = 1;
    while (++i < 77) {
        l *= (long)fMeth(i);
        d1 -= (l--);
        i5 += ((long)i | (long)i);
        l = (long)((-241 * (i5 * i5)) + (d1--));
    }
    i6 = 1;
    do {
        for (iFld = 1; iFld < i6; iFld++) {
            for (i7 = iFld; i7 < 17; ++i7) {
                i5 = iFld;
                lArr1[i6] -= (long)(((i5 - l) * (--f1)) + iArr1[i7]);
                l2 >>= (--i5);
                i5 -= (int)(l2 >>> l);
                i5 = 53;
                i8 -= (i8 - (i7 - (s1 | i)));
            }
            l2 = i;
            for (i9 = 1; i9 < 5; i9 += 2) {
                i5 = (int)((i8--) - ((l += (long)2.66207) - (f1++)));
                l3 = iArr1[i9];
            }
            for (i10 = 1; i10 < iFld; ++i10) {
                iArr1[i6 + 1] = (int)((iArr1[i6 - 1]++) + ((l3--) + i));
                iArr1[iFld - 1] += (iArr1[iFld + 1]--);
                s1 *= (short)l2;
                lArr1[i6 + 1] = (long)(iArr1[iFld] - fFld);
            }
            i8 *= (i8 = by1);
        }
        fFld += (i7 - i6);
    } while (++i6 < 81);

    System.out.println("i l d1 = " + i + "," + l + "," + Double.doubleToLongBits(d1));
    System.out.println("i5 i6 i7 = " + i5 + "," + i6 + "," + i7);
    System.out.println("f1 l2 i8 = " + Float.floatToIntBits(f1) + "," + l2 + "," + i8);
    System.out.println("s1 i9 l3 = " + s1 + "," + i9 + "," + l3);
    System.out.println("i10 by1 lArr1 = " + i10 + "," + by1 + "," + checkSum(lArr1));
    System.out.println("iArr1 = " + checkSum(iArr1));

    System.out.println("iFld fFld = " + iFld + "," + Float.floatToIntBits(fFld));

    System.out.println("fMeth_check_sum: " + fMeth_check_sum);
}
}