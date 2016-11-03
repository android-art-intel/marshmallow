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

// Generated by Android Runtime Fuzzer tool (3.5.002). Sat Sep  5 22:57:34 2015
// srand = 132086161241241122169881075105073464062

package OptimizationTests.regression.test2604;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static short sFld=13190;
    public static float fFld=2.212F;

public static void main(String[] strArr) {

    int i=39629, i1=6, i2=35, iArr[]=new int[N];
    long l=113L, lArr[]=new long[N], lArr1[]=new long[N];
    double d=-42.93284;
    byte by=23, byArr[]=new byte[N];
    boolean b=true;

    init(iArr, -1187);
    init(byArr, (byte)23);
    init(lArr, 60820L);
    init(lArr1, -48847L);

    if (b = (b & (b = (!(l >= l))))) {
        for (i = 5; i < 87; i++) {
            i1 += (-i2);
            i2 += (i + i1);
            i2 = (-(-(++i2)));
            for (int i3 : iArr) {
                i2 += (int)((i1 * (l + sFld)) - i1);
                fFld = (byArr[i] + sFld);
                l <<= i1;
                i1 >>>= (i3 += (i1++));
                i2 -= (int)(l >>= i);
                d = 1;
                do {
                    l = (((13 - sFld) - (--byArr[i - 1])) + ((i3++) + (i2++)));
                    fFld -= (((i2++) - i1) * ((i2 -= (int)fFld) + (lArr[i - 1]++)));
                } while (++d < 32);
                i1 -= (int)(((-36670 + i2) * (-16376 - (i2 - -1452386920L))) * ((fFld = i1) + i3));
                i1 *= (-(++i1));
                fFld += (--l);
            }
            i2 <<= (int)l;
            by <<= (byte)(l = (++i1));
            i2 = (int)((l * i1) + ((--iArr[i + 1]) / (((long)(l = (long)fFld)) | 1)));
            switch ((((i1 + 0) >>> 1) % 2) + 46) {
            case 46:
                i1 *= Math.min(i2--, i);
                for (int i4 : iArr) {
                    i4 += (14 + (i * i));
                    i4 = (int)(l++);
                }
                break;
            case 47:
                lArr1[i - 1] += (--i1);
                break;
            default:
                iArr[i + 1] = (int)((-(sFld + i)) - l);
            }
        }
    }

    System.out.println("i i1 i2 = " + i + "," + i1 + "," + i2);
    System.out.println("l d by = " + l + "," + Double.doubleToLongBits(d) + "," + by);
    System.out.println("b iArr byArr = " + (b ? 1 : 0) + "," + checkSum(iArr) + "," + checkSum(byArr));
    System.out.println("lArr lArr1 = " + checkSum(lArr) + "," + checkSum(lArr1));

    System.out.println("sFld fFld = " + sFld + "," + Float.floatToIntBits(fFld));
}
}
