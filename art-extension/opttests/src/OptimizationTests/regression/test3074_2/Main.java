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

package OptimizationTests.regression.test3074_2;

public class Main {

public static int iMeth(int i3, int i4) {

    for (float f = 1; f < 82; f++) {
        i3 &= (++i4);
    }

    for (int i6 = 1; i6 < 4; ++i6) {
        i3 = (i4--);
    }

    return i3;
}

public static void main(String[] strArr1) {
    System.out.println("iMeth: " + iMeth(1, 2));
}
}
