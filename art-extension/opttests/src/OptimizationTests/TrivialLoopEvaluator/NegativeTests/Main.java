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

package OptimizationTests.TrivialLoopEvaluator.NegativeTests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

	
public class Main {
	
	public static final int maxIter=1000;
	public static int public_var=0;
	public static final int public_final_var=100;
	public static class Test{
		public int value=11;
		private int value1=22;
		public int getter1(){
			return value1;
		}
		public void setter1(int k){
			value1=k;
		}
		public int getter(){
			return value;
		}
		public void setter(int k){
			value=k;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		Class<Main> cls = Main.class;
		Method[] mets = cls.getDeclaredMethods();
		Arrays.sort(mets, new Comparator<Method>() {	
			@Override
			public int compare(Method arg0, Method arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});
		for (Method met:mets){
			if (met.getName().equalsIgnoreCase("main"))
				continue;
			try {
				if (met.getName().indexOf("Arg")>=0){
					System.out.print("Test "+met.getName()+" result: ");
					System.out.print(met.invoke(main,5));
				}
				else{
					System.out.print("Test "+met.getName()+" result: ");
					System.out.print(met.invoke(main));
				}
				System.out.println();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				System.out.println("Exception caught! "+e.getTargetException().getMessage());
			}
		}
	}
	public int testException1(){
		int i=0;
		int ret=100000000;
		for (;i<=100;i++){
			ret+=ret/(100-i);
		}
		return ret;
	}
	public int testException2(){
		int i=0;
		int ret=100000000;
		int[] arr  = new int[]{0,1,2};
		for (;i<=100;i++){
			ret=ret+(arr[i]);
		}
		return ret;
	}
	public int testException3() throws Exception{
		int i=0;
		int ret=100000000;
		for (;i<=100;i++){
			ret=ret+i;
			throw (new Exception("Exception thrown"));
		}
		return ret;
	}
	public int testInnerLoop1(){
		int i=0;
		int k=1;
		int ret=100;
		for (;i<=100;i++){
			for (k=1;k<100;k++){
			    ret+=i%k;
			}
		}
		return ret;
	}
	public int testBranch1(){
		int i=0;
		int k=1;
		int ret=100;
		for (;i<=100;i++){
			if (i<50)
				ret+=2*i;
			else ret+=i-1;
		}
		return ret;
	}
	public int testBranch2(){
		int i=0;
		int k=1;
		int ret=100;
		for (;i<=100;i++){
			ret+=(i<50 ? 3 : 3*i);
		}
		return ret;
	}
	public int testBranch3(){
		int i=0;
		int k=1;
		int ret=100;
		for (;i<=100;i++){
			switch (i%4){
			case 0:
				ret+=-1;
				break;
			case 1:
				ret*=2;
				break;
			case 2:
				ret-=i*1000;
			default:
				ret++;
			}
		}
		return ret;
	}
	public int testFakeLoop1(){
		int i=0;
		int k=1;
		int ret=100;
		for (;i<=100;i++){
			i+=k;
			ret=10;
		}
		return ret;
	}
	public int testPublicVar1(){
		int i=0;
		int k=1;
		int ret=100;
		for (;i<=100;i++){
			i+=k;
			ret+=public_var;
		}
		return ret;
	}
	public int testPublicVar2(){
		int i=0;
		int k=1;
		int ret=100;
		for (;i<=100;i++){
			i+=k;
			ret+=public_final_var;
		}
		return ret;
	}
	public int testGetter1(){
		int i=0;
		int k=1;
		Test t = new Test();
		int ret=t.getter();
		for (;i<=100;i++){
			i+=k;
			ret+=1;
		}
		return ret;
	}
	public int testGetter2(){
		int i=0;
		int k=1;
		Test t = new Test();
		int ret=t.getter1();
		for (;i<=100;i++){
			i+=k;
			ret+=1;
		}
		return ret;
	}
	public int testInvoke1(){
		int i=0;
		int k=1;
		Test t = new Test();
		int ret=11;
		for (;i<=100;i++){
			i+=k;
			t.setter(k);
			ret+=1;
		}
		return ret;
	}
	public int testInvoke2(){
		int i=0;
		int k=1;
		Test t = new Test();
		int ret=11;
		for (;i<=100;i++){
			i+=k;
			t.setter1(k);
			ret+=1;
		}
		return ret;
	}
	public int testArg1(int val){
		int i=0;
		int k=1;
		int ret=val;
		for (;i<=100;i++){
			i+=k;
			ret+=1;
		}
		return ret;
	}
	public int testMaxIter1(){
		int i=0;
		int k=1;
		int ret=0;
		for (i=0;i<maxIter+1;i++){
			ret+=i+k;
		}
		return ret;
	}
	public int testMaxIter2(){
		int i=0;
		int k=1;
		int ret=0;
		for (i=Integer.MIN_VALUE;i<Integer.MAX_VALUE;i++){
			ret+=i+k;
		}
		return ret;
	}
}
