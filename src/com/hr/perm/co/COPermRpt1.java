package com.hr.perm.co;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import com.hr.base.co.COBaseRpt;
import com.hr.perm.entity.Hr_employee;
import com.hr.util.HRUtil;

/**
 * @author Administrator
 *
 */
@ACO(coname = "web.hr.permrpt1")
public class COPermRpt1 {

	private static String empstids = "1,2,3,4,5,7,8";// 需要计算编制的人事状态 "2,3,4,5";

	/**
	 * 添加扩展的条件，职类 职群 职种 职级
	 * 
	 * @param tbaname
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public static String parExtSQL(String tbaname, String sqlstr) throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String hwc_namezl = CorUtil.getJSONParmValue(jps, "hwc_namezl");
		if ((hwc_namezl != null) && (!hwc_namezl.isEmpty())) {
			if (tbaname == null)
				sqlstr = sqlstr + " and hwc_namezl like '%" + hwc_namezl + "%'";
			else
				sqlstr = sqlstr + " and " + tbaname + ".hwc_namezl like '%" + hwc_namezl + "%'";
		}
		String hwc_namezq = CorUtil.getJSONParmValue(jps, "hwc_namezq");
		if ((hwc_namezq != null) && (!hwc_namezq.isEmpty())) {
			if (tbaname == null)
				sqlstr = sqlstr + " and hwc_namezq like '%" + hwc_namezq + "%'";
			else
				sqlstr = sqlstr + " and " + tbaname + ".hwc_namezq like '%" + hwc_namezq + "%'";
		}
		String hwc_namezz = CorUtil.getJSONParmValue(jps, "hwc_namezz");
		if ((hwc_namezz != null) && (!hwc_namezz.isEmpty())) {
			if (tbaname == null)
				sqlstr = sqlstr + " and hwc_namezz like '%" + hwc_namezz + "%'";
			else
				sqlstr = sqlstr + " and " + tbaname + ".hwc_namezz like '%" + hwc_namezz + "%'";
		}

		String lv_num_sql = CorUtil.getJSONParmValue(jps, "lv_num_sql");
		if ((lv_num_sql != null) && (!lv_num_sql.isEmpty())) {
			lv_num_sql = (tbaname == null) ? lv_num_sql : lv_num_sql.replace("lv_num", tbaname + ".lv_num");
			sqlstr = sqlstr + " " + lv_num_sql;
		}
		return sqlstr;
	}

	@ACOAction(eventname = "findEmployeeZWXZPotal", Authentication = true, notes = "人事职位性质分析-门户")
	public String findEmployeeZWXZPotal() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid");
		orgid = (orgid == null) ? CSContext.getDefaultOrgID() : orgid;
		String strempclass = CorUtil.hashMap2Str(parms, "empclass");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		String sqlstr = "select  SUM(1) sumpeo," + " IFNULL(SUM(CASE WHEN e.emnature='脱产' THEN 1 ELSE 0 END),0) ow,"
				+ " IFNULL(SUM(CASE WHEN e.emnature='非脱产' THEN 1 ELSE 0 END),0) notow " + " FROM hr_employee e WHERE e.empstatid IN (" + empstids + ")  "
				+ " AND e.idpath LIKE '" + org.idpath.getValue() + "%'";
		if (empclass == 2)
			sqlstr = sqlstr + " and e.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and e.emnature='非脱产'";

		JSONArray rows = HRUtil.getReadPool().opensql2json_O(sqlstr);
		JSONObject rst = new JSONObject();
		rst.put("rows", rows);
		rst.put("org", org.extorgname.getValue());
		return rst.toString();
	}

	// /////////////////学历开始///////////////////////////////////////////
	@ACOAction(eventname = "findEmployeeXL", Authentication = true, notes = "人事学历分析")
	public String findEmployeeXL() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");

		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		List<String> yearmonths = new ArrayList<String>();
		yearmonths.add(yearmonth);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		return findEmployeeXLAndQS(dws, orgid, yearmonths, empclass, includechild, parms).toString();
	}

	@ACOAction(eventname = "findEmployeeXLPotal", Authentication = true, notes = "人事学历分析-门户")
	public String findEmployeeXLPotal() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid");
		orgid = (orgid == null) ? CSContext.getDefaultOrgID() : orgid;

		String strempclass = CorUtil.hashMap2Str(parms, "empclass");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");

		String sqlstr = "select  SUM(1) sumpeo," + "  SUM(CASE WHEN degree IN (1,2) THEN 1 ELSE 0 END) ss," + "  SUM(CASE WHEN degree =3 THEN 1 ELSE 0 END) bk,"
				+ "  SUM(CASE WHEN degree =4 THEN 1 ELSE 0 END) dz," + "  SUM(CASE WHEN degree =5 THEN 1 ELSE 0 END) gz,"
				+ "  SUM(CASE WHEN degree =6 or degree =7 THEN 1 ELSE 0 END) zzzj," + "  SUM(CASE WHEN degree IN(8,9,10,0) THEN 1 ELSE 0 END) cz,"
				+ "  SUM(CASE WHEN degree IS NULL  THEN 1 ELSE 0 END) qt " + " FROM hr_employee e WHERE e.empstatid IN (2, 3, 4, 5)  " + " AND e.idpath LIKE '"
				+ org.idpath.getValue() + "%'";

		if (empclass == 2)
			sqlstr = sqlstr + " and e.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and e.emnature='非脱产'";

		JSONArray rows = HRUtil.getReadPool().opensql2json_O(sqlstr);
		JSONObject rst = new JSONObject();
		rst.put("rows", rows);
		rst.put("org", org.extorgname.getValue());
		return rst.toString();
	}

	@ACOAction(eventname = "findEmployeeXL_QS", Authentication = true, notes = "人事学历趋势分析")
	public String findEmployeeXL_QS() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth_begin", "需要参数【yearmonth_begin】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 12);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		JSONObject rst = findEmployeeXLAndQS(dws, orgid, yearmonths, empclass, includechild, parms);

		dws = HRUtil.getOrgsByOrgid(orgid, false, yearmonths);
		JSONArray chartdata = buildXLChartData(findEmployeeXLAndQS(dws, orgid, yearmonths, empclass, false, parms).getJSONArray("rows"));
		rst.put("chartdata", chartdata);
		return rst.toString();
	}

	private JSONArray buildXLChartData(JSONArray rows) {
		JSONArray rst = new JSONArray();
		rst.add(buildYearChartDateRow(rows, "ss", "硕士及以上"));
		rst.add(buildYearChartDateRow(rows, "bk", "本科"));
		rst.add(buildYearChartDateRow(rows, "dz", "大专"));
		rst.add(buildYearChartDateRow(rows, "gz", "高中"));
		rst.add(buildYearChartDateRow(rows, "zz", "中专"));
		rst.add(buildYearChartDateRow(rows, "zj", "中技"));
		rst.add(buildYearChartDateRow(rows, "cz", "初中及以下"));
		return rst;
	}

	private JSONObject buildYearChartDateRow(JSONArray rows, String fdname, String lable) {
		JSONObject ss = new JSONObject();
		ss.put("label", lable);
		JSONArray ssArray = new JSONArray();
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			JSONArray cd = new JSONArray();
			String yearmonth = row.getString("yearmonth");
//			yearmonth = yearmonth.replace("-", "");
//			cd.add(Integer.valueOf(yearmonth));
			Date dt = Systemdate.getDateByStr(yearmonth + "-01");
			dt = Systemdate.dateMonthAdd(dt, 1);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			cd.add(calendar.getTimeInMillis());
			if (row.has(fdname))
				cd.add(row.getInt(fdname));
			else
				cd.add(0);
			ssArray.add(cd);
		}
		ss.put("data", ssArray);
		return ss;
	}

	/**
	 * 学历及趋势分析
	 * 
	 * @param dws
	 * @param yearmonths
	 * @param includechild
	 * @param parms
	 * @return
	 * @throws Exception
	 */
	private JSONObject findEmployeeXLAndQS(JSONArray dws, String orgid, List<String> yearmonths, int empclass, boolean includechild,
			HashMap<String, String> parms) throws Exception {

		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		int s_sumpeo = 0, s_ss = 0, s_bk = 0, s_dz = 0, s_gz = 0, s_zz = 0, s_zj = 0, s_cz = 0, s_qt = 0, s_zzzj = 0;
		float bs_ss = 0, bs_bk = 0, bs_dz = 0, bs_gz = 0, bs_zz = 0, bs_zj = 0, bs_cz = 0, bs_qt = 0, bs_zzzj = 0;
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);// 机构 年月
			boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
			// 且
			// 为自身机构时候
			// 不查询子机构数据
			List<String> yms = new ArrayList<String>();
			yms.add(dw.getString("yearmonth"));
			JSONArray cts = getDegreeEmoloyCts(he, dw, yms, empclass, spname, findtype, includechld);
			for (int j = 0; j < cts.size(); j++) {
				JSONObject ct = cts.getJSONObject(j);
				int sumpeo = ct.getInt("sumpeo");// getDegreeEmoloyssct(he, dw,
				// null, yearmonth,
				// includechld);// 总人数
				s_sumpeo += sumpeo;
				int ss = ct.getInt("ss");// getDegreeEmoloyssct(he, dw, "1,2",
				// yearmonth, includechld);// 硕士及以上
				s_ss += ss;
				int bk = ct.getInt("bk");// getDegreeEmoloyssct(he, dw, "3",
				// yearmonth, includechld);// 本科
				s_bk += bk;
				int dz = ct.getInt("dz");// getDegreeEmoloyssct(he, dw, "4",
				// yearmonth, includechld);// 大专
				s_dz += dz;
				int gz = ct.getInt("gz");// getDegreeEmoloyssct(he, dw, "5",
				// yearmonth, includechld);// 高中
				s_gz += gz;
				int zz = ct.getInt("zz");// getDegreeEmoloyssct(he, dw, "6",
				// yearmonth, includechld);// 中专
				s_zz += zz;
				int zj = ct.getInt("zj");// getDegreeEmoloyssct(he, dw, "7",
				// yearmonth, includechld);// 中技
				s_zj += zj;

				int zzzj = ct.getInt("zz") + ct.getInt("zj");
				s_zzzj += zzzj;

				int cz = ct.getInt("cz");// getDegreeEmoloyssct(he, dw,
				// "8,9,10,0", yearmonth,
				// includechld);// 高中
				s_cz += cz;
				int qt = ct.getInt("qt");// getDegreeEmoloyssct(he, dw, "0",
				// yearmonth, includechld);// 其他
				s_qt += qt;
				dw.put("sumpeo", sumpeo);
				dw.put("ss", ss);
				dw.put("bk", bk);
				dw.put("dz", dz);
				dw.put("gz", gz);
				dw.put("zz", zz);
				dw.put("zj", zj);
				dw.put("zzzj", zzzj);
				dw.put("cz", cz);
				dw.put("qt", qt);
				dw.put("bss", ((sumpeo > 0) ? (dec.format((float) ss / sumpeo)) : "0.000"));
				dw.put("bbk", ((sumpeo > 0) ? (dec.format((float) bk / sumpeo)) : "0.000"));
				dw.put("bdz", ((sumpeo > 0) ? (dec.format((float) dz / sumpeo)) : "0.000"));
				dw.put("bgz", ((sumpeo > 0) ? (dec.format((float) gz / sumpeo)) : "0.000"));
				dw.put("bcz", ((sumpeo > 0) ? (dec.format((float) cz / sumpeo)) : "0.000"));
				dw.put("bzz", ((sumpeo > 0) ? (dec.format((float) zz / sumpeo)) : "0.000"));
				dw.put("bzj", ((sumpeo > 0) ? (dec.format((float) zj / sumpeo)) : "0.000"));
				dw.put("bzzzj", ((sumpeo > 0) ? (dec.format((float) zzzj / sumpeo)) : "0.000"));
				dw.put("bqt", ((sumpeo > 0) ? (dec.format((float) qt / sumpeo)) : "0.000"));
			}
		}

		bs_ss = (s_sumpeo == 0) ? 0 : ((float) s_ss / s_sumpeo);
		bs_bk = (s_sumpeo == 0) ? 0 : ((float) s_bk / s_sumpeo);
		bs_gz = (s_sumpeo == 0) ? 0 : ((float) s_gz / s_sumpeo);
		bs_cz = (s_sumpeo == 0) ? 0 : ((float) s_cz / s_sumpeo);
		bs_zz = (s_sumpeo == 0) ? 0 : ((float) s_zz / s_sumpeo);
		bs_zj = (s_sumpeo == 0) ? 0 : ((float) s_zj / s_sumpeo);
		bs_zzzj = (s_sumpeo == 0) ? 0 : ((float) s_zzzj / s_sumpeo);
		bs_dz = (s_sumpeo == 0) ? 0 : ((float) s_dz / s_sumpeo);
		bs_qt = (s_sumpeo == 0) ? 0 : ((float) s_qt / s_sumpeo);

		JSONObject srow = new JSONObject();
		srow.put("sumpeo", s_sumpeo);
		srow.put("bss", bs_ss);
		srow.put("bbk", bs_bk);
		srow.put("bgz", bs_gz);
		srow.put("bcz", bs_cz);
		srow.put("bzz", bs_zz);
		srow.put("bzj", bs_zj);
		srow.put("bzzzj", bs_zzzj);
		srow.put("bdz", bs_dz);
		srow.put("bqt", bs_qt);

		srow.put("ss", s_ss);
		srow.put("bk", s_bk);
		srow.put("dz", s_dz);
		srow.put("gz", s_gz);
		srow.put("zz", s_zz);
		srow.put("zj", s_zj);
		srow.put("zzzj", s_zzzj);
		srow.put("cz", s_cz);
		srow.put("qt", s_qt);

		srow.put("orgname", "合计");
		JSONArray footer = new JSONArray();
		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
		rst.put("footer", footer);
		String scols = parms.get("cols");
		if (scols == null) {
			return rst;
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	/**
	 * 学历Ex
	 * 
	 * @param he
	 * @param dw
	 * @param yms
	 * 月度列表
	 * @param includchd
	 * @return
	 * @throws Exception
	 */
	private JSONArray getDegreeEmoloyCts(Hr_employee he, JSONObject dw, List<String> yms, int empclass, String spname, int findtype, boolean includchd) throws Exception {
		String yearmonths = "";
		for (String ym : yms) {
			yearmonths += "'" + ym + "',";
		}
		if (yearmonths.length() > 0)
			yearmonths = yearmonths.substring(0, yearmonths.length() - 1);

		boolean isnowmonth = (yms.size() == 1) && isNowMonth(yearmonths);

		String sqlstr = "SELECT ";
		if (isnowmonth)
			sqlstr = sqlstr + "  '" + yms.get(0) + "' yearmonth,";
		else
			sqlstr = sqlstr + "  yearmonth,";
		sqlstr = sqlstr + "  SUM(1) sumpeo," + "  SUM(CASE WHEN degree IN (1,2) THEN 1 ELSE 0 END) ss," + "  SUM(CASE WHEN degree =3 THEN 1 ELSE 0 END) bk,"
				+ "  SUM(CASE WHEN degree =4 THEN 1 ELSE 0 END) dz," + "  SUM(CASE WHEN degree =5 THEN 1 ELSE 0 END) gz,"
				+ "  SUM(CASE WHEN degree =6 THEN 1 ELSE 0 END) zz," + "  SUM(CASE WHEN degree =7 THEN 1 ELSE 0 END) zj,"
				+ "  SUM(CASE WHEN degree IN(8,9,10,0) THEN 1 ELSE 0 END) cz," + "  SUM(CASE WHEN degree IS NULL  THEN 1 ELSE 0 END) qt";
		if (isnowmonth)
			sqlstr = sqlstr + " FROM hr_employee e";
		else
			sqlstr = sqlstr + " FROM hr_month_employee e";
		sqlstr = sqlstr + " WHERE e.`empstatid` IN(" + empstids + ") ";

		if (empclass == 2)
			sqlstr = sqlstr + " and e.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and e.emnature='非脱产'";
		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and e.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and e.sp_name like '%" + spname + "%'";
		}
		if (yms.size() == 1) {
			if (!isnowmonth)
				sqlstr = sqlstr + " and yearmonth=" + yearmonths + "";
		} else if (yms.size() > 1)
			sqlstr = sqlstr + "AND yearmonth IN (" + yearmonths + ") ";
		else
			throw new Exception("月度长度不能为0");
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");

		sqlstr = parExtSQL("e", sqlstr);

		sqlstr = sqlstr + " GROUP BY yearmonth";
		return HRUtil.getReadPool().opensql2json_O(sqlstr);
	}

	/**
	 * 学历
	 * 
	 * @param he
	 * @param dw
	 * @param degree
	 * @param yearmonth
	 * @param includchd
	 * @return
	 * @throws Exception
	 */
	// private int getDegreeEmoloyssct(Hr_employee he, JSONObject dw, String
	// degree, String yearmonth, boolean includchd) throws Exception {
	// String sqlstr = "SELECT count(*) ct FROM hr_month_employee WHERE
	// yearmonth='" + yearmonth + "' ";
	// if ("0".equals(degree))
	// sqlstr = sqlstr + " AND degree is null ";
	// else if (degree != null)
	// sqlstr = sqlstr + " AND degree in(" + degree + ") ";
	//
	// sqlstr = sqlstr + " AND empstatid IN(SELECT statvalue FROM
	// hr_employeestat WHERE usable=1 AND isquota=1) ";
	// if (includchd)
	// sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
	// else
	// sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
	// return Integer.valueOf(he.pool.openSql2List(sqlstr).get(0).get("ct"));
	// }

	// /////////////////学历结束///////////////////////////////////////////

	@ACOAction(eventname = "findEmployeeAge", Authentication = true, notes = "人事年龄分析")
	public String findEmployeeAge() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = new ArrayList<String>();
		yearmonths.add(yearmonth);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		return findEmployeeAgeAndQS(dws, orgid, yearmonths, empclass, includechild, parms).toString();
	}

	@ACOAction(eventname = "findEmployeeAgePotal", Authentication = true, notes = "人事年龄分析门户")
	public String findEmployeeAgePotal() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid");
		orgid = (orgid == null) ? CSContext.getDefaultOrgID() : orgid;
		String strempclass = CorUtil.hashMap2Str(parms, "empclass");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		String nowdatestr = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM");
		String sqlstr = "select  IFNULL(SUM(1),0) sumpeo," + "   IFNULL(SUM(CASE WHEN age>=50 THEN 1 ELSE 0 END),0) f50,"
				+ "   IFNULL(SUM(CASE WHEN age>=40 AND age<50 THEN 1 ELSE 0 END),0) f40,"
				+ "   IFNULL(SUM(CASE WHEN age>=30 AND age<40 THEN 1 ELSE 0 END),0) f30,"
				+ "   IFNULL(SUM(CASE WHEN age>=18 AND age<30 THEN 1 ELSE 0 END),0) f20," + "   IFNULL(SUM(CASE WHEN age<18 THEN 1 ELSE 0 END),0) f00 "
				+ " FROM  (" + " SELECT he.*, (DATE_FORMAT(FROM_DAYS(TO_DAYS(CONCAT('" + nowdatestr + "','-01')) - TO_DAYS(birthday)),'%Y') + 0) AS age  "
				+ " FROM hr_employee he where `empstatid` IN(" + empstids + ") ";
		if (empclass == 2)
			sqlstr = sqlstr + " and he.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and he.emnature='非脱产'";
		sqlstr = sqlstr + " AND he.idpath LIKE '" + org.idpath.getValue() + "%') tb ";
		JSONArray rows = HRUtil.getReadPool().opensql2json_O(sqlstr);
		JSONObject rst = new JSONObject();
		rst.put("rows", rows);
		rst.put("org", org.extorgname.getValue());
		return rst.toString();
	}

	@ACOAction(eventname = "findEmployeeAge_QS", Authentication = true, notes = "人事年龄趋势分析")
	public String findEmployeeAge_QS() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth_begin", "需要参数【yearmonth_begin】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 12);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		JSONObject rst = findEmployeeAgeAndQS(dws, orgid, yearmonths, empclass, includechild, parms);
		dws = HRUtil.getOrgsByOrgid(orgid, false, yearmonths);
		JSONArray chartdata = buildAgeChartData(findEmployeeAgeAndQS(dws, orgid, yearmonths, empclass, false, parms).getJSONArray("rows"));
		rst.put("chartdata", chartdata);
		return rst.toString();
	}

	private JSONObject findEmployeeAgeAndQS(JSONArray dws, String orgid, List<String> yearmonths, int empclass, boolean includechild,
			HashMap<String, String> parms) throws Exception {

		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		int s_sumpeo = 0, s_f50 = 0, s_f40 = 0, s_f30 = 0, s_f20 = 0, s_f00 = 0;
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);// 机构 年月
			boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
			// 且
			// 为自身机构时候
			// 不查询子机构数据
			List<String> yms = new ArrayList<String>();
			yms.add(dw.getString("yearmonth"));
			JSONArray cts = getAgetEmpCts(he, dw, yms, empclass, spname, findtype, includechld);
			for (int j = 0; j < cts.size(); j++) {
				JSONObject ct = cts.getJSONObject(j);
				int sumpeo = ct.getInt("sumpeo");// getAgeEmoloyct(he, dw, -1,
				// -1, yearmonth,
				// includechld);// 总人数
				s_sumpeo += sumpeo;
				int f50 = ct.getInt("f50");// getAgeEmoloyct(he, dw, 50, -1,
				// yearmonth, includechld);// 50岁及以上
				s_f50 += f50;
				int f40 = ct.getInt("f40");// getAgeEmoloyct(he, dw, 40, 50,
				// yearmonth, includechld);//
				// 40(含)-50岁
				s_f40 += f40;
				int f30 = ct.getInt("f30");// getAgeEmoloyct(he, dw, 30, 40,
				// yearmonth, includechld);//
				// 30(含)-40岁
				s_f30 += f30;
				int f20 = ct.getInt("f20");// getAgeEmoloyct(he, dw, 18, 30,
				// yearmonth, includechld);//
				// 18(含)-30岁
				s_f20 += f20;
				int f00 = ct.getInt("f00");// getAgeEmoloyct(he, dw, -1, 18,
				// yearmonth, includechld);// 18岁以下
				s_f00 += f00;
				dw.put("sumpeo", sumpeo);
				dw.put("f50", f50);
				dw.put("f40", f40);
				dw.put("f30", f30);
				dw.put("f20", f20);
				dw.put("f00", f00);
				dw.put("bf50", ((sumpeo > 0) ? (dec.format((float) f50 / sumpeo)) : "0.000"));
				dw.put("bf40", ((sumpeo > 0) ? (dec.format((float) f40 / sumpeo)) : "0.000"));
				dw.put("bf30", ((sumpeo > 0) ? (dec.format((float) f30 / sumpeo)) : "0.000"));
				dw.put("bf20", ((sumpeo > 0) ? (dec.format((float) f20 / sumpeo)) : "0.000"));
				dw.put("bf00", ((sumpeo > 0) ? (dec.format((float) f00 / sumpeo)) : "0.000"));
			}
		}

		JSONObject srow = new JSONObject();
		srow.put("sumpeo", s_sumpeo);
		srow.put("f50", s_f50);
		srow.put("f40", s_f40);
		srow.put("f30", s_f30);
		srow.put("f20", s_f20);
		srow.put("f00", s_f00);

		srow.put("bf50", ((s_sumpeo > 0) ? (dec.format((float) s_f50 / s_sumpeo)) : "0.000"));
		srow.put("bf40", ((s_sumpeo > 0) ? (dec.format((float) s_f40 / s_sumpeo)) : "0.000"));
		srow.put("bf30", ((s_sumpeo > 0) ? (dec.format((float) s_f30 / s_sumpeo)) : "0.000"));
		srow.put("bf20", ((s_sumpeo > 0) ? (dec.format((float) s_f20 / s_sumpeo)) : "0.000"));
		srow.put("bf00", ((s_sumpeo > 0) ? (dec.format((float) s_f00 / s_sumpeo)) : "0.000"));

		srow.put("orgname", "合计");
		JSONArray footer = new JSONArray();
		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
		rst.put("footer", footer);
		String scols = parms.get("cols");
		if (scols == null) {
			return rst;
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private JSONArray buildAgeChartData(JSONArray rows) {
		JSONArray rst = new JSONArray();
		rst.add(buildYearChartDateRow(rows, "f50", "50岁及以上"));
		rst.add(buildYearChartDateRow(rows, "f40", "40(含)-50岁"));
		rst.add(buildYearChartDateRow(rows, "f30", "30(含)-40岁"));
		rst.add(buildYearChartDateRow(rows, "f20", "18(含)-30岁"));
		rst.add(buildYearChartDateRow(rows, "f00", "18岁以下"));
		return rst;
	}

	private JSONArray getAgetEmpCts(Hr_employee he, JSONObject dw, List<String> yms, int empclass, String spname, int findtype, boolean includchd) throws Exception {
		String yearmonths = "";
		for (String ym : yms) {
			yearmonths += "'" + ym + "',";
		}
		if (yearmonths.length() > 0)
			yearmonths = yearmonths.substring(0, yearmonths.length() - 1);
		boolean isnowmonth = false;
		if (yms.size() == 1) {
			isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yms.get(0));
		}

		String nowdatestr = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM");

		String sqlstr = "SELECT ";
		if (isnowmonth)
			sqlstr = sqlstr + "  '" + yms.get(0) + "' yearmonth,";
		else
			sqlstr = sqlstr + "  yearmonth,";
		sqlstr = sqlstr + "  IFNULL(SUM(1),0) sumpeo," + "   IFNULL(SUM(CASE WHEN age>=50 THEN 1 ELSE 0 END),0) f50,"
				+ "   IFNULL(SUM(CASE WHEN age>=40 AND age<50 THEN 1 ELSE 0 END),0) f40,"
				+ "   IFNULL(SUM(CASE WHEN age>=30 AND age<40 THEN 1 ELSE 0 END),0) f30,"
				+ "   IFNULL(SUM(CASE WHEN age>=18 AND age<30 THEN 1 ELSE 0 END),0) f20," + "   IFNULL(SUM(CASE WHEN age<18 THEN 1 ELSE 0 END),0) f00 ";
		if (isnowmonth) {
			sqlstr = sqlstr + " FROM  (" + " SELECT he.*, (DATE_FORMAT(FROM_DAYS(TO_DAYS(CONCAT('" + nowdatestr
					+ "','-01')) - TO_DAYS(birthday)),'%Y') + 0) AS age  " + " FROM hr_employee he where `empstatid` IN(" + empstids + ") ";
		} else
			sqlstr = sqlstr + " FROM  (" + " SELECT he.*, (DATE_FORMAT(FROM_DAYS(TO_DAYS(CONCAT(yearmonth,'-01')) - TO_DAYS(birthday)),'%Y') + 0) AS age  "
					+ " FROM hr_month_employee he where `empstatid` IN(" + empstids + ") ";

		if (yms.size() == 1) {
			if (!isnowmonth)
				sqlstr = sqlstr + " and yearmonth=" + yearmonths + "";
		} else if (yms.size() > 1)
			sqlstr = sqlstr + "AND yearmonth IN (" + yearmonths + ") ";
		else
			throw new Exception("月度长度不能为0");

		if (empclass == 2)
			sqlstr = sqlstr + " and emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and emnature='非脱产'";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}

		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");

		sqlstr = parExtSQL("he", sqlstr);

		sqlstr = sqlstr + " ) tb ";
		if (!isnowmonth)
			sqlstr = sqlstr + " GROUP BY tb.yearmonth";
		return HRUtil.getReadPool().opensql2json_O(sqlstr);
	}

	// // -1 作为条件 包含minAge
	// private int getAgeEmoloyct(Hr_employee he, JSONObject dw, int minAge, int
	// MaxAge, String yearmonth, boolean includchd) throws Exception {
	// String sqlstr = "SELECT COUNT(*) ct FROM ("
	// + " SELECT he.*, (DATE_FORMAT(FROM_DAYS(TO_DAYS(NOW()) -
	// TO_DAYS(birthday)),'%Y') + 0) AS age "
	// + " FROM hr_month_employee he WHERE yearmonth='" + yearmonth +
	// "' AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1
	// AND isquota=1 ) ";
	// if (includchd)
	// sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%' ";
	// else
	// sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
	// sqlstr = sqlstr + ") tb where 1=1 ";
	// if (minAge != -1)
	// sqlstr = sqlstr + " and tb.age>=" + minAge;
	// if (MaxAge != -1)
	// sqlstr = sqlstr + " and tb.age<" + MaxAge;
	// return Integer.valueOf(he.pool.openSql2List(sqlstr).get(0).get("ct"));
	// }

	// /////////////////年龄结束///////////////////////////////////////////

	@ACOAction(eventname = "findEmployeeSex", Authentication = true, notes = "人事性别分析")
	public String findEmployeeSex() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = new ArrayList<String>();
		yearmonths.add(yearmonth);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		return findEmployeeSexAndQS(dws, orgid, yearmonths, empclass, includechild, parms).toString();
	}

	@ACOAction(eventname = "findEmployeeSexPotal", Authentication = true, notes = "人事性别分析门户")
	public String findEmployeeSexPotal() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid");
		orgid = (orgid == null) ? CSContext.getDefaultOrgID() : orgid;
		String strempclass = CorUtil.hashMap2Str(parms, "empclass");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		String sqlstr = "SELECT IFNULL(SUM(1), 0) sumpeo, " + "  IFNULL(SUM(1),0) sumpeo," + "  IFNULL(SUM(CASE WHEN sex=1 THEN 1 ELSE 0 END),0) nv,"
				+ "  IFNULL(SUM(CASE WHEN sex =2 THEN 1 ELSE 0 END),0) nan" + " FROM hr_employee e " + " WHERE e.`empstatid` IN(" + empstids + ") ";
		sqlstr = sqlstr + " AND idpath LIKE '" + org.idpath.getValue() + "%'";
		if (empclass == 2)
			sqlstr = sqlstr + " and e.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and e.emnature='非脱产'";

		JSONArray rows = HRUtil.getReadPool().opensql2json_O(sqlstr);
		JSONObject rst = new JSONObject();
		rst.put("rows", rows);
		rst.put("org", org.extorgname.getValue());
		return rst.toString();
	}

	@ACOAction(eventname = "findEmployeeSex_QS", Authentication = true, notes = "人事性别趋势分析")
	public String findEmployeeSex_QS() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth_begin", "需要参数【yearmonth_begin】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 12);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		JSONObject rst = findEmployeeSexAndQS(dws, orgid, yearmonths, empclass, includechild, parms);

		dws = HRUtil.getOrgsByOrgid(orgid, false, yearmonths);
		JSONArray chartdata = buildSexChartData(findEmployeeSexAndQS(dws, orgid, yearmonths, empclass, false, parms).getJSONArray("rows"));
		rst.put("chartdata", chartdata);
		return rst.toString();
	}

	private JSONArray buildSexChartData(JSONArray rows) {
		JSONArray rst = new JSONArray();
		rst.add(buildYearChartDateRow(rows, "nan", "男"));
		rst.add(buildYearChartDateRow(rows, "nv", "女"));
		return rst;
	}

	private JSONObject findEmployeeSexAndQS(JSONArray dws, String orgid, List<String> yearmonths, int empclass, boolean includechild,
			HashMap<String, String> parms) throws Exception {

		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		int s_sumpeo = 0, s_nan = 0, s_nv = 0;
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);// 机构 年月
			boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
			// 且
			// 为自身机构时候
			// 不查询子机构数据
			List<String> yms = new ArrayList<String>();
			yms.add(dw.getString("yearmonth"));
			JSONArray cts = getSexEmpCts(he, dw, yms, empclass, spname, findtype, includechld);
			for (int j = 0; j < cts.size(); j++) {
				JSONObject ct = cts.getJSONObject(j);
				int sumpeo = ct.getInt("sumpeo");// 总人数
				s_sumpeo += sumpeo;
				int nan = ct.getInt("nan");
				s_nan += nan;
				int nv = ct.getInt("nv");
				s_nv += nv;
				dw.put("sumpeo", sumpeo);
				dw.put("nan", nan);
				dw.put("nv", nv);
				dw.put("bnan", ((sumpeo > 0) ? (dec.format((float) nan / sumpeo)) : "0.000"));
				dw.put("bnv", ((sumpeo > 0) ? (dec.format((float) nv / sumpeo)) : "0.000"));
			}
		}
		JSONObject srow = new JSONObject();
		srow.put("sumpeo", s_sumpeo);
		srow.put("nan", s_nan);
		srow.put("nv", s_nv);
		srow.put("orgname", "合计");

		srow.put("bnan", ((s_sumpeo > 0) ? (dec.format((float) s_nan / s_sumpeo)) : "0.000"));
		srow.put("bnv", ((s_sumpeo > 0) ? (dec.format((float) s_nv / s_sumpeo)) : "0.000"));

		JSONArray footer = new JSONArray();
		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
		rst.put("footer", footer);

		String scols = parms.get("cols");
		if (scols == null) {
			return rst;
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private JSONArray getSexEmpCts(Hr_employee he, JSONObject dw, List<String> yms, int empclass, String spname, int findtype, boolean includchd) throws Exception {
		String yearmonths = "";
		for (String ym : yms) {
			yearmonths += "'" + ym + "',";
		}
		if (yearmonths.length() > 0)
			yearmonths = yearmonths.substring(0, yearmonths.length() - 1);

		boolean isnowmonth = false;
		if (yms.size() == 1) {
			isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yms.get(0));
		}

		String sqlstr = "SELECT ";
		if (isnowmonth)
			sqlstr = sqlstr + "'" + Systemdate.getStrDateByFmt(new Date(), "yyyy-MM") + "'  yearmonth, ";
		else
			sqlstr = sqlstr + "  yearmonth,";
		sqlstr = sqlstr + "  IFNULL(SUM(1),0) sumpeo," + "  IFNULL(SUM(CASE WHEN sex=1 THEN 1 ELSE 0 END),0) nv,"
				+ "  IFNULL(SUM(CASE WHEN sex =2 THEN 1 ELSE 0 END),0) nan";
		if (isnowmonth)
			sqlstr = sqlstr + " FROM hr_employee e ";
		else
			sqlstr = sqlstr + " FROM hr_month_employee e ";
		sqlstr = sqlstr + " WHERE e.`empstatid` IN(" + empstids + ") ";

		if (isnowmonth) {

		} else {
			if (yms.size() == 1)
				sqlstr = sqlstr + " and yearmonth=" + yearmonths + "";
			else if (yms.size() > 1)
				sqlstr = sqlstr + "AND yearmonth IN (" + yearmonths + ") ";
			else
				throw new Exception("月度长度不能为0");
		}

		if (empclass == 2)
			sqlstr = sqlstr + " and e.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and e.emnature='非脱产'";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}

		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		sqlstr = parExtSQL("e", sqlstr);
		if (!isnowmonth)
			sqlstr = sqlstr + " GROUP BY yearmonth";
		return HRUtil.getReadPool().opensql2json_O(sqlstr);
	}

	// // sex -1 所有
	// private int getDegreeEmoloySexct(Hr_employee he, JSONObject dw, int sex,
	// String yearmonth, boolean includchd) throws Exception {
	// String sqlstr = "SELECT count(*) ct FROM hr_month_employee WHERE
	// yearmonth='" + yearmonth + "'";
	// if (sex != -1)
	// sqlstr = sqlstr + " AND sex =" + sex + " ";
	// sqlstr = sqlstr + " AND empstatid IN(SELECT statvalue FROM
	// hr_employeestat WHERE usable=1 AND isquota=1) ";
	// if (includchd)
	// sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
	// else
	// sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
	// return Integer.valueOf(he.pool.openSql2List(sqlstr).get(0).get("ct"));
	// }

	// /////////////////性别结束///////////////////////////////////////////

	@ACOAction(eventname = "findEmployeeMZ", Authentication = true, notes = "员工民族分析")
	public String findEmployeeMZ() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");

		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 12);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths); // 第一个为当前机构
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		int s_sumpeo = 0;
		JSONObject srow = new JSONObject();
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			boolean includechld = dw.getInt("_incc") == 1;// (!includechild || (i != 0));// 包含子机构 且 为自身机构时候
			// 不查询子机构数据
			String yearmonth = dw.getString("yearmonth");
			JSONArray mzs = getEmployeeMZ(he, dw, yearmonth, spname, findtype, includechld);
			int sumpeo = 0;
			for (int j = 0; j < mzs.size(); j++) {
				JSONObject mz = mzs.getJSONObject(j);
				int ct = mz.getInt("ct");
				sumpeo = sumpeo + ct;
				String fdname = "f" + mz.getString("dictvalue");
				dw.put(fdname, ct);
				if (srow.has(fdname))
					srow.put(fdname, srow.getInt(fdname) + ct);
				else
					srow.put(fdname, ct);
			}
			dw.put("sumpeo", sumpeo);
			s_sumpeo += sumpeo;
			for (int j = 0; j < mzs.size(); j++) {
				JSONObject mz = mzs.getJSONObject(j);
				if (sumpeo != 0) {
					int ct = mz.getInt("ct");
					dw.put("bf" + mz.getString("dictvalue"), dec.format((float) ct / sumpeo));
				} else {
					dw.put("bf" + mz.getString("dictvalue"), 0);
				}
			}
		}
		srow.put("orgname", "合计");
		srow.put("sumpeo", s_sumpeo);
		JSONArray footer = new JSONArray();
		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
		rst.put("footer", footer);

		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private JSONArray getEmployeeMZ(Hr_employee he, JSONObject dw, String yearmonth, String spname, int findtype, boolean includchd) throws Exception {
		boolean isnowmonth = isNowMonth("'" + yearmonth + "'");
		String sqlstr = "SELECT d.dictvalue,IFNULL(ct,0) ct FROM shwdict d LEFT JOIN " + " (SELECT anation,COUNT(*) ct  FROM ("
				+ " SELECT IF(e.`nation` IS NULL,57,e.`nation`) anation, " +
				"         e.* FROM ";
		if (isnowmonth) {
			sqlstr = sqlstr + "hr_employee e WHERE ";
		} else {
			sqlstr = sqlstr + "hr_month_employee e WHERE yearmonth='" + yearmonth + "' AND ";
		}
		sqlstr = sqlstr + " empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) ";
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}

		sqlstr = parExtSQL(null, sqlstr);

		sqlstr = sqlstr + "  ) tb" + " GROUP BY anation) tb1 ON d.dictvalue=tb1.anation" + " WHERE d.pid=797" + " ORDER BY (d.dictvalue+0)";

		return HRUtil.getReadPool().opensql2json_O(sqlstr);
	}

	// /////////////////名族结束///////////////////////////////////////////

	@ACOAction(eventname = "findEmployeeLev", Authentication = true, notes = "员工职级分布分析")
	public String findEmployeeLev() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));

		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = new ArrayList<String>();
		yearmonths.add(yearmonth);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		return findEmployeeLevAndQS(dws, orgid, yearmonths, empclass, includechild, parms).toString();
	}

	@ACOAction(eventname = "findEmployeeLev_QS", Authentication = true, notes = "员工职级趋势分析")
	public String findEmployeeLev_QS() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth_begin", "需要参数【yearmonth_begin】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));

		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 12);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		JSONObject rst = findEmployeeLevAndQS(dws, orgid, yearmonths, empclass, includechild, parms);

		dws = HRUtil.getOrgsByOrgid(orgid, false, yearmonths);
		JSONArray chartdata = buildLevChartData(findEmployeeLevAndQS(dws, orgid, yearmonths, empclass, false, parms).getJSONArray("rows"));
		rst.put("chartdata", chartdata);
		return rst.toString();
	}

	private JSONArray buildLevChartData(JSONArray rows) {
		JSONArray rst = new JSONArray();
		rst.add(buildYearChartDateRow(rows, "f1", "1.1-1.3"));
		rst.add(buildYearChartDateRow(rows, "f2", "2.0"));
		rst.add(buildYearChartDateRow(rows, "f21", "2.1-2.3"));
		rst.add(buildYearChartDateRow(rows, "f3", "3.0"));
		rst.add(buildYearChartDateRow(rows, "f31", "3.1-3.3"));
		rst.add(buildYearChartDateRow(rows, "f4", "4.0"));
		rst.add(buildYearChartDateRow(rows, "f41", "4.1-4.3"));
		rst.add(buildYearChartDateRow(rows, "f51", "5.1-5.2"));
		rst.add(buildYearChartDateRow(rows, "f61", "6.1-6.3"));
		rst.add(buildYearChartDateRow(rows, "f71", "7.1-7.2"));
		rst.add(buildYearChartDateRow(rows, "f8", "8.0"));
		return rst;
	}

	private JSONObject findEmployeeLevAndQS(JSONArray dws, String orgid, List<String> yearmonths, int empclass, boolean includechild,
			HashMap<String, String> parms) throws Exception {

		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		int s_sumpeo = 0, s_f1 = 0, s_f2 = 0, s_f21 = 0, s_f3 = 0, s_f31 = 0, s_f4 = 0, s_f41 = 0, s_f51 = 0, s_f61 = 0, s_f71 = 0, s_f8 = 0;
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);// 机构 年月
			boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
			// 且
			// 为自身机构时候
			// 不查询子机构数据
			List<String> yms = new ArrayList<String>();
			yms.add(dw.getString("yearmonth"));
			JSONArray cts = getEmployeeLevcts(he, dw, yms, empclass, spname, findtype, includechld);
			for (int j = 0; j < cts.size(); j++) {
				JSONObject ct = cts.getJSONObject(j);
				int sumpeo = ct.getInt("sumpeo");// 总人数
				s_sumpeo += sumpeo;
				int f1 = ct.getInt("f1");//
				s_f1 += f1;
				int f2 = ct.getInt("f2");//
				s_f2 += f2;
				int f21 = ct.getInt("f21");//
				s_f21 += f21;
				int f3 = ct.getInt("f3");//
				s_f3 += f3;
				int f31 = ct.getInt("f31");//
				s_f31 += f31;
				int f4 = ct.getInt("f4");//
				s_f4 += f4;
				int f41 = ct.getInt("f41");//
				s_f41 += f41;
				int f51 = ct.getInt("f51");//
				s_f51 += f51;
				int f61 = ct.getInt("f61");//
				s_f61 += f61;
				int f71 = ct.getInt("f71");//
				s_f71 += f71;
				int f8 = ct.getInt("f8");//
				s_f8 += f8;
				dw.put("sumpeo", sumpeo);
				dw.put("f1", f1);
				dw.put("f2", f2);
				dw.put("f21", f21);
				dw.put("f3", f3);
				dw.put("f31", f31);
				dw.put("f4", f4);
				dw.put("f41", f41);
				dw.put("f51", f51);
				dw.put("f61", f61);
				dw.put("f71", f71);
				dw.put("f8", f8);
				dw.put("bf1", ((sumpeo > 0) ? (dec.format((float) f1 / sumpeo)) : "0.000"));
				dw.put("bf2", ((sumpeo > 0) ? (dec.format((float) f2 / sumpeo)) : "0.000"));
				dw.put("bf21", ((sumpeo > 0) ? (dec.format((float) f21 / sumpeo)) : "0.000"));
				dw.put("bf3", ((sumpeo > 0) ? (dec.format((float) f3 / sumpeo)) : "0.000"));
				dw.put("bf31", ((sumpeo > 0) ? (dec.format((float) f31 / sumpeo)) : "0.000"));
				dw.put("bf4", ((sumpeo > 0) ? (dec.format((float) f4 / sumpeo)) : "0.000"));
				dw.put("bf41", ((sumpeo > 0) ? (dec.format((float) f41 / sumpeo)) : "0.000"));
				dw.put("bf51", ((sumpeo > 0) ? (dec.format((float) f51 / sumpeo)) : "0.000"));
				dw.put("bf61", ((sumpeo > 0) ? (dec.format((float) f61 / sumpeo)) : "0.000"));
				dw.put("bf71", ((sumpeo > 0) ? (dec.format((float) f71 / sumpeo)) : "0.000"));
				dw.put("bf8", ((sumpeo > 0) ? (dec.format((float) f8 / sumpeo)) : "0.000"));
			}
		}
		JSONObject srow = new JSONObject();
		srow.put("sumpeo", s_sumpeo);
		srow.put("f1", s_f1);
		srow.put("f2", s_f2);
		srow.put("f21", s_f21);
		srow.put("f3", s_f3);
		srow.put("f31", s_f31);
		srow.put("f4", s_f4);
		srow.put("f41", s_f41);
		srow.put("f51", s_f51);
		srow.put("f61", s_f61);
		srow.put("f71", s_f71);
		srow.put("f8", s_f8);
		srow.put("orgname", "合计");

		srow.put("bf1", ((s_sumpeo > 0) ? (dec.format((float) s_f1 / s_sumpeo)) : "0.000"));
		srow.put("bf2", ((s_sumpeo > 0) ? (dec.format((float) s_f2 / s_sumpeo)) : "0.000"));
		srow.put("bf21", ((s_sumpeo > 0) ? (dec.format((float) s_f21 / s_sumpeo)) : "0.000"));
		srow.put("bf3", ((s_sumpeo > 0) ? (dec.format((float) s_f3 / s_sumpeo)) : "0.000"));
		srow.put("bf31", ((s_sumpeo > 0) ? (dec.format((float) s_f31 / s_sumpeo)) : "0.000"));
		srow.put("bf4", ((s_sumpeo > 0) ? (dec.format((float) s_f4 / s_sumpeo)) : "0.000"));
		srow.put("bf41", ((s_sumpeo > 0) ? (dec.format((float) s_f41 / s_sumpeo)) : "0.000"));
		srow.put("bf51", ((s_sumpeo > 0) ? (dec.format((float) s_f51 / s_sumpeo)) : "0.000"));
		srow.put("bf61", ((s_sumpeo > 0) ? (dec.format((float) s_f61 / s_sumpeo)) : "0.000"));
		srow.put("bf71", ((s_sumpeo > 0) ? (dec.format((float) s_f71 / s_sumpeo)) : "0.000"));
		srow.put("bf8", ((s_sumpeo > 0) ? (dec.format((float) s_f8 / s_sumpeo)) : "0.000"));

		JSONArray footer = new JSONArray();
		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
		rst.put("footer", footer);

		String scols = parms.get("cols");
		if (scols == null) {
			return rst;
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private JSONArray getEmployeeLevcts(Hr_employee he, JSONObject dw, List<String> yms, int empclass, String spname, int findtype, boolean includchd) throws Exception {
		String yearmonths = "";
		for (String ym : yms) {
			yearmonths += "'" + ym + "',";
		}
		if (yearmonths.length() > 0)
			yearmonths = yearmonths.substring(0, yearmonths.length() - 1);

		boolean isnowmonth = (yms.size() == 1) && isNowMonth(yearmonths);

		String sqlstr = "SELECT ";
		if (isnowmonth)
			sqlstr = sqlstr + "  '" + yms.get(0) + "' yearmonth,";
		else
			sqlstr = sqlstr + "  yearmonth,";
		sqlstr = sqlstr + "  SUM(1) sumpeo," + "   SUM(CASE WHEN lv_num>=1.1 AND lv_num<=1.9 THEN 1 ELSE 0 END) f1,"
				+ "   SUM(CASE WHEN lv_num=2.0 THEN 1 ELSE 0 END) f2," + "   SUM(CASE WHEN lv_num>=2.1 AND lv_num<=2.9 THEN 1 ELSE 0 END) f21,"
				+ "   SUM(CASE WHEN lv_num=3.0 THEN 1 ELSE 0 END) f3," + "   SUM(CASE WHEN lv_num>=3.1 AND lv_num<=3.9 THEN 1 ELSE 0 END) f31,"
				+ "   SUM(CASE WHEN lv_num=4.0 THEN 1 ELSE 0 END) f4," + "   SUM(CASE WHEN lv_num>=4.1 AND lv_num<=4.9 THEN 1 ELSE 0 END) f41,"
				+ "   SUM(CASE WHEN lv_num>=5.1 AND lv_num<=5.9 THEN 1 ELSE 0 END) f51,"
				+ "   SUM(CASE WHEN lv_num>=6.1 AND lv_num<=6.9 THEN 1 ELSE 0 END) f61,"
				+ "   SUM(CASE WHEN lv_num>=7.1 AND lv_num<=7.9 THEN 1 ELSE 0 END) f71," + "   SUM(CASE WHEN lv_num=8 THEN 1 ELSE 0 END) f8";
		if (isnowmonth)
			sqlstr = sqlstr + " FROM hr_employee e";
		else
			sqlstr = sqlstr + " FROM hr_month_employee e";
		sqlstr = sqlstr + " WHERE e.`empstatid` IN(" + empstids + ") ";
		if (yms.size() == 1) {
			if (!isnowmonth)
				sqlstr = sqlstr + " and yearmonth=" + yearmonths + "";
		} else if (yms.size() > 1)
			sqlstr = sqlstr + "AND yearmonth IN (" + yearmonths + ") ";
		else
			throw new Exception("月度长度不能为0");

		if (empclass == 2)
			sqlstr = sqlstr + " and e.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and e.emnature='非脱产'";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and e.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and e.sp_name like '%" + spname + "%'";
		}

		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");

		sqlstr = parExtSQL("e", sqlstr);

		sqlstr = sqlstr + " GROUP BY yearmonth";
		return HRUtil.getReadPool().opensql2json_O(sqlstr);
	}

	// // minlev maxLev -1 略过
	// /**
	// * 职级
	// *
	// * @param he
	// * @param dw
	// * @param minlev
	// * @param maxLev
	// * @param yearmonth
	// * @param includchd
	// * @return
	// * @throws Exception
	// */
	// private int getEmployeeLevct(Hr_employee he, JSONObject dw, double
	// minlev, double maxLev, String yearmonth, boolean includchd) throws
	// Exception {
	// String sqlstr = "SELECT count(*) ct FROM hr_month_employee WHERE
	// yearmonth='" + yearmonth + "' ";
	// if (minlev != -1)
	// sqlstr = sqlstr + " AND lv_num >=" + minlev + " ";
	// if (maxLev != -1)
	// sqlstr = sqlstr + " AND lv_num <=" + maxLev + " ";
	// sqlstr = sqlstr + " AND empstatid IN(SELECT statvalue FROM
	// hr_employeestat WHERE usable=1 AND isquota=1) ";
	// if (includchd)
	// sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
	// else
	// sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
	// return Integer.valueOf(he.pool.openSql2List(sqlstr).get(0).get("ct"));
	// }

	// /////////////////职级结束///////////////////////////////////////////

	@ACOAction(eventname = "findEmpoyeeOrg", Authentication = true, notes = "员工机构分析")
	public String findEmpoyeeOrg() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ap = CorUtil.hashMap2Str(parms, "allowemptparm");
		boolean allowemptparm = (ap == null) ? false : Boolean.valueOf(ap);
		String orgid = null, yearmonth_begin = null, yearmonth_end = null;
		boolean includechild = false;
		int empclass = 1;
		if (!allowemptparm) {
			List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
			orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
			yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
			yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");
			includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
			String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
			empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);
		} else {
			orgid = CSContext.getDefaultOrgID();
			yearmonth_begin = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM");
			yearmonth_end = yearmonth_begin;
			includechild = true;
		}

		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 12);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");

		// int ct0 = getOrgEmpctSum(he, org, false);// 自己的总数
		// JSONObject dw0 = org.toJsonObj();
		// dw0.put("ct", ct0);
		// if (sumpeo == 0)
		// dw0.put("bct", "0.0000");
		// else
		// dw0.put("bct", dec.format((float) ct0 / sumpeo));

		for (String yearmonth : yearmonths) {
			int sumpeo = getOrgEmpctSum(he, org, yearmonth, empclass, spname, findtype, true);// 包括子机构的合计
			for (int i = 0; i < dws.size(); i++) {
				JSONObject dw = dws.getJSONObject(i);
				String eyearmonth = dw.getString("yearmonth");
				if (yearmonth.equalsIgnoreCase(eyearmonth)) {
					boolean includechld = dw.getInt("_incc") == 1;
					// 不查询子机构数据
					if (sumpeo != 0) {
						int ct = getOrgEmpct(he, dw, yearmonth, empclass, spname, findtype, includechld);
						dw.put("ct", ct);
						dw.put("bct", dec.format((float) ct / sumpeo));
					} else {
						dw.put("ct", 0);
						dw.put("bct", "0.0000");
					}
				}

			}
		}

//		JSONObject srow = new JSONObject();
//		srow.put("ct", sumpeo);
//		srow.put("orgname", "合计");
//		JSONArray footer = new JSONArray();
//		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
//		rst.put("footer", footer);

		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}

	}

	private JSONParm getParmByName(List<JSONParm> jps, String pname) {
		for (JSONParm jp : jps) {
			if (jp.getParmname().equals(pname))
				return jp;
		}
		return null;
	}

	private int getOrgEmpctSum(Hr_employee he, Shworg org, String yearmonth, int empclass, String spname, int findtype, boolean includechilds) throws Exception {
		boolean isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yearmonth);

		String sqlstr = "SELECT count(*) ct FROM ";
		if (isnowmonth)
			sqlstr = sqlstr + "hr_employee WHERE 1=1 ";
		else
			sqlstr = sqlstr + "hr_month_employee WHERE yearmonth='" + yearmonth + "'";
		sqlstr = sqlstr + " and usable=1 AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) AND idpath LIKE '"
				+ org.idpath.getValue() + "%'";
		if (empclass == 2)
			sqlstr = sqlstr + " and emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and emnature='非脱产'";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}

		sqlstr = parExtSQL(null, sqlstr);

		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	private int getOrgEmpct(Hr_employee he, JSONObject dw, String yearmonth, int empclass, String spname, int findtype, boolean includchd) throws Exception {
		boolean isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yearmonth);
		String sqlstr = "SELECT count(*) ct FROM ";
		if (isnowmonth)
			sqlstr = sqlstr + "hr_employee WHERE 1=1 ";
		else
			sqlstr = sqlstr + "hr_month_employee WHERE yearmonth='" + yearmonth + "'";
		sqlstr = sqlstr + " AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) ";
		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");

		if (empclass == 2)
			sqlstr = sqlstr + " and emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and emnature='非脱产'";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}

		sqlstr = parExtSQL(null, sqlstr);

		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	// /////////////////机构分析结束///////////////////////////////////////////

	@ACOAction(eventname = "findEmployeeClass", Authentication = true, notes = "员工职类分析")
	public String findEmployeeClass() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));

		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = new ArrayList<String>();
		yearmonths.add(yearmonth);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		return findEmployeeClsAndQS(dws, orgid, yearmonths, empclass, includechild, parms).toString();
	}

	@ACOAction(eventname = "findEmployeeClassPotal", Authentication = true, notes = "员工职类分析-门户")
	public String findEmployeeClassPotal() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String orgid = CorUtil.hashMap2Str(parms, "orgid");
		orgid = (orgid == null) ? CSContext.getDefaultOrgID() : orgid;

		String strempclass = CorUtil.hashMap2Str(parms, "empclass");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");
		String sqlstr = "SELECT IFNULL(SUM(1), 0) sumpeo, " + "IFNULL(SUM(CASE WHEN hwc_namezl = 'M类' THEN 1 ELSE 0 END),0) M, "
				+ "IFNULL(SUM(CASE WHEN hwc_namezl = 'P类' THEN 1 ELSE 0 END),0) P, " + "IFNULL(SUM(CASE WHEN hwc_namezl = 'A类' THEN 1 ELSE 0 END),0) A, "
				+ "IFNULL(SUM(CASE WHEN (hwc_namezl = 'OA' OR hwc_namezl = 'OM' OR hwc_namezl = 'OO' OR hwc_namezl = 'OP') THEN 1 ELSE 0 END),0) O  "
				+ "FROM hr_employee e WHERE e.empstatid IN (2, 3, 4, 5)  " + "AND e.idpath LIKE '" + org.idpath.getValue() + "%'";
		if (empclass == 2)
			sqlstr = sqlstr + " and e.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and e.emnature='非脱产'";

		JSONArray rows = HRUtil.getReadPool().opensql2json_O(sqlstr);
		JSONObject rst = new JSONObject();
		rst.put("rows", rows);
		rst.put("org", org.extorgname.getValue());
		return rst.toString();
	}

	@ACOAction(eventname = "findEmployeeClass_QS", Authentication = true, notes = "员工职类趋势分析")
	public String findEmployeeClass_QS() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth_begin", "需要参数【yearmonth_begin】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));

		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 12);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		JSONObject rst = findEmployeeClsAndQS(dws, orgid, yearmonths, empclass, includechild, parms);

		dws = HRUtil.getOrgsByOrgid(orgid, false, yearmonths);
		JSONArray chartdata = buildClsChartData(findEmployeeClsAndQS(dws, orgid, yearmonths, empclass, false, parms).getJSONArray("rows"));
		rst.put("chartdata", chartdata);
		return rst.toString();
	}

	private JSONArray buildClsChartData(JSONArray rows) {
		JSONArray rst = new JSONArray();
		rst.add(buildYearChartDateRow(rows, "M", "M类"));
		rst.add(buildYearChartDateRow(rows, "P", "P类"));
		rst.add(buildYearChartDateRow(rows, "A", "A类"));
		rst.add(buildYearChartDateRow(rows, "OM", "OM"));
		rst.add(buildYearChartDateRow(rows, "OP", "OP"));
		rst.add(buildYearChartDateRow(rows, "OA", "OA"));
		rst.add(buildYearChartDateRow(rows, "OO", "OO"));
		return rst;
	}

	private JSONObject findEmployeeClsAndQS(JSONArray dws, String orgid, List<String> yearmonths, int empclass, boolean includechild,
			HashMap<String, String> parms) throws Exception {

		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		int s_sumpeo = 0, s_M = 0, s_P = 0, s_A = 0, s_OM = 0, s_OP = 0, s_OA = 0, s_OO = 0, s_SO = 0;
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);// 机构 年月
			boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
			// 且
			// 为自身机构时候
			// 不查询子机构数据
			List<String> yms = new ArrayList<String>();
			yms.add(dw.getString("yearmonth"));
			JSONArray cts = getEmployeeClsCts(he, dw, yms, empclass, spname, findtype, includechld);
			for (int j = 0; j < cts.size(); j++) {
				JSONObject ct = cts.getJSONObject(j);
				int sumpeo = ct.getInt("sumpeo");// 总人数
				s_sumpeo += sumpeo;
				int M = ct.getInt("M");//
				s_M += M;
				int P = ct.getInt("P");//
				s_P += P;
				int A = ct.getInt("A");//
				s_A += A;
				int OM = ct.getInt("OM");//
				s_OM += OM;
				int OP = ct.getInt("OP");//
				s_OP += OP;
				int OA = ct.getInt("OA");//
				s_OA += OA;
				int OO = ct.getInt("OO");//
				s_OO += OO;
				int SO = OM + OP + OA + OO;//
				s_SO += SO;

				dw.put("sumpeo", sumpeo);
				dw.put("M", M);
				dw.put("P", P);
				dw.put("A", A);
				dw.put("OM", OM);
				dw.put("OP", OP);
				dw.put("OA", OA);
				dw.put("OO", OO);
				dw.put("SO", SO);
				dw.put("bM", ((sumpeo > 0) ? (dec.format((float) M / sumpeo)) : "0.000"));
				dw.put("bP", ((sumpeo > 0) ? (dec.format((float) P / sumpeo)) : "0.000"));
				dw.put("bA", ((sumpeo > 0) ? (dec.format((float) A / sumpeo)) : "0.000"));
				dw.put("bOM", ((sumpeo > 0) ? (dec.format((float) OM / sumpeo)) : "0.000"));
				dw.put("bOP", ((sumpeo > 0) ? (dec.format((float) OP / sumpeo)) : "0.000"));
				dw.put("bOA", ((sumpeo > 0) ? (dec.format((float) OA / sumpeo)) : "0.000"));
				dw.put("bOO", ((sumpeo > 0) ? (dec.format((float) OO / sumpeo)) : "0.000"));
				dw.put("bSO", ((sumpeo > 0) ? (dec.format((float) SO / sumpeo)) : "0.000"));
			}
		}
		JSONObject srow = new JSONObject();
		srow.put("sumpeo", s_sumpeo);
		srow.put("M", s_M);
		srow.put("P", s_P);
		srow.put("A", s_A);
		srow.put("OM", s_OM);
		srow.put("OP", s_OP);
		srow.put("OA", s_OA);
		srow.put("OO", s_OO);
		srow.put("SO", s_SO);
		srow.put("orgname", "合计");

		srow.put("bM", ((s_sumpeo > 0) ? (dec.format((float) s_M / s_sumpeo)) : "0.000"));
		srow.put("bP", ((s_sumpeo > 0) ? (dec.format((float) s_P / s_sumpeo)) : "0.000"));
		srow.put("bA", ((s_sumpeo > 0) ? (dec.format((float) s_A / s_sumpeo)) : "0.000"));
		srow.put("bOM", ((s_sumpeo > 0) ? (dec.format((float) s_OM / s_sumpeo)) : "0.000"));
		srow.put("bOP", ((s_sumpeo > 0) ? (dec.format((float) s_OP / s_sumpeo)) : "0.000"));
		srow.put("bOA", ((s_sumpeo > 0) ? (dec.format((float) s_OA / s_sumpeo)) : "0.000"));
		srow.put("bOO", ((s_sumpeo > 0) ? (dec.format((float) s_OO / s_sumpeo)) : "0.000"));
		srow.put("bSO", ((s_sumpeo > 0) ? (dec.format((float) s_SO / s_sumpeo)) : "0.000"));

		JSONArray footer = new JSONArray();
		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
		rst.put("footer", footer);
		String scols = parms.get("cols");
		if (scols == null) {
			return rst;
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private JSONArray getEmployeeClsCts(Hr_employee he, JSONObject dw, List<String> yms, int empclass, String spname, int findtype, boolean includchd) throws Exception {
		String yearmonths = "";
		for (String ym : yms) {
			yearmonths += "'" + ym + "',";
		}
		if (yearmonths.length() > 0)
			yearmonths = yearmonths.substring(0, yearmonths.length() - 1);
		boolean isnowmonth = false;
		if (yms.size() == 1) {
			isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yms.get(0));
		}

		String sqlstr = "SELECT ";
		if (isnowmonth)
			sqlstr = sqlstr + "'" + Systemdate.getStrDateByFmt(new Date(), "yyyy-MM") + "'  yearmonth, ";
		else
			sqlstr = sqlstr + "  yearmonth,";
		sqlstr = sqlstr + "  IFNULL(SUM(1),0) sumpeo," + " IFNULL(SUM(CASE WHEN hwc_namezl='M类' THEN 1 ELSE 0 END),0) M,"
				+ " IFNULL(SUM(CASE WHEN hwc_namezl='P类' THEN 1 ELSE 0 END),0) P," + " IFNULL(SUM(CASE WHEN hwc_namezl='A类' THEN 1 ELSE 0 END),0) A,"
				+ " IFNULL(SUM(CASE WHEN hwc_namezl='OA' THEN 1 ELSE 0 END),0) OA," + " IFNULL(SUM(CASE WHEN hwc_namezl='OM' THEN 1 ELSE 0 END),0) OM,"
				+ " IFNULL(SUM(CASE WHEN hwc_namezl='OO' THEN 1 ELSE 0 END),0) OO," + " IFNULL(SUM(CASE WHEN hwc_namezl='OP' THEN 1 ELSE 0 END),0) OP";
		if (isnowmonth)
			sqlstr = sqlstr + " FROM hr_employee e ";
		else
			sqlstr = sqlstr + " FROM hr_month_employee e ";
		sqlstr = sqlstr + " WHERE e.`empstatid` IN(" + empstids + ") ";

		if (isnowmonth) {

		} else {
			if (yms.size() == 1)
				sqlstr = sqlstr + " and yearmonth=" + yearmonths + "";
			else if (yms.size() > 1)
				sqlstr = sqlstr + "AND yearmonth IN (" + yearmonths + ") ";
			else
				throw new Exception("月度长度不能为0");
		}

		if (empclass == 2)
			sqlstr = sqlstr + " and e.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and e.emnature='非脱产'";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and e.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and e.sp_name like '%" + spname + "%'";
		}

		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");

		sqlstr = parExtSQL("e", sqlstr);

		if (!isnowmonth)
			sqlstr = sqlstr + " GROUP BY yearmonth";
		return HRUtil.getReadPool().opensql2json_O(sqlstr);
	}

	// childs包含子节点
	public List<HashMap<String, String>> getEmployeeClass(Hr_employee he, JSONObject dw, String yearmonth, boolean childs) throws Exception {
		String sqlstr = null;
		if (childs)
			sqlstr = String.format(EmployeeClassSql, yearmonth, "AND o.idpath LIKE '" + dw.getString("idpath") + "%'");
		else
			sqlstr = String.format(EmployeeClassSql, yearmonth, "AND o.orgid=" + dw.getString("orgid"));
		return HRUtil.getReadPool().openSql2List(sqlstr);
	}

	private String EmployeeClassSql = "SELECT wc.hw_code, IFNULL(tb1.ct,0) ct " + " FROM hr_wclass wc LEFT JOIN "
			+ " (SELECT hwc_idzl,hw_codezl,COUNT(*) ct FROM  " + " (SELECT h.*,ho.hwc_idzl,ho.hw_codezl "
			+ " FROM hr_month_employee h,hr_orgposition ho,shworg o  "
			+ " WHERE h.yearmonth='%s' AND h.empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) "
			+ "       AND h.ospid=ho.ospid AND h.orgid=o.orgid and o.usable=1 " + "   %s    " + " ) tb " + " GROUP BY hwc_idzl,hw_codezl) tb1 "
			+ " ON wc.hwc_id=tb1.hwc_idzl " + " WHERE wc.type_id=1 ";

	@ACOAction(eventname = "findblackaddrpt", Authentication = true, notes = "员工加黑分析")
	public String findblackaddrpt() throws Exception {
		String[] notnull = {};
		String sqlstr = "SELECT * FROM hr_black_add WHERE stat=9 " + CSContext.getIdpathwhere();
		return new CReport(HRUtil.getReadPool(), sqlstr, "createtime desc", notnull).findReport();
	}

	@ACOAction(eventname = "findblackdelrpt", Authentication = true, notes = "员工减黑分析")
	public String findblackdelrpt() throws Exception {
		String[] notnull = {};
		String sqlstr = "SELECT * FROM hr_black_del WHERE stat=9 " + CSContext.getIdpathwhere() + " order by createtime desc";
		return new CReport(HRUtil.getReadPool(), sqlstr, "createtime desc", notnull).findReport();
	}

	@ACOAction(eventname = "findzyrpt", Authentication = true, notes = "员工专业分析")
	public String findzyrpt() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		List<String> yearmonths = new ArrayList<String>();
		yearmonths.add(yearmonth);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths); // 第一个为当前机构
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");

		int sumpeo = 0;
		JSONArray rows = new JSONArray();
		for (int i = 0; i < dws.size(); i++) {
			boolean includechld = (!includechild || (i != 0));// 包含子机构 且 为自身机构时候
			// 不查询子机构数据
			JSONObject dw = dws.getJSONObject(i);
			JSONArray dwrst = getOrgZYPres(he, dw, yearmonth, empclass, spname, findtype, includechld);
			if (dwrst.size() != 0) {
				rows.addAll(dwrst);
				JSONObject row = dwrst.getJSONObject(dwrst.size() - 1);
				sumpeo += row.getInt("ct");
			}
		}

		JSONObject srow = new JSONObject();
		srow.put("ct", sumpeo);
		srow.put("orgname", "合计");
		srow.put("bl", "1");

		JSONArray footer = new JSONArray();
		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", rows);
		rst.put("footer", footer);

		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(rows, scols);
			return null;
		}

	}

	private JSONArray getOrgZYPres(Hr_employee he, JSONObject dw, String yearmonth, int empclass, String spname, int findtype, boolean childs) throws Exception {
		boolean isnowmonth = isNowMonth("'" + yearmonth + "'");
		String sqlstr = "SELECT '" + yearmonth + "'  yearmonth, '" + dw.getString("orgname") + "' orgname, major,COUNT(*) ct "
				+ "FROM   " + ((isnowmonth) ? "hr_employee" : "hr_month_employee")
				+ "  where 1=1 ";
		if (!isnowmonth)
			sqlstr = sqlstr + "and  yearmonth='" + yearmonth + "'";
		sqlstr += " and empstatid in( " + empstids + " ) ";

		if (childs)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");

		if (empclass == 2)
			sqlstr = sqlstr + " and emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and emnature='非脱产'";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}

		sqlstr = parExtSQL(null, sqlstr);

		sqlstr = sqlstr + " GROUP BY major";
		JSONArray dwrst = HRUtil.getReadPool().opensql2json_O(sqlstr);
		int sumpeo = 0;
		for (int i = 0; i < dwrst.size(); i++) {
			JSONObject to = dwrst.getJSONObject(i);
			sumpeo = sumpeo + to.getInt("ct");
		}
		DecimalFormat dec = new DecimalFormat("0.0000");
		for (int i = 0; i < dwrst.size(); i++) {
			JSONObject to = dwrst.getJSONObject(i);
			to.put("bl", dec.format((float) to.getInt("ct") / sumpeo));
		}
		if (dwrst.size() != 0) {
			JSONObject to = new JSONObject();
			to.put("yearmonth", yearmonth);
			to.put("orgname", dw.getString("orgname"));
			to.put("major", "小计");
			to.put("ct", sumpeo);
			dwrst.add(to);
			return dwrst;
		} else
			return dwrst;
	}

	private int zlzwbzrpttreeid = 0;

	@ACOAction(eventname = "findzlzwbzrpt", Authentication = true, notes = "职类职位编制分析")
	public String findzlzwbzrpt() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgcode");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgcode");
		if (jp == null) {
			throw new Exception("需要参数orgcode");
		}
		String orgcode = jp.getParmvalue();

		Shworg org = new Shworg();
		org.findBySQL("select * from shworg where usable=1 and code='" + orgcode + "'", false);
		if (org.isEmpty())
			throw new Exception("编码为【" + orgcode + "】的机构不存在");

		JSONArray dws = HRUtil.getOrgsByPid(org.orgid.getValue());
		Hr_employee he = new Hr_employee();

		dws.add(0, org.toJsonObj());
		// System.out.println("dws:" + dws.toString());
		zlzwbzrpttreeid = 0;
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);
			dw.put("treeid", zlzwbzrpttreeid++);
			getzlzwbz(HRUtil.getReadPool(), dw, true);
		}
		String scols = parms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private void getzlzwbz(CDBPool pool, JSONObject dw, boolean childs) throws Exception {
		String w = null;
		if (childs)
			w = "idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			w = "orgid=" + dw.getString("orgid");
		String sqlstr = String.format(orgzlsql, w, w);
		JSONArray zls = pool.opensql2json_O(sqlstr);
		int dwsumemp = 0;
		for (int i = 0; i < zls.size(); i++) {
			JSONObject zl = zls.getJSONObject(i);
			if (childs)
				w = "idpath LIKE '" + dw.getString("idpath") + "%'";
			else
				w = "orgid=" + dw.getString("orgid");
			sqlstr = String.format(orglzempctsql, w, zl.getString("hwc_idzl"));
			int quota = Integer.valueOf(zl.getInt("quota"));
			int empsum = Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
			dwsumemp += empsum;
			zl.put("empsum", empsum);
			zl.put("cbnum", (empsum <= quota) ? 0 : (empsum - quota));
			zl.put("treeid", zlzwbzrpttreeid++);
			zl.put("state", "closed");
			getzwbz(pool, dw, zl, childs);
		}
		dw.put("empsum", dwsumemp);
		dw.put("children", zls);
	}

	private void getzwbz(CDBPool pool, JSONObject dw, JSONObject zl, boolean childs) throws Exception {
		String w = null;
		if (childs)
			w = "idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			w = "orgid=" + dw.getString("orgid");
		String sqlstr = String.format(orgzlzwctsql, zl.getString("hwc_idzl"), w);
		JSONArray jos = pool.opensql2json_O(sqlstr);
		int s_empsum = 0;
		int s_cbnum = 0;
		int s_quota = 0;
		for (int i = 0; i < jos.size(); i++) {
			JSONObject jo = jos.getJSONObject(i);
			sqlstr = String.format(zwempctsql, w, zl.getString("hwc_idzl"), jo.get("sp_id"));
			int quota = Integer.valueOf(jo.getInt("quota"));
			int empsum = Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("empsum"));
			jo.put("empsum", empsum);
			jo.put("cbnum", (empsum <= quota) ? 0 : (empsum - quota));
			jo.put("treeid", zlzwbzrpttreeid++);

			s_empsum += Integer.valueOf(jo.getString("empsum"));
			s_cbnum += Integer.valueOf(jo.getString("cbnum"));
			s_quota += Integer.valueOf(jo.getString("quota"));
		}
		dw.put("empsum", s_empsum);
		dw.put("cbnum", s_cbnum);
		dw.put("quota", s_quota);
		zl.put("children", jos);
	}

	private String zwempctsql = "SELECT COUNT(*) empsum FROM hr_employee emp "
			+ " WHERE emp.ospid IN("
			+ " SELECT ospid FROM hr_orgposition osp WHERE osp.%s AND osp.hwc_idzl=%s AND osp.sp_id=%s )"
			+ " AND emp.empstatid IN "
			+ "   (SELECT statvalue FROM hr_employeestat WHERE usable = 1 AND isquota = 1) ";

	private String orgzlzwctsql = "SELECT osp.sp_id, osp.sp_name orgname, ifnull(SUM(osp.quota),0) quota " + " FROM hr_orgposition osp WHERE osp.hwc_idzl = %s "
			+ "  AND osp.%s" + " GROUP BY osp.sp_id, osp.sp_name";

	private String orglzempctsql = "SELECT COUNT(*) ct " + " FROM hr_employee e,hr_orgposition osp"
			+ " WHERE e.ospid=osp.ospid AND empstatid IN(SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1)"
			+ " AND osp.%s AND osp.hwc_idzl=%s";

	private String orgzlsql = "select "
			+ "  hwc_idzl, hwc_namezl orgname, ifnull(sum(quota),0) quota "
			+ "from "
			+ "  (select "
			+ "    osp.hwc_idzl, osp.hwc_namezl, osp.quota "
			+ "  from "
			+ "    hr_orgposition osp "
			+ "  where osp.%s "
			+ "  union all "
			+ "  SELECT "
			+ "    c.hwc_id hwc_idzl, c.hwc_name hwc_namezl, qc.quota "
			+ "  FROM "
			+ "    hr_quotaoc qc, shworg org, hr_wclass c "
			+ "  WHERE qc.orgid = org.orgid "
			+ "    AND qc.usable = 1 "
			+ "    AND qc.classid = c.hwc_id "
			+ "    and org.%s) tb "
			+ "where hwc_idzl <> 0 "
			+ "group by hwc_idzl, hwc_namezl ";

	@ACOAction(eventname = "contractanalysis", Authentication = true, notes = "合同签订分析")
	public String contractanalysis() throws Exception {
		String yearmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM");
		List<String> yms = new ArrayList<String>();
		yms.add(yearmonth);

		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要参数orgid");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		JSONParm jp = getParmByName(jps, "orgid");
		if (jp == null) {
			throw new Exception("需要参数orgid");
		}
		String orgid = jp.getParmvalue();
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));

		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yms);
		Hr_employee he = new Hr_employee();
		DecimalFormat dec = new DecimalFormat("0.0000");
		Shworg org = new Shworg();
		org.findByID(orgid, false);
		if (org.isEmpty())
			throw new Exception("ID为【" + orgid + "】的机构不存在");

		for (int i = 0; i < dws.size(); i++) {

			JSONObject dw = dws.getJSONObject(i);
			boolean includchd = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
			// 且
			// 为自身机构时候
			// 不查询子机构数据
			Shworg countorg = new Shworg();
			countorg.findByID(dw.getString("orgid"));
			int empsum = getOrgEmpctSum(he, countorg, yearmonth, 0, null, 0, includchd); // 单位总人数
			int signnumber = getsignnumbers(he.pool, dw, includchd);// 合同签订人数
			int openend = getopenends(he.pool, dw, includchd);// 无固定合同签订人数
			int emnature1 = getoverdues(he.pool, dw, includchd, "脱产");// 脱产下月合同到期人数
			int emnature2 = getoverdues(he.pool, dw, includchd, "非脱产");// 非脱产下月合同到期人数
			int constop = getopenends(he.pool, dw, includchd, 2);// 终止合同人数
			int concancel = getopenends(he.pool, dw, includchd, 5);// 解除合同人数
			int conover = getopenends(he.pool, dw, includchd, 3);// 过期合同人数
			int shoudsign = getShouldSign(he.pool, dw, includchd);// 应续签人数，已签但过期的和入职但没签的总和
			int realsign = getRealSign(he.pool, dw, includchd);// 实际续签人数，合同表里有续签状态人员的个数

			dw.put("empsum", empsum);
			dw.put("sn", signnumber);
			dw.put("oe", openend);
			dw.put("et1", emnature1);
			dw.put("et2", emnature2);
			dw.put("cs", constop);
			dw.put("cc", concancel);
			dw.put("co", conover);
			dw.put("ss", shoudsign);
			dw.put("rs", realsign);

			dw.put("sn1", ((empsum > 0) ? (dec.format((float) signnumber / empsum)) : "0.000"));// 合同签订率
			dw.put("oe1", ((empsum > 0) ? (dec.format((float) openend / empsum)) : "0.000"));// 无固定期限合同签订率
			dw.put("cs1", ((empsum > 0) ? (dec.format((float) constop / empsum)) : "0.000"));// 终止比率
			dw.put("cc1", ((empsum > 0) ? (dec.format((float) concancel / empsum)) : "0.000"));// 解除比率
			dw.put("co1", ((empsum > 0) ? (dec.format((float) conover / empsum)) : "0.000"));// 过期比率
			dw.put("xq1", ((empsum > 0) ? (dec.format((float) realsign / empsum)) : "0.000"));// 续签达成率。
			// 实际续签人数/总人数？？

		}
		String scols = parms.get("cols");
		if (scols == null) {
			return dws.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private int getsignnumbers(CDBPool pool, JSONObject dw, boolean childs) throws Exception {
		String sqlstr = null;
		sqlstr = "SELECT COUNT(DISTINCT er_id) ct from `hr_employee_contract` WHERE stat=9 AND contractstat=1 "
				+ " AND er_id IN (SELECT er_id FROM hr_employee WHERE usable=1 "
				+ " AND empstatid IN (SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1 ))";
		if (childs) {
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		} else {
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		}
		// sqlstr=sqlstr+" group by er_id";
		if (pool.opensql2json_O(sqlstr).size() > 0) {
			JSONObject jo = pool.opensql2json_O(sqlstr).getJSONObject(0);
			return Integer.valueOf(jo.get("ct").toString());
		} else {
			return 0;
		}
	}

	private int getopenends(CDBPool pool, JSONObject dw, boolean childs) throws Exception {
		String sqlstr = null;
		sqlstr = "SELECT  COUNT(DISTINCT er_id) ct FROM `hr_employee_contract` WHERE stat=9 AND contractstat=1  AND deadline_type=2 ";
		if (childs) {
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		} else {
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		}
		;
		if (pool.opensql2json_O(sqlstr).size() > 0) {
			JSONObject jo = pool.opensql2json_O(sqlstr).getJSONObject(0);

			return Integer.valueOf(jo.get("ct").toString());
		} else {
			return 0;
		}
	}

	private int getoverdues(CDBPool pool, JSONObject dw, boolean childs, String nature) throws Exception {
		String sqlstr = null;
		sqlstr = "SELECT count(*) ct FROM `hr_employee_contract` con,hr_employee emp "
				+ " WHERE con.stat=9 AND con.contractstat=1 AND con.end_date>DATE_ADD(CURDATE() - DAY(CURDATE()) + 1, INTERVAL 1 MONTH) AND "
				+ " con.end_date<=LAST_DAY( DATE_ADD(CURDATE() - DAY(CURDATE()) + 1, INTERVAL 1 MONTH)) AND con.er_id=emp.er_id AND emp.emnature='" + nature
				+ "'";
		if (childs) {
			sqlstr = sqlstr + " AND con.idpath LIKE '" + dw.getString("idpath") + "%'";
		} else {
			sqlstr = sqlstr + " AND con.orgid=" + dw.getString("orgid");
		}

		if (pool.openSql2List(sqlstr).isEmpty()) {
			return 0;
		} else {
			return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
		}
	}

	private int getopenends(CDBPool pool, JSONObject dw, boolean childs, int constat) throws Exception {
		String sqlstr = null;
		sqlstr = "SELECT count(DISTINCT er_id) ct from `hr_employee_contract` WHERE stat=9 AND contractstat=" + constat;
		if (childs) {
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		} else {
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		}

		if (pool.openSql2List(sqlstr).isEmpty()) {
			return 0;
		} else {
			return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
		}
	}

	private int getShouldSign(CDBPool pool, JSONObject dw, boolean childs) throws Exception {
		String sql1 = "SELECT  COUNT(*)  ct FROM hr_employee_contract con WHERE con.stat=9 AND end_date<CURDATE() AND con.contractstat=1  AND er_id IN "
				+ "(SELECT er_id FROM hr_employee WHERE usable=1 AND empstatid IN ( SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) ";

		String sql2 = "SELECT (case COUNT(*) when COUNT(*)>0 then COUNT(*) else 0 end ) ct FROM hr_employee WHERE usable=1 AND empstatid IN ( SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) "
				+ " AND er_id NOT IN (SELECT er_id FROM hr_employee_contract WHERE stat=9 AND contractstat=1 ";

		if (childs) {
			sql1 = sql1 + " AND idpath LIKE '" + dw.getString("idpath") + "%') ";
			sql2 = sql2 + " AND idpath LIKE '" + dw.getString("idpath") + "%') AND idpath LIKE '" + dw.getString("idpath") + "%' ";
		} else {
			sql1 = sql1 + " AND orgid=" + dw.getString("orgid") + " )  ";
			sql2 = sql2 + " AND orgid=" + dw.getString("orgid") + ") AND orgid=" + dw.getString("orgid");
		}
		int oversign = Integer.valueOf(pool.openSql2List(sql1).get(0).get("ct"));
		int neversign = Integer.valueOf(pool.openSql2List(sql2).get(0).get("ct"));

		return oversign + neversign;
	}

	private int getRealSign(CDBPool pool, JSONObject dw, boolean childs) throws Exception {
		String sqlstr = null;
		sqlstr = "SELECT (case COUNT(*) when COUNT(*)>0 then COUNT(*) else 0 end) ct FROM hr_employee_contract con WHERE con.stat=9 AND con.contractstat=6 AND con.er_id IN "
				+ " (SELECT er_id FROM hr_employee WHERE usable=1 AND empstatid IN "
				+ " ( SELECT statvalue FROM hr_employeestat WHERE usable=1 AND isquota=1) ";
		if (childs) {
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%') GROUP BY con.er_id";
		} else {
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid") + " )  GROUP BY con.er_id";
		}
		if (pool.openSql2List(sqlstr).isEmpty()) {
			return 0;
		} else {
			return Integer.valueOf(pool.openSql2List(sqlstr).get(0).get("ct"));
		}
	}

	@ACOAction(eventname = "staffretention", Authentication = true, notes = "人员流失率")
	public String staffretention() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");

		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));

		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		// List<String> yearmonths = new ArrayList<String>();
		// yearmonths.add(yearmonth);
		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 24);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);

		return dogetdetail(dws, orgid, yearmonths, empclass, spname, findtype, parms);
	}

	private String dogetdetail(JSONArray dws, String orgid, List<String> yearmonths, int empclass, String spname, int findtype, HashMap<String, String> parms)
			throws Exception {
		for (String yearmonth : yearmonths) {
			String pyearmonth = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(Systemdate.getDateByStr(yearmonth), -1), "yyyy-MM");
			for (int i = 0; i < dws.size(); i++) {
				int s_offjobquota = 0, s_noffjobquota = 0, s_totalquota = 0, s_emnum = 0, s_emnumoff = 0, s_emnumnoff = 0;
				int s_selfleave = 0, s_quit = 0, s_dismiss = 0, s_reduce = 0, s_retire = 0, s_cpst = 0, s_ncpst = 0, s_totalleave = 0;
				JSONObject dw = dws.getJSONObject(i);
				String eyearmonth = dw.getString("yearmonth");
				if (yearmonth.equalsIgnoreCase(eyearmonth)) {
					// boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
					boolean includechld = dw.getInt("_incc") == 1;
					// 且
					// 为自身机构时候
					// 不查询子机构数据
					// 机构脱产编制
					dw.put("offjobquota", getOffQuota(dw, yearmonth, includechld));
					s_offjobquota += dw.getInt("offjobquota");

					// 机构非脱产编制 可能要从 机构职类编制获取？？？？？？？

					dw.put("noffjobquota", getNotOffQuota(dw, yearmonth, includechld));// 非脱产编制
					s_noffjobquota += dw.getInt("noffjobquota");
					dw.put("totalquota", dw.getInt("offjobquota") + dw.getInt("noffjobquota"));// 总编制数
					s_totalquota += dw.getInt("totalquota");

					// 实际人数
					HashMap<String, String> mp = getTrueEmpCts(dw, pyearmonth, yearmonth, empclass, includechld);
					dw.put("emnum", mp.get("emnum"));
					dw.put("emnumoff", mp.get("emnumoff"));// 脱产人数
					dw.put("emnumnoff", mp.get("emnumnoff"));//
					s_emnum += dw.getInt("emnum");

					s_emnumoff += dw.getInt("emnumoff");
					s_emnumnoff += dw.getInt("emnumnoff");

					// 离职人数
					mp = getLVDatainfo(dw, yearmonth, empclass, spname, findtype, includechld);
					dw.put("selfleave", mp.get("ct1"));// 自离
					dw.put("quit", mp.get("ct2"));// 辞职
					dw.put("dismiss", mp.get("ct3"));// 辞退
					dw.put("reduce", mp.get("ct4"));// 裁员
					dw.put("retire", mp.get("ct5"));// 退休
					dw.put("cpst", mp.get("ct6"));// 有经济补偿
					dw.put("ncpst", mp.get("ct7"));// 无经济补偿
					dw.put("totalleave", dw.getInt("selfleave") + dw.getInt("quit") + dw.getInt("dismiss") + dw.getInt("reduce") + dw.getInt("retire")
							+ dw.getInt("cpst") + dw.getInt("ncpst"));// 合计离职人数
					s_selfleave += dw.getInt("selfleave");
					s_quit += dw.getInt("quit");
					s_dismiss += dw.getInt("dismiss");
					s_reduce += dw.getInt("reduce");
					s_retire += dw.getInt("retire");
					s_cpst += dw.getInt("cpst");
					s_ncpst += dw.getInt("ncpst");
					s_totalleave += dw.getInt("totalleave");

					// 员工流失率
					DecimalFormat dec = new DecimalFormat("0.0000");
					dw.put("bsf", ((dw.getInt("emnum") > 0) ? (dec.format((float) dw.getInt("selfleave") / dw.getInt("emnum"))) : "0.000"));// 自离率
					dw.put("bq", ((dw.getInt("emnum") > 0) ? (dec.format((float) dw.getInt("quit") / dw.getInt("emnum"))) : "0.000"));// 辞职率
					dw.put("bdm", ((dw.getInt("emnum") > 0) ? (dec.format((float) dw.getInt("dismiss") / dw.getInt("emnum"))) : "0.000"));// 辞退率
					dw.put("brd", ((dw.getInt("emnum") > 0) ? (dec.format((float) dw.getInt("reduce") / dw.getInt("emnum"))) : "0.000"));// 退休率
					dw.put("brt", ((dw.getInt("emnum") > 0) ? (dec.format((float) dw.getInt("retire") / dw.getInt("emnum"))) : "0.000"));// 裁员率
					dw.put("bcpst", ((dw.getInt("emnum") > 0) ? (dec.format((float) dw.getInt("cpst") / dw.getInt("emnum"))) : "0.000"));// 有经济补偿
					dw.put("bncpst", ((dw.getInt("emnum") > 0) ? (dec.format((float) dw.getInt("ncpst") / dw.getInt("emnum"))) : "0.000"));// 无经济补偿
					dw.put("bsls", dw.getDouble("bsf") + dw.getDouble("bq"));// 主动流失率
					dw.put("brls", dw.getDouble("bdm") + dw.getDouble("brd") + dw.getDouble("brt"));// 被动流失率
					dw.put("btls", dw.getDouble("bcpst") + dw.getDouble("bncpst"));// 协商流失率
					dw.put("btotals", dw.getDouble("bsls") + dw.getDouble("brls") + dw.getDouble("btls"));// 绝对流失率
				}
//如果需要加小结
			}
		}

//		JSONObject srow = new JSONObject();
//		srow.put("offjobquota", s_offjobquota);
//		srow.put("noffjobquota", s_noffjobquota);
//		srow.put("totalquota", s_totalquota);
//		srow.put("emnum", s_emnum);
//		srow.put("emnumoff", s_emnumoff);
//		srow.put("emnumnoff", s_emnumnoff);
//		srow.put("selfleave", s_selfleave);
//		srow.put("quit", s_quit);
//		srow.put("dismiss", s_dismiss);
//		srow.put("reduce", s_reduce);
//		srow.put("retire", s_retire);
//		srow.put("cpst", s_cpst);
//		srow.put("ncpst", s_ncpst);
//		srow.put("totalleave", s_totalleave);
//		srow.put("orgname", "合计");
//		JSONArray footer = new JSONArray();
//		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
//		rst.put("footer", footer);
		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	/**
	 * @param dw
	 * @param yearmonth
	 * @param includechld
	 * @return 离职信息
	 * @throws Exception
	 */
	private HashMap<String, String> getLVDatainfo(JSONObject dw, String yearmonth, int empclass, String spname, int findtype, boolean includechld) throws Exception {
		String sqlstr = "SELECT IFNULL(SUM(CASE  WHEN hl.ljtype1=1 THEN 1 ELSE 0 END ),0) ct1,IFNULL(SUM(CASE  WHEN hl.ljtype1=2 THEN 1 ELSE 0 END ),0) ct2,"
				+ "IFNULL(SUM(CASE  WHEN hl.ljtype1=3 THEN 1 ELSE 0 END ),0) ct3,IFNULL(SUM(CASE  WHEN hl.ljtype1=4 THEN 1 ELSE 0 END ),0) ct4,"
				+ "IFNULL(SUM(CASE  WHEN hl.ljtype1=5 THEN 1 ELSE 0 END ),0) ct5,IFNULL(SUM(CASE  WHEN hl.ljtype1=6 THEN 1 ELSE 0 END ),0) ct6,"
				+ "IFNULL(SUM(CASE  WHEN hl.ljtype1=7 THEN 1 ELSE 0 END ),0) ct7 "
				+ " FROM `hr_leavejob` hl,hr_employee emp WHERE hl.stat=9 AND hl.ljtype=2 and hl.er_id=emp.er_id and hl.iscanced=2  "
				+ "and DATE_FORMAT(hl.ljdate,'%Y-%m')='" + yearmonth + "'";
		if (includechld)
			sqlstr = sqlstr + " AND hl.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND hl.orgid=" + dw.getString("orgid");

		if (empclass == 2)
			sqlstr = sqlstr + " and emp.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and emp.emnature='非脱产'";

		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and hl.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and hl.sp_name like '%" + spname + "%'";
		}
		sqlstr = parExtSQL("emp", sqlstr);
		return HRUtil.getReadPool().openSql2List(sqlstr).get(0);
	}

	/**
	 * @param dw
	 * @param yearmonth
	 * @param pyearmonth
	 * @param includechld
	 * @return 实际人数
	 * @throws Exception
	 */
	public static HashMap<String, String> getTrueEmpCts(JSONObject dw, String pyearmonth, String yearmonth, int empclass, boolean includechld)
			throws Exception {
		boolean isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yearmonth);
		int d = (isnowmonth) ? 1 : 2;
		String sqlstr = "SELECT ifnull(round(COUNT(*) /" + d + "),0) emnum,ifnull(round(SUM(CASE WHEN emnature='脱产' THEN 1 ELSE 0 END)/" + d + "),0) emnumoff,"
				+ "ifnull(round(SUM(CASE WHEN emnature='非脱产' THEN 1 ELSE 0 END)/" + d + "),0) emnumnoff ";
		if (!isnowmonth)
			sqlstr = sqlstr + "FROM hr_month_employee he ";
		else
			sqlstr = sqlstr + "FROM hr_employee he ";

		sqlstr = sqlstr + " WHERE he.empstatid IN(" + empstids + ") ";
		if (!isnowmonth)
			sqlstr = sqlstr + "and yearmonth in('" + pyearmonth + "','" + yearmonth + "') ";
		if (includechld)
			sqlstr = sqlstr + " AND he.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND he.orgid=" + dw.getString("orgid");

		if (empclass == 2)
			sqlstr = sqlstr + " and he.emnature='脱产'";
		if (empclass == 3)
			sqlstr = sqlstr + " and he.emnature='非脱产'";

		sqlstr = parExtSQL("he", sqlstr);

		return HRUtil.getReadPool().openSql2List(sqlstr).get(0);
	}

	/**
	 * @param dw
	 * @param yearmonth
	 * @param includechld
	 * @return 机构脱产编制
	 * @throws Exception
	 */
	public static int getOffQuota(JSONObject dw, String yearmonth, boolean includechld) throws Exception {
		boolean isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yearmonth);
		String sqlstr = "SELECT IFNULL(SUM(op.quota),0) quota ";
		if (isnowmonth)
			sqlstr = sqlstr + " FROM hr_orgposition op,hr_standposition sp ";
		else
			sqlstr = sqlstr + " FROM hr_month_orgposition op,hr_standposition sp ";
		sqlstr = sqlstr + " WHERE 1=1  ";
		if (!isnowmonth)
			sqlstr = sqlstr + " and op.yearmonth=" + yearmonth + "";
		if (includechld)
			sqlstr = sqlstr + " AND op.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND op.orgid=" + dw.getString("orgid");
		sqlstr = sqlstr + " AND op.sp_id=sp.sp_id AND op.isoffjob=1";
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("quota"));
	}

	/**
	 * @param dw
	 * @param yearmonth
	 * @param includechld
	 * @return 非脱产编制
	 * @throws Exception
	 */
	public static int getNotOffQuota(JSONObject dw, String yearmonth, boolean includechld) throws Exception {
		boolean isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yearmonth);
		String sqlstr = "SELECT IFNULL(SUM(q.quota),0) quota FROM ";
		if (isnowmonth)
			sqlstr = sqlstr + " hr_quotaoc q, hr_wclass c,shworg o";
		else
			sqlstr = sqlstr + " hr_month_quotaoc q, hr_wclass c,shworg o";
		sqlstr = sqlstr + " WHERE q.orgid=o.orgid AND q.classid=c.hwc_id AND c.isoffjob=2 and o.usable=1 ";
		if (!isnowmonth)
			sqlstr = sqlstr + " and q.yearmonth='" + yearmonth + "'";
		if (includechld)
			sqlstr = sqlstr + " AND o.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND o.orgid=" + dw.getString("orgid");
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("quota"));
	}

	/**
	 * @param dw
	 * @param yearmonth
	 * @param includechld
	 * @return 入职信息 不需要关联到月结人事表
	 * @throws Exception
	 */
	public static HashMap<String, String> getEntyInfo(JSONObject dw, int empclass, String spname, int findtype, boolean includechld, Date bgdate, Date eddate) throws Exception {
		String sqlstr = "SELECT  IFNULL(SUM(1),0) sument, IFNULL(SUM(CASE WHEN osp.isoffjob=1 THEN 1 ELSE 0 END),0) entnumoff, "
				+ "  IFNULL(SUM(CASE WHEN osp.isoffjob=2 THEN 1 ELSE 0 END),0) entnumnoff"
				+ " FROM `hr_entry` n, `hr_orgposition` osp  WHERE  n.`stat`=9  and n.ospid=osp.ospid ";
		if (includechld)
			sqlstr = sqlstr + " AND osp.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND osp.orgid=" + dw.getString("orgid");
		sqlstr = sqlstr + " AND n.entrydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND n.entrydate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "'";
		if (empclass == 2)
			sqlstr = sqlstr + " and osp.isoffjob=1";
		if (empclass == 3)
			sqlstr = sqlstr + " and osp.isoffjob=2";
		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and osp.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and osp.sp_name like '%" + spname + "%'";
		}
		sqlstr = parExtSQL("osp", sqlstr);/// 可能有问题

		JSONObject jo1 = HRUtil.getReadPool().opensql2json_O(sqlstr).getJSONObject(0);

		//// 招募
		sqlstr = "SELECT  IFNULL(SUM(1),0) sument, IFNULL(SUM(CASE WHEN osp.isoffjob=1 THEN 1 ELSE 0 END),0) entnumoff, "
				+ "  IFNULL(SUM(CASE WHEN osp.isoffjob=2 THEN 1 ELSE 0 END),0) entnumnoff"
				+ " FROM `hr_recruit_form` n, `hr_orgposition` osp  WHERE  n.`stat`<10  and n.ospid=osp.ospid ";
		if (includechld)
			sqlstr = sqlstr + " AND osp.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND osp.orgid=" + dw.getString("orgid");
		sqlstr = sqlstr + " AND n.entrydate>='" + Systemdate.getStrDateyyyy_mm_dd(bgdate)
				+ "' AND n.entrydate<='" + Systemdate.getStrDateyyyy_mm_dd(eddate) + "'";

		if (empclass == 2)
			sqlstr = sqlstr + " and osp.isoffjob=1";
		if (empclass == 3)
			sqlstr = sqlstr + " and osp.isoffjob=2";
		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and osp.sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and osp.sp_name like '%" + spname + "%'";
		}
		sqlstr = parExtSQL("osp", sqlstr);/// 可能有问题

		JSONObject jo2 = HRUtil.getReadPool().opensql2json_O(sqlstr).getJSONObject(0);

		HashMap<String, String> rst = new HashMap<String, String>();
		rst.put("sument", String.valueOf(jo1.getInt("sument") + jo2.getInt("sument")));
		rst.put("entnumoff", String.valueOf(jo1.getInt("entnumoff") + jo2.getInt("entnumoff")));
		rst.put("entnumnoff", String.valueOf(jo1.getInt("entnumnoff") + jo2.getInt("entnumnoff")));

		return rst;
	}

	// ////////////////////////////////

	@ACOAction(eventname = "rpt_hrrztj", Authentication = true, notes = "人员入职分析")
	public String rpt_hrrztj() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");

		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));

		String strempclass = CorUtil.getJSONParmValue(jps, "empclass", "需要参数【empclass】");
		int empclass = (strempclass == null) ? 1 : Integer.valueOf(strempclass);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 24);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		return dogetLVdetail(dws, orgid, yearmonths, empclass, parms);
	}

	private String dogetLVdetail(JSONArray dws, String orgid, List<String> yearmonths, int empclass, HashMap<String, String> parms)
			throws Exception {

		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		for (String yearmonth : yearmonths) {
			String pyearmonth = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(Systemdate.getDateByStr(yearmonth), -1), "yyyy-MM");
			Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(yearmonth)));// 去除时分秒
			Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
			for (int i = 0; i < dws.size(); i++) {
				int s_offjobquota = 0, s_noffjobquota = 0, s_totalquota = 0, s_emnum = 0, s_emnumoff = 0, s_emnumnoff = 0;
				int s_sument = 0, s_entnumoff = 0, s_entnumnoff = 0;

				JSONObject dw = dws.getJSONObject(i);
				// boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
				// 且
				// 为自身机构时候
				// 不查询子机构数据
				// 机构脱产编制
				boolean includechld = dw.getInt("_incc") == 1;
				String eyearmonth = dw.getString("yearmonth");
				if (yearmonth.equalsIgnoreCase(eyearmonth)) {
					dw.put("offjobquota", getOffQuota(dw, yearmonth, includechld));
					s_offjobquota += dw.getInt("offjobquota");

					// 机构非脱产编制 可能要从 机构职类编制获取？？？？？？？

					dw.put("noffjobquota", getNotOffQuota(dw, yearmonth, includechld));// 非脱产编制
					s_noffjobquota += dw.getInt("noffjobquota");
					dw.put("totalquota", dw.getInt("offjobquota") + dw.getInt("noffjobquota"));// 总编制数
					s_totalquota += dw.getInt("totalquota");

					// 实际人数
					HashMap<String, String> mp = getTrueEmpCts(dw, pyearmonth, yearmonth, empclass, includechld);
					dw.put("emnum", mp.get("emnum"));
					dw.put("emnumoff", mp.get("emnumoff"));// 脱产人数
					dw.put("emnumnoff", mp.get("emnumnoff"));//
					s_emnum += dw.getInt("emnum");
					s_emnumoff += dw.getInt("emnumoff");
					s_emnumnoff += dw.getInt("emnumnoff");

					// 入职人数
					mp = getEntyInfo(dw, empclass, spname, findtype, includechld, bgdate, eddate);
					dw.put("sument", mp.get("sument"));
					dw.put("entnumoff", mp.get("entnumoff"));// 脱产人数
					dw.put("entnumnoff", mp.get("entnumnoff"));//
					s_sument += dw.getInt("sument");
					s_entnumoff += dw.getInt("entnumoff");
					s_entnumnoff += dw.getInt("entnumnoff");
				}
			}
			/// 小结
		}

//		JSONObject srow = new JSONObject();
//		srow.put("offjobquota", s_offjobquota);
//		srow.put("noffjobquota", s_noffjobquota);
//		srow.put("totalquota", s_totalquota);
//		srow.put("emnum", s_emnum);
//		srow.put("emnumoff", s_emnumoff);
//		srow.put("emnumnoff", s_emnumnoff);
//		srow.put("sument", s_sument);
//		srow.put("entnumoff", s_entnumoff);
//		srow.put("entnumnoff", s_entnumnoff);
//		srow.put("orgname", "合计");
//		JSONArray footer = new JSONArray();
//		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
//		rst.put("footer", footer);
		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	@ACOAction(eventname = "rpt_mlevtj", Authentication = true, notes = "M lev分析")
	public String rpt_mlevtj() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth", "需要参数【yearmonth】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");

		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 12);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);

		return dogetmlevtj(dws, orgid, yearmonths, parms, spname);
	}

	private String dogetmlevtj(JSONArray dws, String orgid, List<String> yearmonths, HashMap<String, String> parms, String spname) throws Exception {
		for (String yearmonth : yearmonths) {
			int s_bzpznum = 0, s_mlev1 = 0, s_mlev2 = 0, s_mlev3 = 0, s_mlev4 = 0, s_mlev5 = 0, s_mlev6 = 0, s_mlev7 = 0, s_mlev8 = 0, s_mlevall = 0, s_mlevpc = 0, s_cbnum = 0, s_cbpc = 0;
			for (int i = 0; i < dws.size(); i++) {
				JSONObject dw = dws.getJSONObject(i);
				String eyearmonth = dw.getString("yearmonth");
				if (yearmonth.equalsIgnoreCase(eyearmonth)) {
					// Date bgdate = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(yearmonth)));// 去除时分秒
					boolean isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yearmonth);
					boolean includechld = dw.getInt("_incc") == 1;

					int bzpznum = getbzpznum(dw, isnowmonth, yearmonth, spname, includechld);// 标准配置人数
					JSONObject jo = getlevs(dw, isnowmonth, yearmonth, spname, includechld);// 所有lev
					int mlev1 = jo.getInt("mlev1");
					int mlev2 = jo.getInt("mlev2");
					int mlev3 = jo.getInt("mlev3");
					int mlev4 = jo.getInt("mlev4");
					int mlev5 = jo.getInt("mlev5");
					int mlev6 = jo.getInt("mlev6");
					int mlev7 = jo.getInt("mlev7");
					int mlev8 = jo.getInt("mlev8");
					dw.put("bzpznum", bzpznum);
					dw.put("mlev1", mlev1);
					dw.put("mlev2", mlev2);
					dw.put("mlev3", mlev3);
					dw.put("mlev4", mlev4);
					dw.put("mlev5", mlev5);
					dw.put("mlev6", mlev6);
					dw.put("mlev7", mlev7);
					dw.put("mlev8", mlev8);
					dw.put("mlevall", mlev1 + mlev2 + mlev3 + mlev4 + mlev5 + mlev6 + mlev7 + mlev8);
					dw.put("mlevpc", dw.getInt("mlevall") - bzpznum);
					int cbnum = getcb(dw, isnowmonth, yearmonth, spname, includechld);// 储备
					dw.put("cbnum", cbnum);
					dw.put("cbpc", dw.getInt("mlevall") + cbnum - bzpznum);

					s_bzpznum += bzpznum;
					s_mlev1 += mlev1;
					s_mlev2 += mlev2;
					s_mlev3 += mlev3;
					s_mlev4 += mlev4;
					s_mlev5 += mlev5;
					s_mlev6 += mlev6;
					s_mlev7 += mlev7;
					s_mlev8 += mlev8;
					s_mlevall += dw.getInt("mlevall");
					s_mlevpc += dw.getInt("mlevpc");
					s_cbnum += dw.getInt("cbnum");
					s_cbpc += dw.getInt("cbpc");
				}
			}
			// 小结
		}
//		JSONObject srow = new JSONObject();
//		srow.put("bzpznum", s_bzpznum);
//		srow.put("mlev1", s_mlev1);
//		srow.put("mlev2", s_mlev2);
//		srow.put("mlev3", s_mlev3);
//		srow.put("mlev4", s_mlev4);
//		srow.put("mlev5", s_mlev5);
//		srow.put("mlev6", s_mlev6);
//		srow.put("mlev7", s_mlev7);
//		srow.put("mlev8", s_mlev8);
//		srow.put("mlevall", s_mlevall);
//		srow.put("cbnum", s_cbnum);
//		srow.put("cbpc", s_cbpc);
//		srow.put("mlevpc", s_mlevpc);

//		srow.put("orgname", "合计");
		JSONArray footer = new JSONArray();
//		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", dws);
		rst.put("footer", footer);
		String scols = parms.get("cols");
		if (scols == null) {
			return rst.toString();
		} else {
			(new CReport()).export2excel(dws, scols);
			return null;
		}
	}

	private int getbzpznum(JSONObject dw, boolean isnowmonth, String yearmonth, String spname, boolean includechld) throws NumberFormatException, Exception {
		String sqlstr = "SELECT IFNULL(SUM(quota),0) quota FROM ";
		if (isnowmonth)
			sqlstr = sqlstr + " hr_orgposition ";
		else
			sqlstr = sqlstr + " hr_month_orgposition ";
		sqlstr = sqlstr + " WHERE hwc_namezl='M类' ";
		if (!isnowmonth)
			sqlstr = sqlstr + " and yearmonth='" + yearmonth + "'";
		else
			sqlstr = sqlstr + " and usable=1 ";
		if (includechld)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		if ((spname != null) && (!spname.isEmpty())) {
			sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}

		sqlstr = parExtSQL(null, sqlstr);

		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("quota"));
	}

	private JSONObject getlevs(JSONObject dw, boolean isnowmonth, String yearmonth, String spname, boolean includechld) throws Exception {
		String sqlstr = "select " +
				"  ifnull(sum(if(e.lv_num=2,1,0)),0) mlev1, " + // 总监级（2.0）=职级为2.0的人员；
				"  IFNULL(SUM(IF(e.lv_num>2 and e.lv_num<=2.3,1,0)),0) mlev2, " + // 总监级（2.1-2.3）=职级为2.1、2.2、2.3的人员；
				"  IFNULL(SUM(IF(e.mlev=3 or e.sp_name = '高级经理',1,0)),0) mlev3, " + // 职位为高级经理的人员+M类数据基础表 层级为高级经理（3.0-3.1）的人员
				"  IFNULL(SUM(IF(e.mlev=4 or e.sp_name='经理' or e.sp_name='副经理' or e.sp_name='见习副经理' or e.sp_name='警务室主任' or e.sp_name='警务室副主任',1,0)),0) mlev4, " + // 经理级（3.1-3.3）=职位为经理、副经理、见习副经理、警务室主任、警务室副主任的人员+M类数据基础表 层级为经理（3.1-3.3）的人员；
				"  IFNULL(SUM(IF(e.mlev=5,1,0)),0) mlev5, " + // 产品类项目经理
				"  IFNULL(SUM(IF(e.mlev=6,1,0)),0) mlev6, " + // 非产品类项目经理
				"  IFNULL(SUM(IF(e.lv_num=4 and (e.sp_name = '高级科长' or e.sp_name = '高级车间主任' or e.mlev=7),1,0)),0) mlev7, " + // 高级科长（4.0）=职位为高级科长、高级车间主任的人员; //myh +层级是 高级科长（4.0）20200318
				"  IFNULL(SUM(IF(e.lv_num>4 AND e.lv_num<4.3 and e.hwc_namezl='M类' AND e.sp_name NOT LIKE '%储备%' ,1,0)),0) mlev8 ";// 科长级（4.1-4.2）=（职级为4.1、4.2M类人员)-(职位包含“储备”且职级为4.1、4.2M类人员）
		if (!isnowmonth)
			sqlstr = sqlstr + " FROM hr_month_employee e ";
		else
			sqlstr = sqlstr + " FROM hr_employee e ";
		sqlstr = sqlstr + " WHERE e.empstatid IN(" + empstids + ") ";
		if (!isnowmonth)
			sqlstr = sqlstr + "and e.yearmonth ='" + yearmonth + "' ";
		if (includechld)
			sqlstr = sqlstr + " AND e.idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND e.orgid=" + dw.getString("orgid");
		if ((spname != null) && (!spname.isEmpty())) {
			sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}
		sqlstr = parExtSQL("e", sqlstr);
		JSONObject jo = HRUtil.getReadPool().opensql2json_O(sqlstr).getJSONObject(0);
		return jo;
	}

	// 储备
	private int getcb(JSONObject dw, boolean isnowmonth, String yearmonth, String spname, boolean includechld) throws NumberFormatException, Exception {
		String sqlstr = "select ifnull(count(*),0) ct ";
		if (!isnowmonth)
			sqlstr = sqlstr + " FROM hr_month_employee e ";
		else
			sqlstr = sqlstr + " FROM hr_employee e ";
		sqlstr = sqlstr + " WHERE e.empstatid IN(" + empstids + ") ";
		if (!isnowmonth)
			sqlstr = sqlstr + "and e.yearmonth ='" + yearmonth + "' ";
		if (includechld)
			sqlstr = sqlstr + " AND e.uidpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND e.uorgid=" + dw.getString("orgid");
		sqlstr = sqlstr + " and hwc_namezl='M类' and sp_name LIKE '%储备%'";
		if ((spname != null) && (!spname.isEmpty())) {
			sqlstr = sqlstr + " and e.sp_name like '%" + spname + "%'";
		}
		return Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct"));
	}

	/**
	 * 是否当月
	 * 
	 * @param ym
	 * @return
	 */
	private boolean isNowMonth(String ym) {
		String nym = "'" + Systemdate.getStrDateByFmt(new Date(), "yyyy-MM") + "'";
		System.out.println("nym:" + nym + " ym:" + ym);
		return (nym.equalsIgnoreCase(ym));
	}

	@ACOAction(eventname = "rpt_zwqstj", Authentication = true, notes = "职位趋势分析")
	public String rpt_zwqstj() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		String orgid = CorUtil.getJSONParmValue(jps, "orgid", "需要参数【orgid】");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth_begin", "需要参数【yearmonth_begin】");
		String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");
		boolean includechild = Boolean.valueOf(CorUtil.getJSONParmValue(jps, "includechild", "需要参数【includechild】"));
		String spname = CorUtil.getJSONParmValue(jps, "spname");
		String iswzjq_str = CorUtil.getJSONParmValue(jps, "findtype");
		int findtype = (iswzjq_str == null) ? 1 : Integer.valueOf(iswzjq_str);

		List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 24);
		JSONArray dws = HRUtil.getOrgsByOrgid(orgid, includechild, yearmonths);
		JSONObject rst = findSpQS(dws, orgid, yearmonths, spname, includechild, findtype, parms);
		// dws = HRUtil.getOrgsByOrgid(orgid, false, yearmonths);
		// JSONArray chartdata = buildXLChartData(findEmployeeXLAndQS(dws, orgid, yearmonths, empclass, false, parms).getJSONArray("rows"));
		// rst.put("chartdata", chartdata);
		JSONArray chartdata = buildZWChartData(rst.getJSONArray("rows"));
		rst.put("chartdata", chartdata);
		return rst.toString();

	}

	private JSONArray buildZWChartData(JSONArray rows) throws ParseException {
		JSONArray rst = new JSONArray();
		List<String> ctsers = new ArrayList<String>();
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			String sp_name = row.getString("sp_name");
			if (!ctsers.contains(sp_name))
				ctsers.add(sp_name);
		}
		int i = 0;
		for (String sp_name : ctsers) {
			rst.add(buildZWFChartDateRow(rows, "fd" + (++i), sp_name));
		}
		return rst;
	}

	private TwoInt getTwoIntByInt1(List<TwoInt> ints, int int1) {
		for (TwoInt oint : ints) {
			if (oint.getInt1() == int1)
				return oint;
		}
		return null;
	}

	private JSONObject buildZWFChartDateRow(JSONArray rows, String fdname, String lable) throws ParseException {
		JSONObject ss = new JSONObject();
		ss.put("label", lable);
		// HashMap<Integer, Integer> ymdatas = new HashMap<Integer, Integer>();
		List<TwoInt> ymdatas = new ArrayList<TwoInt>();
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			String spname = row.getString("sp_name");
			if (lable.equalsIgnoreCase(spname)) {
				String yearmonth = row.getString("yearmonth");
				Integer ymint = Integer.valueOf(yearmonth.replace("-", ""));
				int emnum = row.getInt("emnum");
				// 取出数据
				TwoInt oint = getTwoIntByInt1(ymdatas, ymint);
				int ymemnum = ((oint == null) ? 0 : oint.getInt2());
				ymemnum = ymemnum + emnum;
				// 存储数据
				if (oint == null)
					ymdatas.add(new TwoInt(ymint, ymemnum));
				else
					oint.setInt2(ymemnum);
			}
		}

		// 排序
		Collections.sort(ymdatas, new Comparator<TwoInt>() {
			// 升序排序
			public int compare(TwoInt o1, TwoInt o2) {
				if (o1.getInt1() > o2.getInt1())
					return 1;
				else if (o1.getInt1() < o2.getInt1())
					return -1;
				else
					return 0;
			}
		});

		JSONArray ssArray = new JSONArray();
		for (TwoInt oint : ymdatas) {
			JSONArray cd = new JSONArray();
			String dtstr = String.valueOf(oint.getInt1()) + "01";
			Date dt = new SimpleDateFormat("yyyyMMdd").parse(dtstr);
			// Date dt = Systemdate.getDateByStr(yearmonth + "-01");
			dt = Systemdate.dateMonthAdd(dt, 1);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			cd.add(calendar.getTimeInMillis());
			// cd.add();
			cd.add(oint.getInt2());
			ssArray.add(cd);
		}
		ss.put("data", ssArray);
		return ss;
	}

	private JSONObject findSpQS(JSONArray dws, String orgid, List<String> yearmonths, String spname, boolean includechild, int findtype, HashMap<String, String> parms) throws Exception {
		JSONArray rows = new JSONArray();
		Hr_employee he = new Hr_employee();
		int s_sumpeo = 0;
		for (int i = 0; i < dws.size(); i++) {
			JSONObject dw = dws.getJSONObject(i);// 机构 年月
			boolean includechld = (!includechild || (!orgid.equals(dw.getString("orgid"))));// 包含子机构
			// 且
			// 为自身机构时候
			// 不查询子机构数据
			JSONArray cts = getZWQSData(he, dw, dw.getString("yearmonth"), spname, includechld, findtype);
			for (int j = 0; j < cts.size(); j++) {
				JSONObject row = JSONObject.fromObject(dw.toString());

				JSONObject ct = cts.getJSONObject(j);
				int sumpeo = ct.getInt("ct");// getDegreeEmoloyssct(he, dw,
				s_sumpeo += sumpeo;
				row.put("emnum", sumpeo);
				row.put("sp_name", ct.getString("sp_name"));
				rows.add(row);
			}
		}
		JSONObject srow = new JSONObject();
		srow.put("emnum", s_sumpeo);
		srow.put("orgname", "合计");
		JSONArray footer = new JSONArray();
		footer.add(srow);
		JSONObject rst = new JSONObject();
		rst.put("rows", rows);
		rst.put("footer", footer);
		String scols = parms.get("cols");
		if (scols == null) {
			return rst;
		} else {
			(new CReport()).export2excel(rows, scols);
			return null;
		}
	}

	/**
	 * @param he
	 * @param dw
	 * @param ym
	 * @param spname
	 * @param includchd
	 * @param findtype 精确 模糊明细 模糊汇总
	 * @return
	 * @throws Exception
	 */
	private JSONArray getZWQSData(Hr_employee he, JSONObject dw, String ym, String spname, boolean includchd, int findtype) throws Exception {
		String ymstr = "'" + ym + "'";
		boolean isnowmonth = isNowMonth(ymstr);
		String sqlstr = "SELECT ";
		if (isnowmonth)
			sqlstr = sqlstr + "  '" + ym + "' yearmonth,";
		else
			sqlstr = sqlstr + "  yearmonth,";
		if (findtype == 3)
			sqlstr = sqlstr + " '" + spname + "' sp_name,";
		else
			sqlstr = sqlstr + " e.sp_name,";

		sqlstr = sqlstr + " count(*) ct";
		if (isnowmonth)
			sqlstr = sqlstr + " FROM hr_employee e";
		else
			sqlstr = sqlstr + " FROM hr_month_employee e";
		sqlstr = sqlstr + " WHERE e.`empstatid` IN(" + empstids + ") ";

		if (!isnowmonth)
			sqlstr = sqlstr + " and yearmonth=" + ymstr + "";

		if (includchd)
			sqlstr = sqlstr + " AND idpath LIKE '" + dw.getString("idpath") + "%'";
		else
			sqlstr = sqlstr + " AND orgid=" + dw.getString("orgid");
		if ((spname != null) && (!spname.isEmpty())) {
			if (findtype == 1)
				sqlstr = sqlstr + " and sp_name='" + spname + "'";
			else
				sqlstr = sqlstr + " and sp_name like '%" + spname + "%'";
		}

		sqlstr = parExtSQL("e", sqlstr);

		if (findtype != 3)
			sqlstr = sqlstr + " group by e.sp_name";
		return HRUtil.getReadPool().opensql2json_O(sqlstr);
	}

	@ACOAction(eventname = "getadvtechmodes", Authentication = false, notes = "获取高技模块")
	public String getadvtechmodes() throws Exception {
		String sqlstr = "SELECT * FROM hr_employee_advtechmode WHERE usable=1";
		return HRUtil.getReadPool().opensql2json(sqlstr);
	}

	@ACOAction(eventname = "rptadvtech", Authentication = true, notes = "高技报表")
	public String rptadvtech() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		List<JSONParm> jps = CJSON.getParms(parms.get("parms"));
		int menutag = Integer.valueOf(CorUtil.getJSONParmValue(jps, "menutag", "需要参数【menutag】"));
		String advmodename = CorUtil.getJSONParmValue(jps, "advmodename");
		String yearmonth_begin = CorUtil.getJSONParmValue(jps, "yearmonth_begin", "需要参数【yearmonth_begin】");
		String sqlstr = "SELECT * FROM hr_employee_advtechmode WHERE usable=1";
		if ((advmodename != null) && (!advmodename.isEmpty()))
			sqlstr = sqlstr + " and mdname='" + advmodename + "'";
		JSONArray rows = new JSONArray();
		JSONArray advnms = HRUtil.getReadPool().opensql2json_O(sqlstr);
		JSONObject rst = new JSONObject();

		if (menutag == 1) {// 分析
			rows = findMonthAdvTech(yearmonth_begin, advnms);
		} else if (menutag == 2) {// 趋势
			String yearmonth_end = CorUtil.getJSONParmValue(jps, "yearmonth_end", "需要参数【yearmonth_end】");
			List<String> yearmonths = HRUtil.buildYeatMonths(yearmonth_begin, yearmonth_end, 24);
			for (String yearmonth : yearmonths) {
				rows.addAll(findMonthAdvTech(yearmonth, advnms));
			}
			rst.put("chartdata", buildAdvtechChartData(yearmonths, rows));
		} else
			throw new Exception("menutag【" + menutag + "】未定义");
		String scols = parms.get("cols");
		if (scols == null) {
			rst.put("rows", rows);
			return rst.toString();
		} else {
			(new CReport()).export2excel(rows, scols);
			return null;
		}
	}

	private JSONArray buildAdvtechChartData(List<String> yearmonths, JSONArray rows) {
		JSONArray rst = new JSONArray();
		rst.add(buildAdvtechChartDateRow(yearmonths, rows, "zrnum", "主任级"));
		rst.add(buildAdvtechChartDateRow(yearmonths, rows, "fzrnum", "副主任级"));
		rst.add(buildAdvtechChartDateRow(yearmonths, rows, "gjnum", "高级"));
		return rst;
	}

	private JSONObject buildAdvtechChartDateRow(List<String> yearmonths, JSONArray rows, String fdname, String lable) {
		JSONObject ss = new JSONObject();
		ss.put("label", lable);
		JSONArray ssArray = new JSONArray();

		for (String ym : yearmonths) {
			JSONArray cd = new JSONArray();

			Date dt = Systemdate.getDateByStr(ym + "-01");
			dt = Systemdate.dateMonthAdd(dt, 1);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			cd.add(calendar.getTimeInMillis());

			// cd.add(Integer.valueOf(ym.replace("-", "")));
			int sum = 0;
			for (int i = 0; i < rows.size(); i++) {
				JSONObject row = rows.getJSONObject(i);
				String yearmonth = row.getString("yearmonth");
				if (yearmonth.equalsIgnoreCase(ym)) {
					if (row.has(fdname))
						sum += row.getInt(fdname);
				}
			}
			cd.add(sum);
			ssArray.add(cd);
		}
		ss.put("data", ssArray);
		return ss;
	}

	/**
	 * @param yearmonth yyyy-mm
	 * @param advnms
	 * @return
	 * @throws Exception
	 */
	private JSONArray findMonthAdvTech(String yearmonth, JSONArray advnms) throws Exception {
		JSONArray rst = new JSONArray();
		for (int i = 0; i < advnms.size(); i++) {
			JSONObject jo = advnms.getJSONObject(i);
			boolean isnowmonth = Systemdate.getStrDateByFmt(new Date(), "yyyy-MM").equalsIgnoreCase(yearmonth);
			String atmid = jo.getString("atmid");
			String sqlstr = "SELECT '" + yearmonth + "' yearmonth, " +
					"IFNULL(SUM(IF(sp_name LIKE '%主任%' AND sp_name NOT LIKE '%副主任%' ,1,0)),0) zrnum, " +
					"IFNULL(SUM(IF(sp_name LIKE '%副主任%' ,1,0)),0) fzrnum, " +
					"IFNULL(SUM(IF(sp_name LIKE '%高级%' ,1,0)),0) gjnum ";
			if (isnowmonth)
				sqlstr = sqlstr + "FROM hr_employee  WHERE empstatid>0 AND empstatid<10";
			else
				sqlstr = sqlstr + "FROM hr_month_employee  WHERE yearmonth='" + yearmonth + "'";
			sqlstr = sqlstr + " and atmid=" + atmid + " and isadvtch=1";
			JSONObject r = HRUtil.getReadPool().opensql2json_O(sqlstr).getJSONObject(0);
			JSONObject row = JSONObject.fromObject(jo.toString());
			row.put("yearmonth", r.getString("yearmonth"));
			row.put("zrnum", r.getString("zrnum"));
			row.put("fzrnum", r.getString("fzrnum"));
			row.put("gjnum", r.getString("gjnum"));
			row.put("emnum", r.getInt("zrnum") + r.getInt("fzrnum") + r.getInt("gjnum"));
			sqlstr = "SELECT IFNULL(count(*),0) ct "
					+ " FROM `hr_leavejob` hl,hr_employee emp WHERE hl.stat=9 AND hl.ljtype=2 and hl.er_id=emp.er_id and hl.iscanced=2  "
					+ "and DATE_FORMAT(hl.ljdate,'%Y-%m')='" + yearmonth + "' and emp.isadvtch=1 and emp.atmid=" + atmid;
			row.put("leavnum", HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct").toString());
			String pyearmonth = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(Systemdate.getDateByStr(yearmonth + "-01"), -1), "yyyy-MM");
			sqlstr = "SELECT IFNULL(count(*),0) ct "
					+ "FROM hr_month_employee  WHERE yearmonth='" + pyearmonth + "'"
					+ " and atmid=" + atmid + " and isadvtch=1";
			int sumemp = (Integer.valueOf(HRUtil.getReadPool().openSql2List(sqlstr).get(0).get("ct").toString()) + row.getInt("emnum")) / 2;
			float leavper = (sumemp == 0) ? 0 : row.getInt("leavnum") / sumemp;
			if (leavper > 1)
				leavper = 1;
			row.put("leavper", leavper);
			rst.add(row);
		}
		return rst;
	}

}
