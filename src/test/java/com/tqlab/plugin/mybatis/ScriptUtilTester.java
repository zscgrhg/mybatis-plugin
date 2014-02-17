/**
 * 
 */
package com.tqlab.plugin.mybatis;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.tqlab.plugin.mybatis.util.ScriptUtil;

/**
 * @author John Lee
 * 
 */
public class ScriptUtilTester {

	@Test
	public void testGetBindNames() {
		String s = "dsds<bind name=\"pattern\" value=\"'%' + firstname      + '%'\" \r \n   />ssss<bind name=\"key\" value=\"'%' + firstname      + '%'\"    />";
		Set<String> result = ScriptUtil.getBindNames(s);
		Assert.assertTrue(result.size() == 2);
	}
}
