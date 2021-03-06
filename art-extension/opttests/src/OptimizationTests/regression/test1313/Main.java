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

// Generated by Dalvik Fuzzer tool (3.5.001). Wed Jan 28 01:52:08 2015
package OptimizationTests.regression.test1313;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long lArrFld[]=new long[N];
    public static float fFld=38.450F;
    public static int iFld=60615;

    static {
        init(lArrFld, -154L);
    }

    public static long iMeth_check_sum = 0;

public static int iMeth(int i2, double d) {

    short s1=2842;
    byte by=6;
    float fArr[]=new float[N];
    long l1=45346L;
    int i3=-3, i4=-37687, i5=75, iArr1[]=new int[N];

    init(iArr1, 85);
    init(fArr, 0.273F);

    i3 = 80;
    while (--i3 > 0) {
        i4 = 1;
        do {
            i2 = (int)(((i2 <<= (int)-7L) * (i4 + 8L)) + (i3 - (--i2)));
            s1 += (short)(d - (l1 - (i2++)));
            i2 |= i3;
            i2 >>= (int)((lArrFld[i4 - 1] * (++i2)) * i2);
            fFld += (-53L + (i4 * i4));
            d = ((i4 - (d * 19512)) * ((-241 >> i2) >> l1));
        } while (++i4 < i3);
        i2 += (((i3 + i3) + i3) + (i2 + (-33213 + (++iArr1[i3 - 1]))));
        for (i5 = 1; i5 < i3; ++i5) {
            fArr[i5] = (9 + (--fArr[i5 + 1]));
            by += (byte)(i5 * i5);
        }
    }
    long meth_res = i2 + Double.doubleToLongBits(d) + i3 + i4 + s1 + l1 + i5 + by + checkSum(iArr1) +
        Double.doubleToLongBits(checkSum(fArr));
    iMeth_check_sum += meth_res;
    return (int)meth_res;
}

public static void main(String[] strArr) {

    boolean b=true;
    short s=-3947, sArr[]=new short[N];
    byte by1=23;
    float fArr1[]=new float[N];
    double d1=46.104126, d2=0.69989, d3=121.16158;
    long l=-1793248522L;
    int i=-7, i1=127, i6=-3, iArr[]=new int[N];

    init(iArr, 89);
    init(fArr1, 2.959F);
    init(sArr, (short)16597);

    for (i = 4; i < 98; i++) {
        i1 -= (int)(l++);
        i1 = iArr[i + 1];
        s *= (short)iMeth((int)((d1 + i) - lArrFld[i]), Long.reverseBytes(5699219824803257667L) * l);
        l *= ((iMeth(i, d2) + (i + l)) >> (--l));
        i1 >>= by1;
        d3 += (l++);
        iFld = 1;
        while (++iFld < 9) {
            fArr1[iFld] = ((fArr1[i] * iFld) - (i1 - 12569));
            i1 += ((long)iFld | (long)i1);
            try {
                i1 -= (int)(i1 = (int)Long.reverseBytes((long)(d3 + d3)));
                if (b) break;
                i1 = ((i1 /= i) - (++i1));
                i1 -= (int)(d2 + (fFld--));
                i1 = (i1 = (--iArr[iFld - 1]));
                i1 += iFld;
            }
            catch (ArithmeticException exc) {
                iArr[i] += 181;
                l += (i % ((iFld - (l - iFld)) | 1));
            }
            i1 = (sArr[iFld + 1]--);
            i6 = 1;
            while (++i6 < i) {
                i1 += (int)(((fFld++) / (l | 1)) + (--i1));
                i1 = (i1--);
                i1 = iArr[i6 + 1];
                i1 -= (int)(((lArrFld[i6 + 1] += i) - 58338) - (i1++));
            }
        }
    }

    System.out.println("i i1 l = " + i + "," + i1 + "," + l);
    System.out.println("s d1 d2 = " + s + "," + Double.doubleToLongBits(d1) + "," + Double.doubleToLongBits(d2));
    System.out.println("by1 d3 b = " + by1 + "," + Double.doubleToLongBits(d3) + "," + (b ? 1 : 0));
    System.out.println("i6 iArr fArr1 = " + i6 + "," + checkSum(iArr) + "," + Double.doubleToLongBits(checkSum(fArr1)));
    System.out.println("sArr = " + checkSum(sArr));

    System.out.println("fFld iFld lArrFld = " + Float.floatToIntBits(fFld) + "," + iFld + "," + checkSum(lArrFld));

    System.out.println("iMeth_check_sum: " + iMeth_check_sum);
}
}
