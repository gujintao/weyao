package com.weyao.srv.report.intercepter.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.weyao.info.common.DictConstants;
import com.weyao.info.dictionary.Dictionary;
import com.weyao.srv.dao.report.mapper.ReportMapper;
import com.weyao.srv.dictionary.DictionarySrv;
import com.weyao.srv.report.intercepter.AbstractIntercepter;

/** 
  * 
  * @version 1.0
  * @author  taoxiaoyan
  * @date 创建时间：2016年9月21日 上午10:40:57
  */
public class InsuranceStatusIntercepter extends AbstractIntercepter<Integer> {

    public InsuranceStatusIntercepter(ReportMapper reportMapper, String sourceFieldName, String targetFieldName,
            boolean forceUpdate) {
        super(reportMapper, sourceFieldName, targetFieldName, forceUpdate);
    }

    private static final Log LOGGER=LogFactory.getLog(InsuranceStatusIntercepter.class);
    @Autowired
    private DictionarySrv dictionarySrv;
    
    @Override
    public Object getTargetValue(Integer sourceValue) {
        try{
            List<Dictionary> insuranceStatuses=this.dictionarySrv.listDictionary(DictConstants.保单状态);
            if(!CollectionUtils.isEmpty(insuranceStatuses)){
                for(Dictionary insuranceStatus:insuranceStatuses){
                    if(insuranceStatus.getCode().equals(sourceValue.toString())){
                        return insuranceStatus.getName();
                    }
                }
            }
        }catch(Exception ex){
            LOGGER.error(String.format("无法通过保单状态id【%s】查询保单状态名称", sourceValue),ex);
        }
        return "";
    }

}
