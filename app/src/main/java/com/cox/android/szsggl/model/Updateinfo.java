/*
 * Copyright (c) www.spyatsea.com  2014 
 */
package com.cox.android.szsggl.model;

/**
 * 升级信息表(UPDATEINFO)抽象类
 * 
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({ "serial" })
public class Updateinfo implements java.io.Serializable {

	// Fields

	private String ids;
	private String type;
	private String vercode;
	private String vername;
	private String content;
	private String filename;
	private String size;
	private String time;
	private String active;

	// Constructors

	/** default constructor */
	public Updateinfo() {
	}

	/** minimal constructor */
	public Updateinfo(String ids) {
		this.ids = ids;
	}

	public Updateinfo(String ids, String type, String vercode, String vername, String content, String filename,
			String size, String time, String active) {
		this.ids = ids;
		this.type = type;
		this.vercode = vercode;
		this.vername = vername;
		this.content = content;
		this.filename = filename;
		this.size = size;
		this.time = time;
		this.active = active;
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

	public String getVercode() {
		return vercode;
	}

	public void setVercode(String vercode) {
		this.vercode = vercode;
	}

	public String getVername() {
		return vername;
	}

	public void setVername(String vername) {
		this.vername = vername;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == this.getClass()) {
			return this.getIds().equals(((Updateinfo) obj).getIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}
}