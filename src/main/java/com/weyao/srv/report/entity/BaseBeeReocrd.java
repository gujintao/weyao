package com.weyao.srv.report.entity;

import java.util.Date;

public class BaseBeeReocrd {

	/*
	 * 日期
	 */
	private Date recordDate;
	
	/*
	 * 小蜜蜂微信号
	 */
	private String nickName;
	
	/*
	 * 小蜜蜂姓名
	 */
	private String beeName;

	/*
	 * 手机号
	 */
	private String mobile;

	/*
	 * 城市名称
	 */
	private String cityName;
	
	/*
	 * 区域名称
	 */
	private String areaName;
	
	/*
	 * 拓展员
	 */
	private String operName;
	
	/*
	 * 小蜜蜂来源
	 */
	private String fromSource;
	
	/*
	 * 小蜜蜂类型
	 */
	private String beeType;

	/*
	 * 小蜜蜂状态度
	 */
	private String beeStatus;

	public String getBeeStatus() {
		return beeStatus;
	}

	public void setBeeStatus(String beeStatus) {
		this.beeStatus = beeStatus;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecord_date(Date recordDate) {
		this.recordDate = recordDate;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getBeeName() {
		return beeName;
	}

	public void setBeeName(String beeName) {
		this.beeName = beeName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public String getFromSource() {
		return fromSource;
	}

	public void setFromSource(String fromSource) {
		this.fromSource = fromSource;
	}

	public String getBeeType() {
		return beeType;
	}

	public void setBeeType(String beeType) {
		this.beeType = beeType;
	}
}
