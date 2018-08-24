/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.entity.takeinfo;


import java.math.BigDecimal;

import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 带人信息管理Entity
 * @author zhaol
 * @version 2018-05-15
 */
public class TakeInfo extends DataEntity<TakeInfo> {
	
	private static final long serialVersionUID = 1L;
	private String pulisherId;		// 信息发布人
	private String nickname;		// 主播昵称
	private String funsNum;		// 粉丝要求数
	private String platformcode;		// 直播平台
	private String timeSection;		// 要求时段
	private BigDecimal price;		// 带人价格
	private String takedesc;       //描述
	private Integer monthOrdernum;		// 每月接单数
	private String status;		// 是否马上接单
	private String publishType;  //发布类别
	
	public TakeInfo() {
		super();
	}

	public TakeInfo(String id){
		super(id);
	}

	@ExcelField(title="信息发布人", align=2, sort=7)
	public String getPulisherId() {
		return pulisherId;
	}

	public void setPulisherId(String pulisherId) {
		this.pulisherId = pulisherId;
	}
	
	@ExcelField(title="主播昵称", align=2, sort=8)
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	@ExcelField(title="粉丝要求数", align=2, sort=9)
	public String getFunsNum() {
		return funsNum;
	}

	public void setFunsNum(String funsNum) {
		this.funsNum = funsNum;
	}
	
	@ExcelField(title="直播平台", dictType="platform", align=2, sort=10)
	public String getPlatformcode() {
		return platformcode;
	}

	public void setPlatformcode(String platformcode) {
		this.platformcode = platformcode;
	}
	
	@ExcelField(title="要求时段", align=2, sort=11)
	public String getTimeSection() {
		return timeSection;
	}

	public void setTimeSection(String timeSection) {
		this.timeSection = timeSection;
	}
	
	@ExcelField(title="带人价格", align=2, sort=12)
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public String getTakedesc() {
		return takedesc;
	}

	public void setTakedesc(String takedesc) {
		this.takedesc = takedesc;
	}

	@ExcelField(title="每月接单数", align=2, sort=13)
	public Integer getMonthOrdernum() {
		return monthOrdernum;
	}

	public void setMonthOrdernum(Integer monthOrdernum) {
		this.monthOrdernum = monthOrdernum;
	}
	
	@ExcelField(title="是否马上接单", dictType="yes_no", align=2, sort=14)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}
	
	
}