SET @version_id = (
  SELECT
    (
      IFNULL(MAX(version_id), 0) + 1
    ) AS version_id
  FROM
    t_insurance_finance_show_report_new
);

INSERT INTO t_insurance_finance_show_report_new (
  order_id,
  cid,
  dianfu,
  dianfu_comment,
  order_create_time,
  channel_type,
  channel_name,
  force_activity_name,
  business_activity_name,
  supplier_id,
  supplier_name,
  op_name,
  car_number,
  insurance_person,
  force_number,
  business_number,
  force_amount,
  tax_amount,
  business_amount,
  insurance_reviewed_amount,
  dianfu_amount,
  force_zhekou,
  business_zhekou,
  force_zhekou_amount,
  business_zhekou_amount,
  force_coupon_amount,
  business_coupon_amount,
  real_amount,
  order_extra_amount,
  order_outcome,
  order_user_pay,
  receive_amount,
  chae_amount,
  wancheng,
  force_reward,
  business_reward,
  lipin,
  insurance_status,
  status_name,
  flow_detail,
  city_id,
  city_name,
  psy_time,
  pay_style,
  oem_name,
  oem_payment_mode,
  flow_finish_time,
  out_trade_no,
  force_commission_amount,
  business_commission_amount,
  force_rule_formula,
  business_rule_formula,
  force_commission_rate,
  business_commission_rate,
  oem_code,
  oem_agency_name,
  order_original_total_amount,
  order_other_amount,
  force_insurance_operations_amount,
  business_insurance_operations_amount,
  force_insurance_operations_formula,
  business_insurance_operations_formula,
  force_insurance_operations_percentage,
  business_insurance_operations_percentage,
  insurance_products_payFirst_amount,
  express_name,
  delivery_times,
  order_source_type,
  force_insurance_end_date,
  business_insurance_end_date,
  force_insurance_start_date,
  business_insurance_start_date,
  signing_company_name,
  version_id
) SELECT
    based.order_id AS '订单号',
    cid,
    based.first_payment_time AS '垫付时间',
    dianfu_comment,
    based.insurance_create_time AS '订单生成时间',
    based.channel_type AS '渠道类型',
    based.channel_id AS '渠道',
    max(based.force_activity_name) AS '交强险活动名称',
    max(
        based.business_activity_name
    ) AS '商业险活动名称',
    based.insurance_supplier_id AS '保险公司ID',
    based.insurance_supplier_name AS '保险公司',
    based.insurance_create_op_name AS '坐席名称',
    based.car_number AS '车牌号',
    based.insuranced_person_name AS '被保人',
    max(based.force_number) AS '交强险交易号',
    max(based.business_number) AS '商业险交易号',
    ROUND(
        (
          MAX(based.force_amount) - max(based.tax_fee)
        ) / 100,
        2
    ) AS '交强险金额（元）',
    ROUND(max(based.tax_fee) / 100, 2) AS '车船税金额（元）',
    ROUND(
        max(based.business_amount) / 100,
        2
    ) AS '商业险金额（元）',
    ROUND(
        (
          max(based.force_amount) + max(based.business_amount)
        ) / 100,
        2
    ) AS '保单保费（包括车船税）（元）',
    ROUND(
        max(based.order_advance_amount) / 100,
        2
    ) AS '垫付金额（元）',
    (
      CASE
      WHEN max(based.force_discount) = 1 THEN
        0
      ELSE
        max(based.force_discount)
      END
    ) AS '交强险折扣率',
    (
      CASE
      WHEN max(based.business_discount) = 1 THEN
        0
      ELSE
        max(based.business_discount)
      END
    ) AS '商业险折扣率',
    ROUND(
        max(
            based.force_discount_amount
        ) / 100,
        2
    ) AS '交强险折扣金额（元）',
    ROUND(
        max(
            based.business_discount_amount
        ) / 100,
        2
    ) AS '商业险折扣金额（元）',
    ROUND(
        max(based.force_coupon_amount) / 100,
        2
    ) AS '交强险优惠券金额（元）',
    ROUND(
        max(
            based.business_coupon_amount
        ) / 100,
        2
    ) AS '商业险优惠券金额（元）',
    ROUND(
        max(based.order_real_amount) / 100,
        2
    ) AS '客户应付金额（元）',
    ROUND(
        max(based.order_extra_amount) / 100,
        2
    ) AS '另收（元）',
    ROUND(
        max(based.order_outcome) / 100,
        2
    ) AS '账户只出（元）',
    ROUND(
        max(based.order_user_pay) / 100,
        2
    ) AS '用户付款金额（元）',
    ROUND(
        max(based.order_receive_amount) / 100,
        2
    ) AS '回款金额（元）',
    ROUND(
        (
          max(based.order_real_amount) - max(based.order_receive_amount)
        ) / 100,
        2
    ) AS '回款差额（元）',
    GREATEST(
        ifnull(max(now_delivery_time), ''),
        ifnull(max(now_finish_time), '')
    ) AS '回款日期',
    ROUND(
        max(based.force_reward) / 100,
        2
    ),
    ROUND(
        max(based.business_reward) / 100,
        2
    ),
    order_comments AS '促销礼品（油卡等）',
    insurance_current_status AS '保单状态',
    (
      SELECT
        NAME
      FROM
        t_dictionary
      WHERE
        type = 20013
        AND CODE = based.insurance_current_status
    ) AS '保单状态名称',
    max(flow_detail) AS '回款记录',
    insurance_city_id AS '投保城市id',
    insurance_city_name AS '投保城市',
    order_payment_time AS '订单线上支付时间',
    order_pay_style AS '支付方式',
    oem_name AS 'OEM名称',
    oem_payment_mode AS '结算方式',
    GREATEST(
        ifnull(max(now_cancel_time), ''),
        ifnull(max(now_finish_time), '')
    ) AS '结束时间',
    out_trade_no AS '支付唯一订单号',
    ROUND(
        max(force_commission_amount) / 100,
        2
    ),
    ROUND(
        max(business_commission_amount) / 100,
        2
    ),
    MAX(force_rule_formula),
    MAX(business_rule_formula),
    max(force_commission_rate),
    max(business_commission_rate),
    oem_code,
    oem_agency_name,
    ROUND(
        max(
            based.order_original_total_amount
        ) / 100,
        2
    ),
    ROUND(
        max(based.order_other_amount) / 100,
        2
    ),
    ROUND(
        max(
            force_insurance_operations_amount
        ) / 100,
        2
    ),
    ROUND(
        max(
            business_insurance_operations_amount
        ) / 100,
        2
    ),
    MAX(
        force_insurance_operations_formula
    ),
    MAX(
        business_insurance_operations_formula
    ),
    MAX(
        force_insurance_operations_percentage
    ),
    MAX(
        business_insurance_operations_percentage
    ),
    ROUND(
        max(
            insurance_products_payFirst_amount
        ) / 100,
        2
    ),
    express_name,
    delivery_times,
    order_source_type,
    max(force_insurance_end_date),
    max(
        business_insurance_end_date
    ),
    max(force_insurance_start_date),
    max(
        business_insurance_start_date
    ),
    signing_company_name,
    @version_id
  FROM
    (
      SELECT
        based.order_id,
        based.cid,
        based.first_payment_time,
        dianfu.dianfu_comment,
        based.insurance_create_time,
        (
          SELECT
            NAME
          FROM
            t_dictionary
          WHERE
            TYPE = 10026
            AND CODE = based.channel_type
        ) AS channel_type,
        (
          SELECT
            channel_name
          FROM
            t_channel
          WHERE
            channel_id = based.channel_id
        ) AS channel_id,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.activity_name
          ELSE
            NULL
          END
        ) AS force_activity_name,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.activity_name
          ELSE
            NULL
          END
        ) AS business_activity_name,
        based.insurance_supplier_id,
        based.insurance_supplier_name,
        (
          SELECT
            NAME
          FROM
            t_passport
          WHERE
            passport_id = based.insurance_create_op_id
        ) AS insurance_create_op_name,
        car_number,
        insuranced_person_name,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_company_fid
          ELSE
            NULL
          END
        ) AS force_number,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_company_fid
          ELSE
            NULL
          END
        ) AS business_number,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_reviewed_amount
          ELSE
            0
          END
        ) AS force_amount,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_reviewed_amount
          ELSE
            0
          END
        ) AS business_amount,
        ifnull(based.tax_fee, 0) AS tax_fee,
        order_advance_amount,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_products_discount_percentage
          ELSE
            0
          END
        ) AS force_discount,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_products_discount_percentage
          ELSE
            0
          END
        ) AS business_discount,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            (
              based.item_amount - based.item_real_amount - based.coupon_amount
            )
          ELSE
            0
          END
        ) AS force_discount_amount,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            (
              based.item_activity_amount - ifnull(
                  based.insurance_products_payFirst_amount,
                  0
              )
            )
          ELSE
            0
          END
        ) AS business_discount_amount,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.coupon_amount
          ELSE
            0
          END
        ) AS force_coupon_amount,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.coupon_amount
          ELSE
            0
          END
        ) AS business_coupon_amount,
        order_real_amount,
        order_receive_amount,
        now_delivery_time,
        now_finish_time,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_reward
          ELSE
            NULL
          END
        ) AS force_reward,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_reward
          ELSE
            NULL
          END
        ) AS business_reward,
        based.order_comments,
        insurance_current_status,
        hkjl.flow_detail,
        insurance_city_id,
        insurance_city_name,
        order_payment_time,
        (
          SELECT
            NAME
          FROM
            t_dictionary
          WHERE
            type = 10019
            AND CODE = based.order_pay_style
        ) AS order_pay_style,
        oem_name,
        (
          CASE
          WHEN based.oem_payment_mode = 1 THEN
            '净费'
          WHEN based.oem_payment_mode = 2 THEN
            '毛费'
          ELSE
            ''
          END
        ) AS oem_payment_mode,
        based.now_cancel_time,
        pay.out_trade_no,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.commission_amount
          ELSE
            0
          END
        ) AS force_commission_amount,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.commission_amount
          ELSE
            0
          END
        ) AS business_commission_amount,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.rule_formula
          ELSE
            0
          END
        ) AS force_rule_formula,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.rule_formula
          ELSE
            0
          END
        ) AS business_rule_formula,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.commission_rate
          ELSE
            0
          END
        ) AS force_commission_rate,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.commission_rate
          ELSE
            0
          END
        ) AS business_commission_rate,
        based.oem_code,
        based.oem_agency_name,
        based.order_original_total_amount,
        based.order_other_amount,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_operations_amount
          ELSE
            0
          END
        ) AS force_insurance_operations_amount,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_operations_amount
          ELSE
            0
          END
        ) AS business_insurance_operations_amount,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_operations_formula
          ELSE
            0
          END
        ) AS force_insurance_operations_formula,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_operations_formula
          ELSE
            0
          END
        ) AS business_insurance_operations_formula,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_operations_percentage
          ELSE
            0
          END
        ) AS force_insurance_operations_percentage,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_operations_percentage
          ELSE
            0
          END
        ) AS business_insurance_operations_percentage,
        insurance_products_payFirst_amount,
        express_name,
        delivery_times,
        (
          SELECT
            NAME
          FROM
            t_dictionary
          WHERE
            TYPE = 10018
            AND CODE = based.order_source_type
        ) AS order_source_type,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_end_date
          ELSE
            NULL
          END
        ) AS force_insurance_end_date,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_end_date
          ELSE
            NULL
          END
        ) AS business_insurance_end_date,
        (
          CASE
          WHEN based.insurance_ptype = 201 THEN
            based.insurance_start_date
          ELSE
            NULL
          END
        ) AS force_insurance_start_date,
        (
          CASE
          WHEN based.insurance_ptype = 200 THEN
            based.insurance_start_date
          ELSE
            NULL
          END
        ) AS business_insurance_start_date,
        order_amount.extra_amount AS order_extra_amount,
        -- 另收
        (
          CASE
          WHEN order_flow.flow_type = 9 THEN
            order_flow.amount
          ELSE
            NULL
          END
        ) AS order_outcome,
        -- 账户支出: 从用户余额的支出
        (
          CASE
          WHEN order_flow.flow_type = 3 THEN
            order_flow.amount
          ELSE
            NULL
          END
        ) AS order_user_pay, -- 用户付款金额:  用户付款
        oem.signing_company_name
      FROM
        t_st_insurance_based based
        LEFT JOIN t_pay_detail pay ON based.order_id = pay.order_id
        AND pay.request_type = 102 AND pay.status = 0
        JOIN t_order order_amount ON based.order_id = order_amount.order_id
        LEFT JOIN t_order_payment_flow order_flow ON based.order_id = order_flow.order_id
        AND order_flow.flow_type IN (3, 9)
        LEFT JOIN t_oem oem ON based.insurance_guadan_id = oem.id
        LEFT JOIN (
                    SELECT
                      a.insurance_id,
                      IFNULL(
                          GROUP_CONCAT(a.flow_detail),
                          ''
                      ) AS flow_detail
                    FROM
                      (
                        SELECT
                          insurance_id,
                          CONCAT(
                              '收款方式',
                              SUBSTRING_INDEX(
                                  SUBSTRING_INDEX(
                                      flow_detail,
                                      '收款方式',
                                      - 1
                                  ),
                                  '，流程已完成',
                                  1
                              )
                          ) AS flow_detail
                        FROM
                          t_insurance_flow_detail
                        WHERE
                          insurance_status IN (11, 12) AND flow_detail LIKE '%交易号%'
                      ) a
                    GROUP BY
                      a.insurance_id
                  ) hkjl ON based.insurance_id = hkjl.insurance_id
        LEFT JOIN (
                    SELECT
                      a.insurance_id,
                      IFNULL(
                          GROUP_CONCAT(a.flow_detail),
                          ''
                      ) AS dianfu_comment
                    FROM
                      (
                        SELECT
                          insurance_id,
                          CONCAT(
                              SUBSTRING_INDEX(
                                  SUBSTRING_INDEX(
                                      flow_detail,
                                      '意见:',
                                      - 1
                                  ),
                                  "】",
                                  1
                              ), "】"
                          ) AS flow_detail
                        FROM
                          t_insurance_flow_detail
                        WHERE
                          insurance_status = 5 and insurance_status_from = 21 and flow_detail like '%意见%'
                      ) a
                    GROUP BY
                      a.insurance_id
                  ) dianfu ON based.insurance_id = dianfu.insurance_id
      WHERE
        based.first_payment_time IS NOT NULL AND based.insurance_current_status != 10
    ) based
  GROUP BY
    based.order_id
