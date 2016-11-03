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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeSuperABooleanThrowNullGet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Main {
    final static int iterations = 10;
    
    public static void main(String[] args) {
        Test test = new Test(iterations);

        boolean nextThingy = false;
        boolean sumArrElements = false;
        for(int i = 0; i < iterations; i++) {
            SuperTest.thingiesArray[i] = true;
            sumArrElements = sumArrElements & SuperTest.thingiesArray[i];
        }

        for(int i = 0; i < iterations; i++) {
            if (i == iterations - 1) 
                SuperTest.thingiesArray = null;
            nextThingy = test.getThingies(SuperTest.thingiesArray, i) || true;
            test.setThingies(SuperTest.thingiesArray, nextThingy, i);
        }

    }
}
