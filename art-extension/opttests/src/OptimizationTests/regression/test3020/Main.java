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

// Generated by Dalvik Fuzzer tool (3.5.001). Tue Dec 22 15:42:36 2015

package OptimizationTests.regression.test3020;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    int i=-25167, i2=7, i3=6, i4=-193, i5=2, iArr[]=new int[N];
    long l=-1L, lArr[]=new long[N];
    byte by=78, by1=-97;
    double d=-8.78584;
    float f=1.377F, fArr[]=new float[N];
    short s=20774;
    boolean b=true;

    init(iArr, -212);
    init(fArr, -1.1022F);
    init(lArr, 5516536289570231548L);

    for (i = 1; i < 91; i++) {
        for (int i1 : iArr) {
            i1 >>= (int)(++l);
        }
        i2 = (iArr[i - 1]--);
        i2 = (int)(i2 * l);
        for (i3 = 1; i3 < 20; i3++) {
            i2 *= (int)((-(++l)) * (++by));
            iArr[i] = (int)(((i2 + l) + (i2--)) + (--i2));
            i2 += (int)(d += (i3 - (i2 - f)));
            for (i4 = 2; i4 < 71; ++i4) {
                i2 = i3;
            }
            l += ((++i2) - Math.max((int)(by + l), (int)(l + by)));
            d += ((i2 >>= (int)(-64085 * (f + by1))) + i);
            i5 = 1;
            do {
                iArr[i5 - 1] += (int)((long)(-(f--)) | (long)((i5 - f) - (d - by1)));
                by1 += (byte)(((fArr[i]++) + (l * l)) + lArr[i3 + 1]);
                iArr[i5] = (--i2);
                s -= (short)((l--) * ((-240 + (i4 ^ l)) - (i * l)));
                i2 -= i2;
                l -= i;
                b = (b = ((d * f) >= (f - i)));
                d = i4;
                iArr[i5] += (((++i2) * i3) * (-iArr[i + 1]));
                i2 -= (int)((-i3) * (d - (i - i4)));
            } while (++i5 < 19);
        }
        i2 += (int)(--l);
    }

    System.out.println("i l i2 = " + i + "," + l + "," + i2);
    System.out.println("i3 by d = " + i3 + "," + by + "," + Double.doubleToLongBits(d));
    System.out.println("f i4 by1 = " + Float.floatToIntBits(f) + "," + i4 + "," + by1);
    System.out.println("i5 s b = " + i5 + "," + s + "," + (b ? 1 : 0));
    System.out.println("iArr fArr lArr = " + checkSum(iArr) + "," + Double.doubleToLongBits(checkSum(fArr)) + "," +
        checkSum(lArr));

}
}
