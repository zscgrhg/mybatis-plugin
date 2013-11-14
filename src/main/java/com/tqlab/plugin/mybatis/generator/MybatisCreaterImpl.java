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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.tqlab.plugin.mybatis.database.ColumnResult;
import com.tqlab.plugin.mybatis.database.Database;
import com.tqlab.plugin.mybatis.database.DatabaseEnum;

/**
 * @author John Lee
 * 
 */
public class MybatisCreaterImpl implements MybatisCreater {

	private static final String NEW_LINE = "\r\n";

	private Log logger;
	private Properties properties;

	public MybatisCreaterImpl(Log logger, Properties properties) {
		this.logger = logger;
		this.properties = properties;
	}

	public List<MybatisBean> create(Database database, String url,
			String databaseName, String userName, String password,
			String dalPackage, String dir, boolean overwrite) {
		return create(database, url, databaseName, userName, password,
				dalPackage, dir, overwrite, new String[] {});
	}

	public List<MybatisBean> create(Database database, String url,
			String databaseName, String userName, String password,
			String dalPackage, String dir, boolean overwrite, String... tables) {

		dir = dir.replace(File.separator, "/");
		if (url.contains("&") && !url.contains("&amp;")) {
			url = url.replace("&", "&amp;");
		}

		String java = dir + "/src/main/java/";
		String res = dir + "/src/main/resources/";

		File f = new File(java);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(res);
		if (!f.exists()) {
			f.mkdirs();
		}

		StringBuffer buf = new StringBuffer();

		List<String> list = Arrays.asList(tables);
		for (Iterator<String> i = list.iterator(); i.hasNext();) {
			buf.append(getTableString(database.getDatabaseEnum(), database,
					i.next()));
		}
		database.close();

		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(NEW_LINE);
		sb.append("<!DOCTYPE generatorConfiguration");
		sb.append(NEW_LINE);
		sb.append(" PUBLIC \"-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN\"");
		sb.append(NEW_LINE);
		sb.append(" \"http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd\">");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("<generatorConfiguration>");
		sb.append(NEW_LINE);
		sb.append("  <context id=\""
				+ databaseName
				+ "\" targetRuntime=\"MyBatis3\" defaultModelType=\"hierarchical\">");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <plugin type=\"org.mybatis.generator.plugins.SerializablePlugin\" />");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <plugin type=\"com.tqlab.plugin.mybatis.generator.SqlTempleatePluginAdapter\" >");
		sb.append(NEW_LINE);
		if (null != properties) {
			Set<Entry<Object, Object>> set = properties.entrySet();
			for (Iterator<Entry<Object, Object>> i = set.iterator(); i
					.hasNext();) {
				Entry<Object, Object> e = i.next();
				sb.append("      <property name=\"" + e.getKey()
						+ "\" value=\"" + e.getValue() + "\" />");
				sb.append(NEW_LINE);
			}
		}
		sb.append("    </plugin>");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <jdbcConnection driverClass=\""
				+ database.getDirverClass() + "\"");
		sb.append(NEW_LINE);
		sb.append("      connectionURL=\"" + url + "\"");
		sb.append(NEW_LINE);
		sb.append("      userId=\"" + userName + "\"");
		sb.append(NEW_LINE);
		sb.append("      password=\"" + password + "\">");
		sb.append(NEW_LINE);
		sb.append("    </jdbcConnection>");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <javaTypeResolver >");
		sb.append(NEW_LINE);
		sb.append("      <property name=\"forceBigDecimals\" value=\"false\" />");
		sb.append(NEW_LINE);
		sb.append("    </javaTypeResolver>");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <javaModelGenerator targetPackage=\"" + dalPackage
				+ ".dataobject" + "\" targetProject=\"" + java + "\">");
		sb.append(NEW_LINE);
		sb.append("      <property name=\"enableSubPackages\" value=\"true\" />");
		sb.append(NEW_LINE);
		sb.append("      <property name=\"trimStrings\" value=\"true\" />");
		sb.append(NEW_LINE);
		sb.append("    </javaModelGenerator>");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <sqlMapGenerator targetPackage=\"sqlmaps\"  targetProject=\""
				+ res + "\">");
		sb.append(NEW_LINE);
		sb.append("      <property name=\"enableSubPackages\" value=\"true\" />");
		sb.append(NEW_LINE);
		sb.append("    </sqlMapGenerator>");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <javaClientGenerator  type=\"ANNOTATEDMAPPER\" targetPackage=\""
				+ dalPackage + ".dao\"" + " targetProject=\"" + java + "\">");
		sb.append(NEW_LINE);
		sb.append("      <property name=\"enableSubPackages\" value=\"true\" />");
		sb.append(NEW_LINE);
		sb.append("    </javaClientGenerator >");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <commentGenerator>");
		sb.append(NEW_LINE);
		sb.append("      <property name=\"suppressDate\" value=\"true\" />");
		sb.append(NEW_LINE);
		sb.append("    </commentGenerator>");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("    <!-- tables -->");
		sb.append(NEW_LINE);
		sb.append(buf.toString());
		sb.append("    <!-- tables end -->");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("  </context>");
		sb.append(NEW_LINE);
		sb.append("</generatorConfiguration>");
		sb.append(NEW_LINE);

		logger.info("###################################################################");
		logger.info(NEW_LINE + NEW_LINE + sb.toString() + NEW_LINE + NEW_LINE);
		logger.info("###################################################################");
		// 将字符串转换成2进制流
		InputStream is = null;

		try {
			is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			logger.error("", e1);
		}

		List<String> warnings = new ArrayList<String>();
		try {
			ConfigurationParser cp = new ConfigurationParser(warnings);
			Configuration config = cp.parseConfiguration(is);
			DefaultShellCallback shellCallback = new DefaultShellCallback(
					overwrite);

			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
					shellCallback, warnings);
			myBatisGenerator.generate(null);
		} catch (XMLParserException e) {
			List<String> errors = e.getErrors();
			for (String s : errors) {
				logger.error(s);
			}
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		} catch (InvalidConfigurationException e) {
			logger.error(e);
		}

		List<MybatisBean> myList = new ArrayList<MybatisBean>();
		if (null != list) {
			for (String s : list) {
				s = getTableName(s);
				String temp = getObjectName(s);
				String beanId = temp.substring(0, 1).toLowerCase()
						+ temp.substring(1) + "Mapper";
				MybatisBean mybatisBean = new MybatisBean();
				mybatisBean.setBeanId(beanId);
				mybatisBean.setBeanName(beanId);
				mybatisBean
						.setClassPath(dalPackage + ".dao." + temp + "Mapper");
				myList.add(mybatisBean);
			}
		}

		logger.info("##############################################################");
		logger.info("Create completely");
		logger.info("##############################################################");
		return myList;
	}

	private String getTableString(DatabaseEnum dbEnum, Database database,
			String tableName) {
		tableName = getTableName(tableName);
		ColumnResult result = database.getColumns(tableName);
		StringBuffer sb = new StringBuffer();
		sb.append("    <table ");
		sb.append("tableName=\"" + tableName + "\" ");
		sb.append("domainObjectName=\"" + getObjectName(tableName) + "\" ");
		sb.append("escapeWildcards=\"true\" ");
		sb.append("enableSelectByExample=\"false\" ");
		sb.append("enableDeleteByExample=\"false\" ");
		sb.append("enableCountByExample=\"false\" ");
		sb.append("enableUpdateByExample=\"false\">");
		sb.append(NEW_LINE);

		List<String> list = result.getAutoIncrementPrimaryKeys();
		for (String pk : list) {
			sb.append("      <generatedKey column=\"" + pk + "\" ");
			sb.append("sqlStatement=\"" + dbEnum.getSqlStatement() + "\" ");
			sb.append("identity=\"true\" />");
			sb.append(NEW_LINE);
		}
		sb.append("    </table>");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		return sb.toString();
	}

	private String getObjectName(String table) {
		if (table == null || table.trim().equals("")) {
			return null;
		}
		// 除去中间空格
		int index = table.indexOf(" ");
		while (index != -1) {
			if (index + 1 >= table.length() || index + 2 >= table.length()) {
				table = table.replace(" ", "");
				break;
			}
			String s1 = table.substring(index + 1, index + 2);
			String s2 = s1.toUpperCase();
			table = table.replace(" " + s1, "" + s2);
			index = table.indexOf(" ");
		}
		// 除去下划线
		index = table.indexOf("_");
		while (index != -1) {
			if (index + 1 >= table.length() || index + 2 >= table.length()) {
				table = table.replace("_", "");
				break;
			}
			String s1 = table.substring(index + 1, index + 2);
			String s2 = s1.toUpperCase();
			table = table.replace("_" + s1, "" + s2);
			index = table.indexOf("_");
		}
		if (table.length() == 0) {
			return null;
		}
		String s = table.substring(0, 1).toUpperCase()
				+ table.substring(1, table.length());
		return s;
	}

	private String getTableName(String tableName) {
		tableName = tableName.trim();

		Pattern p = Pattern.compile("^[^a-zA-Z0-9](.)+[^a-zA-Z0-9]$");
		Matcher matcher = p.matcher(tableName);
		if (matcher.find()) {
			tableName = tableName.substring(1, tableName.length() - 1);
		}
		return tableName;
	}
}
