package com.corsair.server.weixin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.weixin.entity.Shwwxapp;
import com.corsair.server.weixin.entity.Shwwxappparm;
import com.corsair.server.weixin.entity.Shwwxtempmsg;
import com.corsair.server.weixin.entity.Shwwxtempmsgsend;
import com.corsair.server.weixin.entity.Wx_news;
import com.corsair.server.weixin.entity.Wx_newsline;
import com.corsair.server.weixin.entity.Wx_user;

public class WXMsgSend {
	/**
	 * 调用客服接口，发送文本信息
	 * 
	 * @param openid
	 * @param conent
	 * @return
	 * @throws Exception
	 */
	public static String sendTextMsg(String openid, String conent) throws Exception {
		if ((openid == null) || (openid.isEmpty()))
			throw new Exception("微信推送客服消息【openid】参数不能为空");
		if ((conent == null) || (conent.isEmpty()))
			throw new Exception("微信推送客服消息【conent】参数不能为空");

		Wx_user wuser = new Wx_user();
		String sqlstr = "select * from wx_user where openid='" + openid + "'";
		wuser.findBySQL(sqlstr);

		JSONObject msg = new JSONObject();
		msg.put("touser", openid);
		msg.put("msgtype", "text");
		JSONObject text = new JSONObject();
		text.put("content", MergeText(wuser, conent, true));
		msg.put("text", text);
		// String strtp = "{" + "\"touser\":\"%s\"," + " \"msgtype\":\"text\"," + " \"text\":" + " { " + " \"content\":\"%s\" " + " } " + " } ";
		// String senddata = String.format(strtp, openid, conent);
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + WXUtil.getTonken(wuser.appid.getValue());
		String rst = WXHttps.postHttps(url, null, msg.toString());
		// System.out.println(rst);
		return rst;
	}

	/**
	 * @param openid
	 * @param pfid
	 * @return
	 * @throws Exception
	 */
	public static String sendImgMsg(String openid, int pfid) throws Exception {
		Wx_user wuser = new Wx_user();
		String sqlstr = "select * from wx_user where openid='" + openid + "'";
		wuser.findBySQL(sqlstr);
		if (wuser.isEmpty())
			throw new Exception("【openid】不存在");
		String media_id = WXHttps.uploadWxTempFile(wuser.appid.getValue(), pfid, "image");
		return sendImgMsg(openid, media_id);
	}

	/**
	 * 调用客服接口，发送图片信息
	 * 
	 * @param openid
	 * @param media_id
	 * @return
	 * @throws Exception
	 */
	public static String sendImgMsg(String openid, String media_id) throws Exception {
		if ((openid == null) || (openid.isEmpty()))
			throw new Exception("微信推送客服消息【openid】参数不能为空");
		if ((media_id == null) || (media_id.isEmpty()))
			throw new Exception("微信推送客服消息【media_id】参数不能为空");
		Wx_user wuser = new Wx_user();
		String sqlstr = "select * from wx_user where openid='" + openid + "'";
		wuser.findBySQL(sqlstr);
		if (wuser.isEmpty())
			throw new Exception("【openid】不存在");
		JSONObject msg = new JSONObject();
		msg.put("touser", openid);
		msg.put("msgtype", "image");
		JSONObject image = new JSONObject();
		image.put("media_id", media_id);
		msg.put("image", image);
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + WXUtil.getTonken(wuser.appid.getValue());
		String rst = WXHttps.postHttps(url, null, msg.toString());
		return rst;
	}

	/**
	 * 调用客服接口，发送语音信息
	 * 
	 * @param openid
	 * @param pfid
	 * @return
	 * @throws Exception
	 */
	public static String sendVoiceMsg(String openid, int pfid) throws Exception {
		if ((openid == null) || (openid.isEmpty()))
			throw new Exception("微信推送客服消息【openid】参数不能为空");
		Wx_user wuser = new Wx_user();
		String sqlstr = "select * from wx_user where openid='" + openid + "'";
		wuser.findBySQL(sqlstr);
		if (wuser.isEmpty())
			throw new Exception("【openid】不存在");
		String media_id = WXHttps.uploadWxTempFile(wuser.appid.getValue(), pfid, "voice");
		return sendVoiceMsg(openid, media_id);
	}

	/**
	 * 调用客服接口，发送语音信息
	 * 
	 * @param openid
	 * @param media_id
	 * @return
	 * @throws Exception
	 */
	public static String sendVoiceMsg(String openid, String media_id) throws Exception {
		if ((openid == null) || (openid.isEmpty()))
			throw new Exception("微信推送客服消息【openid】参数不能为空");
		if ((media_id == null) || (media_id.isEmpty()))
			throw new Exception("微信推送客服消息【media_id】参数不能为空");
		Wx_user wuser = new Wx_user();
		String sqlstr = "select * from wx_user where openid='" + openid + "'";
		wuser.findBySQL(sqlstr);
		if (wuser.isEmpty())
			throw new Exception("【openid】不存在");
		JSONObject msg = new JSONObject();
		msg.put("touser", openid);
		msg.put("msgtype", "voice");
		JSONObject voice = new JSONObject();
		voice.put("media_id", media_id);
		msg.put("voice", voice);
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + WXUtil.getTonken(wuser.appid.getValue());
		String rst = WXHttps.postHttps(url, null, msg.toString());
		return rst;
	}

	/**
	 * 发送视频
	 * 
	 * @param openid
	 * @param media_id
	 * @param thumb_media_id
	 *            缩略图/小程序卡片图片的媒体ID，小程序卡片图片建议大小为520*416
	 * @param title
	 * @param description
	 *            图文消息/视频消息/音乐消息的描述
	 * @return
	 * @throws Exception
	 */
	public static String sendVideoMsg(String openid, String media_id, String thumb_media_id, String title, String description) throws Exception {
		if ((openid == null) || (openid.isEmpty()))
			throw new Exception("微信推送客服消息【openid】参数不能为空");
		if ((media_id == null) || (media_id.isEmpty()))
			throw new Exception("微信推送客服消息【media_id】参数不能为空");
		Wx_user wuser = new Wx_user();
		String sqlstr = "select * from wx_user where openid='" + openid + "'";
		wuser.findBySQL(sqlstr);
		if (wuser.isEmpty())
			throw new Exception("【openid】不存在");
		JSONObject msg = new JSONObject();
		msg.put("touser", openid);
		msg.put("msgtype", "video");
		JSONObject video = new JSONObject();
		video.put("media_id", media_id);
		video.put("thumb_media_id", thumb_media_id);
		video.put("title", title);
		video.put("description", description);
		msg.put("video", video);
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + WXUtil.getTonken(wuser.appid.getValue());
		String rst = WXHttps.postHttps(url, null, msg.toString());
		return rst;
	}

	/**
	 * 发送音乐
	 * 
	 * @param openid
	 * @param title
	 * @param description
	 *            图文消息/视频消息/音乐消息的描述
	 * @param musicurl
	 *            音乐链接
	 * @param hqmusicurl
	 *            高品质音乐链接，wifi环境优先使用该链接播放音乐
	 * @param thumb_media_id
	 *            缩略图/小程序卡片图片的媒体ID，小程序卡片图片建议大小为520*416
	 * @return
	 * @throws Exception
	 */
	public static String sendMusicMsg(String openid, String title, String description, String musicurl, String hqmusicurl, String thumb_media_id) throws Exception {
		if ((openid == null) || (openid.isEmpty()))
			throw new Exception("微信推送客服消息【openid】参数不能为空");
		Wx_user wuser = new Wx_user();
		String sqlstr = "select * from wx_user where openid='" + openid + "'";
		wuser.findBySQL(sqlstr);
		if (wuser.isEmpty())
			throw new Exception("【openid】不存在");
		JSONObject msg = new JSONObject();
		msg.put("touser", openid);
		msg.put("msgtype", "music");
		JSONObject music = new JSONObject();
		music.put("title", title);
		music.put("description", description);
		music.put("musicurl", musicurl);
		music.put("hqmusicurl", hqmusicurl);
		music.put("thumb_media_id", thumb_media_id);
		msg.put("music", music);
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + WXUtil.getTonken(wuser.appid.getValue());
		String rst = WXHttps.postHttps(url, null, msg.toString());
		return rst;
	}

	/**
	 * 用实体属性值 替换 消息里面的变量{{}}
	 * 
	 * @param jpa
	 * @param conent
	 * @param clear
	 *            如果实体不存在该字段 是否清除
	 * @return
	 * @throws Exception
	 */
	public static String MergeText(CJPA jpa, String conent, boolean clear) throws Exception {
		String temp = conent;
		List<String> pnames = new ArrayList<String>();
		while (true) {
			int bg = temp.indexOf("{{");
			int ed = temp.indexOf("}}");
			if ((bg < 0) || (bg < 0))
				break;
			if ((bg + 2) > ed) {
				break;
			} else {
				String pname = temp.substring(bg + 2, ed);
				pnames.add(pname);
			}
			temp = temp.substring(ed + 2, temp.length());
		}
		System.out.println(pnames.size());
		if (pnames.size() > 0) {

			for (String p : pnames) {
				CField fd = jpa.cfield(p);
				if (fd != null) {
					conent = conent.replace("{{" + p + "}}", fd.getValue());
				} else {
					conent = conent.replace("{{" + p + "}}", "");
				}
			}
		}
		return conent;
	}

	/**
	 * 调用客服接口，发送图文消息
	 * 
	 * @param openid
	 * @param newsid
	 * @return
	 */
	public static boolean sendNewMsgByID(String openid, String newsid) {
		try {
			Wx_news news = new Wx_news();
			news.findByID(newsid);
			if (news.isEmpty())
				throw new Exception("ID为【" + newsid + "】的news不存在");
			return sendNewMsg(openid, news);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}

	/**
	 * 调用客服接口，发送图文消息
	 * 
	 * @param openid
	 * @param newscode
	 * @return
	 */
	public static boolean sendNewMsg(String openid, String newscode) {
		try {
			Wx_news news = new Wx_news();
			String sqlstr = "select * from wx_news where newscode='" + newscode + "'";
			news.findBySQL(sqlstr);
			if (news.isEmpty())
				throw new Exception("编码为【" + newscode + "】的news不存在");
			return sendNewMsg(openid, news);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}

	/**
	 * 调用客服接口，发送图文消息
	 * 
	 * @param appid
	 * @param openid
	 * @param news
	 * @return
	 */
	public static boolean sendNewMsg(String openid, Wx_news news) {
		try {
			Wx_user wuser = new Wx_user();
			String sqlstr = "select * from wx_user where openid='" + openid + "'";
			wuser.findBySQL(sqlstr);
			if (wuser.isEmpty())
				throw new Exception("【openid】不存在");
			String senddata = getWXJsonByNews(openid, news);
			String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + WXUtil.getTonken(wuser.appid.getValue());
			String rst = WXHttps.postHttps(url, null, senddata);
			// System.out.println(rst);
			return true;
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}

	public static String getWXJsonByNews(String openid, Wx_news news) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		jg.writeStringField("touser", openid);
		jg.writeStringField("msgtype", "news");
		jg.writeFieldName("news");
		jg.writeStartObject();
		jg.writeFieldName("articles");
		jg.writeStartArray();
		for (CJPABase jpa : news.wx_newslines) {
			Wx_newsline nl = (Wx_newsline) jpa;
			jg.writeRawValue(nl.tojson());
		}
		jg.writeEndArray();
		jg.writeEndObject();
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/**
	 * 从微信更新模板消息列表
	 * 
	 * @throws Exception
	 */
	public static void upDateTempleMsgList() throws Exception {
		String sqlstr = "SELECT w.* FROM shwwxapp w ,shwwxappparm p" +
				" WHERE w.appid=p.appid AND p.parmname='WxType' AND p.parmvalue=4";
		CJPALineData<Shwwxapp> wxapps = new CJPALineData<Shwwxapp>(Shwwxapp.class);
		wxapps.findDataBySQL(sqlstr, false, false);
		for (CJPABase jpa : wxapps) {
			Shwwxapp wxapp = (Shwwxapp) jpa;
			upDateTempleMsgList(wxapp.wxappid.getValue());
		}
	}

	/**
	 * 从微信更新模板消息列表
	 * 
	 * @param appid
	 * @throws Exception
	 */
	public static void upDateTempleMsgList(String wxappid) throws Exception {
		Shwwxtempmsg tm = new Shwwxtempmsg();
		Shwwxapp wxapp = WXAppParms.getByWXAppID(wxappid);
		if (wxapp.isEmpty())
			throw new Exception("未发现微信【" + wxappid + "】配置信息，如果新加了微信可能需要重启服务");
		CDBConnection con = tm.pool.getCon("upDateTempleMsgList");
		con.startTrans();
		try {
			String sqlstr = "update Shwwxtempmsg set usable=2,disabletime=now() where wxappid='" + wxappid + "'";
			con.execsql(sqlstr);
			String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=" + WXUtil.getTonken(wxappid);
			String rst = WXHttps.getHttps(url, null);
			JSONObject jo = JSONObject.fromObject(rst);
			JSONArray jtms = jo.getJSONArray("template_list");
			for (int i = 0; i < jtms.size(); i++) {
				JSONObject jtm = jtms.getJSONObject(i);
				String template_id = jtm.getString("template_id");
				sqlstr = "select * from Shwwxtempmsg where template_id='" + template_id + "'";
				tm.clear();
				tm.findBySQL(con, sqlstr, false);
				tm.appid.setValue(wxapp.appid.getValue()); // 微信ID
				tm.wxappid.setValue(wxapp.wxappid.getValue()); // 微信appid
				tm.template_id.setValue(template_id); // 微信模板ID
				tm.title.setValue(jtm.getString("title")); // 模板标题
				tm.primary_industry.setValue(jtm.getString("primary_industry")); // 主业
				tm.deputy_industry.setValue(jtm.getString("deputy_industry")); // 副业
				tm.content.setValue(jtm.getString("content")); // 内容
				tm.example.setValue(jtm.getString("example")); // 发送案例
				tm.usable.setValue(1); // 可用
				if (tm.allusersend.isEmpty())
					tm.allusersend.setValue(1);
				tm.disabletime.setValue(null); // 停用时间
				if (tm.creator.isEmpty()) {
					String creater = CSContext.getUserDisplaynameNoErr();
					creater = ((creater == null) || (creater.isEmpty())) ? "system" : creater;
					tm.creator.setValue(creater);
				}
				tm.save(con, false);
			}
			con.submit();
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			con.rollback();
		} finally {
			con.close();
		}
	}

	/**
	 * 发送模板消息
	 * 
	 * @param appid
	 * @param tpmsg
	 */
	public static void sendTempleMsg(String wxappid, WXTemplateMsg tpmsg) {
		try {
			Shwwxapp app = WXAppParms.getByWXAppID(wxappid);
			Shwwxtempmsgsend tm = new Shwwxtempmsgsend();
			tm.appid.setValue(app.appid.getValue()); // 微信ID
			tm.wxappid.setValue(app.wxappid.getValue()); // 微信appid
			tm.template_id.setValue(tpmsg.getTemplate_id()); // 微信模板ID
			tm.touser.setValue(tpmsg.getTouser()); // 接收用户ID
			tm.rdurl.setValue(tpmsg.getUrl()); // 跳转路径
			tm.sddata.setValue(tpmsg.toWXJson()); // 发送的数据
			tm.save();
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + WXUtil.getTonken(wxappid);
			String rst = WXHttps.postHttps(url, null, tpmsg.toWXJson());
			JSONObject jo = JSONObject.fromObject(rst);
			tm.sderrmsg.setValue(jo.getString("errmsg")); // 微信返回的发送状态
			tm.msgid.setValue(jo.getString("msgid")); // 微信返回的消息ID
			tm.sduserid.setValue(CSContext.getUserIDNoErr());
			tm.save();
			// tm.rcstate.setValue() // 微信用户接收状态
			// System.out.println(rst);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
