package com.corsair.server.weixin;

import java.io.File;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.util.Base64;
import com.corsair.server.util.CorUtil;
import com.corsair.server.weixin.entity.Shwwxapp;
import com.corsair.server.weixin.entity.Shwwxappqrcode;
import com.corsair.server.weixin.entity.Shwwxapptag;
import com.corsair.server.weixin.entity.Wx_received_msg;
import com.corsair.server.weixin.entity.Wx_user;
import com.corsair.server.weixin.entity.Wx_user_tag;

public class WXUtil {
	/**
	 * @author shangwen
	 *         普通消息
	 *         text 文本消息 image 图片消息 voice 语音消息 voice 视频消息 shortvideo 小视频消息
	 *         location 地理位置消息 link 链接消息
	 *         事件消息
	 *         subscribe 关注 subscribe_SCAN 扫描带参数二维码事件 unsubscribe 取消关注事件
	 *         SCAN LOCATION 上报地理位置事件
	 *         菜单事件
	 *         CLICK 点击菜单拉取消息时的事件推送 VIEW 点击菜单跳转链接时的事件推送
	 *         auth2 鉴权
	 *         多客服
	 *         OlS_notol 如果启用在线客服转发 ， 收到任何普通消息将转发在线客服; 暂时先取消 还不确定
	 *         KF_switch_session 多客服转接 貌似微信取消了 没找到
	 *         设备
	 *         DeviceText 收到设备信息 DeviceEvent_Bind 设备绑定 DeviceEvent_UNBind 设备取消绑定
	 * 
	 */
	public enum MsgType {
		text, image, voice, video, shortvideo, location, link, subscribe, subscribe_SCAN, unsubscribe,
		SCAN, LOCATION, CLICK, VIEW, auth2, OlS_notol, KF_switch_session, DeviceText, DeviceEvent_Bind,
		DeviceEvent_UNBind,sendTemplateMsgFinish
	}

	// OlS_notol 客服不在线

	/**
	 * 调用定义好的监听方法
	 * 
	 * @param appid
	 * @param mstype
	 * @param postparms
	 */
	public static void callEventListener(String appid, MsgType mstype, Map<String, String> postparms) {
		try {
			WXEventListener el = getEventListener(appid);
			if (el != null)
				el.onEvent(appid, mstype, postparms);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 回复默认文本消息
	 * 
	 * @param appid
	 * @param openid
	 * @param text_atrep_msg
	 *            返回值 表示需要调用默认发送方法
	 */
	public static boolean callReplayTextMessage(String appid, String openid, String text_atrep_msg) {
		try {
			WXEventListener el = getEventListener(appid);
			if (el != null)
				return el.onReplayTextMessage(appid, openid, text_atrep_msg);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return true;
	}
	
	/** 回复默认图文消息
	 * @param appid
	 * @param openid
	 * @param newsid
	 * @return
	 */
	public static boolean callReplayNewsMessage(String appid, String openid, String newsid) {
		try {
			WXEventListener el = getEventListener(appid);
			if (el != null)
				return el.onReplayNewsMessage(appid, openid, newsid);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return true;
	}

	public static WXEventListener getEventListener(String appid) throws Exception {
		String lcname = WXAppParms.getAppParm(appid, "WxEventListener", "没有设置WxEventListener参数");// ConstsSw.geAppParmStr("WxEventListener");
		if ((lcname == null) || lcname.isEmpty())
			return null;
		Class<?> clazz = Class.forName(lcname);

		if (!WXEventListener.class.isAssignableFrom(clazz))
			throw new Exception("微信事件监听类【" + lcname + "】必须实现【com.corsair.server.weixin.WXEventListener】接口");
		return (WXEventListener) clazz.newInstance();
	}

	public static long getWXDatetime() {
		return Math.round((double) System.currentTimeMillis() / 1000);
	}

	public static String createWXSha1Sign(HashMap<String, String> map) throws Exception {
		Object[] key_arr = map.keySet().toArray();
		Arrays.sort(key_arr);
		String strA = "";
		for (Object key : key_arr) {
			Object o = map.get(key);
			if (o == null)
				continue;
			String value = o.toString();
			if (value.isEmpty())
				continue;
			strA = strA + key + "=" + value + "&";
		}
		if (strA.isEmpty())
			throw new Exception("没有需要签名的数据");
		else
			strA = strA.substring(0, strA.length() - 1);
		// System.out.println("strA:" + strA);
		String rst = DigestUtils.sha1Hex(strA);
		// System.out.println("sha1Str:" + rst);
		return rst;
	}

	public static boolean checkSign(String Tonken, String signature, String timestamp, String nonce) throws Exception {
		String[] tmpArr = { Tonken, timestamp, nonce };
		Arrays.sort(tmpArr);
		String strtmp = "";
		for (String s : tmpArr) {
			strtmp = strtmp + s;
		}
		String temstr = DigestUtils.sha1Hex(strtmp);
		return signature.equalsIgnoreCase(temstr);
	}

	/**
	 * 解析微信消息成map，参数大小写不变
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> parePostData(String xml) throws Exception {
		Map<String, String> rst = new HashMap<String, String>();
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		for (int i = 0; i < root.elements().size(); i++) {
			Element el = (Element) root.elements().get(i);
			rst.put(el.getName(), el.getTextTrim());
		}
		return rst;
	}

	public static void createMenu(String appid) throws Exception {
		String fsep = System.getProperty("file.separator");
		// String fname = ConstsSw._root_filepath + "WEB-INF" + fsep + "conf" +
		// fsep + "weixinmenu.json";
		String fname = ConstsSw._root_filepath + "WEB-INF" + fsep + "conf" + fsep + "wxmenu" + fsep;
		fname = fname + appid + ".json";
		File f = new File(fname);
		if (f.exists() && f.isFile()) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(f);
			String ms = rootNode.toString();
			String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + getTonken(appid);
			String rst = WXHttps.postHttps(url, null, ms.trim());
			// System.out.println("创建微信菜单:" + rst);
		} else {
			throw new Exception("微信菜单文件【" + fname + "】不存在");
		}
	}

	/**
	 * 创建默认菜单
	 * 
	 * @param appid
	 * @return
	 * @throws Exception
	 */
	public static String createMenuex(String appid) throws Exception {
		Shwwxapp app = WXAppParms.getByAppID(Integer.valueOf(appid));
		if (app.menujson.isEmpty())
			throw new Exception("未设置菜单内容");
		return createMenuex(app.wxappid.getValue(), app.menujson.getValue());
		// {"errcode":0,"errmsg":"ok"}
	}

	public static String createMenuex(String appid, String menujson) throws Exception {
		String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + getTonken(appid);
		return WXHttps.postHttps(url, null, menujson);
	}

	/**
	 * 获取菜单，包括默认菜单和tag菜单
	 * 
	 * @param appid
	 * @return
	 * @throws Exception
	 */
	public static String getWXOnlineMenu(String appid) throws Exception {
		Shwwxapp app = WXAppParms.getByAppID(Integer.valueOf(appid));
		String url = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=" + getTonken(app.wxappid.getValue());
		return WXHttps.getHttps(url, null);
		// {"errcode":0,"errmsg":"ok"}
	}

	/**
	 * 只删除微信端菜单 本地不动，否则删错了会死人滴
	 * 
	 * @param appid
	 * @return
	 * @throws Exception
	 */
	public static String deleteallmenu(String appid) throws Exception {
		Shwwxapp app = WXAppParms.getByAppID(Integer.valueOf(appid));
		String url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + getTonken(app.wxappid.getValue());
		return WXHttps.getHttps(url, null);
		// {"errcode":0,"errmsg":"ok"}
	}

	/**
	 * 创建标签菜单
	 * 
	 * @param tag
	 * @throws Exception
	 */
	public static void createTagMenuex(Shwwxapptag tag) throws Exception {
		if (tag.tagmenujson.isEmpty())
			throw new Exception("未设置菜单内容");
		if (tag.tagid.isEmpty())
			throw new Exception("没有tagid，可能是tag未同步到微信");
		Shwwxapp app = WXAppParms.getByAppID(tag.appid.getAsInt());
		String rst = createTagMenuex(app, tag.tagid.getValue(), tag.tagmenujson.getValue());
		JSONObject jrst = JSONObject.fromObject(rst);
		tag.wxtagmenuid.setValue(jrst.getString("menuid")); // {"menuid":"208379533"}
		tag.save();
	}

	public static String createTagMenuex(Shwwxapp app, String tagid, String tagmenujson) throws Exception {
		String url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=" + getTonken(app.wxappid.getValue());
		JSONObject mj = JSONObject.fromObject(tagmenujson);
		JSONObject mcj = new JSONObject();
		mcj.put("tag_id", tagid);
		mj.put("matchrule", mcj);
		return WXHttps.postHttps(url, null, mj.toString());
	}

	/**
	 * 移除标签菜单
	 * 
	 * @param tag
	 * @throws Exception
	 */
	public static void delTagMenuex(Shwwxapptag tag) throws Exception {
		if (tag.wxtagmenuid.isEmpty())
			throw new Exception("微信菜单ID为空");
		Shwwxapp app = WXAppParms.getByAppID(tag.appid.getAsInt());
		delTagMenuex(app, tag.wxtagmenuid.getValue());
		tag.wxtagmenuid.setValue(null);
		tag.save();
	}

	/**
	 * 移除标签菜单 不处理标签菜单ID
	 * 
	 * @param app
	 * @param menuid
	 * @throws Exception
	 */
	public static void delTagMenuex(Shwwxapp app, String menuid) throws Exception {
		String url = "https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=" + getTonken(app.wxappid.getValue());
		JSONObject mj = new JSONObject();
		mj.put("menuid", menuid);
		String rst = WXHttps.postHttps(url, null, mj.toString());
	}

	/**
	 * 按标签删除所有标签菜单,不处理标签菜单ID
	 * 
	 * @param app
	 * @param tagid
	 * @throws Exception
	 */
	public static void detTagMenmuex(Shwwxapp app, String tagid) throws Exception {
		String mstr = getWXOnlineMenu(app.appid.getValue());
		JSONObject moj = JSONObject.fromObject(mstr);
		if (moj.has("conditionalmenu")) {
			JSONArray ms = moj.getJSONArray("conditionalmenu");
			for (int i = 0; i < ms.size(); i++) {
				JSONObject m = ms.getJSONObject(i);
				if (m.has("matchrule") && (m.getJSONObject("matchrule").has("group_id"))) {
					String group_id = m.getJSONObject("matchrule").getString("group_id");
					if (tagid.equals(group_id)) {
						String menuid = m.getString("menuid");
						delTagMenuex(app, menuid);
					}
				}
			}
		}
	}

	/**
	 * 创建微信标签
	 * 
	 * @param appid
	 * @param tgid
	 * @return
	 * @throws Exception
	 */
	public static String createWXTag(Shwwxapptag tag) throws Exception {
		Shwwxapp app = WXAppParms.getByAppID(tag.appid.getAsInt());
		String url = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=" + WXUtil.getTonken(app.wxappid.getValue());
		// { "tag" : { "name" : "广东"//标签名 } }
		JSONObject jo = new JSONObject();
		JSONObject jotag = new JSONObject();
		jotag.put("name", tag.tagname.getValue());
		jo.put("tag", jotag);
		return WXHttps.postHttps(url, null, jo.toString());
	}

	/**
	 * 更新本地微信标签
	 * 
	 * @param app
	 * @throws Exception
	 */
	public static void updateWXTag(Shwwxapp app) throws Exception {
		String url = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=" + WXUtil.getTonken(app.wxappid.getValue());
		// { "tag" : { "name" : "广东"//标签名 } }
		String rst = WXHttps.getHttps(url, null);
		System.out.println(rst);
		JSONObject jrst = JSONObject.fromObject(rst);
		JSONArray tags = jrst.getJSONArray("tags");
		Shwwxapptag tg = new Shwwxapptag();
		String sqlstr = "update shwwxapptag set  wxusable=2 where appid=" + app.appid.getValue();
		tg.pool.execsql(sqlstr);// 更新前先设置所有tag 微信标志不可用
		for (int i = 0; i < tags.size(); i++) {
			JSONObject tag = tags.getJSONObject(i);
			// { "id":1, "name":"每天一罐可乐星人", "count":0 //此标签下粉丝数 }
			String tagid = tag.getString("id");
			String tagname = tag.getString("name");
			String uscount = tag.getString("count");
			sqlstr = "select * from shwwxapptag where appid=" + app.appid.getValue() + " and tagid=" + tagid;
			tg.clear();
			tg.findBySQL(sqlstr);
			if (!tg.isEmpty()) {// 根据appid 和 tagid 检索，如果有则更新其它属性
				tg.tagname.setValue(tagname);
				tg.uscount.setValue(uscount);
				tg.wxusable.setAsInt(1);
				tg.save();
			} else {
				sqlstr = "select * from shwwxapptag where appid=" + app.appid.getValue() + " and tagname='" + tagname + "'";
				tg.findBySQL(sqlstr);
				if (!tg.isEmpty()) {// 根据appid 和 tagname 检索，如果有则更新其它属性
					tg.tagid.setValue(tagid);
					tg.uscount.setValue(uscount);
					tg.wxusable.setAsInt(1);
					tg.save();
				} else {// 还没找到则新建一个
					tg.clear();
					tg.tagid.setValue(tagid);
					tg.appid.setValue(app.appid.getValue());
					tg.tagname.setValue(tagname);
					tg.uscount.setValue(uscount);
					tg.wxusable.setAsInt(1);
					tg.save();
				}
			}
		}
		// 更新缓存数据
		sqlstr = "select * from shwwxapptag where appid=" + app.appid.getValue();
		app.shwwxapptags.clear();
		app.shwwxapptags.findDataBySQL(sqlstr);
	}

	public static void doSaveReceivedMsg(WXUtil.MsgType msgtype, Map<String, String> postparms) {
		try {
			Wx_received_msg rm = new Wx_received_msg();
			rm.fromusername.setValue(postparms.get("FromUserName").toString());
			rm.createtime.setValue(postparms.get("CreateTime").toString());
			rm.msgtype.setValue(postparms.get("MsgType").toString());
			switch (msgtype) {
			case text:
				rm.msgid.setValue(postparms.get("MsgId").toString());
				rm.content.setValue(postparms.get("Content").toString());
				break;
			case image:
				rm.msgid.setValue(postparms.get("MsgId").toString());
				rm.picurl.setValue(postparms.get("PicUrl").toString());
				rm.mediaid.setValue(postparms.get("MediaId").toString());
				break;
			case voice:
				rm.msgid.setValue(postparms.get("MsgId").toString());
				rm.title.setValue(postparms.get("Format").toString());
				rm.mediaid.setValue(postparms.get("MediaId").toString());
				break;
			case video:
				rm.msgid.setValue(postparms.get("MsgId").toString());
				rm.title.setValue(postparms.get("ThumbMediaId").toString());
				rm.mediaid.setValue(postparms.get("MediaId").toString());
				break;
			case location:
				rm.msgid.setValue(postparms.get("MsgId").toString());
				rm.location_x.setValue(postparms.get("Location_X").toString());
				rm.location_y.setValue(postparms.get("Location_Y").toString());
				rm.scale.setValue(postparms.get("Scale").toString());
				rm.label.setValue(postparms.get("Label").toString());
				break;
			case link:
				rm.msgid.setValue(postparms.get("MsgId").toString());
				rm.title.setValue(postparms.get("Title").toString());
				rm.description.setValue(postparms.get("Description").toString());
				rm.url.setValue(postparms.get("Url").toString());
				break;
			case subscribe:
			case unsubscribe:
				rm.event.setValue(postparms.get("Event").toString());
				break;
			case subscribe_SCAN:
				rm.event.setValue(postparms.get("Event").toString());
				rm.eventkey.setValue(postparms.get("EventKey").toString());
				break;
			case SCAN:
				rm.event.setValue(postparms.get("Event").toString());
				rm.eventkey.setValue(postparms.get("EventKey").toString());
				break;
			case LOCATION:
				rm.event.setValue(postparms.get("Event").toString());
				rm.latitude.setValue(postparms.get("Latitude").toString());
				rm.longitude.setValue(postparms.get("Longitude").toString());
				rm.precision.setValue(postparms.get("Precision").toString());
				break;
			case CLICK:
				rm.event.setValue(postparms.get("Event").toString());
				rm.eventkey.setValue(postparms.get("EventKey").toString());
				break;
			case VIEW:
				rm.event.setValue(postparms.get("Event").toString());
				rm.eventkey.setValue(postparms.get("EventKey").toString());
				break;
			}
			rm.save();
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 获取单个用户信息
	 * 
	 * @param appid
	 * @param openid
	 * @param subscribed
	 * @return
	 * @throws Exception
	 */
	public static Wx_user getSingleWXUser(String appid, String openid) {// , boolean subscribed
		try {
			Shwwxapp app = WXAppParms.getByWXAppID(appid);
			if ((app == null) || app.isEmpty()) {
				throw new Exception("APPID【" + appid + "】参数未发现");
			}
			String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + getTonken(appid)
					+ "&openid=" + openid + "&lang=zh_CN";
			String rsp = WXHttps.getHttps(url, null);
			JSONObject ju = JSONObject.fromObject(rsp);
			return updateLocalWxUserInfo(app, ju);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * 根据返回详细信息 更新本地微信粉丝信息
	 * {
	 * "subscribe": 1,
	 * "openid": "o6_bmjrPTlm6_2sgVt7hMZOPfL2M",
	 * "nickname": "Band",
	 * "sex": 1,
	 * "language": "zh_CN",
	 * "city": "广州",
	 * "province": "广东",
	 * "country": "中国",
	 * "headimgurl":"http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
	 * "subscribe_time": 1382694957,
	 * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
	 * "remark": "",
	 * "groupid": 0,
	 * "tagid_list":[128,2],
	 * "subscribe_scene": "ADD_SCENE_QR_CODE",
	 * "qr_scene": 98765,
	 * "qr_scene_str": ""
	 * }
	 * 或
	 * {
	 * "subscribe": 0,
	 * "openid": "otvxTs_JZ6SEiP0imdhpi50fuSZg"
	 * }
	 * 
	 * @param ju
	 * @throws Exception
	 */
	private static Wx_user updateLocalWxUserInfo(Shwwxapp app, JSONObject ju) throws Exception {
		String openid = ju.getString("openid");
		Wx_user user = new Wx_user();
		String sqlstr = "select * from wx_user where openid='" + openid + "'";
		user.findBySQL(sqlstr);
		if (ju.getInt("subscribe") == 1) {
			user.subscribe.setValue("1");
			user.openid.setValue(openid);
			user.nickname.setValue(ju.getString("nickname"));
			user.b64nickname.setValue(Base64.EncodeStringBase64(ju.getString("nickname")));
			Integer isex = ju.getInt("sex");
			if (isex == 1)
				user.sex.setAsInt(2);
			else if (isex == 2)
				user.sex.setAsInt(1);
			else
				user.sex.setAsInt(0);
			user.city.setValue(ju.getString("city"));
			user.country.setValue(ju.getString("country"));
			user.province.setValue(ju.getString("province"));
			user.language.setValue(ju.getString("language"));
			user.headimgurl.setValue(ju.getString("headimgurl"));
			user.subscribe_time.setValue(ju.getString("subscribe_time"));
			user.remark.setValue(ju.getString("remark"));
			user.subscribe_scene.setValue(ju.getString("subscribe_scene"));
			user.qr_scene.setValue(ju.getString("qr_scene"));
			user.qr_scene_str.setValue(ju.getString("qr_scene_str"));
		} else {
			String nsstr = WXAppParms.getAppParm(app, "NeedSubscribe");
			if (nsstr != null) {
				nsstr = nsstr.trim();
				int ns = Integer.valueOf(nsstr);
				if (ns == 1) {
					String surl = WXAppParms.getAppParm(app, "SubscribeUrl");
					if ((surl == null) || (surl.trim().isEmpty()))
						throw new Exception("需要设置参数【SubscribeUrl】");
					CSContext.getResponse().sendRedirect(surl);
					return null;
				}
			}

			user.subscribe.setValue("0");
			user.openid.setValue(openid);
		}
		user.appid.setValue(app.wxappid.getValue());
		user.update_time.setAsDatetime(new Date());
		user.save();
		updateUserTag(user, ju.getString("tagid_list"));
		return user;
	}

	/**
	 * 更新微信粉丝 的 tags
	 * 
	 * @param user
	 * @param tagid_list
	 *            "tagid_list":[128,2]
	 * @throws Exception
	 */
	private static void updateUserTag(Wx_user user, String tagid_list) throws Exception {
		if ((tagid_list == null) || (tagid_list.isEmpty()))
			return;
		Shwwxapp app = WXAppParms.getByWXAppID(user.appid.getValue());
		if ((app == null) || app.isEmpty()) {
			throw new Exception("APPID【" + user.appid.getValue() + "】参数未发现");
		}
		Wx_user_tag ut = new Wx_user_tag();
		String sqlstr = "delete from wx_user_tag where wxuserid=" + user.wxuserid.getValue();
		ut.pool.execsql(sqlstr);
		if (tagid_list.startsWith("["))
			tagid_list = tagid_list.substring(1, tagid_list.length());
		if (tagid_list.endsWith("]"))
			tagid_list = tagid_list.substring(0, tagid_list.length() - 1);
		String[] ids = tagid_list.split(",");
		for (String id : ids) {
			if ((id == null) || (id.isEmpty()))
				continue;
			Shwwxapptag tag = WXAppParms.getTag(app, id);
			if (tag == null) {// 去同步tag
				throw new Exception("tagid为【" + id + "】的tag不存在，试着先同步tags");
			}
			ut.clear();
			ut.wxuserid.setValue(user.wxuserid.getValue());// wxuserid
			ut.openid.setValue(user.openid.getValue()); // openid
			ut.tgid.setValue(tag.tgid.getValue()); // 平台ID
			ut.appid.setValue(app.appid.getValue());// 平台appid
			ut.tagid.setValue(id); // 微信tagid 一开始允许为空，同步后不能为空
			ut.tagname.setValue(tag.tagname.getValue()); // tag名称
			ut.save();
		}

	}

	// // 通过Oauth2.0授权后获取用户资料
	// public static Wx_user updateWXUserByOAuth2(String access_token, String appid, String openid) {
	// try {
	// String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
	// String rsp = WXHttps.getHttps(url, null);
	// HashMap<String, String> data = CJSON.Json2HashMap(rsp);
	// Wx_user user = new Wx_user();
	// String sqlstr = "select * from wx_user where openid='" + openid + "'";
	// user.findBySQL(sqlstr);
	// if (user.isEmpty())
	// user.subscribe.setValue("0");
	// user.appid.setValue(appid);
	// user.openid.setValue(getMapStr(data, "openid"));
	// user.nickname.setValue(getMapStr(data, "nickname"));
	// user.b64nickname.setValue(Base64.EncodeStringBase64(getMapStr(data, "nickname")));
	// Integer isex = Integer.valueOf(getMapStr(data, "sex").trim());
	// if (isex == 1)
	// user.sex.setAsInt(2);
	// else if (isex == 2)
	// user.sex.setAsInt(1);
	// else
	// user.sex.setAsInt(0);
	// user.city.setValue(getMapStr(data, "city"));
	// user.country.setValue(getMapStr(data, "country"));
	// user.province.setValue(getMapStr(data, "province"));
	// user.language.setValue("zh_CN");
	// user.headimgurl.setValue(getMapStr(data, "headimgurl"));
	// user.unionid.setValue(getMapStr(data, "unionid"));
	// user.update_time.setAsDatetime(new Date());
	// user.privilege.setValue(getMapStr(data, "privilege"));
	// user.save();
	// updateUserTag(user, getMapStr(data, "tagid_list"));
	// return user;
	// } catch (Exception e) {
	// try {
	// Logsw.error(e);
	// } catch (Exception e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// return null;
	// }
	// }

	private static String getMapStr(HashMap<String, String> map, String key) {
		if (map.get(key) == null)
			return null;
		else
			return map.get(key).toString();
	}

	public static Wx_user findUser(String appid, String openid) throws Exception {
		Wx_user user = new Wx_user();
		user.findBySQL("select * from wx_user where openid='" + openid + "'");
		if (user.isEmpty()) {
			user = getSingleWXUser(appid, openid);
		}
		return user;
	}

	/**
	 * @param wxappid
	 * @return
	 * @throws Exception
	 */
	public static String getTonken(String wxappid) throws Exception {
		if ((wxappid == null) || wxappid.isEmpty())
			throw new Exception("获取Tonken【appid】不允许为空");
		String pn = WXAppParms.getAppParm(wxappid, "WxDbpool");
		CDBPool pool = DBPools.poolByName(pn);
		String sqlstr = "select tonken from wx_access_tonken where appid='" + wxappid + "'";
		List<HashMap<String, String>> rows = pool.openSql2List(sqlstr);
		if (rows.size() <= 0) {
			throw new Exception("没有发现Tonken,请确认，Tonken服务是否运行");
		} else {
			return rows.get(0).get("tonken").toString();
		}
	}

	/**
	 * @param wxappid
	 * @return
	 * @throws Exception
	 */
	public static String getTicket(String wxappid) throws Exception {
		String pn = WXAppParms.getAppParm(wxappid, "WxDbpool");
		CDBPool pool = DBPools.poolByName(pn);
		String sqlstr = "select ticket from wx_access_tonken where appid='" + wxappid + "'";
		List<HashMap<String, String>> rows = pool.openSql2List(sqlstr);
		if (rows.size() <= 0) {
			throw new Exception("没有发现ticket,请确认，ticket服务是否运行");
		} else {
			return rows.get(0).get("ticket").toString();
		}
	}

	// 获取在线客服信息
	// kf_account 完整客服账号，格式为：账号前缀@公众号微信号
	// status 客服在线状态 1：pc在线，2：手机在线。若pc和手机同时在线则为 1+2=3
	// kf_id 客服工号
	// auto_accept 客服设置的最大自动接入数
	// accepted_case 客服当前正在接待的会话数
	public static List<HashMap<String, String>> getOnLineServerList(String appid) {
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=" + getTonken(appid);
			String rsp = WXHttps.getHttps(url, null);
			// {"kf_online_list":[]}
			HashMap<String, String> rst = new HashMap<String, String>();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rsp);
			rsp = rootNode.path("kf_online_list").toString();
			List<HashMap<String, String>> oss = CJSON.parArrJson(rsp);
			return oss;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// <ToUserName><![CDATA[touser]]></ToUserName>
	// <FromUserName><![CDATA[fromuser]]></FromUserName>
	// <CreateTime>1399197672</CreateTime>
	// <MsgType><![CDATA[event]]></MsgType>
	// <Event><![CDATA[kf_switch_session]]></Event>
	// <FromKfAccount><![CDATA[test1@test]]></FromKfAccount>
	// <ToKfAccount><![CDATA[test2@test]]></ToKfAccount>
	public static void createKFSession(String appid, Map<String, String> postparms) {
		String rst = null;
		try {
			String ToKfAccount = postparms.get("ToKfAccount").toString();
			String FromUserName = postparms.get("FromUserName").toString();
			String url = " https://api.weixin.qq.com/customservice/kfsession/create?access_token=" + getTonken(appid);
			String data = "{\"kf_account\": \"" + ToKfAccount + "\","
					+ "\"openid\" : \"" + FromUserName + "\","
					+ "\"text\" : \"这是一段附加信息\"}";
			rst = WXHttps.postHttps(url, null, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("rst" + rst);
	}

	// {"total":7,"count":7,"data":{"openid":["oIDso1DuN7EUFMHguzHm7DZ-2hwI","oIDso1Og414fkBFWDE4UHFYYbI7U",
	// "oIDso1Dgb2Xj5DLNpZp_lPNxWjGQ","oIDso1MURQO44ExAL0uKutvo3KRg","oIDso1DAZu2KbO0lgn9EhZnG1Zd8",
	// "oIDso1Mrt5R0R30ramu8k4_-llVQ","oIDso1ADTZTXh3XaDiiYeeL1bPlI"]},
	// "next_openid":"oIDso1ADTZTXh3XaDiiYeeL1bPlI"}

	public static void updateAllUser(Shwwxapp app) throws Exception {
		String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + WXUtil.getTonken(app.wxappid.getValue());
		// +"&next_openid=NEXT_OPENID"
		String rst = WXHttps.getHttps(url, null);
		JSONObject jr = JSONObject.fromObject(rst);
		int loadedcount = 0;
		int total = jr.getInt("total");
		int count = jr.getInt("count");
		loadedcount = loadedcount + count;
		String next_openid = jr.getString("next_openid");
		JSONArray jopenids = jr.getJSONObject("data").getJSONArray("openid");
		parseOpenIDList(app, jopenids);// 解析OPENID 列表 并处理
		while (loadedcount < total) {
			url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + WXUtil.getTonken(app.wxappid.getValue()) + "&next_openid=" + next_openid;
			rst = WXHttps.getHttps(url, null);
			jr = JSONObject.fromObject(rst);
			total = jr.getInt("total");
			count = jr.getInt("count");
			loadedcount = loadedcount + count;
			next_openid = jr.getString("next_openid");
			jopenids = jr.getJSONObject("data").getJSONArray("openid");
			parseOpenIDList(app, jopenids);// 解析OPENID 列表 并处理
		}
	}

	/**
	 * 将获取到的openid列表 按100 个包装成获取详情的json
	 * 
	 * @param jopenids
	 *            ["openid1","openid2"]
	 * @throws Exception
	 */
	private static void parseOpenIDList(Shwwxapp app, JSONArray jopenids) throws Exception {
		int total = jopenids.size();
		final int tmax = 100;// 批量拉取 每次最多100
		int pages = ((total % tmax) == 0) ? total / tmax : (total / tmax) + 1; // 页数-1
		System.out.println("pages:" + pages);
		for (int p = 0; p <= pages; p++) {
			JSONArray ids = new JSONArray();
			for (int i = 0; i < tmax; i++) {
				int idx = p * tmax + i;
				if (idx >= total)
					break;
				JSONObject id = new JSONObject();
				id.put("openid", jopenids.getString(idx));
				id.put("lang", "zh_CN");
				ids.add(id);
			}
			if (ids.size() <= 0)
				return;
			getWXUserInfo(app, ids);
		}
	}

	/**
	 * 通过openids 批量获取 用户详情
	 * 
	 * @param ids
	 *            不超过100个
	 *            [
	 *            {
	 *            "openid": "otvxTs4dckWG7imySrJd6jSi0CWE",
	 *            "lang": "zh_CN"
	 *            },
	 *            {
	 *            "openid": "otvxTs_JZ6SEiP0imdhpi50fuSZg",
	 *            "lang": "zh_CN"
	 *            }
	 *            ]
	 * @throws Exception
	 */
	private static void getWXUserInfo(Shwwxapp app, JSONArray ids) throws Exception {
		if (ids.size() <= 0)
			return;
		String url = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=" + WXUtil.getTonken(app.wxappid.getValue());
		JSONObject data = new JSONObject();
		data.put("user_list", ids);
		String rst = WXHttps.postHttps(url, null, data.toString());
		// System.out.println("rst:" + rst);
		JSONArray jus = JSONObject.fromObject(rst).getJSONArray("user_info_list");
		for (int i = 0; i < jus.size(); i++) {
			JSONObject ju = jus.getJSONObject(i);
			updateLocalWxUserInfo(app, ju); // 更新用户资料
		}
	}

	/**
	 * 为批量用户取消标签
	 * 
	 * @param app
	 * @param jopenids
	 *            ["openid1","openid2"] 长度不能超过50
	 * @param tagid
	 * @throws Exception
	 */
	public static void cancelUserTag(Shwwxapp app, JSONArray jopenids, int tagid) throws Exception {
		if (jopenids.size() <= 0)
			return;
		if (jopenids.size() > 50)
			throw new Exception("用户数量不能大于50");
		String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=" + WXUtil.getTonken(app.wxappid.getValue());
		JSONObject data = new JSONObject();
		data.put("openid_list", jopenids);
		data.put("tagid", tagid);
		String rst = WXHttps.postHttps(url, null, data.toString());
		JSONObject jrst = JSONObject.fromObject(rst);
		if (jrst.getInt("errcode") == 0) {
			String ids = "";
			for (int i = 0; i < jopenids.size(); i++) {
				ids = ids + "'" + jopenids.getString(i) + "',";
			}
			if (!ids.isEmpty())
				ids = ids.substring(0, ids.length() - 1);
			String sqlstr = "DELETE FROM wx_user_tag WHERE tagid=" + tagid + " AND openid IN(" + ids + ") and appid=" + app.appid.getValue();
			app.pool.execsql(sqlstr);
		}
	}

	/**
	 * 为批量用户打标签
	 * 
	 * @param app
	 * @param jopenids
	 *            ["openid1","openid2"] 长度不能超过50
	 * @param tagid
	 * @throws Exception
	 */
	public static void addUserTag(Shwwxapp app, JSONArray jopenids, int tgid) throws Exception {
		System.out.println("addUserTag:" + jopenids.size());
		if (jopenids.size() <= 0)
			return;
		if (jopenids.size() > 50)
			throw new Exception("用户数量不能大于50");

		String sqlstr = "SELECT tgid,appid,tagid,tagname FROM shwwxapptag WHERE appid=" + app.appid.getValue() + " AND tgid=" + tgid;
		Shwwxapptag tag = new Shwwxapptag();
		tag.findBySQL(sqlstr);
		if (tag.isEmpty())
			throw new Exception("【" + app.wxappid.getValue() + "】标签不存在【" + tgid + "】");

		String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=" + WXUtil.getTonken(app.wxappid.getValue());
		JSONObject data = new JSONObject();
		data.put("openid_list", jopenids);
		data.put("tagid", tag.tagid.getValue());
		String rst = WXHttps.postHttps(url, null, data.toString());
		System.out.println("rst:" + rst);
		JSONObject jrst = JSONObject.fromObject(rst);
		if (jrst.getInt("errcode") == 0) {
			Wx_user_tag ut = new Wx_user_tag();
			Wx_user user = new Wx_user();
			for (int i = 0; i < jopenids.size(); i++) {
				String openid = jopenids.getString(i);
				sqlstr = "select * from wx_user where appid='" + app.wxappid.getValue() + "' and openid='" + openid + "'";
				user.findBySQL(sqlstr);
				if (user.isEmpty())
					throw new Exception("微信用户不存在");
				ut.clear();
				sqlstr = "SELECT * FROM wx_user_tag WHERE tagid=" + tag.tagid.getValue() + " AND appid=" + app.appid.getValue() + " AND openid='" + jopenids.getString(i) + "'";
				ut.findBySQL(sqlstr);
				ut.wxuserid.setValue(user.wxuserid.getValue());// wxuserid
				ut.openid.setValue(user.openid.getValue()); // openid
				ut.tgid.setValue(tag.tgid.getValue()); // 平台ID
				ut.appid.setValue(app.appid.getValue());// 平台appid
				ut.tagid.setAsInt(tag.tagid.getAsInt()); // 微信tagid 一开始允许为空，同步后不能为空
				ut.tagname.setValue(tag.tagname.getValue()); // tag名称
				ut.save();
			}
		}
	}

	/**
	 * @param app
	 * @param expire_seconds
	 *            超时秒 0永久
	 * @param scene_id
	 * @param scene_str
	 *            如果为null 则创建 id的
	 * @throws Exception
	 */
	public static Shwwxappqrcode getWXQrcode(Shwwxapp app, int expire_seconds, int scene_id, String scene_str) throws Exception {
		Shwwxappqrcode qc = new Shwwxappqrcode();
		String sqlstr = null;
		if ((scene_str == null) || (scene_str.isEmpty())) {
			sqlstr = "SELECT * FROM shwwxappqrcode WHERE appid=" + app.appid.getValue() + " AND scene_id=" + scene_id + " AND (expiretime=0 OR expiretime<UNIX_TIMESTAMP(NOW()))";
		} else {
			sqlstr = "SELECT * FROM shwwxappqrcode WHERE appid=" + app.appid.getValue() + "  AND scene_str='" + scene_str + "' AND (expiretime=0 OR expiretime<UNIX_TIMESTAMP(NOW()))";
		}
		qc.findBySQL(sqlstr);
		if (!qc.isEmpty())
			return qc;
		JSONObject data = new JSONObject();
		JSONObject action_info = new JSONObject();
		JSONObject scene = new JSONObject();
		String action_name = null;
		if (expire_seconds == 0) {// 永久
			qc.expiretime.setAsInt(0);
			if ((scene_str == null) || (scene_str.isEmpty())) {// 永久整数
				action_name = "QR_LIMIT_SCENE";
				scene.put("scene_id", scene_id);
			} else {// 永久字符串
				action_name = "QR_LIMIT_STR_SCENE";
				scene.put("scene_str", scene_str);
			}
		} else {
			data.put("action_name", "QR_LIMIT_SCENE");
			qc.expiretime.setAsLong(System.currentTimeMillis() / 1000 + expire_seconds);
			if ((scene_str == null) || (scene_str.isEmpty())) {// 临时整数
				action_name = "QR_SCENE";
				scene.put("scene_id", scene_id);
			} else {// 临时字符串
				action_name = "QR_STR_SCENE";
				scene.put("scene_str", scene_str);
			}
		}
		data.put("action_name", action_name);
		action_info.put("scene", scene);
		data.put("action_info", action_info);
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + WXUtil.getTonken(app.wxappid.getValue());
		String rst = WXHttps.postHttps(url, null, data.toString());
		System.out.println("rst:" + rst);
		JSONObject jrst = JSONObject.fromObject(rst);
		qc.ticket.setValue(jrst.getString("ticket"));
		qc.url.setValue(jrst.getString("url"));
		qc.scene_id.setAsInt(scene_id);
		qc.scene_str.setValue(scene_str);
		qc.appid.setValue(app.appid.getValue());
		qc.save();
		return qc;
	}
}
