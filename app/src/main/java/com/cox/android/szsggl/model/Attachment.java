/*
 * Copyright (c) www.spyatsea.com  2012 
 */
package com.cox.android.szsggl.model;

/**
 * 附件(attachment)对象
 * 
 * @author 乔勇(Jacky Qiao)
 */
@SuppressWarnings({ "serial" })
public class Attachment implements java.io.Serializable {
	private String ids;
	private String noteids;
	private String filetype;
	private String filename;
	private String filesize;
	private String createdtime;
	private String valid;
	private String lat;
	private String lon;
	private String lat_baidu;
	private String lon_baidu;
	private String memo;

	public Attachment() {
	}

	public Attachment(String ids) {
		this.ids = ids;
	}

	public Attachment(String ids, String noteids, String filetype, String filename, String filesize,
			String createdtime, String valid, String lat, String lon, String lat_baidu, String lon_baidu, String memo) {
		this.ids = ids;
		this.noteids = noteids;
		this.filetype = filetype;
		this.filename = filename;
		this.filesize = filesize;
		this.createdtime = createdtime;
		this.valid = valid;
		this.lat = lat;
		this.lon = lon;
		this.lat_baidu = lat_baidu;
		this.lon_baidu = lon_baidu;
		this.memo = memo;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getNoteids() {
		return noteids;
	}

	public void setNoteids(String noteids) {
		this.noteids = noteids;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(String createdtime) {
		this.createdtime = createdtime;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
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

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == this.getClass()) {
			return this.getIds().equals(((Attachment) obj).getIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}
}