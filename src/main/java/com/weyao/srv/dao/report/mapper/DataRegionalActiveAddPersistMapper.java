package com.weyao.srv.dao.report.mapper;

import com.weyao.srv.report.entity.DataRegionalActiveAdd;

/** 
  *  区域运营活跃&增长
  * @version 1.0
  * @author  Tao Xiaoyan
  * @date 创建时间：2017年7月20日 上午10:17:19
  */
public interface DataRegionalActiveAddPersistMapper {
     
	/**
	 * 
	 * @param dataRegionalOperation
	 */
	public abstract void saveOrUpdate(DataRegionalActiveAdd dataRegionalActiveAdd);
}
