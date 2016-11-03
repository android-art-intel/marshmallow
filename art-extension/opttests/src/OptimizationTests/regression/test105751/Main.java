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

package OptimizationTests.regression.test105751;

class Main {
    public static void main(String args[]) {
        int sumAll = 0;
        for (int i = 0; i < 100000; i++) {
            int n = sumAll + 1;
            int m = n + (i * i);
            sumAll += (m + 1);
        }
        System.out.println("sumAll = " + sumAll);
    }
}
