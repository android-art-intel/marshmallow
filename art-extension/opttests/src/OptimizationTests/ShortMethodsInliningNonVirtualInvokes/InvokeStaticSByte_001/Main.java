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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticSByte_001;
class Main {
    final static int iterations = 10;
    static byte thingies = -128;

    public static byte getThingies() {
        return thingies;
    }

    public static void setThingies(byte newThingies) {
        thingies = newThingies;
    }
    
    public static void main(String[] args) {
        byte nextThingy = thingies;
 
        System.out.println("Initial nextThingy value is " + nextThingy);

        for(int i = 0; i < iterations; i++) {
            nextThingy = (byte)(getThingies() + i);
            setThingies(nextThingy);
        }

        System.out.println("Final nextThingy value is " + nextThingy);

    }
}
