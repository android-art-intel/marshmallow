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

package OptimizationTests.LoadHoistStoreSink.Throw;

public class Main
{
    public class A
    {
        public int value;
    }

    public int testLoop()
    {
        A x;
        x = new A();
        int a0 = 0x7;

        try 
        {
            for (int i = 0; i < 10; i++)
            {
                a0 ++;
                x.value = a0;
                throw new java.lang.ArithmeticException();
            }
        }catch(java.lang.ArithmeticException e){
        }
        return x.value;
    }

    public void test()
    {
        int sum = 0;
        for (int i = 0; i < 10; i++)
        {
            sum = sum + testLoop();
        }
        System.out.println(testLoop());
    }

    public static void main(String[] args)
    {
        new Main().test();        
    }    
}
