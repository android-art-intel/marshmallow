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

package OptimizationTests.UnusedLoopRemoval.Throw_02;

public class Main {

    // Test a loop that throws
    public int loop() {
    
        String strnull = null;
        String str = new String();
        for (int i = 0; i < 10000; i++) {
            str = strnull.toString();
        }
        return 0;
    }

    public static void main(String[] args) {

        int res = 0;
        try {
            res = new Main().loop();
        } catch (Exception e) {
            System.out.println(e.getClass());
        }
        System.out.println(res);
    }
}
