/**
 * 
 */
package com.tqlab.plugin.mybatis;

import java.io.IOException;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.tqlab.plugin.mybatis.util.SqlUtil;

/**
 * @author lijun
 * 
 */
public class SqlTester {

	@Test
	public void testSimpleSelectSql() throws JSQLParserException {
		String sql = " SELECT *,a,b,c, sum(a) FROM (SELECT * FROM bns_thousands_course_group WHERE number_of_group < limit_of_group ORDER BY rand()) g GROUP BY g.subject_id";
		parse(sql);
	}

	@Test
	public void testJoinSelectSql() throws JSQLParserException {
		String sql = "SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate FROM Orders INNER JOIN Customers ON Orders.CustomerID=Customers.CustomerID;";
		parse(sql);
	}

	@Test
	public void testMybatis1() throws JSQLParserException {
		String sql = "select * from content_category where fid=#{fid, jdbcType=INTEGER} ";
		parse(sql);
	}

	@Test
	public void testMybatis2() throws JSQLParserException {
		String sql = "select * from ip where `begin`<=#{ip,jdbcType=BIGINT} and `end` >=#{ip,jdbcType=BIGINT} order by `begin` asc limit 1";
		parse(sql);
	}

	@Test
	public void testMybatis3() throws JSQLParserException {
		String sql = "select * from content where content_status=#{status,jdbcType=VARCHAR} order by id desc LIMIT 0,#{size,jdbcType=INTEGER};";
		parse(sql);
	}

	@Test
	public void testMybatis4() throws JSQLParserException {
		String sql = "SELECT TOP #{size,jdbcType=INTEGER} * FROM Customers;";
		parse(sql);
	}

	@Test
	public void testMybatis5() throws JSQLParserException {
		String sql = "SELECT AVG(Price) AS PriceAverage FROM Products;";
		parse(sql);
	}

	@Test
	public void testMybatis6() throws JSQLParserException {
		String sql = "SELECT MAX(Price) AS HighestPrice FROM Products;";
		parse(sql);
	}

	@Test
	public void testMybatis7() throws JSQLParserException {
		String sql = "SELECT MID(City,d1d,yy) AS ShortCity FROM Customers;";
		parse(sql);
	}

	@Test
	public void testMybatis8() throws JSQLParserException {
		String sql = "SELECT ProductName, ROUND(Price,a) AS RoundedPrice FROM Products;";
		parse(sql);
	}

	@Test
	public void testMyBatis9() throws JSQLParserException {
		String sql = "<script>"
				+ "<bind name=\"pattern\" value=\"'%' + keyword + '%'\" />"
				+ "select a.id, a.group_id, a.module_id, a.readable, a.writeable,"
				+ "b.name, b.url, b.icon, b.fid, b.path "
				+ "from admin_group_privilege a, admin_module b "
				+ "where a.module_id=b.id "
				+ "and a.group_id = (select group_id from admin_info where id=#{adminId,jdbcType=BIGINT}) "
				+ "and b.id not in (SELECT DISTINCT fid FROM admin_module)"
				+ "and b.name like #{pattern}" + "order by b.id asc;"
				+ "</script>";
		parse(sql);
	}

	@Test
	public void testMyBatis10() throws JSQLParserException {
		String sql = "UPDATE bns_thousands_course_group SET number_of_group = #{number_of_group,jdbcType=INT}  WHERE group_id = #{group_id,jdbcType=BIGINT}";
		parse(sql);
	}

	@Test
	public void testMybatis11() throws JSQLParserException, IOException {
		String sql = IOUtils.toString(getClass().getResourceAsStream(
				"/sql/test1.sql"));
		parse(sql);
	}

	private void parse(final String sql) throws JSQLParserException {
		String str = SqlUtil.filterSql(sql);
		System.out.println("Sql: " + str);
		Statement statement = CCJSqlParserUtil.parse(str);
		if (statement instanceof Select) {
			Select selectStatement = (Select) statement;

			PlainSelect body = (PlainSelect) selectStatement.getSelectBody();
			List<SelectItem> list = body.getSelectItems();
			for (SelectItem item : list) {
				System.out.println("select item:	" + item + "		"
						+ item.getClass().getName());
			}
			System.out.println("select from:	" + body.getFromItem());
			TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
			List<String> tableList = tablesNamesFinder
					.getTableList(selectStatement);
			for (String s : tableList) {
				System.out.println("select table:	" + s);
			}
		} else if (statement instanceof Update) {
			Update updateStatement = (Update) statement;
			List<Column> list = updateStatement.getColumns();
			for (Column c : list) {
				System.out.println("update column:	" + c);
			}
		}
		System.out.println();
	}
}
