package com.weyao.srv.report.service;

/** 
  * 报表服务接口
  * @version 1.0
  * @author  taoxiaoyan
  * @date 创建时间：2017年7月19日 上午10:13:50
  */
public interface IndicatorPersistService<T> {
    
	public void saveOrUpdate(T t);
}
