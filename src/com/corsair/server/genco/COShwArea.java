package com.corsair.server.genco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shwarea;
import com.corsair.server.generic.Shwcity;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;

@ACO(coname = "web.shwarea")
public class COShwArea {
	@ACOAction(eventname = "findAreaPath", Authentication = true, ispublic = true)
	public String findAreaPath() throws Exception {
		String areaid = CSContext.getParms().get("areaid");
		if ((areaid == null) || areaid.isEmpty())
			throw new Exception("需要areaid参数");
		List<String> areas = new ArrayList<String>();
		Shwarea area = new Shwarea();
		area.findByID(areaid);
		if (area.isEmpty() || (area.areaid.getAsIntDefault(0) == 1000))
			throw new Exception("没有找到ID为：" + areaid + "的区域资料");
		areas.add(area.tojson());// 3级别
		area.findByID(area.superid.getValue());
		if (!area.isEmpty() && (area.areaid.getAsIntDefault(0) != 1000)) {
			areas.add(area.tojson());
			area.findByID(area.superid.getValue());
			if (!area.isEmpty() && (area.areaid.getAsIntDefault(0) != 1000)) {
				areas.add(area.tojson());
			}
		}

		String rst = "[";
		for (int i = areas.size() - 1; i >= 0; i--) {
			String sa = areas.get(i);
			rst = rst + sa + ",";
		}
		rst = rst.substring(0, rst.length() - 1) + "]";
		return rst;
	}

	@ACOAction(eventname = "areas", Authentication = false, ispublic = true)
	public String getareas() throws Exception {
		String pid = CSContext.getParms().get("pid");
		if (pid == null) {
			pid = "1000";
		}
		String sqlstr = "select * from shwarea where superid=" + pid;

		return (new CReport(sqlstr, null)).findReport(null, null); // 18-01-04 其它条件无法定位BUG修正

		// CJPALineData<Shwarea> shareas = new CJPALineData<Shwarea>(Shwarea.class);
		// shareas.findDataBySQL(sqlstr, true, false);
		// return shareas.tojson();
	}

	private boolean hasChild(String areaid) throws Exception {
		String sqlstr = "SELECT count(*) ct FROM shwarea WHERE superid = " + areaid;
		return Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct")) > 0;
	}

	@ACOAction(eventname = "getareagtree", Authentication = false, ispublic = true)
	public String getareagtree() throws Exception {
		HashMap<String, String> urlparms = CSContext.getParms();
		String sqlstr = null;
		String id = urlparms.get("id");
		if (id == null) {
			sqlstr = "select * from shwarea where superid=1000";
		} else {
			sqlstr = "select * from shwarea where superid=" + id;
		}
		JSONArray areas = DBPools.defaultPool().opensql2json_O(sqlstr);
		for (int i = 0; i < areas.size(); i++) {
			JSONObject area = areas.getJSONObject(i);
			area.put("state", hasChild(area.getString("areaid")) ? "closed" : "open");
		}
		return areas.toString();
	}

	@ACOAction(eventname = "getallareagtree", Authentication = false, ispublic = true)
	public String getallareagtree() throws Exception {
		HashMap<String, String> urlparms = CSContext.getParms();
		String sqlstr = null;
		sqlstr = "select * from shwarea ";
		return DBPools.defaultPool().opensql2jsontree(sqlstr, "areaid", "superid", false);
	}

	@ACOAction(eventname = "getCityFirstPYTree", Authentication = false, ispublic = true, notes = "按首字母获取城市树")
	public String getCityFirstPYTree() throws Exception {
		Shwcity c = new Shwcity();
		String sqlstr = "SELECT DISTINCT fpym FROM `shwcity` ORDER BY fpym";
		JSONArray rows = c.pool.opensql2json_O(sqlstr);
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			sqlstr = "SELECT * FROM shwcity WHERE fpym='" + row.getString("fpym") + "' ORDER BY pym";
			JSONArray cts = c.pool.opensql2json_O(sqlstr);
			row.put("children", cts);
		}
		return rows.toString();
	}

	@ACOAction(eventname = "getCityByName", Authentication = false, ispublic = true, notes = "按名称获取城市对象")
	public String getCityByName() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String cname = CorUtil.hashMap2Str(parms, "cname", "cname");
		Shwcity c = new Shwcity();
		String sqlstr = "SELECT * FROM shwcity WHERE cname LIKE '%" + cname + "%'";
		c.findBySQL(sqlstr);
		return c.tojson();
	}

	/**
	 * 根据传入的参数
	 * areaid 或 cname 或 cname 和 tp 1 国家 2 省份 3 市 4 县区
	 * 获取区域信息，包括国家 country 、省 province 、市 city、县区 district
	 * 
	 * @return
	 * @throws Exception
	 */
	@ACOAction(eventname = "findPathAreas", Authentication = true, ispublic = true)
	public String findPathAreas() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String areaid = CorUtil.hashMap2Str(parms, "areaid");
		String cname = CorUtil.hashMap2Str(parms, "cname");
		String tp = CorUtil.hashMap2Str(parms, "tp");
		JSONArray rst = new JSONArray();
		String sqlstr = null;
		if ((areaid == null) || (areaid.isEmpty())) {
			if ((cname == null) || (cname.isEmpty())) {
				throw new Exception("不允许两个参数都为空");
			} else {
				sqlstr = "select * from shwarea where areaname like '%" + cname + "%'";
				if ((tp != null) && (!tp.isEmpty()))
					sqlstr = sqlstr + " and lvidx=" + tp;
			}
		} else
			sqlstr = "select * from shwarea where areaid=" + areaid;
		CJPALineData<Shwarea> areas = new CJPALineData<Shwarea>(Shwarea.class);
		areas.findDataBySQL(sqlstr, true, false);
		if (areas.size() > 30)
			throw new Exception("查询条件不够精确");
		for (CJPABase cjpa : areas) {
			Shwarea area = (Shwarea) cjpa;
			JSONObject jo = new JSONObject();
			if (area.isEmpty())
				throw new Exception("不存在指定的区域");
			putArea2Rst(jo, area);
			while (area.superid.getAsIntDefault(-1) != 0) {
				String superid = area.superid.getValue();
				area.clear();
				area.findByID(superid);
				if (area.isEmpty()) {
					throw new Exception("ID为【】的区域不存在");
				}
				putArea2Rst(jo, area);
			}
			rst.add(jo);
		}
		return rst.toString();
	}

	private void putArea2Rst(JSONObject rst, Shwarea area) throws Exception {
		int lvidx = area.lvidx.getAsIntDefault(-1);
		switch (lvidx) {
		case 1:
			rst.put("country", area.toJsonObj());
			break;
		case 2:
			rst.put("province", area.toJsonObj());
			break;
		case 3:
			rst.put("city", area.toJsonObj());
			break;
		case 4:
			rst.put("district", area.toJsonObj());
			break;
		}
	}

}
