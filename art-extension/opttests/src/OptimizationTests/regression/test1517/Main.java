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

// Generated by Dalvik Fuzzer tool (3.5.001). Sat Feb 14 19:44:24 2015
package OptimizationTests.regression.test1517;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static int iFld=-49089;

    public static long iMeth_check_sum = 0;
    public static long vMeth_check_sum = 0;

public static int iMeth() {

    int i3=8924, i4=51, i5=-50591, i6=9, i7=-4, iArr1[]=new int[N];
    float f1=0.989F;
    byte by1=121;
    long l1=1303L, lArr1[]=new long[N];

    init(iArr1, 2);
    init(lArr1, 4L);

    for (i3 = 5; i3 < 98; i3++) {
        iArr1[i3 - 1] = by1;
        i4 = (int)-2.86429;
    }
    i5 = 1;
    while ((i5 += 2) < 78) {
        for (i6 = 42; i6 > 1; i6 -= 2) {
            i4 -= (int)lArr1[i6];
        }
    }
    for (i7 = 1; i7 < 82; ++i7) {
        try {
            iArr1[i7] = (i6 / i7);
            iArr1[i7 - 1] = (i5 % -27);
            i4 = (17336 / i5);
        } catch (ArithmeticException a_e) {}
        iArr1[i7] *= (int)(i6 * ((i5 - i5) * (-44.289F + f1)));
        iArr1[i7] = (int)(++l1);
        by1 += (byte)((iArr1[i7 - 1] + (i4++)) * Integer.reverseBytes(++i4));
    }
    i4 += i7;
    long meth_res = i3 + by1 + i4 + i5 + i6 + i7 + Float.floatToIntBits(f1) + l1 + checkSum(iArr1) + checkSum(lArr1);
    iMeth_check_sum += meth_res;
    return (int)meth_res;
}

public static void vMeth(int i8, float f2) {

    int i9=4, i10=-75, i11=-3, i12=253, i13=14, iArr2[]=new int[N];
    short s1=-15458;
    boolean b1=true;
    long l2=-4734L, lArr2[]=new long[N];
    double d1=-90.70515;

    init(iArr2, -7);
    init(lArr2, 9L);

    for (iFld = 4; iFld < 95; ++iFld) {
        b1 = (!(iFld != i8));
        i8 += iFld;
    }
    for (i9 = 77; i9 > 3; i9 -= 3) {
        for (i10 = 1; i10 < i9; i10++) {
            i8 += (int)(-((--i8) - (-74.178F * (21372 + f2))));
            iArr2[(i11 >>> 1) % N] >>>= (int)(((i8++) / ((long)(f2 + i12) | 1)) * (d1 * i11));
            for (i13 = 1; i13 < i10; i13++) {
                i11 -= iArr2[i13 + 1];
                i11 += (-((i12 % (i11 | 1)) - i9));
            }
            lArr2[i9 - 1] = (((++s1) % ((-7 % (iArr2[i10] | 1)) | 1)) - ((i8--) - (i10 + i11)));
            i8 -= (int)(((-(l2 + i13)) + -109L) << (long)(d1 + i9));
        }
    }
    vMeth_check_sum += i8 + Float.floatToIntBits(f2) + (b1 ? 1 : 0) + i9 + i10 + i11 + i12 +
        Double.doubleToLongBits(d1) + i13 + s1 + l2 + checkSum(iArr2) + checkSum(lArr2);
}

public static void main(String[] strArr) {

    int i=4, i1=-55420, i2=-3180, i14=8256, i15=28482, iArr[]=new int[N];
    byte by=-23;
    float f=1.444F;
    short s=-25102, sArr[][]=new short[N][N];
    boolean b=true;
    long l=14118L, lArr[]=new long[N];
    double d=0.22692;

    init(lArr, 6001397311037077384L);
    init(iArr, 4);
    init(sArr, (short)18362);

    for (i = 3; i < 80; ++i) {
        i1 = (int)((++l) - ((i1 * 51438) + (f - f)));
        i1 += (int)(lArr[i - 1] = (long)(d -= iArr[i]));
        f += (i + i);
        for (i2 = i; i2 < 66; ++i2) {
            b = ((-(i + i)) > ((i1 + 1.718F) * (i1 - i1)));
            i1 -= (by - i2);
            l += (i2 * i);
            l += (-128 + (i2 * i2));
            i1 += (i2 * i);
            iArr[i2 - 1] *= (int)(((l - i) - (f--)) / ((-(l * i)) | 1));
            i1 += (int)(((i1 - i2) + (--lArr[i - 1])) + ((f += f) + (++i1)));
            if (b) continue;
            f = Math.min(lArr[i2], (i - s) - (i * l));
        }
        sArr[i - 1][i - 1] |= (short)l;
        lArr[i - 1] |= i;
        iArr[i - 1] *= (int)((i2 - (--f)) - lArr[i]);
        i1 -= (int)(((f = l) - (--f)) * iMeth());
        lArr[i] -= i;
        vMeth(--iArr[i - 1], l * (i + (iFld - l)));
    }
    for (i14 = 5; i14 < 81; ++i14) {
        for (i15 = 1; i15 < 38; i15 += 2) {
            i1 /= -3;
        }
        i1 += (int)(((iFld * i14) - Double.doubleToRawLongBits(d)) - ((iFld + l) - d));
        by = (byte)(((l - i) - iFld) + ((i1 / (i | 1)) - (iFld + i)));
    }

    System.out.println("i i1 l = " + i + "," + i1 + "," + l);
    System.out.println("f d i2 = " + Float.floatToIntBits(f) + "," + Double.doubleToLongBits(d) + "," + i2);
    System.out.println("b by s = " + (b ? 1 : 0) + "," + by + "," + s);
    System.out.println("i14 i15 lArr = " + i14 + "," + i15 + "," + checkSum(lArr));
    System.out.println("iArr sArr = " + checkSum(iArr) + "," + checkSum(sArr));

    System.out.println("iFld = " + iFld);

    System.out.println("iMeth_check_sum: " + iMeth_check_sum);
    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
}
}
