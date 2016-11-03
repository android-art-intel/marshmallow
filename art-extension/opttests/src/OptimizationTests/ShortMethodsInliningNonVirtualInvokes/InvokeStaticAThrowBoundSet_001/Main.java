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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticAThrowBoundSet_001;
// The test checks that stack after ArrayIndexOutOffBouns Exception occurs is correct despite inlining
class Main {
    final static int iterations = 10;
    static int[] thingiesArray = new int[iterations];

    public static int getThingies(int[] arr, int i) {
        return arr[i];
    }

    public static void setThingies(int[] arr, int newThingy, int i) {
        arr[i] = newThingy;
    }

    public static void main(String[] args) {
        int nextThingy = -10;
        int sumArrElements = 0;
        for(int i = 0; i < iterations; i++) {
            thingiesArray[i] = i;
            sumArrElements = sumArrElements + thingiesArray[i];
        }

        for(int i = 0; i < iterations + 1; i++) {
            if (i < iterations)            
                nextThingy = getThingies(thingiesArray, i) - i*1;
            setThingies(thingiesArray, nextThingy, i);
        }

    }
}

