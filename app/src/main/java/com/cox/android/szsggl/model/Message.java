/*
 * Copyright (c) 2016 山西考科思 版权所有
 */
package com.cox.android.szsggl.model;

import java.io.Serializable;

/**
 * 消息记录表(t_base_message)类
 * <p>
 * 20160824更新 by Jacky
 * 
 * @author 乔勇(Jacky Qiao)
 */
public class Message implements Serializable {
	private static final long serialVersionUID = 48L;

	private Integer ids;
	private String userId;
	private String userName;
	private String mobilephone;
	private String title;
	private String description;
	private Integer userType;
	private Integer messageType;
	private Integer pushType;
	private Integer deviceType;
	private Integer pushMsgType;
	private String customContent;
	private Integer infoType;
	private String bizMsgCat;
	private String bizMsgType;
	private String createdtime;
	private String receivedtime;
	private String sendtime;
	private String sendFlag;
	private String valid;

	public Message() {
	}

	public Message(Integer ids, String userId, String userName, String mobilephone, String title, String description, Integer userType,
			Integer messageType, Integer pushType, Integer deviceType, Integer pushMsgType, String customContent, Integer infoType, String bizMsgCat,
			String bizMsgType, String createdtime, String receivedtime, String sendtime, String sendFlag, String valid) {
		this.ids = ids;
		this.userId = userId;
		this.userName = userName;
		this.mobilephone = mobilephone;
		this.title = title;
		this.description = description;
		this.userType = userType;
		this.messageType = messageType;
		this.pushType = pushType;
		this.deviceType = deviceType;
		this.pushMsgType = pushMsgType;
		this.customContent = customContent;
		this.infoType = infoType;
		this.bizMsgCat = bizMsgCat;
		this.bizMsgType = bizMsgType;
		this.createdtime = createdtime;
		this.receivedtime = receivedtime;
		this.sendtime = sendtime;
		this.sendFlag = sendFlag;
		this.valid = valid;
	}

	/**
	 * @return the ids
	 */
	public Integer getIds() {
		return ids;
	}

	/**
	 * @param ids
	 *            the ids to set
	 */
	public void setIds(Integer ids) {
		this.ids = ids;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the mobilephone
	 */
	public String getMobilephone() {
		return mobilephone;
	}

	/**
	 * @param mobilephone
	 *            the mobilephone to set
	 */
	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the userType
	 */
	public Integer getUserType() {
		return userType;
	}

	/**
	 * @param userType
	 *            the userType to set
	 */
	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	/**
	 * @return the messageType
	 */
	public Integer getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType
	 *            the messageType to set
	 */
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the pushType
	 */
	public Integer getPushType() {
		return pushType;
	}

	/**
	 * @param pushType
	 *            the pushType to set
	 */
	public void setPushType(Integer pushType) {
		this.pushType = pushType;
	}

	/**
	 * @return the deviceType
	 */
	public Integer getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType
	 *            the deviceType to set
	 */
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * @return the pushMsgType
	 */
	public Integer getPushMsgType() {
		return pushMsgType;
	}

	/**
	 * @param pushMsgType
	 *            the pushMsgType to set
	 */
	public void setPushMsgType(Integer pushMsgType) {
		this.pushMsgType = pushMsgType;
	}

	/**
	 * @return the customContent
	 */
	public String getCustomContent() {
		return customContent;
	}

	/**
	 * @param customContent
	 *            the customContent to set
	 */
	public void setCustomContent(String customContent) {
		this.customContent = customContent;
	}

	/**
	 * @return the infoType
	 */
	public Integer getInfoType() {
		return infoType;
	}

	/**
	 * @param infoType
	 *            the infoType to set
	 */
	public void setInfoType(Integer infoType) {
		this.infoType = infoType;
	}

	/**
	 * @return the bizMsgCat
	 */
	public String getBizMsgCat() {
		return bizMsgCat;
	}

	/**
	 * @param bizMsgCat
	 *            the bizMsgCat to set
	 */
	public void setBizMsgCat(String bizMsgCat) {
		this.bizMsgCat = bizMsgCat;
	}

	/**
	 * @return the bizMsgType
	 */
	public String getBizMsgType() {
		return bizMsgType;
	}

	/**
	 * @param bizMsgType
	 *            the bizMsgType to set
	 */
	public void setBizMsgType(String bizMsgType) {
		this.bizMsgType = bizMsgType;
	}

	/**
	 * @return the createdtime
	 */
	public String getCreatedtime() {
		return createdtime;
	}

	/**
	 * @param createdtime
	 *            the createdtime to set
	 */
	public void setCreatedtime(String createdtime) {
		this.createdtime = createdtime;
	}

	/**
	 * @return the receivedtime
	 */
	public String getReceivedtime() {
		return receivedtime;
	}

	/**
	 * @param receivedtime
	 *            the receivedtime to set
	 */
	public void setReceivedtime(String receivedtime) {
		this.receivedtime = receivedtime;
	}

	/**
	 * @return the sendtime
	 */
	public String getSendtime() {
		return sendtime;
	}

	/**
	 * @param sendtime
	 *            the sendtime to set
	 */
	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	/**
	 * @return the sendFlag
	 */
	public String getSendFlag() {
		return sendFlag;
	}

	/**
	 * @param sendFlag
	 *            the sendFlag to set
	 */
	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}

	/**
	 * @return the valid
	 */
	public String getValid() {
		return valid;
	}

	/**
	 * @param valid
	 *            the valid to set
	 */
	public void setValid(String valid) {
		this.valid = valid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == this.getClass()) {
			return this.getIds().equals(((Message) obj).getIds());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [ids=" + ids + ", userId=" + userId + ", userName=" + userName + ", mobilephone=" + mobilephone + ", title=" + title
				+ ", description=" + description + ", userType=" + userType + ", messageType=" + messageType + ", pushType=" + pushType
				+ ", deviceType=" + deviceType + ", pushMsgType=" + pushMsgType + ", customContent=" + customContent + ", infoType=" + infoType
				+ ", bizMsgCat=" + bizMsgCat + ", bizMsgType=" + bizMsgType + ", createdtime=" + createdtime + ", receivedtime=" + receivedtime
				+ ", sendtime=" + sendtime + ", sendFlag=" + sendFlag + ", valid=" + valid + "]";
	}

}