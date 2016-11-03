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

// Generated by Android Runtime Fuzzer tool (3.5.002). Fri Jul 17 23:37:13 2015
// srand = 24394207145402713569853012439897357708434482515149494764551906229704

package OptimizationTests.regression.test2485;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long lFld=183L;
    public static float fArrFld[]=new float[N];

    static {
        init(fArrFld, -42.629F);
    }

    public static long vMeth_check_sum = 0;
    public static long byMeth_check_sum = 0;

public static void vMeth(short s1, int i6) {

    int i7=-6, i9=33186, i10=57, iArr1[]=new int[N];
    double d1=0.40105;

    init(iArr1, 10);

    i7 = 1;
    do {
        i6 = (--i6);
        i6 = (int)(lFld + (--lFld));
    } while (++i7 < 94);
    for (int i8 : iArr1) {
        for (i9 = 97; i9 > 3; i9 -= 3) {
            for (i10 = 2; i10 < 62; ++i10) {
                iArr1[i10] >>= (int)(lFld + (i6 + Math.max(-11, i9)));
                lFld = (i7 - (--i6));
                i8 *= (i8 += (++i8));
                i6 *= (int)lFld;
                i6 = (int)(d1++);
                i6 += (int)(i8 = (int)((lFld + i10) + i8));
            }
        }
    }
    vMeth_check_sum += s1 + i6 + i7 + i9 + i10 + Double.doubleToLongBits(d1) + checkSum(iArr1);
}

public static byte byMeth(int i12, int i13) {

    int i14=-12, i15=-218, i16=-61386, iArr2[]=new int[N];
    double d2=0.26451;
    byte by1=-83;
    long l=-1571806365229687290L;

    init(iArr2, -12);

    for (i14 = 2; i14 < 92; ++i14) {
        i13 = (int)(((i12++) + (-61.99F * (-iArr2[i14]))) % (((long)(-(d2 - i14))) | 1));
        for (i15 = i14; i15 < 96; ++i15) {
            i12 >>= (int)(((16614 - (lFld - i14)) - lFld) * (++i13));
            i13 += (i15 ^ l);
            i13 *= (--i12);
            i13 = ((i12 -= (i13 * i14)) >> (i13 + (i13--)));
            try {
                i12 += (by1--);
            }
            catch (ArrayIndexOutOfBoundsException exc) {
                iArr2[i14 - 1] += i13;
                try {
                    i13 = (i13 / -163);
                    iArr2[i15 - 1] = (i15 % i15);
                    i16 = (i12 % 94);
                } catch (ArithmeticException a_e) {}
                d2 += ((-iArr2[i14 + 1]) - (iArr2[i15] + (lFld *= 0L)));
            }
        }
    }
    long meth_res = i12 + i13 + i14 + Double.doubleToLongBits(d2) + i15 + l + by1 + i16 + checkSum(iArr2);
    byMeth_check_sum += meth_res;
    return (byte)meth_res;
}

public static void main(String[] strArr) {

    int i=123, i1=42, i2=34790, i3=-48959, i4=18, i5=11, i11=29702, i17=-6, i18=-35745, i19=4, i20=-7, iArr[]=new
        int[N];
    short s=-19842;
    double d=-95.66001;
    long lArr[]=new long[N];
    boolean b=true;
    byte by=81;
    float f=-91.136F;

    init(iArr, -64114);
    init(lArr, 11462L);

    for (i = 5; i < 90; ++i) {
        i1 <<= (int)lFld;
    }
    if (!(((lFld * i5) >= (++i11)) ^ ((i20 == i19) || (b & false)))) {
        for (i2 = 2; i2 < 79; ++i2) {
            i1 += (((i2 * i2) + i1) - i1);
            lFld = (((iArr[i2 + 1]++) * (--lFld)) + (iArr[i2 - 1]--));
            lFld *= i1;
            i1 += (i2 - i);
            i1 -= (int)(lFld--);
        }
        for (i3 = 3; i3 < 79; ++i3) {
            s = (short)((i2 * (++by)) + ((d = i2) + (f + f)));
        }
        for (i4 = 2; i4 < 84; i4++) {
            i1 *= (int)fArrFld[(i2 >>> 1) % N];
            d = ((-f) + (-(5 - f)));
            d *= (iArr[i4] -= ((i1 - i4) - (i1--)));
            f += (((i4 * i3) + i3) - lFld);
            i1 = (int)(f * ((-6 + (d * i)) + (--lArr[i4])));
        }
    } else if ((b = b) == ((lFld >= (lFld--)) && ((f != i5) & (i18 < i)))) {
        if (((i11 = i2) + (byMeth(i3, i11) * (-7 + i5))) < (-((i3 + i4) * (i11 + i)))) {
            for (i5 = 4; i5 < 89; ++i5) {
                iArr[i5 + 1] = (int)(((++i1) + (i5 - i3)) - (i + (11L - iArr[i5 + 1])));
                lFld = (long)((++i1) - ((f / (i5 | 1)) - d));
                i1 = i4;
                lFld += (long)((d++) + ((lFld <<= i2) * (f + i4)));
                i1 <<= i4;
            }
            vMeth(s, i1--);
        } else if (((s--) >>> ((--iArr[(i17 >>> 1) % N]) - (i3 + i5))) != (((i + 110) & (long)(d + f)) / (((long)((i18
            * i11) + (f * by))) | 1))) {
            i1 -= (int)-6015757933019506180L;
        } else if (iArr[(i11 >>> 1) % N] >= (++i1)) {
            vMeth((short)((i * lFld) - (--i11)), iArr[(i11 >>> 1) % N]);
        } else {
            lFld = (((i17 + 10316) - (i2 - i)) * i19);
        }
    }

    System.out.println("i i1 i2 = " + i + "," + i1 + "," + i2);
    System.out.println("i3 s by = " + i3 + "," + s + "," + by);
    System.out.println("d f i4 = " + Double.doubleToLongBits(d) + "," + Float.floatToIntBits(f) + "," + i4);
    System.out.println("i5 i11 i17 = " + i5 + "," + i11 + "," + i17);
    System.out.println("i18 i19 i20 = " + i18 + "," + i19 + "," + i20);
    System.out.println("b iArr lArr = " + (b ? 1 : 0) + "," + checkSum(iArr) + "," + checkSum(lArr));

    System.out.println("lFld fArrFld = " + lFld + "," + Double.doubleToLongBits(checkSum(fArrFld)));

    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
    System.out.println("byMeth_check_sum: " + byMeth_check_sum);
}
}
