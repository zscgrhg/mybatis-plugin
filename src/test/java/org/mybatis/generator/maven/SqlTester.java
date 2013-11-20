/**
 * 
 */
package org.mybatis.generator.maven;

import org.junit.Test;

/**
 * @author lijun
 * 
 */
public class SqlTester {

	@Test
	public void testSql() {
		String sql = " (SELECT * FROM bns_thousands_course_group WHERE number_of_group &lt; limit_of_group ORDER BY rand()) g GROUP BY ";
		String s = getTableNameFromComplexSql(sql);
		System.out.println(s);
	}

	public String getTableNameFromComplexSql(final String sql) {
		String s = sql.trim();
		if (s.startsWith("(")) {
			int indexEnd = s.indexOf(")");
			s = s.substring(0, indexEnd + 1);
			String arrays[] = s.split("\\(");
			int size = arrays.length;
			for (int i = 0; i < sql.length(); i++) {
				if (sql.charAt(i) == ')') {
					size--;
				}
				if (size == 1) {
					return sql.substring(0, i + 1);
				}
			}
		}
		return sql.substring(0, sql.indexOf(" "));
	}

}
