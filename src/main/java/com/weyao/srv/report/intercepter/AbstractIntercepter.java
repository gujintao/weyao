package com.weyao.srv.report.intercepter;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.weyao.common.JsonHelper2;
import com.weyao.srv.dao.report.mapper.ReportMapper;

/**
 * 报表记录拦截器抽象类 K表示待处理对象中源字段的值类型
 * 
 * @version 1.0
 * @author Pin Xiong
 * @date 创建时间：2016年7月14日 下午2:27:42
 */
public abstract class AbstractIntercepter<K> implements Intercepter {

    /**
     * 初始化拦截器
     * 
     * @param reportService
     *            报表服务
     * @param sourceFieldName
     *            源字段名称
     * @param targetFieldName
     *            目标字段名称
     * @param forceUpdate
     *            是否需要更新数据
     */
    public AbstractIntercepter(ReportMapper reportMapper, String sourceFieldName, String targetFieldName,
            boolean forceUpdate) {
        this.reportMapper = reportMapper; 
        this.sourceFieldName = sourceFieldName;
        this.targetFieldName = targetFieldName;
        this.forceUpdate = forceUpdate;
    }

    private ReportMapper reportMapper;
    private String sourceFieldName;
    private String targetFieldName;
    private boolean forceUpdate;
    private static final Log LOGGER = LogFactory.getLog(AbstractIntercepter.class);

    /**
     * 更新待处理的记录
     * 
     * @param obj
     *            待处理的记录
     */
    @SuppressWarnings("unchecked")
    public <T> void update(T obj) {
        this.reportMapper.updateReport(obj);
    }

    /**
     * 获取目标数据值
     * 
     * @param sourceValue
     *            源数据值
     * @return 目标数据值
     */
    public abstract Object getTargetValue(K sourceValue);

    @SuppressWarnings("unchecked")
    @Override
    public <T> void handle(T report) {
        if (report != null && !StringUtils.isEmpty(this.sourceFieldName)
                && !StringUtils.isEmpty(this.targetFieldName)) {
            Class<T> clazz = (Class<T>) report.getClass();
            try {
                /**
                 * 1.获取源字段的值
                 * 2.如果源字段与目标字段相同，更新对象的值；如果forceUpdate=true则更新数据库，否则不更新数据库
                 * 3.如果源字段与目标字段不相同，目标字段的值为空，更新对象的值，并更新数据库
                 * 4.如果源字段与目标字段不相同，目标字段的值不为空，forceUpdate=true时，
                 * 更新对象的值，并更新数据库，否则不更新目标的值和数据库
                 */
                Field sourceField = clazz.getDeclaredField(this.sourceFieldName.trim());
                sourceField.setAccessible(true);
                Object sourceFieldvalue = sourceField.get(report);
                if (sourceFieldvalue != null) {
                    Object newTargetFieldValue = this.getTargetValue((K) sourceFieldvalue);
                    if (this.sourceFieldName.trim().equals(this.targetFieldName.trim())) {
                        sourceField.set(report, newTargetFieldValue);
                        if (this.forceUpdate) {
//                            this.update(report);
                        }
                    } else {
                        Field targetField = clazz.getDeclaredField(this.targetFieldName.trim());
                        targetField.setAccessible(true);
                        Object targetFieldvalue = targetField.get(report);
                        if (StringUtils.isEmpty(targetFieldvalue) || this.forceUpdate) {
                            targetField.set(report, newTargetFieldValue);
//                            this.update(report);
                        }
                    }
                }
            } catch (Exception e) {
            	e.printStackTrace();
                LOGGER.error(String.format("处理对象【%s】出现异常", JsonHelper2.toJson(report)), e);
            }
        }
    }

    @Override
    public <T> void Handle(List<T> reports) {
        if (!CollectionUtils.isEmpty(reports)) {
            for (T report : reports) {
                this.handle(report);
            }
        }
    }
}
