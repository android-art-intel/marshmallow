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

// Generated by Dalvik Fuzzer tool (3.5.001). Tue Apr  7 14:10:20 2015
package OptimizationTests.regression.test1186;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    long l=-8L, lArr[]=new long[N];
    short s=-22731;
    int i=30, i1=106, i2=10, i3=-98, i4=-4, i5=-51560, i6=56125, i7=154, i8=-165, iArr[][]=new int[N][N];
    float f=0.72F, f1=1.255F;
    byte by=-64;
    double d=-2.8280;

    init(lArr, 51L);
    init(iArr, 16229);

    for (i = 5; i < 90; i++) {
        i1 -= (int)(((i2 + -32550) - Math.abs(l)) + (i2++));
        i3 = 1;
        do {
            i2 -= i3;
            for (i4 = 1; i4 < 15; ++i4) {
                l = (long)(-(--f));
                i2 = (int)lArr[i];
                i2 *= (--iArr[i4 + 1][i - 1]);
                i2 -= (int)((lArr[i + 1]--) - (f + l));
                i1 = (7 - (i1--));
                i1 += i1;
                iArr[i4 - 1][i4 + 1] -= (--i1);
                f *= ((Math.min(1, i2) - (l++)) - ((++iArr[i - 1][i4 - 1]) - 13));
                i1 = (int)(lArr[i4 + 1] = ((-15892 - (i1 = i3)) * i2));
            }
            i2 = (int)((--l) * ((i3 + f1) + (d - i)));
        } while (++i3 < 88);
        d *= (((i + i4) + iArr[i][i]) * ((102 - (s + 229)) - Math.abs(l)));
    }
    for (i5 = 2; i5 < 78; ++i5) {
        i6 -= (int)((i1--) * (++f));
        i2 += ((by++) + (Math.min(i1, i2) * (i2++)));
    }
    for (i7 = 4; i7 < 98; i7++) {
        for (i8 = i7; i8 < 32; i8 += 2) {
            i6 ^= (++i2);
            i6 *= (s + ((++by) - (i2 + -148)));
            i1 = (int)(iArr[i8][i7] -= (int)(2695 + (-896884430L * s)));
            i6 -= (int)((i2--) & ((i2 / (i | 1)) - (-(i6 - l))));
        }
    }

    System.out.println("i i1 i2 = " + i + "," + i1 + "," + i2);
    System.out.println("l i3 i4 = " + l + "," + i3 + "," + i4);
    System.out.println("f f1 d = " + Float.floatToIntBits(f) + "," + Float.floatToIntBits(f1) + "," +
        Double.doubleToLongBits(d));
    System.out.println("s i5 i6 = " + s + "," + i5 + "," + i6);
    System.out.println("by i7 i8 = " + by + "," + i7 + "," + i8);
    System.out.println("lArr iArr = " + checkSum(lArr) + "," + checkSum(iArr));

}
}
