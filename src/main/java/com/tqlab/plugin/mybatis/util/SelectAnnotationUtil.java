/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

import org.apache.log4j.Logger;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

import com.tqlab.plugin.mybatis.generator.GeneratorCallback;

/**
 * @author John Lee
 * 
 */
public final class SelectAnnotationUtil {

	private static final Logger LOGGER = Logger
			.getLogger(SelectAnnotationUtil.class);

	private SelectAnnotationUtil() {

	}

	/**
	 * @param introspectedTable
	 * @param interfaze
	 * @param method
	 * @param hasScript
	 * @param sql
	 * @param generator
	 */
	public static void addSelectAnnotation(
			final IntrospectedTable introspectedTable,
			final Interface interfaze, final Method method,
			final boolean hasScript, final String sql,
			final GeneratorCallback generator) {
		doSelectAnnotation(introspectedTable, interfaze, method, hasScript, sql);
		generator.addAnnotatedResults(interfaze, method);
	}

	/**
	 * 
	 * @param introspectedTable
	 * @param interfaze
	 * @param method
	 * @param hasScript
	 * @param sql
	 */
	private static void doSelectAnnotation(
			final IntrospectedTable introspectedTable,
			final Interface interfaze, final Method method,
			final boolean hasScript, final String sql) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("table: "
					+ introspectedTable.getFullyQualifiedTableNameAtRuntime()
					+ ", sql: " + sql);
		}

		interfaze.addImportedType(new FullyQualifiedJavaType(
				"org.apache.ibatis.annotations.Select")); //$NON-NLS-1$
		final StringBuilder buf = new StringBuilder(256);
		method.addAnnotation("@Select({"); //$NON-NLS-1$
		ScriptUtil.addScriptStart(hasScript, method);

		javaIndent(buf, 1);
		buf.append(Constants.QUOTE);
		buf.append(SqlUtil.pdataFilter(sql, hasScript)); //$NON-NLS-1$
		buf.append(Constants.QUOTE);

		method.addAnnotation(buf.toString());

		ScriptUtil.addScriptEnd(hasScript, method);
		method.addAnnotation("})"); // $NO
	}
}
