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

package OptimizationTests.regression.test118838;

import java.io.*; 

/*
 * The test checks duration of execution of Test class.
 * It takes less than a second in fast interpreter and should not take much longer with JIT.
 * We don't pass different VM modes to dalvik process so it always executes in default mode.
 */
public class Main {
    static final int nbLOOP = 100000;

    public static void main(String[] args) {
        long starttime = System.currentTimeMillis();
        Process p = null;
        String dalvikProcess = "dalvikvm -cp /sdcard/tests/regression.jar regression.bugs.test118838.Test";
        try {
            p = Runtime.getRuntime().exec(dalvikProcess);
        } catch (java.io.IOException ioe) {
        }
        try {
            p.waitFor();
        } catch (InterruptedException ie) {
        }
        long stoptime = System.currentTimeMillis();
        long delta = stoptime - starttime;
        if (delta > 10000l) {
            System.out.println("TEST FAILED: duration = " + delta + ", expected duration < 10000");
        } else {
            System.out.println("TEST PASSED");
        }
    }

}
