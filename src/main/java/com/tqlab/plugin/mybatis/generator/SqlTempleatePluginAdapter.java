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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;

import com.tqlab.plugin.mybatis.generator.config.Config;
import com.tqlab.plugin.mybatis.util.SqlTemplateParserUtil;

/**
 * @author John Lee
 * 
 */
public class SqlTempleatePluginAdapter extends PluginAdapter {

	private static final String CACHE_NAMESPACE_FQN = "org.apache.ibatis.annotations.CacheNamespace";
	private static final String SQL_TEMPLATE_PATH = "sql.template.path";
	private static final String WITH_XML = ".xml";
	private static final String XML_PATTERN = "\\<.*?/>|\\<.*?>.*\\<\\/.*>";

	private Map<String, DbTable> map = new HashMap<String, DbTable>();
	private Map<String, GeneratedJavaFile> maps = new HashMap<String, GeneratedJavaFile>();

	private Config config;

	public void setContext(Context context) {
		super.setContext(context);
	}

	public void setProperties(Properties properties) {
		super.setProperties(properties);

		String sqlTemplatePath = properties.getProperty(SQL_TEMPLATE_PATH);

		if (null == sqlTemplatePath) {
			return;
		}

		File sqlTemplateDir = new File(sqlTemplatePath);
		if (!sqlTemplateDir.exists()) {
			return;
		}

		File[] files = sqlTemplateDir.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(WITH_XML)) {
				DbTable dbTable = SqlTemplateParserUtil.parseDbTable(context,
						file, maps);
				if (null != dbTable) {
					map.put(dbTable.getName(), dbTable);
				}
			}
		}
	}

	@Override
	public boolean validate(List<String> warnings) {
		if (config == null)
			config = new Config(getProperties());
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// Check use cache or not
		this.checkCache(interfaze);

		String tableName = introspectedTable
				.getAliasedFullyQualifiedTableNameAtRuntime();
		DbTable dbTable = this.map.get(tableName.toLowerCase());
		if (null == dbTable) {
			return false;
		}

		FullyQualifiedJavaType parameterType = introspectedTable.getRules()
				.calculateAllFieldsClass();
		interfaze.addImportedType(parameterType);

		AnnotatedJavaMapperMethodGenerator generator = new AnnotatedJavaMapperMethodGenerator(
				false);
		generator.setContext(context);
		generator.setIntrospectedTable(introspectedTable);

		for (DbTableOperation operation : dbTable.getOperations()) {
			Method method = new Method();
			method.setReturnType(getReturnFullyQualifiedJavaType(operation,
					interfaze, introspectedTable, parameterType));
			method.setVisibility(JavaVisibility.PUBLIC);
			method.setName(operation.getId());
			String[] comments = operation.getComment() == null ? null
					: operation.getComment().split("\n");
			addGeneralMethodComment(method, introspectedTable, comments);
			Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

			String sql = operation.getSql();
			boolean hasScript = sql.contains("<script>");
			if (hasScript) {
				sql = sql.replace("<script>", "");
				sql = sql.replace("</script>", "");
			}

			sql = sql.replace("\r", " ");
			sql = sql.replace("\n", " ");
			sql = sql.replace("\t", " ");
			sql = sql.replace("\"", "\\\"");
			sql = sql.replace("  ", " ");
			sql = sql.trim();
			List<Parameter> list = this.parseSqlParameter(sql,
					operation.getParams(), interfaze, introspectedTable);

			for (Parameter p : list) {
				method.addParameter(p); //$NON-NLS-1$
				importedTypes.add(p.getType());
			}

			generator.addMapperAnnotations(interfaze, method,
					null == operation.getResultType(), operation.getResult(),
					hasScript, sql);
			interfaze.addMethod(method);
			interfaze.addImportedTypes(importedTypes);

			if (null != operation.getResult()) {
				interfaze.addImportedType(operation.getResult().getType());
			}
		}

		return true;
	}

	private void checkCache(Interface interfaze) {
		// Check use cache or not
		if (!config.isUseCache()) {
			return;
		}

		String cacheValue = config.getCacheValue(interfaze.getType()
				.getFullyQualifiedName());
		if (cacheValue != null) {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					CACHE_NAMESPACE_FQN));

			StringBuilder sb = new StringBuilder();
			sb.append("@CacheNamespace(\n").append(cacheValue).append("\n)");
			interfaze.addAnnotation(sb.toString());
		}
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {

		List<GeneratedJavaFile> list = new ArrayList<GeneratedJavaFile>();
		Set<Map.Entry<String, GeneratedJavaFile>> set = maps.entrySet();
		for (Iterator<Map.Entry<String, GeneratedJavaFile>> i = set.iterator(); i
				.hasNext();) {
			Map.Entry<String, GeneratedJavaFile> e = i.next();
			list.add(e.getValue());
		}
		return list;
	}

	private FullyQualifiedJavaType getReturnFullyQualifiedJavaType(
			DbTableOperation operation, Interface interfaze,
			IntrospectedTable introspectedTable,
			FullyQualifiedJavaType parameterType) {
		//
		if (null != operation.getResultType()) {
			return this.getSelect(operation, interfaze,
					operation.getResultType());
		}
		String sql = operation.getSql().toLowerCase().trim();
		boolean hasScript = sql.contains("<script>");
		if (hasScript) {
			sql = sql.replace("<script>", "");
			sql = sql.replace("</script>", "");
		}

		sql = sql.replaceAll(XML_PATTERN, "").trim();

		if (sql.startsWith("delete") || sql.startsWith("update")
				|| sql.startsWith("insert")) {
			return FullyQualifiedJavaType.getIntInstance();
		} else if (!sql.startsWith("select")) {
			throw new RuntimeException(sql + " not supported.");
		}

		DbSelectResult result = operation.getResult();
		// If you specify a result type
		if (result != null) {
			return this.getSelect(operation, interfaze, result.getType()
					.getShortName());
		}
		//
		sql = sql.substring(6, sql.indexOf("from")).trim();
		boolean hasSelectedBLOB = this.hasSelectedBLOB(introspectedTable, sql);
		String name = parameterType.getShortName();
		if (!hasSelectedBLOB && introspectedTable.hasBLOBColumns()) {
			name = name.substring(0, name.length() - 9); // delete BLOBs
		}
		return this.getSelect(operation, interfaze, name);
	}

	private boolean hasSelectedBLOB(final IntrospectedTable introspectedTable,
			final String sql) {

		if (!introspectedTable.hasBLOBColumns()) {
			return false;
		}
		//
		boolean hasSelectedBLOB = false;
		// select all
		if (sql.equals("*")) {
			hasSelectedBLOB = true;
		} else {
			String[] cloumnsArray = sql.split(",");
			Set<String> cloumns = new HashSet<String>();
			for (String s : cloumnsArray) {
				cloumns.add(s.trim());
			}
			List<IntrospectedColumn> list = introspectedTable.getBLOBColumns();
			for (IntrospectedColumn c : list) {
				if (cloumns.contains(c.getActualColumnName().toLowerCase())) {
					hasSelectedBLOB = true;
					break;
				}
			}
		}
		return hasSelectedBLOB;
	}

	private FullyQualifiedJavaType getSelect(DbTableOperation operation,
			Interface interfaze, String name) {
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(name);
		interfaze.addImportedType(type);
		if (operation.isMany()) {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"java.util.List")); //$NON-NLS-1$
			String s = "java.util.List<" + name + ">";
			return new FullyQualifiedJavaType(s);
		} else {
			return type;
		}
	}

	private List<Parameter> parseSqlParameter(final String sql,
			List<DbParam> params, Interface interfaze,
			IntrospectedTable introspectedTable) {
		List<Parameter> result = new ArrayList<Parameter>();
		for (DbParam param : params) {
			result.add(getParameter(param.getType(), param.getObjectName()));
		}

		//
		String sqlTemp = new String(sql);
		List<String> list = new ArrayList<String>();
		parseSqlParameter(list, sqlTemp);
		for (String param : list) {
			String s[] = param.split(",");
			FullyQualifiedJavaType type = null;
			if (s.length == 1) {
				// type = FullyQualifiedJavaType.getStringInstance();
				continue;
			} else {
				String jdbcTypeName = SqlTemplateParserUtil
						.parseJdbcTypeName(s[1]);
				type = SqlTemplateParserUtil
						.getFullyQualifiedJavaType(jdbcTypeName);
			}
			result.add(getParameter(type, s[0]));
		}

		if (result.size() > 0) {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.Param")); //$NON-NLS-1$
		}
		return result;
	}

	private Parameter getParameter(FullyQualifiedJavaType type, String name) {
		return new Parameter(type, name, "@Param(\"" + name + "\")");
	}

	private List<String> parseSqlParameter(List<String> list, String sql) {
		if (null == list) {
			list = new ArrayList<String>();
		}
		try {
			int index = sql.indexOf('#');
			if (index >= 0) {
				sql = sql.substring(index + 2);
			} else {
				return list;
			}

			index = sql.indexOf('}');
			String parameter = sql.substring(0, index);
			if (!list.contains(parameter)) {
				list.add(parameter);
			}
			parseSqlParameter(list, sql);
		} catch (Exception e) {

		}

		return list;
	}

	private void addGeneralMethodComment(Method method,
			IntrospectedTable introspectedTable, String... comments) {

		StringBuilder sb = new StringBuilder();

		method.addJavaDocLine("/**"); //$NON-NLS-1$
		method.addJavaDocLine(" * This method was generated by MyBatis Generator."); //$NON-NLS-1$

		sb.append(" * This method corresponds to the database table "); //$NON-NLS-1$
		sb.append(introspectedTable.getFullyQualifiedTable());
		method.addJavaDocLine(sb.toString());

		if (null != comments) {
			for (String comment : comments) {
				sb.setLength(0);
				sb.append(" * ");
				sb.append(comment.trim());
				method.addJavaDocLine(sb.toString()); //$NON-NLS-1$
			}
		}

		SqlTemplateParserUtil.addJavadocTag(method, false);

		method.addJavaDocLine(" */"); //$NON-NLS-1$
	}

	@Override
	public boolean modelFieldGenerated(Field field,
			TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable,
			Plugin.ModelClassType modelClassType) {

		FullyQualifiedJavaType javaType = this.getJavaType(topLevelClass,
				introspectedColumn, introspectedTable);
		if (null != javaType) {
			field.setType(javaType);
		}
		return true;
	}

	@Override
	public boolean modelGetterMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable,
			Plugin.ModelClassType modelClassType) {
		FullyQualifiedJavaType javaType = this.getJavaType(topLevelClass,
				introspectedColumn, introspectedTable);
		if (null != javaType) {
			method.setReturnType(javaType);
		}
		return true;
	}

	@Override
	public boolean modelSetterMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable,
			Plugin.ModelClassType modelClassType) {
		FullyQualifiedJavaType javaType = this.getJavaType(topLevelClass,
				introspectedColumn, introspectedTable);
		if (null != javaType) {
			Parameter p = method.getParameters().get(0);
			Parameter parameter = new Parameter(javaType, p.getName(),
					p.isVarargs());
			parameter.getAnnotations().addAll(p.getAnnotations());
			method.getParameters().clear();
			method.getParameters().add(parameter);
		}
		return true;
	}

	private FullyQualifiedJavaType getJavaType(TopLevelClass topLevelClass,
			IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable) {

		String tableName = introspectedTable
				.getAliasedFullyQualifiedTableNameAtRuntime();

		DbTable dbTable = this.map.get(tableName.toLowerCase());
		if (null == dbTable) {
			return null;
		}
		List<DbColumn> columns = dbTable.getColumns();
		if (null == columns || columns.size() == 0) {
			return null;
		}
		for (DbColumn column : columns) {
			if (introspectedColumn.getActualColumnName().equalsIgnoreCase(
					column.getName())) {
				FullyQualifiedJavaType javaType = new FullyQualifiedJavaType(
						column.getJavaType());
				Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
				importedTypes.add(javaType);
				topLevelClass.addImportedTypes(importedTypes);
				return javaType;
			}
		}
		return null;
	}
}