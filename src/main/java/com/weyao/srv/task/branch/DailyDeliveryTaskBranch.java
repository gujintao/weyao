package com.weyao.srv.task.branch;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.weyao.exception.SrvException;
import com.weyao.srv.report.entity.delivery.OrderDelivery;

import com.weyao.srv.report.service.impl.DeliveryReportService;

/**
 * 每日配送报表
 * 发送前一天早上7点到当天早上7点之间，进入配送阶段的订单清单。
 * 可以根据保单基表中的first_delivery_time时间在前一天早上7点到当天早上7点之间的订单进行筛选。
 * @author dujingjing
 *
 */
@Component
public class DailyDeliveryTaskBranch extends AbstractTaskBrahch<OrderDelivery>{

	public DailyDeliveryTaskBranch() {
		super("daily_order_delivery");
	}
	
	@Autowired
	private DeliveryReportService deliveryReportService;

	@Override
	protected Map<String, List<? extends Object>> export() throws SrvException {
		Map<String, Object> params = Maps.newHashMap();
		List<OrderDelivery> deliveries = this.deliveryReportService.listReport(params, null).getBeanList();
		Map<String, List<? extends Object>> map = Maps.newHashMap();
//		map.put(new FileTask(DEFAULT, "财务应付明细%s.xls"),insuranceFinanceReport.getBeanList());
		map.put("daily_order_delivery", deliveries);
		return map;
	}
	
}
