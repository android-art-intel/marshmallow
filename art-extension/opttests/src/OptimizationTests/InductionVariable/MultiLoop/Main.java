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

package OptimizationTests.InductionVariable.MultiLoop;
/**
 * Test BIV with multiple loops.
 */
public class Main {

	static int iterations = 10;

	public void test() {

		int m = 0;
		for ( int i = 0; i < iterations; i++) {
           m = m + 2;
		}
		
		for ( int j = 0; j < iterations; j++) {
			m = m + 1;
		}
		
			System.out.println(m);
	}
	public static void main(String[] args) {
		if (args.length != 0) {
			iterations = new Integer(args[0]).intValue();
		}
		new Main().test();
	}
}
