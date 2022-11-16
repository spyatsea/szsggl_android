/*
 * Copyright (c) www.spyatsea.com
 */
package com.cox.android.szsggl.model;

/**
 * 记录(note)对象
 * 
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({ "serial" })
public class Note implements java.io.Serializable {
	private String ids;
	private String title;
	private String infodata;
	private String createdtime;
	private String lat;
	private String lon;
	private String lat_baidu;
	private String lon_baidu;
	private String address;
	private String valid;

	public Note() {
	}

	public Note(String ids) {
		this.ids = ids;
	}

	public Note(String ids, String title, String infodata, String createdtime, String lat, String lon,
			String lat_baidu, String lon_baidu, String address, String valid) {
		this.ids = ids;
		this.title = title;
		this.infodata = infodata;
		this.createdtime = createdtime;
		this.lat = lat;
		this.lon = lon;
		this.lat_baidu = lat_baidu;
		this.lon_baidu = lon_baidu;
		this.address = address;
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

	public String getInfodata() {
		return infodata;
	}

	public void setInfodata(String infodata) {
		this.infodata = infodata;
	}

	public String getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(String createdtime) {
		this.createdtime = createdtime;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat_baidu() {
		return lat_baidu;
	}

	public void setLat_baidu(String lat_baidu) {
		this.lat_baidu = lat_baidu;
	}

	public String getLon_baidu() {
		return lon_baidu;
	}

	public void setLon_baidu(String lon_baidu) {
		this.lon_baidu = lon_baidu;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
			return this.getIds().equals(((Note) obj).getIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}
}