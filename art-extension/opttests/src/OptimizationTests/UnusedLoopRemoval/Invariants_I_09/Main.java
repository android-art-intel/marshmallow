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

package OptimizationTests.UnusedLoopRemoval.Invariants_I_09;

public class Main {

    // M: negative, local_var = field; is not hoisted
    // Test a loop with invariants, which should be hoisted 

    int invar0 = 05234234;
    int invar1 = 15234;
    int invar2 = 253434;
    int invar3 = 353645;
    int invar4 = 45433345;
    int invar5 = 5535656;
    int invar6 = 653680;
    int invar7 = 758545;
    int invar8 = 855685;
    int invar9 = 9526934;
    int invar10 = 1745645;
    int invar11 = 27456456;
    int invar12 = 37456457;
    int invar13 = 47456456;
    int invar14 = 5745645;
    int invar15 = 67456456;

    public int loop() {
        int used0 = 0;
        int used1 = 1;
        int used2 = 2;
        int used3 = 3;
        int used4 = 4;
        int used5 = 5;
        int used6 = 6;
        int used7 = 7;
        int used8 = 8;
        int used9 = 9;
        int used10 = 10;
        int used11 = 11;
        int used12 = 12;
        int used13 = 13;
        int used14 = 14;
       for (int i = 0; i < 10000; i++) {
            used0 = invar0 + invar1;
            used1 += invar1 + invar2 + invar0;
            used2 = invar2 | invar3;
            used3 += invar3 * invar4;
            used4 = invar4 | invar5 & invar4;
            used5 += invar5 - invar6;
            used6 = invar6 + invar7;
            used7 += invar7 - invar8;
            used8 = invar8 * invar9;
            used9 += invar9 - invar10;
            used10 = invar10 + invar11;
            used11 -= invar11 - invar12;
            used12 = invar12 | invar13;
            used13 -= invar13 - invar14;
            used14 = invar14 | invar15;
        }
        return used0 + used1 + used2 + used3 + used4 + used5 + used6 + used7 + used8 + used9 + used10 + used11 + used12 + used13 + used14;
    }

    public static void main(String[] args) {
        int res = new Main().loop();
        System.out.println(res);
    }
}
