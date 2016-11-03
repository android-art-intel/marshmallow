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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticRangeACharThrowNullGet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Main {
    final static int iterations = 10;
    static char[] thingiesArray = new char[iterations];

    public static int getThingies(char[] arr, int i, char c1, char c2, char c3, char c4, char c5, char c6) {
        return arr[i];
    }

    public static void setThingies(char[] arr, char newThingy, int i, char c1, char c2, char c3, char c4, char c5, char c6) {
        arr[i] = newThingy;
    }
    
    public static void main(String[] args) {
        char nextThingy = 'a';
        char sumArrElements = '0';
        char d1 = 'a';
        char d2 = 'b';
        char d3 = 'c';
        char d4 = 'd';
        char d5 = 'e';
        char d6 = 'f';

        for(int i = 0; i < iterations; i++) {
            thingiesArray[i] = (char) (nextThingy + 1);
            sumArrElements = (char)(sumArrElements + thingiesArray[i]);
        }

        for(int i = 0; i < iterations + 1; i++) {
            if (i == iterations) 
                thingiesArray = null;
            nextThingy = (char)(getThingies(thingiesArray, i, d1, d2, d3, d4, d5, d6) + 1);
            setThingies(thingiesArray, nextThingy, i, d1, d2, d3, d4, d5, d6);
        }

    }
}
