package com.weyao.srv.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weyao.srv.dao.report.mapper.DataRegionalActiveAddPersistMapper;
import com.weyao.srv.report.entity.DataRegionalActiveAdd;
import com.weyao.srv.report.service.IndicatorPersistService;

/**
 *  区域运营活跃&增长持久化服务
 * @author Victor
 */
@Service(value = "dataRegionalActiveAddPersistService")
public class DataRegionalActiveAddPersistService implements IndicatorPersistService<DataRegionalActiveAdd>  {

    @Autowired
    private DataRegionalActiveAddPersistMapper dataRegionalActiveAddPersistMapper;
    

	@Override
	public void saveOrUpdate(DataRegionalActiveAdd dataRegionalActiveAdd) {
		 this.dataRegionalActiveAddPersistMapper.saveOrUpdate(dataRegionalActiveAdd);
	}
 

}
