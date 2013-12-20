/**
 * 
 */
package com.tqlab.demo;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tqlab.demo.dal.dao.StarMapper;
import com.tqlab.demo.dal.dataobject.Star;

/**
 * @author John Lee
 * 
 */
public class StarMapperTester {

	@Test
	public void select() {
		String configLocations[] = { "META-INF/spring/common-db.xml",
				"META-INF/spring/common-db-mapper.xml" };
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				configLocations);
		StarMapper starMapper = (StarMapper) applicationContext
				.getBean("starMapper");
		Assert.assertNotNull(starMapper);
		List<Star> list = starMapper.selectWithPagination(10, 0);
		Assert.assertNotNull(list);
	}
}
