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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticRangeSByte_001;
class Main {
    final static int iterations = 10;
    static byte thingies = -128;

    public static byte getThingies(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        return thingies;
    }

    public static void setThingies(byte newThingies, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        thingies = newThingies;
    }
    
    public static void main(String[] args) {
        byte nextThingy = thingies;
        byte c1 = -1;
        byte c2 = -2;
        byte c3 = -3;
        byte c4 = -4;
        byte c5 = -5;
        byte c6 = -6;

        System.out.println("Initial nextThingy value is " + nextThingy);

        for(int i = 0; i < iterations; i++) {
            nextThingy = (byte)(getThingies(c1, c2, c3, c4, c5, c6) + i);
            setThingies(nextThingy, c1, c2, c3, c4, c5, c6);
        }

        System.out.println("Final nextThingy value is " + nextThingy);

    }
}
