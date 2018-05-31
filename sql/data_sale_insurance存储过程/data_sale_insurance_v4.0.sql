
-- 全局销售日报表数据生成存储过程
DROP PROCEDURE IF EXISTS `data_sale_insurance_proc`;
CREATE PROCEDURE `data_sale_insurance_proc`(IN `beginTime` VARCHAR(10),IN `endTime` VARCHAR(10))
BEGIN
	
	-- 查询已有的保单报表
	-- DECLARE beginTime VARCHAR(10); 
	-- DECLARE endTime date DEFAULT NOW();
	DECLARE lastTime VARCHAR(10); -- 取垫付时间和取消时间的最大值的较大值
	DECLARE businessCount bigint(20) DEFAULT null;  -- 商业险销售量
    DECLARE businessSum   double DEFAULT null;		 -- 商业险销售额
    DECLARE orderCount bigint(20) DEFAULT null; 	 -- 订单销售量
    DECLARE orderSum   double DEFAULT null;		 -- 订单销售额 减去了车船税后的金额
    DECLARE cancleCount bigint(20) DEFAULT null; 	 -- 取消商业险数量
    DECLARE cancleSum  double DEFAULT null;		 -- 取消商业险金额
	DECLARE v_count INT(11);
	DECLARE done INT(11) DEFAULT -1;
	DECLARE done1 INT(11) DEFAULT -1;
	DECLARE done2 INT(11) DEFAULT -1;
	DECLARE channelCode varchar(10);
	DECLARE channelName varchar(100); 
	DECLARE insuranceCityId int(11);		-- 投保城市ID
	DECLARE supplierId int(11);			-- 供应商ID
	DECLARE insuranceCityName VARCHAR(20);		-- 投保城市名称
	DECLARE supplierName VARCHAR(30);			-- 供应商名称
	DECLARE orderSourceType int(11);   -- 订单来源
	DECLARE orderSourceTypeName varchar(100); 

	
	IF ISNULL(beginTime) THEN
		SET beginTime =(select max(statis_date) a
				from data_sale_insurance);
	END IF;
	
	IF ISNULL(endTime) THEN
		SET endTime = date(now());
	END IF;
	
	-- 循环beginTime到今日的每一天 
	BEGIN
	DECLARE curStatDate CURSOR FOR select DATE_FORMAT(first_payment_time,'%Y-%m-%d') 
	from t_st_insurance_based 
	where DATE_FORMAT(first_payment_time,'%Y-%m-%d') >= beginTime
	and DATE_FORMAT(first_payment_time,'%Y-%m-%d') <= endTime
	group by DATE_FORMAT(first_payment_time,'%Y-%m-%d')
	union
	select DATE_FORMAT(first_cancel_time,'%Y-%m-%d') 
	from t_st_insurance_based
	where DATE_FORMAT(first_cancel_time,'%Y-%m-%d') >= beginTime
	and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') <= endTime
	and DATE_FORMAT(first_cancel_time,'%Y-%m') = DATE_FORMAT(first_payment_time,'%Y-%m')
	group by DATE_FORMAT(first_cancel_time,'%Y-%m-%d');
	
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done1 = 1;
	
	OPEN curStatDate;

		myLoop:LOOP
		FETCH curStatDate INTO lastTime;
		IF done1 = 1 THEN
			LEAVE myLoop;
		END IF;
		
	
		-- lastTime 这天的商业险统计 overall
		select count(*),ifnull(sum(insurance_reviewed_amount),0)
		into businessCount,businessSum
		from t_st_insurance_based
		where insurance_city_id < 100000 and insurance_ptype = 200 and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
		-- lastTime 这天的订单统计 overall
		select count(*),ifnull(sum(a.summ),0)  
		into orderCount,orderSum
		from
		(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
		from t_st_insurance_based
		where insurance_city_id < 100000 and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
		group by order_id) a;
		
		-- lastTime 这天的当月垫付后取消的订单统计 overall
		select count(*),ifnull(sum(a.summ),0)
		into cancleCount,cancleSum
		from (select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
		from t_st_insurance_based 
		where insurance_city_id < 100000 and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
		and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
		group by order_id) a;
		
		-- 判断是否已存在此日期overall的统计数据，如果存在更新现有的统计信息，如果不存在，insert
		select count(*) into v_count from data_sale_insurance 
		where statis_date = lastTime and angle_type = 'overall' and angle = 'overall';
		-- 统计表中存在此日期的数据，更新
		if v_count >= 1 then
			update data_sale_insurance 
			set business_count = businessCount,
				business_sum = businessSum,
				order_count = orderCount,
				order_sum = orderSum,
				cancle_count = cancleCount,
				cancle_sum = cancleSum
			where statis_date = lastTime and angle_type = 'overall' and angle = 'overall';
		else
			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
				order_count,order_sum,cancle_count,cancle_sum)
				VALUES(lastTime,'overall','overall',businessCount,businessSum,orderCount,orderSum,cancleCount,
				cancleSum);
			end if;
		end if;
		
		-- lastTime 这天的商业险统计 产品 X
		select count(*),ifnull(sum(insurance_reviewed_amount),0)
		into businessCount,businessSum
		from t_st_insurance_based
		where insurance_city_id < 100000 and insurance_ptype = 200 and activity_name LIKE '%X%' 
		and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
		-- lastTime 这天的订单统计 产品 X 
		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
		into orderCount,orderSum
		from t_st_insurance_based 
		where exists(select 1 from t_st_insurance_based a 
				where a.order_id = t_st_insurance_based.order_id 
				and a.insurance_city_id < 100000 and a.activity_name LIKE '%X%' 
				and DATE_FORMAT(a.first_payment_time,'%Y-%m-%d') = lastTime);
 
		-- lastTime 这天的当月垫付后取消的订单统计 产品 X
		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
		into cancleCount,cancleSum
		from t_st_insurance_based 
		where exists(select 1 from t_st_insurance_based a 
				where a.order_id = t_st_insurance_based.order_id 
				and a.insurance_city_id < 100000 and a.activity_name LIKE '%X%' 
				and DATE_FORMAT(a.first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
				and DATE_FORMAT(a.first_cancel_time,'%Y-%m-%d') = lastTime);
		
		-- 判断是否已存在此日期产品 X的统计数据，如果存在更新现有的统计信息，如果不存在，insert
		select count(*) into v_count from data_sale_insurance 
		where statis_date = lastTime and angle_type = 'activityName' and angle = 'X产品';
		-- 统计表中存在此日期的数据，更新
		if v_count >= 1 then
			update data_sale_insurance 
			set business_count = businessCount,
				business_sum = businessSum,
				order_count = orderCount,
				order_sum = orderSum,
				cancle_count = cancleCount,
				cancle_sum = cancleSum
			where statis_date = lastTime and angle_type = 'activityName' and angle = 'X产品';
		else
			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
				order_count,order_sum,cancle_count,cancle_sum)
				VALUES(lastTime,'activityName','X产品',businessCount,businessSum,orderCount,orderSum,cancleCount,
				cancleSum);
			end if;
		end if;
		
		-- lastTime 这天的商业险统计 产品 E
		select count(*),ifnull(sum(insurance_reviewed_amount),0)
		into businessCount,businessSum
		from t_st_insurance_based
		where insurance_city_id < 100000 and insurance_ptype = 200 and activity_name LIKE '%E%' 
		and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
		-- lastTime 这天的订单统计 产品 E 订单的e产品包含所有单交强的订单
		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
		into orderCount,orderSum
		from t_st_insurance_based 
		where exists(select 1 from t_st_insurance_based a 
				where a.order_id = t_st_insurance_based.order_id 
				and a.insurance_city_id < 100000 
				and ((a.insurance_ptype= 201 and not exists(select 1 from t_st_insurance_based t 
						where t.order_id = a.order_id and t.insurance_ptype = 200)) 
					or a.activity_name LIKE '%E%') 
				and DATE_FORMAT(a.first_payment_time,'%Y-%m-%d') = lastTime);
		
		-- lastTime 这天的当月垫付后取消的订单统计 产品 E 订单的e产品包含所有单交强的订单
		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
		into cancleCount,cancleSum
		from t_st_insurance_based 
		where exists(select 1 from t_st_insurance_based a 
				where a.order_id = t_st_insurance_based.order_id 
				and a.insurance_city_id < 100000 
				and ((a.insurance_ptype= 201 and not exists(select 1 from t_st_insurance_based t 
						where t.order_id = a.order_id and t.insurance_ptype = 200)) 
					or a.activity_name LIKE '%E%')
				and DATE_FORMAT(a.first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
				and DATE_FORMAT(a.first_cancel_time,'%Y-%m-%d') = lastTime);
		
		-- 判断是否已存在此日期产品 E 的统计数据，如果存在更新现有的统计信息，如果不存在，insert
		select count(*) into v_count from data_sale_insurance 
		where statis_date = lastTime and angle_type = 'activityName' and angle = 'E产品';
		-- 统计表中存在此日期的数据，更新
		if v_count >= 1 then
			update data_sale_insurance 
			set business_count = businessCount,
				business_sum = businessSum,
				order_count = orderCount,
				order_sum = orderSum,
				cancle_count = cancleCount,
				cancle_sum = cancleSum
			where statis_date = lastTime and angle_type = 'activityName' and angle = 'E产品';
		else
			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
				order_count,order_sum,cancle_count,cancle_sum)
				VALUES(lastTime,'activityName','E产品',businessCount,businessSum,orderCount,orderSum,cancleCount,
				cancleSum);
			end if;
		end if;
		
		-- lastTime 这天的商业险统计 产品 无活动
		select count(*),ifnull(sum(insurance_reviewed_amount),0)
		into businessCount,businessSum
		from t_st_insurance_based
		where insurance_city_id < 100000 and insurance_ptype = 200 and activity_name is null 
		and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
		-- lastTime 这天的订单统计 产品 无活动 订单的无活动不包含所有单交强的订单，因为单交强归为了E
		-- 因此订单的无活动统计只算含商业险的
		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
		into orderCount,orderSum
		from t_st_insurance_based 
		where exists(select 1 from t_st_insurance_based a 
				where a.order_id = t_st_insurance_based.order_id 
				and a.insurance_city_id < 100000 and insurance_ptype = 200 and a.activity_name is null 
				and DATE_FORMAT(a.first_payment_time,'%Y-%m-%d') = lastTime
				and not exists(select 1 from t_st_insurance_based based where based.order_id = a.order_id 
					and based.activity_name is not null ));
		
		-- lastTime 这天的当月垫付后取消的订单统计 产品 无活动
		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
		into cancleCount,cancleSum
		from t_st_insurance_based 
		where exists(select 1 from t_st_insurance_based a 
				where a.order_id = t_st_insurance_based.order_id 
				and a.insurance_city_id < 100000 and insurance_ptype = 200 and a.activity_name is null 
				and DATE_FORMAT(a.first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
				and DATE_FORMAT(a.first_cancel_time,'%Y-%m-%d') = lastTime
				and not exists(select 1 from t_st_insurance_based based where based.order_id = a.order_id 
					and based.activity_name is not null ));
		
		-- 判断是否已存在此日期产品 无活动 的统计数据，如果存在更新现有的统计信息，如果不存在，insert
		select count(*) into v_count from data_sale_insurance 
		where statis_date = lastTime and angle_type = 'activityName' and angle = '无活动';
		-- 统计表中存在此日期的数据，更新
		if v_count >= 1 then
			update data_sale_insurance 
			set business_count = businessCount,
				business_sum = businessSum,
				order_count = orderCount,
				order_sum = orderSum,
				cancle_count = cancleCount,
				cancle_sum = cancleSum
			where statis_date = lastTime and angle_type = 'activityName' and angle = '无活动';
		else
			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
				order_count,order_sum,cancle_count,cancle_sum)
				VALUES(lastTime,'activityName','无活动',businessCount,businessSum,orderCount,orderSum,cancleCount,
				cancleSum);
			end if;
		end if;
		
		-- lastTime 这天的订单统计 产品 单交强
		set businessCount = null;
		set businessSum = null;
		set cancleCount = null;
		set cancleSum = null;
		select count(*),ifnull(sum(a.summ),0)
		into orderCount,orderSum
		from
		(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
		from t_st_insurance_based
		where insurance_city_id < 100000 and insurance_ptype= 201 
		and not exists(select 1 from t_st_insurance_based t where t.order_id = t_st_insurance_based.order_id 
		and t.insurance_ptype = 200)
		and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
		group by order_id) a;
		-- lastTime 这天的当月垫付后取消的订单统计 产品 单交强 
		select count(*),ifnull(sum(a.summ),0)
		into cancleCount,cancleSum
		from
		(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
		from t_st_insurance_based
		where insurance_city_id < 100000 and insurance_ptype= 201 
		and not exists(select 1 from t_st_insurance_based t where t.order_id = t_st_insurance_based.order_id 
		and t.insurance_ptype = 200) 
		and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
		and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
		group by order_id) a;
		-- 判断是否已存在此日期产品 X的统计数据，如果存在更新现有的统计信息，如果不存在，insert
		select count(*) into v_count from data_sale_insurance 
		where statis_date = lastTime and angle_type = 'activityName' and angle = '单交强';
		-- 统计表中存在此日期的数据，更新
		if v_count >= 1 then
			update data_sale_insurance 
			set business_count = businessCount,
				business_sum = businessSum,
				order_count = orderCount,
				order_sum = orderSum,
				cancle_count = cancleCount,
				cancle_sum = cancleSum
			where statis_date = lastTime and angle_type = 'activityName' and angle = '单交强';
		else
			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
				order_count,order_sum,cancle_count,cancle_sum)
				VALUES(lastTime,'activityName','单交强',businessCount,businessSum,orderCount,orderSum,cancleCount,
				cancleSum);
			end if;
		end if;
		
		-- lastTime 这天的商业险统计 渠道
		SET done = -1;
		BEGIN
		DECLARE curCode CURSOR FOR SELECT distinct ifnull(channel_type,0) from t_st_insurance_based 
		where insurance_city_id < 100000 and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
		
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
		
		OPEN curCode;

			myLoop1:LOOP
			FETCH curCode INTO channelCode;
			IF done = 1 THEN
				LEAVE myLoop1;
			END IF;
			
			-- lastTime 这天的商业险统计 渠道
			set businessCount = null;
			set businessSum = null;
			set orderCount = null;
			set orderSum = null;
			set cancleCount = null;
			set cancleSum = null;
			
			-- 小蜜蜂要分城市
			if channelCode = 30 then
				-- lastTime 这天的商业险统计 渠道
				set insuranceCityName = null;
				SET done2 = -1;
				BEGIN
				DECLARE curCity CURSOR FOR SELECT distinct ifnull(insurance_city_name,0) from t_st_insurance_based 
				where insurance_city_id < 100000 and channel_type = channelCode 
				and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
				
				DECLARE CONTINUE HANDLER FOR NOT FOUND SET done2 = 1;
				
				OPEN curCity;

					myLoop3:LOOP
					FETCH curCity INTO insuranceCityName;
					IF done2 = 1 THEN
						LEAVE myLoop3;
					END IF;
					
					select count(*),ifnull(sum(insurance_reviewed_amount),0)
					into businessCount,businessSum
					from t_st_insurance_based
					where insurance_ptype = 200 and channel_type = channelCode 
					and insurance_city_name = insuranceCityName
					and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
					-- lastTime 这天的订单统计 渠道
					select count(*),ifnull(sum(a.summ),0)
					into orderCount,orderSum
					from
					(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
					from t_st_insurance_based 
					where channel_type = channelCode and insurance_city_name = insuranceCityName
							and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
					group by order_id) a;
					
					-- lastTime 这天的当月垫付后取消的订单统计 渠道
					select count(*),ifnull(sum(a.summ),0)
					into cancleCount,cancleSum
					from
					(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
					from t_st_insurance_based 
					where channel_type = channelCode and insurance_city_name = insuranceCityName
							and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
							and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
					group by order_id) a;
					
					set channelName = (select name from t_dictionary where code = channelCode and type = 10026 
										and code < 100 order by name desc limit 1);
					if channelName is null then
						set channelName = '渠道为空';
					end if;

					-- 判断是否已存在此日期渠道的统计数据，如果存在更新现有的统计信息，如果不存在，insert
					select count(*) into v_count from data_sale_insurance 
					where statis_date = lastTime and angle_type = 'channelType' 
					and angle = CONCAT(channelName,'-',replace(insuranceCityName,'市',''));
					-- 统计表中存在此日期的数据，更新
					if v_count >= 1 then
						update data_sale_insurance 
						set business_count = businessCount,
							business_sum = businessSum,
							order_count = orderCount,
							order_sum = orderSum,
							cancle_count = cancleCount,
							cancle_sum = cancleSum
						where statis_date = lastTime and angle_type = 'channelType' 
						and angle = CONCAT(channelName,'-',replace(insuranceCityName,'市',''));
					else
						if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
							INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
							order_count,order_sum,cancle_count,cancle_sum)
							VALUES(lastTime,'channelType',CONCAT(channelName,'-',replace(insuranceCityName,'市','')),
							businessCount,businessSum,orderCount,orderSum,cancleCount,cancleSum);
						end if;
					end if;
					END LOOP myLoop3;
				CLOSE curCity;
				END;	
			else 
				select count(*),ifnull(sum(insurance_reviewed_amount),0)
				into businessCount,businessSum
				from t_st_insurance_based
				where insurance_city_id < 100000 and insurance_ptype = 200 and channel_type = channelCode 
				and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
				-- lastTime 这天的订单统计 渠道
				select count(*),ifnull(sum(a.summ),0)
				into orderCount,orderSum
				from 
				(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
				from t_st_insurance_based 
				where insurance_city_id < 100000 and channel_type = channelCode
						and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
				group by order_id) a;
				
				-- lastTime 这天的当月垫付后取消的订单统计 渠道
				select count(*),ifnull(sum(a.summ),0)
				into cancleCount,cancleSum
				from 
				(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
				from t_st_insurance_based 
				where insurance_city_id < 100000 and channel_type = channelCode
					and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
					and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
				group by order_id) a;
				
				set channelName = (select name from t_dictionary where code = channelCode and type = 10026 
									and code < 100 order by name desc limit 1);
				if channelName is null then
					set channelName = '渠道为空';
				end if;

				-- 判断是否已存在此日期渠道的统计数据，如果存在更新现有的统计信息，如果不存在，insert
				select count(*) into v_count from data_sale_insurance 
				where statis_date = lastTime and angle_type = 'channelType' and angle = channelName;
				-- 统计表中存在此日期的数据，更新
				if v_count >= 1 then
					update data_sale_insurance 
					set business_count = businessCount,
						business_sum = businessSum,
						order_count = orderCount,
						order_sum = orderSum,
						cancle_count = cancleCount,
						cancle_sum = cancleSum
					where statis_date = lastTime and angle_type = 'channelType' and angle = channelName;
				else
					if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
						INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
						order_count,order_sum,cancle_count,cancle_sum)
						VALUES(lastTime,'channelType',channelName,businessCount,businessSum,orderCount,orderSum,cancleCount,
						cancleSum);
					end if;
				end if;
			end if;
			END LOOP myLoop1;
		CLOSE curCode;
		END;
		
		-- lastTime 这天的商业险统计 城市-保司
		set insuranceCityId = null;
		set supplierId = null;
		SET done = -1;
		BEGIN
		DECLARE curCitySupplier CURSOR FOR select distinct insurance_city_id,insurance_supplier_id 
		from t_st_insurance_based 
		where insurance_city_id < 100000 and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
		
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
		
		OPEN curCitySupplier;

			myLoop2:LOOP
			FETCH curCitySupplier INTO insuranceCityId,supplierId;
			IF done = 1 THEN
				LEAVE myLoop2;
			END IF;
			
			-- lastTime 这天的商业险统计 城市-保司
			set businessCount = null;
			set businessSum = null;
			set orderCount = null;
			set orderSum = null;
			set cancleCount = null;
			set cancleSum = null;
			
			select count(*),ifnull(sum(insurance_reviewed_amount),0)
			into businessCount,businessSum
			from t_st_insurance_based
			where insurance_ptype = 200 
			and insurance_city_id = insuranceCityId AND insurance_supplier_id = supplierId
			and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
			-- lastTime 这天的订单统计 城市-保司
			select count(*),ifnull(sum(a.summ),0)
			into orderCount,orderSum
			from
			(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
			from t_st_insurance_based 
			where insurance_city_id = insuranceCityId AND insurance_supplier_id = supplierId 
			and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
			group by order_id) a;
			
			-- lastTime 这天的当月垫付后取消的商业险统计 城市-保司
			select count(*),ifnull(sum(a.summ),0)
			into cancleCount,cancleSum
			from
			(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
			from t_st_insurance_based 
			where insurance_city_id = insuranceCityId AND insurance_supplier_id = supplierId 
				and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
				and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
			group by order_id) a;
			
			set insuranceCityName = (select name from t_city where city_id = insuranceCityId);
			set supplierName = (select supplier_name from t_supplier where supplier_id = supplierId);
			-- 判断是否已存在此日期城市-保司的统计数据，如果存在更新现有的统计信息，如果不存在，insert
			select count(*) into v_count from data_sale_insurance 
			where statis_date = lastTime and angle_type = 'cityName_supplierName' 
			and angle = CONCAT(replace(insuranceCityName,'市',''),'-',supplierName);
			-- 统计表中存在此日期的数据，更新
			if v_count >= 1 then
				update data_sale_insurance 
				set business_count = businessCount,
					business_sum = businessSum,
					order_count = orderCount,
					order_sum = orderSum,
					cancle_count = cancleCount,
					cancle_sum = cancleSum
				where statis_date = lastTime and angle_type = 'cityName_supplierName' 
				and angle = CONCAT(replace(insuranceCityName,'市',''),'-',supplierName);
			else
				if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
					INSERT INTO data_sale_insurance(statis_date, angle_type, angle, business_count, business_sum,
					order_count, order_sum, cancle_count, cancle_sum)
					VALUES(lastTime,'cityName_supplierName',
					CONCAT(replace(insuranceCityName,'市',''),'-',supplierName),businessCount,businessSum,
					orderCount,orderSum,cancleCount,cancleSum);
				end if;
			end if;
			
			END LOOP myLoop2;
		CLOSE curCitySupplier;
		END;
		
		-- lastTime 这天的商业险统计 订单来源
		set insuranceCityId = null;
		set supplierId = null;
		SET done = -1;
		BEGIN
		DECLARE curOrderSourceType CURSOR FOR select distinct order_source_type 
		from t_st_insurance_based 
		where insurance_city_id < 100000 and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
		
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
		
		OPEN curOrderSourceType;

			myLoopS:LOOP
			FETCH curOrderSourceType INTO orderSourceType;
			IF done = 1 THEN
				LEAVE myLoopS;
			END IF;
			
			-- lastTime 这天的商业险统计 订单来源
			set businessCount = null;
			set businessSum = null;
			set orderCount = null;
			set orderSum = null;
			set cancleCount = null;
			set cancleSum = null;
			
			-- 又一单 经纪人要分城市
			if orderSourceType = 300 or orderSourceType = 400 then
				-- lastTime 这天的商业险统计 订单来源
				set insuranceCityName = null;
				SET done2 = -1;
				BEGIN
				DECLARE curOrderCity CURSOR FOR SELECT distinct ifnull(insurance_city_name,0) from t_st_insurance_based 
				where insurance_city_id < 100000 and (order_source_type = 300 or order_source_type = 400)
				and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
				
				DECLARE CONTINUE HANDLER FOR NOT FOUND SET done2 = 1;
				
				OPEN curOrderCity;

					myLoopS1:LOOP
					FETCH curOrderCity INTO insuranceCityName;
					IF done2 = 1 THEN
						LEAVE myLoopS1;
					END IF;
					
					select count(*),ifnull(sum(insurance_reviewed_amount),0)
					into businessCount,businessSum
					from t_st_insurance_based
					where insurance_ptype = 200 
					and (order_source_type = 300 or order_source_type = 400) 
					and insurance_city_name = insuranceCityName
					and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
					-- lastTime 这天的订单统计 城市+订单来源
					select count(*),ifnull(sum(a.summ),0)
					into orderCount,orderSum
					from
					(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
					from t_st_insurance_based 
					where (order_source_type = 300 or order_source_type = 400)
					and insurance_city_name = insuranceCityName
					and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
					group by order_id) a;
					
					-- lastTime 这天的当月垫付后取消的商业险统计 订单来源
					select count(*),ifnull(sum(a.summ),0)
					into cancleCount,cancleSum
					from
					(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
					from t_st_insurance_based 
					where (order_source_type = 300 or order_source_type = 400)
					and insurance_city_name = insuranceCityName
					and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
					and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
					group by order_id) a;
					
					-- 判断是否已存在此日期城市-保司的统计数据，如果存在更新现有的统计信息，如果不存在，insert
					select count(*) into v_count from data_sale_insurance 
					where statis_date = lastTime and angle_type = 'order_source_type'
					and angle = CONCAT('LB','-',replace(insuranceCityName,'市',''));
					-- 统计表中存在此日期的数据，更新
					if v_count >= 1 then
						update data_sale_insurance 
						set business_count = businessCount,
							business_sum = businessSum,
							order_count = orderCount,
							order_sum = orderSum,
							cancle_count = cancleCount,
							cancle_sum = cancleSum
						where statis_date = lastTime and angle_type = 'order_source_type' 
						and angle = CONCAT('LB','-',replace(insuranceCityName,'市',''));
					else
						if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
							INSERT INTO data_sale_insurance(statis_date, angle_type, angle, business_count, business_sum,
							order_count, order_sum, cancle_count, cancle_sum)
							VALUES(lastTime,'order_source_type',CONCAT('LB','-',replace(insuranceCityName,'市','')),
							businessCount,businessSum,orderCount,orderSum,cancleCount,cancleSum);
						end if;
					end if;
					END LOOP myLoopS1;
				CLOSE curOrderCity;
				END;	
			elseif orderSourceType = 100 or orderSourceType = 200 then 
			
				select count(*),ifnull(sum(insurance_reviewed_amount),0)
				into businessCount,businessSum
				from t_st_insurance_based
				where insurance_city_id < 100000 and insurance_ptype = 200 
				and (order_source_type = 100 or order_source_type = 200)
				and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
				-- lastTime 这天的订单统计 订单来源
				select count(*),ifnull(sum(a.summ),0)
				into orderCount,orderSum
				from
				(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
				from t_st_insurance_based 
				where insurance_city_id < 100000 and (order_source_type = 100 or order_source_type = 200)
				and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
				group by order_id) a;
				
				-- lastTime 这天的当月垫付后取消的商业险统计 订单来源
				select count(*),ifnull(sum(a.summ),0)
				into cancleCount,cancleSum
				from
				(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
				from t_st_insurance_based 
				where insurance_city_id < 100000 and (order_source_type = 100 or order_source_type = 200)
					and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
					and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
				group by order_id) a;
				
				set orderSourceTypeName = '网电销';
				
				-- 判断是否已存在此日期城市-保司的统计数据，如果存在更新现有的统计信息，如果不存在，insert
				select count(*) into v_count from data_sale_insurance 
				where statis_date = lastTime and angle_type = 'order_source_type'
				and angle = orderSourceTypeName;
				-- 统计表中存在此日期的数据，更新
				if v_count >= 1 then
					update data_sale_insurance 
					set business_count = businessCount,
						business_sum = businessSum,
						order_count = orderCount,
						order_sum = orderSum,
						cancle_count = cancleCount,
						cancle_sum = cancleSum
					where statis_date = lastTime and angle_type = 'order_source_type' 
					and angle = orderSourceTypeName;
				else
					if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
						INSERT INTO data_sale_insurance(statis_date, angle_type, angle, business_count, business_sum,
						order_count, order_sum, cancle_count, cancle_sum)
						VALUES(lastTime,'order_source_type',orderSourceTypeName,businessCount,businessSum,
						orderCount,orderSum,cancleCount,cancleSum);
					end if;
				end if;
			 
			end if;
							
			END LOOP myLoopS;
		CLOSE curOrderSourceType;
		END;
		
		commit;
		
		END LOOP myLoop;
	CLOSE curStatDate; 
	END;
	commit;
END