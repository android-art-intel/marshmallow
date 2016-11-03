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

package OptimizationTests.TrivialLoopEvaluator.DivTests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
	
	public static final int maxIter=999;
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
				System.out.println("Test "+met.getName()+" result: "+met.invoke(main));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	public int testInt1(){
		int i=1;
		int res=123456;
		int res1=1;
		for (;i<5;i++){
			res1+=res/i;
		}
		return res1;
	}
	public int testInt2(){
		int i=1;
		int res=123456;
		int res1=1;
		int tmp=-10;
		for (;i<50;i++){
			res/=i;
			tmp--;
			res1=res/tmp;
		}
		return res1;
	}
	public int testInt3(){
		int i=1;
		int res=123456778;
		int res1=1;
		int tmp=1;
		for (i=0;i<maxIter;i++){
			res1+=res/tmp;
			tmp++;
		}
		return res1;
	}
	public int testInt4(){
		int i=1;
		int res=1234567;
		int res1=10000;
		int tmp=-10;
		for (i=tmp;i<res;i++){
			res1+=res/tmp;
		}
		return res1;
	}
	public long testLong1(){
		long i=1;
		long res=123456;
		long res1=0;
		for (;i<5;i++){
			res1+=res/i;
		}
		return res1;
	}
	public long testLong2(){
		long i=1;
		long res=123456;
		long res1=0;
		long tmp=10;
		for (;i<50;i++){
			res1+=res/i;
			tmp++;
			res+=tmp;
		}
		return res1;
	}
	public long testLong3(){
		long i=1;
		long res=123455;
		long res1=0;
		long tmp=10;
		for (i=0;i<maxIter;i++){
			res1+=res/tmp;
			tmp++;
		}
		return res1;
	}
	public long testLong4(){
		long i=1;
		long res=1234456;
		long res1=10000;
		long tmp=-10;
		for (i=tmp;i<res;i++){
			res1+=res/tmp;
		}
		return res1;
	}
	public byte testByte1(){
		byte i=1;
		byte res=126;
		byte res1=1;
		for (;i<5;i++){
			res1+=res/i;
		}
		return res1;
	}
	public byte testByte2(){
		byte i=1;
		byte res=126;
		byte res1=1;
		byte tmp=-10;
		for (;i<50;i++){
			res/=i;
			tmp--;
			res1=(byte) (res/tmp);
		}
		return res1;
	}
	public byte testByte3(){
		byte i=1;
		byte res=127;
		byte res1=1;
		byte tmp=1;
		for (i=Byte.MIN_VALUE/100;i<Byte.MAX_VALUE/100;i++){
			res1+=res/tmp;
			tmp++;
		}
		return res1;
	}
	public byte testByte4(){
		byte i=1;
		byte res=123;
		byte res1=100;
		byte tmp=-10;
		for (i=tmp;i<res;i++){
			res1+=res/tmp;
		}
		return res1;
	}
	public short testShort1(){
		short i=1;
		short res=1236;
		short res1=0;
		for (;i<5;i++){
			res1+=res/i;
		}
		return res1;
	}
	public short testShort2(){
		short i=1;
		short res=1236;
		short res1=0;
		short tmp=10;
		for (;i<50;i++){
			res1+=res/i;
			tmp++;
			res+=tmp;
		}
		return res1;
	}
	public short testShort3(){
		short i=1;
		short res=1234;
		short res1=0;
		short tmp=10;
		for (i=0/100;i<maxIter/100;i++){
			res1+=res/tmp;
			tmp++;
		}
		return res1;
	}
	public short testShort4(){
		short i=1;
		short res=12344;
		short res1=10000;
		short tmp=-10;
		for (i=tmp;i<res;i++){
			res1+=res/tmp;
		}
		return res1;
	}
	public int testFloat1(){
		int i=1;
		float res=1234567.0f;
		float res1=0.0f;
		for (;i<5;i++){
			res1+=res/i;
		}
		return Float.floatToIntBits(res1);
	}
	public int testFloat2(){
		int i=1;
		float res=1234456.0f;
		float res1=0.0f;
		float tmp=10.0f;
		for (;i<50;i++){
			res1+=res/i;
			tmp++;
			res+=tmp;
		}
		return Float.floatToIntBits(res1);
	}
	public int testFloat3(){
		int i=1;
		float res=1234456.0f;
		float res1=0.0f;
		float tmp=10.0f;
		for (i=00;i<maxIter;i++){
			res1+=res/tmp;
			tmp++;
		}
		return Float.floatToIntBits(res1);
	}
	public int testFloat4(){
		int i=1;
		float res=12345567.0f;
		float res1=10000.0f;
		float tmp=-10.0f;
		for (i=(int)tmp;i<res;i++){
			res1+=res/tmp;
		}
		return Float.floatToIntBits(res1);
	}
	public double testDouble1(){
		int i=1;
		double res=123434576.0;
		double res1=0.0;
		for (;i<5;i++){
			res1+=res/i;
		}
		return res1;
	}
	public double testDouble2(){
		int i=1;
		double res=1234356.0;
		double res1=0.0;
		double tmp=-10.0;
		for (;i<50;i++){
			res1+=res/i;
			tmp--;
			res+=tmp;
		}
		return res1;
	}
	public double testDouble3(){
		int i=1;
		double res=1234456.0;
		double res1=0.0;
		double tmp=-10.0;
		for (i=00;i<maxIter;i++){
			res1+=res/tmp;
			tmp--;
		}
		return res1;
	}
	public double testDouble4(){
		int i=1;
		double res=11233456.0;
		double res1=10000.0;
		double tmp=-10.0;
		for (i=(int)tmp;i<res;i++){
			res1+=res/tmp;
		}
		return res1;
	}
	public int testInt5(){
		int i=1;
		int res=123456;
		int res1=Integer.MAX_VALUE;
		for (;i<5;i++){
			res1/=res/i;
		}
		return res1;
	}
	public int testInt6(){
		int i=1;
		int res=123456;
		int res1=1;
		int tmp=-10;
		for (;i<50;i++){
			res/=i;
			tmp--;
			res1=res/tmp;
		}
		return res1;
	}
	public int testInt7(){
		int i=1;
		int res=123456778;
		int res1=1;
		int tmp=50000000;
		for (i=0;i<maxIter;i++){
			res1/=res/tmp;
		}
		return res1;
	}
	public int testInt8(){
		int i=1;
		int res=1234567;
		int res1=500000;
		int tmp=-10;
		for (i=tmp;i<res;i++){
			res1/=res/tmp;
		}
		return res1;
	}
	public long testLong5(){
		long i=1;
		long res=123456;
		long res1=2000000000;
		for (;i<5;i++){
			res1/=res/i;
		}
		return res1;
	}
	public long testLong6(){
		long i=1;
		long res=126;
		long res1=2000000000;
		long tmp=10;
		for (;i<50;i++){
			res1/=res/i;
			tmp++;
			res+=tmp;
		}
		return res1;
	}
	public long testLong7(){
		long i=1;
		long res=123455;
		long res1=0;
		long tmp=1;
		for (i=0;i<maxIter;i++){
			res1/=tmp;
			tmp++;
		}
		return res1;
	}
	public long testLong8(){
		long i=1;
		long res=1234456;
		long res1=1000000000;
		long tmp=1;
		for (i=tmp;i<res;i++){
			res1/=tmp;
			tmp++;
		}
		return res1;
	}
	public byte testByte5(){
		byte i=1;
		byte res=12;
		byte res1=127;
		for (;i<5;i++){
			res1/=res-i;
		}
		return res1;
	}
	public byte testByte6(){
		byte i=1;
		byte res=126;
		byte res1=127;
		byte tmp=-10;
		for (;i<50;i++){
			res+=i;
			tmp--;
			res1=(byte) (res/tmp);
		}
		return res1;
	}
	public byte testByte7(){
		byte i=1;
		byte res=127;
		byte res1=1;
		byte tmp=1;
		for (i=Byte.MIN_VALUE/100;i<Byte.MAX_VALUE/100;i++){
			res1/=res-tmp;
			tmp++;
		}
		return res1;
	}
	public byte testByte8(){
		byte i=1;
		byte res=123;
		byte res1=100;
		byte tmp=-10;
		for (i=tmp;i<res;i++){
			res1/=res+tmp;
		}
		return res1;
	}
	public short testShort5(){
		short i=1;
		short res=1236;
		short res1=12334;
		for (;i<5;i++){
			res1/=res-i;
		}
		return res1;
	}
	public short testShort6(){
		short i=1;
		short res=12336;
		short res1=0;
		short tmp=10;
		for (;i<50;i++){
			res1+=res/i;
			tmp++;
			res/=tmp;
		}
		return res1;
	}
	public short testShort7(){
		short i=1;
		short res=1234;
		short res1=0;
		short tmp=10;
		for (i=0/100;i<maxIter/100;i++){
			res1/=res-tmp;
			tmp++;
		}
		return res1;
	}
	public short testShort8(){
		short i=1;
		short res=12;
		short res1=10000;
		short tmp=-10;
		for (i=tmp;i<res;i++){
			res1/=res-tmp;
		}
		return res1;
	}
	public float testFloat5(){
		int i=1;
		float res=1234567.0f;
		float res1=0.0f;
		for (;i<5;i++){
			res1/=res/i;
		}
		return res1;
	}
	public float testFloat6(){
		int i=1;
		float res=1234456.0f;
		float res1=0.0f;
		float tmp=10.0f;
		for (;i<50;i++){
			res1+=res/i;
			tmp++;
			res/=tmp;
		}
		return res1;
	}
	public float testFloat7(){
		int i=1;
		float res=1234456.0f;
		float res1=0.0f;
		float tmp=10.0f;
		for (i=00;i<maxIter;i++){
			res1/=res/tmp;
			tmp++;
		}
		return res1;
	}
	public float testFloat8(){
		int i=1;
		float res=12345567.0f;
		float res1=10000.0f;
		float tmp=-10.0f;
		for (i=(int)tmp;i<res;i++){
			res1/=res/tmp;
		}
		return res1;
	}
	public double testDouble5(){
		int i=1;
		double res=123434576.0;
		double res1=0.0;
		for (;i<5;i++){
			res1/=res/i;
		}
		return res1;
	}
	public double testDouble6(){
		int i=1;
		double res=1234356.0;
		double res1=0.0;
		double tmp=-10.0;
		for (;i<50;i++){
			res1+=res/i;
			tmp--;
			res/=tmp;
		}
		return res1;
	}
	public double testDouble7(){
		int i=1;
		double res=1234456.0;
		double res1=0.0;
		double tmp=-10.0;
		for (i=00;i<maxIter;i++){
			res1/=res/tmp;
			tmp--;
		}
		return res1;
	}
	public double testDouble8(){
		int i=1;
		double res=11233456.0;
		double res1=10000.0;
		double tmp=-10.0;
		for (i=(int)tmp;i<res;i++){
			res1/=res/tmp;
		}
		return res1;
	}
}
