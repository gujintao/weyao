package com.weyao.srv.task;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.BaseTask;


public class IReportTaskTest extends BaseTask{

	@Autowired
	private BroaderReportTask broaderReportTask;
	
	//yyd_online_payment,19
	//date_sale_insurance,6,7,false,false,All lb_daily_record,7,8,false,true,Excel insurance_finance_show_report,9,10,false,false,Excel yyd_online_payment,18,18,false,false,Excel daily_dianfu_report,19,19,false,false,Excel
	//date_sale_insurance,6,7,false,false,All lb_daily_record,7,8,false,true,Excel daily_order_delivery,7,8,false,false,Excel insurance_finance_show_report,9,10,false,false,Excel yyd_online_payment,18,18,false,false,Excel
	@Test
	public void doTaskTest(){
		ArrayList<String> list = new ArrayList<String>();
//		list.add("date_sale_insurance,6,7,false,false,All"); //全局销售报表
//		list.add("lb_daily_record,7,8,false,true,Excel"); //小蜜蜂报表
//		list.add("daily_dianfu_report,7,8,false,false,Excel"); //小蜜蜂报表(当日补发的每日垫付数据)
//		list.add("daily_order_delivery,7,8,false,false,Excel"); //每日配送报表
		list.add("insurance_finance_show_report,9,10,false,false,Excel"); //财务应付明细
//		list.add("yyd_online_payment,9,10,false,false,Excel"); //又一单先付
		String[] params = new String[list.size()];
		for (int i = 0; i < params.length; i++) {
			params[i] = list.get(i);
		}
		broaderReportTask.handleBiz(params);
//		broaderReportTask.handleBiz(new String[]{"lb_daily_record,7,8,false,true"});
	}
}
