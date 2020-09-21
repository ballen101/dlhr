package com.corsair.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.CSContext.ActionType;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.Login;
import com.corsair.server.base.Loginex;
import com.corsair.server.csession.CSession;
import com.corsair.server.csession.CToken;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.retention.COAcitonItem;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DesSw;
import com.corsair.server.util.TerminalType;

import net.sf.json.JSONObject;

public class CRDispatcherServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum ReqType {
		rget, rpost;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CSContext.setservlet(this);
		CSContext.setResponse(resp);
		CSContext.setactiontype(ActionType.actGet);
		req.getSession(true);
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		String rst = null;
		try {
			rst = callCOAction(req, resp, getCOActionkey(req), ReqType.rget);
		} catch (Exception e) {
			rst = CJSON.getErrJson(getClientErrmsg(e));
			// resp.setStatus(500, e.toString());
			Logsw.error(getStackTrace(e));
		}
		if (rst != null) {
			// System.out.println("rst:" + rst);
			PrintWriter out = resp.getWriter();
			out.print(rst);
			out.flush();
		}
		CSContext.removeResponse();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CSContext.setservlet(this);
		CSContext.setResponse(resp);
		CSContext.setactiontype(ActionType.actPost);
		req.getSession(true);
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		String rst = null;
		try {
			rst = callCOAction(req, resp, getCOActionkey(req), ReqType.rpost);
		} catch (Exception e) {
			rst = CJSON.getErrJson(getClientErrmsg(e));
			// resp.setStatus(500, e.toString());
			Logsw.error(getStackTrace(e));
		}
		if (rst != null) {
			// System.out.println("rst:" + rst);
			PrintWriter out = resp.getWriter();
			out.print(rst);
			out.flush();
		}
		CSContext.removeResponse();
	}

	private String getCOActionkey(HttpServletRequest req) {
		String s = req.getServletPath();
		String coname = s.substring(s.indexOf("/") + 1, s.lastIndexOf("."));
		return coname.replace("/", ".");
	}

	private String callCOAction(HttpServletRequest req, HttpServletResponse resp, String coname, ReqType rtype) throws Exception {
		String postdata = null;
		HashMap<String, String> parms = ServletUtil.parseGetParms(req); // for
																		// get
		if (!ServletFileUpload.isMultipartContent(req)) {// 不是文件类型，解析出来
			// System.out.print("不是上传文件");
			postdata = ServletUtil.getPostData(req); // for post
			CSContext.setPostdata(postdata);
			CSContext.setIsMultipartContent(false);
		} else {
			// System.out.print("是上传文件");
			CSContext.setIsMultipartContent(true);
		}

		CSContext.setParms(parms);
		COAcitonItem actionitem = (COAcitonItem) ConstsSw._allCoClassName.get(coname);
		if (actionitem == null) {
			throw new Exception("没有找到注册的CO:" + coname);
		}
		printdebugmsg(actionitem, rtype, parms, postdata);
		AuthChecked(actionitem, req, parms);// 权限检查
		Class<?> coclass = Class.forName(actionitem.getClassname());
		Method method = coclass.getMethod(actionitem.getMethodname(), new Class[] {});
		if (method == null)
			throw new Exception("没有找到满足条件的方法" + actionitem.getClassname() + "." + actionitem.getMethodname());
		Object co = coclass.newInstance();
		if (co == null)
			throw new Exception("无法实例化CO类【" + actionitem.getClassname() + "】");
		Object obj = method.invoke(co);
		if (obj == null) {
			return null;
		} else
			return obj.toString();
	}

	private void AuthChecked(COAcitonItem actionitem, HttpServletRequest req, HashMap<String, String> parms) throws Exception {
		ACOAction cce = actionitem.getCce();
		String trueip = ServletUtil.getIpAddr(req);
		if (cce.isIntranet()) {// 内网
			if ((trueip == null) || (trueip.isEmpty()))
				throw new Exception("获取客户端IP错误");
			if (!CorUtil.isInner(trueip))
				throw new Exception("只允许内网执行:" + actionitem.getClassname() + "." + actionitem.getMethodname());
		}

		HttpSession session = req.getSession();
		if (session.getAttribute("clientip") == null)
			CSession.putvalue(session.getId(), "clientip", trueip);

		if (!cce.Authentication())// 不需要登录
			return;

		if (!CSContext.logined()) {
			autoLogin(req, parms);
		} else {
			// if (ConstsSw.getAppParmBoolean("websocket")) {
			// CWebSessionSocket ssocket = CSContext.wbsktpool.getCreateSSocket(req.getSession().getId());
			// if (ssocket != null)
			// ssocket.setUserid(CSContext.getUserID());
			// }
		}
		new CToken().updateTokenTimeOut(parms);
		if (cce.Authentication()) {
			if (!CSContext.logined())
				throw new Exception("未登录用户不允许执行:" + actionitem.getClassname() + "." + actionitem.getMethodname());
			if (!"1".equalsIgnoreCase(CSContext.getUserParmValue("usertype")))
				CoAuthChecked(actionitem);
		}

	}

	private void CoAuthChecked(COAcitonItem actionitem) throws Exception {
		ACOAction cce = actionitem.getCce();
		if (cce.ispublic())
			return;
		String cokey = actionitem.getKey();
		String sqlstr = "SELECT mc.coname FROM `shwroleuser` ru,`shwrolemenu` rm,`shwmenuco` mc"
				+ " WHERE ru.`roleid`=rm.`roleid` AND rm.`menuid`=mc.`menuid` AND ru.`userid`=" + CSContext.getUserID();
		List<HashMap<String, String>> cos = DBPools.defaultPool().openSql2List(sqlstr);
		for (HashMap<String, String> co : cos) {
			if (cokey.equalsIgnoreCase(co.get("coname").toString()))
				return;
		}
		throw new Exception("当前登录用户没有CO【" + cokey + "】权限");
	}

	private void printdebugmsg(COAcitonItem actionitem, ReqType rtype, HashMap<String, String> parms, String postdata) {
		if (ConstsSw.getAppParmBoolean("Debug_Mode")) {
			String dbugmsg = null;
			if (rtype == ReqType.rget) {
				String str = "\n";
				if (parms != null)
					for (String key : parms.keySet()) {
						str = str + key + ":" + parms.get(key) + "\n";
					}
				dbugmsg = "CO get调用：" + actionitem.getClassname() + "." + actionitem.getMethodname() + " Parms:" + str;
			}
			if (rtype == ReqType.rpost) {
				String str = "\n";
				if (parms != null)
					for (String key : parms.keySet()) {
						str = str + key + ":" + parms.get(key) + "\n";
					}
				dbugmsg = "CO post调用：" + actionitem.getClassname() + "." + actionitem.getMethodname() + " Parms:" + str + " PostData:" + postdata;
			}
			Logsw.debug(dbugmsg);
		}
	}

	public String getStackTrace(Throwable aThrowable) {
		if (aThrowable == null) {
			return "null";
		}
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	public String getClientErrmsg(Exception e) {
		String s = null;
		if (e.getCause() != null)
			s = e.getCause().getMessage();
		else
			s = e.getMessage();
		if (s == null)
			s = "null pointer error";
		return s;
	}

	/**
	 * 新的登录方式
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void autoLoginByHttpHeadNew(HttpServletRequest request) throws Exception {
		String al = ConstsSw.geAppParmStr("AutoLogin");
		if ((al == null) || (al.equalsIgnoreCase("none")))
			return;
		String lgstr = request.getHeader("_lgstr");
		if ((lgstr != null) && (!lgstr.isEmpty())) {
			lgstr = URLDecoder.decode(lgstr, "utf-8");
			JSONObject jo = JSONObject.fromObject(lgstr);
			// System.out.println("jo:" + jo.toString());
			String username = CorUtil.getJSONValue(jo, "username", "需要参数username");
			String noncestr = CorUtil.getJSONValue(jo, "noncestr", "需要参数noncestr");
			String timestr = CorUtil.getJSONValue(jo, "timestr", "需要参数timestr");
			String md5sign = CorUtil.getJSONValue(jo, "md5sign", "需要参数md5sign");
			String token = Loginex.dologinweb(username, noncestr, timestr, md5sign);
		}
	}

	/**
	 * 旧的方式
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void autoLoginByHttpHead(HttpServletRequest request) throws Exception {
		String al = ConstsSw.geAppParmStr("AutoLogin");
		if ((al == null) || (al.equalsIgnoreCase("none")))
			return;

		if (!CSContext.logined()) {
			String uname = request.getHeader("uname");
			String pwd = request.getHeader("pwd");
			if (((uname == null) || (uname.isEmpty()) || (pwd == null) || (pwd.isEmpty())) && request.getCookies() != null) {
				for (Cookie ck : request.getCookies()) {
					if ("uname".equalsIgnoreCase(ck.getName()))
						uname = ck.getValue();
					if ("pwd".equalsIgnoreCase(ck.getName()))
						pwd = ck.getValue();
				}
			}

			if ((uname == null) || (uname.isEmpty()) || (pwd == null) || (pwd.isEmpty())) {
				// Logsw.debug("设置为自动登录，但是请求头并未包含登录信息，无法登录");
				return;
			}

			uname = java.net.URLDecoder.decode(uname, "utf-8");
			pwd = DesSw.EncryStrHex(pwd, ConstsSw._userkey);

			if (al.equalsIgnoreCase("AllFlatform")) {// 所有平台自动登录
				(new Login()).dologinweb(uname, pwd, 0.0, null);
			} else {
				if ((TerminalType.getTermKink(CSContext.getTermType()) == TerminalType.TermKink.desktop) && (al.equalsIgnoreCase("Desktop"))) {// 桌面端自动登录
					(new Login()).dologinweb(uname, pwd, 0.0, null);
					return;
				}
				if ((TerminalType.getTermKink(CSContext.getTermType()) == TerminalType.TermKink.mobile) && (al.equalsIgnoreCase("Mobile"))) {// 移动端自动登录
					(new Login()).dologinweb(uname, pwd, 0.0, null);
					return;
				}
			}
		}
	}

	private void autoLogin(HttpServletRequest request, HashMap<String, String> parms) throws Exception {
		CToken.autoLoginBytoken(parms);
		if (CSContext.logined())
			return;
		String utoken = parms.get("utoken");
		if ((utoken != null) && (!utoken.isEmpty())) {
			CToken.autoLoginByUtoken(utoken);
		}

		if (CSContext.logined())
			return;
		autoLoginByHttpHeadNew(request);
		if (CSContext.logined())
			return;
		autoLoginByHttpHead(request);
		if (CSContext.logined())
			return;

		try {
			if (CSContext.getPostdata() != null) {
				HashMap<String, String> pprams = CSContext.parPostDataParms();
				String lgstr = pprams.get("_lgstr");
				// System.out.println("lgstr:" + lgstr);
				if ((lgstr != null) && (!lgstr.isEmpty())) {
					JSONObject jo = JSONObject.fromObject(lgstr);
					// System.out.println("jo:" + jo.toString());
					String username = CorUtil.getJSONValue(jo, "username", "需要参数username");
					String noncestr = CorUtil.getJSONValue(jo, "noncestr", "需要参数noncestr");
					String timestr = CorUtil.getJSONValue(jo, "timestr", "需要参数timestr");
					String md5sign = CorUtil.getJSONValue(jo, "md5sign", "需要参数md5sign");
					String token = Loginex.dologinweb(username, noncestr, timestr, md5sign);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
