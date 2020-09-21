package com.corsair.server.baiduueditor.co;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.server.baiduueditor.ActionMap;
import com.corsair.server.baiduueditor.AppInfo;
import com.corsair.server.baiduueditor.BaseState;
import com.corsair.server.baiduueditor.ConfigManager;
import com.corsair.server.baiduueditor.State;
import com.corsair.server.baiduueditor.Uploader;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shworgfile;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;

/**
 * edditror co
 * 
 * @author Administrator
 *
 */
@ACO(coname = "baidu.ueditor")
public class CUeditor {
	// config uploadimage
	/**
	 * ueeditor 接口
	 * 
	 * @return
	 * @throws Exception
	 */
	@ACOAction(eventname = "controller", Authentication = true, notes = "许可", ispublic = true)
	public String controller() throws Exception {
		HashMap<String, String> urlparms = CSContext.getParms();
		String action = CorUtil.hashMap2Str(urlparms, "action", "需要参数action");
		//System.out.println("controller_action:" + action);
		if (action == null || !ActionMap.mapping.containsKey(action)) {
			return new BaseState(false, AppInfo.INVALID_ACTION).toJSONString();
		}
		if (ConfigManager.configManager == null)
			ConfigManager.configManager = ConfigManager.getInstance();
		if (ConfigManager.configManager == null || !ConfigManager.configManager.valid()) {
			return new BaseState(false, AppInfo.CONFIG_ERROR).toJSONString();
		}
		int actionCode = ActionMap.getType(action);
		return execaction(actionCode);
	}

	private String execaction(int actionCode) {
		//System.out.println("actionCode:" + actionCode);
		State state = null;
		Map<String, Object> conf = ConfigManager.configManager.getConfig(actionCode);
		switch (actionCode) {
		case ActionMap.CONFIG:
			return getconfig();
		case ActionMap.UPLOAD_SCRAWL:
		case ActionMap.UPLOAD_VIDEO:
		case ActionMap.UPLOAD_FILE:
		case ActionMap.UPLOAD_IMAGE:
			state = Uploader.doupdate(conf, actionCode);
			break;

		case ActionMap.CATCH_IMAGE:
			// String[] list = this.request.getParameterValues((String) conf.get("fieldName"));
			// state = new ImageHunter(conf).capture(list);
			break;

		case ActionMap.LIST_IMAGE:
			String rst = getFileList();
			return rst;
		case ActionMap.LIST_FILE:
			break;
		}

		return state.toJSONString();

	}

	private String getFileList() {
		try {
			HashMap<String, String> urlparms = CSContext.getParms();
			String orgid = CorUtil.hashMap2Str(urlparms, "orgid", "需要参数orgid");
			String start = CorUtil.hashMap2Str(urlparms, "start");
			Shworg org = new Shworg();
			org.findByID(orgid);
			if (org.isEmpty())
				throw new Exception("机构不存在");
			if ((start == null) || (start.isEmpty()))
				start = "1";
			Shworgfile of = new Shworgfile();
			String sqlstr = "SELECT * FROM shworgfile WHERE idpath LIKE '" + org.idpath.getValue() + "%'";
			sqlstr = sqlstr + " and ptype=1";

			JSONObject jo = of.pool.opensql2json_O(sqlstr, Integer.valueOf(start), 20);
			JSONObject rst = new JSONObject();
			rst.put("state", "SUCCESS");
			rst.put("total", jo.getInt("total"));
			rst.put("start", jo.getInt("page"));
			JSONArray list = new JSONArray();
			for (int i = 0; i < jo.getJSONArray("rows").size(); i++) {
				JSONObject r = jo.getJSONArray("rows").getJSONObject(i);
				String pfid = r.getString("pfid");
				JSONObject jr = new JSONObject();
				jr.put("pfid", pfid);
				list.add(jr);
			}
			rst.put("list", list);
			return rst.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return (new BaseState(false, e.getMessage())).toJSONString();
		}

	}

	private String getconfig() {
		String rst = ConfigManager.configManager.getAllConfig().toString();
		//System.out.println("uedit config:" + rst);
		return rst;
	}
}
