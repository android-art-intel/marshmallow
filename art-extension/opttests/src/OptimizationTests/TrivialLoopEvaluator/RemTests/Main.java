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

package OptimizationTests.TrivialLoopEvaluator.RemTests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
	public static final int maxIter=999;
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
			res1+=res%i;
		}
		return res1;
	}
	public int testInt2(){
		int i=1;
		int res=123456;
		int res1=1;
		int tmp=-10;
		for (;i<50;i++){
			res%=i;
			tmp--;
			res1=res%tmp;
		}
		return res1;
	}
	public int testInt3(){
		int i=1;
		int res=123456778;
		int res1=1;
		int tmp=1;
		for (i=0;i<maxIter;i++){
			res1+=res%tmp;
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
			res1+=res%tmp;
		}
		return res1;
	}
	public long testLong1(){
		long i=1;
		long res=123456;
		long res1=0;
		for (;i<5;i++){
			res1+=res%i;
		}
		return res1;
	}
	public long testLong2(){
		long i=1;
		long res=123456;
		long res1=0;
		long tmp=10;
		for (;i<50;i++){
			res1+=res%i;
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
			res1+=res%tmp;
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
			res1+=res%tmp;
		}
		return res1;
	}
	public byte testByte1(){
		byte i=127;
		byte res=12;
		byte res1=1;
		for (;i<5;i++){
			res1+=res%i;
		}
		return res1;
	}
	public byte testByte2(){
		byte i=1;
		byte res=126;
		byte res1=1;
		byte tmp=-10;
		for (;i<50;i++){
			res%=i;
			tmp--;
			res1=(byte) (res%tmp);
		}
		return res1;
	}
	public byte testByte3(){
		byte i=1;
		byte res=123;
		byte res1=1;
		byte tmp=1;
		for (i=Byte.MIN_VALUE/100;i<Byte.MAX_VALUE/100;i++){
			res1+=res%tmp;
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
			res1+=res%tmp;
		}
		return res1;
	}
	public short testShort1(){
		short i=1;
		short res=1236;
		short res1=0;
		for (;i<5;i++){
			res1+=res%i;
		}
		return res1;
	}
	public short testShort2(){
		short i=1;
		short res=1236;
		short res1=0;
		short tmp=10;
		for (;i<50;i++){
			res1+=res%i;
			tmp++;
			res+=tmp;
		}
		return res1;
	}
	public short testShort3(){
		short i=1;
		short res=1235;
		short res1=0;
		short tmp=10;
		for (i=0/100;i<maxIter/100;i++){
			res1+=res%tmp;
			tmp++;
		}
		return res1;
	}
	public short testShort4(){
		short i=1;
		short res=12356;
		short res1=10000;
		short tmp=-10;
		for (i=tmp;i<res;i++){
			res1+=res%tmp;
		}
		return res1;
	}
	public float testFloat1(){
		int i=1;
		float res=1234567.0f;
		float res1=0.0f;
		for (;i<5;i++){
			res1+=res%i;
		}
		return res1;
	}
	public float testFloat2(){
		int i=1;
		float res=1234456.0f;
		float res1=0.0f;
		float tmp=10.0f;
		for (;i<50;i++){
			res1+=res%i;
			tmp++;
			res+=tmp;
		}
		return res1;
	}
	public float testFloat3(){
		int i=1;
		float res=1234456.0f;
		float res1=0.0f;
		float tmp=10.0f;
		for (i=00;i<maxIter;i++){
			res1+=res%tmp;
			tmp++;
		}
		return res1;
	}
	public float testFloat4(){
		int i=1;
		float res=12345567.0f;
		float res1=10000.0f;
		float tmp=-10.0f;
		for (i=(int)tmp;i<res;i++){
			res1+=res%tmp;
		}
		return res1;
	}
	public double testDouble1(){
		int i=1;
		double res=123434576.0;
		double res1=0.0;
		for (;i<5;i++){
			res1+=res%i;
		}
		return res1;
	}
	public double testDouble2(){
		int i=1;
		double res=1234356.0;
		double res1=0.0;
		double tmp=-10.0;
		for (;i<50;i++){
			res1+=res%i;
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
			res1+=res%tmp;
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
			res1+=res%tmp;
		}
		return res1;
	}
	public int testInt5(){
		int i=1;
		int res=123456;
		int tmp=1132322;
		for (;i<5;i++){
			res%=tmp;
			tmp++;
		}
		return res;
	}
	public int testInt6(){
		int i=1;
		int res=12;
		int tmp=1122220;
		for (;i<50;i++){
			res%=tmp;
			tmp--;
		}
		return res;
	}
	public int testInt7(){
		int i=1;
		int res=123456778;
		int tmp=1123121231;
		for (i=0;i<maxIter;i++){
			res%=tmp;
			tmp++;
		}
		return res;
	}
	public int testInt8(){
		int i=1;
		int res=1234567;
		int tmp=10242342;
		for (i=tmp;i<res;i++){
			res%=tmp++;
		}
		return res;
	}
	public long testLong5(){
		long i=1;
		long res=123456;
		long res1=1111110;
		for (;i<5;i++){
			res%=res1++;
		}
		return res;
	}
	public long testLong6(){
		long i=1;
		long res=123456;
		long tmp=1012333;
		for (;i<50;i++){
			res%=tmp;
			tmp++;
		}
		return res;
	}
	public long testLong7(){
		long i=1;
		long res=123455;
		long tmp=1034232;
		for (i=0;i<maxIter;i++){
			res%=tmp;
			tmp++;
		}
		return res;
	}
	public long testLong8(){
		long i=1;
		long res=1234456;
		long tmp=1123423420;
		for (i=tmp;i<res;i++){
			res%=tmp++;
		}
		return res;
	}
	public byte testByte5(){
		byte i=1;
		byte res=100;
		byte res1=127;
		for (;i<5;i++){
			res%=res1--;
		}
		return res;
	}
	public byte testByte6(){
		byte i=1;
		byte res=60;
		byte res1=127;
		for (;i<50;i++){
			res%=res1--;
		}
		return res;
	}
	public byte testByte7(){
		byte i=1;
		byte res=1;
		byte res1=127;
		for (i=Byte.MIN_VALUE;i<Byte.MAX_VALUE;i++){
			res%=res1+=128;
		}
		return res;
	}
	public byte testByte8(){
		byte i=1;
		byte res=1;
		byte res1=127;
		for (i=res;i<res;i++){
			res1%=res1;
		}
		return res;
	}
	public short testShort5(){
		short i=1;
		short res=1236;
		short res1=12312;
		for (;i<5;i++){
			res%=res1--;
		}
		return res;
	}
	public short testShort6(){
		short i=1;
		short res=1236;
		short res1=12123;
		for (;i<50;i++){
			res%=res1--;
		}
		return res;
	}
	public short testShort7(){
		short i=1;
		short res=123;
		short res1=12311;
		for (i=0/100;i<maxIter/100;i++){
			res%=res1--;
		}
		return res;
	}
	public short testShort8(){
		short i=1;
		short res=1236;
		short res1=10000;
		for (;i<res;i++){
			res%=res1--;
		}
		return res;
	}
	public float testFloat5(){
		int i=1;
		float res=12347.0f;
		float res1=1231110.0f;
		for (;i<5;i++){
			res%=res1--;
		}
		return res;
	}
	public float testFloat6(){
		int i=1;
		float res=1234456.0f;
		float res1=123121231212312.0f;
		for (;i<50;i++){
			res%=res1--;
		}
		return res;
	}
	public float testFloat7(){
		int i=1;
		float res=1234456.0f;
		float res1=0.0f;
		for (i=00;i<maxIter;i++){
			res%=res1--;
		}
		return res;
	}
	public float testFloat8(){
		int i=1;
		float res=12345567.0f;
		float res1=1000123120.0f;
		float tmp=-10;
		for (i=(int)tmp;i<res;i++){
			res%=res1--;
		}
		return res;
	}
	public double testDouble5(){
		int i=1;
		double res=123434576.0;
		double res1=12311111230.0;
		for (;i<5;i++){
			res%=res1--;
		}
		return res;
	}
	public double testDouble6(){
		int i=1;
		double res=1234356.0;
		double res1=356678545.0;
		for (;i<50;i++){
			res%=res1--;
		}
		return res;
	}
	public double testDouble7(){
		int i=1;
		double res=1234456.0;
		double res1=12312110.0;
		for (i=00;i<maxIter;i++){
			res%=res1--;
		}
		return res;
	}
	public double testDouble8(){
		int i=1;
		double res=11233456.0;
		double res1=10000.0;
		for (;i<res;i++){
			res%=res1--;
		}
		return res;
	}
}
