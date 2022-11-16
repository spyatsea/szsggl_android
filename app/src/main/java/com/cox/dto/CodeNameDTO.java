/*
 * Copyright (c) www.spyatsea.com  2012 
 */
package com.cox.dto;

/**
 * 数据传输对象，用于传输IvCode表的sort和name信息。
 * 
 * @author 乔勇(Jacky Qiao)
 * */
@SuppressWarnings("serial")
public class CodeNameDTO implements java.io.Serializable {

	private String code;
	private String name;

	public CodeNameDTO() {
		super();
	}

	public CodeNameDTO(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}