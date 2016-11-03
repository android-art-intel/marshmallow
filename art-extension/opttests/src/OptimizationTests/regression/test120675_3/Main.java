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

package OptimizationTests.regression.test120675_3;

/*
 * DDDO (Dual Double Divide Optimization) doesn't give bit exact results for some values of n and d, 
 * n/d double precision. 
 * The test compares plain and optimized division results.
 */
public class Main {

    double d1 = 0;
    double n1 = 0;

    public long divPlain(double d, int n) {
        double d1 = n;
        return Double.doubleToLongBits(d / d1);
    }

    public void test() {
        int n_bad = 0;
        double cst = 10;

        for (int i = 1; i < 10000; i++) {
            for (double j = .00001; j < .01; j += .000003) {
                long t1 = divPlain(j, i);
                double d2 = (double) i;
                double d3 = (double) (i + 2);
                d2 = j / d2;
                d3 = j / d3;
                long t2 = Double.doubleToLongBits(d2);
                if (t1 != t2) {
                    System.out.println("t1 = " + t1 + " t2 = " + t2);
                    n_bad += 1;
                    if (n_bad > 100) {
                        System.exit(1);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new Main().test();
        System.out.println("TEST PASSED");
    }

}
