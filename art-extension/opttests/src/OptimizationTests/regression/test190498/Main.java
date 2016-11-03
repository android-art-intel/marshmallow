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

package OptimizationTests.regression.test190498;

class Main {
    
    final static int iterations = 10000000;

    public static void main(String[] args) {
        int result = 0;
        int result2 = 0;
        
        for(int i = 0; i < iterations; i++) {
            result = i + 1;
            result2 += result;
        }

        System.out.println("Final result is " + result + " " + result2);
    }
}
