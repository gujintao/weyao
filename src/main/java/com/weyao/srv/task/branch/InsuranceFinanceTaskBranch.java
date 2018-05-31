package com.weyao.srv.task.branch;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.weyao.exception.SrvException;
import com.weyao.info.common.PageResult;
import com.weyao.srv.report.entity.InsuranceFinanceReport;
import com.weyao.srv.report.service.impl.InsuranceFinanceReportService;

/**
 * 财务应付明细报表任务
 * @author dujingjing
 *
 */
@Component
public class InsuranceFinanceTaskBranch extends AbstractTaskBrahch<InsuranceFinanceReport>{

	public InsuranceFinanceTaskBranch() {
		super("insurance_finance_show_report");
	}
	
	@Autowired
	private InsuranceFinanceReportService insuranceFinanceReportService;

	@Override
	protected Map<String, List<? extends Object>> export() throws SrvException {
		Map<String, Object> param = Maps.newHashMap();
		PageResult<InsuranceFinanceReport> insuranceFinanceReport = this.insuranceFinanceReportService.listReport(param, null);
		Map<String, List<? extends Object>> map = Maps.newHashMap();
//		map.put(new FileTask(DEFAULT, "财务应付明细%s.xls"),insuranceFinanceReport.getBeanList());
		map.put("insurance_finance_show_report",insuranceFinanceReport.getBeanList());
		return map;
	}
	
}
