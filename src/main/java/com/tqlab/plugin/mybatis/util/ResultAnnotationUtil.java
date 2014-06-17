package com.tqlab.plugin.mybatis.util;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.dom.java.Interface;

/**
 * 
 * @author John Lee
 * 
 */
public final class ResultAnnotationUtil {

	private ResultAnnotationUtil() {

	}

	/**
	 * 
	 * @param interfaze
	 * @param column
	 * @param javaProperty
	 * @param jdbcType
	 * @return
	 */
	public static String getResultAnnotation(final Interface interfaze,
			final String column, final String javaProperty,
			final String jdbcType) {
		final StringBuilder buf = new StringBuilder(64);
		buf.append("@Result(column=\""); //$NON-NLS-1$
		buf.append(column);
		buf.append("\", property=\""); //$NON-NLS-1$
		buf.append(javaProperty);
		buf.append((char) '"');
		if (StringUtils.isNotBlank(jdbcType)) {
			buf.append(", jdbcType=");
			buf.append(jdbcType);
		}
		buf.append(')');

		return buf.toString();
	}
}
