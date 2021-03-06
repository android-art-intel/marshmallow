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

// Generated by Android Runtime Fuzzer tool (3.5.002). Sun Jun 21 02:40:29 2015
// srand = 12932704710205268337980940538765664995045203008331416304057130122399

package OptimizationTests.regression.test2197_1;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


    public static long vMeth_check_sum = 0;
    public static long iMeth_check_sum = 0;

public static void vMeth() {

    short s=10970;
    int i=1030, i1=1, i2=105, i3=1, iArr[]=new int[N];
    float f=2.542F, fArr[]=new float[N];
    long l=3661886431L, l1=1839418237L;

    init(iArr, 195);
    init(fArr, -1.674F);

    for (i = 1; i < 94; i++) {
        s *= (short)(++i1);
        iArr[i + 1] = -20659;
        i1 = (int)(((-192 * (26929 / ((l * i1) | 1))) - (++l1)) - ((i * 10) - (14L * (-8688 - (iArr[i] = 22643)))));
        i1 -= (int)(i1 - (l1 - (i1 + l1)));
        i1 >>>= ((i1--) - ((i + i1) * (i + 72)));
        i1 = (int)f;
    }
    i2 = 1;
    while (++i2 < 95) {
        i1 -= (-i3);
        i1 += (((i2 * i) + i1) - i2);
        i1 |= (int)((i1++) - (--fArr[i2 - 1]));
        if (Math.max((i2 + i1) + (i1++), i3) != i3) continue;
    }
    vMeth_check_sum += i + s + i1 + l + l1 + Float.floatToIntBits(f) + i2 + i3 + checkSum(iArr) +
        Double.doubleToLongBits(checkSum(fArr));
}

public static int iMeth(int i6, int i7) {

    float f2=-84.197F;

    f2 -= (((-(i7--)) >>> (++i6)) - i7);
    long meth_res = i6 + i7 + Float.floatToIntBits(f2);
    iMeth_check_sum += meth_res;
    return (int)meth_res;
}

public static void main(String[] strArr) {

    double d=103.101371;
    short s1=16005;
    byte by=-103;
    int i4=1, i5=23759, i8=-12, i9=163, i10=183, i11=-22191, i12=-212, iArr1[]=new int[N];
    long l2=15782L, l3=-4104602129L, l4=-16L, lArr[]=new long[N];
    float f1=121.1006F, fArr1[]=new float[N];

    init(iArr1, -20685);
    init(fArr1, 1.422F);
    init(lArr, 13L);

    vMeth();
    for (i4 = 4; i4 < 76; ++i4) {
        i5 += (i5 -= (iArr1[i4 + 1] >>= (--iArr1[i4])));
        i5 += (int)(-16292 - ((++f1) - iArr1[i4 - 1]));
        i5 += (((i4 * f1) + i4) - i5);
        i5 += (int)(l2 * (iMeth(i4, -10) / ((l2 + i5) | 1)));
        l2 -= (((-(-100 * (++i5))) + (l3 - i4)) - ((l2 >> by) - Integer.reverseBytes(i4)));
        iArr1[i4 - 1] -= (int)(d * (-12 * (-(156 + (i5--)))));
        i5 ^= (i5++);
        for (i8 = i4; i8 < 81; i8++) {
            i5 = ((i5 += (i5 * i5)) + (i5 + i4));
            fArr1[i8 - 1] += (i5++);
            i5 += (int)(lArr[i8] = (iArr1[i4 + 1]++));
            by = (byte)(93 * i9);
            i9 /= (int)((((by = (byte)l4) - i5) + (i5--)) | 1);
            for (i10 = 1; i10 < i4; i10++) {
                iArr1[i4 + 1] = i9;
                l3 += i10;
            }
        }
        i11 = 1;
        do {
            d *= (iArr1[i11] + i8);
            l2 >>= i10;
            i9 = (int)(((l2++) - (by - i10)) + (i10 * (i4 * f1)));
            f1 -= i10;
            s1 += (short)(((i11 * l4) + i11) - i11);
        } while (++i11 < 59);
        i12 -= (int)f1;
    }

    System.out.println("i4 i5 f1 = " + i4 + "," + i5 + "," + Float.floatToIntBits(f1));
    System.out.println("l2 l3 by = " + l2 + "," + l3 + "," + by);
    System.out.println("d i8 i9 = " + Double.doubleToLongBits(d) + "," + i8 + "," + i9);
    System.out.println("l4 i10 i11 = " + l4 + "," + i10 + "," + i11);
    System.out.println("s1 i12 iArr1 = " + s1 + "," + i12 + "," + checkSum(iArr1));
    System.out.println("fArr1 lArr = " + Double.doubleToLongBits(checkSum(fArr1)) + "," + checkSum(lArr));


    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
    System.out.println("iMeth_check_sum: " + iMeth_check_sum);
}
}
