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

package OptimizationTests.regression.test3735_2;

public class Main {

    public static float fFld=1.1016F;
    public static double dFld=68.98670;

public static void main(String[] strArr) {

    int i=-7, i3=-220, i5=47399, iArr[]=new int[100];
    long l=242L;
    byte byArr[]=new byte[100];
	
    for (i = 1; i < 92; i++) {
        for (i3 = 1; i3 < i; i3++) {
            for (i5 = 1; i5 < 43; i5++) {
                l += (long)((iArr[i5]) - (i  - (i / ((long)(Main.fFld)))));
                Main.dFld += Main.fFld;
                iArr[i3 + 1] += (i + iArr[i]);
                l -= i;
            }
        }
    }

    System.out.println("l = " + l);
}
}
