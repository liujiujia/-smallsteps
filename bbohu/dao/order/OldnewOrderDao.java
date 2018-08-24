/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.dao.order;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.oldnew.entity.order.OldnewOrder;

/**
 * 订单管理DAO接口
 * @author chenhx
 * @version 2018-05-17
 */
@MyBatisDao
public interface OldnewOrderDao extends CrudDao<OldnewOrder> {

	//获取当天最大订单号
	Long getMaxOrderCode(Long min);

	//获取某用户最近一次的订单
	OldnewOrder getRecentOrder(String id);

	//获取当月接单数
	int getOrderNumByMonth(String userId);
	
	//查询两人未付款订单
	List<OldnewOrder> getUnfinishedOrder(@Param("publisherId") String publisherId,@Param("takerId") String takerId);
	
	//查询带人订单
	List<OldnewOrder> findtakeInfo(@Param("userId") String userId);
	//查询求带订单
	List<OldnewOrder> findRequestInfo(@Param("userId") String userId);
	/**
	 * 根据求带订单ID查询
	 * @param orderId
	 * @return
	 */
	OldnewOrder getRequestOrderById(String orderId);
	/**
	 * 根据带人订单ID查询
	 * @param orderId
	 * @return
	 */
	OldnewOrder getTakeOrderById(String orderId);
	/**
	 * 没人接单只查自己信息
	 * @param orderId
	 * @return
	 */
	OldnewOrder getRequestOrderByZiji(String orderId);

	OldnewOrder findById(String id);

}