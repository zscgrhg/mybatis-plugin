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
package org.mybatis.generator.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mybatis.generator.internal.util.StringUtility;

import com.tqlab.plugin.mybatis.database.Database;
import com.tqlab.plugin.mybatis.database.DatabaseEnum;
import com.tqlab.plugin.mybatis.database.DatabaseFactoryImpl;
import com.tqlab.plugin.mybatis.generator.MybatisBean;
import com.tqlab.plugin.mybatis.generator.MybatisCreater;
import com.tqlab.plugin.mybatis.generator.MybatisCreaterImpl;

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
	@Parameter(property = "mybatis.generator.jdbcURL", required = true)
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
	 * Comma delimited list of table names to generate
	 */
	@Parameter(property = "mybatis.generator.tableNames")
	private String tableNames;

	/**
	 * The application database name
	 */
	@Parameter(property = "mybatis.generator.database", required = true)
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
	 * Extra config.
	 */
	@Parameter(property = "mybatis.generator.properties")
	private Properties properties;

	private String getJDBCPassword() {
		return null == jdbcPassword ? "" : jdbcPassword;
	}

	private boolean isOverwrite() {
		return null == overwrite ? false : Boolean.parseBoolean(overwrite);
	}

	private String getJDBCUrl() {
		DatabaseEnum databaseEnum = DatabaseEnum.getDatabaseEnum(dbName);
		switch (databaseEnum) {
		case HSQLDB: {
			return jdbcURL.replace("\\", "/");
		}
		default: {
			//
		}
		}
		return this.jdbcURL;
	}

	public void execute() throws MojoExecutionException {

		if (null == dbName) {
			return;
		}

		Database databaseObj = new DatabaseFactoryImpl().getDatabase(
				DatabaseEnum.getDatabaseEnum(dbName), database, getJDBCUrl(),
				jdbcUserId, getJDBCPassword());

		List<String> warnings = new ArrayList<String>();

		runScriptIfNecessary(databaseObj);

		Set<String> fullyqualifiedTables = new HashSet<String>();
		if (StringUtility.stringHasValue(tableNames)) {
			StringTokenizer st = new StringTokenizer(tableNames, ","); //$NON-NLS-1$
			while (st.hasMoreTokens()) {
				String s = st.nextToken().trim();
				if (s.length() > 0) {
					fullyqualifiedTables.add(s);
				}
			}
		}

		if (fullyqualifiedTables.isEmpty()) {
			fullyqualifiedTables.addAll(databaseObj.getTablesName());
		}

		try {
			Properties properties = new Properties();
			properties.put("sql.template.path", sqlTemplatePath);
			//
			properties.put("use.cache", useCache);
			properties.putAll(this.properties);

			String tablesArray[] = fullyqualifiedTables.toArray(new String[0]);
			MybatisCreater creater = new MybatisCreaterImpl(getLog(),
					properties);
			List<MybatisBean> list = creater.create(databaseObj, getJDBCUrl(),
					database, jdbcUserId, getJDBCPassword(), packages,
					outputDirectory.getAbsolutePath(), isOverwrite(),
					tablesArray);
			if ("true".equalsIgnoreCase(generateJdbcConfig)) {
				// /////////////////////////////////////////////////////////////
				// jdbc.properties
				// /////////////////////////////////////////////////////////////
				StringBuffer replaceBuf = new StringBuffer();
				replaceBuf.append("jdbc.driver=");
				replaceBuf.append(databaseObj.getDirverClass());
				replaceBuf.append("\r\n");
				replaceBuf.append("jdbc.url=");
				replaceBuf.append(getJDBCUrl());
				replaceBuf.append("\r\n");
				replaceBuf.append("jdbc.username=");
				replaceBuf.append(jdbcUserId);
				replaceBuf.append("\r\n");
				replaceBuf.append("jdbc.password=");
				replaceBuf.append(getJDBCPassword());
				replaceBuf.append("\r\n");
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
					replaceBuf.append("\r\n");
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
					replaceBuf.append("\r\n");
				}
				this.write(outputDirectory.getAbsolutePath() + File.separator
						+ "src/main/resources/META-INF/spring/",
						"common-dal-osgi.xml", "common-dal-osgi.template",
						"${osgi}", replaceBuf.toString(), isOverwrite());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException(e.getMessage());
		}

		for (String error : warnings) {
			getLog().warn(error);
		}

		// if (project != null && outputDirectory != null
		// && outputDirectory.exists()) {
		// project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
		//
		// Resource resource = new Resource();
		// resource.setDirectory(outputDirectory.getAbsolutePath());
		// resource.addInclude("**/*.xml");
		// project.addResource(resource);
		// }
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
			buf.append("\r\n");
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
				database.getDirverClass(), getJDBCUrl(), jdbcUserId,
				getJDBCPassword());
		scriptRunner.setLog(getLog());
		scriptRunner.executeScript();
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}
}
