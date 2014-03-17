package com.easy4j.test;

import java.util.Arrays;

import org.junit.Test;

public class BreakTest {

	@Test
	public void test() {
		loop1 :{
			for(int i = 0 ;i<10;i++){
				System.out.println("i = "+i);
				loop2 : {
					for (int j = 0; j < 10; j++) {
						System.out.println("------j = "+j);
						if(j == 3) break loop1;
						
					}
				}
			}
		}
	}
	
	@Test
	public void enumTest(){
		System.out.println(Arrays.toString(Tn.values()));
	}
	enum Tn{
		SUCC(200),FAIL(500);
		private int code;
		Tn(int code){
			this.code = code;
		}
		@Override
		public String toString() {
			return String.valueOf(this.code);
		}
		
	}

	@Test
	public void grammerTest(){
		int i = 2;
		int a = ~i + 1;
		System.out.println( "a = "+ a);
		System.out.println( false ||  true && false);
		System.out.println("i = "+ i);
	}
}
