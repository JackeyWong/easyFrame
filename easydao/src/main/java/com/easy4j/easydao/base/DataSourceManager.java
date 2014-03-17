package com.easy4j.easydao.base;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class DataSourceManager {

	protected DataSource dataSource;
	protected JdbcTemplate jdbcTemplate;

	public DataSourceManager() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}