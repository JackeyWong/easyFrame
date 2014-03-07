package com.easy4j.test;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.easy4j.test.dao.UserDao;
import com.easy4j.test.domain.User;

public class UserTest {

	private static UserDao<User> dao;

	@BeforeClass
	public static void init(){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		dao = (UserDao<User>) context.getBean("userDao");
	}
	
	@Test
	public void insertTest(){
		User entity = new User();
		entity.setId(2);
		entity.setPassword("test02");
		entity.setUserName("name02");
		dao.insert(entity );
	}
	@Test
	public void updateTest(){
		User entity = new User();
		entity.setId(1);
		entity.setPassword("test01");
		entity.setUserName("test01");
		dao.update(entity, null, null);
	}
	@Test
	public void queryTest(){
		List<User> query = dao.query();
		for (User user : query) {
			System.out.println(user);
		}
	}
	@Test
	public void deleteTest(){
		int effect = dao.delete(2);
		System.out.println("effect = "+effect);
	}
	
	@Test
	public void getTest(){
		User user = dao.get(1);
		System.out.println(user);
	}
	@Test
	public void getTotalTest(){
		System.out.println("total = "+ dao.getTotal());
	}
	@Test
	public void deleteTest2(){
		int delete = dao.delete("userName = ?", new String[]{"1"});
		System.out.println("delete = " + delete);
	}
}
