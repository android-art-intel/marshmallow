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

package OptimizationTests.regression.test1974;

public class Main {
    public static long vMeth_check_sum = 0;
    public static final int N = 100;

public static void vMeth(int i, float f) {

    short s=-5496;
    long l=677882642L, l1=-1L;
    int i1=-215, i2=-1, i3=174, i4=-9035, i5=-44991, iArr[]=new int[N];

    //init(iArr, -8);

    for (i1 = 5; i1 < 81; ++i1) {
        l = (i -= (int)(iArr[i1 - 1] + (l1 + l)));
        l += i1;
        i += (((i1 * l) + i) - f);
    }
    for (i2 = 1; i2 < 98; i2++) {
        f += l;
        l = -61197;
    }
    System.out.println("i1 = " + i1);
    for (i3 = 4; i3 < 82; ++i3) {
        for (i4 = 2; i4 < 98; i4++) {
            s = (short)(i1 * i4);
        }
        f += (((i5 * i1) * (i5 - f)) - ((i3 - f) * s));
        iArr[i3] = (-48 * (++iArr[i3]));
    }
    vMeth_check_sum += i + Float.floatToIntBits(f) + i1 + l + l1 + i2 + i3 + i4 + s + i5 /*+ checkSum(iArr)*/;
}

public static void main(String[] args) {
    vMeth(8, 2.638F);
    System.out.println("Checksum = " + vMeth_check_sum);
}
}
