Mybatis Generator Plugin
==============
##1. Maven repository

```
<dependency>
    <groupId>com.tqlab.plugin</groupId>
    <artifactId>tqlab-mybatis-plugin</artifactId>
    <version>1.0.6</version>
</dependency>
```

##2. Plugin Activity Diagram

![Mybatis Generator Plugin Activity Diagram](http://img.tqlab.com/mybatis-plugin/mybatis-gernrator-plugin-activity-diagram.png)


##3. Plugin Configuration Sample

```
	<build>
		<plugins>
			<plugin>
				<groupId>com.tqlab.plugin</groupId>
				<artifactId>tqlab-mybatis-plugin</artifactId>
				<version>1.0.6</version>
				<executions>
					<execution>
						<id>Generate MyBatis Artifacts</id>
						<phase>deploy</phase>
						<goals>
							<goal>generate</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<outputDirectory>${project.basedir}</outputDirectory>
					<!-- db config -->
					<jdbcURL>jdbc:mysql://localhost/testdb?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull</jdbcURL>
					<jdbcUserId>user</jdbcUserId>
					<jdbcPassword>password</jdbcPassword>
					<database>testdb</database>
					<dbName>mysql</dbName>
					<!-- db config end -->
					<!-- <sqlScript>${project.basedir}/src/main/resources/mysql.sql</sqlScript> -->
					<packages>com.taobao.bns.dal</packages>
					<sqlTemplatePath>${project.basedir}/src/main/resources/sqltemplate/</sqlTemplatePath>
					<overwrite>true</overwrite>
					<useCache>false</useCache>
					<generateJdbcConfig>false</generateJdbcConfig>
					<generateSpringConfig>true</generateSpringConfig>
				</configuration>
			</plugin>
		</plugins>
	</build>
```


Attribute		|	Description		|	Default value    | Required
----------------|------------------|--------------------|------------
outputDirectory	|Oupput directory 	|${project.build.directory}/generated-sources/mybatis-generator  | 
sqlScript		|Location of a SQL script file to run before generating code.||false
jdbcURL			|Database url		|	|true
jdbcUserId		|Database user		|	|false
jdbcPassword	|Database password	|	|false
tableNames		|Comma delimited list of table names to generate|all tables of current database|false
tablePrefix		|For example, table name: wp_xxxx, the word 'wp' is the table prefix||false
database		|Database			|	|true
dbName			|Database name, mysql，hsqldb etc.||true
packages		|Java package name, com.tqlab.test etc.||true
overwrite		| Overwrite the exist code, config file or not.|false|false
sqlTemplatePath	|SqlMapper template path||true
useCache|Use cache or not.|false|false
generateSpringConfig|Generate spring osgi xml config file or not.| false|false
generateOsgiConfig|Generate spring osgi xml config file or not.|false|false
properties|extra config||false



##4. Sql Template File Sample


<pre>
<code>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
&lt;table xmlns="http://schema.tqlab.com/mybatis" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://schema.tqlab.com/mybatis http://schema.tqlab.com/mybatis/tqlab-mybatis-plugin.xsd"
	 name=&quot;star&quot;&gt;

	&lt;operation id=&quot;deleteById&quot;&gt;
		&lt;sql&gt;
			&lt;![CDATA[
			delete from star where id=#{id,jdbcType=INTEGER};
			]]&gt;
		&lt;/sql&gt;
	&lt;/operation&gt;

	&lt;operation id=&quot;count&quot; resultType=&quot;java.lang.Integer&quot;&gt;
		&lt;sql&gt;
			&lt;![CDATA[
			select count(*) from star;
			]]&gt;
		&lt;/sql&gt;
	&lt;/operation&gt;

	&lt;operation id=&quot;sum&quot; resultType=&quot;java.lang.Integer&quot;&gt;
		&lt;sql&gt;
			&lt;![CDATA[
			select sum(id) from star;
			]]&gt;
		&lt;/sql&gt;
	&lt;/operation&gt;

	&lt;operation id=&quot;selectAll&quot; many=&quot;true&quot;&gt;
		&lt;sql&gt;
			&lt;![CDATA[
			select * from star;
			]]&gt;
		&lt;/sql&gt;
	&lt;/operation&gt;

	&lt;operation id=&quot;selectById&quot; many=&quot;false&quot;&gt;
		&lt;sql&gt;
			&lt;![CDATA[
			select * from star where id=#{id,jdbcType=INTEGER};
			]]&gt;
		&lt;/sql&gt;
	&lt;/operation&gt;

	&lt;operation id=&quot;selectWithPagination&quot;&gt;
		&lt;comment&gt;
			demo
		&lt;/comment&gt;
		&lt;sql&gt;
			&lt;![CDATA[
			select limit #{start,jdbcType=INTEGER} #{size,jdbcType=INTEGER} * from star;
			]]&gt;
		&lt;/sql&gt;
	&lt;/operation&gt;

	&lt;operation id=&quot;selectComplex1&quot; many=&quot;true&quot;&gt;
		&lt;result objectName=&quot;StarMovies&quot;&gt;
			&lt;property cloumn=&quot;id&quot; javaProperty=&quot;id&quot; javaType=&quot;java.lang.Integer&quot; /&gt;
			&lt;property cloumn=&quot;firstname&quot; javaProperty=&quot;firstname&quot;
				javaType=&quot;java.lang.String&quot; /&gt;
			&lt;property cloumn=&quot;lastname&quot; javaProperty=&quot;lastname&quot;
				javaType=&quot;java.lang.String&quot; /&gt;
			&lt;property cloumn=&quot;movieid&quot; javaProperty=&quot;movieid&quot; javaType=&quot;java.lang.Integer&quot; /&gt;
			&lt;property cloumn=&quot;title&quot; javaProperty=&quot;title&quot; javaType=&quot;java.lang.String&quot; /&gt;
		&lt;/result&gt;
		&lt;sql&gt;
			&lt;![CDATA[
			select a.*, b.* from star a, movies b where a.id = b.starid
			]]&gt;
		&lt;/sql&gt;
	&lt;/operation&gt;

	&lt;operation id=&quot;selectComplex2&quot; many=&quot;true&quot;&gt;
		&lt;result objectName=&quot;StarMovies2&quot;&gt;
			&lt;property cloumn=&quot;star_id&quot; javaProperty=&quot;id&quot; javaType=&quot;java.lang.Integer&quot; /&gt;
			&lt;property cloumn=&quot;name&quot; javaProperty=&quot;firstname&quot; javaType=&quot;java.lang.String&quot; /&gt;
			&lt;property cloumn=&quot;lastname&quot; javaProperty=&quot;lastname&quot;
				javaType=&quot;java.lang.String&quot; /&gt;
			&lt;property cloumn=&quot;movieid&quot; javaProperty=&quot;movieid&quot; javaType=&quot;java.lang.Integer&quot; /&gt;
			&lt;property cloumn=&quot;title&quot; javaProperty=&quot;title&quot; javaType=&quot;java.lang.String&quot; /&gt;
		&lt;/result&gt;
		&lt;sql&gt;
			&lt;![CDATA[
			select a.id as star_id, a.firstname as name, a.lastname, 
			b.movieid, b.title from star a, movies b 
			where a.id = b.starid
			]]&gt;
		&lt;/sql&gt;
	&lt;/operation&gt;
&lt;/table&gt;
</code>
</pre>

More sample: [https://github.com/tqlab/mybatis-plugin/blob/master/demo/src/main/resources/sqltemplate/hsqldb/table.star.xml](https://github.com/tqlab/mybatis-plugin/blob/master/demo/src/main/resources/sqltemplate/hsqldb/table.star.xml)

##5. Change log
###v 1.0.2
1. bugfix hsqldb sql
2. Add mysql support
3. Add extra config (tableNames)

###v 1.0.3

1. modify Java Code generate commment
2. config commentGenerator property suppressDate false
3. change @paramter expression to maven annotation

###v1.0.4
1. bugfix 1.0.3. The v1.0.3 has a serious bug when release to maven centeral.
 
###v1.0.5
1. Add mybatis sql template xml Scheam support.
2. Add log4j support when generate Java Mapper.
3. Add tableNames validate.
4. Add jsqlpaser for SQL validation. 
5. Add tablePrefix config support.
6. Add auto delete ..*.dao /..*.dataobject package files when set overwrite is ture.
7. Delete unused code.

###v1.0.6
1. bugfix mysql table generate code error when name is uppercase.
2. bugfix table generate code error when the column's type is BLOB,BINARY or VARBINARY.
3. Add dynamic script for annotation supported.
4. Add parameter of Object type supported.

###v1.0.7
1. update jsqlparser version to 0.8.9
2. Add mybatis Options annotation supported.
3. Add alias name config supported.
4. Add specific driver supported.
