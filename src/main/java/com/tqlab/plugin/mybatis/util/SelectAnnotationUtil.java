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
public class SelectAnnotationUtil {

	private static final Logger LOGGER = Logger
			.getLogger(SelectAnnotationUtil.class);

	/**
	 * 
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
			final IntrospectedTable introspectedTable, Interface interfaze,
			final Method method, final boolean hasScript, final String sql) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("table: "
					+ introspectedTable.getFullyQualifiedTableNameAtRuntime()
					+ ", sql: " + sql);
		}

		interfaze.addImportedType(new FullyQualifiedJavaType(
				"org.apache.ibatis.annotations.Select")); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		method.addAnnotation("@Select({"); //$NON-NLS-1$
		ScriptUtil.addScriptStart(hasScript, method);

		javaIndent(sb, 1);
		sb.append("\"");
		sb.append(sql); //$NON-NLS-1$
		sb.append("\"");

		method.addAnnotation(sb.toString());

		ScriptUtil.addScriptEnd(hasScript, method);
		method.addAnnotation("})"); // $NO
	}
}
