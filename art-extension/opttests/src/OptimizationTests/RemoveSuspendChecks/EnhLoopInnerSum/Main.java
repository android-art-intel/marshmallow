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

package OptimizationTests.RemoveSuspendChecks.EnhLoopInnerSum;

public class Main {
    public static int testLoop() {
        int m = 10000;
        int sum = 0;
        int[] iterations = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90};

        for (int j = 0; j < m; j++) {
            for (int i : iterations) {
                sum +=  i;
            }
        }
        return sum;
    }

    public void test() {
        System.out.println(testLoop());
    }

    public static void main(String[] args) {
        new Main().test();
    }
}


