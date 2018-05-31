package com.weyao.srv.task.branch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.weyao.common.Vacations;
import com.weyao.exception.SrvException;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.info.indicator.constant.AngleType;
import com.weyao.srv.document.CallBack;
import com.weyao.srv.report.entity.DaySaleReport;
import com.weyao.srv.report.util.DateUtil;

/**
 * 全局销售报表任务分支
 * @author dujingjing
 *
 */
@Component
public class DateSaleInsuranceTaskBranch extends AbstractTaskBrahch<DaySaleReport>{

	private static Logger logger = LoggerFactory.getLogger(DateSaleInsuranceTaskBranch.class);
	
	public DateSaleInsuranceTaskBranch() {
		super("date_sale_insurance", new CallBack() {

			@Override
			public void afterWrote(XSSFWorkbook workbook, XSSFSheet sheet, int lineNum, int colNum) throws Exception {
				for(int row = 0; row < lineNum; row ++){
					int col = 0;
					int nextCol = 1;
					Row sheetRow = null;
					while (nextCol < 5 && col < 4) {
						sheetRow = sheet.getRow(row);
						Cell cel = sheetRow.getCell(col);
						Cell nextCel = sheetRow.getCell(nextCol);
						if(cel == null || nextCel == null || nextCel.getCellType() != Cell.CELL_TYPE_STRING){
							col ++;
							nextCol ++;
							continue;
						}
						String content = cel.getStringCellValue();
						String anotherContent = nextCel.getStringCellValue();
						if(content.equals(anotherContent)){
							//找到相等的单元格，继续搜索
							nextCol = searchContent(sheetRow, nextCol, content);
							//合并单元格
							try{
								CellRangeAddress cra = new CellRangeAddress(row, row, col, nextCol - 1);
								sheet.addMergedRegion(cra);
//								sheet.mergeCells(col, row, nextCol - 1, row);
//								sheet.getWritableCell(col, row).setCellFormat(centerAlignFormat);
							}catch(Exception e){
								e.printStackTrace();
							}
							col = nextCol;
						}else{
							//不相等
							col ++;
							nextCol ++;
						}
					}
				}
			}
			
			private int searchContent(Row row, int col, String content){
				Cell cell = null;
				for(col ++ ; col < 5; col ++){
					cell = row.getCell(col);
					if(cell.getCellType() != Cell.CELL_TYPE_STRING){
						return col;
					}
					String another = cell.getStringCellValue();
					if(!content.equals(another)){
						return col;
					}
				}
				return col;
			}
		});
	}
	
	private final static String ORGINAL_SQL = "IFNULL(a.busiCount,0) as busiCount, IFNULL(a.busiSum/100,0) as busiSum ,IFNULL(a.orderCount,0) as orderCount, IFNULL(a.orderSum/100,0) as orderSum ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiCount,0)-?*IFNULL(f.busiCount,0))/(?*IFNULL(f.busiCount,0)),2),'%'),'-') as busiTbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiCount,0)-?*IFNULL(c.busiCount,0))/(?*IFNULL(c.busiCount,0)),2),'%'),'-') as busiCountHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiSum,0)-?*IFNULL(c.busiSum,0))/(?*IFNULL(c.busiSum,0)),2),'%'),'-') as busiSumHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.orderCount,0)-?*IFNULL(c.orderCount,0))/(?*IFNULL(c.orderCount,0)),2),'%'),'-') as orderCountHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.orderSum,0)-?*IFNULL(c.orderSum,0))/(?*IFNULL(c.orderSum,0)),2),'%'),'-') as orderSumHbRate ,IFNULL(a.cancleCount,0) as cancleCount, IFNULL(a.cancleSum/100,0) as cancleSum from ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND angle = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) a left join ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND angle = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) c on a.angle = c.angle left join ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND angle = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) f on a.angle = f.angle order by angleSecond";
	
	private final static String MONTH_TOTAL_SQL = "select '月累计' AS currentDate, '总计' as angleFirst, '总计' as angleSecond, '总计' as angleThird, " + ORGINAL_SQL;
	
	private final static String OVERALL_SQL = "select '1' AS currentDate, a.angle as angleFirst, a.angle as angleSecond, a.angle as angleThird, " + ORGINAL_SQL;
	
	private final static String PRODUCT_SQL = "select '1' AS currentDate, '产品' AS angleFirst, a.angle as angleSecond, a.angle as angleThird, " + ORGINAL_SQL;
	
//	private final static String CHANNEL_SQL = "select '1' AS currentDate, '渠道' AS angleFirst, a.angle as angleSecond, a.angle as angleThird, " + "IFNULL(a.busiCount,0) as busiCount, IFNULL(a.busiSum/100,0) as busiSum ,IFNULL(a.orderCount,0) as orderCount, IFNULL(a.orderSum/100,0) as orderSum ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiCount,0)-?*IFNULL(f.busiCount,0))/(?*IFNULL(f.busiCount,0)),2),'%'),'-') as busiTbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiCount,0)-?*IFNULL(c.busiCount,0))/(?*IFNULL(c.busiCount,0)),2),'%'),'-') as busiCountHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiSum,0)-?*IFNULL(c.busiSum,0))/(?*IFNULL(c.busiSum,0)),2),'%'),'-') as busiSumHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.orderCount,0)-?*IFNULL(c.orderCount,0))/(?*IFNULL(c.orderCount,0)),2),'%'),'-') as orderCountHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.orderSum,0)-?*IFNULL(c.orderSum,0))/(?*IFNULL(c.orderSum,0)),2),'%'),'-') as orderSumHbRate ,IFNULL(a.cancleCount,0) as cancleCount,IFNULL(a.cancleSum/100,0) as cancleSum from ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) a left join ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) c on a.angle = c.angle left join ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) f on a.angle = f.angle order by angleSecond";

	private final static String ORDER_SOURCE_TYPE_SQL = "select '1' AS currentDate, '订单来源' AS angleFirst, a.angle as angleSecond, a.angle as angleThird, " + "IFNULL(a.busiCount,0) as busiCount, IFNULL(a.busiSum/100,0) as busiSum ,IFNULL(a.orderCount,0) as orderCount, IFNULL(a.orderSum/100,0) as orderSum ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiCount,0)-?*IFNULL(f.busiCount,0))/(?*IFNULL(f.busiCount,0)),2),'%'),'-') as busiTbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiCount,0)-?*IFNULL(c.busiCount,0))/(?*IFNULL(c.busiCount,0)),2),'%'),'-') as busiCountHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiSum,0)-?*IFNULL(c.busiSum,0))/(?*IFNULL(c.busiSum,0)),2),'%'),'-') as busiSumHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.orderCount,0)-?*IFNULL(c.orderCount,0))/(?*IFNULL(c.orderCount,0)),2),'%'),'-') as orderCountHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.orderSum,0)-?*IFNULL(c.orderSum,0))/(?*IFNULL(c.orderSum,0)),2),'%'),'-') as orderSumHbRate ,IFNULL(a.cancleCount,0) as cancleCount,IFNULL(a.cancleSum/100,0) as cancleSum from ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) a left join ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) c on a.angle = c.angle left join ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) f on a.angle = f.angle order by angleSecond";
	
	private final static String CITY_SUPPLIER_SQL = "select '1' AS currentDate, '城市-保司' AS angleFirst, substring_index(a.angle, '-', 1) AS angleSecond, substring_index(a.angle, '-', - 1) AS angleThird, " + "IFNULL(a.busiCount,0) as busiCount, IFNULL(a.busiSum/100,0) as busiSum ,IFNULL(a.orderCount,0) as orderCount, IFNULL(a.orderSum/100,0) as orderSum ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiCount,0)-?*IFNULL(f.busiCount,0))/(?*IFNULL(f.busiCount,0)),2),'%'),'-') as busiTbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiCount,0)-?*IFNULL(c.busiCount,0))/(?*IFNULL(c.busiCount,0)),2),'%'),'-') as busiCountHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.busiSum,0)-?*IFNULL(c.busiSum,0))/(?*IFNULL(c.busiSum,0)),2),'%'),'-') as busiSumHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.orderCount,0)-?*IFNULL(c.orderCount,0))/(?*IFNULL(c.orderCount,0)),2),'%'),'-') as orderCountHbRate ,IFNULL(CONCAT( FORMAT(100*(IFNULL(a.orderSum,0)-?*IFNULL(c.orderSum,0))/(?*IFNULL(c.orderSum,0)),2),'%'),'-') as orderSumHbRate ,IFNULL(a.cancleCount,0) as cancleCount,IFNULL(a.cancleSum/100,0) as cancleSum from ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) a left join ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) c on a.angle = c.angle left join ( select angle,sum(business_count) as busiCount,sum(business_sum) as busiSum, sum(order_count) as orderCount,sum(order_sum) as orderSum, sum(cancle_count) as cancleCount,sum(cancle_sum) as cancleSum from data_sale_insurance where 1=1 AND angle_type = ? AND statis_date >= ? AND statis_date <= ? group by angle,angle_type ) f on a.angle = f.angle order by angleSecond";
	

	@Override
	protected Map<String, List<? extends Object>> export() throws SrvException {
		Map<String, List<? extends Object>> map = Maps.newHashMap();
//		map.put(new FileTask(DEFAULT, "全局销售报表%s.xls"), this.saleReport());
		map.put("date_sale_insurance", this.saleReport());
		return map;
	}
	
	/**
	 * 全局销售报表V1.1.0
	 */
	private List<DaySaleReport> saleReport(){
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		c.setTime(date);

		// date所在月的第一天
		Calendar firstDay = Calendar.getInstance();
		firstDay.set(Calendar.YEAR, c.get(Calendar.YEAR));
		firstDay.set(Calendar.MONTH, c.get(Calendar.MONTH));
		firstDay.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String fromStr = format.format(firstDay.getTime()) + " 00:00:00";

		// date所在月的最后一天
		Calendar last = Calendar.getInstance();
		last.set(Calendar.YEAR, c.get(Calendar.YEAR));
		last.set(Calendar.MONTH, c.get(Calendar.MONTH));
		last.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastStr = format.format(last.getTime()) + " 23:59:59";
		Vacations v = new Vacations();
		int workDay = 0;
		int allWorkDay = 0;
		try {
			// 计算date所在月的1号到date之间的工作日天数。 
			workDay = v.countWorkDay(fromStr,format.format(date)+" 23:59:59");
//			logger.info("date所在月的1号到date之间的工作日天数workDay = " + workDay);

			// 计算date所在月总共的工作日天数。
			allWorkDay = v.countWorkDay(fromStr, lastStr);
//			logger.info("date所在月总共的工作日天数allWorkDay = " + allWorkDay);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		// 日同比：date 与上个月去除休息日的顺序对齐做同比
		// 计算10.21的上个月9月1日的第workDay工作日后的日期
		// 上个月的第一天
		Calendar lastMFirstD = Calendar.getInstance();
		lastMFirstD.set(Calendar.YEAR, firstDay.get(Calendar.YEAR));
		lastMFirstD.set(Calendar.MONTH, firstDay.get(Calendar.MONTH) - 1);
		lastMFirstD.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String lastMFirstDStr = format.format(lastMFirstD.getTime());

		// 上个月的最后一天
		Calendar lastMTheLastD = Calendar.getInstance();
		lastMTheLastD.set(Calendar.YEAR, firstDay.get(Calendar.YEAR));
		lastMTheLastD.set(Calendar.MONTH, firstDay.get(Calendar.MONTH)-1);
		lastMTheLastD.set(Calendar.DAY_OF_MONTH, lastMTheLastD.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastMTheLastDStr = format.format(lastMTheLastD.getTime());
		
		// 上年同月的第一天 月累计同比用
		Calendar lastYthisMFirstD = Calendar.getInstance();
		lastYthisMFirstD.set(Calendar.YEAR, firstDay.get(Calendar.YEAR) - 1);
		lastYthisMFirstD.set(Calendar.MONTH, firstDay.get(Calendar.MONTH));
		lastYthisMFirstD.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String lastYthisMFirstDStr = format.format(lastYthisMFirstD.getTime());

		// 上年同月的最后一天
		Calendar lastYthisMTheLastD = Calendar.getInstance();
		lastYthisMTheLastD.set(Calendar.YEAR, firstDay.get(Calendar.YEAR) - 1);
		lastYthisMTheLastD.set(Calendar.MONTH, firstDay.get(Calendar.MONTH));
		lastYthisMTheLastD.set(Calendar.DAY_OF_MONTH, lastYthisMTheLastD.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastYthisMTheLastDStr = format.format(lastYthisMTheLastD.getTime());

		double rate = (double) (workDay +1) / (double) (allWorkDay + 1);
		// 月累计统计
		List<DaySaleReport> count = this.queryMonthSaleReportByST(
				MONTH_TOTAL_SQL, AngleType.Overall.getAngleType(),
				"Overall", format.format(firstDay.getTime()), format.format(last.getTime()),lastMFirstDStr,lastMTheLastDStr, 
				lastYthisMFirstDStr, lastYthisMTheLastDStr, rate);
		logger.info("月度统计查询完成.");
		Date beforeDayj = DateUtil.substractDateByWorkDay(date, -1); // 当前统计日期
		logger.debug("beforeDayj:----"+beforeDayj);
		// 日同比：10.21 与上个月去除休息日的顺序对齐做同比
		Date rtbj = DateUtil.addDateByWorkDay(lastMFirstD.getTime(), workDay - 1);

		// 环比：与上一个工作日做环比
		Date beforeDayjLastDay = DateUtil.substractDateByWorkDay(beforeDayj, -1);

		// 时间参数的开始与结束字符串
		String beforeDayjFrom = format.format(beforeDayj); 

		String rtbjFrom = format.format(rtbj); 

		String beforeDayjLastDayFrom = format.format(beforeDayjLastDay); 
		
		logger.debug("beforeDayjFrom:----"+beforeDayjFrom);
		// 指定日期(acturalTime为指定日期) overall
		List<DaySaleReport> overAll = this.queryMonthSaleReportByST(
				OVERALL_SQL, AngleType.Overall.getAngleType(),
				"Overall", beforeDayjFrom, beforeDayjFrom, beforeDayjLastDayFrom,beforeDayjLastDayFrom, rtbjFrom, rtbjFrom, 1);
		if (CollectionUtils.isNotEmpty(overAll)){
			for (DaySaleReport d : overAll) {
				d.setCurrentDate(beforeDayjFrom);
			}
			count.addAll(overAll);
		}
		// 指定日期(acturalTime为指定日期) 产品 X
		List<DaySaleReport> X = this.queryMonthSaleReportByST(
				PRODUCT_SQL, AngleType.产品.getAngleType(), "X产品",
				beforeDayjFrom, beforeDayjFrom, beforeDayjLastDayFrom,beforeDayjLastDayFrom, rtbjFrom, rtbjFrom, 1);
		if (CollectionUtils.isNotEmpty(X)){
			for (DaySaleReport d : X) {
				d.setCurrentDate(beforeDayjFrom);
			}
			count.addAll(X);
		}
		// 指定日期(acturalTime为指定日期) 产品 E
		List<DaySaleReport> E = this.queryMonthSaleReportByST(
				PRODUCT_SQL, AngleType.产品.getAngleType(), "E产品",
				beforeDayjFrom, beforeDayjFrom, beforeDayjLastDayFrom,beforeDayjLastDayFrom, rtbjFrom, rtbjFrom, 1);
		if (CollectionUtils.isNotEmpty(E)){
			for (DaySaleReport d : E) {
				d.setCurrentDate(beforeDayjFrom);
			}
			count.addAll(E);
		}

		// 指定日期(acturalTime为指定日期) 产品 无活动
		List<DaySaleReport> noActivity = this.queryMonthSaleReportByST(
				PRODUCT_SQL, AngleType.产品.getAngleType(),
				"无活动", beforeDayjFrom, beforeDayjFrom, beforeDayjLastDayFrom,beforeDayjLastDayFrom, rtbjFrom, rtbjFrom, 1);
		if (CollectionUtils.isNotEmpty(noActivity)){
			for (DaySaleReport d : noActivity) {
				d.setCurrentDate(beforeDayjFrom);
			}
			count.addAll(noActivity);
		}

		// 指定日期(acturalTime为指定日期) 产品 单交强
		List<DaySaleReport> onlyForce = this.queryMonthSaleReportByST(
				PRODUCT_SQL, AngleType.产品.getAngleType(),
				"单交强", beforeDayjFrom, beforeDayjFrom, beforeDayjLastDayFrom,beforeDayjLastDayFrom, rtbjFrom, rtbjFrom, 1);
		if (CollectionUtils.isNotEmpty(onlyForce)){
			for (DaySaleReport d : onlyForce) {
				d.setCurrentDate(beforeDayjFrom);
			}
			count.addAll(onlyForce);
		}

		// 指定日期(acturalTime为指定日期) 渠道 电销 LB-上海 LB-武汉 WX DL
//		List<DaySaleReport> channelType = this.queryMonthSaleReportByST(
//				CHANNEL_SQL, AngleType.渠道.getAngleType(),
//				null, beforeDayjFrom, beforeDayjFrom, beforeDayjLastDayFrom,beforeDayjLastDayFrom, rtbjFrom, rtbjFrom, 1);
//		if (CollectionUtils.isNotEmpty(channelType)){
//			for (DaySaleReport d : channelType) {
//				d.setCurrentDate(beforeDayjFrom);
//			}
//			count.addAll(channelType);
//		}
		
		// 指定日期(acturalTime为指定日期) 订单来源 网电销 LB-上海 LB-武汉
		List<DaySaleReport> orderSourceType = this.queryMonthSaleReportByST(
				ORDER_SOURCE_TYPE_SQL, "order_source_type",
				null, beforeDayjFrom, beforeDayjFrom, beforeDayjLastDayFrom,beforeDayjLastDayFrom, rtbjFrom, rtbjFrom, 1);
		if (CollectionUtils.isNotEmpty(orderSourceType)){
			for (DaySaleReport d : orderSourceType) {
				d.setCurrentDate(beforeDayjFrom);
			}
			count.addAll(orderSourceType);
		}

		// 指定日期(acturalTime为指定日期) 城市-保司
		List<DaySaleReport> cityName_supplierName = this.queryMonthSaleReportByST(
				CITY_SUPPLIER_SQL, AngleType.城市_保司.getAngleType(), 
				null, beforeDayjFrom, beforeDayjFrom, beforeDayjLastDayFrom,beforeDayjLastDayFrom, rtbjFrom, rtbjFrom, 1);
		if (CollectionUtils.isNotEmpty(cityName_supplierName)){
			for (DaySaleReport d : cityName_supplierName) {
				d.setCurrentDate(beforeDayjFrom);
			}
			count.addAll(cityName_supplierName);
		}
		return count;

	}
	
	private List<DaySaleReport> queryMonthSaleReportByST(String sql, String angleType, String angle, String from, String to,
			String lastMonthFrom, String lastMonthTo, String lastYthisMFrom, String lastYthisMTo, double rate){
		try {
			return super.proxyQuery(sql, ResultType.LIST, fillParameters(angleType, angle, from, to, lastMonthFrom, lastMonthTo, lastYthisMFrom, lastYthisMTo, rate), DaySaleReport.class);
		} catch (SrvException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Object> fillParameters(String angleType, String angle, String from, String to, String lastMonthFrom,
			String lastMonthTo, String lastYthisMFrom, String lastYthisMTo, double rate) {
		List<Object> queryParams = new ArrayList<Object>();
		for (int i = 0; i < 10; i++) {
			queryParams.add(rate);
		}
		queryParams.add(angleType);
		if (angle != null) {
			queryParams.add(angle);
		}
		queryParams.add(from);
		queryParams.add(to);
		queryParams.add(angleType);
		if (angle != null) {
			queryParams.add(angle);
		}
		queryParams.add(lastMonthFrom);
		queryParams.add(lastMonthTo);
		queryParams.add(angleType);
		if (angle != null) {
			queryParams.add(angle);
		}
		queryParams.add(lastYthisMFrom);
		queryParams.add(lastYthisMTo);
		return queryParams;
	}
	
}
