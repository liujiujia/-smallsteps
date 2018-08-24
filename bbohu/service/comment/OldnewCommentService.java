/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.service.comment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.oldnew.entity.comment.OldnewComment;
import com.jeeplus.modules.oldnew.dao.comment.OldnewCommentDao;

/**
 * 订单评价Service
 * @author jiangjl
 * @version 2018-05-22
 */
@Service
@Transactional(readOnly = true)
public class OldnewCommentService extends CrudService<OldnewCommentDao, OldnewComment> {

	public OldnewComment get(String id) {
		return super.get(id);
	}
	
	public List<OldnewComment> findList(OldnewComment oldnewComment) {
		return super.findList(oldnewComment);
	}
	
	public Page<OldnewComment> findPage(Page<OldnewComment> page, OldnewComment oldnewComment) {
		return super.findPage(page, oldnewComment);
	}
	
	@Transactional(readOnly = false)
	public void save(OldnewComment oldnewComment) {
		super.save(oldnewComment);
	}
	
	@Transactional(readOnly = false)
	public void delete(OldnewComment oldnewComment) {
		super.delete(oldnewComment);
	}

	//获取某人某订单评价
	public OldnewComment getOneComment(String orderId, String id, String requesterComment) {
		return dao.getOneComment(orderId, id, requesterComment);
	}

	//获取某订单评价个数
	public int getCommentCountByOrder(String orderId) {
		return dao.getCommentCountByOrder(orderId);
	}
	
	
	
	
}