package com.weyao.srv.report.entity;

import java.util.Date;

public class LBTotalRecord {

	private Date recordDate;
	
	private String nickName;
	
	private String beeName;

	private String mobile;

	private String cityName;
	
	private String areaName;
	
	private String operName;
	
	private String fromSource;
	
	private String beeType;
	
	private String calcFrom0Status;
	
	private String calcFromaStatus;

	private String beeStatus;

	public String getCalcFrom0Status() {
		return calcFrom0Status;
	}

	public void setCalcFrom0Status(String calcFrom0Status) {
		this.calcFrom0Status = calcFrom0Status;
	}

	public String getCalcFromaStatus() {
		return calcFromaStatus;
	}

	public void setCalcFromaStatus(String calcFromaStatus) {
		this.calcFromaStatus = calcFromaStatus;
	}

	public String getBeeStatus() {
		return beeStatus;
	}

	public void setBeeStatus(String beeStatus) {
		this.beeStatus = beeStatus;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
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
