package com.weyao.srv.task.branch;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.weyao.exception.SrvException;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.srv.report.entity.DailyBeeActive;

/**
 * 小蜜蜂报表数据分支任务
 * @author dujingjing
 *
 */
@Component
public class DailyBeeActivitiTaskBranch extends AbstractTaskBrahch<DailyBeeActive>{

	public DailyBeeActivitiTaskBranch() {
		super("daily_bee_activiti");
	}

	private final static String DAILY_BEE_ACTIVITI_SQL = "SELECT MAX(timeline.record_date) AS recordDate, 'Overall' AS type, 'Overall' AS area, count(1) AS totalBeeCount, count(timeline.is_new) AS newBeeCount, count(timeline.is_daya) AS dayActive, count(timeline.is_weeka) AS weekActive, count(timeline.is_montha) AS monthActive, count(timeline.is_daya)/count(1) AS active, count(timeline.is_lost) AS lostBeeCount, count(timeline.is_recover) AS recoverBeeCount FROM t_bee_timeline timeline UNION ALL SELECT MAX(timeline.record_date) AS recordDate, '注册类型' AS type, CASE timeline.from_source WHEN 0 THEN '又一单' WHEN 1 THEN '经纪人' ELSE NULL END AS area, count(1) AS totalBeeCount, count(timeline.is_new) AS newBeeCount, count(timeline.is_daya) AS dayActive, count(timeline.is_weeka) AS weekActive, count(timeline.is_montha) AS monthActive, count(timeline.is_daya)/count(1) AS active, count(timeline.is_lost) AS lostBeeCount, count(timeline.is_recover) AS recoverBeeCount FROM t_bee_timeline timeline GROUP BY timeline.from_source UNION ALL SELECT MAX(timeline.record_date) AS recordDate, city.`name` AS type, CASE timeline.bee_type WHEN 1 THEN '综合门店' WHEN 2 THEN '代理人' WHEN 3 THEN '快修美容店' WHEN 4 THEN '修理厂' WHEN 5 THEN '内部' WHEN 6 THEN '待分配' END AS area, count(1) AS totalBeeCount, count(timeline.is_new) AS newBeeCount, count(timeline.is_daya) AS dayActive, count(timeline.is_weeka) AS weekActive, count(timeline.is_montha) AS monthActive, count(timeline.is_daya)/count(1) AS active, count(timeline.is_lost) AS lostBeeCount, count(timeline.is_recover) AS recoverBeeCount FROM t_bee_timeline timeline, t_city city WHERE timeline.city_id=city.city_id GROUP BY timeline.city_id, timeline.bee_type UNION ALL SELECT MAX(timeline.record_date) AS recordDate, city.`name` AS type, area.`name` AS area, count(1) AS totalBeeCount, count(timeline.is_new) AS newBeeCount, count(timeline.is_daya) AS dayActive, count(timeline.is_weeka) AS weekActive, count(timeline.is_montha) AS monthActive, count(timeline.is_daya)/count(1) AS active, count(timeline.is_lost) AS lostBeeCount, count(timeline.is_recover) AS recoverBeeCount FROM t_bee_timeline timeline, t_city city, t_area area WHERE timeline.city_id=city.city_id AND timeline.county_id=area.area_id GROUP BY timeline.city_id, timeline.county_id UNION ALL SELECT line_record.record_date AS recordDate, '报价来源' AS type, CASE line_record.calc_from WHEN 0 THEN '又一单' WHEN 1 THEN '经纪人' END AS area, CASE line_record.calc_from WHEN 0 THEN (SELECT count(1) FROM t_bee_member_rele tbmr where tbmr.first_login_time IS NOT NULL) WHEN 1 THEN (SELECT (SELECT count(1) from t_bee_member_rele) - (SELECT count(1) FROM (SELECT r.cid AS calc_from_0 FROM t_bee_calc_record r GROUP BY r.cid HAVING count(IF(r.calc_from=1, 1, NULL))=0) AS a)) END AS totalBeeCount, CASE line_record.calc_from WHEN 0 THEN (SELECT count(1) FROM t_bee_member_rele tbmr where DATE(tbmr.first_login_time)=DATE_SUB(CURDATE(), INTERVAL 1 DAY)) WHEN 1 THEN (SELECT count(1) FROM (SELECT r.cid FROM t_bee_calc_record r GROUP BY r.cid HAVING MIN(DATE(r.create_time))=DATE_SUB(CURDATE(), INTERVAL 1 DAY)) AS a) END AS newBeeCount, count(line_record.is_calc_daya) AS dayActive, count(line_record.is_calc_weeka) AS weekActive, count(line_record.is_calc_montha) AS monthActive, (count(line_record.is_calc_daya)/(CASE line_record.calc_from WHEN 0 THEN (SELECT count(1) FROM t_bee_member_rele tbmr where tbmr.first_login_time IS NOT NULL) WHEN 1 THEN (SELECT (SELECT count(1) from t_bee_member_rele) - (SELECT count(1) FROM (SELECT r.cid AS calc_from_0 FROM t_bee_calc_record r GROUP BY r.cid HAVING count(IF(r.calc_from=1, 1, NULL))=0) AS a)) END)) AS active, '/' AS lostBeeCount, '/' AS recoverBeeCount FROM(SELECT record.cid, record.calc_from, DATE_SUB(CURDATE(), INTERVAL 1 DAY) AS record_date, IF(count(IF(DATE(record.create_time)=DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, NULL))=0, NULL, 1) AS is_calc_daya, IF(count(CASE WHEN DATE(record.create_time)>=DATE_SUB(CURDATE(), INTERVAL WEEKDAY(DATE_SUB(CURDATE(), INTERVAL 1 DAY)) + 1 DAY) AND DATE(record.create_time)<=DATE_ADD(CURDATE(), INTERVAL 5 - WEEKDAY(DATE_SUB(CURDATE(), INTERVAL 1 DAY)) DAY) THEN 1 ELSE NULL END)>0, 1, 	NULL) AS is_calc_weeka, IF(count(CASE WHEN DATE(record.create_time)>=DATE_ADD(CURDATE(), INTERVAL -DAY(curdate())+1 day) AND DATE(record.create_time)<=LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 DAY)) THEN 1 ELSE NULL END)>0, 1, NULL) AS is_calc_montha FROM t_bee_calc_record record GROUP BY record.cid, record.calc_from) line_record GROUP BY line_record.calc_from";
	
	@Override
	protected Map<String, List<? extends Object>> export() throws SrvException {
		Map<String, List<? extends Object>> map = Maps.newHashMap();
//		map.put(new FileTask(DEFAULT, "小蜜蜂活跃度报表%s.xls"), super.proxyQuery(DAILY_BEE_ACTIVITI_SQL, ResultType.LIST, null, DailyBeeActive.class));
		map.put("daily_bee_activiti", super.proxyQuery(DAILY_BEE_ACTIVITI_SQL, ResultType.LIST, null, DailyBeeActive.class));
		return map;
	}
}
