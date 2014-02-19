/**
 * 
 */
package com.tqlab.plugin.mybatis;

import org.junit.Test;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import com.tqlab.plugin.mybatis.util.SqlTemplateParserUtil;

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

		System.out.println(FullyQualifiedJavaType.getObjectInstance()
				.getFullyQualifiedName());
		System.out.println(SqlTemplateParserUtil.isBasicType("int"));
	}

	private void modify(String s) {
		s = "b";
	}
}
