package com.weyao.srv.report.entity;

/**
 * 每日成单明细数据报表
 * @author dujingjing
 *
 */
public class DailyDianfuRecord extends DailyOrder{
	
	/*
	 * 报价来源
	 */
	private String calcFromSource;
	
	/*
	 * FORMAT(tod.business_amount/100, 2) AS business_amount,
	 */
	private Float businessAmount;
	
	/*
	 * FORMAT((tod.force_amount - tod.tax_fee)/100, 0) AS force_discount_amount,
	 */
	private Float forceDiscountAmount;
	
	/*
	 * FORMAT(tod.tax_fee/100, 0) AS vehicle_tax,
	 */
	private Float vehicleTax;
	
	/*
	 * FORMAT((tod.business_amount + tod.force_amount)/100, 2) AS total_amount,
	 */
	private Float totalAmount;
	
	/*
	 * FORMAT(toder.real_amount/100, 2) AS real_amount
	 */
	private Float realAmount;
	
	/*
	 * 商业险状态
	 */
	private String businessStatusName;
	
	/*
	 * 交强险状态
	 */
	private String forceStatusName;
	
	/*
	 * 运维人员
	 */
	private String operator;
	
	private int time;
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public Float getForceDiscountAmount() {
		return forceDiscountAmount;
	}

	public void setForceDiscountAmount(Float forceDiscountAmount) {
		this.forceDiscountAmount = forceDiscountAmount;
	}

	public Float getVehicleTax() {
		return vehicleTax;
	}

	public void setVehicleTax(Float vehicleTax) {
		this.vehicleTax = vehicleTax;
	}

	public Float getRealAmount() {
		return realAmount;
	}

	public void setRealAmount(Float realAmount) {
		this.realAmount = realAmount;
	}

	public String getCalcFromSource() {
		return calcFromSource;
	}

	public void setCalcFromSource(String calcFromSource) {
		this.calcFromSource = calcFromSource;
	}

	public Float getBusinessAmount() {
		return businessAmount;
	}

	public void setBusinessAmount(Float businessAmount) {
		this.businessAmount = businessAmount;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getBusinessStatusName() {
		return businessStatusName;
	}

	public void setBusinessStatusName(String businessStatusName) {
		this.businessStatusName = businessStatusName;
	}

	public String getForceStatusName() {
		return forceStatusName;
	}

	public void setForceStatusName(String forceStatusName) {
		this.forceStatusName = forceStatusName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
