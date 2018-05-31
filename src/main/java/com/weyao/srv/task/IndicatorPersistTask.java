package com.weyao.srv.task;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dsf.platform.jobs.job.JobResult;
import com.google.gson.reflect.TypeToken;
import com.weyao.common.D;
import com.weyao.common.DateUtil;
import com.weyao.common.JsonHelper2;
import com.weyao.exception.SrvException;
import com.weyao.info.indicatorquery.Indicator;
import com.weyao.info.indicatorquery.Indicator.GroupCol;
import com.weyao.info.indicatorquery.Indicator.WhereCol;
import com.weyao.srv.indicatorquery.IndicatorQuerySrv;
import com.weyao.srv.report.entity.DataRegionalActiveAdd;
import com.weyao.srv.report.entity.DataRegionalCalcPaid;
import com.weyao.srv.report.entity.DataRegionalClassifyAchieve;
import com.weyao.srv.report.entity.DataRegionalOperation;
import com.weyao.srv.report.service.IndicatorPersistService;

public class IndicatorPersistTask extends AbstractTimerTask {

	private static Logger logger = LoggerFactory.getLogger(IndicatorPersistTask.class);

	@Autowired
	private IndicatorQuerySrv indicatorQuerySrv;

    @Autowired
    IndicatorPersistService<DataRegionalOperation> dataRegionalOperationPersistService;

    @Autowired
    IndicatorPersistService<DataRegionalCalcPaid> dataRegionalCalcPaidPersistService;
    
    @Autowired
    IndicatorPersistService<DataRegionalActiveAdd> dataRegionalActiveAddPersistService;
    
    @Autowired
    IndicatorPersistService<DataRegionalClassifyAchieve> dataRegionalClassifyAchievePersistService;
    
	@Override
	public JobResult handleBiz(String[] arg0) {
		JobResult result = new JobResult();
		try {
			result.setSendMail(true);
			logger.debug("定时持久化指标数据,开始------");
			String nowTime = D.formatDate("HH:mm");
			logger.debug("定时持久化指标数据,当前时间点：" + nowTime);
			
//			String startStr = "20170824";
//			String endStr = DateUtil.getFormatDate(new Date());
//			Calendar start = Calendar.getInstance();
//			Calendar end = Calendar.getInstance();
//			try {
//				start.setTime(DateUtil.convertToDate(startStr));
//				end.setTime(DateUtil.convertToDate(endStr));
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			
//		while (start.before(end)) {
//			String nowDateStr = DateUtil.getFormatDate(start.getTime());
//			String firstOfMonth = DateUtil.getFirstOfMonth(start.getTime());
//			String nowMonthStr = DateUtil.getFormatMonth(start.getTime());
//			String firstOfWeek = DateUtil.getFirstOfWeek(start.getTime());
			
			String nowDateStr = DateUtil.getFormatDate(new Date());
			String firstOfMonth = DateUtil.getFirstOfMonth(new Date());
			String nowMonthStr = DateUtil.getFormatMonth(new Date());
			String firstOfWeek = DateUtil.getFirstOfWeek(new Date());
			
			String thirtyDaysAgo = DateUtil.addDaysToDate(nowDateStr, Indicator.thirtyDays活跃数);
			String cityStr = "city_id";
			String calcCityStr = "d_calc_city";
			String ordCityStr = "d_order_city";
			String beeCityStr = "city_id";
			String countStr = "count";
			// 区域运营报表相关指标持久化
			Set<Long> citySet =new HashSet<Long>();
			// 1、当日成单车辆数 垫付完成的车数
			Map<WhereCol, Object> dayPaidCarWhereCol = new HashMap<WhereCol, Object>();
			dayPaidCarWhereCol.put(Indicator.垫付订单车数.where开始时间, nowDateStr);
			dayPaidCarWhereCol.put(Indicator.垫付订单车数.where结束时间, nowDateStr);
			String jsonDayPaidCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付订单车数, 
					Arrays.asList(Indicator.垫付订单车数.日,Indicator.垫付订单车数.订单城市ID), 
					dayPaidCarWhereCol);
			List<Map<String, Object>> jsonDayPaidCarNumList = JsonHelper2.fromJson(jsonDayPaidCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> dayPaidCarNumMap = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonDayPaidCarNumList, dayPaidCarNumMap, citySet, ordCityStr, countStr); 
			
			// 2、当天完成商业险垫付车辆数
			Map<WhereCol, Object> dayPaidBusiCarWhereCol = new HashMap<WhereCol, Object>();
			dayPaidBusiCarWhereCol.put(Indicator.垫付商业险车数.where开始时间, nowDateStr);
			dayPaidBusiCarWhereCol.put(Indicator.垫付商业险车数.where结束时间, nowDateStr);
			String jsonDayPaidBusiCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付商业险车数, 
					Arrays.asList(Indicator.垫付商业险车数.日,Indicator.垫付商业险车数.订单城市ID), dayPaidBusiCarWhereCol);
			List<Map<String, Object>> jsonDayPaidBusiCarNumList = JsonHelper2.fromJson(jsonDayPaidBusiCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> dayPaidBusiCarNumMap = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonDayPaidBusiCarNumList, dayPaidBusiCarNumMap, citySet, ordCityStr, countStr);
			
			// 3、当月累计完成垫付的车辆数
			Map<WhereCol, Object> monthPaidCarWhereCol = new HashMap<WhereCol, Object>();
			monthPaidCarWhereCol.put(Indicator.垫付订单车数.where开始时间, firstOfMonth);
			monthPaidCarWhereCol.put(Indicator.垫付订单车数.where结束时间, nowDateStr);
			String jsonMonthPaidCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付订单车数, 
					Arrays.asList(Indicator.垫付订单车数.月,Indicator.垫付订单车数.订单城市ID), monthPaidCarWhereCol);
			List<Map<String, Object>> jsonMonthPaidCarNumList = JsonHelper2.fromJson(jsonMonthPaidCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> monthPaidCarNumMap = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonMonthPaidCarNumList, monthPaidCarNumMap, citySet, ordCityStr, countStr);
			 
			// 4、当月完成商业险垫付车辆数
			Map<WhereCol, Object> monthPaidBusiCarWhereCol = new HashMap<WhereCol, Object>();
			monthPaidBusiCarWhereCol.put(Indicator.垫付商业险车数.where开始时间, firstOfMonth);
			monthPaidBusiCarWhereCol.put(Indicator.垫付商业险车数.where结束时间, nowDateStr);
			String jsonMonthPaidBusiCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付商业险车数, 
					Arrays.asList(Indicator.垫付商业险车数.月,Indicator.垫付商业险车数.订单城市ID), monthPaidBusiCarWhereCol);
			List<Map<String, Object>> jsonMonthPaidBusiCarNumList = JsonHelper2.fromJson(jsonMonthPaidBusiCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> monthPaidBusiCarNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonMonthPaidBusiCarNumList, monthPaidBusiCarNum, citySet, ordCityStr, countStr);
			
			// 5、月累计保单保费：当月累计的保单保费金额，精确到分，以垫付完成为准
			Map<WhereCol, Object> monthPaidPremiumWhereCol = new HashMap<WhereCol, Object>();
			monthPaidPremiumWhereCol.put(Indicator.垫付订单保单保费金额.where开始时间, firstOfMonth);
			monthPaidPremiumWhereCol.put(Indicator.垫付订单保单保费金额.where结束时间, nowDateStr);
			String jsonMonthPaidPremium = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付订单保单保费金额, 
					Arrays.asList(Indicator.垫付订单保单保费金额.月,Indicator.垫付订单保单保费金额.订单城市ID), monthPaidPremiumWhereCol);
			List<Map<String, Object>> jsonMonthPaidPremiumList = JsonHelper2.fromJson(jsonMonthPaidPremium, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> monthPaidPremium = new HashMap<String, Long>();
			Map<String,Long> monthPaidOrderNum = new HashMap<String, Long>();
			for (Map<String, Object> m : jsonMonthPaidPremiumList){
				String dateCityKey = nowDateStr;
				Long countValue = 0l;
				Long premiumValue = 0l;
				for (Map.Entry<String, Object> entry : m.entrySet()) {
					String a = entry.getKey();
					Object b = entry.getValue();
					 if (b instanceof Double) {
						if(b.toString().indexOf("E")>0){
							BigDecimal bd = new BigDecimal(b.toString());  
							String str = bd.toPlainString();
							b = Long.parseLong(str);
						} else {
							BigDecimal bd = new BigDecimal(b.toString());  
							b = bd.longValue();
						}
					}
					if (a.equals("d_order_city")){
						citySet.add((Long) b);
						dateCityKey += b.toString();
					} else if (a.equals("count")){
						countValue = (Long) b;
					} else if (a.equals("premium")){
						premiumValue = (Long) b;
					}
				}
				monthPaidPremium.put(dateCityKey, premiumValue);
				monthPaidOrderNum.put(dateCityKey, countValue);
			}
			
			// 6、月累计退保保费（订单数）：当月累计的退保单数，退保的保单保费金额 暂且留空
			// 7、当日询价车辆数：当天有询价的车辆数
			Map<WhereCol, Object> dayCalcCarWhereCol = new HashMap<WhereCol, Object>();
			dayCalcCarWhereCol.put(Indicator.询价车辆数.where开始时间, nowDateStr);
			dayCalcCarWhereCol.put(Indicator.询价车辆数.where结束时间, nowDateStr);
			String jsonDayCalcCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.询价车辆数, 
					Arrays.asList(Indicator.询价车辆数.日,Indicator.询价车辆数.询价城市ID), dayCalcCarWhereCol);
			List<Map<String, Object>> jsonDayCalcCarNumList = JsonHelper2.fromJson(jsonDayCalcCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> dayCalcCarNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonDayCalcCarNumList, dayCalcCarNum, citySet, calcCityStr, countStr);
			
			// 8、月累计询价车辆数：当月累计询价的车辆数，本月内car_id去重。
			Map<WhereCol, Object> monthCalcCarWhereCol = new HashMap<WhereCol, Object>();
			monthCalcCarWhereCol.put(Indicator.询价车辆数.where开始时间, firstOfMonth);
			monthCalcCarWhereCol.put(Indicator.询价车辆数.where结束时间, nowDateStr);
			String jsonMonthCalcCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.询价车辆数, 
					Arrays.asList(Indicator.询价车辆数.月,Indicator.询价车辆数.询价城市ID), monthCalcCarWhereCol);
			List<Map<String, Object>> jsonMonthCalcCarNumList = JsonHelper2.fromJson(jsonMonthCalcCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> monthCalcCarNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonMonthCalcCarNumList, monthCalcCarNum, citySet, calcCityStr, countStr);
			
			// 9、当日成单率：当日成单车辆数/询价车辆数*100%  由两个值计算而得，在查询的时候计算此字段，不持久化
			// 10、累计小蜜蜂数：非冻结小蜜蜂数  累计小蜜蜂数没有开始日期
			Map<WhereCol, Object> beeTotalWhereCol = new HashMap<WhereCol, Object>();
			beeTotalWhereCol.put(Indicator.小蜜蜂累计数.where结束时间, nowDateStr);
			String jsonBeeTotalNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂累计数, 
					Arrays.asList(Indicator.小蜜蜂累计数.城市ID), beeTotalWhereCol);
			List<Map<String, Object>> jsonBeeTotalNumList = JsonHelper2.fromJson(jsonBeeTotalNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> beeTotalNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonBeeTotalNumList, beeTotalNum, citySet, cityStr, countStr);
			
			// 11、累计又一单小蜜蜂数  小蜜蜂又一单登录累计数
			Map<WhereCol, Object> yydBeeTotalWhereCol = new HashMap<WhereCol, Object>();
			yydBeeTotalWhereCol.put(Indicator.小蜜蜂又一单登录累计数.where结束时间, nowDateStr);
			String jsonYYDBeeTotalNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂又一单登录累计数, 
					Arrays.asList(Indicator.小蜜蜂又一单登录累计数.城市ID), yydBeeTotalWhereCol);
			List<Map<String, Object>> jsonYYDBeeTotalNumList = JsonHelper2.fromJson(jsonYYDBeeTotalNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> yydBeeTotalNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonYYDBeeTotalNumList, yydBeeTotalNum, citySet, cityStr, countStr);
			
			// 12、日询价活跃率：当天有询价的小蜜蜂占比，当天有询价的小蜜蜂数/累计小蜜蜂数*100%  取小蜜蜂日活跃数，率通过展示除法求
			Map<WhereCol, Object> dayBeeActivityWhereCol = new HashMap<WhereCol, Object>();
			dayBeeActivityWhereCol.put(Indicator.小蜜蜂活跃数.where开始时间, nowDateStr);
			dayBeeActivityWhereCol.put(Indicator.小蜜蜂活跃数.where结束时间, nowDateStr);
			String jsonDayBeeActivityNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂活跃数, 
					Arrays.asList(Indicator.小蜜蜂活跃数.日, Indicator.小蜜蜂活跃数.城市ID), dayBeeActivityWhereCol);
			List<Map<String, Object>> jsonDayBeeActivityNumList = JsonHelper2.fromJson(jsonDayBeeActivityNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> dayBeeActivityNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonDayBeeActivityNumList, dayBeeActivityNum, citySet, cityStr, countStr);
			
			// 13、30天询价活跃率：30天内（包含当天）有询价的小蜜蜂占比，30天内（包含当天）有询价的小蜜蜂数/累计小蜜蜂数*100%
			Map<WhereCol, Object> thirtyDayBeeActivityWhereCol = new HashMap<WhereCol, Object>();
			thirtyDayBeeActivityWhereCol.put(Indicator.小蜜蜂活跃数.where开始时间, thirtyDaysAgo);
			thirtyDayBeeActivityWhereCol.put(Indicator.小蜜蜂活跃数.where结束时间, nowDateStr);
			String jsonThirtyDayBeeActivityNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂活跃数, 
					Arrays.asList(Indicator.小蜜蜂活跃数.城市ID), thirtyDayBeeActivityWhereCol);
			List<Map<String, Object>> jsonThirtyDayBeeActivityNumList = JsonHelper2.fromJson(jsonThirtyDayBeeActivityNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> thirtyDayBeeActivityNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonThirtyDayBeeActivityNumList, thirtyDayBeeActivityNum, citySet, cityStr, countStr);
			
			// 14、30天出单活跃率：30天内（包含当天）有出单的小蜜蜂占比，30天内（包含当天）有出单的小蜜蜂数/累计小蜜蜂数*100%
			Map<WhereCol, Object> thirtyOrderActivityBeeWhereCol = new HashMap<WhereCol, Object>();
			thirtyOrderActivityBeeWhereCol.put(Indicator.小蜜蜂出单活跃数.where开始时间, thirtyDaysAgo);
			thirtyOrderActivityBeeWhereCol.put(Indicator.小蜜蜂出单活跃数.where结束时间, nowDateStr);
			String jsonThirtyOrderActivityBeeNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂出单活跃数, 
					Arrays.asList(Indicator.小蜜蜂出单活跃数.城市ID), thirtyOrderActivityBeeWhereCol);
			List<Map<String, Object>> jsonThirtyOrderActivityBeeNumList = JsonHelper2.fromJson(jsonThirtyOrderActivityBeeNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> thirtyOrderActivityNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonThirtyOrderActivityBeeNumList, thirtyOrderActivityNum, citySet, cityStr, countStr);
			
			// 15、30天商业险出单活跃率 ：30天内（包含当天）有出商业险的小蜜蜂占比，30天内（包含当天）有出商业险的小蜜蜂数/累计小蜜蜂数*100%
			Map<WhereCol, Object> thirtyVCIOrderActivityBeeWhereCol = new HashMap<WhereCol, Object>();
			thirtyVCIOrderActivityBeeWhereCol.put(Indicator.小蜜蜂商业险出单活跃数.where开始时间, thirtyDaysAgo);
			thirtyVCIOrderActivityBeeWhereCol.put(Indicator.小蜜蜂商业险出单活跃数.where结束时间, nowDateStr);
			String jsonThirtyVCIOrderActivityBeeNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂商业险出单活跃数, 
					Arrays.asList(Indicator.小蜜蜂商业险出单活跃数.城市ID), thirtyVCIOrderActivityBeeWhereCol);
			List<Map<String, Object>> jsonThirtyVCIOrderActivityBeeNumList = JsonHelper2.fromJson(jsonThirtyVCIOrderActivityBeeNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Map<String,Long> thirtyVCIOrderActivityNum = new HashMap<String, Long>();
			listToMap(nowDateStr, jsonThirtyVCIOrderActivityBeeNumList, thirtyVCIOrderActivityNum, citySet, cityStr, countStr);
			
			for (Iterator<Long> city = citySet.iterator(); city.hasNext(); ) {
				Integer cityId = city.next().intValue();
				String mapKey = nowDateStr;
				DataRegionalOperation dataRegionalOperation = new DataRegionalOperation();
				dataRegionalOperation.setStatisDate(Integer.parseInt(nowDateStr));
				dataRegionalOperation.setCityId(cityId);
				mapKey += cityId.toString();
				dataRegionalOperation.setDayPaidCarNum(dayPaidCarNumMap.isEmpty() || !dayPaidCarNumMap.containsKey(mapKey) ? 0 : dayPaidCarNumMap.get(mapKey)); // 当日成单车辆数 垫付完成的车数
				dataRegionalOperation.setDayPaidBusiCarNum(dayPaidBusiCarNumMap.isEmpty() || !dayPaidBusiCarNumMap.containsKey(mapKey) ? 0 : dayPaidBusiCarNumMap.get(mapKey)); // 当天完成商业险垫付车辆数
				dataRegionalOperation.setMonthPaidCarNum(monthPaidCarNumMap.isEmpty() || !monthPaidCarNumMap.containsKey(mapKey) ? 0 : monthPaidCarNumMap.get(mapKey)); // 当月累计完成垫付的车辆数
				dataRegionalOperation.setMonthPaidBusiCarNum(monthPaidBusiCarNum.isEmpty() || !monthPaidBusiCarNum.containsKey(mapKey) ? 0 : monthPaidBusiCarNum.get(mapKey)); // 当月完成商业险垫付车辆数
				dataRegionalOperation.setMonthPaidOrderNum(monthPaidOrderNum.isEmpty() || !monthPaidOrderNum.containsKey(mapKey) ? 0 : monthPaidOrderNum.get(mapKey)); // 月累计保单数量
				dataRegionalOperation.setMonthPaidPremium(monthPaidPremium.isEmpty() || !monthPaidPremium.containsKey(mapKey) ? 0 : monthPaidPremium.get(mapKey)); // 月累计保单保费
				dataRegionalOperation.setDayCalcCarNum(dayCalcCarNum.isEmpty() || !dayCalcCarNum.containsKey(mapKey) ? 0 : dayCalcCarNum.get(mapKey)); // 当日询价车辆数
				dataRegionalOperation.setMonthCalcCarNum(monthCalcCarNum.isEmpty() || !monthCalcCarNum.containsKey(mapKey) ? 0 : monthCalcCarNum.get(mapKey)); // 月累计询价车辆数
				dataRegionalOperation.setBeeTotalNum(beeTotalNum.isEmpty() || !beeTotalNum.containsKey(mapKey) ? 0 : beeTotalNum.get(mapKey)); // 累计小蜜蜂数
				dataRegionalOperation.setYydBeeTotalNum(yydBeeTotalNum.isEmpty() || !yydBeeTotalNum.containsKey(mapKey) ? 0 : yydBeeTotalNum.get(mapKey)); // 累计又一单小蜜蜂数 
				dataRegionalOperation.setDayBeeActivity(dayBeeActivityNum.isEmpty() || !dayBeeActivityNum.containsKey(mapKey) ? 0 : dayBeeActivityNum.get(mapKey)); // 小蜜蜂日活跃数
				dataRegionalOperation.setThirtyDaysBeeActivity(thirtyDayBeeActivityNum.isEmpty() || !thirtyDayBeeActivityNum.containsKey(mapKey) ? 0 : thirtyDayBeeActivityNum.get(mapKey)); // 30天询价活跃数
				dataRegionalOperation.setThirtyDaysOrderActivity(thirtyOrderActivityNum.isEmpty() || !thirtyOrderActivityNum.containsKey(mapKey) ? 0 : thirtyOrderActivityNum.get(mapKey)); // 30天出单活跃数
				dataRegionalOperation.setThirtyDaysVCIorderActivity(thirtyVCIOrderActivityNum.isEmpty() || !thirtyVCIOrderActivityNum.containsKey(mapKey) ? 0 : thirtyVCIOrderActivityNum.get(mapKey)); // 30天商业险出单活跃数
				if (dataRegionalOperation.getDayPaidCarNum() != 0 || dataRegionalOperation.getDayPaidBusiCarNum() != 0
						|| dataRegionalOperation.getMonthPaidCarNum() != 0 || dataRegionalOperation.getMonthPaidBusiCarNum() != 0
						|| dataRegionalOperation.getMonthPaidOrderNum() != 0 || dataRegionalOperation.getMonthPaidPremium() != 0
						|| dataRegionalOperation.getDayCalcCarNum() != 0 || dataRegionalOperation.getMonthCalcCarNum() != 0
						|| dataRegionalOperation.getBeeTotalNum() != 0 || dataRegionalOperation.getYydBeeTotalNum() != 0
						|| dataRegionalOperation.getDayBeeActivity() != 0 || dataRegionalOperation.getThirtyDaysBeeActivity() != 0
						|| dataRegionalOperation.getThirtyDaysOrderActivity() != 0 
						|| dataRegionalOperation.getThirtyDaysVCIorderActivity() != 0) {
					dataRegionalOperationPersistService.saveOrUpdate(dataRegionalOperation);
				}
				
			}
			
			// 区域运营询价&成单
			// 月累计
			persistDataRegionalCalcPaid(nowDateStr, firstOfMonth, nowMonthStr, nowMonthStr, "0", "month", calcCityStr,
					ordCityStr, countStr);
			// 周累计
			Map<WhereCol, Object> weekWhereCol = new HashMap<WhereCol, Object>();
			weekWhereCol.put(Indicator.获取天所在的周.where日期, nowDateStr); 
			String jsonWeekId = indicatorQuerySrv.indicatorNormalQuery(Indicator.获取天所在的周, 
					Arrays.asList(Indicator.获取天所在的周.周次代码) ,weekWhereCol);
			List<Map<String, Object>> jsonWeekIdList = JsonHelper2.fromJson(jsonWeekId, new TypeToken<List<Map<String, Object>>>() {}.getType());
			Object week = jsonWeekIdList.get(0).get("year_week_id");
			BigDecimal bd = new BigDecimal(week.toString());  
			week = bd.longValue();
			String weekId = week.toString();
			persistDataRegionalCalcPaid(nowDateStr, firstOfWeek, weekId, "0", weekId, "week", calcCityStr,
					ordCityStr, countStr);
			// 日累计
			persistDataRegionalCalcPaid(nowDateStr, nowDateStr, nowDateStr, "0", "0", "day", calcCityStr,
					ordCityStr, countStr);
			
			// 区域运营活跃&增长
			// 月累计
			persistDataRegionalAcitveAdd(nowDateStr, firstOfMonth, nowMonthStr, nowMonthStr, "0", "month", 
					beeCityStr, countStr);
			// 周累计
			persistDataRegionalAcitveAdd(nowDateStr, firstOfWeek, weekId, "0", weekId, "week", beeCityStr, countStr);
			// 日累计
			persistDataRegionalAcitveAdd(nowDateStr, nowDateStr, nowDateStr, "0", "0", "day", beeCityStr, countStr);
			
			// 分类业务统计
			// 拓展
			String tuozhanId = "d_bee_tuozhan";
			String tuozhanName = "d_bee_tuozhan_name_cn";
			// 按日
			persistDataRegionalClassifyAchieve(nowDateStr, nowDateStr, "day", nowDateStr, calcCityStr, ordCityStr, countStr, 0, tuozhanId, tuozhanName);
			// 按月
			persistDataRegionalClassifyAchieve(nowDateStr, firstOfMonth, "month", nowMonthStr, calcCityStr, ordCityStr, countStr, 0, tuozhanId, tuozhanName);
			
			// 渠道
			String qudaoId = "d_bee_type";
			String qudaoName = "d_bee_type_name_cn";
			// 按日
			persistDataRegionalClassifyAchieve(nowDateStr, nowDateStr, "day", nowDateStr, calcCityStr, ordCityStr, countStr, 1, qudaoId, qudaoName);
			// 按月
			persistDataRegionalClassifyAchieve(nowDateStr, firstOfMonth, "month", nowMonthStr, calcCityStr, ordCityStr, countStr, 1, qudaoId, qudaoName);
			
			// 保司
			String supplierId = "supplier_id";
			String supplierName = "supplier_name_cn";
			// 按日
			persistDataRegionalClassifyAchieve(nowDateStr, nowDateStr, "day", nowDateStr, calcCityStr, ordCityStr, countStr, 2, supplierId, supplierName);
			// 按月
			persistDataRegionalClassifyAchieve(nowDateStr, firstOfMonth, "month", nowMonthStr, calcCityStr, ordCityStr, countStr, 2, supplierId, supplierName);
			
			logger.debug("定时持久化指标数据,结束------");
			result.setSuccess(true);
//			start.add(Calendar.DAY_OF_MONTH, 1);
//		}
		} catch (Exception e) {
			result.setSuccess(false);
			e.printStackTrace();
			logger.error("定时持久化指标数据", e);
		}
		return result;
	}

	private void persistDataRegionalClassifyAchieve(String nowDateStr, String startDateStr, String groupByDateType, String subKey,
			String calcCityStr, String ordCityStr, String countStr, int classifyType, String classifyCodeStr, String classifyNameStr) 
					throws SrvException {
		
		GroupCol calcClassifyCodeCol = null;
		GroupCol calcClassifyNameCol = null;
		GroupCol ordClassifyCodeCol = null;
		GroupCol ordClassifyNameCol = null;
		GroupCol onlyTCIClassifyCodeCol = null;
		GroupCol onlyTCIClassifyNameCol = null;
		if(classifyType == 0) {
			calcClassifyCodeCol = Indicator.询价车辆数.拓展员ID;
			calcClassifyNameCol = Indicator.询价车辆数.拓展员名称;
			ordClassifyCodeCol = Indicator.垫付商业险车数.拓展员ID;
			ordClassifyNameCol = Indicator.垫付商业险车数.拓展员名称;
			onlyTCIClassifyCodeCol = Indicator.垫付单交强车数.拓展员ID;
			onlyTCIClassifyNameCol = Indicator.垫付单交强车数.拓展员名称;
		} else if(classifyType == 1) {
			calcClassifyCodeCol = Indicator.询价车辆数.小蜜蜂类型ID;
			calcClassifyNameCol = Indicator.询价车辆数.小蜜蜂类型;
			ordClassifyCodeCol = Indicator.垫付商业险车数.小蜜蜂类型ID;
			ordClassifyNameCol = Indicator.垫付商业险车数.小蜜蜂类型;
			onlyTCIClassifyCodeCol = Indicator.垫付单交强车数.小蜜蜂类型ID;
			onlyTCIClassifyNameCol = Indicator.垫付单交强车数.小蜜蜂类型;
		} else if(classifyType == 2) {
			calcClassifyCodeCol = Indicator.询价车辆数.询价保司ID;
			calcClassifyNameCol = Indicator.询价车辆数.询价保司名称;
			ordClassifyCodeCol = Indicator.垫付商业险车数.订单保司ID;
			ordClassifyNameCol = Indicator.垫付商业险车数.订单保司名称;
			onlyTCIClassifyCodeCol = Indicator.垫付单交强车数.订单保司ID;
			onlyTCIClassifyNameCol = Indicator.垫付单交强车数.订单保司名称;
		}
		GroupCol calcDateType = null;
		GroupCol paidVCIDateType = null;
		GroupCol paidOnlyTCIDateType = null;
		if("month".equals(groupByDateType)) {
			calcDateType = Indicator.询价车辆数.月; 
			paidVCIDateType = Indicator.垫付商业险车数.月;
			paidOnlyTCIDateType = Indicator.垫付单交强车数.月; 
		} else if("day".equals(groupByDateType)) {
			calcDateType = Indicator.询价车辆数.日; 
			paidVCIDateType = Indicator.垫付商业险车数.日;
			paidOnlyTCIDateType = Indicator.垫付单交强车数.日; 
		}
		
		Set<Long> citySet =new HashSet<Long>();
		Set<Long> classifyCodeSet =new HashSet<Long>();
		Set<String> classifyNameSet =new HashSet<String>();
		// 1、经管询价车数 每日 算价的系统来源: 又一单：1，经纪人：2
		Map<WhereCol, Object> jgCalcCarWhereCol = new HashMap<WhereCol, Object>();
		jgCalcCarWhereCol.put(Indicator.询价车辆数.where开始时间, startDateStr);
		jgCalcCarWhereCol.put(Indicator.询价车辆数.where结束时间, nowDateStr);
		jgCalcCarWhereCol.put(Indicator.询价车辆数.where询价系统来源, 2);
		String jsonJGCalcCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.询价车辆数, 
				Arrays.asList(calcDateType,Indicator.询价车辆数.询价城市ID,calcClassifyCodeCol,
						calcClassifyNameCol), jgCalcCarWhereCol);
		List<Map<String, Object>> jsonJGCalcCarNumList = JsonHelper2.fromJson(jsonJGCalcCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> jgCalcCarMap = new HashMap<String, Long>();
		listToMap(subKey, jsonJGCalcCarNumList, jgCalcCarMap,
				citySet, classifyCodeSet, classifyNameSet, calcCityStr, classifyCodeStr, classifyNameStr, countStr);

		// 2、又一单询价车数 每日 算价的系统来源: 又一单：1，经纪人：2
		Map<WhereCol, Object> yydCalcCarWhereCol = new HashMap<WhereCol, Object>();
		yydCalcCarWhereCol.put(Indicator.询价车辆数.where开始时间, startDateStr);
		yydCalcCarWhereCol.put(Indicator.询价车辆数.where结束时间, nowDateStr);
		yydCalcCarWhereCol.put(Indicator.询价车辆数.where询价系统来源, 1);
		String jsonYYDCalcCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.询价车辆数, 
				Arrays.asList(calcDateType,Indicator.询价车辆数.询价城市ID,calcClassifyCodeCol,
						calcClassifyNameCol),yydCalcCarWhereCol);
		List<Map<String, Object>> jsonYYDCalcCarNumList = JsonHelper2.fromJson(jsonYYDCalcCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydCalcCarMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDCalcCarNumList, yydCalcCarMap,
				citySet, classifyCodeSet, classifyNameSet, calcCityStr, classifyCodeStr, classifyNameStr, countStr);
		
		// 3、总询价车数
		Map<WhereCol, Object> overallCalcCarWhereCol = new HashMap<WhereCol, Object>();
		overallCalcCarWhereCol.put(Indicator.询价车辆数.where开始时间, startDateStr);
		overallCalcCarWhereCol.put(Indicator.询价车辆数.where结束时间, nowDateStr);
		String jsonOverallCalcCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.询价车辆数, 
				Arrays.asList(calcDateType,Indicator.询价车辆数.询价城市ID,calcClassifyCodeCol,
						calcClassifyNameCol), overallCalcCarWhereCol);
		List<Map<String, Object>> jsonOverallCalcCarNumList = JsonHelper2.fromJson(jsonOverallCalcCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> overallCalcCarNum = new HashMap<String, Long>();
		listToMap(subKey, jsonOverallCalcCarNumList, overallCalcCarNum,
				citySet, classifyCodeSet, classifyNameSet, calcCityStr, classifyCodeStr, classifyNameStr, countStr);
		
		// 4、经管订单车数，  城市的垫付完成的含商业险的订单的车数   订单来源：300:经纪人；400:又一单
		Map<WhereCol, Object> jgPaidVCICarNumWhereCol = new HashMap<WhereCol, Object>();
		jgPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where开始时间, startDateStr);
		jgPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where结束时间, nowDateStr);
		jgPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where订单来源, 300);
		String jsonJGPaidVCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付商业险车数, 
				Arrays.asList(paidVCIDateType,Indicator.垫付商业险车数.订单城市ID,ordClassifyCodeCol,
						ordClassifyNameCol), jgPaidVCICarNumWhereCol);
		List<Map<String, Object>> jsonJGPaidVCICarNumList = JsonHelper2.fromJson(jsonJGPaidVCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> jgPaidVCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonJGPaidVCICarNumList, jgPaidVCICarNumMap,
				citySet, classifyCodeSet, classifyNameSet, ordCityStr, classifyCodeStr, classifyNameStr, countStr);

		// 5、又一单成单车数，含商业险的订单(垫付完成)  
		Map<WhereCol, Object> yydPaidVCICarNumWhereCol = new HashMap<WhereCol, Object>();
		yydPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where开始时间, startDateStr);
		yydPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where结束时间, nowDateStr);
		yydPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where订单来源, 400);
		String jsonYYDPaidVCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付商业险车数, 
				Arrays.asList(paidVCIDateType,Indicator.垫付商业险车数.订单城市ID,ordClassifyCodeCol,
						ordClassifyNameCol), yydPaidVCICarNumWhereCol);
		List<Map<String, Object>> jsonYYDPaidVCICarNumList = JsonHelper2.fromJson(jsonYYDPaidVCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydPaidVCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDPaidVCICarNumList, yydPaidVCICarNumMap,
				citySet, classifyCodeSet, classifyNameSet, ordCityStr, classifyCodeStr, classifyNameStr, countStr);

		// 6、单交强成单车数(垫付完成) 
		Map<WhereCol, Object> onlyPaidTCICarNumWhereCol = new HashMap<WhereCol, Object>();
		onlyPaidTCICarNumWhereCol.put(Indicator.垫付单交强车数.where开始时间, startDateStr);
		onlyPaidTCICarNumWhereCol.put(Indicator.垫付单交强车数.where结束时间, nowDateStr);
		String jsonOnlyPaidTCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付单交强车数, 
				Arrays.asList(paidOnlyTCIDateType,Indicator.垫付单交强车数.订单城市ID,onlyTCIClassifyCodeCol,
						onlyTCIClassifyNameCol), onlyPaidTCICarNumWhereCol);
		List<Map<String, Object>> jsonOnlyPaidTCICarNumList = JsonHelper2.fromJson(jsonOnlyPaidTCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> onlyPaidTCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonOnlyPaidTCICarNumList, onlyPaidTCICarNumMap,
				citySet, classifyCodeSet, classifyNameSet, ordCityStr, classifyCodeStr, classifyNameStr, countStr);

		// 7、总成单车数（垫付完成），含商业险的订单 （月累计） 
		Map<WhereCol, Object> overallPaidVCICarWhereCol = new HashMap<WhereCol, Object>();
		overallPaidVCICarWhereCol.put(Indicator.垫付商业险车数.where开始时间, startDateStr);
		overallPaidVCICarWhereCol.put(Indicator.垫付商业险车数.where结束时间, nowDateStr);
		String jsoOverallPaidVCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付商业险车数, 
				Arrays.asList(paidVCIDateType,Indicator.垫付商业险车数.订单城市ID,ordClassifyCodeCol,
						ordClassifyNameCol), overallPaidVCICarWhereCol);
		List<Map<String, Object>> jsonOverallPaidVCICarNumList = JsonHelper2.fromJson(jsoOverallPaidVCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> overallOverallPaidVCICarNum = new HashMap<String, Long>();
		listToMap(subKey, jsonOverallPaidVCICarNumList, overallOverallPaidVCICarNum,
				citySet, classifyCodeSet, classifyNameSet, ordCityStr, classifyCodeStr, classifyNameStr, countStr);		
		
		
		for (Iterator<Long> city = citySet.iterator(); city.hasNext();) {
			// 先循环城市维度
			Integer cityId = city.next().intValue();
			for (Iterator<Long> classifyCode = classifyCodeSet.iterator(); classifyCode.hasNext();) {
				// 再循环分类的id，分别为拓展员id、渠道id或者保司id
				Integer classifyCodeId = classifyCode.next().intValue();
				for (Iterator<String> classifyName = classifyNameSet.iterator(); classifyName.hasNext();) {
					// 最后循环分类的名称，分别为拓展员名称、渠道名称或者保司名称
					String classifyNameActual = classifyName.next();
					String mapKeyAchieve = subKey;
					DataRegionalClassifyAchieve dataRegionalClassifyAchieve = new DataRegionalClassifyAchieve();
					dataRegionalClassifyAchieve.setStatisDate(Integer.parseInt(subKey));
					dataRegionalClassifyAchieve.setCityId(cityId);
					dataRegionalClassifyAchieve.setClassifyType(classifyType);
					mapKeyAchieve += cityId.toString();
					
					dataRegionalClassifyAchieve.setClassifyCode(classifyCodeId);
					mapKeyAchieve += classifyCodeId.toString();
					
					dataRegionalClassifyAchieve.setClassifyName(classifyNameActual);
					mapKeyAchieve += classifyNameActual;
				
					dataRegionalClassifyAchieve.setJgCalcCar(jgCalcCarMap.isEmpty() || !jgCalcCarMap.containsKey(mapKeyAchieve) ? 0 : jgCalcCarMap.get(mapKeyAchieve)); // 经管询价车数，有在经管询过价的车数
					dataRegionalClassifyAchieve.setYydCalcCar(yydCalcCarMap.isEmpty() || !yydCalcCarMap.containsKey(mapKeyAchieve) ? 0 : yydCalcCarMap.get(mapKeyAchieve)); // 又一单询价车数
					dataRegionalClassifyAchieve.setOverallCalcCar(overallCalcCarNum.isEmpty() || !overallCalcCarNum.containsKey(mapKeyAchieve) ? 0 : overallCalcCarNum.get(mapKeyAchieve)); // 总询价车数
					dataRegionalClassifyAchieve.setJgPaidVCICar(jgPaidVCICarNumMap.isEmpty() || !jgPaidVCICarNumMap.containsKey(mapKeyAchieve) ? 0 : jgPaidVCICarNumMap.get(mapKeyAchieve)); // 经管成单车数，含商业险的订单(垫付完成)
					dataRegionalClassifyAchieve.setYydPaidVCICar(yydPaidVCICarNumMap.isEmpty() || !yydPaidVCICarNumMap.containsKey(mapKeyAchieve) ? 0 : yydPaidVCICarNumMap.get(mapKeyAchieve)); // 又一单成单车数，含商业险的订单(垫付完成)
					dataRegionalClassifyAchieve.setOnlyPaidTCICar(onlyPaidTCICarNumMap.isEmpty() || !onlyPaidTCICarNumMap.containsKey(mapKeyAchieve) ? 0 : onlyPaidTCICarNumMap.get(mapKeyAchieve)); // 单交强成单车数
					dataRegionalClassifyAchieve.setOverallPaidVCICar(overallOverallPaidVCICarNum.isEmpty() || !overallOverallPaidVCICarNum.containsKey(mapKeyAchieve) ? 0 : overallOverallPaidVCICarNum.get(mapKeyAchieve)); // 总成单车数，含商业险的订单(垫付完成)
					if (dataRegionalClassifyAchieve.getJgCalcCar() != 0 || dataRegionalClassifyAchieve.getYydCalcCar() != 0 
						|| dataRegionalClassifyAchieve.getOverallCalcCar() != 0 || dataRegionalClassifyAchieve.getJgPaidVCICar() != 0 
						|| dataRegionalClassifyAchieve.getYydPaidVCICar() != 0 || dataRegionalClassifyAchieve.getOnlyPaidTCICar() != 0 
						|| dataRegionalClassifyAchieve.getOverallPaidVCICar() != 0) {
						dataRegionalClassifyAchievePersistService.saveOrUpdate(dataRegionalClassifyAchieve);
					}
					
				}
			
			}
			
		}
	}

	/**
	 *  可查月 周 日 等每种周期开始结束日期内的询价成单数 有开始结束日期确定查询的数据时间范围  有subKey确定查询的数据返回类型
	 * @param nowDateStr 查询的结束日期
	 * @param startDateStr 查询的开始日期
	 * @param subKey 组成map的key的前部分子串
	 * @param queryMonth 查询的月份
	 * @param queryWeek 查询的周
	 * @param groupByDateType 按时间分组的类型 月 日 周 等
	 * @param calcCityStr 算价城市字段名
	 * @param ordCityStr 订单城市字段名
	 * @param countStr	返回count数的字段名
	 * @param cityList 开城了的城市
	 * @throws SrvException
	 */
	private void persistDataRegionalCalcPaid(String nowDateStr, String startDateStr, String subKey, String queryMonth,
			String queryWeek, String groupByDateType, String calcCityStr, String ordCityStr, String countStr) throws SrvException {
		
		GroupCol calcDateType = null;
		GroupCol submitOrdDateType = null;
		GroupCol paidVCIDateType = null;
		GroupCol paidOnlyTCIDateType = null;
		GroupCol paidOrdDateType = null;
		if("month".equals(groupByDateType)) {
			calcDateType = Indicator.询价车辆数.月;
			submitOrdDateType = Indicator.提交商业险订单车数.月;
			paidVCIDateType = Indicator.垫付商业险车数.月;
			paidOnlyTCIDateType = Indicator.垫付单交强车数.月;
			paidOrdDateType = Indicator.垫付订单保单保费金额.月;
		} else if("week".equals(groupByDateType)) {
			calcDateType = Indicator.询价车辆数.周;
			submitOrdDateType = Indicator.提交商业险订单车数.周;
			paidVCIDateType = Indicator.垫付商业险车数.周;
			paidOnlyTCIDateType = Indicator.垫付单交强车数.周;
			paidOrdDateType = Indicator.垫付订单保单保费金额.周;
		} else if("day".equals(groupByDateType)) {
			calcDateType = Indicator.询价车辆数.日;
			submitOrdDateType = Indicator.提交商业险订单车数.日;
			paidVCIDateType = Indicator.垫付商业险车数.日;
			paidOnlyTCIDateType = Indicator.垫付单交强车数.日;
			paidOrdDateType = Indicator.垫付订单保单保费金额.日;
		}
		
		Set<Long> citySet =new HashSet<Long>();
		
		// 1、经管询价车数 （月累计）  城市的询价车数  	算价的系统来源: 又一单：1，经纪人：2
		Map<WhereCol, Object> jgCalcCarWhereCol = new HashMap<WhereCol, Object>();
		jgCalcCarWhereCol.put(Indicator.询价车辆数.where开始时间, startDateStr);
		jgCalcCarWhereCol.put(Indicator.询价车辆数.where结束时间, nowDateStr);
		jgCalcCarWhereCol.put(Indicator.询价车辆数.where询价系统来源, 2);
		String jsonJGCalcCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.询价车辆数, 
				Arrays.asList(calcDateType,Indicator.询价车辆数.询价城市ID),jgCalcCarWhereCol);
		List<Map<String, Object>> jsonJGCalcCarNumList = JsonHelper2.fromJson(jsonJGCalcCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> jgCalcCarMap = new HashMap<String, Long>();
		listToMap(subKey, jsonJGCalcCarNumList, jgCalcCarMap, citySet, calcCityStr, countStr);
		
		// 2、又一单询价车数（月累计），有在又一单询价的  ，本月内car_id去重。
		Map<WhereCol, Object> yydCalcCarWhereCol = new HashMap<WhereCol, Object>();
		yydCalcCarWhereCol.put(Indicator.询价车辆数.where开始时间, startDateStr);
		yydCalcCarWhereCol.put(Indicator.询价车辆数.where结束时间, nowDateStr);
		yydCalcCarWhereCol.put(Indicator.询价车辆数.where询价系统来源, 1);
		String jsonYYDCalcCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.询价车辆数, 
				Arrays.asList(calcDateType,Indicator.询价车辆数.询价城市ID),
				yydCalcCarWhereCol);
		List<Map<String, Object>> jsonYYDCalcCarNumList = JsonHelper2.fromJson(jsonYYDCalcCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydCalcCarMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDCalcCarNumList, yydCalcCarMap, citySet, calcCityStr, countStr); 
		
		// 3、月累计总询价车数
		Map<WhereCol, Object> overallCalcCarWhereCol = new HashMap<WhereCol, Object>();
		overallCalcCarWhereCol.put(Indicator.询价车辆数.where开始时间, startDateStr);
		overallCalcCarWhereCol.put(Indicator.询价车辆数.where结束时间, nowDateStr);
		String jsonOverallCalcCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.询价车辆数, 
				Arrays.asList(calcDateType,Indicator.询价车辆数.询价城市ID), overallCalcCarWhereCol);
		List<Map<String, Object>> jsonOverallCalcCarNumList = JsonHelper2.fromJson(jsonOverallCalcCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> overallMonthCalcCarNum = new HashMap<String, Long>();
		listToMap(subKey, jsonOverallCalcCarNumList, overallMonthCalcCarNum, citySet, calcCityStr, countStr);
		
		// 4、经管提交订单车数 （月累计）   城市的提交含商业险的订单的车数   订单来源：300:经纪人；400:又一单
		Map<WhereCol, Object> jgNewVCICarNumWhereCol = new HashMap<WhereCol, Object>();
		jgNewVCICarNumWhereCol.put(Indicator.提交商业险订单车数.where开始时间, startDateStr);
		jgNewVCICarNumWhereCol.put(Indicator.提交商业险订单车数.where结束时间, nowDateStr);
		jgNewVCICarNumWhereCol.put(Indicator.提交商业险订单车数.where订单来源, 300);
		String jsonJGNewVCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.提交商业险订单车数, 
				Arrays.asList(submitOrdDateType,Indicator.提交商业险订单车数.订单城市ID), 
				jgNewVCICarNumWhereCol);
		List<Map<String, Object>> jsonJGNewVCICarNumList = JsonHelper2.fromJson(jsonJGNewVCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> jgNewVCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonJGNewVCICarNumList, jgNewVCICarNumMap, citySet, ordCityStr, countStr); 

		// 5、又一单提交订单车数，在又一单提交的，含商业险的订单 （月累计） 
		Map<WhereCol, Object> yydNewVCICarNumWhereCol = new HashMap<WhereCol, Object>();
		yydNewVCICarNumWhereCol.put(Indicator.提交商业险订单车数.where开始时间, startDateStr);
		yydNewVCICarNumWhereCol.put(Indicator.提交商业险订单车数.where结束时间, nowDateStr);
		yydNewVCICarNumWhereCol.put(Indicator.提交商业险订单车数.where订单来源, 400);
		String jsonYYDNewVCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.提交商业险订单车数, 
				Arrays.asList(submitOrdDateType,Indicator.提交商业险订单车数.订单城市ID), 
				yydNewVCICarNumWhereCol);
		List<Map<String, Object>> jsonYYDNewVCICarNumList = JsonHelper2.fromJson(jsonYYDNewVCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydNewVCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDNewVCICarNumList, yydNewVCICarNumMap, citySet, ordCityStr, countStr); 

		// 6、总提交订单车数，含商业险的订单 （月累计） 
		Map<WhereCol, Object> overallNewVCICarNumWhereCol = new HashMap<WhereCol, Object>();
		overallNewVCICarNumWhereCol.put(Indicator.提交商业险订单车数.where开始时间, startDateStr);
		overallNewVCICarNumWhereCol.put(Indicator.提交商业险订单车数.where结束时间, nowDateStr);
		String jsonOverallNewVCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.提交商业险订单车数, 
				Arrays.asList(submitOrdDateType,Indicator.提交商业险订单车数.订单城市ID), 
				overallNewVCICarNumWhereCol);
		List<Map<String, Object>> jsonOverallNewVCICarNumList = JsonHelper2.fromJson(jsonOverallNewVCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> overallNewVCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonOverallNewVCICarNumList, overallNewVCICarNumMap, citySet, ordCityStr, countStr); 
		
		// 7、经管成单车数，（月累计）   城市的垫付完成的含商业险的订单的车数   订单来源：300:经纪人；400:又一单
		Map<WhereCol, Object> jgPaidVCICarNumWhereCol = new HashMap<WhereCol, Object>();
		jgPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where开始时间, startDateStr);
		jgPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where结束时间, nowDateStr);
		jgPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where订单来源, 300);
		String jsonJGPaidVCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付商业险车数, 
				Arrays.asList(paidVCIDateType,Indicator.垫付商业险车数.订单城市ID), 
				jgPaidVCICarNumWhereCol);
		List<Map<String, Object>> jsonJGPaidVCICarNumList = JsonHelper2.fromJson(jsonJGPaidVCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> jgPaidVCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonJGPaidVCICarNumList, jgPaidVCICarNumMap, citySet, ordCityStr, countStr); 

		// 8、又一单成单车数，含商业险的订单(垫付完成)（月累计） 
		Map<WhereCol, Object> yydPaidVCICarNumWhereCol = new HashMap<WhereCol, Object>();
		yydPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where开始时间, startDateStr);
		yydPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where结束时间, nowDateStr);
		yydPaidVCICarNumWhereCol.put(Indicator.垫付商业险车数.where订单来源, 400);
		String jsonYYDPaidVCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付商业险车数, 
				Arrays.asList(paidVCIDateType,Indicator.垫付商业险车数.订单城市ID), 
				yydPaidVCICarNumWhereCol);
		List<Map<String, Object>> jsonYYDPaidVCICarNumList = JsonHelper2.fromJson(jsonYYDPaidVCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydPaidVCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDPaidVCICarNumList, yydPaidVCICarNumMap, citySet, ordCityStr, countStr); 

		// 9、单交强成单车数(垫付完成)（月累计） 
		Map<WhereCol, Object> paidOnlyTCICarNumWhereCol = new HashMap<WhereCol, Object>();
		paidOnlyTCICarNumWhereCol.put(Indicator.垫付单交强车数.where开始时间, startDateStr);
		paidOnlyTCICarNumWhereCol.put(Indicator.垫付单交强车数.where结束时间, nowDateStr);
		String jsonPaidOnlyTCICarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付单交强车数, 
				Arrays.asList(paidOnlyTCIDateType,Indicator.垫付单交强车数.订单城市ID), 
				paidOnlyTCICarNumWhereCol);
		List<Map<String, Object>> jsonPaidOnlyTCICarNumList = JsonHelper2.fromJson(jsonPaidOnlyTCICarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> paidOnlyTCICarNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonPaidOnlyTCICarNumList, paidOnlyTCICarNumMap, citySet, ordCityStr, countStr); 

		// 10、总成单车数（垫付完成），含商业险的订单 （月累计） 
		Map<WhereCol, Object> paidBusiCarWhereCol = new HashMap<WhereCol, Object>();
		paidBusiCarWhereCol.put(Indicator.垫付商业险车数.where开始时间, startDateStr);
		paidBusiCarWhereCol.put(Indicator.垫付商业险车数.where结束时间, nowDateStr);
		String jsoPaidBusiCarNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付商业险车数, 
				Arrays.asList(paidVCIDateType,Indicator.垫付商业险车数.订单城市ID), paidBusiCarWhereCol);
		List<Map<String, Object>> jsonPaidBusiCarNumList = JsonHelper2.fromJson(jsoPaidBusiCarNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> overallPaidBusiCarNum = new HashMap<String, Long>();
		listToMap(subKey, jsonPaidBusiCarNumList, overallPaidBusiCarNum, citySet, ordCityStr, countStr);
			
		// 11、当月累计订单数量 当月累计保单保费(垫付完成),精确到分
		Map<WhereCol, Object> paidPremiumWhereCol = new HashMap<WhereCol, Object>();
		paidPremiumWhereCol.put(Indicator.垫付订单保单保费金额.where开始时间, startDateStr);
		paidPremiumWhereCol.put(Indicator.垫付订单保单保费金额.where结束时间, nowDateStr);
		String jsonPaidPremium = indicatorQuerySrv.indicatorNormalQuery(Indicator.垫付订单保单保费金额, 
				Arrays.asList(paidOrdDateType,Indicator.垫付订单保单保费金额.订单城市ID), paidPremiumWhereCol);
		List<Map<String, Object>> jsonPaidPremiumList = JsonHelper2.fromJson(jsonPaidPremium, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> paidPremiumMap = new HashMap<String, Long>();
		Map<String,Long> paidOrderNumMap = new HashMap<String, Long>();
		for (Map<String, Object> m : jsonPaidPremiumList){
			String dateCityKey = subKey;
			Long countValue = 0l;
			Long premiumValue = 0l;
			for (Map.Entry<String, Object> entry : m.entrySet()) {
				String a = entry.getKey();
				Object b = entry.getValue();
				 if (b instanceof Double) {
					if(b.toString().indexOf("E")>0){
						BigDecimal bd = new BigDecimal(b.toString());  
						String str = bd.toPlainString();
						b = Long.parseLong(str);
					} else {
						BigDecimal bd = new BigDecimal(b.toString());  
						b = bd.longValue();
					}
				}
				if (a.equals("d_order_city")){
					citySet.add((Long) b);
					dateCityKey += b.toString();
				} else if (a.equals("count")){
					countValue = (Long) b;
				} else if (a.equals("premium")){
					premiumValue = (Long) b;
				}
			}
			paidPremiumMap.put(dateCityKey, premiumValue);
			paidOrderNumMap.put(dateCityKey, countValue);
		}
		
		
		for (Iterator<Long> city = citySet.iterator(); city.hasNext();  ) {
			String mapKey2 = subKey;
			Integer cityId = city.next().intValue();
			
			DataRegionalCalcPaid dataRegionalCalcPaid = new DataRegionalCalcPaid();
			dataRegionalCalcPaid.setStatisDate(Integer.parseInt(nowDateStr));
			dataRegionalCalcPaid.setStatisDateMonth(Integer.parseInt(queryMonth));
			dataRegionalCalcPaid.setStatisDateWeek(Integer.parseInt(queryWeek));
			dataRegionalCalcPaid.setCityId(cityId);
			mapKey2 += cityId.toString();
			
			dataRegionalCalcPaid.setJgCalcCarNum(jgCalcCarMap.isEmpty() || !jgCalcCarMap.containsKey(mapKey2) ? 0 : jgCalcCarMap.get(mapKey2)); // 经管询价车数，有在经管询价的
			dataRegionalCalcPaid.setYydCalcCarNum(yydCalcCarMap.isEmpty() || !yydCalcCarMap.containsKey(mapKey2) ? 0 : yydCalcCarMap.get(mapKey2)); // 又一单询价车数，有在又一单询价的
			dataRegionalCalcPaid.setOverallCalcCarNum(overallMonthCalcCarNum.isEmpty() || !overallMonthCalcCarNum.containsKey(mapKey2) ? 0 : overallMonthCalcCarNum.get(mapKey2)); // 总询价车数，询过价的
			dataRegionalCalcPaid.setJgSubmitCarNum(jgNewVCICarNumMap.isEmpty() || !jgNewVCICarNumMap.containsKey(mapKey2) ? 0 : jgNewVCICarNumMap.get(mapKey2)); // 经管提交订单车数，在经管提交的，含商业险的订单
			dataRegionalCalcPaid.setYydSubmitCarNum(yydNewVCICarNumMap.isEmpty() || !yydNewVCICarNumMap.containsKey(mapKey2) ? 0 : yydNewVCICarNumMap.get(mapKey2)); // 又一单提交订单车数，在又一单提交的，含商业险的订单
			dataRegionalCalcPaid.setOverallSubmitCarNum(overallNewVCICarNumMap.isEmpty() || !overallNewVCICarNumMap.containsKey(mapKey2) ? 0 : overallNewVCICarNumMap.get(mapKey2)); // 总提交订单车数，含商业险的订单
			dataRegionalCalcPaid.setJgPaidCarNum(jgPaidVCICarNumMap.isEmpty() || !jgPaidVCICarNumMap.containsKey(mapKey2) ? 0 : jgPaidVCICarNumMap.get(mapKey2)); // 经管成单车数，含商业险的订单
			dataRegionalCalcPaid.setYydPaidCarNum(yydPaidVCICarNumMap.isEmpty() || !yydPaidVCICarNumMap.containsKey(mapKey2) ? 0 : yydPaidVCICarNumMap.get(mapKey2)); // 又一单成单车数，含商业险的订单
			dataRegionalCalcPaid.setOnlyTCIPaidCarNum(paidOnlyTCICarNumMap.isEmpty() || !paidOnlyTCICarNumMap.containsKey(mapKey2) ? 0 : paidOnlyTCICarNumMap.get(mapKey2)); // 单交强成单车数
			dataRegionalCalcPaid.setOverallPaidCarNum(overallPaidBusiCarNum.isEmpty() || !overallPaidBusiCarNum.containsKey(mapKey2) ? 0 : overallPaidBusiCarNum.get(mapKey2)); // 总成单车数，含商业险的订单
			dataRegionalCalcPaid.setMonthPaidOrderNum(paidOrderNumMap.isEmpty() || !paidOrderNumMap.containsKey(mapKey2) ? 0 : paidOrderNumMap.get(mapKey2)); // 当月累计订单数量
			dataRegionalCalcPaid.setMonthPaidPremium(paidPremiumMap.isEmpty() || !paidPremiumMap.containsKey(mapKey2) ? 0 : paidPremiumMap.get(mapKey2)); // 当月累计保单保费 
			if (dataRegionalCalcPaid.getJgCalcCarNum() !=0 || dataRegionalCalcPaid.getYydCalcCarNum() != 0
					|| dataRegionalCalcPaid.getOverallCalcCarNum() != 0 || dataRegionalCalcPaid.getJgSubmitCarNum() != 0
					|| dataRegionalCalcPaid.getYydSubmitCarNum() != 0 || dataRegionalCalcPaid.getOverallSubmitCarNum() != 0
					|| dataRegionalCalcPaid.getJgPaidCarNum() != 0 || dataRegionalCalcPaid.getYydPaidCarNum() != 0
					|| dataRegionalCalcPaid.getOnlyTCIPaidCarNum() != 0 || dataRegionalCalcPaid.getOverallPaidCarNum() != 0
					|| dataRegionalCalcPaid.getMonthPaidOrderNum() != 0 || dataRegionalCalcPaid.getMonthPaidPremium() != 0) {
				dataRegionalCalcPaidPersistService.saveOrUpdate(dataRegionalCalcPaid);
			}
			
		}
	}
	
	/**
	 *  可查月 周 日 等每种周期开始结束日期内的活跃增长数据 有开始结束日期确定查询的数据时间范围  有subKey确定查询的数据返回类型
	 * @param nowDateStr 查询的结束日期
	 * @param startDateStr 查询的开始日期
	 * @param subKey 组成map的key的前部分子串
	 * @param queryMonth 查询的月份
	 * @param queryWeek 查询的周
	 * @param groupByDateType 按时间分组的类型 月 日 周 等
	 * @param calcCityStr 算价城市字段名
	 * @param ordCityStr 订单城市字段名
	 * @param countStr	返回count数的字段名
	 * @param cityList 开城了的城市
	 * @throws SrvException
	 */
	private void persistDataRegionalAcitveAdd(String nowDateStr, String startDateStr, String subKey, String queryMonth,
			String queryWeek, String groupByDateType, String beeCityStr, String countStr) throws SrvException {
		
		GroupCol calcActiveDateType = null;
		GroupCol ordActiveDateType = null;
		GroupCol newBeeDateType = null; 
		GroupCol firstLoginDateType = null; 
		if("month".equals(groupByDateType)) {
			calcActiveDateType = Indicator.小蜜蜂活跃数.月;
			ordActiveDateType = Indicator.小蜜蜂出单活跃数.月;
			newBeeDateType = Indicator.小蜜蜂新增数.月; 
			firstLoginDateType = Indicator.又一单首登小蜜蜂数.月;
		} else if("week".equals(groupByDateType)) {
			calcActiveDateType = Indicator.小蜜蜂活跃数.周;
			ordActiveDateType = Indicator.小蜜蜂出单活跃数.周;
			newBeeDateType = Indicator.小蜜蜂新增数.周; 
			firstLoginDateType = Indicator.又一单首登小蜜蜂数.周;
		} else if("day".equals(groupByDateType)) {
			calcActiveDateType = Indicator.小蜜蜂活跃数.日;
			ordActiveDateType = Indicator.小蜜蜂出单活跃数.日;
			newBeeDateType = Indicator.小蜜蜂新增数.日; 
			firstLoginDateType = Indicator.又一单首登小蜜蜂数.日;
		}

		Set<Long> citySet =new HashSet<Long>();
		
		// 1、经管报价活跃，有在经管询过价的小蜜蜂数量   算价的系统来源: 又一单：1，经纪人：2
		Map<WhereCol, Object> jgCalcActiveWhereCol = new HashMap<WhereCol, Object>();
		jgCalcActiveWhereCol.put(Indicator.小蜜蜂活跃数.where开始时间, startDateStr);
		jgCalcActiveWhereCol.put(Indicator.小蜜蜂活跃数.where结束时间, nowDateStr);
		jgCalcActiveWhereCol.put(Indicator.小蜜蜂活跃数.where询价系统来源, 2);
		String jsonJGCalcActiveNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂活跃数, 
				Arrays.asList(calcActiveDateType,Indicator.小蜜蜂活跃数.城市ID),jgCalcActiveWhereCol);
		List<Map<String, Object>> jsonJGCalcActiveNumList = JsonHelper2.fromJson(jsonJGCalcActiveNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> jgCalcActiveMap = new HashMap<String, Long>();
		listToMap(subKey, jsonJGCalcActiveNumList, jgCalcActiveMap, citySet, beeCityStr, countStr);
		
		// 2、又一单报价活跃，有在又一单询过价的小蜜蜂数量。
		Map<WhereCol, Object> yydCalcActiveWhereCol = new HashMap<WhereCol, Object>();
		yydCalcActiveWhereCol.put(Indicator.小蜜蜂活跃数.where开始时间, startDateStr);
		yydCalcActiveWhereCol.put(Indicator.小蜜蜂活跃数.where结束时间, nowDateStr);
		yydCalcActiveWhereCol.put(Indicator.小蜜蜂活跃数.where询价系统来源, 1);
		String jsonYYDCalcActiveNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂活跃数, 
				Arrays.asList(calcActiveDateType,Indicator.小蜜蜂活跃数.城市ID),
				yydCalcActiveWhereCol);
		List<Map<String, Object>> jsonYYDCalcActiveNumList = JsonHelper2.fromJson(jsonYYDCalcActiveNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydCalcActiveMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDCalcActiveNumList, yydCalcActiveMap, citySet, beeCityStr, countStr); 
		
		// 3、总报价活跃，有询过价的小蜜蜂数量
		Map<WhereCol, Object> overallCalcActiveWhereCol = new HashMap<WhereCol, Object>();
		overallCalcActiveWhereCol.put(Indicator.小蜜蜂活跃数.where开始时间, startDateStr);
		overallCalcActiveWhereCol.put(Indicator.小蜜蜂活跃数.where结束时间, nowDateStr);
		String jsonOverallCalcActiveNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂活跃数, 
				Arrays.asList(calcActiveDateType,Indicator.小蜜蜂活跃数.城市ID), overallCalcActiveWhereCol);
		List<Map<String, Object>> jsonOverallCalcActiveNumList = JsonHelper2.fromJson(jsonOverallCalcActiveNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> overallCalcActiveNum = new HashMap<String, Long>();
		listToMap(subKey, jsonOverallCalcActiveNumList, overallCalcActiveNum, citySet, beeCityStr, countStr);
		
		// 4、经管出单活跃，有在经管出过单（以垫付订单为准，下同）的小蜜蜂数量 订单来源：300:经纪人；400:又一单
		Map<WhereCol, Object> jgOrderActiveNumWhereCol = new HashMap<WhereCol, Object>();
		jgOrderActiveNumWhereCol.put(Indicator.小蜜蜂出单活跃数.where开始时间, startDateStr);
		jgOrderActiveNumWhereCol.put(Indicator.小蜜蜂出单活跃数.where结束时间, nowDateStr);
		jgOrderActiveNumWhereCol.put(Indicator.小蜜蜂出单活跃数.where出单系统来源, 300);
		String jsonJGOrderActiveNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂出单活跃数, 
				Arrays.asList(ordActiveDateType,Indicator.小蜜蜂出单活跃数.城市ID), 
				jgOrderActiveNumWhereCol);
		List<Map<String, Object>> jsonJGOrderActiveNumList = JsonHelper2.fromJson(jsonJGOrderActiveNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> jgOrderActiveNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonJGOrderActiveNumList, jgOrderActiveNumMap, citySet, beeCityStr, countStr); 

		// 5、又一单出单活跃，在又一单出过单（以垫付订单为准，下同）的小蜜蜂数量
		Map<WhereCol, Object> yydOrderActiveNumWhereCol = new HashMap<WhereCol, Object>();
		yydOrderActiveNumWhereCol.put(Indicator.小蜜蜂出单活跃数.where开始时间, startDateStr);
		yydOrderActiveNumWhereCol.put(Indicator.小蜜蜂出单活跃数.where结束时间, nowDateStr);
		yydOrderActiveNumWhereCol.put(Indicator.小蜜蜂出单活跃数.where出单系统来源, 400);
		String jsonYYDOrderActiveNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂出单活跃数, 
				Arrays.asList(ordActiveDateType,Indicator.小蜜蜂出单活跃数.城市ID), 
				yydOrderActiveNumWhereCol);
		List<Map<String, Object>> jsonYYDOrderActiveNumList = JsonHelper2.fromJson(jsonYYDOrderActiveNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydOrderActiveNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDOrderActiveNumList, yydOrderActiveNumMap, citySet, beeCityStr, countStr); 

		// 6、总出单活跃，有出过单（以垫付为准，下同）的小蜜蜂数量
		Map<WhereCol, Object> overallOrderActiveNumWhereCol = new HashMap<WhereCol, Object>();
		overallOrderActiveNumWhereCol.put(Indicator.小蜜蜂出单活跃数.where开始时间, startDateStr);
		overallOrderActiveNumWhereCol.put(Indicator.小蜜蜂出单活跃数.where结束时间, nowDateStr);
		String jsonOverallOrderActiveNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂出单活跃数, 
				Arrays.asList(ordActiveDateType,Indicator.小蜜蜂出单活跃数.城市ID), 
				overallOrderActiveNumWhereCol);
		List<Map<String, Object>> jsonOverallOrderActiveNumList = JsonHelper2.fromJson(jsonOverallOrderActiveNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> overallOrderActiveNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonOverallOrderActiveNumList, overallOrderActiveNumMap, citySet, beeCityStr, countStr); 
		
		// 7、又一单首登小蜜蜂增长 增长指新增 又一单首登，首登日期在此时间范围内的
		Map<WhereCol, Object> yydFirstLoginNumWhereCol = new HashMap<WhereCol, Object>();
		yydFirstLoginNumWhereCol.put(Indicator.又一单首登小蜜蜂数.where开始时间, startDateStr);
		yydFirstLoginNumWhereCol.put(Indicator.又一单首登小蜜蜂数.where结束时间, nowDateStr);
		String jsonYYDFirstLoginNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.又一单首登小蜜蜂数, 
				Arrays.asList(firstLoginDateType,Indicator.又一单首登小蜜蜂数.城市ID), 
				yydFirstLoginNumWhereCol);
		List<Map<String, Object>> jsonYYDFirstLoginNumList = JsonHelper2.fromJson(jsonYYDFirstLoginNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydFirstLoginNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDFirstLoginNumList, yydFirstLoginNumMap, citySet, beeCityStr, countStr); 

		// 8、又一单注册小蜜蜂增长 注册日期是此时间范围内的  即小蜜蜂新增数  0:系统;  1:又一单;  2	:经纪人导入;  3:H5录入;
		Map<WhereCol, Object> yydRegisterBeeNumWhereCol = new HashMap<WhereCol, Object>();
		yydRegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where开始时间, startDateStr);
		yydRegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where结束时间, nowDateStr);
		yydRegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where注册系统来源, 1);
		String jsonYYDRegisterBeeNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂新增数, 
				Arrays.asList(newBeeDateType,Indicator.小蜜蜂新增数.城市ID), 
				yydRegisterBeeNumWhereCol);
		List<Map<String, Object>> jsonYYDRegisterBeeNumList = JsonHelper2.fromJson(jsonYYDRegisterBeeNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> yydRegisterBeeNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonYYDRegisterBeeNumList, yydRegisterBeeNumMap, citySet, beeCityStr, countStr); 

		// 9、经管注册小蜜蜂增长
		Map<WhereCol, Object> jgRegisterBeeNumWhereCol = new HashMap<WhereCol, Object>();
		jgRegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where开始时间, startDateStr);
		jgRegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where结束时间, nowDateStr);
		jgRegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where注册系统来源, 0);
		String jsonJGRegisterBeeNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂新增数, 
				Arrays.asList(newBeeDateType,Indicator.小蜜蜂新增数.城市ID), 
				jgRegisterBeeNumWhereCol);
		List<Map<String, Object>> jsonJGRegisterBeeNumList = JsonHelper2.fromJson(jsonJGRegisterBeeNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> jgRegisterBeeNumMap = new HashMap<String, Long>();
		listToMap(subKey, jsonJGRegisterBeeNumList, jgRegisterBeeNumMap, citySet, beeCityStr, countStr); 

		// 10、H5注册小蜜蜂增长
		Map<WhereCol, Object> h5RegisterBeeNumWhereCol = new HashMap<WhereCol, Object>();
		h5RegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where开始时间, startDateStr);
		h5RegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where结束时间, nowDateStr);
		h5RegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where注册系统来源, 3);
		String jsoH5RegisterBeeNum = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂新增数, 
				Arrays.asList(newBeeDateType,Indicator.小蜜蜂新增数.城市ID), h5RegisterBeeNumWhereCol);
		List<Map<String, Object>> jsonH5RegisterBeeNumList = JsonHelper2.fromJson(jsoH5RegisterBeeNum, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> h5RegisterBeeNum = new HashMap<String, Long>();
		listToMap(subKey, jsonH5RegisterBeeNumList, h5RegisterBeeNum, citySet, beeCityStr, countStr);
			
		// 11、批量导入注册小蜜蜂增长
		Map<WhereCol, Object> batchImportBeeNumWhereCol = new HashMap<WhereCol, Object>();
		batchImportBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where开始时间, startDateStr);
		batchImportBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where结束时间, nowDateStr);
		batchImportBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where注册系统来源, 2);
		String jsonBatchImportBee = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂新增数, 
				Arrays.asList(newBeeDateType,Indicator.小蜜蜂新增数.城市ID), batchImportBeeNumWhereCol);
		List<Map<String, Object>> jsonBatchImportBeeList = JsonHelper2.fromJson(jsonBatchImportBee, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> batchImportBeeMap = new HashMap<String, Long>(); 
		listToMap(subKey, jsonBatchImportBeeList, batchImportBeeMap, citySet, beeCityStr, countStr); 
		
		// 12、合计新增
		Map<WhereCol, Object> overallRegisterBeeNumWhereCol = new HashMap<WhereCol, Object>();
		overallRegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where开始时间, startDateStr);
		overallRegisterBeeNumWhereCol.put(Indicator.小蜜蜂新增数.where结束时间, nowDateStr);
		String jsonOverallRegisterBee = indicatorQuerySrv.indicatorNormalQuery(Indicator.小蜜蜂新增数, 
				Arrays.asList(newBeeDateType,Indicator.小蜜蜂新增数.城市ID), overallRegisterBeeNumWhereCol);
		List<Map<String, Object>> jsonOverallRegisterBeeList = JsonHelper2.fromJson(jsonOverallRegisterBee, new TypeToken<List<Map<String, Object>>>() {}.getType());
		Map<String,Long> overallRegisterBeeMap = new HashMap<String, Long>(); 
		listToMap(subKey, jsonOverallRegisterBeeList, overallRegisterBeeMap, citySet, beeCityStr, countStr); 
		
		for (Iterator<Long> city = citySet.iterator(); city.hasNext();) {
			String mapKey2 = subKey;
			Integer cityId = city.next().intValue();
			
			DataRegionalActiveAdd dataRegionalActiveAdd = new DataRegionalActiveAdd();
			dataRegionalActiveAdd.setStatisDate(Integer.parseInt(nowDateStr));
			dataRegionalActiveAdd.setStatisDateMonth(Integer.parseInt(queryMonth));
			dataRegionalActiveAdd.setStatisDateWeek(Integer.parseInt(queryWeek));
			dataRegionalActiveAdd.setCityId(cityId);
			mapKey2 += cityId.toString();
			dataRegionalActiveAdd.setJgCalcActive(jgCalcActiveMap.isEmpty() || !jgCalcActiveMap.containsKey(mapKey2) ? 0 : jgCalcActiveMap.get(mapKey2)); // 经管询价车数，有在经管询价的
			dataRegionalActiveAdd.setYydCalcActive(yydCalcActiveMap.isEmpty() || !yydCalcActiveMap.containsKey(mapKey2) ? 0 : yydCalcActiveMap.get(mapKey2)); // 又一单询价车数，有在又一单询价的
			dataRegionalActiveAdd.setOverallCalcActive(overallCalcActiveNum.isEmpty() || !overallCalcActiveNum.containsKey(mapKey2) ? 0 : overallCalcActiveNum.get(mapKey2)); // 总询价车数，询过价的
			dataRegionalActiveAdd.setJgOrderActive(jgOrderActiveNumMap.isEmpty() || !jgOrderActiveNumMap.containsKey(mapKey2) ? 0 : jgOrderActiveNumMap.get(mapKey2)); // 经管提交订单车数，在经管提交的，含商业险的订单
			dataRegionalActiveAdd.setYydOrderActive(yydOrderActiveNumMap.isEmpty() || !yydOrderActiveNumMap.containsKey(mapKey2) ? 0 : yydOrderActiveNumMap.get(mapKey2)); // 又一单提交订单车数，在又一单提交的，含商业险的订单
			dataRegionalActiveAdd.setOverallOrderActive(overallOrderActiveNumMap.isEmpty() || !overallOrderActiveNumMap.containsKey(mapKey2) ? 0 : overallOrderActiveNumMap.get(mapKey2)); // 总提交订单车数，含商业险的订单
			dataRegionalActiveAdd.setYydFirstLoginBee(yydFirstLoginNumMap.isEmpty() || !yydFirstLoginNumMap.containsKey(mapKey2) ? 0 : yydFirstLoginNumMap.get(mapKey2)); // 经管成单车数，含商业险的订单
			dataRegionalActiveAdd.setYydRegisterBee(yydRegisterBeeNumMap.isEmpty() || !yydRegisterBeeNumMap.containsKey(mapKey2) ? 0 : yydRegisterBeeNumMap.get(mapKey2)); // 又一单成单车数，含商业险的订单
			dataRegionalActiveAdd.setJgRegisterBee(jgRegisterBeeNumMap.isEmpty() || !jgRegisterBeeNumMap.containsKey(mapKey2) ? 0 : jgRegisterBeeNumMap.get(mapKey2)); // 单交强成单车数
			dataRegionalActiveAdd.setH5RegisterBee(h5RegisterBeeNum.isEmpty() || !h5RegisterBeeNum.containsKey(mapKey2) ? 0 : h5RegisterBeeNum.get(mapKey2)); // 总成单车数，含商业险的订单
			dataRegionalActiveAdd.setBatchImportBee(batchImportBeeMap.isEmpty() || !batchImportBeeMap.containsKey(mapKey2) ? 0 : batchImportBeeMap.get(mapKey2)); // 当月累计订单数量
			dataRegionalActiveAdd.setOverallRegisterBee(overallRegisterBeeMap.isEmpty() || !overallRegisterBeeMap.containsKey(mapKey2) ? 0 : overallRegisterBeeMap.get(mapKey2)); // 当月累计保单保费 
			if (dataRegionalActiveAdd.getJgCalcActive() != 0 || dataRegionalActiveAdd.getYydCalcActive() != 0
					|| dataRegionalActiveAdd.getOverallCalcActive() != 0 || dataRegionalActiveAdd.getJgOrderActive() != 0
					|| dataRegionalActiveAdd.getYydOrderActive() != 0 || dataRegionalActiveAdd.getOverallOrderActive() != 0
					|| dataRegionalActiveAdd.getYydFirstLoginBee() != 0 || dataRegionalActiveAdd.getYydRegisterBee() != 0
					|| dataRegionalActiveAdd.getJgRegisterBee() != 0 || dataRegionalActiveAdd.getH5RegisterBee() != 0
					|| dataRegionalActiveAdd.getBatchImportBee() != 0 || dataRegionalActiveAdd.getOverallRegisterBee() != 0) {
				dataRegionalActiveAddPersistService.saveOrUpdate(dataRegionalActiveAdd);
			}
		}
	}

	private void listToMap(String nowDateStr, List<Map<String, Object>> jsonList,
			Map<String, Long> map, Set<Long> citySet, String cityStr,String countStr) {
		for (Map<String, Object> m : jsonList){
			String dateCityKey = nowDateStr;
			Long value = 0l;
			for (Map.Entry<String, Object> entry : m.entrySet()) {
				String a = entry.getKey();
				Object b = entry.getValue();
				 if (b instanceof Double) {
					if(b.toString().indexOf("E")>0){
						BigDecimal bd = new BigDecimal(b.toString());  
						String str = bd.toPlainString();
						b = Long.parseLong(str);
					} else {
						BigDecimal bd = new BigDecimal(b.toString());  
						b = bd.longValue();
					}
				}
				if (a.equals(cityStr)){
					citySet.add((Long) b);
					dateCityKey += b.toString();
				} else if (a.equals(countStr)){
					value = (Long) b;
				}
			}
			map.put(dateCityKey, value);
		}
	}
	
	private void listToMap(String nowDateStr, List<Map<String, Object>> jsonList, Map<String, Long> map,
			Set<Long> citySet,Set<Long> classifyCodeSet,Set<String> classifyNameSet, String cityStr,String classifyCodeStr,String classifyNameStr, 
			String countStr) {
		for (Map<String, Object> m : jsonList){
			String  cityKey = "";
			String  classifyCodeKey = "";
			String classifyNameKey = "";
			Long value = 0l;
			for (Map.Entry<String, Object> entry : m.entrySet()) {
				String a = entry.getKey();
				Object b = entry.getValue();
				 if (b instanceof Double) {
					if(b.toString().indexOf("E")>0){
						BigDecimal bd = new BigDecimal(b.toString());  
						String str = bd.toPlainString();
						b = Long.parseLong(str);
					} else {
						BigDecimal bd = new BigDecimal(b.toString());  
						b = bd.longValue();
					}
				}
				if (a.equals(cityStr)){
					citySet.add((Long) b);
					cityKey += b.toString();
				} else if (a.equals(classifyCodeStr)){
					classifyCodeSet.add((Long) b);
					classifyCodeKey += b.toString();
				} else if (a.equals(classifyNameStr)){
					classifyNameSet.add(b.toString());
					classifyNameKey += b.toString();
				} else if (a.equals(countStr)){
					value = (Long) b;
				}
			}
			map.put(nowDateStr+cityKey+classifyCodeKey+classifyNameKey, value);
		}
	}
}
