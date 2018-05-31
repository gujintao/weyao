
 -- ȫ�������ձ����������ɴ洢����
DROP PROCEDURE IF EXISTS `data_sale_insurance_proc`;
 CREATE PROCEDURE `data_sale_insurance_proc`(IN `beginTime` VARCHAR(10),IN `endTime` VARCHAR(10))
 BEGIN
 	
 	-- ��ѯ���еı�������
 	-- DECLARE beginTime VARCHAR(10); 
 	-- DECLARE endTime date DEFAULT NOW();
 	DECLARE lastTime VARCHAR(10); -- ȡ�渶ʱ���ȡ��ʱ������ֵ�Ľϴ�ֵ
 	DECLARE businessCount bigint(20) DEFAULT null;  -- ��ҵ��������
     DECLARE businessSum   double DEFAULT null;		 -- ��ҵ�����۶�
     DECLARE orderCount bigint(20) DEFAULT null; 	 -- ����������
     DECLARE orderSum   double DEFAULT null;		 -- �������۶� ��ȥ�˳���˰��Ľ��
     DECLARE cancleCount bigint(20) DEFAULT null; 	 -- ȡ����ҵ������
     DECLARE cancleSum  double DEFAULT null;		 -- ȡ����ҵ�ս��
 	DECLARE v_count INT(11);
 	DECLARE done INT(11) DEFAULT -1;
 	DECLARE done1 INT(11) DEFAULT -1;
 	DECLARE done2 INT(11) DEFAULT -1;
 	DECLARE channelCode varchar(10);
 	DECLARE channelName varchar(100); 
 	DECLARE insuranceCityId int(11);		-- Ͷ������ID
 	DECLARE supplierId int(11);			-- ��Ӧ��ID
 	DECLARE insuranceCityName VARCHAR(20);		-- Ͷ����������
 	DECLARE supplierName VARCHAR(30);			-- ��Ӧ������
 	DECLARE orderSourceType int(11);   -- ������Դ
 	DECLARE orderSourceTypeName varchar(100); 
 
 	
 	IF ISNULL(beginTime) THEN
 		SET beginTime =(select max(statis_date) a
 				from data_sale_insurance);
 	END IF;
 	
 	IF ISNULL(endTime) THEN
 		SET endTime = date(now());
 	END IF;
 	
 	-- ѭ��beginTime�����յ�ÿһ�� 
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
 		
 	
 		-- lastTime �������ҵ��ͳ�� overall
 		select count(*),ifnull(sum(insurance_reviewed_amount),0)
 		into businessCount,businessSum
 		from t_st_insurance_based
 		where insurance_city_id < 100000 and insurance_ptype = 200 and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
 		-- lastTime ����Ķ���ͳ�� overall
 		select count(*),ifnull(sum(a.summ),0)  
 		into orderCount,orderSum
 		from
 		(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
 		from t_st_insurance_based
 		where insurance_city_id < 100000 and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
 		group by order_id) a;
 		
 		-- lastTime ����ĵ��µ渶��ȡ���Ķ���ͳ�� overall
 		select count(*),ifnull(sum(a.summ),0)
 		into cancleCount,cancleSum
 		from (select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
 		from t_st_insurance_based 
 		where insurance_city_id < 100000 and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
 		and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
 		group by order_id) a;
 		
 		-- �ж��Ƿ��Ѵ��ڴ�����overall��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 		select count(*) into v_count from data_sale_insurance 
 		where statis_date = lastTime and angle_type = 'overall' and angle = 'overall';
 		-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
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
 		
 		-- lastTime �������ҵ��ͳ�� ��Ʒ X
 		select count(*),ifnull(sum(insurance_reviewed_amount),0)
 		into businessCount,businessSum
 		from t_st_insurance_based
 		where insurance_city_id < 100000 and insurance_ptype = 200 and activity_name LIKE '%X%' 
 		and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
 		-- lastTime ����Ķ���ͳ�� ��Ʒ X 
 		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
 		into orderCount,orderSum
 		from t_st_insurance_based 
 		where exists(select 1 from t_st_insurance_based a 
 				where a.order_id = t_st_insurance_based.order_id 
 				and a.insurance_city_id < 100000 and a.activity_name LIKE '%X%' 
 				and DATE_FORMAT(a.first_payment_time,'%Y-%m-%d') = lastTime);
  
 		-- lastTime ����ĵ��µ渶��ȡ���Ķ���ͳ�� ��Ʒ X
 		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
 		into cancleCount,cancleSum
 		from t_st_insurance_based 
 		where exists(select 1 from t_st_insurance_based a 
 				where a.order_id = t_st_insurance_based.order_id 
 				and a.insurance_city_id < 100000 and a.activity_name LIKE '%X%' 
 				and DATE_FORMAT(a.first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
 				and DATE_FORMAT(a.first_cancel_time,'%Y-%m-%d') = lastTime);
 		
 		-- �ж��Ƿ��Ѵ��ڴ����ڲ�Ʒ X��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 		select count(*) into v_count from data_sale_insurance 
 		where statis_date = lastTime and angle_type = 'activityName' and angle = 'X��Ʒ';
 		-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
 		if v_count >= 1 then
 			update data_sale_insurance 
 			set business_count = businessCount,
 				business_sum = businessSum,
 				order_count = orderCount,
 				order_sum = orderSum,
 				cancle_count = cancleCount,
 				cancle_sum = cancleSum
 			where statis_date = lastTime and angle_type = 'activityName' and angle = 'X��Ʒ';
 		else
 			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
 				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
 				order_count,order_sum,cancle_count,cancle_sum)
 				VALUES(lastTime,'activityName','X��Ʒ',businessCount,businessSum,orderCount,orderSum,cancleCount,
 				cancleSum);
 			end if;
 		end if;
 		
 		-- lastTime �������ҵ��ͳ�� ��Ʒ E
 		select count(*),ifnull(sum(insurance_reviewed_amount),0)
 		into businessCount,businessSum
 		from t_st_insurance_based
 		where insurance_city_id < 100000 and insurance_ptype = 200 and activity_name LIKE '%E%' 
 		and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
 		-- lastTime ����Ķ���ͳ�� ��Ʒ E ������e��Ʒ�������е���ǿ�Ķ���
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
 		
 		-- lastTime ����ĵ��µ渶��ȡ���Ķ���ͳ�� ��Ʒ E ������e��Ʒ�������е���ǿ�Ķ���
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
 		
 		-- �ж��Ƿ��Ѵ��ڴ����ڲ�Ʒ E ��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 		select count(*) into v_count from data_sale_insurance 
 		where statis_date = lastTime and angle_type = 'activityName' and angle = 'E��Ʒ';
 		-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
 		if v_count >= 1 then
 			update data_sale_insurance 
 			set business_count = businessCount,
 				business_sum = businessSum,
 				order_count = orderCount,
 				order_sum = orderSum,
 				cancle_count = cancleCount,
 				cancle_sum = cancleSum
 			where statis_date = lastTime and angle_type = 'activityName' and angle = 'E��Ʒ';
 		else
 			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
 				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
 				order_count,order_sum,cancle_count,cancle_sum)
 				VALUES(lastTime,'activityName','E��Ʒ',businessCount,businessSum,orderCount,orderSum,cancleCount,
 				cancleSum);
 			end if;
 		end if;
 		
 		-- lastTime �������ҵ��ͳ�� ��Ʒ �޻
 		select count(*),ifnull(sum(insurance_reviewed_amount),0)
 		into businessCount,businessSum
 		from t_st_insurance_based
 		where insurance_city_id < 100000 and insurance_ptype = 200 and activity_name is null 
 		and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
 		-- lastTime ����Ķ���ͳ�� ��Ʒ �޻ �������޻���������е���ǿ�Ķ�������Ϊ����ǿ��Ϊ��E
 		-- ��˶������޻ͳ��ֻ�㺬��ҵ�յ�
 		select count(distinct order_id),sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0))
 		into orderCount,orderSum
 		from t_st_insurance_based 
 		where exists(select 1 from t_st_insurance_based a 
 				where a.order_id = t_st_insurance_based.order_id 
 				and a.insurance_city_id < 100000 and insurance_ptype = 200 and a.activity_name is null 
 				and DATE_FORMAT(a.first_payment_time,'%Y-%m-%d') = lastTime
 				and not exists(select 1 from t_st_insurance_based based where based.order_id = a.order_id 
 					and based.activity_name is not null ));
 		
 		-- lastTime ����ĵ��µ渶��ȡ���Ķ���ͳ�� ��Ʒ �޻
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
 		
 		-- �ж��Ƿ��Ѵ��ڴ����ڲ�Ʒ �޻ ��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 		select count(*) into v_count from data_sale_insurance 
 		where statis_date = lastTime and angle_type = 'activityName' and angle = '�޻';
 		-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
 		if v_count >= 1 then
 			update data_sale_insurance 
 			set business_count = businessCount,
 				business_sum = businessSum,
 				order_count = orderCount,
 				order_sum = orderSum,
 				cancle_count = cancleCount,
 				cancle_sum = cancleSum
 			where statis_date = lastTime and angle_type = 'activityName' and angle = '�޻';
 		else
 			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
 				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
 				order_count,order_sum,cancle_count,cancle_sum)
 				VALUES(lastTime,'activityName','�޻',businessCount,businessSum,orderCount,orderSum,cancleCount,
 				cancleSum);
 			end if;
 		end if;
 		
 		-- lastTime ����Ķ���ͳ�� ��Ʒ ����ǿ
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
 		-- lastTime ����ĵ��µ渶��ȡ���Ķ���ͳ�� ��Ʒ ����ǿ 
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
 		-- �ж��Ƿ��Ѵ��ڴ����ڲ�Ʒ X��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 		select count(*) into v_count from data_sale_insurance 
 		where statis_date = lastTime and angle_type = 'activityName' and angle = '����ǿ';
 		-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
 		if v_count >= 1 then
 			update data_sale_insurance 
 			set business_count = businessCount,
 				business_sum = businessSum,
 				order_count = orderCount,
 				order_sum = orderSum,
 				cancle_count = cancleCount,
 				cancle_sum = cancleSum
 			where statis_date = lastTime and angle_type = 'activityName' and angle = '����ǿ';
 		else
 			if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
 				INSERT INTO data_sale_insurance(statis_date,angle_type,angle,business_count,business_sum,
 				order_count,order_sum,cancle_count,cancle_sum)
 				VALUES(lastTime,'activityName','����ǿ',businessCount,businessSum,orderCount,orderSum,cancleCount,
 				cancleSum);
 			end if;
 		end if;
 		
 		
 		
 		-- lastTime �������ҵ��ͳ�� ����-��˾
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
 			
 			-- lastTime �������ҵ��ͳ�� ����-��˾
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
 			-- lastTime ����Ķ���ͳ�� ����-��˾
 			select count(*),ifnull(sum(a.summ),0)
 			into orderCount,orderSum
 			from
 			(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
 			from t_st_insurance_based 
 			where insurance_city_id = insuranceCityId AND insurance_supplier_id = supplierId 
 			and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
 			group by order_id) a;
 			
 			-- lastTime ����ĵ��µ渶��ȡ������ҵ��ͳ�� ����-��˾
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
 			-- �ж��Ƿ��Ѵ��ڴ����ڳ���-��˾��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 			select count(*) into v_count from data_sale_insurance 
 			where statis_date = lastTime and angle_type = 'cityName_supplierName' 
 			and angle = CONCAT(replace(insuranceCityName,'��',''),'-',supplierName);
 			-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
 			if v_count >= 1 then
 				update data_sale_insurance 
 				set business_count = businessCount,
 					business_sum = businessSum,
 					order_count = orderCount,
 					order_sum = orderSum,
 					cancle_count = cancleCount,
 					cancle_sum = cancleSum
 				where statis_date = lastTime and angle_type = 'cityName_supplierName' 
 				and angle = CONCAT(replace(insuranceCityName,'��',''),'-',supplierName);
 			else
 				if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
 					INSERT INTO data_sale_insurance(statis_date, angle_type, angle, business_count, business_sum,
 					order_count, order_sum, cancle_count, cancle_sum)
 					VALUES(lastTime,'cityName_supplierName',
 					CONCAT(replace(insuranceCityName,'��',''),'-',supplierName),businessCount,businessSum,
 					orderCount,orderSum,cancleCount,cancleSum);
 				end if;
 			end if;
 			
 			END LOOP myLoop2;
 		CLOSE curCitySupplier;
 		END;
 		
 		-- lastTime �������ҵ��ͳ�� ������Դ
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
 			
 			-- lastTime �������ҵ��ͳ�� ������Դ
 			set businessCount = null;
 			set businessSum = null;
 			set orderCount = null;
 			set orderSum = null;
 			set cancleCount = null;
 			set cancleSum = null;
 			
 			-- ��һ�� ������Ҫ�ֳ���
 			if orderSourceType = 300 or orderSourceType = 400 then
 				-- lastTime �������ҵ��ͳ�� ������Դ
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
 					-- lastTime ����Ķ���ͳ�� ����+������Դ
 					select count(*),ifnull(sum(a.summ),0)
 					into orderCount,orderSum
 					from
 					(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
 					from t_st_insurance_based 
 					where (order_source_type = 300 or order_source_type = 400)
 					and insurance_city_name = insuranceCityName
 					and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
 					group by order_id) a;
 					
 					-- lastTime ����ĵ��µ渶��ȡ������ҵ��ͳ�� ������Դ
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
 					
 					-- �ж��Ƿ��Ѵ��ڴ����ڳ���-��˾��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 					select count(*) into v_count from data_sale_insurance 
 					where statis_date = lastTime and angle_type = 'order_source_type'
 					and angle = CONCAT('LB','-',replace(insuranceCityName,'��',''));
 					-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
 					if v_count >= 1 then
 						update data_sale_insurance 
 						set business_count = businessCount,
 							business_sum = businessSum,
 							order_count = orderCount,
 							order_sum = orderSum,
 							cancle_count = cancleCount,
 							cancle_sum = cancleSum
 						where statis_date = lastTime and angle_type = 'order_source_type' 
 						and angle = CONCAT('LB','-',replace(insuranceCityName,'��',''));
 					else
 						if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
 							INSERT INTO data_sale_insurance(statis_date, angle_type, angle, business_count, business_sum,
 							order_count, order_sum, cancle_count, cancle_sum)
 							VALUES(lastTime,'order_source_type',CONCAT('LB','-',replace(insuranceCityName,'��','')),
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
 				-- lastTime ����Ķ���ͳ�� ������Դ
 				select count(*),ifnull(sum(a.summ),0)
 				into orderCount,orderSum
 				from
 				(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
 				from t_st_insurance_based 
 				where insurance_city_id < 100000 and (order_source_type = 100 or order_source_type = 200)
 				and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
 				group by order_id) a;
 				
 				-- lastTime ����ĵ��µ渶��ȡ������ҵ��ͳ�� ������Դ
 				select count(*),ifnull(sum(a.summ),0)
 				into cancleCount,cancleSum
 				from
 				(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
 				from t_st_insurance_based 
 				where insurance_city_id < 100000 and (order_source_type = 100 or order_source_type = 200)
 					and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
 					and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
 				group by order_id) a;
 				
 				set orderSourceTypeName = '������';
 				
 				-- �ж��Ƿ��Ѵ��ڴ����ڳ���-��˾��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 				select count(*) into v_count from data_sale_insurance 
 				where statis_date = lastTime and angle_type = 'order_source_type'
 				and angle = orderSourceTypeName;
 				-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
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
 			 



			 -- �յ�
			 elseif orderSourceType = 500  then
				set insuranceCityName = null;
 				SET done2 = -1;
 				BEGIN
 				DECLARE curOrderCity CURSOR FOR SELECT distinct ifnull(insurance_city_name,0) from t_st_insurance_based 
 				where insurance_city_id < 100000 and order_source_type = 500
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
 					and order_source_type = 500
 					and insurance_city_name = insuranceCityName
 					and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime;
 					-- lastTime ����Ķ���ͳ�� ����+������Դ
 					select count(*),ifnull(sum(a.summ),0)
 					into orderCount,orderSum
 					from
 					(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
 					from t_st_insurance_based 
 					where order_source_type = 500
 					and insurance_city_name = insuranceCityName
 					and DATE_FORMAT(first_payment_time,'%Y-%m-%d') = lastTime
 					group by order_id) a;
 					
 					-- lastTime ����ĵ��µ渶��ȡ������ҵ��ͳ�� ������Դ
 					select count(*),ifnull(sum(a.summ),0)
 					into cancleCount,cancleSum
 					from
 					(select order_id,sum(ifnull(insurance_reviewed_amount,0))-sum(ifnull(tax_fee,0)) as summ
 					from t_st_insurance_based 
 					where order_source_type = 500
 					and insurance_city_name = insuranceCityName
 					and DATE_FORMAT(first_payment_time,'%Y-%m') = DATE_FORMAT(lastTime,'%Y-%m')
 					and DATE_FORMAT(first_cancel_time,'%Y-%m-%d') = lastTime
 					group by order_id) a;
 					
 					-- �ж��Ƿ��Ѵ��ڴ����ڳ���-��˾��ͳ�����ݣ�������ڸ������е�ͳ����Ϣ����������ڣ�insert
 					select count(*) into v_count from data_sale_insurance 
 					where statis_date = lastTime and angle_type = 'order_source_type'
 					and angle = CONCAT('SD','-',replace(insuranceCityName,'��',''));

 					-- ͳ�Ʊ��д��ڴ����ڵ����ݣ�����
 					if v_count >= 1 then
 						update data_sale_insurance 
 						set business_count = businessCount,
 							business_sum = businessSum,
 							order_count = orderCount,
 							order_sum = orderSum,
 							cancle_count = cancleCount,
 							cancle_sum = cancleSum
 						where statis_date = lastTime and angle_type = 'order_source_type' 
 						and angle = CONCAT('SD','-',replace(insuranceCityName,'��',''));
 					else
 						if businessCount<>0 or orderCount<>0 or cancleCount<>0 then
 							INSERT INTO data_sale_insurance(statis_date, angle_type, angle, business_count, business_sum,
 							order_count, order_sum, cancle_count, cancle_sum)
 							VALUES(lastTime,'order_source_type',CONCAT('SD','-',replace(insuranceCityName,'��','')),
 							businessCount,businessSum,orderCount,orderSum,cancleCount,cancleSum);
 						end if;
 					end if;
				END LOOP myLoopS1;
 				CLOSE curOrderCity;
 				END;	



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