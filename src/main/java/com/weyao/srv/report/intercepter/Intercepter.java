package com.weyao.srv.report.intercepter;

import java.util.List;

/** 
  * 报表记录拦截器
  * @version 1.0
  * @author  Pin Xiong
  * @date 创建时间：2016年7月13日 下午5:12:48
  */
public interface Intercepter {
    
    /**
     * 根据源字段来更新目标字段的值
     * @param report 待处理的记录
     */
    public <T>void handle(T report);
    
    /**
     * 根据源字段来更新目标字段的值
     * @param reports 待处理的记录集合
     */
    public <T>void Handle(List<T>reports);
}
