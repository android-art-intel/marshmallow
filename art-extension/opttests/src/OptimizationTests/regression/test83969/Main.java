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

package OptimizationTests.regression.test83969;

public class Main {

    static int res[] = new int[4];

    private static void init(int[] a, int n, int seed) {
        for (int j = 0; j < n; j++) {
            a[j] = seed;
        }
    }

    public static void main(String[] args) {
        int N = 100;
        while (N-- > 0) {
            int n5 = 2, j6, n = 1;
            int u[] = new int[100], y9[] = new int[100];

            init(u, 100, 8);
            init(y9, 100, 3);

            int[] exc_4 = null;
            for (j6 = 3; j6 < 7; j6++) {
                try {
                    n5 -= (5 - (u[j6])) * (y9[j6]);
                    u[j6 - 1] += ++n;
                    n5 = exc_4[1];
                } catch (NullPointerException exc_5) {
                    res[j6 - 3] = n5;
                }
            }
        }
        System.out.println("res[0] = " + res[0]);
        System.out.println("res[1] = " + res[1]);
        System.out.println("res[2] = " + res[2]);
        System.out.println("res[3] = " + res[3]);
    }
}
