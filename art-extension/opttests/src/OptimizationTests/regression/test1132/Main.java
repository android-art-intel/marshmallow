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

// Generated by Dalvik Fuzzer tool (3.5.001). Fri Jan  9 23:09:12 2015
package OptimizationTests.regression.test1132;

import OptimizationTests.regression.shared.*;

class UserDefinedException extends RuntimeException {
    public int field;
}

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long lFld=-1L;

public static void main(String[] strArr) {

    int i=-12974, i1=0, i2=-31, i3=1, iArr[]=new int[N];
    float f=2.297F, f1=2.1023F;
    byte by=-72;
    long lArr[]=new long[N];
    boolean b=false;
    short s=-5062;
    double d=0.37997, dArr[]=new double[N];

    init(dArr, 60.22940);
    init(lArr, -79184181L);
    init(iArr, 5);

    for (i = 2; i < 91; ++i) {
        i1 >>= (int)(((i - -92.10599) - f) - (i1 = (int)(--dArr[i + 1])));
        try {
            lFld = i1;
            for (i2 = 3; i2 < 59; i2++) {
                s += (short)(Long.reverseBytes(Double.doubleToRawLongBits(d)) - (++i1));
                i1 -= ((i1++) - ((i2 + by) + (--i1)));
                i1 = i;
            }
            i1 *= (int)(lArr[i]--);
            try {
                iArr[i] = ((Math.min(i, i2) - (i1--)) - i1);
                dArr[i - 1] = lFld;
                i1 = (int)((by + (-1405 - (--i1))) + ((d - 21139L) - (lFld--)));
                i1 -= (int)(i1 -= (int)(Double.longBitsToDouble(lFld) + i2));
                i1 = (int)((iArr[i]++) + ((iArr[i]--) + (lFld++)));
                lFld += (long)(f1 -= ((s % lFld) * (i2 + -56)));
            }
            catch (UserDefinedException exc) {
                lFld = iArr[i];
            }
            finally {
                if (b) break;
                by += (byte)((i * (i1 = (int)f)) * (--i1));
                i1 += (int)(-27.594F * i2);
            }
            f += (((i * i2) + lFld) - i);
            d += (((-(i + -1.16347)) * (i2 + i2)) + ((i1 *= (int)-8038257065992419420L) / (-(i1 % 0))));
        }
        catch (NullPointerException exc1) {
            i1 -= (int)((lFld--) + ((236 | (lFld + lFld)) * (f--)));
        }
        catch (ArithmeticException exc2) {
            for (i3 = i; i3 < 37; i3++) {
                iArr[i] -= (int)((-(d + i3)) - i3);
                f += (--lFld);
            }
        }
    }

    System.out.println("i i1 f = " + i + "," + i1 + "," + Float.floatToIntBits(f));
    System.out.println("i2 s d = " + i2 + "," + s + "," + Double.doubleToLongBits(d));
    System.out.println("by f1 b = " + by + "," + Float.floatToIntBits(f1) + "," + (b ? 1 : 0));
    System.out.println("i3 dArr lArr = " + i3 + "," + Double.doubleToLongBits(checkSum(dArr)) + "," + checkSum(lArr));
    System.out.println("iArr = " + checkSum(iArr));

    System.out.println("lFld = " + lFld);
}
}