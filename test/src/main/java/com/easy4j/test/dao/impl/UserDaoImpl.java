package com.easy4j.test.dao.impl;

import javax.sql.DataSource;

import com.easy4j.easydao.base.DAOSupport;
import com.easy4j.test.dao.UserDao;
import com.easy4j.test.domain.User;

public class UserDaoImpl extends DAOSupport<User> implements UserDao<User> {
	public UserDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

}
