package com.weyao.srv.report.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 区域运营报表总览
 * 
 * @author taoxiaoyan
 */
public class DataRegionalOperation implements Serializable {

	private static final long serialVersionUID = 1057142807598622625L;
	/** 
	 * @Fields _count : Mybatis 处理save or update 时使用
	 */ 
	private int _count;
	
	private Integer statisDate; // 统计日期，格式为yyyymmdd
	private Integer cityId; // 城市
	private long dayPaidCarNum; // 当日成单车辆数(垫付完成)
	private long dayPaidBusiCarNum; // 当日成单商业险车辆数(垫付完成)
	private long monthPaidCarNum; // 当月累计成单车辆数(垫付完成)
	private long monthPaidBusiCarNum; // 当月累计成单商业险车辆数(垫付完成)
	private long monthPaidOrderNum; // 当月累计订单数量
	private long monthPaidPremium; // 当月累计保单保费(垫付完成),精确到分
	private long monthCancelNum; // 月累计退保订单数
	private long monthCancelPremium; // 月累计退保保费,精确到分
	private long dayCalcCarNum; // 当日询价车辆数
	private long monthCalcCarNum; // 月累计询价车辆数
	private long beeTotalNum; // 累计小蜜蜂数
	private long yydBeeTotalNum; // 累计又一单小蜜蜂数
	private long dayBeeActivity; // 当日询价活跃数
	private long thirtyDaysBeeActivity; // 30天询价活跃数
	private long thirtyDaysOrderActivity; // 30天出单活跃数
	private long thirtyDaysVCIorderActivity; // 30天商业险出单活跃数
	private Timestamp updateTime; // 更新时间
	private Timestamp createTime; // 创建时间
	
	public DataRegionalOperation(Integer statisDate, Integer cityId, long dayPaidCarNum, long dayPaidBusiCarNum,
			long monthPaidCarNum, long monthPaidBusiCarNum, long monthPaidOrderNum, long monthPaidPremium,
			long monthCancelNum, long monthCancelPremium, long dayCalcCarNum, long monthCalcCarNum, long beeTotalNum,
			long yydBeeTotalNum, long dayBeeActivity, long thirtyDaysBeeActivity, long thirtyDaysOrderActivity,
			long thirtyDaysVCIorderActivity, Timestamp updateTime, Timestamp createTime) {
		super();
		this.statisDate = statisDate;
		this.cityId = cityId;
		this.dayPaidCarNum = dayPaidCarNum;
		this.dayPaidBusiCarNum = dayPaidBusiCarNum;
		this.monthPaidCarNum = monthPaidCarNum;
		this.monthPaidBusiCarNum = monthPaidBusiCarNum;
		this.monthPaidOrderNum = monthPaidOrderNum;
		this.monthPaidPremium = monthPaidPremium;
		this.monthCancelNum = monthCancelNum;
		this.monthCancelPremium = monthCancelPremium;
		this.dayCalcCarNum = dayCalcCarNum;
		this.monthCalcCarNum = monthCalcCarNum;
		this.beeTotalNum = beeTotalNum;
		this.yydBeeTotalNum = yydBeeTotalNum;
		this.dayBeeActivity = dayBeeActivity;
		this.thirtyDaysBeeActivity = thirtyDaysBeeActivity;
		this.thirtyDaysOrderActivity = thirtyDaysOrderActivity;
		this.thirtyDaysVCIorderActivity = thirtyDaysVCIorderActivity;
		this.updateTime = updateTime;
		this.createTime = createTime;
	}

	public DataRegionalOperation() {
		super();
	}

	public final Integer getStatisDate() {
		return statisDate;
	}

	public final void setStatisDate(Integer statisDate) {
		this.statisDate = statisDate;
	}

	public final Integer getCityId() {
		return cityId;
	}

	public final void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public final long getDayPaidCarNum() {
		return dayPaidCarNum;
	}

	/** 当日成单车辆数(垫付完成) */
	public final void setDayPaidCarNum(long dayPaidCarNum) {
		this.dayPaidCarNum = dayPaidCarNum;
	}

	public final long getDayPaidBusiCarNum() {
		return dayPaidBusiCarNum;
	}
	
	/** 当日成单商业险车辆数(垫付完成) */  
	public final void setDayPaidBusiCarNum(long dayPaidBusiCarNum) {
		this.dayPaidBusiCarNum = dayPaidBusiCarNum;
	}

	public final long getMonthPaidCarNum() {
		return monthPaidCarNum;
	}

	/** 当月累计成单车辆数(垫付完成) */  
	public final void setMonthPaidCarNum(long monthPaidCarNum) {
		this.monthPaidCarNum = monthPaidCarNum;
	}

	public final long getMonthPaidBusiCarNum() {
		return monthPaidBusiCarNum;
	}
	
	/** 当月累计成单商业险车辆数(垫付完成) */  
	public final void setMonthPaidBusiCarNum(long monthPaidBusiCarNum) {
		this.monthPaidBusiCarNum = monthPaidBusiCarNum;
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

	public final long getMonthCancelNum() {
		return monthCancelNum;
	}

	/** 月累计退保订单数 */  
	public final void setMonthCancelNum(long monthCancelNum) {
		this.monthCancelNum = monthCancelNum;
	}

	public final long getMonthCancelPremium() {
		return monthCancelPremium;
	}

	/** 月累计退保保费,精确到分 */  
	public final void setMonthCancelPremium(long monthCancelPremium) {
		this.monthCancelPremium = monthCancelPremium;
	}

	public final long getDayCalcCarNum() {
		return dayCalcCarNum;
	}

	/** 当日询价车辆数 */  
	public final void setDayCalcCarNum(long dayCalcCarNum) {
		this.dayCalcCarNum = dayCalcCarNum;
	}

	public final long getMonthCalcCarNum() {
		return monthCalcCarNum;
	}

	/** 月累计询价车辆数 */  
	public final void setMonthCalcCarNum(long monthCalcCarNum) {
		this.monthCalcCarNum = monthCalcCarNum;
	}

	public final long getBeeTotalNum() {
		return beeTotalNum;
	}

	/** 累计小蜜蜂数 */  
	public final void setBeeTotalNum(long beeTotalNum) {
		this.beeTotalNum = beeTotalNum;
	}

	public final long getYydBeeTotalNum() {
		return yydBeeTotalNum;
	}

	/** 累计又一单小蜜蜂数 */  
	public final void setYydBeeTotalNum(long yydBeeTotalNum) {
		this.yydBeeTotalNum = yydBeeTotalNum;
	}

	public final long getDayBeeActivity() {
		return dayBeeActivity;
	}

	/** 当日询价活跃数 */ 
	public final void setDayBeeActivity(long dayBeeActivity) {
		this.dayBeeActivity = dayBeeActivity;
	}

	public final long getThirtyDaysBeeActivity() {
		return thirtyDaysBeeActivity;
	}

	/** 30天询价活跃数 */ 
	public final void setThirtyDaysBeeActivity(long thirtyDaysBeeActivity) {
		this.thirtyDaysBeeActivity = thirtyDaysBeeActivity;
	}

	public final long getThirtyDaysOrderActivity() {
		return thirtyDaysOrderActivity;
	}

	/** 30天出单活跃数 */ 
	public final void setThirtyDaysOrderActivity(long thirtyDaysOrderActivity) {
		this.thirtyDaysOrderActivity = thirtyDaysOrderActivity;
	}

	public final long getThirtyDaysVCIorderActivity() {
		return thirtyDaysVCIorderActivity;
	}

	/**  30天商业险出单活跃数 */ 
	public final void setThirtyDaysVCIorderActivity(long thirtyDaysVCIorderActivity) {
		this.thirtyDaysVCIorderActivity = thirtyDaysVCIorderActivity;
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

	@Override
	public String toString() {
		return "DataRegionalOperation [statisDate=" + statisDate + ", cityId=" + cityId + ", dayPaidCarNum="
				+ dayPaidCarNum + ", dayPaidBusiCarNum=" + dayPaidBusiCarNum + ", monthPaidCarNum=" + monthPaidCarNum
				+ ", monthPaidBusiCarNum=" + monthPaidBusiCarNum + ", monthPaidOrderNum=" + monthPaidOrderNum
				+ ", monthPaidPremium=" + monthPaidPremium + ", monthCancelNum=" + monthCancelNum
				+ ", monthCancelPremium=" + monthCancelPremium + ", dayCalcCarNum=" + dayCalcCarNum
				+ ", monthCalcCarNum=" + monthCalcCarNum + ", beeTotalNum=" + beeTotalNum + ", yydBeeTotalNum="
				+ yydBeeTotalNum + ", dayBeeActivity=" + dayBeeActivity + ", thirtyDaysBeeActivity="
				+ thirtyDaysBeeActivity + ", thirtyDaysOrderActivity=" + thirtyDaysOrderActivity
				+ ", thirtyDaysVCIorderActivity=" + thirtyDaysVCIorderActivity + "]";
	}

}
