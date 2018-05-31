package com.weyao.srv.dao.bee.mapper;

import org.apache.ibatis.annotations.Param;

public interface BeeCalcInsuranceMapper {
	int updateInsuranceRemindTime(@Param(value = "calcRecordItemId") long calcRecordItemId, @Param(value = "remindTime") String remindTime);
	
	int updateInsurancePayRemindMark(@Param(value = "calcRecordItemId") long calcRecordItemId);
}
