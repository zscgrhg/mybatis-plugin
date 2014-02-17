/**
 * 
 */
package com.tqlab.plugin.mybatis.generator;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Parameter;

/**
 * @author John Lee
 * 
 */
public class MybatisParameter extends Parameter {

	/**
	 * @param type
	 * @param name
	 */
	public MybatisParameter(FullyQualifiedJavaType type, String name) {
		super(type, name);
	}

	/**
	 * @param type
	 * @param name
	 * @param isVarargs
	 */
	public MybatisParameter(FullyQualifiedJavaType type, String name,
			boolean isVarargs) {
		super(type, name, isVarargs);
	}

	/**
	 * @param type
	 * @param name
	 * @param annotation
	 */
	public MybatisParameter(FullyQualifiedJavaType type, String name,
			String annotation) {
		super(type, name, annotation);
	}

	/**
	 * @param type
	 * @param name
	 * @param annotation
	 * @param isVarargs
	 */
	public MybatisParameter(FullyQualifiedJavaType type, String name,
			String annotation, boolean isVarargs) {
		super(type, name, annotation, isVarargs);
	}

	public void addAnnotation(String annotation) {
		if (null != annotation && !"".equals(annotation)) {
			super.addAnnotation(annotation);
		}
	}
}
