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

// Generated by Dalvik Fuzzer tool (3.5.001). Fri Mar 20 10:21:20 2015
package OptimizationTests.regression.test1757_1;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    long l=-28462L, l1=-12L, l2=38L, lArr[]=new long[N];
    double d=115.38467, dArr[]=new double[N];
    short s=14914, sArr[]=new short[N];
    float f=1.177F;
    byte by=-60;
    int i=-239, i1=121, i2=19, i3=14, i4=-7, i5=-7, iArr[]=new int[N];

    init(dArr, -97.113989);
    init(lArr, 184098577L);
    init(iArr, -7);
    init(sArr, (short)-2170);

    for (i = 5; i < 82; i++) {
        i1 += i;
        i1 -= i;
        l <<= (long)((dArr[i - 1]--) * ((5278 * (by * -3.53277)) - (i1 * l)));
        f = s;
        i1 = (((i % (i1 | 1)) * (i1 <<= i1)) + ((++i1) + (i - 40912)));
        i1 += (i1++);
        d = (((0 - (~(160 - (i1 - l)))) + (i * i)) + (s++));
        i1 -= (int)((-(--l1)) * lArr[i]);
    }
    i2 = 1;
    while (++i2 < 80) {
        for (i3 = 2; i3 < 95; i3++) {
            for (i4 = 3; i4 < 54; ++i4) {
                iArr[i4] = (i1++);
                by -= (byte)(--i1);
                i1 = (int)(i2 + (--l1));
                i1 *= (int)((l1 + 14) + (i1 -= iArr[i2]));
                l1 -= ((i * (i3 * i3)) - (i1++));
                i1 >>= (i1 *= (sArr[i4 - 1] + (i1 - i1)));
                i1 -= (int)(l2--);
                iArr[i2 - 1] = (i1++);
                l1 -= iArr[i3 + 1];
                i1 = (int)((Math.abs(i1) / (l1 | 1)) * l);
            }
            i5 = 1;
            do {
                i1 = -3;
                l += (long)(-f);
            } while (++i5 < 63);
        }
    }

    System.out.println("i i1 l = " + i + "," + i1 + "," + l);
    System.out.println("by f s = " + by + "," + Float.floatToIntBits(f) + "," + s);
    System.out.println("d l1 i2 = " + Double.doubleToLongBits(d) + "," + l1 + "," + i2);
    System.out.println("i3 i4 l2 = " + i3 + "," + i4 + "," + l2);
    System.out.println("i5 dArr lArr = " + i5 + "," + Double.doubleToLongBits(checkSum(dArr)) + "," + checkSum(lArr));
    System.out.println("iArr sArr = " + checkSum(iArr) + "," + checkSum(sArr));

}
}