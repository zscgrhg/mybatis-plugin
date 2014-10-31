/*
 * Copyright 2009 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tqlab.plugin.mybatis.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mybatis.generator.internal.util.StringUtility;

import com.google.common.base.Splitter;
import com.tqlab.plugin.mybatis.database.Database;
import com.tqlab.plugin.mybatis.database.DatabaseEnum;
import com.tqlab.plugin.mybatis.database.DatabaseFactoryImpl;
import com.tqlab.plugin.mybatis.generator.DbTable;
import com.tqlab.plugin.mybatis.generator.MybatisBean;
import com.tqlab.plugin.mybatis.generator.MybatisCreater;
import com.tqlab.plugin.mybatis.generator.MybatisCreaterImpl;
import com.tqlab.plugin.mybatis.util.Constants;
import com.tqlab.plugin.mybatis.util.SqlTemplateParserUtil;

/**
 * Goal which generates MyBatis/iBATIS artifacts.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Execute(goal = "generate", phase = LifecyclePhase.GENERATE_SOURCES)
public class MyBatisGeneratorMojo extends AbstractMojo {

	@Parameter(property = "mybatis.generator.outputDirectory", defaultValue = "${project.build.directory}/generated-sources/mybatis-generator")
	private File outputDirectory;

	/**
	 * Location of a SQL script file to run before generating code. If null,
	 * then no script will be run. If not null, then jdbcDriver, jdbcURL must be
	 * supplied also, and jdbcUserId and jdbcPassword may be supplied.
	 */
	@Parameter(property = "mybatis.generator.sqlScript")
	private String sqlScript;

	/**
	 * JDBC URL to use if a sql.script.file is specified
	 */
	@Parameter(property = "mybatis.generator.jdbcURL")
	private String jdbcURL;

	/**
	 * JDBC user ID to use
	 */
	@Parameter(property = "mybatis.generator.jdbcUserId")
	private String jdbcUserId;

	/**
	 * JDBC password to use
	 */
	@Parameter(property = "mybatis.generator.jdbcPassword")
	private String jdbcPassword;

	/**
	 * JDBC driver to use
	 */
	@Parameter(property = "mybatis.generator.jdbcDriver")
	private String jdbcDriver;

	/**
	 * Comma delimited list of table names to generate
	 */
	@Parameter(property = "mybatis.generator.tableNames")
	private String tableNames;

	/**
	 * The table name's prefix. For example, db_xxxxxx.
	 */
	@Parameter(property = "mybatis.generator.tablePrefix")
	private String tablePrefix;

	/**
	 * The application database name
	 */
	@Parameter(property = "mybatis.generator.database")
	private String database;

	/**
	 * The database name, mysql, hsqldb etc.
	 */
	@Parameter(property = "mybatis.generator.dbName", required = true)
	private String dbName;

	/**
	 * The package for java code generator.
	 */
	@Parameter(property = "mybatis.generator.packages", required = true)
	private String packages;

	/**
	 * Overwrite the exist code, config file or not.
	 */
	@Parameter(property = "mybatis.generator.overwrite", defaultValue = "true")
	private String overwrite;

	/**
	 * Sql template file path.
	 */
	@Parameter(property = "mybatis.generator.sqlTemplatePath", defaultValue = "${project.basedir}/src/main/resources/sqltemplate")
	private String sqlTemplatePath;

	/**
	 * Use cache or not.
	 */
	@Parameter(property = "mybatis.generator.useCache", defaultValue = "false")
	private String useCache;

	/**
	 * Generate JDBC config file or not.
	 */
	@Parameter(property = "mybatis.generator.generateJdbcConfig", defaultValue = "false")
	private String generateJdbcConfig;

	/**
	 * Generate spring xml config file or not.
	 */
	@Parameter(property = "mybatis.generator.generateSpringConfig", defaultValue = "false")
	private String generateSpringConfig;

	/**
	 * Generate spring osgi xml config file or not.
	 */
	@Parameter(property = "mybatis.generator.generateOsgiConfig", defaultValue = "false")
	private String generateOsgiConfig;

	/**
	 * Tables alias config
	 */
	@Parameter
	private String tableAlias;

	/**
	 * Extra config.
	 */
	@Parameter
	private Properties properties;

	private String getJDBCPassword() {
		return null == jdbcPassword ? "" : jdbcPassword;
	}

	private boolean isOverwrite() {
		return null == overwrite ? false : Boolean.parseBoolean(overwrite);
	}

	private String getJDBCUrl() {
		if (StringUtils.isBlank(jdbcURL)) {
			return " ";
		}
		final DatabaseEnum databaseEnum = DatabaseEnum.getDatabaseEnum(dbName);
		String jdbcURL = this.jdbcURL;
		if (databaseEnum == DatabaseEnum.HSQLDB) {
			jdbcURL = jdbcURL.replace("\\", "/");
		}
		return jdbcURL;
	}

	private void loadLog4j() {
		try {
			Properties props = new Properties();
			props.load(getClass().getResourceAsStream(
					"/com/tqlab/plugin/mybatis/log4j.properties"));
			PropertyConfigurator.configure(props);
		} catch (IOException e1) {
			getLog().warn("load log4j.properties error.");
		}
	}

	public void execute() throws MojoExecutionException {

		if (null == dbName) {
			return;
		}
		// load log4j
		loadLog4j();

		getLog().info("context: " + this.getPluginContext());
		getLog().info("tableAlias: " + tableAlias);

		Properties properties = buildProperties();
		Properties info = new Properties();

		if (jdbcUserId != null) {
			info.put("user", jdbcUserId);
		}
		if (getJDBCPassword() != null) {
			info.put("password", getJDBCPassword());
		}
		info.putAll(properties);

		Database databaseObj = new DatabaseFactoryImpl().getDatabase(
				DatabaseEnum.getDatabaseEnum(dbName), database, getJDBCUrl(),
				info, this.jdbcDriver);

		runScriptIfNecessary(databaseObj);

		Set<String> fullyqualifiedTables = new HashSet<String>();
		if (StringUtility.stringHasValue(tableNames)) {
			StringTokenizer st = new StringTokenizer(tableNames, ","); //$NON-NLS-1$
			while (st.hasMoreTokens()) {
				String s = st.nextToken().trim();
				fullyqualifiedTables.add(s);
			}
		}

		if (fullyqualifiedTables.isEmpty()) {
			Set<String> tables = new HashSet<String>();
			Pattern p = Pattern.compile("^_(\\d)+$");
			for (String s : databaseObj.getTablesName()) {
				String find = null;
				for (Map.Entry<String, String> e : getTableAlias().entrySet()) {
					if (s.startsWith(e.getKey())) {
						String left = s.substring(0, e.getKey().length());
						if (p.matcher(left).find()) {
							find = e.getKey();
							break;
						}
					}
				}
				if (null != find) {
					tables.add(find);
				} else {
					tables.add(s);
				}

			}
			fullyqualifiedTables.addAll(databaseObj.getTablesName());
		}

		try {
			String tablesArray[] = fullyqualifiedTables.toArray(new String[0]);
			MybatisCreater creater = new MybatisCreaterImpl(properties);
			List<MybatisBean> list = creater.create(databaseObj, getJDBCUrl(),
					database, jdbcUserId, getJDBCPassword(), packages,
					outputDirectory.getAbsolutePath(), isOverwrite(),
					getDbTables(), tablesArray);
			if (null == list || list.size() == 0) {
				return;
			}

			if ("true".equalsIgnoreCase(generateJdbcConfig)) {
				// /////////////////////////////////////////////////////////////
				// jdbc.properties
				// /////////////////////////////////////////////////////////////
				StringBuffer replaceBuf = new StringBuffer();
				replaceBuf.append("jdbc.driver=");
				replaceBuf.append(databaseObj.getDriverClass());
				replaceBuf.append(Constants.LINE_SEPARATOR);
				replaceBuf.append("jdbc.url=");
				replaceBuf.append(getJDBCUrl());
				replaceBuf.append(Constants.LINE_SEPARATOR);
				replaceBuf.append("jdbc.username=");
				replaceBuf.append(jdbcUserId);
				replaceBuf.append(Constants.LINE_SEPARATOR);
				replaceBuf.append("jdbc.password=");
				replaceBuf.append(getJDBCPassword());
				replaceBuf.append(Constants.LINE_SEPARATOR);
				this.write(outputDirectory.getAbsolutePath() + File.separator
						+ "src/main/resources/", "jdbc.properties",
						"jdbc.template", "${jdbc}", replaceBuf.toString());
			}

			if ("true".equalsIgnoreCase(generateSpringConfig)) {
				// /////////////////////////////////////////////////////////////
				// common-db-mapper.xml
				// /////////////////////////////////////////////////////////////

				StringBuffer replaceBuf = new StringBuffer();
				for (MybatisBean bean : list) {
					replaceBuf.append(bean.toString());
					replaceBuf.append(Constants.LINE_SEPARATOR);
				}
				this.write(outputDirectory.getAbsolutePath() + File.separator
						+ "src/main/resources/META-INF/spring/",
						"common-db-mapper.xml", "common-db-mapper.template",
						"${beans}", replaceBuf.toString(), isOverwrite());
			}

			if ("true".equalsIgnoreCase(generateOsgiConfig)) {
				// /////////////////////////////////////////////////////////////
				// common-dal-osgi.xml
				// /////////////////////////////////////////////////////////////

				StringBuffer replaceBuf = new StringBuffer();
				for (MybatisBean bean : list) {
					replaceBuf.append(bean.toOsgiServiceString());
					replaceBuf.append(Constants.LINE_SEPARATOR);
				}
				this.write(outputDirectory.getAbsolutePath() + File.separator
						+ "src/main/resources/META-INF/spring/",
						"common-dal-osgi.xml", "common-dal-osgi.template",
						"${osgi}", replaceBuf.toString(), isOverwrite());
			}
		} catch (IOException e) {
			throw new MojoExecutionException("", e);
		}
	}

	private void write(String outDir, String fileName, String templateName,
			String replaceStr, String repalceValue) throws IOException {
		this.write(outDir, fileName, templateName, replaceStr, repalceValue,
				true);
	}

	private void write(String outDir, String fileName, String templateName,
			String replaceStr, String repalceValue, boolean overwrite)
			throws IOException {

		InputStream is = this.getClass()
				.getResourceAsStream("/" + templateName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuffer buf = new StringBuffer();
		while (null != (line = br.readLine())) {
			buf.append(line);
			buf.append(Constants.LINE_SEPARATOR);
		}

		String result = buf.toString().replace(replaceStr, repalceValue);
		File dir = new File(outDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String outputPath = dir + File.separator + fileName;

		if (!overwrite) {
			File file = new File(outputPath);
			while (file.exists()) {
				String name = getFileName(file.getName());
				outputPath = dir + File.separator + name;
				file = new File(outputPath);
			}
		}

		this.write(outputPath, result);
	}

	private void write(String filePath, String str) throws IOException {
		File file = new File(filePath);

		if (file.getParentFile() != null && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8");
		writer.write(str);
		writer.flush();
		writer.close();

		getLog().info(str);

	}

	private String getFileName(String name) {
		int index = name.lastIndexOf(".");
		if (index > 0) {
			String temp1 = name.substring(0, index);
			String temp2 = name.substring(index + 1);
			try {
				Integer i = Integer.parseInt(temp2);
				i++;
				temp1 = temp1 + "." + i;
				return temp1;
			} catch (Exception e) {
				return name + ".1";
			}
		}
		return name + ".1";
	}

	private void runScriptIfNecessary(Database database)
			throws MojoExecutionException {
		if (sqlScript == null) {
			return;
		}

		SqlScriptRunner scriptRunner = new SqlScriptRunner(sqlScript,
				database.getDriverClass(), getJDBCUrl(), jdbcUserId,
				getJDBCPassword());
		scriptRunner.executeScript();
	}

	private Properties buildProperties() {
		Properties properties = new Properties();
		if (StringUtils.isNotBlank(sqlTemplatePath)) {
			properties.put(Constants.SQL_TEMPLATE_PATH, sqlTemplatePath);
		}
		if (StringUtils.isNotBlank(useCache)) {
			properties.put(Constants.USE_CACHE, useCache);
		}
		if (StringUtils.isNotBlank(tablePrefix)) {
			properties.put(Constants.TABLE_PREFIX, tablePrefix);
		}
		if (null != this.tableAlias) {
			properties.put(Constants.TABLE_ALIAS, getTableAlias());
		}

		if (null != this.properties) {
			properties.putAll(this.properties);
		}
		return properties;
	}

	private Map<String, String> getTableAlias() {
		if (StringUtils.isBlank(tableAlias) || tableAlias.length() < 5) {
			return new HashMap<String, String>();
		}
		Map<String, String> tableAlias = Splitter
				.on(',')
				.withKeyValueSeparator('=')
				.split(this.tableAlias.substring(1,
						this.tableAlias.length() - 1));
		return tableAlias;
	}

	private Map<String, DbTable> getDbTables() {

		Map<String, DbTable> map = new HashMap<String, DbTable>();
		File sqlTemplateDir = new File(sqlTemplatePath);
		if (!sqlTemplateDir.exists()) {
			return map;
		}

		File[] files = sqlTemplateDir.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".xml")) {
				DbTable dbTable = SqlTemplateParserUtil.parseDbTable(file);
				if (null != dbTable) {
					String name = getTableAlias().get(dbTable.getName());
					if (null == name) {
						name = dbTable.getName();
					}
					map.put(name.toLowerCase(), dbTable);
				}
			}
		}
		return map;
	}
}
