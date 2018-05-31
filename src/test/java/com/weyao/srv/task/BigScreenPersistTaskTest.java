package com.weyao.srv.task;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.BaseTask;


public class BigScreenPersistTaskTest extends BaseTask{

	@Autowired
	private BigScreenPersistTask bigScreenPersistTask;
	
	@Test
	public void doTaskTest(){
		bigScreenPersistTask.handleBiz(null);
	}
}
