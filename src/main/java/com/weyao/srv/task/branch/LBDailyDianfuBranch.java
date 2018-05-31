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
public class LBDailyDianfuBranch extends AbstractTaskBrahch<LBTotalRecord>{

	public LBDailyDianfuBranch() {
		super("daily_dianfu_report");
	}
	
	@Autowired
	private LBDailyRecordReportService lbDailyRecordReportService;

	@Override
	protected Map<String, List<? extends Object>> export() throws SrvException {
		Map<String, List<? extends Object>> map = Maps.newHashMap();
		//每日垫付明细
		map.put("daily_dianfu_report", lbDailyRecordReportService.listDailyDianfus(2));
		
		return map;
	}
	
}
