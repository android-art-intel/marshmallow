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

package OptimizationTests.LoadHoistStoreSink.SimpleLoopArrayShort;

public class Main
{
    public short[]  value = new short[3];
	   
    public short testLoop(short a0)
    {
        int iterations = 100;
        for (int i = 0; i < iterations; i++)
        {
            a0++;
            value[0] = a0;
        }

        return value[0];
    }

    public void test()
    {
        System.out.println(testLoop((short)0x7007));
    }
    
    public static void main(String[] args)
    {
        new Main().test();        
    }    
}
