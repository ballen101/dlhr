package com.corsair.server.weixin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLContext;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.util.FilePath;
import com.corsair.server.util.UpLoadFileEx;

/**
 * 微信HTTPS类
 * 
 * @author Administrator
 *
 */
public class WXHttps {
	// {"errcode":41002,"errmsg":"appid missing"}
	public static String getHttps(String url, Map<String, String> parms) throws Exception {
		String strp = "";
		if (parms != null) {
			Iterator<String> it = parms.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				String value = parms.get(key);
				strp = strp + "&" + key + "=" + value;
			}
		}
		String strurl = url;
		if (!strp.isEmpty()) {
			if (strurl.indexOf("?") < 0)
				strurl = strurl + "?";
			strurl = strurl + strp.substring(1);
		}

		CloseableHttpClient httpclient = getsslHttpClient(null, null);
		HttpGet get = new HttpGet(strurl);
		HttpResponse resp = httpclient.execute(get);
		return getResStr(resp, strurl);
	}

	public static String postHttps(String url, Map<String, String> parms, String data, boolean needcert) throws Exception {
		String strp = "";
		if (parms != null) {
			Iterator<String> it = parms.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				String value = parms.get(key);
				strp = strp + "&" + key + "=" + value;
			}
		}
		String strurl = url;
		if (!strp.isEmpty()) {
			if (strurl.indexOf("?") < 0)
				strurl = strurl + "?";
			strurl = strurl + strp.substring(1);
		}
		CloseableHttpClient httpclient = getsslHttpClient(null, null);
		HttpPost httppost = new HttpPost(strurl);
		Logsw.debug("posturl:" + strurl);
		Logsw.debug("postData:" + data);
		httppost.addHeader("Content-Type", "text/json");
		httppost.setEntity(new StringEntity(data, "utf-8"));//
		HttpResponse resp = httpclient.execute(httppost);
		return getResStr(resp, strurl);
	}

	public static String postHttps(String url, Map<String, String> parms, String data) throws Exception {
		return postHttps(url, parms, data, false);
	}

	public static String postWXPayHttps(String url, String data, String certfile, String certpsw) throws Exception {
		CloseableHttpClient httpclient = getsslHttpClient(certfile, certpsw);
		HttpPost httppost = new HttpPost(url);
		Logsw.debug("posturl:" + url);
		Logsw.debug("postData:" + data);
		httppost.addHeader("Content-Type", "text/xml");
		httppost.setEntity(new StringEntity(data, "utf-8"));//
		HttpResponse resp = httpclient.execute(httppost);
		int code = resp.getStatusLine().getStatusCode();
		if (code == 200) {
			HttpEntity entity = resp.getEntity();
			if (null != entity) {
				return EntityUtils.toString(entity, "UTF-8");
			} else
				throw new Exception("httppost 错误,NUll【" + code + "】URL:" + url);
		} else {
			throw new Exception("httppost 错误【" + code + "】URL:" + url);
		}
	}

	public static String postWXPayHttps(String url, String data) throws Exception {
		return postWXPayHttps(url, data, null, null);
	}

	private static String getResStr(HttpResponse resp, String url) throws Exception {
		int code = resp.getStatusLine().getStatusCode();
		if (code == 200) {
			HttpEntity entity = resp.getEntity();
			if (null != entity) {
				String rst = EntityUtils.toString(entity, "UTF-8");
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rst);
				if (rootNode.has("errcode")) {
					JsonNode errnode = rootNode.findPath("errcode");
					int errcode = errnode.asInt();
					if (errcode == 0)
						return rst;
					else {
						String ermsg = WXErrCode.getErrMsg(errcode);
						if (ermsg == null)
							ermsg = rootNode.path("errmsg").asText();
						throw new Exception("httpget 错误【" + errcode + ":" + ermsg + "】");
					}
				} else
					return rst;
			} else
				throw new Exception("httpget 错误,NUll【" + code + "】");
		} else {
			throw new Exception("httpget 错误【" + code + "】");
		}
	}

	private static CloseableHttpClient getsslHttpClient(String certfile, String certpsw) throws Exception {
		SSLContext sslContext = null;
		if (certfile == null) {
			sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {// 信任所有
					return true;
				}
			}).build();
		} else {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new FileInputStream(new File(certfile)), certpsw.toCharArray());

			sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(keyStore, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).loadKeyMaterial(keyStore, certpsw.toCharArray()).build();
		}
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		return httpclient;
	}

	/**
	 * 新增临时素材
	 * 
	 * @param appid
	 * @param pfid
	 * @param type
	 *            媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
	 *            图片（image）: 1M，支持JPG格式
	 *            语音（voice）：2M，播放长度不超过60s，支持AMR\MP3格式
	 *            视频（video）：10MB，支持MP4格式
	 *            缩略图（thumb）：64KB，支持JPG格式
	 * @return
	 * @throws Exception
	 */
	public static String uploadWxTempFile(String appid, int pfid, String type) throws Exception {
		Shw_physic_file pf = new Shw_physic_file();
		pf.findByID(String.valueOf(pfid));
		if (pf.isEmpty())
			throw new Exception("ID为【" + pfid + "】的物理文件不存在");
		String fullname = UpLoadFileEx.getPhysicalFileName(pf);
		File file = new File(fullname);
		if ((!file.exists()) || (!file.isFile()))
			throw new Exception("文件" + fullname + "不存在!");
		return uploadWxTempFile(appid, file, type);
	}

	/**
	 * 新增临时素材
	 * 
	 * @param appid
	 * @param file
	 * @param type
	 *            媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
	 *            图片（image）: 1M，支持JPG格式
	 *            语音（voice）：2M，播放长度不超过60s，支持AMR\MP3格式
	 *            视频（video）：10MB，支持MP4格式
	 *            缩略图（thumb）：64KB，支持JPG格式
	 * @return media_id
	 * @throws Exception
	 */
	public static String uploadWxTempFile(String appid, File file, String type) throws Exception {
		checkUploadFile(file, type);
		String url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=" + WXUtil.getTonken(appid) + "&type=" + type;
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		FileBody bin = new FileBody(file);
		HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addPart("media", bin).build();
		httppost.setEntity(reqEntity);
		HttpResponse res = httpClient.execute(httppost);
		String rst = getResStr(res, url);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rst);
		if (rootNode.has("media_id"))
			return rootNode.path("media_id").getTextValue();
		else
			throw new Exception("上传文件返回值未发现期待的media_id参数");
		// {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
	}

	private static void checkUploadFile(File file, String type) throws Exception {
		if (!file.exists())
			throw new Exception("上传的文件不存在");
		String fileName = file.getName();
		System.out.println("fileName:" + fileName);
		String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
		float size = file.length() / 1024;

		if (type.equals("image")) {
			if (!("jpg".equalsIgnoreCase(prefix) || "jpeg".equalsIgnoreCase(prefix)))
				throw new Exception("上传的图片文件只支持JPG格式");
			if (size > 1024) {
				throw new Exception("上传的图片文件不能大于1M");
			}
		} else if (type.equals("voice")) {
			if (!("AMR".equalsIgnoreCase(prefix) || "MP3".equalsIgnoreCase(prefix)))
				throw new Exception("上传的声音文件只支持ARM/MP3格式");
			if (size > 2 * 1024) {
				throw new Exception("上传的声音文件不能大于2M");
			}
		} else if (type.equals("video")) {
			if (!"MP4".equalsIgnoreCase(prefix))
				throw new Exception("上传的video文件只支持MP4格式");
			if (size > 10 * 1024) {
				throw new Exception("上传的video文件不能大于10M");
			}
		} else if (type.equals("thumb")) {
			if (!("jpg".equalsIgnoreCase(prefix) || "jpeg".equalsIgnoreCase(prefix)))
				throw new Exception("上传的图片文件只支持JPG格式");
			if (size > 64) {
				throw new Exception("上传的图片文件不能大于64K");
			}
		}
	}

	private static boolean isCanLoad(String ct) {
		if (ct.indexOf("image/jpeg") >= 0)
			return true;
		if (ct.indexOf("audio/amr") >= 0)
			return true;
		return false;
	}

	private static String getExtFileName(String ct) {
		if (ct.indexOf("image/jpeg") >= 0)
			return ".jpg";
		if (ct.indexOf("audio/amr") >= 0)
			return ".amr";
		return "";
	}

	/**
	 * 获取临时素材
	 * 
	 * @param appid
	 * @param media_id
	 * @return
	 * @throws Exception
	 */
	public static Shw_physic_file dowloadloadWxTempFile(String appid, String media_id) throws Exception {
		String strurl = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + WXUtil.getTonken(appid) + "&media_id=" + media_id;
		System.out.println(strurl);
		CloseableHttpClient httpclient = getsslHttpClient(null, null);
		HttpGet get = new HttpGet(strurl);
		HttpResponse resp = httpclient.execute(get);
		int code = resp.getStatusLine().getStatusCode();
		if (code == 200) {
			HttpEntity entity = resp.getEntity();
			if (null != entity) {
				String ct = entity.getContentType().toString().toLowerCase();
				System.out.println(ct); // audio/amr
				if (isCanLoad(ct)) {
					FilePath fp = UpLoadFileEx.getFilePath();
					String extfname = getExtFileName(ct);// ".jpg";
					String fname = UpLoadFileEx.getNoReptFileName(extfname);// 数据库里面存的文件名
					File f = new File(fp.FilePath + fname);
					FileOutputStream output = null;
					InputStream input = null;
					try {
						output = new FileOutputStream(f);
						input = entity.getContent();
						byte b[] = new byte[1024];
						int j = 0;
						while ((j = input.read(b)) != -1) {
							output.write(b, 0, j);
						}
						String un = (CSContext.getUserNameEx() == null) ? "SYSTEM" : CSContext.getUserNameEx();
						DecimalFormat df = new DecimalFormat("0.##");
						Shw_physic_file pf = new Shw_physic_file();
						pf.ppath.setValue(fp.aPath);
						pf.pfname.setValue(fname);
						pf.create_time.setAsDatetime(new Date());
						pf.creator.setValue(un);
						pf.displayfname.setValue(fname);
						pf.filesize.setValue(df.format(f.length() / (1024f)));// K
						pf.extname.setValue(extfname);
						pf.fsid.setAsInt(0);
						pf.setJpaStat(CJPAStat.RSINSERT);
						pf.save();
						return pf;
					} finally {
						if (input != null)
							input.close();
						if (output != null) {
							output.flush();
							output.close();
						}
					}
				} else if (ct.indexOf("application/json") >= 0) {
					JSONObject jo = JSONObject.fromObject(EntityUtils.toString(entity, "UTF-8"));
					if (jo.has("errcode")) {
						throw new Exception("错误【" + jo.getString("errcode") + "】【" + jo.getString("errmsg") + "】");
					} else {
						if (jo.has("video_url")) {
							throw new Exception("获取到视频素材了，还不支持哈哈,自己去下载把【" + jo.getString("video_url") + "】");
						} else
							throw new Exception("错误,啥意思?【" + jo.toString() + "】");
					}
				} else {
					throw new Exception("错误,ContentType啥意思?【" + ct + "】");
				}
			} else
				throw new Exception("httpget 错误,NUll【" + code + "】");
		} else {
			throw new Exception("httpget 错误【" + code + "】");
		}
	}
}
