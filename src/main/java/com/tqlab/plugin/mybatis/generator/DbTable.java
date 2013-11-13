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
package com.tqlab.plugin.mybatis.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author John Lee
 * 
 */
public class DbTable {

	private String name;
	private List<DbColumn> columns = new ArrayList<DbColumn>();
	private List<DbSelectResult> selectResults = new ArrayList<DbSelectResult>();
	private List<DbTableOperation> operations = new ArrayList<DbTableOperation>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the columns
	 */
	public final List<DbColumn> getColumns() {
		return columns;
	}

	/**
	 * @param columns
	 *            the columns to set
	 */
	public final void setColumns(List<DbColumn> columns) {
		this.columns = columns;
	}

	/**
	 * @return the selectResults
	 */
	public final List<DbSelectResult> getSelectResults() {
		return selectResults;
	}

	/**
	 * @param selectResults
	 *            the selectResults to set
	 */
	public final void setSelectResults(List<DbSelectResult> selectResults) {
		this.selectResults = selectResults;
	}

	/**
	 * @return the operations
	 */
	public final List<DbTableOperation> getOperations() {
		return operations;
	}

	/**
	 * @param operations
	 *            the operations to set
	 */
	public final void setOperations(List<DbTableOperation> operations) {
		this.operations = operations;
	}

}
