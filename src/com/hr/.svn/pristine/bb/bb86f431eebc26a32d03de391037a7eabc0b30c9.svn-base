package com.hr.util.wx;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;



public class WeixinUtil {
	//企业ID
	private final static  String corpID="ww4aab23995cfc1d6a";
	//企业密码
	private final static  String secret="u5jm05e5LM4sCu6XyYvobac-txc2tbRYc0GtsM68zhw";
	//应用ID
	public final static String agentid="1000166";
	//获取token地址
	private static  String access_token_url="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CorpID&corpsecret=SECRET";
	//消息发送地址
	public  static String send_msg_url="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
	public  static AccessToken accessToken;

	/** 
	 * 获取access_token 
	 *  
	 * @param CorpID 企业Id 
	 * @param SECRET 管理组的凭证密钥，每个secret代表了对应用、通讯录、接口的不同权限；不同的管理组拥有不同的secret 
	 * @return 
	 */  
	public static AccessToken getAccessToken() {  
		if(accessToken==null) {
			String requestUrl = access_token_url.replace("CorpID", corpID).replace("SECRET", secret);  
			JSONObject jsonObject = HttpRequest(requestUrl, "GET", null);  
			// 如果请求成功
			if (null != jsonObject) {
				try {
					accessToken = new AccessToken();  
					accessToken.setToken(jsonObject.getString("access_token"));  
					accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
					System.out.println("获取token成功:"+jsonObject.getString("access_token")+"————"+jsonObject.getInt("expires_in"));
				} catch (Exception e) {  
					accessToken = null;  
					// 获取token失败  
					String error = String.format("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg")); 
					System.out.println(error);
					getAccessToken();
				}  
			} 
		}

		return accessToken;  
	}


	/**
	 * 数据提交与请求通用方法
	 * @param access_token 凭证
	 * @param RequestMt 请求方式
	 * @param RequestURL 请求地址
	 * @param outstr 提交json数据
	 * */
	public static int PostMessage(String access_token ,String RequestMt , String RequestURL , String outstr){
		int result = 0;
		RequestURL = RequestURL.replace("ACCESS_TOKEN", access_token);
		JSONObject jsonobject = WeixinUtil.HttpRequest(RequestURL, RequestMt, outstr);
		if (null != jsonobject) {  
			if (0 != jsonobject.getInt("errcode")) {  
				result = jsonobject.getInt("errcode");  
				String error = String.format("操作失败 errcode:{} errmsg:{}", jsonobject.getInt("errcode"), jsonobject.getString("errmsg"));  
				System.out.println(error); 
			}  
		}
		return result;
	}



	/** 
	 * 发起https请求并获取结果 
	 *  
	 * @param requestUrl 请求地址 
	 * @param requestMethod 请求方式（GET、POST） 
	 * @param outputStr 提交的数据 
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值) 
	 */  
	public static JSONObject HttpRequest(String request , String RequestMethod , String output ){
		@SuppressWarnings("unused")
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			//建立连接
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod(RequestMethod);
			if(output!=null){
				OutputStream out = connection.getOutputStream();
				out.write(output.getBytes("UTF-8"));
				out.close();
			}
			//流处理
			InputStream input = connection.getInputStream();
			InputStreamReader inputReader = new InputStreamReader(input,"UTF-8");
			BufferedReader reader = new BufferedReader(inputReader);
			String line;
			while((line=reader.readLine())!=null){
				buffer.append(line);
			}
			//关闭连接、释放资源
			reader.close();
			inputReader.close();
			input.close();
			input = null;
			connection.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (Exception e) {
		}
		return jsonObject;
	}
}
