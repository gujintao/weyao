package com.weyao.srv.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.json.JSON;
import com.dsf.platform.jobs.job.JobResult;
import com.google.gson.reflect.TypeToken;
import com.weyao.common.Context;
import com.weyao.common.D;
import com.weyao.common.JsonHelper2;
import com.weyao.exception.SrvException;
import com.weyao.info.core.pay.PaymentInfo;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.info.dictionary.BeeCityConfig;
import com.weyao.srv.core.CoreSrv;
import com.weyao.srv.dataproxy.DataProxySrv;
import com.weyao.srv.dictionary.DictionarySrv;
import com.weyao.srv.pay.PaySrv;

public class OrderPaymentExpiredTask extends AbstractTimerTask {

	private static Logger logger = LoggerFactory.getLogger(OrderPaymentExpiredTask.class);

	@Autowired
	private DataProxySrv dataProxySrv;
	@Autowired
	private CoreSrv coreSrv;
	@Autowired
	private PaySrv paySrv;
	@Autowired
	private DictionarySrv dictionarySrv;

	@Override
	public JobResult handleBiz(String[] arg0) {
		JobResult result = new JobResult();
		try {
			result.setSendMail(true);
			logger.debug("定时取消未确认订单,开始------");
			String nowTime = D.formatDate("HH:mm");
			logger.debug("定时取消未确认订单,当前时间点：" + nowTime);
			List<BeeCityConfig> cityConfigList = dictionarySrv.listBeeCityConfig();
			if (CollectionUtils.isNotEmpty(cityConfigList)) {
				for (BeeCityConfig cityConfig : cityConfigList) {
					Calendar cal = Calendar.getInstance();
					if (nowTime.equals(cityConfig.getCancelOrderTime())) {
						String jintiandate = Context.SHORTDATEFORMAT.format(cal.getTime());
						if(cityConfig.getCancelOrderDate()==null) continue;
						cal.add(Calendar.DATE, -cityConfig.getCancelOrderDate().intValue());
						String zuotiandate = Context.SHORTDATEFORMAT.format(cal.getTime());
						if (cityConfig.getCancelOrderDate() == 0) {
							zuotiandate = zuotiandate + " 00:00:00";
							jintiandate = jintiandate + " " + cityConfig.getCancelOrderTime() + ":00";
						}
						List<Object> params = new ArrayList<Object>();
						params.add(cityConfig.getCityId());
						params.add(zuotiandate);
						params.add(jintiandate);
						logger.debug("取消未支付订单查询参数：" + JsonHelper2.toJson(params));
						String sql = "SELECT DISTINCT a.order_id FROM t_insurance a JOIN (SELECT insurance_id,MAX(create_time) AS create_time FROM t_insurance_flow_detail WHERE insurance_status=20 GROUP BY insurance_id) b ON a.insurance_id=b.insurance_id WHERE a.STATUS = 20 AND a.city_id = ? AND b.create_time>? AND b.create_time<?";
						String jsonStr = dataProxySrv.excuteQuery(sql, ResultType.LIST, params);
						List<Map<String, Long>> orderIdList = JsonHelper2.fromJson(jsonStr, new TypeToken<List<Map<String, Long>>>() {
						}.getType());
						int cancleOrders = 0;
						if (CollectionUtils.isNotEmpty(orderIdList)) {
							for (Map<String, Long> map : orderIdList) {
								Long orderId = map.get("order_id");
								logger.debug(String.format("定时取消未确认订单，订单ID=%s", orderId));
								// 查询订单是否有支付成功记录
								try {
									if (!paySrv.isPaySuccess(orderId)) {
										PaymentInfo paymentInfo = new PaymentInfo();
										paymentInfo.setOrderId(orderId);
										coreSrv.cancelPayment(paymentInfo);
										cancleOrders++;
									}
								} catch (SrvException e) {
									logger.error(String.format("取消未确认订单失败，订单ID=%s", orderId), e);
								}
							}
						}
						result.setMessage(String.format("定时取消未确认订单：日期=%s，总订单数=%s，取消的订单数=%s", zuotiandate, orderIdList == null ? 0 : orderIdList.size(),
								cancleOrders));
					}
				}
			}
			logger.debug("定时取消未确认订单,结束------");
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("定时取消未确认订单", e);
		}
		return result;
	}
}
