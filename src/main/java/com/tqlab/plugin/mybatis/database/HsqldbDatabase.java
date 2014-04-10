/**
 * 
 */
package com.tqlab.plugin.mybatis.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author John Lee
 * 
 */
public class HsqldbDatabase extends AbstractDatabase {

	private static final String DRIVER = "org.hsqldb.jdbcDriver";

	/**
	 * 
	 * @param database
	 * @param url
	 * @param user
	 * @param password
	 */
	public HsqldbDatabase(final String database, final String url,
			final Properties properties) {
		super(DRIVER, database, url, properties);
	}

	@Override
	protected String getTablesQuerySql() {
		return "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE='TABLE';";
	}

	protected String getTableName(ResultSet resultSet) throws SQLException {
		String name = (String) resultSet.getObject("TABLE_NAME");
		if (null != name) {
			name = name.toLowerCase();
		}
		return name;
	}

	@Override
	public DatabaseEnum getDatabaseEnum() {
		return DatabaseEnum.HSQLDB;
	}

	@Override
	protected String getColumnsQuerySql(String tableName) {
		if (null == tableName) {
			return null;
		}
		return "SELECT * FROM " + tableName + " LIMIT 1";
	}

	@Override
	protected String getColumnName(final String column) {

		if (null == column) {
			return null;
		}
		String columnName = column;
		if (!columnName.startsWith("[")) {
			columnName = "[" + columnName;
		}
		if (!columnName.endsWith("]")) {
			columnName = columnName + "]";
		}

		return columnName;
	}

}