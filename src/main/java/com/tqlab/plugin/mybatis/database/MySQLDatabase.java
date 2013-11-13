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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author John Lee
 * 
 */
public class MySQLDatabase extends AbstractDatabase {

	private static final String DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 */
	public MySQLDatabase(String database, String url, String user,
			String password) {
		super(DRIVER, database, url, user, password);
	}

	@Override
	public String getDirverClass() {
		return DRIVER;
	}

	@Override
	protected String getQuerySql() {
		return "show tables;";
	}

	protected String getTableName(ResultSet resultSet) throws SQLException {
		String name = (String) resultSet.getObject(1);
		if (null != name) {
			name = name.toLowerCase();
		}
		return "`" + name + "`";
	}

	@Override
	public DatabaseEnum getDatabaseEnum() {
		return DatabaseEnum.MYSQL;
	}

	@Override
	protected String getColumnsSql(String tableName) {
		if (null == tableName) {
			return null;
		}
		if (!tableName.startsWith("`")) {
			tableName = "`" + tableName;
		}
		if (!tableName.endsWith("`")) {
			tableName = tableName + "`";
		}
		return "SELECT * FROM " + tableName + " LIMIT 1";
	}

	@Override
	protected String getColumnName(String columnName) {
		if (null == columnName) {
			return null;
		}
		if (!columnName.startsWith("`")) {
			columnName = "`" + columnName;
		}
		if (!columnName.endsWith("`")) {
			columnName = columnName + "`";
		}
		return columnName;
	}
}
