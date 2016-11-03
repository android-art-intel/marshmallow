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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectRangeAByteThrowNullGet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Test {
    byte[] thingiesArray;
    byte bean = -128;
    int iterr;

    public Test (int iter) {
        this.thingiesArray = new byte[iter];
        this.iterr = iter;
        for(int i = 0; i < iter; i++) {
            this.thingiesArray[i] = (byte)(bean + 1);
        }
    }

    private byte getThingies(byte[] arr, int i, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        return arr[i];
    }

    private void setThingies(byte[] arr, byte newThingy, int i, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        arr[i] = newThingy;
    }

    public byte gimme(byte[] arr, int i, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
       if (i == iterr - 1)
           arr = null;
       return getThingies(arr, i, b1, b2, b3, b4, b5, b6);
    }

    public void hereyouare(byte[] arr, byte newThingy, int i, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        setThingies(arr, newThingy, i, b1, b2, b3, b4, b5, b6);
    }
}

