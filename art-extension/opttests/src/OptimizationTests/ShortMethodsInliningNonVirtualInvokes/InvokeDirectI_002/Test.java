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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectI_002;
class Test {
    float thingies = 0;

    private float getThingies() {
        return thingies;
    }

    private void setThingies(float newThingies) {
        thingies = newThingies;
    }

    public float gimme() {
       return getThingies();
    }

    public void hereyouare(float newThingies) {
        setThingies(newThingies);
    }
}

