/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

import java.util.Locale;

import org.codehaus.plexus.util.StringUtils;
import org.mybatis.generator.api.dom.java.Method;

/**
 * @author John Lee
 * 
 */
public final class ScriptUtil {

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
		if (StringUtils.isBlank(str)) {
			final String temp = str.toLowerCase(Locale.getDefault());
			result = temp.startsWith(Constants.SCRIPT_START);
		} else {
			result = false;
		}
		return result;
	}

}
