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

package OptimizationTests.LoadHoistStoreSink.SimpleLoopStaticArrayByte_3;

// LHSS does not occur, because LHSS pass cannot distinguish two gets in the loop
//    0    2    i141  Phi 0 [ i26 i117 ]
//    0    0    b84   ArrayGet [ l17 i141 ]
//    0    1    b98   ArrayGet [ l56 i61 ]
//    0    1    i101  Add [ b98 i26 ]
//    0    1    b104  TypeConversion [ i101 ]
//    0    0    v115  ArraySet [ l56 i61 b104 ]
//    0    3    i117  Add [ i141 i26 ]
//    0    1    z171  GreaterThanOrEqual [ i117 i14 ]
//    0    0    v172  If [ z171 ]
// sinking does not occur, becuse of memory aliasing:
// loadhoist_storesink: Set v115 ArraySet [ l56 i61 b104 ] aliases with instruction b98 ArrayGet [ l56 i61 ]


public class Main
{
    public static class A
    {
        public static byte[]  value = new byte[3];
    }

    public byte testLoop(byte a0)
    {
        byte[] iterations = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90};
        A.value[0] = a0;

        for (byte i : iterations)
        {
            A.value[0]++;
        }

        return A.value[0];
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
