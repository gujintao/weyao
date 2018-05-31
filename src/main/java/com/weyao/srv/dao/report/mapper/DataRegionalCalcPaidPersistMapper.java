package com.weyao.srv.dao.report.mapper;

import com.weyao.srv.report.entity.DataRegionalCalcPaid;

/** 
  * 区域运营询价&成单
  * @version 1.0
  * @author  Tao Xiaoyan
  * @date 创建时间：2017年7月20日 上午10:17:19
  */
public interface DataRegionalCalcPaidPersistMapper {
     
	/**
	 * 
	 * @param dataRegionalOperation
	 */
	public abstract void saveOrUpdate(DataRegionalCalcPaid dataRegionalCalcPaid);
}
