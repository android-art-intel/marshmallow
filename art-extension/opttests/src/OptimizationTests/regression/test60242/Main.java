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

package OptimizationTests.regression.test60242;

public class Main {
    
    public static int LIMIT = 1000000;

    private static long foo(long n){
        return (n * n);
    }


    public static void main(String[] args) {
        for (int i = 0; i < LIMIT; i++) {
            long one = foo(1);
            if ( one != 1 ) {
                System.out.println("i = " + i + " 1 * 1 = " + one);
                System.out.println("TEST FAILED");
                System.exit(1);
            }
        }
        System.out.println("TEST PASSED");
    }
}
