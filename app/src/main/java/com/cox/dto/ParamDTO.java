/*
 * Copyright (c) www.spyatsea.com  2013 
 */
package com.cox.dto;

/**
 * 数据传输对象，用于传输key和value信息。
 * 
 * @author 乔勇(Jacky Qiao)
 * */
@SuppressWarnings("serial")
public class ParamDTO implements java.io.Serializable {

	private String key;
	private String value;

	public ParamDTO(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public ParamDTO() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}