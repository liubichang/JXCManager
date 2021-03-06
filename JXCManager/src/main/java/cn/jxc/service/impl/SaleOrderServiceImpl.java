package cn.jxc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.jxc.mapper.SalesOrderMapper;
import cn.jxc.pojo.SalesOrder;
import cn.jxc.service.SaleOrderService;

public class SaleOrderServiceImpl implements SaleOrderService {

	@Autowired
	private SalesOrderMapper saleMap;
	
	//查询所有
	@Override
	public List<SalesOrder> getSaleAll() {
		// TODO Auto-generated method stub
		return saleMap.getSaleAll();
	}

	//按条件查询
	@Override
	public SalesOrder salesorderbyId(String soId) {
		// TODO Auto-generated method stub
		return saleMap.salesorderbyId(soId);
	}

	//新增
	@Override
	public int SalesAdd(SalesOrder salesOrder) {
		// TODO Auto-generated method stub
		return 0;
	}

	//修改
	@Override
	public int SalesUpdate(SalesOrder salesOrder) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
