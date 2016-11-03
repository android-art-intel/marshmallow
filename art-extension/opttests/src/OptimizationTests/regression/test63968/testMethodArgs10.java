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

package OptimizationTests.regression.test63968;

public class testMethodArgs10 extends testMethodArgs {

    public void printInfo() {
   	    System.out.println("Number of registers in run_call: 203");
    }

    public int run_call(int last) {
	    testMethodArgs tObj = new testMethodArgs();
		int i0 = 1234567;
        int i1 = 1234567;
        float f3 = 9.3f;
        float f4 = 9.3f;
        char c2 = 'a';
        int i7 = 1234567;
        int i8 = 12345678;
        int i9 = 12345657;
        float f0 = 9.3f;
        float f1 = 9.3f;
        char c3 = 'a';
        testMethodArgs o3 = tObj;
        float f5 = 9.3f;
        double d8 = 456e45;
        float f8 = 9.3f;
        float f9 = 9.3f;
        double d0 = 456e45;
        double d1 = 456e45;
        short s6 = (short)32767;
        double d4 = 456e45;
        int i4 = 1234567;
        int i5 = 1234567;
        int i6 = 1234567;
        double d5 = 456e45;
        int i2 = 1234567;
        int i3 = 1234567;
        float f2 = 9.3f;
        double d6 = 456e45;
        double d9 = 456e45;
        testMethodArgs o1 = tObj;
        testMethodArgs o2 = tObj;
        byte b3 = (byte)-127;
        short s7 = (short)32767;
        short s8 = (short)32767;
        double d2 = 456e45;
        testMethodArgs o5 = tObj;
        float f7 = 9.3f;
        double d7 = 456e45;
        testMethodArgs o10 = new testMethodArgs();
        testMethodArgs o11 = new testMethodArgs();
        testMethodArgs o12 = tObj;
        short s0 = (short)32767;
        short s1 = (short)32767;
        testMethodArgs o9 = tObj;
        float f6 = 9.3f;
        short s4 = (short)32767;
        short s5 = (short)32767;
        testMethodArgs o6 = tObj;
        testMethodArgs o7 = new testMethodArgs();
        testMethodArgs o8 = tObj;

		return call_1(i0, i1, f3, f4, c2, i7, i8, i9, f0, f1, c3, o3, f5, d8, f8, f9, d0, d1, s6, d4, i4, i5, i6, d5, i2, i3, f2, d6, d9, o1, o2, b3, s7, s8, d2, o5, f7, d7, o10, o11, o12, s0, s1, o9, f6, s4, s5, o6, o7, o8, last);
	}

    public int call_1(int i0, int i1, float f3, float f4, char c2, int i7, int i8, int i9, float f0, float f1, char c3, testMethodArgs o3, float f5, double d8, float f8, float f9, double d0, double d1, short s6, double d4, int i4, int i5, int i6, double d5, int i2, int i3, float f2, double d6, double d9, testMethodArgs o1, testMethodArgs o2, byte b3, short s7, short s8, double d2, testMethodArgs o5, float f7, double d7, testMethodArgs o10, testMethodArgs o11, testMethodArgs o12, short s0, short s1, testMethodArgs o9, float f6, short s4, short s5, testMethodArgs o6, testMethodArgs o7, testMethodArgs o8, int last) {
	    testMethodArgs tObj = new testMethodArgs();
		int i00 = i0;
        int i11 = i1;
        float f33 = f3;
        float f44 = f4;
        char c22 = c2;
        int i77 = i7;
        int i88 = i8;
        int i99 = i9;
        float f00 = f0;
        float f11 = f1;
        char c33 = c3;
        testMethodArgs o33 = o3;
        float f55 = f5;
        double d88 = d8;
        float f88 = f8;
        float f99 = f9;
        double d00 = d0;
        double d11 = d1;
        short s66 = s6;
        double d44 = d4;
        int i44 = i4;
        int i55 = i5;
        int i66 = i6;
        double d55 = d5;
        int i22 = i2;
        int i33 = i3;
        float f22 = f2;
        double d66 = d6;
        double d99 = d9;
        testMethodArgs o111 = o11;
        testMethodArgs o22 = tObj;
        byte b33 = b3;
        short s77 = s7;
        short s88 = s8;
        double d22 = d2;
        testMethodArgs o55 = tObj;
        float f77 = f7;
        double d77 = d7;
        testMethodArgs o100 = new testMethodArgs();
        testMethodArgs o1111 = new testMethodArgs();
        testMethodArgs o121 = tObj;
        short s00 = s0;
        short s11 = s1;
        testMethodArgs o99 = tObj;
        float f66 = f6;
        short s44 = s4;
        short s55 = s5;
        testMethodArgs o66 = tObj;
        testMethodArgs o77 = new testMethodArgs();
        testMethodArgs o88 = tObj;

		return call_2(i00, i11, f33, f44, c22, i77, i88, i99, f00, f11, c33, o33, f55, d88, f88, f99, d00, d11, s66, d44, i44, i55, i66, d55, i22, i33, f22, d66, d99, o11, o22, b33, s77, s88, d22, o55, f77, d77, o100, o111, o121, s00, s11, o99, f66, s44, s55, o66, o77, o88, last);
	}

    public int call_2(int i0, int i1, float f3, float f4, char c2, int i7, int i8, int i9, float f0, float f1, char c3, testMethodArgs o3, float f5, double d8, float f8, float f9, double d0, double d1, short s6, double d4, int i4, int i5, int i6, double d5, int i2, int i3, float f2, double d6, double d9, testMethodArgs o1, testMethodArgs o2, byte b3, short s7, short s8, double d2, testMethodArgs o5, float f7, double d7, testMethodArgs o10, testMethodArgs o11, testMethodArgs o12, short s0, short s1, testMethodArgs o9, float f6, short s4, short s5, testMethodArgs o6, testMethodArgs o7, testMethodArgs o8, int last) {
	    testMethodArgs tObj = new testMethodArgs();
		int i00 = i0;
        int i10 = i1;
        float f33 = f3;
        float f44 = f4;
        char c22 = c2;
        int i77 = i7;
        int i88 = i8;
        int i99 = i9;
        float f00 = f0;
        float f11 = f1;
        char c33 = c3;
        testMethodArgs o33 = o3;
        float f55 = f5;
        double d88 = d8;
        float f88 = f8;
        float f99 = f9;
        double d00 = d0;
        double d11 = d1;
        short s66 = s6;
        double d44 = d4;
        int i44 = i4;
        int i55 = i5;
        int i66 = i6;
        double d55 = d5;
        int i22 = i2;
        int i33 = i3;
        float f22 = f2;
        double d66 = d6;
        double d99 = d9;
        testMethodArgs o111 = tObj;
        testMethodArgs o22 = tObj;
        byte b33 = b3;
        short s77 = s7;
        short s88 = s8;
        double d22 = d2;
        testMethodArgs o55 = tObj;
        float f77 = f7;
        double d77 = d7;
        testMethodArgs o101 = new testMethodArgs();
        testMethodArgs o1111 = new testMethodArgs();
        testMethodArgs o121 = tObj;
        short s00 = s0;
        short s11 = s1;
        testMethodArgs o99 = tObj;
        float f66 = f6;
        short s44 = s4;
        short s55 = s5;
        testMethodArgs o66 = tObj;
        testMethodArgs o77 = new testMethodArgs();
        testMethodArgs o88 = tObj;

		return call_3(last);
	}

    public int call_3(int last) {
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
                (short)32767,
                (short)32767,
                456e45,
                null,
                9.3f,
                456e45,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
                (short)32767,
                (short)32767,
                new testMethodArgs(), //            null,
                9.3f,
                (short)32767,
                (short)32767,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
               (short)32767,
               (byte)-127,
               (short)32767,
               (byte)-127,
               456e45,
               (byte)-127,
               (byte)-127,
               'a',
               'a',
               null,
               'a',
               'a',
               'a',
               (byte)-127,
               (byte)-127,
               (byte)-127,
               (short)32767,
               (byte)-127,
               (byte)-127,
               'a',
               'a',
               'a',
               new String("proba1"),
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
               tObj, //            null,
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
               tObj, //            null,
               tObj, //            null,
               (byte)-127,
               (short)32767,
               (short)32767,
               456e45,
               tObj, //            null,
               9.3f,
               456e45,
               tObj, //            null,
               tObj, //            null,
               tObj, //            null,
               (short)32767,
               (short)32767,
               tObj, //            null,

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
                (short)32767,
                (short)32767,
                456e45,
                null,
                9.3f,
                456e45,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
                (short)32767,
                (short)32767,
                new testMethodArgs(), //            null,
                9.3f,
                (short)32767,
                (short)32767,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
                new testMethodArgs(), //            null,
               (short)32767,
               (byte)-127,
               (short)32767,
               (byte)-127,
               456e45,
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
        short s7,
        short s8,
        double d2,
        testMethodArgs o5,
        float f7,
        double d7,
        testMethodArgs o10,
        testMethodArgs o11,
        testMethodArgs o12,
        short s0,
        short s1,
        testMethodArgs o9,
        float f6,
        short s4,
        short s5,
        testMethodArgs o6,
        testMethodArgs o7,
        testMethodArgs o8,
        short s9,
        byte b0,
        short s3,
        byte b2,
        double d3,
        byte b4,
        byte b9,
        char c0,
        char c1,
        testMethodArgs o4,
        char c4,
        char c5,
        char c6,
        byte b5,
        byte b6,
        byte b1,
        short s2,
        byte b7,
        byte b8,
        char c7,
        char c8,
        char c9,
        String str1,
        int i00,
        int i11,
        float f33,
        float f44,
        char c22,
        int i77,
        int i88,
        int i99,
        float f00,
        float f11,
        char c33,
        testMethodArgs o3_1,
        float f55,
        double d88,
        float f88,
        float f99,
        double d00,
        double d11,
        short s66,
        double d44,
        int i44,
        int i55,
        int i66,
        double d55,
        int i22,
        int i33,
        float f22,
        double d66,
        double d99,
        testMethodArgs o1_1,
        testMethodArgs o2_1,
        byte b33,
        short s77,
        short s88,
        double d22,
        testMethodArgs o5_5,
        float f77,
        double d77,
        testMethodArgs o10_1,
        testMethodArgs o11_1,
        testMethodArgs o12_1,
        short s00,
        short s11,
        testMethodArgs o9_1,

        int i000,
        int i1111,
        float f333,
        float f444,
        char c222,
        int i777,
        int i888,
        int i999,
        float f000,
        float f111,
        char c333,
        testMethodArgs o333,
        float f555,
        double d888,
        float f888,
        float f999,
        double d000,
        double d111,
        short s666,
        double d444,
        int i444,
        int i555,
        int i666,
        double d555,
        int i222,
        int i333,
        float f222,
        double d666,
        double d999,
        testMethodArgs o111,
        testMethodArgs o222,
        byte b333,
        short s777,
        short s888,
        double d222,
        testMethodArgs o555,
        float f777,
        double d777,
        testMethodArgs o1010,
        testMethodArgs o1111,
        testMethodArgs o1212,
        short s000,
        short s111,
        testMethodArgs o999,
        float f666,
        short s444,
        short s555,
        testMethodArgs o666,
        testMethodArgs o777,
        testMethodArgs o888,
        short s999,
        byte b000,
        short s333,
        byte b222,
        double d333,
        int last) {
        
        testF = last;
        return last;
    }
}

