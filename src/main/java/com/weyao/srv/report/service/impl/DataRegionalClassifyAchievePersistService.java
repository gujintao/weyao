package com.weyao.srv.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weyao.srv.dao.report.mapper.DataRegionalClassifyAchievePersistMapper;
import com.weyao.srv.report.entity.DataRegionalClassifyAchieve;
import com.weyao.srv.report.service.IndicatorPersistService;

/**
 *  区域运营分类业绩统计持久化服务
 * @author Victor
 */
@Service(value = "dataRegionalClassifyAchievePersistService")
public class DataRegionalClassifyAchievePersistService implements IndicatorPersistService<DataRegionalClassifyAchieve>  {

    @Autowired
    private DataRegionalClassifyAchievePersistMapper dataRegionalClassifyAchievePersistMapper;
    

	@Override
	public void saveOrUpdate(DataRegionalClassifyAchieve dataRegionalClassifyAchieve) {
		 this.dataRegionalClassifyAchievePersistMapper.saveOrUpdate(dataRegionalClassifyAchieve);
	}
 

}
