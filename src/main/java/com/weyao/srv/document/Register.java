package com.weyao.srv.document;

/**
 * Excel模板注册功能接口
 * @author dujingjing
 *
 */
public interface Register {

	/**
	 * 定义一个模板模型
	 * @param target 指定Class
	 */
	public <T> Template<T> defineTemplate(String templateKey, String reportName, Class<T> target);
	
	/**
	 * 取回一个可写模板
	 * @param target 指定Class
	 */
	public <T> Writer<T> tryToGetTemplate(String templateKey, String serviceName);
	
	/**
	 * 取回一个可写模板
	 * @param target 指定Class
	 */
	public <T> Writer<T> tryToGetTemplate(String templateKey, String serviceName, CallBack callback);
}
