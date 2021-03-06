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

// Generated by Dalvik Fuzzer tool (3.5.001). Fri Feb 13 23:55:52 2015
package OptimizationTests.regression.test1520_2;

import OptimizationTests.regression.shared.*;

public class Main extends FuzzerUtils {

    public static final int N = 100;

    public static long lFld=-43L;

public static int iMeth() {

    long lArr[]=new long[N];

    return 5;
}

public static void main(String[] strArr) {

    long lArr1[]=new long[N];
    int i7=28689;
    double d1=-2.15830, dArr[]=new double[N];

    init(lArr1, -1654385235L);
    init(dArr, -43.109697);

    for (i7 = 4; i7 < 78; ++i7) {
        d1 = lArr1[i7];
        dArr[i7] = iMeth();
        lFld *= Double.doubleToRawLongBits(d1++) | lFld;
    }

    System.out.println("d1 = " + Double.doubleToLongBits(d1));
    System.out.println("i7 = " + i7);
    System.out.println("lArr1 = " + checkSum(lArr1));
    System.out.println("dArr = " + Double.doubleToLongBits(checkSum(dArr)));

    System.out.println("lFld = " + lFld);

}
}
