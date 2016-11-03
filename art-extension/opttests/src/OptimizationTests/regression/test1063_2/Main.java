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

// Simplified test
package OptimizationTests.regression.test1063_2;

import OptimizationTests.regression.shared.*;

public class Main {
    public static final int N = 100;
    public static int k = -8819;

    public static void main(String[] xb) {
        int y=-19986;
        short e=16185;
        int u=-13193, k3=48860, llq=36445, zx[]=new int[N];
        int n = (int)(34554 * (-(53715 + 48159)));
        float c = (y--) + (--y);
        long l4 = 1312901528617136739L;
        for (k = 2; k < 98; ++k) {
            for (u = 2; u < 45; u++) {
                n = (int)l4; 
                c *= -3723; 
                n += (u | k3); 
                k3 -= (int)((++l4) + (-(e - n))); 
                k3 += (int)(zx[u] >>>= (int)((l4++) * l4));
            }
        }
    }
}
