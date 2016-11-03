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

package OptimizationTests.LoadHoistStoreSink.ChangedStaticValue_1;

public class Main
{
    public static class A 
    {
        public static int value;
    }

    public int testLoop(int a0, int b0)
    {
        A x;
        x = new A();
        x.value = 0x0;

// pass a0, b0 via parameters to isolate LHSS pass from other optimization passes
//        int a0 = 0x7;
//        int b0 = 0x77;

        for (int i = 0; i < 10; i++)
        {
            if (i%2 == 0)
            {
                a0 = b0;
            }
            a0++;
            x.value = a0;
        }

        return x.value;
    }

    public void test()
    {
        System.out.println(testLoop(0x7, 0x77));
    }

    public static void main(String[] args)
    {
        new Main().test();        
    }    
}
