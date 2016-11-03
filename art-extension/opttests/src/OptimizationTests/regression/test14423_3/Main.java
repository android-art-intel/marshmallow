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

// Generated by Android Runtime Fuzzer tool (3.5.002). Sun Jun 21 16:05:06 2015
// srand = 12760893585802880995140876850241890297042269973950004815553312497236

package OptimizationTests.regression.test14423_3;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


    public static long iMeth_check_sum = 0;

public static int iMeth(long l) {

    long lArr[]=new long[N];
    byte by=124;
    boolean b=true;
    short s=-21330;
    double d1=9.98279;
    int i1=-12, i2=10, i3=86, i4=-32897, i5=-6, i6=-2, i7=14, iArr[]=new int[N];
    float f=1.655F;

    init(lArr, -9L);
    init(iArr, -30);

    for (i1 = 2; i1 < 82; ++i1) {
        l *= by;
    }
    for (i2 = 3; i2 < 98; i2++) {
        lArr[i2] -= (long)((Integer.reverseBytes(8669) + (d1 + f)) - ((i1 * -41204) * Math.min(i1, i1)));
        s = (short)((--i3) ^ (i4++));
    }
    if (b = b) {
        i5 = 89;
        while (--i5 > 0) {
            if ((((f - l) + (i4 - l)) + 39) != (iArr[i5] + i7)) {
                by <<= (byte)iArr[i5];
                i3 *= (int)(Math.abs(l) + (-lArr[i5 - 1]));
                i4 *= (int)(i4 + (++l));
            } else if ((((s - 1.375F) + (-(0 + (-16204 - (i1 + i4))))) * ((i6 * i6) - (d1++))) == (((++i3) - (i6 - i1))
                ^ i4)) {
                b = (b = ((i3 != -67.745F) & (25616 == (++i3))));
            } else if (!(((10L / (i5 | 1)) <= (iArr[i5 - 1] >>>= (int)-8952978999664000338L)) && (!(f <= 8)))) {
                iArr[i5 + 1] = i4;
            } else {
                i6 /= (int)((((i5 * i6) + (l - i6)) + ((161 + (i7--)) + (l - i5))) | 1);
            }
        }
    } else {
        lArr[(-8555 >>> 1) % N] *= i4;
    }
    long meth_res = l + i1 + by + i2 + Double.doubleToLongBits(d1) + Float.floatToIntBits(f) + s + i3 + i4 + i5 + (b ?
        1 : 0) + i6 + i7 + checkSum(lArr) + checkSum(iArr);
    iMeth_check_sum += meth_res;
    return (int)meth_res;
}

public static void main(String[] strArr) {

    byte by1=27;
    long l1=3019986376405078905L, l2=2L, lArr1[]=new long[N];
    boolean b1=true;
    short s1=32461;
    int i=13, i8=-29268, i9=-9384, i10=52816, iArr1[]=new int[N];
    double d=2.67938, d2=-109.57901, dArr[]=new double[N];
    float f1=66.67F, fArr[]=new float[N];

    init(iArr1, -2);
    init(lArr1, 28L);
    init(fArr, 1.971F);
    init(dArr, 108.93647);

    for (d = 3; d < 78; d++) {
        i >>>= Math.max(iMeth((long)(i - -33.619F)), (int)(i + (5 * (-3047474164L * l1))));
        i8 = 1;
        while (++i8 < 98) {
            i *= (int)(((i *= 232) - (i + i)) + Float.intBitsToFloat(i));
        }
        l1 <<= ((iArr1[(int)(d - 1)] * iArr1[(int)(d)]) * iMeth(i * i8));
        f1 -= ((-13L + (i8 * i)) * (++l1));
        if (false) {
            i = (i - iArr1[(int)(d)]);
        } else if ((l2 >>= (iMeth(l2) + 417156568L)) != (108 - i)) {
            i += (int)(d - l1);
        } else if (false) {
            for (i9 = (int)d; i9 < 14; i9++) {
                i = (int)(f1 + (Float.intBitsToFloat(i8) - (i8 + i9)));
                i >>>= (int)(l1 | (--i));
                d2 = i;
                i += (i9 ^ i);
                i *= (int)lArr1[(int)(d - 1)];
                i += (((i--) * (i9 - 12)) + i9);
                d2 += -72;
                s1 = (short)((i8 + (i + i8)) << (--i));
                for (i10 = 1; i10 < 64; ++i10) {
                    if (!b1) {
                        iArr1[i10 - 1] -= (i -= (-(i8 | i10)));
                        f1 = ((iArr1[(int)(d + 1)] - (i9 - i)) * (l1 + i10));
                    } else if ((((s1 * l1) * (--l1)) - ((l2 * d2) + (dArr[(int)(d + 1)]--))) > i8) {
                        i *= i10;
                    } else if (b1 = (((i9 + i10) - l1) <= l1)) {
                        l1 -= (long)(l2 * fArr[(int)(d)]);
                        i += 48690;
                    } else {
                        by1 -= (byte)i10;
                    }
                }
            }
        }
    }

    System.out.println("d i l1 = " + Double.doubleToLongBits(d) + "," + i + "," + l1);
    System.out.println("i8 f1 i9 = " + i8 + "," + Float.floatToIntBits(f1) + "," + i9);
    System.out.println("d2 s1 i10 = " + Double.doubleToLongBits(d2) + "," + s1 + "," + i10);
    System.out.println("l2 b1 by1 = " + l2 + "," + (b1 ? 1 : 0) + "," + by1);
    System.out.println("iArr1 lArr1 fArr = " + checkSum(iArr1) + "," + checkSum(lArr1) + "," +
        Double.doubleToLongBits(checkSum(fArr)));
    System.out.println("dArr = " + Double.doubleToLongBits(checkSum(dArr)));


    System.out.println("iMeth_check_sum: " + iMeth_check_sum);
}
}
