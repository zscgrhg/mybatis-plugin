/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

import java.util.regex.Pattern;

/**
 * @author John Lee
 * 
 */
public final class SqlUtil {

	/**
	 * <xxxx /> or <xxxx>fff</xxxx>
	 */
	private static final String XML_PATTERN = "((<[^>]+>[^<]*</[^>]+>)|(<[^<]*/>))";

	/**
	 * <pre>
	 * <foreach item="item" index="index" collection="list" open="(" separator="," close=")"> #{item} 
	 * </ foreach>
	 * </pre>
	 */
	private static final String FOREACH_PATTERN = "<(foreach)([\\sa-zA-Z0-9=\"#{}(),_\\-\\\\])*>([\\sa-zA-Z0-9=\"#{}(),_\\-\\\\])*</(foreach)>";

	private static final String SELECT_KEY_PATTERN = "<(selectKey)([\\sa-zA-Z0-9=\"#{}(),_\\-\\\\])*>([\\sa-zA-Z0-9=\"#{}(),_\\-\\\\])*</(selectKey)>";
	/**
	 * Such as #{key,jdbcType=BIGINT}
	 */
	private static final String PARAM_PATTERN = "#\\{[a-zA-Z,=\\-\\_\\s]+\\}";

	/**
	 * TOP #{size,jdbcType=BIGINT}
	 */
	private static final String TOP_PATTERN = "(top)(\\s+" + PARAM_PATTERN
			+ ")";

	/**
	 * Limit 0, #{size,jdbcType=BIGINT}
	 */
	private static final String LIMIT_PATTERN = "(limit)(\\s*(\\d+|"
			+ PARAM_PATTERN + ")\\s*(,|offset)\\s*)?(\\s*(\\d+|"
			+ PARAM_PATTERN + "))";

	private static final String LT = "&lt;";
	private static final String AMP = "&amp;";

	private static final String[] DYNAMIC_KEYS = { "<if", "<choose", "<when",
			"<otherwise", "<where", "<trim", "<set", "<foreach", "<bind",
			"<selectKey", "</" };

	private SqlUtil() {

	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public static String filterSql(final String sql) {
		String mybatisSql = filterXml(sql, " ");
		mybatisSql = replaceAll(TOP_PATTERN, mybatisSql, "TOP 1 ");
		mybatisSql = replaceAll(LIMIT_PATTERN, mybatisSql, "LIMIT 1 ");
		mybatisSql = replaceAll(PARAM_PATTERN, mybatisSql, "TQLAB ");
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

		String s = replaceAll(FOREACH_PATTERN, str, "('')");
		s = replaceAll(SELECT_KEY_PATTERN, s, "");
		s = doFilterXml(s, replacement);

		return s;
	}

	private static String doFilterXml(final String str, final String replacement) {
		String s = str;
		while (Pattern.compile(XML_PATTERN).matcher(s).find()) {
			s = replaceAll(XML_PATTERN, s, replacement);
		}
		return s;
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
		String s = sql.trim();
		s = ScriptUtil.trimScript(s);
		s = SqlUtil.filterBlank(s, " ");
		s = s.replace("\"", "\\\"");
		s = s.replace("  ", " ");
		s = s.trim();
		return s;
	}

	/**
	 * 
	 * @param sql
	 * @param hasScript
	 * @return
	 */
	public static String pdataFilter(final String sql, boolean hasScript) {
		String s = sql.trim();
		if (hasScript) {
			// s = s.replace("&", AMP);
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '<') {
					if (i == s.length() - 1) {
						buf.append(LT);
					} else {
						String sTmp = s.substring(i);
						if (isDynamicKeys(sTmp)) {
							int index = sTmp.indexOf('>');
							if (index > 0) {
								i += index;
								buf.append(sTmp.substring(0, index + 1));
							} else {
								buf.append(sTmp);
								break;
							}
						} else {
							buf.append(LT);
						}
					}
				} else if (c == '&') {
					//
					if (i + 3 < s.length()) {
						//
						String tmp = s.substring(i, i + 4);
						if (tmp.equals("&lt;") || tmp.equals("&gt;")) {
							buf.append(c);
							continue;
						} else {
							buf.append(AMP);
						}
					} else {
						buf.append(c);
					}
				} else {
					buf.append(c);
				}
			}
			s = buf.toString();
		}
		return s;
	}

	public static String sql(String sql) {
		String tmp = sql.replace("&lt;", "<");
		tmp = tmp.replace("&gt;", ">");
		return tmp;
	}

	private static boolean isDynamicKeys(String s) {
		for (String key : DYNAMIC_KEYS) {
			if (s.startsWith(key)) {
				return true;
			}
		}
		return false;
	}

	private static String replaceAll(String pattern, String str,
			String replacement) {
		return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(str)
				.replaceAll(replacement);
	}
}
