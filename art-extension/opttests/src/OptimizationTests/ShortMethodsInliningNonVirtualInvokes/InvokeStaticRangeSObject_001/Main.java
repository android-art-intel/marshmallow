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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticRangeSObject_001;
class Main {
    final static int iterations = 10;
    static Foo thingies= new Foo();

    public static Foo getThingies(Foo f1, Foo f2, Foo f3, Foo f4, Foo f5, Foo f6) {
        return thingies;
    }

    public static void setThingies(Foo newThingies, Foo f1, Foo f2, Foo f3, Foo f4, Foo f5, Foo f6) {
        thingies = newThingies;
    }
    
    public static void main(String[] args) {
        Foo nextThingy = thingies;
        Foo g1 = new Foo();
        Foo g2 = new Foo();
        Foo g3 = new Foo();
        Foo g4 = new Foo();
        Foo g5 = new Foo();
        Foo g6 = new Foo();

        System.out.println("Initial nextThingy value is " + nextThingy.getClass().toString());

        for(int i = 0; i < iterations; i++) {
            nextThingy = getThingies(g1, g2, g3, g4, g5, g6);
            setThingies(nextThingy, g1, g2, g3, g4, g5, g6);
        }
       
        System.out.println("Final nextThingy value is " + nextThingy.getClass().toString());

    }
}
