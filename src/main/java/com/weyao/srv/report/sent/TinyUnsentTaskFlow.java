package com.weyao.srv.report.sent;

import java.util.Collection;
import java.util.Date;

/**
 * 回调接口
 * @author dujingjing
 *
 */
public interface TinyUnsentTaskFlow {

	/**
	 * 返回需要发送的附件列表
	 * @return
	 */
	public Collection<UnsentFile> getUnsentList();

	/**
	 * 获取任务对应的key
	 * @return
	 */
	public String getKey();
	
	/**
	 * 返回该任务创建的时间
	 * @return
	 */
	public Date getCreateDate();
	
	/**
	 * 发生异常
	 */
	public void error();
	
	/**
	 * 处理结束
	 */
	public void finish();
}
