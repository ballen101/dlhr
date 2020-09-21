package com.corsair.server.weixin;

import java.util.Map;

public abstract class WXEventListener {
	/**
	 * 收到消息的事件
	 * 
	 * @param appid
	 * @param msgtype
	 * @param postparms
	 */
	public abstract void onEvent(String appid, WXUtil.MsgType msgtype, Map<String, String> postparms);

	/**
	 * 回复默认文本消息
	 * 
	 * @param appid
	 * @param openid
	 * @param text_atrep_msg
	 * @return
	 */
	public boolean onReplayTextMessage(String appid, String openid, String text_atrep_msg) {
		return true;
	}

	/**
	 * 回复默认图文消息
	 * 
	 * @param appid
	 * @param openid
	 * @param text_atrep_msg
	 * @return
	 */
	public boolean onReplayNewsMessage(String appid, String openid, String newsid) {
		return true;
	}

}
