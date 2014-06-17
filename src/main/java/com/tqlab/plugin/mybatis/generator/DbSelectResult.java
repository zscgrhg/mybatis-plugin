package com.tqlab.plugin.mybatis.generator;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

public class DbSelectResult {

	private String objectName;
	private FullyQualifiedJavaType type;
	private List<DbColumn> list = new ArrayList<DbColumn>();

	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName
	 *            the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return the type
	 */
	public FullyQualifiedJavaType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(FullyQualifiedJavaType type) {
		this.type = type;
	}

	/**
	 * Get selected result columns.
	 * 
	 * @return
	 */
	public List<DbColumn> getColumns() {
		return list;
	}

	/**
	 * 
	 * @param name
	 * @param javaProperty
	 * @param javaType
	 * @param jdbcType
	 */
	public void addDbColumn(String name, String javaProperty, String javaType,
			String jdbcType) {
		DbColumn dbColumn = new DbColumn();
		dbColumn.setName(name);
		dbColumn.setJavaType(javaType);
		dbColumn.setJavaProperty(javaProperty);
		dbColumn.setJdbcType(jdbcType);
		this.list.add(dbColumn);
	}
}
