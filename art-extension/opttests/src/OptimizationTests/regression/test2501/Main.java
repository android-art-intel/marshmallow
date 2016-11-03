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

package OptimizationTests.regression.test2501;

public class Main {

  public void test999(){
    int i = 0;
    int res = 0;
    int tmp = -10;

    for (i = 0; i < 999; i++){
      res += tmp;
      tmp++;
    }
    System.out.println("res 999 = " + res); 
  }

  public void test1000(){
    int i = 0;
    int res = 0;
    int tmp = -10;

    for (i = 0; i < 1000; i++){
      res += tmp;
      tmp++;
    }
    System.out.println("res 1000 = " + res); 
  }

  public void test1001(){
    int i = 0;
    int res = 0;
    int tmp = -10;

    for (i = 0; i < 1001; i++){
      res += tmp;
      tmp++;
    }
    System.out.println("res 1001 = " + res); 
  }

  public static void main(String[] args) {
    new Main().test999();
    new Main().test1000();
    new Main().test1001();
  }
}
