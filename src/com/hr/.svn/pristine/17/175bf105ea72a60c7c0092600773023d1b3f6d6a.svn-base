package com.hr.util.wx;

import net.sf.json.JSONObject;

public class WeixinDepartUtil {
	// 获取部门列表地址
	private static String depart_list_url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN";

	private static String get_user_url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&userid=USERID";
	public static String GetDepartList(){
		String requestUrl = depart_list_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken().getToken()); 
		JSONObject jsonObject = WeixinUtil.HttpRequest(requestUrl, "GET", null);  
		// 如果请求成功  
		if (null != jsonObject) { 
			return jsonObject.toString();
		} else{
			return "";
		}

	}
	
	public static String GetUser(){
		String requestUrl = get_user_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken().getToken()).replace("USERID", "377614"); 
		JSONObject jsonObject = WeixinUtil.HttpRequest(requestUrl, "GET", null);  
		// 如果请求成功  
		if (null != jsonObject) { 
			return jsonObject.toString();
		} else{
			return "";
		}

	}
}
