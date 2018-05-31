package com.weyao.srv.task;

import com.weyao.srv.BaseTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SaleLogTaskTest extends BaseTask{

	@Autowired
	private SaleLogTask saleLogTask;
	
	@Test
	public void doTaskTest(){
		saleLogTask.handleBiz(null);
	}
}
