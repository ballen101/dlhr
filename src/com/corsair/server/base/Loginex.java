package com.corsair.server.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpSession;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.csession.CSession;
import com.corsair.server.csession.CToken;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shworguser;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.generic.Shwusertoken;
import com.corsair.server.util.DesSw;
import com.corsair.server.util.DesSwEx;
import com.hr.perm.entity.Hr_employee;

/**
 * @author shangwen
 *         更加安全的登录方式
 */
public class Loginex {
	private static long time_maxtdes = 60 * 30;// 登录时候 允许客户端与服务器有30分钟时差

	/**
	 * 登录
	 * 
	 * @param username
	 * @param noncestr
	 * @param timestr
	 * @param md5sign
	 * @return 成功返回TOKEN
	 * @throws Exception
	 */
	public static String dologinweb(String username, String noncestr, String timestr, String md5sign) throws Exception {
		Date t = Systemdate.getDateByStr(timestr, "yyyyMMddHHmmss");
		if (t == null)
			throw new Exception("时间戳格式错误");
		long st = System.currentTimeMillis() / (1000);
		long stl = t.getTime() / (1000);
		if (Math.abs(st - stl) > time_maxtdes) {
			// 登录时间:【2018-04-15 12:18:43】系统时间:【2018-04-15 12:17:16】 2018-04-15 12:17:16
			Logsw.debug("登录时间:【" + Systemdate.getStrDateByFmt(t, "yyyy-MM-dd HH:mm:ss") + "】系统时间:【" + Systemdate.getStrDateByFmt(new Date(), "yyyy-MM-dd HH:mm:ss") + "】");
			Logsw.debug("登录时间:【" + st + "】系统时间:【" + stl + "】最小差异【" + time_maxtdes + "】 现在差异【" + Math.abs(st - stl) + "】");
			throw new Exception("系统时间错误");
		}
		HttpSession session = CSContext.getSession();
		Shwuser user = new Shwuser();
		String sqlstr = "SELECT * FROM shwuser WHERE username=?";
		PraperedSql psql = new PraperedSql();
		psql.setSqlstr(sqlstr);
		psql.getParms().add(new PraperedValue(java.sql.Types.VARCHAR, username.trim()));// UserName.trim().toUpperCase()
		user.findByPSQL(psql);
		if (user.isEmpty())
			throw new Exception("用户名不存在!");
		if (user.actived.getAsIntDefault(0) != 1)
			throw new Exception("用户已禁用!");
		String userpass = (!user.userpass.isEmpty()) ? DesSw.DESryStrHex(user.userpass.getValue(), ConstsSw._userkey) : "";
		String sqlstr2="select RIGHT(id_number,6)id_number from hr_employee where employee_code='"+user.username.getValue()+"'";
		
		String md5str = "username=" + username + "&noncestr=" + noncestr + "&timestr=" + timestr + "&password=" + userpass;
		//System.out.println(md5str);
		md5str = DesSwEx.MD5(md5str).toUpperCase();
		if (!md5str.equalsIgnoreCase(md5sign)) {
			//System.out.println("正确MD5为:" + md5str);
			String[] fields = { "sessionid", "remotehost", "userid", "displayname", "username", "usertype", ConstsSw.session_username,
					ConstsSw.session_logined,
					ConstsSw.session_logintime };
			CSession.removeValue(session.getId(), fields);
			throw new Exception("密码错误");
		} else {
			Hr_employee emp=new Hr_employee();
			emp.findBySQL(sqlstr2);
			if(!emp.isEmpty())//密码为身份证后6位的拒绝登陆！
			{
				if(emp.id_number.getValue().equals(userpass)){
					String[] fields = { "sessionid", "remotehost", "userid", "displayname", "username", "usertype", ConstsSw.session_username,
							ConstsSw.session_logined,
							ConstsSw.session_logintime };
					CSession.removeValue(session.getId(), fields);
					throw new Exception("当前您的密码为初始化密码，为保障信息安全，请即时更改密码。");
				}
					
			}
			CSession.putvalue(session.getId(), "sessionid", session.getId());
			CSession.putvalue(session.getId(), "remotehost", CSContext.getRequest().getRemoteHost());
			CSession.putvalue(session.getId(), "userid", user.userid.getValue());
			CSession.putvalue(session.getId(), "displayname", user.displayname.getValue());
			CSession.putvalue(session.getId(), "username", user.username.getValue());
			CSession.putvalue(session.getId(), "usertype", user.usertype.getAsIntDefault(0));
			CSession.putvalue(session.getId(), ConstsSw.session_userpwd, null);
			CSession.putvalue(session.getId(), ConstsSw.session_logined, true);
			CSession.putvalue(session.getId(), ConstsSw.session_logintime, System.currentTimeMillis());
			String sessionid = (CSContext.getSession() == null) ? "" : CSContext.getSession().getId();
			setLoginedParms(session, user);
			Shwusertoken tk = CToken.createToken(user, null, sessionid, 0);
			return tk.token.getValue();
		}
	}

	/**
	 * 登录 密码是明文
	 * 
	 * @param username
	 * @param noncestr
	 * @param timestr
	 * @param md5sign
	 * @return 成功返回TOKEN
	 * @throws Exception
	 */
	public static String dologinwebmw(String username, String noncestr, String timestr, String md5sign) throws Exception {
		Date t = Systemdate.getDateByStr(timestr, "yyyyMMddHHmmss");
		if (t == null)
			throw new Exception("时间戳格式错误");
		long st = System.currentTimeMillis() / (1000);
		long stl = t.getTime() / (1000);
		if (Math.abs(st - stl) > time_maxtdes) {
			// 登录时间:【2018-04-15 12:18:43】系统时间:【2018-04-15 12:17:16】 2018-04-15 12:17:16
			Logsw.debug("登录时间:【" + Systemdate.getStrDateByFmt(t, "yyyy-MM-dd HH:mm:ss") + "】系统时间:【" + Systemdate.getStrDateByFmt(new Date(), "yyyy-MM-dd HH:mm:ss") + "】");
			Logsw.debug("登录时间:【" + st + "】系统时间:【" + stl + "】最小差异【" + time_maxtdes + "】 现在差异【" + Math.abs(st - stl) + "】");
			throw new Exception("系统时间错误");
		}
		HttpSession session = CSContext.getSession();
		Shwuser user = new Shwuser();
		String sqlstr = "SELECT * FROM shwuser WHERE username=?";
		PraperedSql psql = new PraperedSql();
		psql.setSqlstr(sqlstr);
		psql.getParms().add(new PraperedValue(java.sql.Types.VARCHAR, username.trim()));// UserName.trim().toUpperCase()
		user.findByPSQL(psql);
		if (user.isEmpty())
			throw new Exception("用户名不存在!");
		if (user.actived.getAsIntDefault(0) != 1)
			throw new Exception("用户已禁用!");
		String userpass = (!user.userpass.isEmpty()) ? ConstsSw.DESryUserPassword(user.userpass.getValue()) : "";
		String md5str = "username=" + username + "&noncestr=" + noncestr + "&timestr=" + timestr + "&password=" + userpass;
		//System.out.println(md5str);
		md5str = DesSwEx.MD5(md5str).toUpperCase();
		if (!md5str.equalsIgnoreCase(md5sign)) {
			//System.out.println("正确MD5为:" + md5str);
			String[] fields = { "sessionid", "remotehost", "userid", "displayname", "username", "usertype", ConstsSw.session_username,
					ConstsSw.session_logined,
					ConstsSw.session_logintime };
			CSession.removeValue(session.getId(), fields);
			throw new Exception("密码错误");
		} else {
			CSession.putvalue(session.getId(), "sessionid", session.getId());
			CSession.putvalue(session.getId(), "remotehost", CSContext.getRequest().getRemoteHost());
			CSession.putvalue(session.getId(), "userid", user.userid.getValue());
			CSession.putvalue(session.getId(), "displayname", user.displayname.getValue());
			CSession.putvalue(session.getId(), "username", user.username.getValue());
			CSession.putvalue(session.getId(), "usertype", user.usertype.getAsIntDefault(0));
			CSession.putvalue(session.getId(), ConstsSw.session_userpwd, null);
			CSession.putvalue(session.getId(), ConstsSw.session_logined, true);
			CSession.putvalue(session.getId(), ConstsSw.session_logintime, System.currentTimeMillis());
			String sessionid = (CSContext.getSession() == null) ? "" : CSContext.getSession().getId();
			setLoginedParms(session, user);
			Shwusertoken tk = CToken.createToken(user, null, sessionid, 0);
			return tk.token.getValue();
		}
	}

	private static void setLoginedParms(HttpSession session, Shwuser user) throws Exception {
		defaultorgid(session, user);
		Object o = CSession.getvalue(session.getId(), "curentid");
		if (o == null) {
			throw new Exception("登录用户【" + user.username.getValue() + "】没有默认机构");
		}
		setIdpathlike(o.toString(), user.userid.getValue(), session);
	}

	// 需要包含不可用机构的权限
	private static void defaultorgid(HttpSession session, Shwuser user) throws Exception {
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

	/**
	 * 登录成功后，给session设置机构，idpath ，idpathwhere 等信息
	 * 需要包含不可用机构的权限
	 * 
	 * @param entid
	 * @param userid
	 * @param session
	 * @throws Exception
	 */
	private static void setIdpathlike(String entid, String userid, HttpSession session) throws Exception {
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
	private static boolean isNeedAppIdpath(List<String> ridps, String idp) {
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
}
