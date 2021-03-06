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

// Generated by Dalvik Fuzzer tool (3.5.001). Wed Jan 28 11:15:33 2015
package OptimizationTests.regression.test1309;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static float fFld=2.210F;
    public static int iFld=52518;

    public static long vMeth_check_sum = 0;
    public static long vMeth1_check_sum = 0;

public static void vMeth() {

    byte by2=-41;
    float f1=0.533F;
    long l2=-2506L, lArr[]=new long[N];
    int i5=14, i6=3, iArr1[]=new int[N];
    double d2=1.81992;

    init(iArr1, -11574);
    init(lArr, -11L);

    for (i5 = 4; i5 < 89; i5++) {
        i6 |= (i6++);
        iArr1[i5] = (int)((-(f1 -= by2)) + (l2 + (l2 + i5)));
        lArr[i5] -= (i6 % ((++l2) | 1));
        i6 = (int)((iArr1[i5 + 1] = (i5 + i6)) + ((-(-d2)) - (i5 + l2)));
        i6 |= (int)(l2 <<= (iArr1[i5 - 1] += (i6--)));
    }
    vMeth_check_sum += i5 + i6 + Float.floatToIntBits(f1) + by2 + l2 + Double.doubleToLongBits(d2) + checkSum(iArr1) +
        checkSum(lArr);
}

public static void vMeth1(byte by3, boolean b1, long l3) {

    byte byArr[]=new byte[N];
    short s=-17608;
    int i7=121, i8=-144, i9=60204, i10=15587, i11=117, i12=8, iArr2[]=new int[N];

    init(byArr, (byte)-76);
    init(iArr2, -1);

    for (i7 = 4; i7 < 77; i7 += 3) {
        fFld -= ((++l3) - -2793038923747957399L);
        for (iFld = 1; iFld < i7; iFld++) {
            for (i8 = i7; i8 > 1; i8--) {
                i9 = (int)((fFld -= i7) - (i8 - (i9 + -5046958849725918475L)));
            }
            l3 += (++byArr[i7]);
            fFld += (((iFld * l3) + i7) - iFld);
            s -= (short)-119;
            i9 = (i9--);
            i9 += (int)((-(-23482 * (i9 - i7))) + (++l3));
            fFld += ((i10 * iArr2[iFld + 1]) + ((--i11) % ((i11 + -12) | 1)));
        }
        i12 = (i10++);
    }
    vMeth1_check_sum += by3 + (b1 ? 1 : 0) + l3 + i7 + i8 + i9 + s + i10 + i11 + i12 + checkSum(byArr) +
        checkSum(iArr2);
}

public static void main(String[] strArr) {

    float f=0.752F;
    byte by=93, by1=-66, byArr1[][]=new byte[N][N];
    long l=5990634409366323833L, l1=32278L, lArr1[]=new long[N];
    short s1=16500;
    boolean b=true, b2=true, bArr[]=new boolean[N];
    int i=-61815, i1=231, i2=15295, i3=11, i4=-7764, i13=-77, iArr[]=new int[N];
    double d=0.127961, d1=-98.75362;

    init(iArr, 51);
    init(lArr1, -43540L);
    init(byArr1, (byte)-60);
    init(bArr, true);

    if (((++i2) - l1) != (i - ((d - i1) * (46249 + (-(++i13)))))) {
        switch (((11928 >>> 1) % 3) + 84) {
        case 84:
            for (i = 5; i < 92; ++i) {
                for (i1 = 1; i1 < 2; i1++) {
                    d += (by *= (byte)(l + (i1 - i)));
                    l ^= (--i2);
                    d = f;
                    l = (~((i2 + i2) - (i2++)));
                    i2 += i1;
                    if (b) {
                        i3 = (int)(((l % ((long)d1 | 1)) - (l - by)) + ((-(f - 3501925805808115091L)) - (--i3)));
                    } else if (b = (((i * by) == (i - i)) & (b = (1 <= i)))) {
                        by -= (byte)(i2++);
                        l = (long)d1;
                    } else {
                        i3 = (i2 + ((i1 + i2) + (i2 -= i3)));
                        i2 = by1;
                        b = (((122 % ((long)(-3326963184L + f) | 1)) - (-(l * i1))) > (Float.floatToRawIntBits(-2.806F)
                            + (i2++)));
                        i2 = i1;
                    }
                }
                i2 = (int)((l--) * iArr[i]);
                for (i4 = i; i4 < 88; i4 += 3) {
                    i2 = (int)Math.min(--l1, l--);
                    by -= (byte)(l1 - ((++l1) * (i2 + f)));
                    f = ((-i2) - (f = (-(++i2))));
                    by *= (byte)((i1 + f) * (-(--i2)));
                    i2 ^= (((--iArr[i4]) + (-(i4 + i))) - (i3++));
                    i3 -= (i3 * (-(i3 += i2)));
                }
            }
        case 85:
            i2 = i4;
            break;
        case 86:
            vMeth();
            break;
        default:
            vMeth1(by1, b = b2, lArr1[(i1 >>> 1) % N]);
        }
    } else if (bArr[(i2 >>> 1) % N] = (bArr[(i >>> 1) % N] = ((-36662 < (i3 + i3)) && (i == l1)))) {
        lArr1[(i4 >>> 1) % N] = (long)((iArr[(i4 >>> 1) % N] - s1) + -75.285F);
    } else if (((-9 * (iFld * 52)) + i4) >= s1) {
        vMeth1((byte)(byArr1[(i >>> 1) % N][(i >>> 1) % N]++), b, (-(i13++)) * (--i13));
    } else {
        l1 += (((10 + (i13 - l)) | (by - 10282)) + s1);
    }

    System.out.println("i i1 d = " + i + "," + i1 + "," + Double.doubleToLongBits(d));
    System.out.println("by l i2 = " + by + "," + l + "," + i2);
    System.out.println("f i3 d1 = " + Float.floatToIntBits(f) + "," + i3 + "," + Double.doubleToLongBits(d1));
    System.out.println("b by1 i4 = " + (b ? 1 : 0) + "," + by1 + "," + i4);
    System.out.println("l1 b2 s1 = " + l1 + "," + (b2 ? 1 : 0) + "," + s1);
    System.out.println("i13 iArr lArr1 = " + i13 + "," + checkSum(iArr) + "," + checkSum(lArr1));
    System.out.println("byArr1 bArr = " + checkSum(byArr1) + "," + checkSum(bArr));

    System.out.println("fFld iFld = " + Float.floatToIntBits(fFld) + "," + iFld);

    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
    System.out.println("vMeth1_check_sum: " + vMeth1_check_sum);
}
}
