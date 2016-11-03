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

package OptimizationTests.ConstantCalculationSinking.FloatIV_02;

/**
*
* Test with float point induction variable + nested loops + other cases
* Expected result: sinking is expected if induction variable is presented as (N + M/2^K) in each iteration and
*
**/

public class Main {                                                                                                                                                   
    final int iterations = 1100;
    float non_const_iterations = 1100.123f;

    class C {
        double value = 0;
        C(double x) {
            value = x;
        }
    }
  
    // Nested loop: int sinking expected, no float sinking because no overflow
    public int testLoopAddInt() {
        int testVar = 0xAAA;
        float testVarF = 1.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1.2345f;
        double additionalVar3 = 6.7899d;
        int additionalVar4 = 0xB;
        for (int i = 0; i < 1100; i++) {
            for (float f = 0 ; f < iterations; f += 0.25f) {
                additionalVar1 = 2*additionalVar1/100 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
                testVar +=  additionalVar4;
                testVarF += additionalVar2;
            }
        }
        return testVar + (int)(testVarF + additionalVar1);
    }


    // 3 sinking expected
    public float testLoopAddLongSeveralLoops(int n) {
        long testVar = Integer.MAX_VALUE;
        float testVarF1 = 1234.2345f;
        float testVarF2 = 1234.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        testVar += (int)testVarF1;

        for (float f = 0.25f ; f < iterations ; f += 1.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
            testVarF1 +=  Float.MAX_VALUE - 1  ;
        }

        testVarF1 = 555.5f;

        float f1 = 0.25f ;
        while (f1 < iterations) 
        {   f1 += 1.25f;
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f1/3 + n/3;
            testVarF1 +=  Float.MAX_VALUE - 1;
        }

        float f2 = 0.25f ;
        do {
            f2 += 1.25f;
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
            testVarF2 +=  Float.MAX_VALUE - 1;
        } while (f2 < iterations);



        return testVar + testVarF1 - testVarF2;
    }



    // Nested loop: int sinking expected, float sinking expected because of overflow
    public int testLoopSubInt() {
        int testVar = 0xAAA;
        float testVarF = 1.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        int additionalVar4 = 0xBBB;
        for (float f1 = 0f ; f1 < iterations; f1 += 10.26f) {
            for (float f2 = 0f ; f2 < iterations; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f1/3;
                testVar -=  additionalVar4;
                testVarF -= -Float.MAX_VALUE/31;
            }
        }
        return testVar + (int)(testVarF + additionalVar1);
    }

    // Nested loop: int sinking expected, float sinking expected because of overflow
    public float testLoopSubLong(int n) {
        long testVar = Integer.MAX_VALUE;
        float testVarF1 = 1234.2345f;
        float testVarF2 = -1234.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (float f1 = 0f ; f1 < iterations; f1 += 10.26f) {
            for (float f2 = 0.0001f ; f2 < iterations; f2 += 1.25f) {
 
                for (float f = 0.015625f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar -=  additionalVar4;
                }

//                testVarF += 3;
                for (float f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar += 3;
                    testVarF1 -=  Float.MAX_VALUE - 1  ;
                }

                testVar -=  additionalVar4;
  
                for (float f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVarF2 -=  Float.MAX_VALUE - 1;
                }

                testVar += Long.MAX_VALUE/3;

            }
        }
        return testVar + testVarF1 - testVarF2;
    }

    // Nested loop: 2 int sinkings expected, only 1 float sinking expected because second float variable is modified in outer loop
    public float testLoopSubLongModifiedInOuterLoop(int n) {
        long testVar = Integer.MAX_VALUE;
        float testVarF1 = 1234.2345f;
        float testVarF2 = -1234.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (float f1 = 0f ; f1 < iterations; f1 += 10.26f) {
            // float sinking candidate is used in outer loop -> shouldn't be sunk
            testVarF1 += 3;
            for (float f2 = 0.0001f ; f2 < iterations; f2 += 1.25f) {
 
                for (float f = 0.015625f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar -=  additionalVar4;
                }

                for (float f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar += 3;
                    testVarF1 -=  Float.MAX_VALUE - 1  ;
                }

                testVar -=  additionalVar4;
  
                for (float f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    // testVarF2 should be sunk
                    testVarF2 -=  Float.MAX_VALUE - 1;
                }

                testVar += Long.MAX_VALUE/3;

            }
        }
        return testVar + testVarF1 - testVarF2;
    }

    // Nested loop: one int sinking expected (second should be rejected due to unkown bound?), two float sinking expected because second float variable used in outer loop but not modified
    public float testLoopSubLongReadInOuterLoopAndNonConstBound(int n) {
        long testVar = Integer.MAX_VALUE;
        float testVarF1 = 1234.2345f;
        float testVarF2 = -1234.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 0xBBB;
        for (float f1 = 0f ; f1 < iterations; f1 += 10.26f) {
            testVar += (int)testVarF1;
            for (float f2 = 0.0001f ; f2 < iterations; f2 += 1.25f) {
 
                for (float f = 0.015625f ; f < non_const_iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar -=  additionalVar4;
                }

                for (float f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVar += 3;
                    testVarF1 -=  Float.MAX_VALUE - 1  ;
                }

                testVar -=  additionalVar4;
  
                for (float f = 0.25f ; f < iterations ; f += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3 + n/3;
                    testVarF2 -=  Float.MAX_VALUE - 1;
                }

                testVar += Long.MAX_VALUE/3;

            }
        }
        return testVar + testVarF1 - testVarF2;
    }


    //1 sinking: int is rejected because of overflow, float is sunk
    public int testLoopMulIntNested(int n) {
        int testVar = 1;
        float testVarF1 = 1234.2345f;
        float testVarF2 = -1234.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1f;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (float f = -0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar *=  additionalVar4;
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                    testVar *= 3;
                    testVarF1 *= Float.MAX_VALUE - 1234.1234f;
                }


        }
        return testVar + (int)additionalVar1;
    }

    //no sinking: int is rejected because of overflow, float is rejected because constant is < 0
    public long testLoopMulLongNested(long n) {
        long testVar = 1;
        float testVarF1 = 1234.2345f;
        float testVarF2 = -1234.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1f;
        double additionalVar3 = 1d;
        long additionalVar4 = 3;
        for (float f1 = 0 ; f1 < iterations; f1 += 99.30f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f1/3;
            testVar *=  additionalVar4;
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                    additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                    testVar *= 3;
                    testVarF1 *= -Float.MAX_VALUE - 1  ;
                }


        }
        return testVar + (long)additionalVar1  + (long)testVarF1 + (long)testVarF2;
    }





    //4 sinking: 2 float sinking, 2 int sinking
    public int testLoopDivNested1(int n) {
        int testVar = 1;
        float testVarF1 = 1234.2345f;
        float testVarF2 = -123456789.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1f;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (float f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVar /= 3;
                testVarF1 /= 123456789.2345f;
            }
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarF2 /= 123456789.2345f;
                testVar += -1;
            }
        }


        testVarF1 +=1;

        return testVar + (int)additionalVar1 + (int)testVarF1 + (int)testVarF2;
    }

 


    // 1 sinking expected
    public long testLoopDivNested2(long n) {
        long testVar = 0xAAA;
        float testVarF1 = 12345.12345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 123.45f;
        double additionalVar3 = 678.9d;
        long additionalVar4 = 3L;
        for (float f = -iterations ; f < iterations; f += 99.25f) {
            float d = 0.25f ;
            while (d < iterations) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)d/3 +  (long)d % 3 - (int)additionalVar2/2 + (long)d*4 +  (long)d*5 + (long)d*6 + n/2 + n/3;
                testVarF1 /= 123.123f;
                d += 0.75f;
            }
        }
        return (long)testVarF1 + (long)additionalVar1;
    }

    //4 sinking: 2 float sinking, 2 int sinking: float overflow to infinity
    public float testLoopDivNested3(int n) {
        int testVar = 1;
        float testVarF1 = Float.MAX_VALUE/2;
        float testVarF2 = Float.MIN_VALUE/2;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1f;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (float f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVar /= 3;
                testVarF1 /= 0.34567892345f;
            }
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarF2 /= 0.1234567892345f;
                testVar += -1;
            }
        }


        testVarF1 +=1;

        return testVar + additionalVar1 + testVarF1 + testVarF2;
    }

     //No sinking: division by negative constant in nested loop
    public float testLoopDivNested4(int n) {
        int testVar = 1;
        float testVarF1 = Float.MAX_VALUE/2;
        float testVarF2 = Float.MIN_VALUE/2;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1f;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (float f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarF1 /= -0.00034567892345f;
            }
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarF2 /= -0.1234567892345f;
            }
        }


        testVarF1 += 1;
        testVarF2 -= 1;

        return testVar + additionalVar1 + testVarF1 + testVarF2;
    }



    //4 sinking: 2 int sinking, 2 float sinking
    public int testLoopRemIntNested(int n) {
        int testVar = 1;
        float testVarF1 = 1234.2345f;
        float testVarF2 = -123456789.2345f;
        long additionalVar1 = 0x100;
        float additionalVar2 = 1f;
        double additionalVar3 = 1d;
        int additionalVar4 = 3;
        for (float f = 0 ; f < iterations; f += 99.25f) {
            additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f/3;
            testVar +=  additionalVar4;
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVarF1 %= 123.2345f;
            }
            for (float f2 = 0.25f ; f2 < iterations ; f2 += 1.25f) {
                additionalVar1 = 2*additionalVar1 + 1 + 2 + additionalVar4 + (long)additionalVar3 + (int)additionalVar2 + (long)f2/3 + n/3;
                testVar %= 3;
                testVarF2 %= 2;
            }
        }


        testVarF1 +=1;

        return testVar + (int)additionalVar1 + (int)testVarF1 + (int)testVarF2;
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

