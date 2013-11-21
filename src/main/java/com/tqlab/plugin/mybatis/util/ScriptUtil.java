/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

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

}
