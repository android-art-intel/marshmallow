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

package OptimizationTests.InductionVariable.FloatAdd_03;

/**
 * Simple BIV test with different increments.
 */
public class Main {
    
    static float threshold = 10f;

    public void test() {
        float i = 0;
		float j = 1.332f;
		float k = 3.883f;
		float m = 1.43f;
		float m1 = m;
		while ( i < threshold ) {
			float m2 = m + 1;
            i = i + m;
			j = j + m1;
			k = k + m2;
        }
        System.out.println(i);
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            threshold = new Float(args[0]).floatValue();
        }
        new Main().test();
    }
}
