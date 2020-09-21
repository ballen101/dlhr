package com.corsair.server.weixin;

import java.util.List;

import com.corsair.server.base.ConstsSw;

//高级群发接口
public class SuperMassSend {

	// {
	// "touser":[
	// "OPENID1",
	// "OPENID2"
	// ],
	// "mpnews":{
	// "media_id":"123dsdajkasd231jhksad"
	// },
	// "msgtype":"mpnews"
	// }

	public static boolean sendSuperMassMsg(String appid, List<String> toUsers, String media_id) throws Exception {
		String senddata = pars(toUsers, media_id, "news");
		String url = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=" + WXUtil.getTonken(appid);
		String rst = WXHttps.postHttps(url, null, senddata);
		return true;
	}

	private static String pars(List<String> toUsers, String media_id, String msgtype) {
		return "";
	}
}
