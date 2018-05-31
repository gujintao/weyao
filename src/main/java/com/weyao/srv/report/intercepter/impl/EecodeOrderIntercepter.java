package com.weyao.srv.report.intercepter.impl;

import com.weyao.common.IdEncoder;
import com.weyao.srv.dao.report.mapper.ReportMapper;
import com.weyao.srv.report.intercepter.AbstractIntercepter;

/** 
  * 
  * @version 1.0
  * @author  Pin Xiong
  * @date 创建时间：2016年7月14日 下午3:30:16
  */
public class EecodeOrderIntercepter extends AbstractIntercepter<Long> {

    public EecodeOrderIntercepter(ReportMapper reportMapper,String sourceFieldName, String targetFieldName, boolean forceUpdate) {
        super(reportMapper,sourceFieldName, targetFieldName, forceUpdate);
    }

    @Override
    public Object getTargetValue(Long sourceValue) {
        return IdEncoder.ORDER_IDENCODER.EncodeId(sourceValue);
    }

}
