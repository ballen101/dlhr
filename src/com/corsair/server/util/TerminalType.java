package com.corsair.server.util;

/**
 * 终端编码
 * 
 * @author Administrator
 *
 */
public class TerminalType {
	public enum TermKink {
		desktop, mobile;
	}

	public enum TermType {
		iPhone, iPod, android, win_Phone, blackBerry, symbian, windows, macintosh, linux, freeBSD, sunOS, unKnow, delphiclt, wx_android, wx_iphone;
	}

	// PC:[Boland,Windows,MSIE,Macintosh,Linux,FreeBSD,SunOS];
	// Mobile:[iPhone,iPod,Android,Windows Phone,BlackBerry,Symbian]
	// 微信：userAgent:Mozilla/5.0 (Linux; Android 5.1.1; vivo X6SPlus D Build/LMY47V; wv)
	// AppleWebKit/537.36 (KHTML, like Gecko)
	// Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043909
	// Mobile Safari/537.36 MicroMessenger/6.6.5.1280(0x26060533) NetType/WIFI Language/zh_CN
	public static TermType getTerminalType(String userAgent) {
		if (userAgent == null)
			return TermType.unKnow;
		if (userAgent.indexOf("MicroMessenger") >= 0) {// 微信
			if (userAgent.indexOf("iPhone") >= 0)
				return TermType.wx_iphone;
			if (userAgent.indexOf("Android") >= 0)
				return TermType.wx_android;
		} else {
			if (userAgent.indexOf("iPhone") >= 0)
				return TermType.iPhone;
			if (userAgent.indexOf("iPod") >= 0)
				return TermType.iPod;
			if (userAgent.indexOf("Android") >= 0)
				return TermType.android;
		}
		if (userAgent.indexOf("Windows Phone") >= 0)
			return TermType.win_Phone;
		if (userAgent.indexOf("BlackBerry") >= 0)
			return TermType.blackBerry;
		if (userAgent.indexOf("Boland") >= 0)
			return TermType.delphiclt;
		if (userAgent.indexOf("Windows") >= 0)
			return TermType.windows;
		if (userAgent.indexOf("Macintosh") >= 0)
			return TermType.macintosh;
		if (userAgent.indexOf("Linux") >= 0)
			return TermType.linux;
		if (userAgent.indexOf("FreeBSD") >= 0)
			return TermType.freeBSD;
		if (userAgent.indexOf("SunOS") >= 0)
			return TermType.sunOS;
		if (userAgent.indexOf("MSIE") >= 0)
			return TermType.windows;
		return TermType.unKnow;
	}

	public static TermKink getTermKink(TermType tt) {
		TermType[] dts = { TermType.windows, TermType.macintosh, TermType.linux, TermType.freeBSD, TermType.sunOS, TermType.unKnow, TermType.delphiclt };
		for (TermType dt : dts) {
			if (dt == tt)
				return TermKink.desktop;
		}
		return TermKink.mobile;
	}
}
