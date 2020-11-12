package com.usoft.mq.utils;

/**
 * 消息内容
 *
 * @author: wangcanyi
 * @date: 2018-08-24 15:08
 **/
public class MessageInfo {
	/**
	 * 消息ID
	 */
	private String msgId;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 应用号
	 */
	private String appId;
	/**
	 * 业务类型
	 */
	private String bizType;
	/**
	 * 业务号
	 */
	private String bizId;
	/**
	 * 业务内容
	 */
	private String bizContent;
	/**
	 * 时间戳
	 */
	private long timestamp;
	/**
	 * 重试次数
	 */
	private int retryCount;

	/**
	 * 消息ID
	 *
	 * @return
	 */
	public String getMsgId() {
		return msgId;
	}

	/**
	 * @param msgId 消息ID
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	/**
	 * 用户ID
	 *
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId 用户ID
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 应用号
	 *
	 * @return
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId 应用号
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 业务类型
	 *
	 * @return
	 */
	public String getBizType() {
		return bizType;
	}

	/**
	 * @param bizType 业务类型
	 */
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	/**
	 * 业务号
	 *
	 * @return
	 */
	public String getBizId() {
		return bizId;
	}

	/**
	 * @param bizId 业务号
	 */
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	/**
	 * 业务内容
	 *
	 * @return
	 */
	public String getBizContent() {
		return bizContent;
	}

	/**
	 * 业务内容
	 *
	 * @param bizContent
	 */
	public void setBizContent(String bizContent) {
		this.bizContent = bizContent;
	}

	/**
	 * 时间戳
	 *
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp 时间戳
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 重试次数
	 *
	 * @return
	 */
	public int getRetryCount() {
		return retryCount;
	}

	/**
	 * 重试次数
	 *
	 * @param retryCount
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
}
