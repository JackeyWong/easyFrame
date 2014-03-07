package com.easy4j.test.domain;

import com.easy4j.easydao.annotation.Column;
import com.easy4j.easydao.annotation.ID;
import com.easy4j.easydao.annotation.Table;

@Table("td_user")
public class User {

	@ID(Autoincrement = true)
	@Column("id")
	private int id;
	@Column("username")
	private String userName;
	@Column("password")
	private String password;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password="
				+ password + "]";
	}
}
