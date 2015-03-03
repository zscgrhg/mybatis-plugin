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
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.tqlab.plugin.mybatis.MybatisPluginException;

/**
 * @author John Lee
 * 
 */
public abstract class AbstractDatabase implements Database {

	private static final Logger LOGGER = Logger
			.getLogger(AbstractDatabase.class);

	private String driverClass;
	private final transient String database;
	private final transient String url;
	private final Properties properties;
	/**
	 * Database connection
	 */
	private transient Connection conn;

	/**
	 * 
	 * @param driverClass
	 * @param database
	 * @param url
	 * @param properties
	 */
	public AbstractDatabase(final String driverClass, final String database,
			final String url, final Properties properties) {
		this.driverClass = driverClass;
		this.database = database;
		this.url = url;
		this.properties = properties;
	}

	/**
	 * @return the driverClass
	 */
	public final String getDriverClass() {
		return driverClass;
	}

	/**
	 * @param driverClass
	 *            the driverClass to set
	 */
	public final void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * 
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected Connection getConnection() throws SQLException {
		try {
			if (null == conn || conn.isClosed()) {
				Class.forName(driverClass);
				Properties p = new Properties();
				for (Entry<Object, Object> e : properties.entrySet()) {
					if (e.getValue() instanceof String) {
						p.put(e.getKey(), e.getValue());
					}
				}
				conn = DriverManager.getConnection(url, p);
			}

		} catch (ClassNotFoundException e) {
			throw new MybatisPluginException(e);
		}
		return conn;
	}

	/*
	 * (non-Javadoc)
	 */
	public ColumnResult getColumns(final String tableName) {

		final ColumnResult result = new ColumnResult();

		final List<String> columns = new ArrayList<String>();
		final List<String> primaryKeys = new ArrayList<String>();
		final List<String> autoIncrementPK = new ArrayList<String>();

		result.setTableName(tableName);
		result.setColumns(columns);
		result.setAutoIncrementPrimaryKeys(autoIncrementPK);
		result.setPrimaryKeys(primaryKeys);

		Statement stmt = null;
		ResultSet res = null;
		try {
			final Connection conn = this.getConnection();
			stmt = conn.createStatement();
			final String sql = getColumnsQuerySql(tableName);
			res = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = res.getMetaData();
			int colcount = rsmd.getColumnCount();// 取得全部列数

			List<String> autoIncrementColumn = new ArrayList<String>();
			for (int i = 1; i <= colcount; i++) {
				final String column = getColumnName(rsmd.getColumnName(i));
				columns.add(column);
				// Indicates whether the designated column is automatically
				// numbered.
				if (rsmd.isAutoIncrement(i)) {
					autoIncrementColumn.add(column);
				}
			}

			res.close();

			DatabaseMetaData dbmd = conn.getMetaData();
			res = dbmd.getPrimaryKeys(null, null, tableName);
			while (res.next()) {
				String primaryKey = res.getString("COLUMN_NAME");
				if (autoIncrementColumn.contains(primaryKey)) {
					autoIncrementPK.add(primaryKey);
				}
				primaryKeys.add(primaryKey);
			}
		} catch (Exception e) {
			LOGGER.error("Error", e);
		} finally {
			releaseDbQuery(stmt, res);
		}

		return result;
	}

	@Override
	public Set<String> getTablesName() {

		final Set<String> set = new HashSet<String>();

		Statement stmt = null;
		ResultSet res = null;
		try {
			final Connection conn = this.getConnection();
			stmt = conn.createStatement();
			res = stmt.executeQuery(getTablesQuerySql());
			while (res.next()) {
				//
				set.add(getTableName(res));
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		} finally {
			releaseDbQuery(stmt, res);
		}
		return set;
	}

	private void releaseDbQuery(Statement stmt, ResultSet res) {
		try {
			if (res != null)
				res.close();
		} catch (Exception ex) {
			LOGGER.error(ex);
		}

		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
	}

	/**
	 * Filter the database keyword
	 * 
	 * @param columnName
	 * @return
	 */
	protected abstract String getColumnName(String columnName);

	/**
	 * Get columns query sql
	 * 
	 * @param tableName
	 * @return
	 */
	protected abstract String getColumnsQuerySql(String tableName);

	/**
	 * Get tables query sql
	 * 
	 * @return
	 */
	protected abstract String getTablesQuerySql();

	/**
	 * Get table name from resultSet and filter the database keyword
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
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
