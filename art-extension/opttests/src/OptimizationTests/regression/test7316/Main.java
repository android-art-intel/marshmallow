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

// Generated by Dalvik Fuzzer tool (3.5.001). Fri Mar 20 13:26:38 2015
package OptimizationTests.regression.test7316;

import OptimizationTests.regression.shared.*;

class UserDefinedException extends RuntimeException {
    public int field;
}

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    long l=237L, l1=7721892532419686975L, lArr[]=new long[N];
    float f=0.473F;
    byte by=-13, byArr[]=new byte[N];
    double d=-89.70106, dArr[]=new double[N];
    boolean b=true, bArr[]=new boolean[N];
    int i=46503, i1=-11, i2=61515, i3=-1, i4=-6738, i5=11260, iArr[]=new int[N];

    init(dArr, 29.12575);
    init(lArr, 161L);
    init(bArr, true);
    init(iArr, 46763);
    init(byArr, (byte)-27);

    i = 1;
    do {
        l += (i * i);
        d -= dArr[i];
        i1 = (int)(l = l);
        i1 <<= i1;
        i1 = (i1--);
        f -= i;
        f *= (--i1);
        i1 = (int)((i1++) + ((13.48F + i2) - (i2 + i2)));
        i1 = (int)(i - (lArr[i]--));
    } while (++i < 93);
    try {
        for (i3 = 78; i3 > 4; i3 -= 2) {
            i2 <<= i3;
            for (i4 = 2; i4 < 41; i4 += 2) {
                l = i2;
                i1 *= (i1++);
                bArr[i4 + 1] = (b != ((f == -223) | b));
                f = l;
                for (i5 = 5; i5 < 81; i5++) {
                    i2 = (int)f;
                    i2 += (i5 * i5);
                }
                iArr[i4 - 1] >>= (int)(l1 + ((l1 + -64.43300) - Math.abs(i1)));
                i2 += (((i4 * i5) + i3) - i4);
                lArr[i4] += (long)((i + (2.702F * (--i1))) - ((i2 * i) - 49775));
                byArr[i4] *= (byte)(((168 - (i2 - 29124)) + (l1 * by)) >> (--i2));
            }
        }
    }
    catch (UserDefinedException exc) {
        i2 += ((i3 - (i2 * i5)) + i2);
    }

    System.out.println("i l d = " + i + "," + l + "," + Double.doubleToLongBits(d));
    System.out.println("i1 f i2 = " + i1 + "," + Float.floatToIntBits(f) + "," + i2);
    System.out.println("i3 i4 b = " + i3 + "," + i4 + "," + (b ? 1 : 0));
    System.out.println("i5 l1 by = " + i5 + "," + l1 + "," + by);
    System.out.println("dArr lArr bArr = " + Double.doubleToLongBits(checkSum(dArr)) + "," + checkSum(lArr) + "," +
        checkSum(bArr));
    System.out.println("iArr byArr = " + checkSum(iArr) + "," + checkSum(byArr));

}
}
