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

// Generated by Dalvik Fuzzer tool (3.5.001). Sat Apr 25 05:39:53 2015
package OptimizationTests.regression.test8560;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static int iFld=-10;
    public static long lFld=-14L;

    public static long lMeth_check_sum = 0;
    public static long vMeth_check_sum = 0;

public static long lMeth(double d, int i4) {

    byte by1=-49;
    double d1=-1.127406, d2=-4.33757;
    short s=-11464;
    int i5=-28100, i6=-27469, i7=203, i8=217, iArr1[]=new int[N];
    long l=20247L;

    init(iArr1, 5);

    for (d1 = 2; d1 < 86; d1++) {
        i4 -= (((i4 - -36) + i4) + i4);
        iArr1[(int)(d1 - 1)] &= ((++iArr1[(int)(d1)]) - Math.min((int)(i4 = (int)1.931F), -120 - (s ^ i4)));
    }
    i5 = 1;
    while (++i5 < 80) {
        for (d2 = 1; d2 < i5; d2++) {
            i4 = ((--i4) * Integer.reverseBytes(by1 * s));
            l += (long)d2;
            for (i6 = 5; i6 < 93; ++i6) {
                i4 += (-35024 + (i6 * i6));
                l = 25674;
            }
            i4 = (-((i4 - i7) + -67));
            i4 <<= (int)((long)((d2 * i8) + l) | (++iArr1[(int)(d2 - 1)]));
        }
    }
    long meth_res = Double.doubleToLongBits(d) + i4 + Double.doubleToLongBits(d1) + s + i5 +
        Double.doubleToLongBits(d2) + by1 + l + i6 + i7 + i8 + checkSum(iArr1);
    lMeth_check_sum += meth_res;
    return (long)meth_res;
}

public static void vMeth() {

    float f1=1.409F, f2=2.2F, fArr[]=new float[N];
    byte by2=-108, byArr[]=new byte[N];
    int i10=-25, i11=-12, iArr2[]=new int[N];
    long lArr1[]=new long[N];

    init(fArr, 1.573F);
    init(byArr, (byte)93);
    init(iArr2, -1);
    init(lArr1, -7L);

    for (f1 = 4; f1 < 79; f1++) {
        for (i10 = 1; i10 < 15; ++i10) {
            if (false) break;
            lFld = (long)(((i10 - by2) + (i10 - lFld)) * fArr[(int)(f1 - 1)]);
            i11 = 1;
            while (++i11 < (int)f1) {
                byArr[i10 - 1] = (byte)(++lFld);
                iArr2[(int)(f1)] = (int)(++lArr1[i10 - 1]);
                iArr2[i10] <<= (int)(lFld * (--lFld));
                iArr2[i10 - 1] = (int)(lArr1[i11 - 1] * (f2--));
                iFld -= (-(iFld + (iFld + i10)));
                iFld = (int)(i10 + (-(28785L * (lFld - i10))));
                f2 += (++lArr1[i10]);
            }
        }
    }
    vMeth_check_sum += Float.floatToIntBits(f1) + i10 + by2 + i11 + Float.floatToIntBits(f2) +
        Double.doubleToLongBits(checkSum(fArr)) + checkSum(byArr) + checkSum(iArr2) + checkSum(lArr1);
}

public static void main(String[] strArr) {

    float f=35.174F, fArr1[]=new float[N];
    byte by=-36;
    double d3=-105.28929;
    int i=7, i1=-26, i2=-99, i3=-11, i9=-28861, i12=4, i13=-63027, i14=64173, iArr[]=new int[N];
    long lArr[]=new long[N];

    init(iArr, -46);
    init(lArr, -35002L);
    init(fArr1, -2.254F);

    for (i = 3; i < 88; ++i) {
        i1 &= i1;
        i1 += (((i * i) + i) - i2);
        i1 = i2;
        iArr[i] -= i2;
        switch (((i % 1) * 5) + 104) {
        case 107:
            by += (byte)(i * f);
            i1 *= iArr[i];
        }
    }
    for (i3 = 4; i3 < 83; i3++) {
        i1 >>= (int)lMeth(d3, i1);
        d3 -= i9;
        lFld *= (((by + i3) + (-(30765 / ((lArr[i3]++) | 1)))) * Integer.reverseBytes((int)(-119 * f)));
        vMeth();
        try {
            i1 = (i2 % 57);
            iFld = (i % -50352);
            i1 = (i9 / 24);
        } catch (ArithmeticException a_e) {}
        i2 -= (int)((fArr1[i3 + 1]--) - (i2++));
    }
    for (i12 = 4; i12 < 97; i12++) {
        i1 *= (int)((-(i * iFld)) >> lArr[i12]);
    }
    for (i13 = 3; i13 < 97; i13++) {
        i14 = 1;
        while (++i14 < 30) {
            lFld -= (i12 * (-(58 + (lFld += i2))));
            lFld += (long)(((++lFld) / ((long)(-(0.890F - iFld)) | 1)) * (f - (iFld--)));
            iFld = (int)(i1 - fArr1[i14]);
            lFld = i2;
            iArr[i14 + 1] -= (int)(lFld++);
            i2 -= (int)(((2704634649L / (i | 1)) % ((i - i12) | 1)) * ((iFld * i3) * (i1 = iFld)));
        }
    }

    System.out.println("i i1 i2 = " + i + "," + i1 + "," + i2);
    System.out.println("by f i3 = " + by + "," + Float.floatToIntBits(f) + "," + i3);
    System.out.println("d3 i9 i12 = " + Double.doubleToLongBits(d3) + "," + i9 + "," + i12);
    System.out.println("i13 i14 iArr = " + i13 + "," + i14 + "," + checkSum(iArr));
    System.out.println("lArr fArr1 = " + checkSum(lArr) + "," + Double.doubleToLongBits(checkSum(fArr1)));

    System.out.println("lFld iFld = " + lFld + "," + iFld);

    System.out.println("lMeth_check_sum: " + lMeth_check_sum);
    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
}
}