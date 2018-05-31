package com.weyao.srv.task;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.dsf.platform.jobs.job.JobResult;
import com.weyao.srv.document.Register;
import com.weyao.srv.report.entity.*;
import com.weyao.srv.report.entity.delivery.OrderDelivery;
import com.weyao.srv.task.branch.TaskBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.sql.JDBCType.*;

/**
 * 报表数据统计任务，该任务主要完成两项工作：
 * 1、在每日凌晨时分从数据库中读取必要的报表数据，将其转换成JSON格式数据，保存至Redis集群中；
 * 2、根据统计的数据，生成对应的Excel文件，将其归档至OSS文件服务器，也同时会加载至邮件任务，进行邮件预发送
 * 该任务的启动参数比较特殊，例如：daily_dianfu_report,8,9,false,false,Excel。
 * 其参数规则如下：
 * 1、任务唯一KEY，系统会根据该KEY进行任务的确定以及Excel，PDF模板的选择，如果出现重复，将会导致错误
 * 2、数据收集时间，当达到这个准点时刻，系统开始调用数据库查询，进行数据收集
 * 3、邮件发送时间，当达到这个准点时刻，系统将会连同生成的附件一起发送邮件
 * 4、如果超时，是否丢弃
 * 5、是否需要进行打包操作
 * 6、对应其文档服务，目前支持Excel，PDF（如果为All，那么同时会发送两个文档）
 * @author dujingjing
 * @version 1.0
 */
@Scope("singleton")
@DisconfFile(filename = "timmer.properties")
public class BroaderReportTask extends AbstractTimerTask implements InitializingBean{

	private static Logger logger = LoggerFactory.getLogger(BroaderReportTask.class);

	@Autowired
	private Register register;
	
	private static ExecutorService TASK_BRANCH_SERVICE = Executors.newCachedThreadPool();

	@Autowired
	private List<TaskBranch> taskBranchList;
	
	/**
	 * date_sale_insurance", "insurance_finance_show_report", "yyd_online_payment
	 */
	@Override
	public JobResult handleBiz(String[] arg0) {
		if(arg0 == null || arg0.length == 0){
			return this.loadSuccessJobResult("没有需要执行的key");
		}
		if(taskBranchList == null || taskBranchList.size() == 0){
			return this.loadSuccessJobResult("没有预定义的报表Task分支");
		}
		logger.info("计数器倒计时：" + arg0.length);
		CountDownLatch latch = new CountDownLatch(arg0.length);
		String[][] entries = new String[arg0.length][4];
		for (int i = 0; i < arg0.length; i ++) {
			String[] param = arg0[i].split(",");
			if(param.length != 6){
				String errorMsg = "该定时任务配置参数不正确，请重新配置！";
				logger.error(errorMsg);
				return this.loadErrorJobResult(errorMsg);
			}else{
				entries[i] = param;
			}
		}
		for (String[] strs : entries) {
			for (TaskBranch taskBranch : taskBranchList) {
				if(taskBranch.getKey().trim().equals(strs[0])){
					logger.debug("加载key为：" + strs[0] + "的Task分支");
					taskBranch.loadComponent(latch, Integer.valueOf(strs[1]), Integer.valueOf(strs[2]), Boolean.valueOf(strs[3]), Boolean.valueOf(strs[4]), strs[5]);
					TASK_BRANCH_SERVICE.submit(taskBranch);
				}
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			String errorMsg = "当前Task主线程让步失败，" + e.getMessage();
			logger.debug(errorMsg);
			return this.loadErrorJobResult(errorMsg);
		}
//		Future<Integer> future = null;
//		try {
//			future = TASK_BRANCH_SERVICE.submit(new ReportMailTask(pkg));
//			return this.loadSuccessJobResult("总共成功处理：" + future.get() + "封邮件");
//		} catch (Exception e) {
//			String errorMsg = "Email任务执行失败，无法获取返回值，" + e.getMessage();
//			logger.debug(errorMsg);
//			return this.loadErrorJobResult(errorMsg);
//		}
		return this.loadSuccessJobResult("成功处理");
	}

	private JobResult loadErrorJobResult(String message){
		return this.loadJobResult(message, false);
	}
	
	private JobResult loadSuccessJobResult(String message){
		return this.loadJobResult(message, true);
	}
	
	/**
	 * 装载JobResult
	 * @param message
	 * @param success
	 * @return
	 */
	private JobResult loadJobResult(String message, boolean success){
		JobResult result = new JobResult();
		result.setMessage(message);
		result.setSuccess(success);
		return result;
	}
	
	/**
	 * 采用Spring的Bean后处理器，当Bean生成完整后，进行Excel模板的注册
	 * @throws Exception
	 */
	public void afterPropertiesSet() throws Exception {
		/*
		 * 注册全局销售报表
		 */
		register.defineTemplate("date_sale_insurance", "全局销售报表%s.xls", DaySaleReport.class)
			.defineCol("currentDate", "日期", 0, VARCHAR, true)
			.defineCol("angleFirst", "类型", 1, VARCHAR, true)
			.defineCol("angleSecond", "类型", 2, VARCHAR, true)
			.defineCol("angleThird", "类型", 3, VARCHAR, true)
			.defineCol("busiCount", "商业险保单销售量", 4, INTEGER)
			.defineCol("busiSum", "商业险保费金额", 5, DOUBLE)
			.defineCol("orderCount", "订单销售量", 6, INTEGER)
			.defineCol("orderSum", "订单总保费金额", 7, DOUBLE)
			.defineCol("busiTbRate", "商业险保单同比增长率", 8, VARCHAR)
			.defineCol("busiCountHbRate", "商业险保单环比增长率", 9, VARCHAR)
			.defineCol("busiSumHbRate", "商业险保费金额环比增长率", 10, VARCHAR)
			.defineCol("orderCountHbRate", "订单销售量环比增长率", 11, VARCHAR)
			.defineCol("orderSumHbRate", "订单保费金额环比增长率", 12, VARCHAR)
			.defineCol("cancleCount", "当月垫付后退保的订单数量", 13, INTEGER)
			.defineCol("cancleSum", "当月垫付后退保的订单保费金额", 14, DOUBLE);

		/*
		 * 注册每日确认下单明细报表定义
		 */
//		register.defineTemplate("每日确认下单明细报表", DailyConfirmedOrder.class)
//			.defineCol("insuranceId", "保单ID", 0, INTEGER)
//			.defineCol("today", "当前日期", 1, DATE)
//			.defineCol("source", "活跃来源", 2, VARCHAR)
//			.defineCol("nickName", "小蜜蜂微信号", 3, VARCHAR)
//			.defineCol("mobile", "小蜜蜂手机号", 4, VARCHAR)
//			.defineCol("cityName", "城市", 5, VARCHAR)
//			.defineCol("areaName", "区域", 6, VARCHAR)
//			.defineCol("managerName", "拓展员", 7, VARCHAR)
//			.defineCol("batchName", "渠道", 8, VARCHAR)
//			.defineCol("plateNumber", "车牌号", 9, VARCHAR)
//			.defineCol("createTime", "下单时间", 10, TIMESTAMP)
//			.defineCol("supplierName", "保险公司", 11, VARCHAR)
//			.defineCol("passportName", "运维人员", 12, VARCHAR);
		/*
		 * 注册每日小蜜蜂活跃度报表定义
		 */
//		register.defineTemplate("每日确认下单明细报表", DailyBeeActive.class)
//			.defineCol("recordDate", "日期", 0, DATE)
//			.defineCol("type", "类型", 1, VARCHAR, true)
//			.defineCol("area", "区域", 2, VARCHAR)
//			.defineCol("totalBeeCount", "累计小蜜蜂", 3, INTEGER)
//			.defineCol("newBeeCount", "新增小蜜蜂", 4, INTEGER)
//			.defineCol("dayActive", "日活跃", 5, INTEGER)
//			.defineCol("weekActive", "周活跃", 6, INTEGER)
//			.defineCol("monthActive", "月活跃", 7, INTEGER)
//			.defineCol("active", "活跃度", 8, FLOAT)
//			.defineCol("lostBeeCount", "小蜜蜂流失数", 9, VARCHAR)
//			.defineCol("recoverBeeCount", "小蜜蜂回流数", 10, VARCHAR);
		/*
		 * 注册财务应付明细报表定义
		 */
		register.defineTemplate("insurance_finance_show_report", "财务应付明细%s.xls", InsuranceFinanceReport.class)
			.defineCol("orderId", "订单号", 0, VARCHAR)
			.defineCol("dianfuDate", "垫付日期", 1, DATE)
			.defineCol("dianfu", "垫付时间", 2, TIMESTAMP)
			.defineCol("orderCreateTime", "订单生成时间", 3, TIMESTAMP)
			.defineCol("channelType", "渠道类型", 4, VARCHAR)
			.defineCol("channelName", "渠道", 5, VARCHAR)
			.defineCol("forceActivityName", "交强险活动", 6, VARCHAR)
			.defineCol("businessActivityName", "商业险活动", 7, VARCHAR)
			.defineCol("supplierName", "保险公司", 8, VARCHAR)
			.defineCol("opName", "坐席名称", 9, VARCHAR)
			.defineCol("carNumber", "车牌号", 10, VARCHAR)
			.defineCol("insurancePerson", "被保人", 11, VARCHAR)
			.defineCol("forceNumber", "交强险交易号", 12, VARCHAR)
			.defineCol("businessNumber", "商业险交易号", 13, VARCHAR)
			.defineCol("forceAmount", "交强险金额（元）", 14, FLOAT)
			.defineCol("taxAmount", "车船税金额（元）", 15, FLOAT)
			.defineCol("businessAmount", "商业险金额（元）", 16, FLOAT)
			.defineCol("insuranceReviewedAmount", "保单保费（包括车船税）（元）", 17, FLOAT)
			.defineCol("dianfuAmount", "垫付金额（元）", 18, FLOAT)
			.defineCol("forceZhekou", "交强险折扣率", 19, FLOAT)
			.defineCol("businessZhekou", "商业险折扣率", 20, FLOAT)
			.defineCol("forceZhekouAmount", "交强险折扣金额（元）", 21, FLOAT)
			.defineCol("businessZhekouAmount", "商业险折扣金额（元）", 22, FLOAT)
			.defineCol("insuranceProductsPayFirstAmount", "产品策略先付优惠金额", 23, FLOAT) 
			.defineCol("forceCouponAmount", "交强险优惠券金额（元）", 24, FLOAT)
			.defineCol("businessCouponAmount", "商业险优惠券金额（元）", 25, FLOAT)
			.defineCol("realAmount", "原始应付金额（元）", 26, FLOAT)
			.defineCol("orderExtraAmount", "另收(元）", 27, FLOAT)
			.defineCol("orderOutcome", "账户支出（元）", 28, FLOAT)
			.defineCol("orderUserPay", "用户付款金额（元）", 29, FLOAT)
			.defineCol("receiveAmount", "回款金额（元）", 30, FLOAT)
			.defineCol("chaeAmount", "回款差额（元）", 31, FLOAT)
			.defineCol("wancheng", "回款日期(配送完成或收款完成日期)", 32, TIMESTAMP)
			.defineCol("forceReward", "交强险追加奖励金额(元)", 32, FLOAT)
			.defineCol("businessReward", "商业险追加奖励金额(元)", 32, FLOAT)
			.defineCol("lipin", "促销礼品", 33, VARCHAR)
			.defineCol("statusName", "保单状态", 34, VARCHAR)
			.defineCol("flowDetail", "回款记录", 35, VARCHAR)
			.defineCol("cityName", "投保城市", 36, VARCHAR)
			.defineCol("payTime", "支付时间", 37, TIMESTAMP)
			.defineCol("payStyle", "支付方式", 38, VARCHAR)
			.defineCol("oemName", "OEM名称", 39, VARCHAR)
			.defineCol("oemPaymentMode", "结算方式", 40, VARCHAR)
			.defineCol("outTradeNo", "支付唯一订单号", 41, VARCHAR)
			.defineCol("forceCommissionAmount", "交强险手续费应收金额（元）", 42, FLOAT)
			.defineCol("businessCommissionAmount", "商业险手续费应收金额（元）", 43, FLOAT)
			.defineCol("forceRuleFormula", "交强险结算公式", 44, VARCHAR)
			.defineCol("businessRuleFormula", "商业险结算公式", 45, VARCHAR)
			.defineCol("forceCommissionRate", "交强险手续费比例或金额", 46, VARCHAR)
			.defineCol("businessCommissionRate", "商业险手续费比例或金额", 47, VARCHAR)
			.defineCol("oemCode", "OEM代号", 48, VARCHAR)
			.defineCol("oemAgencyName", "分支机构", 49, VARCHAR)
			.defineCol("orderSourceType", "订单来源", 50, VARCHAR)
			.defineCol("forceInsuranceOperationsAmount", "交强险运营策略折扣金额（元）", 51, FLOAT)
			.defineCol("businessInsuranceOperationsAmount", "商业险运营策略折扣金额（元）", 52, FLOAT)
			.defineCol("forceInsuranceOperationsFormula", "交强险运营策略折扣计算公式", 53, VARCHAR)
			.defineCol("businessInsuranceOperationsFormula", "商业险运营策略折扣计算公式", 54, VARCHAR)
			.defineCol("forceInsuranceOperationsPercentage", "交强险运营策略折扣百分比", 55, VARCHAR)
			.defineCol("businessInsuranceOperationsPercentage", "商业险运营策略折扣百分比", 56, VARCHAR)
			.defineCol("forceInsuranceStartDate", "交强险起保日期", 57, TIMESTAMP)
			.defineCol("businessInsuranceStartDate", "商业险起保日期", 58, TIMESTAMP)
			.defineCol("forceInsuranceEndDate", "交强险结束日期", 59, TIMESTAMP)
			.defineCol("businessInsuranceEndDate", "商业险结束日期", 60, TIMESTAMP)
			.defineCol("expressName", "物流公司", 61, VARCHAR)
			.defineCol("deliveryTimes", "配送次数", 62, INTEGER)
			.defineCol("flowFinishTime", "流程结束时间", 63, TIMESTAMP)
			.defineCol("signingCompanyName", "签约主体", 64, VARCHAR)
			.defineCol("cid", "小蜜蜂CID", 65, VARCHAR)
			.defineCol("dianfuComment", "待垫付申请备注", 66, VARCHAR)
			;
		/*
		 * 注册又一单先付明细报表定义
		 */
		register.defineTemplate("yyd_online_payment", "又一单先付明细%s.xls", YydOnlinePaymentReport.class)
			.defineCol("orderId", "订单号", 0, VARCHAR)
			.defineCol("carNumber", "车牌号", 1, VARCHAR)
			.defineCol("paymentTime", "支付时间", 2, TIMESTAMP)
			.defineCol("payStyle", "支付方式", 3, VARCHAR)
			.defineCol("commission", "佣金金额(元)", 4, FLOAT)
			.defineCol("opName", "下单人", 5, VARCHAR)
			.defineCol("sourceType", "订单来源", 6, VARCHAR)
			.defineCol("supplierName", "保司名称", 7, VARCHAR)
			.defineCol("addressMsg", "配送地址", 8, VARCHAR)
			.defineCol("mobile", "收货人手机号", 9, VARCHAR)
			.defineCol("recipientName", "收件人姓名", 10, VARCHAR)
			.defineCol("insuranceCity", "投保城市", 11, VARCHAR);
		
		/**
		 * TODO 
		 * 未考虑到如果多张报表多个列重复，或者完全一样，造成的模板定义重复的问题
		 * 后期替换方案，不再使用该注册模板的方式
		 */
		/*
		 * 小蜜蜂询价状态报表
		 */
		register.defineTemplate("bee_calc_report", "小蜜蜂活跃状态报表%s.xls", LBTotalRecord.class)
		.defineCol("recordDate", "日期", 0, DATE)
		.defineCol("nickName", "小蜜蜂微信名", 1, VARCHAR)
		.defineCol("beeName", "姓名", 2, VARCHAR)
		.defineCol("mobile", "电话", 3, VARCHAR)
		.defineCol("cityName", "城市", 4, VARCHAR)
		.defineCol("areaName", "地区", 5, VARCHAR)
		.defineCol("operName", "拓展员", 6, VARCHAR)
		.defineCol("fromSource", "小蜜蜂来源", 7, VARCHAR)
		.defineCol("beeType", "渠道", 8, VARCHAR)
		.defineCol("calcFrom0Status", "又一单状态", 9, VARCHAR)
		.defineCol("calcFromaStatus", "总体状态", 10, VARCHAR)
		.defineCol("beeStatus", "小蜜蜂状态", 11, VARCHAR);
		
		/*
		 * 所有的小蜜蜂数据导出报表
		 */
		register.defineTemplate("bee_total_report", "历史小蜜蜂明细%s.xls", LBGatherInfo.class)
		.defineCol("registerDate", "注册时间", 0, DATE)
		.defineCol("cid", "小蜜蜂CID", 1, INTEGER)
		.defineCol("fromSource", "注册来源", 2, VARCHAR)
		.defineCol("mobile", "小蜜蜂手机号", 3, VARCHAR)
		.defineCol("cityName", "城市", 4, VARCHAR)
		.defineCol("areaName", "地区", 5, VARCHAR)
		.defineCol("operName", "拓展", 6, VARCHAR)
		.defineCol("beeType", "经纪人类型", 7, VARCHAR)
		.defineCol("beeStatus", "经纪人状态", 8, VARCHAR)
		.defineCol("nickName", "小蜜蜂微信名", 9, VARCHAR)
		.defineCol("beeName", "小蜜蜂姓名", 10, VARCHAR)
		.defineCol("address", "地址", 11, VARCHAR)
		.defineCol("firstLoginTime", "首次登陆又一单时间", 12, TIMESTAMP)
		.defineCol("calcNumYyd", "又一单询价车数", 13, INTEGER)
		.defineCol("calcNumJjr", "经纪人询价车数", 14, INTEGER)
		.defineCol("chengdanNumYyd", "又一单成单车数", 15, INTEGER)
		.defineCol("chengdanNumJjr", "经纪人成单车数", 16, INTEGER);
		
		/*
		 * 开通又一单的小蜜蜂明细
		 */
		register.defineTemplate("bee_yyd_report", "又一单登陆小蜜蜂明细%s.xls", LBTotalRecord.class)
		.defineCol("recordDate", "日期", 0, DATE)
		.defineCol("nickName", "小蜜蜂微信名", 1, VARCHAR)
		.defineCol("beeName", "姓名", 2, VARCHAR)
		.defineCol("mobile", "电话", 3, VARCHAR)
		.defineCol("cityName", "城市", 4, VARCHAR)
		.defineCol("areaName", "地区", 5, VARCHAR)
		.defineCol("operName", "拓展员", 6, VARCHAR)
		.defineCol("fromSource", "小蜜蜂来源", 7, VARCHAR)
		.defineCol("beeType", "渠道", 8, VARCHAR);
		
		/*
		 * 每日下单明细
		 */
		register.defineTemplate("daily_order_report", "每日下单明细统计报表%s.xls", DailyOrder.class)
		.defineCol("insuranceId", "保单ID", 0, INTEGER)
		.defineCol("recordDate", "日期", 1, DATE)
		.defineCol("fromSource", "活跃来源", 2, VARCHAR)
		.defineCol("nickName", "小蜜蜂微信名", 3, VARCHAR)
		.defineCol("mobile", "电话", 4, VARCHAR)
		.defineCol("cityName", "城市", 5, VARCHAR)
		.defineCol("areaName", "地区", 6, VARCHAR)
		.defineCol("operName", "拓展员", 7, VARCHAR)
		.defineCol("beeType", "渠道", 8, VARCHAR)
		.defineCol("plateNumber", "车牌号", 9, VARCHAR)
		.defineCol("orderTime", "下单时间", 10, TIMESTAMP)
		.defineCol("supplierName", "保险公司", 11, VARCHAR)
		.defineCol("passportName", "运维人员", 12, VARCHAR);
		
		/*
		 * 每日垫付明细
		 */
//		register.defineTemplate("daily_chengdan_report.bak", "每日垫付明细统计报表(删除)%s.xls", DailyDianfuRecord.class)
//		.defineCol("recordDate", "日期", 1, VARCHAR)
//		.defineCol("calcFromSource", "报价来源", 2, VARCHAR)
//		.defineCol("nickName", "小蜜蜂微信名", 3, VARCHAR)
//		.defineCol("mobile", "电话", 4, VARCHAR)
//		.defineCol("cityName", "城市", 5, VARCHAR)
//		.defineCol("areaName", "地区", 6, VARCHAR)
//		.defineCol("operName", "拓展员", 7, VARCHAR)
//		.defineCol("beeType", "渠道", 8, VARCHAR)
//		.defineCol("plateNumber", "车牌号", 9, VARCHAR)
//		.defineCol("orderTime", "成单时间", 10, TIMESTAMP)
//		.defineCol("supplierName", "保险公司", 11, VARCHAR)
//		.defineCol("passportName", "运维人员", 12, VARCHAR)
//		.defineCol("force_discount_amount", "交强险金额", 13, FLOAT)
//		.defineCol("business_discount_amount", "商业险金额", 14, FLOAT)
//		.defineCol("vehicle_tax", "车船险金额", 15, FLOAT)
//		.defineCol("insurance_reviewed_amount", "垫付金额（元）", 16, FLOAT)
//		.defineCol("real_amount", "实收金额（元）", 17, FLOAT);
		
		/*
		 * 每日垫付明细
		 */
		register.defineTemplate("daily_dianfu_report", "历史垫付完成明细统计报表%s.xls", DailyDianfuRecord.class)
		.defineCol("recordDate", "日期", 1, DATE)
		.defineCol("calcFromSource", "报价来源", 2, VARCHAR)
		.defineCol("nickName", "小蜜蜂微信名", 3, VARCHAR)
		.defineCol("mobile", "电话", 4, VARCHAR)
		.defineCol("cityName", "城市", 5, VARCHAR)
		.defineCol("areaName", "地区", 6, VARCHAR)
		.defineCol("operName", "拓展员", 7, VARCHAR)
		.defineCol("beeType", "渠道", 8, VARCHAR)
		.defineCol("plateNumber", "车牌号", 9, VARCHAR)
		.defineCol("orderTime", "成单时间", 10, TIMESTAMP)
		.defineCol("supplierName", "保险公司", 11, VARCHAR)
		.defineCol("businessAmount", "商业险金额", 12, FLOAT)
		.defineCol("forceDiscountAmount", "交强险金额", 13, FLOAT)
		.defineCol("vehicleTax", "车船险金额", 14, FLOAT)
		.defineCol("totalAmount", "总金额（元）", 15, FLOAT)
		.defineCol("realAmount", "实收金额（元）", 16, FLOAT)
		.defineCol("businessStatusName", "商业险保单状态", 17, VARCHAR)
		.defineCol("forceStatusName", "交强险保单状态", 18, VARCHAR)
		.defineCol("operator", "订单运维人员", 19, VARCHAR);
		
		/*
		 * 每日成单明细
		 */
		register.defineTemplate("daily_calc_report", "每日询价明细统计报表%s.xls", DailyCalcRecord.class)
		.defineCol("recordDate", "日期", 1, DATE)
		.defineCol("calcFromSource", "报价来源", 2, VARCHAR)
		.defineCol("nickName", "小蜜蜂微信名", 3, VARCHAR)
		.defineCol("mobile", "小蜜蜂手机号", 4, VARCHAR)
		.defineCol("cityName", "城市", 5, VARCHAR)
		.defineCol("areaName", "地区", 6, VARCHAR)
		.defineCol("operName", "拓展员", 7, VARCHAR)
		.defineCol("beeType", "渠道", 8, VARCHAR)
		.defineCol("plateNumber", "车牌号", 9, VARCHAR)
		.defineCol("orderTime", "报价时间", 10, TIMESTAMP)
		.defineCol("calcAmount", "报价金额", 11, FLOAT)
		.defineCol("supplierName", "保险公司", 12, VARCHAR)
		.defineCol("passportName", "运维人员", 13, VARCHAR);
		
		/*
		 * 每日配送报表
		 */
		register.defineTemplate("daily_order_delivery", "每日配送统计报表%s.xls", OrderDelivery.class)
		.defineCol("recordDate", "日期", 1, DATE)
		.defineCol("cityName", "城市", 5, VARCHAR)
		.defineCol("orderId", "订单号", 4, BIGINT)
		.defineCol("deliveryWay", "配送方式", 6, VARCHAR);
	}
}
