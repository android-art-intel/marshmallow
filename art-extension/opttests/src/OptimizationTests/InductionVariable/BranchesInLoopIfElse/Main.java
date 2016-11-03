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

package OptimizationTests.InductionVariable.BranchesInLoopIfElse;
/**
 * Test BIV that in control flow branch: IfElse.
 */
public class Main {

	static int iterations = 5;

	public void test() {
		int i = 0;	    
		int j = 2;
		while (i < iterations) {
	
			if (j < 5) {
               i = i + 1;
			} else if ( i < 10 ) {
				i = 5*j - 2;
			} else if ( i < 20 ) {
				i = j*(i + 3);
			} else if ( i < 30 ) {
				i = i * j - 5;
			} else {
                i = j * (i + 2) ;
			}
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
