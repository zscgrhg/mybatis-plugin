package com.tqlab.plugin.mybatis.maven;

import java.io.IOException;

public interface MybatisExecutorCallback {

	String getJdbcConfigPostfix();

	String getStringConfigPostfix();

	String getOsgiConfigPostfix();

	void onWriteJdbcConfig(String str);

	void onWriteSpringConfig(String str);

	void onWriteOsgiConfig(String str);

	void onFinsh(boolean overwrite) throws IOException;
}
