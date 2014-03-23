/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

/**
 * @author John Lee
 * 
 */
public final class SqlUtil {

	/**
	 * <xxxx /> or <xxxx>fff</xxxx>
	 */
	private static final String XML_PATTERN = "<[^>]*>";

	/**
	 * <pre>
	 * <foreach item="item" index="index" collection="list" open="(" separator="," close=")"> #{item} 
	 * </ foreach>
	 * </pre>
	 */
	private static final String FOREACH_PATTERN = "<([Ff][Oo][Rr][Ee][Aa][Cc][Hh])([\\sa-zA-Z0-9=\"#{}(),_\\-\\\\])*>([\\sa-zA-Z0-9=\"#{}(),_\\-\\\\])*</([Ff][Oo][Rr][Ee][Aa][Cc][Hh])>";

	/**
	 * Such as #{key,jdbcType=BIGINT}
	 */
	private static final String PARAM_PATTERN = "#\\{[a-zA-Z,=\\-\\_\\s]+\\}";

	/**
	 * TOP #{size,jdbcType=BIGINT}
	 */
	private static final String TOP_PATTERN = "([Tt][Oo][Pp])(\\s+"
			+ PARAM_PATTERN + ")";

	/**
	 * Limit 0, #{size,jdbcType=BIGINT}
	 */
	private static final String LIMIT_PATTERN = "([Ll][Ii][Mm][Ii][Tt])(\\s*(\\d+|"
			+ PARAM_PATTERN
			+ ")\\s*(,|([Oo][Ff][Ff][Ss][Ee][Tt]))\\s*)?(\\s*(\\d+|"
			+ PARAM_PATTERN + "))";

	private static final String LT = "&lt;";
	private static final String AMP = "&amp;";

	private SqlUtil() {

	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public static String filterSql(final String sql) {
		String mybatisSql = filterXml(sql, " ");
		mybatisSql = mybatisSql.replaceAll(TOP_PATTERN, "TOP 1 ");
		mybatisSql = mybatisSql.replaceAll(LIMIT_PATTERN, "LIMIT 1 ");
		mybatisSql = mybatisSql.replaceAll(PARAM_PATTERN, "TQLAB ");
		mybatisSql = mybatisSql.trim();
		return mybatisSql;
	}

	/**
	 * 
	 * @param str
	 * @param replacement
	 * @return
	 */
	public static String filterXml(final String str, final String replacement) {
		String s = str.replaceAll(FOREACH_PATTERN, "('')");
		return s.replaceAll(XML_PATTERN, replacement);
	}

	/**
	 * 
	 * @param str
	 * @param replacement
	 * @return
	 */
	public static String filterBlank(final String str, final String replacement) {
		String s = str.replace("\r", replacement);
		s = s.replace("\n", replacement);
		s = s.replace("\t", replacement);
		s = s.replace("  ", " ");
		s = s.trim();
		return s;
	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public static String trimSql(final String sql) {
		final boolean hasScript = ScriptUtil.hasScript(sql);
		String s = sql.trim();
		s = ScriptUtil.trimScript(s);
		s = SqlUtil.filterBlank(s, " ");
		s = s.replace("\"", "\\\"");
		s = s.replace("  ", " ");
		s = s.trim();
		if (hasScript) {
			s = s.replace("<", LT);
			s = s.replace("&", AMP);
		}
		return s;
	}
}
