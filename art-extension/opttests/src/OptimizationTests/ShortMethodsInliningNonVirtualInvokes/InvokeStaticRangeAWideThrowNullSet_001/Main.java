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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticRangeAWideThrowNullSet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Main {
    final static int iterations = 10;
    static double[] thingiesArray = new double[iterations];

    public static double getThingies(double[] arr, int i, double d1, double d2, double d3, double d4, double d5, double d6) {
        return arr[i];
    }

    public static void setThingies(double[] arr, double newThingy, int i, double d1, double d2, double d3, double d4, double d5, double d6) {
        arr[i] = newThingy;
    }
    
    public static void main(String[] args) {
        double nextThingy = -10.0;
        double sumArrElements = 0.0;
        for(int i = 0; i < iterations; i++) {
            thingiesArray[i] = i*1.0;
            sumArrElements = sumArrElements + thingiesArray[i];
        }

        for(int i = 0; i < iterations; i++) {
            nextThingy = getThingies(thingiesArray, i, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0) - i*1.0;
            if (i == iterations - 1)
                thingiesArray = null;
            setThingies(thingiesArray, nextThingy, i, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        }

    }
}
