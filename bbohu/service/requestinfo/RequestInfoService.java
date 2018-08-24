/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.service.requestinfo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.oldnew.entity.requestinfo.RequestInfo;
import com.jeeplus.modules.oldnew.dao.requestinfo.RequestInfoDao;

/**
 * 求带信息Service
 * @author jiangjl
 * @version 2018-05-14
 */
@Service
@Transactional(readOnly = true)
public class RequestInfoService extends CrudService<RequestInfoDao, RequestInfo> {

	public RequestInfo get(String id) {
		return super.get(id);
	}
	
	public List<RequestInfo> findList(RequestInfo requestInfo) {
		return super.findList(requestInfo);
	}
	
	public Page<RequestInfo> findPage(Page<RequestInfo> page, RequestInfo requestInfo) {
		return super.findPage(page, requestInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(RequestInfo requestInfo) {
		super.save(requestInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(RequestInfo requestInfo) {
		super.delete(requestInfo);
	}

	//获取求带信息列表
	public List<Map<String, Object>> findRequestInfoList(Integer start, Integer num, String status, String companyId) {
		return dao.findRequestInfoList(start,num,status,companyId);
	}
	
	
	
	
}