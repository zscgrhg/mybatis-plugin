package com.tqlab.plugin.mybatis;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import com.tqlab.plugin.mybatis.maven.MyBatisGeneratorMojo;

/**
 * 
 * @author John Lee
 * 
 */
public class MyBatisGeneratorMojoTester {

	@Test
	public void testHsqldb() throws MojoExecutionException, URISyntaxException {

		String path = this.getClass().getResource("/").getPath();

		MyBatisGeneratorMojo generator = new MyBatisGeneratorMojo();
		set("outputDirectory", generator,
				new File(System.getProperty("user.home")
						+ "/Desktop/test/hsqldb/"));
		set("jdbcURL", generator, "jdbc:hsqldb:file:" + path + "db/sample");
		set("jdbcUserId", generator, "sa");
		set("jdbcPassword", generator, "");
		set("sqlScript", generator, path + "hsqldb.sql");
		set("database", generator, "sample");
		set("dbName", generator, "hsqldb");
		set("packages", generator, "net.hidev.api.common.dal");
		set("overwrite", generator, "true");
		set("sqlTemplatePath", generator, path + "hsqldb/sqltemplate/");
		generator.execute();
	}

	@Test
	public void testMysql() throws MojoExecutionException, URISyntaxException {

		String path = this.getClass().getResource("/").getPath();

		MyBatisGeneratorMojo generator = new MyBatisGeneratorMojo();
		set("outputDirectory", generator,
				new File(System.getProperty("user.home")
						+ "/Desktop/test/mysql/"));
		set("jdbcURL", generator, "jdbc:mysql://127.0.0.1/openapi");
		set("jdbcUserId", generator, "test");
		set("jdbcPassword", generator, "123456");
		set("sqlScript", generator, path + "mysql.sql");
		set("database", generator, "openapi");
		set("dbName", generator, "mysql");
		set("packages", generator, "net.hidev.api.common.dal");
		generator.execute();
	}

	private void set(String name, Object obj, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
