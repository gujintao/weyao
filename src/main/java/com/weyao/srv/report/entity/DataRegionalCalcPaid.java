package com.weyao.srv.report.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 区域运营询价&成单
 * 
 * @author taoxiaoyan
 */
public class DataRegionalCalcPaid implements Serializable {

	private static final long serialVersionUID = -2620871470534753702L;

	/**
	 * @Fields _count : Mybatis 处理save or update 时使用
	 */
	private int _count;

	private Integer statisDate; // 统计日期，格式为yyyymmdd
	private Integer statisDateWeek; // 统计日期所在的周，格式形如201645
	private Integer statisDateMonth; // 统计日期所在的月，格式为yyyymm
	private Integer cityId; // 城市
	private long jgCalcCarNum; // 经管询价车数，有在经管询价的
	private long yydCalcCarNum; // 又一单询价车数，有在又一单询价的
	private long overallCalcCarNum; // 总询价车数，询过价的
	private long jgSubmitCarNum; // 经管提交订单车数，在经管提交的，含商业险的订单
	private long yydSubmitCarNum; // 又一单提交订单车数，在又一单提交的，含商业险的订单
	private long overallSubmitCarNum; // 总提交订单车数，含商业险的订单
	private long jgPaidCarNum; // 经管成单车数，含商业险的订单(垫付完成)
	private long yydPaidCarNum; // 又一单成单车数，含商业险的订单(垫付完成)
	private long onlyTCIPaidCarNum; // 单交强成单车数(垫付完成)
	private long overallPaidCarNum; // 总成单车数，含商业险的订单(垫付完成)
	private long monthPaidOrderNum; // 当月累计订单数量(垫付完成)
	private long monthPaidPremium; // 当月累计保单保费(垫付完成),精确到分
	private Timestamp updateTime; // 更新时间
	private Timestamp createTime; // 创建时间

	public DataRegionalCalcPaid() {
		super();
	}

	public final Integer getStatisDate() {
		return statisDate;
	}

	public final void setStatisDate(Integer statisDate) {
		this.statisDate = statisDate;
	}

	public final Integer getStatisDateWeek() {
		return statisDateWeek;
	}

	public final void setStatisDateWeek(Integer statisDateWeek) {
		this.statisDateWeek = statisDateWeek;
	}

	public final Integer getStatisDateMonth() {
		return statisDateMonth;
	}

	public final void setStatisDateMonth(Integer statisDateMonth) {
		this.statisDateMonth = statisDateMonth;
	}

	public final Integer getCityId() {
		return cityId;
	}

	public final void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public final long getJgCalcCarNum() {
		return jgCalcCarNum;
	}

	/** 经管询价车数，有在经管询价的 */
	public final void setJgCalcCarNum(long jgCalcCarNum) {
		this.jgCalcCarNum = jgCalcCarNum;
	}

	public final long getYydCalcCarNum() {
		return yydCalcCarNum;
	}

	/** 又一单询价车数，有在又一单询价的 */
	public final void setYydCalcCarNum(long yydCalcCarNum) {
		this.yydCalcCarNum = yydCalcCarNum;
	}

	public final long getOverallCalcCarNum() {
		return overallCalcCarNum;
	}

	/** 总询价车数，询过价的 */
	public final void setOverallCalcCarNum(long overallCalcCarNum) {
		this.overallCalcCarNum = overallCalcCarNum;
	}

	public final long getJgSubmitCarNum() {
		return jgSubmitCarNum;
	}

	/** 经管提交订单车数，在经管提交的，含商业险的订单 */
	public final void setJgSubmitCarNum(long jgSubmitCarNum) {
		this.jgSubmitCarNum = jgSubmitCarNum;
	}

	public final long getYydSubmitCarNum() {
		return yydSubmitCarNum;
	}

	/** 又一单提交订单车数，在又一单提交的，含商业险的订单 */
	public final void setYydSubmitCarNum(long yydSubmitCarNum) {
		this.yydSubmitCarNum = yydSubmitCarNum;
	}

	public final long getOverallSubmitCarNum() {
		return overallSubmitCarNum;
	}

	/** 总提交订单车数，含商业险的订单 */
	public final void setOverallSubmitCarNum(long overallSubmitCarNum) {
		this.overallSubmitCarNum = overallSubmitCarNum;
	}

	public final long getJgPaidCarNum() {
		return jgPaidCarNum;
	}

	/** 经管成单车数，含商业险的订单(垫付完成) */
	public final void setJgPaidCarNum(long jgPaidCarNum) {
		this.jgPaidCarNum = jgPaidCarNum;
	}

	public final long getYydPaidCarNum() {
		return yydPaidCarNum;
	}

	/** 又一单成单车数，含商业险的订单(垫付完成) */
	public final void setYydPaidCarNum(long yydPaidCarNum) {
		this.yydPaidCarNum = yydPaidCarNum;
	}

	public final long getOnlyTCIPaidCarNum() {
		return onlyTCIPaidCarNum;
	}

	/** 单交强成单车数(垫付完成) */
	public final void setOnlyTCIPaidCarNum(long onlyTCIPaidCarNum) {
		this.onlyTCIPaidCarNum = onlyTCIPaidCarNum;
	}

	public final long getOverallPaidCarNum() {
		return overallPaidCarNum;
	}

	/** 总成单车数，含商业险的订单(垫付完成) */
	public final void setOverallPaidCarNum(long overallPaidCarNum) {
		this.overallPaidCarNum = overallPaidCarNum;
	}

	public final long getMonthPaidOrderNum() {
		return monthPaidOrderNum;
	}

	/** 当月累计订单数量 */
	public final void setMonthPaidOrderNum(long monthPaidOrderNum) {
		this.monthPaidOrderNum = monthPaidOrderNum;
	}

	public final long getMonthPaidPremium() {
		return monthPaidPremium;
	}

	/** 当月累计保单保费(垫付完成),精确到分 */
	public final void setMonthPaidPremium(long monthPaidPremium) {
		this.monthPaidPremium = monthPaidPremium;
	}

	public final Timestamp getUpdateTime() {
		return updateTime;
	}

	public final void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public final Timestamp getCreateTime() {
		return createTime;
	}

	public final void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public final int get_count() {
		return _count;
	}

	public final void set_count(int _count) {
		this._count = _count;
	}

}
