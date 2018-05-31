package com.weyao.srv.report.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.weyao.common.JsonHelper2;
import com.weyao.info.common.PageBean;
import com.weyao.info.common.PageResult;
import com.weyao.srv.report.intercepter.Intercepter;

/**
 * 报表抽象类
 * 
 * @version 1.0
 * @author Pin Xiong
 * @date 创建时间：2016年7月12日 上午10:22:43
 */
public abstract class AbstractReportService<T> implements ReportService<T> {

    private final Log LOGGER = LogFactory.getLog(AbstractReportService.class);
    private List<Intercepter> intercepters;
    
    public List<Intercepter> getIntercepters() {
        return intercepters;
    }

    public void setIntercepters(List<Intercepter> intercepters) {
        this.intercepters = intercepters;
    }

    @Override
    public PageResult<T> listReport(Map<String, Object> param, PageBean pageBean) {
        PageResult<T> result = new PageResult<T>();
        try {
            LOGGER.debug(String.format("查询条件：param=【%s】，pageBean=【%s】", JsonHelper2.toJson(param),
                    JsonHelper2.toJson(pageBean)));
            param.put("pageBean", pageBean);
            
            if(param.get("startTime") != null){
            	String startTime=param.get("startTime").toString();
                if(!StringUtils.isEmpty(startTime)){
                    param.put("startTime",startTime+" 00:00:00");
                }
            }
            if(param.get("endTime") != null){
            	String endTime=param.get("endTime").toString();
                if(!StringUtils.isEmpty(endTime)){
                    param.put("endTime",endTime+" 23:59:59");
                }
            }
            
            //垫付时间区间
            if(param.get("dianfuStartTime") != null){
            	String dianfuStartTime=param.get("dianfuStartTime").toString();
                if(!StringUtils.isEmpty(dianfuStartTime)){
                    param.put("dianfuStartTime",dianfuStartTime+" 00:00:00");
                }
            }
            if(param.get("dianfuEndTime") != null){
            	String dianfuEndTime=param.get("dianfuEndTime").toString();
                if(!StringUtils.isEmpty(dianfuEndTime)){
                    param.put("dianfuEndTime",dianfuEndTime+" 23:59:59");
                }
            }
            
            //订单生成时间区间
            if(param.get("orderStartTime") != null){
            	String orderStartTime=param.get("orderStartTime").toString();
                if(!StringUtils.isEmpty(orderStartTime)){
                    param.put("orderStartTime",orderStartTime+" 00:00:00");
                }
            }
            if(param.get("orderEndTime") != null){
            	String orderEndTime=param.get("orderEndTime").toString();
                if(!StringUtils.isEmpty(orderEndTime)){
                    param.put("orderEndTime",orderEndTime+" 23:59:59");
                }
            }
            
            // 总记录数
//            Integer count = this.getTotalCount(param);
//            pageBean.setTotalSize(count);
            // 查询列表
            List<T> reports = this.listReport(param);
            if(!CollectionUtils.isEmpty(reports)&&!CollectionUtils.isEmpty(this.getIntercepters())){
                for(Intercepter intercepter:this.getIntercepters()){
                    intercepter.<T>Handle(reports);
                }
            }
            result.setBeanList(reports);
            result.setPageBean(pageBean);
        } catch (Exception ex) {
            LOGGER.error(String.format("查询报表信息异常。param=【%s】，pageBean=【%s】", JsonHelper2.toJson(param),
                    JsonHelper2.toJson(pageBean)), ex);
        }
        return result;
    }
    
    @Override
    public PageResult<T> listReport(Map<String, Object> param, int pageNum){
        PageBean pageBean=new PageBean(pageNum);
        return this.listReport(param, pageBean);
    }
    
    @Override
    public PageResult<T> listReport(Map<String, Object> param, int pageSize, int pageNum){
        PageBean pageBean=new PageBean(pageSize, pageNum);
        return this.listReport(param, pageBean);
    }

    /**
     * 获取记录总数
     * 
     * @param param
     *            查询条件
     * @return 记录总数
     */
    public abstract Integer getTotalCount(Map<String, Object> param);

    /**
     * 根据查询条件查询报表信息
     * 
     * @param param
     *            查询条件
     * @return 报表信息集合
     */
    public abstract List<T> listReport(Map<String, Object> param);
    
    /**
     * 更新对象信息
     * @param obj 待修改的对象
     * @return ID
     */
    public abstract Long updateReport(T obj);

}
