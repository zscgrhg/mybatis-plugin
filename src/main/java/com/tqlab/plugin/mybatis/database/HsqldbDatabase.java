/**
 * 
 */
package com.tqlab.plugin.mybatis.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author John Lee
 * 
 */
public class HsqldbDatabase extends AbstractDatabase {

	private static final String DRIVER = "org.hsqldb.jdbcDriver";

	public HsqldbDatabase(String database, String url, String user,
			String password) {
		super(DRIVER, database, url, user, password);
	}

	@Override
	protected String getQuerySql() {
		return "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE='TABLE';";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tqlab.plugin.mybatis.database.Database#getDirverClass()
	 */
	@Override
	public String getDirverClass() {
		return DRIVER;
	}

	protected String getTableName(ResultSet resultSet) throws SQLException {
		String name = (String) resultSet.getObject("TABLE_NAME");
		if (null != name) {
			name = name.toLowerCase();
		}
		return "[" + name + "]";
	}

	@Override
	public DatabaseEnum getDatabaseEnum() {
		return DatabaseEnum.HSQLDB;
	}

	@Override
	protected String getColumnsSql(String tableName) {
		if (null == tableName) {
			return null;
		}
		return "SELECT * FROM " + tableName + " LIMIT 1";
	}

	@Override
	protected String getColumnName(String columnName) {

		if (null == columnName) {
			return null;
		}
		if (!columnName.startsWith("[")) {
			columnName = "[" + columnName;
		}
		if (!columnName.endsWith("]")) {
			columnName = columnName + "]";
		}

		return columnName;
	}

}
