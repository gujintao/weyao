package com.weyao.srv.report.entity;

import java.io.Serializable;

/**
 * 全局销售日报表
 * @author taoxiaoyan
 */
public class DaySaleReport implements Serializable {

	private static final long serialVersionUID = 8311821919235487117L;
	
	private String currentDate; // 日期，为月统计的时候，该值为“月累计”
	private String angleFirst;  // 维度值按中划线拆分后的第一个
	private String angleSecond; // 维度值按中划线拆分后的第二个
	private String angleThird; // 维度值按中划线拆分后的第三个
	private Integer busiCount; // 商业险保单销售量
	private Double busiSum; // 商业险保单销售额
	private Integer orderCount; // 订单销售量
	private Double orderSum; // 订单销售总金额 
	private String busiTbRate; // 商业险保单数量同比比增长率
	private String busiCountHbRate; // 商业险保单数量环比增长率
	private String busiSumHbRate; // 商业险保单金额环比增长率
	private String orderCountHbRate; // 订单销售量环比增长率
	private String orderSumHbRate; // 订单金额环比增长率
	private Integer cancleCount; // 当月垫付后取消或退保的商业险数量
	private Double cancleSum; // 当月垫付后取消或退保的商业险数量

	public String getAngleFirst() {
		return angleFirst;
	}

	public void setAngleFirst(String angleFirst) {
		this.angleFirst = angleFirst;
	}

	public String getAngleSecond() {
		return angleSecond;
	}

	public void setAngleSecond(String angleSecond) {
		this.angleSecond = angleSecond;
	}

	public Integer getBusiCount() {
		return busiCount;
	}

	public void setBusiCount(Integer busiCount) {
		this.busiCount = busiCount;
	}

	public Double getBusiSum() {
		return busiSum;
	}

	public void setBusiSum(Double busiSum) {
		this.busiSum = busiSum;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public Double getOrderSum() {
		return orderSum;
	}

	public void setOrderSum(Double orderSum) {
		this.orderSum = orderSum;
	}

	public String getBusiTbRate() {
		return busiTbRate;
	}

	public void setBusiTbRate(String busiTbRate) {
		this.busiTbRate = busiTbRate;
	}

	public String getBusiCountHbRate() {
		return busiCountHbRate;
	}

	public void setBusiCountHbRate(String busiCountHbRate) {
		this.busiCountHbRate = busiCountHbRate;
	}

	public String getBusiSumHbRate() {
		return busiSumHbRate;
	}

	public void setBusiSumHbRate(String busiSumHbRate) {
		this.busiSumHbRate = busiSumHbRate;
	}

	public String getOrderCountHbRate() {
		return orderCountHbRate;
	}

	public void setOrderCountHbRate(String orderCountHbRate) {
		this.orderCountHbRate = orderCountHbRate;
	}

	public String getOrderSumHbRate() {
		return orderSumHbRate;
	}

	public void setOrderSumHbRate(String orderSumHbRate) {
		this.orderSumHbRate = orderSumHbRate;
	}

	public Integer getCancleCount() {
		return cancleCount;
	}

	public void setCancleCount(Integer cancleCount) {
		this.cancleCount = cancleCount;
	}

	public Double getCancleSum() {
		return cancleSum;
	}

	public void setCancleSum(Double cancleSum) {
		this.cancleSum = cancleSum;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getAngleThird() {
		return angleThird;
	}

	public void setAngleThird(String angleThird) {
		this.angleThird = angleThird;
	}

	@Override
	public String toString() {
		return "DaySaleReport [angleFirst=" + angleFirst + ", angleSecond=" + angleSecond + ", busiCount=" + busiCount
				+ ", busiSum=" + busiSum + ", orderCount=" + orderCount + ", orderSum=" + orderSum + ", busiTbRate="
				+ busiTbRate + ", busiCountHbRate=" + busiCountHbRate + ", busiSumHbRate=" + busiSumHbRate
				+ ", orderCountHbRate=" + orderCountHbRate + ", orderSumHbRate=" + orderSumHbRate + ", cancleCount="
				+ cancleCount + ", cancleSum=" + cancleSum + "]";
	}
}
