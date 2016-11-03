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

// Generated by Android Runtime Fuzzer tool (4.1.002). Mon Mar 21 01:37:20 2016
// srand = 270993105796788518068318364497009903842

package OptimizationTests.regression.test3491;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long instanceCount=91L;
    public static long lArrFld[]=new long[N];

    static {
        init(Main.lArrFld, -9L);
    }

public static void main(String[] strArr) {

    int i=16443, i1=-137, i2=49593, i3=-252, i4=124, iArr[][]=new int[N][N];
    double d=33.33841;
    float f=-76.99F;
    byte by=39;

    init(iArr, 25);

    for (i = 4; i < 92; ++i) {
        i1 += (i + i);
        iArr[i][i] *= (int)((iArr[i - 1][i]++) - ((d + 4) * (Main.instanceCount * i1)));
        i1 = (int)(iArr[i + 1][i] <<= (int)((i + Main.instanceCount) - -4L));
        Main.instanceCount += (long)(((++i1) + (84 - (i - i1))) * ((2.441F + (i1 + i1)) * d));
        i1 |= (int)Main.instanceCount;
        f += (-27987 + (i * i));
        Main.instanceCount += (long)(--f);
        d *= (--iArr[i][i + 1]);
        for (i2 = i; i2 > i; i2 -= 3) {
            Main.instanceCount = (((i * i1) | (i2 * i)) << 109);
            i3 = 1;
            do {
                i1 *= (i - (++i1));
                iArr[i2 + 1][i2] = (int)(-(i1 * (Main.instanceCount + Main.instanceCount)));
                Main.instanceCount *= (--i1);
                i4 *= (i1++);
                i4 = (int)((-(i + d)) - ((iArr[i3][i2] -= i2) >> i4));
                Main.lArrFld[i2 - 1] *= ((i4--) - (i2 - (-33463 + i)));
                by *= (byte)(((i4 - i3) - f) * ((-223 ^ (long)(-1.175F + (i4++))) - (i4--)));
            } while (++i3 < 38);
        }
    }

    System.out.println("i i1 d = " + i + "," + i1 + "," + Double.doubleToLongBits(d));
    System.out.println("f i2 i3 = " + Float.floatToIntBits(f) + "," + i2 + "," + i3);
    System.out.println("i4 by iArr = " + i4 + "," + by + "," + checkSum(iArr));

    System.out.println("Main.instanceCount Main.lArrFld = " + Main.instanceCount + "," + checkSum(Main.lArrFld));
}
}
