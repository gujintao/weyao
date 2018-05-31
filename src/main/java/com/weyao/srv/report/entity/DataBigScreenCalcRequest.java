package com.weyao.srv.report.entity;

import java.io.Serializable;

/**
 * 大屏实时算价请求趋势数据
 * 
 * @author taoxiaoyan
 */
public class DataBigScreenCalcRequest implements Serializable {

	private static final long serialVersionUID = 5047131528000945675L;

	/**
	 * @Fields _count : Mybatis 处理save or update 时使用
	 */
	private int _count;

	private String statisDate; // 统计日期，格式为yyyymmdd
	private Integer timeInterval; // 时间分段间隔，分钟
	private Integer segmentStage; // 时间分割阶段
	private Integer calcRequestTimes; // 相应的时间段内的请求次数

	public final int get_count() {
		return _count;
	}

	public final void set_count(int _count) {
		this._count = _count;
	}

	public final String getStatisDate() {
		return statisDate;
	}

	public final void setStatisDate(String statisDate) {
		this.statisDate = statisDate;
	}

	public final Integer getTimeInterval() {
		return timeInterval;
	}

	public final void setTimeInterval(Integer timeInterval) {
		this.timeInterval = timeInterval;
	}

	public final Integer getSegmentStage() {
		return segmentStage;
	}

	public final void setSegmentStage(Integer segmentStage) {
		this.segmentStage = segmentStage;
	}

	public final Integer getCalcRequestTimes() {
		return calcRequestTimes;
	}

	public final void setCalcRequestTimes(Integer calcRequestTimes) {
		this.calcRequestTimes = calcRequestTimes;
	}

}
