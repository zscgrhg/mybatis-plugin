/**
 * 
 */
package com.tqlab.demo;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tqlab.demo.dal.dao.AdminMapper;
import com.tqlab.demo.dal.dataobject.Admin;
import com.tqlab.demo.dal.dataobject.AdminKey;

/**
 * @author John Lee
 * 
 */
public class AdminMapperTester {

	@Test
	public void select() {
		String configLocations[] = { "META-INF/spring/common-db.xml",
				"META-INF/spring/common-db-mapper.xml" };
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				configLocations);
		AdminMapper adminMapper = (AdminMapper) applicationContext
				.getBean("adminMapper");
		Assert.assertNotNull(adminMapper);
		AdminKey key = new AdminKey();
		key.setId(1);
		Admin admin = adminMapper.selectByPrimaryKey(key);
		Assert.assertNotNull(admin);
		
		admin = new Admin();
		admin.setName("a");
		admin.setPwd("b");
		adminMapper.insertSelective(admin);
		System.out.println(admin.getId());
	}
}
