package com.corsair.server.genco;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.GetNewSEQID;
import com.corsair.server.weixin.WXUtil;

@ACO(coname = "web.system")
public class COSystemMangent {
	@ACOAction(eventname = "getdblist", notes = "获取数据库列表", Authentication = true)
	public String getDataBaseList() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartArray();
		for (CDBPool pool : DBPools.getDbpools()) {
			jg.writeStartObject();
			jg.writeStringField("poolname", pool.pprm.name);
			jg.writeEndObject();
		}
		jg.writeEndArray();
		jg.close();
		return baos.toString("utf-8");
	}

	@ACOAction(eventname = "getWxAppList", notes = "获取微信APPID列表", ispublic = true, Authentication = true)
	public String getWxAppList() throws Exception {
		String fsep = System.getProperty("file.separator");
		String dirroot = ConstsSw._root_filepath + "WEB-INF" + fsep + "conf" + fsep + "wxmenu" + fsep;
		File d = new File(dirroot);
		List<HashMap<String, String>> fs = new ArrayList<HashMap<String, String>>();
		for (File f : d.listFiles()) {
			HashMap<String, String> fh = new HashMap<String, String>();
			String filename = f.getName();
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				String extf = filename.substring(dot + 1);
				if ((extf != null) && (extf.equalsIgnoreCase("json"))) {// 只处理JSON文件
					filename = filename.substring(0, dot);
					fh.put("appid", filename);
					fs.add(fh);
				}
			}

		}
		return CJSON.List2JSON(fs);
	}

	@ACOAction(eventname = "resetseq", notes = "重置序列", Authentication = true)
	public String resetseq() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String poolname = parms.get("poolname");
		if ((poolname == null) || (poolname.isEmpty())) {
			throw new Exception("poolname不能为空 ");
		}

		GetNewSEQID.resetSeq(poolname);
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "createwxmenu", notes = "创建微信菜单", Authentication = true)
	public String createWXMenu() throws Exception {
		String appid = CSContext.getParms().get("appid");
		if ((appid == null) || (appid.isEmpty())) {
			throw new Exception("appid不能为空 ");
		}
		WXUtil.createMenu(appid);
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "testInitHtml", notes = "初始化页面", Authentication = false)
	public String testInitHtml() throws Exception {
		// CHtmlTemplateJS.testHtmlInit();
		return "{\"result\":\"OK\"}";
	}
}
