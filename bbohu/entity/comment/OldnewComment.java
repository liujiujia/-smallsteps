/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.entity.comment;


import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 订单评价Entity
 * @author jiangjl
 * @version 2018-05-22
 */
public class OldnewComment extends DataEntity<OldnewComment> {
	
	private static final long serialVersionUID = 1L;
	private String satisfyLevel;		// 满意度
	private Integer maxPopularity;		// 人气峰值
	private Integer fansUp;		// 粉丝增长
	private Integer duration;		// 持续时间(单位：分)
	private String content;		// 评价内容
	private String label;		// 标签
	private String orderId;		// 订单id
	private String userId;		// 评价人id
	private String type;		// 评价类型  1 求带人评价   2 带人者评价    3 经纪人评价
	
	public OldnewComment() {
		super();
	}

	public OldnewComment(String id){
		super(id);
	}

	@ExcelField(title="满意度", align=2, sort=3)
	public String getSatisfyLevel() {
		return satisfyLevel;
	}

	public void setSatisfyLevel(String satisfyLevel) {
		this.satisfyLevel = satisfyLevel;
	}
	
	@ExcelField(title="人气峰值", align=2, sort=4)
	public Integer getMaxPopularity() {
		return maxPopularity;
	}

	public void setMaxPopularity(Integer maxPopularity) {
		this.maxPopularity = maxPopularity;
	}
	
	@ExcelField(title="粉丝增长", align=2, sort=5)
	public Integer getFansUp() {
		return fansUp;
	}

	public void setFansUp(Integer fansUp) {
		this.fansUp = fansUp;
	}
	
	@ExcelField(title="持续时间", align=2, sort=6)
	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	@ExcelField(title="评价内容", align=2, sort=7)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@ExcelField(title="标签", align=2, sort=8)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@ExcelField(title="订单id", align=2, sort=9)
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@ExcelField(title="评价人id", align=2, sort=10)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@ExcelField(title="评价类型", dictType="oldnew_comment_type", align=2, sort=11)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}