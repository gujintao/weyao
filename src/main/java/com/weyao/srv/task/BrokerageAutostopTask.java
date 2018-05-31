package com.weyao.srv.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dsf.platform.jobs.job.JobResult;
import com.weyao.common.JsonHelper2;
import com.weyao.info.brokerage.tool.BrokerageStrategyJson;
import com.weyao.info.brokerage.tool.QueryCondition;
import com.weyao.info.common.PageBean;
import com.weyao.info.common.PageResult;
import com.weyao.srv.brokerage.BrokerageSrv;

public class BrokerageAutostopTask extends AbstractTimerTask {

	private static Logger logger = LoggerFactory.getLogger(BrokerageAutostopTask.class);

	@Autowired
	private BrokerageSrv brokerageSrv;
	
	@Override
	public JobResult handleBiz(String[] arg0) {
		JobResult result = new JobResult();
		logger.info(".......定时设置过期佣金策略无效.....start.......");
		try {
			QueryCondition  queryCondition = QueryCondition.builder().setEndTimeUpperLimit(new Date());
			//默认是查询50条数据
			PageBean pageBean = new PageBean(1);
			PageResult<BrokerageStrategyJson> strategies = brokerageSrv.getStrategies(queryCondition, pageBean);
			if(null != strategies.getBeanList()){
				logger.info(".......需要执行数据条数....."+strategies.getBeanList().size()+".......");
				for (BrokerageStrategyJson broker : strategies.getBeanList()) {
					logger.info(".......执行数据条....."+JsonHelper2.toJson(broker)+".......");
					brokerageSrv.updateStrategyStatus(broker.getStrategyId(), 0, 0);
				}
		   }
			result.setSendMail(true);
			result.setSuccess(true);
			logger.info(".......定时设置过期佣金策略无效.....EDN.......");
		} catch (Exception e) {
			logger.error("定时设置过期佣金策略无效失败!", e);
		}
		return result;
	}
}
