/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.web.requestinfo;

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
import com.jeeplus.modules.oldnew.entity.requestinfo.RequestInfo;
import com.jeeplus.modules.oldnew.service.requestinfo.RequestInfoService;

/**
 * 求带信息Controller
 * @author jiangjl
 * @version 2018-05-14
 */
@Controller
@RequestMapping(value = "${adminPath}/oldnew/requestinfo/requestInfo")
public class RequestInfoController extends BaseController {

	@Autowired
	private RequestInfoService requestInfoService;
	
	@ModelAttribute
	public RequestInfo get(@RequestParam(required=false) String id) {
		RequestInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = requestInfoService.get(id);
		}
		if (entity == null){
			entity = new RequestInfo();
		}
		return entity;
	}
	
	/**
	 * 求带信息列表页面
	 */
	@RequiresPermissions("oldnew:requestinfo:requestInfo:list")
	@RequestMapping(value = {"list", ""})
	public String list(RequestInfo requestInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<RequestInfo> page = requestInfoService.findPage(new Page<RequestInfo>(request, response), requestInfo); 
		model.addAttribute("page", page);
		return "modules/oldnew/requestinfo/requestInfoList";
	}

	/**
	 * 查看，增加，编辑求带信息表单页面
	 */
	@RequiresPermissions(value={"oldnew:requestinfo:requestInfo:view","oldnew:requestinfo:requestInfo:add","oldnew:requestinfo:requestInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(RequestInfo requestInfo, Model model) {
		model.addAttribute("requestInfo", requestInfo);
		return "modules/oldnew/requestinfo/requestInfoForm";
	}

	/**
	 * 保存求带信息
	 */
	@RequiresPermissions(value={"oldnew:requestinfo:requestInfo:add","oldnew:requestinfo:requestInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(RequestInfo requestInfo, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, requestInfo)){
			return form(requestInfo, model);
		}
		if(!requestInfo.getIsNewRecord()){//编辑表单保存
			RequestInfo t = requestInfoService.get(requestInfo.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(requestInfo, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			requestInfoService.save(t);//保存
		}else{//新增表单保存
			requestInfoService.save(requestInfo);//保存
		}
		addMessage(redirectAttributes, "保存求带信息成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/requestinfo/requestInfo/?repage";
	}
	
	/**
	 * 删除求带信息
	 */
	@RequiresPermissions("oldnew:requestinfo:requestInfo:del")
	@RequestMapping(value = "delete")
	public String delete(RequestInfo requestInfo, RedirectAttributes redirectAttributes) {
		requestInfoService.delete(requestInfo);
		addMessage(redirectAttributes, "删除求带信息成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/requestinfo/requestInfo/?repage";
	}
	
	/**
	 * 批量删除求带信息
	 */
	@RequiresPermissions("oldnew:requestinfo:requestInfo:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			requestInfoService.delete(requestInfoService.get(id));
		}
		addMessage(redirectAttributes, "删除求带信息成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/requestinfo/requestInfo/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("oldnew:requestinfo:requestInfo:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(RequestInfo requestInfo, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "求带信息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<RequestInfo> page = requestInfoService.findPage(new Page<RequestInfo>(request, response, -1), requestInfo);
    		new ExportExcel("求带信息", RequestInfo.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出求带信息记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/requestinfo/requestInfo/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oldnew:requestinfo:requestInfo:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<RequestInfo> list = ei.getDataList(RequestInfo.class);
			for (RequestInfo requestInfo : list){
				try{
					requestInfoService.save(requestInfo);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条求带信息记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条求带信息记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入求带信息失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/requestinfo/requestInfo/?repage";
    }
	
	/**
	 * 下载导入求带信息数据模板
	 */
	@RequiresPermissions("oldnew:requestinfo:requestInfo:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "求带信息数据导入模板.xlsx";
    		List<RequestInfo> list = Lists.newArrayList(); 
    		new ExportExcel("求带信息数据", RequestInfo.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/requestinfo/requestInfo/?repage";
    }
	
	
	

}