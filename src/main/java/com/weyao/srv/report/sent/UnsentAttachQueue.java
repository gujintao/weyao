package com.weyao.srv.report.sent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weyao.srv.task.branch.mail.MetaMailPkg;
import com.weyao.srv.task.branch.mail.ReportMailTask;

/**
 * 用来存放待发送邮件附件的名称列表
 */
public final class UnsentAttachQueue implements UnsentQueue{
	
	private Logger logger = LoggerFactory.getLogger(UnsentQueue.class);
	
	private final static UnsentAttachQueue INSTANCE = new UnsentAttachQueue();
	
	public static UnsentAttachQueue getInstance(){
		return INSTANCE;
	}
	
	/*
	 * 使用线程安全的ArrayBlockingQueue来充当任务队列 
	 */
//	private BlockingQueue<UnsentTaskDescription> queue = new PriorityBlockingQueue<UnsentTaskDescription>();
	private BlockingQueue<UnsentTaskDescription> queue = new LinkedBlockingQueue<UnsentTaskDescription>();
	
	/**
	 * 往阻塞队列中添加新任务
	 * @param fileNames
	 * @return 添加成功后，返回当前阻塞队列中已有的任务数量
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public int addTask(String key, MetaMailPkg pkg, boolean compress, UnsentFile... unsentFile) throws InterruptedException{
		if(unsentFile == null){
			return 0;
		}
		new UnsentTaskDescription(key, unsentFile);
		new ReportMailTask(pkg).action(compress);
		return queue.size();
	}
	
	/**
	 * 返回是否还有剩余的任务
	 * @return
	 */
	public boolean hasMoreTask(){
		return this.queue.size() > 0;
	}
	
	/**
	 * 从缓存队列中取出数据
	 * @param callback
	 * @return
	 */
	public TinyUnsentTaskFlow getTask(){
		//获取待发送邮件附件列表任务
		final UnsentTaskDescription task = queue.peek();
		
		return new TinyUnsentTaskFlow() {
			
			@Override
			public Collection<UnsentFile> getUnsentList() {
				return Collections.unmodifiableCollection(task.list);
			}
			
			@Override
			public void error() {
				logger.error("任务处理失败，" + task);
			}
			
			@Override
			public void finish() {
				if(!queue.remove(task)){
					logger.error("任务清除失败，" + task);
				}
			}
			
			@Override
			public Date getCreateDate(){
				return new Date(task.createDate.getTime());
			}

			@Override
			public String getKey() {
				return task.key;
			}
		};
	}
	
	/**
	 * 用来描述待发送信息
	 */
	private class UnsentTaskDescription /*implements Comparable<UnsentTaskDescription>*/{
		
		private String key;
		
		UnsentTaskDescription(String key, UnsentFile... unsentFiles) throws InterruptedException{
			for (UnsentFile fileName : unsentFiles) {
				this.list.add(fileName);
				list.trimToSize();
			}
			this.key = key;
			this.createDate = new Date();
			UnsentAttachQueue.this.queue.put(this);
		}
		
		//附件名称缓存
		private ArrayList<UnsentFile> list = new ArrayList<UnsentFile>();
	
		//任务创建时间
		private Date createDate;
		
		@Override
		public boolean equals(Object obj) {
			if(obj == this){
				return true;
			}
			UnsentTaskDescription other = (UnsentTaskDescription)obj;
			if(other.list.equals(this.list) && other.createDate.equals(this.createDate)){
				return true;
			}
			return false;
		}

//		@Override
//		public int compareTo(UnsentTaskDescription o) {
//			return this.event.getSpecificHour() > o.event.getSpecificHour() ? 1 : 
//				(this.event.getSpecificHour() == o.event.getSpecificHour() ? 0 : -1);
//		}
	}
}
