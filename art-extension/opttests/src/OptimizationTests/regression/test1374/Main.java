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

// Generated by Dalvik Fuzzer tool (3.5.001). Sat Jan 31 07:13:38 2015
package OptimizationTests.regression.test1374;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


    public static long sMeth_check_sum = 0;

public static short sMeth(long l1, long l2, int i1) {

    long lArr[]=new long[N];
    byte by=121;
    int i2=-59189, i3=-6, iArr[]=new int[N];
    short s=704;
    double d=2.105713, dArr[]=new double[N];
    float f=-122.82F, fArr[]=new float[N];

    init(lArr, -28160L);
    init(iArr, 7259);
    init(fArr, -115.766F);
    init(dArr, 122.100640);

    i2 = 1;
    while ((i2 += 2) < 76) {
        i3 = (int)l2;
        l2 -= (long)(((by + -1.227F) + lArr[i2 - 1]) * i2);
        i3 -= ((i1 & (i2 + i1)) - ((iArr[i2] >>= i1) - i2));
        for (d = 1; d < 46; d++) {
            i3 >>= -45;
            iArr[i2] = (int)((--f) + f);
            s *= (short)i1;
            fArr[(int)(d)] = (i2 - iArr[(int)(d)]);
            i1 -= (s * i3);
            i1 *= (int)((dArr[i2]--) * l2);
            i1 *= (int)((i1--) << (long)((i2 + 0.151F) + (-3143 - (i2 + -209))));
        }
    }
    long meth_res = l1 + l2 + i1 + i2 + i3 + by + Double.doubleToLongBits(d) + Float.floatToIntBits(f) + s +
        checkSum(lArr) + checkSum(iArr) + Double.doubleToLongBits(checkSum(fArr)) +
        Double.doubleToLongBits(checkSum(dArr));
    sMeth_check_sum += meth_res;
    return (short)meth_res;
}

public static void main(String[] strArr) {

    byte by1=0, byArr[]=new byte[N];
    long l=3653724016606480421L, lArr1[]=new long[N];
    int i=-53859, i4=-203, i5=1, i6=-42349, i7=-62054, i8=178, i9=101, i10=0, iArr1[]=new int[N];
    boolean bArr[]=new boolean[N];
    short sArr[]=new short[N];
    double d1=0.116757, dArr1[]=new double[N];
    float f1=0.861F, fArr1[]=new float[N];

    init(sArr, (short)7788);
    init(lArr1, 7341073130216741268L);
    init(iArr1, 103);
    init(dArr1, 0.19457);
    init(bArr, false);
    init(fArr1, 1.176F);
    init(byArr, (byte)-48);

    for (i = 3; i < 84; ++i) {
        l = (long)((-sMeth(l, l, 202)) + ((13 * (i * l)) % ((long)d1 | 1)));
        i4 -= (-81 * (sArr[(i >>> 1) % N] - (i - i4)));
    }
    for (i5 = 1; i5 < 89; i5 += 2) {
        l <<= (by1 * (i4 - (i4 + i5)));
        for (i6 = 2; i6 < 69; ++i6) {
            l <<= (--i7);
            i7 >>= (int)(f1 + (--l));
            i4 += i5;
            i4 -= (-i7);
            l = l;
            l += (l = i4);
        }
        lArr1[i5] = ((l - i8) * (Float.floatToRawIntBits(f1) >>> iArr1[i5]));
        i4 += (((i5 * i7) + i7) - i5);
        for (i9 = 3; i9 < 98; i9++) {
            dArr1[i9 - 1] += (((by1 >>> l) + (-(l * i9))) * (d1++));
            i4 += (int)l;
            i8 |= Math.abs(i4--);
            if (bArr[i9 + 1]) break;
            i4 = (((++i8) + i) * iArr1[i5 - 1]);
            f1 = l;
            fArr1[i5] = (((i9 + i7) - iArr1[(i8 >>> 1) % N]) - (i7 <<= (--byArr[i5 - 1])));
            for (i10 = 5; i10 < 94; i10++) {
                l += (i10 * i10);
                i4 -= (++iArr1[i9]);
            }
        }
    }

    System.out.println("i l d1 = " + i + "," + l + "," + Double.doubleToLongBits(d1));
    System.out.println("i4 i5 by1 = " + i4 + "," + i5 + "," + by1);
    System.out.println("i6 i7 f1 = " + i6 + "," + i7 + "," + Float.floatToIntBits(f1));
    System.out.println("i8 i9 i10 = " + i8 + "," + i9 + "," + i10);
    System.out.println("sArr lArr1 iArr1 = " + checkSum(sArr) + "," + checkSum(lArr1) + "," + checkSum(iArr1));
    System.out.println("dArr1 bArr fArr1 = " + Double.doubleToLongBits(checkSum(dArr1)) + "," + checkSum(bArr) + "," +
        Double.doubleToLongBits(checkSum(fArr1)));
    System.out.println("byArr = " + checkSum(byArr));


    System.out.println("sMeth_check_sum: " + sMeth_check_sum);
}
}
