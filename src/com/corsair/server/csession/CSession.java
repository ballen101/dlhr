package com.corsair.server.csession;

import javax.servlet.http.HttpSession;

import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;

/** Session管理
 * @author Administrator
 *
 */
public class CSession {

	public static void putvalue(String field, String value) throws Exception {
		putvalue(null, field, value);
	}

	public static void putvalue(String field, boolean value) throws Exception {
		putvalue(null, field, value);
	}

	public static void putvalue(String field, int value) throws Exception {
		putvalue(null, field, value);
	}

	public static void putvalue(String field, long value) throws Exception {
		putvalue(null, field, value);
	}

	public static void putvalue(String sessionkey, String field, String value) throws Exception {
		HttpSession session = CSContext.getSession();
		if (session == null) {
			throw new Exception("CSession保存共享数据没有发现Session");
		}
		session.setAttribute(field, value);
	}

	public static void putvalue(String sessionkey, String field, boolean value) throws Exception {
		putvalue(sessionkey, field, String.valueOf(value));
	}

	public static void putvalue(String sessionkey, String field, long value) throws Exception {
		putvalue(sessionkey, field, String.valueOf(value));
	}

	public static void putvalue(String sessionkey, String field, int value) throws Exception {
		putvalue(sessionkey, field, String.valueOf(value));
	}

	public static void removeValue(String sessionkey, String[] fields) throws Exception {
		HttpSession session = CSContext.getSession();
		for (String field : fields) {
			session.removeAttribute(field);
		}
	}

	public static void removeValue(String sessionkey, String field) throws Exception {
		HttpSession session = CSContext.getSession();
		session.removeAttribute(field);
	}

	public static void removeValue(String field) throws Exception {
		HttpSession session = CSContext.getSession();
		session.removeAttribute(field);
	}

	public static String getvalue(String field) throws Exception {
		return getvalue(null, field);
	}

	public static long getvalueAsLong(String field) throws Exception {
		String v = getvalue(field);
		if (v == null)
			return 0;
		return Long.valueOf(v);
	}

	public static long getvalueAsLong(String field, String errmsg) throws Exception {
		String v = getvalue(field);
		if (v == null)
			throw new Exception(errmsg);
		return Long.valueOf(v);
	}

	public static String getvalue(String sessionkey, String field) throws Exception {
		HttpSession session = CSContext.getSession();
		if (session == null) {
			throw new Exception("CSession保存共享数据没有发现Session");
		}
		Object o = session.getAttribute(field);
		if (o == null)
			return null;
		else
			return o.toString();

	}

}
