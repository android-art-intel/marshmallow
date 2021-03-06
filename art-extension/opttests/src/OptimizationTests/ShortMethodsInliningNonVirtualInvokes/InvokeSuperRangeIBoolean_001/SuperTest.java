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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeSuperRangeIBoolean_001;
class SuperTest {
    boolean thingies = false;

    public boolean getThingies(int i1, int i2, int i3, int i4, int i5, int i6) {
        return thingies;
    }

    public void setThingies(boolean newThingies, int i1, int i2, int i3, int i4, int i5, int i6) {
        thingies = newThingies;
    }
}

