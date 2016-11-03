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

package OptimizationTests.UnusedLoopRemoval.Invariants_L_01;

public class Main {

    // M: negative because type casts are not sunk/hoisted
    // Test a loop with invariants, which should be hoisted 

    public long loop() {
        long used1 = 1;
        long used2 = 2;
        long used3 = 3;
        long used4 = 4;
        long invar1 = 1592834792834728937l;
        long invar2 = 2520728572987878988l;
        long invar3 = 3528394283472384834l;
        long invar4 = 4528798279287589927l;


        for (long i = 0; i < 10000; i++) {
            used1 += (long) (invar1 + invar2);
            used2 -= (long) (invar2 - invar3);
            used3 /= (long) (invar3 * invar4);
            used4 *= (long) (invar1 * invar2 - invar3 + invar4);
        }
        return (long) (used1 + used2 + used3 + used4);
    }

    public static void main(String[] args) {
        long res = new Main().loop();
        System.out.println(res);
    }
}
