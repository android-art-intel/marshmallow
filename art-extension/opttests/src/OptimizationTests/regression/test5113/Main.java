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

// Generated by Dalvik Fuzzer tool (3.5.001). Sun Jan 11 18:59:47 2015
package OptimizationTests.regression.test5113;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    int i=14, i1=-14595, i2=-32471, i3=184, i4=-2, i5=5, i6=-40231, iArr[]=new int[N], iArr1[]=new int[N];
    byte by=-101;
    long l=10L, l1=7L;
    double dArr[]=new double[N];
    float f=0.828F, f1=-11.860F;
    boolean b=false;

    init(iArr, -46470);
    init(iArr1, -8);
    init(dArr, -125.123241);

    i = 1;
    do {
        i1 >>= (((i1 - i) - iArr[i - 1]) - iArr1[(i >>> 1) % N]);
        i2 = i;
        while ((i2 -= 3) > 0) {
            iArr1[i - 1] = i2;
            i1 -= (-(iArr[i2] + i));
            i1 ^= (--i1);
        }
        switch ((i % 1) + 117) {
        case 117:
            i1 = (++i1);
            for (i3 = i; i3 < 29; i3++) {
                i4 >>= i5;
                i4 *= (int)(l--);
                i1 = ((-i1) * i2);
                i4 = (int)f;
                i1 = (int)Math.abs(-59.585F);
                l += (((i3 * l) + i5) - f);
            }
            break;
        default:
            if (46781L == i1) continue;
            if ((l * (++i1)) > ((i1--) + ((--dArr[i]) - (l * -44071)))) continue;
        }
        for (f1 = 1; f1 < 46; ++f1) {
            b = ((--l) != (-(l1 += -25779)));
            i5 = (i1 - iArr[(int)(f1)]);
            i1 <<= (iArr[i] -= ((i4--) * (i5 - i2)));
        }
        iArr[i - 1] += (int)((iArr1[i - 1] + i4) / ((long)((f *= 11) + (i1 + -4)) | 1));
    } while (++i < 84);
    i6 = 1;
    do {
        i5 -= (((i + i4) - (i - by)) - i);
        by += (byte)(((i6 * i6) + f1) - l1);
    } while (++i6 < 98);

    System.out.println("i i1 i2 = " + i + "," + i1 + "," + i2);
    System.out.println("i3 i4 i5 = " + i3 + "," + i4 + "," + i5);
    System.out.println("l f f1 = " + l + "," + Float.floatToIntBits(f) + "," + Float.floatToIntBits(f1));
    System.out.println("b l1 i6 = " + (b ? 1 : 0) + "," + l1 + "," + i6);
    System.out.println("by iArr iArr1 = " + by + "," + checkSum(iArr) + "," + checkSum(iArr1));
    System.out.println("dArr = " + Double.doubleToLongBits(checkSum(dArr)));

}
}
