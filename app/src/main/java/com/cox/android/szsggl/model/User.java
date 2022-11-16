/*
 * Copyright (c) www.spyatsea.com  2013 
 */
package com.cox.android.szsggl.model;

import java.util.Map;

/**
 * 人员(userinfo)对象
 * 
 * @author 乔勇(Jacky Qiao)
 */
public class User implements java.io.Serializable {
	private static final long serialVersionUID = 48L;
	private String ids;
	private String type;
	private String account;
	private String password;
	private String realname;
	private Integer gender;
	private String mobilephone;
	private String deptid;
	private String deptname;
	private String idsn;
	private int loginnum;
	private String latestlogintime;
	private String categoryIds;
	private Map<String, Boolean> permissionMap;

	public User() {
	}

	public User(String ids) {
		this.ids = ids;
	}

	public User(String ids, String type, String account, String password, String realname, Integer gender,
			String mobilephone, String deptid, String deptname, String idsn, int loginnum, String latestlogintime,
			String categoryIds, Map<String, Boolean> permissionMap) {
		this.ids = ids;
		this.type = type;
		this.account = account;
		this.password = password;
		this.realname = realname;
		this.gender = gender;
		this.mobilephone = mobilephone;
		this.deptid = deptid;
		this.deptname = deptname;
		this.idsn = idsn;
		this.loginnum = loginnum;
		this.latestlogintime = latestlogintime;
		this.categoryIds = categoryIds;
		this.permissionMap = permissionMap;
	}

	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
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

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public String getIdsn() {
		return idsn;
	}

	public void setIdsn(String idsn) {
		this.idsn = idsn;
	}

	public int getLoginnum() {
		return loginnum;
	}

	public void setLoginnum(int loginnum) {
		this.loginnum = loginnum;
	}

	public String getLatestlogintime() {
		return latestlogintime;
	}

	public void setLatestlogintime(String latestlogintime) {
		this.latestlogintime = latestlogintime;
	}

	public Map<String, Boolean> getPermissionMap() {
		return permissionMap;
	}

	public void setPermissionMap(Map<String, Boolean> permissionMap) {
		this.permissionMap = permissionMap;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == this.getClass()) {
			return this.getIds().equals(((User) obj).getIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}

	@Override
	public String toString() {
		return "User [ids=" + ids + ", type=" + type + ", account=" + account + ", password=" + password
				+ ", realname=" + realname + ", gender=" + gender + ", mobilephone=" + mobilephone + ", deptid="
				+ deptid + ", deptname=" + deptname + ", idsn=" + idsn + ", loginnum=" + loginnum
				+ ", latestlogintime=" + latestlogintime + ", categoryIds=" + categoryIds + ", permissionMap="
				+ permissionMap + "]";
	}
}