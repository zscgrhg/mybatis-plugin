/**
 * 
 */
package com.tqlab.plugin.mybatis.generator.config;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.tqlab.plugin.mybatis.util.Constants;

/**
 * @author John Lee
 * 
 */
public class Config {

	private final Properties props;
	private final List<CacheConfigItem> items;

	@SuppressWarnings("unchecked")
	public Config(final Properties props) {
		this.props = props;
		this.items = new ArrayList<CacheConfigItem>();

		Enumeration<String> e = (Enumeration<String>) props.propertyNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			String obj = props.getProperty(key);
			if (obj == null) {
				continue;
			}
			items.add(CacheConfigItem.valueOf(key, obj));
		}
		System.getProperties().putAll(props);
	}

	public String getCacheValue(final String classFQN) {

		for (CacheConfigItem item : items) {
			if (item.getClassRegexp().matcher(classFQN).matches())
				return item.getCacheValue();
		}
		return null;

	}

	public boolean isUseCache() {
		return "true".equalsIgnoreCase((String) props.get(Constants.USE_CACHE));
	}

	public String getTablePrefix() {
		String prefix = (String) props.get(Constants.TABLE_PREFIX);
		if (null != prefix) {
			prefix = prefix.trim().toLowerCase();
		}
		return prefix;
	}
}
