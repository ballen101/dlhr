package com.corsair.server.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPoolParms;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CWFNotify;
import com.corsair.server.eai.EAIController;
import com.corsair.server.eai.PropertiesHelper;
import com.corsair.server.generic.Shwsystemparms;
import com.corsair.server.listener.CFileListener;
import com.corsair.server.weixin.TonkenReflash;
import com.corsair.server.weixin.WXAppParms;

/**
 * 初始化系统参数
 * 
 * @author Administrator
 *
 */
public class InitSysParms {
	private String rootpath;

	public InitSysParms(String rootpath) {
		this.rootpath = rootpath;
		ConstsSw._root_path = rootpath;
		readSysParms();
	}

	private void readSysParms() {
		try {
			// CServletProperty.getEndPoints();
			String fsep = System.getProperty("file.separator");
			ConstsSw._root_filepath = rootpath;
			// D:\MyWorks2\zy\webservice\tomcat71\webapps\dlhr\
			// System.out.println("rootpath:" + rootpath);
			ConstsSw.eaiXMLFilePath = ConstsSw._root_filepath + "WEB-INF" + fsep + "conf" + fsep + "CEAI" + fsep;
			ConstsSw.excelModelPath = ConstsSw._root_filepath + "ExcelModels" + fsep + "";
			ConstsSw.eaiDatesProtertys = new PropertiesHelper(ConstsSw.eaiXMLFilePath + "eailastdates.prt");
			File f = new File(rootpath);
			Logsw.context = f.getName();
			ConstsSw._service_path = f.getParentFile().getParentFile().toString() + fsep;
			initIpHelper(ConstsSw._root_filepath + "WEB-INF" + fsep + "conf" + fsep + "17monipdb.dat");
			readFromFile();
			loaddbpools();
			// loadLoginParms();
			WXAppParms.InitParms();
			getSystemParms();
			if (ConstsSw.getAppParmBoolean("EnableEAI"))
				ConstsSw.eaicontroller = new EAIController();

			ConstsSw.tonkenReflash = new TonkenReflash();
			ConstsSw.tonkenReflash.StartService();
			PackageUtil.initAllCOClassName();
			CWFNotify.initNotityInfo();// 初始化流程事件监听器
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initIpHelper(String fname) {
		try {
			File file = new File(fname);
			if ((file.exists()) && (file.isFile())) {
				System.out.println("初始化IP数据库......");
				ConstsSw.iphandler = new IPDataHandler(fname);
				System.out.println("初始化IP数据库......OK");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getSystemParms() throws Exception {
		if (ConstsSw._sys_parms == null)
			ConstsSw._sys_parms = new CJPALineData<Shwsystemparms>(Shwsystemparms.class);
		String sqlstr = "select * from shwsystemparms where useable=1 order by sspid";
		ConstsSw._sys_parms.findDataBySQL(sqlstr, true, false);
	}

	private void readFromFile() {
		String fsep = System.getProperty("file.separator");
		String fname = rootpath + "WEB-INF" + fsep + "conf" + fsep + "corsair.properties";
		try {
			System.out.println("解析系统参数......");
			// InputStreamReader
			InputStream in = new BufferedInputStream(new FileInputStream(fname));
			Properties prop = new Properties();
			// prop.load(in);
			prop.load(new InputStreamReader(in, "UTF-8"));

			Enumeration<?> emns = prop.propertyNames();
			while (emns.hasMoreElements()) {
				String key = (String) emns.nextElement();
				String value = prop.getProperty(key);

				if (key.equalsIgnoreCase("UDFilePath")) {
					if ((value == null) || value.length() == 0)
						value = "downloads";
					value = getdir(value);
				}
				if (key.equalsIgnoreCase("ClientPath")) {
					if ((value == null) || value.length() == 0)
						value = "c:\\corsair";
					value = getdir(value);
				}
				if (key.equalsIgnoreCase("LogPath")) {
					if ((value == null) || value.length() == 0)
						value = "log";
					value = getdir(value);
					File file = new File(value);
					if (!file.exists() && !file.isDirectory()) {
						file.mkdir();
					}
					Logsw.LogPath = value;
				}
				if (key.equalsIgnoreCase("HtmlTempSrc")) {
					if ((value != null) && !value.isEmpty()) {
						List<String> htss = new ArrayList<String>();
						String[] btpaths = value.split(";");
						for (String btpath : btpaths) {
							if ((btpath != null) && (!btpath.isEmpty())) {
								String truepath = getdir(btpath);
								File file = new File(truepath);
								if (!file.exists() && !file.isDirectory()) {
									file.mkdir();
								}
								htss.add(truepath);
							}
						}
						ConstsSw._app_parms.put(key, htss);
						value = null;// 不再加入列表
					}
				}

				if (key.equalsIgnoreCase("Debug_Mode")) {
					Logsw.Debug_Mode = Boolean.valueOf(value);
				}
				if (key.equalsIgnoreCase("DBLog")) {

					Logsw.savedblog = Boolean.valueOf(value);
					System.out.println("Logsw.savedblog:" + Logsw.savedblog);
				}
				if (value != null)
					ConstsSw._app_parms.put(key, value);
				System.out.println(key + ":" + value);
			}
			// String udf = prop.getProperty("UDFilePath");

			if (in != null)
				in.close();

			initSessionShareType();
			initFildListener();
			System.out.println("解析系统参数......OK");
		} catch (Exception e) {
			Logsw.error(e.getMessage());
			e.printStackTrace();
		}

	}// synchronized

	private void initFildListener() throws Exception {
		Object o = ConstsSw.getAppParm("AutoBuildTemp");
		if ((o != null) && (!o.toString().isEmpty())) {
			if (Boolean.valueOf(o.toString())) {
				o = ConstsSw.getAppParm("AutoBuildTempTime");
				int ot = (o == null) ? 5000 : Integer.valueOf(o.toString());
				ConstsSw.fmonitor = new FileAlterationMonitor(ot);
				ArrayList<String> pahts = (ArrayList<String>) ConstsSw.getAppParm("HtmlTempSrc");
				for (String btpath : pahts) {
					if ((btpath != null) && (!btpath.isEmpty())) {
						FileAlterationObserver observer = new FileAlterationObserver(new File(btpath));
						ConstsSw.fmonitor.addObserver(observer);
						observer.addListener(new CFileListener());
					}
				}
				ConstsSw.fmonitor.start();
			}
		}
	}

	private void initSessionShareType() {
		Object o = ConstsSw.getAppParm("Allow_redis");
		if (o != null) {
			ConstsSw._allow_redis = Boolean.valueOf(o.toString());
			if (ConstsSw._allow_redis) {
				ConstsSw._redis_ip = ConstsSw.getAppParm("Redis_SerIP").toString();
				ConstsSw._redis_port = Integer.valueOf(ConstsSw.getAppParm("Redis_SerPort").toString());
				ConstsSw._redis_timeout = Integer.valueOf(ConstsSw.getAppParm("Redis_ParmTimeout").toString());
			}
		}
	}

	private void loaddbpools() throws Exception {
		DBPools.setCblog(new CBDLog());// 配置数据日志
		String fsep = System.getProperty("file.separator");
		String fname = rootpath + "WEB-INF" + fsep + "conf" + fsep + "dbpools.xml";

		System.out.println("数据库连接池配置......");

		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(new File(fname));
		} catch (DocumentException e) {
			throw e;
		}
		Element root = document.getRootElement();
		if (!root.getName().toUpperCase().equals("dbpools".toUpperCase())) {
			throw new Exception("解析XML错误，未发现dbpools根节点!");
		}
		List<?> pools = root.elements("dbpool");
		if (pools.size() == 0) {
			throw new Exception("无数据库连接池！");
		}
		boolean isfinddefault = false;
		for (int i = 0; i < pools.size(); i++) {
			DBPoolParms pp = new DBPoolParms();
			Element pool = (Element) pools.get(i);
			pp.name = pool.element("name").getText();
			try {
				pp.isdefault = Boolean.valueOf(pool.element("default").getText());
				if (isfinddefault && pp.isdefault) {
					throw new Exception("发现多个默认连接池");
				}
				if (pp.isdefault)
					isfinddefault = true;
				pp.dbtype = Integer.valueOf(pool.element("dbtype").getText());
				pp.dirver = pool.element("driver").getText();
				pp.url = pool.element("url").getText();
				pp.schema = pool.element("schema").getText();
				pp.user = pool.element("user").getText();
				pp.password = pool.element("password").getText();
				pp.minsession = Integer.valueOf(pool.element("minsession").getText());
				pp.maxsession = Integer.valueOf(pool.element("maxsession").getText());
				pp.checkcontime = Integer.valueOf(pool.element("checkcontime").getText());
				pp.timeout = Integer.valueOf(pool.element("timeout").getText());
				Element e = pool.element("encpassword");
				if (e != null) {
					boolean encpassword = Boolean.valueOf(e.getText());
					if (encpassword) {
						pp.password = DesSw.DESryStrHex(pp.password, ConstsSw._userkey);
					}
				}
				CDBPool p = new CDBPool(pp);
				p.setIct(new CDBContext());// 设置连接池获取引用信息
				DBPools.addPool(p);
				System.out.println("	" + pp.name + " OK!");
			} catch (Exception e) {
				throw e;
			}
		}
		if (!isfinddefault)
			throw new Exception("没有发现默认连接池");
		System.out.println("数据库连接池配置......OK");
	}

	public String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	// value c:\corsair\log\ or \log\
	// /root/aa/bb or ./bb/
	private String getdir(String value) {
		String fsep = System.getProperty("file.separator");
		value = value.replace("\\", fsep);
		value = value.replace("/", fsep);
		String osname = System.getProperties().getProperty("os.name");
		String rst = null;
		if (osname.indexOf("Windows") != -1) {
			if (value.indexOf(":" + fsep) != -1)
				rst = value;
			else {
				if (value.substring(0, 1).equalsIgnoreCase(fsep))
					rst = rootpath + value.substring(1);
				else
					rst = rootpath + value;
			}
		} else {
			if (value.substring(0, 1).equals(".")) {
				rst = rootpath + value.substring(1);
			} else
				rst = value;
		}
		if (!rst.substring(rst.length() - 1).equals(fsep))
			rst = rst + fsep;
		return rst;
	}

}
