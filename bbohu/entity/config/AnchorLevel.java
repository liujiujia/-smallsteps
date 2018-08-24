/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.entity.config;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 主播级别Entity
 * @author jiangjl
 * @version 2018-05-16
 */
public class AnchorLevel extends DataEntity<AnchorLevel> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 级别名称
	private String note;		// 说明
	private Integer minFansNum;		// 最小粉丝数
	private Integer maxFansNum;		// 最大粉丝数
	private String level;		// 主播级别（级别越大粉丝越多）
	private BigDecimal takePrice;		// 带新价格
	private Integer takeNum;		// 带新次数
	
	public AnchorLevel() {
		super();
	}

	public AnchorLevel(String id){
		super(id);
	}

	@ExcelField(title="级别名称", align=2, sort=5)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ExcelField(title="说明", align=2, sort=6)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	@NotNull(message="最小粉丝数不能为空")
	@ExcelField(title="最小粉丝数", align=2, sort=7)
	public Integer getMinFansNum() {
		return minFansNum;
	}

	public void setMinFansNum(Integer minFansNum) {
		this.minFansNum = minFansNum;
	}
	
	@ExcelField(title="最大粉丝数", align=2, sort=8)
	public Integer getMaxFansNum() {
		return maxFansNum;
	}

	public void setMaxFansNum(Integer maxFansNum) {
		this.maxFansNum = maxFansNum;
	}
	
	@ExcelField(title="主播级别（级别越大粉丝越多）", align=2, sort=9)
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	@NotNull(message="带新价格不能为空")
	@ExcelField(title="带新价格", align=2, sort=10)
	public BigDecimal getTakePrice() {
		return takePrice;
	}

	public void setTakePrice(BigDecimal takePrice) {
		this.takePrice = takePrice;
	}
	
	@NotNull(message="带新次数不能为空")
	@ExcelField(title="带新次数", align=2, sort=11)
	public Integer getTakeNum() {
		return takeNum;
	}

	public void setTakeNum(Integer takeNum) {
		this.takeNum = takeNum;
	}
	
}