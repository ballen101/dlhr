package com.corsair.server.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationMonitor;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.JPAControllerBase;
import com.corsair.server.ctrl.OnChangeOrgInfoListener;
import com.corsair.server.eai.EAIController;
import com.corsair.server.eai.PropertiesHelper;
import com.corsair.server.generic.Shwsystemparms;
import com.corsair.server.listener.SWSpecClass;
import com.corsair.server.util.DesSw;
import com.corsair.server.util.IPDataHandler;
import com.corsair.server.weixin.TonkenReflash;

/**
 * 常量
 * 
 * @author Administrator
 *
 */
public class ConstsSw {

	/**
	 * 是否启用redis，启用后session里面东西将保存到redis
	 */
	public static boolean _allow_redis = false;
	public static String _redis_ip = null;
	public static int _redis_port = 0;
	public static int _redis_timeout = 0;
	// public static long debug_zip_time = 0;
	// public static long debug_base64_time = 0;

	/**
	 * 好像EAI调用的吧？很久没用不记得了!!!!!!!!!!!
	 */
	public static EAIController eaicontroller = null;
	/**
	 * 微信2小时更新一次的token
	 */
	public static TonkenReflash tonkenReflash = null;
	/**
	 * ip地址解析对象
	 */
	public static IPDataHandler iphandler = null;

	// // user session
	public static final String session_logined = "LOGINED";
	public static final String session_username = "USERNAME";
	public static final String session_userpwd = "USERPWD";
	public static final String session_logintime = "LOGINTIME";
	public static final String session_activetime = "ACTIVETIME";
	public static final String session_remoteaddr = "REMOTEADDR";
	// //
	// public static final String MSG_OF_LOGIN_ACCESS = "Login Accessed";
	// public static String spms_ltable = "";// 表
	// public static String spms_luserfd = "";// 用户名字段
	// public static String spms_lpwdfd = "";// 用户密码字段
	// public static String spms_lstaufd = "";// 用户状态字段
	// public static float spms_llowestver = 0;// 最低版本
	// public static String spms_ls_dftv = "";// 允许登录的值
	// public static List<String[]> spms_lnotallvs = new ArrayList<String[]>();// 不允许登录的值和提示信息
	/**
	 * corsair.properties参数列表
	 */
	public static HashMap<Object, Object> _app_parms = new HashMap<Object, Object>();
	/**
	 * Shwsystemparms表中的参数列表
	 */
	public static CJPALineData<Shwsystemparms> _sys_parms = null;
	/**
	 * CO请求处理列表
	 */
	public static HashMap<String, Object> _allCoClassName = new HashMap<String, Object>(); // 所有co请求
	/**
	 * 特殊类列表 如servetContextInit
	 */
	public static List<SWSpecClass> _allSpecClass = new ArrayList<SWSpecClass>();//
	/**
	 * 所有 websocket 类
	 */
	public static HashMap<String, Object> _allSocketClassName = new HashMap<String, Object>(); //
	/**
	 * 公用JPAController
	 */
	public static JPAControllerBase publicJPAController = null;
	/**
	 * 机构信息变化监听器
	 */
	public static OnChangeOrgInfoListener _onchgorginfolestener;//
	/**
	 * 文件变化监听器
	 */
	public static FileAlterationMonitor fmonitor = null;//

	public static final int dbk_BLOB = 1;
	public static final int dbk_CLOB = 2;

	// public static Category[] _categoryArray = new Category[4];
	/**
	 * 中间件路径 D:\MyWorks2\zy\webservice\tomcat71\
	 */
	public static String _service_path;
	/**
	 * 系统应用路径 ，如：D:\MyWorks2\zy\webservice\tomcat71\webapps\dlhr\
	 */
	public static String _root_path;
	/**
	 * 系统附件文件路径
	 */
	public static String _root_filepath;

	/**
	 * EAI XML配置文件路径
	 */
	public static String eaiXMLFilePath;
	/**
	 * EAI XML模板文件路径
	 */
	public static String excelModelPath;
	/**
	 * 不求记得了
	 */
	public static PropertiesHelper eaiDatesProtertys;

	public static final String _userkey = "Shangwen_!@#";

	/**
	 * 获取应用参数
	 * 
	 * @param key
	 * @return
	 */
	public static Object getAppParm(String key) {
		return _app_parms.get(key);
	}

	/**
	 * 获取应用参数，不存在就报错
	 * 
	 * @param key
	 * @param errmsg
	 * @return
	 * @throws Exception
	 */
	public static String geAppParmStr_E(String key, String errmsg) throws Exception {
		Object o = _app_parms.get(key);
		if (o == null) {
			throw new Exception(errmsg);
		}
		return o.toString();
	}

	/**
	 * 获取Boolean类型应用参数
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getAppParmBoolean(String key) {
		if (_app_parms.get(key) == null)
			return false;
		else
			return Boolean.valueOf(_app_parms.get(key).toString().trim());
	}

	/**
	 * 获取int类型应用参数
	 * 
	 * @param key
	 * @return
	 */
	public static int getAppParmInt(String key) {
		return Integer.valueOf(_app_parms.get(key).toString());
	}

	/**
	 * 获取String类型应用参数
	 * 
	 * @param key
	 * @return
	 */
	public static String geAppParmStr(String key) {
		Object o = _app_parms.get(key);
		if (o == null)
			return null;
		else
			return o.toString();
	}

	/**
	 * 获取String类型系统参数
	 * 
	 * @param parmname
	 * @return
	 */
	public static String getSysParm(String parmname) {
		for (CJPABase jpa : _sys_parms) {
			Shwsystemparms sp = (Shwsystemparms) jpa;
			if (sp.parmname.getValue().equals(parmname)) {
				return sp.parmvalue.getValue();
			}
		}
		return null;
	}

	/**
	 * 获取boolean类型系统参数
	 * 
	 * @param parmname
	 * @return
	 */
	public static boolean getSysParmBoolean(String parmname) {
		String pv = getSysParm(parmname);
		if (pv == null)
			return false;
		return Boolean.valueOf(pv);
	}

	/**
	 * 获取int类型系统参数
	 * 
	 * @param parmname
	 * @return
	 */
	public static int getSysParmInt(String parmname) {
		String pv = getSysParm(parmname);
		if (pv == null)
			return 0;
		return Integer.valueOf(pv);
	}

	/**
	 * 获取int类型系统参数，没有或格式错误返回默认值
	 * 
	 * @param parmname
	 * @param dftv
	 * @return
	 */
	public static int getSysParmIntDefault(String parmname, int dftv) {
		String pv = getSysParm(parmname);
		if (pv == null)
			return dftv;
		try {
			return Integer.valueOf(pv);
		} catch (NumberFormatException e) {
			return dftv;
		}
	}

	/**
	 * 按用户密码加密算法，加密字符串
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String EncryUserPassword(String value) throws Exception {
		return DesSw.EncryStrHex(value, ConstsSw._userkey);
	}

	/**
	 * 按用户密码解密算法，解密字符串
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public static String DESryUserPassword(String value) throws IOException {
		return DesSw.DESryStrHex(value, ConstsSw._userkey);
	}

}
