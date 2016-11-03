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

package OptimizationTests.ShortLeafMethodsInlining.InvokeStatic_float_to_long_001;
class Main {
    final static int iterations = 10;

    public static long simple_method(float jj) {
        long ii;
        ii = (long)jj;
        return ii;
    }

    public static void main(String[] args) {
        float workJ = 1.40e-45f;
        long workK = 0x0L;

        System.out.println("Initial workJ value is " + workJ);

        for(long i = 0x0L; i < iterations; i++) {
            workK = simple_method(workJ) + i;
            workJ = (float)workK;
        }

        System.out.println("Final workJ value is " + workJ);
    }
}
