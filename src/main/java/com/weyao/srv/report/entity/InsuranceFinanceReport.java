package com.weyao.srv.report.entity;

import java.io.Serializable;
import java.util.Date;

/** 
  * 财务垫付报表
  * @version 1.0
  * @author  Tao Xiaoyan
  * @date 创建时间：2016年9月18日 上午10:16:34
  */
public class InsuranceFinanceReport implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private Long orderId;
    private String dianfuDate;
    private String dianfu;
    private Date orderCreateTime;
    private String channelType;
    private String channelName;
    private String forceActivityName;
    private String businessActivityName;
    private Integer supplierId;
    private String supplierName;
    private String opName;
    private String carNumber;
    private String insurancePerson;
    private String forceNumber;
    private String businessNumber;
    private Float forceAmount;
    private Float taxAmount;
    private Float businessAmount;
    private Double insuranceReviewedAmount;
    private Float dianfuAmount;
    private Float forceZhekou;
    private Float businessZhekou;
    private Float forceZhekouAmount;
    private Float businessZhekouAmount;
    private Float forceCouponAmount;
    private Float businessCouponAmount;
    private Float realAmount;


	private Float orderExtraAmount;
	private Float orderOutcome;
	private Float orderUserPay;



	private Float receiveAmount;
    private Float chaeAmount;
    private String wancheng;
	private Float forceReward;
	private Float businessReward;
    private String lipin;
    private String statusName;
    private Integer insuranceStatus;
    private String flowDetail;
    private Integer cityId;
    private String cityName;
    private Integer versionId;
    private Date createTime;
    private Date payTime;
    private String payStyle;
    private Date flowFinishTime;
    private String oemName;
    private String oemPaymentMode;
    private String outTradeNo;
    private Float forceCommissionAmount;
    private Float businessCommissionAmount;
    private String forceRuleFormula;
    private String businessRuleFormula;
    private String forceCommissionRate;
    private String businessCommissionRate;
    private String oemCode;
    private String oemAgencyName;
    private Float orderOriginalTotalAmount;
    private Float orderOtherAmount;
    private Float forceInsuranceOperationsAmount;
    private Float businessInsuranceOperationsAmount;
    private String forceInsuranceOperationsFormula;
    private String businessInsuranceOperationsFormula;
    private String forceInsuranceOperationsPercentage;
    private String businessInsuranceOperationsPercentage;
    private Float insuranceProductsPayFirstAmount;
    private String expressName;
    private Integer deliveryTimes;
    private String orderSourceType;
    private Date forceInsuranceStartDate;
    private Date businessInsuranceStartDate;
    private Date forceInsuranceEndDate;
    private Date businessInsuranceEndDate;

    private String signingCompanyName;
    private Long cid;
    private String dianfuComment;


    /**
     * 获取主键id
     * @return 主键id
     */
    public String getId() {
        return id;
    }
    
    /**
     * 设置主键id
     * @param id 主键id
     */
    public void setId(String id) {
        this.id = id;
    }
    
     
    /**
     * 获取订单id
     * @return 订单id
     */
    public Long getOrderId() {
		return orderId;
	}

	/**
	 * 设置订单id
	 * @param orderId 订单id
	 */
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public final String getDianfuDate() {
		return dianfuDate;
	}

	public final void setDianfuDate(String dianfuDate) {
		this.dianfuDate = dianfuDate;
	}

	/**
     * 获取垫付时间
     * @return 垫付时间
     */
    public String getDianfu() {
		return dianfu;
	}

	/**
	 * 设置垫付时间
	 * @param dianfu 垫付时间
	 */
	public void setDianfu(String dianfu) {
		this.dianfu = dianfu;
	}

	/**
	 * 获取订单生成时间
	 * @return 订单生成时间
	 */
	public Date getOrderCreateTime() {
		return orderCreateTime;
	}

	/**
	 * 设置订单生成时间
	 * @param orderCreateTime 订单生成时间
	 */
	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	/**
	 * 获取渠道类型
	 * @return 渠道类型
	 */
	public String getChannelType() {
		return channelType;
	}

	/**
	 * 设置渠道类型
	 * @param channelType 渠道类型
	 */
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	/**
	 * 获取渠道名称
	 * @return 渠道名称
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * 设置渠道名称
	 * @param channelName 渠道名称
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * 获取交强险活动名称
	 * @return 交强险活动名称
	 */
	public String getForceActivityName() {
		return forceActivityName;
	}

	/**
	 * 设置交强险活动名称
	 * @param forceActivityName 交强险活动名称
	 */
	public void setForceActivityName(String forceActivityName) {
		this.forceActivityName = forceActivityName;
	}

	/**
	 * 获取商业险活动名称
	 * @return 商业险活动名称
	 */
	public String getBusinessActivityName() {
		return businessActivityName;
	}

	/**
	 * 设置商业险活动名称
	 * @param businessActivityName 商业险活动名称
	 */
	public void setBusinessActivityName(String businessActivityName) {
		this.businessActivityName = businessActivityName;
	}

	/**
	 * 获取保险公司ID
	 * @return 保险公司ID
	 */
	public Integer getSupplierId() {
		return supplierId;
	}
	
	/**
	 * 设置保险公司ID
	 * @param supplierId 保险公司ID
	 */
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * 获取保险公司
	 * @return 保险公司
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * 设置保险公司
	 * @param supplierName 保险公司
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * 获取坐席名称
	 * @return 坐席名称
	 */
	public String getOpName() {
		return opName;
	}

	/**
	 * 设置坐席名称
	 * @param opName 坐席名称
	 */
	public void setOpName(String opName) {
		this.opName = opName;
	}
	
	/**
     * 获取车牌号
     * @return 车牌号
     */
    public String getCarNumber() {
        return carNumber;
    }
    
    /**
     * 设置车牌号
     * @param carNumber 车牌号
     */
    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

	/**
	 * 获取被保人
	 * @return 被保人
	 */
	public String getInsurancePerson() {
		return insurancePerson;
	}
	
	/**
	 * 设置被保人
	 * @param insuranceperson 被保人
	 */
	public void setInsurancePerson(String insurancePerson) {
		this.insurancePerson = insurancePerson;
	}

	/**
	 * 获取交强险交易号
	 * @return 交强险交易号
	 */
	public String getForceNumber() {
		return forceNumber;
	}

	/**
	 * 设置交强险交易号
	 * @param forceNumber 交强险交易号
	 */
	public void setForceNumber(String forceNumber) {
		this.forceNumber = forceNumber;
	}

	/**
	 * 获取商业险交易号
	 * @return 商业险交易号
	 */
	public String getBusinessNumber() {
		return businessNumber;
	}

	/**
	 * 设置商业险交易号
	 * @param businessNumber 商业险交易号
	 */
	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	/**
	 * 获取交强险金额
	 * @return 交强险金额
	 */
	public Float getForceAmount() {
		return forceAmount;
	}

	/**
	 * 设置交强险金额
	 * @param forceAmount 交强险金额
	 */
	public void setForceAmount(Float forceAmount) {
		this.forceAmount = forceAmount;
	}

	/**
	 * 获取车船税金额
	 * @return 车船税金额
	 */
	public Float getTaxAmount() {
		return taxAmount;
	}

	/**
	 * 设置车船税金额
	 * @param taxAmount 车船税金额
	 */
	public void setTaxAmount(Float taxAmount) {
		this.taxAmount = taxAmount;
	}

	/**
	 * 获取商业险金额
	 * @return 商业险金额
	 */
	public Float getBusinessAmount() {
		return businessAmount;
	}

	/**
	 * 设置商业险金额
	 * @param businessAmount 商业险金额
	 */
	public void setBusinessAmount(Float businessAmount) {
		this.businessAmount = businessAmount;
	}

	/**
	 * 获取垫付金额
	 * @return 垫付金额
	 */
	public Float getDianfuAmount() {
		return dianfuAmount;
	}

	/**
	 * 设置垫付金额
	 * @param dianfuAmount 垫付金额
	 */
	public void setDianfuAmount(Float dianfuAmount) {
		this.dianfuAmount = dianfuAmount;
	}
	
	/**
	 * 获取交强险折扣率 
	 * @return 交强险折扣率
	 */
	public Float getForceZhekou() {
		return forceZhekou;
	}

	/**
	 * 设置交强险折扣率
	 * @param forceZhekou 交强险折扣率
	 */
	public void setForceZhekou(Float forceZhekou) {
		this.forceZhekou = forceZhekou;
	}

	/**
	 * 获取商业险折扣率
	 * @return 商业险折扣率
	 */
	public Float getBusinessZhekou() {
		return businessZhekou;
	}

	/**
	 * 设置商业险折扣率
	 * @param businessZhekou 商业险折扣率
	 */
	public void setBusinessZhekou(Float businessZhekou) {
		this.businessZhekou = businessZhekou;
	}

	/**
	 * 获取交强险折扣金额
	 * @return 交强险折扣金额
	 */
	public Float getForceZhekouAmount() {
		return forceZhekouAmount;
	}

	/**
	 * 获取交强险折扣金额
	 * @param forceZhekouAmount 交强险折扣金额
	 */
	public void setForceZhekouAmount(Float forceZhekouAmount) {
		this.forceZhekouAmount = forceZhekouAmount;
	}

	/**
	 * 获取商业险折扣金额
	 * @return 商业险折扣金额
	 */
	public Float getBusinessZhekouAmount() {
		return businessZhekouAmount;
	}

	/**
	 * 设置商业险折扣金额
	 * @param businessZhekouAmount 商业险折扣金额
	 */
	public void setBusinessZhekouAmount(Float businessZhekouAmount) {
		this.businessZhekouAmount = businessZhekouAmount;
	}

	/**
	 * 获取交强险优惠券金额
	 * @return 交强险优惠券金额
	 */
	public Float getForceCouponAmount() {
		return forceCouponAmount;
	}

	/**
	 * 设置交强险优惠券金额
	 * @param forceCouponAmount 交强险优惠券金额
	 */
	public void setForceCouponAmount(Float forceCouponAmount) {
		this.forceCouponAmount = forceCouponAmount;
	}

	/**
	 * 获取商业险优惠券金额
	 * @return 商业险优惠券金额
	 */
	public Float getBusinessCouponAmount() {
		return businessCouponAmount;
	}

	/**
	 * 设置商业险优惠券金额
	 * @param businessCouponAmount 商业险优惠券金额
	 */
	public void setBusinessCouponAmount(Float businessCouponAmount) {
		this.businessCouponAmount = businessCouponAmount;
	}

	/**
	 * 获取客户应付金额
	 * @return 客户应付金额
	 */
	public Float getRealAmount() {
		return realAmount;
	}

	/**
	 * 设置客户应付金额
	 * @param realAmount 客户应付金额
	 */
	public void setRealAmount(Float realAmount) {
		this.realAmount = realAmount;
	}

	/**
	 * 获取回款金额
	 * @return 回款金额
	 */
	public Float getReceiveAmount() {
		return receiveAmount;
	}

	/**
	 * 设置回款金额
	 * @param receiveAmount 回款金额
	 */
	public void setReceiveAmount(Float receiveAmount) {
		this.receiveAmount = receiveAmount;
	}

	/**
	 * 获取回款差额
	 * @return 回款差额
	 */
	public Float getChaeAmount() {
		return chaeAmount;
	}

	/**
	 * 设置回款差额
	 * @param chaeAmount 回款差额
	 */
	public void setChaeAmount(Float chaeAmount) {
		this.chaeAmount = chaeAmount;
	}

	/**
	 * 获取回款日期
	 * @return 回款日期
	 */
	public String getWancheng() {
		return wancheng;
	}

	/**
	 * 设置回款日期
	 * @param wancheng 回款日期
	 */
	public void setWancheng(String wancheng) {
		this.wancheng = wancheng;
	}

	/**
	 * 获取促销礼品（油卡等）
	 * @return 促销礼品（油卡等）
	 */
	public String getLipin() {
		return lipin;
	}

	/**
	 * 设置促销礼品（油卡等）
	 * @param lipin 促销礼品（油卡等）
	 */
	public void setLipin(String lipin) {
		this.lipin = lipin;
	}

	/**
	 * 获取保单状态
	 * @return 保单状态
	 */
	public String getStatusName() {
		return statusName;
	}

	/**
	 * 设置保单状态
	 * @param statusName 保单状态
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	/**
	 * 获取保单状态
	 * @return 保单状态
	 */
	public Integer getInsuranceStatus() {
		return insuranceStatus;
	}

	/**
	 * 设置保单状态
	 * @param insuranceStatus 保单状态
	 */
	public void setInsuranceStatus(Integer insuranceStatus) {
		this.insuranceStatus = insuranceStatus;
	}

	/**
	 * 获取回款记录
	 * @return 回款记录
	 */
	public String getFlowDetail() {
		return flowDetail;
	}

	/**
	 * 设置回款记录
	 * @param flowDetail 回款记录
	 */
	public void setFlowDetail(String flowDetail) {
		this.flowDetail = flowDetail;
	}

	/**
	 * 获取城市ID
	 * 
	 * @return 城市ID
	 */
	public Integer getCityId() {
		return cityId;
	}

	/**
	 * 设置城市ID
	 * 
	 * @param cityId   城市ID
	 */
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	/**
	 * 获取城市名称
	 * 
	 * @return 城市名称
	 */
	public String getCityName() {
		return cityName;
	}

	/**
	 * 设置城市名称
	 * 
	 * @param cityName 城市名称
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * @return
	 */
	public Integer getVersionId() {
		return versionId;
	}

	/**
	 * @param versionId
	 */
	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

    /**
     * 获取创建时间
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }
    
    /**
     * 设置创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getPayStyle() {
		return payStyle;
	}

	public void setPayStyle(String payStyle) {
		this.payStyle = payStyle;
	}

	public Double getInsuranceReviewedAmount() {
		return insuranceReviewedAmount;
	}

	public void setInsuranceReviewedAmount(Double insuranceReviewedAmount) {
		this.insuranceReviewedAmount = insuranceReviewedAmount;
	}

	public String getOemName() {
		return oemName;
	}

	public void setOemName(String oemName) {
		this.oemName = oemName;
	}

	public String getOemPaymentMode() {
		return oemPaymentMode;
	}

	public void setOemPaymentMode(String oemPaymentMode) {
		this.oemPaymentMode = oemPaymentMode;
	}
	
	public Date getFlowFinishTime() {
		return flowFinishTime;
	}

	public void setFlowFinishTime(Date flowFinishTime) {
		this.flowFinishTime = flowFinishTime;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public Float getForceCommissionAmount() {
		return forceCommissionAmount;
	}

	public void setForceCommissionAmount(Float forceCommissionAmount) {
		this.forceCommissionAmount = forceCommissionAmount;
	}

	public Float getBusinessCommissionAmount() {
		return businessCommissionAmount;
	}

	public void setBusinessCommissionAmount(Float businessCommissionAmount) {
		this.businessCommissionAmount = businessCommissionAmount;
	}

	public String getForceRuleFormula() {
		return forceRuleFormula;
	}

	public void setForceRuleFormula(String forceRuleFormula) {
		this.forceRuleFormula = forceRuleFormula;
	}

	public String getBusinessRuleFormula() {
		return businessRuleFormula;
	}

	public void setBusinessRuleFormula(String businessRuleFormula) {
		this.businessRuleFormula = businessRuleFormula;
	}

	public String getForceCommissionRate() {
		return forceCommissionRate;
	}

	public void setForceCommissionRate(String forceCommissionRate) {
		this.forceCommissionRate = forceCommissionRate;
	}

	public String getBusinessCommissionRate() {
		return businessCommissionRate;
	}

	public void setBusinessCommissionRate(String businessCommissionRate) {
		this.businessCommissionRate = businessCommissionRate;
	}

	public String getOemCode() {
		return oemCode;
	}

	public void setOemCode(String oemCode) {
		this.oemCode = oemCode;
	}

	public String getOemAgencyName() {
		return oemAgencyName;
	}

	public void setOemAgencyName(String oemAgencyName) {
		this.oemAgencyName = oemAgencyName;
	}

	public Float getOrderOriginalTotalAmount() {
		return orderOriginalTotalAmount;
	}

	public void setOrderOriginalTotalAmount(Float orderOriginalTotalAmount) {
		this.orderOriginalTotalAmount = orderOriginalTotalAmount;
	}

	public Float getOrderOtherAmount() {
		return orderOtherAmount;
	}

	public void setOrderOtherAmount(Float orderOtherAmount) {
		this.orderOtherAmount = orderOtherAmount;
	}

	public Float getForceInsuranceOperationsAmount() {
		return forceInsuranceOperationsAmount;
	}

	public void setForceInsuranceOperationsAmount(Float forceInsuranceOperationsAmount) {
		this.forceInsuranceOperationsAmount = forceInsuranceOperationsAmount;
	}

	public Float getBusinessInsuranceOperationsAmount() {
		return businessInsuranceOperationsAmount;
	}

	public void setBusinessInsuranceOperationsAmount(Float businessInsuranceOperationsAmount) {
		this.businessInsuranceOperationsAmount = businessInsuranceOperationsAmount;
	}

	public String getForceInsuranceOperationsFormula() {
		return forceInsuranceOperationsFormula;
	}

	public void setForceInsuranceOperationsFormula(String forceInsuranceOperationsFormula) {
		this.forceInsuranceOperationsFormula = forceInsuranceOperationsFormula;
	}

	public String getBusinessInsuranceOperationsFormula() {
		return businessInsuranceOperationsFormula;
	}

	public void setBusinessInsuranceOperationsFormula(String businessInsuranceOperationsFormula) {
		this.businessInsuranceOperationsFormula = businessInsuranceOperationsFormula;
	}

	public String getForceInsuranceOperationsPercentage() {
		return forceInsuranceOperationsPercentage;
	}

	public void setForceInsuranceOperationsPercentage(String forceInsuranceOperationsPercentage) {
		this.forceInsuranceOperationsPercentage = forceInsuranceOperationsPercentage;
	}

	public String getBusinessInsuranceOperationsPercentage() {
		return businessInsuranceOperationsPercentage;
	}

	public void setBusinessInsuranceOperationsPercentage(String businessInsuranceOperationsPercentage) {
		this.businessInsuranceOperationsPercentage = businessInsuranceOperationsPercentage;
	}

	public Float getInsuranceProductsPayFirstAmount() {
		return insuranceProductsPayFirstAmount;
	}

	public void setInsuranceProductsPayFirstAmount(Float insuranceProductsPayFirstAmount) {
		this.insuranceProductsPayFirstAmount = insuranceProductsPayFirstAmount;
	}

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}

	public Integer getDeliveryTimes() {
		return deliveryTimes;
	}

	public void setDeliveryTimes(Integer deliveryTimes) {
		this.deliveryTimes = deliveryTimes;
	}

	public String getOrderSourceType() {
		return orderSourceType;
	}

	public void setOrderSourceType(String orderSourceType) {
		this.orderSourceType = orderSourceType;
	}

	public Date getForceInsuranceEndDate() {
		return forceInsuranceEndDate;
	}

	public void setForceInsuranceEndDate(Date forceInsuranceEndDate) {
		this.forceInsuranceEndDate = forceInsuranceEndDate;
	}

	public Date getBusinessInsuranceEndDate() {
		return businessInsuranceEndDate;
	}

	public void setBusinessInsuranceEndDate(Date businessInsuranceEndDate) {
		this.businessInsuranceEndDate = businessInsuranceEndDate;
	}

	public Date getForceInsuranceStartDate() {
		return forceInsuranceStartDate;
	}

	public void setForceInsuranceStartDate(Date forceInsuranceStartDate) {
		this.forceInsuranceStartDate = forceInsuranceStartDate;
	}

	public Date getBusinessInsuranceStartDate() {
		return businessInsuranceStartDate;
	}

	public void setBusinessInsuranceStartDate(Date businessInsuranceStartDate) {
		this.businessInsuranceStartDate = businessInsuranceStartDate;
	}



	public Float getOrderExtraAmount() {
		return orderExtraAmount;
	}

	public void setOrderExtraAmount(Float orderExtraAmount) {
		this.orderExtraAmount = orderExtraAmount;
	}

	public Float getOrderOutcome() {
		return orderOutcome;
	}

	public void setOrderOutcome(Float orderOutcome) {
		this.orderOutcome = orderOutcome;
	}

	public Float getOrderUserPay() {
		return orderUserPay;
	}

	public void setOrderUserPay(Float orderUserPay) {
		this.orderUserPay = orderUserPay;
	}

	public String getDianfuComment() {

		return dianfuComment;
	}

	public void setDianfuComment(String dianfuComment) {

		this.dianfuComment = dianfuComment;
	}

	public Long getCid() {

		return cid;
	}

	public void setCid(Long cid) {

		this.cid = cid;
	}

	public String getSigningCompanyName() {

		return signingCompanyName;
	}

	public void setSigningCompanyName(String signingCompanyName) {

		this.signingCompanyName = signingCompanyName;
	}

	public Float getForceReward() {

		return forceReward;
	}

	public void setForceReward(Float forceReward) {

		this.forceReward = forceReward;
	}

	public Float getBusinessReward() {

		return businessReward;
	}

	public void setBusinessReward(Float businessReward) {

		this.businessReward = businessReward;
	}
}
