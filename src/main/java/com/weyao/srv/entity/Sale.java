package com.weyao.srv.entity;


import java.util.Date;

public class Sale {

    private Long saleId;

    private String name;

    private Integer type;

    private Integer cityId;

    private Integer insuranceCompany;

    private String insuranceItems;

    private String activityInfo;

    private String strategyInfo;

    private String topicUrl;

    private String landingPageInfo;

    private String generalPageInfo;

    private String mainPageInfo;

    private String failPageInfo;

    private Byte status;

    private Date startTime;

    private Date endTime;

    private Integer customerNum;

    private Integer calcNum;

    private Integer orderNum;

    private Integer orderSuccNum;

    private Integer batchNum;

    private Date updateTime;

    private Date createTime;

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(Integer insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceItems() {
        return insuranceItems;
    }

    public void setInsuranceItems(String insuranceItems) {
        this.insuranceItems = insuranceItems;
    }

    public String getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(String activityInfo) {
        this.activityInfo = activityInfo;
    }

    public String getStrategyInfo() {
        return strategyInfo;
    }

    public void setStrategyInfo(String strategyInfo) {
        this.strategyInfo = strategyInfo;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTopicUrl() {
        return topicUrl;
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl = topicUrl;
    }

    public String getLandingPageInfo() {
        return landingPageInfo;
    }

    public void setLandingPageInfo(String landingPageInfo) {
        this.landingPageInfo = landingPageInfo;
    }

    public String getGeneralPageInfo() {
        return generalPageInfo;
    }

    public void setGeneralPageInfo(String generalPageInfo) {
        this.generalPageInfo = generalPageInfo;
    }

    public String getMainPageInfo() {
        return mainPageInfo;
    }

    public void setMainPageInfo(String mainPageInfo) {
        this.mainPageInfo = mainPageInfo;
    }

    public String getFailPageInfo() {
        return failPageInfo;
    }

    public void setFailPageInfo(String failPageInfo) {
        this.failPageInfo = failPageInfo;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(Integer customerNum) {
        this.customerNum = customerNum;
    }

    public Integer getCalcNum() {
        return calcNum;
    }

    public void setCalcNum(Integer calcNum) {
        this.calcNum = calcNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Integer batchNum) {
        this.batchNum = batchNum;
    }

    public Integer getOrderSuccNum() {
        return orderSuccNum;
    }

    public void setOrderSuccNum(Integer orderSuccNum) {
        this.orderSuccNum = orderSuccNum;
    }
}
