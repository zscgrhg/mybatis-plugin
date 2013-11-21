package com.tqlab.plugin.mybatis.util;

import org.mybatis.generator.api.dom.java.Interface;

/**
 * 
 * @author John Lee
 * 
 */
public class ResultAnnotationUtil {

	/**
	 * 
	 * @param interfaze
	 * @param column
	 * @param javaProperty
	 * @return
	 */
	public static String getResultAnnotation(Interface interfaze,
			String column, String javaProperty) {
		StringBuilder sb = new StringBuilder();
		sb.append("@Result(column=\""); //$NON-NLS-1$
		sb.append(column);
		sb.append("\", property=\""); //$NON-NLS-1$
		sb.append(javaProperty);
		sb.append('\"');
		sb.append(')');

		return sb.toString();
	}
}
