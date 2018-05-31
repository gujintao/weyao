package com.weyao.srv.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.CollectionUtils;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import com.dianping.cat.Cat;
import com.dsf.platform.jobs.job.JobResult;
import com.google.gson.reflect.TypeToken;
import com.weyao.common.JsonHelper2;
import com.weyao.common.MainRK;
import com.weyao.exception.SrvException;
import com.weyao.info.crm.TWorkbenchConfig;
import com.weyao.info.crm.param.AppointmentEventParam;
import com.weyao.info.crm.param.CarBelongLog;
import com.weyao.info.customer.AssignCar;
import com.weyao.info.customer.Car;
import com.weyao.info.customer.CustomerCarExt;
import com.weyao.info.customer.SimpleCar;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.redis.RedisService;
import com.weyao.srv.crm.CrmConstants;
import com.weyao.srv.crm.CrmConstants.BelongLogTypeEnum;
import com.weyao.srv.crm.CrmSrv;
import com.weyao.srv.customer.CustomerSrv;
import com.weyao.srv.dataproxy.DataProxySrv;
import com.weyao.srv.util.DateUtil;

@Scope("singleton")
@DisconfFile(filename = "timmer.properties")
public class AutoAssignTask extends AbstractTimerTask {
	
	private static Logger logger = LoggerFactory.getLogger(AutoAssignTask.class);
	
	private static boolean autoAssignRun = true;
	
	@DisconfFileItem(name = "assign.task.run",associateField="autoAssignRun")
	public static boolean getAutoAssignRun() {
		return autoAssignRun;
	}

	public static void setAutoAssignRun(boolean autoAssignRun) {
		AutoAssignTask.autoAssignRun = autoAssignRun;
	}

	
	@Autowired
	private CustomerSrv customerSrv;

	@Autowired
	private CrmSrv crmSrv;

	@Autowired
	private RedisService redis; 
	
	@Autowired
	private DataProxySrv dataProxySrv;
    
	Random random = new Random();
	
    int totalNum;
    
	@Override
	public JobResult handleBiz(String[] args) {
		JobResult result = new JobResult();
		result.setSendMail(true);
		Cat.logEvent(com.weyao.cat.Constants.E_THREAD_START, "AutoAssignThread");

        logger.debug("自动分配----------------------开始-----------------------");

        String str; // redis字符串
        AssignCar car; // 解析成待分配车辆
        CustomerCarExt customerCarExt; // 实际车辆信息
        long carId; // 车辆ID
        String plateNumber; // 车牌号
        Date insuranceExpired; // 保险到期日
        int sellingDuration; // 可销售天数
        long actualSellingDuration; // 实际天数
        int isSelling; // 是否可销售 0-否，1-是，3-未知（无保险到期日）

        List<TWorkbenchConfig> configList; // 所有坐席配置
        List<TWorkbenchConfig> tempList; // 符合条件的坐席配置
        Map<Integer, WeightScope> weight; // 坐席权重MAP
        WeightScope weightScope; // 某个坐席的权重
        Map<Integer, Integer> cityConfigMap; // 城市是可销售天数配置MAP

        SimpleCar assignedCar; // 车牌号已分配的某辆车

        int randomNum; // 随机数
        try {
            while (autoAssignRun) {
                logger.debug("getting one data from redis start...");
                // 获取一条待分配任务，阻塞式，不限制阻塞时间
                str = redis.brpop(MainRK.CUSTOMER_AUTO_ASSIGN_QUEUE_KEY, 0, TimeUnit.SECONDS);
                logger.debug("getting one data from redis end... str = " + str);
                car = JsonHelper2.fromJson(str, AssignCar.class);
                // 防止出现redis队列阻塞导致手动分配先于自动分配的情况，需时时查询一次数据库
                customerCarExt = customerSrv.getCustomerCarByCarId(car.getCarId());
                if (customerCarExt.getBelongOpId() != null) {
                    logger.info(String.format("车辆【%s】已分配！", car.getCarId()));
                    continue;
                }
                // 获取所有可分配坐席配置
                configList = crmSrv.listWorkbenchConfig();
                // 获取城市可销售日期配置
                cityConfigMap = crmSrv.getCityConfigMap(CrmConstants.CITY_CONFIG_SELLING);
                if (CollectionUtils.isEmpty(configList) || CollectionUtils.isEmpty(cityConfigMap)) {
                    logger.error("Please set workbench config and city config!!!");
                    redis.lpush(MainRK.CUSTOMER_AUTO_ASSIGN_QUEUE_KEY, str);
                    autoAssignRun = false;
                }
                // 如果车牌号已分配给坐席，当前车也分配给那个坐席
                carId = car.getCarId();
                plateNumber = car.getPlateNumber();
                assignedCar = customerSrv.getAssignedCar(plateNumber);
                logger.debug("assignedCar = " + JsonHelper2.toJson(assignedCar));
                if (assignedCar != null ) {
                	// modify by zhaixingpeng 加个条件必须是网销渠道的
                	Car assignedCarInfo=customerSrv.getCarByCarId(assignedCar.getCarId());
					if (checkChanel(assignedCarInfo.getChannelId())) {
						customerSrv.setBelongOpId(carId, assignedCar.getBelongOpId());
						// 记录归属人流水
						addCarBelongLog(carId, assignedCar.getBelongOpId(), BelongLogTypeEnum.自动分配.getTypeId());
						crmSrv.autoAssignSuccess(car, assignedCar.getBelongOpId());
						continue;
					}
                   
                }
                // 判断该任务是否可销售
                insuranceExpired = car.getInsuranceExpired();
                if (!cityConfigMap.containsKey(car.getCityId())) {
                    logger.error(String.format("城市【%s】未配置可销售时间段，任务【%s】无法分配", car.getCityId(), carId));
                    redis.lpush(MainRK.CUSTOMER_AUTO_ASSIGN_QUEUE_KEY, str);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("Thread can't go into sleep!");
                    }
                    continue;
                }
                if (insuranceExpired != null) {
                    sellingDuration = cityConfigMap.get(car.getCityId());
                    actualSellingDuration = DateUtil.diffDay(DateUtil.getCurrentDate(), insuranceExpired);
                    if (0 <= actualSellingDuration && actualSellingDuration <= sellingDuration) {
                        isSelling = 1;
                    } else {
                        isSelling = 0;
                    }
                } else {
                    // 没有保险到期日，分配给"未知"
                    isSelling = 3;
                }
                logger.debug("isSelling = " + isSelling);
                tempList = new ArrayList<>();
                // 取出符合条件的坐席列表
                for (TWorkbenchConfig workbenchConfig: configList) {
                    if (("," + workbenchConfig.getCities() + ",").contains("," + car.getCityId() + ",")
                            && ("," + workbenchConfig.getSources() + ",").contains("," + car.getSourceBatchId())
                            && ("," + workbenchConfig.getIsSelling() + ",").contains("," + isSelling + ",")) {
                        tempList.add(workbenchConfig);
                    }
                }
                logger.debug("tempList = " + JsonHelper2.toJson(tempList));
                if (CollectionUtils.isEmpty(tempList)) {
                    logger.error(String.format("没有符合条件的坐席，carId = %s", carId));
                    redis.lpush(MainRK.CUSTOMER_AUTO_ASSIGN_QUEUE_KEY, str);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("Thread can't go into sleep!");
                    }
                    continue;
                }
                // 计算权重
                weight = generateWeightScope(tempList);
                logger.debug("weight = " + JsonHelper2.toJson(weight));
                // 按权重分配
                randomNum = random.nextInt(totalNum);
                logger.debug("totalNum = " + totalNum + "; randomNum = " + randomNum);
                for (TWorkbenchConfig workbenchConfig: tempList) {
                    weightScope = weight.get(workbenchConfig.getOperatorId());
                    if (weightScope.getMin() <= randomNum && randomNum <= weightScope.getMax()) {
                        customerSrv.setBelongOpId(carId, workbenchConfig.getOperatorId());
                        // 记录归属人流水
						addCarBelongLog(carId, workbenchConfig.getOperatorId(), BelongLogTypeEnum.自动分配.getTypeId());
                        int operatorId = workbenchConfig.getOperatorId();
                        generate3rdAppointment(customerCarExt, operatorId);
                        crmSrv.autoAssignSuccess(car, operatorId);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.error("Thread can't go into sleep!");
                }
            }
            result.setMessage("自动分配停止运行");
            logger.debug("自动分配停止运行");
        } catch (Exception e) {
        	result.setSuccess(false);
            logger.error("AutoAssignThread is shutting down: " + e.getMessage(), e);
        }
		return result;
	}
	private void generate3rdAppointment(CustomerCarExt customerCarExt, Integer operatorId) {

        try {
        	AppointmentEventParam event = new AppointmentEventParam();
            event.setCarId(customerCarExt.getCarId());
            event.setAppointmentTime(new Date());
            event.setBelongOpId(operatorId);
            event.setCreateOpId(operatorId);
            event.setCustomerName(customerCarExt.getCustomerName());
            event.setCustomerMobile(customerCarExt.getCustomerMobile());
            event.setPlateNumber(customerCarExt.getPlateNumber());
            event.setInsuranceExpired(customerCarExt.getInsuranceExpired());
            crmSrv.addAppointmentEvent(event, CrmConstants.EventTypeEnum.网销获客.getTypeId(), CrmConstants.CarTypeEnum.核心客户.getTypeId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Map<Integer, WeightScope> generateWeightScope(List<TWorkbenchConfig> configList) {

        Map<Integer, WeightScope> weight = new HashMap<>();

        totalNum = 0;
        for (TWorkbenchConfig config: configList) {
            WeightScope weightScope = new WeightScope(totalNum, totalNum + config.getTotalNumber() - 1);
            weight.put(config.getOperatorId(), weightScope);
            totalNum += config.getTotalNumber();
        }

        return weight;
    }
    
    
	private boolean checkChanel(long channelId) {
		String selectChannel = "select  channel_id channelId from t_channel where status=0 and channel_type=10";
		try {
			String channelResult = dataProxySrv.excuteQuery(selectChannel, ResultType.LIST, null);

			List<Map<String, Long>> channelIds = JsonHelper2.fromJson(channelResult,
					new TypeToken<List<Map<String, Long>>>() {
					}.getType());
			if (!CollectionUtils.isEmpty(channelIds)) {
				for (Map<String, Long> item : channelIds) {
					if (channelId == item.get("channelId"))
						return true;
				}

			}
		} catch (SrvException e) {
			logger.error("查询网销渠道异常!", e);
			return false;
		}
		return false;

	}
	
	private void addCarBelongLog( long carId, int newBelongOpId, int type) {
		CarBelongLog carBelongLog = new CarBelongLog();
		carBelongLog.setCarId(carId);
		carBelongLog.setNewBelongOpId(newBelongOpId);
		carBelongLog.setType(type);
		crmSrv.addCarBelongLog(carBelongLog);
	}
    
	class WeightScope {

        int min;

        int max;

        public WeightScope(int min, int max) {

            this.min = min;
            this.max = max;
        }

        public int getMin() {

            return min;
        }

        public int getMax() {

            return max;
        }
    }
	
	
}
