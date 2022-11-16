/*
 * Copyright (c) www.spyatsea.com  2012 
 */
package com.cox.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 通用数据传输对象。
 * 
 * @author 乔勇(Jacky Qiao)
 * */
@SuppressWarnings({ "serial", "rawtypes" })
public class CommonDTO implements java.io.Serializable {

	private String p1;
	private String p2;
	private String p3;
	private String p4;
	private String p5;
	private String p6;
	private String p7;
	private String p8;
	private String p9;
	private String p10;
	private String p11;
	private String p12;
	private String p13;
	private String p14;
	private String p15;
	private Boolean b1;

	private Integer i1;
	private Integer i2;
	private Integer i3;

	private Long plong1;
	private List plist1;
	private Map pmap1;
	private byte[] bytes1;

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9,
			String p10, String p11, String p12, String p13, String p14, String p15, Boolean b1, Long plong1,
			List plist1, Map pmap1) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
		this.p7 = p7;
		this.p8 = p8;
		this.p9 = p9;
		this.p10 = p10;
		this.p11 = p11;
		this.p12 = p12;
		this.p13 = p13;
		this.p14 = p14;
		this.p15 = p15;
		this.b1 = b1;
		this.plong1 = plong1;
		this.plist1 = plist1;
		this.pmap1 = pmap1;
	}

	public CommonDTO(String p1, Integer i1, Integer i2) {
		super();
		this.p1 = p1;
		this.i1 = i1;
		this.i2 = i2;
	}

	public Integer getI1() {
		return i1;
	}

	public void setI1(Integer i1) {
		this.i1 = i1;
	}

	public Integer getI2() {
		return i2;
	}

	public void setI2(Integer i2) {
		this.i2 = i2;
	}

	public String getP11() {
		return p11;
	}

	public void setP11(String p11) {
		this.p11 = p11;
	}

	public String getP12() {
		return p12;
	}

	public void setP12(String p12) {
		this.p12 = p12;
	}

	public String getP13() {
		return p13;
	}

	public void setP13(String p13) {
		this.p13 = p13;
	}

	public String getP14() {
		return p14;
	}

	public void setP14(String p14) {
		this.p14 = p14;
	}

	public String getP15() {
		return p15;
	}

	public void setP15(String p15) {
		this.p15 = p15;
	}

	public CommonDTO() {
		this.b1 = false;
	}

	public Integer getI3() {
		return i3;
	}

	public void setI3(Integer i3) {
		this.i3 = i3;
	}

	public CommonDTO(String p1) {
		super();
		this.p1 = p1;
	}

	public CommonDTO(String p1, String p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

	public CommonDTO(String p1, String p2, String p3) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	public CommonDTO(String p1, String p2, String p3, String p4) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6, String p7) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
		this.p7 = p7;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
		this.p7 = p7;
		this.p8 = p8;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9,
			String p10) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
		this.p7 = p7;
		this.p8 = p8;
		this.p9 = p9;
		this.p10 = p10;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9,
			String p10, List plist1) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
		this.p7 = p7;
		this.p8 = p8;
		this.p9 = p9;
		this.p10 = p10;
		this.plist1 = plist1;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9,
			String p10, Long plong1, List plist1) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
		this.p7 = p7;
		this.p8 = p8;
		this.p9 = p9;
		this.p10 = p10;
		this.plong1 = plong1;
		this.plist1 = plist1;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9,
			String p10, Boolean b1, Long plong1, List plist1) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
		this.p7 = p7;
		this.p8 = p8;
		this.p9 = p9;
		this.p10 = p10;
		this.b1 = b1;
		this.plong1 = plong1;
		this.plist1 = plist1;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9,
			String p10, Boolean b1, Long plong1, List plist1, Map pmap1) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.p6 = p6;
		this.p7 = p7;
		this.p8 = p8;
		this.p9 = p9;
		this.p10 = p10;
		this.b1 = b1;
		this.plong1 = plong1;
		this.plist1 = plist1;
		this.pmap1 = pmap1;
	}

	public Map getPmap1() {
		return pmap1;
	}

	public void setPmap1(Map pmap1) {
		this.pmap1 = pmap1;
	}

	public CommonDTO(String p1, String p2, String p3, String p4, String p5, Boolean b1) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.b1 = b1;
	}

	public Long getPlong1() {
		return plong1;
	}

	public void setPlong1(Long plong1) {
		this.plong1 = plong1;
	}

	public String getP1() {
		return p1;
	}

	public void setP1(String p1) {
		this.p1 = p1;
	}

	public Boolean getB1() {
		return b1;
	}

	public void setB1(Boolean b1) {
		this.b1 = b1;
	}

	public String getP2() {
		return p2;
	}

	public void setP2(String p2) {
		this.p2 = p2;
	}

	public String getP3() {
		return p3;
	}

	public void setP3(String p3) {
		this.p3 = p3;
	}

	public String getP4() {
		return p4;
	}

	public void setP4(String p4) {
		this.p4 = p4;
	}

	public String getP5() {
		return p5;
	}

	public void setP5(String p5) {
		this.p5 = p5;
	}

	public String getP6() {
		return p6;
	}

	public void setP6(String p6) {
		this.p6 = p6;
	}

	public String getP7() {
		return p7;
	}

	public void setP7(String p7) {
		this.p7 = p7;
	}

	public String getP8() {
		return p8;
	}

	public void setP8(String p8) {
		this.p8 = p8;
	}

	public String getP9() {
		return p9;
	}

	public void setP9(String p9) {
		this.p9 = p9;
	}

	public String getP10() {
		return p10;
	}

	public void setP10(String p10) {
		this.p10 = p10;
	}

	public List getPlist1() {
		return plist1;
	}

	public void setPlist1(List plist1) {
		this.plist1 = plist1;
	}

	public byte[] getBytes1() {
		return bytes1;
	}

	public void setBytes1(byte[] bytes1) {
		this.bytes1 = bytes1;
	}

	@Override
	public String toString() {
		return "CommonDTO [p1=" + p1 + ", p2=" + p2 + ", p3=" + p3 + ", p4=" + p4 + ", p5=" + p5 + ", p6=" + p6
				+ ", p7=" + p7 + ", p8=" + p8 + ", p9=" + p9 + ", p10=" + p10 + ", p11=" + p11 + ", p12=" + p12
				+ ", p13=" + p13 + ", p14=" + p14 + ", p15=" + p15 + ", b1=" + b1 + ", i1=" + i1 + ", i2=" + i2
				+ ", i3=" + i3 + ", plong1=" + plong1 + ", plist1=" + plist1 + ", pmap1=" + pmap1 + ", bytes1="
				+ Arrays.toString(bytes1) + "]";
	}

}