/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.entity.mycoupon;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import com.jeeplus.modules.sys.entity.Office;

/**
 * 我的优惠券Entity
 * @author jiangjl
 * @version 2018-05-18
 */
public class MyCoupon extends DataEntity<MyCoupon> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 优惠券名称
	private BigDecimal price;		// 优惠券价格
	private Date getime;		// 获取时间
	private Date disableDate;		// 截止时间
	private String status;		// 优惠券状态
	private String userId;		// 用户id
	private String userName;    //用户姓名
	private String couponType;   //优惠券类型
	private Office company;       //公司ID
	
	@JsonIgnore
	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}
	
	public String getCouponType() {
		return couponType;
	}

	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public MyCoupon() {
		super();
	}

	public MyCoupon(String id){
		super(id);
	}

	@ExcelField(title="优惠券名称", align=2, sort=1)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ExcelField(title="优惠券价格", align=2, sort=2)
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="获取时间", align=2, sort=3)
	public Date getGetime() {
		return getime;
	}

	public void setGetime(Date getime) {
		this.getime = getime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="截止时间", align=2, sort=4)
	public Date getDisableDate() {
		return disableDate;
	}

	public void setDisableDate(Date disableDate) {
		this.disableDate = disableDate;
	}
	
	@ExcelField(title="优惠券状态", dictType="is_used", align=2, sort=5)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@ExcelField(title="用户id", align=2, sort=6)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}