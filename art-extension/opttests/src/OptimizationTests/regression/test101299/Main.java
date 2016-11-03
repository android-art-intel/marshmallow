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

package OptimizationTests.regression.test101299;

public class Main {

    public static final int N = 100;

    private static void init(int[] a, int n, int seed) {
        for (int j = 0; j < n; j++) {
            a[j] = ((j % 2 == 0) ? seed + j : seed - j) % 65535;
        }
    }

    public static void main(String[] args) {
        int f[][] = new int[N][N], xg[] = new int[N], md[][] = new int[N][N], ah[] = new int[N], bg[] = new int[N], h6[] = new int[N], v[] = new int[N], ar[] = new int[N], a[] = new int[N], o[] = new int[N], i[] = new int[N], b[] = new int[N], bn[] = new int[N], ii[] = new int[N], vh[] = new int[N], g[] = new int[N], r[] = new int[N], v4[] = new int[N], qa6[] = new int[N];

        int qn = 11792, jy = -24459, n = 52312, qs = -14641, res = -47027;

        init(xg, N, 42831);
        init(ah, N, 14181);
        init(bg, N, 1775);
        init(h6, N, 50675);
        init(v, N, 23153);
        init(ar, N, 41348);

        for (jy = 1; jy < 49; ++jy) {
            try {
                xg[jy + 1] = h6[jy - 1] / n;
                ah[jy] = h6[jy + 1] % 2123548296;
                ar[jy + 1] = v[jy] / ar[jy];
                xg[jy + 1] = 134 % h6[jy + 1];
                qs = bg[jy] % qn;
            } catch (ArithmeticException a_e) {
            }
        }

        System.out.println("qs = " + qs);
    }
}
