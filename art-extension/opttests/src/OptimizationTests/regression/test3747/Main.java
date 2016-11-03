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

// Generated by Android Runtime Fuzzer tool (4.1.002). Sun Apr  3 14:55:46 2016
// srand = 68076840519803635989659827619182084627

package OptimizationTests.regression.test3747;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long instanceCount=1488701506508021985L;
    public static byte byFld=-113;

    public static long lMeth_check_sum = 0;

public static long lMeth(int i7) {

    int i8=-184, i9=13, i10=-67;
    double d1=0.91076;
    float f2=-2.657F;
    long lArr1[]=new long[N];
    short sArr[]=new short[N];

    init(lArr1, 55986L);
    init(sArr, (short)-19164);

    for (i8 = 1; i8 < 82; i8++) {
        for (i9 = 2; i9 < 34; ++i9) {
            Main.instanceCount -= ((Main.instanceCount * (--i7)) + (--i7));
            d1 += ((i7++) - ((i7++) - (-(5695 + (i8 + i9)))));
            i7 += (((i9 * i9) + i7) - i8);
            if (i7 != 0) {
            }
            i7 -= (int)lArr1[i8];
            for (i10 = 34; i10 > i9; i10--) {
                sArr[i10 + 1] <<= (short)i10;
                i7 -= (int)f2;
                Main.instanceCount *= (Main.instanceCount + (Main.instanceCount++));
                Main.instanceCount = i7;
            }
        }
    }
    long meth_res = i7 + i8 + i9 + Double.doubleToLongBits(d1) + i10 + Float.floatToIntBits(f2) + checkSum(lArr1) +
        checkSum(sArr);
    lMeth_check_sum += meth_res;
    return (long)meth_res;
}

public static void main(String[] strArr) {

    int i=51169, i1=11, i2=34012, i3=-1, i4=0, i5=49874, i6=4079, iArr[]=new int[N], iArr1[]=new int[N], iArr2[]=new
        int[N];
    short s=-17762;
    boolean b=true;
    float f1=-113.298F;
    long lArr[]=new long[N];
    double dArr[]=new double[N];

    init(lArr, -711903145970246087L);
    init(iArr, 33345);
    init(iArr1, -49744);
    init(iArr2, 7);
    init(dArr, -1.128406);

    for (i = 4; i < 95; i++) {
        s -= (short)(((-(i1--)) * (i - i1)) - (i1 = i));
    }
    for (i2 = 4; i2 < 87; ++i2) {
        float f=36.619F;
        lArr[i2] += iArr[i2];
        iArr1[i2 - 1] <<= (int)((f * (i - i1)) + ((++f) - (++i1)));
        i3 = 38;
        do {
            double d=70.44128;
            b = (Main.instanceCount == ((long)(i1 + d) >>> i3));
            i1 -= iArr2[i3];
            i1 += (i3 * i);
            for (i4 = 1; i4 < 15; ++i4) {
                i1 = (int)((Main.instanceCount + Math.min(i, i3)) - ((Main.instanceCount - 28861) - (Main.instanceCount
                    + Main.byFld)));
                Main.instanceCount = i1;
                i1 -= (int)Main.instanceCount;
                iArr1[i2] >>>= (int)(Float.intBitsToFloat(i2) + (Main.instanceCount << lArr[i4 + 1]));
                Main.instanceCount += (long)(f--);
                i1 += (i4 * i4);
            }
            dArr[i2] = (iArr[i3 + 1] -= (int)((Main.instanceCount + d) - (f - i1)));
            i1 = i2;
            i1 = (((i1 >> 4) - i2) - (++i1));
        } while (--i3 > 0);
        dArr[i2 - 1] = (~(long)((i1 - f) * (i1 - i)));
        i1 += ((i + (--iArr[i2 - 1])) + ((i1 -= i5) | (i3 & -91)));
    }
    for (i6 = 82; i6 > 3; --i6) {
        f1 *= ((Main.instanceCount + (Main.instanceCount * Main.instanceCount)) - i6);
        Main.instanceCount *= (long)((i1--) * ((Main.instanceCount - i1) + (-(f1 + i1))));
        Main.instanceCount += ((++i1) - lMeth(-2));
    }

    System.out.println("i s i1 = " + i + "," + s + "," + i1);
    System.out.println("i2 i3 b = " + i2 + "," + i3 + "," + (b ? 1 : 0));
    System.out.println("i4 i5 i6 = " + i4 + "," + i5 + "," + i6);
    System.out.println("f1 lArr iArr = " + Float.floatToIntBits(f1) + "," + checkSum(lArr) + "," + checkSum(iArr));
    System.out.println("iArr1 iArr2 dArr = " + checkSum(iArr1) + "," + checkSum(iArr2) + "," +
        Double.doubleToLongBits(checkSum(dArr)));

    System.out.println("Main.instanceCount Main.byFld = " + Main.instanceCount + "," + Main.byFld);

    System.out.println("lMeth_check_sum: " + lMeth_check_sum);
}
}