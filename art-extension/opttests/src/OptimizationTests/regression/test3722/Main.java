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

// Generated by Android Runtime Fuzzer tool (4.1.002). Thu Mar 31 17:11:19 2016
// srand = 11002248178532180170063383353192336271661130150772213562215693261672

package OptimizationTests.regression.test3722;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long instanceCount=-5L;
    public static int iFld=10;
    public static Object OArrFld[]=new Object[679387];

    static {
        init(Main.OArrFld, new Cls());
    }

public static void main(String[] strArr) {

    int i=77, i1=-250, i2=135, i3=3, i5=76, i6=13, iArr[][]=new int[N][N];
    double d1=-64.55936;
    Cls O=new Cls();

    init(iArr, 52260);

    for (i = 1; i < 84; ++i) {
        byte by=127;
        double d=1.124293;
        Main.iFld += (int)(d--);
        Main.iFld += (i ^ (long)O.fFld);
        Main.iFld = (int)(Main.iFld ^= (int)(Cls.instanceCount + -6));
        Cls.iArrFld = (iArr = iArr);
        by -= (byte)(-(Main.iFld >>= (int)Cls.instanceCount));
    }
    i1 = 1;
    while (++i1 < 90) {
        O.fFld = (Main.iFld << (long)((-i) + (O.fFld - -29.241F)));
        Main.iFld += (i1 - 30);
        Main.iFld -= 10183;
        Main.OArrFld[i1 + 1] = (Main.OArrFld[i1 - 1] = (O = (O = O)));
    }
    for (i2 = 3; i2 < 84; ++i2) {
        Cls.bFld = (Main.iFld >= (Long.reverseBytes(53507L) - (O.fFld + i)));
        for (i3 = 1; i3 < 65; ++i3) {
            int i4=-41;
            if (O.fFld != (Main.instanceCount += (Cls.instanceCount >> (Main.instanceCount - i)))) continue;
            iArr[i3][i2] = ((Main.iFld++) + ((i3 - i4) * (i4 + i)));
            for (i5 = 1; i5 < 36; i5 += 2) {
                long lArr[]=new long[N];
                Main.instanceCount += (((i5 * i4) + O.fFld) - i);
                Cls.instanceCount += lArr[i5 - 1];
                lArr[i2] *= (long)(((d1 + i3) + (O.fFld--)) * (--iArr[i2][i5 - 1]));
                Cls.instanceCount >>= (++lArr[i3]);
                O.fFld += (((Main.iFld - O.iFld1) - Integer.reverseBytes(O.iFld1)) - (Main.iFld - -2.729F));
                Main.OArrFld[i5] = null;
                i4 += i5;
                Main.iFld = ((i6--) - (-(i3 + -154)));
            }
        }
    }

    System.out.println("i O.fFld i1 = " + i + "," + Float.floatToIntBits(O.fFld) + "," + i1);
    System.out.println("i2 i3 i5 = " + i2 + "," + i3 + "," + i5);
    System.out.println("d1 O.iFld1 i6 = " + Double.doubleToLongBits(d1) + "," + O.iFld1 + "," + i6);
    System.out.println("iArr O Cls = " + checkSum(iArr) + "," + checkSum(O) + "," + Cls.instanceCount);

    System.out.println("Main.instanceCount Main.iFld Main.OArrFld = " + Main.instanceCount + "," + Main.iFld + "," +
        checkSum(Main.OArrFld));
    System.out.println("Cls = " + Cls.instanceCount);
}
}

class Cls extends FuzzerUtils {

    public static final int N = 100;

    public static long instanceCount=-3570010803806662134L;
    public float fFld=0.172F;
    public static boolean bFld=false;
    public int iFld1=37183;
    public static int iArrFld[][]=new int[N][N];

    static {
        init(Cls.iArrFld, 225);
    }

}

//DEBUG  Cls ->  Cls main Test
//DEBUG  Test ->  Test
//DEBUG  main ->  main Test
//DEBUG  Depth = 2
//DEBUG  Classes = 2
//DEBUG  static objects = {}