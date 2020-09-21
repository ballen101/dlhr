package com.hr.attd.co;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.hr.attd.entity.Hrkq_leave_blance;

@ACO(coname = "web.hrkq.onduty")
public class COHrkq_onduty {
	@ACOAction(eventname = "findOnDutyTotal", Authentication = true, ispublic = false, notes = "查询值班汇总")
	public String findOnDutyTotal() throws Exception {

		HashMap<String, String> parms = CSContext.get_pjdataparms();

		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要选择筛选条件");
		}
		List<JSONParm> jps = CJSON.getParms(ps);

		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		String orgcode = jporgcode.getParmvalue();

		JSONParm jp2 = getParmByName(jps, "employee_code");
		String employee_code = null;
		if (jp2 != null) {
			employee_code = jp2.getParmvalue();
		}
//od.check_type,
		String sqlstr = "SELECT od.od_code,od.er_id,od.employee_code,od.employee_name,od.orgid,od.orgcode,od.orgname,od.lv_id,od.lv_num," +
				" od.orghrlev,od.emplev,od.ospid,od.ospcode,od.sp_name,od.odhours,od.duty_type,od.dealtype,od.odreason,od.remark," +
				" od.stat,od.idpath,od.entid,od.creator,od.createtime,od.updator,od.updatetime, odl.*," +
				" SUM(TIMESTAMPDIFF(HOUR,odl.begin_date,odl.end_date)) odhos," +
				" SUM(CASE WHEN duty_type=1 THEN TIMESTAMPDIFF(HOUR,odl.begin_date,odl.end_date) ELSE 0 END ) odhos1," +
				" SUM(CASE WHEN duty_type=2 THEN TIMESTAMPDIFF(HOUR,odl.begin_date,odl.end_date) ELSE 0 END ) odhos2 " +
				" FROM `hrkq_onduty` od,`hrkq_ondutyline` odl WHERE od.stat=9 AND odl.od_id=od.od_id ";
		if ((employee_code != null)) {
			sqlstr = sqlstr + " and od.employee_code='" + employee_code + "'";
		}
		if (orgcode != null) {
			Shworg org = new Shworg();
			String sqlstr1 = "select * from shworg where usable=1 and code='" + orgcode + "'";
			org.findBySQL(sqlstr1);
			if (org.isEmpty())
				throw new Exception("编码为【" + orgcode + "】的机构不存在");
			sqlstr = sqlstr + " and od.idpath LIKE '%" + org.idpath.getValue() + "%'";
		}
		sqlstr = sqlstr + " GROUP BY od.er_id";
		String[] ignParms = { "orgcode" };// 忽略的查询条件
		JSONObject jo = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rts = jo.getJSONArray("rows");
		getleaveblance(rts);
		String scols = parms.get("cols");
		if (scols == null) {
			return jo.toString();
		} else {
			(new CReport()).export2excel(rts, scols);
			return null;
		}
	}

	private void getleaveblance(JSONArray rts) throws Exception {
		if (rts.size() > 0) {
			for (int i = 0; i < rts.size(); i++) {
				String sqlstr = "select SUM(CASE WHEN alllbtime>0 THEN alllbtime ELSE 0 END ) alllbtime, " +
						"SUM(CASE WHEN usedlbtime>0 THEN usedlbtime ELSE 0 END ) usedlbtime FROM `hrkq_leave_blance` WHERE stype=3 AND NOW()<valdate";
				sqlstr = sqlstr + " and er_id=" + rts.getJSONObject(i).getString("er_id") + " GROUP BY er_id";

				JSONArray result = DBPools.defaultPool().opensql2json_O(sqlstr);

				JSONObject rt = rts.getJSONObject(i);
				if (result.size() != 0) {
					if (!result.getJSONObject(0).has("alllbtime")) {
						rt.put("alllbtime", 0);
					} else {
						rt.put("alllbtime", result.getJSONObject(0).get("alllbtime"));
					}
					if (!result.getJSONObject(0).has("usedlbtime")) {
						rt.put("usedlbtime", 0);
					} else {
						rt.put("usedlbtime", result.getJSONObject(0).get("usedlbtime"));
					}
					rts.set(i, rt);
				} else {
					rt.put("alllbtime", 0);
					rt.put("usedlbtime", 0);
					rts.set(i, rt);
				}
			}
		}

	}

	private JSONParm getParmByName(List<JSONParm> jps, String pname) {
		for (JSONParm jp : jps) {
			if (jp.getParmname().equals(pname))
				return jp;
		}
		return null;
	}

}
