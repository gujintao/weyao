package com.weyao.srv.report.intercepter.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.info.sso.Operator;
import com.weyao.srv.dao.report.mapper.ReportMapper;
import com.weyao.srv.report.intercepter.AbstractIntercepter;
import com.weyao.srv.sso.SsoSrv;

/** 
  * 
  * @version 1.0
  * @author  Pin Xiong
  * @date 创建时间：2016年7月14日 下午6:46:18
  */
public class OperatorIntercepter extends AbstractIntercepter<Integer> {

    public OperatorIntercepter(ReportMapper reportMapper, String sourceFieldName, String targetFieldName,
            boolean forceUpdate) {
        super(reportMapper, sourceFieldName, targetFieldName, forceUpdate);
    }
    
    private static final Log LOGGER=LogFactory.getLog(OperatorIntercepter.class);
    @Autowired
    private SsoSrv ssoSrv;
    
    @Override
    public Object getTargetValue(Integer sourceValue) {
        if(sourceValue!=null){
            try{
                Operator operator= this.ssoSrv.getOperator(sourceValue);
                return operator.getUsername();
            }catch(Exception ex){
                LOGGER.error(String.format("无法通过opid【%s】查询用户名称", sourceValue),ex);
            }
        }
        return "";
    }
}
