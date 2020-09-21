package com.corsair.server.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.server.csession.CSession;
import com.corsair.server.csession.CToken;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shworguser;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.generic.Shwuserexeclog;
import com.corsair.server.generic.Shwusertoken;
import com.corsair.server.util.DesSw;

/**
 * 登录的类
 * 
 * @author Administrator
 *
 */
public class Login {

	// public void dologinex(String UserName, String UserPWD, Double Vession) throws Exception {
	// HttpSession session = CSContext.getSession();// request.getSession();
	// Shwuser user = new Shwuser();
	// dologininsession(session, UserName, UserPWD, Vession, CSContext.getRequest().getRemoteHost(), user, null);
	// }

	/**
	 * 网页登录
	 * 
	 * @param UserName
	 * @param UserPWD
	 * @param Vession
	 * @param atoken
	 * @return 成功登录返回token 字符串 登录失败 直接报错
	 * @throws Exception
	 */
	public String dologinweb(String UserName, String UserPWD, Double Vession, Shwusertoken atoken) throws Exception {
		// System.out.println("dologinweb!!!!!!!!!!!!!!!!!!!!");
		HttpSession session = CSContext.getSession();
		Shwuser user = new Shwuser();
		dologininsession(session, UserName, UserPWD, Vession, CSContext.getRequest().getRemoteHost(), user, atoken);
		String sessionid = (CSContext.getSession() == null) ? "" : CSContext.getSession().getId();
		Shwusertoken tk = CToken.createToken(user, atoken, sessionid, 0);
		return tk.token.getValue();
	}

	// ///web login end///////////

	private void dologininsession(HttpSession session, String UserName, String UserPWD, Double Vession, String remotehost,
			Shwuser user, Shwusertoken token)
			throws Exception {
		Logsw.debug("用户登录:" + UserName);
		try {
			String sqlstr = "SELECT * FROM shwuser WHERE username=?";
			PraperedSql psql = new PraperedSql();
			psql.setSqlstr(sqlstr);
			psql.getParms().add(new PraperedValue(java.sql.Types.VARCHAR, UserName.trim()));// UserName.trim().toUpperCase()
			user.findByPSQL(psql);
			if (user.isEmpty())
				throw new Exception("用户名不存在!");
			if (user.actived.getAsIntDefault(0) != 1)
				throw new Exception("用户已禁用!");

			if ((user.userpass.isEmpty() && UserPWD.isEmpty()) || (token != null) || user.userpass.getValue().equals(UserPWD)) {
				// 登录成功
				CSession.putvalue(session.getId(), "sessionid", session.getId());
				CSession.putvalue(session.getId(), "remotehost", remotehost);
				CSession.putvalue(session.getId(), "userid", user.userid.getValue());
				CSession.putvalue(session.getId(), "displayname", user.displayname.getValue());
				CSession.putvalue(session.getId(), "username", user.username.getValue());
				CSession.putvalue(session.getId(), "usertype", user.usertype.getAsIntDefault(0));
				CSession.putvalue(session.getId(), ConstsSw.session_userpwd, UserPWD);
				CSession.putvalue(session.getId(), ConstsSw.session_logined, true);
				CSession.putvalue(session.getId(), ConstsSw.session_logintime, System.currentTimeMillis());
				setLoginedParms(session, user);
			} else {
				throw new Exception("密码错误");
			}
		} catch (Exception e) {
			String[] fields = { "sessionid", "remotehost", "userid", "displayname", "username", "usertype", ConstsSw.session_username,
					ConstsSw.session_logined,
					ConstsSw.session_logintime };
			CSession.removeValue(session.getId(), fields);
			throw e;
		}
	}

	// /////用户参数///////////////////////
	// //根据参数名验证设置的值是否合法
	protected boolean onSetUserParmValue(String parmname, String value) {
		// System.out.println("onSetUserParmValue:" + parmname + " " + value);
		return true;
	}

	// public String setUserParmValue(String xmlstr) throws Exception {
	// return CodeXML.AddCodeStr(ConstsSw.code_of_setUserParmValue, "OK");
	// }

	private void setLoginedParms(HttpSession session, Shwuser user) throws Exception {
		defaultorgid(session, user);
		Object o = CSession.getvalue(session.getId(), "curentid");
		if (o == null) {
			throw new Exception("登录用户【" + user.username.getValue() + "】没有默认组织");
		}
		setIdpathlike(o.toString(), user.userid.getValue(), session);
	}

	// 需要包含不可用机构的权限
	private void defaultorgid(HttpSession session, Shwuser user) throws Exception {
		CJPALineData<Shworguser> ous = new CJPALineData<Shworguser>(Shworguser.class);
		ous.setPool(user.pool);
		String sqlstr = "select * from shworguser where userid=" + user.userid.getValue();
		ous.findDataBySQL(sqlstr, true, true);
		for (CJPABase oub : ous) {
			Shworguser ou = (Shworguser) oub;
			if (ou.isdefault.getAsIntDefault(0) == 1) {
				Shworg org = new Shworg();
				org.findByID(ou.orgid.getValue());
				// if (org.usable.getAsInt() == 1) {
				CSession.putvalue(session.getId(), "defaultorgid", ou.orgid.getValue());
				CSession.putvalue(session.getId(), "curentid", org.entid.getValue());
				break;
				// }
			}
		}
	}

	//
	/**
	 * 登录成功后，给session设置机构，idpath ，idpathwhere 等信息
	 * 需要包含不可用机构的权限
	 * 
	 * @param entid
	 * @param userid
	 * @param session
	 * @throws Exception
	 */
	private void setIdpathlike(String entid, String userid, HttpSession session) throws Exception {
		CJPALineData<Shworg> userorgs = new CJPALineData<Shworg>(Shworg.class);
		String sqlstr = "select a.* from shworg a,shworguser b where a.orgid=b.orgid and a.entid=" + entid + " and b.userid=" + userid;
		userorgs.findDataBySQL(sqlstr, true, true);

		List<String> allidpaths = new ArrayList<String>();
		List<String> allorgids = new ArrayList<String>();
		for (CJPABase cjpa : userorgs) {
			Shworg borg = (Shworg) cjpa;
			allidpaths.add(borg.idpath.getValue());
			allorgids.add(borg.orgid.getValue());
		}

		sqlstr = "SELECT fidpath,forgid FROM shworg_find WHERE orgid=" + CSession.getvalue(session.getId(), "defaultorgid");// session.getAttribute("");
		List<HashMap<String, String>> idps = DBPools.defaultPool().openSql2List(sqlstr);
		for (HashMap<String, String> idp : idps) {
			allidpaths.add(idp.get("fidpath"));
			allorgids.add(idp.get("forgid"));
		}

		// 去掉子 idpath 及重复 idpath
		List<String> ridps = new ArrayList<String>();
		for (String idp : allidpaths) {
			if (isNeedAppIdpath(ridps, idp)) {
				ridps.add(idp);
			}
		}
		String oidstr = "";
		List<String> orgids = new ArrayList<String>();
		for (String orgid : allorgids) {
			if (!orgids.contains(orgid)) {
				orgids.add(orgid);
				oidstr = oidstr + orgid + ",";
			}
		}

		String fdips = "";
		String idpwhr = "";
		for (String idp : ridps) {
			idpwhr = idpwhr + "( idpath like '" + idp + "%') or ";
			fdips = fdips + idp + "#";
		}
		if (fdips.length() > 0) {
			fdips = fdips.substring(0, fdips.length() - 1);
			CSession.putvalue(session.getId(), "idpaths", fdips);
		}
		if (idpwhr.length() > 0) {
			idpwhr = idpwhr.substring(0, idpwhr.length() - 3);
			idpwhr = " and (" + idpwhr + ")";
			CSession.putvalue(session.getId(), "idpathwhere", idpwhr);
		}
		if (oidstr.length() > 0) {
			oidstr = oidstr.substring(0, oidstr.length() - 1);
			CSession.putvalue(session.getId(), "curorgids", oidstr);
		}
	}

	/**
	 * 是否需要添加IDPATH
	 * 
	 * @param ridps
	 * @param idp
	 * @return
	 */
	private boolean isNeedAppIdpath(List<String> ridps, String idp) {
		if (idp == null)
			return false;// 不给加入，标识为存在
		// System.out.println("isNeedAppIdpath:" + idp);
		Iterator<String> it = ridps.iterator();
		while (it.hasNext()) {
			String ridp = it.next();
			// System.out.println(idp + ":" + ridp);
			if (idp.equalsIgnoreCase(ridp))
				return false;
			if ((idp.length() > ridp.length()) && (idp.substring(0, ridp.length()).equals(ridp))) {
				return false;
			}
			if ((idp.length() < ridp.length()) && (ridp.substring(0, idp.length()).equals(idp))) {
				it.remove();
				return true;
			}

		}
		return true;
	}

	// ///////用户参数 end//////////////////

	// ////////////////////
	/**
	 * 用户登出
	 * 
	 * @throws Exception
	 */
	public static void dologinout() throws Exception {
		HttpSession session = CSContext.getSession();
		if (session == null)
			return;
		String userid = null;
		Object o = CSession.getvalue(session.getId(), "userid");// session.getAttribute("userid");
		if (o != null)
			userid = o.toString();
		String username = null;
		o = CSession.getvalue(session.getId(), "username");// session.getAttribute("username");
		if (o != null)
			username = o.toString();
		String remotehost = null;
		o = CSession.getvalue(session.getId(), "remotehost");// session.getAttribute("");
		if (o != null)
			remotehost = o.toString();

		String sqlstr = "DELETE FROM  shwusertoken WHERE sessionid='" + session.getId() + "'";
		DBPools.defaultPool().execsql(sqlstr);

		Shwuserexeclog ec = new Shwuserexeclog();
		ec.userid.setValue(userid);
		ec.username.setValue(username);
		ec.exectype.setAsInt(4);// 登出
		ec.execname.setValue("登出系统");
		ec.exectime.setAsDatetime(new Date());
		ec.remoteip.setValue(remotehost);
		ec.sessionid.setValue(session.getId());
		try {
			ec.save();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		String[] fields = { "sessionid", "remotehost", "userid", "displayname", "username", "usertype", ConstsSw.session_username,
				ConstsSw.session_logined,
				ConstsSw.session_logintime };
		CSession.removeValue(session.getId(), fields);
	}

	//
	/**
	 * 获取一个可用管理员用户 解密登录密码
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Shwuser getAdminUser() throws Exception {
		String sqlstr = "SELECT * FROM shwuser WHERE actived=1 AND usertype=1";
		Shwuser user = new Shwuser();
		user.findBySQL(sqlstr);
		if (user.isEmpty())
			throw new Exception("无可用系统管理员!");
		if (!user.userpass.isEmpty()) {
			user.userpass.setValue(DesSw.DESryStrHex(user.userpass.getValue(), ConstsSw._userkey));
		}
		return user;
	}

	/**
	 * 获取用户密码
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static String getUserPass(String username) throws Exception {
		String sqlstr = "select * from shwuser where username='" + username.trim().toUpperCase() + "'";
		Shwuser user = new Shwuser();
		user.findBySQL(sqlstr);
		if (user.isEmpty())
			throw new Exception("用户名不存在!");
		if (user.userpass.isEmpty())
			return "";
		return DesSw.DESryStrHex(user.userpass.getValue(), ConstsSw._userkey);
	}
}
