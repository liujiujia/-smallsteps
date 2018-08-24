/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.dao.mycoupon;

import java.util.List;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.oldnew.entity.mycoupon.MyCoupon;

/**
 * 我的优惠券DAO接口
 * @author jiangjl
 * @version 2018-05-18
 */
@MyBatisDao
public interface MyCouponDao extends CrudDao<MyCoupon> {

	//查询用户可用优惠券
	List<MyCoupon> findUseableByUser(String id, String unused, String orderCoupon);

	
}