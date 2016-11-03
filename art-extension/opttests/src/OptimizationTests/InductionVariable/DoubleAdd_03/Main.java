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

package OptimizationTests.InductionVariable.DoubleAdd_03;

/**
 * Simple BIV test with different increments.
 */
public class Main {
    
    static double threshold = 10;

    public void test() {
        double i = 0;
		double j = 39;
		double k = 17;
		double m = 2;
		double m1 = m;
		while ( i < threshold ) {
			double m2 = m + 1;
            i = i + m;
			j = j + m1;
			k = k + m2;
        }
        System.out.println(i);
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            threshold = new Double(args[0]).doubleValue();
        }
        new Main().test();
    }
}
