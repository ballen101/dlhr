package com.corsair.server.weixin;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DesSwEx;
import com.corsair.server.util.HttpTookit;
import com.corsair.server.weixin.entity.Shwwxapp;

public class WCCSOAPI {
	private static HttpTookit httptookit = new HttpTookit();

	/**
	 * 接入客服
	 * 
	 * @param wxapp
	 * @param postparms
	 * @return
	 */
	public static boolean connectToCSO(Shwwxapp wxapp, Map<String, String> postparms) {
		try {
			String csourl = WXAppParms.getAppParm(wxapp, "WxCSOURL");
			String username = WXAppParms.getAppParm(wxapp, "WxCSOURLUser");
			String key = WXAppParms.getAppParm(wxapp, "WxCSOURLKey");
			if ((csourl == null) || (csourl.isEmpty()) || (username == null) || (username.isEmpty()))
				return false;
			JSONObject message = map2json(postparms);
			JSONObject senddata = new JSONObject();
			senddata.put("action", 2);
			senddata.put("signdata", buildsigndata(username, key));
			senddata.put("wxappid", wxapp.wxappid.getValue());
			senddata.put("openid", message.getString("FromUserName"));
			senddata.put("message", message);
			JSONObject rst = JSONObject.fromObject(httptookit.doPostJSON(csourl, senddata, "UTF-8"));
			int errcode = rst.getInt("errcode");
			if (errcode == 0)
				return true;
			String emsg = "接入在线客服错误【" + errcode + "】";
			if (rst.has("errmsg"))
				emsg = emsg + rst.getString("errmsg");
			Logsw.error(emsg);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 转发客服信息CSO接口
	 * 
	 * @param wxapp
	 * @param postparms
	 * @return
	 */
	public static boolean resendToCSO(Shwwxapp wxapp, Map<String, String> postparms) {
		try {
			String csourl = WXAppParms.getAppParm(wxapp, "WxCSOURL");
			String username = WXAppParms.getAppParm(wxapp, "WxCSOURLUser");
			String key = WXAppParms.getAppParm(wxapp, "WxCSOURLKey");
			if ((csourl == null) || (csourl.isEmpty()) || (username == null) || (username.isEmpty()))
				return false;
			JSONObject message = map2json(postparms);
			JSONObject senddata = new JSONObject();
			senddata.put("action", 3);
			senddata.put("signdata", buildsigndata(username, key));
			senddata.put("wxappid", wxapp.wxappid.getValue());
			senddata.put("openid", message.getString("FromUserName"));
			senddata.put("message", message);
			JSONObject rst = JSONObject.fromObject(httptookit.doPostJSON(csourl, senddata, "UTF-8"));
			int errcode = rst.getInt("errcode");
			if (errcode == 0)
				return true;
			String emsg = "转发信息错误【" + errcode + "】";
			if (rst.has("errmsg"))
				emsg = emsg + rst.getString("errmsg");
			Logsw.error(emsg);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static JSONObject map2json(Map<String, String> postparms) {
		JSONObject rst = new JSONObject();
		Iterator<Map.Entry<String, String>> it = postparms.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			rst.put(entry.getKey(), entry.getValue());
		}
		return rst;
	}

	/**
	 * 创建认证包
	 * 
	 * @param username
	 * @param key
	 * @return
	 */
	private static JSONObject buildsigndata(String username, String key) {
		String noncestr = CorUtil.getShotuuid();
		String timestr = Systemdate.getStrDateByFmt(new Date(), "yyyyMMddHHmmss");
		String md5str = "username=" + username + "&noncestr=" + noncestr + "&timestr=" + timestr + "&password=" + key;
		md5str = DesSwEx.MD5(md5str).toUpperCase();
		JSONObject rst = new JSONObject();
		rst.put("username", username);
		rst.put("noncestr", noncestr);
		rst.put("timestr", timestr);
		rst.put("md5sign", md5str);
		return rst;
	}

}
