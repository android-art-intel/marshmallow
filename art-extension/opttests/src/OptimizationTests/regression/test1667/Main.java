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

// Generated by Dalvik Fuzzer tool (3.5.001). Sun Mar  8 03:22:05 2015
package OptimizationTests.regression.test1667;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    long l=-2143690266L, lArr[]=new long[N];
    int i=-130, i1=215, i2=16941, i3=179, i4=12512, i5=46228, i6=-161, iArr[]=new int[N];
    short s=-22186;
    double d=-1.52730, dArr[][]=new double[N][N];
    byte by=56;
    float f=-72.440F;

    init(iArr, -58914);
    init(lArr, -5527L);
    init(dArr, -55.49315);

    if ((i6 - s) != (++by)) {
        i = 1;
        do {
            i1 += (i1++);
            by >>= (byte)(i1--);
            f /= (l | 1);
        } while (++i < 95);
        for (i2 = 4; i2 < 98; i2 += 3) {
            i1 -= (int)(iArr[i2 + 1] + (l--));
            i1 = (i + (++i1));
        }
        for (i3 = 3; i3 < 91; ++i3) {
            for (i4 = 1; i4 < 11; ++i4) {
                lArr[i4 + 1] *= l;
                for (i5 = 1; i5 < 25; i5++) {
                    l = i4;
                    l <<= (long)((s + (f - l)) * (iArr[i4 - 1] * (i4 - -16549)));
                    d += (i4 - i2);
                    lArr[i5] += iArr[i4];
                }
                for (i6 = 2; i6 < 37; ++i6) {
                    i1 += (int)(((i3 * i6) + iArr[(-3 >>> 1) % N]) + (dArr[i6 + 1][i3 + 1]--));
                    s += (short)(--i1);
                    i1 = (int)lArr[i3];
                    l %= ((iArr[i3 - 1] - ((s * i6) * (i1++))) | 1);
                    dArr[i3 - 1][i3 + 1] = i6;
                }
                i1 >>= (int)(--l);
                iArr[i3 + 1] *= (int)(-(f - (228L - (i6 * f))));
                if ((iArr[i4 + 1] % ((i1--) | 1)) != ((--i1) + ((-(i1 / (i | 1))) + iArr[i3 - 1]))) continue;
            }
        }
    }

    System.out.println("i i1 by = " + i + "," + i1 + "," + by);
    System.out.println("f l i2 = " + Float.floatToIntBits(f) + "," + l + "," + i2);
    System.out.println("i3 i4 i5 = " + i3 + "," + i4 + "," + i5);
    System.out.println("s d i6 = " + s + "," + Double.doubleToLongBits(d) + "," + i6);
    System.out.println("iArr lArr dArr = " + checkSum(iArr) + "," + checkSum(lArr) + "," +
        Double.doubleToLongBits(checkSum(dArr)));

}
}
