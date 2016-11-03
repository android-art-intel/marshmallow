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

package OptimizationTests.InductionVariable.CommulateOperands;

/**
 * Commulate the operands.
 */
public class Main {
    
    static int threshold = 50;

    public void test() {
        int i0 = 0;
		int i1 = 274;
		int i2 = 762;
		int i3 = 53;
		int i4 = 87;
		int i5 = 123;
		int m = 15;
		while ( i0 < threshold ) {
			i0 = 5 + i0;
            i1 = m + i1;
			i2 = m + 3 + i2;
			i3 = 74 + m + i3;
			i4 = i4 + 74 + m;
			i5 = 74 + i5 + m;
		}
        System.out.println(i0);
    }

	public static void main(String[] args) {
		if (args.length != 0) {
			threshold = new Integer(args[0]).intValue();
		}
		new Main().test();
    }
}
