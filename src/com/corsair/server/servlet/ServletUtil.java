package com.corsair.server.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class ServletUtil {
	public static HashMap<String, String> parseGetParms(HttpServletRequest req) throws Exception {
		HashMap<String, String> parms = new HashMap<String, String>();
		Enumeration<?> keys = req.getParameterNames();// 取出客户端传入的所有参数名
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String s = req.getParameter(key).toString();
			// System.out.println("111111111111 old:"+s);
			String value = new String(s.getBytes("UTF-8"), "UTF-8");
			// System.out.println("111111111111 utf-8:"+value);
			// System.out.println("111111111111 gbk:"+new String(s.getBytes("GBK"), "UTF-8"));
			// System.out.println(key.toLowerCase()+":"+value);
			parms.put(key.toLowerCase(), value);
		}
		return parms;
	}

	public static String getPostData(HttpServletRequest req) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) req.getInputStream(), "utf-8"));
		StringBuffer sb = new StringBuffer("");
		String temp;
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();
		return sb.toString();
	}

	public static String getIpAddr(HttpServletRequest request) {
		// String ip = request.getHeader("x-forwarded-for");
		// if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		// ip = request.getHeader("Proxy-Client-IP");
		// }
		// if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
		// ip = request.getHeader("WL-Proxy-Client-IP");
		// }
		// if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		// ip = request.getRemoteAddr();
		// }
		// return ip;
		if (request == null)
			return null;
		String Xip = request.getHeader("X-Real-IP");
		String XFor = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = XFor.indexOf(",");
			if (index != -1) {
				return XFor.substring(0, index);
			} else {
				return XFor;
			}
		}
		XFor = Xip;
		if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
			return XFor;
		}
		if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
			XFor = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
			XFor = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
			XFor = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
			XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
			XFor = request.getRemoteAddr();
		}
		return XFor;
	}
}
