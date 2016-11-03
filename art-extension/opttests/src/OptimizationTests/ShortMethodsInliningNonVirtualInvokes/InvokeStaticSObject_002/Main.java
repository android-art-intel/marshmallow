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

package OptimizationTests.ShortMethodsInliningNonVirtualInvokes.InvokeStaticSObject_002;
class Main {
    final static int iterations = 10;
    static Foo thingies = new Foo();
    static Foo nextThingy = new Foo('Q');

    public static Foo getThingies() {
        return thingies;
    }

    public static void setThingies(Foo newThingies) {
        thingies = newThingies;
    }
    
    public static void main(String[] args) {

        System.out.println("Initial nextThingy value is " + nextThingy.getClass().toString());
        System.out.println("Initial nextThingy.a value is " + nextThingy.a);

        for(int i = 0; i < iterations; i++) {
            nextThingy = getThingies();
            setThingies(nextThingy.toFoo());
        }

        System.out.println("Final nextThingy value is " + nextThingy.getClass().toString());
        System.out.println("Final nextThingy.a value is " + nextThingy.a);

    }
}
