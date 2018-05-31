package com.weyao.srv.dao.report.mapper;

import com.weyao.srv.report.entity.DataRegionalClassifyAchieve;

/** 
  *  区域运营分类业绩统计
  * @version 1.0
  * @author  Tao Xiaoyan
  * @date 创建时间：2017年7月21日 上午10:17:19
  */
public interface DataRegionalClassifyAchievePersistMapper {
     
	/**
	 * 
	 * @param dataRegionalClassifyAchieve
	 */
	public abstract void saveOrUpdate(DataRegionalClassifyAchieve dataRegionalClassifyAchieve);
}
