package com.weyao.srv.report.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 又一单先付明细报表
 * 
 * @version 1.0
 * @author Tao Xiaoyan
 * @date 创建时间：2016年12月8日 上午10:16:34
 */
public class YydOnlinePaymentReport implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long orderId; // 订单号
	private String carNumber; // 车牌号
	private Date paymentTime; // 支付时间
	private String payStyle; // 支付方式
	private Float commission; // 佣金金额(元)
	private String opName; // 下单人
	private String sourceType; // 订单来源
	private String supplierName; // 保司名称
	private String addressMsg; // 详细地址
	private String mobile; // 收货人手机号
	private String recipientName; // 收货人姓名
	private String insuranceCity; // 投保城市

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}

	public String getPayStyle() {
		return payStyle;
	}

	public void setPayStyle(String payStyle) {
		this.payStyle = payStyle;
	}

	public Float getCommission() {
		return commission;
	}

	public void setCommission(Float commission) {
		this.commission = commission;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getAddressMsg() {
		return addressMsg;
	}

	public void setAddressMsg(String addressMsg) {
		this.addressMsg = addressMsg;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getInsuranceCity() {
		return insuranceCity;
	}

	public void setInsuranceCity(String insuranceCity) {
		this.insuranceCity = insuranceCity;
	}

}
