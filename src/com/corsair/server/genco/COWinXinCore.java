package com.corsair.server.genco;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;

import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.CSContext.ActionType;
import com.corsair.server.csession.CSession;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.Base64;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.weixin.CoreService;
import com.corsair.server.weixin.WXAppParms;
import com.corsair.server.weixin.WXHttps;
import com.corsair.server.weixin.WXUtil;
import com.corsair.server.weixin.WXUtil.MsgType;
import com.corsair.server.weixin.entity.Shwwxapp;
import com.corsair.server.weixin.entity.Shwwxapptag;
import com.corsair.server.weixin.entity.Wx_newsline;
import com.corsair.server.weixin.entity.Wx_user;

@ACO(coname = "weixin")
public class COWinXinCore {
	// private String token = "GlKCxf39";

	// 页面注册参数JSON对象
	@ACOAction(eventname = "getJSRegitParms", Authentication = false, notes = "页面注册参数JSON对象")
	public String getJSRegitParms() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String appid = CorUtil.hashMap2Str(parms, "appid", "需要设置appid参数");
		String purl = CorUtil.hashMap2Str(parms, "purl", "需要参数purl");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("noncestr", CorUtil.randomString(32));
		map.put("jsapi_ticket", WXUtil.getTicket(appid));
		map.put("timestamp", String.valueOf(WXUtil.getWXDatetime()));
		map.put("url", purl);
		String signature = WXUtil.createWXSha1Sign(map);
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put("debug", "true");
		configMap.put("appId", appid);
		configMap.put("timestamp", map.get("timestamp"));
		configMap.put("nonceStr", map.get("noncestr"));
		configMap.put("signature", signature);
		String rstjson = CJSON.HashMap2Json(configMap, false);
		// System.out.println("regjson:" + rstjson);
		return rstjson;
	}

	/**
	 * 给微信回调的方法
	 * 
	 * @return
	 * @throws Exception
	 */
	@ACOAction(eventname = "sercore", notes = "微信接口核心方法", Authentication = false)
	public String winxincore() throws Exception {
		String appid = CorUtil.hashMap2Str(CSContext.getParms(), "appid", "没有发现期待的appid参数");
		//System.out.println("sercore:" + appid);
		return CoreService.winxincore(appid);
	}

	@ACOAction(eventname = "auth2redirect", notes = "微信auth2认证", Authentication = false)
	public String oAuth2Redirect() throws Exception {
		String state = CSContext.getParms().get("state").toString();
		if (state.indexOf("rdt_") == -1)
			state = new String(Base64.decodeBase64(state.getBytes()), "UTF-8");
		Object obj = CSContext.getParms().get("code");
		if ((obj == null) || obj.toString().isEmpty()) {
			// 用户禁止授权
			Logsw.error("用户禁止授权");
			return null;
		}
		String appid = CorUtil.ParseUrlParm(state, "appid", "参数state跳转页面需要 appid参数");
		String code = obj.toString();
		String openid = getOpenID(appid, code);
		CSession.putvalue(CSContext.getSession().getId(), "openid", openid);
		HashMap<String, String> parms = new HashMap<String, String>();
		parms.put("openid", openid);
		parms.put("state", state);
		WXUtil.callEventListener(appid, MsgType.auth2, parms);
		return null;
	}

	private String getOpenID(String appid, String code) throws Exception {
		String appsecret = WXAppParms.getAppParm(appid, "Wxappsecret", "没有发现Wxappsecret参数");
		// 获取web授权access_token
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + appsecret + "&code=" + code
				+ "&grant_type=authorization_code";
		//System.out.println("获取web授权access_token111111111111111111111111:" + url);
		String rst = WXHttps.getHttps(url, null);
		// {"access_token":"OezXcEiiBSKSxW0eoylIeJ1EWXh_-s-ARlDQlBtk5tOZb1CWyuarU9oExCzZMcSr-nl3W64gaNUU0YymdWh02fXW_tNu-j_pbUJ8DSm39G-HhIkAVysbxwBQIgBbw-1e-14qKMBS-nawlIYz_Ot5iQ","expires_in":7200,"refresh_token":"OezXcEiiBSKSxW0eoylIeJ1EWXh_-s-ARlDQlBtk5tOZb1CWyuarU9oExCzZMcSrlTpkiuU60MbLCX051DYvYkGnLJWMvI_pKHXW3pP1UWpNNGJAFMLyzEBaiT1OGqpY_UqVbATnVaSjQ5aNprQ7lw","openid":"ojcfEt9Lq2xEGjyROrRPWfH6IDgc","scope":"snsapi_userinfo"}
		//System.out.println("获取web授权 rst:" + rst);
		HashMap<String, String> map = CJSON.Json2HashMap(rst);
		String access_token = map.get("access_token").toString();
		String openid = map.get("openid").toString();
		// Wx_user user = WXUtil.updateWXUserByOAuth2(access_token, appid, openid);
		Wx_user user = WXUtil.getSingleWXUser(appid, openid);
		if (user == null) {
			throw new Exception("获取用户授权信息错误");
		}
		return user.openid.getValue();
	}

	@ACOAction(eventname = "upwxnewspic", Authentication = true, notes = "上传微信图文图片")
	public String uploadadpic() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		String newslid = CorUtil.hashMap2Str(CSContext.getParms(), "newslid", "需要参数newslid");
		String serurl = CorUtil.hashMap2Str(CSContext.getParms(), "serurl", "需要参数serurl");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false);
		if (pfs.size() > 0) {
			Shw_physic_file p = (Shw_physic_file) pfs.get(0);
			Wx_newsline wl = new Wx_newsline();
			wl.findByID(newslid, false);
			if (wl.isEmpty()) {
				throw new Exception("ID为【" + newslid + "】的微信行不存在");
			}
			String picurl = serurl + "/web/common/downattfile.co?pfid=" + p.pfid.getValue();
			wl.picurl.setValue(picurl);
			wl.save(false);
			if (pfs.size() > 1) {// 太多文件只留第一个
				for (int i = 1; i < pfs.size(); i++) {
					Shw_physic_file pf = (Shw_physic_file) pfs.get(i);
					UpLoadFileEx.delAttFile(pf.pfid.getValue());
				}
			}
			return p.tojson();
		} else
			throw new Exception("没有文件");
	}

}
