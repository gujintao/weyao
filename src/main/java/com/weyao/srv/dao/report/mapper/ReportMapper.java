package com.weyao.srv.dao.report.mapper;

import java.util.List;
import java.util.Map;

/** 
  * 报表接口
  * @version 1.0
  * @author  Pin Xiong
  * @date 创建时间：2016年7月12日 上午10:02:10
  */
public interface ReportMapper<T> {

    /**
     * 获取记录总数
     * @param param 查询条件
     * @return 记录总数
     */
    public Integer getTotalCount(Map<String, Object> param);
    
    /**
     * 根据查询条件查询报表信息
     * @param param 查询条件
     * @return 报表信息集合
     */
    public List<T> listReport(Map<String, Object> param);
    
    /**
     * 更新对象信息
     * @param obj 待修改的对象
     */
    public void updateReport(T obj);
}
