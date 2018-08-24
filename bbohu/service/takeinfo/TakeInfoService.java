/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.service.takeinfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.oldnew.entity.takeinfo.TakeInfo;
import com.jeeplus.modules.api.model.TakeInfoModel;
import com.jeeplus.modules.oldnew.dao.takeinfo.TakeInfoDao;

/**
 * 带人信息管理Service
 * @author zhaol
 * @version 2018-05-15
 */
@Service
@Transactional(readOnly = true)
public class TakeInfoService extends CrudService<TakeInfoDao, TakeInfo> {
	
	@Autowired
	private TakeInfoDao takeInfoDao;

	public TakeInfo get(String id) {
		return super.get(id);
	}
	
	public List<TakeInfo> findList(TakeInfo takeInfo) {
		return super.findList(takeInfo);
	}
	
	public Page<TakeInfo> findPage(Page<TakeInfo> page, TakeInfo takeInfo) {
		return super.findPage(page, takeInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(TakeInfo takeInfo) {
		super.save(takeInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(TakeInfo takeInfo) {
		super.delete(takeInfo);
	}
	
	//带人信息列表
	public List<TakeInfoModel> getTakeList(String company,Integer start,Integer num){
		return takeInfoDao.getTakeList(company,start,num);	
	}
	
	//明星主播列表
	public List<TakeInfoModel> getStarList(String company,String status,int fansAmount,Integer start,Integer num){
		return takeInfoDao.getStarList(company,status,fansAmount,start,num);	
	}
	
}