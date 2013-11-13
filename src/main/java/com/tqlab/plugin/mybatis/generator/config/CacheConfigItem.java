/**
 * 
 */
package com.tqlab.plugin.mybatis.generator.config;

import java.util.regex.Pattern;

/**
 * @author John Lee
 * 
 */
public class CacheConfigItem {

	private Pattern classRegexp;

	private String cacheValue;

	private CacheConfigItem(Pattern classRegexp, String cacheValue) {
		this.classRegexp = classRegexp;
		this.cacheValue = cacheValue;
	}

	public static final CacheConfigItem valueOf(String key, String value) {

		if (key == null)
			throw new IllegalArgumentException(
					"Property's key should be specified!");
		if (value == null)
			throw new IllegalArgumentException(
					"Property's value should be specified!");

		return new CacheConfigItem(Pattern.compile(key), value);

	}

	public Pattern getClassRegexp() {
		return classRegexp;
	}

	public void setClassRegexp(Pattern classRegexp) {
		this.classRegexp = classRegexp;
	}

	public String getCacheValue() {
		return cacheValue;
	}

	public void setCacheValue(String cacheValue) {
		this.cacheValue = cacheValue;
	}
}
