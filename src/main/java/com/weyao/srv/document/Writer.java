package com.weyao.srv.document;

import java.io.File;
import java.util.List;

/**
 * Excel可写模板的功能接口
 * @author dujingjing
 *
 */
public interface Writer<T> {

	/**
	 * 将数据写入到Excel中，并导出其二进制数据
	 * @param source
	 * @return
	 * @throws Exception 
	 */
	public File[] fillBlank(List<T> source) throws Exception;
	
	/**
	 * 获取报表名称
	 * @return
	 */
	public String getReportName();
}
