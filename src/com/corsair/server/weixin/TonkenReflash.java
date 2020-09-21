package com.corsair.server.weixin;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.weixin.entity.Shwwxapp;

public class TonkenReflash {
	private static int expires_in = 7200;

	private List<Thread> tonkensers = new ArrayList<Thread>();

	public TonkenReflash() throws Exception {
		// WxTonkenServiceActive appid1;appid2;appid3;
		// if (ConstsSw.getAppParm("WxTonkenSerAppid") != null) {
		// String ids = ConstsSw.geAppParmStr("WxTonkenSerAppid");
		// String[] appids = ids.split(";");
		// for (String appid : appids) {
		// Shwwxapp app = WXAppParms.getShwwxappByAppID(appid);
		// if (app != null) {
		// if ("1".equals(WXAppParms.getAppParm(app, "UsesAble"))) {
		// createAPPReflasher(app);
		// }
		// }
		// }
		// }
		if (ConstsSw.getAppParmBoolean("WxTonkenServiceActive")) {
			CJPALineData<Shwwxapp> shwwxapps = WXAppParms.getShwwxapps();
			for (CJPABase jpa : shwwxapps) {
				Shwwxapp wxapp = (Shwwxapp) jpa;
				if ("1".equals(WXAppParms.getAppParm(wxapp, "UsesAble"))) {
					createAPPReflasher(wxapp);
				}
			}
		}

	}

	private void createAPPReflasher(Shwwxapp app) throws Exception {
		System.out.println("TonkenReflash created,APPID:" + app.wxappid.getValue());
		String pn = WXAppParms.getAppParm(app, "WxDbpool");
		CDBPool pool = DBPools.poolByName(pn);
		String appsecret = WXAppParms.getAppParm(app, "Wxappsecret");
		createTable(pool);
		Thread tonkenService = new Thread(new TonkenService(pool, app.wxappid.getValue(), appsecret));
		tonkensers.add(tonkenService);
	}

	public void StartService() {
		for (Thread t : tonkensers) {
			t.start();
		}
	}

	public void StopService() {
		for (Thread t : tonkensers) {
			if (t != null)
				t.interrupt();
		}
	}

	private void createTable(CDBPool pool) throws Exception {
		String csql = " CREATE TABLE IF NOT EXISTS `wx_access_tonken` ("
				+ "  `appid` varchar(128) NOT NULL,"
				+ "  `tonken` varchar(1024) NOT NULL,"
				+ "  `ticket` varchar(1024) NOT NULL,"
				+ "  `updatetime` datetime NOT NULL,"
				+ "  PRIMARY KEY (`appid`)"
				+ ")  ENGINE=MyISAM DEFAULT CHARSET=utf8";

		pool.execsql(csql);
	}

	private class TonkenService implements Runnable {
		private CDBPool pool;
		public String appid;
		public String appsecret;

		private TonkenService(CDBPool pool, String appid, String appsecret) {
			this.pool = pool;
			this.appid = appid;
			this.appsecret = appsecret;
		}

		@Override
		public void run() {
			while (true) {
				// {"access_token":"YSLFrnqYhvetcX3AkoyQ5o08_viO594-L8XLmBC31WRSkmpSj13PiavQghg68NtYqSaF-nkftmieMSrfDxqIkipLqYBdHbkRK11Pmw0JKl4","expires_in":7200}
				try {
					getaccess_token();
				} catch (Exception e1) {
					try {
						Thread.sleep(5000);
						getaccess_token();
					} catch (Exception e) {
						try {
							Logsw.error(e);
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}
					try {
						Logsw.error(e1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep((expires_in - 5) * 1000);// 提前5m刷新
				} catch (InterruptedException e) {
					try {
						Logsw.error(e);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}

		private void getaccess_token() throws Exception {
			String url = "https://api.weixin.qq.com/cgi-bin/token";
			HashMap<String, String> parms = new HashMap<String, String>();
			parms.put("grant_type", "client_credential");
			parms.put("appid", appid);
			parms.put("secret", appsecret);
			String rst = WXHttps.getHttps(url, parms);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rst);
			if (rootNode.has("access_token")) {
				String access_token = rootNode.path("access_token").getTextValue();
				url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=jsapi";
				rst = WXHttps.getHttps(url, null);
				rootNode = mapper.readTree(rst);
				if (rootNode.has("ticket")) {
					String ticket = rootNode.path("ticket").getTextValue();
					updateToken(access_token, ticket);
				} else
					System.out.println("获取jsapi错误！");

				System.out.println("reflash access tonken【" + access_token + "】at 【" + Systemdate.getStrDate() + "】");
			}
			if (rootNode.has("expires_in")) {
				expires_in = rootNode.path("expires_in").asInt();
			}
		}

		private void updateToken(String token, String ticket) throws Exception {
			CDBConnection con = pool.getCon(this);
			try {
				con.startTrans();
				con.execsql("delete from wx_access_tonken where appid='" + appid + "'");
				String sqlstr = "insert into wx_access_tonken(appid,tonken,ticket,updatetime) values('"
						+ appid + "','" + token + "','" + ticket + "',sysdate())";
				con.execsql(sqlstr);
				con.submit();
			} catch (Exception e) {
				con.rollback();
				Logsw.error(e);
			} finally {
				con.close();
			}
		}
	}

	private boolean isIPInSelf(String ip) {
		if (ip == null)
			return false;
		try {
			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
				NetworkInterface item = e.nextElement();
				for (InterfaceAddress address : item.getInterfaceAddresses()) {
					if (address.getAddress() instanceof Inet4Address) {
						Inet4Address inet4Address = (Inet4Address) address.getAddress();
						if (ip.equalsIgnoreCase(inet4Address.getHostAddress()))
							return true;
					}
				}
			}
		} catch (IOException ex) {

		}
		return false;
	}

}
