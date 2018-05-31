package com.weyao.srv.document.impl;

import static java.sql.JDBCType.BIGINT;
import static java.sql.JDBCType.DATE;
import static java.sql.JDBCType.DOUBLE;
import static java.sql.JDBCType.FLOAT;
import static java.sql.JDBCType.INTEGER;
import static java.sql.JDBCType.TIMESTAMP;
import static java.sql.JDBCType.VARCHAR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import com.weyao.common.JsonHelper2;
import com.weyao.srv.document.Adapters;
import com.weyao.srv.document.CallBack;
import com.weyao.srv.document.DocumentService;
import com.weyao.srv.document.Template;

//import jxl.Workbook;
//import jxl.write.DateFormat;
//import jxl.write.DateTime;
//import jxl.write.Label;
//import jxl.write.Number;
//import jxl.write.NumberFormats;
//import jxl.write.WritableCellFormat;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//import jxl.write.WriteException;

public class ExcelService<T> implements DocumentService<T> {

	private final static Log LOGGER = LogFactory.getLog(ExcelService.class);

//	private static WritableCellFormat wcfDF = null;
//	
//	private static WritableCellFormat JXL_DATE = null;
//	
//	private static WritableCellFormat wcfTEXT = null;
//	
//	private static WritableCellFormat wcfINTEGER = null;
//	
//	private static WritableCellFormat wcfFLOAT = null;
	
	private static final SimpleDateFormat DATA_FORMAT_HH = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final SimpleDateFormat DATA_FORMAT_DD = new SimpleDateFormat("yyyy-MM-dd");
	
	private XSSFDataFormat data_format = null;
	
	private XSSFCellStyle style_date = null;

	private XSSFCellStyle style_time = null;

	private XSSFCellStyle style_bigDecimal = null;
	
//	static{
//		DateFormat df = new DateFormat("yyyy-MM-dd HH:mm:ss");
//		wcfDF = new WritableCellFormat(data_format_HH);
//		df = new DateFormat("yyyy-MM-dd");
//		JXL_DATE = new WritableCellFormat(df);
//		wcfTEXT = new WritableCellFormat(NumberFormats.TEXT);
//		wcfINTEGER = new WritableCellFormat(NumberFormats.INTEGER);
//		wcfFLOAT = new WritableCellFormat(NumberFormats.FLOAT);
//	}
	
	/*
	 * 用来表示一个Excel页的列集合，以列号进行排序
	 */
	private List<Template<T>.Col> cols = null;

	private XSSFWorkbook workbook;
	
	private XSSFSheet sheet;
	
	private File out = null;
	
	private CallBack callback = null;
	
	private String reportName;
	
	public ExcelService() {

	}
	
	public ExcelService(CallBack callback){
		this.callback = callback;
	}
	
	/*
	 * 记录行号
	 */
	private int lineNum;
	
	/*
	 * 居中格式
	 */
//	private static CellStyle CENTER_ALIGN_FORMAT;
	
//	static{
//		try{
//			//设置居中格式
//			CENTER_ALIGN_FORMAT = new WritableCellFormat();
//			CENTER_ALIGN_FORMAT.setAlignment(jxl.format.Alignment.CENTRE);//单元格中的内容水平方向居中
//			CENTER_ALIGN_FORMAT.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//单元格的内容垂直方向居中
//		}catch(WriteException e){
//			e.printStackTrace();
//		}
//	}

	@Override
	public boolean createDocument(Template<T> template) {
		if(template == null){
			return false;
		}
		//接收列集合
		cols = template.exportCols();
		//创建内存空间，我们这里直接把Excel数据写入到内存中，而不再写入第三方文件
//		out = new ByteArrayOutputStream();
		
		String fileName = String.format("%s.xlsx", System.currentTimeMillis() + new Random().nextInt());
		out = com.weyao.boss.common.util.FileUtil.createTempFile(fileName); //创建CSV文件
//		out = new File("D:\\" + fileName);
		
		//创建WorkBook
		workbook = new XSSFWorkbook();
		data_format = workbook.createDataFormat();
		style_date = workbook.createCellStyle();
		style_time = workbook.createCellStyle();
		style_bigDecimal = workbook.createCellStyle();
		style_date.setDataFormat(data_format.getFormat("yyyy-MM-dd"));
		style_time.setDataFormat(data_format.getFormat("yyyy-MM-dd HH:mm:ss"));
		style_bigDecimal.setDataFormat(data_format.getFormat("###0.00"));
		//创建sheet页，目前暂时只有一页
		sheet = workbook.createSheet(template.getReportName());
//			Label label = null;
		//填充标题
//			CellView cellView = new CellView();  
//			cellView.setAutosize(true); //设置自动大小  
		Row row = sheet.createRow(lineNum);
		for (int i = 0; i < cols.size(); i++) {
			Template<T>.Col col = cols.get(i);
//				label = new Label(i, lineNum, col.getColName());
//				if(col.isMergeAllowed()){
//					label.setCellFormat(CENTER_ALIGN_FORMAT);
//				}
//			sheet.setColumnWidth(i, col.getColName().getBytes().length * 256/2+4);//根据内容自动设置列宽
			Cell cell = row.createCell(i);
			cell.getCellStyle().setAlignment(XSSFCellStyle.ALIGN_CENTER);
			cell.setCellValue(col.getColName());
		}
		lineNum ++;
		//传递模板名称
		this.reportName = template.getReportName();
		return true;
	}

	@Override
	public File fillBlank(List<T> source) throws Exception {
		List<MergedCell> mergedCells = new ArrayList<MergedCell>();
//		Label cell = null;
		Method method = null;
        for (Template<T>.Col col : cols) {
			col.clear();
		}
		try{
			for (T t : source) {
				Row row = sheet.createRow(lineNum);
				//填充数据
				String content;
				Cell cell = null;
				for (int i = 0; i < cols.size(); i++) {
					Template<T>.Col col = cols.get(i);
					if(t instanceof Map){
						method = t.getClass().getMethod("get", Object.class);
						content = Adapters.toString(method.invoke(t, col.getKey()), col.getType());
					}else{
						method = col.getMethod();
						content = Adapters.toString(method.invoke(t), col.getType());
					}
					//说明这一列允许相同的值的Y轴相邻单元格进行合并
					try {
						
					
					if(col.isMergeAllowed()){
						if(col.getLastContent() == null){
							//说明是第一个单元格，那么进行单元格记录
							mergedCells.add(new MergedCell(i, lineNum, lineNum, content));
							col.logContent(content);
							//POI需要先写入数据
							cell = row.createCell(i);
							cell.getCellStyle().setAlignment(HSSFCellStyle.ALIGN_CENTER);
							cell.setCellValue(content);
						}else{
							if(col.getLastContent().equals(content.trim())){
								//相同的值
								MergedCell unMerged = findMergedCell(mergedCells, i, lineNum - 1, false);
								unMerged.goDown();
							}else{
								//不相同，则写入原先的值，并将该记录清除
								MergedCell unMerged = findMergedCell(mergedCells, i, lineNum - 1, true);
								if(unMerged.xEnd > unMerged.xStart){
									CellRangeAddress cra = new CellRangeAddress(unMerged.xStart, unMerged.xEnd, unMerged.x, unMerged.x);
									sheet.addMergedRegion(cra);
//									sheet.mergeCells(unMerged.x, unMerged.xStart, unMerged.x, unMerged.xEnd);
								}
								//填入值
								cell = row.createCell(i);
								cell.getCellStyle().setAlignment(HSSFCellStyle.ALIGN_CENTER);
								cell.setCellValue(content);
//								cell = new Label(unMerged.x, unMerged.xStart, unMerged.content, CENTER_ALIGN_FORMAT);
//								sheet.addCell(cell);
								//记录新的节点
								mergedCells.add(new MergedCell(i, lineNum, lineNum, content));
							}
						}
					}else{
						cell = row.createCell(i);
						if(col.getType() == VARCHAR){
//							cell = new Label(i, lineNum, content, wcfTEXT);
//							sheet.addCell(cell);
							cell.setCellValue(content);
						} else if (col.getType() == INTEGER) {
//							Number number = new Number(i, lineNum, (Integer) method.invoke(t));
//							number.setCellFormat(wcfINTEGER);
//							sheet.addCell(number);
							Object value = method.invoke(t);
							if(value != null){
								cell.setCellValue(Integer.valueOf(value.toString()));
							}
						} else if (col.getType() == FLOAT) {
							Object obj = method.invoke(t);
							if(obj != null){
								double O = Double.parseDouble(String.valueOf(obj));
//								Number number = new Number(i, lineNum, O, wcfFLOAT);
								cell.setCellValue(O);
							}
						} else if (col.getType() == DOUBLE) {
							Object obj = method.invoke(t);
							if(obj != null){
//								double O = Double.parseDouble(String.valueOf(obj));
//								Number number = new Number(i, lineNum, O, wcfFLOAT);
								BigDecimal bd = new BigDecimal(String.valueOf(obj));
								double doubleVal = ((BigDecimal) bd).doubleValue(); 
								cell.setCellStyle(style_bigDecimal);
								cell.setCellValue(doubleVal);
							}
						} else if (col.getType() == BIGINT) {
							Object obj = method.invoke(t);
							if(obj != null){
								Long value = Long.class.cast(obj);
//								Number number = new Number(i, lineNum, value, wcfINTEGER);
//								sheet.addCell(number);
								cell.setCellValue(value);
							}
						} else if(col.getType() == TIMESTAMP){
//							sheet.setColumnWidth(i, content.getBytes().length/2+4);//根据内容自动设置列宽
							Object o = method.invoke(t);
							Date dd = null;
							Calendar cal = Calendar.getInstance();
							if (o instanceof String){
								if (StringUtils.isEmpty(o)){
									cell.setCellValue(content);
								} else {
									dd = DATA_FORMAT_HH.parse((String) o);
									cal.setTime(dd);
									cell.setCellValue(cal);
									cell.setCellStyle(style_time);
								}
							} else {
								if (o != null) {
									cal.setTime(Date.class.cast(o));
									cell.setCellValue(cal);
									cell.setCellStyle(style_time);
								} else {
									cell.setCellValue(content);
								}
							}
						} else if(col.getType() == DATE){
							Object o = method.invoke(t);
							Calendar cal = Calendar.getInstance();
							try{
								if (o instanceof String){
									if (StringUtils.isEmpty(o)){
										cell.setCellValue(content);
									} else {
										try {
											cal.setTime(DATA_FORMAT_DD.parse(content));
										} catch (Exception e) {
											LOGGER.error("t类型为---"+t.getClass().getName());
											LOGGER.error("改行数据为：---"+JsonHelper2.toJson(t), e);
										}
										
										cell.setCellValue(cal);
										cell.setCellStyle(style_date);
									}
								} else {
									if (o != null) {
//										cal.setTime(Date.class.cast(o));
//										cell.setCellValue(cal);
//										cell.setCellStyle(style_date);
										try {
											cal.setTime(DATA_FORMAT_DD.parse(content));
										} catch (Exception e) {
											LOGGER.error("t类型为---"+t.getClass().getName());
											LOGGER.error("改行数据为：---"+JsonHelper2.toJson(t), e);
										}
										
										cell.setCellValue(cal);
										cell.setCellStyle(style_date);
									} else {
										cell.setCellValue(content);
									}
								}
//								cal.setTime(DATA_FORMAT_DD.parse(content));
//								cell.setCellValue(cal);
//								cell.setCellStyle(style_date);
							}catch(Exception e){
								LOGGER.info("[" + content + "]无法被转化成时间格式，[" + cell.getRowIndex() + "," + cell.getColumnIndex() + "]单元格被抛弃。");
							}
						}
					}
					col.logContent(content);
					} catch (Exception e) {
						LOGGER.info("key--:"+col.getKey());
						LOGGER.info("type--:"+col.getType());
						LOGGER.info("content--:"+content);
					}
				}
					
				lineNum ++;
			}
			if(!mergedCells.isEmpty()){
				for (MergedCell mergedCell : mergedCells) {
					if(mergedCell.xEnd > mergedCell.xStart){
						CellRangeAddress cra = new CellRangeAddress(mergedCell.xStart, mergedCell.xEnd, mergedCell.x, mergedCell.x);
						sheet.addMergedRegion(cra);
					}
//					Cell cell = row.createCell(mergedCell.x);
					//填入值
//					cell.setCellValue(content);
//					cell = new Label(mergedCell.x, mergedCell.xStart, mergedCell.content, CENTER_ALIGN_FORMAT);
//					sheet.addCell(cell);
//					cell.getCellStyle().setAlignment(HSSFCellStyle.ALIGN_CENTER);
//					cell.setCellValue(mergedCell.content);
				}
			}
			if(callback != null){
				callback.afterWrote(workbook, sheet, lineNum, cols.size());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			try {
				workbook.write(new FileOutputStream(out));
//				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out;
	}
	
	/**
	 * 找出上个节点
	 * @param cells
	 * @param x
	 * @param xEnd
	 * @return
	 */
	private MergedCell findMergedCell(List<MergedCell> cells, int x, int xEnd, boolean remove){
		MergedCell mergedCell = null;
		for (int i = 0; i < cells.size(); i ++) {
			mergedCell = cells.get(i);
			if(mergedCell.x == x && mergedCell.xEnd == xEnd){
				if(remove){
					cells.remove(i);
				}
				return mergedCell;
			}
		}
		return null;
	}
	
	/**
	 * 该类表示一个需要合并的单元格信息
	 * @author dujingjing
	 *
	 */
	private class MergedCell{
		
		//列号
		private int x;
		
		private int xStart;
	
		//Y轴偏移量
		private int xEnd;
		
		//内容
		@SuppressWarnings("unused")
		private String content;
		
		MergedCell(int x, int xStart, int xEnd, String content){
			this.x = x;
			this.xStart = xStart;
			this.xEnd = xEnd;
			this.content = content;
		}
		
		void goDown(){
			this.xEnd ++;
		}
	}

	@Override
	public String getReportName() {
		return this.reportName.split(".")[0] + ".xls";
	}
}
