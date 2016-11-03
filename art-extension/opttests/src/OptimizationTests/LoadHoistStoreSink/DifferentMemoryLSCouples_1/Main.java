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

package OptimizationTests.LoadHoistStoreSink.DifferentMemoryLSCouples_1;

// positive

public class Main
{
    class A {
        int fieldA;
        int dummy;
    }

    class B {
        int dummy;
        int fieldB;
    }

    public void testLoop()
    {
        A inst1 = new A();
        B inst2 = new B();
        int iterations = 50;
        for (int i = 0; i < iterations; i++)
        {
            int a = inst1.fieldA;
            inst1.fieldA = a + i;
            int d = inst2.dummy;
            inst2.dummy = d + i *3;
            int b = inst2.fieldB;
            inst2.fieldB = b + 2 * i;
        }
        System.out.println(inst1.fieldA);
        System.out.println(inst2.dummy);
        System.out.println(inst2.fieldB);
    }

    public void test()
    {
        testLoop();
    }
    
    public static void main(String[] args)
    {
        new Main().test();        
    }    
}
