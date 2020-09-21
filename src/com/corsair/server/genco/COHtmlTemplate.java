package com.corsair.server.genco;

import java.util.HashMap;
import java.util.List;

import com.corsair.dbpool.util.CJSON;
import com.corsair.server.base.CSContext;
import com.corsair.server.html.CHtmlTemplate;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;

/**
 * HTML模板CO
 * 
 * @author Administrator
 *
 */
@ACO(coname = "web.htmltem")
public class COHtmlTemplate {

	@ACOAction(eventname = "getTmfTree", Authentication = false)
	public String getTmfTree() throws Exception {
		return CHtmlTemplate.getSrc2JsonTree();
	}

	@ACOAction(eventname = "reBuildHtmls", Authentication = false)
	public String reBuildHtmls() throws Exception {
		HashMap<String, String> pparms = CSContext.parPostDataParms();
		String nodestr = pparms.get("node");
		String rootpath = null;
		if (nodestr != null) {
			HashMap<String, String> node = CJSON.Json2HashMap(nodestr);
			rootpath = node.get("path");
		}

		List<String> fnnames = CHtmlTemplate.getSrcList(rootpath);
		for (String s : fnnames) {
			CHtmlTemplate.buildHtml(s);
		}
		return "{\"result\":\"OK\"}";
	}

}
