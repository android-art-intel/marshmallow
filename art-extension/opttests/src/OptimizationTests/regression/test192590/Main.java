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

package OptimizationTests.regression.test192590;

public class Main {
    
    static int iterations = 10000;

    public void test() {
        System.out.println(testLoop());
    }

    public int testLoop() {

        int sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum = sum + testLoop1();
        }
        return sum;
    } 

    /**
     * 
     * App hot code
     * 
     */
    public int testLoop1() {

        int m = 0;
        int n = 0;
        
        while (m < 10001) {
        	n += 2 + m * m;
        	m++;
        }
        return n;
    }

    public static void main(String[] args) {
        new Main().test();
    }
}
