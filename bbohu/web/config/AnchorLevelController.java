/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.web.config;

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
import com.jeeplus.modules.oldnew.entity.config.AnchorLevel;
import com.jeeplus.modules.oldnew.service.config.AnchorLevelService;

/**
 * 主播级别Controller
 * @author jiangjl
 * @version 2018-05-16
 */
@Controller
@RequestMapping(value = "${adminPath}/oldnew/config/anchorLevel")
public class AnchorLevelController extends BaseController {

	@Autowired
	private AnchorLevelService anchorLevelService;
	
	@ModelAttribute
	public AnchorLevel get(@RequestParam(required=false) String id) {
		AnchorLevel entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = anchorLevelService.get(id);
		}
		if (entity == null){
			entity = new AnchorLevel();
		}
		return entity;
	}
	
	/**
	 * 主播级别列表页面
	 */
	@RequiresPermissions("oldnew:config:anchorLevel:list")
	@RequestMapping(value = {"list", ""})
	public String list(AnchorLevel anchorLevel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AnchorLevel> page = anchorLevelService.findPage(new Page<AnchorLevel>(request, response), anchorLevel); 
		model.addAttribute("page", page);
		return "modules/oldnew/config/anchorLevelList";
	}

	/**
	 * 查看，增加，编辑主播级别表单页面
	 */
	@RequiresPermissions(value={"oldnew:config:anchorLevel:view","oldnew:config:anchorLevel:add","oldnew:config:anchorLevel:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(AnchorLevel anchorLevel, Model model) {
		model.addAttribute("anchorLevel", anchorLevel);
		return "modules/oldnew/config/anchorLevelForm";
	}

	/**
	 * 保存主播级别
	 */
	@RequiresPermissions(value={"oldnew:config:anchorLevel:add","oldnew:config:anchorLevel:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(AnchorLevel anchorLevel, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, anchorLevel)){
			return form(anchorLevel, model);
		}
		if(!anchorLevel.getIsNewRecord()){//编辑表单保存
			AnchorLevel t = anchorLevelService.get(anchorLevel.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(anchorLevel, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			anchorLevelService.save(t);//保存
		}else{//新增表单保存
			anchorLevelService.save(anchorLevel);//保存
		}
		addMessage(redirectAttributes, "保存主播级别成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/config/anchorLevel/?repage";
	}
	
	/**
	 * 删除主播级别
	 */
	@RequiresPermissions("oldnew:config:anchorLevel:del")
	@RequestMapping(value = "delete")
	public String delete(AnchorLevel anchorLevel, RedirectAttributes redirectAttributes) {
		anchorLevelService.delete(anchorLevel);
		addMessage(redirectAttributes, "删除主播级别成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/config/anchorLevel/?repage";
	}
	
	/**
	 * 批量删除主播级别
	 */
	@RequiresPermissions("oldnew:config:anchorLevel:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			anchorLevelService.delete(anchorLevelService.get(id));
		}
		addMessage(redirectAttributes, "删除主播级别成功");
		return "redirect:"+Global.getAdminPath()+"/oldnew/config/anchorLevel/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("oldnew:config:anchorLevel:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(AnchorLevel anchorLevel, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "主播级别"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<AnchorLevel> page = anchorLevelService.findPage(new Page<AnchorLevel>(request, response, -1), anchorLevel);
    		new ExportExcel("主播级别", AnchorLevel.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出主播级别记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/config/anchorLevel/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oldnew:config:anchorLevel:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<AnchorLevel> list = ei.getDataList(AnchorLevel.class);
			for (AnchorLevel anchorLevel : list){
				try{
					anchorLevelService.save(anchorLevel);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条主播级别记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条主播级别记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入主播级别失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/config/anchorLevel/?repage";
    }
	
	/**
	 * 下载导入主播级别数据模板
	 */
	@RequiresPermissions("oldnew:config:anchorLevel:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "主播级别数据导入模板.xlsx";
    		List<AnchorLevel> list = Lists.newArrayList(); 
    		new ExportExcel("主播级别数据", AnchorLevel.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oldnew/config/anchorLevel/?repage";
    }
	
	
	

}