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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectRangeAWideThrowNullGet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Test {
    double[] thingiesArray;
    int iterr;

    public Test (int iter) {
        this.thingiesArray = new double[iter];
        this.iterr = iter;
        for(int i = 0; i < iter; i++) {
            this.thingiesArray[i] = i*1.0;
        }
    }

    private double getThingies(double[] arr, int i, double d1, double d2, double d3, double d4, double d5, double d6) {
        return arr[i];
    }

    private void setThingies(double[] arr, double newThingy, int i, double d1, double d2, double d3, double d4, double d5, double d6) {
        arr[i] = newThingy;
    }

    public double gimme(double[] arr, int i, double d1, double d2, double d3, double d4, double d5, double d6) {
       if (i == iterr - 1)
           arr = null;
       return getThingies(arr, i, d1, d2, d3, d4, d5, d6);
    }

    public void hereyouare(double[] arr, double newThingy, int i, double d1, double d2, double d3, double d4, double d5, double d6) {
        setThingies(arr, newThingy, i, d1, d2, d3, d4, d5, d6);
    }
}

