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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticRangeAByteThrowNullGet_001;
// The test checks that stack after NullPointerException occurs is correct despite inlining
class Main {
    final static int iterations = 10;
    static byte[] thingiesArray = new byte[iterations];

    public static byte getThingies(byte[] arr, int i, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        return arr[i];
    }

    public static void setThingies(byte[] arr, byte newThingy, int i, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        arr[i] = newThingy;
    }
    
    public static void main(String[] args) {
        byte nextThingy = -128;
        byte sumArrElements = 0;
        byte c1 = -1;
        byte c2 = -2;
        byte c3 = -3;
        byte c4 = -4;
        byte c5 = -5;
        byte c6 = -6;

        for(int i = 0; i < iterations; i++) {
            thingiesArray[i] = (byte) (nextThingy + 1);
            sumArrElements = (byte)(sumArrElements + thingiesArray[i]);
        }

        for(int i = 0; i < iterations + 1; i++) {
            if (i == iterations) 
                thingiesArray = null;
            nextThingy = (byte)(getThingies(thingiesArray, i, c1, c2, c3, c4, c5, c6));
            setThingies(thingiesArray, nextThingy, i, c1, c2, c3, c4, c5, c6);
        }

    }
}
