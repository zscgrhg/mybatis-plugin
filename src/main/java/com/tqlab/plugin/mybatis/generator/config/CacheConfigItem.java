/**
 * 
 */
package com.tqlab.plugin.mybatis.generator.config;

import java.util.regex.Pattern;

import com.tqlab.plugin.mybatis.MybatisPluginException;

/**
 * @author John Lee
 * 
 */
public final class CacheConfigItem {

	private Pattern classRegexp;

	private String cacheValue;

	private CacheConfigItem(final Pattern classRegexp, final String cacheValue) {
		this.classRegexp = classRegexp;
		this.cacheValue = cacheValue;
	}

	public static CacheConfigItem valueOf(final String key, final String value) {

		if (key == null)
			throw new MybatisPluginException(
					"Property's key should be specified!");
		if (value == null)
			throw new MybatisPluginException(
					"Property's value should be specified!");

		return new CacheConfigItem(Pattern.compile(key), value);

	}

	public Pattern getClassRegexp() {
		return classRegexp;
	}

	public void setClassRegexp(final Pattern classRegexp) {
		this.classRegexp = classRegexp;
	}

	public String getCacheValue() {
		return cacheValue;
	}

	public void setCacheValue(final String cacheValue) {
		this.cacheValue = cacheValue;
	}
}
