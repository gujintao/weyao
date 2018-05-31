package com.weyao.srv.dao.bee.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface BeeBannerMapper {
	List<Long> selectOverdueBanner();
	
	int updateOverdueBanner(@Param(value = "bannerId") long bannerId);
}
