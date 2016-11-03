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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectRangeABooleanThrowNullSet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Test {
    boolean[] thingiesArray;
    int iterr;

    public Test (int iter) {
        this.thingiesArray = new boolean[iter];
        this.iterr = iter;
        for(int i = 0; i < iter; i++) {
            this.thingiesArray[i] = true;
        }
    }

    private boolean getThingies(boolean[] arr, int i, int i1, int i2, int i3, int i4, int i5, int i6) {
        return arr[i];
    }

    private void setThingies(boolean[] arr, boolean newThingy, int i, int i1, int i2, int i3, int i4, int i5, int i6) {
        arr[i] = newThingy;
    }

    public boolean gimme(boolean[] arr, int i, int i1, int i2, int i3, int i4, int i5, int i6) {
       return getThingies(arr, i, i1, i2, i3, i4, i5, i6);
    }

    public void hereyouare(boolean[] arr, boolean newThingy, int i, int i1, int i2, int i3, int i4, int i5, int i6) {
       if (i == iterr - 1)
           arr = null;
        setThingies(arr, newThingy, i, i1, i2, i3, i4, i5, i6);
    }
}

