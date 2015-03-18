/**
 * 
 */
package com.tqlab.plugin.mybatis.generator;

/**
 * @author John Lee
 *
 */
public class DbSql {

	private String id;
	private String sql;

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the sql
	 */
	public final String getSql() {
		return sql;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(String id) {
		this.id = id;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public final void setSql(String sql) {
		this.sql = sql;
	}

}
