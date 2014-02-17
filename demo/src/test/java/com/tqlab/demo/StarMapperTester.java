/**
 * 
 */
package com.tqlab.demo;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tqlab.demo.dal.dao.StarMapper;
import com.tqlab.demo.dal.dataobject.Star;

/**
 * @author John Lee
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "/META-INF/spring/common-db.xml",
		"/META-INF/spring/common-db-mapper.xml" })
public class StarMapperTester {

	@Autowired
	private StarMapper starMapper;

	@Test
	public void select() {
		Assert.assertNotNull(starMapper);

		Star s = new Star();
		s.setFirstname("John");
		s.setLastname("Lee");
		starMapper.insert(s);

		List<Star> list = starMapper.selectWithPagination(10, 0);
		Assert.assertTrue(list.size() > 0);

		list = starMapper.searchByfirstname("o");
		Assert.assertTrue(list.size() > 0);
	}

	@Test
	public void update() {
		int i = starMapper.update("John", null, 1);
		Assert.assertTrue(i > 0);
		Star star = new Star();
		star.setFirstname("Simith");
		i = starMapper.update2(star);
		Assert.assertTrue(i > 0);
	}
}
