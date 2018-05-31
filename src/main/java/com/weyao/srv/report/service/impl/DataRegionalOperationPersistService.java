package com.weyao.srv.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weyao.srv.dao.report.mapper.DataRegionalOperationPersistMapper;
import com.weyao.srv.report.entity.DataRegionalOperation;
import com.weyao.srv.report.service.IndicatorPersistService;

/**
 * 区域运营报表总览持久化服务
 * @author Victor
 */
@Service(value = "dataRegionalOperationPersistService")
public class DataRegionalOperationPersistService implements IndicatorPersistService<DataRegionalOperation>  {

    @Autowired
    private DataRegionalOperationPersistMapper dataRegionalOperationPersistMapper;
    

	@Override
	public void saveOrUpdate(DataRegionalOperation dataRegionalOperation) {
		 this.dataRegionalOperationPersistMapper.saveOrUpdate(dataRegionalOperation);
	}
 

}
