package com.weyao.srv.task;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.BaseTask;



public class BeePayRemindTaskTest extends BaseTask{

	@Autowired
	private BeePayRemindTask beePayRemindTask;
	
	@Test
	public void doTaskTest(){
		beePayRemindTask.handleBiz(null);
	}
}
