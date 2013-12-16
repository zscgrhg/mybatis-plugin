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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
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

	private static final Logger LOGGER = Logger
			.getLogger(MybatisCreaterImpl.class);
	private Properties properties;

	public MybatisCreaterImpl(Properties properties) {
		this.properties = properties;
	}

	public List<MybatisBean> create(final Database database, final String url,
			final String databaseName, final String userName,
			final String password, final String dalPackage, final String dir,
			final boolean overwrite) {
		return create(database, url, databaseName, userName, password,
				dalPackage, dir, overwrite, new String[] {});
	}

	public List<MybatisBean> create(final Database database,
			final String jdbcUrl, final String databaseName,
			final String userName, final String password,
			final String dalPackage, final String outputDir,
			final boolean overwrite, final String... tables) {

		String dir = outputDir.replace(File.separator, "/");
		String url = jdbcUrl;
		if (url.contains("&") && !url.contains("&amp;")) {
			url = url.replace("&", "&amp;");
		}

		final String java = dir + "/src/main/java/";
		final String res = dir + "/src/main/resources/";

		File f = new File(java);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(res);
		if (!f.exists()) {
			f.mkdirs();
		}

		StringBuffer buf = new StringBuffer();

		for (final String name : tables) {
			buf.append(getTableString(database.getDatabaseEnum(), database,
					name));
		}
		database.close();

		final StringBuffer sb = new StringBuffer();
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
		sb.append("    <commentGenerator>");
		sb.append(NEW_LINE);
		sb.append("      <property name=\"suppressDate\" value=\"true\" />");
		sb.append(NEW_LINE);
		sb.append("    </commentGenerator>");
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

		LOGGER.info("###################################################################");
		LOGGER.info(NEW_LINE + NEW_LINE + sb.toString() + NEW_LINE + NEW_LINE);
		LOGGER.info("###################################################################");
		// 将字符串转换成2进制流
		InputStream is = null;

		try {
			is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error("data get btyes error.", e1);
			return null;
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

			List<MybatisBean> myList = new ArrayList<MybatisBean>();
			for (String s : tables) {
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

			LOGGER.info("##############################################################");
			LOGGER.info("Create completely");
			LOGGER.info("##############################################################");
			return myList;
		} catch (XMLParserException e) {
			List<String> errors = e.getErrors();
			for (String s : errors) {
				LOGGER.error(s);
			}
			LOGGER.error(e);
		} catch (SQLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		} catch (InvalidConfigurationException e) {
			LOGGER.error(e);
		}
		return null;
	}

	private String getTableString(final DatabaseEnum dbEnum,
			final Database database, final String tableName) {
		final String name = getTableName(tableName);
		final ColumnResult result = database.getColumns(name);
		final StringBuffer buf = new StringBuffer(300);
		buf.append("    <table ");
		buf.append("tableName=\"");
		buf.append(name);
		buf.append("\" ");
		buf.append("domainObjectName=\"");
		buf.append(getObjectName(name));
		buf.append("\" ");
		buf.append("escapeWildcards=\"true\" ");
		buf.append("enableSelectByExample=\"false\" ");
		buf.append("enableDeleteByExample=\"false\" ");
		buf.append("enableCountByExample=\"false\" ");
		buf.append("enableUpdateByExample=\"false\">");
		buf.append(NEW_LINE);

		final List<String> list = result.getAutoIncrementPrimaryKeys();
		for (String key : list) {
			buf.append("      <generatedKey column=\"");
			buf.append(key);
			buf.append("\" ");
			buf.append("sqlStatement=\"");
			buf.append(dbEnum.getSqlStatement());
			buf.append("\" ");
			buf.append("identity=\"true\" />");
			buf.append(NEW_LINE);
		}
		buf.append("    </table>");
		buf.append(NEW_LINE);
		buf.append(NEW_LINE);
		return buf.toString();
	}

	private String getObjectName(final String table) {
		if (null==table||"".equals(table.trim())) {
			return null;
		}
		String tableName = table;
		int index = tableName.indexOf(" ");
		while (index != -1) {
			if (index + 1 >= tableName.length()
					|| index + 2 >= tableName.length()) {
				tableName = tableName.replace(" ", "");
				break;
			}
			String s1 = tableName.substring(index + 1, index + 2);
			String s2 = s1.toUpperCase();
			tableName = tableName.replace(" " + s1, "" + s2);
			index = tableName.indexOf(" ");
		}
		//
		index = tableName.indexOf("_");
		while (index != -1) {
			if (index + 1 >= tableName.length()
					|| index + 2 >= tableName.length()) {
				tableName = tableName.replace("_", "");
				break;
			}
			String s1 = tableName.substring(index + 1, index + 2);
			String s2 = s1.toUpperCase();
			tableName = tableName.replace("_" + s1, "" + s2);
			index = tableName.indexOf("_");
		}
		if (tableName.length() == 0) {
			return null;
		}
		String s = tableName.substring(0, 1).toUpperCase()
				+ tableName.substring(1, tableName.length());
		return s;
	}

	private String getTableName(final String tableName) {
		String name = tableName.trim();

		final Pattern pattern = Pattern
				.compile("^[^a-zA-Z0-9](.)+[^a-zA-Z0-9]$");
		final Matcher matcher = pattern.matcher(name);
		if (matcher.find()) {
			name = name.substring(1, name.length() - 1);
		}
		return name;
	}
}
