package com.weyao.srv.report.sent;

import java.io.IOException;

import com.weyao.srv.task.branch.mail.MetaMailPkg;

/**
 * 存储待发送邮件列表任务的队列工具接口
 * @author dujingjing
 *
 */
public interface UnsentQueue {

	/**
	 * 往阻塞队列中添加新任务
	 * @param fileNames
	 * @return 添加成功后，返回当前阻塞队列中已有的任务数量
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public int addTask(String key, MetaMailPkg pkg, boolean compress, UnsentFile... unsentFile) throws InterruptedException;
	
	/**
	 * 返回是否还有剩余的任务
	 * @return
	 */
	public boolean hasMoreTask();
	
	/**
	 * 从缓存队列中取出数据
	 * @param callback
	 * @return
	 */
	public TinyUnsentTaskFlow getTask();
}
