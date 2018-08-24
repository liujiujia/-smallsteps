/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.dao.config;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.oldnew.entity.config.AnchorLevel;

/**
 * 主播级别DAO接口
 * @author jiangjl
 * @version 2018-05-16
 */
@MyBatisDao
public interface AnchorLevelDao extends CrudDao<AnchorLevel> {

	
}