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

package OptimizationTests.LoadHoistStoreSink.RangeCheckSetter_5;
// positive
// loadhoist_storesink: Gets and sets for method "int OptimizationTests.LoopInvariantSinking.RangeCheckSetter_5.Main.testLoop(int)"
// loadhoist_storesink: There are 0 get/set pairs and 1 sets to sink out of the loop.
// loadhoist_storesink: Set "v59 InstanceFieldSet [ l7 i53 ]" has been successfully sunk out of the loop #3 in block #6 successfully!


public class Main
{
    public int value;

    public int testLoop(int a0)
    {
        int[] arr = new int[100];
        for (int i = 0; i < 100; i++) {
            arr[i] = 1 + i + i * i;
            value = arr[i];
        }

        return value;
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
