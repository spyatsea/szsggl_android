/*
 * Copyright (c) www.spyatsea.com  2015 
 */
package com.cox.android.szsggl.model;

/**
 * 
 * 版本信息类
 * 
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({ "serial" })
public class VerInfo implements java.io.Serializable {

	private String vercode;
	private String vername;
	private String url;
	private String vercontent;

	public VerInfo() {
	}

	public VerInfo(String vercode, String vername, String url) {
		this.vercode = vercode;
		this.vername = vername;
		this.url = url;
	}

	public VerInfo(String vercode, String vername, String url, String vercontent) {
		this.vercode = vercode;
		this.vername = vername;
		this.url = url;
		this.vercontent = vercontent;
	}

	public String getVercontent() {
		return vercontent;
	}

	public void setVercontent(String vercontent) {
		this.vercontent = vercontent;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}
