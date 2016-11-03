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

package OptimizationTests.LoadHoistStoreSink.RangeCheckStaticSetter_6;

// negative
// loadhoist_storesink: Set v77 ArraySet [ l143 i145 i62 ] aliases with instruction i62 ArrayGet [ l23 i103 ].
// loadhoist_storesink: There are 0 get/set pairs and 0 sets to sink out of the loop.

public class Main
{
    public static class A
    {
        public static int[] value = new int[3];
    }

    public int testLoop(int a0)
    {
        A x;
        x = new A();
        int[] arr = new int[100];
        for (int i = 0; i < 100; i++) {
            arr[i] = 1 + i + i * i;
            a0 = arr[i];
            x.value[0] = a0;
        }

        return x.value[0];
    }

    public void test()
    {
        System.out.println(testLoop(0x7));
    }
    public static void main(String[] args)
    {
        new Main().test();        
    }    
}
