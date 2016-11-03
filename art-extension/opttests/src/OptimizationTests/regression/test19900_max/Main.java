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

package OptimizationTests.regression.test19900_max;

public class Main {

    static int NUM_ITERATIONS = 2;
    static int[] limValues = { 5, 7 };
    static int[] a = new int[10];

    static void initArray() {
        for (int i = 0; i < a.length; i++)
            a[i] = 0;
    }

    static void buildArray(int m) {
        for (int i = 0; i < a.length; i++)
            a[i] = Math.max(i, m);
    }

    static boolean checkArray(int m) {
        boolean result = true;
        for (int k = 0; k < a.length; k++) {
            if (a[k] < m) {
                if (result) System.out.println();
                System.out.println("\t***ERROR*** a[" + k + "] = " + a[k]
                        + " - wrong value");
                System.out.println("\t\texpected value: " + m);
                result = false;
            }
        }
        return result;
    }

    static public void runTest() {
        boolean status = true;
        for (int i = 0; i < NUM_ITERATIONS; i++) {

            for (int j = 0; j < limValues.length; j++) {
                initArray();

                System.out.print("Max limit value:" + limValues[j]);
                buildArray(limValues[j]);

                if (!checkArray(limValues[j])) {
                    System.out.println("***FAIL***");
                    status = false;
                } else {
                    System.out.println(" - OK");
                }

                System.out.println();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }
        if (status)
            System.out.println("TEST PASSED");
        else
            System.out.println("TEST FAILED");

    }

    static void parseParams(String[] params) {
        if (params.length >= 1) {
            NUM_ITERATIONS = Integer.parseInt(params[0]);
        }
    }

    public static void main(String[] args) {
        parseParams(args);
        runTest();
    }

}
