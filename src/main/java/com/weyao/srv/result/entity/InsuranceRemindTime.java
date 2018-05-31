package com.weyao.srv.result.entity;

public class InsuranceRemindTime {
	private long calcRecordItemId;
	private String remindTime;
	public InsuranceRemindTime() {
		super();
	}
	public InsuranceRemindTime(long calcRecordItemId, String remindTime) {
		super();
		this.calcRecordItemId = calcRecordItemId;
		this.remindTime = remindTime;
	}
	public long getCalcRecordItemId() {
		return calcRecordItemId;
	}
	public void setCalcRecordItemId(long calcRecordItemId) {
		this.calcRecordItemId = calcRecordItemId;
	}
	public String getRemindTime() {
		return remindTime;
	}
	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
	}
}
