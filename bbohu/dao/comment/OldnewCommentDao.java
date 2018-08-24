/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.dao.comment;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.oldnew.entity.comment.OldnewComment;

/**
 * 订单评价DAO接口
 * @author jiangjl
 * @version 2018-05-22
 */
@MyBatisDao
public interface OldnewCommentDao extends CrudDao<OldnewComment> {

	//获取某人某订单评价
	OldnewComment getOneComment(String orderId, String id, String requesterComment);

	//获取某订单评价个数
	int getCommentCountByOrder(String orderId);

	
}