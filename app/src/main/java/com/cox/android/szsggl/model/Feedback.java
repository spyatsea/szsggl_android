/*
 * Copyright (c) www.spyatsea.com  2014 
 */
package com.cox.android.szsggl.model;

import java.io.Serializable;

/**
 * 反馈信息表(MESSAGE)类
 * 
 * @author 乔勇(Jacky Qiao)
 */
public class Feedback implements Serializable {
	private static final long serialVersionUID = 48L;

	private String ids;
	private String title;
	private String description;
	private String userId;
	private String userName;
	private String tel;
	private String email;
	private String qq;
	private String ip;
	private String createdtime;
	private String processorId;
	private String processorName;
	private String processtime;
	private String resultContent;
	private String valid;

	public Feedback() {
	}

	public Feedback(String ids, String title, String description, String userId, String userName, String tel,
			String email, String qq, String ip, String createdtime, String processorId, String processorName,
			String processtime, String resultContent, String valid) {
		this.ids = ids;
		this.title = title;
		this.description = description;
		this.userId = userId;
		this.userName = userName;
		this.tel = tel;
		this.email = email;
		this.qq = qq;
		this.ip = ip;
		this.createdtime = createdtime;
		this.processorId = processorId;
		this.processorName = processorName;
		this.processtime = processtime;
		this.resultContent = resultContent;
		this.valid = valid;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(String createdtime) {
		this.createdtime = createdtime;
	}

	public String getProcessorId() {
		return processorId;
	}

	public void setProcessorId(String processorId) {
		this.processorId = processorId;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	public String getProcesstime() {
		return processtime;
	}

	public void setProcesstime(String processtime) {
		this.processtime = processtime;
	}

	public String getResultContent() {
		return resultContent;
	}

	public void setResultContent(String resultContent) {
		this.resultContent = resultContent;
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
			return this.getIds().equals(((Feedback) obj).getIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}

}