/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tqlab.plugin.mybatis.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author John Lee
 * 
 */
public abstract class AbstractDatabase implements Database {

	private String driverClass;
	private String database;
	private String url;
	private String user;
	private String password;

	public AbstractDatabase(String driverClass, String database, String url,
			String user, String password) {
		this.driverClass = driverClass;
		this.database = database;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	private static Connection conn = null;

	/**
	 * 获取数据库连接。
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected Connection getConnection() {
		try {
			if (null != conn && !conn.isClosed()) {
				return conn;
			}
			Class.forName(driverClass);
			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	/*
	 * (non-Javadoc)
	 */
	public ColumnResult getColumns(String tableName) {

		ColumnResult result = new ColumnResult();

		List<String> columns = new ArrayList<String>();
		List<String> primaryKeys = new ArrayList<String>();
		List<String> autoIncrementPrimaryKeys = new ArrayList<String>();

		Connection conn = this.getConnection();
		if (conn != null) {
			Statement stmt = null;
			ResultSet res = null;
			try {
				stmt = conn.createStatement();
				String sql = getColumnsSql(tableName);
				res = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = res.getMetaData();
				int colcount = rsmd.getColumnCount();// 取得全部列数

				List<String> autoIncrementColumn = new ArrayList<String>();
				for (int i = 1; i <= colcount; i++) {

					columns.add(getColumnName(rsmd.getColumnName(i)));
					// Indicates whether the designated column is automatically
					// numbered.
					if (rsmd.isAutoIncrement(i)) {
						autoIncrementColumn.add(getColumnName(rsmd
								.getColumnName(i)));
					}
				}

				res.close();

				DatabaseMetaData dbmd = conn.getMetaData();
				res = dbmd.getPrimaryKeys(null, null, tableName);
				while (res.next()) {
					String primaryKey = res.getString("COLUMN_NAME");
					if (autoIncrementColumn.contains(primaryKey)) {
						autoIncrementPrimaryKeys.add(primaryKey);
					}
					primaryKeys.add(primaryKey);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (res != null)
						res.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				try {
					if (stmt != null)
						stmt.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		result.setTableName(tableName);
		result.setColumns(columns);
		result.setAutoIncrementPrimaryKeys(autoIncrementPrimaryKeys);
		result.setPrimaryKeys(primaryKeys);
		return result;
	}

	protected abstract String getColumnName(String columnName);

	@Override
	public List<String> getTablesName() {

		List<String> list = new ArrayList<String>();

		Connection conn = this.getConnection();
		if (conn != null) {
			Statement stmt = null;
			ResultSet res = null;
			try {
				stmt = conn.createStatement();
				res = stmt.executeQuery(getQuerySql());
				while (res.next()) {
					//
					list.add(getTableName(res));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (res != null)
						res.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				try {
					if (stmt != null)
						stmt.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return list;
	}

	protected abstract String getColumnsSql(String tableName);

	protected abstract String getQuerySql();

	protected abstract String getTableName(ResultSet resultSet)
			throws SQLException;

	@Override
	public String getDatabase() {
		return this.database;
	}

	/*
	 * (non-Javadoc)
	 */
	public void close() {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
