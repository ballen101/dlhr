package com.corsair.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shw_physic_file;

/**
 * 基于 httpclient 4.3.1版本的 http工具类
 * 
 */
public class HttpTookit {
	private CloseableHttpClient httpClient;
	public final String CHARSET = "UTF-8";

	public HttpTookit() {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(30000).build();
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

	public String doGet(String url, Map<String, String> params) {
		return doGet(url, params, CHARSET);
	}

	public String doPost(String url, Map<String, String> params) {
		return doPost(url, params, CHARSET);
	}

	/**
	 * HTTP Get 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public String doGet(String url, Map<String, String> params, String charset) {
		if ((url == null) || url.isEmpty()) {
			return null;
		}
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				String ps = EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
				if (url.indexOf("?") < 0)
					url += "?" + ps;
				else
					url += "&" + ps;
			}
			HttpGet httpGet = new HttpGet(url);
			Logsw.dblog("urlnew:" + url);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param url
	 * @param json
	 * @param headparams
	 *            httpres头参数
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public String doPostJSON(String url, Object json, Map<String, String> headparams, String charset) throws Exception {
		String vcharset = ((charset == null) || (charset.isEmpty())) ? "utf-8" : charset;
		String data = null;
		if (json instanceof JSONObject)
			data = ((JSONObject) json).toString();
		else if (json instanceof JSONArray) {
			data = ((JSONArray) json).toString();
		} else
			return null;
		return doPostJSON(url, data, headparams, vcharset);

	}

	public String doPostJSON(String url, String data, Map<String, String> headparams, String charset) throws Exception {
		String vcharset = ((charset == null) || (charset.isEmpty())) ? "utf-8" : charset;
		try {
			HttpPost httpPost = new HttpPost(url);
			if (headparams != null) {
				Iterator<?> iter = headparams.entrySet().iterator();
				while (iter.hasNext()) {
					@SuppressWarnings("unchecked")
					Map.Entry<String, String> entry = (Entry<String, String>) iter.next();
					String key = entry.getKey().toString();
					String val = entry.getValue().toString();
					httpPost.setHeader(key, val);
				}
			}

			StringEntity reqentity = new StringEntity(data, vcharset);// 解决中文乱码问题
			reqentity.setContentEncoding(vcharset);
			reqentity.setContentType("application/json");
			httpPost.setEntity(reqentity);

			CloseableHttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			statusCode = (int) (Math.floor(statusCode / 100) * 100);// 2**都算成功
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, vcharset);
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * http post json
	 * 
	 * @param url
	 * @param json
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public String doPostJSON(String url, Object json, String charset) throws Exception {
		return doPostJSON(url, json, null, charset);
	}

	/**
	 * HTTP Post 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public String doPost(String url, Map<String, String> params, String charset) {
		String vcharset = ((charset == null) || (charset.isEmpty())) ? "utf-8" : charset;
		if ((url == null) || url.isEmpty()) {
			return null;
		}
		try {
			List<NameValuePair> pairs = null;
			if (params != null && !params.isEmpty()) {
				pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
			}
			HttpPost httpPost = new HttpPost(url);
			if (pairs != null && pairs.size() > 0) {
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, vcharset));
			}
			CloseableHttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, vcharset);
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从网络缓存文件
	 * 
	 * @param strurl
	 * @param suffix
	 *            文件后缀 .jpg 等
	 *            null 自动根据content-type计算
	 * @return
	 * @throws Exception
	 */
	public Shw_physic_file getUrlFile(String strurl, String suffix) throws Exception {
		//System.out.println(strurl);
		HttpGet get = new HttpGet(strurl);
		HttpResponse resp = httpClient.execute(get);
		int code = resp.getStatusLine().getStatusCode();
		code = code / 100 * 100;// 按百位取整 有些网站 2** 都算成功
		if (code == 200) {
			if (suffix == null)
				suffix = CMIMEType.getSuffix(resp.getLastHeader("content-type").getValue());
			HttpEntity entity = resp.getEntity();
			if (null != entity) {
				FilePath fp = UpLoadFileEx.getFilePath();
				String fname = UpLoadFileEx.getNoReptFileName(suffix);// 数据库里面存的文件名
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
					pf.extname.setValue(suffix);
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
			} else
				throw new Exception("httpget 错误,NUll【" + code + "】");
		} else {
			throw new Exception("httpget 错误【" + code + "】");
		}
	}

	public static void main(String[] args) throws Exception {
		String url = "http://img.taopic.com/uploads/allimg/121014/234931-1210140JK414.jpg";
		// String url =
		// "http://thirdwx.qlogo.cn/mmopen/vi_32/MVKN0Nv3KnTIO0AFBtLIicjBiaibpImMAXRFSlMvOYCibdTxGibVn1IqrghHeHnDg8RmicSX3zzur3O5h9ibSUNlNnqHA/132";
		new HttpTookit().getUrlFile(url, ".jpg");
	}
}
