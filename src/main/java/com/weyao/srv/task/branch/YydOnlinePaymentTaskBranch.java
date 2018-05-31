package com.weyao.srv.task.branch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.weyao.exception.SrvException;
import com.weyao.info.common.PageResult;
import com.weyao.srv.report.entity.YydOnlinePaymentReport;
import com.weyao.srv.report.service.impl.YydOnlinePaymentReportService;

/**
 * 小蜜蜂报表数据分支任务
 * @author dujingjing
 *
 */
@Component
public class YydOnlinePaymentTaskBranch extends AbstractTaskBrahch<YydOnlinePaymentReport>{

	public YydOnlinePaymentTaskBranch() {
		super("yyd_online_payment");
	}
	
	@Autowired
	private YydOnlinePaymentReportService yydOnlinePaymentReportService;

	@Override
	protected Map<String, List<? extends Object>> export() throws SrvException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> param = new HashMap<String, Object>();
		Calendar calendar = Calendar.getInstance();//此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, -1);    //得到前一天
		String  paymentStartTime= format.format(calendar.getTime());

		param.put("paymentStartTime", paymentStartTime + " 18:00:00");
		param.put("paymentEndTime", format.format(new Date()) + " 18:00:00");
		PageResult<YydOnlinePaymentReport> yydOnlinePaymentReport = this.yydOnlinePaymentReportService.listReport(param,null);
		

		Map<String, List<? extends Object>> map = Maps.newHashMap();
//		map.put(new FileTask(DEFAULT, "又一单先付明细%s.xls"), yydOnlinePaymentReport.getBeanList());
		map.put("yyd_online_payment", yydOnlinePaymentReport.getBeanList());
		return map;
	}
	
}
