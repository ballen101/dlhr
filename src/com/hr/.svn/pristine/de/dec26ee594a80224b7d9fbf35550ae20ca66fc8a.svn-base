package com.hr.perm.co;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBPool;
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
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_entry;
import com.hr.util.HRUtil;

@ACO(coname = "web.hr.permrpt3")
public class COPermRpt3 {

	@ACOAction(eventname = "empentryprob_list", Authentication = true, notes = "rpt单独入职转正列表  核薪")
	public String empentryprob_list() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		int tp = Integer.valueOf(CorUtil.hashMap2Str(parms, "tp", "参数tp不能为空"));
		String idpathw = CSContext.getIdpathwhere().replace("idpath", "o.idpath");
		String[] notnull = {};
//		String sqlstr = "SELECT * FROM hr_entry_prob t WHERE t.stat=9 ";
//		if (tp == 2)
//			sqlstr = sqlstr + "and pbtsarylev>0";
//		sqlstr = sqlstr + " AND (" + "   EXISTS(SELECT 1 FROM shworg o WHERE t.orgid=o.orgid " + idpathw + ")" + " )";
//		String order = (tp == 2) ? " salarydate desc " : " ";

		String sqlstr = "SELECT t.* FROM hr_entry_prob t,shworg o " +
				"WHERE t.stat=9 AND t.orgid=o.orgid ";
		if (tp == 2)
			sqlstr = sqlstr + "and t.pbtsarylev>0";
		sqlstr = sqlstr + idpathw;
		String order = (tp == 2) ? " salarydate desc " : " ";

		return new CReport(HRUtil.getReadPool(), sqlstr, order, notnull).findReport(null, null);
	}

	@ACOAction(eventname = "emptransfer_list", Authentication = true, notes = "rpt单独调动列表  核薪")
	public String emptransfer_list() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		int tp = Integer.valueOf(CorUtil.hashMap2Str(parms, "tp", "参数tp不能为空"));

		String idpathw = CSContext.getIdpathwhere().replace("idpath", "o.idpath");
		String[] notnull = {};
		String sqlstr = "SELECT * FROM hr_employee_transfer t WHERE t.stat=9 and t.newcalsalarytype='月薪' ";
		if (tp == 2)
			sqlstr = sqlstr + "and (oldposition_salary+oldbase_salary+oldtech_salary+oldachi_salary+oldtech_allowance+oldavg_salary"
					+ "+newposition_salary+newbase_salary+newtech_salary+newachi_salary+newtech_allowance)>0";
		sqlstr = sqlstr + " AND (" + "   EXISTS(SELECT 1 FROM shworg o WHERE t.odorgid=o.orgid " + idpathw + ")" + "   OR "
				+ "   EXISTS(SELECT 1 FROM shworg o WHERE t.neworgid=o.orgid " + idpathw + ")" + " )";
		String order = (tp == 2) ? " salarydate desc " : " tranfcmpdate desc ";
		CDBPool pool = HRUtil.getReadPool();
		return new CReport(pool, sqlstr, order, notnull).findReport(null, null);
	}

	@ACOAction(eventname = "emptransferprod_list", Authentication = true, notes = "rpt调动转正列表")
	public String emptransferprod_list() throws Exception {
		String[] notnull = {};
		String sqlstr = "SELECT * FROM hr_transfer_prob WHERE stat=9 ";
		return new CReport(sqlstr, " salarydate desc ", notnull).findReport();
	}

	@ACOAction(eventname = "emptransferbatch_list", Authentication = true, notes = "rpt批量调动列表")
	public String emptransferbatch_list() throws Exception {
		String idpathw = CSContext.getIdpathwhere().replace("idpath", "o.idpath");
		String[] notnull = {};
		String sqlstr = "SELECT bl.*,b.tranfcmpdate,b.tranfreason,b.createtime FROM  hr_emptransferbatch b,hr_emptransferbatch_line bl"
				+ " WHERE b.emptranfb_id=bl.emptranfb_id AND b.stat=9" + " AND (" + "   EXISTS(SELECT 1 FROM shworg o WHERE b.olorgid=o.orgid " + idpathw + ")"
				+ "   OR " + "   EXISTS(SELECT 1 FROM shworg o WHERE b.neworgid=o.orgid " + idpathw + ")" + " )";
		CDBPool pool = HRUtil.getReadPool();
		return new CReport(pool, sqlstr, " probationdate desc ", notnull).findReport();
	}

	@ACOAction(eventname = "rpt_hr_entry_sy", Authentication = true, notes = "rpt试用期管理月报")
	public String rpt_hr_entry_sy() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpdqdate = CjpaUtil.getParm(jps, "month");
		if (jpdqdate == null)
			throw new Exception("需要参数【month】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpdqdate.getParmvalue();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		Hr_entry he = new Hr_entry();
		String sqlstr = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		dws.add(0, org.toJsonObj());
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			boolean includechld = true;
			int syqrs = gesyqrs(he, dw, includechld, bgdate, eddate);// 试用期人数
			dw.put("syqrs", syqrs);
			int yzzrs = geyzzrs(he, dw, includechld, bgdate, eddate);// 应转正人数
			dw.put("yzzrs", yzzrs);
			int aqzzrs = getaqOryqRS(he, dw, includechld, bgdate, eddate, 1);// 按期转正人数
			int yqzzrs = getaqOryqRS(he, dw, includechld, bgdate, eddate, 2);// 延期转正人数
			dw.put("aqzzrs", aqzzrs);
			dw.put("yqzzrs", yqzzrs);
			int sylzrs = getsylzrs(he, dw, includechld, bgdate, eddate);// 试用离职人数
			dw.put("sylzrs", sylzrs);
			dw.put("aqzzl", (yzzrs == 0) ? 0 : ((float) aqzzrs * 100) / yzzrs);
			dw.put("yqzzl", (yzzrs == 0) ? 0 : ((float) yqzzrs * 100) / yzzrs);
			dw.put("sylzl", (yzzrs == 0) ? 0 : ((float) sylzrs * 100) / yzzrs);
		}
		return dws.toString();
	}

	private int gesyqrs(Hr_entry he, JSONObject dw, boolean includechld, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_entry WHERE stat=9  AND entrydate<'" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND promotionday>='"
				+ Systemdate.getStrDateyyyy_mm_dd(bgdate) + "'";
		if (includechld)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	private int geyzzrs(Hr_entry he, JSONObject dw, boolean includechld, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_entry WHERE stat=9  AND promotionday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND promotionday<='"
				+ Systemdate.getStrDateyyyy_mm_dd(eddate) + "'";
		if (includechld)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	private int getaqOryqRS(Hr_entry he, JSONObject dw, boolean includechld, Date bgdate, Date eddate, int tp) throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_entry e ,hr_entry_prob ep " + " WHERE e.entry_id=ep.sourceid AND e.stat=9 AND ep.stat=9  "
				+ "  AND promotionday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND promotionday<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "'";
		if (tp == 1)
			sqlstr = sqlstr + " and ep.wfresult=1 ";
		if (tp == 2)
			sqlstr = sqlstr + " and ep.wfresult=2 ";
		if (includechld)
			sqlstr = sqlstr + " AND e.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND e.orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	private int getsylzrs(Hr_entry he, JSONObject dw, boolean includechld, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String sqlstr1 = "SELECT COUNT(*) ct FROM hr_entry e ,hr_leavejobbatch lv,hr_leavejobbatchline lvl "
				+ " WHERE e.er_id=lvl.er_id AND lv.ljbid=lvl.ljbid AND lv.stat=9 AND e.stat=9 " + " AND e.promotionday>='"
				+ Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND e.promotionday<='" + Systemdate.getStrDateyyyy_mm_dd(eddate)
				+ "'  AND lvl.ljdate<=e.promotionday ";
		if (includechld)
			sqlstr1 = sqlstr1 + " AND e.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr1 = sqlstr1 + " AND e.orgid=" + dw.getString("orgid");
		String sqlstr2 = "SELECT COUNT(*) ct " + " FROM hr_entry e ,hr_leavejob lv" + " WHERE e.er_id=lv.er_id AND e.stat=9 AND lv.stat=9 "
				+ " AND e.promotionday>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND e.promotionday<='" + Systemdate.getStrDateyyyy_mm_dd(eddate)
				+ "' AND lv.ljdate<=e.promotionday";
		if (includechld)
			sqlstr2 = sqlstr2 + " AND e.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr2 = sqlstr2 + " AND e.orgid=" + dw.getString("orgid");

		String sqlstr = "SELECT SUM(ct) ct FROM( " + sqlstr1 + " union " + sqlstr2 + ") tb";
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	@ACOAction(eventname = "rpt_hr_transfer_kc", Authentication = true, notes = "rpt考察期管理月报")
	public String rpt_hr_transfer_kc() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpdqdate = CjpaUtil.getParm(jps, "month");
		if (jpdqdate == null)
			throw new Exception("需要参数【month】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpdqdate.getParmvalue();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		Hr_entry he = new Hr_entry();
		String sqlstr = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		dws.add(0, org.toJsonObj());
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			boolean includechld = true;
			int kcqrs = getkcqrs(he, dw, includechld, bgdate, eddate);// 考察期人数
			dw.put("kcqrs", kcqrs);
			int yzzrs = getyzzrs(he, dw, includechld, bgdate, eddate);// 应转正人数
			dw.put("yzzrs", yzzrs);
			int aqzzrs = getaqOryqZZRS(he, dw, includechld, bgdate, eddate, 1);// 按期转正人数
			int yqzzrs = getaqOryqZZRS(he, dw, includechld, bgdate, eddate, 2);// 延期转正人数
			dw.put("aqzzrs", aqzzrs);
			dw.put("yqzzrs", yqzzrs);
			int kclzrs = getkclzrs(he, dw, includechld, bgdate, eddate);// 试用离职人数
			dw.put("kclzrs", kclzrs);
			dw.put("aqzzl", (yzzrs == 0) ? 0 : ((float) aqzzrs * 100) / yzzrs);
			dw.put("yqzzl", (yzzrs == 0) ? 0 : ((float) yqzzrs * 100) / yzzrs);
			dw.put("kclzl", (yzzrs == 0) ? 0 : ((float) kclzrs * 100) / yzzrs);

		}
		return dws.toString();
	}

	private int getkcqrs(Hr_entry he, JSONObject dw, boolean includechld, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_employee_transfer WHERE stat=9  AND tranfcmpdate<'" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND probationdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "'";
		if (includechld)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	private int getyzzrs(Hr_entry he, JSONObject dw, boolean includechld, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_employee_transfer WHERE stat=9 AND probationdate>= '" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND probationdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		if (includechld)
			sqlstr = sqlstr + " AND neworgid IN (SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%')";
		else
			sqlstr = sqlstr + " AND neworgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	private int getaqOryqZZRS(Hr_entry he, JSONObject dw, boolean includechld, Date bgdate, Date eddate, int tp) throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_employee_transfer t,hr_transfer_prob tp "
				+ " WHERE t.stat=9 AND tp.stat=9 AND t.emptranf_id=tp.sourceid AND probationdate>= '" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND probationdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		if (includechld)
			sqlstr = sqlstr + " AND t.neworgid IN (SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%')";
		else
			sqlstr = sqlstr + " AND t.neworgid=" + dw.getString("orgid");
		if (tp == 1)
			sqlstr = sqlstr + "AND tp.wfresult=1";
		if (tp == 2)
			sqlstr = sqlstr + "AND tp.wfresult=2";
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	private int getkclzrs(Hr_entry he, JSONObject dw, boolean includechld, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String sqlstr1 = "SELECT COUNT(*) ct FROM hr_employee_transfer e ,hr_leavejobbatch lv,hr_leavejobbatchline lvl "
				+ " WHERE e.er_id=lvl.er_id AND lv.ljbid=lvl.ljbid AND lv.stat=9 AND e.stat=9 " + " AND e.probationdate>='"
				+ Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND e.probationdate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate)
				+ "'  AND lvl.ljdate<=e.probationdate ";
		if (includechld)
			sqlstr1 = sqlstr1 + " AND e.neworgid IN (SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%')";
		else
			sqlstr1 = sqlstr1 + " AND e.neworgid=" + dw.getString("orgid");

		String sqlstr2 = "SELECT COUNT(*) ct " + " FROM hr_employee_transfer e ,hr_leavejob lv" + " WHERE e.er_id=lv.er_id AND e.stat=9 AND lv.stat=9 "
				+ " AND e.probationdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND e.probationdate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate)
				+ "' AND lv.ljdate<=e.probationdate";
		if (includechld)
			sqlstr1 = sqlstr1 + " AND e.neworgid IN (SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%')";
		else
			sqlstr1 = sqlstr1 + " AND e.neworgid=" + dw.getString("orgid");

		String sqlstr = "SELECT SUM(ct) ct FROM( " + sqlstr1 + " union " + sqlstr2 + ") tb";
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	@ACOAction(eventname = "rpt_hr_entry_sylist", Authentication = true, notes = "rpt试用期管理信息列表")
	public String rpt_hr_entry_sylist() throws Exception {
		String sqlstr = "SELECT tb1.*,tb2.wfpbtdate,tb2.wfresult, " + " CASE " + " WHEN tb1.ispromotioned=2 AND tb1.promotionday>NOW() THEN  '试用期中'"
				+ " WHEN tb1.ispromotioned=2 AND tb2.wfresult=2 THEN  '试用延期'" + " WHEN tb1.ispromotioned=2 AND tb1.promotionday<NOW() THEN  '试用过期'"
				+ " WHEN  tb1.ispromotioned=1 AND tb2.wfresult=1 THEN  '试用合格'" + " WHEN  tb1.ispromotioned=2 AND tb2.wfresult=3 THEN  '试用不合格'" + "  ELSE '其它'"
				+ " END  pstat" + " FROM "
				+ " (SELECT en.entry_id,en.er_id,en.employee_code,e.employee_name,e.orgid,e.orgname,e.sp_name,e.lv_num,e.hwc_namezl,e.idpath,"
				+ "        e.hiredday,en.probation,en.promotionday,en.ispromotioned" + " FROM hr_entry en,hr_employee e"
				+ " WHERE en.er_id=e.er_id AND en.stat=9) tb1 LEFT JOIN " + " (SELECT * FROM hr_entry_prob ep WHERE ep.stat=9 )tb2"
				+ " ON tb1.entry_id=tb2.sourceid ";
		String[] notnull = {};
		return new CReport(HRUtil.getReadPool(), sqlstr, " promotionday desc ", notnull).findReport();
	}

	@ACOAction(eventname = "rpthr_emploanbatch", Authentication = true, notes = "rpt借调数据报表")
	public String rpthr_emploanbatch() throws Exception {
		String sqlstr = "SELECT l.*,h.idpath,h.loancode,h.loandate,h.loanreason,h.loantype,h.returndate,h.stat "
				+ " FROM hr_emploanbatch h,hr_emploanbatch_line l " + " WHERE h.loanid=l.loanid";
		String[] notnull = {};
		return new CReport(HRUtil.getReadPool(), sqlstr, "", notnull).findReport();
	}

	@ACOAction(eventname = "rpt_hr_transfer_hzfx", Authentication = true, notes = "rpt人事调动报表")
	public String rpt_hr_transfer_hzfx() throws Exception {
		DecimalFormat dec = new DecimalFormat("0.00000000");
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpdqdate = CjpaUtil.getParm(jps, "month");
		if (jpdqdate == null)
			throw new Exception("需要参数【month】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpdqdate.getParmvalue();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		dws.add(0, org.toJsonObj());
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			boolean includechld = true;
			int sumpeo = getEmoloyssct(org.pool, dw, 0, includechld);
			dw.put("sumpemnum", sumpeo);// 总人数
			dw.put("tcrs", getEmoloyssct(org.pool, dw, 1, includechld));// 脱产
			dw.put("ttcrs", getEmoloyssct(org.pool, dw, 2, includechld));// 非脱产
			int jsrs = getTransferJSOrJZ(org.pool, dw, 1, true, 0, null, 0, bgdate, eddate);
			dw.put("jsrs", jsrs);// 晋升
			int jzrs = getTransferJSOrJZ(org.pool, dw, 2, true, 0, null, 0, bgdate, eddate);
			dw.put("jzrs", jzrs);// 降职
			int nbddrs = getTransferNB(org.pool, dw, true, 0, null, 0, bgdate, eddate);
			dw.put("nbddrs", nbddrs);// 内部调动
			int drrs = getTransferDROrDC(org.pool, dw, 1, true, 0, null, 0, bgdate, eddate);
			dw.put("drrs", drrs);// 调入
			int dcrs = getTransferDROrDC(org.pool, dw, 2, true, 0, null, 0, bgdate, eddate);
			dw.put("dcrs", dcrs);// 调出
			int plddrs = getTransferBtchDROrDC(org.pool, dw, 0, true, 0, null, 0, bgdate, eddate);
			dw.put("plddrs", plddrs);// 批调
			int pldrrs = getTransferBtchDROrDC(org.pool, dw, 1, true, 0, null, 0, bgdate, eddate);
			dw.put("pldrrs", pldrrs);// 批调入
			int pldcrs = getTransferBtchDROrDC(org.pool, dw, 2, true, 0, null, 0, bgdate, eddate);
			dw.put("pldcrs", pldcrs);// 批调出
			// if (sumpeo != 0)
			// System.out.println(jsrs + "/" + sumpeo + " jsrsbl:" + ((float)
			// jsrs / sumpeo));
			dw.put("jsbl", ((sumpeo > 0) ? (dec.format((float) (jsrs * 100) / sumpeo)) : "0.00000000"));// 晋升
			dw.put("jzbl", ((sumpeo > 0) ? (dec.format((float) (jzrs * 100) / sumpeo)) : "0.00000000"));// 降职
			dw.put("nbddbl", ((sumpeo > 0) ? (dec.format((float) (nbddrs * 100) / sumpeo)) : "0.00000000"));// 内部调动
			dw.put("drbl", ((sumpeo > 0) ? (dec.format((float) (drrs * 100) / sumpeo)) : "0.00000000"));// 调入
			dw.put("dcbl", ((sumpeo > 0) ? (dec.format((float) (dcrs * 100) / sumpeo)) : "0.00000000"));// 调出
			dw.put("plddbl", ((sumpeo > 0) ? (dec.format((float) (plddrs * 100) / sumpeo)) : "0.00000000"));// 批调
			dw.put("pldrbl", ((sumpeo > 0) ? (dec.format((float) (pldrrs * 100) / sumpeo)) : "0.00000000"));// 批调入
			dw.put("pldcbl", ((sumpeo > 0) ? (dec.format((float) (pldcrs * 100) / sumpeo)) : "0.00000000"));// 批调出
		}

		String scols = urlparms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	// 批量调动 ，调入 调出 tc 0内部 1入 2出
	private int getTransferBtchDROrDC(CDBPool pool, JSONObject dw, int tc, boolean includechld, int empclass, String spname, int findtype, Date bgdate, Date eddate)
			throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_emptransferbatch t,hr_emptransferbatch_line l,hr_orgposition op"
				+ " WHERE l.odospid=op.ospid and t.emptranfb_id=l.emptranfb_id AND t.stat=9" + " AND t.tranfcmpdate>='"
				+ Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND t.tranfcmpdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		if (tc == 1) {
			if (includechld) {
				sqlstr = sqlstr + " and l.neworgid IN ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
				sqlstr = sqlstr + " and l.odorgid NOT IN ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
			} else {
				sqlstr = sqlstr + " and l.neworgid=" + dw.getString("orgid");
				sqlstr = sqlstr + " and l.odorgid<>" + dw.getString("orgid");
			}
		}
		if (tc == 2) {
			if (includechld) {
				sqlstr = sqlstr + " and l.odorgid IN (SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
				sqlstr = sqlstr + " and l.neworgid NOT IN ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
			} else {
				sqlstr = sqlstr + " and l.odorgid=" + dw.getString("orgid");
				sqlstr = sqlstr + " and l.neworgid<>" + dw.getString("orgid");
			}
		}
		if (tc == 0) {
			if (includechld) {
				sqlstr = sqlstr + " and (l.neworgid IN ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
				sqlstr = sqlstr + " and l.odorgid IN ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' ))";
				// sqlstr = sqlstr + " or l.odorgid IN (SELECT orgid FROM shworg
				// WHERE idpath LIKE '" + dw.getString("idpath") + "%' ))";
			} else {
				sqlstr = sqlstr + " and (l.neworgid =" + dw.getString("orgid");
				sqlstr = sqlstr + " and l.odorgid =" + dw.getString("orgid") + ")";
			}
		}

		if (empclass == 2)
			sqlstr = sqlstr + " and op.isoffjob=1";
		if (empclass == 3)
			sqlstr = sqlstr + " and op.isoffjob=2";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and op.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and op.sp_name like '%" + spname + "%'";
		}
		sqlstr = COPermRpt1.parExtSQL("op", sqlstr);

		return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	// tc 1 调入 2 调出
	private int getTransferDROrDC(CDBPool pool, JSONObject dw, int tc, boolean includechld, int empclass, String spname, int findtype, Date bgdate, Date eddate)
			throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_employee_transfer t,hr_orgposition op  " + " WHERE t.odospid=op.ospid and  t.stat=9 " + " AND (";
		if (tc == 1) {
			if (includechld) {
				sqlstr = sqlstr + "   t.neworgid IN  ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
				sqlstr = sqlstr + " and  t.odorgid NOT IN  ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
			} else {
				sqlstr = sqlstr + "   t.neworgid =" + dw.getString("orgid");
				sqlstr = sqlstr + " and  t.odorgid <>" + dw.getString("orgid");
			}
		}

		if (tc == 2) {
			if (includechld) {
				sqlstr = sqlstr + "   t.odorgid IN( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
				sqlstr = sqlstr + "  and t.neworgid NOT IN  ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
			} else {
				sqlstr = sqlstr + "   t.odorgid =" + dw.getString("orgid");
				sqlstr = sqlstr + "  and t.neworgid <>" + dw.getString("orgid");
			}

		}

		sqlstr = sqlstr + ") " + " AND t.tranfcmpdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND t.tranfcmpdate<'"
				+ Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";

		if (empclass == 2)
			sqlstr = sqlstr + " and op.isoffjob=1";
		if (empclass == 3)
			sqlstr = sqlstr + " and op.isoffjob=2";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and op.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and op.sp_name like '%" + spname + "%'";
		}
		sqlstr = COPermRpt1.parExtSQL("op", sqlstr);

		return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	// 内部调动
	private int getTransferNB(CDBPool pool, JSONObject dw, boolean includechld, int empclass, String spname, int findtype, Date bgdate, Date eddate)
			throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_employee_transfer t,hr_orgposition op  " + " WHERE t.odospid=op.ospid and t.stat=9 ";
		if (includechld)
			sqlstr = sqlstr + " AND (" + "   t.odorgid IN( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' ) and "
					+ "   t.neworgid IN  ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )" + ") ";
		else
			sqlstr = sqlstr + " and t.odorgid=t.neworgid and t.neworgid=" + dw.getString("orgid");
		sqlstr = sqlstr + " AND t.tranfcmpdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND t.tranfcmpdate<'"
				+ Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";

		if (empclass == 2)
			sqlstr = sqlstr + " and op.isoffjob=1";
		if (empclass == 3)
			sqlstr = sqlstr + " and op.isoffjob=2";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and op.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and op.sp_name like '%" + spname + "%'";
		}
		sqlstr = COPermRpt1.parExtSQL("op", sqlstr);

		return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	// tc 1 晋升 2 降职
	private int getTransferJSOrJZ(CDBPool pool, JSONObject dw, int tc, boolean includechld, int empclass, String spname, int findtype, Date bgdate, Date eddate)
			throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_employee_transfer t,hr_orgposition op  " + " WHERE t.odospid=op.ospid and  t.stat=9 " + " AND (";
		if (includechld)
			sqlstr = sqlstr + "   t.neworgid IN  ( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' )";
		else
			sqlstr = sqlstr + "   t.neworgid =" + dw.getString("orgid");
		sqlstr = sqlstr + ") " + " AND t.tranfcmpdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND t.tranfcmpdate<'"
				+ Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		if (tc == 1)
			sqlstr = sqlstr + " AND tranftype1=1 ";
		if (tc == 2)
			sqlstr = sqlstr + " AND tranftype1=2 ";// 3

		if (empclass == 2)
			sqlstr = sqlstr + " and op.isoffjob=1";
		if (empclass == 3)
			sqlstr = sqlstr + " and op.isoffjob=2";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and op.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and op.sp_name like '%" + spname + "%'";
		}
		sqlstr = COPermRpt1.parExtSQL("op", sqlstr);

		return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	// tc 0 所有 1 脱产 2非脱产
	private int getEmoloyssct(CDBPool pool, JSONObject dw, int tc, boolean includchd) throws Exception {
		String sqlstr = "SELECT count(*) ct FROM hr_employee WHERE usable=1 ";
		sqlstr = sqlstr + " AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) ";
		if (tc == 1)
			sqlstr = sqlstr + " and hwc_namezl<>'OO'";
		if (tc == 2)
			sqlstr = sqlstr + " and hwc_namezl='OO'";
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	// tc 1脱产 2非脱产
	private int getEmoloyquota(CDBPool pool, JSONObject dw, int tc, boolean includchd) throws NumberFormatException, Exception {
		String sqlstr = null;
		if (tc == 1) {
			sqlstr = "SELECT IFNULL(SUM(op.quota),0) quota " + " FROM hr_orgposition op,hr_standposition sp " + " WHERE 1=1  ";
			if (includchd)
				sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
			else
				sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
			sqlstr = sqlstr + " AND op.sp_id=sp.sp_id AND op.isoffjob=1";
		}
		if (tc == 2) {
			sqlstr = "SELECT IFNULL(SUM(q.quota),0) quota FROM   hr_quotaoc q, hr_wclass c,shworg o"
					+ " WHERE q.orgid=o.orgid AND q.classid=c.hwc_id AND c.isoffjob=2 ";
			if (includchd)
				sqlstr = sqlstr + " AND o.idpath LIKE '" + dw.getString("idpath") + "%'";
			else
				sqlstr = sqlstr + " AND o.orgid=" + dw.getString("orgid");
		}
		return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("quota"));
	}

	@ACOAction(eventname = "rpt_hrempptjob", Authentication = true, notes = "rpt兼职数据报表")
	public String rpt_hrempptjob() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpdqdate = CjpaUtil.getParm(jps, "month");
		if (jpdqdate == null)
			throw new Exception("需要参数【month】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpdqdate.getParmvalue();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		dws.add(0, org.toJsonObj());
		JSONArray ptjresult = new JSONArray();
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			// boolean includechld = true;
			String sqlstr = "SELECT pja.*,pji.ptjstat FROM `hr_empptjob_app` pja,`hr_empptjob_info` pji WHERE pja.stat=9 AND pji.sourceid=pja.ptjaid "
					+ " and pja.startdate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' and pja.startdate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate)
					+ "' ";
			// if (includechld)
			// sqlstr = sqlstr + " AND pja.idpath LIKE '" +
			// dw.getString("idpath") + "%'";
			// else
			sqlstr = sqlstr + " AND pja.odorgid=" + dw.getString("orgid");
			JSONArray res = HRUtil.getReadPool().opensql2json_O(sqlstr);
			if (res.size() != 0)
				ptjresult.addAll(res);
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			return ptjresult.toString();
		} else {
			(new CReport()).export2excel(ptjresult, scols);
			return null;
		}
	}

	@ACOAction(eventname = "rpt_hrentrylist", Authentication = true, notes = "rpt员工入职数据统计分析报表")
	public String rpt_hrentrylist() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpdqdate = CjpaUtil.getParm(jps, "month");
		if (jpdqdate == null)
			throw new Exception("需要参数【month】");
		String orgcode = jporgcode.getParmvalue();
		String dqdate = jpdqdate.getParmvalue();
		Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(dqdate)));// 去除时分秒
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String sqlstr = "SELECT et.entrydate,et.orgid AS inorgid,et.lv_num AS inlv_num,emp.* FROM `hr_entry` et,`hr_employee` emp WHERE et.stat=9 AND et.er_id=emp.er_id "
				+ " AND emp.empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) " + "" + "   AND et.entrydate>='"
				+ Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND et.entrydate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' AND et.idpath LIKE '"
				+ org.idpath.getValue() + "%'";
		JSONArray res = HRUtil.getReadPool().opensql2json_O(sqlstr);

		String scols = urlparms.get("cols");
		if (scols == null) {
			return res.toString();
		} else {
			(new CReport()).export2excel(res, scols);
			return null;
		}
	}

	// 查询离职信息
	private void getLeavInfo(CDBPool pool, JSONObject dw, boolean includchd, int empclass, Date bgdate, Date eddate) throws Exception {
		String sqlstr = "SELECT IFNULL(SUM(CASE  WHEN ljtype1=1 THEN 1 ELSE 0 END ),0) ct1,IFNULL(SUM(CASE  WHEN ljtype1=2 THEN 1 ELSE 0 END ),0) ct2,"
				+ "IFNULL(SUM(CASE  WHEN ljtype1=3 THEN 1 ELSE 0 END ),0) ct3,IFNULL(SUM(CASE  WHEN ljtype1=4 THEN 1 ELSE 0 END ),0) ct4,"
				+ "IFNULL(SUM(CASE  WHEN ljtype1=5 THEN 1 ELSE 0 END ),0) ct5,IFNULL(SUM(CASE  WHEN ljtype1=6 THEN 1 ELSE 0 END ),0) ct6,"
				+ "IFNULL(SUM(CASE  WHEN ljtype1=7 THEN 1 ELSE 0 END ),0) ct7 "
				+ "FROM `hr_leavejob` hl,hr_orgposition op WHERE hl.stat=9 and hl.ospid=op.ospid AND ljtype=2  " + " AND hl.ljdate>='"
				+ Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND hl.ljdate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		if (includchd)
			sqlstr = sqlstr + " AND hl.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND hl.orgid=" + dw.getString("orgid");

		if (empclass == 2)
			sqlstr = sqlstr + " and op.isoffjob=1";
		if (empclass == 3)
			sqlstr = sqlstr + " and op.isoffjob=2";

		List<HashMap<String, String>> datas = pool.openSql2List(sqlstr);
		dw.put("selfleave", Integer.valueOf(datas.get(0).get("ct1")));// 自离
		dw.put("quit", Integer.valueOf(datas.get(0).get("ct2")));// 辞职
		dw.put("dismiss", Integer.valueOf(datas.get(0).get("ct3")));// 辞退
		dw.put("reduce", Integer.valueOf(datas.get(0).get("ct4")));// 裁员
		dw.put("retire", Integer.valueOf(datas.get(0).get("ct5")));// 退休
		dw.put("cpst", Integer.valueOf(datas.get(0).get("ct6")));// 有经济补偿
		dw.put("ncpst", Integer.valueOf(datas.get(0).get("ct7")));// 无经济补偿
		dw.put("totalleave", dw.getInt("selfleave") + dw.getInt("quit") + dw.getInt("dismiss") + dw.getInt("reduce") + dw.getInt("retire") + dw.getInt("cpst")
				+ dw.getInt("ncpst"));// 合计离职人数

	}

	private void getEntityInfo(CDBPool pool, JSONObject dw, boolean includchd, Date bgdate, Date eddate) throws Exception {
		String sqlstr = "SELECT  IFNULL(SUM(CASE  WHEN hwc_namezl='OO' THEN 1 ELSE 0 END ),0) ftcrzrs,IFNULL(SUM(CASE  WHEN hwc_namezl<>'OO' THEN 1 ELSE 0 END ),0) tcrzrs "
				+ " FROM hr_entry  e ,hr_employee em " + " WHERE e.stat=9 AND e.er_id=em.er_id AND e.entrydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND e.entrydate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		if (includchd)
			sqlstr = sqlstr + " AND em.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND em.orgid=" + dw.getString("orgid");
		List<HashMap<String, String>> datas = pool.openSql2List(sqlstr);
		dw.put("ftcrzrs", datas.get(0).get("ftcrzrs"));// 非脱产入职数量
		dw.put("tcrzrs", datas.get(0).get("tcrzrs"));// 非脱产入职数量
		dw.put("rzrs", dw.getInt("ftcrzrs") + dw.getInt("tcrzrs"));// 非脱产入职数量

	}

	// ct:1 借入 2：借出
	private int getLeaonNum(CDBPool pool, JSONObject dw, int ct, boolean includechld, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String sqlstr = "SELECT COUNT(*) ct FROM hr_emploanbatch b,hr_emploanbatch_line bl" + " WHERE b.loanid=bl.loanid AND b.stat=9 " + " AND b.loandate>'"
				+ Systemdate.getStrDateyyyy_mm_dd(bgdate) + "' AND b.loandate<'" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "' ";
		if (ct == 1) {
			if (includechld)
				sqlstr = sqlstr + " and b.odorgid IN( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' ) ";
			else
				sqlstr = sqlstr + " and b.odorgid=" + dw.getString("orgid");
		}
		if (ct == 2) {
			if (includechld)
				sqlstr = sqlstr + " and b.neworgid IN( SELECT orgid FROM shworg WHERE idpath LIKE '" + dw.getString("idpath") + "%' ) ";
			else
				sqlstr = sqlstr + " and b.neworgid=" + dw.getString("orgid");
		}
		return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	private int getPartJobNum(CDBPool pool, JSONObject dw, boolean includechld, Date bgdate, Date eddate) throws NumberFormatException, Exception {
		String d1 = Systemdate.getStrDateyyyy_mm_dd(bgdate);
		String d2 = Systemdate.getStrDateyyyy_mm_dd(eddate);
		String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM hr_empptjob_info pt,hr_orgposition op " + " WHERE pt.`newospid`=op.`ospid` ";
		if (includechld)
			sqlstr = sqlstr + " AND op.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND op.orgid=" + dw.getString("orgid");
		sqlstr = sqlstr + " AND ((pt.startdate>='" + d1 + "' AND pt.startdate<'" + d2 + "') "
				+ "      OR(pt.enddate>='" + d1 + "' AND pt.enddate<'" + d2
				+ "') " + "      OR(pt.startdate<'" + d1 + "' AND pt.enddate>'" + d2 + "'))";
		return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	@ACOAction(eventname = "rpt_hr_entry_trylist", Authentication = true, notes = "rpt试用期人员列表")
	public String rpt_hr_entry_trylist() throws Exception {
		String[] notnull = {};
		String sqlstr = "SELECT * FROM hr_entry_try WHERE 1=1 ";
		return new CReport(sqlstr, " promotionday desc ", notnull).findReport();
	}

	@ACOAction(eventname = "rpt_hr_transfer_trylist", Authentication = true, notes = "rpt考察期人员列表")
	public String rpt_hr_transfer_trylist() throws Exception {
		String[] notnull = {};
		String sqlstr = "SELECT tf.tranftype4,tf.tranftype1,tt.* FROM hr_transfer_try tt,Hr_employee_transfer tf "
				+ "WHERE tf. emptranf_id=tt.emptranf_id and tf.ispromotioned=2 ";
		return new CReport(sqlstr, " probationdate desc ", notnull).findReport();
	}

	@ACOAction(eventname = "rpt_hrrldeclarezb", Authentication = true, notes = "rpt关联关系占比")
	public String rpt_hrrldeclarezb() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");

		int zbtp = Integer.valueOf(CorUtil.hashMap2Str(urlparms, "zbtp", "需要参数zbtp"));

		String orgcode = jporgcode.getParmvalue();
		String sqlstr1 = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr1);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");

		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		dws.add(0, org.toJsonObj());
		JSONArray rst = new JSONArray();
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);

			int smemp = getSumEmoloyssct(he, dw);
			int rrs1 = getRLCT(he, dw, 1, 1);
			int rrs2 = getRLCT(he, dw, 2, 1);

			String zb1 = ((smemp > 0) ? (dec.format((float) rrs1 / smemp)) : "0.000");
			String zb2 = ((smemp > 0) ? (dec.format((float) rrs2 / smemp)) : "0.000");

			JSONObject row1 = JSONObject.fromObject(dw.toString());
			row1.put("smemp", smemp);
			row1.put("rrs", rrs1);
			row1.put("zb", zb1);
			row1.put("ndgk", 1);
			rst.add(row1);

			JSONObject row2 = JSONObject.fromObject(dw.toString());
			row2.put("smemp", smemp);
			row2.put("rrs", rrs2);
			row2.put("zb", zb2);
			row2.put("ndgk", 2);
			rst.add(row2);
		}
		String scols = urlparms.get("cols");
		if (scols == null) {
			// System.out.println(rst.toString());
			return rst.toString();
		} else {
			(new CReport()).export2excel(rst, scols);
			return null;
		}
	}

	/**
	 * @param tp
	 * 类型 1 亲属 2 其他
	 * @param rm
	 * 管控 1不管控 2 管控
	 * @return
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	private int getRLCT(Hr_employee he, JSONObject dw, int tp, int rm) throws NumberFormatException, Exception {
		String sqlstrorg = "SELECT er_id FROM hr_employee WHERE idpath LIKE '" + dw.getString("idpath") + "%' AND empstatid NOT IN(6,11,12,13)";
		String sqlstr = "SELECT IFNULL(COUNT(*),0) ct FROM "
				+ " (SELECT DISTINCT IF(d.er_id<d.rer_id,d.er_id,d.rer_id) erid1, IF(d.er_id>d.rer_id,d.er_id,d.rer_id) erid2 " + " FROM hrrl_declare  d"
				+ " WHERE d.er_id IN(" + sqlstrorg + " ) AND d.rer_id IN(" + sqlstrorg + " )";
		if (rm == 1) {
			sqlstr = sqlstr + " AND d.useable=1 AND d.rctrms=" + tp + " AND d.rltype1=1) tb";
		} else
			sqlstr = sqlstr + " AND d.useable=1 AND d.rctrms=" + tp + " AND d.rltype1<>1) tb";
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	private int getSumEmoloyssct(Hr_employee he, JSONObject dw) throws Exception {
		String sqlstr = "SELECT count(*) ct FROM hr_employee WHERE usable=1 ";
		sqlstr = sqlstr + " AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) ";
		sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	@ACOAction(eventname = "rpt_transfer_try_fx", Authentication = true, notes = "rpt考察期统计报表")
	public String rpt_transfer_try_fx() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(urlparms.get("parms"));
		JSONParm jporgcode = CjpaUtil.getParm(jps, "orgcode");
		if (jporgcode == null)
			throw new Exception("需要参数【orgcode】");
		JSONParm jpbgdate = CjpaUtil.getParm(jps, "bgdate");
		if (jpbgdate == null)
			throw new Exception("需要参数【bgdate】");
		JSONParm jpeddate = CjpaUtil.getParm(jps, "eddate");
		if (jpeddate == null)
			throw new Exception("需要参数【eddate】");
		String orgcode = jporgcode.getParmvalue();
		String sqlstr = "select * from shworg where code='" + orgcode + "'";
		Shworg org = new Shworg();
		org.findBySQL(sqlstr);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");
		String bgdate = jpbgdate.getParmvalue();
		String eddate = jpeddate.getParmvalue();

		sqlstr = rpt_transfer_try_sqlstr.replace("<*orgid*>", org.orgid.getValue());
		sqlstr = sqlstr.replace("<*bgdate*>", bgdate);
		sqlstr = sqlstr.replace("<*eddate*>", eddate);
		String[] ignParms = { "orgcode", "bgdate", "eddate" };
		String scols = urlparms.get("cols");
		JSONObject rst = new CReport(sqlstr, null).findReport2JSON_O(ignParms);
		JSONArray rows = rst.getJSONArray("rows");
		for (int i = 0; i < rows.size(); i++) {
			JSONObject jo = rows.getJSONObject(i);
			int ygzz = jo.getInt("ygzz");// 总人数
			jo.put("yjzzl", (ygzz == 0) ? 0 : (jo.getInt("yjzz") / Float.valueOf(ygzz)));// 转载率
			jo.put("yqzzl", (ygzz == 0) ? 0 : (jo.getInt("yqzz") / Float.valueOf(ygzz)));// 延期率
			jo.put("bhgzz", (ygzz == 0) ? 0 : (jo.getInt("bhgzz") / Float.valueOf(ygzz)));// 不合格率
		}
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(rst.getJSONArray("rows"), scols);
			return null;
		}
	}

	@ACOAction(eventname = "rpt_entrybatch_list", Authentication = true, notes = "rpt试用期批量转正列表报表")
	public String rpt_entrybatch_list() throws Exception {
		String idpathw = CSContext.getIdpathwhere().replace("idpath", "o.idpath");
//		String sqlstr = "SELECT p.ebpcode,p.pbttype2,pl.*" + " FROM `hr_entrybatch_prob` p,`hr_entrybatch_probline` pl"
//				+ " WHERE p.`ebptid`=pl.`ebptid` AND p.`stat`=9";
//		
//		sqlstr = sqlstr + " AND (" + "   EXISTS(SELECT 1 FROM shworg o WHERE p.odorgid=o.orgid " + idpathw + ")" + "   OR "
//				+ "   EXISTS(SELECT 1 FROM shworg o WHERE p.neworgid=o.orgid " + idpathw + ")" + " )";

		String sqlstr = "SELECT pl.*,p.ebpcode,p.pbttype2 " +
				"FROM hr_entrybatch_prob p, hr_entrybatch_probline pl,shworg o " +
				"WHERE p.ebptid = pl.ebptid AND p.stat = 9 " +
				"  AND p.orgid=o.orgid " + idpathw;

		String orderby = " pbtdate desc ";

		return new CReport(sqlstr, orderby, null).findReport(null, null);
	}

	@ACOAction(eventname = "rpt_hrrsydtj", Authentication = true, notes = "rpt人员异动数据统计报表")
	public String rpt_hrrsydtj() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");

		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 24);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);

		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);
		Shworg org = new Shworg();

		for (String yearmonth : yearmonths) {
			int s_sumpemnum = 0, s_tcrs = 0, s_ttcrs = 0, s_offjobquota = 0, s_noffjobquota = 0, s_quota = 0, s_tccqbrs = 0, s_ftccqbrs = 0, s_cqbrs = 0;
			int s_jsrs = 0, s_jzrs = 0, s_nbddrs = 0, s_drrs = 0, s_dcrs = 0, s_plddrs = 0, s_pldrrs = 0, s_pldcrs = 0, s_jdrrs = 0, s_jdcrs = 0, s_partjbrs = 0;
			int s_selfleave = 0, s_quit = 0, s_dismiss = 0, s_reduce = 0, s_retire = 0, s_cpst = 0, s_ncpst = 0, s_totalleave = 0;
			int s_rzrs = 0, s_tcrzrs = 0, s_ftcrzrs = 0;
			for (int i = 0; i < dws.size(); i++) {
				JSONObject dw = dws.getJSONObject(i);
				String pyearmonth = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(Systemdate.getDateByStr(yearmonth), -1), "yyyy-MM");

				String eyearmonth = dw.getString("yearmonth");
				if (yearmonth.equalsIgnoreCase(eyearmonth)) {
					Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(yearmonth + "-01")));
					Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
					// boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
					boolean includechld = dw.getInt("_incc") == 1;
					// 且
					// 为自身机构时候
					// 不查询子机构数据
					HashMap<String, String> mp = COPermRpt1.getTrueEmpCts(dw, pyearmonth, yearmonth, empclass, includechld);
					dw.put("sumpemnum", mp.get("emnum"));// 总人数
					dw.put("tcrs", mp.get("emnumoff"));// 脱产人数
					dw.put("ttcrs", mp.get("emnumnoff"));// 非脱产人数
					s_sumpemnum += dw.getInt("sumpemnum");
					s_tcrs += dw.getInt("tcrs");
					s_ttcrs += dw.getInt("ttcrs");

					dw.put("offjobquota", COPermRpt1.getOffQuota(dw, yearmonth, includechld));// 脱产编制
					dw.put("noffjobquota", COPermRpt1.getNotOffQuota(dw, yearmonth, includechld));// 非脱产编制
					dw.put("quota", dw.getInt("offjobquota") + dw.getInt("noffjobquota"));// 总编制数
					s_offjobquota += dw.getInt("offjobquota");
					s_noffjobquota += dw.getInt("noffjobquota");
					s_quota += dw.getInt("quota");

					int tccbrs = dw.getInt("offjobquota") - dw.getInt("tcrs"); // 脱产超编人数
					// if (tccbrs < 0)
					// tccbrs = 0;
					// int tcqbrs = dw.getInt("offjobquota") - dw.getInt("tcrs"); //
					// 脱产缺编人数
					// if (tcqbrs < 0)
					// tcqbrs = 0;
					int tccqbrs = tccbrs;// + tcqbrs;// 脱产超缺编人数
					dw.put("tccqbrs", tccqbrs);// 脱产超缺编人数
					s_tccqbrs += dw.getInt("tccqbrs");

					int ftccbrs = dw.getInt("noffjobquota") - dw.getInt("ttcrs"); // 非脱产超编人数
					// if (ftccbrs < 0)
					// ftccbrs = 0;
					// int ftcqbrs = dw.getInt("noffjobquota") - dw.getInt("ttcrs"); //
					// 非脱产缺编人数
					// if (ftcqbrs < 0)
					// ftcqbrs = 0;
					int ftccqbrs = ftccbrs;// +ftcqbrs ;// 非脱产超缺编人数
					dw.put("ftccqbrs", ftccqbrs);// 非脱产超缺编人数
					dw.put("cqbrs", tccqbrs + ftccqbrs);// 超缺编总人数
					s_ftccqbrs += dw.getInt("ftccqbrs");
					s_cqbrs += dw.getInt("cqbrs");

					int jsrs = getTransferJSOrJZ(HRUtil.getReadPool(), dw, 1, includechld, empclass, spname, findtype, bgdate, eddate);
					s_jsrs += jsrs;
					dw.put("jsrs", jsrs);// 晋升
					int jzrs = getTransferJSOrJZ(HRUtil.getReadPool(), dw, 2, includechld, empclass, spname, findtype, bgdate, eddate);
					s_jzrs += jzrs;
					dw.put("jzrs", jzrs);// 降职
					int nbddrs = getTransferNB(HRUtil.getReadPool(), dw, includechld, empclass, spname, findtype, bgdate, eddate);
					s_nbddrs += nbddrs;
					dw.put("nbddrs", nbddrs);// 内部调动
					int drrs = getTransferDROrDC(HRUtil.getReadPool(), dw, 1, includechld, empclass, spname, findtype, bgdate, eddate);
					s_drrs += drrs;
					dw.put("drrs", drrs);// 调入
					int dcrs = getTransferDROrDC(HRUtil.getReadPool(), dw, 2, includechld, empclass, spname, findtype, bgdate, eddate);
					s_dcrs += dcrs;
					dw.put("dcrs", dcrs);// 调出
					int plddrs = getTransferBtchDROrDC(HRUtil.getReadPool(), dw, 0, includechld, empclass, spname, findtype, bgdate, eddate);
					s_plddrs += plddrs;
					dw.put("plddrs", plddrs);// 批调
					int pldrrs = getTransferBtchDROrDC(HRUtil.getReadPool(), dw, 1, includechld, empclass, spname, findtype, bgdate, eddate);
					s_pldrrs += pldrrs;
					dw.put("pldrrs", pldrrs);// 批调入
					int pldcrs = getTransferBtchDROrDC(HRUtil.getReadPool(), dw, 2, includechld, empclass, spname, findtype, bgdate, eddate);
					s_pldcrs += pldcrs;
					dw.put("pldcrs", pldcrs);// 批调出

					getLeavInfo(HRUtil.getReadPool(), dw, includechld, empclass, bgdate, eddate);// 离职信息
					s_selfleave += dw.getInt("selfleave");
					s_quit += dw.getInt("quit");
					s_dismiss += dw.getInt("dismiss");
					s_reduce += dw.getInt("reduce");
					s_retire += dw.getInt("retire");
					s_cpst += dw.getInt("cpst");
					s_ncpst += dw.getInt("ncpst");
					s_totalleave += dw.getInt("totalleave");

					getEntityInfo(HRUtil.getReadPool(), dw, includechld, bgdate, eddate);// 入职信息
					s_rzrs += dw.getInt("rzrs");
					s_tcrzrs += dw.getInt("tcrzrs");
					s_ftcrzrs += dw.getInt("ftcrzrs");

					int jdrrs = getLeaonNum(HRUtil.getReadPool(), dw, 1, includechld, bgdate, eddate);// 借入
					int jdcrs = getLeaonNum(HRUtil.getReadPool(), dw, 2, includechld, bgdate, eddate);// 借出
					dw.put("jdrrs", jdrrs);// 借入
					dw.put("jdcrs", jdcrs);// 借出
					s_jdrrs += jdrrs;
					s_jdcrs += jdcrs;

					int partjbrs = getPartJobNum(HRUtil.getReadPool(), dw, includechld, bgdate, eddate);
					dw.put("partjbrs", partjbrs);// 兼职人数
					s_partjbrs += partjbrs;
				}
//如果需要加小结
			}
		}

//		JSONObject srow = new JSONObject();
//		srow.put("sumpemnum", s_sumpemnum);
//		srow.put("tcrs", s_tcrs);
//		srow.put("ttcrs", s_ttcrs);
//		srow.put("offjobquota", s_offjobquota);
//		srow.put("noffjobquota", s_noffjobquota);
//		srow.put("quota", s_quota);
//		srow.put("tccqbrs", s_tccqbrs);
//		srow.put("ftccqbrs", s_ftccqbrs);
//		srow.put("cqbrs", s_cqbrs);
//		srow.put("jsrs", s_jsrs);
//		srow.put("jzrs", s_jzrs);
//		srow.put("nbddrs", s_nbddrs);
//		srow.put("drrs", s_drrs);
//		srow.put("dcrs", s_dcrs);
//		srow.put("plddrs", s_plddrs);
//		srow.put("pldrrs", s_pldrrs);
//		srow.put("pldcrs", s_pldcrs);
//		srow.put("jdrrs", s_jdrrs);
//		srow.put("jdcrs", s_jdcrs);
//		srow.put("partjbrs", s_partjbrs);
//
//		srow.put("selfleave", s_selfleave);
//		srow.put("quit", s_quit);
//		srow.put("dismiss", s_dismiss);
//		srow.put("reduce", s_reduce);
//		srow.put("retire", s_retire);
//		srow.put("cpst", s_cpst);
//		srow.put("ncpst", s_ncpst);
//		srow.put("totalleave", s_totalleave);
//
//		srow.put("rzrs", s_rzrs);
//		srow.put("tcrzrs", s_tcrzrs);
//		srow.put("ftcrzrs", s_ftcrzrs);

//		srow.put("orgname", "合计");
//		JSONArray footer = new JSONArray();
//		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
//		rst.put("footer", footer);

		String scols = parms.get("cols");
		if (scols == null) {
			// System.out.println("rst:" + rst.toString());
			return rst.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	String rpt_transfer_try_sqlstr = "SELECT org.orgid,org.extorgname,org.idpath,COUNT(*) zrs, "
			+ " IFNULL(SUM(CASE  WHEN (probationdate>'<*bgdate*>' AND probationdate<'<*eddate*>') OR (delaypromotionday>'<*bgdate*>' AND delaypromotionday<'<*eddate*>') THEN 1 ELSE 0 END ),0) ygzz,"
			+ " IFNULL(SUM(CASE  WHEN trystat=4 THEN 1 ELSE 0 END ),0) yjzz," + " IFNULL(SUM(CASE  WHEN trystat=3 THEN 1 ELSE 0 END ),0) yqzz,"
			+ " IFNULL(SUM(CASE  WHEN trystat=2 THEN 1 ELSE 0 END ),0) gqzz," + " IFNULL(SUM(CASE  WHEN trystat=5 THEN 1 ELSE 0 END ),0) bhgzz"
			+ " FROM `hr_transfer_try` t,(" + "  SELECT orgid,extorgname,idpath"
			+ "  FROM shworg WHERE stat=1 AND usable=1 AND (superid=<*orgid*> OR orgid=<*orgid*>) " + "  ORDER BY orgid" + " ) org"
			+ " WHERE t.`idpath` LIKE CONCAT(org.idpath,'%') "
			+ " AND tranfcmpdate>'<*bgdate*>' AND tranfcmpdate<'<*eddate*>' AND (probationdate>'<*bgdate*>' OR delaypromotionday>'<*bgdate*>')"
			+ " GROUP BY org.orgid";

}
