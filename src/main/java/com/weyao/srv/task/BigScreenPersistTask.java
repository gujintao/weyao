package com.weyao.srv.task;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dsf.platform.jobs.job.JobResult;
import com.weyao.common.D;
import com.weyao.common.DateUtil;
import com.weyao.redis.RedisService;
import com.weyao.srv.constant.InteractionCOE;
import com.weyao.srv.constant.RedisKey;
import com.weyao.srv.report.entity.DataBigScreenCalcRequest;
import com.weyao.srv.report.entity.DataBigScreenTrends;
import com.weyao.srv.report.service.BigScreenPersistService;

public class BigScreenPersistTask extends AbstractTimerTask {

	private static Logger logger = LoggerFactory.getLogger(BigScreenPersistTask.class);

	@Autowired
	private RedisService redisService;
	
	 @Autowired
	 BigScreenPersistService bigScreenPersistService;
	 
	
	private static Integer timeInterval = 5;
	
	public JobResult handleBiz(String[] arg0) {
		JobResult result = new JobResult();
		try {
			result.setSendMail(true);
			logger.debug("定时持久化指标数据,开始------");
			String nowTime = D.formatDate("HH:mm");
			logger.debug("定时持久化指标数据,当前时间点：" + nowTime);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date date = calendar.getTime();
	        
			String dateStr = DateUtil.getFormatDate(date);
			DataBigScreenTrends dataBigScreenTrends = new DataBigScreenTrends();
			dataBigScreenTrends.setStatisDate(dateStr);
			// 今日报价规模
			String calcAmountKey = String.format(RedisKey.每日算价规模KEY,dateStr);
			String calcAmountStr = redisService.get(calcAmountKey);
			// redis中存的今日算价规模
			long calcAmount = Long.parseLong(calcAmountStr == null ? "0" : calcAmountStr);
			dataBigScreenTrends.setBiddingScale(calcAmount);
			
			// 今日报价次数
			String calcTimesKey = String.format(RedisKey.每日算价次数KEY,dateStr);
			String calcTimesStr = redisService.get(calcTimesKey);
			// redis中存的今日算价次数
			Integer calcTimes = Integer.parseInt(calcTimesStr == null ? "0" : calcTimesStr);
			dataBigScreenTrends.setCalcTimes(calcTimes);
			
			// 今日服务车主
			String calcCarsKey = String.format(RedisKey.每日服务车主KEY,dateStr);
			String calcCarsStr = redisService.get(calcCarsKey);
			// redis中存的今日算价次数
			Integer calcCars = Integer.parseInt(calcCarsStr == null ? "0" : calcCarsStr);
			dataBigScreenTrends.setServiceOwner(calcCars);
			
			// 交互量-又一单算价次数
			String yydCalcTimesKey = String.format(RedisKey.每日又一单算价次数KEY,dateStr);
			String yydCalcTimesStr = redisService.get(yydCalcTimesKey);
			// redis中存的今日又一单算价数
			Integer yydCalcTimes = Integer.parseInt(yydCalcTimesStr == null ? "0" : yydCalcTimesStr);
			dataBigScreenTrends.setYydCalcTimes(yydCalcTimes);

			// 交互量-经管算价次数
			String jgCalcTimesKey = String.format(RedisKey.每日经管算价次数KEY,dateStr);
			String jgCalcTimesStr = redisService.get(jgCalcTimesKey);
			// redis中存的今日经管算价数
			Integer jgCalcTimes = Integer.parseInt(jgCalcTimesStr == null ? "0" : jgCalcTimesStr);
			dataBigScreenTrends.setJgCalcTimes(jgCalcTimes);

			// 交互量-量子算价次数
			String crmCalcTimesKey = String.format(RedisKey.每日量子算价次数KEY,dateStr);
			String crmCalcTimesStr = redisService.get(crmCalcTimesKey);
			// redis中存的今日量子算价数
			Integer crmCalcTimes = Integer.parseInt(crmCalcTimesStr == null ? "0" : crmCalcTimesStr);
			dataBigScreenTrends.setCrmCalcTimes(crmCalcTimes);
			
			// 交互量-今日新建订单数 从redies中取  key的格式为：BIG_SCREEN_NEW_ORDER_yyyyMMdd. 例如：BIG_SCREEN_NEW_ORDER_20170802
			String bigScreenNewOrderKey = String.format(RedisKey.每日新建订单数KEY,dateStr);
			String bigScreenNewOrderStr = redisService.get(bigScreenNewOrderKey);
			// redis中存的今日新建订单数量
			long newOrderNum = Long.parseLong(bigScreenNewOrderStr == null ? "0" : bigScreenNewOrderStr);
			dataBigScreenTrends.setNewOrderNum(newOrderNum);

			// 交互量-今日完成订单数
			String bigScreenFinishOrderKey = String.format(RedisKey.每日完成订单数KEY,dateStr);
			String bigScreenFinishOrderStr = redisService.get(bigScreenFinishOrderKey);
			// redis中存的今日完成的订单数量 key的格式为：BIG_SCREEN_FINISH_ORDER_yyyyMMdd. 例如：BIG_SCREEN_FINISH_ORDER_20170802
			long finishOrderNum = Long.parseLong(bigScreenFinishOrderStr == null ? "0" : bigScreenFinishOrderStr);
			dataBigScreenTrends.setFinishedOrderNum(finishOrderNum);
			
			// 今日交互量的计算
			long interactionQuantity = yydCalcTimes * InteractionCOE.算价来源权值_又一单.getValue()
					+ jgCalcTimes * InteractionCOE.算价来源权值_经管.getValue()
					+ crmCalcTimes * InteractionCOE.算价来源权值_量子.getValue()
					+ newOrderNum * InteractionCOE.新增订单数_来源又一单或经管或者量子.getValue()
					+ finishOrderNum * InteractionCOE.完成的订单数.getValue();
			dataBigScreenTrends.setInteractionQuantity(interactionQuantity);
			// 持久化到数据库中，更新或插入
			bigScreenPersistService.saveOrUpdateTrends(dataBigScreenTrends);

			// 实时报价请求趋势 存每日的算价请求
			for (int i = 1; i <= 288; i++) {
				DataBigScreenCalcRequest dataBigScreenCalcRequest = new DataBigScreenCalcRequest();
				dataBigScreenCalcRequest.setStatisDate(dateStr);
				dataBigScreenCalcRequest.setTimeInterval(timeInterval);
				
				// 获取在redies里记录今日的各阶段的请求次数，即key为前缀+current_date+阶段；值为算价请求次数
				String calcRequestKey = String.format(RedisKey.每日各阶段算价请求次数KEY, dateStr, i);
				String calcRequestStr = redisService.get(calcRequestKey);
				// redis中存的今日的阶段stage的请求次数
				Integer calcRequest = calcRequestStr != null ? Integer.parseInt(calcRequestStr) : 0;
				
				dataBigScreenCalcRequest.setSegmentStage(i);
				dataBigScreenCalcRequest.setCalcRequestTimes(calcRequest);
				
				bigScreenPersistService.saveOrUpdateCalcRequest(dataBigScreenCalcRequest);
			}
			
			
			logger.debug("定时持久化指标数据,结束------");
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			e.printStackTrace();
			logger.error("定时持久化指标数据", e);
		}
		return result;
	}
 
}
