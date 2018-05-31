package com.weyao.srv.dao.report.mapper;

import com.weyao.srv.report.entity.DataBigScreenCalcRequest;
import com.weyao.srv.report.entity.DataBigScreenTrends;

/**
 * 大屏报表
 * 
 * @version 1.0
 * @author Tao Xiaoyan
 * @date 创建时间：2017年8月30日 上午10:17:19
 */
public interface BigSreenPersistMapper {

	/**
	 * 
	 * @param dataBigScreenTrends
	 */
	public abstract void saveOrUpdateTrends(DataBigScreenTrends dataBigScreenTrends);

	/**
	 * 
	 * @param dataBigScreenTrends
	 */
	public abstract void saveOrUpdateCalcRequest(DataBigScreenCalcRequest dataBigScreenCalcRequest);
}
