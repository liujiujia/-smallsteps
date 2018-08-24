/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.oldnew.web.mycoupon;

import java.util.ArrayList;
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
import com.jeeplus.modules.anchor.entity.AnchorInfo;
import com.jeeplus.modules.anchor.service.AnchorInfoService;
import com.jeeplus.modules.oldnew.entity.mycoupon.MyCoupon;
import com.jeeplus.modules.oldnew.service.mycoupon.MyCouponService;
import com.jeeplus.modules.sys.entity.User;

/**
 * 我的优惠券Controller
 * 
 * @author jiangjl
 * @version 2018-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/oldnew/mycoupon/myCoupon")
public class MyCouponController extends BaseController {

	@Autowired
	private MyCouponService myCouponService;

	@Autowired
	private AnchorInfoService InfoService;

	@ModelAttribute
	public MyCoupon get(@RequestParam(required = false) String id) {
		MyCoupon entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = myCouponService.get(id);
		}
		if (entity == null) {
			entity = new MyCoupon();
		}
		return entity;
	}

	/**
	 * 我的优惠券列表页面
	 */
	@RequiresPermissions("oldnew:mycoupon:myCoupon:list")
	@RequestMapping(value = { "list", "" })
	public String list(MyCoupon myCoupon, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 获取当前登录的用户
		User user = myCoupon.getCurrentUser();
		Page<MyCoupon> page = null;
		if (!user.isAdmin()) {
			// 获取公司ID
			myCoupon.setCompany(user.getCompany());
			page = myCouponService.findPage(new Page<MyCoupon>(request, response), myCoupon);
		}else {
			page = myCouponService.findAllList(new Page<MyCoupon>(request, response),new MyCoupon());
		}
		
		model.addAttribute("page", page);
		return "modules/oldnew/mycoupon/myCouponList";
	}

	/**
	 * 查看，增加，编辑我的优惠券表单页面
	 */
	@RequiresPermissions(value = { "oldnew:mycoupon:myCoupon:view", "oldnew:mycoupon:myCoupon:add",
			"oldnew:mycoupon:myCoupon:edit" }, logical = Logical.OR)
	@RequestMapping(value = "form")
	public String form(MyCoupon myCoupon, Model model) {
		model.addAttribute("myCoupon", myCoupon);
		// 获取当前登录的用户
		User user = myCoupon.getCurrentUser();
		AnchorInfo info = new AnchorInfo();
		if (!user.isAdmin()) {
			// 获取公司ID
			info.setCompany(user.getCompany());
		}
		List<AnchorInfo> findList = InfoService.findList(info);
		List<AnchorInfo> list = new ArrayList<AnchorInfo>();
		for (AnchorInfo anchorInfo : findList) {
			if (StringUtils.isNoneEmpty(anchorInfo.getName())) {
				list.add(anchorInfo);
			}
		}
		model.addAttribute("user", list);
		return "modules/oldnew/mycoupon/myCouponForm";
	}

	/**
	 * 保存我的优惠券
	 */
	@RequiresPermissions(value = { "oldnew:mycoupon:myCoupon:add",
			"oldnew:mycoupon:myCoupon:edit" }, logical = Logical.OR)
	@RequestMapping(value = "save")
	public String save(MyCoupon myCoupon, Model model, RedirectAttributes redirectAttributes) throws Exception {
		if (!beanValidator(model, myCoupon)) {
			return form(myCoupon, model);
		}
		if (!myCoupon.getIsNewRecord()) {// 编辑表单保存
			MyCoupon t = myCouponService.get(myCoupon.getId());// 从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(myCoupon, t);// 将编辑表单中的非NULL值覆盖数据库记录中的值
			myCouponService.save(t);// 保存
		} else {// 新增表单保存
			myCouponService.save(myCoupon);// 保存
		}
		addMessage(redirectAttributes, "保存我的优惠券成功");
		return "redirect:" + Global.getAdminPath() + "/oldnew/mycoupon/myCoupon/?repage";
	}

	/**
	 * 删除我的优惠券
	 */
	@RequiresPermissions("oldnew:mycoupon:myCoupon:del")
	@RequestMapping(value = "delete")
	public String delete(MyCoupon myCoupon, RedirectAttributes redirectAttributes) {
		myCouponService.delete(myCoupon);
		addMessage(redirectAttributes, "删除我的优惠券成功");
		return "redirect:" + Global.getAdminPath() + "/oldnew/mycoupon/myCoupon/?repage";
	}

	/**
	 * 批量删除我的优惠券
	 */
	@RequiresPermissions("oldnew:mycoupon:myCoupon:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] = ids.split(",");
		for (String id : idArray) {
			myCouponService.delete(myCouponService.get(id));
		}
		addMessage(redirectAttributes, "删除我的优惠券成功");
		return "redirect:" + Global.getAdminPath() + "/oldnew/mycoupon/myCoupon/?repage";
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("oldnew:mycoupon:myCoupon:export")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(MyCoupon myCoupon, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			String fileName = "我的优惠券" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<MyCoupon> page = myCouponService.findPage(new Page<MyCoupon>(request, response, -1), myCoupon);
			new ExportExcel("我的优惠券", MyCoupon.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出我的优惠券记录失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/oldnew/mycoupon/myCoupon/?repage";
	}

	/**
	 * 导入Excel数据
	 * 
	 */
	@RequiresPermissions("oldnew:mycoupon:myCoupon:import")
	@RequestMapping(value = "import", method = RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<MyCoupon> list = ei.getDataList(MyCoupon.class);
			for (MyCoupon myCoupon : list) {
				try {
					myCouponService.save(myCoupon);
					successNum++;
				} catch (ConstraintViolationException ex) {
					failureNum++;
				} catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum > 0) {
				failureMsg.insert(0, "，失败 " + failureNum + " 条我的优惠券记录。");
			}
			addMessage(redirectAttributes, "已成功导入 " + successNum + " 条我的优惠券记录" + failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入我的优惠券失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/oldnew/mycoupon/myCoupon/?repage";
	}

	/**
	 * 下载导入我的优惠券数据模板
	 */
	@RequiresPermissions("oldnew:mycoupon:myCoupon:import")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "我的优惠券数据导入模板.xlsx";
			List<MyCoupon> list = Lists.newArrayList();
			new ExportExcel("我的优惠券数据", MyCoupon.class, 1).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/oldnew/mycoupon/myCoupon/?repage";
	}

}