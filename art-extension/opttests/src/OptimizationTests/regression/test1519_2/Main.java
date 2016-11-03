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

// Simplified test
package OptimizationTests.regression.test1519_2;

import OptimizationTests.regression.shared.*;

public class Main {

  public int iMeth(long l) {
    short s=15359;
    int i2=49786, i3=9, i4=3;

    for (i2 = 1; i2 < 90; ++i2) {
        i3 = (int)((i3 - 98 ) - ( l ^ l));
        i3 += (int)(43 + Math.min(l, 3L) - i2 * l);
    }
  
    for (i4 = 1; i4 < 85; i4++) {
        s += (short)l;
    }
    return (int)s;
  }

  public static void main(String[] strArr) {
      System.out.println("res = " + new Main().iMeth(667L));
  }
}
