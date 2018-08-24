/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.service.mycoupon;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.oldnew.entity.mycoupon.MyCoupon;
import com.jeeplus.modules.oldnew.dao.mycoupon.MyCouponDao;

/**
 * 我的优惠券Service
 * @author jiangjl
 * @version 2018-05-18
 */
@Service
@Transactional(readOnly = true)
public class MyCouponService extends CrudService<MyCouponDao, MyCoupon> {

	public MyCoupon get(String id) {
		return super.get(id);
	}
	
	public List<MyCoupon> findList(MyCoupon myCoupon) {
		return super.findList(myCoupon);
	}
	
	public Page<MyCoupon> findPage(Page<MyCoupon> page, MyCoupon myCoupon) {
		return super.findPage(page, myCoupon);
	}
	
	@Transactional(readOnly = false)
	public void save(MyCoupon myCoupon) {
		super.save(myCoupon);
	}
	
	@Transactional(readOnly = false)
	public void delete(MyCoupon myCoupon) {
		super.delete(myCoupon);
	}

	//查询用户可用老带新优惠券
	public List<MyCoupon> findUseableByUser(String id, String unused, String orderCoupon) {
		return dao.findUseableByUser(id, unused,orderCoupon);
	}

	public Page<MyCoupon> findAllList(Page<MyCoupon> page, MyCoupon myCoupon) {
		// TODO Auto-generated method stub
		myCoupon.setPage(page);
		return page.setList(dao.findAllList(myCoupon));
	}
	
	
	
	
}