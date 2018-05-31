package com.weyao.srv.document;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel文件生成的回调函数，对于一些特定的报表数据，
 * 将通过该回调进行类似于单元格合并之类的操作
 * @author dujingjing
 */
public interface CallBack {

	/**
	 * 当数据写完之后，进行回调
	 */
	public void afterWrote(XSSFWorkbook workbook, XSSFSheet sheet, int lineNum, int colNum) throws Exception;
}
