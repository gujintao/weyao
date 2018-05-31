package com.weyao.srv.dao.report.mapper;

import com.weyao.srv.report.entity.DataRegionalOperation;

/** 
  * 区域运营报表总览
  * @version 1.0
  * @author  Tao Xiaoyan
  * @date 创建时间：2017年7月19日 上午10:17:19
  */
public interface DataRegionalOperationPersistMapper {
     
	/**
	 * 
	 * @param dataRegionalOperation
	 */
	public abstract void saveOrUpdate(DataRegionalOperation dataRegionalOperation);
}
