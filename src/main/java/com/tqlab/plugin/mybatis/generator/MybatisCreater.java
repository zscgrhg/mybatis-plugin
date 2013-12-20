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

import java.util.List;

import com.tqlab.plugin.mybatis.database.Database;

/**
 * @author John Lee
 * 
 */
public interface MybatisCreater {

	/**
	 * 
	 * @param database
	 * @param url
	 * @param databaseName
	 * @param userName
	 * @param password
	 * @param dalPackage
	 * @param dir
	 * @param overwrite
	 * @param tables
	 *            Specific the tables to create MYBATIS configuration. If tables
	 *            is null, all tables in the database will be used.
	 * @return
	 */
	List<MybatisBean> create(Database database, String url,
			String databaseName, String userName, String password,
			String dalPackage, String dir, boolean overwrite, String... tables);
}
