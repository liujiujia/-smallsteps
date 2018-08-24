package com.jeeplus.modules.api.web;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.exceptions.ClientException;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.sms.SmsSender;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.anchor.entity.AnchorInfo;
import com.jeeplus.modules.anchor.entity.AnchorPersonal;
import com.jeeplus.modules.anchor.service.AnchorInfoService;
import com.jeeplus.modules.anchor.service.AnchorPersonalService;
import com.jeeplus.modules.api.entity.UserApi;
import com.jeeplus.modules.api.model.AnchorPersonModel;
import com.jeeplus.modules.api.service.UserApiService;
import com.jeeplus.modules.bill.entity.Bill;
import com.jeeplus.modules.bill.service.BillService;
import com.jeeplus.modules.oldnew.entity.comment.OldnewComment;
import com.jeeplus.modules.oldnew.entity.config.AnchorLevel;
import com.jeeplus.modules.oldnew.entity.mycoupon.MyCoupon;
import com.jeeplus.modules.oldnew.entity.order.OldnewOrder;
import com.jeeplus.modules.oldnew.entity.requestinfo.RequestInfo;
import com.jeeplus.modules.oldnew.service.comment.OldnewCommentService;
import com.jeeplus.modules.oldnew.service.config.AnchorLevelService;
import com.jeeplus.modules.oldnew.service.mycoupon.MyCouponService;
import com.jeeplus.modules.oldnew.service.order.OldnewOrderService;
import com.jeeplus.modules.oldnew.service.requestinfo.RequestInfoService;
import com.jeeplus.modules.oldnew.utils.OldnewOrderTimer;

/**
 * 求带信息api
 *
 */
@Controller
@RequestMapping(value = "${frontPath}/api/")
public class RequestInfoApiController {
	
	@Autowired
	private AnchorLevelService anchorLevelService;
	@Autowired
	private AnchorInfoService anchorInfoService;
	@Autowired
	private AnchorPersonalService anchorPersonalService;
	@Autowired
	private RequestInfoService requestInfoService;
	@Autowired
	private OldnewOrderService oldnewOrderService;
	@Autowired
	private OldnewCommentService oldnewCommentService;
	@Autowired
	private MyCouponService myCouponService;
	@Autowired
	private UserApiService userApiService;
	@Autowired
	private BillService billService;


	/**
	 * 获取主播级别列表,优惠券列表
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAnchorLevelList" )
	public AjaxJson getAnchorLevelList(String token,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*"); 
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		List<AnchorLevel> levels = anchorLevelService.findList(new AnchorLevel());
		//该主播老带新优惠券列表
		List<MyCoupon> coupons = myCouponService.findUseableByUser(userApi.getId(),Global.UNUSED,Global.ORDER_COUPON);
		
	    ajaxJson.setSuccess(true);
	    ajaxJson.put("levels", levels);
	    ajaxJson.put("coupons", coupons);
	    ajaxJson.put("couponsCount", coupons.size());
		return ajaxJson;
	}
	
	/**
	 * 发布求带信息
	 * @param token
	 * @param type
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "releaseRequestInfo" )
	public AjaxJson releaseRequestInfo(String openid,String token,RequestInfo requestInfo,String couponId,OldnewOrder order,HttpServletRequest req,HttpServletResponse resp) {
		AjaxJson ajaxJson = new AjaxJson();
		resp.setHeader("Access-Control-Allow-Origin", "*");
		if(StringUtils.isBlank(openid) || "null".equals(openid) || "undefined".equals(openid)){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("请在微信浏览器中打开");
			return ajaxJson;
		}
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
		//该主播已有订单状态 不是退款中、已关闭、待评价、已完成 时，不能再发布
		OldnewOrder recentOrder = oldnewOrderService.getRecentOrder(userApi.getId());
		/*if(recentOrder != null && Integer.parseInt(recentOrder.getStatus())<Integer.parseInt(Global.REFUNDING)){
			if(Global.PAYING.equals(recentOrder.getStatus())){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("您有待支付订单，请去“我的订单”支付或取消");
				return ajaxJson;
			}
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("您有订单未完成，请完成后再发布");
			return ajaxJson;
		}*/
		//查询优惠券是否可用
		if(StringUtils.isNotBlank(couponId)){
			MyCoupon myCoupon = myCouponService.get(couponId);
			if(myCoupon==null){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("所选优惠券不存在");
				return ajaxJson;
			}
			if(Global.USED.equals(myCoupon.getStatus()) || new Date().after(DateUtils.addDay(myCoupon.getDisableDate(), 1))){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("所选优惠券已使用或已过期");
				return ajaxJson;
			}
			//修改优惠券状态
			myCoupon.setStatus(Global.USED);//已使用
			myCouponService.save(myCoupon);
			
			order.setCouponId(couponId);
		}
		
		requestInfo.setPulisherId(userApi.getId());
		requestInfoService.save(requestInfo);
		
		order.setMessageid(requestInfo.getId());
		order.setPublisherId(userApi.getId());
		order.setStatus(Global.PAYING);//待支付
		order.setOrgerType(Global.REQUEST);//求带
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
		order.setOrderCode(maxorderCode);
		//订单价格为0
		if(order.getActualMoney().compareTo(new BigDecimal("0")) == 0){
			order.setStatus(Global.TAKING);//待接单
			oldnewOrderService.save(order);
			ajaxJson.put("orderId", order.getId());
			ajaxJson.put("jsPay", false);
			ajaxJson.setSuccess(true);
			return ajaxJson;
		}
		oldnewOrderService.save(order);
		try {
			OldnewOrderTimer.closeOrder(order.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		int money = order.getActualMoney().multiply(new BigDecimal("100")).intValue();
		System.out.println("orderMoney:"+money);
		//微信支付
		WeixinApiController wxapi = new WeixinApiController();
		AjaxJson paramsJson = wxapi.getUnifiedorder(order.getId(),openid,money,"老带新订单",req,resp);
		//AjaxJson paramsJson = wxapi.getUnifiedorder(order.getId(),userApi.getWeixinOperid(),money,"老带新订单",req,resp);
		if(paramsJson.isSuccess()){
			ajaxJson.put("payParams", paramsJson.getBody().get("payParams"));
			ajaxJson.put("orderId", order.getId());
			ajaxJson.put("jsPay", true);
			ajaxJson.setSuccess(true);
			return ajaxJson;
		}else{
			return paramsJson;
		}
	}
	
	/**
	 * 获取求带信息列表
	 * @param token
	 * @param start 分页查询 起始记录index
	 * @param num   分页查询 记录个数
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getRequestInfoList" )
	public AjaxJson getRequestInfoList(String token,Integer start, Integer num,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
		//待接单求带信息分页查询
		List<Map<String,Object>> list = requestInfoService.findRequestInfoList(start,num,Global.TAKING,anchorInfo.getCompany().getId());
		
		ajaxJson.setSuccess(true);
		ajaxJson.put("list", list);
		
		return ajaxJson;
		
	}
	
	/**
	 * 确认接单
	 * @param token
	 * @param orderId 订单id
	 * @param requestInfoId 求带信息id
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "confirmTaking" )
	public AjaxJson confirmTaking(String token,String orderId, String requestInfoId,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		AnchorPersonal personal = anchorPersonalService.findByUserId(userApi.getId());
		AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
		//判断是否能够接单 平台 粉丝数 每月接单数
		OldnewOrder order = oldnewOrderService.findById(orderId);
		RequestInfo requestInfo = requestInfoService.get(requestInfoId);
		if(requestInfo.getPulisherId().equals(userApi.getId())){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("不能接自己的单哦");
			return ajaxJson;
		}
		if(StringUtils.isNotBlank(order.getTakerId()) || !Global.TAKING.equals(order.getStatus())){
			if(userApi.getId().equals(order.getTakerId())){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("您已经接过这单啦");
				return ajaxJson;
			}
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("抱歉，该订单被人抢先一步接下~");
			return ajaxJson;
		}
		if(personal == null || personal.getFansAmount() == null){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("您的粉丝数不符合接单要求，请联系经纪人修改");
			return ajaxJson;
		}
		if(StringUtils.isBlank(anchorInfo.getPlayPlatform())){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("您的直播平台为空，请联系经纪人修改");
			return ajaxJson;
		}
		if(!requestInfo.getPlatformcode().equals(anchorInfo.getPlayPlatform())){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("您的直播平台与该订单需求不符，无法接单");
			return ajaxJson;
		}
		AnchorLevel level = anchorLevelService.get(requestInfo.getAnchorLevelId());
		int fansNum = personal.getFansAmount().intValue();
		if(fansNum < level.getMinFansNum().intValue()){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("您的粉丝数未达到该订单需求，无法接单，可联系经纪人修改");
			return ajaxJson;
		}
		int numByMonth = oldnewOrderService.getOrderNumByMonth(userApi.getId());//每月已接单数
		int canTakeNum = 0;//每月可带人数
		List<AnchorLevel> levelList = anchorLevelService.findList(new AnchorLevel());
		for (AnchorLevel le : levelList) {
			if(fansNum >= le.getMinFansNum() && (le.getMaxFansNum()==null || fansNum < le.getMaxFansNum())){
				canTakeNum = le.getTakeNum();
				break;
			}
		}
		if(numByMonth >= canTakeNum){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("您本月接单数已达到上限，无法接单");
			return ajaxJson;
		}
		order.setTakerId(userApi.getId());
		order.setStatus(Global.ONGOING);
		order.setReceiveDate(new Date());
		oldnewOrderService.save(order);
		try {
			OldnewOrderTimer.finishOrder(order.getId());
		} catch (Exception e) {
		 	e.printStackTrace();
		}
		//通知双方
		UserApi userPuber = userApiService.get(order.getPublisherId());
		UserApi userTaker = userApiService.get(order.getTakerId());
		String totaker = userTaker.getWeixinOperid();
		String topuber = userPuber.getWeixinOperid();
		AnchorPersonModel taker = anchorInfoService.getBasicInfo(order.getTakerId());
		if(StringUtils.isNotBlank(topuber)&&StringUtils.isNotBlank(totaker)){
			String template_id = Global.getConfig("order_template_id");
			String url = Global.getConfig("anchor_redirect_uri");
	        //根据具体模板参数组装
			AnchorPersonModel puber = anchorInfoService.getBasicInfo(order.getPublisherId());
			String title = "您的老带新订单已被主播"+taker.getNickName()+"接单";
			String title1 = "您已经接受主播"+puber.getNickName()+"发布的订单";
			String orderCode = order.getOrderCode().toString();
			String status = "进行中";
			DecimalFormat df = new DecimalFormat("0.00");  
		    String money = df.format(order.getOrderPrice()); 
			String info = "订单金额："+money+"元，请主动联系对方，登录系统查看订单详情";
			String color = "#09bb07";
			Map<String, Map<String, String>> params = WeixinApiController.getParams(title, orderCode, status, info, color);
			Map<String, Map<String, String>> params1 = WeixinApiController.getParams(title1, orderCode, status, info, color);
	        WeixinApiController weixinApi = new WeixinApiController();
	        try {
	        	weixinApi.sendTemplate(topuber, template_id, url, params);
	        	weixinApi.sendTemplate(totaker, template_id, url, params1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//短信通知求带人
		String status = "进行中";
		String remark = "接单人："+taker.getNickName()+"，订单金额："+order.getOrderPrice()+"元";
		String contentJson="{\"status\":\""+status+"\",\"remark\":\""+remark+"\"}"; 
		try {
			SmsSender.sendSms(userPuber.getMobile(),SmsSender.SMS_ANCHOR_RECEIVE_ORDER_TEMPLATE,contentJson);
		} catch (ClientException e) {
			e.printStackTrace();
		}
		
		ajaxJson.setSuccess(true);
		
		return ajaxJson;
		
	}
	
	/**
	 * 评价
	 * @param token
	 * @param orderId 订单id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "comment" )
	public AjaxJson comment(String token,OldnewComment comment,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		OldnewOrder order = oldnewOrderService.findById(comment.getOrderId());
		if(userApi.getId().equals(order.getPublisherId())){
			//求带者评价
			OldnewComment exist = oldnewCommentService.getOneComment(comment.getOrderId(),userApi.getId(),Global.REQUESTER_COMMENT);
			if(exist !=null){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("已评价，请勿重复评价");
				return ajaxJson;
			}else{
				
				comment.setUserId(userApi.getId());
				comment.setType(Global.REQUESTER_COMMENT);//求带者评价
				oldnewCommentService.save(comment);
				//获取某订单评价个数
				int count = oldnewCommentService.getCommentCountByOrder(comment.getOrderId());
				if(count >= 3){
					order.setStatus(Global.COMPLETED);//已完成
					oldnewOrderService.save(order);
				}
				ajaxJson.setSuccess(true);
				return ajaxJson;
			}
		}else{
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
					oldnewOrderService.save(order);
				}
				ajaxJson.setSuccess(true);
				return ajaxJson;
			}
		}
	}
	
	/**
	 * 取消订单
	 * @param token
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "cancelOrder" )
	public AjaxJson cancelOrder(String token,String orderId,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		
		OldnewOrder order = oldnewOrderService.findById(orderId);
		//待支付直接取消
		if(Global.PAYING.equals(order.getStatus())){
			order.setStatus(Global.CLOSED);//已关闭
			order.setOverDate(new Date());
			oldnewOrderService.save(order);
			//还原优惠券
			if(StringUtils.isNotBlank(order.getCouponId())){
				MyCoupon coupons = myCouponService.get(order.getCouponId());
				coupons.setStatus(Global.UNUSED);
				myCouponService.save(coupons);
			}
		}else if(Global.TAKING.equals(order.getStatus())){
			//已支付向sys_bill插入退款记录
			//判断订单金额，为0不插入
			if(new BigDecimal("0").compareTo(order.getActualMoney()) != 0 ){
				AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
				Bill bill = new Bill();
				bill.setname(anchorInfo.getName());
				bill.setUserId(userApi.getId());
				bill.setType("退款");
				bill.setMoney(order.getActualMoney().doubleValue());
				bill.setDesc("老带新取消订单，退款");
				bill.setBeginCreateDate(new Date());
				bill.setStatus(0);//未结算
				bill.setOrderId(orderId);
				billService.save(bill);
			}
			order.setStatus(Global.REFUNDING);//退款中
			oldnewOrderService.save(order);
			//还原优惠券
			if(StringUtils.isNotBlank(order.getCouponId())){
				MyCoupon coupons = myCouponService.get(order.getCouponId());
				coupons.setStatus(Global.UNUSED);
				myCouponService.save(coupons);
			}
		}else{
			if(StringUtils.isNotBlank(order.getTakerId())){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("大主播已接单，不能取消哦");
				return ajaxJson;
			}
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("订单已取消，请勿重复操作");
			return ajaxJson;
		}
		ajaxJson.setSuccess(true);
		return ajaxJson;
		
	}
	
	/**
	 * 待支付订单支付
	 * @param token
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/request/pay" )
	public AjaxJson pay(String token,String orderId,String openid,HttpServletResponse resp,HttpServletRequest req) {
		AjaxJson ajaxJson = new AjaxJson();
		resp.setHeader("Access-Control-Allow-Origin", "*");
		if(StringUtils.isBlank(openid) || "null".equals(openid) || "undefined".equals(openid)){
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("请在微信浏览器中打开");
			return ajaxJson;
		}
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		OldnewOrder order = oldnewOrderService.findById(orderId);
		//AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
		if(!Global.PAYING.equals(order.getStatus())){
			if(Global.CLOSED.equals(order.getStatus())){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("订单已关闭,无法支付");
				return ajaxJson;
			}
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("订单已支付，请不要重复支付");
			return ajaxJson;
		}
		WeixinApiController wxapi = new WeixinApiController();
		int money = order.getActualMoney().multiply(new BigDecimal("100")).intValue();
		System.out.println("orderMoney:"+money);
		AjaxJson paramsJson = wxapi.getUnifiedorder(order.getId(),openid,money,"老带新订单",req,resp);
		//AjaxJson paramsJson = wxapi.getUnifiedorder(order.getId(),userApi.getWeixinOperid(),money,"老带新订单",req,resp);
		if(paramsJson.isSuccess()){
			ajaxJson.put("payParams", paramsJson.getBody().get("payParams"));
			ajaxJson.put("orderId", order.getId());
			ajaxJson.setSuccess(true);
			return ajaxJson;
		}else{
			return paramsJson;
		}
	}
	
	/**
	 * 确认完成
	 * @param token
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "confirmComplete" )
	public AjaxJson confirmComplete(String token,String orderId,HttpServletResponse response) {
		AjaxJson ajaxJson = new AjaxJson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		UserApi userApi=(UserApi)CacheUtils.get(CacheUtils.USER_API_CACHE, token);
		OldnewOrder order = oldnewOrderService.findById(orderId);
		//AnchorInfo anchorInfo = anchorInfoService.findBymobile(userApi.getMobile());
		if(!Global.ONGOING.equals(order.getStatus())){
			if(Global.APPLY_FOR_REFUNDING.equals(order.getStatus())){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("该订单申请退款中，不能确认完成");
				return ajaxJson;
			}
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("该订单已完成，不允许此操作");
			return ajaxJson;
		}
		order.setStatus(Global.APPRAISING);//待评价
		order.setOverDate(new Date());//完成时间
		oldnewOrderService.save(order);
		//通知双方订单完成
		UserApi userPuber = userApiService.get(order.getPublisherId());
		UserApi userTaker = userApiService.get(order.getTakerId());
		String topuber = userPuber.getWeixinOperid();
		String totaker = userTaker.getWeixinOperid();
		if(StringUtils.isNotBlank(topuber)&&StringUtils.isNotBlank(totaker)){
			String template_id = Global.getConfig("order_template_id");
			String url = Global.getConfig("anchor_redirect_uri");
	        //根据具体模板参数组装 
			String title = "您的老带新订单已完成";
			String orderCode = order.getOrderCode().toString();
			String status = "待评价";
			DecimalFormat df = new DecimalFormat("0.00");  
		    String money = df.format(order.getOrderPrice()); 
			String info = "订单金额："+money+"元，请对本次订单进行评价，登录系统查看订单详情";
			String color = "#ff0000";
			Map<String, Map<String, String>> params = WeixinApiController.getParams(title, orderCode, status, info, color);
	        WeixinApiController weixinApi = new WeixinApiController();
	        try {
				weixinApi.sendTemplate(topuber, template_id, url, params);
				weixinApi.sendTemplate(totaker, template_id, url, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		UserApi taker = userApiService.get(order.getTakerId());//带人者
		AnchorInfo anchor = anchorInfoService.findBymobile(taker.getMobile());
		Bill bill = new Bill();
		bill.setname(anchor.getName());
		bill.setUserId(taker.getId());
		bill.setType("转账");
		bill.setMoney(order.getOrderPrice().doubleValue());
		bill.setDesc("老带新订单完成，转账给大主播");
		bill.setBeginCreateDate(new Date());
		bill.setStatus(0);//未结算
		bill.setOrderId(order.getId());
		billService.save(bill);
		
		ajaxJson.setSuccess(true);
		return ajaxJson;
		
	}

	
	
	public static void main(String[] args) {
		//String res = WeixinApiController.closeOrder("123456789456");
		String res = WeixinApiController.closeOrder("ab9d03b097754bdbb7db9a74fb1d7e70");
		
	}
}
