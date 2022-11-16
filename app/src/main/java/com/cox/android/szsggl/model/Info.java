/*
 * Copyright (c) 2014 山西考科思 版权所有
 */
package com.cox.android.szsggl.model;

import java.io.Serializable;

/**
 * 信息表(INFO)类
 * 
 * @author 乔勇(Jacky Qiao)
 */
public class Info implements Serializable {
	private static final long serialVersionUID = 48L;

	private String ids;
	private String title;
	private String deptid;
	private String treeid;
	private String fldh;
	private String community_id;
	private String createdtime;
	private String modifiedtime;
	private String inputer_id;
	private String infodata;
	private String publishflag;
	private String valid;
	private String status;
	private String f1;
	private String f2;
	private String f3;
	private String f4;
	private String f5;
	private String f6;
	private String f7;
	private String f8;
	private String f9;
	private String f10;
	private String attafile;
	private Long pxbh;
	private String publisher_id;
	private String publishtime;
	private String infotype;
	private String topflag;
	private String summary;
	private String keywords;
	private String dept_fldh;
	private Long click_num;

	public Info() {
	}

	public Info(String ids) {
		this.ids = ids;
	}

	public Info(String ids, String title, String deptid, String treeid, String fldh, String community_id,
			String createdtime, String modifiedtime, String inputer_id, String infodata, String publishflag,
			String valid, String status, String f1, String f2, String f3, String f4, String f5, String f6, String f7,
			String f8, String f9, String f10, String attafile, Long pxbh, String publisher_id, String publishtime,
			String infotype, String topflag, String summary, String keywords, String dept_fldh, Long click_num) {
		this.ids = ids;
		this.title = title;
		this.deptid = deptid;
		this.treeid = treeid;
		this.fldh = fldh;
		this.community_id = community_id;
		this.createdtime = createdtime;
		this.modifiedtime = modifiedtime;
		this.inputer_id = inputer_id;
		this.infodata = infodata;
		this.publishflag = publishflag;
		this.valid = valid;
		this.status = status;
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		this.f4 = f4;
		this.f5 = f5;
		this.f6 = f6;
		this.f7 = f7;
		this.f8 = f8;
		this.f9 = f9;
		this.f10 = f10;
		this.attafile = attafile;
		this.pxbh = pxbh;
		this.publisher_id = publisher_id;
		this.publishtime = publishtime;
		this.infotype = infotype;
		this.topflag = topflag;
		this.summary = summary;
		this.keywords = keywords;
		this.dept_fldh = dept_fldh;
		this.click_num = click_num;
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

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getTreeid() {
		return treeid;
	}

	public void setTreeid(String treeid) {
		this.treeid = treeid;
	}

	public String getFldh() {
		return fldh;
	}

	public void setFldh(String fldh) {
		this.fldh = fldh;
	}

	public String getCommunity_id() {
		return community_id;
	}

	public void setCommunity_id(String community_id) {
		this.community_id = community_id;
	}

	public String getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(String createdtime) {
		this.createdtime = createdtime;
	}

	public String getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(String modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	public String getInputer_id() {
		return inputer_id;
	}

	public void setInputer_id(String inputer_id) {
		this.inputer_id = inputer_id;
	}

	public String getInfodata() {
		return infodata;
	}

	public void setInfodata(String infodata) {
		this.infodata = infodata;
	}

	public String getPublishflag() {
		return publishflag;
	}

	public void setPublishflag(String publishflag) {
		this.publishflag = publishflag;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getF1() {
		return f1;
	}

	public void setF1(String f1) {
		this.f1 = f1;
	}

	public String getF2() {
		return f2;
	}

	public void setF2(String f2) {
		this.f2 = f2;
	}

	public String getF3() {
		return f3;
	}

	public void setF3(String f3) {
		this.f3 = f3;
	}

	public String getF4() {
		return f4;
	}

	public void setF4(String f4) {
		this.f4 = f4;
	}

	public String getF5() {
		return f5;
	}

	public void setF5(String f5) {
		this.f5 = f5;
	}

	public String getF6() {
		return f6;
	}

	public void setF6(String f6) {
		this.f6 = f6;
	}

	public String getF7() {
		return f7;
	}

	public void setF7(String f7) {
		this.f7 = f7;
	}

	public String getF8() {
		return f8;
	}

	public void setF8(String f8) {
		this.f8 = f8;
	}

	public String getF9() {
		return f9;
	}

	public void setF9(String f9) {
		this.f9 = f9;
	}

	public String getF10() {
		return f10;
	}

	public void setF10(String f10) {
		this.f10 = f10;
	}

	public String getAttafile() {
		return attafile;
	}

	public void setAttafile(String attafile) {
		this.attafile = attafile;
	}

	public Long getPxbh() {
		return pxbh;
	}

	public void setPxbh(Long pxbh) {
		this.pxbh = pxbh;
	}

	public String getPublisher_id() {
		return publisher_id;
	}

	public void setPublisher_id(String publisher_id) {
		this.publisher_id = publisher_id;
	}

	public String getPublishtime() {
		return publishtime;
	}

	public void setPublishtime(String publishtime) {
		this.publishtime = publishtime;
	}

	public String getInfotype() {
		return infotype;
	}

	public void setInfotype(String infotype) {
		this.infotype = infotype;
	}

	public String getTopflag() {
		return topflag;
	}

	public void setTopflag(String topflag) {
		this.topflag = topflag;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getDept_fldh() {
		return dept_fldh;
	}

	public void setDept_fldh(String dept_fldh) {
		this.dept_fldh = dept_fldh;
	}

	public Long getClick_num() {
		return click_num;
	}

	public void setClick_num(Long click_num) {
		this.click_num = click_num;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == this.getClass()) {
			return this.getIds().equals(((Info) obj).getIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}

	@Override
	public String toString() {
		return "Info [ids=" + ids + ", title=" + title + ", deptid=" + deptid + ", treeid=" + treeid + ", fldh=" + fldh
				+ ", community_id=" + community_id + ", createdtime=" + createdtime + ", modifiedtime=" + modifiedtime
				+ ", inputer_id=" + inputer_id + ", infodata=" + infodata + ", publishflag=" + publishflag + ", valid="
				+ valid + ", status=" + status + ", f1=" + f1 + ", f2=" + f2 + ", f3=" + f3 + ", f4=" + f4 + ", f5="
				+ f5 + ", f6=" + f6 + ", f7=" + f7 + ", f8=" + f8 + ", f9=" + f9 + ", f10=" + f10 + ", attafile="
				+ attafile + ", pxbh=" + pxbh + ", publisher_id=" + publisher_id + ", publishtime=" + publishtime
				+ ", infotype=" + infotype + ", topflag=" + topflag + ", summary=" + summary + ", keywords=" + keywords
				+ ", dept_fldh=" + dept_fldh + ", click_num=" + click_num + "]";
	}

}
