package com.weyao.srv.task;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.BaseTask;


public class IndicatorPersistTaskTest extends BaseTask{

	@Autowired
	private IndicatorPersistTask indicatorPersistTask;
	
	@Test
	public void doTaskTest(){
		indicatorPersistTask.handleBiz(null);
	}
}
