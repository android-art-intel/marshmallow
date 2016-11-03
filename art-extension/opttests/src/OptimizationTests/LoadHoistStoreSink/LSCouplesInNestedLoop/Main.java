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

package OptimizationTests.LoadHoistStoreSink.LSCouplesInNestedLoop;

// positive

public class Main
{
    class A {
        int fieldA;
        int fieldB;
    }
//    public static A field;

    public void testLoop()
    {
        A inst = new A();
        int iterationsnested = 50;
        int iterations = 10;
        int c = 0;
        for (int j = 0; j < iterations; j++)
        {
            for (int i = 0; i < iterationsnested; i++)
            {
                int a = inst.fieldA;
                inst.fieldA = a + i;
                int b = inst.fieldB;
                inst.fieldB = b + 2 * i;
            }
           
            System.out.println(inst.fieldA);
            System.out.println(inst.fieldB);
            c = inst.fieldA + inst.fieldB;
        }
        System.out.println(c);
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
