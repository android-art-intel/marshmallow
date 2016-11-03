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

package OptimizationTests.regression.test116048;

class Helper {
    public Helper intern;
    public int cnt;
    public double value;
}

public class Main {

    public static void foo(Helper elem, Helper elem2, int n) {
        int i = 1;
        int value = 2;

        double cst = 10;
        int icst = 10;

        Helper extern1, extern2;

        while (i < value) {
            Helper inner1 = elem.intern;
            Helper inner2 = inner1.intern;

            double tmp = inner2.value;

            double cnt = i;
            cnt = cst / cnt;

            double cnt2 = i + 2;
            cnt2 = cst / cnt2;

            double cnt3 = cnt - cnt2;

            double cnt4 = tmp + cnt3;
            inner2.value = cnt4;

            i += 4;

            value = value / 32;
            value = value + icst;
        }
    }

    public static void main(String[] args) {
        Helper inner2 = new Helper();
        Helper inner1 = new Helper();
        Helper help = new Helper();

        help.intern = inner1;
        inner1.intern = inner2;
        inner2.cnt = 5000000;

      for (int i = 0; i < 100000; i++) {
            foo(help, help, 50000);
      }

      System.out.println("inner2.value = " + inner2.value);
      System.out.println("inner2.cnt   = " + inner2.cnt);
    }
}
