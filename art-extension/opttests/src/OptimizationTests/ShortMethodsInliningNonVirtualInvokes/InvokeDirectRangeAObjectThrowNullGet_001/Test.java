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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectRangeAObjectThrowNullGet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Test {
    Foo[] thingiesArray;
    int iterr;

    public Test (int iter) {
        this.thingiesArray = new Foo[iter];
        this.iterr = iter;
        for(int i = 0; i < iter; i++) {
            this.thingiesArray[i] = new Foo();
        }
    }

    private Foo getThingies(Foo[] arr, int i, Foo f1, Foo f2, Foo f3, Foo f4, Foo f5, Foo f6) {
        return arr[i];
    }

    private void setThingies(Foo[] arr, Foo newThingy, int i, Foo f1, Foo f2, Foo f3, Foo f4, Foo f5, Foo f6) {
        arr[i] = newThingy;
    }

    public Foo gimme(Foo[] arr, int i, Foo f1, Foo f2, Foo f3, Foo f4, Foo f5, Foo f6) {
       if (i == iterr - 1)
           arr = null;
       return getThingies(arr, i, f1, f2, f3, f4, f5, f6);
    }

    public void hereyouare(Foo[] arr, Foo newThingy, int i, Foo f1, Foo f2, Foo f3, Foo f4, Foo f5, Foo f6) {
        setThingies(arr, newThingy, i, f1, f2, f3, f4, f5, f6);
    }
}

