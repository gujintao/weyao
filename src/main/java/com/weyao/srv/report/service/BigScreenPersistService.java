package com.weyao.srv.report.service;

import com.weyao.srv.report.entity.DataBigScreenCalcRequest;
import com.weyao.srv.report.entity.DataBigScreenTrends;

/** 
  * 报表服务接口
  * @version 1.0
  * @author  taoxiaoyan
  * @date 创建时间：2017年8月30日 上午10:13:50
  */
public interface BigScreenPersistService {
    
	public void saveOrUpdateTrends(DataBigScreenTrends t);
	
	public void saveOrUpdateCalcRequest(DataBigScreenCalcRequest t);
	
}
