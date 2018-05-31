package com.weyao.srv.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.rpc.RpcException;
import com.dsf.platform.jobs.job.JobResult;
import com.google.gson.reflect.TypeToken;
import com.weyao.common.JsonHelper2;
import com.weyao.common.SourceSystemConstants;
import com.weyao.common.WeyaoRpcContext;
import com.weyao.exception.SrvException;
import com.weyao.info.calc.CalcConstants;
import com.weyao.info.calc.CalcQueryParam;
import com.weyao.info.customer.Car;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.srv.calc.CalcSrv;
import com.weyao.srv.dataproxy.DataProxySrv;
import com.weyao.srv.util.DateUtil;


public class CarClaimsTask extends AbstractTimerTask{
	
	private static Logger logger = LoggerFactory.getLogger(CarClaimsTask.class);
	
	private static final int 上海 = 3;
	
	@Autowired
	private DataProxySrv dataProxySrv;
	@Autowired
	private CalcSrv calcSrv;
	
	@Override
	public JobResult handleBiz(String[] arg0) {
		JobResult result = new JobResult();
		try{
		result.setSendMail(true);
		logger.info("查询已成单车辆的理赔信息,开始------");
		Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        c.add(Calendar.YEAR, -1);
        Date y = c.getTime();
        String d = DateUtil.年月日.format(y);
        List<Object> list = new ArrayList<Object>();
        list.add(d);
		String sql = "SELECT DISTINCT b.car_id AS carId,b.plate_number AS plateNumber,b.vin AS vin,b.engine_no AS engineNo,b.car_owner AS carOwner,DATE_FORMAT(b.register_date, '%Y-%m-%d') AS registerDate,b.model_code AS modelCode FROM t_insurance a JOIN t_car b ON a.car_id = b.car_id WHERE a.status = 12 AND a.ptype = 200 AND a.insurance_start_date = ? AND a.city_id = 3 AND b.channel_id IN (1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011)"; 
		String jsonStr = dataProxySrv.excuteQuery(sql, ResultType.LIST, list);
		List<CarInfo> carIdList= JsonHelper2.fromJson(jsonStr, new TypeToken<List<CarInfo>>(){}.getType());
		int all = 0;
		if(CollectionUtils.isNotEmpty(carIdList)){
			all = carIdList.size();
			Car car = null;
			for(CarInfo carInfo : carIdList){
				car = new Car();
				car.setCarId(carInfo.getCarId());
				logger.info(String.format("查询已成单车辆的理赔信息，车辆信息=%s", JsonHelper2.toJson(carInfo)));
				String plateNumber = carInfo.getPlateNumber();
				car.setPlateNumber(plateNumber);
				CalcQueryParam param = new CalcQueryParam();   
				param.setInsuranceCompany(CalcConstants.CALC_INSURANCE_COMPANY_CPIC);
				param.setCityId(上海); 
				if(plateNumber.startsWith("沪")){
					param.setPlateNumber(plateNumber);
				}else{
					BeanUtils.copyProperties(carInfo, param);
				}
				try{
					WeyaoRpcContext.setSourceSystem(SourceSystemConstants.系统来源_定时任务平台);
					calcSrv.calcQuery(param);
				}catch(SrvException e){
					Thread.sleep(10*1000);
					//报错重试一次
					try{
						WeyaoRpcContext.setSourceSystem(SourceSystemConstants.系统来源_定时任务平台);
						calcSrv.calcQuery(param);
					}catch(SrvException ex){
						logger.error(String.format("调用算价服务查询已成单车辆的理赔信息，车辆信息=%s", JsonHelper2.toJson(carInfo)),e);
					}
				}catch(RpcException e){
					logger.error("算价查询结果异常",e);
				}
				Thread.sleep(10*1000);
			}
		}
		result.setMessage(String.format("查询%s已成单车辆的理赔信息：车辆总数=%d",d,all));
		logger.info("查询已成单车辆的理赔信息,结束------");
		result.setSuccess(true);
		}catch(Exception e){
			result.setSuccess(false);
			logger.error("查询已成单车辆的理赔信息", e);
		}
		return result;
	}
	class CarInfo{
		private long carId;
		private String plateNumber;
		private String vin;
		private String engineNo;
		private String carOwner;
		private String registerDate;
		private String modelCode;
		public long getCarId() {
			return carId;
		}
		public void setCarId(long carId) {
			this.carId = carId;
		}
		public String getPlateNumber() {
			return plateNumber;
		}
		public void setPlateNumber(String plateNumber) {
			this.plateNumber = plateNumber;
		}
		public String getVin() {
			return vin;
		}
		public void setVin(String vin) {
			this.vin = vin;
		}
		public String getEngineNo() {
			return engineNo;
		}
		public void setEngineNo(String engineNo) {
			this.engineNo = engineNo;
		}
		public String getCarOwner() {
			return carOwner;
		}
		public void setCarOwner(String carOwner) {
			this.carOwner = carOwner;
		}
		public String getRegisterDate() {
			return registerDate;
		}
		public void setRegisterDate(String registerDate) {
			this.registerDate = registerDate;
		}
		public String getModelCode() {
			return modelCode;
		}
		public void setModelCode(String modelCode) {
			this.modelCode = modelCode;
		}
		
	}
}
