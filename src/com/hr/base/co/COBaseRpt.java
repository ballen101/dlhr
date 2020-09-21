package com.hr.base.co;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.co.COPermRpt1;
import com.hr.util.HRUtil;

@ACO(coname = "web.hr.baserpt")
public class COBaseRpt {
	// 组织机构列表
	@ACOAction(eventname = "findOrgList", Authentication = true, notes = "组织机构列表报表")
	public String findOrgList() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpdqdate = CjpaUtil.getParm(jps, "yyyymm");
		if (jpdqdate == null)
			throw new Exception("需要参数【dqdate】");
		String orgcode = jporgcode.getParmvalue();
		String yyyymm = jpdqdate.getParmvalue();

		String sqlstr = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		dws.add(0, org.toJsonObj());
		JSONArray footer = new JSONArray();
		JSONObject srow = new JSONObject();
		dofinddetail(dws, srow, yyyymm);
		footer.add(srow);

		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
		rst.put("footer", footer);

		String scols = urlparms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private void dofinddetail(JSONArray datas, JSONObject srow, String yearmonth) throws Exception {
		boolean isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yearmonth);
		String pyearmonth = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(Systemdate.getDateByStr(yearmonth), -1), "yyyy-MM");
		int s_offquota = 0, s_not_offquota = 0, s_quota = 0, s_emnum = 0, s_emnumoff = 0, s_emnumnoff = 0;
		for (int i = 0; i < datas.size(); i++) {
			boolean includechld = (i != 0);
			// boolean includechld = true;
			JSONObject dw = datas.getJSONObject(i);
			dw.put("yyyymm", yearmonth);
			String orgid = dw.getString("orgid").toString();
			// 机构脱产编制
			String sqlstr = "SELECT IFNULL(SUM(op.quota),0) quota ";
			if (isnowmonth)
				sqlstr = sqlstr + " FROM hr_orgposition op,hr_standposition sp ";
			else
				sqlstr = sqlstr + " FROM hr_month_orgposition op,hr_standposition sp ";
			sqlstr = sqlstr + " WHERE sp.usable=1  ";
			if (!isnowmonth)
				sqlstr = sqlstr + " and op.yearmonth=" + yearmonth + "";
			if (includechld)
				sqlstr = sqlstr + " AND op.idpath LIKE '" + dw.getString("idpath") + "%'";
			else
				sqlstr = sqlstr + " AND op.orgid=" + dw.getString("orgid");
			sqlstr = sqlstr + " AND op.sp_id=sp.sp_id AND op.isoffjob=1";
			dw.put("offquota", DBPools.defaultPool().openSql2List(sqlstr).get(0).get("quota")); // 拖产编制
			s_offquota = s_offquota + dw.getInt("offquota");

			// 机构非脱产编制 可能要从 机构职类编制获取？？？？？？？
			sqlstr = "SELECT IFNULL(SUM(q.quota),0) quota FROM ";
			if (isnowmonth)
				sqlstr = sqlstr + " hr_quotaoc q, hr_wclass c,shworg o";
			else
				sqlstr = sqlstr + " hr_month_quotaoc q, hr_wclass c,shworg o";
			sqlstr = sqlstr + " WHERE q.orgid=o.orgid AND q.classid=c.hwc_id AND c.isoffjob=2 and o.usable=1 and c.usable=1 ";
			if (!isnowmonth)
				sqlstr = sqlstr + " and q.yearmonth='" + yearmonth + "'";
			if (includechld)
				sqlstr = sqlstr + " AND o.idpath LIKE '" + dw.getString("idpath") + "%'";
			else
				sqlstr = sqlstr + " AND o.orgid=" + dw.getString("orgid");
			dw.put("not_offquota", DBPools.defaultPool().openSql2List(sqlstr).get(0).get("quota"));// 非拖产编制
			s_not_offquota = s_not_offquota + dw.getInt("not_offquota");

			dw.put("quota", dw.getInt("offquota") + dw.getInt("not_offquota"));// 总编制
			s_quota = s_quota + dw.getInt("quota");

			// 实际人数
			HashMap<String, String> mp = COPermRpt1.getTrueEmpCts(dw, pyearmonth, yearmonth, 0, includechld);
			dw.put("emnum", mp.get("emnum"));
			dw.put("emnumoff", mp.get("emnumoff"));// 脱产人数
			dw.put("emnumnoff", mp.get("emnumnoff"));//
			s_emnum = s_emnum + dw.getInt("emnum");
			s_emnumoff = s_emnumoff + dw.getInt("emnumoff");
			s_emnumnoff = s_emnumnoff + dw.getInt("emnumnoff");
		}
		srow.put("offquota", s_offquota);
		srow.put("not_offquota", s_not_offquota);
		srow.put("quota", s_quota);

		srow.put("emnum", s_emnum);
		srow.put("emnumoff", s_emnumoff);
		srow.put("emnumnoff", s_emnumnoff);
		srow.put("orgname", "合计");
	}

	@ACOAction(eventname = "findopquota", Authentication = true, notes = "查询机构职位及编制")
	public String findopquota() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");

		String sqlstr = "SELECT op.*,IFNULL(COUNT(he.er_id),0) empct FROM hr_orgposition op,hr_employee he   " +
				"WHERE op.orgid=" + orgid + " AND op.ospid=he.ospid AND he.usable=1  " +
				"AND EXISTS(SELECT 1 FROM hr_employeestat st WHERE he.empstatid=st.statvalue AND st.usable=1 AND st.isquota=1) " +
				" GROUP BY op.ospid";
		// String sqlstr = "select * from hr_orgposition where orgid=" + orgid;
		Hr_orgposition hsp = new Hr_orgposition();
		// JSONArray datas = hsp.pool.opensql2jsontree_o(sqlstr, "ospid", "pid", false);
		JSONArray datas = hsp.pool.opensql2json_O(sqlstr);
		for (int i = 0; i < datas.size(); i++) {
			JSONObject dt = datas.getJSONObject(i);
			int quota = (dt.has("quota")) ? Integer.valueOf(dt.getString("quota")) : 0;
			int empct = Integer.valueOf(dt.getString("empct"));
			int cbnum = empct - quota;
			if (cbnum < 0)
				cbnum = 0;
			int qbnum = quota - empct;
			if (qbnum < 0)
				qbnum = 0;
			dt.put("cbnum", cbnum);
			dt.put("qbnum", qbnum);
		}
		// findENumOp(hsp, datas);
		return datas.toString();
	}

	private void findENumOp(Hr_orgposition hsp, JSONArray dts) throws Exception {
		for (int i = 0; i < dts.size(); i++) {
			JSONObject dt = dts.getJSONObject(i);
			String ospid = dt.getString("ospid");
			String sqlstr = "SELECT COUNT(*) ct FROM hr_employee he " + " WHERE he.usable=1 AND he.ospid=" + ospid + "  AND he.empstatid IN"
					+ " (SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1)";
			dt.put("empct", hsp.pool.openSql2List(sqlstr).get(0).get("ct"));

			int quota = (dt.has("quota")) ? Integer.valueOf(dt.getString("quota")) : 0;
			int empct = Integer.valueOf(dt.getString("empct"));
			int cbnum = empct - quota;
			if (cbnum < 0)
				cbnum = 0;
			int qbnum = quota - empct;
			if (qbnum < 0)
				qbnum = 0;
			dt.put("cbnum", cbnum);
			dt.put("qbnum", qbnum);
			// if (dt.has("children")) {
			// findENumOp(hsp, dt.getJSONArray("children"));
			// }
		}
	}

	@ACOAction(eventname = "findOrgEmployee", Authentication = true, notes = "机构包含员工")
	public String findOrgEmployee() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String sqlstr = "SELECT * FROM hr_employee he WHERE he.empstatid IN(" + "   SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1"
				+ " ) AND he.orgid=" + orgid;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findOrgEmployeejz", Authentication = true, notes = "机构兼职员工")
	public String findOrgEmployeejz() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String sqlstr = "SELECT eip.* " + " FROM hr_empptjob_info ji,hr_employee he,hr_empptjob_app eip "
				+ " WHERE ji.er_id=he.er_id AND ji.enddate>=NOW() AND ji.startdate<=NOW() AND ji.sourceid=eip.ptjaid" + " AND eip.neworgid=" + orgid
				+ " AND he.empstatid IN(" + "      SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1)";
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findOrgEmployeejr", Authentication = true, notes = "机构借入员工")
	public String findOrgEmployeejr() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String sqlstr = "SELECT l.*,h.loandate,h.returndate,h.loanreason FROM hr_emploanbatch h,hr_emploanbatch_line l,hr_employee e"
				+ " WHERE h.stat=9 AND h.loanid=l.loanid AND l.er_id=e.er_id AND h.returndate>=NOW() AND h.loandate<=NOW()"
				+ " AND e.empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1)" + " AND l.neworgid=" + orgid;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findOrgEmployeejc", Authentication = true, notes = "机构借出员工")
	public String findOrgEmployeejc() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid", "需要参数orgid");
		String sqlstr = "SELECT l.*,h.loandate,h.returndate,h.loanreason FROM hr_emploanbatch h,hr_emploanbatch_line l,hr_employee e"
				+ " WHERE h.stat=9 AND h.loanid=l.loanid AND l.er_id=e.er_id AND h.returndate>=NOW() AND h.loandate<=NOW()"
				+ " AND e.empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1)" + " AND l.odorgid=" + orgid;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "findEmployeeClassQuota", Authentication = true, notes = "机构职类编制")
	public String findEmployeeClassQuota() throws Exception {
		JSONArray dws = HRUtil.getOrgsByType("6");// 工厂
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			JSONArray cqs = getClassQuota(dw);
			int emnum = getClassEmployees(dw);
			if (cqs.size() > 0) {
				JSONObject cq = cqs.getJSONObject(0);
				dw.put("quota", cq.getString("quota"));
				dw.put("emnum", emnum);
				dw.put("hwc_id", cq.getString("hwc_id"));
				dw.put("hw_code", cq.getString("hw_code"));
				dw.put("hwc_name", cq.getString("hwc_name"));
			} else {
				dw.put("quota", 0);
				dw.put("emnum", emnum);
			}
			int hwcquota = dw.getInt("quota");

			if (hwcquota != 0) {
				if (hwcquota > emnum) {
					dw.put("overquota", hwcquota - emnum);
					dw.put("lowquota", 0);
				} else {
					dw.put("overquota", 0);
					dw.put("lowquota", emnum - hwcquota);
				}
			}

		}
		JSONArray temple = new JSONArray();
		for (int j = 0; j < dws.size(); j++) {
			JSONObject dw = dws.getJSONObject(j);
			if (dw.getInt("quota") != 0) {
				temple.add(dw);
			}
		}

		return temple.toString();
	}

	private JSONArray getClassQuota(JSONObject dw) throws Exception {
		String sqlstr = "SELECT hwc.hwc_id,hwc.hw_code,hwc.hwc_name, hqoc.quota,o.orgid,o.orgname,o.code FROM "
				+ " hr_quotaoc hqoc,hr_wclass hwc,(SELECT * FROM  shworg  WHERE stat=1 AND entid=1 AND orgtype=6 AND idpath LIKE '" + dw.getString("idpath")
				+ "%' ) o " + " WHERE  hqoc.orgid=o.orgid AND hqoc.usable=1 AND hwc.hwc_id=hqoc.classid ORDER BY o.orgid";

		return DBPools.defaultPool().opensql2json_O(sqlstr);
	}

	private int getClassEmployees(JSONObject dw) throws Exception {
		String sqlstr = " SELECT COUNT(*) ct FROM hr_employee WHERE "
				+ " usable=1  AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) " + " AND idpath = '"
				+ dw.getString("idpath") + "'";

		return Integer.valueOf(DBPools.defaultPool().opensql2json_O(sqlstr).getJSONObject(0).getString("ct"));
	}

	private JSONParm getParmByName(List<JSONParm> jps, String pname) {
		for (JSONParm jp : jps) {
			if (jp.getParmname().equals(pname))
				return jp;
		}
		return null;
	}

}
