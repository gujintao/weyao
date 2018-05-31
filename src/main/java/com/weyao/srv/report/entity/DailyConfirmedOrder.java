package com.weyao.srv.report.entity;

import java.util.Date;

/**
 * 每日确认下单实体
 * @author dujingjing
 *
 */
public final class DailyConfirmedOrder {

	/*
	 * A.insurance_id AS '保单ID',
	 */
	private Integer insuranceId;
	
	/*
	 * A.today AS '日期',
	 */
	private Date today;
	
	/*
	 * A.source AS '活跃来源',
	 */
	private String source;
	
	/*
	 * B.nick_name AS '小蜜蜂微信号',
	 */
	private String nickName;
	
	/*
	 * B.mobile AS '小蜜蜂手机号',
	 */
	private String mobile;
	
	/*
	 * city.`name` AS '城市',
	 */
	private String cityName;
	
	/* 
	 * area.`name` AS '区域',
	 */
	private String areaName;
	
	/* 
	 * B.manager_name AS '拓展员',
	 */
	private String managerName;
	
	/* 
	 * C.batch_name AS '渠道',
	 */
	private String batchName;
	
	/* 
	 * C.plate_number AS '车牌号',
	 */
	private String plateNumber;
	
	/* 
	 * A.create_time AS '下单时间',
	 */
	private Date createTime;
	
	/* 
	 * A.supplier_name AS '保险公司',
	 */
	private String supplierName;
	
	/* 
	 * A.passport_name AS '运维人员'
	 */
	private String passportName;

	public Integer getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(Integer insuranceId) {
		this.insuranceId = insuranceId;
	}

	public Date getToday() {
		return today;
	}

	public void setToday(Date today) {
		this.today = today;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getPassportName() {
		return passportName;
	}

	public void setPassportName(String passportName) {
		this.passportName = passportName;
	}
}
