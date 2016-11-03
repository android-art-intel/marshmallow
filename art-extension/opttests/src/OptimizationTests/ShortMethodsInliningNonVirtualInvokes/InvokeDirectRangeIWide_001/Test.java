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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectRangeIWide_001;
class Test {
    double thingies = 0;

    private double getThingies(double d1, double d2, double d3, double d4, double d5, double d6) {
        return thingies;
    }

    private void setThingies(double newThingies, double d1, double d2, double d3, double d4, double d5, double d6) {
        thingies = newThingies;
    }

    public double gimme(double d1, double d2, double d3, double d4, double d5, double d6) {
       return getThingies(d1, d2, d3, d4, d5, d6);
    }

    public void hereyouare(double newThingies, double d1, double d2, double d3, double d4, double d5, double d6) {
        setThingies(newThingies, d1, d2, d3, d4, d5, d6);
    }
}

