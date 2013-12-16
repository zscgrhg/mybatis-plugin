/**
 * 
 */
package com.tqlab.plugin.mybatis.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
import org.xml.sax.SAXException;

import com.tqlab.plugin.mybatis.generator.DbColumn;
import com.tqlab.plugin.mybatis.generator.DbParam;
import com.tqlab.plugin.mybatis.generator.DbSelectResult;
import com.tqlab.plugin.mybatis.generator.DbTable;
import com.tqlab.plugin.mybatis.generator.DbTableOperation;
import com.tqlab.plugin.mybatis.generator.SimpleErrorHandler;

/**
 * @author John Lee
 * 
 */
public final class SqlTemplateParserUtil {

	private static final Logger LOGGER = Logger
			.getLogger(SqlTemplateParserUtil.class);

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

	private static final String MYBATIS_XSD_LOCAL = "/com/tqlab/plugin/mybatis/tqlab-mybatis-plugin.xsd";
	private static final String MYBATIS_XSD_REMOTE = "http://schema.tqlab.com/mybatis/tqlab-mybatis-plugin.xsd";
	private static final String FEATURE = "http://apache.org/xml/features/validation/schema";

	private SqlTemplateParserUtil() {

	}

	private static SAXReader reader;

	private static SAXReader getSAXReader() throws SAXException,
			ParserConfigurationException {
		if (null != reader) {
			return reader;
		}
		// Check remote xsd file exit or not
		InputStream is = null;
		try {
			URL url = new URL(MYBATIS_XSD_REMOTE);
			is = url.openStream();
		} catch (IOException e) {
			is = null;
			LOGGER.warn("Read file: " + MYBATIS_XSD_REMOTE + " error.");
		}
		SAXParserFactory factory = SAXParserFactory.newInstance();
		if (null == is) {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source sources[] = new Source[] { new StreamSource(
					SqlTemplateParserUtil.class
							.getResourceAsStream(MYBATIS_XSD_LOCAL)) };
			factory.setSchema(schemaFactory.newSchema(sources));
			SAXParser parser = factory.newSAXParser();
			reader = new SAXReader(parser.getXMLReader());
			reader.setValidation(false);
		} else {
			factory.setValidating(true);
			SAXParser parser = factory.newSAXParser();
			reader = new SAXReader(parser.getXMLReader());
			reader.setValidation(true);
			// request XML Schema validation
			reader.setFeature(FEATURE, true);
		}
		reader.setErrorHandler(new SimpleErrorHandler());
		return reader;
	}

	@SuppressWarnings("unchecked")
	public static DbTable parseDbTable(Context context, File file,
			Map<String, GeneratedJavaFile> maps) {

		LOGGER.info("start parse file :	" + file);
		Document document = null;
		try {
			SAXReader reader = getSAXReader();
			//
			document = reader.read(file);
		} catch (Exception e) {
			LOGGER.error("Parse sql template file error. File :" + file, e);
		}

		if (null == document) {
			return null;
		}

		Element rootElement = document.getRootElement();
		if (!TABLE.equalsIgnoreCase(rootElement.getName())) {
			return null;
		}
		//
		DbTable table = new DbTable();
		String name = rootElement.attributeValue(NAME);

		LOGGER.info("parse table :	" + name);

		table.setName(name.toLowerCase());

		List<Element> list = rootElement.elements();
		for (Element e : list) {
			if (COLUMN.equalsIgnoreCase(e.getName())) {
				String columnName = e.attributeValue(NAME);
				String javaProperty = e.attributeValue(JAVA_PROPERTY);
				String columnJavaType = e.attributeValue(JAVA_TYPE);

				DbColumn column = new DbColumn();
				column.setJavaType(columnJavaType);
				column.setName(columnName);
				column.setJavaProperty(javaProperty);
				table.getColumns().add(column);
			} else if (RESULT.equalsIgnoreCase(e.getName())) {
				DbSelectResult dbSelectResult = parseDbSelectResult(e, null,
						context, maps);
				table.getSelectResults().add(dbSelectResult);
			} else if (OPERATION.equalsIgnoreCase(e.getName())) {
				DbTableOperation operation = parseDbTableOperation(e, context,
						table.getSelectResults(), maps);
				if (null == operation) {
					continue;
				}
				table.getOperations().add(operation);
			}
		}
		return table;
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
	@SuppressWarnings("unchecked")
	private static DbTableOperation parseDbTableOperation(Element e,
			Context context, List<DbSelectResult> results,
			Map<String, GeneratedJavaFile> maps) {
		String id = e.attributeValue(ID);
		String many = e.attributeValue(MANY);
		String resultType = e.attributeValue(RESULT_TYPE);
		String sql = e.elementText(SQL);
		Element result = e.element(RESULT);
		Element comment = e.element(COMMNET);
		Element params = e.element(PARAMS);
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
			//
			if (operation.getResult() == null) {
				DbSelectResult dbSelectResult = new DbSelectResult();
				dbSelectResult.setType(fullyQualifiedJavaType);
				operation.setResult(dbSelectResult);
			}
			//
			if (operation.getResult() == null) {
				DbSelectResult dbSelectResult = new DbSelectResult();
				dbSelectResult.setType(fullyQualifiedJavaType);
				operation.setResult(dbSelectResult);
			}
		}

		if (null != params && null != params.elements(PARAM)) {
			for (Element el : (List<Element>) params.elements(PARAM)) {
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
	@SuppressWarnings("unchecked")
	private static DbSelectResult parseDbSelectResult(Element result,
			String resultType, Context context,
			Map<String, GeneratedJavaFile> maps) {
		String objectName = result.attributeValue(OBJECT_NAME);
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

			List<Element> properties = result.elements(PROPERTY);
			for (Element el : properties) {

				String javaType = el.attributeValue(JAVA_TYPE);
				String javaProperty = el.attributeValue(JAVA_PROPERTY);
				String column = el.attributeValue(COLUMN);
				processProperty(topLevelClass, column, javaProperty, javaType);
				dbSelectResult.addDbColumn(column, javaProperty, javaType);
			}
			maps.put(objectType.getFullyQualifiedName(), generatedJavaFile);
		}
		// //////
		else if (null != resultType && !isBasicType(resultType)) {
			FullyQualifiedJavaType fullyQualifiedJavaType = getFullyQualifiedJavaType(
					context, resultType);

			List<Element> properties = result.elements(PROPERTY);
			for (Element el : properties) {
				String javaType = el.attributeValue(JAVA_TYPE);
				String javaProperty = el.attributeValue(JAVA_PROPERTY);
				String column = el.attributeValue(COLUMN);
				dbSelectResult.addDbColumn(column, javaProperty, javaType);
			}
			dbSelectResult.setType(fullyQualifiedJavaType);
		}
		return dbSelectResult;
	}

	private static FullyQualifiedJavaType getFullyQualifiedJavaType(
			final Context context, final String resultType) {
		if (!isBasicType(resultType)) {
			String basicPackage = context.getJavaModelGeneratorConfiguration()
					.getTargetPackage();
			String type = resultType;
			if (null != basicPackage && !"".equals(basicPackage)
					&& !resultType.startsWith(basicPackage)
					&& resultType.indexOf('.') == -1) {
				type = basicPackage + "." + resultType;
			}
			return new FullyQualifiedJavaType(type);
		} else {
			return new FullyQualifiedJavaType(resultType);
		}
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

	private static void processProperty(final TopLevelClass topLevelClass,
			final String cloumn, final String javaProperty,
			final String javaType) {

		final FullyQualifiedJavaType type = new FullyQualifiedJavaType(javaType);

		final Field field = new Field();
		field.setName(javaProperty);
		field.setType(type);
		field.setVisibility(JavaVisibility.PRIVATE);
		addFieldComment(field, cloumn);

		topLevelClass.addField(field);
		topLevelClass.addImportedType(javaType);

		Method method = new Method();
		String methodName = javaProperty.length() > 1 ? javaProperty.substring(
				0, 1).toUpperCase()
				+ javaProperty.substring(1) : javaProperty.toUpperCase();
		method.setName("set" + methodName);
		method.addParameter(new Parameter(type, javaProperty));
		method.addBodyLine("this." + javaProperty + "=" + javaProperty + ";");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.setVisibility(JavaVisibility.PUBLIC);
		addGeneralMethodComment(method, cloumn);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setName("get" + methodName);
		method.setReturnType(type);
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

	/**
	 * 
	 * @param jdbcTypeName
	 * @return
	 */
	public static FullyQualifiedJavaType getFullyQualifiedJavaType(
			final String jdbcTypeName) {
		String jdbcType = jdbcTypeName;
		if (null == jdbcType || jdbcType.trim().equals("")) {
			jdbcType = "JAVA_OBJECT";
		}
		final int type = JdbcTypeNameTranslator.getJdbcType(jdbcType);
		switch (type) {
		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER: {
			return new FullyQualifiedJavaType(Integer.class.getName());
		}
		case Types.BIGINT: {
			return new FullyQualifiedJavaType(Long.class.getName());
		}
		case Types.CHAR: {
			return new FullyQualifiedJavaType(Character.class.getName());
		}
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.DOUBLE: {
			return new FullyQualifiedJavaType(Double.class.getName());
		}
		case Types.FLOAT: {
			return new FullyQualifiedJavaType(Float.class.getName());
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
			return new FullyQualifiedJavaType(List.class.getName());
		}
		case Types.JAVA_OBJECT:
		default: {
			return new FullyQualifiedJavaType(Object.class.getName());
		}
		}
	}

	public static String parseJdbcTypeName(final String str) {
		final String jdbcTypeName = str.replace("jdbcType", "")
				.replace("=", "").trim();
		return jdbcTypeName;
	}
}
