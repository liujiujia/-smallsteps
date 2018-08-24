/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.dao.takeinfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.api.model.TakeInfoModel;
import com.jeeplus.modules.oldnew.entity.takeinfo.TakeInfo;

/**
 * 带人信息管理DAO接口
 * @author zhaol
 * @version 2018-05-15
 */
@MyBatisDao
public interface TakeInfoDao extends CrudDao<TakeInfo> {
	
	//带人信息列表
	public List<TakeInfoModel> getTakeList(@Param("company")String company,@Param("start")Integer start,@Param("num")Integer num);

	//明星主播列表
	public List<TakeInfoModel> getStarList(@Param("company")String company,@Param("status")String status,@Param("fansAmount")int fansAmount,
			@Param("start")Integer start,@Param("num")Integer num);
	
}