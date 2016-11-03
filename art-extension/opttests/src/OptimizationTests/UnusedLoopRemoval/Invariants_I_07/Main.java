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

package OptimizationTests.UnusedLoopRemoval.Invariants_I_07;

public class Main {

    // M: negative, count down loop
    // Test a loop with invariants, which should be hoisted 

    int invar0 = 05;
    int invar1 = 15;
    int invar2 = 25;
    int invar3 = 35;
    int invar4 = 45;
    int invar5 = 55;
    int invar6 = 65;
    int invar7 = 75;
    int invar8 = 85;
    int invar9 = 95;
    int invar10 = 17;
    int invar11 = 27;
    int invar12 = 37;
    int invar13 = 47;
    int invar14 = 57;
    int invar15 = 67;
    int invar16 = 77;
    int invar17 = 87;
    int invar18 = 97;
    int invar19 = 07;

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
        int used11 = 10;
        int used12 = 10;
        int used13 = 10;
        int used14 = 10;
        int used15 = 10;
        int used16 = 10;
        int used17 = 10;
        int used18 = 10;
        int used19 = 10;
        int i = 10000;
        do {
            used0 = invar0 + invar1;
            used1 = used0 + invar1 + invar2 + invar0;
            used2 = used1 + invar2 | invar3;
            used3 = used2 + invar3 * invar4;
            used4 = used3 + invar4 | invar5 & invar4;
            used5 = used4 + invar5 - invar6;
            used6 = used5 + invar6 + invar7;
            used7 = used6 + invar7 - invar8;
            used8 = used7 + invar8 * invar9;
            used9 = used8 + invar9 - invar10;
            used10 = used9 + invar10 + invar11;
            used11 = used10 + invar11 - invar12;
            used12 = used11 + invar12 | invar13;
            used13 = used12 + invar13 - invar14;
            used14 = used13 + invar14 | invar15;
            used15 = used14 + invar15 - invar16;
            used16 = used15 + invar16 + invar17;
            used17 = used16 + invar17 - invar18;
            used18 = used17 + invar18 * invar19;
            used19 = used18 + invar19 + invar0;
            i--;
        } while (i > -2);
        return used0 + used1 + used2 + used3 + used4 + used5 + used6 + used7 + used8 + used9 + 
               used10 + used11 + used12 + used13 + used14 + used15 + used16 + used17 + used18 + used19;
    }

    public static void main(String[] args) {
        int res = new Main().loop();
        System.out.println(res);
    }
}
