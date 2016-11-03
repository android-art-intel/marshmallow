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

package OptimizationTests.regression.test3670_2;

public class Main {

    public static long instanceCount=0L;
	
public static void main(String[] strArr) {

    int i=50383, i2=0, i3=30492;
    short sArr[]=new short[100];

    for (i = 2; i < 96; i++) {
       
        for (i2 = 2; i2 < 33; i2++) {
            i3 = i;
            while ((i3 -= 3) > 0) {
                Main.instanceCount += sArr[i3];
            }
        }

    }

    System.out.println("Main.instanceCount: " + Main.instanceCount);
}
}
