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

import java.util.List;

/**
 * @author John Lee
 * 
 */
public class ColumnResult extends Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = -224299652009061636L;

	/**
	 * Table name
	 */
	private String tableName;

	/**
	 * Columns name list
	 */
	private List<String> columns;

	/**
	 * Table primary keys
	 */
	private List<String> primaryKeys;

	/**
	 * Table primary keys which is auto increment
	 */
	private List<String> autoIncrementPrimaryKeys;

	/**
	 * @return the tableName
	 */
	public final String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public final void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the columns
	 */
	public final List<String> getColumns() {
		return columns;
	}

	/**
	 * @return the primaryKeys
	 */
	public final List<String> getPrimaryKeys() {
		return primaryKeys;
	}

	/**
	 * @return the autoIncrementPrimaryKeys
	 */
	public final List<String> getAutoIncrementPrimaryKeys() {
		return autoIncrementPrimaryKeys;
	}

	/**
	 * @param autoIncrementPrimaryKeys
	 *            the autoIncrementPrimaryKeys to set
	 */
	public final void setAutoIncrementPrimaryKeys(
			List<String> autoIncrementPrimaryKeys) {
		this.autoIncrementPrimaryKeys = autoIncrementPrimaryKeys;
	}

	/**
	 * @param columns
	 *            the columns to set
	 */
	public final void setColumns(List<String> columns) {
		this.columns = columns;
	}

	/**
	 * @param primaryKeys
	 *            the primaryKeys to set
	 */
	public final void setPrimaryKeys(List<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
}
