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

package OptimizationTests.regression.test12609;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class Main {
    private static final boolean isDalvik = "Dalvik".equals(System.getProperty("java.vm.name"));

    public void runSubprocess()
    {
        ProcessBuilder builder = new ProcessBuilder().redirectErrorStream(true);
        if (isDalvik) {
            builder.command().add("dalvikvm");
            builder.command().add("-cp");
            builder.command().add("/sdcard/data/Main.jar");
            builder.command().add("OptimizationTests.regression.test12609.Subprocess");
        } else {
            builder.command().add("java");
            builder.command().add("-cp");
            builder.command().add("classes");
            builder.command().add("OptimizationTests.regression.test12609.Subprocess");
        }

        Process p = null;
        try {
            System.out.println("[main] starting subprocess...");
            p = builder.start();
            System.out.println("[main] started.");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

        try {
            String s = null;
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(p.getInputStream()));
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            p.destroy();
            System.out.println("[main] subprocess finished\n");
        }
    }

    public void run(int number)
    {
        for (int i = 0; i < number; i++) {
            runSubprocess();
        }
    }

    public static void main(String[] args)
    {
        new Main().run(400);
        System.out.println("TEST COMPLETED");
    }
}
