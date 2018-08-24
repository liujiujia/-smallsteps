/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.entity.requestinfo;

import com.jeeplus.modules.anchor.entity.AnchorInfo;
import javax.validation.constraints.NotNull;

import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 求带信息Entity
 * @author jiangjl
 * @version 2018-05-14
 */
public class RequestInfo extends DataEntity<RequestInfo> {
	
	private static final long serialVersionUID = 1L;
	private String pulisherId;		// 信息发布人
	private String nickname;		// 主播昵称
	private String anchorLevelId;		// 要求主播级别id
	private String platformcode;		// 直播平台
	private String timeSection;		// 要求时段
	private String description;		// 描述
	private String publishType;     //发布类别
	
	public RequestInfo() {
		super();
	}

	public RequestInfo(String id){
		super(id);
	}
	
	@ExcelField(title="主播昵称", align=2, sort=2)
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	@ExcelField(title="直播平台", align=2, sort=4)
	public String getPlatformcode() {
		return platformcode;
	}

	public void setPlatformcode(String platformcode) {
		this.platformcode = platformcode;
	}
	
	@ExcelField(title="要求时段", align=2, sort=5)
	public String getTimeSection() {
		return timeSection;
	}

	public void setTimeSection(String timeSection) {
		this.timeSection = timeSection;
	}
	
	@ExcelField(title="描述", align=2, sort=6)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPulisherId() {
		return pulisherId;
	}

	public void setPulisherId(String pulisherId) {
		this.pulisherId = pulisherId;
	}

	public String getAnchorLevelId() {
		return anchorLevelId;
	}

	public void setAnchorLevelId(String anchorLevelId) {
		this.anchorLevelId = anchorLevelId;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}
	
}