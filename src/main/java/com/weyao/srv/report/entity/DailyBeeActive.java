package com.weyao.srv.report.entity;

import java.util.Date;

/**
 * 小蜜蜂活跃度统计POJO实体
 * @author dujingjing
 * @version 1.0
 */
public class DailyBeeActive {

	/*
	 * MAX(timeline.record_date) AS '日期',
	 */
	private Date recordDate;
	
	/*
	 * 'Overall' AS '类型',
	 */
	private String type;
	
	/*
	 * 'Overall' AS '区域',
	 */
	private String area;
	
	/* 
	 * count(1) AS '累计小蜜蜂',
	 */
	private Integer totalBeeCount;
	
	/*
	 * count(timeline.is_new) AS '新增小蜜蜂',
	 */
	private Integer newBeeCount;
	
	/*
	 * count(timeline.is_daya) AS '日活跃',
	 */
	private Integer dayActive;
	
	/*
	 * count(timeline.is_weeka) AS '周活跃',
	 */
	private Integer weekActive;
	
	/*
	 * count(timeline.is_montha) AS '月活跃',
	 */
	private Integer monthActive;

	/*
	 * count(timeline.is_daya)/count(1) AS '活跃度' ,
	 */
	private Float active;
	
	/*
	 * count(timeline.is_lost) AS '小蜜蜂流失数',
	 */
	private String lostBeeCount;
	
	/*
	 * count(timeline.is_recover) AS '小蜜蜂回流数'
	 */
	private String recoverBeeCount;

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer getTotalBeeCount() {
		return totalBeeCount;
	}

	public void setTotalBeeCount(Integer totalBeeCount) {
		this.totalBeeCount = totalBeeCount;
	}

	public Integer getNewBeeCount() {
		return newBeeCount;
	}

	public void setNewBeeCount(Integer newBeeCount) {
		this.newBeeCount = newBeeCount;
	}

	public Integer getDayActive() {
		return dayActive;
	}

	public void setDayActive(Integer dayActive) {
		this.dayActive = dayActive;
	}

	public Integer getWeekActive() {
		return weekActive;
	}

	public void setWeekActive(Integer weekActive) {
		this.weekActive = weekActive;
	}

	public Integer getMonthActive() {
		return monthActive;
	}

	public void setMonthActive(Integer monthActive) {
		this.monthActive = monthActive;
	}

	public Float getActive() {
		return active;
	}

	public void setActive(Float active) {
		this.active = active;
	}

	public String getLostBeeCount() {
		return lostBeeCount;
	}

	public void setLostBeeCount(String lostBeeCount) {
		this.lostBeeCount = lostBeeCount;
	}

	public String getRecoverBeeCount() {
		return recoverBeeCount;
	}

	public void setRecoverBeeCount(String recoverBeeCount) {
		this.recoverBeeCount = recoverBeeCount;
	}
	
	
}
