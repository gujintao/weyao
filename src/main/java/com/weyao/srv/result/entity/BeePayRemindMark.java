package com.weyao.srv.result.entity;

public class BeePayRemindMark {
	private long calcRecordItemId;
	private String supplierName;
	private String carNumber;
	private String mobile;
	private int realAmount;
	private long cid;
	private long orderId;
	private int cityId;
	private int sourceType;
	public BeePayRemindMark() {
		super();
	}

	public BeePayRemindMark(long calcRecordItemId, String supplierName, String carNumber, String mobile, int realAmount, long cid,
			long orderId, int cityId, int sourceType) {
		super();
		this.calcRecordItemId = calcRecordItemId;
		this.supplierName = supplierName;
		this.carNumber = carNumber;
		this.mobile = mobile;
		this.realAmount = realAmount;
		this.cid = cid;
		this.orderId = orderId;
		this.cityId = cityId;
		this.sourceType = sourceType;
	}
	public long getCalcRecordItemId() {
		return calcRecordItemId;
	}
	public void setCalcRecordItemId(long calcRecordItemId) {
		this.calcRecordItemId = calcRecordItemId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getCarNumber() {
		return carNumber;
	}
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getRealAmount() {
		return realAmount;
	}
	public void setRealAmount(int realAmount) {
		this.realAmount = realAmount;
	}
	public long getCid() {
		return cid;
	}
	public void setCid(long cid) {
		this.cid = cid;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
}
