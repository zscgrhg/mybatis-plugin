/**
 * 
 */
package com.tqlab.plugin.mybatis.generator;

import java.io.File;
import java.io.Serializable;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.internal.types.JdbcTypeNameTranslator;

/**
 * @author John Lee
 * 
 */
public class SqlTemplateParserUtil {

	private static final String TABLE = "table";
	private static final String NAME = "name";
	private static final String OPERATION = "operation";
	private static final String ID = "id";
	private static final String MANY = "many";
	private static final String RESULT_TYPE = "resultType";
	private static final String RESULT = "result";
	private static final String PARAMS = "params";
	private static final String PARAM = "param";
	private static final String SQL = "sql";
	private static final String COMMNET = "comment";
	private static final String OBJECT_NAME = "objectName";
	private static final String SERIAL_VERSION_UID = "serialVersionUID";
	private static final String PROPERTY = "property";
	private static final String JAVA_TYPE = "javaType";
	private static final String JAVA_PROPERTY = "javaProperty";
	private static final String COLUMN = "column";

	public static DbTable parseDbTable(Context context, File file,
			Map<String, GeneratedJavaFile> maps) {
		try {
			SAXBuilder sb = new SAXBuilder();
			Document document = sb.build(file);
			Element rootElement = document.getRootElement();
			if (TABLE.equalsIgnoreCase(rootElement.getName())) {
				DbTable table = new DbTable();
				String name = rootElement.getAttributeValue(NAME);
				table.setName(name.toLowerCase());

				List<Element> list = rootElement.getChildren();
				for (Element e : list) {
					if (COLUMN.equalsIgnoreCase(e.getName())) {
						String columnName = e.getAttributeValue(NAME);
						String javaProperty = e
								.getAttributeValue(JAVA_PROPERTY);
						String columnJavaType = e.getAttributeValue(JAVA_TYPE);

						DbColumn column = new DbColumn();
						column.setJavaType(columnJavaType);
						column.setName(columnName);
						column.setJavaProperty(javaProperty);
						table.getColumns().add(column);
					} else if (RESULT.equalsIgnoreCase(e.getName())) {
						DbSelectResult dbSelectResult = parseDbSelectResult(e,
								null, context, maps);
						table.getSelectResults().add(dbSelectResult);
					}
				}

				for (Element e : list) {
					if (OPERATION.equalsIgnoreCase(e.getName())) {
						DbTableOperation operation = parseDbTableOperation(e,
								context, table.getSelectResults(), maps);
						if (null == operation) {
							continue;
						}
						table.getOperations().add(operation);
					}
				}
				return table;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Parse db table operation.
	 * 
	 * @param e
	 * @param context
	 * @param results
	 * @param maps
	 * @return
	 */
	private static DbTableOperation parseDbTableOperation(Element e,
			Context context, List<DbSelectResult> results,
			Map<String, GeneratedJavaFile> maps) {
		String id = e.getAttributeValue(ID);
		String many = e.getAttributeValue(MANY);
		String resultType = e.getAttributeValue(RESULT_TYPE);
		String sql = e.getChild(SQL).getTextTrim();
		Element result = e.getChild(RESULT);
		Element comment = e.getChild(COMMNET);
		Element params = e.getChild(PARAMS);
		if (null == id || null == sql) {
			return null;
		}

		DbTableOperation operation = new DbTableOperation();
		operation.setId(id);
		if (null != many) {
			operation.setMany(Boolean.parseBoolean(many));
		}
		operation.setSql(sql.replace("  ", " ").trim());
		operation.setResultType(resultType);

		if (null != comment) {
			operation.setComment(comment.getTextTrim());
		}

		if (null != result) {
			DbSelectResult dbSelectResult = parseDbSelectResult(result,
					resultType, context, maps);
			operation.setResult(dbSelectResult);
		} else if (null != resultType) {
			FullyQualifiedJavaType fullyQualifiedJavaType = getFullyQualifiedJavaType(
					context, resultType);
			for (DbSelectResult dbSelectResult : results) {
				if (dbSelectResult.getType().equals(fullyQualifiedJavaType)) {
					operation.setResult(dbSelectResult);
					break;
				}
			}
		}

		if (null != params) {
			List<Element> list = params.getChildren(PARAM);
			if (null != list) {
				for (Element el : list) {
					String s = el.getTextTrim();
					int index = s.indexOf('#');
					if (index >= 0) {
						s = s.substring(index + 2);
					} else {
						continue;
					}

					index = s.indexOf('}');
					String parameter = s.substring(0, index);
					String ss[] = parameter.split(",");
					String objectName = null;
					FullyQualifiedJavaType type = null;
					if (ss.length == 1) {
						objectName = s;
						type = getFullyQualifiedJavaType(null);
					} else {
						objectName = ss[0];
						type = getFullyQualifiedJavaType(parseJdbcTypeName(ss[1]));
					}
					DbParam dbParam = new DbParam();
					dbParam.setObjectName(objectName);
					dbParam.setType(type);
					operation.addParams(dbParam);
				}
			}
		}
		return operation;
	}

	/**
	 * Parse db select result
	 * 
	 * @param result
	 * @param resultType
	 * @param context
	 * @param maps
	 * @return
	 */
	private static DbSelectResult parseDbSelectResult(Element result,
			String resultType, Context context,
			Map<String, GeneratedJavaFile> maps) {
		String objectName = result.getAttributeValue(OBJECT_NAME);
		DbSelectResult dbSelectResult = new DbSelectResult();
		dbSelectResult.setObjectName(objectName);
		String basicPackage = context.getJavaModelGeneratorConfiguration()
				.getTargetPackage();
		if (null != objectName) {
			FullyQualifiedJavaType objectType = new FullyQualifiedJavaType(
					basicPackage + "." + objectName);

			dbSelectResult.setType(objectType);

			TopLevelClass topLevelClass = new TopLevelClass(objectType);

			FullyQualifiedJavaType serializable = new FullyQualifiedJavaType(
					Serializable.class.getName());
			topLevelClass.addImportedType(serializable);
			topLevelClass.addSuperInterface(serializable);

			Field field = new Field();
			field.setFinal(true);
			field.setInitializationString("1L"); //$NON-NLS-1$
			field.setName(SERIAL_VERSION_UID); //$NON-NLS-1$
			field.setStatic(true);
			field.setType(new FullyQualifiedJavaType("long")); //$NON-NLS-1$
			field.setVisibility(JavaVisibility.PRIVATE);
			topLevelClass.addField(field);

			DefaultJavaFormatter formatter = new DefaultJavaFormatter();
			formatter.setContext(context);
			GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
					topLevelClass, context.getJavaModelGeneratorConfiguration()
							.getTargetProject(), formatter);
			topLevelClass.setVisibility(JavaVisibility.PUBLIC);

			List<Element> properties = result.getChildren(PROPERTY);
			for (Element el : properties) {

				String javaType = el.getAttributeValue(JAVA_TYPE);
				String javaProperty = el.getAttributeValue(JAVA_PROPERTY);
				String column = el.getAttributeValue(COLUMN);
				processProperty(topLevelClass, column, javaProperty, javaType);
				dbSelectResult.addDbColumn(column, javaProperty, javaType);
			}
			maps.put(objectType.getFullyQualifiedName(), generatedJavaFile);
		}
		// //////
		else if (null != resultType && !isBasicType(resultType)) {
			FullyQualifiedJavaType fullyQualifiedJavaType = getFullyQualifiedJavaType(
					context, resultType);

			List<Element> properties = result.getChildren(PROPERTY);
			for (Element el : properties) {
				String javaType = el.getAttributeValue(JAVA_TYPE);
				String javaProperty = el.getAttributeValue(JAVA_PROPERTY);
				String column = el.getAttributeValue(COLUMN);
				dbSelectResult.addDbColumn(column, javaProperty, javaType);
			}
			dbSelectResult.setType(fullyQualifiedJavaType);
		}
		return dbSelectResult;
	}

	private static FullyQualifiedJavaType getFullyQualifiedJavaType(
			Context context, String resultType) {
		String basicPackage = context.getJavaModelGeneratorConfiguration()
				.getTargetPackage();
		String type = resultType;
		if (null != basicPackage && !"".equals(basicPackage)
				&& !resultType.startsWith(basicPackage)
				&& resultType.indexOf('.') == -1) {
			type = basicPackage + "." + resultType;
		}
		return new FullyQualifiedJavaType(type);
	}

	private static boolean isBasicType(String resultType) {
		return "java.lang.Integer".equals(resultType)
				|| "Integer".equalsIgnoreCase(resultType)
				|| "java.lang.Long".equals(resultType)
				|| "Long".equalsIgnoreCase(resultType)
				|| "java.lang.Float".equals(resultType)
				|| "Float".equalsIgnoreCase(resultType)
				|| "java.lang.Double".equals(resultType)
				|| "Double".equalsIgnoreCase(resultType)
				|| "java.lang.Boolean".equals(resultType)
				|| "Boolean".equalsIgnoreCase(resultType);
	}

	private static void processProperty(TopLevelClass topLevelClass,
			String cloumn, String javaProperty, String javaType) {

		FullyQualifiedJavaType _javaType = new FullyQualifiedJavaType(javaType);

		Field field = new Field();
		field.setName(javaProperty);
		field.setType(_javaType);
		field.setVisibility(JavaVisibility.PRIVATE);
		addFieldComment(field, cloumn);

		topLevelClass.addField(field);
		topLevelClass.addImportedType(javaType);

		Method method = new Method();
		String methodName = javaProperty.length() > 1 ? javaProperty.substring(
				0, 1).toUpperCase()
				+ javaProperty.substring(1) : javaProperty.toUpperCase();
		method.setName("set" + methodName);
		method.addParameter(new Parameter(_javaType, javaProperty));
		method.addBodyLine("this." + javaProperty + "=" + javaProperty + ";");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.setVisibility(JavaVisibility.PUBLIC);
		addGeneralMethodComment(method, cloumn);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setName("get" + methodName);
		method.setReturnType(_javaType);
		method.addBodyLine("return " + javaProperty + ";");
		method.setVisibility(JavaVisibility.PUBLIC);
		addGeneralMethodComment(method, cloumn);
		topLevelClass.addMethod(method);
	}

	private static void addFieldComment(Field field, String cloumn) {

		StringBuilder sb = new StringBuilder();

		field.addJavaDocLine("/**"); //$NON-NLS-1$
		field.addJavaDocLine(" * This field was generated by MyBatis Generator plugin (SqlTemplatePlugin)."); //$NON-NLS-1$

		sb.append(" * This field corresponds to the database table "); //$NON-NLS-1$
		sb.append(cloumn);
		field.addJavaDocLine(sb.toString());

		addJavadocTag(field, false);

		field.addJavaDocLine(" */"); //$NON-NLS-1$
	}

	private static void addGeneralMethodComment(Method method, String cloumn) {

		StringBuilder sb = new StringBuilder();

		method.addJavaDocLine("/**"); //$NON-NLS-1$
		method.addJavaDocLine(" * This method was generated by MyBatis Generator plugin (SqlTemplatePlugin)."); //$NON-NLS-1$

		sb.append(" * This method corresponds to the database table "); //$NON-NLS-1$
		sb.append(cloumn);
		method.addJavaDocLine(sb.toString());

		addJavadocTag(method, false);

		method.addJavaDocLine(" */"); //$NON-NLS-1$
	}

	public static void addJavadocTag(JavaElement javaElement,
			boolean markAsDoNotDelete) {
		javaElement.addJavaDocLine(" *"); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		sb.append(MergeConstants.NEW_ELEMENT_TAG);
		if (markAsDoNotDelete) {
			sb.append(" do_not_delete_during_merge"); //$NON-NLS-1$
		}
		javaElement.addJavaDocLine(sb.toString());
	}

	public static FullyQualifiedJavaType getFullyQualifiedJavaType(
			String jdbcTypeName) {
		if (null == jdbcTypeName || jdbcTypeName.trim().equals("")) {
			jdbcTypeName = "JAVA_OBJECT";
		}
		int type = JdbcTypeNameTranslator.getJdbcType(jdbcTypeName);
		switch (type) {
		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER: {
			return new FullyQualifiedJavaType("java.lang.Integer");
		}
		case Types.BIGINT: {
			return new FullyQualifiedJavaType("java.lang.Long");
		}
		case Types.CHAR: {
			return new FullyQualifiedJavaType("java.lang.Character");
		}
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.DOUBLE: {
			return new FullyQualifiedJavaType("java.lang.Double");
		}
		case Types.FLOAT: {
			return new FullyQualifiedJavaType("java.lang.Float");
		}
		case Types.VARCHAR:
		case Types.NVARCHAR:
		case Types.LONGVARCHAR:
		case Types.LONGNVARCHAR:
		case Types.BLOB: {
			return FullyQualifiedJavaType.getStringInstance();
		}
		case Types.BIT:
		case Types.BOOLEAN: {
			return FullyQualifiedJavaType.getBooleanPrimitiveInstance();
		}
		case Types.TIME:
		case Types.TIMESTAMP:
		case Types.DATE: {
			return FullyQualifiedJavaType.getDateInstance();
		}
		case Types.ARRAY: {
			return new FullyQualifiedJavaType("java.lang.List");
		}
		case Types.JAVA_OBJECT:
		default: {
			return new FullyQualifiedJavaType("java.lang.Object");
		}
		}
	}

	public static String parseJdbcTypeName(String str) {
		String jdbcTypeName = str.replace("jdbcType", "").replace("=", "")
				.trim();
		return jdbcTypeName;
	}
}
