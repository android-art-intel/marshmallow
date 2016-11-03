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

// Generated by Dalvik Fuzzer tool (3.5.001). Thu Feb  5 12:32:48 2015
package OptimizationTests.regression.test1116_1;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    long l=202L, l1=4050997145L, lArr[]=new long[N];
    double d=-60.125578, dArr[]=new double[N];
    byte byArr[]=new byte[N];
    short s=-4704, sArr[]=new short[N];
    float f1=-86.63F, fArr[]=new float[N];
    int i=-12, i1=20279, i2=-58431, i3=14, i4=-7018, i5=-9, i6=-87, i7=-187, iArr[][]=new int[N][N];

    init(fArr, -2.31F);
    init(byArr, (byte)31);
    init(iArr, -2);
    init(sArr, (short)-28270);
    init(dArr, 35.112550);
    init(lArr, -3860105347288028137L);

    for (float f : fArr) {
        i += (int)(((i + i) + l) + (-i));
    }
    for (i1 = 2; i1 < 97; ++i1) {
        i2 = 1;
        while (++i2 < 34) {
            i -= (--byArr[i2]);
            for (i3 = 58; i3 > 2; i3 -= 2) {
                i = Math.abs((int)((--f1) / ((--iArr[i3 - 1][i2]) | 1)));
                l = (((-191L * (-i2)) + Math.min(46369099L, l)) * ((i1 - i4) * (s <<= (short)i1)));
                i4 *= (int)(((--iArr[i1 + 1][i2]) % ((long)(l - 2.107789) | 1)) - iArr[i3][i1]);
                i4 += (int)(((++i5) - (l + i6)) * (s++));
                i6 -= (++iArr[i2 + 1][i1]);
                fArr[i3] = i1;
                sArr[i2 + 1] += (short)(--i4);
                l = (--i5);
                i4 -= (i6++);
                i6 *= (++s);
            }
            s *= (short)(iArr[i2 - 1][i2]++);
            for (i7 = 1; i7 < 90; ++i7) {
                i = (--i5);
                l = (long)(d + (++l));
            }
            iArr[i1 + 1][i1] += (int)(dArr[i2 + 1] += (1.33487 * (d -= l)));
            i4 -= (int)(++lArr[i1]);
            s |= (short)i2;
        }
        dArr[i1 + 1] += ((dArr[i1 - 1] - iArr[i1][i1]) - ((i3 - l1) + d));
        lArr[i1] = Math.abs((int)(i5 &= (int)(i2 + f1)));
    }

    System.out.println("i l i1 = " + i + "," + l + "," + i1);
    System.out.println("i2 i3 f1 = " + i2 + "," + i3 + "," + Float.floatToIntBits(f1));
    System.out.println("i4 s i5 = " + i4 + "," + s + "," + i5);
    System.out.println("i6 i7 d = " + i6 + "," + i7 + "," + Double.doubleToLongBits(d));
    System.out.println("l1 fArr byArr = " + l1 + "," + Double.doubleToLongBits(checkSum(fArr)) + "," + checkSum(byArr));
    System.out.println("iArr sArr dArr = " + checkSum(iArr) + "," + checkSum(sArr) + "," +
        Double.doubleToLongBits(checkSum(dArr)));
    System.out.println("lArr = " + checkSum(lArr));

}
}
