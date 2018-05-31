package com.weyao.srv.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.dao.report.mapper.InsuranceFinanceReportMapper;
import com.weyao.srv.report.entity.InsuranceFinanceReport;
import com.weyao.srv.report.service.AbstractReportService;

/**
 * 财务垫付报表服务
 * @author Victor
 */
public class InsuranceFinanceReportService extends AbstractReportService<InsuranceFinanceReport>  {

    @Autowired
    private InsuranceFinanceReportMapper insuranceFinanceReportMapper;
    
    @Override
    public List<InsuranceFinanceReport> listReport(Map<String, Object> param) {
        return this.insuranceFinanceReportMapper.listReport(param);
    }

	@Override
	public Integer getTotalCount(Map<String, Object> param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long updateReport(InsuranceFinanceReport obj) {
		// TODO Auto-generated method stub
		return null;
	}
 

}
