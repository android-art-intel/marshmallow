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

// Generated by Android Runtime Fuzzer tool (3.5.002). Mon Jul 27 17:53:02 2015
// srand = 18549618822946597416045128773174602736294746070280799450840672702967

package OptimizationTests.regression.test2370_3;

public class Main {

public static void main(String[] strArr) {
    int i=-1378, i1=-189, i2=10, i3=-18098, i4=197, i5=-62213;
    long l=-42122L;
    for (i = 2; i < 98; ++i) {
        for (i2 = 38; i2 > i; i2 -= 3) {
            i5 = 1;
            do {
                i4 = (++i3);
                i3 = (int)(l++);
            } while (++i5 < 57);
            l += i2;
        }
    }

    System.out.println("i4 = " + i4);
}
}
