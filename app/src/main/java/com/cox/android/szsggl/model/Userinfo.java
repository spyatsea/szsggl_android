/*
 * Copyright (c) www.spyatsea.com  2013 
 */
package com.cox.android.szsggl.model;

/**
 * 人员(userinfo)对象
 * 
 * @author 乔勇(Jacky Qiao)
 */
public class Userinfo implements java.io.Serializable {
	private static final long serialVersionUID = 48L;
	private String ids;
	private String type;
	private String account;
	private String password;
	private String realname;
	private String deptids;
	private String deptname;
	private String position;
	private Integer pxbh;
	private String valid;

	public Userinfo() {
	}

	public Userinfo(String ids) {
		this.ids = ids;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccount() {
		return account;
	}

	public String getDeptname() {
		return deptname;
	}

	public Integer getPxbh() {
		return pxbh;
	}

	public void setPxbh(Integer pxbh) {
		this.pxbh = pxbh;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getDeptids() {
		return deptids;
	}

	public void setDeptids(String deptids) {
		this.deptids = deptids;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == this.getClass()) {
			return this.getIds().equals(((Userinfo) obj).getIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}
}