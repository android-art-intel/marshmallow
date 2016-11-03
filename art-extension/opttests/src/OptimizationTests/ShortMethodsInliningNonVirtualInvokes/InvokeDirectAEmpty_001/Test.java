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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectAEmpty_001;
class Test {
//    int thingies = 0;
    int[] thingiesArray;

    public Test (int iter) {
        this.thingiesArray = new int[iter];
        for(int i = 0; i < iter; i++) {
            this.thingiesArray[i] = i;
        }
    }

    private int getThingies(int[] arr, int i) {
        return arr[i];
    }

    private void setThingies(int[] arr, int newThingy, int i) {
// empty method
    }

    public int gimme(int[] arr, int i) {
       return getThingies(arr, i);
    }

    public void hereyouare(int[] arr, int newThingy, int i) {
        setThingies(arr, newThingy, i);
    }
}
