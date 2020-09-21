package com.corsair.server.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.util.CJSON;
import com.corsair.server.csession.CSession;
import com.corsair.server.generic.Shworg;
import com.corsair.server.servlet.ServletUtil;
import com.corsair.server.util.TerminalType;
import com.corsair.server.util.TerminalType.TermKink;
import com.corsair.server.websocket.CWebSocketPool;

/**
 * 请求环境变量
 * 
 * @author Administrator
 *
 */
public class CSContext {
	public enum ActionType {
		actGet, actPost
	}

	private static List<HttpSession> sessions = Collections.synchronizedList(new ArrayList<HttpSession>());
	private static Lock lock = new ReentrantLock();
	private static ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();
	private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> response = new ThreadLocal<HttpServletResponse>();
	private static ThreadLocal<HashMap<String, String>> parms = new ThreadLocal<HashMap<String, String>>();
	private static ThreadLocal<String> postdata = new ThreadLocal<String>();
	private static ThreadLocal<Boolean> isMultipartContent = new ThreadLocal<Boolean>();
	private static ThreadLocal<HttpServlet> servlet = new ThreadLocal<HttpServlet>();
	private static ThreadLocal<ActionType> actionType = new ThreadLocal<ActionType>();
	private static ThreadLocal<TerminalType.TermType> termType = new ThreadLocal<TerminalType.TermType>();

	/**
	 * webscoket
	 */
	public static CWebSocketPool wbsktpool = new CWebSocketPool();

	private static void addSession2List(HttpSession _session) {
		lock.lock();
		try {
			sessions.add(_session);
		} finally {
			lock.unlock();
		}
	}

	private static void removeSession4List(HttpSession _session) {
		lock.lock();
		try {
			sessions.remove(_session);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 删除session
	 */
	public static void removeSession() {
		removeSession4List(getSession());
		session.remove();
	}

	public static HttpSession getSession() {
		return session.get();
	}

	public static void setSession(HttpSession _session) {
		session.set(_session);
		addSession2List(_session);
	}

	// ///////////////////////
	public static void removeRequest() {
		request.remove();
	}

	public static HttpServletRequest getRequest() {
		return request.get();
	}

	public static void setRequest(HttpServletRequest req) {
		request.set(req);
	}

	// ///////////////////////
	public static void removeResponse() {
		response.remove();
	}

	public static HttpServletResponse getResponse() {
		return response.get();
	}

	public static void setResponse(HttpServletResponse resp) {
		response.set(resp);
	}

	// ///////////////////////
	public static void removeParms() {
		parms.remove();
	}

	/**
	 * url parms
	 * +
	 * post 的_pjdata 字段
	 * +
	 * 并解析POST的字段
	 * 
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> get_pjdataparms() throws Exception {
		HashMap<String, String> urlparms = getParms();
		HashMap<String, String> parms = new HashMap<String, String>();
		for (String key : urlparms.keySet()) {
			if (key.toString().equalsIgnoreCase("_pjdata")) {
				HashMap<String, String> pparms = CJSON.Json2HashMap(urlparms.get(key));
				parms.putAll(pparms);
			} else
				parms.put(key.toString(), urlparms.get(key));
		}

		try {
			String pdata = getPostdata();
			JSONObject jo = JSONObject.fromObject(pdata);
			Iterator<?> iterator = jo.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = jo.getString(key);
				if ((key != null) && (!key.isEmpty()) && (!"_pjdata".equalsIgnoreCase(key)))
					parms.put(key, value);
			}
		} catch (Exception e) {
			// parms错误不处理
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return parms;
	}

	/**
	 * 获取页面get键值对参数
	 * 
	 * @return
	 */
	public static HashMap<String, String> getParms() {
		return parms.get();
	}

	public static void setParms(HashMap<String, String> ps) {
		parms.set(ps);
	}

	public static void pushParm(String key, String value) {
		session.get().setAttribute(key, value);
	}

	public static String getParm(String key) {
		Object o = session.get().getAttribute(key);
		if (o == null)
			return null;
		else
			return o.toString();
	}

	public static void removeParm(String key) {
		session.get().removeAttribute(key);
	}

	// ///////////////////////
	public static void removePostdata() {
		postdata.remove();
	}

	public static String getPostdata() {
		return postdata.get();
	}

	public static void setPostdata(String pd) {
		postdata.set(pd);
	}

	// ////////////////
	public static void removeisMultipartContent() {
		isMultipartContent.remove();
	}

	public static Boolean isMultipartContent() {
		return isMultipartContent.get();
	}

	public static void setIsMultipartContent(Boolean value) {
		isMultipartContent.set(value);
	}

	// ////////////////
	public static void removeservlet() {
		servlet.remove();
	}

	public static HttpServlet getservlet() {
		return servlet.get();
	}

	public static void setservlet(HttpServlet value) {
		servlet.set(value);
	}

	// ////////////////////////////

	public static void removeactiontype() {
		actionType.remove();
	}

	public static ActionType getActionType() {
		return actionType.get();
	}

	public static void setactiontype(ActionType value) {
		actionType.set(value);
	}

	// //////////////////

	// ///////////////////////
	public static void removeTermType() {
		termType.remove();
	}

	public static TerminalType.TermType getTermType() {
		return termType.get();
	}

	public static void setTermType(TerminalType.TermType tp) {
		termType.set(tp);
	}

	// /////////

	public static TermKink getTermKink() {
		return TerminalType.getTermKink(getTermType());
	}

	/**
	 * 当前会话是否已经登录
	 * 
	 * @return
	 */
	public static boolean logined() {
		HttpSession session = getSession();
		if (session == null)
			return false;
		Object o = null;
		try {
			o = CSession.getvalue(session.getId(), ConstsSw.session_logined);
		} catch (Exception e) {
			return false;
		}
		if (o == null)
			return false;
		return Boolean.valueOf(o.toString());
	}

	// //////////////

	/**
	 * 前台 传入的系统参数 idpathwhere;curentid;defaultorgid;
	 * userid;displayname;username;defaultorg:Shworg; userorgs:
	 * CJPALineData<Shworg>;remotehost
	 * 
	 * @param parmname
	 * @return
	 * @throws Exception
	 */
	public static Object getUserParmObject(String parmname) throws Exception {
		HttpSession session = getSession();
		if (session == null)
			throw new Exception("获取系统参数<" + parmname + ">Session 未找到!");
		Object o = CSession.getvalue(session.getId(), parmname);
		if (o == null)
			throw new Exception("获取系统参数<" + parmname + ">为空!");
		return o;
	}

	/**
	 * 根据参数名获取参数
	 * 
	 * @param parmname
	 * @return
	 * @throws Exception
	 */
	public static String getUserParmValue(String parmname) throws Exception {
		Object o = getUserParmObject(parmname);
		return o.toString();
	}

	/**
	 * 获取Idpath
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getIdpathwhere() throws Exception {
		return getUserParmValue("idpathwhere");
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static String getClientIP() {
		try {
			HttpSession session = getSession();
			if (session == null)
				return null;
			Object o = session.getAttribute("clientip");
			if (o == null)
				return null;
			else
				return o.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取当前组织
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getCurEntID() throws Exception {
		return getUserParmValue("curentid");
	}

	/**
	 * 获取默认机构
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getDefaultOrgID() throws Exception {
		return getUserParmValue("defaultorgid");
	}

	/**
	 * 获取登录用户ID，未登录报错
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getUserID() throws Exception {
		return getUserParmValue("userid");
	}

	/**
	 * 获取登录用户显示名 未登录报错
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getUserDisplayname() throws Exception {
		return getUserParmValue("displayname");
	}

	/**
	 * 获取登录用户名 未登录报错
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getUserName() throws Exception {
		return getUserParmValue("username");
	}

	/**
	 * 获取登录用户名 未登录返回null
	 * 
	 * @return
	 */
	public static String getUserNameEx() {
		HttpSession session = getSession();
		if (session == null)
			return null;
		Object o = null;
		try {
			o = CSession.getvalue(session.getId(), "username");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // session.getAttribute("");
		if (o == null)
			return null;
		return o.toString();
	}

	/**
	 * 获取登录用户默认机构 未登录返回null
	 * 
	 * @return
	 */
	public static Shworg getUserDefaultOrg() {
		try {
			Shworg org = new Shworg();
			org.findByID(getDefaultOrgID());
			return org;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// no errror

	/**
	 * 获取登录用户参数，未登录不报错 返回null
	 * 
	 * @param parmname
	 * @return
	 */
	public static Object getUserParmObjectNoErr(String parmname) {
		HttpSession session = getSession();
		if (session == null)
			return null;
		try {
			Object o = CSession.getvalue(session.getId(), parmname);
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取登录用户参数，未登录不报错 返回null
	 * 
	 * @param parmname
	 * @return
	 */
	public static String getUserParmValueNoErr(String parmname) {
		Object o = getUserParmObjectNoErr(parmname);
		if (o == null)
			return null;
		else
			return o.toString();
	}

	/**
	 * 获取登录用户idpath
	 * 
	 * @return
	 */
	public static String getIdpathwhereNoErr() {
		return getUserParmValueNoErr("idpathwhere");
	}

	/**
	 * 获取登录用户组织id
	 * 
	 * @return
	 */
	public static String getCurEntIDNoErr() {
		return getUserParmValueNoErr("curentid");
	}

	/**
	 * 获取登录用户默认机构
	 * 
	 * @return
	 */
	public static String getDefaultOrgIDNoErr() {
		return getUserParmValueNoErr("defaultorgid");
	}

	/**
	 * 获取登录用户ID
	 * 
	 * @return
	 */
	public static String getUserIDNoErr() {
		return getUserParmValueNoErr("userid");
	}

	/**
	 * 获取登录用户显示名称
	 * 
	 * @return
	 */
	public static String getUserDisplaynameNoErr() {
		return getUserParmValueNoErr("displayname");
	}

	/**
	 * 获取登录用户
	 * 
	 * @return
	 */
	public static String getUserNameNoErr() {
		return getUserParmValueNoErr("username");
	}

	/**
	 * 获取登录用户ID
	 * 
	 * @return
	 */
	public static String getUserIDNotErr() {
		HttpSession session = getSession();
		if (session == null)
			return null;
		try {
			Object o = CSession.getvalue(session.getId(), "userid");
			if (o == null)
				return null;
			return o.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判断登录用户是否管理员
	 * 
	 * @return
	 */
	public static boolean isAdminNoErr() {
		HttpSession session = getSession();
		if (session == null)
			return false;
		try {
			Object o = CSession.getvalue(session.getId(), "usertype");
			if (o == null)
				return false;
			int ut = Integer.valueOf(o.toString());
			return (ut == 1);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取登录用户所属机构列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static CJPALineData<Shworg> getUserOrgs() throws Exception {
		return getUserOrgs(getUserID(), getCurEntID());
	}

	/**
	 * 获取登录用户所属机构ID列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getUserOrgIDs() throws Exception {
		return getUserParmValue("curorgids");
	}

	/**
	 * 获取登录用户所属机构所有idpath
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String[] getUserIdpaths() throws Exception {
		String sidps = getUserParmValue("idpaths");
		return sidps.split("#");
	}

	/**
	 * 获取登录用户所属机构列表
	 * 
	 * @param userid
	 * @param entid
	 * @return
	 * @throws Exception
	 */
	public static CJPALineData<Shworg> getUserOrgs(String userid, String entid) throws Exception {
		CJPALineData<Shworg> userorgs = new CJPALineData<Shworg>(Shworg.class);
		String sqlstr = "select a.* from shworg a,shworguser b where a.orgid=b.orgid and a.entid=" + entid + " and b.userid=" + userid;
		userorgs.findDataBySQL(sqlstr, true, true);
		return userorgs;
	}

	/**
	 * 获取远程主机名（如果使用了代理是无法获取远程主机名的）
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getRemoteHost() throws Exception {
		return getUserParmValue("remotehost");
	}

	/**
	 * 获取远程IP
	 * 
	 * @return
	 */
	public static String getTrueRemoteHostIP() {
		return ServletUtil.getIpAddr(getRequest());
	}

	/**
	 * 解析前端提交的参数
	 * 
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> parPostDataParms() throws Exception {
		return CJSON.Json2HashMap(getPostdata());
	}

	/**
	 * 将前端提交的参数专为JSON对象
	 * 
	 * @return
	 */
	public static JSONObject parPostData2JSONObject() {
		return JSONObject.fromObject(getPostdata());
	}

	/**
	 * 将前端提交的参数专为JSON数组对象
	 * 
	 * @return
	 */
	public static JSONArray parPostData2JSONArray() {
		return JSONArray.fromObject(getPostdata());
	}

	/**
	 * 获取所有session
	 * 
	 * @return
	 */
	public static List<HttpSession> getSessions() {
		return sessions;
	}

	/**
	 * 返回处理成功的JSON对象
	 * 
	 * @return
	 */
	public static String getJSON_OK() {
		JSONObject rst = new JSONObject();
		rst.put("errcode", 0);
		return rst.toString();
	}

	/**
	 * 返回错误JSON对象
	 * 
	 * @return
	 */
	public static String getJSON_Err() {
		return getJSON_Err(-1, null);
	}

	/**
	 * 返回错误JSON对象
	 * 
	 * @param errcode
	 * @return
	 */
	public static String getJSON_Err(int errcode) {
		return getJSON_Err(errcode, null);
	}

	/**
	 * 返回错误JSON对象
	 * 
	 * @param errcode
	 * @param errmsg
	 * @return
	 */
	public static String getJSON_Err(int errcode, String errmsg) {
		JSONObject rst = new JSONObject();
		rst.put("errcode", errcode);
		if (errmsg != null)
			rst.put("errmsg", errmsg);
		return rst.toString();
	}

}
