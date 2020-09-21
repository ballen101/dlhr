package com.corsair.server.csession;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.Login;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.generic.Shwusertoken;
import com.corsair.server.util.DesSw;

/**Token管理
 * @author Administrator
 *
 */
public class CToken {
	class TUpdateTokenTimeOut implements Runnable {
		HashMap<String, String> parms;

		public TUpdateTokenTimeOut(HashMap<String, String> parms) {
			this.parms = parms;
		}

		public void run() {
			try {
				Object o = parms.get("token");
				String token = (o == null) ? null : o.toString();
				if (token == null)
					return;
				String tts = ConstsSw.getAppParm("token_timeout").toString();
				int tt = (tts == null) ? 30 : Integer.valueOf(tts);
				if (ConstsSw._allow_redis) {
					RedisUtil.setfdexpire(token, tt * 60);
				} else {
					Shwusertoken stoken = new Shwusertoken();
					stoken.findByID(token);
					if (stoken.isEmpty()) {
						return;
					} else {

						stoken.timeout.setAsLong(System.currentTimeMillis() + tt * 60000);
						stoken.save();
					}
				}
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

	// 更新Token time out
	public void updateTokenTimeOut(HashMap<String, String> parms) throws Exception {
		new Thread(new TUpdateTokenTimeOut(parms)).start();
	}

	// 根据Tonek重新登录
	public static void autoLoginBytoken(HashMap<String, String> parms) throws Exception {
		Object o = parms.get("token");
		String token = (o == null) ? null : o.toString();
		if (token == null)
			return;
		Shwusertoken stoken = new Shwusertoken();
		if (ConstsSw._allow_redis) {
			// 根据token重新登录
			stoken.token.setValue(token);
			stoken.userid.setValue(RedisUtil.gettokefdvalue(token, "userid"));
			stoken.username.setValue(RedisUtil.gettokefdvalue(token, "username"));
			stoken.sessionid.setValue(RedisUtil.gettokefdvalue(token, "sessionid"));
			stoken.starttime.setValue(RedisUtil.gettokefdvalue(token, "starttime"));
			stoken.timeout.setValue(RedisUtil.gettokefdvalue(token, "timeout"));
			if (stoken.userid.getValue() == null)
				return;
			(new Login()).dologinweb(stoken.username.getValue(), "", 0.0, stoken);
		} else {
			stoken.findByID(token);
			if (stoken.isEmpty()) {
				Logsw.debug("重新登录Token【" + token + "】为空，登录失败");
				return;
			} else {
				if (stoken.timeout.getAsLongDefault(0) < System.currentTimeMillis()) {
					System.out.println("timeout:" + stoken.timeout.getAsLongDefault(0) + "-currentTime:" + System.currentTimeMillis());
					Logsw.debug("重新登录Token【" + token + "】超时，登录失败");
					return;
				}

				(new Login()).dologinweb(stoken.username.getValue(), "", 0.0, stoken);
			}
		}
	}

	// username userpass 二次加密 确保无逗号;
	/**
	 * 根据用户资料创建登录token
	 * 
	 * @param user
	 * @param validmit
	 *            有效时长(分钟) 5：表示 从创建时刻开始之后5分钟内有效
	 * @return 返回Token字符串
	 */
	public static String createUToken(Shwuser user, int validmit) {
		try {
			String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
			String v = String.valueOf(new Date().getTime() + (validmit * 60000));
			String userpass = DesSw.EncryStrHex(user.userpass.getValue(), ConstsSw._userkey);
			String username = DesSw.EncryStrHex(user.username.getValue(), ConstsSw._userkey);
			String utstr = uuid + "," + username + "," + userpass + "," + v;//
			return DesSw.EncryStrHex(utstr, ConstsSw._userkey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param utstr
	 *            根据token登录
	 * @throws Exception
	 */
	public static void autoLoginByUtoken(String utstr) throws Exception {
		String utstr1 = DesSw.DESryStrHex(utstr, ConstsSw._userkey);
		// System.out.println("utstr1:" + utstr1);
		String[] strs = utstr1.split(",");
		String uuid = strs[0];
		String username = DesSw.DESryStrHex(strs[1], ConstsSw._userkey);
		String userpass = DesSw.DESryStrHex(strs[2], ConstsSw._userkey);
		long v = Long.valueOf(strs[3]);
		// if (v < new Date().getTime())
		// throw new Exception("UserToken已经超时，需要重新登录");
		new Login().dologinweb(username, userpass, 0.0, null);
	}

	// 有效期 validmit 分钟 为0时候使用系统参数token_timeout；没设置参数 默认30分钟
	public static Shwusertoken createToken(Shwuser user, Shwusertoken atoken, String sessionid, int validmit) throws Exception {
		Object spttout = ConstsSw.getAppParm("token_timeout");
		int timeout = (validmit == 0) ? ((spttout == null) ? 30 : Integer.valueOf(spttout.toString())) : validmit;
		return createToken(atoken, user.userid.getValue(), user.username.getValue(), sessionid, timeout);
	}

	public static Shwusertoken createToken(Shwusertoken atoken, String userid, String username, String sessionid, int timeout) throws Exception {
		Shwusertoken token = (atoken == null) ? new Shwusertoken() : atoken;
		// 是否删除该登录用户其它TOKEN？
		token.userid.setValue(userid);
		token.username.setValue(username);
		token.sessionid.setValue(sessionid);
		if (token.starttime.isEmpty())
			token.starttime.setAsLong(System.currentTimeMillis());

		token.timeout.setAsLong(System.currentTimeMillis() + timeout * 60000);
		if (token.token.isEmpty()) {
			String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
			token.token.setValue(uuid);
		}
		if (ConstsSw._allow_redis) {
			RedisUtil.settokenfdvalue(token.token.getValue(), "userid", token.userid.getValue());
			RedisUtil.settokenfdvalue(token.token.getValue(), "username", token.username.getValue());
			RedisUtil.settokenfdvalue(token.token.getValue(), "sessionid", token.sessionid.getValue());
			RedisUtil.settokenfdvalue(token.token.getValue(), "starttime", token.starttime.getValue());
			RedisUtil.settokenfdvalue(token.token.getValue(), "timeout", token.timeout.getValue());
			RedisUtil.setfdexpire(token.token.getValue(), timeout * 60);
		} else {
			token.save();
		}
		return token;
	}

}
