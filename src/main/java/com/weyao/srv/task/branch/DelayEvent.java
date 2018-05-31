package com.weyao.srv.task.branch;

/**
 * 延迟时间接口
 * @author dujingjing
 *
 */
public interface DelayEvent {

	/**
	 * 延迟事件，延迟操作一般都在此完成，不再对外暴露
	 * @throws InterruptedException
	 * @throws Exception 
	 */
	public boolean delay() throws Exception;
	
	/**
	 * 获取该任务指定触发时刻
	 * @return
	 */
	public int getSpecificHour();
}
