package com.corsair.server.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;

public class CorUtil {

	public static String sql2 = "";

	public static boolean isStrEmpty(String str) {
		return ((str == null) || (str.isEmpty()));
	}

	public static boolean isNumeric(String str) {
		if (str.isEmpty())
			return false;
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	private static boolean isStrMath1(String value1, String relo, String value2) {
		if (value1 == null) {
			if (relo.equalsIgnoreCase("=")) {
				return value2 == null;
			}
			if (relo.equalsIgnoreCase(">")) {
				return false;
			}
			if (relo.equalsIgnoreCase("<")) {
				return value2 != null;
			}
			if (relo.equalsIgnoreCase("<=")) {
				return (false || (value2 == null));
			}
			if (relo.equalsIgnoreCase(">=")) {
				return value2 == null;
			}
			if (relo.equalsIgnoreCase("<>") || (relo.equalsIgnoreCase("!="))) {
				return value2 != null;
			}
		} else if (value2 == null) {
			if (relo.equalsIgnoreCase("=")) {
				return value1 == null;
			}
			if (relo.equalsIgnoreCase(">")) {
				return value1 != null;
			}
			if (relo.equalsIgnoreCase("<")) {
				return false;
			}
			if (relo.equalsIgnoreCase("<=")) {
				return (false || (value1 == null));
			}
			if (relo.equalsIgnoreCase(">=")) {
				return true;
			}
			if (relo.equalsIgnoreCase("<>") || (relo.equalsIgnoreCase("!="))) {
				return value1 != null;
			}
		}
		return false;
	}

	public static boolean isStrMath(String value1, String relo, String value2) throws Exception {
		if (isStrEmpty(relo))
			throw new Exception("文本计算时候发现运算符号为空!");
		// if ((value1 == null) || (value2 == null))
		// throw new Exception("文本计算时候发现值为NULL!");

		// System.out.println("isStrMath:" + value1 + relo + value2);
		if (relo.equalsIgnoreCase("any")) {
			return true;
		}
		if ((value2 != null) && (value2.equalsIgnoreCase("null") || (value2.isEmpty()))) {
			value2 = null;
		}

		if ((value1 != null) && (value1.isEmpty())) {
			value1 = null;
		}

		if ((value1 == null) || (value2 == null)) {
			return isStrMath1(value1, relo, value2);
		}

		boolean ismic = (isNumeric(value1) && isNumeric(value2));
		float v1 = 0;
		float v2 = 0;
		if (ismic) {
			v1 = Float.valueOf(value1);
			v2 = Float.valueOf(value2);
		}

		if (relo.equalsIgnoreCase("=")) {
			if (ismic)
				return (v1 == v2);
			else
				return (value1.equals(value2));
		}

		if (relo.equalsIgnoreCase(">")) {
			if (ismic)
				return (v1 > v2);
			else
				return (value1.compareTo(value2) > 0);
		}

		if (relo.equalsIgnoreCase("<")) {
			if (ismic)
				return (v1 < v2);
			else
				return (value1.compareTo(value2) < 0);
		}

		if (relo.equalsIgnoreCase(">=")) {
			if (ismic)
				return (v1 >= v2);
			else
				return (value1.compareTo(value2) >= 0);
		}

		if (relo.equalsIgnoreCase("<=")) {
			if (ismic)
				return (v1 <= v2);
			else
				return (value1.compareTo(value2) <= 0);
		}

		if (relo.equalsIgnoreCase("<>") || (relo.equalsIgnoreCase("!="))) {
			if (ismic)
				return (v1 != v2);
			else
				return (!value1.equals(value2));
		}

		throw new Exception("文本计算时运算符<" + relo + ">未定义!");
	}

	public static String getDicDisplay(int dicgid, String dictvalue) throws Exception {
		String sqlstr = "select language1 from shwdict where pid=" + dicgid + " and dictvalue='" + dictvalue + "' and usable=1";
		List<HashMap<String, String>> dicvs = DBPools.defaultPool().openSql2List(sqlstr);
		if (dicvs.size() == 0)
			return null;
		return dicvs.get(0).get("language1").toString();
	}

	public static String hashMap2Str(HashMap<String, String> hm, String fdname) {
		Object o = hm.get(fdname);
		if (o == null)
			return null;
		else
			return o.toString();
	}

	/**
	 * 从JSON对象获取属性值，没有返回NULL
	 * 
	 * @param jo
	 * @param fdname
	 * @return
	 * @throws Exception
	 */
	public static String getJSONValue(JSONObject jo, String fdname) throws Exception {
		if (jo.has(fdname)) {
			return jo.getString(fdname);
		} else
			return null;
	}

	/**
	 * 从JSON对象获取属性值，没有返回默认
	 * 
	 * @param jo
	 * @param fdname
	 * @param dvalue
	 * @return
	 * @throws Exception
	 */
	public static String getJSONValueDefault(JSONObject jo, String fdname, String dvalue) throws Exception {
		if (jo.has(fdname)) {
			String rst = jo.getString(fdname);
			if (rst == null) {
				return dvalue;
			}
			return rst;
		} else
			return dvalue;
	}

	/**
	 * 从JSON对象获取属性值，没有报错
	 * 
	 * @param jo
	 * @param fdname
	 * @param errmsg
	 * @return
	 * @throws Exception
	 */
	public static String getJSONValue(JSONObject jo, String fdname, String errmsg) throws Exception {
		if (jo.has(fdname)) {
			String rst = jo.getString(fdname);
			if (rst == null) {
				throw new Exception(errmsg);
			}
			return rst;
		} else
			throw new Exception(errmsg);
	}

	public static int getJSONAsInt(JSONObject jo, String fdname, String errmsg) throws Exception {
		if (jo.has(fdname)) {
			String rst = jo.getString(fdname);
			if (rst == null) {
				throw new Exception(errmsg);
			}
			return Integer.valueOf(rst);
		} else
			throw new Exception(errmsg);
	}

	public static int getJSONAsIntDefault(JSONObject jo, String fdname, int dft) {
		if (jo.has(fdname)) {
			String rst = jo.getString(fdname);
			if (rst == null) {
				return dft;
			}
			try {
				return Integer.valueOf(rst);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return dft;
			}
		} else
			return dft;
	}

	// /////////////

	public static String hashMap2Str(HashMap<String, String> hm, String fdname, String notfinderr) throws Exception {
		Object o = hm.get(fdname);
		if (o == null)
			throw new Exception(notfinderr);
		else
			return o.toString();
	}

	// 获取 JSONParm 值
	public static String getJSONParmValue(List<JSONParm> jps, String parmname, String errmsg) throws Exception {
		String rst = getJSONParmValue(jps, parmname);
		if (rst == null)
			throw new Exception(errmsg);
		return rst;
	}

	// 获取 JSONParm 值
	public static String getJSONParmValue(List<JSONParm> jps, String parmname) throws Exception {
		JSONParm rst = getJSONParm(jps, parmname);
		if (rst == null)
			return null;
		return rst.getParmvalue();
	}

	// 获取 JSONParm
	public static JSONParm getJSONParm(List<JSONParm> jps, String parmname, String errmsg) throws Exception {
		JSONParm rst = getJSONParm(jps, parmname);
		if (rst == null)
			throw new Exception(errmsg);
		return rst;
	}

	// 获取 JSONParm
	public static JSONParm getJSONParm(List<JSONParm> jps, String parmname) {
		for (JSONParm jp : jps) {
			if (jp.getParmname().equals(parmname))
				return jp;
		}
		return null;
	}

	/**
	 * @param jps
	 * @param parmname
	 * @return 获取参数列表
	 */
	public static List<JSONParm> getJSONParms(List<JSONParm> jps, String parmname) {
		List<JSONParm> rst = new ArrayList<JSONParm>();
		for (JSONParm jp : jps) {
			if (jp.getParmname().equals(parmname))
				rst.add(jp);
		}
		return rst;
	}

	// 获取随机字符串
	public static final String randomString(int length) {
		Random randGen = new Random();
		char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
				"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		if (length < 1) {
			return null;
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * 以62进制（字母加数字）生成19位UUID，最短的UUID,区分大小写
	 * 
	 * @return
	 */
	public static String getShotuuid() {
		UUID uuid = UUID.randomUUID();
		StringBuilder sb = new StringBuilder();
		sb.append(digits(uuid.getMostSignificantBits() >> 32, 8));
		sb.append(digits(uuid.getMostSignificantBits() >> 16, 4));
		sb.append(digits(uuid.getMostSignificantBits(), 4));
		sb.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
		sb.append(digits(uuid.getLeastSignificantBits(), 12));
		return sb.toString();
	}

	private static String digits(long val, int digits) {
		long hi = 1L << (digits * 4);
		return UUIDNumbers.toString(hi | (val & (hi - 1)), UUIDNumbers.MAX_RADIX)
				.substring(1);
	}

	// 解析URL参数
	public static List<NameValuePair> ParseUrlParm(String url) {
		String ps = url.substring(url.indexOf("?") + 1, url.length());
		return URLEncodedUtils.parse(ps, Charset.forName("UTF-8"));
	}

	public static String ParseUrlParm(String url, String parmname) {
		if (parmname == null)
			return null;
		List<NameValuePair> ps = ParseUrlParm(url);
		for (NameValuePair p : ps) {
			if (parmname.equals(p.getName())) {
				return p.getValue();
			}
		}
		return null;
	}

	public static String ParseUrlParm(String url, String parmname, String errmsg) throws Exception {
		String rst = ParseUrlParm(url, parmname);
		if (rst == null)
			throw new Exception(errmsg);
		return rst;
	}

	/**
	 * 内网IP是以下面几个段的IP.用户可以自己设置.常用的内网IP地址:
	 * 10.0.0.0~10.255.255.255
	 * 172.16.0.0~172.31.255.255
	 * 192.168.0.0~192.168.255.255
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isInner(String ip) {
		// 正则表达式=。// =、懒得做文字处理了、
		String reg = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";
		Pattern p = Pattern.compile(reg);
		Matcher matcher = p.matcher(ip);
		return matcher.find();
	}

	/**
	 * 获取服务器URL路径
	 * 如果有HttpServletRequest ，从Req中获取，
	 * 否则从配置文件中获取端口
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getBasePath() throws Exception {
		HttpServletRequest request = CSContext.getRequest();
		String basePath = null;
		if (request != null) {
			basePath = request.getScheme() + "://" + request.getServerName();
			if (request.getServerPort() != 80)
				basePath = basePath + ":" + request.getServerPort();
			basePath = basePath + request.getContextPath();
		} else {
			Object o = ConstsSw.getAppParm("DomainName");
			String dname = (o == null) ? null : o.toString();
			if ((dname == null) || (dname.isEmpty())) {
				InetAddress ia = InetAddress.getLocalHost();
				dname = ia.getHostAddress();
			}
			basePath = "http://" + dname + ":" + CServletProperty.getServerPort("http")
					+ "/" + CServletProperty.getContextPath();
		}
		return basePath;
	}

}
