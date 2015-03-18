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
public class DbTableOperation {

	private String id;
	private boolean many = true;
	private String comment;
	private String sql;
	private String resultType;
	private String parameterType;
	private List<DbOption> options = new ArrayList<DbOption>();
	private List<DbParam> params = new ArrayList<DbParam>();
	private DbSelectResult result;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the many
	 */
	public boolean isMany() {
		return many;
	}

	/**
	 * @param many
	 *            the many to set
	 */
	public void setMany(boolean many) {
		this.many = many;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @return the resultType
	 */
	public String getResultType() {
		return resultType;
	}

	/**
	 * @param resultType
	 *            the resultType to set
	 */
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	/**
	 * @return the parameterType
	 */
	public final String getParameterType() {
		return parameterType;
	}

	/**
	 * @param parameterType
	 *            the parameterType to set
	 */
	public final void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	/**
	 * @return the options
	 */
	public final List<DbOption> getOptions() {
		return options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public final void addOption(DbOption option) {
		if (null == option) {
			return;
		}
		for (DbOption o : options) {
			if (o.getName().equals(option.getName())) {
				return;
			}
		}
		this.options.add(option);
	}

	/**
	 * @return the params
	 */
	public final List<DbParam> getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the param to add
	 */
	public final void addParams(DbParam param) {
		if (null == param) {
			return;
		}
		for (DbParam p : params) {
			if (p.getObjectName().equals(param.getObjectName())) {
				return;
			}
		}
		this.params.add(param);
	}

	/**
	 * @return the result
	 */
	public DbSelectResult getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(DbSelectResult result) {
		this.result = result;
	}

}
