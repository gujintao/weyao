package com.weyao.srv;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:config/spring/spring-main.xml"
})
//@TransactionConfiguration(defaultRollback=true) //默认事务回滚
public class BaseTask {
	
}
