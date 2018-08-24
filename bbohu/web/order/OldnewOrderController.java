/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.web.order;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.anchor.service.AnchorInfoService;
import com.jeeplus.modules.api.entity.UserApi;
import com.jeeplus.modules.api.model.AnchorModelAdmin;
import com.jeeplus.modules.api.service.UserApiService;
import com.jeeplus.modules.oldnew.entity.comment.OldnewComment;
import com.jeeplus.modules.oldnew.entity.order.OldnewOrder;
import com.jeeplus.modules.oldnew.service.comment.OldnewCommentService;
import com.jeeplus.modules.oldnew.service.order.OldnewOrderService;

/**
 * 订单管理Controller
 * @author chenhx
 * @version 2018-05-17
 */
@Controller
@RequestMapping(value = "${adminPath}/oldnew/order/oldnewOrder")
public class OldnewOrderController extends BaseController {

	@Autowired
	private OldnewOrderService oldnewOrderService;
	
	@Autowired
	private OldnewCommentService commentService;
	
	@Autowired
	private UserApiService userService;
	
	@ModelAttribute
	public OldnewOrder get(@RequestParam(required=false) String id) {
		OldnewOrder entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = oldnewOrderService.get(id);
		}
		if (entity == null){
			entity = new OldnewOrder();
		}
		return entity;
	}
	
	/**
	 * 订单管理列表页面
	 */
	@RequiresPermissions("oldnew:order:oldnewOrder:list")
	@RequestMapping(value = {"list", ""})
	public String list(AnchorModelAdmin anchorInfo,OldnewOrder oldnewOrder, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<OldnewOrder> page = oldnewOrderService.findList(new Page<OldnewOrder>(request, response), oldnewOrder);
		List<UserApi> list = userService.findList(new UserApi());
		for (int i = 0; i < list.size(); i++) {
			if(StringUtils.isEmpty(list.get(i).getNickname())) {
				list.remove(i);
			}
		}
		model.addAttribute("user", list);
		model.addAttribute("page", page);
		return "modules/oldnew/order/oldnewOrderList";
	}

	/**
	 * 查看，增加，编辑订单管理表单页面
	 */
	@RequiresPermissions(value={"oldnew:order:oldnewOrder:view","oldnew:order:oldnewOrder:add","oldnew:order:oldnewOrder:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(OldnewOrder oldnewOrder, Model model) {
		model.addAttribute("oldnewOrder", oldnewOrder);
		return "modules/oldnew/order/oldnewOrderForm";
	}

	/**
	 * 保存订单管理
	 */
	@RequiresPermissions(value={"oldnew:order:oldnewOrder:add","oldnew:order:oldnewOrder:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(OldnewOrder oldnewOrder, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, oldnewOrder)){
			return form(oldnewOrder, model);
		}
		if(!oldnewOrder.getIsNewRecord()){//编辑表单保存
			OldnewOrder t = oldnewOrderService.get(oldnewOrder.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(oldnewOrder, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			oldnewOrderService.save(t);//保存
		}else{//新增表单保存
			oldnewOrderService.save(oldnewOrder);//保存
		}
		addMessage(redirectAttributes, "保存订单管理成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/order/oldnewOrder/?repage";
	}
	
	/**
	 * 查看订单评论
	 */
	@RequestMapping(value = "getComment")
	@ResponseBody
	public AjaxJson getComment(OldnewOrder oldnewOrder) {
		AjaxJson ajaxJson = new AjaxJson();
		OldnewComment comment = new OldnewComment();
		comment.setOrderId(oldnewOrder.getId());
		List<OldnewComment> list = commentService.findList(comment);
		ajaxJson.setSuccess(true);
		ajaxJson.put("comment", list);
		return ajaxJson;
	}
	
	/**
	 * 批量删除订单管理
	 */
	@RequiresPermissions("oldnew:order:oldnewOrder:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			oldnewOrderService.delete(oldnewOrderService.get(id));
		}
		addMessage(redirectAttributes, "删除订单管理成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/order/oldnewOrder/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("oldnew:order:oldnewOrder:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(OldnewOrder oldnewOrder, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "订单管理"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<OldnewOrder> page = oldnewOrderService.findPage(new Page<OldnewOrder>(request, response, -1), oldnewOrder);
    		new ExportExcel("订单管理", OldnewOrder.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出订单管理记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/order/oldnewOrder/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oldnew:order:oldnewOrder:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<OldnewOrder> list = ei.getDataList(OldnewOrder.class);
			for (OldnewOrder oldnewOrder : list){
				try{
					oldnewOrderService.save(oldnewOrder);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条订单管理记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条订单管理记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入订单管理失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/order/oldnewOrder/?repage";
    }
	
	/**
	 * 下载导入订单管理数据模板
	 */
	@RequiresPermissions("oldnew:order:oldnewOrder:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "订单管理数据导入模板.xlsx";
    		List<OldnewOrder> list = Lists.newArrayList(); 
    		new ExportExcel("订单管理数据", OldnewOrder.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/order/oldnewOrder/?repage";
    }
	
	
	

}