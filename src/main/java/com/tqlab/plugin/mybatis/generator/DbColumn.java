/**
 * 
 */
package com.tqlab.plugin.mybatis.generator;

/**
 * @author John Lee
 * 
 */
public class DbColumn {

	private String name;
	private String javaProperty;
	private String javaType;

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the javaProperty
	 */
	public final String getJavaProperty() {
		return javaProperty;
	}

	/**
	 * @param javaProperty the javaProperty to set
	 */
	public final void setJavaProperty(String javaProperty) {
		this.javaProperty = javaProperty;
	}

	/**
	 * @return the javaType
	 */
	public final String getJavaType() {
		return javaType;
	}

	/**
	 * @param javatype
	 *            the javaType to set
	 */
	public final void setJavaType(String javaType) {
		this.javaType = javaType;
	}

}
