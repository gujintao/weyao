package com.weyao.srv.task.monitor;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import io.netty.util.concurrent.Future;

/**
 * 用来监视每一个任务分支的监视器线程
 * @author dujingjing
 *
 */
public final class BranchMonitor implements Callable<Integer>{
	
	private Future<Integer> future;
	
	private CountDownLatch latch;
	
	public BranchMonitor(Future<Integer> future, CountDownLatch latch){
		this.future = future;
		this.latch = latch;
	}
	
	@Override
	public Integer call() throws Exception {
		int result = this.future.get();
		this.latch.countDown();
		return result;
	}
}
