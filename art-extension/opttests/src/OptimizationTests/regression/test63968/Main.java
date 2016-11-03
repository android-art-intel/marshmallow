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

package OptimizationTests.regression.test63968;

public class Main {
    public static final int ARRSIZE = 200;
    // public static final String TESTNN = "testMethodArgs_";
    public static final int LOOPCNT = 500;

    public static final int RESULT_PASS = 104;
    public static final int RESULT_FAIL = 105;
    public static final int PASSANS = RESULT_PASS;

    public int test(String index) {
        int cntLoop = LOOPCNT;
        int cntClss = ARRSIZE;
        System.out
                .println("Use class which contains method with many arguments");
        System.out.println("Number of class instantiations: " + cntClss);
        System.out.println("Number of iterations: " + cntLoop);

        Class arrClss;
        Object arrObj = null;

        String name = this.getClass().getPackage().getName()
                .concat(".testMethodArgs" + index);
        try {
            arrClss = Class.forName(name);
            arrObj = arrClss.newInstance();
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
        testMethodArgs tt = (testMethodArgs) arrObj;
        tt.printInfo();

        for (int t = 0; t < cntLoop; t++) {
            for (int i = 0; i < cntClss; i++) {
                int x = tt.run_call(PASSANS);
                if (x != PASSANS) {
                    System.out.println("Bad answer: " + x + "  step " + i
                            + "  iteration: " + t);
                    return RESULT_FAIL;
                }
                int ans = tt.getF();
                if (ans != PASSANS) {
                    System.out.println("getF() returns unexpected value: "
                            + ans + " should be " + PASSANS);
                    return RESULT_FAIL;
                }
            }
            arrObj = null;
            arrClss = null;
        }
        return RESULT_PASS;
    }

    public static void main(String[] args) {
        for (int i = 6; i < 12; i++) {
            new Main().test(new Integer(i).toString());
        }
    }
}
