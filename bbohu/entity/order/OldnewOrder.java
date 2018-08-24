/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.entity.order;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import com.jeeplus.modules.sys.entity.Office;

/**
 * 订单管理Entity
 * @author chenhx
 * @version 2018-05-15
 */
public class OldnewOrder extends DataEntity<OldnewOrder> {
	
	private static final long serialVersionUID = 1L;
	private Long orderCode;		// 订单编号
	private BigDecimal orderPrice;		// 订单价格
	private BigDecimal reduceMoney;		// 优惠金额
	private BigDecimal actualMoney;		// 应付金额
	private String status;		// 订单状态
	private String messageid;		// 信息发布ID
	private String publisherId;		// 被带人ID
	private String takerId;		// 带人id
	private Date receiveDate;		// 接单时间
	private Date affirmDate;		// 确认时间
	private String orgerType;		// 订单类型
	private Date overDate;		// 完成时间
	private String paymentStatus;  //支付状态
	private String couponId;   //优惠券id
	private String publisherName; //信息发布人姓名
	private String takerName;    //信息接单人姓名
	private String platform;   //直播平台
	private String orderDesc;   //订单描述
	private String starttime;  //开播时间
	private String endtime;   //结束时间
	private String remarks;   //求带订单备注信息
	private String description; //带人订单备注信息
	private String mobile;     //手机号
	private String commentStatus;  //评论状态
	private int jieDanTime;   //待接单时间
	private int queRenTime;  //待确认时间
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getJieDanTime() {
		return jieDanTime;
	}

	public void setJieDanTime(int jieDanTime) {
		this.jieDanTime = jieDanTime;
	}

	public int getQueRenTime() {
		return queRenTime;
	}

	public void setQueRenTime(int queRenTime) {
		this.queRenTime = queRenTime;
	}

	public String getCommentStatus() {
		return commentStatus;
	}

	public void setCommentStatus(String commentStatus) {
		this.commentStatus = commentStatus;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	@ExcelField(title="信息发布人", align=2, sort=7)
	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}
	
	@ExcelField(title="信息接单人", align=2, sort=8)
	public String getTakerName() {
		return takerName;
	}

	public void setTakerName(String takerName) {
		this.takerName = takerName;
	}

	public OldnewOrder() {
		super();
	}

	public OldnewOrder(String id){
		super(id);
	}

	@ExcelField(title="订单编号", align=2, sort=1)
	public Long getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(Long orderCode) {
		this.orderCode = orderCode;
	}
	
	@ExcelField(title="订单价格", align=2, sort=2)
	public BigDecimal getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}
	
	@ExcelField(title="优惠金额", align=2, sort=3)
	public BigDecimal getReduceMoney() {
		return reduceMoney;
	}

	public void setReduceMoney(BigDecimal reduceMoney) {
		this.reduceMoney = reduceMoney;
	}
	
	@ExcelField(title="应付金额", align=2, sort=4)
	public BigDecimal getActualMoney() {
		return actualMoney;
	}

	public void setActualMoney(BigDecimal actualMoney) {
		this.actualMoney = actualMoney;
	}
	
	@ExcelField(title="订单状态", align=2, sort=5)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@ExcelField(title="信息发布ID", align=2, sort=6)
	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}
	
	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}
	
	public String getTakerId() {
		return takerId;
	}

	public void setTakerId(String takerId) {
		this.takerId = takerId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@ExcelField(title="接单时间", align=2, sort=10)
	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@ExcelField(title="确认时间", align=2, sort=11)
	public Date getAffirmDate() {
		return affirmDate;
	}

	public void setAffirmDate(Date affirmDate) {
		this.affirmDate = affirmDate;
	}
	
	@ExcelField(title="订单类型", align=2, sort=12)
	public String getOrgerType() {
		return orgerType;
	}

	public void setOrgerType(String orgerType) {
		this.orgerType = orgerType;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="完成时间", align=2, sort=13)
	public Date getOverDate() {
		return overDate;
	}

	public void setOverDate(Date overDate) {
		this.overDate = overDate;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	
}