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

package OptimizationTests.regression.test202111;

public class Main {

    public static void main(String[] args) {

        int numerator = 20;
        int denominator = 1;
        int res = 0;

        while (res >= 0) {
            if (numerator > 100000) {
                res = numerator / 0;
            }

            res = numerator / denominator;
            numerator++;
        }
    }
}

