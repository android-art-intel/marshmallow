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

package OptimizationTests.InductionVariable.LongAdd_03;

/**
 * Simple BIV test with different increments.
 */
public class Main {
    
    static long threshold = 10;

    public void test() {
        long i = 0;
		long j = 238;
		long k = 7;
		long m = 2;
		long m1 = m + 383;
		while ( i < threshold ) {
			long m2 = m * 74;
            i = i + m;
			j = j + m1;
			k = k + m2;
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
