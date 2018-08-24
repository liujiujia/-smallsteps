/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.service.config;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.oldnew.entity.config.AnchorLevel;
import com.jeeplus.modules.oldnew.dao.config.AnchorLevelDao;

/**
 * 主播级别Service
 * @author jiangjl
 * @version 2018-05-16
 */
@Service
@Transactional(readOnly = true)
public class AnchorLevelService extends CrudService<AnchorLevelDao, AnchorLevel> {

	public AnchorLevel get(String id) {
		return super.get(id);
	}
	
	public List<AnchorLevel> findList(AnchorLevel anchorLevel) {
		return super.findList(anchorLevel);
	}
	
	public Page<AnchorLevel> findPage(Page<AnchorLevel> page, AnchorLevel anchorLevel) {
		return super.findPage(page, anchorLevel);
	}
	
	@Transactional(readOnly = false)
	public void save(AnchorLevel anchorLevel) {
		super.save(anchorLevel);
	}
	
	@Transactional(readOnly = false)
	public void delete(AnchorLevel anchorLevel) {
		super.delete(anchorLevel);
	}
	
	
	
	
}