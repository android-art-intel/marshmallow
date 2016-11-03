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

package OptimizationTests.regression.test3074_1;

public class Main {

    public static long vMeth_check_sum = 0;

public static void vMeth(float f) {
    int i3 = 0, d1 = 10;
    for (int d2 = 1; d2 < d1; d2 += 3) {
        for (int i4 = 2; i4 < 4; i4++) {
            i3 = i4;
        }
    }
    vMeth_check_sum += i3;
}


public static void main(String[] strArr) {

    vMeth(0.0f);

    System.out.println("vMeth_check_sum: " + vMeth_check_sum);
}
}
