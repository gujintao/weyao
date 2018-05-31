package com.weyao.srv.report.entity;

import java.util.Date;

/**
 * 历史小蜜蜂数据明细
 * @author dujingjing
 *
 */
public class LBGatherInfo {

	/*
	 * DATE_FORMAT(a.create_time, '%Y-%m-%d') AS register_date,
	 */
	private Date registerDate;
	
	/*
	 * a.cid,
	 */
	private int cid;
	
	/*
	 * (CASE WHEN a.from_source = 0 THEN '经纪人' WHEN a.from_source = 1 THEN '又一单' ELSE '导入' END ) from_source,
	 */
	private String fromSource;
	
	/*
	 * c.mobile,
	 */
	private String mobile;
	
	/*
	 * d.`name` AS city_name,
	 */
	private String cityName;
	
	/*
	 * e.`name` AS area_name,
	 */
	private String areaName;
	
	/*
	 * f.`name` AS oper_name,
	 */
	private String operName;
	
	/*
	 * bee_type
	 */
	private String beeType;
	
	/*
	 * bee_status
	 */
	private String beeStatus;
	
	/*
	 * a.nick_name,
	 */
	private String nickName;
	
	/*
	 * c.`name` AS bee_name,
	 */
	private String beeName;
	
	/*
	 * a.address,
	 */
	private String address;
	
	/*
	 * a.first_login_time,
	 */
	private String firstLoginTime;
	
	/*
	 * b.yydbjsl AS calc_num_yyd,
	 */
	private int calcNumYyd;
	
	/*
	 * b.jjrbjsl AS calc_num_jjr,
	 */
	private int calcNumJjr;
	
	/*
	 * g.ybjcdsl AS chengdan_num_yyd,
	 */
	private int chengdanNumYyd;
	
	/*
	 * g.jjrcdsl AS chengdan_num_jjr
	 */
	private int chengdanNumJjr;

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getFromSource() {
		return fromSource;
	}

	public void setFromSource(String fromSource) {
		this.fromSource = fromSource;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public String getBeeType() {
		return beeType;
	}

	public void setBeeType(String beeType) {
		this.beeType = beeType;
	}

	public String getBeeStatus() {
		return beeStatus;
	}

	public void setBeeStatus(String beeStatus) {
		this.beeStatus = beeStatus;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getBeeName() {
		return beeName;
	}

	public void setBeeName(String beeName) {
		this.beeName = beeName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFirstLoginTime() {
		return firstLoginTime;
	}

	public void setFirstLoginTime(String firstLoginTime) {
		this.firstLoginTime = firstLoginTime;
	}

	public int getCalcNumYyd() {
		return calcNumYyd;
	}

	public void setCalcNumYyd(int calcNumYyd) {
		this.calcNumYyd = calcNumYyd;
	}

	public int getCalcNumJjr() {
		return calcNumJjr;
	}

	public void setCalcNumJjr(int calcNumJjr) {
		this.calcNumJjr = calcNumJjr;
	}

	public int getChengdanNumYyd() {
		return chengdanNumYyd;
	}

	public void setChengdanNumYyd(int chengdanNumYyd) {
		this.chengdanNumYyd = chengdanNumYyd;
	}

	public int getChengdanNumJjr() {
		return chengdanNumJjr;
	}

	public void setChengdanNumJjr(int chengdanNumJjr) {
		this.chengdanNumJjr = chengdanNumJjr;
	}
}
