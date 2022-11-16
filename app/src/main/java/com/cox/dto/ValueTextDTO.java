/*
 * Copyright (c) 2013 山西考科思 版权所有
 */
package com.cox.dto;

/**
 * 数据传输对象，用于传输格式为{value:'', text: ''}的信息。
 * 
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings("serial")
public class ValueTextDTO implements java.io.Serializable {

	private String value;
	private String text;

	public ValueTextDTO() {
	}

	public ValueTextDTO(String value, String text) {
		this.value = value;
		this.text = text;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

}