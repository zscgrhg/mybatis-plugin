/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

import org.codehaus.plexus.util.StringUtils;
import org.mybatis.generator.api.dom.java.Method;

/**
 * @author John Lee
 * 
 */
public class ScriptUtil {

	/**
	 * 
	 * @param hasScript
	 * @param method
	 */
	public static void addScriptStart(final boolean hasScript,
			final Method method) {
		if (hasScript) {
			StringBuilder sb = new StringBuilder();
			javaIndent(sb, 1);
			sb.append("\"<script>\",");
			method.addAnnotation(sb.toString());
		}
	}

	/**
	 * 
	 * @param hasScript
	 * @param method
	 */
	public static void addScriptEnd(final boolean hasScript, final Method method) {
		if (hasScript) {
			StringBuilder sb = new StringBuilder();
			javaIndent(sb, 1);
			sb.append(",\"</script>\"");
			method.addAnnotation(sb.toString());
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String trimScript(final String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}

		String result = str;
		String s = str.toLowerCase();
		if (s.startsWith("<script>")) {
			result = str.substring(8);
		}

		if (s.endsWith("</script>")) {
			result = result.substring(0, result.length() - 9);
		}
		return result;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static final boolean hasScript(final String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		return str.toLowerCase().startsWith("<script>");
	}

}
