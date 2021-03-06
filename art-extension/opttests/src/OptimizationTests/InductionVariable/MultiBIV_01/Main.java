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

package OptimizationTests.InductionVariable.MultiBIV_01;

/**
 * Test the mix of different BIVs.
 */
public class Main {

	static int iterations = 5;

	public void test() {
		int i1 = 0;
		int i2 = 83442;
		int m = 391;
		long j1 = -142l;
		long j2 = 783l;
		float f = 23.4632f;
		double d = 0;

		while (i1 < iterations) {
			i1 = i1 + 1;	
			i2 = i2 - m;	
			j1 = j1 + 13l;
			j2 = j2 - 57l;
			f = f + 349f;
			d = d + 97;
		}
		System.out.println(i1);
	}

	public static void main(String[] args) {
		if (args.length != 0) {
			iterations = new Integer(args[0]).intValue();
		}
		new Main().test();
	}
}
