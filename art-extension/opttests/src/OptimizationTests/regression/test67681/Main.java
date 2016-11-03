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

package OptimizationTests.regression.test67681;

public class Main {

    public static long a = 0;
    public static volatile long x = 5;

    public static void empty() {
    }

    public static void put(long val) {
        a = val;
    }

    public static void test() {
        empty();
        put(x);
    }

    public static void test1() {
        for (int i = 0; i < 100000; i++) {
            a = 0;
            test();
            if (a != 5) {
                break;
            } 
        }
        System.out.println("a = " + a);
    }

    public static void main(String[] args) {
        new Main().test1();
    }
}
