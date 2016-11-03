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

package OptimizationTests.LoadHoistStoreSink.MultiSetter;

public class Main
{
    public class A 
    {
        public int val0;
        public int val1;
    }

    public int testLoop()
    {
        A x;
        x = new A();

        int a0 = 0x7;
        int a1 = 0x77;

        for (int i = 0; i < 10; i++)
        {
            a0 ++;
            x.val0 = a0;
            a1 ++;
            x.val1 = a1;
        }

        return x.val0 + x.val1;
    }

    public void test()
    {
        System.out.println(testLoop());
    }

    public static void main(String[] args)
    {
        new Main().test();        
    }    
}
