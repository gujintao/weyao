-- delete from t_st_insurance_based;
-- 13.0版本 拓展员字段抽取错误修正
-- 保单数据抽取存储过程
DROP PROCEDURE IF EXISTS `st_insurance_based_data_proc`;
CREATE PROCEDURE `st_insurance_based_data_proc`(IN `pTime` timestamp,IN `endTime` timestamp)
BEGIN
	
	-- 查询待垫付状态之后的保单的订单id
	-- DECLARE afterPaymentStatus INT(11) DEFAULT 6;     
	DECLARE createStatus INT(11) DEFAULT -1;
	DECLARE done INT(11) DEFAULT -1;
	DECLARE validStatus INT(11) DEFAULT 0;
	DECLARE tifCreateTime TIMESTAMP; 		-- 当前状态的创建日期
	DECLARE orderIdNum INT(11) DEFAULT 0;
	DECLARE sourceType INT(11) DEFAULT NULL; 	-- 订单来源（100、电销；200、网销；300、经纪人；400、又一单），字典表type=10018
	DECLARE v_count INT(11);
	
	DECLARE insuranceId BIGINT(20);
	DECLARE relationInsuranceId BIGINT(20);
	DECLARE orderId BIGINT;
	DECLARE itemId BIGINT(20);
	DECLARE var_ptype INT(11); 					-- 产品类型 
	DECLARE createOpGroupName VARCHAR(100);		-- 保单创建人所属团队名称 
	DECLARE createOpId INT(11);					-- 保单创建人ID
	DECLARE insuranceCreateTime TIMESTAMP; 		-- 保单创建日期
	DECLARE couponAmount INT(11);				-- 优惠券优惠金额
	DECLARE itemRealAmount INT(11);				-- 优惠券优惠金额
	DECLARE taxFee INT(11);						-- 车船税，分为单位
	DECLARE sycComputeId BIGINT(20);			-- 算价ID 
	DECLARE activityId INT(11);					-- 活动id
	DECLARE activityName VARCHAR(100);			-- 活动名称
	DECLARE carId BIGINT(20);					-- 车辆id 
	DECLARE var_cid BIGINT(20);					-- 归属的客户ID 
	DECLARE sourceBatchId INT(11);				-- 来源批次
	DECLARE channelId INT(11);					-- 渠道
	DECLARE channelType INT(11);				-- 渠道类型
	DECLARE carCityId INT(11) DEFAULT 0;		-- 车辆所属城市id
	DECLARE carCityName VARCHAR(20);			-- 车辆所属城市名称
	DECLARE carCreateTime TIMESTAMP; 			-- 保单创建日期
	DECLARE var_mobile VARCHAR(15);					-- 客户电话
	DECLARE customerCreateTime TIMESTAMP; 		-- 客户创建日期
	DECLARE customerCityId INT(11) DEFAULT 0;	-- 客户城市
	DECLARE beeOpId INT(11); 					-- 拓展员ID
	DECLARE beeCityId INT(11); 					-- 小蜜蜂城市ID
	DECLARE countyId INT(11); 					-- 小蜜蜂所属区/县
	DECLARE beeType INT(11); 					-- 小蜜蜂类型
	DECLARE beeFromSource tinyint(4); 			-- 经纪人来源；0：系统，1：又一单，2：导入
	DECLARE beeCreateTime TIMESTAMP; 			-- 小蜜蜂创建时间
	DECLARE insuranceCityId INT(11) DEFAULT 0;	-- 投保城市id
	DECLARE insuranceCityName VARCHAR(20);		-- 投保城市名称
	DECLARE carNumber VARCHAR(25);				-- 车牌号
	DECLARE insuranceStartDate TIMESTAMP; 		-- 保单起保日期 
	DECLARE var_pid INT(11); 						-- 产品ID
	DECLARE	supplierId INT(11);					-- 供应商id
	DECLARE supplierName VARCHAR(30);			-- 供应商名称
	DECLARE guadanId INT(11); 					-- 挂单公司ID
	DECLARE insuranceReviewedAmount INT(11) DEFAULT '0'; 	-- 保险公司核后金额
	DECLARE currentStatus INT(11); 				-- 当前状态
	DECLARE preStatus INT(11); 					-- 当前状态的上一个状态
	DECLARE couponId BIGINT(20);				-- 券ID 
	DECLARE moneyType INT(11); 					-- 面值类型
	DECLARE actualAmount INT(11); 				-- 实际抵扣金额；单位：分
	DECLARE firstUnderwriterTime TIMESTAMP; 	-- 第一次核保通过时间 
	DECLARE nowUnderwriterTime TIMESTAMP; 		-- 当前核保通过时间 
	DECLARE firstCheckTime TIMESTAMP; 			-- 第一次确认通过时间 
	DECLARE nowCheckTime TIMESTAMP; 			-- 当前确认通过时间  
	DECLARE firstPrintNoticeTime TIMESTAMP; 	-- 第一次完成打印付费通知单时间 
	DECLARE nowPrintNoticeTime TIMESTAMP; 		-- 当前完成打印付费通知单时间 
	DECLARE firstRecheckTime TIMESTAMP; 		-- 第一次复核通过时间 
	DECLARE nowRecheckTime TIMESTAMP; 			-- 当前复核通过时间 
	DECLARE firstPendingApplyTime TIMESTAMP; 	-- 第一次待垫付申请通过时间 
	DECLARE nowPendingApplyTime TIMESTAMP; 		-- 当前待垫付申请通过时间  
	DECLARE firstPaymentTime TIMESTAMP; 		-- 第一次垫付完成时间 
	DECLARE nowPaymentTime TIMESTAMP; 			-- 当前垫付完成时间 
	DECLARE printOrderTime TIMESTAMP; 			-- 打单完成时间  旧流程的垫付完成  
	DECLARE firstFiledTime TIMESTAMP; 			-- 第一次存档完成时间 
	DECLARE nowFiledTime TIMESTAMP; 			-- 当前存档完成时间 
	DECLARE firstDispatchTime TIMESTAMP; 		-- 第一次调度完成时间 
	DECLARE nowDispatchTime TIMESTAMP; 			-- 当前调度完成时间 
	DECLARE firstDeliveryTime TIMESTAMP; 		-- 第一次配送完成时间 
	DECLARE nowDeliveryTime TIMESTAMP; 			-- 当前配送完成时间 
	DECLARE firstFinishTime TIMESTAMP; 			-- 第一次收款完成时间 
	DECLARE nowFinishTime TIMESTAMP; 			-- 当前收款完成时间 
	DECLARE firstCancleTime TIMESTAMP; 			-- 第一次取消或退保时间 
	DECLARE nowCancleTime TIMESTAMP; 			-- 当前取消或退保时间 
	DECLARE insurancedPersonName varchar(100);  -- 被保人姓名
	DECLARE insuranceCompanyFid varchar(50); 	-- 保险公司投保单ID
	DECLARE insuranceEndDate datetime; 		    -- 保单结束日期  
	DECLARE itemAmount int(11);					-- 订单子项原金额
	DECLARE orderAdvanceAmount int(11);			-- 订单垫付金额
	DECLARE orderRealAmount int(11);			-- 订单实际总金额
	DECLARE orderReceiveAmount int(11);			-- 订单实收金额
	DECLARE orderComments varchar(300);			-- 订单促销礼品,备注
	DECLARE insuranceReward BIGINT(20);			-- 追加奖励金额 
	DECLARE orderPayStyle int(11);				-- 订单付款方式 
	DECLARE orderPaymentTime TIMESTAMP; 		-- 订单线上支付时间
	DECLARE oemName varchar(50);				-- oem名称
	DECLARE oemPaymentMode tinyint(4);			-- 结算方式 
	DECLARE commissionAmount int(11);			-- 手续费应收金额  
	DECLARE ruleFormula varchar(100);			-- 结算公式  
	DECLARE commissionRate varchar(10);			-- 结算费率   
	DECLARE oemCode varchar(10);				-- OEM代号   
	DECLARE oemAgencyName varchar(50);			-- 分支机构 
	DECLARE orderOriginalTotalAmount int(11);	-- 订单实际总金额  
	DECLARE orderOtherAmount int(11);			-- 订单其他产品金额  
	DECLARE insuranceOperationsAmount int(11);	-- 运营策略折扣金额 
	DECLARE insuranceOperationsFormula varchar(100);		-- 运营策略折扣计算公式  
	DECLARE insuranceOperationsPercentage varchar(10); -- 运营策略折扣百分比   
	DECLARE insuranceProductsPayFirstAmount int(11); -- 产品策略先付优惠金额  
	DECLARE expressName varchar(50); -- 物流公司   
	DECLARE deliveryTimes int(4);	-- 配送次数 
	DECLARE insuranceProductsDiscountPercentage FLOAT;	-- 产品打折策略折扣百分比 
	DECLARE itemActivityAmount FLOAT;	-- 活动扣除的金额 
	DECLARE handleRule varchar(2000); -- 产品策略处理规则
	
	 
	
	-- 查询垫付完成、取消或者退保的保单
	DECLARE curInusranceId CURSOR FOR SELECT a.insurance_id FROM t_insurance a,t_insurance_flow_detail b
	WHERE a.insurance_id=b.insurance_id
	and b.create_time >= pTime
	and b.create_time <= endTime
	group by a.insurance_id
	order by a.create_time;
	
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	
	OPEN curInusranceId;

		myLoop:LOOP
		set insuranceId = null;
		FETCH curInusranceId INTO insuranceId;
		IF insuranceId is null THEN
			LEAVE myLoop;
		END IF;
		
		-- 公共信息
		SELECT a.relation_insurance_id,a.order_id,a.item_id,
		a.create_time,a.ptype,a.city_id,a.insurance_reviewed_amount,a.supplier_id,a.supplier_name,
		a.car_number,a.insurance_start_date,a.pid,a.guadan_id,a.status,a.car_id,a.cid,insurance_company_fid,
		insurance_end_date
		INTO relationInsuranceId,orderId,itemId,
		insuranceCreateTime,var_ptype,insuranceCityId,insuranceReviewedAmount,supplierId,supplierName,
		carNumber,insuranceStartDate,var_pid,guadanId,currentStatus,carId,var_cid,insuranceCompanyFid,
		insuranceEndDate
		FROM t_insurance a
		WHERE a.insurance_id=insuranceId;
		
		set supplierName = (select supplier_name from t_supplier where supplier_id = supplierId);
		
		
		-- 保单创建人ID
		SET createOpId = (SELECT e.op_id FROM t_insurance_flow_detail e 
			 WHERE e.insurance_id=insuranceId AND e.insurance_status=createStatus 
			 ORDER BY e.create_time DESC LIMIT 1);
			 
		-- 订单子项信息 订单子项的状态为有效的status = 0
		set couponAmount= null;
		set itemRealAmount= null;
		set activityId= null;
		set itemAmount = null;
		SELECT item.coupon_amount,item.real_amount,item.activity_id,item.amount,item.activity_amount
		into couponAmount,itemRealAmount,activityId,itemAmount,itemActivityAmount
		FROM t_order_item item 
		WHERE item.item_id=itemId and status= validStatus;
		
		-- 车船税信息
		SET taxFee = (SELECT tax.tax_fee FROM t_tax_fee_detail tax 
			 WHERE tax.insurance_id=insuranceId and status = validStatus);
		-- 算价信息
		SET sycComputeId = (SELECT ord.syc_compute_id FROM t_order ord WHERE ord.order_id=orderId);
		
		-- 产品策略活动信息
		SET activityName = (select ac.activity_name from t_activity ac where ac.activity_id=activityId);
		-- 车辆信息
		set v_count = 0;
		set sourceBatchId = null;
		set carCityId = null;
		set carCreateTime = null;
		set channelType = null;
		set channelId = null;
		set carCityName = null;
		select count(*) into v_count FROM t_car c WHERE c.car_id=carId;
		if v_count > 0 then
			SELECT c.source_batch_id,c.city_id,c.create_time
			INTO sourceBatchId,carCityId,carCreateTime
			FROM t_car c WHERE c.car_id=carId;
			
			SET channelType = (SELECT batch.channel_type FROM t_batch batch WHERE batch.batch_id=sourceBatchId); 
			SET channelId = (SELECT batch.channel_id FROM t_batch batch WHERE batch.batch_id=sourceBatchId); 
			set carCityName = (select name from t_city where city_id = carCityId); 
		end if;
		
		-- 客户信息
		set v_count = 0;
		set var_mobile = null;
		set customerCityId = null;
		set customerCreateTime = null;
		select count(*) into v_count FROM t_customer c WHERE c.cid=var_cid;
		if v_count > 0 then
			SELECT c.mobile,c.create_time,c.city_id
			INTO var_mobile,customerCreateTime,customerCityId
			FROM t_customer c WHERE c.cid=var_cid;
		end if;
		
		-- 小蜜蜂专有信息 t_bee_member_rele
		set v_count = 0;
		set beeOpId = null;
		set beeCityId = null;
		set countyId = null;
		set beeType = null;
		set beeFromSource = null;
		set beeCreateTime = null;
		select count(*) into v_count FROM t_bee_member_rele bee WHERE bee.cid=var_cid;
		IF v_count > 0 THEN
			SELECT bee.member_op_id,bee.city_id,bee.county_id,bee.type,bee.from_source,bee.create_time 
			into beeOpId,beeCityId,countyId,beeType,beeFromSource,beeCreateTime 
			FROM t_bee_member_rele bee WHERE bee.cid=var_cid and bee.create_time <= insuranceCreateTime;
		END IF;
		 
		-- 保单信息
		IF ISNULL(insuranceCityId) THEN
			SET insuranceCityId=0;
		END IF;
		set insuranceCityName = (select `name` from t_city where city_id = insuranceCityId);
		
		SET tifCreateTime = (select create_time from t_insurance_flow_detail 
			where insurance_id = insuranceId and insurance_status = currentStatus order by create_time desc limit 1);
			
		SET preStatus = (select insurance_status from t_insurance_flow_detail 
				where insurance_id = insuranceId and create_time < tifCreateTime order by create_time desc limit 1);
		 
		-- 用券情况
		set v_count = 0;
		set couponId = null;
		set moneyType = null;
		set actualAmount = null;
		select count(*) into v_count
		from t_orderitem_coupon_detail a 
			join t_order_item c on a.item_id = c.item_id 
			join t_coupon d on a.coupon_id = d.coupon_id
			where a.order_id = orderId and a.item_id = itemId and c.status = 0 and d.money_type <> 2;
		if v_count > 0 then
			select a.coupon_id,d.money_type,actual_amount into couponId,moneyType,actualAmount
			from t_orderitem_coupon_detail a 
			join t_order_item c on a.item_id = c.item_id 
			join t_coupon d on a.coupon_id = d.coupon_id
			where a.order_id = orderId and a.item_id = itemId and c.status = 0 and d.money_type <> 2;
		end if;
		
		-- 被保人信息
		set insurancedPersonName = (select name from t_insurance_person where type = 2 and insurance_id = insuranceId);
		
		-- 订单信息
		SET sourceType = (SELECT ord.source_type FROM t_order ord WHERE ord.order_id=orderId);
		SET orderAdvanceAmount = (SELECT ord.advance_amount FROM t_order ord WHERE ord.order_id=orderId);
		SET orderRealAmount = (SELECT ord.real_amount FROM t_order ord WHERE ord.order_id=orderId);
		SET orderReceiveAmount = (SELECT ord.receive_amount FROM t_order ord WHERE ord.order_id=orderId);
		SET orderComments = (SELECT ord.comments FROM t_order ord WHERE ord.order_id=orderId);
		SET orderPayStyle = (SELECT ord.pay_style FROM t_order ord WHERE ord.order_id=orderId);
		SET orderPaymentTime = (SELECT tordf.create_time FROM t_order_payment_flow tordf
			WHERE flow_type = 3 and tordf.order_id=orderId order by tordf.create_time desc limit 1);
		SET orderOriginalTotalAmount = (SELECT ord.amount FROM t_order ord WHERE ord.order_id=orderId);
		SET orderOtherAmount = (select sum(amount) from t_order_item where status = 0 and ptype not in(201,200) 
								and order_id = orderId);
		
		-- OEM信息
		set oemName = (select name from t_oem where id = guadanId);
		-- 查某个保单的价格政策，需要查必返对应的价格政策去找结算方式。
		set oemPaymentMode = (select tp.payment_mode from t_commission tc 
			join t_rule tr on tc.rule_id = tr.id
			join t_policy tp on tc.policy_id = tp.id
			where tr.type = 0 AND tc.insurance_id = insuranceId order by tc.create_time desc limit 1);
		set oemCode = (select code from t_oem where id = guadanId);
		set oemAgencyName = (select ta.name from t_commission tc 
			join t_rule tr on tc.rule_id = tr.id
			join t_agency ta on tc.agency_id = ta.id
			where tr.type = 0 AND tc.insurance_id = insuranceId order by tc.create_time desc limit 1);
		-- 新增oem中必返的手续费信息和手续费率 modify by tao at 2017/3/28
		-- 必返的 商业险手续费 商业险结算公式 商业险结算费点  交强险手续费 交强险结算公式 交强险结算费点
		if var_ptype = 200 then
			set commissionAmount = (select amount from t_commission where rule_type = 0 and insurance_id = insuranceId 
					order by create_time desc limit 1);
			set ruleFormula = (select tr.business_formula from t_commission tc join t_rule tr on tc.rule_id = tr.id
				where tc.rule_type = 0 and tc.insurance_id = insuranceId order by tc.create_time desc limit 1);
			if SUBSTR(ruleFormula,9,2) = '10' then 
				set commissionRate = SUBSTR(ruleFormula,20,LENGTH(ruleFormula)-20)/1000000;
			ELSEIF SUBSTR(ruleFormula,9,1) = '0' then
				set commissionRate = SUBSTR(ruleFormula,19,LENGTH(ruleFormula)-19)/1000;
			ELSE	
				set commissionRate = SUBSTR(ruleFormula,19,LENGTH(ruleFormula)-19)/100;
			END IF;  

		else
			set commissionAmount = (select amount from t_commission where rule_type = 0 and insurance_id = insuranceId
					order by create_time desc limit 1);
			set ruleFormula = (select tr.force_formula from t_commission tc join t_rule tr on tc.rule_id = tr.id
				where tc.rule_type = 0 and tc.insurance_id = insuranceId order by tc.create_time desc limit 1);
			if SUBSTR(ruleFormula,9,2) = '10' then 
				set commissionRate = SUBSTR(ruleFormula,20,LENGTH(ruleFormula)-20)/1000000;
			ELSEIF SUBSTR(ruleFormula,9,1) = '0' then
				set commissionRate = SUBSTR(ruleFormula,19,LENGTH(ruleFormula)-19)/1000;
			ELSE	
				set commissionRate = SUBSTR(ruleFormula,19,LENGTH(ruleFormula)-19)/100;
			END IF; 
		end if; 
		
		-- 运营策略折扣计算公式 运营策略折扣金额 运营策略折扣百分比
		set insuranceOperationsAmount = (select item.brokerage_amount
			from t_order_item item join t_brokerage brk on item.item_id = brk.item_id
			left join t_brokerage_strategy tbs on tbs.strategy_id = brk.strategy_id
			where item.status = validStatus and brk.status = validStatus and item.item_id = itemId 
			order by brokerage_id desc limit 1);
		set insuranceOperationsFormula = (select tbs.formula
			from t_order_item item join t_brokerage brk on item.item_id = brk.item_id
			left join t_brokerage_strategy tbs on tbs.strategy_id = brk.strategy_id
			where item.status = validStatus and brk.status = validStatus and item.item_id = itemId 
			order by brokerage_id desc limit 1);
		if substring(insuranceOperationsFormula,9,2) = '10' then 
			set insuranceOperationsPercentage = SUBSTR(insuranceOperationsFormula,20,LENGTH(insuranceOperationsFormula)-20)/1000000;
		ELSEIF SUBSTR(insuranceOperationsFormula,9,1) = '0' then
			set insuranceOperationsPercentage = SUBSTR(insuranceOperationsFormula,19,LENGTH(insuranceOperationsFormula)-19)/1000;
		ELSE	
			set insuranceOperationsPercentage = SUBSTR(insuranceOperationsFormula,19,LENGTH(insuranceOperationsFormula)-19)/100;
		END IF;
		-- 产品策略先付优惠金额
		SET insuranceProductsPayFirstAmount = (select handle_result from t_order_item item 
			join t_item_strategy stra on item.item_id = stra.item_id
			where item.status = validStatus and stra.is_execute = 1 and stra.strategy_type = 310 
			and item.item_id  = itemId);
		-- 追加奖励金额
		SET insuranceReward = (select handle_result from t_order_item item 
			join t_item_strategy stra on item.item_id = stra.item_id
			where item.status = validStatus and stra.is_execute = 1 and stra.strategy_type = 312 
			and item.item_id  = itemId);
		-- 产品打折策略折扣百分比
		SET handleRule = (select stra.handle_rule
			from t_order_item item join t_item_strategy stra on item.item_id = stra.item_id
			where item.status = validStatus and stra.is_execute = 1 and stra.strategy_type in (301 ,311)
			and item.item_id = itemId);
			
		if SUBSTR(handleRule,9,2) = '10' then 
			set insuranceProductsDiscountPercentage = SUBSTR(handleRule,20,LENGTH(handleRule)-20)/1000000;
		ELSEIF SUBSTR(handleRule,9,1) = '0' then
			set insuranceProductsDiscountPercentage = SUBSTR(handleRule,19,LENGTH(handleRule)-19)/1000;
		ELSEIF SUBSTR(handleRule,9,1) = '1' and SUBSTR(handleRule,9,2) != '10' then	
			set insuranceProductsDiscountPercentage = SUBSTR(handleRule,19,LENGTH(handleRule)-19)/100;
		ELSE	
			set insuranceProductsDiscountPercentage = SUBSTR(handleRule,10,LENGTH(handleRule)-10)/100;
		END IF;  
		-- 物流公司
		SET expressName = (select express_name from t_order_express_info 
							where order_id = orderId order by create_time desc limit 1);
							
		-- 配送次数 即打印次数 只统计批量导入的
		set deliveryTimes = (select count(*) from t_order_express_info 
							where property = 10 and order_id = orderId);	
		
		-- 工作流程信息
		-- 确认通过时间 查得到前置状态为20，后置状态为3,4,5,21的就取，查不到，给null
		SET firstCheckTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
			WHERE b.insurance_id=insuranceId
			AND b.insurance_status_from = 20 and b.insurance_status in(3,4,5,21)
			ORDER BY b.create_time asc LIMIT 1);
			
		SET nowCheckTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
			WHERE b.insurance_id=insuranceId
			AND b.insurance_status_from = 20 and b.insurance_status in(3,4,5,21)
			ORDER BY b.create_time DESC LIMIT 1);
		
		-- 核保通过时间
		SET firstUnderwriterTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				AND b.insurance_status_from=1 and b.insurance_status in(3,4,5,20,21)
				ORDER BY b.create_time asc LIMIT 1);
		SET nowUnderwriterTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				AND b.insurance_status_from=1 and b.insurance_status in(3,4,5,20,21)
				ORDER BY b.create_time DESC LIMIT 1);
				
		-- 完成打印付费通知单时间
		SET firstPrintNoticeTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 3 AND b.insurance_status = 4 
				ORDER BY b.create_time asc LIMIT 1);
		SET nowPrintNoticeTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 3 AND b.insurance_status = 4 
				ORDER BY b.create_time DESC LIMIT 1);
		-- 复核通过时间	
		SET firstRecheckTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 4 AND b.insurance_status in (5,21)
				ORDER BY b.create_time asc LIMIT 1);
		SET nowRecheckTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 4 AND b.insurance_status in (5,21)
				ORDER BY b.create_time DESC LIMIT 1);
		-- 待垫付申请完成时间	
		SET firstPendingApplyTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 21 AND b.insurance_status = 5
				ORDER BY b.create_time asc LIMIT 1);
		SET nowPendingApplyTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 21 AND b.insurance_status = 5
				ORDER BY b.create_time DESC LIMIT 1);
		-- 垫付完成时间	
		SET firstPaymentTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 5 AND b.insurance_status in (6,7,9)
				ORDER BY b.create_time asc LIMIT 1);
		SET nowPaymentTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 5 AND b.insurance_status in (6,7,9)
				ORDER BY b.create_time DESC LIMIT 1);
		-- 打单完成时间	
		SET printOrderTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 6 AND b.insurance_status = 7
				ORDER BY b.create_time DESC LIMIT 1);
		-- 存档完成时间	
		SET firstFiledTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 7 AND b.insurance_status = 8
				ORDER BY b.create_time asc LIMIT 1);
		SET nowFiledTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 7 AND b.insurance_status = 8
				ORDER BY b.create_time DESC LIMIT 1);
		-- 调度完成时间	
		SET firstDispatchTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 8 AND b.insurance_status = 9
				ORDER BY b.create_time asc LIMIT 1);
		SET nowDispatchTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 8 AND b.insurance_status = 9
				ORDER BY b.create_time DESC LIMIT 1);
		-- 配送完成时间	
		SET firstDeliveryTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 9 AND b.insurance_status in (11,12)
				ORDER BY b.create_time asc LIMIT 1);
		SET nowDeliveryTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				and b.insurance_status_from = 9 AND b.insurance_status in (11,12)
				ORDER BY b.create_time DESC LIMIT 1);
		-- 收款完成时间	
		SET firstFinishTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				AND b.insurance_status = 12
				ORDER BY b.create_time asc LIMIT 1);
		-- 收款完成时间	
		SET nowFinishTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				AND b.insurance_status = 12
				ORDER BY b.create_time DESC LIMIT 1);
		-- 取消或退保时间	
		SET firstCancleTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				AND b.insurance_status IN (10,13)
				ORDER BY b.create_time asc LIMIT 1);
		-- 取消或退保时间	
		SET nowCancleTime = (SELECT b.create_time FROM t_insurance_flow_detail b 
				WHERE b.insurance_id=insuranceId
				AND b.insurance_status IN (10,13)
				ORDER BY b.create_time DESC LIMIT 1);
				
		-- 判断是否已存在保单，如果存在更新现有的保单信息，如果不存在，insert.
		set orderIdNum = (SELECT COUNT(*) FROM t_st_insurance_based WHERE insurance_id = insuranceId);
		IF orderIdNum >= 1 THEN
			
			-- 已有保单，更新操作
			UPDATE t_st_insurance_based SET 
				insurance_ptype = var_ptype,
				order_source_type = sourceType,
				activity_id = activityId,
				activity_name = activityName,
				source_batch_id = sourceBatchId,
				channel_id = channelId,
				channel_type = channelType,
				car_city_id = carCityId,
				car_city_name = carCityName,
				car_create_time = carCreateTime,
				customer_mobile = var_mobile,
				customer_create_time = customercreatetime,
				customer_city_id = customerCityId,
				car_number = carNumber,
				insurance_city_id = insuranceCityId,
				insurance_city_name = insuranceCityName,
				insurance_pid = var_pid,
				insurance_supplier_id = supplierId,
				insurance_supplier_name = supplierName,
				coupon_amount = couponAmount, 
				item_real_amount = itemRealAmount,
				tax_fee = taxFee,
				insurance_start_date = insuranceStartDate,
				insurance_guadan_id = guadanId,
				insurance_reviewed_amount = insuranceReviewedAmount, 
				insurance_current_status = currentStatus,
				insurance_pre_status = preStatus, 
				coupon_id = couponId, 
				coupon_money_type = moneyType,
				coupon_actual_amount = actualAmount, 
				first_underwriter_time = firstUnderwriterTime,
				now_underwriter_time = nowUnderwriterTime, 
				first_check_time =firstCheckTime,
				now_check_time = nowCheckTime,
				first_print_notice_time = firstPrintNoticeTime,
				now_print_notice_time = nowPrintNoticeTime,
				first_recheck_time = firstRecheckTime,
				now_recheck_time = nowRecheckTime,
				first_pending_apply_time = firstPendingApplyTime,
				now_pending_apply_time = nowPendingApplyTime,
				first_payment_time = firstPaymentTime,
				now_payment_time = nowPaymentTime,
				print_order_time = printOrderTime,
				first_filed_time = firstFiledTime,
				now_filed_time = nowFiledTime,
				first_dispatch_time = firstDispatchTime,
				now_dispatch_time = nowDispatchTime,
				first_delivery_time = firstDeliveryTime,
				now_delivery_time = nowDeliveryTime,
				first_finish_time = firstFinishTime,
				now_finish_time = nowFinishTime,
				first_cancel_time = firstCancleTime,
				now_cancel_time = nowCancleTime,
				insuranced_person_name = insurancedPersonName,
				insurance_company_fid = insuranceCompanyFid,
				insurance_end_date = insuranceEndDate,
				item_amount = itemAmount,
				order_advance_amount = orderAdvanceAmount,
				order_real_amount = orderRealAmount,
				order_receive_amount = orderReceiveAmount,
				order_comments = orderComments,
				insurance_reward = insuranceReward,
				order_pay_style = orderPayStyle,
				order_payment_time = orderPaymentTime,
				oem_name = oemName,
				oem_payment_mode = oemPaymentMode,
				commission_amount = commissionAmount,
				rule_formula = ruleFormula,
				commission_rate = commissionRate,
				oem_code = oemCode,
				oem_agency_name = oemAgencyName,
				order_original_total_amount = orderOriginalTotalAmount,
				order_other_amount = orderOtherAmount,
				insurance_operations_amount = insuranceOperationsAmount,
				insurance_operations_formula = insuranceOperationsFormula,
				insurance_operations_percentage = insuranceOperationsPercentage,
				insurance_products_payFirst_amount = insuranceProductsPayFirstAmount,
				express_name = expressName,
				delivery_times = deliveryTimes,
				item_activity_amount = itemActivityAmount,
				insurance_products_discount_percentage = insuranceProductsDiscountPercentage
			WHERE insurance_id = insuranceId;
		ELSE
			-- 无此保单，insert操作
			INSERT INTO t_st_insurance_based(insurance_id, relation_insurance_id, order_id, item_id, insurance_ptype, order_source_type, 
			insurance_create_op_id, insurance_create_op_group_name, insurance_create_time, coupon_amount, item_real_amount,tax_fee,
			syc_compute_id,activity_id, activity_name, car_id, cid, source_batch_id, channel_id, channel_type, car_city_id, 
			car_city_name, car_create_time, customer_mobile, customer_create_time, customer_city_id, bee_op_id,bee_city_id, 
			bee_county_id, bee_type, bee_from_source, bee_create_time, car_number, insurance_start_date, insurance_city_id,
			insurance_city_name, insurance_pid, insurance_supplier_id, insurance_supplier_name, insurance_guadan_id, 
			insurance_reviewed_amount, insurance_current_status,
			insurance_pre_status, coupon_id, coupon_money_type, coupon_actual_amount, first_underwriter_time, now_underwriter_time, 
			first_check_time, now_check_time, first_print_notice_time, now_print_notice_time, first_recheck_time,
			now_recheck_time, first_pending_apply_time, now_pending_apply_time, first_payment_time, 
			now_payment_time, print_order_time, first_filed_time, now_filed_time,
			first_dispatch_time, now_dispatch_time, first_delivery_time, now_delivery_time, first_finish_time,
			now_finish_time, first_cancel_time, now_cancel_time, insuranced_person_name, insurance_company_fid,
			item_amount,order_advance_amount,order_real_amount,order_receive_amount,order_comments, insurance_reward,
			order_pay_style,order_payment_time,oem_name,oem_payment_mode,commission_amount,rule_formula,commission_rate,
			oem_code,oem_agency_name,order_original_total_amount,order_other_amount,insurance_operations_amount,
			insurance_operations_formula,insurance_operations_percentage,insurance_products_payFirst_amount,
			express_name,delivery_times,item_activity_amount,insurance_products_discount_percentage,insurance_end_date)
			VALUES(insuranceId, relationInsuranceId, orderId, itemId, var_ptype, sourceType, 
			createOpId, createOpGroupName, insuranceCreateTime, couponAmount, itemRealAmount,taxFee,
			sycComputeId,activityId, activityName, carId, var_cid, sourceBatchId, channelId, channelType, carCityId, 
			carCityName, carCreateTime, var_mobile, customerCreateTime, customerCityId, beeOpId, beeCityId, 
			countyId, beeType, beeFromSource, beeCreateTime, carNumber, insuranceStartDate, insuranceCityId,
			insuranceCityName, var_pid, supplierId, supplierName, guadanId, insuranceReviewedAmount, currentStatus,
			preStatus, couponId, moneyType, actualAmount, firstUnderwriterTime, nowUnderwriterTime, 
			firstCheckTime, nowCheckTime, firstPrintNoticeTime, nowPrintNoticeTime, firstRecheckTime,
			nowRecheckTime, firstPendingApplyTime, nowPendingApplyTime, firstPaymentTime, nowPaymentTime, 
			printOrderTime, firstFiledTime, nowFiledTime,
			firstDispatchTime, nowDispatchTime, firstDeliveryTime, nowDeliveryTime, firstFinishTime,nowFinishTime, 
			firstCancleTime, nowCancleTime, insurancedPersonName, insuranceCompanyFid, itemAmount,
			orderAdvanceAmount,orderRealAmount,orderReceiveAmount,orderComments, insuranceReward, orderPayStyle,orderPaymentTime,
			oemName,oemPaymentMode,commissionAmount,ruleFormula,commissionRate,oemCode,oemAgencyName,orderOriginalTotalAmount,
			orderOtherAmount,insuranceOperationsAmount,insuranceOperationsFormula,insuranceOperationsPercentage,
			insuranceProductsPayFirstAmount,expressName,deliveryTimes,itemActivityAmount,
			insuranceProductsDiscountPercentage,insuranceEndDate);
		END IF;
		commit;
		END LOOP myLoop;
	CLOSE curInusranceId;
END