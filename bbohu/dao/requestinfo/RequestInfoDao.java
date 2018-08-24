/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.dao.requestinfo;

import java.util.List;
import java.util.Map;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.oldnew.entity.requestinfo.RequestInfo;

/**
 * 求带信息DAO接口
 * @author jiangjl
 * @version 2018-05-14
 */
@MyBatisDao
public interface RequestInfoDao extends CrudDao<RequestInfo> {

	//获取求带信息列表
	List<Map<String, Object>> findRequestInfoList(Integer start, Integer num, String status, String companyId);
	
}