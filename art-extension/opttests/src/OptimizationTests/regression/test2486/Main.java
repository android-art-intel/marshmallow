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

// Generated by Android Runtime Fuzzer tool (3.5.002). Fri Jul 17 19:06:47 2015
// srand = 1136461587594179453454733509392671324787519644278844032011185475697

package OptimizationTests.regression.test2486;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long lArrFld[]=new long[N];

    static {
        init(lArrFld, 7023247417744657331L);
    }

    public static long lMeth_check_sum = 0;
    public static long dMeth_check_sum = 0;

public static long lMeth(double d, int i1, int i2) {

    long l1=4L, lArr[]=new long[N];
    int i3=13, i5=211, iArr1[]=new int[N];
    short s=23819;
    float f1=46.119F, f2=-30.805F;

    init(lArr, -3411170204L);
    init(iArr1, -135);

    f1 = 1;
    while (++f1 < 88) {
        i2 <<= (int)((f2 = lArr[(int)(f1)]) + (168 - (-(217 * (--lArr[(int)(f1)])))));
    }
    for (i3 = 4; i3 < 90; ++i3) {
        i2 -= (int)l1;
        d = (2314L - ((i2 * l1) + (-64050 * iArr1[i3 + 1])));
    }
    for (int i4 : iArr1) {
        s <<= (short)iArr1[(i2 >>> 1) % N];
        f2 = -40414;
        l1 = (long)(((i1 + l1) * (f1 + i2)) + ((-14L >> (-(i4 + l1))) >>> i3));
        i5 = 1;
        do {
            f2 %= ((long)(f2) | 1);
        } while (++i5 < 77);
        i4 = (i4 - Integer.reverseBytes(i2));
    }
    long meth_res = Double.doubleToLongBits(d) + i1 + i2 + Float.floatToIntBits(f1) + Float.floatToIntBits(f2) + i3 +
        l1 + s + i5 + checkSum(lArr) + checkSum(iArr1);
    lMeth_check_sum += meth_res;
    return (long)meth_res;
}

public static double dMeth(float f3, int i6, int i7) {

    long l2=0L;
    int i8=-5, i9=39, iArr2[][]=new int[N][N];

    init(iArr2, -221);

    i8 = 1;
    do {
        l2 &= (i7--);
        iArr2[i8 + 1][i8 - 1] = i6;
        lArrFld[i8 - 1] -= -6481;
        try {
            i6 = (i8 % i7);
            i7 = (i8 / 240);
            i7 = (i6 % i6);
        } catch (ArithmeticException a_e) {}
    } while (++i8 < 86);
    i9 = 1;
    do {
        lArrFld[i9] -= (i7--);
        l2 = i7;
        iArr2[i9][i9] *= (int)l2;
        i7 += ((i6--) - (-i7));
        i6 ^= (int)((iArr2[i9][i9 + 1]++) + (--f3));
        f3 = (i8 - ((--i6) - 2));
    } while (++i9 < 88);
    long meth_res = Float.floatToIntBits(f3) + i6 + i7 + i8 + l2 + i9 + checkSum(iArr2);
    dMeth_check_sum += meth_res;
    return (double)meth_res;
}

public static void main(String[] strArr) {

    long l=1L;
    int i10=-4, i11=-41925, i12=-206, i13=-98, iArr[]=new int[N];
    double d1=67.129466;
    boolean b=true, bArr[][]=new boolean[N][N];
    byte by=-45;
    float f=1.721F, fArr[]=new float[N];

    init(iArr, 6);
    init(fArr, 0.556F);
    init(bArr, false);

    for (int i : iArr) {
        try {
            l = ((long)((i + f) - (l + 3L)) >>> lMeth(dMeth(f, i, i), i, ++iArr[(-11 >>> 1) % N]));
        }
        catch (NullPointerException exc) {
            l += l;
            for (i10 = 2; i10 < 81; i10++) {
                l = (-((i - i10) << (i10 * -341096430L)));
                f = (float)((++i) - d1);
                switch ((i10 % 2) + 2) {
                case 2:
                    i -= (int)(--l);
                    d1 *= (by *= (byte)(l * (-(l++))));
                    b = b;
                    break;
                case 3:
                    b = b;
                    l += (long)(d1 = (i--));
                    iArr[i10 - 1] = (int)f;
                    i = (int)(l += ((l + i10) - l));
                    break;
                default:
                    i11 -= (int)(((l - i11) + (f++)) + (++i11));
                }
            }
            if (b) {
                iArr[(13 >>> 1) % N] >>= (++i11);
            } else if (bArr[(i >>> 1) % N][(i >>> 1) % N] = (b && b)) {
                i11 = (int)(fArr[(-51 >>> 1) % N]++);
                l ^= l;
            } else {
                l = (++i);
            }
            l += (long)(Double.longBitsToDouble(i * i12) + ((--i12) * (d1 + i)));
        }
    }
    i13 = 1;
    do {
        i12 = ((i11--) ^ ((++i12) + (i11 *= i10)));
        i11 = i13;
    } while (++i13 < 90);

    System.out.println("l f i10 = " + l + "," + Float.floatToIntBits(f) + "," + i10);
    System.out.println("d1 by b = " + Double.doubleToLongBits(d1) + "," + by + "," + (b ? 1 : 0));
    System.out.println("i11 i12 i13 = " + i11 + "," + i12 + "," + i13);
    System.out.println("iArr fArr bArr = " + checkSum(iArr) + "," + Double.doubleToLongBits(checkSum(fArr)) + "," +
        checkSum(bArr));

    System.out.println("lArrFld = " + checkSum(lArrFld));

    System.out.println("lMeth_check_sum: " + lMeth_check_sum);
    System.out.println("dMeth_check_sum: " + dMeth_check_sum);
}
}
