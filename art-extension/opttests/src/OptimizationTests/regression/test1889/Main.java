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

// Generated by Dalvik Fuzzer tool (3.5.001). Fri Apr  3 21:16:31 2015
package OptimizationTests.regression.test1889;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

public static void main(String[] strArr) {

    double d=2.43578, dArr[][]=new double[N][N];
    long l=4726452241884435768L;
    float f=-1.396F;
    int i=-232, i1=-27123, i2=12428, i3=3, i4=86, i5=-4654, i6=3, iArr[]=new int[N], iArr1[]=new int[N];

    init(iArr, -252);
    init(dArr, 17.89507);
    init(iArr1, -31920);

    for (i = 1; i < 82; ++i) {
        for (i1 = 1; i1 < 58; i1++) {
            i2 = (Math.max(i2++, iArr[i1]) * -7);
            l -= (((--i2) + (l * l)) + ((--i2) * (i2 + i3)));
            i2 += (i1 * i1);
        }
        i2 += (--i3);
        i2 -= (--i2);
        iArr[i] = 0;
        iArr[i] -= (int)(((l + i) + i1) - (l++));
        l += i;
        i2 -= (int)(((l + f) + (i1 + d)) + (dArr[i - 1][i] + (l + d)));
        try {
            i3 = (iArr[i + 1] % 190);
            iArr1[i] = (i2 / i);
            iArr[i + 1] = (25493 % i);
        } catch (ArithmeticException a_e) {}
        i4 -= (int)(((i2 - 12) % (iArr[i + 1] | 1)) - (++f));
    }
    i5 = 1;
    do {
        i3 -= iArr1[i5 - 1];
    } while (++i5 < 91);
    for (i6 = 2; i6 < 94; i6++) {
        f += (~i4);
    }

    System.out.println("i i1 i2 = " + i + "," + i1 + "," + i2);
    System.out.println("l i3 f = " + l + "," + i3 + "," + Float.floatToIntBits(f));
    System.out.println("d i4 i5 = " + Double.doubleToLongBits(d) + "," + i4 + "," + i5);
    System.out.println("i6 iArr dArr = " + i6 + "," + checkSum(iArr) + "," + Double.doubleToLongBits(checkSum(dArr)));
    System.out.println("iArr1 = " + checkSum(iArr1));

}
}
