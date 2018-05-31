package com.weyao.srv.dao.report.mapper;

import java.util.List;

import com.weyao.srv.report.entity.DailyBeeActive;

/**
 * 提供给报表业务使用的Mapper接口
 * @author dujingjing
 * @version 1.0
 */
public interface IDailyBeeActiveMapper {

	/**
	 * 查询每日下单明细报表数据，规则中为前一天的数据
	 * @return
	 */
	public List<DailyBeeActive> reportDailyBeeActive();
}
