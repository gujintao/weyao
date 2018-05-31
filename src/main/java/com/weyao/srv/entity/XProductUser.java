package com.weyao.srv.entity;


import java.util.Date;

public class XProductUser {

    private int id;                         //

    private String name;                    //姓名

    private String plateNumber;             //车牌

    private String mobile;                  //手机号码

    private Date insuranceEndDate;          //保险到期日

    private int cxCount;                    //出险次数

    private String activityName;            //上年产品

    private String insurancePtype;          //上年险种

    private int insuranceReviewedAmount;    //成单金额

    private String insuranceSupplierName;   //上年保司

    private Date insuranceCreateTime;       //上年出单日

    private Date firstPaymentTime;          //垫付日期

    private String cdName;                  //上年出单人

    private String vehicleName;             //车型

    private String vin;                     //车架号

    private String engineNo;                //发动机号

    private Date registerDate;              //初登日期

    private String carOwner;                //被保人姓名

    private String bankName;                //开户行

    private String bankCard;                //支行

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getInsuranceEndDate() {
        return insuranceEndDate;
    }

    public void setInsuranceEndDate(Date insuranceEndDate) {
        this.insuranceEndDate = insuranceEndDate;
    }

    public int getCxCount() {
        return cxCount;
    }

    public void setCxCount(int cxCount) {
        this.cxCount = cxCount;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getInsurancePtype() {
        return insurancePtype;
    }

    public void setInsurancePtype(String insurancePtype) {
        this.insurancePtype = insurancePtype;
    }

    public int getInsuranceReviewedAmount() {
        return insuranceReviewedAmount;
    }

    public void setInsuranceReviewedAmount(int insuranceReviewedAmount) {
        this.insuranceReviewedAmount = insuranceReviewedAmount;
    }

    public String getInsuranceSupplierName() {
        return insuranceSupplierName;
    }

    public void setInsuranceSupplierName(String insuranceSupplierName) {
        this.insuranceSupplierName = insuranceSupplierName;
    }

    public Date getInsuranceCreateTime() {
        return insuranceCreateTime;
    }

    public void setInsuranceCreateTime(Date insuranceCreateTime) {
        this.insuranceCreateTime = insuranceCreateTime;
    }

    public Date getFirstPaymentTime() {
        return firstPaymentTime;
    }

    public void setFirstPaymentTime(Date firstPaymentTime) {
        this.firstPaymentTime = firstPaymentTime;
    }

    public String getCdName() {
        return cdName;
    }

    public void setCdName(String cdName) {
        this.cdName = cdName;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(String carOwner) {
        this.carOwner = carOwner;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }
}
