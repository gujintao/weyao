package com.weyao.srv.task;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.dsf.platform.jobs.job.JobResult;
import com.weyao.common.JsonHelper2;
import com.weyao.exception.SrvException;
import com.weyao.info.dataproxy.ResultType;
import com.weyao.info.insurance.Insurance;
import com.weyao.srv.dao.sale.mapper.SaleLogMapper;
import com.weyao.srv.dataproxy.DataProxySrv;
import com.weyao.srv.entity.Sale;
import com.weyao.srv.entity.SaleUrl;
import com.weyao.srv.entity.SqlStructure;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


public class SaleLogTask extends AbstractTimerTask {

    private static Logger logger = LoggerFactory.getLogger(SaleLogTask.class);

    @Autowired
    private SaleLogMapper saleLogMapper;
    @Autowired
    private DataProxySrv dataProxySrv;

    @Override
    public JobResult handleBiz(String[] arg0) {
        StringBuilder sb = new StringBuilder();
        JobResult result = new JobResult();
        try {
            result.setSendMail(true);
            logger.info("统计网销报表数据开始\n");
            sb.append("--------------------渠道统计开始--------------------\n");
            List<SaleUrl> list = saleLogMapper.getAllBatchUrlInfo();
            for (SaleUrl saleUrl : list) {

                int customerNum = saleLogMapper.getCustomerNum(saleUrl.getSaleId(), saleUrl.getBatchId());
                int calcNum = saleLogMapper.getCalcNum(saleUrl.getSaleId(), saleUrl.getBatchId());

                int orderNum = getOrderNum(saleUrl.getSaleId(), saleUrl.getBatchId());
                int orderSuccNum = getOrderSucNum(saleUrl.getSaleId(), saleUrl.getBatchId());

                int batchNum = saleLogMapper.getBatchNum(saleUrl.getSaleId());

                if (customerNum >= saleUrl.getCustomerNum() && calcNum >= saleUrl.getCalcNum() && orderNum >= saleUrl.getOrderNum()) {
                    saleUrl.setCustomerNum(customerNum);
                    saleUrl.setCalcNum(calcNum);
                    saleUrl.setOrderNum(orderNum);
                    saleUrl.setOrderSuccNum(orderSuccNum);
                    saleUrl.setBatchNum(batchNum);
                    saleLogMapper.updateSaleUrl(saleUrl);
                    sb.append("主题id为" + saleUrl.getSaleId() +"渠道id为" + saleUrl.getBatchId() + "的统计结果为\n");
                    sb.append("customerNum :" + customerNum + ",calcNum:" + calcNum + ",orderNum:" + orderNum + ",orderSuccNum:" + orderSuccNum +"\n");
                    logger.info("网销报表数据saleUrl:" + JsonHelper2.toJson(saleUrl) + "-每个渠道");
                } else {
                    logger.info("该渠道统计有误,batchId:" + saleUrl.getBatchId());
                }
            }
            sb.append("--------------------渠道统计结束--------------------\n");
            sb.append("--------------------主题统计开始--------------------\n");
            List<Sale> saleList = saleLogMapper.getAllSaleInfo();

            for (Sale sale : saleList) {

                int customerNum = saleLogMapper.getCustomerNum(sale.getSaleId(), null);
                int calcNum = saleLogMapper.getCalcNum(sale.getSaleId(), null);
                int orderSuccNum = getOrderSucNum(sale.getSaleId());
                int batchNum = saleLogMapper.getBatchNum(sale.getSaleId());

                if (customerNum >= sale.getCustomerNum() && calcNum >= sale.getCalcNum() && orderSuccNum >= sale.getOrderSuccNum()) {
                    sale.setCustomerNum(customerNum);
                    sale.setCalcNum(calcNum);
                    sale.setOrderNum(orderSuccNum);
                    sale.setBatchNum(batchNum);
                    sale.setStatus(batchNum == 0 ? (byte)0 : (byte)1);
                    saleLogMapper.updateSale(sale);
                    sb.append("主题id为" + sale.getSaleId() + "的统计结果为\n");
                    sb.append("customerNum :" + customerNum + ",calcNum:" + calcNum + ",orderSuccNum:" + orderSuccNum + ",batchNum:" + batchNum + "\n");
                    logger.info("网销报表数据sale:" + JsonHelper2.toJson(sale) + "");
                }
            }
            sb.append("--------------------主题统计结束--------------------\n");
            result.setMessage(sb.toString());
            result.setSuccess(true);
            logger.info("统计网销报表数据成功");
        } catch (Exception e) {
            logger.error("统计网销报表数据出错", e);
        }
        return result;
    }

    private int getOrderSucNum(Long saleId, int batchId) throws SrvException {
        StringBuffer 成单数sql = new StringBuffer("SELECT count(distinct o.order_id) FROM t_order o LEFT JOIN t_sale_record sr on sr.car_id=o.car_id ");
        SqlStructure sql = new SqlStructure(成单数sql);
        List<Object> objqaram = new ArrayList<Object>();
        sql.addAndParam(SqlStructure.Symbol.AND, "sr.sale_id = ?");
        objqaram.add(saleId);
        sql.addAndParam(SqlStructure.Symbol.AND, "sr.batch_id = ?");
        objqaram.add(batchId);
        //保单状态 具体参看Insurance
        sql.addAndParam(SqlStructure.Symbol.AND, "o.status = 10");
        return Integer.parseInt(dataProxySrv.excuteQuery(sql.getCompleteSql(), ResultType.VALUE, objqaram));
    }

    private int getOrderNum(Long saleId, int batchId) throws SrvException { //getOrderSucNum
        StringBuffer 订单数sql = new StringBuffer("SELECT count(distinct o.order_id) FROM t_order o LEFT JOIN t_sale_record sr on sr.car_id=o.car_id ");
        SqlStructure sql = new SqlStructure(订单数sql);
        List<Object> objqaram = new ArrayList<Object>();
        sql.addAndParam(SqlStructure.Symbol.AND, "sr.sale_id = ?");
        objqaram.add(saleId);
        sql.addAndParam(SqlStructure.Symbol.AND, "sr.batch_id = ?");
        objqaram.add(batchId);
        //保单状态 具体参看Insurance
        sql.addAndParam(SqlStructure.Symbol.AND, "o.status != -10");
        return Integer.parseInt(dataProxySrv.excuteQuery(sql.getCompleteSql(), ResultType.VALUE, objqaram));
    }

    private int getOrderSucNum(Long saleId) throws SrvException {
        StringBuffer 成单数sql = new StringBuffer("SELECT count(distinct o.order_id) FROM t_order o LEFT JOIN t_sale_record sr on sr.car_id=o.car_id ");
        SqlStructure sql = new SqlStructure(成单数sql);
        List<Object> objqaram = new ArrayList<Object>();
        sql.addAndParam(SqlStructure.Symbol.AND, "sr.sale_id = ?");
        objqaram.add(saleId);
        //保单状态 具体参看Insurance
        sql.addAndParam(SqlStructure.Symbol.AND, "o.status=10");
        return Integer.parseInt(dataProxySrv.excuteQuery(sql.getCompleteSql(), ResultType.VALUE, objqaram));
    }
}
