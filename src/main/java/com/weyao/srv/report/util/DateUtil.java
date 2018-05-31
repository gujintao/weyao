package com.weyao.srv.report.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.weyao.common.Vacations;

/**
 * 日期工具类
 * 
 * @version 1.0
 * @author taoxiaoyan
 * @date 创建时间：2016年10月27日 上午9:40:59
 */
public class DateUtil {

	/**
	 * 计算n个工作日后的日期是 定义一个int变量，还是循环每一天，如果是工作日，变量加1，如果这个变量跟n相等就返回这个日期
	 * 
	 * @param src
	 *            日期(源)
	 * @param adddays
	 *            要加的天数
	 */
	public static Date addDateByWorkDay(Date src, int adddays) {
		Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		c2.setTime(src);

		boolean workDayFlag = false;
		int a = 0;
		workDayFlag = Vacations.上班(c2);
		if (workDayFlag) {
			a++;
		}
		for (int i = a; i <= adddays;) {
			// 把源日期加一天
			c2.add(Calendar.DAY_OF_MONTH, 1);
			workDayFlag = Vacations.上班(c2);
			if (workDayFlag) {
				i++;
			}
		}
		return c2.getTime();
	}

	/**
	 * 计算n个工作日前的日期是 定义一个int变量，还是循环每一天，如果是工作日，变量减1，如果这个变量跟n相等就返回这个日期
	 * 
	 * @param src
	 *            日期(源)
	 * @param subdays
	 *            要减的天数
	 */
	public static Date substractDateByWorkDay(Date src, int subdays) {
		Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		c2.setTime(src);

		boolean workDayFlag = false;
		for (int i = subdays; i < 0;) {// 把源日期加一天
			c2.add(Calendar.DAY_OF_MONTH, -1);
			workDayFlag = Vacations.上班(c2);
			if (workDayFlag) {
				i++;
				continue;
			}

		}
		return c2.getTime();
	}

	/**
	 * 判断当前日期是星期几<br>
	 * <br>
	 * 
	 * @param pTime
	 *            修要判断的时间<br>
	 * @return dayForWeek 判断结果<br>
	 * @Exception 发生异常<br>
	 */
	public static int dayForWeek(Date pTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(pTime);
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}

	/**
	 * 获取某月的最后一天 
	 * @Title:getLastDayOfMonth 
	 * @Description: 
	 * @param:@param year 
	 * @param:@param month 
	 * @param:@return 
	 * @return:Date
	 */
	public static Date getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);

		return cal.getTime();
	}
}
