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

package OptimizationTests.InductionVariable.MultiBIV_02;

/**
 * Test mix BIVs with different constant type convert.
 */
public class Main {

	static int iterations = 5;

	public void test() {
		int i = 0;
		long l1 = 332l;
		float f1 = 0.234f;
		float f2 = 83.982f;
		double d1 = -242;
		double d2 = 475;
		while (i < iterations) {
			i = i + 1;	
			l1 = l1 + 16;
			f1 = f1 + 749;
			f2 = f2 + 39l;
			d1 = d1 + 84l;
			d2 = d2 + 97.323f;
		}
		System.out.println(i);
	}

	public static void main(String[] args) {
		if (args.length != 0) {
			iterations = new Integer(args[0]).intValue();
		}
		new Main().test();
	}
}
