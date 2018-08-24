package com.jeeplus.modules.oldnew.utils;


import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.jeeplus.common.config.Global;
import com.jeeplus.common.utils.SpringContextHolder;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.anchor.entity.AnchorInfo;
import com.jeeplus.modules.anchor.service.AnchorInfoService;
import com.jeeplus.modules.api.entity.UserApi;
import com.jeeplus.modules.api.service.UserApiService;
import com.jeeplus.modules.api.web.WeixinApiController;
import com.jeeplus.modules.bill.entity.Bill;
import com.jeeplus.modules.bill.service.BillService;
import com.jeeplus.modules.oldnew.entity.mycoupon.MyCoupon;
import com.jeeplus.modules.oldnew.entity.order.OldnewOrder;
import com.jeeplus.modules.oldnew.service.mycoupon.MyCouponService;
import com.jeeplus.modules.oldnew.service.order.OldnewOrderService;
import com.jeeplus.modules.sys.dao.DictDao;
import com.jeeplus.modules.sys.entity.Dict;

public class OldnewOrderTimer {

	private static OldnewOrderService oldnewOrderService = SpringContextHolder.getBean(OldnewOrderService.class);
	private static UserApiService userApiService = SpringContextHolder.getBean(UserApiService.class);
	private static MyCouponService myCouponService = SpringContextHolder.getBean(MyCouponService.class);
	private static DictDao dictDao = SpringContextHolder.getBean(DictDao.class);
	private static AnchorInfoService anchorInfoService = SpringContextHolder.getBean(AnchorInfoService.class);
	private static BillService billService = SpringContextHolder.getBean(BillService.class);
	
	 /**
	  * 定时任务,未按时支付关闭订单,以分钟为单位
	  * */
	 public static void closeOrder(final String orderId) {
		 Dict dict = new Dict();
		 dict.setType("oldnew_order_pay_timer");
		 List<Dict> list = dictDao.findListByType(dict);
		 if(list!=null && list.size()>0){
			 
			 int time = Integer.parseInt(list.get(0).getValue().trim());
			 System.out.println(time+"分后执行此方法");
			 time = 1000*60*time;
	         final Timer timer = new Timer();
	         timer.schedule(new TimerTask() {
		         public void run() {
		             //这里写修改方法，根据订单号修改状态就可以了
		        	OldnewOrder order =  oldnewOrderService.findById(orderId);
			       	if(order.getStatus().equals(Global.PAYING)){
			       		//微信关闭订单
			       		String res = WeixinApiController.closeOrder(order.getId());
			       		if("SUCCESS".equals(res)){
			       			order.setStatus(Global.CLOSED);
				       		order.setOverDate(new Date());
				       		oldnewOrderService.save(order);
				       		if(StringUtils.isNotBlank(order.getCouponId())){
				       			MyCoupon coupons = myCouponService.get(order.getCouponId());
								coupons.setStatus(Global.UNUSED);
								myCouponService.save(coupons);
				       		}
				       		System.out.println("----------timer close order success msg---------");
		                	System.out.println("order code:"+order.getOrderCode());
		                	System.out.println("-----------------------------------------------");
			       		}
			       	}else{
			       	   //不要忘记写中断定时器
			            timer.cancel();
			       	}
		        } 
	         },time);//1秒等于1000毫秒
		 }
	 }
	 
	 /**
	  * 定时任务,进行中订单未按时点击确认完成自动完成订单，以天为单位
	  * */
	 public static void finishOrder(final String orderId) {
		 Dict dict = new Dict();
		 dict.setType("oldnew_order_finish_timer");
		 List<Dict> list = dictDao.findListByType(dict);
		 if(list!=null && list.size()>0){
			 double dtime = Double.parseDouble(list.get(0).getValue().trim());
			 System.out.println(dtime+"天后执行此方法");
			 int time = (int) (1000*60*60*24*dtime);
	         final Timer timer = new Timer();
	         timer.schedule(new TimerTask() {
		         public void run() {
		             //这里写修改方法，根据订单号修改状态就可以了
		        	OldnewOrder order =  oldnewOrderService.findById(orderId);
			       	if(order.getStatus().equals(Global.ONGOING)){
			       		order.setStatus(Global.APPRAISING);//待评价
			       		order.setOverDate(new Date());
			       		oldnewOrderService.save(order);
			       		//账单表插数据
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
			       	}else{
			       	   //不要忘记写中断定时器
			            timer.cancel();
			       	}
		        } 
	         },time);//1秒等于1000毫秒
		 }
	 }
}
