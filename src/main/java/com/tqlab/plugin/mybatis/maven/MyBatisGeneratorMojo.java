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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.tqlab.plugin.mybatis.util.Constants;

/**
 * Goal which generates MyBatis/iBATIS artifacts.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Execute(goal = "generate", phase = LifecyclePhase.GENERATE_SOURCES)
public class MyBatisGeneratorMojo extends AbstractMojo {

	@Parameter(property = "mybatis.generator.outputDirectory", defaultValue = "${project.build.directory}/generated-sources/mybatis-generator")
	private File outputDirectory;

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
	@Parameter(property = "mybatis.generator.dbName")
	private String dbName;

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
	@Parameter
	private Properties properties;

	/**
	 * 
	 */
	@Parameter
	private List<DatabaseConfig> databaseConfig;

	private boolean isOverwrite() {
		return null == overwrite ? false : Boolean.parseBoolean(overwrite);
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

		// load log4j
		loadLog4j();

		getLog().info("context: " + this.getPluginContext());
		try {

			String dir = outputDirectory.getAbsolutePath().replace(
					File.separator, "/");
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

			if (this.isOverwrite()) {
				File packageFile = new File(new File(java),
						this.packages.replace('.', '/'));
				File daoPackageFile = new File(packageFile, "dao");
				File dataobjectPackageFile = new File(packageFile, "dataobject");
				try {
					if (daoPackageFile.exists()) {
						FileUtils.deleteDirectory(daoPackageFile);
					}
					if (dataobjectPackageFile.exists()) {
						FileUtils.deleteDirectory(dataobjectPackageFile);
					}
				} catch (IOException e) {
					this.getLog().error("Delete file error.", e);
				}
			}

			MybatisExecutorCallback callback = createMybatisExecutorCallback();
			for (DatabaseConfig config : buildConfig()) {
				MybatisExecutor executor = new MybatisExecutor(this.getLog(),
						this.outputDirectory, this.packages,
						this.isOverwrite(), config);
				executor.execute(callback);
			}
			callback.onFinsh(this.isOverwrite());
		} catch (IOException e) {
			throw new MojoExecutionException("", e);
		}
	}

	private MybatisExecutorCallback createMybatisExecutorCallback() {

		return new MybatisExecutorCallback() {

			private List<String> jdbcConfig = new ArrayList<String>();
			private List<String> springConfig = new ArrayList<String>();
			private List<String> osgiConfig = new ArrayList<String>();

			@Override
			public void onWriteJdbcConfig(String str) {
				jdbcConfig.add(str);
			}

			@Override
			public void onWriteSpringConfig(String str) {
				springConfig.add(str);
			}

			@Override
			public void onWriteOsgiConfig(String str) {
				osgiConfig.add(str);
			}

			@Override
			public void onFinsh(boolean overwrite) throws IOException {

				if (jdbcConfig.size() > 0) {
					this.write(outputDirectory.getAbsolutePath()
							+ File.separator + "src/main/resources/",
							"jdbc.properties", "jdbc.template", "${jdbc}",
							getConfigStr(jdbcConfig), true);
				}

				if (springConfig.size() > 0) {
					this.write(outputDirectory.getAbsolutePath()
							+ File.separator
							+ "src/main/resources/META-INF/spring/",
							"common-db-mapper.xml",
							"common-db-mapper.template", "${beans}",
							getConfigStr(springConfig), overwrite);
				}

				if (osgiConfig.size() > 0) {
					this.write(outputDirectory.getAbsolutePath()
							+ File.separator
							+ "src/main/resources/META-INF/spring/",
							"common-dal-osgi.xml", "common-dal-osgi.template",
							"${osgi}", getConfigStr(osgiConfig), overwrite);
				}

			}

			@Override
			public String getJdbcConfigPostfix() {
				return jdbcConfig.size() == 0 ? "" : "." + jdbcConfig.size();
			}

			@Override
			public String getStringConfigPostfix() {
				return springConfig.size() == 0 ? "" : "."
						+ springConfig.size();
			}

			@Override
			public String getOsgiConfigPostfix() {
				return osgiConfig.size() == 0 ? "" : "." + osgiConfig.size();
			}

			private String getConfigStr(List<String> list) {
				if (null == list) {
					return "";
				}
				String str = "";
				for (String s : list) {
					str += s + "\r\n";
				}
				return str;
			}

			private void write(String outDir, String fileName,
					String templateName, String replaceStr,
					String repalceValue, boolean overwrite) throws IOException {

				InputStream is = this.getClass().getResourceAsStream(
						"/" + templateName);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				StringBuffer buf = new StringBuffer();
				while (null != (line = br.readLine())) {
					buf.append(line);
					buf.append(Constants.LINE_SEPARATOR);
				}

				String result = buf.toString()
						.replace(replaceStr, repalceValue);
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

				if (file.getParentFile() != null
						&& !file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				OutputStreamWriter writer = new OutputStreamWriter(
						new FileOutputStream(file), "UTF-8");
				writer.write(str);
				writer.flush();
				writer.close();

				MyBatisGeneratorMojo.this.getLog().info(str);

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

		};
	}

	private List<DatabaseConfig> buildConfig() {
		List<DatabaseConfig> list = new ArrayList<DatabaseConfig>();
		DatabaseConfig config = new DatabaseConfig();
		config.setDatabase(database);
		config.setDbName(dbName);
		config.setGenerateJdbcConfig(generateJdbcConfig);
		config.setGenerateOsgiConfig(generateOsgiConfig);
		config.setGenerateSpringConfig(generateSpringConfig);
		config.setJdbcDriver(jdbcDriver);
		config.setJdbcPassword(jdbcPassword);
		config.setJdbcURL(jdbcURL);
		config.setJdbcUserId(jdbcUserId);
		config.setProperties(properties);
		config.setSqlScript(sqlScript);
		config.setSqlTemplatePath(sqlTemplatePath);
		config.setTableNames(tableNames);
		config.setTablePrefix(tablePrefix);
		config.setUseCache(useCache);

		list.add(config);
		if (null != databaseConfig) {
			list.addAll(databaseConfig);
		}
		return list;
	}

}
