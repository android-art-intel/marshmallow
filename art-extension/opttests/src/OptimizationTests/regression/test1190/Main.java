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

// Generated by Dalvik Fuzzer tool (3.5.001). Tue Apr  7 13:20:57 2015
package OptimizationTests.regression.test1190;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    double d=0.86337;
    long l=39777L, lArr[]=new long[N];
    short s=20889;
    int i=-2, i1=5, i2=-7, i3=9, i4=-61384, i5=-34645, i6=1727, i7=6, iArr[]=new int[N];
    float f=42.225F;

    init(iArr, 5);
    init(lArr, 609204545L);

    for (i = 1; i < 87; ++i) {
        i1 += (int)(((-2.98089 / ((long)(f * i1) | 1)) % ((long)(++f) | 1)) - (l = (--iArr[i - 1])));
        i1 <<= (int)(((i1 * f) + 6) + (--s));
        i2 = 52;
        do {
            f += ((-(6137 - (-94 * iArr[i2 + 1]))) | lArr[i - 1]);
            for (i3 = 1; i3 < 11; ++i3) {
                i1 += (int)2574417730L;
                l = Float.floatToRawIntBits(--f);
                if (false) continue;
                i1 |= (int)(iArr[i2 - 1] - ((i1--) * l));
                i1 ^= i2;
                lArr[i + 1] >>= (l++);
                i1 <<= i2;
                l = ((iArr[i3]++) - i1);
            }
            d = ((i1++) * ((i1 + d) + (s + f)));
            iArr[i2 - 1] += (int)((++s) * (--l));
        } while (--i2 > 0);
        l -= ((i4++) + iArr[i]);
    }
    for (i5 = 3; i5 < 77; i5++) {
        i4 = (int)((i1--) + l);
        for (i6 = 37; i6 > 2; --i6) {
            l -= 0;
            for (i7 = 1; i7 < 38; ++i7) {
                i1 = i4;
                iArr[i6] *= i4;
                iArr[i5 - 1] = Short.reverseBytes((short)(d - i));
            }
        }
    }

    System.out.println("i i1 f = " + i + "," + i1 + "," + Float.floatToIntBits(f));
    System.out.println("l s i2 = " + l + "," + s + "," + i2);
    System.out.println("i3 d i4 = " + i3 + "," + Double.doubleToLongBits(d) + "," + i4);
    System.out.println("i5 i6 i7 = " + i5 + "," + i6 + "," + i7);
    System.out.println("iArr lArr = " + checkSum(iArr) + "," + checkSum(lArr));

}
}
