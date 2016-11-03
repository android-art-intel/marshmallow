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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticRangeSWide_001;
class Main {
    final static int iterations = 10;
    static double thingies = 0;

    public static double getThingies(double d1, double d2, double d3, double d4, double d5, double d6) {
        return thingies;
    }

    public static void setThingies(double newThingies, double d1, double d2, double d3, double d4, double d5, double d6) {
        thingies = newThingies;
    }
    
    public static void main(String[] args) {
        double nextThingy = -100000.00;

        System.out.println("Initial nextThingy value is " + nextThingy);

        for(int i = 0; i < iterations; i++) {
            nextThingy = getThingies(1.0, 2.0, 3.0, 4.0, 5.0, 6.0) + i*10.00;
            setThingies(nextThingy, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }

         System.out.println("Final nextThingy value is " + nextThingy);

    }
}
