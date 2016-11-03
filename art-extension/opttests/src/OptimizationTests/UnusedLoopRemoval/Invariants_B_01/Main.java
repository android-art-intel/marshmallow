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

package OptimizationTests.UnusedLoopRemoval.Invariants_B_01;

public class Main {

    // M: negative because type casts are not sunk/hoisted 
    // Test a loop with invariants, which should be hoisted

    public byte loop() {
        byte used1 = 1;
        byte used2 = 2;
        byte used3 = 3;
        byte used4 = 4;
        byte invar1 = 15;
        byte invar2 = 25;
        byte invar3 = 35;
        byte invar4 = 45;


        for (byte i = 0; i < 127; i++) {
            used1 -= (byte) (invar1 + invar2);
            used2 *= (byte) (invar2 - invar3);
            used3 += (byte) (invar3 * invar4);
            used4 /= (byte) (invar1 * invar2 - invar3 + invar4);
        }
        return (byte) (used1 + used2 + used3 + used4);
    }

    public static void main(String[] args) {
        byte res = new Main().loop();
        System.out.println(res);
    }
}
