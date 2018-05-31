package com.weyao.srv.report.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 区域运营活跃&增长（增长指新增数）
 * 
 * @author taoxiaoyan
 */
public class DataRegionalActiveAdd implements Serializable {

	private static final long serialVersionUID = 5437707238620948195L;

	/**
	 * @Fields _count : Mybatis 处理save or update 时使用
	 */
	private int _count;

	private Integer statisDate; // 统计日期，格式为yyyymmdd
	private Integer statisDateWeek; // 统计日期所在的周，格式形如201645
	private Integer statisDateMonth; // 统计日期所在的月，格式为yyyymm
	private Integer cityId; // 城市
	private long jgCalcActive; // 经管报价活跃，有在经管询过价的小蜜蜂数量
	private long yydCalcActive; // 又一单报价活跃，有在又一单询过价的小蜜蜂数量
	private long overallCalcActive; // 总报价活跃，有询过价的小蜜蜂数量
	private long jgOrderActive; // 经管出单活跃，有在经管出过单（以垫付订单为准，下同）的小蜜蜂数量
	private long yydOrderActive; // 又一单出单活跃，在又一单出过单（以垫付订单为准，下同）的小蜜蜂数量
	private long overallOrderActive; // 总出单活跃，有出过单（以垫付为准，下同）的小蜜蜂数量
	private long yydFirstLoginBee; // 又一单首登小蜜蜂增长
	private long yydRegisterBee; // 又一单注册小蜜蜂增长
	private long jgRegisterBee; // 经管注册小蜜蜂增长
	private long h5RegisterBee; // H5注册小蜜蜂增长
	private long batchImportBee; // 批量导入注册小蜜蜂增长
	private long overallRegisterBee; // 合计新增
	private Timestamp updateTime; // 更新时间
	private Timestamp createTime; // 创建时间

	public DataRegionalActiveAdd() {
		super();
	}

	public final Integer getStatisDate() {
		return statisDate;
	}

	public final void setStatisDate(Integer statisDate) {
		this.statisDate = statisDate;
	}

	public final Integer getStatisDateWeek() {
		return statisDateWeek;
	}

	public final void setStatisDateWeek(Integer statisDateWeek) {
		this.statisDateWeek = statisDateWeek;
	}

	public final Integer getStatisDateMonth() {
		return statisDateMonth;
	}

	public final void setStatisDateMonth(Integer statisDateMonth) {
		this.statisDateMonth = statisDateMonth;
	}

	public final Integer getCityId() {
		return cityId;
	}

	public final void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public final long getJgCalcActive() {
		return jgCalcActive;
	}

	/** 经管报价活跃，有在经管询过价的小蜜蜂数量 */
	public final void setJgCalcActive(long jgCalcActive) {
		this.jgCalcActive = jgCalcActive;
	}

	public final long getYydCalcActive() {
		return yydCalcActive;
	}

	/** 又一单报价活跃，有在又一单询过价的小蜜蜂数量*/
	public final void setYydCalcActive(long yydCalcActive) {
		this.yydCalcActive = yydCalcActive;
	}

	public final long getOverallCalcActive() {
		return overallCalcActive;
	}

	/** 总报价活跃，有询过价的小蜜蜂数量*/
	public final void setOverallCalcActive(long overallCalcActive) {
		this.overallCalcActive = overallCalcActive;
	}

	public final long getJgOrderActive() {
		return jgOrderActive;
	}

	/** 经管出单活跃，有在经管出过单（以垫付订单为准，下同）的小蜜蜂数量*/
	public final void setJgOrderActive(long jgOrderActive) {
		this.jgOrderActive = jgOrderActive;
	}

	public final long getYydOrderActive() {
		return yydOrderActive;
	}

	/** 又一单出单活跃，在又一单出过单（以垫付订单为准，下同）的小蜜蜂数量*/
	public final void setYydOrderActive(long yydOrderActive) {
		this.yydOrderActive = yydOrderActive;
	}

	public final long getOverallOrderActive() {
		return overallOrderActive;
	}

	/** 总出单活跃，有出过单（以垫付为准，下同）的小蜜蜂数量*/
	public final void setOverallOrderActive(long overallOrderActive) {
		this.overallOrderActive = overallOrderActive;
	}

	public final long getYydFirstLoginBee() {
		return yydFirstLoginBee;
	}

	/** 又一单首登小蜜蜂增长*/
	public final void setYydFirstLoginBee(long yydFirstLoginBee) {
		this.yydFirstLoginBee = yydFirstLoginBee;
	}

	public final long getYydRegisterBee() {
		return yydRegisterBee;
	}

	/** 又一单注册小蜜蜂增长*/
	public final void setYydRegisterBee(long yydRegisterBee) {
		this.yydRegisterBee = yydRegisterBee;
	}

	public final long getJgRegisterBee() {
		return jgRegisterBee;
	}

	/** 经管注册小蜜蜂增长*/
	public final void setJgRegisterBee(long jgRegisterBee) {
		this.jgRegisterBee = jgRegisterBee;
	}

	public final long getH5RegisterBee() {
		return h5RegisterBee;
	}

	/** H5注册小蜜蜂增长*/
	public final void setH5RegisterBee(long h5RegisterBee) {
		this.h5RegisterBee = h5RegisterBee;
	}

	public final long getBatchImportBee() {
		return batchImportBee;
	}

	/** 批量导入注册小蜜蜂增长*/
	public final void setBatchImportBee(long batchImportBee) {
		this.batchImportBee = batchImportBee;
	}

	public final long getOverallRegisterBee() {
		return overallRegisterBee;
	}

	/** 合计新增*/
	public final void setOverallRegisterBee(long overallRegisterBee) {
		this.overallRegisterBee = overallRegisterBee;
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

	public final int get_count() {
		return _count;
	}

	public final void set_count(int _count) {
		this._count = _count;
	}

}
