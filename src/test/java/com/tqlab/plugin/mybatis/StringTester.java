/**
 * 
 */
package com.tqlab.plugin.mybatis;

import org.junit.Test;

/**
 * @author John Lee
 * 
 */
public class StringTester {

	@Test
	public void testStr() {
		String s = "a";
		System.out.println(s);
		modify(s);
		System.out.println(s);
	}

	private void modify(String s) {
		s = "b";
	}
}
