/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */    

/** 
 * @author: Vera Y.Petrashkova
 * @version: $Revision: 1.4 $
 */

package OptimizationTests.regression.test57880;

public class testMethodArgs4 extends testMethodArgs {

    public void printInfo() {
   	    System.out.println("Number of methods arguments: 33");
    }

    public int run_call(int last) {
        testMethodArgs tObj = new testMethodArgs();
            return call(
                1234567,
                1234567,
                9.3f,
                9.3f,
                'a',
                1234567,
                1234567,
                1234567,
                9.3f,
                9.3f,
                'a',
                new testMethodArgs(), //            null,
                9.3f,
                456e45,
                9.3f,
                9.3f,
                456e45,
                456e45,
                (short)32767,
                456e45,
                1234567,
                1234567,
                1234567,
                456e45,
                1234567,
                1234567,
                9.3f,
                456e45,
                456e45,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
                (byte)-127,
                last);			
    }

    public int call(
        int i0,
        int i1,
        float f3,
        float f4,
        char c2,
        int i7,
        int i8,
        int i9,
        float f0,
        float f1,
        char c3,
        testMethodArgs o3,
        float f5,
        double d8,
        float f8,
        float f9,
        double d0,
        double d1,
        short s6,
        double d4,
        int i4,
        int i5,
        int i6,
        double d5,
        int i2,
        int i3,
        float f2,
        double d6,
        double d9,
        testMethodArgs o1,
        testMethodArgs o2,
        byte b3,			
        int last) {
        
        testF = last;
        return last;
    }
}

