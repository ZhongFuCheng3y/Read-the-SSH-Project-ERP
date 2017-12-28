package cn.itcast.erp.invoice.bill.dao.dao;

import java.util.List;

import cn.itcast.erp.invoice.bill.vo.BillQueryModel;
import cn.itcast.erp.invoice.orderdetail.vo.OrderDetailModel;

public interface BillDao{

	public List<Object[]> getBuyBill(BillQueryModel bqm);

	public List<OrderDetailModel> getBuyBillDetail(BillQueryModel bqm);

}
