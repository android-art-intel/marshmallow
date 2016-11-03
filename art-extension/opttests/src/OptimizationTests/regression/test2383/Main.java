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

// Generated by Android Runtime Fuzzer tool (3.5.002). Sat Aug  1 00:01:04 2015
// srand = 10474209063615368747828440518331691136289494653261999692648346776198

package OptimizationTests.regression.test2383;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long lFld=-890242353116168962L;

    public static long iMeth_check_sum = 0;

public static int iMeth(int i, int i1, int i2) {

    float f1=-1.272F, f2=0.397F, fArr[][]=new float[N][N];
    long l=24627L;
    double d1=110.87608, d2=77.3887, d3=1.109476;
    int i3=5, i4=-53, iArr[]=new int[N];

    init(iArr, 25);
    init(fArr, 52.680F);

    for (d1 = 2; d1 < 84; ++d1) {
        iArr[(int)(d1 - 1)] -= (int)((i2 - (i2++)) * (l + iArr[(int)(d1)]));
    }
    for (i3 = 3; i3 < 80; i3 += 3) {
        d2 = 1;
        do {
            i2 = (int)(--lFld);
            i1 -= (i4 - i4);
            l *= 12;
            i4 = (int)(--d3);
        } while (++d2 < 32);
        i += i4;
        iArr[i3] <<= (int)(i *= (int)((i3 * f1) + (-11 * i)));
        f2 = i2;
        i4 += (int)((i2 = (int)(l * f2)) - fArr[i3][i3 - 1]);
    }
    long meth_res = i + i1 + i2 + Double.doubleToLongBits(d1) + l + i3 + Double.doubleToLongBits(d2) + i4 +
        Double.doubleToLongBits(d3) + Float.floatToIntBits(f1) + Float.floatToIntBits(f2) + checkSum(iArr) +
        Double.doubleToLongBits(checkSum(fArr));
    iMeth_check_sum += meth_res;
    return (int)meth_res;
}

public static void main(String[] strArr) {

    long l1=182L;
    float f=0.501F, f3=-99.910F;
    boolean b=true;
    double d=-70.27273;
    int i5=53, i6=7, i7=-48405, i8=-460, i9=-185, i10=-36, i11=26384, i12=-128, iArr1[]=new int[N], iArr2[]=new int[N];
    short sArr[][]=new short[N][N];

    init(sArr, (short)27145);
    init(iArr1, 30432);
    init(iArr2, 75);

    for (d = 3; d < 91; d++) {
        f -= ((f -= (lFld * -38944)) - iMeth(-7351, (int)(f - -229), --i5));
        for (i6 = 1; i6 < 29; ++i6) {
            i5 -= (int)lFld;
            lFld = (((i5 >> i6) - (9 * (i7 - i7))) * sArr[i6 + 1][(int)(d + 1)]);
            b = b;
            i7 = (int)lFld;
            i8 = 1;
            do {
                i5 += (((i8 * i5) + i5) - lFld);
                i5 += (i8 | i7);
                iArr1[i8 + 1] <<= (int)(i8 - (f--));
            } while (++i8 < 59);
            f *= (float)(iArr1[(int)(d - 1)] + d);
        }
        b = (f != f);
        lFld >>= (iArr1[(int)(d + 1)] + (-(lFld--)));
        lFld &= lFld;
    }
    for (i9 = 3; i9 < 77; ++i9) {
        l1 += (i9 * i6);
        for (i10 = 1; i10 < i9; ++i10) {
            i5 *= (int)(((lFld - 0.191F) + (f3 + lFld)) + (i7--));
            sArr[i9 + 1][i9] = (short)l1;
            for (i11 = 1; i11 < 83; i11++) {
                i5 += i11;
                i7 <<= (int)((iArr1[i10] + (i8 - i11)) * (i12 - (++l1)));
                l1 = i11;
            }
            lFld = (((i7--) - iArr2[i9 - 1]) - i6);
        }
    }

    System.out.println("d f i5 = " + Double.doubleToLongBits(d) + "," + Float.floatToIntBits(f) + "," + i5);
    System.out.println("i6 i7 b = " + i6 + "," + i7 + "," + (b ? 1 : 0));
    System.out.println("i8 i9 l1 = " + i8 + "," + i9 + "," + l1);
    System.out.println("i10 f3 i11 = " + i10 + "," + Float.floatToIntBits(f3) + "," + i11);
    System.out.println("i12 sArr iArr1 = " + i12 + "," + checkSum(sArr) + "," + checkSum(iArr1));
    System.out.println("iArr2 = " + checkSum(iArr2));

    System.out.println("lFld = " + lFld);

    System.out.println("iMeth_check_sum: " + iMeth_check_sum);
}
}
