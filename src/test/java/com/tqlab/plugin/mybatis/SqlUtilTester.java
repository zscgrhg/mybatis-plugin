/**
 * 
 */
package com.tqlab.plugin.mybatis;

import org.junit.Test;

import com.tqlab.plugin.mybatis.util.SqlUtil;

/**
 * @author John Lee
 * 
 */
public class SqlUtilTester {

	@Test
	public void testPdata() {
		System.out.println(SqlUtil.pdataFilter("select * from t where i<0",
				true));
		System.out
				.println(SqlUtil
						.pdataFilter(
								"update t <set><if test=\"username != null\">username=#{username}</if></set> where i<10 and j<",
								true));
	}
}
