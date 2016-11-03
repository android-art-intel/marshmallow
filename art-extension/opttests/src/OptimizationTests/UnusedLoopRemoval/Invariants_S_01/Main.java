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

package OptimizationTests.UnusedLoopRemoval.Invariants_S_01;

public class Main {

    // M: negative because type casts are not sunk/hoisted
    // Test a loop with invariants, which should be hoisted 

    public short loop() {
        short used1 = 1;
        short used2 = 2;
        short used3 = 3;
        short used4 = 4;
        short invar1 = 15;
        short invar2 = 25;
        short invar3 = 35;
        short invar4 = 45;


        for (short i = 0; i < 10000; i++) {
            used1 += (short) (invar1 + invar2);
            used2 -= (short) (invar2 - invar3);
            used3 *= (short) (invar3 * invar4);
            used4 %= (short) (invar1 * invar2 - invar3 + invar4);
        }
        return invar1;
    }

    public static void main(String[] args) {
        short res = new Main().loop();
        System.out.println(res);
    }
}
