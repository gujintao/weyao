package com.weyao.srv.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weyao.srv.dao.report.mapper.BigSreenPersistMapper;
import com.weyao.srv.report.entity.DataBigScreenCalcRequest;
import com.weyao.srv.report.entity.DataBigScreenTrends;
import com.weyao.srv.report.service.BigScreenPersistService;

/**
 * 区域运营报表总览持久化服务
 * 
 * @author Victor
 */
@Service(value = "bigScreenPersistService")
public class BigScreenPersistServiceImpl implements BigScreenPersistService {

	@Autowired
	private BigSreenPersistMapper bigSreenPersistMapper;

	@Override
	public void saveOrUpdateTrends(DataBigScreenTrends dataBigScreenTrends) {
		 this.bigSreenPersistMapper.saveOrUpdateTrends(dataBigScreenTrends);
	}

	@Override
	public void saveOrUpdateCalcRequest(DataBigScreenCalcRequest dataBigScreenCalcRequest) {
		this.bigSreenPersistMapper.saveOrUpdateCalcRequest(dataBigScreenCalcRequest);
	}

}
