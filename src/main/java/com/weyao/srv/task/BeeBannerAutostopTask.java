package com.weyao.srv.task;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dsf.platform.jobs.job.JobResult;
import com.weyao.common.JsonHelper2;
import com.weyao.info.brokerage.tool.QueryCondition;
import com.weyao.srv.dao.bee.mapper.BeeBannerMapper;

public class BeeBannerAutostopTask extends AbstractTimerTask {

	private static Logger logger = LoggerFactory.getLogger(BeeBannerAutostopTask.class);

	@Autowired
	private BeeBannerMapper  beeBannerMapper;
	
	@Override
	public JobResult handleBiz(String[] arg0) {
		JobResult result = new JobResult();
		logger.info("........定时设置上架Banner状态.....start.......");
		try {
			QueryCondition  queryCondition = QueryCondition.builder().setEndTimeUpperLimit(new Date());
			//默认是查询50条数据
		 
			List<Long> bannerIds = beeBannerMapper.selectOverdueBanner();
			if(null != bannerIds && bannerIds.size()>0){
				logger.info(".......需要执行数据条数....."+bannerIds.size()+".......");
				for (Long bannerId : bannerIds) {
					logger.info(".......执行数据...bannerId="+JsonHelper2.toJson(bannerId)+".......");
					beeBannerMapper.updateOverdueBanner(bannerId);
				}
		   }
			result.setSendMail(true);
			result.setSuccess(true);
			logger.info(".......定时设置上架Banner状态.....EDN.......");
		} catch (Exception e) {
			logger.error(".定时设置上架Banner状态失败!", e);
		}
		return result;
	}
}
