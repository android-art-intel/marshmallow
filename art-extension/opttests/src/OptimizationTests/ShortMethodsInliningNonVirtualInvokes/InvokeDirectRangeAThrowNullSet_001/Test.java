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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectRangeAThrowNullSet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Test {
    int[] thingiesArray;
    int iterr;

    public Test (int iter) {
        this.thingiesArray = new int[iter];
        this.iterr = iter;
        for(int i = 0; i < iter; i++) {
            this.thingiesArray[i] = i;
        }
    }

    private int getThingies(int[] arr, int i, char c1, char c2, char c3, char c4, char c5, char c6) {
        return arr[i];
    }

    private void setThingies(int[] arr, int newThingy, int i, char c1, char c2, char c3, char c4, char c5, char c6) {
        arr[i] = newThingy;
    }

    public int gimme(int[] arr, int i, char c1, char c2, char c3, char c4, char c5, char c6) {
       return getThingies(arr, i, c1, c2, c3, c4, c5, c6);
    }

    public void hereyouare(int[] arr, int newThingy, int i, char c1, char c2, char c3, char c4, char c5, char c6) {
       if (i == iterr - 1)
           arr = null;
        setThingies(arr, newThingy, i, c1, c2, c3, c4, c5, c6);
    }
}

