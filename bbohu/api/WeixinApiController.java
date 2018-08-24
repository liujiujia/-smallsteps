/**
 * Copyright &copy; 2015-2020 怒醒文化传媒 All rights reserved.
 */
package com.jeeplus.modules.api.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.api.gateway.Response;
import com.aliyun.api.gateway.util.HttpUtil;
import com.aliyun.api.gateway.util.HttpUtils;
import com.aliyuncs.exceptions.ClientException;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.sms.SmsSender;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.common.utils.FileOperUtils;
import com.jeeplus.common.utils.HttpHelp;
import com.jeeplus.common.utils.IpUtil;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.anchor.entity.AnchorInfo;
import com.jeeplus.modules.anchor.service.AnchorInfoService;
import com.jeeplus.modules.api.entity.UserApi;
import com.jeeplus.modules.api.model.AnchorPersonModel;
import com.jeeplus.modules.api.model.PayCallBack;
import com.jeeplus.modules.api.model.UnifiedorderRequest;
import com.jeeplus.modules.api.model.UnifiedorderRespone;
import com.jeeplus.modules.api.service.UserApiService;
import com.jeeplus.modules.bill.entity.Bill;
import com.jeeplus.modules.bill.service.BillService;
import com.jeeplus.modules.oldnew.entity.order.OldnewOrder;
import com.jeeplus.modules.oldnew.entity.resourse.Product;
import com.jeeplus.modules.oldnew.entity.resourse.ResourceOrder;
import com.jeeplus.modules.oldnew.service.order.OldnewOrderService;
import com.jeeplus.modules.oldnew.service.resourse.ProductService;
import com.jeeplus.modules.oldnew.service.resourse.ResourceOrderService;
import com.jeeplus.modules.oldnew.utils.OldnewOrderTimer;
import com.jeeplus.modules.propaganda.entity.Propaganda;
import com.jeeplus.modules.propaganda.service.PropagandaService;
import com.jeeplus.modules.sys.dao.UserDao;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.talent.entity.TalentInfo;
import com.jeeplus.modules.talent.service.TalentInfoService;

/**
 * 微信对接Controller
 * 
 * @author wangqy
 * @version 2018-01-04
 */
@Controller
@RequestMapping(value = "${frontPath}/api/weixin")
public class WeixinApiController extends BaseController {

	@Autowired
	private UserApiService userApiService;
	@Autowired
	private TalentInfoService talentInfoService;
	@Autowired
	private AnchorInfoService anchorInfoService;
	@Autowired
	private PropagandaService propagandaService;
	@Autowired
	private OldnewOrderService oldnewOrderService;
	@Autowired
	private ResourceOrderService resService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ProductService proService;
	@Autowired
	private BillService billService;
	
	@ResponseBody
	@RequestMapping(value = "config")
	public Map<String, String> weixinconfig(String url, HttpServletResponse response) {
		getJsapiTicket();
		Sign.sign(url);
		response.setHeader("Access-Control-Allow-Origin", "*");
		return Global.weixin_config;
	}

	private String getAccessToken() {
		String APPID = Global.getConfig("APPID");
		String APPSECRET = Global.getConfig("APPSECRET");
		String access_token_url = Global.getConfig("access_token_url");
		access_token_url = access_token_url.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		Response response;
		long curTime = System.currentTimeMillis();
		if (Global.weixin_config.get("begin_time") == null || (Global.weixin_config.get("begin_time") != null
				&& curTime - Long.valueOf(Global.weixin_config.get("begin_time")) >= Global.cacheTime)) {
			try {
				// 获取微信access_token
				response = HttpUtil.httpGet(access_token_url);
				System.out.println(response.getBody());
				Map<String, Object> values = JSON.parseObject(response.getBody(), Map.class);
				String access_token = (String) values.get("access_token");
				Global.weixin_config.put("access_token", access_token);
				if (access_token != null) {
					Global.weixin_config.put("begin_time", System.currentTimeMillis() + "");
				}
				return access_token;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Global.weixin_config.get("access_token");
	}

	private String getJsapiTicket() {
		String access_token = getAccessToken();
		String jsapi_ticket_url = Global.getConfig("jsapi_ticket_url");
		jsapi_ticket_url = jsapi_ticket_url.replace("ACCESS_TOKEN", access_token);
		long curTime = System.currentTimeMillis();
		Response response;
		if (Global.weixin_config.get("begin2_time") == null || (Global.weixin_config.get("begin2_time") != null
				&& curTime - Long.valueOf(Global.weixin_config.get("begin2_time")) >= Global.cacheTime)) {
			try {
				// 根据access_token获取 jsapi_ticket
				response = HttpUtil.httpGet(jsapi_ticket_url);
				Map<String, Object> values = JSON.parseObject(response.getBody(), Map.class);
				System.out.println(response.getBody());
				String jsapi_ticket = (String) values.get("ticket");
				Global.weixin_config.put("begin2_time", System.currentTimeMillis() + "");
				Global.weixin_config.put("jsapi_ticket", jsapi_ticket);
				return jsapi_ticket;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Global.weixin_config.get("jsapi_ticket");
	}

	@ResponseBody
	@RequestMapping(value = "/downloadMedia")
	public AjaxJson downloadMediaFromWx(String mediaId, HttpServletRequest request, HttpServletResponse response) {
		String accessToken = getAccessToken();
		String fileSavePath = Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL + "upload/";
		FileOperUtils.createDirectory(fileSavePath);
		String upLoadPath = getUpLoadPath(0);
		AjaxJson ajaxJson = new AjaxJson();
		if (StringUtils.isEmpty(mediaId)) {
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("mediaId不能为空！");
			return ajaxJson;
		}
		String download_media_url = Global.getConfig("download_media_url");
		String requestUrl = download_media_url.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", mediaId);
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			InputStream in = conn.getInputStream();
			String ContentDisposition = conn.getHeaderField("Content-disposition");
			String weixinServerFileName = ContentDisposition.substring(ContentDisposition.indexOf("filename") + 10,
					ContentDisposition.length() - 1);
			weixinServerFileName = new Date().getTime()
					+ weixinServerFileName.substring(weixinServerFileName.indexOf("."));
			fileSavePath += upLoadPath + weixinServerFileName;
			// 修改图片保存路径及名称
			FileUtils.copyInputStreamToFile(in, new File(fileSavePath));
			System.out.println(fileSavePath + "保存图片路径");
			String imageUrl = Global.USERFILES_BASE_URL + "upload/" + upLoadPath + weixinServerFileName;
			ajaxJson.setSuccess(true);
			ajaxJson.put("imageUrl", imageUrl);
			ajaxJson.put("fileSavePath", fileSavePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		conn.disconnect();
		response.setHeader("Access-Control-Allow-Origin", "*");
		return ajaxJson;
	}

	

	private String getUpLoadPath(int pathType) {
		String path = "";
		Date currentDate = new Date();
		switch (pathType) {
		case 1: // 按年月日每天一个文件夹
			path += new SimpleDateFormat("yyyyMMdd").format(currentDate);
			break;
		default: // 按年月/日存入不同的文件夹
			path += new SimpleDateFormat("yyyyMM").format(currentDate) + "/"
					+ new SimpleDateFormat("dd").format(currentDate);
			break;
		}
		return path + "/";
	}

	@ResponseBody
	@RequestMapping(value = "connectReq")
	public AjaxJson connectReq(String refercode, String role, HttpServletResponse httpResponse) {
		String oauth_req_url = Global.getConfig("oauth_req_url");
		String appid = Global.getConfig("APPID");
		String redirectUrl = null;
		if ("Anchor".equals(role)) {
			redirectUrl = Global.getConfig("anchor_redirect_uri");
		}
		if ("Talent".equals(role)) {
			redirectUrl = Global.getConfig("talent_redirect_uri");
		}
		redirectUrl = redirectUrl + "?refercode=" + refercode;
		oauth_req_url = oauth_req_url.replace("APPID", appid);
		oauth_req_url = oauth_req_url.replace("REDIRECT_URI", redirectUrl);
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		AjaxJson ajaxJson = new AjaxJson();
		ajaxJson.setSuccess(true);
		ajaxJson.put("connectReqUrl", oauth_req_url);
		return ajaxJson;
	}



	 @ResponseBody
	 @RequestMapping(value = "getWxId")
	public AjaxJson getWxId(HttpServletRequest request, HttpServletResponse httpResponse) {
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		String code = request.getParameter("code");
		AjaxJson ajaxJson = new AjaxJson();
		if (!StringUtils.isBlank(code)) {
			String oauth_access_token_url = Global.getConfig("oauth_access_token_url");
			String appid = Global.getConfig("WEB_APPID");
			String secret = Global.getConfig("WEB_APPSECRET");
			oauth_access_token_url = oauth_access_token_url.replace("APPID", appid);
			oauth_access_token_url = oauth_access_token_url.replace("SECRET", secret);
			oauth_access_token_url = oauth_access_token_url.replace("CODE", code);

			Response response;
			try {
				// 获取微信access_token
				response = HttpUtil.httpGet(oauth_access_token_url);
				System.out.println(response.getBody());
				Map<String, Object> values = JSON.parseObject(response.getBody(), Map.class);
				String openid = (String) values.get("openid");
				UserApi userApi = userApiService.findByOpenId(openid);
				if (userApi != null) {
					ajaxJson.put("isbind", "YES");
					String token = UUID.randomUUID().toString();
					String usercode = String.valueOf(userApi.getUsercode());
					ajaxJson.setSuccess(true);
					ajaxJson.put("token", token);
					ajaxJson.put("usercode", usercode);
					// 将token及用户信息放入缓存
					CacheUtils.put(CacheUtils.USER_API_CACHE, token, userApi);
				} else {
					ajaxJson.put("isbind", "NO");
					ajaxJson.setSuccess(false);
				}
				ajaxJson.put("openid", openid);
			} catch (Exception e) {
				e.printStackTrace();
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg(e.getMessage());
			}

		} else {
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("code不能为空！");
		}
		return ajaxJson;
	}
	 
	

	/**
	 * @Title: getUnifiedorder @Description: 微信支付获取 预付单Id @param uniOrder @return
	 * String 返回类型 @throws
	 */
	public static String getUnifiedorder(UnifiedorderRequest uniOrder) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String unifiedorder_url = Global.getConfig("unifiedorder_url");
		HttpPost httppost = new HttpPost(unifiedorder_url);
		String retXml = "";
		try {
			String reqXml = HttpHelp.ObjecToXml(uniOrder);
			System.out.println("支付请求reqXml UTF-8 ：" + reqXml);
			reqXml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
			System.out.println("支付请求reqXml 替换后 ：" + reqXml);

			StringEntity myEntity = new StringEntity(reqXml, "UTF-8");
			httppost.addHeader("Content-Type", "text/xml");
			httppost.setEntity(myEntity);
			System.out.println("POST 请求...." + httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				retXml = EntityUtils.toString(entity, "UTF-8");
				System.out.println("支付请求返回retXml UTF-8 ：" + retXml);
				EntityUtils.consume(entity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retXml;
	}

	/**
	 * @Title: getUnifiedorder @Description: 微信支付预付单请 @param openerId
	 * 微信openerId @param money 订单价格(单位：分) @param desc 商品描述 @param rep @param resp
	 * void 返回类型 @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/wx/payRequest")
	public AjaxJson getUnifiedorder(String orderCode, String openerId, int money, String desc, HttpServletRequest rep,
			HttpServletResponse resp) {
		Map<String, String> paramsMapPay = null;
		AjaxJson ajaxJson = new AjaxJson();
		try {
			String appid = Global.getConfig("APPID");
			String mch_id = Global.getConfig("MCHID");
			String apiSecret = Global.getConfig("APISECRET");
			String pay_callback_url = Global.getConfig("pay_callback_url");
			String nonce_str = Sign.create_nonce_str().replace("-", "");
			String ip = IpUtil.getIpAddr(rep);
			UnifiedorderRequest uniOrder = new UnifiedorderRequest();
			uniOrder.setAppid(appid);
			uniOrder.setMch_id(mch_id);
			uniOrder.setBody(desc);
			uniOrder.setNonce_str(nonce_str);
			uniOrder.setNotify_url(pay_callback_url);
			uniOrder.setOut_trade_no(orderCode);
			uniOrder.setTotal_fee(String.valueOf(money));// 商品价格,单位为分
			uniOrder.setTrade_type("JSAPI");
			uniOrder.setSpbill_create_ip(ip);
			uniOrder.setOpenid(openerId);
			// 签名参数准备
			Map<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("appid", appid);
			paramsMap.put("mch_id", mch_id);
			paramsMap.put("body", desc);
			paramsMap.put("nonce_str", nonce_str);
			paramsMap.put("notify_url", pay_callback_url);
			paramsMap.put("out_trade_no", orderCode);
			paramsMap.put("total_fee", String.valueOf(money));// 商品价格,单位为分
			paramsMap.put("trade_type", "JSAPI");
			paramsMap.put("spbill_create_ip", ip);
			paramsMap.put("openid", openerId);

			String sign = Sign.signWXPay(paramsMap, apiSecret);
			uniOrder.setSign(sign);
			// 获取预付单Id
			String retXml = getUnifiedorder(uniOrder);
			UnifiedorderRespone uniOrderRespone = HttpHelp.XmlToObject(retXml, UnifiedorderRespone.class);
			if (!"SUCCESS".equals(uniOrderRespone.getResult_code())
					|| !"SUCCESS".equals(uniOrderRespone.getReturn_code())) {
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg(uniOrderRespone.getErr_code_des());
				return ajaxJson;
			}
			// 准备支付数据
			String timestamp = Sign.create_timestamp();
			String nonce_str_pay = Sign.create_nonce_str().replace("-", "");
			paramsMapPay = new HashMap<String, String>();
			paramsMapPay.put("appId", appid);
			paramsMapPay.put("timeStamp", timestamp);
			paramsMapPay.put("nonceStr", nonce_str_pay);
			paramsMapPay.put("package", "prepay_id=" + uniOrderRespone.getPrepay_id());
			paramsMapPay.put("signType", "MD5");
			// 支付签名
			String paySign = Sign.signWXPay(paramsMapPay, apiSecret);
			paramsMapPay.put("paySign", paySign);
			rep.setAttribute("paramsMap", paramsMapPay);

			ajaxJson.setSuccess(true);
			ajaxJson.put("payParams", paramsMapPay);

		} catch (Exception e) {
			logger.error("微信充值下单失败!:{}", e);
		}
		return ajaxJson;
	}


	/**
	 * @Title: payCallBack @Description: 支付成功后通知商户 @param rep @param resp void
	 * 返回类型 @throws
	 */
	@RequestMapping(value = "/payCallBack")
	private void payCallBack(HttpServletRequest rep, HttpServletResponse resp) {
		InputStream inputStream = null;
		String result = "";
		try {
			inputStream = rep.getInputStream();
			result = IOUtils.toString(inputStream, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("支付回调结果:" + result);

		PayCallBack payCallBack = HttpHelp.XmlToObject(result, PayCallBack.class);
		Map<String, String> paramsMapPay = new HashMap<String, String>();
		paramsMapPay.put("appid", payCallBack.getAppid());
		paramsMapPay.put("bank_type", payCallBack.getBank_type());
		paramsMapPay.put("cash_fee", payCallBack.getCash_fee() + "");
		paramsMapPay.put("fee_type", payCallBack.getFee_type());
		paramsMapPay.put("is_subscribe", payCallBack.getIs_subscribe());
		paramsMapPay.put("mch_id", payCallBack.getMch_id());
		paramsMapPay.put("nonce_str", payCallBack.getNonce_str());
		paramsMapPay.put("openid", payCallBack.getOpenid());
		paramsMapPay.put("out_trade_no", payCallBack.getOut_trade_no());
		paramsMapPay.put("result_code", payCallBack.getResult_code());
		paramsMapPay.put("return_code", payCallBack.getReturn_code());
		paramsMapPay.put("time_end", payCallBack.getTime_end());
		paramsMapPay.put("total_fee", payCallBack.getTotal_fee() + "");
		paramsMapPay.put("trade_type", payCallBack.getTrade_type());
		paramsMapPay.put("transaction_id", payCallBack.getTransaction_id());
		// 回调处理成功后返回
		String returnXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
				+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
		// 支付签名
		String paySign = Sign.signWXPay(paramsMapPay, Global.getConfig("APISECRET"));
		if (paySign.equals(payCallBack.getSign())) {
			if (payCallBack.getResult_code().equals("SUCCESS") && payCallBack.getReturn_code().equals("SUCCESS")) {
				// 业务处理
				synchronized (paySign) {
					OldnewOrder order = oldnewOrderService.findById(payCallBack.getOut_trade_no());
					DecimalFormat df = new DecimalFormat("0.00");
					String money = df.format(order.getOrderPrice());
					// order.setPaymentStatus(Global.PAID);//已支付
					if (StringUtils.isBlank(order.getTakerId())) {
						order.setStatus(Global.TAKING);// 待接单
						oldnewOrderService.save(order);
						// 通知被带者
						UserApi userPuber = userApiService.get(order.getPublisherId());
						String topuber = userPuber.getWeixinOperid();
						if (StringUtils.isNotBlank(topuber)) {
							String template_id = Global.getConfig("order_template_id");
							String url = Global.getConfig("anchor_redirect_uri");
							// 根据具体模板参数组装
							String title = "您的老带新订单已经支付成功";
							String orderCode = order.getOrderCode().toString();
							String status = "待接单";
							String info = "订单金额：" + money + "元，登录系统查看订单详情";
							String color = "#ff0000";
							Map<String, Map<String, String>> params = WeixinApiController.getParams(title, orderCode,
									status, info, color);
							try {
								sendTemplate(topuber, template_id, url, params);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						order.setStatus(Global.ONGOING);// 进行中
						order.setReceiveDate(new Date());// 接单时间
						oldnewOrderService.save(order);
						// 通知双方
						UserApi userPuber = userApiService.get(order.getPublisherId());
						UserApi userTaker = userApiService.get(order.getTakerId());
						String totaker = userTaker.getWeixinOperid();
						String topuber = userPuber.getWeixinOperid();
						AnchorPersonModel puber = anchorInfoService.getBasicInfo(order.getPublisherId());
						if (StringUtils.isNotBlank(topuber) && StringUtils.isNotBlank(totaker)) {
							String template_id = Global.getConfig("order_template_id");
							String url = Global.getConfig("anchor_redirect_uri");
							String title = "您的老带新订单已经支付成功";
							String title1 = "您的老带新订单已经被" + puber.getNickName() + "下单";
							String orderCode = order.getOrderCode().toString();
							String status = "进行中";
							String info = "订单金额：" + money + "元，请主动联系对方，登录系统查看订单详情";
							String color = "#09bb07";
							Map<String, Map<String, String>> params = WeixinApiController.getParams(title, orderCode,
									status, info, color);
							Map<String, Map<String, String>> params1 = WeixinApiController.getParams(title1, orderCode,
									status, info, color);
							try {
								sendTemplate(topuber, template_id, url, params);
								sendTemplate(totaker, template_id, url, params1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// 短信通知带人者
						String status = "进行中";
						String remark = "下单人：" + puber.getNickName() + "，订单金额：" + money + "元";
						String contentJson = "{\"status\":\"" + status + "\",\"remark\":\"" + remark + "\"}";
						try {
							SmsSender.sendSms(userTaker.getMobile(), SmsSender.SMS_ANCHOR_PLACE_ORDER_TEMPLATE,
									contentJson);
						} catch (ClientException e) {
							e.printStackTrace();
						}
						try {
							OldnewOrderTimer.finishOrder(order.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}

			}
		} else {
			returnXml.replace("SUCCESS", "FAIL").replace("OK", "校验失败");
		}
		try {
			resp.getWriter().write(returnXml);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 组装微信订单模板参数
	 */
	public static Map<String, Map<String, String>> getParams(String title, String orderCode, String status, String info,
			String color) {
		Map<String, Map<String, String>> params = new HashMap<String, Map<String, String>>();
		// 根据具体模板参数组装
		Map<String, String> first = new HashMap<String, String>();
		first.put("value", title);
		first.put("color", "#000000");
		params.put("first", first);
		Map<String, String> OrderSn = new HashMap<String, String>();
		OrderSn.put("value", orderCode);
		OrderSn.put("color", "#000000");
		params.put("OrderSn", OrderSn);
		Map<String, String> OrderStatus = new HashMap<String, String>();
		OrderStatus.put("value", status);
		OrderStatus.put("color", color);
		params.put("OrderStatus", OrderStatus);
		Map<String, String> remark = new HashMap<String, String>();
		remark.put("value", info);
		remark.put("color", "#000000");
		params.put("remark", remark);
		return params;
	}

	/**
	 * 关闭订单
	 * 
	 * @param orderId
	 * @param resp
	 * @return
	 */
	public static String closeOrder(String orderId) {
		String appid = Global.getConfig("APPID");
		String mch_id = Global.getConfig("MCHID");
		String apiSecret = Global.getConfig("APISECRET");
		String close_order_url = Global.getConfig("close_order_url");
		String nonce_str = Sign.create_nonce_str().replace("-", "");

		UnifiedorderRequest uniOrder = new UnifiedorderRequest();
		uniOrder.setAppid(appid);
		uniOrder.setMch_id(mch_id);
		uniOrder.setNonce_str(nonce_str);
		uniOrder.setOut_trade_no(orderId);
		uniOrder.setSign_type("MD5");
		// 签名参数准备
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("appid", appid);
		paramsMap.put("mch_id", mch_id);
		paramsMap.put("nonce_str", nonce_str);
		paramsMap.put("out_trade_no", orderId);
		paramsMap.put("sign_type", "MD5");

		String sign = Sign.signWXPay(paramsMap, apiSecret);
		uniOrder.setSign(sign);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(close_order_url);
		String retXml = "";
		try {
			String reqXml = HttpHelp.ObjecToXml(uniOrder);
			System.out.println("支付请求reqXml UTF-8 ：" + reqXml);
			reqXml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
			System.out.println("支付请求reqXml 替换后 ：" + reqXml);

			StringEntity myEntity = new StringEntity(reqXml, "UTF-8");
			httppost.addHeader("Content-Type", "text/xml");
			httppost.setEntity(myEntity);
			System.out.println("POST 请求...." + httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				retXml = EntityUtils.toString(entity, "UTF-8");
				System.out.println("支付请求返回retXml UTF-8 ：" + retXml);
				EntityUtils.consume(entity);

				UnifiedorderRespone uniOrderRespone = HttpHelp.XmlToObject(retXml, UnifiedorderRespone.class);
				if (!"SUCCESS".equals(uniOrderRespone.getResult_code())
						|| !"SUCCESS".equals(uniOrderRespone.getReturn_code())) {
					System.out.println("----------weixin close order error msg---------");
					System.out.println(uniOrderRespone.getErr_code_des());
					System.out.println("-----------------------------------------------");
					return uniOrderRespone.getErr_code_des();
				} else {
					System.out.println("----------weixin close order success msg---------");
					System.out.println("orderId:" + orderId);
					System.out.println("-------------------------------------------------");
					return "SUCCESS";
				}
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "FALT";

	}
}
