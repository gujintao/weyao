package com.weyao.srv.document;

import java.io.File;
import java.util.List;

import com.weyao.srv.document.Template;

/**
 * 用来表示模板引擎操作，该接口对外提供不同类型的模板
 * 目前支持两种：
 * 1.Excel模板
 * 2.Pdf模板
 * 根据用户选择的不同，加载不同的模板
 * @author dujingjing
 *
 */
public interface DocumentService<T> {

	/**
	 * 文档服务的空对象
	 */
	public static final DocumentService<?> NULL = new Null<Object>(); 
	
	/**
	 * 装载一个静态模板，该静态模板中包含具体的，定义表头等信息的元数据
	 * @param template
	 * @return
	 */
	public boolean createDocument(Template<T> template);
	
	/**
	 * 往文档中填写数据
	 * @param source
	 * @throws Exception
	 */
	public File fillBlank(List<T> source) throws Exception;
	
	public String getReportName();
	
	class Null<T> implements DocumentService<T>{

		private String msg = "空对象不支持任何操作";
		
		@Override
		public boolean createDocument(Template<T> template) {
			throw new UnsupportedOperationException(msg);
		}

		@Override
		public File fillBlank(List<T> source) throws Exception {
			throw new UnsupportedOperationException(msg);
		}

		@Override
		public String getReportName() {
			throw new UnsupportedOperationException(msg);
		}
		
	}
}
