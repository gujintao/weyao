package com.weyao.srv.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.dao.report.mapper.DeliveryMapper;
import com.weyao.srv.report.entity.delivery.OrderDelivery;
import com.weyao.srv.report.service.AbstractReportService;

/**
 * 配送报表服务
 * @author Victor
 */
public class DeliveryReportService extends AbstractReportService<OrderDelivery>  {

    @Autowired
    private DeliveryMapper deliveryMapper;
    
	@Override
	public Integer getTotalCount(Map<String, Object> param) {
		throw new UnsupportedOperationException("本版本系统不支持该接口");
	}

	@Override
	public List<OrderDelivery> listReport(Map<String, Object> param) {
		return deliveryMapper.listReports();
	}

	@Override
	public Long updateReport(OrderDelivery obj) {
		throw new UnsupportedOperationException("本版本系统不支持该接口");
	}
    
	
}
