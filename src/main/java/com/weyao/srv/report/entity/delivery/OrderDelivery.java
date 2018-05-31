package com.weyao.srv.report.entity.delivery;

import java.util.Date;

/**
 * 配送报表数据POJO类
 * @author dujingjing
 *
 */
public class OrderDelivery {

	/*
	 * 日期，record_date
	 */
	private Date recordDate;

	/*
	 * 城市，city_name
	 */
	private String cityName;
	
	/*
	 * 订单号，order_id
	 */
	private Long orderId;
	
	/*
	 * 配送方式
	 */
	private String deliveryWay;

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getDeliveryWay() {
		return deliveryWay;
	}

	public void setDeliveryWay(String deliveryWay) {
		this.deliveryWay = deliveryWay;
	}
}
