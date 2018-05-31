package com.weyao.srv.task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import com.dsf.platform.jobs.job.JobResult;
import com.google.common.reflect.TypeToken;
import com.weyao.common.D;
import com.weyao.common.IdEncoder;
import com.weyao.common.JsonHelper2;
import com.weyao.exception.SrvException;
import com.weyao.info.comet.PageConfig;
import com.weyao.info.comet.TransmissionContent;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.info.dictionary.BeeCityConfig;
import com.weyao.info.order.BaseOrder;
import com.weyao.srv.comet.CometConstants.TransmissionTypeEnum;
import com.weyao.srv.dao.bee.mapper.BeeCalcInsuranceMapper;
import com.weyao.srv.dataproxy.DataProxySrv;
import com.weyao.srv.dictionary.DictionarySrv;
import com.weyao.srv.disocnfi.AppConfig;
import com.weyao.srv.message.MessageSrv;
import com.weyao.srv.result.entity.BeePayRemindMark;
import com.weyao.srv.result.entity.InsuranceRemindTime;
import com.weyao.srv.sms.SmsSrv;

@Scope("singleton")
@DisconfFile(filename = "timmer.properties")
public class BeePayRemindTask extends AbstractTimerTask {

	private static Logger logger = LoggerFactory.getLogger(BeePayRemindTask.class);

	@Autowired
	private DataProxySrv dataProxySrv;
	@Autowired
	private SmsSrv smsSrv;
	@Autowired
	private MessageSrv messageSrv;
	@Autowired
	private DictionarySrv dictionarySrv;
	@Autowired
	private BeeCalcInsuranceMapper calcInsuranceMapper;
	
	private static boolean isWhile = true;
	private static final int 提醒支付短信模板 = 17;
	
	private static final int 提醒支付消息模板 = 4;
	
	@DisconfFileItem(name = "bee.task.is.while",associateField="isWhile")
	public static boolean getIsWhile() {
		return isWhile;
	}
	public static void setIsWhile(boolean isWhile) {
		BeePayRemindTask.isWhile = isWhile;
	}
	
	@Override
	public JobResult handleBiz(String[] arg0) {
		JobResult result = new JobResult();
		try {
			while (isWhile) {
				logger.debug("定时消费又一单核保成功记录,开始------");
				StringBuffer query = new StringBuffer(" SELECT ");
				query.append(" bi.calc_record_item_id AS calcRecordItemId, DATE_FORMAT(ifd.create_time, '%Y-%m-%d %T') AS remindTime ");
				query.append(" FROM t_bee_calc_insurance bi, t_bee_calc_record re, t_insurance_flow_detail ifd, t_order o ");
				query.append(" WHERE IF(bi.force_insurance_id = 0, bi.business_insurance_id, bi.force_insurance_id) = ifd.insurance_id ");
				query.append(" AND bi.order_id = o.order_id ");
				query.append(" AND bi.calc_record_id = re.calc_record_id AND bi.remind_time IS NULL ");
				query.append(" AND ifd.create_time >= ? AND ifd.create_time <= ? ");
				query.append(" AND bi.`status` = 3 AND o.source_type in (300,400) ");
				query.append(" AND ifd.insurance_status = 20 GROUP BY bi.calc_record_id ");

				String formatDate = D.formatDate("yyyy-MM-dd");

				List<Object> queryParams = new ArrayList<Object>();
				queryParams.add(formatDate + " 00:00:00");
				queryParams.add(formatDate + " 23:59:59");
				String excuteQuery = dataProxySrv.excuteQuery(query.toString(), ResultType.LIST, queryParams);
				logger.debug("定时消费又一单核保成功记录,查询结果：" + excuteQuery);
				List<InsuranceRemindTime> resultList = new ArrayList<InsuranceRemindTime>();
				if (StringUtils.isNotBlank(excuteQuery)) {
					resultList = JsonHelper2.fromJson(excuteQuery, new TypeToken<List<InsuranceRemindTime>>() {
					}.getType());
				}
				if (CollectionUtils.isNotEmpty(resultList)) {
					logger.debug("定时消费又一单核保成功记录,查询结果List：" + JsonHelper2.toJson(resultList));
					for (InsuranceRemindTime remindTime : resultList) {
						logger.debug(String.format("定时消费又一单核保成功记录,处理remindTime:【%s】,calcRecordItemId:【%s】", remindTime.getRemindTime(),
								remindTime.getCalcRecordItemId()));
						calcInsuranceMapper.updateInsuranceRemindTime(remindTime.getCalcRecordItemId(), remindTime.getRemindTime());
					}
				}
				logger.debug("定时消费又一单核保成功记录,结束------");
				
				beePayRemindMarkHandler();// 执行一个小时未支付的订单提醒任务
				
				Thread.sleep(60000);
			}
			result.setSendMail(true);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("定时消费又一单核保成功记录", e);
		}
		return result;
	}
	
	public void beePayRemindMarkHandler() throws SrvException {
		logger.debug("定时提醒又一单用户支付任务,开始------");
		StringBuffer query = new StringBuffer(" SELECT ");
		query.append(" bi.calc_record_item_id AS calcRecordItemId, i.supplier_name AS supplierName, i.city_id AS cityId, ");
		query.append(" i.car_number AS carNumber,c.mobile AS mobile,o.real_amount AS realAmount,o.cid,o.order_id AS orderId,o.source_type AS sourceType ");
		query.append(" FROM t_bee_calc_insurance bi, t_insurance i, t_insurance_flow_detail id, t_customer c, t_order o ");
		query.append(" WHERE IF((bi.force_insurance_id = 0),bi.business_insurance_id,bi.force_insurance_id) = i.insurance_id ");
		query.append(" AND i.cid = c.cid AND bi.order_id = o.order_id AND i.insurance_id = id.insurance_id ");
		
		
		Date addMinute = D.addMinute(new Date(), -60);
		String formatDate = D.formatDate("yyyy-MM-dd HH:mm:ss", addMinute);
		Map<Integer, BeeCityConfig> cityConfigTemMap = new HashMap<Integer, BeeCityConfig>();
		List<Object> queryParams = new ArrayList<Object>();
		List<BeeCityConfig> cityConfigList = dictionarySrv.listBeeCityConfig();
		if (CollectionUtils.isEmpty(cityConfigList)) {
			logger.info("获取所有小蜜蜂城市配置为空，【BeePayRemindTask-beePayRemindMarkHandler()】不发送又一单定时提醒支付消息！");
			return;
		}
		query.append(" AND o.city_id IN (");
		for (Iterator iterator = cityConfigList.iterator(); iterator.hasNext();) {
			BeeCityConfig cityConfig = (BeeCityConfig) iterator.next();
			query.append("?");
			queryParams.add(cityConfig.getCityId());
			if (iterator.hasNext()) {
				query.append(",");
			} else {
				query.append(") ");
			}
			cityConfigTemMap.put(cityConfig.getCityId(), cityConfig);
		}
		
		query.append(" AND bi.pay_remind_mark = 0 ");
		query.append(" AND bi.`status` = 3 AND i.`status` = 20 AND id.insurance_status = 20 AND id.create_time < ? GROUP BY bi.calc_record_id ");
		
		queryParams.add(formatDate);
		logger.debug("查询需要提醒的数据SQL：【" + query.toString() + "】，参数：" + JsonHelper2.toJson(queryParams));
		
		String excuteQuery = dataProxySrv.excuteQuery(query.toString(), ResultType.LIST, queryParams);
		
		List<BeePayRemindMark> resultList = new ArrayList<BeePayRemindMark>();
		if (StringUtils.isNotBlank(excuteQuery)) {
			resultList = JsonHelper2.fromJson(excuteQuery, new TypeToken<List<BeePayRemindMark>>() {
			}.getType());
		}
		if (CollectionUtils.isNotEmpty(resultList)) {
			DecimalFormat df = new DecimalFormat("0.00");
			for (BeePayRemindMark payRemindMark : resultList) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("plate_number", payRemindMark.getCarNumber());
				params.put("insurance_company", payRemindMark.getSupplierName());
				params.put("real_amount", String.valueOf(df.format((float) payRemindMark.getRealAmount() / 100)));
				BeeCityConfig cityConfig = cityConfigTemMap.get(payRemindMark.getCityId());
				if (cityConfig != null) {
					switch (cityConfig.getIsSendMessage()) {
					case 1:// 发短信1表示又一单和经管来源的单子都发
						smsSrv.send(提醒支付短信模板, payRemindMark.getMobile(), params);
						break;
					case 2:// 发短信2表示又一单来源的才发
						if (payRemindMark.getSourceType() == BaseOrder.又一单)
							smsSrv.send(提醒支付短信模板, payRemindMark.getMobile(), params);
						break;
					case 3:// 发短信3表示经管来源的才发
						if (payRemindMark.getSourceType() == BaseOrder.经纪人)
							smsSrv.send(提醒支付短信模板, payRemindMark.getMobile(), params);
						break;
					default:
						logger.debug("需要提醒的记录：" + JsonHelper2.toJson(payRemindMark) + "未达到发送短信配置要求，不发送短息！");
						break;
					}
				} else {
					logger.debug("需要提醒的记录：" + JsonHelper2.toJson(payRemindMark) + "未达到发送短信配置要求，不发送短息！");
				}
				
				calcInsuranceMapper.updateInsurancePayRemindMark(payRemindMark.getCalcRecordItemId());
				if (cityConfig != null && cityConfig.getIsPushMessage() == 1) {
					List<Long> oidList = new ArrayList<Long>();
					oidList.add(payRemindMark.getCid());
					TransmissionContent content = new TransmissionContent();
					content.setTransmissionType(TransmissionTypeEnum.内部页面.getTransmissionTypeId());
					PageConfig pageConfig = new PageConfig();
					pageConfig.setModelName("another");
					pageConfig.setPageName("details.html");
					String enCodeOrderId = String.valueOf(IdEncoder.ORDER_IDENCODER.EncodeId(payRemindMark.getOrderId()));
					pageConfig.setParams("result={\"orderId\":" + enCodeOrderId + ",\"recordId\":0}");
					content.setPageConfig(pageConfig);
					messageSrv.sendMessage(oidList, AppConfig.getYouYiDanAppId(), 提醒支付消息模板, params, content);
				}
			}
		}
		logger.debug("定时提醒又一单用户支付任务,结束------");
	}
}
