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

package OptimizationTests.UnusedLoopRemoval.NotSimpleBranch_03;

public class Main {

    // Test not a simple loop: branches
    public int loop() {
    
        int sum = 0;
        for (int i = 0; i < 10000; i++) {
            switch (i - 2500){
                case 0:
                    sum += i - 1;
                    break;
                case 2500:
                    sum += i * 2500;
                    break;
                case 7500:
                    sum += i * 7500;
                    break;
                default:
                    sum += i * 312;
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        int res = new Main().loop();
        System.out.println(res);
    }
}
