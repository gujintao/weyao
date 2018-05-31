package com.weyao.srv.report.service;

import java.util.Map;

import com.weyao.info.common.PageBean;
import com.weyao.info.common.PageResult;

/** 
  * 报表服务接口
  * @version 1.0
  * @author  Pin Xiong
  * @date 创建时间：2016年7月12日 上午10:13:50
  */
public interface ReportService<T> {
    
    /**
     * 根据查询条件查询报表信息（可分页）
     * @param param 查询条件
     * @param pageBean 分页信息，null表示不分页
     * @return 报表分页信息集合
     */
    public PageResult<T> listReport(Map<String, Object> param, PageBean pageBean);
    
    /**
     * 根据查询条件查询报表信息（可分页）
     * @param param 查询条件
     * @param pageNum 第几页
     * @return 报表分页信息集合
     */
    public PageResult<T> listReport(Map<String, Object> param, int pageNum);
    
    /**
     * 根据查询条件查询报表信息（可分页）
     * @param param 查询条件
     * @param pageSize 页面大小
     * @param pageNum 第几页
     * @return 报表分页信息集合
     */
    public PageResult<T> listReport(Map<String, Object> param, int pageSize, int pageNum);
}
