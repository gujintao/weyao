package com.weyao.srv.report.intercepter.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.weyao.info.dictionary.City;
import com.weyao.srv.dao.report.mapper.ReportMapper;
import com.weyao.srv.dictionary.DictionarySrv;
import com.weyao.srv.report.intercepter.AbstractIntercepter;

/** 
  * 
  * @version 1.0
  * @author  Pin Xiong
  * @date 创建时间：2016年7月15日 上午10:40:57
  */
public class CityIntercepter extends AbstractIntercepter<Integer> {

    public CityIntercepter(ReportMapper reportMapper, String sourceFieldName, String targetFieldName,
            boolean forceUpdate) {
        super(reportMapper, sourceFieldName, targetFieldName, forceUpdate);
    }

    private static final Log LOGGER=LogFactory.getLog(CityIntercepter.class);
    @Autowired
    private DictionarySrv dictionarySrv;
    
    @Override
    public Object getTargetValue(Integer sourceValue) {
        try{
            List<City>cities=this.dictionarySrv.listAllCity();
            if(!CollectionUtils.isEmpty(cities)){
                for(City city:cities){
                    if(city.getCityId().equals(sourceValue)){
                        return city.getName();
                    }
                }
            }
        }catch(Exception ex){
            LOGGER.error(String.format("无法通过城市id【%s】查询城市名称", sourceValue),ex);
        }
        return "";
    }

}
