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

// Generated by Android Runtime Fuzzer tool (4.1.001). Sat Dec 26 15:00:55 2015
// srand = 202002877970810778802661440585576844004

package OptimizationTests.regression.test3033;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long instanceCount=-2225982771948715062L;
    public static boolean bArrFld[]=new boolean[N];

    static {
        init(Main.bArrFld, true);
    }

public static void main(String[] strArr) {

    int i=211, i1=-55688, i2=130, i3=-7, i4=67, iArr[][]=new int[N][N];
    double d=13.58786, d1=1.2858;
    float f=0.200F;
    boolean b=false;
    byte by=-119;
    long lArr[]=new long[N];

    init(iArr, 3269);
    init(lArr, -96L);

    i = 1;
    while ((i += 2) < 87) {
        short s=28179;
        Main.instanceCount = 10;
        iArr[i + 1][i] = (int)(((Main.instanceCount - i) - (s--)) - (--Main.instanceCount));
    }
    for (d = 78; d > 4; d--) {
        switch ((int)((d % 1) + 60)) {
        case 60:
            Main.instanceCount += (long)d;
            iArr[(int)(d)][(int)(d - 1)] = (int)d;
            break;
        }
        i1 = (int)f;
        b = Main.bArrFld[(int)(d - 1)];
        i1 -= (int)((by * (i - i1)) - ((-(f--)) + (i1 >>> i)));
    }
    if (((++i3) - (-(-(--Main.instanceCount)))) != i3) {
        for (i2 = 82; i2 > 2; --i2) {
            i1 -= (int)Main.instanceCount;
            Main.instanceCount += (long)d;
            f = (((i1--) - (i1 * i)) - ((i2 - Main.instanceCount) * -100));
            i1 <<= (int)(Main.instanceCount++);
            lArr[i2 + 1] += ((Main.instanceCount++) + (--i1));
            i1 = (int)(((--i1) - (iArr[i2 - 1][i2]++)) - (Main.instanceCount - (++f)));
            Main.instanceCount -= (long)((i2 - (f++)) % (((i1--) - i2) | 1));
            i3 *= (int)((Main.instanceCount++) + -7.81408);
        }
        for (d1 = 3; d1 < 90; ++d1) {
            i4 = 1;
            while (++i4 < 7) {
                Main.instanceCount = (--iArr[(int)(d1 + 1)][(int)(d1)]);
                i3 = i1;
                i1 = i4;
            }
        }
    }

    System.out.println("i d i1 = " + i + "," + Double.doubleToLongBits(d) + "," + i1);
    System.out.println("f b by = " + Float.floatToIntBits(f) + "," + (b ? 1 : 0) + "," + by);
    System.out.println("i2 i3 d1 = " + i2 + "," + i3 + "," + Double.doubleToLongBits(d1));
    System.out.println("i4 iArr lArr = " + i4 + "," + checkSum(iArr) + "," + checkSum(lArr));

    System.out.println("Main.instanceCount Main.bArrFld = " + Main.instanceCount + "," + checkSum(Main.bArrFld));
}
}
