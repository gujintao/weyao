package com.weyao.srv.dao.report.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.weyao.info.indicator.bo.IndicatorRecord;
import com.weyao.srv.report.entity.DaySaleReport;

/** 
  * 全局销售报表
  * @version 1.0
  * @author  taoxiaoyan
  * @date 创建时间：2016年10月11日 下午7:17:19
  */
public interface DataSaleInsuranceMapper {
	
	 List<DaySaleReport> queryMonthSaleReportST(@Param("angleType") String angleType,
	            @Param("angle") String angle, @Param("from") String from, @Param("to") String to, 
	            @Param("lastMonthFrom") String lastMonthFrom, @Param("lastMonthTo") String lastMonthTo, 
	            @Param("lastYthisMFrom") String lastYthisMFrom, @Param("lastYthisMTo") String lastYthisMTo, @Param("rate") double rate);
	 
	 List<DaySaleReport> queryCityAndSupplierReportST(@Param("angleType") String angleType,
	            @Param("angle") String angle, @Param("from") String from, @Param("to") String to, 
	            @Param("lastMonthFrom") String lastMonthFrom, @Param("lastMonthTo") String lastMonthTo, 
	            @Param("lastYthisMFrom") String lastYthisMFrom, @Param("lastYthisMTo") String lastYthisMTo, @Param("rate") double rate);

	 List<IndicatorRecord> queryDayOfMonthST(@Param("angleType") String angleType,
	            @Param("angle") String angle, @Param("from") String from, @Param("to") String to);

	List<IndicatorRecord> querySupplierBarChart(@Param("angleType") String angleType,
            @Param("city") String city, @Param("supplier") String supplier, @Param("from") String from, @Param("to") String to);

    List<DaySaleReport> querySupplierCurveChart(@Param("angleType") String angleType,
            @Param("city") String city, @Param("supplier") String supplier, @Param("from") String from, @Param("to") String to);

    List<IndicatorRecord> queryCityBarChart(@Param("angleType") String angleType,
            @Param("city") String city, @Param("supplier") String supplier, @Param("from") String from, @Param("to") String to);
   
    List<DaySaleReport> queryCityCurveChart(@Param("angleType") String angleType,
            @Param("city") String city, @Param("supplier") String supplier, @Param("from") String from, @Param("to") String to);

    List<IndicatorRecord> queryChannelBarChart(@Param("angleType") String angleType,@Param("from") String from, @Param("to") String to);
    
    List<DaySaleReport> queryChannelCurveChart(@Param("angleType") String angleType,
            @Param("from") String from, @Param("to") String to);
    
    List<IndicatorRecord> queryActivityBarChart(@Param("angleType") String angleType, @Param("from") String from, @Param("to") String to);

    List<DaySaleReport> queryActivityCurveChart(@Param("angleType") String angleType, @Param("from") String from, @Param("to") String to);
    
    List<String> queryDataChart(@Param("from") String from, @Param("to") String to);
    
    List<IndicatorRecord> queryCity();

    List<IndicatorRecord> querySupplier();
}
