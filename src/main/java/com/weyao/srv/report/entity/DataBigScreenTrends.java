package com.weyao.srv.report.entity;

import java.io.Serializable;

/**
 * 大屏当日访问趋势
 * 
 * @author taoxiaoyan
 */
public class DataBigScreenTrends implements Serializable {

	private static final long serialVersionUID = 8626442954637490818L;

	/**
	 * @Fields _count : Mybatis 处理save or update 时使用
	 */
	private int _count;

	private String statisDate; // 统计日期，格式为yyyymmdd
	private long biddingScale; // 报价规模，单位为分
	private Integer calcTimes; // 报价次数，单位为次
	private Integer serviceOwner; // 服务车主，单位为人
	private long interactionQuantity; // 交互量，单位为次
	private long yydCalcTimes; // 又一单算价次数，单位为次
	private long jgCalcTimes; // 经管算价次数，单位为次
	private long crmCalcTimes; // 量子算价次数，单位为次
	private long newOrderNum; // 新建订单数，单位为次
	private long finishedOrderNum; // 完成订单数，单位为次

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

	public final long getBiddingScale() {
		return biddingScale;
	}

	public final void setBiddingScale(long biddingScale) {
		this.biddingScale = biddingScale;
	}

	public final Integer getCalcTimes() {
		return calcTimes;
	}

	public final void setCalcTimes(Integer calcTimes) {
		this.calcTimes = calcTimes;
	}

	public final Integer getServiceOwner() {
		return serviceOwner;
	}

	public final void setServiceOwner(Integer serviceOwner) {
		this.serviceOwner = serviceOwner;
	}

	public final long getInteractionQuantity() {
		return interactionQuantity;
	}

	public final void setInteractionQuantity(long interactionQuantity) {
		this.interactionQuantity = interactionQuantity;
	}

	public final long getYydCalcTimes() {
		return yydCalcTimes;
	}

	public final void setYydCalcTimes(long yydCalcTimes) {
		this.yydCalcTimes = yydCalcTimes;
	}

	public final long getJgCalcTimes() {
		return jgCalcTimes;
	}

	public final void setJgCalcTimes(long jgCalcTimes) {
		this.jgCalcTimes = jgCalcTimes;
	}

	public final long getCrmCalcTimes() {
		return crmCalcTimes;
	}

	public final void setCrmCalcTimes(long crmCalcTimes) {
		this.crmCalcTimes = crmCalcTimes;
	}

	public final long getNewOrderNum() {
		return newOrderNum;
	}

	public final void setNewOrderNum(long newOrderNum) {
		this.newOrderNum = newOrderNum;
	}

	public final long getFinishedOrderNum() {
		return finishedOrderNum;
	}

	public final void setFinishedOrderNum(long finishedOrderNum) {
		this.finishedOrderNum = finishedOrderNum;
	}

	@Override
	public String toString() {
		return "DataBigScreenTrends [_count=" + _count + ", statisDate=" + statisDate + ", biddingScale=" + biddingScale
				+ ", calcTimes=" + calcTimes + ", serviceOwner=" + serviceOwner + ", interactionQuantity="
				+ interactionQuantity + ", yydCalcTimes=" + yydCalcTimes + ", jgCalcTimes=" + jgCalcTimes
				+ ", crmCalcTimes=" + crmCalcTimes + ", newOrderNum=" + newOrderNum + ", finishedOrderNum="
				+ finishedOrderNum + "]";
	}

}
