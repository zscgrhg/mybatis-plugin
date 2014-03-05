/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.dom.java.Method;

/**
 * @author John Lee
 * 
 */
public final class ScriptUtil {

	private static final String BIND_REGEX = "(<bind)\\s*(name=\")[a-zA-Z0-9_]+(\")\\s*((value=\")([a-zA-Z'%-_+\\s])*(\")\\s*)(/>)";
	private static final String NAME_REGEX = "(name=\")[a-zA-Z0-9_]+\"";

	private ScriptUtil() {

	}

	/**
	 * 
	 * @param hasScript
	 * @param method
	 */
	public static void addScriptStart(final boolean hasScript,
			final Method method) {
		if (hasScript) {
			final StringBuilder buf = new StringBuilder();
			javaIndent(buf, 1);
			buf.append(Constants.QUOTE);
			buf.append(Constants.SCRIPT_START);
			buf.append(Constants.QUOTE);
			buf.append(Constants.COMMA);
			method.addAnnotation(buf.toString());
		}
	}

	/**
	 * 
	 * @param hasScript
	 * @param method
	 */
	public static void addScriptEnd(final boolean hasScript, final Method method) {
		if (hasScript) {
			final StringBuilder buf = new StringBuilder();
			javaIndent(buf, 1);
			buf.append(Constants.COMMA);
			buf.append(Constants.QUOTE);
			buf.append(Constants.SCRIPT_END);
			buf.append(Constants.QUOTE);
			method.addAnnotation(buf.toString());
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String trimScript(final String str) {
		String result = str;
		if (!StringUtils.isBlank(result)) {

			final String temp = str.toLowerCase(Locale.getDefault());
			if (temp.startsWith(Constants.SCRIPT_START)) {
				result = result.substring(8);
			}

			if (temp.endsWith(Constants.SCRIPT_END)) {
				result = result.substring(0, result.length() - 9);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean hasScript(final String str) {
		boolean result;
		if (StringUtils.isNotBlank(str)) {
			final String temp = str.toLowerCase(Locale.getDefault()).trim();
			result = temp.startsWith(Constants.SCRIPT_START);
		} else {
			result = false;
		}
		return result;
	}

	public static Set<String> getBindNames(final String sql) {
		Set<String> result = new HashSet<String>();
		Pattern p = Pattern.compile(BIND_REGEX);
		Matcher matcher = p.matcher(sql);
		while (matcher.find()) {
			String s = matcher.group();
			Pattern p2 = Pattern.compile(NAME_REGEX);
			Matcher matcher2 = p2.matcher(s);
			if (matcher2.find()) {
				String s2 = matcher2.group();
				String name = s2.substring(s2.indexOf('"') + 1,
						s2.lastIndexOf('"'));
				result.add(name);
			}
		}
		return result;
	}
}
