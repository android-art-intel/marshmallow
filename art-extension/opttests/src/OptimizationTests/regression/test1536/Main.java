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

// Generated by Dalvik Fuzzer tool (3.5.001). Wed Feb 18 00:41:55 2015
package OptimizationTests.regression.test1536;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static short sFld=14949;

public static void main(String[] strArr) {

    long l=-1L, l1=147L, lArr[]=new long[N];
    float f=0.236F;
    boolean bArr[]=new boolean[N];
    double d=-2.105029;
    byte by=47;
    int i=-3, i1=-179, i2=-11, i3=-45686, i4=-225, iArr[][]=new int[N][N];

    init(iArr, 9);
    init(bArr, true);
    init(lArr, -6587056593210507559L);

    for (i = 84; i > 2; i--) {
        i1 = (int)(l + (f / (iArr[i - 1][i] | 1)));
        iArr[i][i] >>>= (--i1);
        i2 = 1;
        do {
            for (i3 = 2; i3 < 94; i3++) {
                bArr[i + 1] = (bArr[i3 - 1] = ((--l) > -33875L));
                lArr[i2] = (i2 - (iArr[i][i] + (i2 + i3)));
                f += (((i3 * l1) + l1) - l);
                i1 ^= (int)l1;
                i1 += (int)(((l1 - i) - (by * 54.689F)) - f);
                i1 += (((i3 * l1) + l) - i1);
                l1 |= iArr[i][i2 + 1];
                lArr[i3 + 1] <<= l;
                i1 += (((i3 * f) + by) - i3);
                i1 = (i << (-(sFld - i)));
            }
            i1 = (int)((-(f--)) + i3);
            i1 = (((-(i - i)) + (i3 * i1)) + ((i2 - i2) + (i1 - i1)));
            i1 -= (int)(-(--l));
            i4 = 1;
            do {
                i1 &= (int)(l1++);
                i1 *= (int)(--l);
                lArr[i4] = i;
            } while (++i4 < i);
            iArr[i2][i + 1] *= (--by);
            f = (float)(((i3 - i2) + (d - l)) + (-29.8214 * (l + i1)));
            i1 += i2;
        } while (++i2 < 88);
    }

    System.out.println("i i1 l = " + i + "," + i1 + "," + l);
    System.out.println("f i2 i3 = " + Float.floatToIntBits(f) + "," + i2 + "," + i3);
    System.out.println("l1 by i4 = " + l1 + "," + by + "," + i4);
    System.out.println("d iArr bArr = " + Double.doubleToLongBits(d) + "," + checkSum(iArr) + "," + checkSum(bArr));
    System.out.println("lArr = " + checkSum(lArr));

    System.out.println("sFld = " + sFld);
}
}
