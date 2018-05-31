package com.weyao.srv.task;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.BaseTask;


public class OrderPaymentExpiredTaskTest extends BaseTask{

	@Autowired
	private OrderPaymentExpiredTask orderPaymentExpiredTask;
	
	@Test
	public void doTaskTest(){
		orderPaymentExpiredTask.handleBiz(null);
	}
}
