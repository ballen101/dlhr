package com.corsair.server.genco;

import java.util.HashMap;

import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.weixin.WXAppParms;
import com.corsair.server.weixin.WXDeviceUtil;

@ACO(coname = "wxdevice")
public class COWeiXinDevice {

	// http://wx.icefall.com.cn/csmtest/wxdevice/device_Auth.co?appid=wx96d9ec67ac1d8cf5&deviceid=BDE150317_A0021
	@ACOAction(eventname = "device_Auth", notes = "获取微信设备授权旧接口", Authentication = false)
	public String device_Auth() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String deviceid = CorUtil.hashMap2Str(parms, "deviceid", "需要deviceid参数");
		String appid = CorUtil.hashMap2Str(parms, "appid", "需要appid参数");
		WXDeviceUtil.device_Auth(appid, "", deviceid, "1234567890AB", false);
		return "{\"result\":\"ok\"}";
	}

	// http://wx.icefall.com.cn/csmtest/wxdevice/compel_bind.co?appid=wx96d9ec67ac1d8cf5&deviceid=BDE150317_A0021&openid=oZsq4t9TT7vqXhy0x8nbEIaIz26w
	@ACOAction(eventname = "compel_bind", notes = "强制绑定设备", Authentication = false)
	public String compel_bind() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String deviceid = CorUtil.hashMap2Str(parms, "deviceid", "需要deviceid参数");
		String appid = CorUtil.hashMap2Str(parms, "appid", "需要appid参数");
		String openid = CorUtil.hashMap2Str(parms, "openid", "需要openid参数");

		if (WXDeviceUtil.compel_bind(appid, deviceid, openid))
			return "{\"result\":\"ok\"}";
		else
			return "{\"result\":\"err\"}";
	}

	@ACOAction(eventname = "sendDeviceMsg", notes = "给设备发消息", Authentication = false)
	public String sendDeviceMsg() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String deviceid = CorUtil.hashMap2Str(parms, "deviceid", "需要deviceid参数");
		String appid = CorUtil.hashMap2Str(parms, "appid", "需要appid参数");
		String openid = CorUtil.hashMap2Str(parms, "openid", "需要openid参数");
		String content = CorUtil.hashMap2Str(parms, "openid", "需要content参数");
		String device_type = WXAppParms.getAppParm(appid, "WxYSID", "没有发现WxYSID参数");

		WXDeviceUtil.sendDeviceMsg(appid, device_type, deviceid, openid, content);
		return "{\"result\":\"ok\"}";
	}

	@ACOAction(eventname = "newwxdevice", notes = "获取微信设备授权新接口", Authentication = false)
	public String newwxdevice() throws Exception {
		String dcts = CorUtil.hashMap2Str(CSContext.getParms(), "dct");
		String appid = CorUtil.hashMap2Str(CSContext.getParms(), "appid", "需要appid参数");
		String productid = CorUtil.hashMap2Str(CSContext.getParms(), "productid", "需要productid参数");
		int dct = ((dcts == null) || (dcts.length() == 0)) ? 1 : Integer.valueOf(dcts);
		if (WXDeviceUtil.getNewDeviceID(appid, dct, productid))
			return "{\"result\":\"ok\"}";
		else
			return "{\"result\":\"err\"}";
	}
}
