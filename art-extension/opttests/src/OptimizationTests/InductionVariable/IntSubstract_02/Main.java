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

package OptimizationTests.InductionVariable.IntSubstract_02;

/**
 * Simple BIV test with different increments: add-int/2addr .
 */
public class Main {

	static int threshold = 10;

	public void test() {
		int i = 30;
		int j = 121;
        int k = 49;
		int m = 9;
		int m1 = m;
		while ( i > threshold ) {
			int m2 = m - 1;
			i -= m;
			j -= m1;
			k -= m2;
		}
		System.out.println(i);
	}

	public static void main(String[] args) {
		if (args.length != 0) {
			threshold = new Integer(args[0]).intValue();
		}
		new Main().test();
	}
}
