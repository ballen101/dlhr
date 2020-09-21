package com.hr.util.wx;
import com.hr.msg.entity.Wx_msg_send;

import net.sf.json.JSONObject;
public class SendMsgUtil {
	
	
	/**
	 * 通知发送(文字)
	 * @param touser 接受人，多人时用"|"分隔
	 * @param content 消息内容
	 * @return 
	 */
	public static String sendText(String touser,String content){
		String requestUrl =WeixinUtil.send_msg_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken().getToken()); 
		String msgType="text";
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"touser\":" + "\"" + touser + "\",");
		//sb.append("\"toparty\":" + "\"" + toparty + "\",");
		//sb.append("\"totag\":" + "\"" + totag + "\",");
		sb.append("\"msgtype\":" + "\"" + msgType + "\",");
		sb.append("\"agentid\":" + "\"" + WeixinUtil.agentid + "\",");
		sb.append("\"text\":" + "{");
		sb.append("\"content\":" + "\"" + content + "\",");
		sb.append("}");
		JSONObject result=	WeixinUtil.HttpRequest(requestUrl, "POST", sb.toString());
	     if(result.get("errcode").toString().equals("42001")){
	    	 WeixinUtil.accessToken=null;
	    	 sendText(touser,content);
	     }
		return result.toString();



	}
	
	/**
	 * 通知发送(图文)
	 * @param touser 接受人，多人时用"|"分隔
	 * @param title 消息标题
	 * @param description 消息内容
	 * @param url 点击时跳转的地址
	 * @return 
	 */
	public static String sendNews(String touser,String title,String description,String url){
		String requestUrl =WeixinUtil.send_msg_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken().getToken()); 
		String msgType="news";
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"touser\":" + "\"" + touser + "\",");
		//sb.append("\"toparty\":" + "\"" + toparty + "\",");
		//sb.append("\"totag\":" + "\"" + totag + "\",");
		sb.append("\"msgtype\":" + "\"" + msgType + "\",");
		sb.append("\"agentid\":" + "\"" + WeixinUtil.agentid + "\",");
		sb.append("\"news\":" + "{");
		sb.append("\"articles\":" + "[");
		sb.append("{");
		sb.append("\"title\":" + "\"" + title + "\",");
		sb.append("\"description\":" + "\"" + description + "\",");
		sb.append("\"url\":" + "\"" + url + "\",");
		//sb.append("\"picurl\":" + "\"" + picurl + "\"");
		sb.append("}");
		sb.append("]");
		sb.append("}");
		JSONObject result=	WeixinUtil.HttpRequest(requestUrl, "POST", sb.toString());
		return result.toString();



	}
	
	
	
}
