/**
 * 
 */
package com.tqlab.plugin.mybatis.maven;

import java.util.Properties;

/**
 * @author lijun
 *
 */
public class DatabaseConfig {

	private String id;
	private String sqlScript;
	private String jdbcURL;
	private String jdbcUserId;
	private String jdbcPassword;
	private String jdbcDriver;
	private String tableNames;
	private String tablePrefix;
	private String database;
	private String dbName;
	private String sqlTemplatePath;
	private String generateJdbcConfig;
	private String generateSpringConfig;
	private String generateOsgiConfig;
	private String useCache;
	private Properties properties;

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the sqlScript
	 */
	public final String getSqlScript() {
		return sqlScript;
	}

	/**
	 * @return the jdbcURL
	 */
	public final String getJdbcURL() {
		return jdbcURL;
	}

	/**
	 * @return the jdbcUserId
	 */
	public final String getJdbcUserId() {
		return jdbcUserId;
	}

	/**
	 * @return the jdbcPassword
	 */
	public final String getJdbcPassword() {
		return jdbcPassword;
	}

	/**
	 * @return the jdbcDriver
	 */
	public final String getJdbcDriver() {
		return jdbcDriver;
	}

	/**
	 * @return the tableNames
	 */
	public final String getTableNames() {
		return tableNames;
	}

	/**
	 * @return the tablePrefix
	 */
	public final String getTablePrefix() {
		return tablePrefix;
	}

	/**
	 * @return the database
	 */
	public final String getDatabase() {
		return database;
	}

	/**
	 * @return the dbName
	 */
	public final String getDbName() {
		return dbName;
	}

	/**
	 * @return the sqlTemplatePath
	 */
	public final String getSqlTemplatePath() {
		return sqlTemplatePath;
	}

	/**
	 * @return the generateJdbcConfig
	 */
	public final String getGenerateJdbcConfig() {
		return generateJdbcConfig;
	}

	/**
	 * @return the generateSpringConfig
	 */
	public final String getGenerateSpringConfig() {
		return generateSpringConfig;
	}

	/**
	 * @return the generateOsgiConfig
	 */
	public final String getGenerateOsgiConfig() {
		return generateOsgiConfig;
	}

	/**
	 * @return the properties
	 */
	public final Properties getProperties() {
		return properties;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(String id) {
		this.id = id;
	}

	/**
	 * @param sqlScript
	 *            the sqlScript to set
	 */
	public final void setSqlScript(String sqlScript) {
		this.sqlScript = sqlScript;
	}

	/**
	 * @param jdbcURL
	 *            the jdbcURL to set
	 */
	public final void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	/**
	 * @param jdbcUserId
	 *            the jdbcUserId to set
	 */
	public final void setJdbcUserId(String jdbcUserId) {
		this.jdbcUserId = jdbcUserId;
	}

	/**
	 * @param jdbcPassword
	 *            the jdbcPassword to set
	 */
	public final void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	/**
	 * @param jdbcDriver
	 *            the jdbcDriver to set
	 */
	public final void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	/**
	 * @param tableNames
	 *            the tableNames to set
	 */
	public final void setTableNames(String tableNames) {
		this.tableNames = tableNames;
	}

	/**
	 * @param tablePrefix
	 *            the tablePrefix to set
	 */
	public final void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	/**
	 * @param database
	 *            the database to set
	 */
	public final void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public final void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @param sqlTemplatePath
	 *            the sqlTemplatePath to set
	 */
	public final void setSqlTemplatePath(String sqlTemplatePath) {
		this.sqlTemplatePath = sqlTemplatePath;
	}

	/**
	 * @param generateJdbcConfig
	 *            the generateJdbcConfig to set
	 */
	public final void setGenerateJdbcConfig(String generateJdbcConfig) {
		this.generateJdbcConfig = generateJdbcConfig;
	}

	/**
	 * @param generateSpringConfig
	 *            the generateSpringConfig to set
	 */
	public final void setGenerateSpringConfig(String generateSpringConfig) {
		this.generateSpringConfig = generateSpringConfig;
	}

	/**
	 * @param generateOsgiConfig
	 *            the generateOsgiConfig to set
	 */
	public final void setGenerateOsgiConfig(String generateOsgiConfig) {
		this.generateOsgiConfig = generateOsgiConfig;
	}

	/**
	 * @return the useCache
	 */
	public final String getUseCache() {
		return useCache;
	}

	/**
	 * @param useCache
	 *            the useCache to set
	 */
	public final void setUseCache(String useCache) {
		this.useCache = useCache;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public final void setProperties(Properties properties) {
		this.properties = properties;
	}
}
