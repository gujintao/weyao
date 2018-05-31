package com.weyao.srv.dao.report.mapper;

import java.util.List;

import com.weyao.srv.report.entity.delivery.OrderDelivery;

/**
 * 配送数据报表查询接口
 * @author dujingjing
 * @version 1.0
 */
public interface DeliveryMapper {

	/**
	 * 小蜜蜂状态数据报表，从又一单/总体的纬度去统计小蜜蜂状态
	 * @return
	 */
	public List<OrderDelivery> listReports();
}
