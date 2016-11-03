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

package OptimizationTests.regression.test98760;

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

    public static void main(String[] args) {
        int ot = 523, n = 21305, dk6 = 21286, jp = 41336, jy8 = 22728, jc4 = 45024, nc = -13886, kq4 = 25346, nn1 = 17757, ni = -17220, p = 50924, i5 = 519, j1 = -29662, no3 = -6337, res = -5741;

        int l5[] = new int[N], bw[] = new int[N], fz[] = new int[N], ca[][] = new int[N][N], kv5[] = new int[N], hx[] = new int[N], au[] = new int[N], ls[] = new int[N], a[] = new int[N], bf6[] = new int[N], s[] = new int[N], en[] = new int[N], ql[] = new int[N], ai4[] = new int[N], e[] = new int[N], y[] = new int[N], jd7[] = new int[N], b1[] = new int[N], j[] = new int[N], f4[] = new int[N];

        init(l5, N, 16178);
        init(bw, N, 48564);
        init(fz, N, 30869);
        init(kv5, N, 15877);
        init(hx, N, 31186);
        init(au, N, 47962);
        init(ls, N, 51374);
        init(a, N, 597);
        init(bf6, N, 37160);
        init(s, N, 36057);
        init(en, N, 25399);
        init(ql, N, 16438);
        init(ai4, N, 36125);
        init(e, N, 14037);
        init(y, N, 13111);
        init(jd7, N, 38910);
        init(b1, N, 11763);
        init(j, N, 47718);
        init(f4, N, 8525);

        l5[61] *= l5[16];
        ot = --n;
        dk6 = bw[25];

        for (jp = 1; jp < 59; jp++) {
            fz[jp] = -(l5[jp]);
            if (jp != 23) {
                if (bw[jp] > 0)
                    break;
                for (jy8 = 1; jy8 < jp; jy8 += 2) {
                    if (kv5[jp] > 0)
                        continue;
                }
                kv5[jp + 1] -= hx[jp];
            }
        }

        for (jc4 = 2; jc4 < 65; ++jc4) {
            au[jc4 - 1] *= nc--;
        }

        for (kq4 = 1; kq4 < 1; ++kq4) {
            ls[kq4 - 1] += (((a[kq4 + 1]) / ((bf6[kq4 - 1] != 0) ? bf6[kq4 - 1]
                    : 1)) * ((nn1--) * (dk6 * (s[kq4 - 1])))) + (--ni);
        }

        res = ot + n - dk6 + nc - nn1 + ni - p + i5 - no3;
        res += checkSum(l5, N) + checkSum(bw, N) - checkSum(fz, N)
                + checkSum(kv5, N) + checkSum(hx, N) - checkSum(au, N)
                + checkSum(ls, N) - checkSum(a, N) + checkSum(bf6, N)
                - checkSum(s, N) + checkSum(en, N) - checkSum(ql, N)
                + checkSum(ai4, N) - checkSum(e, N) + checkSum(y, N)
                - checkSum(jd7, N) + checkSum(b1, N) - checkSum(j, N)
                + checkSum(f4, N);

        System.out.println("res = " + res);
    }

}
