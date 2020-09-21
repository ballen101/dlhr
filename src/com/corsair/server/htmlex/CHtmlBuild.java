package com.corsair.server.htmlex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.corsair.server.html.CcomUrl;

public class CHtmlBuild {

	//////////
	public static void ParHtmlInput(Document srcDoc, List<CcomUrl> curls) throws Exception {
		Context cx = Context.enter();
		try {
			Map<String, Element> labs = new HashMap<String, Element>();
			Elements labeles = srcDoc.select("td[cjoptions]");
			for (Element e : labeles) {
				String cjoptions = e.attr("cjoptions");
				String[] mps = cjoptions.split(":");
				if (mps.length == 2) {
					String pname = mps[0];
					String pvalue = mps[1];
					if ((pname != null) && (!pname.isEmpty())) {
						if ("fdname".equalsIgnoreCase(pname)) {
							labs.put(pvalue.replace("'", ""), e);
						}
					}
				}
			}

			Elements inputets = srcDoc.select("input[cjoptions],select[cjoptions]");
			for (Element e : inputets) {
				boolean required = false;
				boolean readonly = false;
				String fdname = null;
				String cjoptions = e.attr("cjoptions");
				String[] ps = cjoptions.split(",");
				for (String p : ps) {
					String[] mps = p.split(":");
					if (mps.length == 2) {
						String pname = mps[0];
						String pvalue = mps[1];
						if ((pname != null) && (!pname.isEmpty())) {
							if ("readonly".equalsIgnoreCase(pname)) {
								readonly = Boolean.valueOf(pvalue);
								// System.out.println("readonly:" + Boolean.valueOf(pvalue));
							}
							if ("fdname".equalsIgnoreCase(pname)) {
								// System.out.println("fdname:" + pvalue.replace("'", ""));
								fdname = pvalue.replace("'", "");
							}
							if ("crequired".equalsIgnoreCase(pname)) {
								required = Boolean.valueOf(pvalue);
							}
							if ("comidx".equalsIgnoreCase(pname)) {
								String code = "comUrl_" + pvalue.replace("'", "") + ".type";
								String idx = pvalue.replace("'", "");
								CcomUrl cu = getCoByIdx(curls, idx);
								if (cu == null)
									throw new Exception(idx + "不存在");
								if ("combobox".equalsIgnoreCase(cu.getType())) {
									cjoptions = cjoptions + ",valueField:'" + cu.getValueField() + "',textField:'" + cu.getTextField() + "',data:" + "comUrl_" + idx + ".jsondata";
								}
							}
							if ("easyui_class".equalsIgnoreCase(pname)) {
								e.addClass(pvalue.replace("'", "").trim());
							}
						}
					}
				}
				e.attr("data-options", cjoptions);
				e.removeAttr("cjoptions");
				if (required) {
					// System.out.println(fdname + "-required-" + required);
					Element labe = labs.get(fdname);
					if (labe != null) {
						labe.html(labe.html() + "(<span style='color: red'>*</span>)");
					}
				}
				if (readonly) {

				}
			}
		} finally {
			Context.exit();
		}
	}

	private static CcomUrl getCoByIdx(List<CcomUrl> curls, String idx) {
		for (CcomUrl cu : curls) {
			if (idx.equalsIgnoreCase(cu.getIndex()))
				return cu;
		}
		return null;
	}

}
