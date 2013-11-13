Mybatis Generator Plugin Extension
==============

##1. Generator Configuration Sample

```
	<build>
		<plugins>
			<plugin>
				<groupId>com.tqlab.plugin</groupId>
				<artifactId>tqlab-mybatis-plugin</artifactId>
				<version>1.0.2</version>
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

Attribute		|	Description
				|
outputDirectory	|Oupput directory, default ${project.basedir}
jdbcURL			|Database url
jdbcUserId		|Database user
jdbcPassword	|Database password
database		|Database
sqlScript		|Sql file path for execute.
dbName			|Database name, mysql，hsqldb etc.
packages		|Java package name, com.tqlab.test etc.
sqlTemplatePath	|SqlMapper template path
overwrite		|overwrite generated file, default false

##2. Sql Template File Sample


<pre>
<code>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
&lt;table name=&quot;star&quot;&gt;

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
