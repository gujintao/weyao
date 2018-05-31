package com.weyao.srv.dao.sale.mapper;

import com.weyao.srv.entity.Sale;
import com.weyao.srv.entity.SaleUrl;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SaleLogMapper {

	List<Sale> getAllSaleInfo();

	List<SaleUrl> getAllBatchUrlInfo();
	//获客数
	int getCustomerNum(@Param(value = "saleId") Long saleId, @Param(value = "batchId") Integer batchId);
	//算价数
	int getCalcNum(@Param(value = "saleId") Long saleId, @Param(value = "batchId") Integer batchId);
	//渠道数
	int getBatchNum(Long saleId);

	int updateSale(Sale sale);

	int updateSaleUrl(SaleUrl saleUrl);

}
