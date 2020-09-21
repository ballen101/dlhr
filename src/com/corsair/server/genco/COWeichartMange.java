package com.corsair.server.genco;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.weixin.WXAppParms;
import com.corsair.server.weixin.WXMsgSend;
import com.corsair.server.weixin.WXUtil;
import com.corsair.server.weixin.entity.Shwwxapp;
import com.corsair.server.weixin.entity.Shwwxappparm;
import com.corsair.server.weixin.entity.Shwwxapptag;
import com.corsair.server.weixin.entity.Wx_user;

/**
 * 微信管理后台
 * 
 * @author shangwen
 * 
 */
@ACO(coname = "corsair.weichart.mange")
public class COWeichartMange {
	@ACOAction(eventname = "getshwwxappparmdefault", notes = "获取微信配置默认项目", Authentication = true, ispublic = true)
	public String getshwwxappparmdefault() throws Exception {
		return DBPools.defaultPool().opensql2json("select * from shwwxappparmdefault order by pdid");
	}

	@ACOAction(eventname = "uploadwxcert", notes = "上传微信支付证书", Authentication = true, ispublic = true)
	public String uploadwxcert() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String pid = CorUtil.hashMap2Str(parms, "pid", "需要参数【pid】");
		Shwwxappparm wxparm = new Shwwxappparm();
		wxparm.findByID(pid);
		if (wxparm.isEmpty())
			throw new Exception("ID为【" + pid + "】的微信参数不存在");
		Shwwxapp wxapp = new Shwwxapp();
		wxapp.findByID(wxparm.appid.getValue(), false);
		if (wxapp.isEmpty())
			throw new Exception("ID为【" + wxparm.appid.getValue() + "】的微信参数不存在");
		String fsep = System.getProperty("file.separator");
		String filepath = ConstsSw._root_filepath + "WEB-INF" + fsep + "conf" + fsep + "cert" + fsep + wxapp.wxappid.getValue() + fsep;
		String fname = UpLoadFileEx.doupload(filepath);
		wxparm.parmvalue.setValue(fname);
		wxparm.save();
		return wxparm.tojson();
	}

	@ACOAction(eventname = "synwxmenuex", notes = "创建微信菜单", Authentication = true)
	public String createWXMenuex() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String appid = CorUtil.hashMap2Str(parms, "appid", "需要参数【appid】");
		return WXUtil.createMenuex(appid);
	}

	@ACOAction(eventname = "getWXOnlineMenu", notes = "显示微信在线菜单", Authentication = true)
	public String getWXOnlineMenu() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String appid = CorUtil.hashMap2Str(parms, "appid", "需要参数【appid】");
		return WXUtil.getWXOnlineMenu(appid);
	}

	@ACOAction(eventname = "deleteallmenu", notes = "删除微信菜单", Authentication = true)
	public String deleteallmenu() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String appid = CorUtil.hashMap2Str(parms, "appid", "需要参数【appid】");
		return WXUtil.deleteallmenu(appid);
	}

	@ACOAction(eventname = "synwxtagmenu", notes = "创建微信tag菜单", Authentication = true)
	public String synwxtagmenu() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String tgid = CorUtil.hashMap2Str(parms, "tgid", "需要设置tgid参数");
		Shwwxapptag tag = new Shwwxapptag();
		tag.findByID(tgid);
		if (tag.isEmpty())
			throw new Exception("id为【" + tgid + "】标签不存在");
		WXUtil.createTagMenuex(tag);
		return tag.tojson();
	}

	@ACOAction(eventname = "removewxtagmenu", notes = "创建微信tag菜单", Authentication = true)
	public String removewxtagmenu() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String tgid = CorUtil.hashMap2Str(parms, "tgid", "需要设置tgid参数");
		Shwwxapptag tag = new Shwwxapptag();
		tag.findByID(tgid);
		if (tag.isEmpty())
			throw new Exception("id为【" + tgid + "】标签不存在");
		WXUtil.delTagMenuex(tag);
		return tag.tojson();
	}

	@ACOAction(eventname = "createWXTag", Authentication = true, notes = "创建微信标签")
	public String createWXTag() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String tgid = CorUtil.hashMap2Str(parms, "tgid", "需要设置tgid参数");
		Shwwxapptag tag = new Shwwxapptag();
		tag.findByID(tgid);
		if (tag.isEmpty())
			throw new Exception("id为【" + tgid + "】标签不存在");
		String rst = WXUtil.createWXTag(tag);
		JSONObject jrst = JSONObject.fromObject(rst);
		String tagid = jrst.getJSONObject("tag").get("id").toString();
		// {"tag":{"id":100,"name":"测试医院1"}}
		tag.tagid.setValue(tagid);
		tag.save();
		return tag.tojson();
	}

	@ACOAction(eventname = "updateWXTag", Authentication = true, notes = "更新微信标签")
	public String updateWXTag() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		int appid = Integer.valueOf(CorUtil.hashMap2Str(parms, "appid", "需要设置appid参数"));
		Shwwxapp app = WXAppParms.getByAppID(appid);
		WXUtil.updateWXTag(app);
		JSONObject jrst = new JSONObject();
		jrst.put("result", "ok");
		return jrst.toString();
	}

	@ACOAction(eventname = "getWxAndTagsTree", Authentication = true, notes = "获取微信及标签树")
	public String getWxAndTagsTree() throws Exception {
		Shwwxapp app = new Shwwxapp();
		String sqlstr = "SELECT appid wtid, appid,wxappid,acaption wttitle,1 tp FROM shwwxapp";
		JSONArray wxs = app.pool.opensql2json_O(sqlstr);
		for (int i = 0; i < wxs.size(); i++) {
			JSONObject wx = wxs.getJSONObject(i);
			sqlstr = "SELECT tgid,CONCAT(appid,tgid) wtid,appid, tagid,tagname wttitle,2 tp FROM shwwxapptag WHERE appid=" + wx.getString("appid");
			JSONArray tags = app.pool.opensql2json_O(sqlstr);
			wx.put("children", tags);
			if (tags.size() > 0)
				wx.put("state", "open");
		}
		return wxs.toString();
	}

	@ACOAction(eventname = "getWxUser", Authentication = true, notes = "获取微信及标签对应的粉丝")
	public String getWxUser() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		int tp = Integer.valueOf(CorUtil.hashMap2Str(parms, "tp", "需要设置tp参数"));
		String id = CorUtil.hashMap2Str(parms, "id", "需要设置id参数");
		Shwwxapp app = new Shwwxapp();
		JSONArray rst = null;
		if (tp == 1) {
			String sqlstr = "SELECT u.* FROM wx_user u  ,shwwxapp w WHERE u.appid=w.wxappid AND w.appid=" + id;
			rst = app.pool.opensql2json_O(sqlstr);
		} else if (tp == 2) {
			String sqlstr = "SELECT u.* FROM wx_user u , wx_user_tag ut WHERE u.wxuserid=ut.wxuserid AND ut.tgid=" + id;
			rst = app.pool.opensql2json_O(sqlstr);
		} else
			throw new Exception("参数错误");
		return rst.toString();
	}

	@ACOAction(eventname = "updateAllUser", Authentication = true, notes = "更新公众号下所有用户资料")
	public String updateAllUser() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		int appid = Integer.valueOf(CorUtil.hashMap2Str(parms, "appid", "需要设置appid参数"));
		Shwwxapp app = WXAppParms.getByAppID(appid);
		WXUtil.updateAllUser(app);
		JSONObject jrst = new JSONObject();
		jrst.put("result", "ok");
		return jrst.toString();
	}

	@ACOAction(eventname = "moveUsers2Tag", Authentication = true, notes = "将用户移动到tag下面")
	public String moveUser2Tag() throws Exception {
		JSONObject jo = JSONObject.fromObject(CSContext.getPostdata());
		if (!jo.has("tgid"))
			throw new Exception("需要参数【tgid】");
		if (!jo.has("wxuserids"))
			throw new Exception("需要参数【wxuserids】");
		int tgid = Integer.valueOf(jo.getString("tgid"));
		Shwwxapp app = WXAppParms.getByTgid("" + tgid);
		JSONArray uids = jo.getJSONArray("wxuserids");
		Wx_user u = new Wx_user();
		for (int i = 0; i < uids.size(); i++) {
			String uid = uids.getString(i);// wxuserid
			u.findByID(uid);
			if (u.isEmpty())
				throw new Exception("没有找到微信用户");
			String sqlstr = "SELECT t.tagid,u.openid FROM wx_user_tag u,shwwxapptag t WHERE u.tgid=t.tgid AND u.wxuserid=" + uid;
			List<HashMap<String, String>> rs = app.pool.openSql2List(sqlstr);
			JSONArray jopenids = new JSONArray();
			jopenids.add(u.openid.getValue());
			for (HashMap<String, String> r : rs) {
				WXUtil.cancelUserTag(app, jopenids, Integer.valueOf(r.get("tagid")));
			}
			WXUtil.addUserTag(app, jopenids, tgid);
		}
		JSONObject jrst = new JSONObject();
		jrst.put("result", "ok");
		return jrst.toString();
	}

	@ACOAction(eventname = "synWXTempleMessage", Authentication = true, notes = "更新本地模板消息列表")
	public String synWXTempleMessage() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String appid = CorUtil.hashMap2Str(parms, "appid", "需要设置appid参数");
		Shwwxapp app = WXAppParms.getByAppID(Integer.valueOf(appid));
		WXMsgSend.upDateTempleMsgList(app.wxappid.getValue());
		return CSContext.getJSON_OK();
	}
}
