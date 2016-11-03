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

package OptimizationTests.regression.test98749;

public class Main {

    public static final int N = 100;

    private static void init(int[] a, int n, int seed) {
        for (int j = 0; j < n; j++) {
            a[j] = ((j % 2 == 0) ? seed + j : seed - j) % 65535;
        }
    }

    private static int checkSum(int[] a, int n) {
        int sum = 0;
        for (int j = 0; j < n; j++) {
            sum += ((j % 2 == 0) ? a[j] : -a[j]);
        }
        return sum;
    }

    public static void test() {
        int n = -9793, j = 54570, n5 = -11988, nl = 13434, nf = -35188, if9 = 43575, ku0 = -47906, nv6 = 45547, jh = 14466, k = 50778, a2 = -57953, nm = 32565, e = 46546, res = 64282;

        int lp9[] = new int[N], la[] = new int[N], np[][] = new int[N][N], o[] = new int[N], ay[] = new int[N], y[] = new int[N], mg[] = new int[N], uy[] = new int[N], u[] = new int[N];

        init(lp9, N, 25851);
        init(la, N, 37745);
        init(o, N, 33506);
        init(ay, N, 64961);
        init(y, N, 27412);
        init(mg, N, 6762);
        init(uy, N, 37758);
        init(u, N, 28237);

        lp9[18] *= n--;

        for (j = 4; j < 86; ++j) {
            lp9[j] *= j + ((lp9[j - 1]) + (j / ((-6717 != 0) ? -6717 : 1)));
            lp9[j + 1] += (++n5) - ((nl--) + (-25148 + (++nf)));

            for (if9 = 1; if9 < j; ++if9) {
                lp9[if9 - 1] -= lp9[if9 + 1] + lp9[if9 + 1];
                lp9[if9] = 38010;
            }

            for (ku0 = j; ku0 < 9; ku0 += 2) {
                if (ku0 == j - 12) {
                    if (lp9[ku0 - 1] > 0)
                        break;
                    if (la[ku0 - 1] > 0)
                        break;
                }
            }
        }

        for (jh = 24; jh > 1; --jh) {
            n = o[jh + 1];
        }

        if ((((((ay[52]) - jh) + ((-((y[26]) % ((nv6 != 0) ? nv6 : 1))) + n)) / ((y[48] != 0) ? y[48]
                : 1)) + (mg[48]))
                + ku0 != o[94]) {
            for (k = 1; k < 24; k++) {
                la[k + 1] -= uy[k];
                y[k] += 39418;
            }
        } else {
            a2 = --nm;
        }

        e += u[45];

        res = n + n5 - nl + nf - nv6 + jh - a2 + nm - e;
        res += checkSum(lp9, N) + checkSum(la, N) - checkSum(o, N)
                - checkSum(ay, N) + checkSum(y, N) - checkSum(mg, N)
                + checkSum(uy, N) - checkSum(u, N);

        System.out.println("res = " + res);
    }
        
    public static void main(String[] args) {
        Main tb = new Main();
        // As the issue is intermittent, execute the test 5 times
        for (int i = 0; i < 5; i++) {
            tb.test();
        }
    }

}
