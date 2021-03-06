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

// Generated by Android Runtime Fuzzer tool (3.5.002). Sat Jul 11 23:31:11 2015
// srand = 16612790621703052677013446717101782825011970795781919899358422385514

package OptimizationTests.regression.test2293;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static boolean bArrFld[]=new boolean[N];

    static {
        init(bArrFld, false);
    }

    public static long vMeth_check_sum = 0;

public static void vMeth(double d, long l1) {

    int i3=36481, i4=-6, i5=217, i6=-25;

    for (i3 = 4; i3 < 90; ++i3) {
        i4 = (++i4);
        i4 += i3;
        for (i5 = 4; i5 > 1; i5--) {
            i4 += i5;
            i4 &= i3;
            for (i6 = 3; i6 < 95; i6++) {
                i4 += (int)((i4++) * Math.min(-721380549L, 49986 << i5));
            }
            i4 -= (int)(d++);
            i4 = (int)l1;
            i4 = (int)(((--i4) * l1) + (--i4));
            if ((long)i6 != (long)0) {
                vMeth_check_sum += Double.doubleToLongBits(d) + l1 + i3 + i4 + i5 + i6;
                return;
            }
        }
    }
    vMeth_check_sum += Double.doubleToLongBits(d) + l1 + i3 + i4 + i5 + i6;
}

public static void main(String[] strArr) {

    double d1=0.26747;
    long l=64959L, l2=0L, lArr[][]=new long[N][N];
    boolean b=false;
    float f=2.202F, fArr[]=new float[N];
    int i=-204, i1=60590, i2=-12, i7=-23636, i8=-5, i9=46, iArr[]=new int[N];

    init(lArr, 31192L);
    init(iArr, -74);
    init(fArr, -1.213F);

    i = 1;
    while (++i < 76) {
        b = (((-947610058L - i) * (16497 * (f - i))) != (i1--));
        i1 >>= (i | (i1++));
        l = (long)(i1 - ((l % 1.227F) + i1));
    }
    for (i2 = 4; i2 < 82; i2++) {
        bArrFld[i2] = b;
        vMeth(((l * 173) + (i - d1)) + (-(-(-i))), lArr[i2 - 1][i2 + 1] + i);
        l -= (long)((l++) - (i - 1.72830));
        i1 += i2;
        l >>= (iArr[i2 + 1]--);
        i7 = 96;
        while (--i7 > 0) {
            f = l2;
            lArr[i7][i2] += i7;
            i1 += (i1--);
            i8 = 1;
            do {
                switch ((i8 % 2) + 118) {
                case 118:
                    i1 += (--iArr[i2 - 1]);
                    fArr[i8 - 1] += i1;
                    iArr[i2] = (-4 + i2);
                    i1 += (((i8 * f) + f) - i9);
                case 119:
                    i1 -= (int)f;
                    i9 = (int)(l2 * ((l * l) * -12892));
                    i1 += (i8 + l2);
                    i1 -= (i1 *= ((i * i) - (i9--)));
                    break;
                }
            } while (++i8 < 7);
            if (true) break;
        }
    }

    System.out.println("i b f = " + i + "," + (b ? 1 : 0) + "," + Float.floatToIntBits(f));
    System.out.println("i1 l i2 = " + i1 + "," + l + "," + i2);
    System.out.println("d1 i7 l2 = " + Double.doubleToLongBits(d1) + "," + i7 + "," + l2);
    System.out.println("i8 i9 lArr = " + i8 + "," + i9 + "," + checkSum(lArr));
    System.out.println("iArr fArr = " + checkSum(iArr) + "," + Double.doubleToLongBits(checkSum(fArr)));

    System.out.println("bArrFld = " + checkSum(bArrFld));

    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
}
}
