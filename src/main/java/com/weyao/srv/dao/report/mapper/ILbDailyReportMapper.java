package com.weyao.srv.dao.report.mapper;

import java.util.List;

import com.weyao.srv.report.entity.DailyCalcRecord;
import com.weyao.srv.report.entity.DailyDianfuRecord;
import com.weyao.srv.report.entity.DailyOrder;
import com.weyao.srv.report.entity.LBGatherInfo;
import com.weyao.srv.report.entity.LBTotalRecord;

/**
 * 小蜜蜂业务查询接口
 * @author dujingjing
 * @version 1.0
 */
public interface ILbDailyReportMapper {

	/**
	 * 小蜜蜂状态数据报表，从又一单/总体的纬度去统计小蜜蜂状态
	 * @return
	 */
	public List<LBTotalRecord> reportBeeStatus();
	
	/**
	 * 查询每日新增小蜜蜂
	 * @return
	 */
	public List<LBGatherInfo> reportTotalBees();
	
	/**
	 * 查询yyd所有的小蜜蜂
	 * @return
	 */
	public List<LBTotalRecord> reportYydBees();
	
	/**
	 * 查询每日下单明细
	 * @return
	 */
	public List<DailyOrder> listDailyOrders();
	
	/**
	 * 查询每日垫付数据报表
	 * @return
	 */
	public List<DailyDianfuRecord> listDailyDianfus(DailyDianfuRecord record);
	
	/**
	 * 查询每日成单数据报表
	 * @return
	 */
	public List<DailyCalcRecord> listDailyCalcs();
}
