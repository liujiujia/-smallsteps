/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.web.takeinfo;

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
import com.jeeplus.modules.oldnew.entity.takeinfo.TakeInfo;
import com.jeeplus.modules.oldnew.service.takeinfo.TakeInfoService;

/**
 * 带人信息管理Controller
 * @author zhaol
 * @version 2018-05-15
 */
@Controller
@RequestMapping(value = "${adminPath}/oldnew/takeinfo/takeInfo")
public class TakeInfoController extends BaseController {

	@Autowired
	private TakeInfoService takeInfoService;
	
	@ModelAttribute
	public TakeInfo get(@RequestParam(required=false) String id) {
		TakeInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = takeInfoService.get(id);
		}
		if (entity == null){
			entity = new TakeInfo();
		}
		return entity;
	}
	
	/**
	 * 带人信息列表页面
	 */
	@RequiresPermissions("oldnew:takeinfo:takeInfo:list")
	@RequestMapping(value = {"list", ""})
	public String list(TakeInfo takeInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TakeInfo> page = takeInfoService.findPage(new Page<TakeInfo>(request, response), takeInfo); 
		model.addAttribute("page", page);
		return "modules/oldnew/takeinfo/takeInfoList";
	}

	/**
	 * 查看，增加，编辑带人信息表单页面
	 */
	@RequiresPermissions(value={"oldnew:takeinfo:takeInfo:view","oldnew:takeinfo:takeInfo:add","oldnew:takeinfo:takeInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(TakeInfo takeInfo, Model model) {
		model.addAttribute("takeInfo", takeInfo);
		return "modules/oldnew/takeinfo/takeInfoForm";
	}

	/**
	 * 保存带人信息
	 */
	@RequiresPermissions(value={"oldnew:takeinfo:takeInfo:add","oldnew:takeinfo:takeInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(TakeInfo takeInfo, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, takeInfo)){
			return form(takeInfo, model);
		}
		if(!takeInfo.getIsNewRecord()){//编辑表单保存
			TakeInfo t = takeInfoService.get(takeInfo.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(takeInfo, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			takeInfoService.save(t);//保存
		}else{//新增表单保存
			takeInfoService.save(takeInfo);//保存
		}
		addMessage(redirectAttributes, "保存带人信息成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/takeinfo/takeInfo/?repage";
	}
	
	/**
	 * 删除带人信息
	 */
	@RequiresPermissions("oldnew:takeinfo:takeInfo:del")
	@RequestMapping(value = "delete")
	public String delete(TakeInfo takeInfo, RedirectAttributes redirectAttributes) {
		takeInfoService.delete(takeInfo);
		addMessage(redirectAttributes, "删除带人信息成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/takeinfo/takeInfo/?repage";
	}
	
	/**
	 * 批量删除带人信息
	 */
	@RequiresPermissions("oldnew:takeinfo:takeInfo:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			takeInfoService.delete(takeInfoService.get(id));
		}
		addMessage(redirectAttributes, "删除带人信息成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/takeinfo/takeInfo/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("oldnew:takeinfo:takeInfo:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(TakeInfo takeInfo, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "带人信息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TakeInfo> page = takeInfoService.findPage(new Page<TakeInfo>(request, response, -1), takeInfo);
    		new ExportExcel("带人信息", TakeInfo.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出带人信息记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/takeinfo/takeInfo/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oldnew:takeinfo:takeInfo:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TakeInfo> list = ei.getDataList(TakeInfo.class);
			for (TakeInfo takeInfo : list){
				try{
					takeInfoService.save(takeInfo);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条带人信息记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条带人信息记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入带人信息失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/takeinfo/takeInfo/?repage";
    }
	
	/**
	 * 下载导入带人信息数据模板
	 */
	@RequiresPermissions("oldnew:takeinfo:takeInfo:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "带人信息数据导入模板.xlsx";
    		List<TakeInfo> list = Lists.newArrayList(); 
    		new ExportExcel("带人信息数据", TakeInfo.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/takeinfo/takeInfo/?repage";
    }
	
	
	

}