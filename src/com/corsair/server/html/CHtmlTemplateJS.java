package com.corsair.server.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.corsair.server.base.CSContext;
import com.corsair.server.base.Login;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.util.CServletProperty;
import com.corsair.server.util.HttpTookit;

public class CHtmlTemplateJS {

	public static void testHtmlInit(String srcfname) throws Exception {
		File srcf = new File(srcfname);
		Document srcDoc = Jsoup.parse(srcf, "UTF-8");
		Elements ets = srcDoc.getElementsByTag("script");
		Element fe = null;
		String s = "";
		for (Element et : ets) {
			if ("text/javascript".equalsIgnoreCase(et.attr("type")) && ("cserver_js").equalsIgnoreCase(et.attr("style"))) {
				if (fe == null)
					fe = et;
				s = s + et.html();
			}
		}
		String fcs = null;
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		try {
			cx.evaluateString(scope, s, null, 0, null);
			int FormType = htmlUtil.parIntParm(scope, "formtype", 1);
			boolean allowAtt = htmlUtil.parBoolParm(scope, "allowAtt", true);
			boolean allowWF = htmlUtil.parBoolParm(scope, "allowWF", true);
			setPageMailLine(srcDoc, FormType);
			setPageAllowWF(srcDoc, allowWF);
			setPageAllowAtt(srcDoc, allowAtt);
			fcs = CHtmlTemplateJS.parComUrls(scope);
			if (fcs == null)
				fcs = "";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.exit();
		}
		if (fe != null) {
			String s1 = fe.html();
			s1 = fcs + "\n" + s1;
			fe.text(s1);
		}

		String newhtml = srcDoc.html();
		newhtml = newhtml.replace("&lt;", "<");
		newhtml = newhtml.replace("&gt;", ">");
		newhtml = newhtml.replace("&amp;", "&");

		// newhtml = HtmlCompressor.compress(newhtml); //貌似内嵌js压缩有有问题

		// Logsw.debug(newhtml);
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(srcfname), "UTF-8");
			out.write(newhtml);
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new Exception("文件【" + srcfname + "】写入错误！");
		}

	}

	public static String parComUrls(Scriptable scope) throws Exception {
		HttpServletRequest request = CSContext.getRequest();
		String basePath = null;
		if (request != null) {
			basePath = request.getScheme() + "://" + request.getServerName();
			if (request.getServerPort() != 80)
				basePath = basePath + ":" + request.getServerPort();
			basePath = basePath + request.getContextPath();
		} else {
			InetAddress ia = InetAddress.getLocalHost();
			basePath = "http://" + ia.getHostAddress() + ":" + CServletProperty.getServerPort("http")
					+ "/" + CServletProperty.getContextPath();
		}

		Object fobj = scope.get("comUrls", scope);
		if ((fobj == null) || (fobj.equals(Scriptable.NOT_FOUND)))
			return null;

		if (!(fobj instanceof NativeArray))
			throw new Exception("comUrls变量必须为JS数组");
		NativeArray comUrls = (NativeArray) fobj;
		HttpTookit ht = new HttpTookit();

		Shwuser admin = Login.getAdminUser();
		String rst = ht.doGet(basePath + "/web/login/dologin.co?username=" + admin.username.getValue() + "&userpass=" + admin.userpass.getValue()
				+ "&version=1.1", null);
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
			if (!iswie)
				jsfuncs = jsfuncs + "\n" + co.fetchJSFunction();

		}
		return jsfuncs;
	}

	// public static void parlistGridColumns(Scriptable scope, Document srcDoc) throws Exception {
	// Object fobj = scope.get("listGridColumns", scope);
	// if (!(fobj instanceof NativeArray))
	// throw new Exception("listGridColumns变量必须为JS数组");
	// NativeArray comUrls = (NativeArray) fobj;
	// for (Object jo : comUrls) {
	// NativeObject o = (NativeObject) jo;
	// String field = o.get("field").toString();
	// String title = o.get("title").toString();
	// String width = o.get("width").toString();
	// if (o.get("formatter") != null) {
	// String formatter = o.get("formatter").toString();
	// }
	// if (o.get("editor") != null) {
	// String editor = o.get("editor").toString();
	// }
	// // System.out.println(field);
	// }
	// }

	public static void setPageMailLine(Document srcDoc, int formtype) {
		if (formtype == 2) {// simple form
			Element e = srcDoc.getElementById("main_form_div_id");
			if (e != null) {
				e.attr("data-options", "region:'center',border:false");
				e = srcDoc.getElementById("detail_main_layout_id");
				if (e != null)
					e.remove();
			}
		}
	}

	public static void setPageAllowWF(Document srcDoc, boolean allowWF) {
		if (!allowWF) {
			Element e = srcDoc.getElementById("main_tab_wf_id");
			if (e != null)
				e.remove();
		}
	}

	public static void setPageAllowAtt(Document srcDoc, boolean allowAtt) {
		if (!allowAtt) {
			Element e = srcDoc.getElementById("main_tab_att_id");
			if (e != null)
				e.remove();
		}
	}

}
