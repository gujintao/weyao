package com.weyao.srv.dao.sale.mapper;

import com.weyao.srv.entity.Sale;
import com.weyao.srv.entity.XProductUser;

import java.util.List;


public interface XProductUserMapper {

	List<XProductUser> listXProductUser();

	int updateXProductUser(XProductUser xProductUser);

}
