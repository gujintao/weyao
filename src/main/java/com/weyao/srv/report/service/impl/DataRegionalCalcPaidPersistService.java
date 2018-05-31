package com.weyao.srv.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weyao.srv.dao.report.mapper.DataRegionalCalcPaidPersistMapper;
import com.weyao.srv.report.entity.DataRegionalCalcPaid;
import com.weyao.srv.report.service.IndicatorPersistService;

/**
 * 区域运营询价&成单持久化服务
 * @author Victor
 */
@Service(value = "dataRegionalCalcPaidPersistService")
public class DataRegionalCalcPaidPersistService implements IndicatorPersistService<DataRegionalCalcPaid>  {

    @Autowired
    private DataRegionalCalcPaidPersistMapper dataRegionalCalcPaidPersistMapper;
    

	@Override
	public void saveOrUpdate(DataRegionalCalcPaid dataRegionalCalcPaid) {
		 this.dataRegionalCalcPaidPersistMapper.saveOrUpdate(dataRegionalCalcPaid);
	}
 

}
