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

package OptimizationTests.UnusedLoopRemoval.Mixed_I_03;

public class Main {

    // Test a loop with expected inlining and invariant hoisting
    // Several loops in a row, all should be removed


    public static int getValue() {
        return 5;
    }


    public static int foo(int x) {
        return x;
    }

    public static int loop() {
        int res = 0;
        int invar1 = 555;
        for (int i = 0; i < 10000; i++) {
            res += getValue() * 2 + invar1;
        }
        res += 5;
        for (int i = 0; i < 10000; i++) {
            res = getValue() * 5 - invar1;
        }
        
        int j = -1;
        do  {
            res = getValue() * 5 - foo(6)*invar1;
            j++;
        } while (j < 10000);
        return res;
    }

    public static void main(String[] args) {
        int res = new Main().loop();
        System.out.println(res);
    }
}
