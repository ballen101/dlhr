package com.hr.attd.co;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.hr.attd.entity.Hrkq_sched;
import com.hr.attd.entity.Hrkq_sched_line;
import com.hr.attd.entity.Hrkq_workschmonthlist;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hrkq.sched")
public class COHrkq_sched {
	@ACOAction(eventname = "findsched", Authentication = true, notes = "查询班制")
	public String findsched() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String tid = CorUtil.hashMap2Str(parms, "tid", "需要参数tid");
		int ttype = Integer.valueOf(CorUtil.hashMap2Str(parms, "ttype", "需要参数ttype"));
		String orgid = null;
		if (ttype == 2) {// org
			orgid = tid;
		}

		if (ttype == 3) {
			Hr_employee ep = new Hr_employee();
			ep.findByID(tid, false);
			if (ep.isEmpty())
				throw new Exception("ID为【】的员工资料不存在");
			orgid = ep.orgid.getValue();
		}
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("没发现ID为【" + orgid + "】的机构");
		String idp = org.idpath.getValue();
		if (idp.length() > 0)
			idp = idp.substring(0, idp.length() - 1);
		String sqlstr = "select * from hrkq_sched where orgid in (" + idp + ")";
		String[] ignParms = new String[] {};
		JSONObject rst = new CReport(sqlstr, null).findReport2JSON_O(ignParms, true, null);
		JSONArray rows = rst.getJSONArray("rows");
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			row.put("slinfo", getbcinfo(row.getString("scid")));
		}
		return rst.toString();
	}

	@ACOAction(eventname = "findsallched", Authentication = true, ispublic = false, notes = "重新通用班制班次查询CO")
	public String findsallched() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			Hrkq_sched sc = new Hrkq_sched();
			sc.findByID(id);
			return sc.tojson();
		} else {
			String sqlstr = "select * from hrkq_sched where 1=1 ";
			String orderby = " createtime desc ";
			JSONObject rst = new CReport(sqlstr, orderby, null).findReport2JSON_O();
			JSONArray rows = rst.getJSONArray("rows");
			for (int i = 0; i < rows.size(); i++) {
				JSONObject row = rows.getJSONObject(i);
				row.put("slinfo", getbcinfo(row.getString("scid")));
			}
			return rst.toString();
		}
	}

	private String getbcinfo(String scid) throws Exception {
		CJPALineData<Hrkq_sched_line> sls = new CJPALineData<Hrkq_sched_line>(Hrkq_sched_line.class);
		String sqlstr = "select * from hrkq_sched_line where scid=" + scid + " order by sclid";
		sls.findDataBySQL(sqlstr);
		String rst = "";
		for (CJPABase jpa : sls) {
			Hrkq_sched_line sl = (Hrkq_sched_line) jpa;
			rst = rst + "班次:" + sl.bcno.getValue() + "(" + sl.frtime.getValue() + "-" + sl.totime.getValue() + ");";
		}
		if (!rst.isEmpty())
			rst.substring(0, rst.length() - 2);
		return rst;
	}

	@ACOAction(eventname = "findwhmlist", Authentication = true, ispublic = false, notes = "排班列表通用查询CO")
	public String findwhmlist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String type = CorUtil.hashMap2Str(urlparms, "type", "需要参数type");
		String[] notnull = { "yearmonth" };
		if ("byid".equalsIgnoreCase(type)) {
			String id = CorUtil.hashMap2Str(urlparms, "id", "需要参数id");
			String sqlstr = "SELECT l.*,e.sp_name,e.ljdate FROM hrkq_workschmonthlist l,hr_employee e WHERE l.er_id=e.er_id and l.wklistid="
					+ id;
			return (new Hrkq_workschmonthlist()).pool.openrowsql2json(sqlstr);
		} else {
			String sqlstr = "SELECT l.*,e.sp_name,e.ljdate FROM hrkq_workschmonthlist l,hr_employee e WHERE l.er_id=e.er_id";
			sqlstr = sqlstr + CSContext.getIdpathwhere();
			return new CReport(sqlstr, notnull).findReport();
		}
	}

	@ACOAction(eventname = "findnotpbemp", Authentication = true, ispublic = false, notes = "查找某机构某月未排班人事列表")
	public String findnotpbemp() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(urlparms, "orgid", "需要参数orgid");
		String yearmonth = CorUtil.hashMap2Str(urlparms, "yearmonth", "需要参数yearmonth");
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("没发现ID为【" + orgid + "】的机构");
		String llvdate = yearmonth + "-01";
		String sqlstr = "SELECT * "
				+ " FROM hr_employee e "
				+ " WHERE e.idpath LIKE '" + org.idpath.getValue() + "%' "
				+ " AND ( empstatid<=10 || kqdate_end >'" + llvdate + "')"
				+ " AND empstatid>0 "
				+ " AND NOT EXISTS("
				+ " SELECT 1 FROM hrkq_workschmonthlist l WHERE l.yearmonth='" + yearmonth + "' AND l.er_id=e.er_id)";
		return new CReport(sqlstr, null).findReport();
	}
}
