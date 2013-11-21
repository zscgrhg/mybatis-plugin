/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

/**
 * <p>
 * Update, Insert, Delete sql annotation util.
 * </p>
 * 
 * @author John Lee
 * 
 */
public class CommonAnnotationUtil {

	/**
	 * 
	 * @param interfaze
	 * @param method
	 * @param hasScript
	 * @param sql
	 * @param clazz
	 */
	public static void addAnnotation(final Interface interfaze,
			final Method method, final boolean hasScript, final String sql,
			Class<?> clazz) {
		interfaze.addImportedType(new FullyQualifiedJavaType(clazz.getName())); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		method.addAnnotation("@" + clazz.getSimpleName() + "({"); //$NON-NLS-1$
		ScriptUtil.addScriptStart(hasScript, method);
		javaIndent(sb, 1);
		sb.append("\"");
		sb.append(sql.replace("\n", " "));
		sb.append("\"");
		javaIndent(sb, 1);
		method.addAnnotation(sb.toString());
		ScriptUtil.addScriptEnd(hasScript, method);
		method.addAnnotation("})"); //$NON-NLS-1$
	}
}
