package com.weyao.srv.task.branch;

import java.util.concurrent.CountDownLatch;

/**
 * 任务分支
 * @author dujingjing
 *
 */
public interface TaskBranch extends Runnable{

	/**
	 * 处理分支任务
	 * @param queue
	 */
	public void dealBranch() throws Exception;
	
	/**
	 * 获取相对的key
	 * @return
	 */
	public String getKey();
	
	/**
	 * 加载倒计时
	 */
	public void loadComponent(CountDownLatch latch, Integer businessHour, Integer hour, boolean compress, String serviceName);
	
	/**
	 * 加载倒计时
	 */
	public void loadComponent(CountDownLatch latch, Integer businessHour, Integer hour, boolean ignore, boolean compress, String serviceName);
}
