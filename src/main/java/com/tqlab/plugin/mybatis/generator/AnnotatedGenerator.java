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

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;
import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getSelectListPhrase;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import com.tqlab.plugin.mybatis.MybatisPluginException;

/**
 * @author John Lee
 * 
 */
public class AnnotatedGenerator extends AbstractJavaMapperMethodGenerator {

	private boolean useResultMapIfAvailable;

	public AnnotatedGenerator(boolean useResultMapIfAvailable) {
		super();

		this.useResultMapIfAvailable = useResultMapIfAvailable;
	}

	@Override
	public void addInterfaceElements(Interface interfaze) {

	}

	public void addMapperAnnotations(Interface interfaze, Method method,
			boolean needSetResult, DbSelectResult result, boolean hasScript,
			final String sql) {

		if (null != result) {
			this.prosessByResultConfig(interfaze, method, result, hasScript,
					sql);
			return;
		}

		String temp = sql.toLowerCase();
		if (temp.startsWith("delete")) {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.Delete")); //$NON-NLS-1$
			StringBuilder sb = new StringBuilder();
			method.addAnnotation("@Delete({"); //$NON-NLS-1$
			this.addScriptStart(hasScript, method);
			javaIndent(sb, 1);
			sb.append("\"");
			sb.append(sql.replace("\n", " "));
			sb.append("\"");
			javaIndent(sb, 1);
			method.addAnnotation(sb.toString());

			this.addScriptEnd(hasScript, method);
			method.addAnnotation("})"); //$NON-NLS-1$
			return;
		} else if (temp.startsWith("update")) {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.Update")); //$NON-NLS-1$
			StringBuilder sb = new StringBuilder();
			method.addAnnotation("@Update({"); //$NON-NLS-1$
			this.addScriptStart(hasScript, method);
			javaIndent(sb, 1);
			sb.append("\"");
			sb.append(sql.replace("\n", " "));
			sb.append("\"");
			javaIndent(sb, 1);
			method.addAnnotation(sb.toString());
			method.addAnnotation("})"); //$NON-NLS-1$
			return;
		} else if (temp.startsWith("insert")) {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.Insert")); //$NON-NLS-1$
			StringBuilder sb = new StringBuilder();
			method.addAnnotation("@Insert({"); //$NON-NLS-1$
			this.addScriptStart(hasScript, method);
			javaIndent(sb, 1);
			sb.append("\"");
			sb.append(sql.replace("\n", " "));
			sb.append("\"");
			javaIndent(sb, 1);
			method.addAnnotation(sb.toString());

			this.addScriptEnd(hasScript, method);
			method.addAnnotation("})"); //$NON-NLS-1$
			return;
		} else if (temp.startsWith("select") && !needSetResult) {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.Select")); //$NON-NLS-1$
			StringBuilder sb = new StringBuilder();
			method.addAnnotation("@Select({"); //$NON-NLS-1$
			this.addScriptStart(hasScript, method);

			String sqls[] = sql.split("\n");
			for (int i = 0; i < sqls.length; i++) {
				sb.setLength(0);
				javaIndent(sb, 1);
				sb.append("\"");
				sb.append(sqls[i].trim());
				sb.append("\"");
				if (i < sqls.length - 1) {
					sb.append(",");
				}
				javaIndent(sb, 1);
				method.addAnnotation(sb.toString());
			}

			this.addScriptEnd(hasScript, method);
			method.addAnnotation("})"); //$NON-NLS-1$
			return;
		}

		int index = temp.indexOf("from");
		String selectedStr = sql.substring(6, index).trim();

		interfaze.addImportedType(new FullyQualifiedJavaType(
				"org.apache.ibatis.annotations.Select")); //$NON-NLS-1$

		StringBuilder sb = new StringBuilder();
		method.addAnnotation("@Select({"); //$NON-NLS-1$
		this.addScriptStart(hasScript, method);
		javaIndent(sb, 1);
		sb.append("\"select\","); //$NON-NLS-1$
		method.addAnnotation(sb.toString());

		if (selectedStr.contains("*") && !"*".equals(selectedStr)) {
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append('"');
			sb.append(selectedStr.substring(0, selectedStr.indexOf('*')).trim());
			sb.append('"');
			sb.append(',');
			method.addAnnotation(sb.toString());

			selectedStr = selectedStr.substring(selectedStr.indexOf('*'))
					.trim();
		}

		//
		if ("*".equals(selectedStr)) {
			Iterator<IntrospectedColumn> iter = introspectedTable
					.getAllColumns().iterator();
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append('"');
			boolean hasColumns = false;
			while (iter.hasNext()) {
				sb.append(escapeStringForJava(getSelectListPhrase(iter.next())));
				hasColumns = true;

				if (iter.hasNext()) {
					sb.append(", "); //$NON-NLS-1$
				}

				if (sb.length() > 80) {
					sb.append("\","); //$NON-NLS-1$
					method.addAnnotation(sb.toString());

					sb.setLength(0);
					javaIndent(sb, 1);
					sb.append('"');
					hasColumns = false;
				}
			}

			if (hasColumns) {
				sb.append("\","); //$NON-NLS-1$
				method.addAnnotation(sb.toString());
			}
		} else {
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append('"');
			sb.append(selectedStr);
			sb.append("\","); //$NON-NLS-1$
			method.addAnnotation(sb.toString());
		}

		String tableName = "";
		temp = sql.substring(index + 4).trim();
		index = temp.indexOf(" ");
		boolean hasMore = false;
		if (index > 0) {
			tableName = temp.substring(0, index).trim();
			hasMore = true;
		} else {
			tableName = temp.trim();
			hasMore = false;
		}

		if (tableName.contains(";")) {
			tableName = tableName.substring(0, tableName.indexOf(";"));
		}

		if (!tableName.equalsIgnoreCase(introspectedTable
				.getAliasedFullyQualifiedTableNameAtRuntime())) {
			throw new MybatisPluginException("table name " + tableName
					+ " error.");
		}

		index = temp.indexOf("from");

		// ///////////
		sb.setLength(0);
		javaIndent(sb, 1);
		sb.append("\"from "); //$NON-NLS-1$
		sb.append(escapeStringForJava(introspectedTable
				.getAliasedFullyQualifiedTableNameAtRuntime()));
		if (hasMore) {
			sb.append("\","); //$NON-NLS-1$
		} else {
			sb.append("\"");
		}
		method.addAnnotation(sb.toString());

		// /////////////////////////////////////////////////////////////
		if (hasMore) {
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"");
			temp = sql.toLowerCase();
			index = temp.indexOf("from");
			temp = sql.substring(index + 4).trim();
			temp = temp.substring(tableName.length()).trim();
			sb.append(temp);
			sb.append("\"");
			method.addAnnotation(sb.toString());
		}

		this.addScriptEnd(hasScript, method);

		method.addAnnotation("})"); //$NON-NLS-1$

		temp = sql.toLowerCase().trim();
		temp = temp.substring(6, temp.indexOf("from")).trim();
		Set<String> selectedCloumns = new HashSet<String>();
		if (!temp.equals("*")) {
			String[] cloumnsArray = temp.toLowerCase().split(",");
			for (String s : cloumnsArray) {
				selectedCloumns.add(s.trim());
			}
		}

		if (useResultMapIfAvailable) {
			if (introspectedTable.getRules().generateBaseResultMap()
					|| introspectedTable.getRules()
							.generateResultMapWithBLOBs()) {
				addResultMapAnnotation(interfaze, method);
			} else {
				addAnnotatedResults(selectedCloumns, interfaze, method);
			}
		} else {
			addAnnotatedResults(selectedCloumns, interfaze, method);
		}
	}

	private void addResultMapAnnotation(Interface interfaze, Method method) {
		interfaze.addImportedType(new FullyQualifiedJavaType(
				"org.apache.ibatis.annotations.ResultMap")); //$NON-NLS-1$

		String annotation = String
				.format("@ResultMap(\"%s\")",
						introspectedTable.getRules()
								.generateResultMapWithBLOBs() ? introspectedTable
								.getResultMapWithBLOBsId() : introspectedTable
								.getBaseResultMapId());
		method.addAnnotation(annotation);
	}

	private void addAnnotatedResults(Set<String> selectedCloumns,
			Interface interfaze, Method method) {
		interfaze.addImportedType(new FullyQualifiedJavaType(
				"org.apache.ibatis.type.JdbcType")); //$NON-NLS-1$

		if (introspectedTable.isConstructorBased()) {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.Arg")); //$NON-NLS-1$
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.ConstructorArgs")); //$NON-NLS-1$
			method.addAnnotation("@ConstructorArgs({"); //$NON-NLS-1$
		} else {
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.Result")); //$NON-NLS-1$
			interfaze.addImportedType(new FullyQualifiedJavaType(
					"org.apache.ibatis.annotations.Results")); //$NON-NLS-1$
			method.addAnnotation("@Results({"); //$NON-NLS-1$
		}

		StringBuilder sb = new StringBuilder();

		Iterator<IntrospectedColumn> iterPk = introspectedTable
				.getPrimaryKeyColumns().iterator();
		Iterator<IntrospectedColumn> iterNonPk = introspectedTable
				.getNonPrimaryKeyColumns().iterator();
		while (iterPk.hasNext()) {
			IntrospectedColumn introspectedColumn = iterPk.next();
			sb.setLength(0);
			//
			if (!selectedCloumns.isEmpty()
					&& !selectedCloumns.contains(introspectedColumn
							.getActualColumnName().toLowerCase())) {
				continue;
			}

			javaIndent(sb, 1);
			sb.append(getResultAnnotation(interfaze, introspectedColumn, true,
					introspectedTable.isConstructorBased()));

			if (iterPk.hasNext() || iterNonPk.hasNext()) {
				sb.append(',');
			}

			method.addAnnotation(sb.toString());
		}

		while (iterNonPk.hasNext()) {

			IntrospectedColumn introspectedColumn = iterNonPk.next();
			sb.setLength(0);
			//
			if (!selectedCloumns.isEmpty()
					&& !selectedCloumns.contains(introspectedColumn
							.getActualColumnName().toLowerCase())) {
				continue;
			}
			javaIndent(sb, 1);
			sb.append(getResultAnnotation(interfaze, introspectedColumn, false,
					introspectedTable.isConstructorBased()));

			if (iterNonPk.hasNext()) {
				sb.append(',');
			}

			method.addAnnotation(sb.toString());
		}

		// bug fix
		// FIXME
		String s = method.getAnnotations().remove(
				method.getAnnotations().size() - 1);
		if (s.endsWith(",")) {
			s = s.substring(0, s.length() - 1);
		}
		method.getAnnotations().add(s);

		method.addAnnotation("})"); //$NON-NLS-1$
	}

	private void prosessByResultConfig(Interface interfaze, Method method,
			DbSelectResult result, boolean hasScript, String sql) {
		interfaze.addImportedType(new FullyQualifiedJavaType(
				"org.apache.ibatis.annotations.Select")); //$NON-NLS-1$

		StringBuilder sb = new StringBuilder();
		method.addAnnotation("@Select({"); //$NON-NLS-1$

		this.addScriptStart(hasScript, method);

		String sqls[] = sql.split("\n");
		for (int i = 0; i < sqls.length; i++) {
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"");
			sb.append(sqls[i].trim());
			sb.append("\"");
			if (i < sqls.length - 1) {
				sb.append(",");
			}
			javaIndent(sb, 1);
			method.addAnnotation(sb.toString());
		}

		this.addScriptEnd(hasScript, method);

		method.addAnnotation("})"); //$NON-NLS-1$

		interfaze.addImportedType(new FullyQualifiedJavaType(
				"org.apache.ibatis.annotations.Result")); //$NON-NLS-1$
		interfaze.addImportedType(new FullyQualifiedJavaType(
				"org.apache.ibatis.annotations.Results")); //$NON-NLS-1$
		method.addAnnotation("@Results({"); //$NON-NLS-1$
		List<DbColumn> list = result.getColumns();
		for (Iterator<DbColumn> i = list.iterator(); i.hasNext();) {
			sb.setLength(0);
			javaIndent(sb, 1);
			DbColumn column = i.next();
			sb.append(getResultAnnotation(interfaze, column.getName(),
					column.getJavaProperty()));
			if (i.hasNext()) {
				sb.append(',');
			}

			method.addAnnotation(sb.toString());
		}
		method.addAnnotation("})"); //$NON-NLS-1$
	}

	protected String getResultAnnotation(Interface interfaze, String column,
			String javaPropertyÏ) {
		StringBuilder sb = new StringBuilder();
		sb.append("@Result(column=\""); //$NON-NLS-1$
		sb.append(column);
		sb.append("\", property=\""); //$NON-NLS-1$
		sb.append(javaPropertyÏ);
		sb.append('\"');
		sb.append(')');

		return sb.toString();
	}

	private void addScriptStart(final boolean hasScript, Method method) {
		if (hasScript) {
			StringBuilder sb = new StringBuilder();
			javaIndent(sb, 1);
			sb.append("\"<script>\",");
			method.addAnnotation(sb.toString());
		}
	}

	private void addScriptEnd(final boolean hasScript, Method method) {
		if (hasScript) {
			StringBuilder sb = new StringBuilder();
			javaIndent(sb, 1);
			sb.append(",\"</script>\"");
			method.addAnnotation(sb.toString());
		}
	}
}
