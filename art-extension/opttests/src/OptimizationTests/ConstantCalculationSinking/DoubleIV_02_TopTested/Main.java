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

package OptimizationTests.ConstantCalculationSinking.DoubleIV_02_TopTested;

/**
*
* Test with double point induction variable + nested loops + other cases
* Expected result: sinking is expected if double induction variable can be presented on each iteration without precision loss
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;
    double non_const_iterations = 1100.123d;

    class C {
        double value = 0;
        C(double x) {
            value = x;
        }
    }
  
    // Nested loop: int sinking expected, no double sinking because no overflow
    public int testLoopAddInt() {
        int testVar = 0xAAA;
        double testVarD = 1.2345;
        long additionalVar1 = 0x100;
        double additionalVar2 = 1.2345d;
        double additionalVar3 = 6.7899d;
        int additionalVar4 = 0xB;
        for (int i = 0; i < 1100; i++) {
            for (double f = 0 ; f < iterations; f += 0.25d) {
                additionalVar1 = 2*additionalVar1/100 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
                testVar +=  additionalVar4;
                testVarD += additionalVar2;
            }
        }
        return testVar + (int)(testVarD + additionalVar1);
    }


    // 3 sinking expected
    public double testLoopAddLongSeveralLoops(int n) {
        long testVar = Integer.MAX_VALUE;
        double testVarD1 = 1234.2345d;
        double testVarD2 = 1234.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 123.45d;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        testVar += (int)testVarD1;

        for (double f = 0.25d ; f < iterations ; f += 1.25d) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
            testVarD1 +=  Double.MAX_VALUE - 1  ;
        }

        testVarD1 = 555.5d;

        for (double f = 0.25f ; f < iterations ; f += 1.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
            testVarD1 +=  Double.MAX_VALUE - 1;
        }
        for (double f = 0.25f ; f < iterations ; f += 1.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
            testVarD2 +=  Double.MAX_VALUE - 1;
        }



        return testVar + testVarD1 - testVarD2;
    }



    // Nested loop: int sinking expected, double sinking expected because of overflow
    public int testLoopSubInt() {
        int testVar = 0xAAA;
        double testVarD = 1.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 123.45d;
        double additionalVar3 = 678.9d;
        int additionalVar4 = 0xBBB;
        for (double f1 = 0f ; f1 < iterations; f1 += 10.26f) {
            for (double f2 = 0f ; f2 < iterations; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f1/3;
                testVar -=  additionalVar4;
                testVarD -= -Double.MAX_VALUE/31;
            }
        }
        return testVar + (int)(testVarD + additionalVar1);
    }

    // Nested loop: int sinking expected, double sinking expected because of overflow
    public double testLoopSubLong(int n) {
        long testVar = Integer.MAX_VALUE;
        double testVarD1 = 1234.2345d;
        double testVarD2 = -1234.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 123.45d;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (double f1 = 0f ; f1 < iterations; f1 += 10.26f) {
            for (double f2 = 0.0001f ; f2 < iterations; f2 += 1.25f) {
 
                for (double f = 0.015625f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar -=  additionalVar4;
                }

//                testVarD += 3;
                for (double f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar += 3;
                    testVarD1 -=  Double.MAX_VALUE - 1  ;
                }

                testVar -=  additionalVar4;
  
                for (double f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVarD2 -=  Double.MAX_VALUE - 1;
                }

                testVar += Long.MAX_VALUE/3;

            }
        }
        return testVar + testVarD1 - testVarD2;
    }

    // Nested loop: 2 int sinkings expected, only 1 double sinking expected because second double variable is modified in outer loop
    public double testLoopSubLongModifiedInOuterLoop(int n) {
        long testVar = Integer.MAX_VALUE;
        double testVarD1 = 1234.2345d;
        double testVarD2 = -1234.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 123.45d;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (double f1 = 0f ; f1 < iterations; f1 += 10.26f) {
            // double sinking candidate is used in outer loop -> shouldn't be sunk
            testVarD1 += 3;
            for (double f2 = 0.0001f ; f2 < iterations; f2 += 1.25f) {
 
                for (double f = 0.015625f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar -=  additionalVar4;
                }

                for (double f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar += 3;
                    testVarD1 -=  Double.MAX_VALUE - 1  ;
                }

                testVar -=  additionalVar4;
  
                for (double f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    // testVarD2 should be sunk
                    testVarD2 -=  Double.MAX_VALUE - 1;
                }

                testVar += Long.MAX_VALUE/3;

            }
        }
        return testVar + testVarD1 - testVarD2;
    }

    // Nested loop: one int sinking expected (second should be rejected due to unkown bound?), two double sinking expected because second double variable used in outer loop but not modified
    public double testLoopSubLongReadInOuterLoopAndNonConstBound(int n) {
        long testVar = Integer.MAX_VALUE;
        double testVarD1 = 1234.2345d;
        double testVarD2 = -1234.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 123.45d;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (double f1 = 0f ; f1 < iterations; f1 += 10.26f) {
            testVar += (int)testVarD1;
            for (double f2 = 0.0001f ; f2 < iterations; f2 += 1.25f) {
 
                for (double f = 0.015625f ; f < non_const_iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar -=  additionalVar4;
                }

                for (double f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar += 3;
                    testVarD1 -=  Double.MAX_VALUE - 1  ;
                }

                testVar -=  additionalVar4;
  
                for (double f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVarD2 -=  Double.MAX_VALUE - 1;
                }

                testVar += Long.MAX_VALUE/3;

            }
        }
        return testVar + testVarD1 - testVarD2;
    }


    //1 sinking: int is rejected because of overflow, double is sunk
    public int testLoopMulIntNested(int n) {
        int testVar = 1;
        double testVarD1 = 1234.2345d;
        double testVarD2 = -1234.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 1d;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (double f = -0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar *=  additionalVar4;
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                    testVar *= 3;
                    testVarD1 *= Double.MAX_VALUE - 1234.1234d;
                }


        }
        return testVar + (int)additionalVar1;
    }

    //no sinking: int is rejected because of overflow, double is rejected because constant is < 0
    public long testLoopMulLongNested(long n) {
        long testVar = 1;
        double testVarD1 = 1234.2345d;
        double testVarD2 = -1234.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 1d;
        double additionalVar3 = 1d;
        long additionalVar4 = 3;
        for (double f1 = 0 ; f1 < iterations; f1 += 99.30f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f1/3;
            testVar *=  additionalVar4;
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                    testVar *= 3;
                    testVarD1 *= -Double.MAX_VALUE - 1  ;
                }


        }
        return testVar + (long)additionalVar1  + (long)testVarD1 + (long)testVarD2;
    }





    //4 sinking: 2 double sinking, 2 int sinking
    public int testLoopDivNested1(int n) {
        int testVar = 1;
        double testVarD1 = 1234.2345d;
        double testVarD2 = -123456789.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 1d;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (double f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVar /= 3;
                testVarD1 /= 123456789.2345d;
            }
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarD2 /= 123456789.2345d;
                testVar += -1;
            }
        }


        testVarD1 +=1;

        return testVar + (int)additionalVar1 + (int)testVarD1 + (int)testVarD2;
    }

 



    public long testLoopDivNested2(long n) {
        long testVar = 0xAAA;
        double testVarD1 = 12345.12345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 123.45d;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 3L;
        for (double f = -iterations ; f < iterations; f += 99.25f) {
            for (double d = 0.25f ; d < iterations; d += 0.75f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)d/3 +  (long)d % 3 - (int)additionalVar2/2 + (long)d*4 +  (long)d*5 + (long)d*6 + n/2 + n/3;
                testVarD1 /= 1234567891234567.123d;
            }
        }
        return (long)testVarD1 + (long)additionalVar1;
    }

    //4 sinking: 2 double sinking, 2 int sinking: double overflow to infinity
    public double testLoopDivNested3(int n) {
        int testVar = 1;
        double testVarD1 = Double.MAX_VALUE/2;
        double testVarD2 = Double.MIN_VALUE/2;
        long additionalVar1 = 0x100;
        double additionalVar2 = 1d;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (double f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVar /= 3;
                testVarD1 /= 0.34567892345d;
            }
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarD2 /= 0.1234567892345d;
                testVar += -1;
            }
        }


        testVarD1 +=1;

        return testVar + additionalVar1 + testVarD1 + testVarD2;
    }

     //No sinking: division by negative constant in nested loop
    public double testLoopDivNested4(int n) {
        int testVar = 1;
        double testVarD1 = Double.MAX_VALUE/2;
        double testVarD2 = Double.MIN_VALUE/2;
        long additionalVar1 = 0x100;
        double additionalVar2 = 1d;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (double f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarD1 /= -0.00034567892345d;
            }
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarD2 /= -0.1234567892345d;
            }
        }


        testVarD1 += 1;
        testVarD2 -= 1;

        return testVar + additionalVar1 + testVarD1 + testVarD2;
    }



    //4 sinking: 2 int sinking, 2 double sinking
    public int testLoopRemIntNested(int n) {
        int testVar = 1;
        double testVarD1 = 1234.2345d;
        double testVarD2 = -123456789.2345d;
        long additionalVar1 = 0x100;
        double additionalVar2 = 1d;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (double f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarD1 %= 123.2345d;
            }
            for (double f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVar %= 3;
                testVarD2 %= 2;
            }
        }


        testVarD1 +=1;

        return testVar + (int)additionalVar1 + (int)testVarD1 + (int)testVarD2;
    }



 


    public static void main(String[] args)
    {
         System.out.println(new Main().testLoopAddInt());
System.out.println(new Main().testLoopAddLongSeveralLoops(10));
System.out.println(new Main().testLoopRemIntNested(10));
System.out.println(new Main().testLoopMulLongNested(10));
System.out.println(new Main().testLoopMulIntNested(10));
System.out.println(new Main().testLoopSubLong(10));
System.out.println(new Main().testLoopSubLongModifiedInOuterLoop(10));
System.out.println(new Main().testLoopSubLongReadInOuterLoopAndNonConstBound(10));
System.out.println(new Main().testLoopSubInt());
System.out.println(new Main().testLoopDivNested1(10));
System.out.println(new Main().testLoopDivNested2(10));
System.out.println(new Main().testLoopDivNested3(10));
System.out.println(new Main().testLoopDivNested4(10));
    }

}  

