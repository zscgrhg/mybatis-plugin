/**
 * 
 */
package com.tqlab.plugin.mybatis.maven;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.mybatis.generator.internal.util.StringUtility;

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
 * @author lijun
 *
 */
public class MybatisExecutor {

	private Log log;
	private File outputDirectory;
	private String packages;
	private boolean overwrite;
	private DatabaseConfig config;

	public MybatisExecutor(Log log, File outputDirectory, String packages,
			boolean overwrite, DatabaseConfig config) {
		this.log = log;
		this.outputDirectory = outputDirectory;
		this.packages = packages;
		this.overwrite = overwrite;
		this.config = config;
	}

	/**
	 * @param callback
	 * @throws IOException
	 * 
	 */
	public void execute(MybatisExecutorCallback callback) throws IOException,
			MojoExecutionException {

		if (null == config.getDbName()) {
			return;
		}

		log.info("db name: " + config.getDbName());

		Properties properties = buildProperties();
		Properties info = new Properties();

		if (config.getJdbcUserId() != null) {
			info.put("user", config.getJdbcUserId());
		}
		if (getJDBCPassword() != null) {
			info.put("password", getJDBCPassword());
		}
		info.putAll(properties);

		Database databaseObj = new DatabaseFactoryImpl().getDatabase(
				DatabaseEnum.getDatabaseEnum(config.getDbName()),
				config.getDatabase(), getJDBCUrl(), info,
				config.getJdbcDriver());

		runScriptIfNecessary(databaseObj);

		Set<String> fullyqualifiedTables = new HashSet<String>();
		if (StringUtility.stringHasValue(config.getTableNames())) {
			StringTokenizer st = new StringTokenizer(config.getTableNames(),
					","); //$NON-NLS-1$
			while (st.hasMoreTokens()) {
				String s = st.nextToken().trim();
				fullyqualifiedTables.add(s);
			}
		}

		if (fullyqualifiedTables.isEmpty()) {
			Set<String> tables = new HashSet<String>();
			for (String s : databaseObj.getTablesName()) {
				tables.add(s);
			}
			fullyqualifiedTables.addAll(databaseObj.getTablesName());
		}

		String tablesArray[] = fullyqualifiedTables.toArray(new String[0]);
		MybatisCreater creater = new MybatisCreaterImpl(properties);
		List<MybatisBean> list = creater.create(databaseObj, getJDBCUrl(),
				config.getDatabase(), config.getJdbcUserId(),
				getJDBCPassword(), packages, outputDirectory.getAbsolutePath(),
				overwrite, getDbTables(), tablesArray);
		if (null == list || list.size() == 0) {
			return;
		}

		if ("true".equalsIgnoreCase(config.getGenerateJdbcConfig())) {
			// /////////////////////////////////////////////////////////////
			// jdbc.properties
			// /////////////////////////////////////////////////////////////
			StringBuffer replaceBuf = new StringBuffer();
			replaceBuf.append("jdbc.driver=");
			replaceBuf.append(callback.getJdbcConfigPostfix());
			replaceBuf.append("=");
			replaceBuf.append(databaseObj.getDriverClass());
			replaceBuf.append(Constants.LINE_SEPARATOR);
			replaceBuf.append("jdbc.url");
			replaceBuf.append(callback.getJdbcConfigPostfix());
			replaceBuf.append("=");
			replaceBuf.append(getJDBCUrl());
			replaceBuf.append(Constants.LINE_SEPARATOR);
			replaceBuf.append("jdbc.username");
			replaceBuf.append(callback.getJdbcConfigPostfix());
			replaceBuf.append("=");
			replaceBuf.append(config.getJdbcUserId());
			replaceBuf.append(Constants.LINE_SEPARATOR);
			replaceBuf.append("jdbc.password");
			replaceBuf.append(callback.getJdbcConfigPostfix());
			replaceBuf.append("=");
			replaceBuf.append(getJDBCPassword());
			replaceBuf.append(Constants.LINE_SEPARATOR);

			callback.onWriteJdbcConfig(replaceBuf.toString());
		}

		if ("true".equalsIgnoreCase(config.getGenerateSpringConfig())) {
			// /////////////////////////////////////////////////////////////
			// common-db-mapper.xml
			// /////////////////////////////////////////////////////////////

			StringBuffer replaceBuf = new StringBuffer();
			for (MybatisBean bean : list) {
				replaceBuf.append(bean.toString());
				replaceBuf.append(Constants.LINE_SEPARATOR);
			}
			callback.onWriteSpringConfig(replaceBuf.toString());
		}

		if ("true".equalsIgnoreCase(config.getGenerateOsgiConfig())) {
			// /////////////////////////////////////////////////////////////
			// common-dal-osgi.xml
			// /////////////////////////////////////////////////////////////

			StringBuffer replaceBuf = new StringBuffer();
			for (MybatisBean bean : list) {
				replaceBuf.append(bean.toOsgiServiceString());
				replaceBuf.append(Constants.LINE_SEPARATOR);
			}
			callback.onWriteOsgiConfig(replaceBuf.toString());
		}
	}

	private Properties buildProperties() {
		Properties properties = new Properties();
		if (StringUtils.isNotBlank(config.getSqlTemplatePath())) {
			properties.put(Constants.SQL_TEMPLATE_PATH,
					config.getSqlTemplatePath());
		}
		if (StringUtils.isNotBlank(config.getUseCache())) {
			properties.put(Constants.USE_CACHE, config.getUseCache());
		}
		if (StringUtils.isNotBlank(config.getTablePrefix())) {
			properties.put(Constants.TABLE_PREFIX, config.getTablePrefix());
		}

		if (null != config.getProperties()) {
			properties.putAll(config.getProperties());
		}
		return properties;
	}

	private Map<String, DbTable> getDbTables() {

		Map<String, DbTable> map = new HashMap<String, DbTable>();
		File sqlTemplateDir = new File(config.getSqlTemplatePath());
		if (!sqlTemplateDir.exists()) {
			return map;
		}

		File[] files = sqlTemplateDir.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".xml")) {
				DbTable dbTable = SqlTemplateParserUtil.parseDbTable(file);
				if (null != dbTable) {
					String name = dbTable.getName();
					map.put(name.toLowerCase(), dbTable);
				}
			}
		}
		return map;
	}

	private String getJDBCPassword() {
		return null == config.getJdbcPassword() ? "" : config.getJdbcPassword();
	}

	private String getJDBCUrl() {
		if (StringUtils.isBlank(config.getJdbcURL())) {
			return " ";
		}
		final DatabaseEnum databaseEnum = DatabaseEnum.getDatabaseEnum(config
				.getDbName());
		String jdbcURL = config.getJdbcURL();
		if (databaseEnum == DatabaseEnum.HSQLDB) {
			jdbcURL = jdbcURL.replace("\\", "/");
		}
		return jdbcURL;
	}

	private void runScriptIfNecessary(Database database)
			throws MojoExecutionException {
		if (config.getSqlScript() == null) {
			return;
		}

		SqlScriptRunner scriptRunner = new SqlScriptRunner(
				config.getSqlScript(), database.getDriverClass(), getJDBCUrl(),
				config.getJdbcUserId(), getJDBCPassword());
		scriptRunner.executeScript();
	}

}
