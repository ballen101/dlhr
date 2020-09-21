package com.corsair.server.htmlex;

import java.net.InetAddress;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.corsair.server.base.CSContext;
import com.corsair.server.html.CcomUrl;
import com.corsair.server.util.HttpTookit;

public class CHtmlJSRemoteCall {

	public static String parComUrls(List<CcomUrl> comurls, Scriptable scope) throws Exception {
		HttpServletRequest request = CSContext.getRequest();
		String basePath = null;
		if (request != null) {
			basePath = request.getScheme() + "://" + request.getServerName();
			if (request.getServerPort() != 80)
				basePath = basePath + ":" + request.getServerPort();
			basePath = basePath + request.getContextPath();
		} else {
			InetAddress ia = InetAddress.getLocalHost();
			// basePath = "http://" + ia.getHostAddress() + ":" + CServletProperty.getServerPort("http") + "/" + CServletProperty.getContextPath();
			basePath = "http://127.0.0.1:8080/dlhr";
		}

		Object fobj = scope.get("comUrls", scope);
		if ((fobj == null) || (fobj.equals(Scriptable.NOT_FOUND)))
			return null;

		if (!(fobj instanceof NativeArray))
			throw new Exception("comUrls变量必须为JS数组");
		NativeArray comUrls = (NativeArray) fobj;
		HttpTookit ht = new HttpTookit();

		// Shwuser admin = Login.getAdminUser();
		// String username = admin.username.getValue();
		// String userpass = admin.userpass.getValue();
		String username = "DEV";
		String userpass = "Q1W2E3R4";
		String rst = ht.doGet(basePath + "/web/login/dologin.co?username=" + username + "&userpass=" + userpass + "&version=1.1", null);
		String jsfuncs = "";
		for (Object jo : comUrls) {
			NativeObject o = (NativeObject) jo;
			String index = o.get("index").toString();
			String type = o.get("type").toString();
			boolean multiple = (o.get("multiple") == null) ? false : Boolean.valueOf(o.get("multiple").toString());
			String url = o.get("url").toString();
			String valueField = o.get("valueField").toString();
			String textField = o.get("textField").toString();

			if (url.startsWith("/"))
				url = basePath + url;
			else
				url = basePath + "/" + url;

			boolean iswie = (o.get("iswie") == null) ? false : Boolean.valueOf(o.get("iswie").toString());
			// System.out.println(index + " iswie:" + iswie);
			// if (o.get("iswie") != null)
			// System.out.println("iswie:" + o.get("iswie").toString());
			CcomUrl co = new CcomUrl(ht, index, type, multiple, url, valueField, textField);
			comurls.add(co);
			if (!iswie)
				jsfuncs = jsfuncs + "\n" + co.fetchJSFunction();

		}
		return jsfuncs;
	}
}
