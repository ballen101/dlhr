package com.corsair.server.weixin;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.weixin.entity.Wx_device;

public class WXDeviceUtil {
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static final String TransMsgUrl = "https://api.weixin.qq.com/device/transmsg?access_token=ACCESS_TOKEN";
	// 设备授权
	private static final String AuthorizeUrl = "https://api.weixin.qq.com/device/authorize_device?access_token=ACCESS_TOKEN";
	// 设备授权新
	// https://api.weixin.qq.com/device/getqrcode?access_token=ACCESS_TOKEN&product_id=PRODUCT_ID
	private static final String GetQrcode = "https://api.weixin.qq.com/device/getqrcode?access_token=ACCESS_TOKEN&product_id=PRODUCT_ID";

	private static final String CreateQrcode = "https://api.weixin.qq.com/device/create_qrcode?access_token=ACCESS_TOKEN";
	private static final String GetStatUrl = "https://api.weixin.qq.com/device/get_stat?access_token=ACCESS_TOKEN&device_id=DEVICE_ID";
	private static final String VerifyQrcodeUrl = "https://api.weixin.qq.com/device/verify_qrcode?access_token=ACCESS_TOKEN";
	private static final String GetOpenidUrl = "https://api.weixin.qq.com/device/get_openid?access_token=ACCESS_TOKEN&device_type=DEVICE_TYPE&device_id=DEVICE_ID";
	private static final String Create_Qrcode = "https://api.weixin.qq.com/device/create_qrcode?access_token=ACCESS_TOKEN";

	// 第三方公众账号通过设备id从公众平台批量获取设备二维码。
	// {
	// "device_num":"2",
	// "device_id_list":["01234","56789"]
	// }
	public static boolean create_qrcode(String appid, List<String> dids) throws Exception {
		String json = "{\"device_num\":" + dids.size() + ","
				+ "\"device_num\":";
		String ids = "";
		for (String did : dids) {
			ids = "\"" + did + "\",";
		}
		if (ids.length() > 0)
			ids = ids.substring(0, ids.length() - 1);
		ids = "[" + ids + "]";
		json = json + ids + "}";
		System.out.println(json);
		String url = Create_Qrcode.replace("ACCESS_TOKEN", WXUtil.getTonken(appid));

		return false;
	}

	/**
	 * 向设备推送消息
	 * 
	 * @throws Exception
	 */
	public static boolean sendDeviceMsg(String appid, String device_type, String device_id, String openid, String content) throws Exception {
		HashMap<String, String> bdinfo = new HashMap<String, String>();
		bdinfo.put("device_type", device_type);
		bdinfo.put("device_id", device_id);
		bdinfo.put("openid", openid);
		bdinfo.put("content", content);
		String json = CJSON.HashMap2Json(bdinfo, false);
		String url = TransMsgUrl.replace("ACCESS_TOKEN", WXUtil.getTonken(appid));
		String rst = WXHttps.postHttps(url, null, json);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rst);
		if ("0".equalsIgnoreCase(rootNode.path("ret").asText())) {
			return true;
		} else
			throw new Exception("发送设备信息错误:" + rootNode.path("errmsg").asText());
		// {"ret":0,"ret_info":"this is ok"}
		// {"errcode":-1,"errmsg":""}
		// System.out.println("transMsg rst=" + rst);
		// return rst;
	}

	/**
	 * 根据设备id获取二维码生成串
	 * 
	 * @throws Exception
	 */
	public static String createQrcode(String appid, List<String> deviceIds) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("device_num", String.valueOf(deviceIds.size()));
		map.put("device_id_list", deviceIds.toString());
		String url = CreateQrcode.replace("ACCESS_TOKEN", WXUtil.getTonken(appid));
		String json = CJSON.HashMap2Json(map, false);
		String rst = WXHttps.postHttps(url, null, json);
		System.out.println("createQrcode rst=" + rst);
		return rst;
	}

	/**
	 * 批量授权/更新设备属性
	 * <p>
	 * 授权后设备才能进行绑定操作
	 * 
	 * @param devices
	 *            设备属性列表
	 * @param isCreate
	 *            是否首次授权： true 首次授权； false 更新设备属性
	 * @throws Exception
	 */
	public static String authorize(String appid, List<DeviceAuth> devices, boolean isCreate) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		jg.writeNumberField("device_num", devices.size());
		jg.writeNumberField("op_type", isCreate ? 0 : 1);
		jg.writeArrayFieldStart("device_list");
		for (DeviceAuth dev : devices) {
			jg.writeRawValue(objectMapper.writeValueAsString(dev));
		}
		jg.writeEndArray();
		jg.writeEndObject();
		jg.close();
		String json = baos.toString("utf-8");
		String url = AuthorizeUrl.replace("ACCESS_TOKEN", WXUtil.getTonken(appid));
		String rst = WXHttps.postHttps(url, null, json);
		System.out.println("AuthorizeUrl rst=" + rst);
		return rst;
	}

	/**
	 * 设备状态查询
	 * <p>
	 * status 0：未授权 1：已经授权（尚未被用户绑定） 2：已经被用户绑定<br/>
	 * {"errcode":0,"errmsg":"ok","status":1,"status_info":"authorized"}
	 * 
	 * @throws Exception
	 */
	public static String getStat(String appid, String deviceId) throws Exception {
		String url = GetStatUrl.replace("DEVICE_ID", deviceId);
		String rst = WXHttps.getHttps(url, null);
		System.out.println("getStat rst=" + rst);
		return rst;
	}

	/**
	 * 验证二维码 获取二维码对应设备属性
	 * 
	 * @param ticket
	 *            二维码生成串
	 * @throws Exception
	 */
	public static String verifyQrcode(String appid, String ticket) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("ticket", ticket);
		String json = CJSON.HashMap2Json(map, false);
		String rst = WXHttps.postHttps(VerifyQrcodeUrl, null, json);
		System.out.println("AuthorizeUrl rst=" + rst);
		return rst;
	}

	/**
	 * 根据设备类型和设备id查询绑定的openid
	 * 
	 * @throws Exception
	 */
	public static String getOpenId(String appid, String deviceType, String deviceId) throws Exception {
		String url = GetOpenidUrl.replace("DEVICE_TYPE", deviceType).replace(
				"DEVICE_ID", deviceId).replace("ACCESS_TOKEN", WXUtil.getTonken(appid));
		String rst = WXHttps.getHttps(url, null);
		System.out.println("getOpenId rst=" + rst);
		return rst;
	}

	/**
	 * 设备授权
	 * 
	 * @param authKey
	 *            加密key
	 * @param deviceId
	 *            设备id
	 * @param mac
	 *            设备的mac地址
	 * @param 是否首次授权
	 *            ： true 首次授权； false 更新设备属性
	 */
	public static void device_Auth(String appid, String authKey, String deviceId, String mac,
			boolean isCreate) throws Exception {
		DeviceAuth device = new DeviceAuth();
		device.setId(deviceId); // 设备id
		device.setMac(mac); //
		device.setConnect_protocol("1|2");
		device.setAuth_key(authKey); // 加密key 1234567890ABCDEF1234567890ABCDEF
		device.setCrypt_method("0"); // auth加密方法 0：不加密 1：AES加密
		device.setAuth_ver("0"); // 0：不加密的version 1：version 1
		device.setConn_strategy("1"); // 连接策略
		device.setClose_strategy("2");
		device.setManu_mac_pos("-1");
		device.setSer_mac_pos("-2");

		// 调用授权
		List<DeviceAuth> auths = new ArrayList<DeviceAuth>();
		auths.add(device);

		String rst = authorize(appid, auths, isCreate);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rst).findPath("resp");
		for (int i = 0; i < rootNode.size(); i++) {
			JsonNode node = rootNode.get(i);
			System.out.println("node:" + node.toString());
			int errcode = node.findPath("errcode").asInt();
			if ((errcode == 100002) || (errcode == 0)) {
				String deviceid = node.findPath("base_info").findPath("device_id").asText();
				String device_type = node.findPath("base_info").findPath("device_type").asText();
				Wx_device dvc = new Wx_device();
				dvc.findBySQL("select * from wx_device where entid=1 and device_id='" + deviceid + "'");
				if (dvc.isEmpty()) {
					dvc.creator.setValue("SYSTEM");
					dvc.create_time.setAsDatetime(new Date());
					dvc.entid.setAsInt(1);
				}
				dvc.appid.setValue(appid);
				dvc.device_id.setValue(deviceid);
				dvc.qrticket.setValue(device_type);
				dvc.device_type.setAsInt(1);
				dvc.save();
			} else
				Logsw.error("设备【" + deviceId + "】授权错误:" + node.findPath("errmsg").asText());
		}

		// {"resp":[{"base_info":{"device_type":"gh_1bafe245c2cb","device_id":
		// "gh_1bafe245c2cb_9e081608d6d62b984edf52d5d3a50aba"},"errcode":0,"errmsg":"ok"}]}
	}

	// new ______________________________________________________

	// 新 接口 获取设备
	public static boolean getNewDeviceID(String appid, int ct, String productid) throws Exception {
		// String staticQStr = "http://we.qq.com/d/";
		if ((ct <= 0) || (ct > 100)) {
			ct = 1;
		}
		String token = WXUtil.getTonken(appid);
		String url = "https://api.weixin.qq.com/device/getqrcode?access_token=" + token + "&product_id=" + productid;
		for (int i = 0; i < ct; i++) {
			newdevice4wx(appid, url);
		}
		return true;
	}

	private static void newdevice4wx(String appid, String url) throws Exception {
		String rst = WXHttps.getHttps(url, null);
		// {resp_msg:{"ret_code":0," error_info":"ok"},"deviceid":"XXX","qrticket":"XXX"}
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rst);
		JsonNode rmsg = rootNode.findPath("ret_code");
		// if ("ok".equalsIgnoreCase(rmsg.findPath("error_info").asText())) {
		String deviceid = rootNode.findPath("deviceid").asText();
		String qrticket = rootNode.findPath("qrticket").asText();
		Wx_device dvc = new Wx_device();
		dvc.appid.setValue(appid);
		dvc.device_id.setValue(deviceid);
		dvc.qrticket.setValue(qrticket);
		dvc.creator.setValue("SYSTEM");
		dvc.device_type.setAsInt(1);
		dvc.create_time.setAsDatetime(new Date());
		dvc.entid.setAsInt(1);
		dvc.save();
		// }
	}

	// 绑定设备 js调用
	public static boolean bind(String appid, String ticket, String device_id, String openid) throws Exception {
		String token = WXUtil.getTonken(appid);
		String url = "https://api.weixin.qq.com/device/bind?access_token=" + token;
		return bingorunbind(url, ticket, device_id, openid);
	}

	// 解绑设备 js调用
	public static boolean unbind(String appid, String ticket, String device_id, String openid) throws Exception {
		String token = WXUtil.getTonken(appid);
		String url = "https://api.weixin.qq.com/device/unbind?access_token=" + token;
		return bingorunbind(url, ticket, device_id, openid);
	}

	// 强制绑定
	public static boolean compel_bind(String appid, String device_id, String openid) throws Exception {
		String token = WXUtil.getTonken(appid);
		String url = "https://api.weixin.qq.com/device/compel_bind?access_token=" + token;
		return bingorunbind(url, null, device_id, openid);
	}

	// 强制解绑
	public static boolean compel_unbind(String appid, String device_id, String openid) throws Exception {
		String token = WXUtil.getTonken(appid);
		String url = "https://api.weixin.qq.com/device/compel_unbind?access_token=" + token;
		return bingorunbind(url, null, device_id, openid);
	}

	private static boolean bingorunbind(String url, String ticket, String device_id, String openid) throws Exception {
		HashMap<String, String> bdinfo = new HashMap<String, String>();
		if (ticket != null)
			bdinfo.put("ticket", ticket);
		bdinfo.put("device_id", device_id);
		bdinfo.put("openid", openid);
		String json = CJSON.HashMap2Json(bdinfo, false);
		String rst = WXHttps.postHttps(url, null, json);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rst);
		return ("ok".equalsIgnoreCase(rootNode.findPath("base_resp").findPath("errmsg").asText())) ? true : false;
	}

}
