package com.corsair.server.htmlex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.corsair.server.base.ConstsSw;
import com.corsair.server.html.CcomUrl;
import com.corsair.server.html.htmlUtil;

public class CHtmlTemplatEx {

	public static void buildHtml(String srcfname) throws Exception {
		Document srcDoc = meargHtml(srcfname);
		List<CcomUrl> curls = chgHtmlWFMLATT(srcDoc);
		CHtmlBuild.ParHtmlInput(srcDoc, curls);
		String newhtml = srcDoc.html();
		newhtml = newhtml.replace("&lt;", "<");
		newhtml = newhtml.replace("&gt;", ">");
		newhtml = newhtml.replace("&amp;", "&");

		try {
			String outfname = "D:\\MyWorks2\\zy\\webservice\\tomcat71\\webapps\\dlhr\\webapp\\hr_perm\\aa.html";
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outfname), "UTF-8");
			out.write(newhtml);
			out.flush();
			out.close();
		} catch (Exception e) {
		}
	}

	/**
	 * @param tempfname
	 * @param srcfname
	 * @throws Exception
	 */
	public static Document meargHtml(String srcfname) throws Exception {
		// 文件分隔符替换
		// System.out.println("11111111111111:" + srcfname);
		File srcf = new File(srcfname);
		Document srcDoc = Jsoup.parse(srcf, "UTF-8");
		Element srcbody = srcDoc.getElementsByTag("body").get(0);
		Element srchtml = srcDoc.getElementsByTag("html").get(0);
		String tpfname = "D:\\MyWorks2\\zy\\webservice\\tomcat71\\webapps\\dlhr\\webapp\\templet\\default\\main_line_pop.html";
		// gettemplatefname(srchtml.attr("template"));//
		if (tpfname != null) {
			File tmpFile = new File(tpfname);
			if (!tmpFile.exists() || !tmpFile.isFile()) {
				throw new Exception("模板文件【" + tpfname + "】不存在，或不是文件");
			}
			Document tmpDoc = Jsoup.parse(tmpFile, "UTF-8");
			Element divNode = tmpDoc.getElementById("main_form_div_id");
			if (divNode != null) {
				Element et = srcbody.getElementById("maindata_id");
				if (et == null) {
					throw new Exception("HTML源文件【" + srcf.getName() + "】中ID为【maindata_id】的DOM不存在");
				}
				divNode.html(et.toString());
			}
			srcbody.getElementById("maindata_id").remove();
			srcbody.append(tmpDoc.getElementsByTag("body").get(0).html());
		} else {
		}
		return srcDoc;
	}

	private static String getOutFileName(String srcfname, String simplesrcfname, String wkname) throws Exception {
		String fwkname = wkname;
		if ((fwkname == null) || (fwkname.isEmpty())) {
			throw new Exception("文件【" + srcfname + "】没有设置输出路径！");
		}
		String fsep = System.getProperty("file.separator");
		fwkname = fwkname.replace("\\", fsep);
		fwkname = fwkname.replace("/", fsep);

		if (fwkname.substring(0, 1).equals(fsep))
			fwkname = fwkname.substring(1);
		if (!fwkname.substring(fwkname.length() - 1).equals(fsep))
			fwkname = fwkname + fsep;

		String dirname = ConstsSw._root_filepath + fwkname;
		File file = new File(dirname);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		fwkname = dirname + simplesrcfname;

		if (fwkname.equalsIgnoreCase(srcfname)) {
			throw new Exception("文件【" + srcfname + "】有相同的输出文件路径！");
		}
		return fwkname;
	}

	private static String gettemplatefname(String tempfname) throws Exception {
		String ftempfname = tempfname;
		if ((ftempfname == null) || (ftempfname.isEmpty())) {
			return null;
			// throw new Exception("文件【" + srcfname + "】没有设置模板文件！");
		}
		String fsep = System.getProperty("file.separator");
		ftempfname = ftempfname.replace("\\", fsep);
		ftempfname = ftempfname.replace("/", fsep);
		if (ftempfname.substring(0, 1).equals(fsep))
			ftempfname = ftempfname.substring(1);
		return ConstsSw._root_filepath + ftempfname;
	}

	public static List<CcomUrl> chgHtmlWFMLATT(Document srcDoc) throws Exception {
		List<CcomUrl> rst = new ArrayList<CcomUrl>();
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
			fcs = CHtmlJSRemoteCall.parComUrls(rst, scope);
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
		return rst;
	}

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
