package com.corsair.server.weixin;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.weixin.entity.Shwwxapp;
import com.corsair.server.weixin.entity.Shwwxappparm;
import com.corsair.server.weixin.entity.Shwwxapptag;

public class WXAppParms {
	private static CJPALineData<Shwwxapp> shwwxapps = new CJPALineData<Shwwxapp>(Shwwxapp.class);

	public static CJPALineData<Shwwxapp> getShwwxapps() {
		return shwwxapps;
	}

	public static void InitParms() {
		String sqlstr = "select * from shwwxapp";
		try {
			shwwxapps.findDataBySQL(sqlstr);
			for (CJPABase jpa : shwwxapps) {
				Shwwxapp app = (Shwwxapp) jpa;
				sqlstr = "SELECT * FROM shwwxmsgcfg WHERE appid=" + app.appid.getValue() + " AND usable=1 order by mcid desc";
				app.getWxmsgcfg().findBySQL(sqlstr);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void InitParms(CDBConnection con) {
		try {
			String sqlstr = "select * from shwwxapp";
			shwwxapps.findDataBySQL(con, sqlstr, true, false);
			for (CJPABase jpa : shwwxapps) {
				Shwwxapp app = (Shwwxapp) jpa;
				sqlstr = "SELECT * FROM shwwxmsgcfg WHERE appid=" + app.appid.getValue() + " AND usable=1 order by mcid desc";
				app.getWxmsgcfg().findBySQL(con, sqlstr, true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 根据微信APPID获得参数
	 * 
	 * @param wxappid
	 * @return
	 */
	public static Shwwxapp getShwwxappByAppID(String wxappid) {
		if (wxappid == null)
			return null;
		for (CJPABase jpa : shwwxapps) {
			Shwwxapp app = (Shwwxapp) jpa;
			if (wxappid.equals(app.wxappid.getValue())) {
				return app;
			}
		}
		return null;
	}

	/**
	 * 根据参数ID获取参数对象，没有试着去查询一次
	 * 
	 * @param appid
	 * @return
	 * @throws Exception
	 */
	public static Shwwxapp getByAppID(int appid) throws Exception {
		for (CJPABase jpa : shwwxapps) {
			Shwwxapp app = (Shwwxapp) jpa;
			if (app.appid.getAsIntDefault(0) == appid) {
				return app;
			}
		}
		Shwwxapp apptem = new Shwwxapp();
		apptem.findByID(String.valueOf(appid));
		if (apptem.isEmpty())
			return null;
		else {
			String sqlstr = "SELECT * FROM shwwxmsgcfg WHERE appid=" + apptem.appid.getValue() + " AND usable=1 order by mcid desc";
			apptem.getWxmsgcfg().findBySQL(sqlstr);
			shwwxapps.add(apptem);
			return apptem;
		}
	}

	/**
	 * 根据参数ID获取参数对象，没有试着去查询一次
	 * 
	 * @param appid
	 * @return
	 * @throws Exception
	 */
	public static Shwwxapp getByWXAppID(String wxappid) throws Exception {
		for (CJPABase jpa : shwwxapps) {
			Shwwxapp app = (Shwwxapp) jpa;
			if (wxappid.equalsIgnoreCase(app.wxappid.getValue())) {
				return app;
			}
		}
		Shwwxapp apptem = new Shwwxapp();
		String sqlstr = "SELECT * FROM shwwxapp WHERE wxappid='" + wxappid + "'";
		apptem.findBySQL(sqlstr);
		if (apptem.isEmpty())
			return null;
		else {
			sqlstr = "SELECT * FROM shwwxmsgcfg WHERE wxappid=" + wxappid + " AND usable=1 order by mcid desc";
			apptem.getWxmsgcfg().findBySQL(sqlstr);
			shwwxapps.add(apptem);
			return apptem;
		}
	}

	public static Shwwxapp getByTgid(String tgid) {
		for (CJPABase jpa : shwwxapps) {
			Shwwxapp app = (Shwwxapp) jpa;
			for (CJPABase j : app.shwwxapptags) {
				Shwwxapptag tag = (Shwwxapptag) j;
				if (tgid.equals(tag.tgid.getValue()))
					return app;
			}
		}
		return null;
	}

	/**
	 * 重新加载所有
	 * 
	 * @throws Exception
	 */
	public static void reloadAllParms() throws Exception {
		InitParms();
	}

	/**
	 * 重新加载所有
	 * 
	 * @throws Exception
	 */
	public static void reloadAllParms(CDBConnection con) throws Exception {
		InitParms(con);
	}

	/**
	 * 从新加载
	 */
	public static Shwwxapp reloadByAppID(int appid) throws Exception {
		CDBConnection con = DBPools.defaultPool().getCon("WXAppParms");
		try {
			return reloadByAppID(con, appid);
		} finally {
			con.close();
		}
	}

	/**
	 * 从新加载
	 */
	public static Shwwxapp reloadByAppID(CDBConnection con, int appid) throws Exception {
		for (CJPABase jpa : shwwxapps) {
			Shwwxapp app = (Shwwxapp) jpa;
			if (app.appid.getAsIntDefault(0) == appid) {
				app.findByID(con, String.valueOf(appid));
				String sqlstr = "SELECT * FROM shwwxmsgcfg WHERE appid=" + app.appid.getValue() + " AND usable=1 order by mcid desc";
				app.getWxmsgcfg().findBySQL(con, sqlstr, true);
				return app;
			}
		}
		Shwwxapp apptem = new Shwwxapp();
		apptem.findByID(con, String.valueOf(appid));
		if (apptem.isEmpty())
			return null;
		else {
			String sqlstr = "SELECT * FROM shwwxmsgcfg WHERE appid=" + apptem.appid.getValue() + " AND usable=1 order by mcid desc";
			apptem.getWxmsgcfg().findBySQL(con, sqlstr, true);
			shwwxapps.add(apptem);
			return apptem;
		}
	}

	/**
	 * 获取tag对象
	 * 
	 * @param app
	 * @param tagid
	 *            微信tagid
	 * @return
	 */
	public static Shwwxapptag getTag(Shwwxapp app, String tagid) {
		System.out.println("app:" + app);
		System.out.println("app.shwwxapptags:" + app.shwwxapptags);
		for (CJPABase jpa : app.shwwxapptags) {
			Shwwxapptag tag = (Shwwxapptag) jpa;
			if (tagid.equals(tag.tagid.getValue()))
				return tag;
		}
		return null;
	}

	/**
	 * 根据wxappid获取parmvalue
	 * 
	 * @param appid
	 * @param parname
	 * @return
	 */
	public static String getAppParm(String appid, String parname) {
		if (parname == null)
			return null;
		Shwwxapp app = getShwwxappByAppID(appid);
		return getAppParm(app, parname);
	}

	/**
	 * 根据appparm获取parmvalue
	 * 
	 * @param app
	 * @param parname
	 * @return
	 */
	public static String getAppParm(Shwwxapp app, String parname) {
		if (app == null)
			return null;
		for (CJPABase jpa : app.shwwxappparms) {
			Shwwxappparm parm = (Shwwxappparm) jpa;
			if (parname.equals(parm.parmname.getValue())) {
				return parm.parmvalue.getValue();
			}
		}
		return null;
	}

	/**
	 * 根据appparm获取parmvalue 为空则报错
	 * 
	 * @param app
	 * @param parname
	 * @param emsg
	 * @return
	 * @throws Exception
	 */
	public static String getAppParm(Shwwxapp app, String parname, String emsg) throws Exception {
		String v = getAppParm(app, parname);
		if (v == null) {
			throw new Exception(emsg);
		}
		return v;
	}

	public static String getAppParm(String appid, String parname, String emsg) throws Exception {
		String v = getAppParm(appid, parname);
		if (v == null) {
			throw new Exception(emsg);
		}
		return v;
	}

	public static String getAPPCertPath(Shwwxapp wxapp) {
		String fsep = System.getProperty("file.separator");
		return ConstsSw._root_filepath + "WEB-INF" + fsep + "conf" + fsep + "cert" + fsep + wxapp.wxappid.getValue() + fsep;
	}

	/**
	 * 根据微信appid获取cert路径
	 * 
	 * @param wxappid
	 * @return
	 */
	public static String getAPPCertPathByWXAppid(String wxappid) {
		Shwwxapp wxapp = getShwwxappByAppID(wxappid);
		return getAPPCertPath(wxapp);
	}

	/**
	 * 根据微信id获取cert路径
	 * 
	 * @param appid
	 * @return
	 * @throws Exception
	 */
	public static String getAPPCertPathByAppid(int appid) throws Exception {
		Shwwxapp wxapp = getByAppID(appid);
		String fsep = System.getProperty("file.separator");
		return ConstsSw._root_filepath + "WEB-INF" + fsep + "conf" + fsep + "cert" + fsep + wxapp.wxappid.getValue() + fsep;
	}

	/**
	 * 根据pid获取cert路径
	 * 
	 * @param pid
	 * @return
	 * @throws Exception
	 */
	public static String getAPPCertPathByWXPID(String pid) throws Exception {
		Shwwxappparm wxparm = new Shwwxappparm();
		wxparm.findByID(pid);
		if (wxparm.isEmpty())
			throw new Exception("ID为【" + pid + "】的微信参数不存在");
		return getAPPCertPathByAppid(wxparm.appid.getAsInt());
	}

	/**
	 * 获取微信支付证书
	 * 
	 * @param app
	 * @return
	 * @throws Exception
	 */
	public static String getAPPCertFileName(Shwwxapp app) throws Exception {
		CJPALineData<Shwwxappparm> ps = app.shwwxappparms;
		String[][] fdvs = { { "parmname", "WxPayCert" } };
		Shwwxappparm parm = (Shwwxappparm) ps.getJPAByValues(fdvs);
		if (parm == null)
			return null;
		if (parm.parmvalue.isEmpty())
			return null;
		return getAPPCertPath(app) + parm.parmvalue.getValue();
	}
}
