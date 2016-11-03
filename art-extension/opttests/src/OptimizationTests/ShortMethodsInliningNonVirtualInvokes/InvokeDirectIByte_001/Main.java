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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeDirectIByte_001;
class Main {
    final static int iterations = 10;
    
    public static void main(String[] args) {
        Test test = new Test();
        byte nextThingy = -128;

        System.out.println("Initial nextThingy value is " + nextThingy);
        System.out.println("Initial test.thingies value is " + test.thingies);

        for(int i = 0; i < iterations; i++) {
            nextThingy = (byte)(test.gimme() + i);
            test.hereyouare(nextThingy);
        }

        System.out.println("Final nextThingy value is " + nextThingy);
        System.out.println("Final test.thingies value is " + test.thingies);

    } 
}
