/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tqlab.plugin.mybatis.generator;

/**
 * @author John Lee
 * 
 */
public class MybatisBean {

	private static final String DEFAULT_SQL_SESSION_FACTORY = "sqlSessionFactory";
	private String beanId;
	private String beanName;
	private String classPath;
	private String sqlSessionFactory;

	/**
	 * @return the beanId
	 */
	public final String getBeanId() {
		return beanId;
	}

	/**
	 * @return the beanName
	 */
	public final String getBeanName() {
		return beanName;
	}

	/**
	 * @return the classPath
	 */
	public final String getClassPath() {
		return classPath;
	}

	/**
	 * @param beanId
	 *            the beanId to set
	 */
	public final void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	/**
	 * @param beanName
	 *            the beanName to set
	 */
	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * @param classPath
	 *            the classPath to set
	 */
	public final void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	/**
	 * @return the sqlSessionFactory
	 */
	public final String getSqlSessionFactory() {
		if (null == sqlSessionFactory) {
			return DEFAULT_SQL_SESSION_FACTORY;
		}
		return sqlSessionFactory;
	}

	/**
	 * @param sqlSessionFactory
	 *            the sqlSessionFactory to set
	 */
	public final void setSqlSessionFactory(String sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("");
		buf.append("\t<bean id=\"" + beanId
				+ "\" class=\"org.mybatis.spring.mapper.MapperFactoryBean\">");
		buf.append("\r\n");
		buf.append("\t\t<property name=\"mapperInterface\" value=\""
				+ classPath + "\" />");
		buf.append("\r\n");
		buf.append("\t\t<property name=\"sqlSessionFactory\" ref=\""
				+ getSqlSessionFactory() + "\" />");
		buf.append("\r\n");
		buf.append("\t</bean>");
		return buf.toString();
	}

	public String toOsgiServiceString() {
		StringBuffer buf = new StringBuffer("");
		buf.append("\t<osgi:service ");
		buf.append("interface=\"" + classPath + "\" ref=\"" + beanId + "\" ");
		buf.append("auto-export=\"interfaces\" />");
		return buf.toString();
	}
}
