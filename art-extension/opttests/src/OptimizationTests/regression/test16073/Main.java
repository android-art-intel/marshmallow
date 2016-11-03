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

// Generated by Dalvik Fuzzer tool (3.4.002). Mon Jan  5 16:30:34 2015
package OptimizationTests.regression.test16073;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


    public static long vMeth_check_sum = 0;
    public static long vMeth1_check_sum = 0;

public static void vMeth(int i5, float f1, int i6) {

    double d1=90.713;
    float fArr1[]=new float[N];
    long l1=3941413815280645094L, lArr1[]=new long[N];
    int i7=53583, i8=-40160, i9=-25653, i10=-20694, i11=64691, i12=19087, iArr1[]=new int[N];

    init(iArr1, -42129);
    init(lArr1, -8233179144134599170L);
    init(fArr1, 33.258F);

    for (i7 = 3; i7 < 89; i7++) {
        i6 ^= ((i6--) * (iArr1[i7]--));
        i8 <<= (int)l1;
        for (i9 = i7; i9 < 79; i9++) {
            l1 = i10;
            switch ((((((int)(31898 - (l1 - i10))) >>> 1) % 4) * 5) + 86) {
            case 99:
                i10 = (int)d1;
                for (i11 = 63; i11 > i9; --i11) {
                    l1 /= ((i10 * (++iArr1[(i10 >>> 1) % N])) | 1);
                    iArr1[i9 - 1] *= 33004;
                    d1 /= (((i6--) + ((l1 - i12) + -10016)) | 1);
                    l1 = (i8 += (int)(--lArr1[i9]));
                }
                break;
            case 106:
                iArr1[i7] >>>= (int)(l1 = ((l1 + i9) * iArr1[i7 + 1]));
                break;
            case 103:
                lArr1[i9 + 1] ^= l1;
            case 100:
                fArr1[i9] = ((++l1) & i8);
                break;
            default:
                l1 += (-49579 + (i9 * i9));
            }
        }
    }
    vMeth_check_sum += i5 + Float.floatToIntBits(f1) + i6 + i7 + i8 + l1 + i9 + i10 + Double.doubleToLongBits(d1) + i11
        + i12 + checkSum(iArr1) + checkSum(lArr1) + Double.doubleToLongBits(checkSum(fArr1));
}

public static void vMeth1(int i14) {

    double d2=-117.153, dArr1[]=new double[N];
    float f2=52.515F;
    long l2=3105419824215392468L;
    boolean b1=false;
    int i15=-27152, iArr2[]=new int[N];
    short s1=-4109;

    init(iArr2, -26451);
    init(dArr1, 52.45);

    i15 = 1;
    while (++i15 < 95) {
        l2 <<= 21083;
        l2 += (i15 * s1);
        l2 |= 15917;
        l2 = i15;
        b1 = (((l2 - -2.976F) - (l2 + i15)) != i15);
        i14 >>>= -19499;
        iArr2[i15 + 1] = (int)(i14 + ((2390 - (l2 * i14)) * (l2 - d2)));
    }
    for (f2 = 3; f2 < 77; ++f2) {
        dArr1[(int)(f2 + 1)] -= (-(++dArr1[(int)(f2 + 1)]));
        l2 -= i14;
        i14 <<= iArr2[(int)(f2 + 1)];
    }
    vMeth1_check_sum += i14 + i15 + l2 + s1 + (b1 ? 1 : 0) + Double.doubleToLongBits(d2) + Float.floatToIntBits(f2) +
        checkSum(iArr2) + Double.doubleToLongBits(checkSum(dArr1));
}

public static void main(String[] strArr) {

    double d=-46.265, dArr[]=new double[N];
    float f=108.712F, fArr[]=new float[N];
    byte by=93;
    long l=3264309641481796098L, lArr[]=new long[N];
    boolean b=false;
    int i=44047, i1=19158, i2=25392, i3=59666, i4=-54825, i13=-54908, iArr[]=new int[N];
    short s=23601, sArr[]=new short[N];

    init(dArr, 117.367);
    init(iArr, -9806);
    init(lArr, -9188487988011351382L);
    init(fArr, -62.810F);
    init(sArr, (short)-25418);

    for (i = 5; i < 85; ++i) {
        for (i1 = 1; i1 < 97; i1++) {
            dArr[i] = ((++by) % (i | 1));
            i2 *= (i2 + ((++by) + (s = s)));
            iArr[i] = (int)(i2 *= (int)(lArr[i] + (i + f)));
            f = (float)(((i3 * i1) + (-19189 >>> lArr[i])) * -64.869);
            i3 += -15628;
            f += i1;
            fArr[i] += sArr[i1 - 1];
            l += (long)(i3 + ((i >> i1) - fArr[i + 1]));
            l += (i1 + f);
            switch (((i1 % 3) * 5) + 18) {
            case 31:
                i3 += (-i);
                l = (long)d;
                i4 = 1;
                while (++i4 < 55) {
                    iArr[i4] *= i3;
                }
                l -= (long)((f--) + (--l));
            case 20:
                i2 = (int)(((f - i) * 14491) + (i3--));
                break;
            case 22:
                i2 *= (int)((s--) - (l - (i4 - i4)));
                b = (43.270F > i3);
                break;
            }
        }
        vMeth(i2, (--lArr[i]) * ((-56154 * (++f)) - (-(i2 - 2674))), i1);
        by -= (byte)(++i2);
    }
    for (i13 = 5; i13 < 96; ++i13) {
        i3 += iArr[i13 - 1];
    }
    vMeth1(--iArr[(i3 >>> 1) % N]);

    System.out.println("i i1 by = " + i + "," + i1 + "," + by);
    System.out.println("i2 s f = " + i2 + "," + s + "," + Float.floatToIntBits(f));
    System.out.println("i3 l d = " + i3 + "," + l + "," + Double.doubleToLongBits(d));
    System.out.println("i4 b i13 = " + i4 + "," + (b ? 1 : 0) + "," + i13);
    System.out.println("dArr iArr lArr = " + Double.doubleToLongBits(checkSum(dArr)) + "," + checkSum(iArr) + "," +
        checkSum(lArr));
    System.out.println("fArr sArr = " + Double.doubleToLongBits(checkSum(fArr)) + "," + checkSum(sArr));


    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
    System.out.println("vMeth1_check_sum: " + vMeth1_check_sum);
}
}
