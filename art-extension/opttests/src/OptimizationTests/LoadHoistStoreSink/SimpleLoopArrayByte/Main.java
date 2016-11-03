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

package OptimizationTests.LoadHoistStoreSink.SimpleLoopArrayByte;

// remove_loop_suspend_checks: Loop is not countable
// loadhoist_storesink: Set v41 ArraySet [ l83 i85 b26 ] is contained in a loop with suspend point, and its input is neither loop header phi nor invariant
// loadhoist_storesink: There are 0 get/set pairs and 0 sets to sink out of the loop

public class Main
{
    static int iterations = 100000;
    public byte[]  value = new byte[3];

    public byte testLoop(byte a0)
    {

        for (int i = 0; i < iterations; i++)
        {
            a0++;
            value[0] = a0;
        }

        return value[0];
    }

    public void test()
    {
        System.out.println(testLoop((byte)0x7));
    }
    
    public static void main(String[] args)
    {
        new Main().test();        
    }    
}
