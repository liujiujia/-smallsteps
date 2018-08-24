/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.service.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.oldnew.entity.order.OldnewOrder;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.talent.dao.TalentInfoDao;
import com.jeeplus.modules.talent.entity.TalentInfo;
import com.jeeplus.modules.oldnew.dao.order.OldnewOrderDao;

/**
 * 订单管理Service
 * @author chenhx
 * @version 2018-05-17
 */
@Service
@Transactional(readOnly = true)
public class OldnewOrderService extends CrudService<OldnewOrderDao, OldnewOrder> {
	
	@Autowired
	private TalentInfoDao zhaomuTalentDao;
	
	@Autowired
	private OldnewOrderDao orderDao;
	
	public OldnewOrder get(String id) {
		return super.get(id);
	}
	
	//数据权限过滤,(三个参数，1.当前用户，2.机构表，3.用户表)
	public Page<OldnewOrder> findList(Page<OldnewOrder> page, OldnewOrder oldnewOrder) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		//anchorInfo.getSqlMap().put("dsf", dataScopeFilter(anchorInfo.getCurrentUser(), "office", ""));
		User user = oldnewOrder.getCurrentUser();
		String sql = "";
		if (!user.isAdmin()){
			// 获取公司ID
			//oldnewOrder.setCompany(user.getCompany());
			//登录用户是经纪人
			if(Global.AGENT.equals(user.getUserType())){
				sql = "i.agent = "+"'"+user.getId()+"'";
			}else if(Global.TALENT.equals(user.getUserType())){
				//登录用户是星探
				TalentInfo zhaomuTalent = zhaomuTalentDao.findUniqueByProperty("mobile",user.getMobile());
				sql = "i.referee = "+zhaomuTalent.getUsercode();
			}else{
				//其它用户根据部门和权限范围进行数据过滤
				oldnewOrder.getSqlMap().put("dsf", dataScopeFilter(oldnewOrder.getCurrentUser(), "office", ""));
				oldnewOrder.setPage(page);
				page.setList(orderDao.findList(oldnewOrder));
				return page;
			}
		}
		if ( !sql.equals("")){
			sql = " AND (" + sql + ")";
		}
		oldnewOrder.getSqlMap().put("dsf", sql);
		// 设置分页参数
		oldnewOrder.setPage(page);
		// 执行分页查询
		page.setList(orderDao.findList(oldnewOrder));
		return page;
	}
	
	public Page<OldnewOrder> findPage(Page<OldnewOrder> page, OldnewOrder oldnewOrder) {
		return super.findPage(page, oldnewOrder);
	}
	
	@Transactional(readOnly = false)
	public void save(OldnewOrder oldnewOrder) {
		super.save(oldnewOrder);
	}
	
	@Transactional(readOnly = false)
	public void delete(OldnewOrder oldnewOrder) {
		super.delete(oldnewOrder);
	}

	//获取当天最大订单号
	public Long getMaxOrderCode(Long min) {
		return dao.getMaxOrderCode(min);
	}

	//获取某用户最近一次的订单
	public OldnewOrder getRecentOrder(String id) {
		return dao.getRecentOrder(id);
	}
	
	//获取当月接单数
	public int getOrderNumByMonth(String userId) {
		return dao.getOrderNumByMonth(userId);
	}
	//查询带人订单
	public List<OldnewOrder> findtakeInfo(String userId) {
		return dao.findtakeInfo(userId);
	}
	//查询求带订单
	public List<OldnewOrder> findRequestInfo(String userId) {
		return dao.findRequestInfo(userId);
	}
	
	//查询两人未付款订单
	public List<OldnewOrder> getUnfinishedOrder(String publisherId,String takerId) {
		return dao.getUnfinishedOrder(publisherId,takerId);
	}
	/**
	 * 根据求带订单ID查询
	 * @param orderId
	 * @return
	 */
	public OldnewOrder getRequestOrderById(String orderId) {
		return dao.getRequestOrderById(orderId);
	}
	/**
	 * 根据带人订单ID查询
	 * @param orderId
	 * @return
	 */
	public OldnewOrder getTakeOrderById(String orderId) {
		return dao.getTakeOrderById(orderId);
	}
	/**
	 * 没人接单只查自己信息
	 * @param id
	 * @return
	 */
	public OldnewOrder getRequestOrderByZiji(String orderId) {
		return dao.getRequestOrderByZiji(orderId);
	}

	public OldnewOrder findById(String id) {
		return dao.findById(id);
	}


}