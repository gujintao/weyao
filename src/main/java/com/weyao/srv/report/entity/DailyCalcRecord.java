package com.weyao.srv.report.entity;

import java.util.Date;

/**
 * 每日询价明细数据报表
 * @author dujingjing
 *
 */
public class DailyCalcRecord extends BaseBeeReocrd{
	
	/*
	 * 报价来源
	 */
	private String calcFromSource;
	
	/*
	 * car.real_price AS calc_amount,
	 */
	private Float calcAmount;
	
	/*
	 * passport_name，运维人员
	 */
	private String passportName;
	
	/*
	 * plate_number，车牌号
	 */
	private String plateNumber;
	
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

	/*
	 * order_time，下单时间
	 */
	private Date orderTime;

	/*
	 * supplier_name，保险公司
	 */
	private String supplierName;

	public String getCalcFromSource() {
		return calcFromSource;
	}

	public void setCalcFromSource(String calcFromSource) {
		this.calcFromSource = calcFromSource;
	}

	public Float getCalcAmount() {
		return calcAmount;
	}

	public void setCalcAmount(Float calcAmount) {
		this.calcAmount = calcAmount;
	}

	public String getPassportName() {
		return passportName;
	}

	public void setPassportName(String passportName) {
		this.passportName = passportName;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
}
