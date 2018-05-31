package com.weyao.srv.entity;


import java.util.Date;

public class SaleUrl {

    private Long urlId;

    private Long saleId;

    private String topicUrl;

    private Integer batchId;

    private String batchUrl;

    private String batchShortUrl;

    private Byte status;

    private Integer customerNum;

    private Integer calcNum;

    private Integer orderNum;

    private Integer orderSuccNum;

    private Integer batchNum;

    private Date updateTime;

    private Date createTime;

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

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getTopicUrl() {
        return topicUrl;
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl = topicUrl;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getBatchUrl() {
        return batchUrl;
    }

    public void setBatchUrl(String batchUrl) {
        this.batchUrl = batchUrl;
    }

    public String getBatchShortUrl() {
        return batchShortUrl;
    }

    public void setBatchShortUrl(String batchShortUrl) {
        this.batchShortUrl = batchShortUrl;
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

    public Integer getOrderSuccNum() {
        return orderSuccNum;
    }

    public void setOrderSuccNum(Integer orderSuccNum) {
        this.orderSuccNum = orderSuccNum;
    }
}
