/**
 * 
 */
package com.tqlab.plugin.mybatis.generator;

/**
 * @author John Lee
 * 
 */
public class DbOption {

	private String name;
	private String value;

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
	 * @return the value
	 */
	public final String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public final void setValue(String value) {
		this.value = value;
	}

}
