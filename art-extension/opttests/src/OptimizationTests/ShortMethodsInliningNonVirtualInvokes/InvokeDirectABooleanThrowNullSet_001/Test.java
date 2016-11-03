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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectABooleanThrowNullSet_001;
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

    private boolean getThingies(boolean[] arr, int i) {
        return arr[i];
    }

    private void setThingies(boolean[] arr, boolean newThingy, int i) {
        arr[i] = newThingy;
    }

    public boolean gimme(boolean[] arr, int i) {
       return getThingies(arr, i);
    }

    public void hereyouare(boolean[] arr, boolean newThingy, int i) {
       if (i == iterr - 1)
           arr = null;
        setThingies(arr, newThingy, i);
    }
}

