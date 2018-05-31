package com.weyao.srv.constant;

/**
 * @ClassName: RedisKey
 * @Description: redis的key
 * @author Tao xiaoyan
 * @date 2017年8月1日 上午11:28:24
 * @version [1.0, 2017年8月1日]
 * @since version 1.0
 */
public class RedisKey {

	public static final String 每日新建订单数KEY = "BIG_SCREEN_NEW_ORDER_%s";// 大屏交互量中的新建订单数量的key
	public static final String 每日完成订单数KEY = "BIG_SCREEN_FINISH_ORDER_%s"; // 大屏交互量中的完成订单数量的key
	public static final String 每日算价规模KEY = "BIG_SCREEN_CALC_AMOUNT_%s"; // 大屏每日算价规模的key
	public static final String 每日算价次数KEY = "BIG_SCREEN_CALC_TIMES_%s"; // 大屏每日算价次数的key
	public static final String 每日又一单算价次数KEY = "BIG_SCREEN_YYD_CALC_TIMES_%s"; // 大屏又一单每日算价次数的key
	public static final String 每日经管算价次数KEY = "BIG_SCREEN_JG_CALC_TIMES_%s"; // 大屏经管每日算价次数的key
	public static final String 每日量子算价次数KEY = "BIG_SCREEN_CRM_CALC_TIMES_%s"; // 大屏量子每日算价次数的key
	public static final String 每日各阶段算价请求次数KEY = "BIG_SCREEN_CALC_REQUEST_TIMES_%s_%s"; // 大屏量子每日各阶段算价请求次数的key
	public static final String 每日服务车主KEY = "BIG_SCREEN_CALC_CARS_%s"; // 大屏每日服务车主的key
	

}
