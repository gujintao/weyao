package com.weyao.srv.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.dao.report.mapper.YydOnlinePaymentReportMapper;
import com.weyao.srv.report.entity.YydOnlinePaymentReport;
import com.weyao.srv.report.service.AbstractReportService;

/**
 * 财务垫付报表服务
 * @author Victor
 */
public class YydOnlinePaymentReportService extends AbstractReportService<YydOnlinePaymentReport>  {

    @Autowired
    private YydOnlinePaymentReportMapper yydOnlinePaymentReportMapper;
    
    @Override
    public List<YydOnlinePaymentReport> listReport(Map<String, Object> param) {
        return this.yydOnlinePaymentReportMapper.listReport(param);
    }

	@Override
	public Integer getTotalCount(Map<String, Object> param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long updateReport(YydOnlinePaymentReport obj) {
		// TODO Auto-generated method stub
		return null;
	}
 

}
