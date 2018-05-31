package com.weyao.srv.task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dsf.platform.jobs.job.JobResult;
import com.google.gson.reflect.TypeToken;
import com.weyao.common.JsonHelper2;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.redis.RedisService;
import com.weyao.srv.dataproxy.DataProxySrv;

public class CountyStatusDataTask extends AbstractTimerTask {

	private static Logger logger = LoggerFactory.getLogger(CountyStatusDataTask.class);

	@Autowired
	private DataProxySrv  dataProxySrv;
	@Autowired
	private RedisService redisService;
	
	
	@Override
	public JobResult handleBiz(String[] arg0) {
		JobResult result = new JobResult();
		logger.info("........定时查询报价列表订单状态数据.....start.......");
		try {
			StringBuffer sql = new StringBuffer(" SELECT ti.`status`, count(ti.`status`) countNumber,  tb.county_id  countyId ");
						 sql.append(" FROM t_bee_calc_insurance tbc force index(index_name_create_date) ");
						 sql.append(" LEFT JOIN t_bee_calc_record tb ON tbc.calc_record_id = tb.calc_record_id  ");
						 sql.append(" LEFT JOIN t_insurance ti ON ti.insurance_id = IF ( ( tbc.business_insurance_id = 0 ), tbc.force_insurance_id, tbc.business_insurance_id) ");
						 sql.append(" WHERE tbc.`status` between 0 AND 4  ");
						 sql.append(" AND   tbc.create_time > date_add(NOW() , INTERVAL -3 MONTH)  ");
						 sql.append(" AND ti.`status` IN (20, 1, 2, 4, 5, 9, 10, 11, 12)  ");
						 sql.append(" GROUP BY tb.county_id ,ti.`status`   ");
			 List<Object> queryParams = new ArrayList<>();
			 
			 String resultData = dataProxySrv.excuteQuery(sql.toString(), ResultType.LIST, queryParams);
			 logger.info("............resultData="+resultData);
			 Type type = new TypeToken<ArrayList<Map<String, Integer>>>() {}.getType();  
			 List<Map<String, Integer>> retList = JsonHelper2.fromJson(resultData, type); 
			 
			 
			 StringBuffer confirmSql = new StringBuffer(" SELECT tb.county_id countyId, count(ti.`status`) countNumber ");
			 			  confirmSql.append(" FROM 	t_bee_calc_insurance tbc FORCE INDEX (index_name_create_date) ");
			 			  confirmSql.append(" LEFT JOIN t_bee_calc_record tb ON tbc.calc_record_id = tb.calc_record_id ");
			 			  confirmSql.append(" LEFT JOIN t_insurance ti ON ti.insurance_id = IF ( ( tbc.business_insurance_id = 0),tbc.force_insurance_id,tbc.business_insurance_id) ");
			 			  confirmSql.append(" WHERE tbc.`status` !=- 1 AND tbc.create_time > date_add(NOW(), INTERVAL - 3 MONTH) ");
			 			  confirmSql.append(" AND ti.`status` = 20 ");
			 			  confirmSql.append(" AND (date_add(tbc.remind_time,INTERVAL 30 MINUTE) < now() ) ");
			 			  confirmSql.append(" GROUP BY tb.county_id ");
			 
		 String confirmSqlData = dataProxySrv.excuteQuery(confirmSql.toString(), ResultType.LIST, queryParams);
		 logger.info("............resultData="+confirmSqlData);
		 Type typetwo = new TypeToken<ArrayList<Map<String, Integer>>>() {}.getType();  
		 List<Map<String, Integer>> confirmList = JsonHelper2.fromJson(confirmSqlData, typetwo); 
			 
			 
			 
			 
		 Map<String, String> countyMap = new HashMap<>();   //区域
		 Map<String, Integer> statusMap = new HashMap<>();    //总数
		 
			for (Map<String, Integer> map : retList) {
				String countyId = map.get("countyId")+"";
				String countNumber = map.get("countNumber")+"";
				String status = map.get("status")+"";
				String value = countyMap.get(countyId);
				if(StringUtils.isBlank(value)){
					logger.info("...inser Put  ....countyId="+countyId+".....countNumber="+countNumber+"......status="+status+"....value"+value);
					countyMap.put(countyId, "{ \""+status+"\":"+countNumber+",");
				}else{
					value = value +"\""+status+"\":"+countNumber+",";
					logger.info("...update put  ....countyId="+countyId+".....countNumber="+countNumber+"......status="+status+"....value"+value);
					countyMap.put(countyId,value);
				}
				
				Integer  number = statusMap.get(status);
				if(null == number ){
					statusMap.put(status, Integer.valueOf(countNumber));
				}else{
					statusMap.put(status, Integer.valueOf(countNumber)+ number);
				}
			}
			
			
			Integer countValue = 0;
			for (Map<String, Integer> map : confirmList) {
				String countyId = map.get("countyId")+"";
				String countNumber = map.get("countNumber")+"";
				String value = countyMap.get(countyId);
				String status = "99";   //没有此状态,  30分钟还是待确认状态 进行提醒 
				if(StringUtils.isBlank(value)){
					logger.info("...inser Put  ....countyId="+countyId+".....countNumber="+countNumber+"......status="+status+"....value"+value);
					countyMap.put(countyId, "{ \""+status+"\":"+countNumber+",");
				}else{
					value = value +"\""+status+"\":"+countNumber+",";
					logger.info("...update put  ....countyId="+countyId+".....countNumber="+countNumber+"......status="+status+"....value"+value);
					countyMap.put(countyId,value);
				}
				
				countValue += Integer.valueOf(countNumber);
			}
			statusMap.put("99", countValue);
			
			 logger.info(".......需要执行数据条数....."+countyMap.size()+".......");
			  for (String key : countyMap.keySet()) {
				  String statusStr = countyMap.get(key); 
				  statusStr = statusStr.substring(0,statusStr.length()-1)+"}";
				  logger.info(".......执行数据...countyId="+key+" statusStr ="+statusStr+".......");
				  
				  redisService.set(String.format("COUNTY_DATA_%s", key), statusStr);
		          //countyStatusDataMapper.updateCountyStatus(Integer.valueOf(key), statusStr);
		        }  
			  //区域ID 为0的 所有区域
			  redisService.set(String.format("COUNTY_DATA_%s", "0"), JsonHelper2.toJson(statusMap));
			result.setSendMail(true);
			result.setSuccess(true);
			logger.info(".......定时查询报价列表订单状态数据.....EDN.......");
		} catch (Exception e) {
			logger.error(".定时查询报价列表订单状态数据  失败!", e);
			e.printStackTrace();
		}
		return result;
	}
}
