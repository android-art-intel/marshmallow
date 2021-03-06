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

// Generated by Android Runtime Fuzzer tool (3.5.002). Fri Jul 31 16:19:18 2015
// srand = 24926634010422024622221700822336386376908236866517231438407960762728

package OptimizationTests.regression.test2388;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static int iFld=48155;

public static void main(String[] strArr) {

    long l=-2475944826L;
    int i=-64, i1=-10, i2=-251, i3=-23655, i4=8, i5=173, i6=-63, iArr[]=new int[N];
    short sArr[]=new short[N];
    float f=-79.7F, fArr[]=new float[N];
    double d=-2.33753, d1=123.7841;
    boolean b=true;

    init(fArr, -96.197F);
    init(iArr, 61804);
    init(sArr, (short)28971);

    for (i = 3; i < 98; i++) {
        d = (--i1);
        i1 = (--i1);
        l <<= (long)(d + (-(i * i1)));
    }
    for (i2 = 4; i2 < 77; ++i2) {
        for (i3 = 2; i3 < 37; ++i3) {
            for (i4 = 1; i4 < i2; i4++) {
                fArr[i3 + 1] = iArr[i4 - 1];
                i1 <<= sArr[i2 - 1];
                i1 += Math.min((int)((l + i4) + (iArr[i4 + 1] += (int)-73L)), (int)(-2072 - (i1 * d)));
            }
        }
        l *= -12L;
        fArr[i2 - 1] += Math.abs((int)((--l) * (65 * (++i1))));
        i1 += (int)(((-3527176635L - (i3 << i1)) + (i1--)) % (iArr[i2] | 1));
        i1 &= (int)(((d++) - 8) + ((f + i2) * (f = f)));
    }
    for (i5 = 2; i5 < 96; i5 += 3) {
        i1 = (int)((l *= 3) | (i1 *= i1));
        i1 = -1;
    }
    if (b == b) {
        d1 = 1;
        while ((d1 += 3) < 93) {
            i1 = 7;
            i1 += iArr[(int)(d1 + 1)];
            for (i6 = 1; i6 < d1; i6++) {
                i1 = (int)(++l);
                d -= i1;
                i1 = (int)(iFld * ((i3 / -218L) * i6));
            }
        }
    } else {
        b = b;
    }

    System.out.println("i d i1 = " + i + "," + Double.doubleToLongBits(d) + "," + i1);
    System.out.println("l i2 i3 = " + l + "," + i2 + "," + i3);
    System.out.println("i4 f i5 = " + i4 + "," + Float.floatToIntBits(f) + "," + i5);
    System.out.println("d1 i6 b = " + Double.doubleToLongBits(d1) + "," + i6 + "," + (b ? 1 : 0));
    System.out.println("fArr iArr sArr = " + Double.doubleToLongBits(checkSum(fArr)) + "," + checkSum(iArr) + "," +
        checkSum(sArr));

    System.out.println("iFld = " + iFld);
}
}
