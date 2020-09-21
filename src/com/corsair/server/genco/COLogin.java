package com.corsair.server.genco;

import java.util.HashMap;

import net.sf.json.JSONObject;

import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.Login;
import com.corsair.server.base.Loginex;
import com.corsair.server.csession.CToken;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DesSw;

/**
 * 登录CO
 * 
 * @author Administrator
 *
 */
@ACO(coname = "web.login")
public class COLogin {
	@ACOAction(eventname = "dologin", Authentication = false, notes = "web客户端登录/n"
			+ "调用方法:/web/login/dologin.co?username=usname&userpass=pass&&version=1.1 /n"
			+ "参数说明:  username 用户名，userpass 用户密码,key deskey,version版本号 /n"
			+ "返回   值:成功返回User JSON对象，失败返回失败原因字符串", ispublic = true)
	public String login() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String username = parms.get("username");
		String userpass = DesSw.EncryStrHex(parms.get("userpass"), ConstsSw._userkey);
		float version = (parms.get("version") == null) ? 0 : Float.valueOf(parms.get("version"));
		String token = new Login().dologinweb(username, userpass, (double) version, null);
		if (CSContext.logined()) {
			Shwuser user = new Shwuser();
			user.findByID(CSContext.getUserID());
			JSONObject jo = user.toJsonObj();
			jo.put("token", token);
			jo.remove("userpass");
			jo.remove("md5sn");
			return jo.toString();
		} else
			throw new Exception("登录失败");
	}

	/**
	 * 新的登录方式
	 * post
	 * username:"用户名",
	 * noncestr:"FDSAFDSA",// 64位以内随机字符串
	 * timestr:"yyyyMMddHHmmss",// 时间戳
	 * md5sign:"md5密文"
	 * 
	 * @return
	 * @throws Exception
	 */
	@ACOAction(eventname = "lg", Authentication = false, ispublic = true)
	public String loginex() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String username = CorUtil.hashMap2Str(parms, "username", "需要参数username");
		String noncestr = CorUtil.hashMap2Str(parms, "noncestr", "需要参数noncestr");
		String timestr = CorUtil.hashMap2Str(parms, "timestr", "需要参数timestr");
		String md5sign = CorUtil.hashMap2Str(parms, "md5sign", "需要参数md5sign");
		String token = Loginex.dologinweb(username, noncestr, timestr, md5sign);
		if (CSContext.logined()) {
			Shwuser user = new Shwuser();
			user.findByID(CSContext.getUserID());
			JSONObject jo = user.toJsonObj();
			jo.put("token", token);
			jo.remove("userpass");
			jo.remove("md5sn");
			return jo.toString();
		} else
			throw new Exception("登录失败");
	}

	@ACOAction(eventname = "loginByToken", Authentication = false, ispublic = true)
	public String loginByToken() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		CorUtil.hashMap2Str(parms, "token", "需要参数token");
		CToken.autoLoginBytoken(parms);
		if (CSContext.logined())
			return "{\"rst\":\"success\"}";
		else
			return "{\"rst\":\"faild\"}";
	}

	@ACOAction(eventname = "loginout", Authentication = false, ispublic = true)
	public String loginout() throws Exception {
		Login.dologinout();
		return "{\"result\":\"success\"}";
	}

	@ACOAction(eventname = "getdefaultorg", Authentication = true, ispublic = true)
	public String getdefaultorg() throws Exception {
		String sqlstr = "select b.* from shworguser a ,shworg b where a.orgid=b.orgid and a.isdefault=1 and a.userid=" + CSContext.getUserID();
		Shworg dfo = new Shworg();
		dfo.findBySQL(sqlstr, false);
		return dfo.tojson();
	}

	// 重置密码
	@ACOAction(eventname = "reSetPWD", Authentication = true, ispublic = true)
	public String reSetPassWord() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String userid = CorUtil.hashMap2Str(parms, "userid", "需要参数userid");
		String sqlstr = "select * from shwuser where userid=" + CSContext.getUserID();
		Shwuser user = new Shwuser();
		user.findBySQL(sqlstr);
		if (user.isEmpty())
			throw new Exception("用户未登录");
		if (user.usertype.getAsIntDefault(0) != 1)
			throw new Exception("非管理员不允许重置密码!");
		String newpwd = doresepwd(userid);
		return "{\"result\":\"" + newpwd + "\"}";
	}

	private String doresepwd(String userid) throws Exception {
		Shwuser user = new Shwuser();
		user.findByID(userid);
		if (user.isEmpty())
			throw new Exception("没发现ID为【" + userid + "】的用户！");
		String nwpd = CorUtil.randomString(8);
		user.userpass.setValue(DesSw.EncryStrHex(nwpd, ConstsSw._userkey));
		user.save();
		return nwpd;
	}
	
	// 无需登陆修改密码
	@ACOAction(eventname = "updatePWD2", Authentication = false, ispublic = true)
	public String updatePWD2() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String oldpwd = CorUtil.hashMap2Str(parms, "oldpwd", "需要参数oldpwd");
		String newpwd = CorUtil.hashMap2Str(parms, "newpwd", "需要参数newpwd");
		String username= CorUtil.hashMap2Str(parms, "username", "需要参数username");
		String sqlstr = "select * from shwuser where username='"+username+"'";
		Shwuser user = new Shwuser();
		user.findBySQL(sqlstr);

		if (user.isEmpty())
			throw new Exception("没有该用户");
		else {
			if (!user.userpass.isEmpty()) {
				String old1 = DesSw.EncryStrHex(oldpwd, ConstsSw._userkey);
				if (!old1.equals(user.userpass.getValue())) {
					throw new Exception("旧密码不匹配");
				}
			}
			user.userpass.setValue(DesSw.EncryStrHex(newpwd, ConstsSw._userkey));
			user.save();
			return "{\"result\":\"ok\"}";
		}

	}
	
	
	
	// 修改密码
	@ACOAction(eventname = "updatePWD", Authentication = true, ispublic = true)
	public String updatePWD() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String oldpwd = CorUtil.hashMap2Str(parms, "oldpwd", "需要参数oldpwd");
		String newpwd = CorUtil.hashMap2Str(parms, "newpwd", "需要参数newpwd");

		String sqlstr = "select * from shwuser where userid=" + CSContext.getUserID();
		Shwuser user = new Shwuser();
		user.findBySQL(sqlstr);

		if (user.isEmpty())
			throw new Exception("用户未登录");
		else {
			if (!user.userpass.isEmpty()) {
				String old1 = DesSw.EncryStrHex(oldpwd, ConstsSw._userkey);
				if (!old1.equals(user.userpass.getValue())) {
					throw new Exception("旧密码不匹配");
				}
			}
			user.userpass.setValue(DesSw.EncryStrHex(newpwd, ConstsSw._userkey));
			user.save();
			return "{\"result\":\"ok\"}";
		}

	}
}
