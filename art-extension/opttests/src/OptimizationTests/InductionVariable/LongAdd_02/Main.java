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

package OptimizationTests.InductionVariable.LongAdd_02;

/**
 * Simple BIV test with different increments.
 */
public class Main {
    
    static long threshold = 10;

    public void test() {
        long i = 0;
		long j = 2;
		long k = 5;
		long m = 382;
		long n = 293;
		while ( i < threshold ) {
            i += 2;
			j += m;
			k += n + 92;
        }
        System.out.println(i);
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            threshold = new Long(args[0]).longValue();
        }
        new Main().test();
    }
}
