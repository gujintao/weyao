package com.weyao.srv.task.branch;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.weyao.exception.SrvException;
import com.weyao.srv.report.entity.LBTotalRecord;
import com.weyao.srv.report.service.impl.LBDailyRecordReportService;

/**
 * 小蜜蜂数据报表任务分支
 * 在该分支中，
 * 将会提供业务需要的小蜜蜂数据7张报表的数据
 * @author dujingjing
 *
 */
@Component
public class LBDailyRecordTaskBranch extends AbstractTaskBrahch<LBTotalRecord>{

	public LBDailyRecordTaskBranch() {
		super("lb_daily_record");
	}
	
	@Autowired
	private LBDailyRecordReportService lbDailyRecordReportService;

	//每日垫付明细
	//	map.put("daily_dianfu_report", lbDailyRecordReportService.listDailyDianfus());（删除）
	@Override
	protected Map<String, List<? extends Object>> export() throws SrvException {
		Map<String, List<? extends Object>> map = Maps.newHashMap();
		//小蜜蜂询价状态报表
		map.put("bee_calc_report", lbDailyRecordReportService.reportBeeStatus());
		//每日新增小蜜蜂明细
		map.put("bee_total_report", lbDailyRecordReportService.reportTotalBees());
		//又一单出单小蜜蜂统计
		map.put("bee_yyd_report", lbDailyRecordReportService.reportYydBees());
		//每日下单明细报表
		map.put("daily_order_report", lbDailyRecordReportService.listDailyOrders());
		//每日垫付明细
		map.put("daily_dianfu_report", lbDailyRecordReportService.listDailyDianfus(1));
		//每日询价明细
		map.put("daily_calc_report", lbDailyRecordReportService.listDailyCalcs());
		
//		map.put(new FileTask("bee_calc_report", "小蜜蜂询价状态报表%s.xls"), lbDailyRecordReportService.reportBeeStatus());
//		map.put(new FileTask("bee_total_report", "历史小蜜蜂明细%s.xls"), lbDailyRecordReportService.reportTotalBees());
//		map.put(new FileTask("bee_yyd_report", "又一单出单小蜜蜂统计报表%s.xls"), lbDailyRecordReportService.reportYydBees());
		return map;
	}
	
}
