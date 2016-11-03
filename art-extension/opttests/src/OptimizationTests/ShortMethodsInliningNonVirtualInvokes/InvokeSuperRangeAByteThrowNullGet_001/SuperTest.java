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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeSuperRangeAByteThrowNullGet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class SuperTest {
    static byte[] thingiesArray;

    SuperTest(int iterations) {
        this.thingiesArray = new byte[iterations];
    }

    public byte getThingies(byte[] arr, int i, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        return arr[i];
    }

    public void setThingies(byte[] arr, byte newThingy, int i, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        arr[i] = newThingy;
    }
}

