package com.weyao.srv.report.entity;

import java.util.Date;

/**
 * 每日下单POJO
 * @author dujingjing
 *
 */
public class DailyOrder extends BaseBeeReocrd{

	/*
	 * insurance_id，保单ID
	 */
	private int insuranceId;

	/*
	 * plate_number，车牌号
	 */
	private String plateNumber;
	
	/*
	 * order_time，下单时间
	 */
	private Date orderTime;

	/*
	 * supplier_name，保险公司
	 */
	private String supplierName;
	
	/*
	 * passport_name，运维人员
	 */
	private String passportName;

	public int getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(int insuranceId) {
		this.insuranceId = insuranceId;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
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
