package com.weyao.srv.report.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 区域运营分类业绩统计
 * 
 * @author taoxiaoyan
 */
public class DataRegionalClassifyAchieve implements Serializable {

	private static final long serialVersionUID = -1678537127892540495L;

	/**
	 * @Fields _count : Mybatis 处理save or update 时使用
	 */
	private int _count;

	private Integer statisDate; // 统计日期，格式为yyyymmdd
	private Integer cityId; // 城市
	private Integer classifyType; // 分类类型：0:拓展；1：渠道；2：保司
	private Integer classifyCode; // 分类的值。如果type是0，则此字段存拓展员id；如果type是1，存渠道id...
	private String classifyName; // 分类的值。如果type是0，则此字段存拓展员名字；如果type是1，存渠道名字...
	private long jgCalcCar; // 经管询价车数，有在经管询过价的车数
	private long yydCalcCar; // 又一单询价车数
	private long overallCalcCar; // 总询价车数
	private long jgPaidVCICar; // 经管成单车数，含商业险的订单(垫付完成)
	private long yydPaidVCICar; // 又一单成单车数，含商业险的订单(垫付完成)
	private long onlyPaidTCICar; // 单交强成单车数
	private long overallPaidVCICar; // 总成单车数，含商业险的订单(垫付完成)
	private Timestamp updateTime; // 更新时间
	private Timestamp createTime; // 创建时间

	public DataRegionalClassifyAchieve() {
		super();
	}

	public final Integer getStatisDate() {
		return statisDate;
	}

	public final void setStatisDate(Integer statisDate) {
		this.statisDate = statisDate;
	}

	public final Integer getCityId() {
		return cityId;
	}

	public final void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public final int get_count() {
		return _count;
	}

	public final void set_count(int _count) {
		this._count = _count;
	}

	public final Integer getClassifyType() {
		return classifyType;
	}

	public final void setClassifyType(Integer classifyType) {
		this.classifyType = classifyType;
	}

	public final Integer getClassifyCode() {
		return classifyCode;
	}

	public final void setClassifyCode(Integer classifyCode) {
		this.classifyCode = classifyCode;
	}

	public final String getClassifyName() {
		return classifyName;
	}

	public final void setClassifyName(String classifyName) {
		this.classifyName = classifyName;
	}

	public final long getJgCalcCar() {
		return jgCalcCar;
	}

	public final void setJgCalcCar(long jgCalcCar) {
		this.jgCalcCar = jgCalcCar;
	}

	public final long getYydCalcCar() {
		return yydCalcCar;
	}

	public final void setYydCalcCar(long yydCalcCar) {
		this.yydCalcCar = yydCalcCar;
	}

	public final long getOverallCalcCar() {
		return overallCalcCar;
	}

	public final void setOverallCalcCar(long overallCalcCar) {
		this.overallCalcCar = overallCalcCar;
	}

	public final long getJgPaidVCICar() {
		return jgPaidVCICar;
	}

	public final void setJgPaidVCICar(long jgPaidVCICar) {
		this.jgPaidVCICar = jgPaidVCICar;
	}

	public final long getYydPaidVCICar() {
		return yydPaidVCICar;
	}

	public final void setYydPaidVCICar(long yydPaidVCICar) {
		this.yydPaidVCICar = yydPaidVCICar;
	}

	public final long getOnlyPaidTCICar() {
		return onlyPaidTCICar;
	}

	public final void setOnlyPaidTCICar(long onlyPaidTCICar) {
		this.onlyPaidTCICar = onlyPaidTCICar;
	}

	public final long getOverallPaidVCICar() {
		return overallPaidVCICar;
	}

	public final void setOverallPaidVCICar(long overallPaidVCICar) {
		this.overallPaidVCICar = overallPaidVCICar;
	}

	public final Timestamp getUpdateTime() {
		return updateTime;
	}

	public final void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public final Timestamp getCreateTime() {
		return createTime;
	}

	public final void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}
