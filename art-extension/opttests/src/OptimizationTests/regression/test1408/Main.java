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

// Generated by Dalvik Fuzzer tool (3.5.001). Sat Jan 31 14:14:31 2015
package OptimizationTests.regression.test1408;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;


public static void main(String[] strArr) {

    int i=6, i1=0, i2=-241, i3=10630, i4=-44685, iArr[]=new int[N];
    boolean b=false;
    byte by=-54, byArr[]=new byte[N];
    long l=190L, lArr[]=new long[N];
    float f=2.105F;
    short s=-22189, sArr[]=new short[N];
    double d=-87.26861;

    init(iArr, 38496);
    init(lArr, 637356718L);
    init(sArr, (short)9245);
    init(byArr, (byte)-37);

    for (d = 1; d < 84; ++d) {
        for (i = 1; i < d; i++) {
            i1 = i1;
            for (i2 = 1; i2 < 8; ++i2) {
                s = (short)28.36949;
            }
            for (i3 = 1; i3 < 62; ++i3) {
                iArr[(int)(d)] += (int)(f--);
                f -= (--l);
                i1 += (i3 * i3);
            }
            i1 = (int)(-(iArr[i + 1] + (lArr[i + 1]--)));
            iArr[i] ^= (int)(((l -= i2) + i2) - ((i1--) + 4481658704260405841L));
            by = (byte)(sArr[(int)(d)]--);
            switch ((i % 1) + 90) {
            case 90:
                l = i1;
                i1 += (((i * l) + l) - l);
                iArr[i - 1] += (int)(i1 + ((f - l) * (14537 * (++l))));
                break;
            default:
                i1 = s;
                i1 >>= (iArr[i] - byArr[(int)(d + 1)]);
                i1 = i;
                if (b) continue;
            }
            i1 += (((i * f) + i2) - l);
            try {
                i1 = (i2 / iArr[i]);
                i1 = (iArr[(int)(d + 1)] % i);
                i1 = (-24557 % i1);
            } catch (ArithmeticException a_e) {}
        }
        for (i4 = 1; i4 < d; ++i4) {
            l += (long)f;
            i1 += (i4 * i4);
        }
    }

    System.out.println("d i i1 = " + Double.doubleToLongBits(d) + "," + i + "," + i1);
    System.out.println("i2 s i3 = " + i2 + "," + s + "," + i3);
    System.out.println("f l by = " + Float.floatToIntBits(f) + "," + l + "," + by);
    System.out.println("b i4 iArr = " + (b ? 1 : 0) + "," + i4 + "," + checkSum(iArr));
    System.out.println("lArr sArr byArr = " + checkSum(lArr) + "," + checkSum(sArr) + "," + checkSum(byArr));

}
}
