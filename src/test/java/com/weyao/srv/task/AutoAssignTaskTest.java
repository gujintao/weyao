package com.weyao.srv.task;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.weyao.srv.BaseTask;


public class AutoAssignTaskTest extends BaseTask{

	@Autowired
	private AutoAssignTask autoAssignTask;
	
	@Test
	public void doTaskTest(){
		autoAssignTask.handleBiz(null);
	}
}
