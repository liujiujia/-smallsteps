/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.web.comment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.oldnew.entity.comment.OldnewComment;
import com.jeeplus.modules.oldnew.service.comment.OldnewCommentService;
import com.jeeplus.modules.sys.entity.User;

/**
 * 订单评价Controller
 * @author jiangjl
 * @version 2018-05-22
 */
@Controller
@RequestMapping(value = "${adminPath}/oldnew/comment/oldnewComment")
public class OldnewCommentController extends BaseController {

	@Autowired
	private OldnewCommentService oldnewCommentService;
	
	@ModelAttribute
	public OldnewComment get(@RequestParam(required=false) String id) {
		OldnewComment entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = oldnewCommentService.get(id);
		}
		if (entity == null){
			entity = new OldnewComment();
		}
		return entity;
	}
	
	/**
	 * 订单评价列表页面
	 */
	@RequiresPermissions("oldnew:comment:oldnewComment:list")
	@RequestMapping(value = {"list", ""})
	public String list(OldnewComment oldnewComment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OldnewComment> page = oldnewCommentService.findPage(new Page<OldnewComment>(request, response), oldnewComment); 
		model.addAttribute("page", page);
		return "modules/oldnew/comment/oldnewCommentList";
	}

	/**
	 * 查看，增加，编辑订单评价表单页面
	 */
	@RequestMapping(value = "form")
	public String form(OldnewComment oldnewComment, Model model) {
		User user = oldnewComment.getCurrentUser();
		oldnewComment.setUserId(user.getId());
		model.addAttribute("oldnewComment", oldnewComment);
		return "modules/oldnew/order/oldnewCommentForm";
	}
	
	/**
	 * 保存订单评价
	 */
	@RequestMapping(value = "save")
	public String save(OldnewComment oldnewComment, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, oldnewComment)){
			return form(oldnewComment, model);
		}
		if(!oldnewComment.getIsNewRecord()){//编辑表单保存
			OldnewComment t = oldnewCommentService.get(oldnewComment.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(oldnewComment, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			oldnewCommentService.save(t);//保存
		}else{//新增表单保存
			oldnewCommentService.save(oldnewComment);//保存
		}
		addMessage(redirectAttributes, "保存订单评价成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/order/oldnewOrder/?repage";
	}
	
	/**
	 * 删除订单评价
	 */
	@RequiresPermissions("oldnew:comment:oldnewComment:del")
	@RequestMapping(value = "delete")
	public String delete(OldnewComment oldnewComment, RedirectAttributes redirectAttributes) {
		oldnewCommentService.delete(oldnewComment);
		addMessage(redirectAttributes, "删除订单评价成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/comment/oldnewComment/?repage";
	}
	
	/**
	 * 批量删除订单评价
	 */
	@RequiresPermissions("oldnew:comment:oldnewComment:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			oldnewCommentService.delete(oldnewCommentService.get(id));
		}
		addMessage(redirectAttributes, "删除订单评价成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/comment/oldnewComment/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("oldnew:comment:oldnewComment:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(OldnewComment oldnewComment, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "订单评价"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<OldnewComment> page = oldnewCommentService.findPage(new Page<OldnewComment>(request, response, -1), oldnewComment);
    		new ExportExcel("订单评价", OldnewComment.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出订单评价记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/comment/oldnewComment/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oldnew:comment:oldnewComment:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<OldnewComment> list = ei.getDataList(OldnewComment.class);
			for (OldnewComment oldnewComment : list){
				try{
					oldnewCommentService.save(oldnewComment);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条订单评价记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条订单评价记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入订单评价失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/comment/oldnewComment/?repage";
    }
	
	/**
	 * 下载导入订单评价数据模板
	 */
	@RequiresPermissions("oldnew:comment:oldnewComment:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "订单评价数据导入模板.xlsx";
    		List<OldnewComment> list = Lists.newArrayList(); 
    		new ExportExcel("订单评价数据", OldnewComment.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/comment/oldnewComment/?repage";
    }
	
	
	

}