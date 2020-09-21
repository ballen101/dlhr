package com.corsair.server.weixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.CSContext.ActionType;
import com.corsair.server.util.CorUtil;
import com.corsair.server.weixin.WXUtil.MsgType;
import com.corsair.server.weixin.entity.Shwwxapp;
import com.corsair.server.weixin.entity.Shwwxmsgcfg;
import com.corsair.server.weixin.entity.Shwwxmsgcfg_key;

public class CoreService {

	/**
	 * 处理微信接消息核心函数
	 * 解析get/post参数
	 * 验证签名数据是否合法
	 * 
	 * @param appid
	 * @return
	 * @throws Exception
	 */
	public static String winxincore(String appid) throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String signature = CorUtil.hashMap2Str(parms, "signature");
		String timestamp = CorUtil.hashMap2Str(parms, "timestamp");
		String nonce = CorUtil.hashMap2Str(parms, "nonce");
		String WxToken = WXAppParms.getAppParm(appid, "WxToken", "微信服务需要设置WxToken参数");
		// ConstsSw.geAppParmStr_E("WxToken", "微信服务需要设置WxToken参数");
		if ((signature != null) && (timestamp != null) && (nonce != null))
			if (!WXUtil.checkSign(WxToken, signature, timestamp, nonce)) {
				Logsw.error("收到微信消息，验证签名失败");
				return "";
			}
		// System.out.println("winxincore:" + appid);
		if (CSContext.getActionType() == ActionType.actGet) { // 这个应该只是用来配置微信回调URL的，没有这个无法在微信端配置 o(*≧▽≦)ツ
			String echostr = CorUtil.hashMap2Str(parms, "echostr").trim();
			return echostr;
		}
		if (CSContext.getActionType() == ActionType.actPost) {
			String pdata = CSContext.getPostdata();
			// String rst = null;
			// if (WXUtil.checkSign(WxToken, signature, timestamp, nonce)) {
			return processRequest(appid, pdata);// 解析请求
			// } else {
			// rst = "error";
			// }
			// return rst;
		}
		return "error"; // 只要返回数据微信就会认为服务正常
	}

	public static String processRequest(String appid, String data) {
		try {
			Logsw.debug("收到微信信息:" + data);
			Shwwxapp wxapp = WXAppParms.getByWXAppID(appid);
			Map<String, String> postparms = WXUtil.parePostData(data);
			String msgtype = postparms.get("MsgType").toString();
			if ((msgtype == null) || msgtype.isEmpty())
				return "msgtype is null";
			if (msgtype.equalsIgnoreCase("event")) {
				return processEvent(wxapp, postparms);
			} else
				return processOrdMsg(wxapp, postparms);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "";
		}
	}

	// ///////////普通消息////////////////
	private static String processOrdMsg(Shwwxapp wxapp, Map<String, String> postparms) {
		// 在线客服转发，先暂停
		// String trans = WXAppParms.getAppParm(wxapp, "WxServiceOnlineTransfer");
		String trans = WXAppParms.getAppParm(wxapp, "WxServiceOnlineTransfer");
		boolean tran = (trans == null) ? false : Boolean.valueOf(trans);
		if (tran) {
			// String fuopenid = null;
			// Object o = postparms.get("FromUserName");
			// if (o != null)
			// fuopenid = o.toString();
			// List<HashMap<String, String>> oss = WXUtil.getOnLineServerList(wxapp.wxappid.getValue());
			// if ((oss != null) && (oss.size() == 0)) {
			// // WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.OlS_notol, postparms);
			// // return "";
			// } else {
			// String transtr = "<xml>"
			// + " <ToUserName><![CDATA[" + fuopenid + "]]></ToUserName>"
			// + " <FromUserName><![CDATA[ICEFALLService]]></FromUserName>"
			// + " <CreateTime>" + WXUtil.getWXDatetime() + "</CreateTime>"
			// + " <MsgType><![CDATA[transfer_customer_service]]></MsgType>"
			// + "</xml>";
			// Logsw.debug("转发在线客服");
			// return transtr;
			// }
			if (WCCSOAPI.resendToCSO(wxapp, postparms))// 转发在线客服成功 不处理文本消息
				return "";
		}

		String msgtype = postparms.get("MsgType").toString();
		if (msgtype.equalsIgnoreCase("text")) {// 文本消息
			return processTextMsg(wxapp, postparms);
		} else if (msgtype.equalsIgnoreCase("image")) {
			return processPicMsg(wxapp, postparms);
		} else if (msgtype.equalsIgnoreCase("voice")) {
			return processVoiceMsg(wxapp, postparms);
		} else if (msgtype.equalsIgnoreCase("video")) {
			return processVideoMsg(wxapp, postparms);
		} else if (msgtype.equalsIgnoreCase("location")) {
			return processLocationMsg(wxapp, postparms);
		} else if (msgtype.equalsIgnoreCase("link")) {
			return processLinkMsg(wxapp, postparms);
		} else if (msgtype.equalsIgnoreCase("device_text")) {
			WXUtil.doSaveReceivedMsg(MsgType.DeviceText, postparms);
			WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.DeviceText, postparms);
			return "";
		} else if (msgtype.equalsIgnoreCase("device_event")) {
			String Event = postparms.get("Event");
			if (Event == null) {
				Logsw.error("设备事件 Event 参数不能为空");
				return "";
			}
			if (Event.equalsIgnoreCase("bind")) {
				WXUtil.doSaveReceivedMsg(MsgType.DeviceEvent_Bind, postparms);
				WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.DeviceEvent_Bind, postparms);
			}
			if (Event.equalsIgnoreCase("unbind")) {
				WXUtil.doSaveReceivedMsg(MsgType.DeviceEvent_UNBind, postparms);
				WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.DeviceEvent_UNBind, postparms);
			}
			return "";
		}
		return "";
	}

	private static String processTextMsg(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.text, postparms);
		String fuopenid = (postparms.get("FromUserName") == null) ? null : postparms.get("FromUserName").toString();

		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.text, postparms);

		Shwwxmsgcfg msgcfg = wxapp.getWxmsgcfg();
		String content = (postparms.get("Content") == null) ? null : postparms.get("Content").toString();
		if ((content != null) && (!content.isEmpty())) {
			List<Shwwxmsgcfg_key> keys = getmsgkeys(msgcfg, content);
			if (keys.size() == 0) {// 发送默认消息
				sendDefaultMSG(wxapp, msgcfg, postparms, fuopenid);
			} else {// 发送关键字消息
				try {
					sendKeyMsgs(wxapp, msgcfg, postparms, keys, fuopenid);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	/**
	 * 发送关键字信息
	 * 
	 * @param msgcfg
	 * @param keys
	 * @param fuopenid
	 * @throws Exception
	 */
	private static void sendKeyMsgs(Shwwxapp wxapp, Shwwxmsgcfg msgcfg, Map<String, String> postparms, List<Shwwxmsgcfg_key> keys, String fuopenid) throws Exception {
		if ((msgcfg == null) || (fuopenid == null) || (fuopenid.isEmpty()))
			return;
		if ((msgcfg.usable.getAsIntDefault(0) == 2) || (msgcfg.text_atrep_key.getAsIntDefault(0) == 2))// 启用关键字回复
			return;
		// System.out.println("sendKeyMsgs keys" + keys.size());
		for (Shwwxmsgcfg_key key : keys) {
			// System.out.println("sendKeyMsgs key:" + key.tojson());
			// System.out.println("usable:" + (key.usable.getAsIntDefault(0) == 1));
			if (key.usable.getAsIntDefault(0) == 1) {
				String text_atrep_tp = key.text_atrep_tp.getValue();// //自动回复内容方式 1 文本 2 连接 可多选
				//System.out.println(text_atrep_tp);
				//System.out.println(isInArr("3", text_atrep_tp));
				if ((text_atrep_tp == null) || (text_atrep_tp.isEmpty()))
					continue;
				if (isInArr("1", text_atrep_tp)) {
					if (!key.text_atrep_msg.isEmpty()) {
						// System.out.println("sendKeyMsgs key text:" + key.text_atrep_msg.getValue());
						String text_atrep_msg = key.text_atrep_msg.getValue();
						if (WXUtil.callReplayTextMessage(msgcfg.wxappid.getValue(), fuopenid, text_atrep_msg))
							WXMsgSend.sendTextMsg(fuopenid, text_atrep_msg);
					}
				}
				if (isInArr("2", text_atrep_tp)) {
					if (!key.text_newsid.isEmpty()) {
						// System.out.println("sendKeyMsgs key text_newsid:" + key.text_newsid.getValue());
						if (WXUtil.callReplayNewsMessage(msgcfg.wxappid.getValue(), fuopenid, msgcfg.text_newsid.getValue()))
							WXMsgSend.sendNewMsgByID(fuopenid, key.text_newsid.getValue());
					}
				}
				if (isInArr("3", text_atrep_tp)) {// 转接客服
					Logsw.debug("转接客服...");
					WCCSOAPI.connectToCSO(wxapp, postparms);
				}
				if (msgcfg.text_atrep_key_type.getAsIntDefault(0) == 2) {// 多关键字检索方式:1 所有关键字匹配 2 按顺序匹配第一个关键字
					break; // 找到第一个 就不找了
				}
			}
		}
	}

	/**
	 * 发送默认消息
	 * 
	 * @param msgcfg
	 * @param postparms
	 */
	private static void sendDefaultMSG(Shwwxapp wxapp, Shwwxmsgcfg msgcfg, Map<String, String> postparms, String fuopenid) {
		if ((msgcfg == null) || (fuopenid == null) || (fuopenid.isEmpty()))
			return;
		if ((msgcfg.usable.getAsIntDefault(0) == 2) || (msgcfg.text_atrep.getAsIntDefault(0) == 2))// //收到消息自动回复启用
			return;

		String text_atrep_tp = msgcfg.text_atrep_tp.getValue();// //自动回复内容方式 1 文本 2 连接 可多选
		if ((text_atrep_tp == null) || (text_atrep_tp.isEmpty()))
			return;
		if (isInArr("1", text_atrep_tp)) {
			if (!msgcfg.text_atrep_msg.isEmpty()) {
				String text_atrep_msg = msgcfg.text_atrep_msg.getValue();
				try {
					if (WXUtil.callReplayTextMessage(msgcfg.wxappid.getValue(), fuopenid, text_atrep_msg))
						WXMsgSend.sendTextMsg(fuopenid, text_atrep_msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (isInArr("2", text_atrep_tp)) {
			if (!msgcfg.text_newsid.isEmpty()) {
				if (WXUtil.callReplayNewsMessage(msgcfg.wxappid.getValue(), fuopenid, msgcfg.text_newsid.getValue()))
					WXMsgSend.sendNewMsgByID(fuopenid, msgcfg.text_newsid.getValue());
			}
		}
		if (isInArr("3", text_atrep_tp)) {// 转接客服
			Logsw.debug("转接客服...");
			WCCSOAPI.connectToCSO(wxapp, postparms);
		}
	}

	/**
	 * k 是否在 s 以逗号分隔的字符串中
	 * 
	 * @param k
	 * @param s
	 * @return
	 */
	private static boolean isInArr(String k, String s) {
		if ((k == null) || k.isEmpty())
			return false;
		String[] vs = s.split(",");
		for (String v : vs) {
			if (v != null)
				if (k.equals(v))
					return true;
		}
		return false;
	}

	/**
	 * 根据收到的文本信息，匹配关键字消息参数
	 * 
	 * @param msgcfg
	 * @param value
	 * @return
	 */
	private static List<Shwwxmsgcfg_key> getmsgkeys(Shwwxmsgcfg msgcfg, String value) {
		List<Shwwxmsgcfg_key> rst = new ArrayList<Shwwxmsgcfg_key>();
		if (value == null)
			return rst;
		if (msgcfg.text_atrep_key.getAsIntDefault(0) == 1) {
			CJPALineData<Shwwxmsgcfg_key> keys = msgcfg.shwwxmsgcfg_keys;
			for (CJPABase jpa : keys) {
				Shwwxmsgcfg_key key = (Shwwxmsgcfg_key) jpa;
				if (key.usable.getAsIntDefault(0) == 1) {
					if (key.cptype.getAsIntDefault(0) == 1) {// 匹配方式:1 完全匹配 2 部分匹配
						if (value.equals(key.msgkey.getValue())) {
							rst.add(key);
							if (msgcfg.text_atrep_key_type.getAsIntDefault(0) == 2)// 多关键字检索方式:1 所有关键字匹配 2 按顺序匹配第一个关键字
								break;
						}
					} else {
						if (value.indexOf(key.msgkey.getValue()) >= 0) {
							rst.add(key);
							if (msgcfg.text_atrep_key_type.getAsIntDefault(0) == 2)// 多关键字检索方式:1 所有关键字匹配 2 按顺序匹配第一个关键字
								break;
						}
					}
				}
			}
		}
		return rst;
	}

	private static String processPicMsg(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.image, postparms);
		sendDefaultMSG(wxapp, wxapp.getWxmsgcfg(), postparms, postparms.get("FromUserName"));
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.image, postparms);
		return "";
	}

	private static String processVoiceMsg(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.voice, postparms);
		sendDefaultMSG(wxapp, wxapp.getWxmsgcfg(), postparms, postparms.get("FromUserName"));
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.voice, postparms);
		return "";
	}

	private static String processVideoMsg(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.video, postparms);
		sendDefaultMSG(wxapp, wxapp.getWxmsgcfg(), postparms, postparms.get("FromUserName"));
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.video, postparms);
		return "";
	}

	private static String processLocationMsg(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.location, postparms);
		sendDefaultMSG(wxapp, wxapp.getWxmsgcfg(), postparms, postparms.get("FromUserName"));
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.location, postparms);
		return "";
	}

	private static String processLinkMsg(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.link, postparms);
		sendDefaultMSG(wxapp, wxapp.getWxmsgcfg(), postparms, postparms.get("FromUserName"));
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.link, postparms);
		return "";
	}

	// //////////////事件消息///////////////////
	public static String processEvent(Shwwxapp wxapp, Map<String, String> postparms) throws Exception {
		if (postparms == null)
			throw new Exception("postparms is null");
		if (wxapp == null)
			throw new Exception("wxapp is null");
		String event = postparms.get("Event").toString();
		if (event == null)
			throw new Exception("事件消息，Event居然为空，企鹅马搞错了");
		if (event.equalsIgnoreCase("subscribe")) {
			// 关注事件
			return processSubscribeEvent(wxapp, postparms);
		} else if (event.equalsIgnoreCase("unsubscribe")) {
			// 取消关注事件
			return processUnsubscribeEvent(wxapp, postparms);
		} else if (event.equalsIgnoreCase("SCAN")) {
			// 扫码
			return processScanEvent(wxapp, postparms);
		} else if (event.equalsIgnoreCase("LOCATION")) {
			// 位置事件
			return processLocationEvent(wxapp, postparms);
		} else if (event.equalsIgnoreCase("CLICK")) {
			// 菜单单击事件
			return processClickEvent(wxapp, postparms);
		} else if (event.equalsIgnoreCase("VIEW")) {
			// 菜单跳转事件
			return processViewEvent(wxapp, postparms);
		} else if (event.equalsIgnoreCase("kf_switch_session")) {
			// 多客服 转接
			return processKFSwitchSessionEvent(wxapp, postparms);
		} else if (event.equalsIgnoreCase("TEMPLATESENDJOBFINISH")) {
			return processTemplateSendFinishEvent(wxapp, postparms);
		}

		return "";
	}

	private static String processSubscribeEvent(Shwwxapp wxapp, Map<String, String> postparms) {
		Object obj = postparms.get("EventKey");
		// System.out.println(obj);
		if ((obj != null) && (obj.toString().length() > "qrscene_".length())) {
			String eventkey = postparms.get("EventKey").toString();// 开头
			String scanstr = eventkey.substring("qrscene_".length());
			System.out.println("扫码关注scanstr:" + scanstr);
			WXUtil.doSaveReceivedMsg(MsgType.subscribe_SCAN, postparms);
			WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.subscribe_SCAN, postparms);
		} else {
			// 非扫码关注
			WXUtil.doSaveReceivedMsg(MsgType.subscribe, postparms);
			WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.subscribe, postparms);
		}
		sendSubscribeMsg(wxapp, postparms);
		return "";
	}

	/**
	 * 关注回复
	 * 
	 * @param wxapp
	 * @param postparms
	 */
	private static void sendSubscribeMsg(Shwwxapp wxapp, Map<String, String> postparms) {
		String fuopenid = (postparms.get("FromUserName") == null) ? null : postparms.get("FromUserName").toString();
		Shwwxmsgcfg msgcfg = wxapp.getWxmsgcfg();
		if ((msgcfg == null) || (fuopenid == null) || (fuopenid.isEmpty()))
			return;
		if ((msgcfg.usable.getAsIntDefault(0) == 2) || (msgcfg.subscribe_atrep.getAsIntDefault(0) == 2))// 关注自动回复启用
			return;

		String subscribe_atrep_tp = msgcfg.subscribe_atrep_tp.getValue();// 关注自动回复内容方式 1 文本 2 连接 可多选
		if ((subscribe_atrep_tp == null) || (subscribe_atrep_tp.isEmpty()))
			return;
		for (String tp : subscribe_atrep_tp.split(",")) {
			if ("1".equalsIgnoreCase(tp)) {
				if (!msgcfg.subscribe_atrep_msg.isEmpty()) {
					String subscribe_atrep_msg = msgcfg.subscribe_atrep_msg.getValue();
					try {
						if (WXUtil.callReplayTextMessage(msgcfg.wxappid.getValue(), fuopenid, subscribe_atrep_msg))
							WXMsgSend.sendTextMsg(fuopenid, subscribe_atrep_msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if ("2".equalsIgnoreCase(tp)) {
				if (!msgcfg.subscribe_newsid.isEmpty()) {
					if (WXUtil.callReplayNewsMessage(msgcfg.wxappid.getValue(), fuopenid, msgcfg.subscribe_newsid.getValue()))
						WXMsgSend.sendNewMsgByID(fuopenid, msgcfg.subscribe_newsid.getValue());
				}
			}
		}
	}

	private static String processUnsubscribeEvent(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.unsubscribe, postparms);
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.unsubscribe, postparms);
		return "";
	}

	private static String processScanEvent(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.SCAN, postparms);
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.SCAN, postparms);
		return "";
	}

	private static String processLocationEvent(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.LOCATION, postparms);
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.LOCATION, postparms);
		return "";
	}

	private static String processClickEvent(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.CLICK, postparms);
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.CLICK, postparms);
		return "";
	}

	private static String processViewEvent(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.VIEW, postparms);
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.VIEW, postparms);
		return "";
	}

	private static String processKFSwitchSessionEvent(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.KF_switch_session, postparms);
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.KF_switch_session, postparms);
		return "";
	}

	private static String processTemplateSendFinishEvent(Shwwxapp wxapp, Map<String, String> postparms) {
		WXUtil.doSaveReceivedMsg(MsgType.sendTemplateMsgFinish, postparms);
		WXUtil.callEventListener(wxapp.wxappid.getValue(), MsgType.sendTemplateMsgFinish, postparms);

		String msgid = postparms.get("MsgID");
		String Status = postparms.get("Status");
		String sqlstr = "update shwwxtempmsgsend set rcstate='" + Status + "' where msgid='" + msgid + "'";
		try {
			DBPools.defaultPool().execsql(sqlstr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// public static String processRequest(HttpServletRequest request) {
	// String respMessage = null;
	// try {
	// // xml请求解析
	// Map<String, String> requestMap = MessageUtil.parseXml(request);
	//
	// // 发送方帐号（open_id）
	// String fromUserName = requestMap.get("FromUserName");
	// // 公众帐号
	// String toUserName = requestMap.get("ToUserName");
	// // 消息类型
	// String msgType = requestMap.get("MsgType");
	//
	// // 默认回复此文本消息
	// TextMessage textMessage = new TextMessage();
	// textMessage.setToUserName(fromUserName);
	// textMessage.setFromUserName(toUserName);
	// textMessage.setCreateTime(new Date().getTime());
	// textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
	// textMessage.setFuncFlag(0);
	// // 由于href属性值必须用双引号引起，这与字符串本身的双引号冲突，所以要转义
	// textMessage.setContent("欢迎访问<a href=\"http://blog.csdn.net/lyq8479\">柳峰的博客</a>!");
	// // 将文本消息对象转换成xml字符串
	// respMessage = MessageUtil.textMessageToXml(textMessage);
	//
	// // 文本消息
	// if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
	// // 接收用户发送的文本消息内容
	// String content = requestMap.get("Content");
	//
	// // 创建图文消息
	// NewsMessage newsMessage = new NewsMessage();
	// newsMessage.setToUserName(fromUserName);
	// newsMessage.setFromUserName(toUserName);
	// newsMessage.setCreateTime(new Date().getTime());
	// newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
	// newsMessage.setFuncFlag(0);
	//
	// List<Article> articleList = new ArrayList<Article>();
	// // 单图文消息
	// if ("1".equals(content)) {
	// Article article = new Article();
	// article.setTitle("微信公众帐号开发教程Java版");
	// article.setDescription("柳峰，80后，微信公众帐号开发经验4个月。为帮助初学者入门，特推出此系列教程，也希望借此机会认识更多同行！");
	// article.setPicUrl("http://0.xiaoqrobot.duapp.com/images/avatar_liufeng.jpg");
	// article.setUrl("http://blog.csdn.net/lyq8479");
	// articleList.add(article);
	// // 设置图文消息个数
	// newsMessage.setArticleCount(articleList.size());
	// // 设置图文消息包含的图文集合
	// newsMessage.setArticles(articleList);
	// // 将图文消息对象转换成xml字符串
	// respMessage = MessageUtil.newsMessageToXml(newsMessage);
	// }
	// // 单图文消息---不含图片
	// else if ("2".equals(content)) {
	// Article article = new Article();
	// article.setTitle("微信公众帐号开发教程Java版");
	// // 图文消息中可以使用QQ表情、符号表情
	// article.setDescription("柳峰，80后，"
	// + emoji(0x1F6B9)
	// +
	// "，微信公众帐号开发经验4个月。为帮助初学者入门，特推出此系列连载教程，也希望借此机会认识更多同行！\n\n目前已推出教程共12篇，包括接口配置、消息封装、框架搭建、QQ表情发送、符号表情发送等。\n\n后期还计划推出一些实用功能的开发讲解，例如：天气预报、周边搜索、聊天功能等。");
	// // 将图片置为空
	// article.setPicUrl("");
	// article.setUrl("http://blog.csdn.net/lyq8479");
	// articleList.add(article);
	// newsMessage.setArticleCount(articleList.size());
	// newsMessage.setArticles(articleList);
	// respMessage = MessageUtil.newsMessageToXml(newsMessage);
	// }
	// // 多图文消息
	// else if ("3".equals(content)) {
	// Article article1 = new Article();
	// article1.setTitle("微信公众帐号开发教程\n引言");
	// article1.setDescription("");
	// article1.setPicUrl("http://0.xiaoqrobot.duapp.com/images/avatar_liufeng.jpg");
	// article1.setUrl("http://blog.csdn.net/lyq8479/article/details/8937622");
	//
	// Article article2 = new Article();
	// article2.setTitle("第2篇\n微信公众帐号的类型");
	// article2.setDescription("");
	// article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
	// article2.setUrl("http://blog.csdn.net/lyq8479/article/details/8941577");
	//
	// Article article3 = new Article();
	// article3.setTitle("第3篇\n开发模式启用及接口配置");
	// article3.setDescription("");
	// article3.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
	// article3.setUrl("http://blog.csdn.net/lyq8479/article/details/8944988");
	//
	// articleList.add(article1);
	// articleList.add(article2);
	// articleList.add(article3);
	// newsMessage.setArticleCount(articleList.size());
	// newsMessage.setArticles(articleList);
	// respMessage = MessageUtil.newsMessageToXml(newsMessage);
	// }
	// // 多图文消息---首条消息不含图片
	// else if ("4".equals(content)) {
	// Article article1 = new Article();
	// article1.setTitle("微信公众帐号开发教程Java版");
	// article1.setDescription("");
	// // 将图片置为空
	// article1.setPicUrl("");
	// article1.setUrl("http://blog.csdn.net/lyq8479");
	//
	// Article article2 = new Article();
	// article2.setTitle("第4篇\n消息及消息处理工具的封装");
	// article2.setDescription("");
	// article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
	// article2.setUrl("http://blog.csdn.net/lyq8479/article/details/8949088");
	//
	// Article article3 = new Article();
	// article3.setTitle("第5篇\n各种消息的接收与响应");
	// article3.setDescription("");
	// article3.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
	// article3.setUrl("http://blog.csdn.net/lyq8479/article/details/8952173");
	//
	// Article article4 = new Article();
	// article4.setTitle("第6篇\n文本消息的内容长度限制揭秘");
	// article4.setDescription("");
	// article4.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
	// article4.setUrl("http://blog.csdn.net/lyq8479/article/details/8967824");
	//
	// articleList.add(article1);
	// articleList.add(article2);
	// articleList.add(article3);
	// articleList.add(article4);
	// newsMessage.setArticleCount(articleList.size());
	// newsMessage.setArticles(articleList);
	// respMessage = MessageUtil.newsMessageToXml(newsMessage);
	// }
	// // 多图文消息---最后一条消息不含图片
	// else if ("5".equals(content)) {
	// Article article1 = new Article();
	// article1.setTitle("第7篇\n文本消息中换行符的使用");
	// article1.setDescription("");
	// article1.setPicUrl("http://0.xiaoqrobot.duapp.com/images/avatar_liufeng.jpg");
	// article1.setUrl("http://blog.csdn.net/lyq8479/article/details/9141467");
	//
	// Article article2 = new Article();
	// article2.setTitle("第8篇\n文本消息中使用网页超链接");
	// article2.setDescription("");
	// article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
	// article2.setUrl("http://blog.csdn.net/lyq8479/article/details/9157455");
	//
	// Article article3 = new Article();
	// article3.setTitle("如果觉得文章对你有所帮助，请通过博客留言或关注微信公众帐号xiaoqrobot来支持柳峰！");
	// article3.setDescription("");
	// // 将图片置为空
	// article3.setPicUrl("");
	// article3.setUrl("http://blog.csdn.net/lyq8479");
	//
	// articleList.add(article1);
	// articleList.add(article2);
	// articleList.add(article3);
	// newsMessage.setArticleCount(articleList.size());
	// newsMessage.setArticles(articleList);
	// respMessage = MessageUtil.newsMessageToXml(newsMessage);
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return respMessage;
	// }

	/**
	 * emoji表情转换(hex -> utf-16)
	 * 
	 * @param hexEmoji
	 * @return
	 */
	public static String emoji(int hexEmoji) {
		return String.valueOf(Character.toChars(hexEmoji));
	}

	public static void main(String[] args) {
		System.out.println(isInArr("1", "2"));
		System.out.println(isInArr("2", "2"));
		System.out.println(isInArr("3", "3"));
	}

}