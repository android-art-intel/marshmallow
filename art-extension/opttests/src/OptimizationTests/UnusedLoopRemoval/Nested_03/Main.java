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

package OptimizationTests.UnusedLoopRemoval.Nested_03;

public class Main {

    // Test not a simple loop: nested
    // M: inner loops are now supported

    public int loop() {
    
        int sum = 0;
        int used1 = 10;
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 1200; j++) {
                sum += 5;
                used1 -= 3;
            } 
            for (int j = 0; j < 1200; j++) {
                sum += 5;
            } 
            sum += 17;
            used1 -= 19;


        }
        return sum + used1;
    }

    public static void main(String[] args) {
        int res = new Main().loop();
        System.out.println(res);
    }
}
