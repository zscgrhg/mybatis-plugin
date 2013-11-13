/**
 * 
 */
package com.tqlab.plugin.mybatis.generator.config;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author John Lee
 * 
 */
public class Config {

	private List<CacheConfigItem> items;

	@SuppressWarnings("unchecked")
	public Config(Properties props) {

		this.items = new ArrayList<CacheConfigItem>();

		Enumeration<String> e = (Enumeration<String>) props.propertyNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			items.add(CacheConfigItem.valueOf(key, props.getProperty(key)));
		}
	}

	public String getCacheValue(String classFQN) {

		for (CacheConfigItem item : items) {
			if (item.getClassRegexp().matcher(classFQN).matches())
				return item.getCacheValue();
		}
		return null;

	}
}
