/**
 * 
 */
package com.jeeplus.modules.api.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.anchor.entity.AnchorInfo;
import com.jeeplus.modules.anchor.entity.AnchorPersonal;
import com.jeeplus.modules.anchor.service.AnchorInfoService;
import com.jeeplus.modules.anchor.service.AnchorPersonalService;
import com.jeeplus.modules.api.entity.UserApi;
import com.jeeplus.modules.api.model.AnchorPersonModel;
import com.jeeplus.modules.api.model.TakeInfoModel;
import com.jeeplus.modules.api.service.UserApiService;
import com.jeeplus.modules.bill.entity.Bill;
import com.jeeplus.modules.bill.service.BillService;
import com.jeeplus.modules.oldnew.entity.comment.OldnewComment;
import com.jeeplus.modules.oldnew.entity.config.AnchorLevel;
import com.jeeplus.modules.oldnew.entity.mycoupon.MyCoupon;
import com.jeeplus.modules.oldnew.entity.order.OldnewOrder;
import com.jeeplus.modules.oldnew.entity.takeinfo.TakeInfo;
import com.jeeplus.modules.oldnew.service.comment.OldnewCommentService;
import com.jeeplus.modules.oldnew.service.config.AnchorLevelService;
import com.jeeplus.modules.oldnew.service.mycoupon.MyCouponService;
import com.jeeplus.modules.oldnew.service.order.OldnewOrderService;
import com.jeeplus.modules.oldnew.service.takeinfo.TakeInfoService;
import com.jeeplus.modules.oldnew.utils.OldnewOrderTimer;
import com.jeeplus.modules.sys.entity.Dict;
import com.jeeplus.modules.sys.service.DictService;

/**
 * @author zhaol
 * 带人管理Controller
 * 2018年5月14日
 */
@Controller
@RequestMapping(value = "${frontPath}/api/")
public class TakeInfoApiController {
	
	@Autowired
	private TakeInfoService takeInfoService;
	
	@Autowired
	private DictService dictService;
	
	@Autowired
	private AnchorInfoService anchorInfoService;
	
	@Autowired
	private AnchorLevelService anchorLevelService;
	
	@Autowired
	private AnchorPersonalService anchorPersonalService;
	
	@Autowired
	private OldnewOrderService oldnewOrderService;
	
	@Autowired
	private MyCouponService myCouponService;
	
	@Autowired
	private BillService billService;
	@Autowired
	private OldnewCommentService oldnewCommentService;
	@Autowired
	private UserApiService userApiService;
	

	/**
	 * 带人信息发布，根据粉丝数计算价格
	 * @param token
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getTakeInfo" )
	public AjaxJson getTakeInfo(String token,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AnchorInfo anchorInfo = anchorInfoService.findUniqueByProperty("mobile", userApi.getMobile());
		AnchorPersonal anchorPersonal =anchorPersonalService.findUniqueByProperty("user_id",userApi.getId());
		if(anchorInfo!=null){
			//带人要求的粉丝量
			Dict dict = new Dict();
			dict.setType("oldnew_fans_level");//带人要求粉丝量字典type
			List<HashMap<String, Object>> fansNum = dictService.findFrontListByType(dict);
			//主播自身的粉丝量
			if(anchorPersonal!=null && anchorPersonal.getFansAmount()!=null){
				int onwerfans =anchorPersonal.getFansAmount().intValue();
				List<AnchorLevel> levelList = anchorLevelService.findList(new AnchorLevel());
				int oldminfans=levelList.get(0).getMinFansNum();//成为大主播的最小粉丝数
				if(onwerfans<oldminfans){
					ajaxJson.setMsg("你的粉丝没有达到"+oldminfans+",请努力后再来");
				}else{
					for (AnchorLevel anchorLevel : levelList) {
						if(onwerfans>=anchorLevel.getMinFansNum()&&(anchorLevel.getMaxFansNum()==null
								||onwerfans<anchorLevel.getMaxFansNum())){
							ajaxJson.put("price", anchorLevel.getTakePrice());
							ajaxJson.put("monthOrdernum", anchorLevel.getTakeNum());
						}
					}
				}
				ajaxJson.put("oldminfans", oldminfans);	
				ajaxJson.put("anchorPersonal", anchorPersonal); 
     		}
	        ajaxJson.setSuccess(true);
	        ajaxJson.put("fansNum", fansNum);
	        ajaxJson.put("anchorinfo", anchorInfo);  
	        ajaxJson.put("nickname", userApi.getNickname()); 
		}else{
			ajaxJson.setSuccess(false);
	     	ajaxJson.setMsg("系统有误，请重试");
		}
		return ajaxJson;
	}
	
	/**
	 * 带人信息详情页
	 * @param token
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAnchorInfoDetails")
	public AjaxJson getAnchorInfoDetails(String token,HttpServletResponse response){
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi = (UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AjaxJson ajaxJson = new AjaxJson();
		AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
		AnchorPersonal anchorPersonal=anchorPersonalService.findUniqueByProperty("user_id",userApi.getId());
		ajaxJson.setSuccess(true);
		ajaxJson.put("anchor", anchorInfo);
		ajaxJson.put("nickname", userApi.getNickname());
		ajaxJson.put("anchordetails", anchorPersonal);
		return ajaxJson;
	}
	
	/**
	 * 发布带人订单
	 * @param token
	 * @param takeInfo
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "releaseOrder" )
	public AjaxJson releaseOrder(String token,TakeInfo takeInfo, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AjaxJson ajaxJson = new AjaxJson();
		TakeInfo take = takeInfoService.findUniqueByProperty("pulisher_id", userApi.getId());
		if(take!=null){
			MyBeanUtils.copyBeanNotNull2Bean(takeInfo,take);//将编辑表单中的非NULL值覆盖数据库记录中的值
			take.setIsNewRecord(false);
			takeInfoService.save(take);
			ajaxJson.setSuccess(true);
	     	ajaxJson.setMsg("带人订单更新成功");
		}else{
			takeInfo.setNickname(userApi.getNickname());
			takeInfo.setPulisherId(userApi.getId());
			takeInfoService.save(takeInfo);	
			ajaxJson.setSuccess(true);
	     	ajaxJson.setMsg("带人订单发布成功");
		}
		return ajaxJson;
	}
	
	/**
	 * 带人列表展示
	 * @param token
	 * @param response
	 * @param start 分页查询 起始记录index
	 * @param num   分页查询 记录个数
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getTakeList")
	public AjaxJson getTakeList(String token,HttpServletResponse response,Integer start,Integer num) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
		List<TakeInfoModel> takeinfoList = takeInfoService.getTakeList(anchorInfo.getCompany().getId(),start,num);
		for (TakeInfoModel takeInfoModel : takeinfoList) {
			int takeNum = oldnewOrderService.getOrderNumByMonth(takeInfoModel.getPulisherId());//已被接单数
			int residueNum = takeInfoModel.getMonthOrdernum()-takeNum;//剩余带人次数
			takeInfoModel.setTakeNum(residueNum);
		}
	    ajaxJson.setSuccess(true); 
	    ajaxJson.put("takeinfoList", takeinfoList);
	    ajaxJson.setMsg("带人信息列表");
		return ajaxJson;
	}
	
	/**
	 * 查询明星主播
	 * @param token
	 * @param response
	 * @param start 分页查询 起始记录index
	 * @param num   分页查询 记录个数
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getStarList")
	public AjaxJson getStarList(String token,HttpServletResponse response,Integer start,Integer num) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
		String status = Global.ORDERING;
		//明星主播的最低要求
		Dict dict = new Dict();
		dict.setType("oldanchor_minfans");
		List<Dict> list = dictService.findList(dict);
		int fansAmount = Integer.parseInt(list.get(0).getLabel());
		List<TakeInfoModel> takeList = takeInfoService.getStarList(anchorInfo.getCompany().getId(),status, fansAmount,null,null);
		int listNum = takeList.size();
		List<TakeInfoModel> takeinfoList = takeInfoService.getStarList(anchorInfo.getCompany().getId(),status, fansAmount,start,num);
		for (TakeInfoModel takeInfoModel : takeinfoList) {
			int takeNum = oldnewOrderService.getOrderNumByMonth(takeInfoModel.getPulisherId());//已被接单数
			int fansNum = takeInfoModel.getOwnerFans();
			int canTakeNum = 0;//每月可带人数
			List<AnchorLevel> levelList = anchorLevelService.findList(new AnchorLevel());
			for (AnchorLevel le : levelList) {
				if(fansNum >= le.getMinFansNum() && (le.getMaxFansNum()==null || fansNum < le.getMaxFansNum())){
					canTakeNum = le.getTakeNum();
					break;
				}
			}
			int residueNum = canTakeNum-takeNum;//剩余带人次数
			takeInfoModel.setTakeNum(residueNum);
		}
	    ajaxJson.setSuccess(true); 
	    ajaxJson.put("takeinfoList", takeinfoList);
	    ajaxJson.put("listNum", listNum);
		return ajaxJson;
	}
	
	/**
	 * 被带主播下单
	 * @param token
	 * @param userId 所下单主播的userId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "placeOrder")
	public AjaxJson placeOrder(String token,String userId,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		TakeInfo takeInfo = takeInfoService.findUniqueByProperty("pulisher_id", userId);
		List<MyCoupon> coupons = myCouponService.findUseableByUser(userApi.getId(),Global.UNUSED,Global.ORDER_COUPON);
		AnchorPersonModel basicInfo = anchorInfoService.getBasicInfo(userId);
	    ajaxJson.setSuccess(true);
	    ajaxJson.put("takeInfo", takeInfo);
	    ajaxJson.put("basicInfo", basicInfo);
	    ajaxJson.put("coupons", coupons);
	    ajaxJson.put("couponsCount", coupons.size());
	    ajaxJson.setMsg("小主播下单");
		return ajaxJson;
	}
	
	/**
	 * 被带主播确认付款
	 * @param token
	 * @param takeId    带人信息id
	 * @param couponId  优惠券id
	 * @param reduceMoney  优惠金额
	 * @param openid   
	 * @param rep
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "payment")
	public AjaxJson payment(String token,String takeId,String couponId,BigDecimal reduceMoney,BigDecimal actualMoney,String openid,HttpServletRequest rep,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		if(StringUtils.isBlank(openid) || "null".equals(openid) || "undefined".equals(openid)){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("请在微信浏览器中打开");
			return ajaxJson;
	    }
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AnchorInfo anchorInfo = anchorInfoService.findUniqueByProperty("mobile", userApi.getMobile());
		TakeInfo takeInfo = takeInfoService.get(takeId);
		//判断是否是下自己的单
		if(anchorInfo.getUserId().equals(takeInfo.getPulisherId())){
			ajaxJson.setSuccess(false); 
		    ajaxJson.setMsg("你不能支付自己的订单！");
			return ajaxJson;	
		}
		//判断平台是否为空
		if(StringUtils.isBlank(anchorInfo.getPlayPlatform())){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("您的直播平台为空，请联系经纪人修改");
			return ajaxJson;
		}
		//判断是否一个平台
		if(!anchorInfo.getPlayPlatform().equals(takeInfo.getPlatformcode())){
			ajaxJson.setSuccess(false); 
		    ajaxJson.setMsg("你的开播平台和订单要求不符，请查看其它主播的订单");
			return ajaxJson;	
		}
		//判断带人主播还有带人名额'
		int takeNum = oldnewOrderService.getOrderNumByMonth(takeInfo.getPulisherId());//已被接单数
		int residueNum = takeInfo.getMonthOrdernum()-takeNum;//剩余带人次数
		if(residueNum<1){
			 ajaxJson.setSuccess(false); 
		     ajaxJson.setMsg("该主播已没有带人次数,请查看其他主播或过段时间再来！");
			 return ajaxJson;	
		}
		//查询两人未付款订单
		List<OldnewOrder> list = oldnewOrderService.getUnfinishedOrder(userApi.getId(),takeInfo.getPulisherId());
		if(list.size()!=0){
			 ajaxJson.setMsg("你有该主播的待支付订单，请去我的订单支付！");
			 ajaxJson.setSuccess(false); 
			 return ajaxJson;
		}
		OldnewOrder order = new OldnewOrder();
		//查询优惠券是否可用
		//BigDecimal reduceMoney= new BigDecimal("0"); 
		if(StringUtils.isNotBlank(couponId)){
			MyCoupon myCoupon = myCouponService.get(couponId);
			if(myCoupon==null){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("所选优惠券不存在");
				return ajaxJson;
			}
			if(Global.USED.equals(myCoupon.getStatus()) || new Date().after(DateUtils.addDay(myCoupon.getDisableDate(), 1))){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("所选优惠券已过期");
				return ajaxJson;
			}
			//修改优惠券状态
			myCoupon.setStatus(Global.USED);//已使用
			myCouponService.save(myCoupon);
			order.setCouponId(couponId);//优惠券id
		}
		order.setMessageid(takeId); //带人信息id
		order.setPublisherId(userApi.getId()); //被带人id
		order.setStatus(Global.PAYING);//订单状态待支付
		order.setOrgerType(Global.TAKE);//带人
		order.setOrderPrice(takeInfo.getPrice());//订单价格
		order.setReduceMoney(reduceMoney);//优惠金额
		//BigDecimal actualMoney = takeInfo.getPrice().subtract(reduceMoney);//实际支付金额
		order.setActualMoney(actualMoney);//实际支付金额
		order.setTakerId(takeInfo.getPulisherId());//带人id
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Long min = Long.valueOf(sdf.format(new Date())) * 1000;
		//获取当天最大订单号
		Long maxorderCode = oldnewOrderService.getMaxOrderCode(min);
		if(maxorderCode==null){
			//当天第一单
			maxorderCode = min + 1;
		}else{
			//当天最大订单号+1
			maxorderCode++;
		}
		order.setOrderCode(maxorderCode);//订单号
		order.setReceiveDate(new Date()); //接单时间
		//订单价格为0
		if(order.getActualMoney().compareTo(new BigDecimal("0")) == 0){
			order.setStatus(Global.ONGOING);//进行中
			oldnewOrderService.save(order);
			ajaxJson.put("orderId", order.getId());
			ajaxJson.put("jsPay", false);
			ajaxJson.setSuccess(true);
			return ajaxJson;
		}
		oldnewOrderService.save(order);
		//执行定时任务，过时未付款自动关闭
		try {
			OldnewOrderTimer.closeOrder(order.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//微信支付
		WeixinApiController weixin = new WeixinApiController();
		String desc = "老带新订单";
		int money = actualMoney.multiply(new BigDecimal("100")).intValue();//以分为单位
		AjaxJson paramsJson = weixin.getUnifiedorder(order.getId(), openid, money, desc, rep, response);
		//int money = 1; //测试数据
		//AjaxJson paramsJson = weixin.getUnifiedorder(order.getId(), userApi.getWeixinOperid(), money, desc, rep, response);		
		if(paramsJson.isSuccess()){
			ajaxJson.put("payParams", paramsJson.getBody().get("payParams"));
			ajaxJson.put("orderId", order.getId());
			ajaxJson.put("jsPay", true);
			ajaxJson.setSuccess(true);
			ajaxJson.setMsg("支付成功!");
			return ajaxJson;
		}else{
			return paramsJson;
		}
	}
		
//	/**
//     * 被带主播付款订单
//     * */
//	@ResponseBody
//	@RequestMapping(value = "paymentOrder")
//	public AjaxJson paymentOrder(String token,String orderId,HttpServletRequest rep,HttpServletResponse response) {
//		AjaxJson ajaxJson = new AjaxJson();
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
//		OldnewOrder order = oldnewOrderService.findById(orderId);
//		//AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
//		if(!Global.PAYING.equals(order.getStatus())){
//			if(Global.CLOSED.equals(order.getStatus())){
//				ajaxJson.setSuccess(false);
//				ajaxJson.setMsg("该订单超过支付时限，已关闭");
//				return ajaxJson;
//			}
//			ajaxJson.setSuccess(false);
//			ajaxJson.setMsg("订单已支付，请不要重复支付");
//			return ajaxJson;
//		}
//		WeixinApiController wxapi = new WeixinApiController();
//		int money = order.getActualMoney().intValue() * 100;
//		System.out.println("orderMoney:"+money);
//		Map<String, String> map = wxapi.getUnifiedorder(order.getId(),userApi.getWeixinOperid(),1,"带人订单",rep,response);
//		ajaxJson.put("payParams", map);
//		ajaxJson.put("orderId", order.getId());
//		ajaxJson.setSuccess(true);
//		return ajaxJson;
//	}
	
	/**
	 * 申请退款
	 * @param token
	 * @param orderId 订单id
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "drawback")
	public AjaxJson drawback(String token,String orderId,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		OldnewOrder order = oldnewOrderService.findById(orderId);
		//判断订单金额，为0不插入
		if(Global.ONGOING.equals(order.getStatus())){
			Bill bill = new Bill();
			AnchorPersonModel  anchor = anchorInfoService.getBasicInfo(userApi.getId());
			bill.setname(anchor.getName());
			bill.setUserId(userApi.getId());
			bill.setType("申请退款");
			bill.setMoney(order.getActualMoney().doubleValue());
			bill.setDesc("申请退款");
			bill.setBeginCreateDate(new Date());
			bill.setStatus(0);//未结算
			bill.setOrderId(orderId);
			billService.save(bill);	
			order.setStatus(Global.APPLY_FOR_REFUNDING);//订单状态变为退款中
			oldnewOrderService.save(order);
			ajaxJson.setMsg("你的退款申请已提交，请联系经纪人处理");
		}else{
			if(Global.APPLY_FOR_REFUNDING.equals(order.getStatus())){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("你的退款申请已提交，请不要重复提交！");
				return ajaxJson;
			}
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("订单已完成，不能申请退款");
			return ajaxJson;
		}
	    ajaxJson.setSuccess(true); 
		return ajaxJson;
	}
	
	/**
	 * 带人主播评价
	 * @param token
	 * @param comment
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "takeComment")
	public AjaxJson takeComment(String token,OldnewComment comment,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		OldnewOrder order = oldnewOrderService.get(comment.getOrderId());
		//带人者评价
		OldnewComment exist = oldnewCommentService.getOneComment(comment.getOrderId(),userApi.getId(),Global.TAKER_COMMENT);
		if(exist !=null){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("已评价，请勿重复评价");
			return ajaxJson;
		}else{
			comment.setUserId(userApi.getId());
			comment.setType(Global.TAKER_COMMENT);//带人者评价
			oldnewCommentService.save(comment);
			//获取某订单评价个数
			int count = oldnewCommentService.getCommentCountByOrder(comment.getOrderId());
			if(count >= 3){
				order.setStatus(Global.COMPLETED);//已完成
				order.setOverDate(new Date());
				oldnewOrderService.save(order);
			}
			ajaxJson.setSuccess(true);
			return ajaxJson;
		}
	}	
	
}
