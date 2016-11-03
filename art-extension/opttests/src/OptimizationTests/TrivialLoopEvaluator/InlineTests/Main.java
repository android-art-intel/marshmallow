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

package OptimizationTests.TrivialLoopEvaluator.InlineTests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

	public static final int maxIter=999;
	/**
	 * @param args
	 */
	public static int inlineIntAndShlXor(int a, int b){
		int ret=0;
		ret&=a<<b;
		ret^=2;
		return ret;
	}
	public static byte inlineByteAndShlXor(byte a, byte b){
		byte ret=0;
		ret&=a<<b;
		ret^=2;
		return ret;
	}
	public static long inlineLongAndShlXor(long a, long b){
		long ret=0;
		ret&=a<<b;
		ret^=2;
		return ret;
	}
	public static short inlineShortAndShlXor(short a, short b){
		short ret=0;
		ret&=a<<b;
		ret^=2;
		return ret;
	}
	public static int inlineIntOrShrUshr(int a, int b){
		int ret=0;
		ret|=a>>b;
		ret>>>=2;
		return ret;
	}
	public static byte inlineByteOrShrUshr(byte a, byte b){
		byte ret=0;
		ret|=a>>b;
		ret>>>=2;
		return ret;
	}
	public static long inlineLongOrShrUshr(long a, long b){
		long ret=0;
		ret|=a>>b;
		ret>>>=2;
		return ret;
	}
	public static short inlineShortOrShrUshr(short a, short b){
		short ret=0;
		ret|=a>>b;
		ret>>>=2;
		return ret;
	}
	public static int inlineIntAddMul(int a, int b){
		int ret=0;
		ret+=a+b;
		ret*=2;
		return ret;
	}
	public static byte inlineByteAddMul(byte a, byte b){
		byte ret=0;
		ret+=a+b;
		ret*=2;
		return ret;
	}
	public static long inlineLongAddMul(long a, long b){
		long ret=0;
		ret+=a+b;
		ret*=2;
		return ret;
	}
	public static short inlineShortAddMul(short a, short b){
		short ret=0;
		ret+=a+b;
		ret*=2;
		return ret;
	}
	public static float inlineFloatAddMul(float a, float b){
		float ret=0.0f;
		ret+=a+b;
		ret*=2.0f;
		return ret;
	}
	public static double inlineDoubleAddMul(double a, double b){
		double ret=0.0;
		ret+=a+b;
		ret*=2.0;
		return ret;
	}
	public static int inlineIntSubDivRem(int a, int b){
		int ret=0;
		ret-=a-b;
		ret/=2;
		ret-=ret%3;
		return ret;
	}
	public static byte inlineByteSubDivRem(byte a, byte b){
		byte ret=0;
		ret-=a-b;
		ret/=2;
		ret-=ret%3;
		return ret;
	}
	public static long inlineLongSubDivRem(long a, long b){
		long ret=0;
		ret-=a-b;
		ret/=2;
		ret-=ret%3;
		return ret;
	}
	public static short inlineShortSubDivRem(short a, short b){
		short ret=0;
		ret-=a-b;
		ret/=2;
		ret-=ret%3;
		return ret;
	}
	public static float inlineFloatSubDivRem(float a, float b){
		float ret=0.0f;
		ret-=a-b;
		ret/=2.0f;
		ret-=ret%3.0f;
		return ret;
	}
	public static double inlineDoubleSubDivRem(double a, double b){
		double ret=0.0;
		ret-=a-b;
		ret/=2.0;
		ret-=ret%3.0;
		return ret;
	}
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
			if (met.getName().equalsIgnoreCase("main") || met.getName().startsWith("inline"))
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
	public byte testByte1(){
		byte i=0;
		byte res=0;
		for (;i<5;i++){
			res+=i;
			res+=inlineByteAddMul(res, i);
		}
		return res;
	}
	public byte testByte2(){
		byte i=0;
		byte res=0;
		for (;i<50;i++){
			res+=i;
			res+=inlineByteAndShlXor(res, i);
		}
		return res;
	}
	public byte testByte3(){
		byte i=0;
		byte res=0;
		byte tmp=-10;
		for (i=Byte.MIN_VALUE;i<Byte.MAX_VALUE;i++){
			res+=tmp;
			tmp++;
			res+=inlineByteOrShrUshr(res, i);
		}
		return res;
	}
	public byte testByte4(){
		byte i=0;
		byte res=100;
		byte tmp=-10;
		for (i=tmp;i<res;i++){
			res+=tmp;
			res+=inlineByteSubDivRem(res, i);
		}
		return res;
	}
	public short testShort1(){
		short i=0;
		short res=0;
		for (;i<5;i++){
			res+=i;
			res+=inlineShortAddMul(res, i);
		}
		return res;
	}
	public short testShort2(){
		short i=0;
		short res=0;
		for (;i<50;i++){
			res+=i;
			res+=inlineShortAndShlXor(res, i);
		}
		return res;
	}
	public short testShort3(){
		short i=0;
		short res=0;
		short tmp=-10;
		for (i=0;i<maxIter;i++){
			res+=tmp;
			tmp++;
			res+=inlineShortOrShrUshr(res, i);
		}
		return res;
	}
	public short testShort4(){
		short i=0;
		short res=10000;
		short tmp=-10;
		for (i=tmp;i<res;i++){
			res+=tmp;
			res+=inlineShortSubDivRem(res, i);
		}
		return res;
	}	
	public int testInt1(){
		int i=0;
		int res=0;
		for (;i<5;i++){
			res+=i;
			res+=inlineIntAddMul(res, i);
		}
		return res;
	}
	public int testInt2(){
		int i=0;
		int res=0;
		for (;i<50;i++){
			res+=i;
			res+=inlineIntAndShlXor(res, i);
		}
		return res;
	}
	public int testInt3(){
		int i=0;
		int res=0;
		int tmp=-10;
		for (i=0;i<maxIter;i++){
			res+=tmp;
			tmp++;
			res+=inlineIntOrShrUshr(res, i);
		}
		return res;
	}
	public int testInt4(){
		int i=0;
		int res=10000;
		int tmp=-10;
		for (i=tmp;i<res;i++){
			res+=tmp;
			res+=inlineIntSubDivRem(res, i);
		}
		return res;
	}
	public long testLong1(){
		long i=0;
		long res=0;
		for (;i<5;i++){
			res+=i;
			res+=inlineLongAddMul(res, i);
		}
		return res;
	}
	public long testLong2(){
		long i=0;
		long res=0;
		for (;i<50;i++){
			res+=i;
			res+=inlineLongAndShlXor(res, i);
		}
		return res;
	}
	public long testLong3(){
		long i=0;
		long res=0;
		long tmp=-10;
		for (i=0;i<maxIter;i++){
			res+=tmp;
			tmp++;
			res+=inlineLongOrShrUshr(res, i);
		}
		return res;
	}
	public long testLong4(){
		long i=0;
		long res=10000;
		long tmp=-10;
		for (i=tmp;i<res;i++){
			res+=tmp;
			res+=inlineLongSubDivRem(res, i);
		}
		return res;
	}
	public float testFloat1(){
		int i=0;
		float res=0.0f;
		for (;i<5;i++){
			res+=i;
			res+=inlineFloatAddMul(res, 3.4f*i);
		}
		return res;
	}
	public float testFloat2(){
		int i=0;
		float res=0.0f;
		for (;i<50;i++){
			res+=i;
			res+=inlineFloatSubDivRem(res, 3.8f*i);
		}
		return res;
	}
	public double testDouble1(){
		int i=0;
		double res=0.0;
		for (;i<5;i++){
			res+=i;
			res+=inlineDoubleAddMul(res, 4.3*i);
		}
		return res;
	}
	public double testDouble2(){
		int i=0;
		double res=0.0;
		for (;i<50;i++){
			res+=i;
			res+=res+=inlineDoubleAddMul(res, 4.7*i);;
		}
		return res;
	}
}
