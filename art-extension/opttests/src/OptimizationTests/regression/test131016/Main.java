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

package OptimizationTests.regression.test131016;

public class Main {
    protected String msg;
    protected int testResultNum;
    protected int testIteration;
    protected boolean shouldNPE;

    public void testIt() {
        System.out.println("test loop: v20");

        int LENGTH = getArrayLength();
        shouldNPE = false;
        loopNoCatch(-1, -1, 1077);

        shouldNPE = true;
        loopNoCatch(0, 0, 1);
        loopCatch(0, LENGTH, 1);
        loopCatch(LENGTH / 2, LENGTH, 153);
    }

    final public void printResult(String msg) {
        System.out.printf("%2$02d_%3$02d: hash=%4$s %5$s",
                getClass().getName(), testIteration, testResultNum, -1, msg);
        System.out.println();
        testResultNum++;
    }

    final public void loopNoCatch(int start, int end, int delta) {
        loop(start, end, delta);
        printResult("");
    }

    final public void loopCatch(int start, int end, int delta) {
        try {
            loop(start, end, delta);
        } catch (Exception e) {
            printResult(("Caught " + e)
                    .replace("length=" + getArrayLength() + "; ", "")
                    .replace("index=", "")
                    .replace("/ by zero", "divide by zero"));
            return;
        }
        printResult("ERROR: reached unreachable code");
        throw new RuntimeException("reached unreachable code");
    }

    static public final int ARRAY_LENGTH_D1 = 1111;
    static public final int ARRAY_LENGTH_D2 = 11;
    static public final int ARRAY_LENGTH_D3 = 7;
    static public final int ARRAY_LENGTH_D4 = 2;
    static private final int[][] ai2 = new int[ARRAY_LENGTH_D1][ARRAY_LENGTH_D2];
    static private final long[][][] al3 = new long[ARRAY_LENGTH_D1][ARRAY_LENGTH_D2][ARRAY_LENGTH_D3];
    static private final byte[][][][] ab4 = new byte[ARRAY_LENGTH_D1][ARRAY_LENGTH_D2][ARRAY_LENGTH_D3][ARRAY_LENGTH_D4];

    public int getArrayLength() {
        return ARRAY_LENGTH_D1;
    }

    public void loop(int start, int end, int delta) {
        if (shouldNPE)
            loopArray3(ai2, al3, null, start, end, delta);
        else
            loopArray3(ai2, al3, ab4, start, end, delta);
    }

    public void loopArray3(int[][] ai2, long[][][] al3, byte[][][][] ab4,
            int start, int end, int delta) {
        for (int i = start + 1; i < end - 1; i += delta) {
            int[][] cc2 = ai2;
            final int[][] uu2 = ai2;
            byte[][][][] uu4 = ab4;
            cc2 = ai2;
            ai2[i][0] += (ai2[i - 1][0]) / 0xa1;
            ai2[i][0] += (cc2[i - 1][0] + cc2[i][1] + cc2[i + 1][2]) % 0xa3;
            cc2 = uu2;
            cc2[i][0] += (ai2[i - 1][0] + ai2[i][1] + ai2[i + 1][2]) / 0xb1;
            cc2[i][0] += (cc2[i - 1][0] + cc2[i][1] + cc2[i + 1][2]) % 0xb3;
            cc2 = ai2;
            uu2[i][0] += (ai2[i - 1][0] + ai2[i][1] + ai2[i + 1][2]);
            uu2[i][0] += (cc2[i - 1][0] + cc2[i + 1][2]);
            if (i % 2 == 0) {
                if (i % 4 == 0) {
                    ai2[i][0] += (ai2[i - 1][0] + ai2[i + 1][2]);
                } else {
                    ab4[i][0][0][0] += (ab4[i - 1][0][0][0] + ab4[i + 1][2][0][0]);
                }
                al3[i][0][0] += (al3[i - 1][0][0]);
            } else {
                ab4[i][0][0][0] += (ab4[i - 1][0][0][0]);
            }
            al3[i][0][0] += (al3[i - 1][0][0] + al3[i + 1][2][0]);
            uu4[i][0][0][0] += (ab4[i - 1][0][0][0]);
        }
    }

    public static void main(String[] args) {
        new Main().testIt();
    }
}
