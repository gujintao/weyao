package com.weyao.srv.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.dao.report.mapper.ILbDailyReportMapper;
import com.weyao.srv.report.entity.DailyCalcRecord;
import com.weyao.srv.report.entity.DailyDianfuRecord;
import com.weyao.srv.report.entity.DailyOrder;
import com.weyao.srv.report.entity.LBGatherInfo;
import com.weyao.srv.report.entity.LBTotalRecord;
import com.weyao.srv.report.service.AbstractReportService;

/**
 * 小蜜蜂报表服务
 * @author Victor
 */
public class LBDailyRecordReportService extends AbstractReportService<LBTotalRecord>  {

    @Autowired
    private ILbDailyReportMapper reportMapper;
    
	public List<LBTotalRecord> reportBeeStatus(){
		return reportMapper.reportBeeStatus();
	}

	public List<LBGatherInfo> reportTotalBees(){
		return reportMapper.reportTotalBees();
	}

	public List<LBTotalRecord> reportYydBees(){
		return reportMapper.reportYydBees();
	}
	
	public List<DailyOrder> listDailyOrders(){
		return reportMapper.listDailyOrders();
	}
	
	public List<DailyDianfuRecord> listDailyDianfus(int time){
		DailyDianfuRecord record = new DailyDianfuRecord();
		record.setTime(time);
		return reportMapper.listDailyDianfus(record);
	}
	
//	public List<DailyDianfuRecord> listDailyChengdans(){
//		return reportMapper.listDailyDianfus();
//	}
	
	public List<DailyCalcRecord> listDailyCalcs(){
		return reportMapper.listDailyCalcs();
	}
    
    @Override
    public List<LBTotalRecord> listReport(Map<String, Object> param) {
		throw new UnsupportedOperationException("本版本系统不支持该接口");
    }

	@Override
	public Integer getTotalCount(Map<String, Object> param) {
		throw new UnsupportedOperationException("本版本系统不支持该接口");
	}

	@Override
	public Long updateReport(LBTotalRecord obj) {
		throw new UnsupportedOperationException("本版本系统不支持该接口");
	}
}
