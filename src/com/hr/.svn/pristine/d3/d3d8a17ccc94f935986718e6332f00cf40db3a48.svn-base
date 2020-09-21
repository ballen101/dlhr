package com.hr.recruit.co;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CReport;
import com.corsair.server.util.CorUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ACO(coname = "web.hr.recruit.rpt")
public class COReport {

	@ACOAction(eventname = "find_waitfor_disp", Authentication = true, notes = "查询待分配")
	public String find_waitfor_disp() throws Exception {
		String sqlstr = "SELECT e.* FROM hr_recruit_form t,hr_employee e  WHERE t.er_id = e.er_id and e.empstatid=0 and t.chkok=1 ";
		// + "AND NOT EXISTS (SELECT 1 FROM hr_recruit_distribution rd WHERE rd.er_id = t.er_id and rd.stat=9)"; //理论上认识状态为0的就是待分配的
		String[] ignParms = {};
		return new CReport(sqlstr, null).findReport(ignParms);// 不采用IDPATH XX
	}

	// [
	// [
	// {field: 'type', rowspan: 2, title: '类别', width: 50},
	// {field: 'rptdate', title: '招聘手续办理日 ', width: 100},
	// {field: 'fdzg10_03', title: '10-03', width: 50},
	// {field: 'fdzg10_04', title: '10-04', width: 50},
	// {field: 'fdzg10_05', title: '10-05', width: 50},
	// {field: 'fdzg10w1', rowspan: 2, title: 'w1', width: 50},
	// {field: 'fdzg10_06', title: '10-06', width: 50},
	// {field: 'fdzg10_07', title: '10-07', width: 50},
	// {field: 'fdzg10_08', title: '10-08', width: 50},
	// {field: 'fdzg10', rowspan: 2, title: '10月', width: 50}
	// ],
	// [
	// {field: 'rptdate', title: '招聘手续办理日 ', width: 100},
	// {field: 'fdzg10_03', title: '10-04', width: 50},
	// {field: 'orgname3', title: '10-05', width: 50},
	// {field: 'orgname3', title: '10-06', width: 50},
	// {field: 'orgname3', title: '10-07', width: 50},
	// {field: 'orgname3', title: '10-08', width: 50},
	// {field: 'orgname3', title: '10-09', width: 50}
	// ]
	// ];

	@ACOAction(eventname = "getreportfields", Authentication = true, notes = "根据月度获取字段")
	public String getreportfields() throws Exception {
		HashMap<String, String> urlparms = CSContext.get_pjdataparms();
		String yearmonth = CorUtil.hashMap2Str(urlparms, "yearmonth", "需要参数yearmonth");
		Date dtbg = Systemdate.getDateByStr(yearmonth + "-01");
		int mm = Integer.valueOf(Systemdate.getStrDateByFmt(dtbg, "MM"));
		int yyyy = Integer.valueOf(Systemdate.getStrDateByFmt(dtbg, "yyyy"));
		int md = Systemdate.getMothMaxDay(yyyy, mm);
		JSONArray rst = new JSONArray();
		JSONArray row1 = new JSONArray();
		JSONArray row2 = new JSONArray();
		row1.add(JSONObject.fromObject("{field: 'type',halign:'center',rowspan: 2, title: '类别', width: 50}"));
		row1.add(JSONObject.fromObject("{field: 'qunname',halign:'center',rowspan: 2, title: '', width: 40}"));
		row1.add(JSONObject.fromObject("{field: 'itemname',halign:'center', title: '招聘手续办理日', width: 100}"));
		row2.add(JSONObject.fromObject("{field: 'itemname',halign:'center', title: '人事系统入职日', width: 100}"));
		row1.add(JSONObject.fromObject("{field: 'isoktitle',halign:'center', title: '', width: 50}"));
		row2.add(JSONObject.fromObject("{field: 'isoktitle',halign:'center', title: '', width: 50}"));
		Calendar cal = Calendar.getInstance();
		int widx = 0;
		for (int i = 1; i <= md; i++) {
			Date curdate = Systemdate.dateDayAdd(dtbg, i - 1);// 判断当前日期是否星期6
			cal.setTime(curdate);
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;// 0 星期天 ； 1 星期一；2 星期二；3 星期三
			if (w == 0) {// 星期天
				widx++;
				if (i == 1) {// 第一天就是星期天
					continue;
				} else if (i == md) {// 最后一天是星期天
					continue;
				} else {
					row1.add(JSONObject.fromObject("{field: 'w" + widx + "',halign:'center', rowspan: 2, title: 'w" + widx + "', width: 50}"));
				}
			} else {
				row1.add(JSONObject.fromObject("{field: 'fdday" + mm + "_" + i + "',halign:'center', title: '" + mm + "-" + i + " ', width: 50}"));
				Date cdate2 = null;
				if (w == 6) {
					cdate2 = Systemdate.dateDayAdd(curdate, 2);
				} else {
					cdate2 = Systemdate.dateDayAdd(curdate, 1);
				}
				int amm = Integer.valueOf(Systemdate.getStrDateByFmt(cdate2, "MM"));
				int add = Integer.valueOf(Systemdate.getStrDateByFmt(cdate2, "dd"));
				row2.add(JSONObject.fromObject("{field: 'fdday" + mm + "_" + i + "',halign:'center', title: '" + amm + "-" + add + "', width: 50}"));
			}

		}
		row1.add(JSONObject.fromObject("{field: 'month',halign:'center', rowspan: 2, title: '" + mm + "月', width: 80}"));
		rst.add(row1);
		rst.add(row2);
		return rst.toString();

	}

	@ACOAction(eventname = "findmonthrpt", Authentication = true, notes = "招募月报")
	public String findmonthrpt() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();

		String ps = parms.get("parms");
		if (ps == null) {
			throw new Exception("需要选择筛选条件");
		}
		List<JSONParm> jps = CJSON.getParms(ps);
		Calendar cal = Calendar.getInstance();
		JSONParm jpyearmonth = CjpaUtil.getParm(jps, "yearmonth");
		if (jpyearmonth == null)
			throw new Exception("需要参数【yearmonth】");
		String yearmonth = jpyearmonth.getParmvalue();
		String dt1 = yearmonth + "-01";
		Date dtbg = Systemdate.getDateByStr(dt1);
		String dt2 = Systemdate.getStrDateyyyy_mm_dd(Systemdate.dateMonthAdd(dtbg, 1));
		int mm = Integer.valueOf(Systemdate.getStrDateByFmt(dtbg, "MM"));
		int yyyy = Integer.valueOf(Systemdate.getStrDateByFmt(dtbg, "yyyy"));
		int md = Systemdate.getMothMaxDay(yyyy, mm);
		String sqlstr = "SELECT  " +
				"  disday,   " +
				"  SUM(1) zgcj, " +
				"  IFNULL(SUM(CASE WHEN compar_result=1 THEN 1 ELSE 0 END),0) zghg, " +
				"  IFNULL(SUM(CASE WHEN formchk=1 THEN 1 ELSE 0 END),0) formchk, " +
				"  IFNULL(SUM(CASE WHEN cercheck=1 THEN 1 ELSE 0 END),0) cercheck, " +
				"  IFNULL(SUM(CASE WHEN iview=1 AND wtexam=1 THEN 1 ELSE 0 END),0) ivwtexam, " +
				"  IFNULL(SUM(CASE WHEN meexam=1 THEN 1 ELSE 0 END),0) meexam, " +
				"  IFNULL(SUM(CASE WHEN train=1 THEN 1 ELSE 0 END),0) train, " +
				"  IFNULL(SUM(CASE WHEN recruit_distribution_id>0 THEN 1 ELSE 0 END),0) disnum " +
				" FROM  " +
				" (SELECT rq.compar_result,rf.formchk,rf.cercheck,rf.iview,rf.wtexam,rf.meexam,rf.train, " +
				" dr.recruit_distribution_id, DATE_FORMAT(rq.createtime,'%Y-%m-%d') disday " +
				" FROM hr_recruit_quachk rq LEFT JOIN hr_recruit_form rf ON rf.recruit_quachk_id=rq.recruit_quachk_id " +
				" LEFT JOIN hr_recruit_distribution dr ON rq.recruit_quachk_id=dr.recruit_quachk_id " +
				" WHERE rq.createtime>='" + dt1 + "' AND rq.createtime<'" + dt2 + "' ) tb " +
				" GROUP BY disday ";
		JSONArray datas = DBPools.defaultPool().opensql2json_O(sqlstr);
		sqlstr = "select * from hr_recruit_rptconst order by rrcid";
		JSONArray rows = DBPools.defaultPool().opensql2json_O(sqlstr);
		for (int i = 0; i < rows.size(); i++) {
			JSONObject row = rows.getJSONObject(i);
			switch (i) {
			case 0:
				putrowdata(row, datas, dtbg, cal, yyyy, mm, md, "zgcj", null);
				break;
			case 1:
				putrowdata(row, datas, dtbg, cal, yyyy, mm, md, "zghg", null);
				break;
			case 2:
				putrowdata(row, datas, dtbg, cal, yyyy, mm, md, "formchk", null);
				break;
			case 3:
				putrowdata(row, datas, dtbg, cal, yyyy, mm, md, "cercheck", null);
				break;
			case 4:
				putrowdata(row, datas, dtbg, cal, yyyy, mm, md, "ivwtexam", null);
				break;
			case 5:
				putrowdata(row, datas, dtbg, cal, yyyy, mm, md, "meexam", null);
				break;
			case 6:
				putrowdata(row, datas, dtbg, cal, yyyy, mm, md, "train", null);
				break;
			case 7:
				putrowdata(row, datas, dtbg, cal, yyyy, mm, md, "disnum", null);
				break;
			}
		}
		findmonthrptorg(rows, yearmonth);
		String scols = parms.get("cols");
		if (scols == null) {
			return rows.toString();
		} else {
			(new CReport()).export2excel(rows, scols);
			return null;
		}
	}

	/**
	 * 机构数据
	 * 
	 * @param yearmonth
	 * @throws Exception
	 */
	private void findmonthrptorg(JSONArray rows, String yearmonth) throws Exception {
		String dt1 = yearmonth + "-01";
		Date dtbg = Systemdate.getDateByStr(dt1);
		String dt2 = Systemdate.getStrDateyyyy_mm_dd(Systemdate.dateMonthAdd(dtbg, 1));
		int mm = Integer.valueOf(Systemdate.getStrDateByFmt(dtbg, "MM"));
		int yyyy = Integer.valueOf(Systemdate.getStrDateByFmt(dtbg, "yyyy"));
		int md = Systemdate.getMothMaxDay(yyyy, mm);
		Calendar cal = Calendar.getInstance();
		String sqlstr = "SELECT tb2.orgid qunorgid,tb2.orgname qunname,tb1.orgid,tb1.orgname itemname,tb1.idpath,tb2.attribute5 od1,tb1.od2 FROM  " +
				"(SELECT orgid,orgname,superid,idpath,attribute4,attribute5 od2 FROM shworg WHERE superid IN( " +
				"SELECT orgid FROM shworg WHERE superid=1 AND attribute4=1 AND usable=1 " +
				") AND usable=1 ) tb1, " +
				"(SELECT orgid,orgname,attribute5 FROM shworg WHERE superid=1 AND usable=1 AND attribute4=1 ) tb2 " +
				"WHERE tb1.superid=tb2.orgid AND tb1.attribute4=1 " +
				"ORDER BY od1+0,od2+0 ,orgid";
		JSONArray jorgs = DBPools.defaultPool().opensql2json_O(sqlstr);// 机构
		JSONObject sumobj1 = new JSONObject();
		JSONObject sumobj2 = new JSONObject();
		int pqunorgid = 0;
		JSONObject temjorg = null;
		for (int i = 0; i < jorgs.size(); i++) {
			JSONObject jorg = jorgs.getJSONObject(i);
			temjorg = jorg;
			int qunorgid = jorg.getInt("qunorgid");
			if (qunorgid != pqunorgid) {// 需要增加合计
				if (i != 0) {
					sumobj1.put("type", "制造群");
					sumobj1.put("isoktitle", "入系统");
					JSONObject row1 = JSONObject.fromObject(jorgs.get(i - 1));
					putAllJOFields(row1, sumobj1);
					row1.put("itemname", "小计");
					rows.add(row1);
					System.out.println("小计1：" + row1.toString());

					sumobj2.put("type", "制造群");
					sumobj2.put("isoktitle", "发厂牌");
					JSONObject row2 = JSONObject.fromObject(jorgs.get(i - 1));
					putAllJOFields(row2, sumobj2);
					row2.put("itemname", "小计");
					rows.add(row2);
					System.out.println("小计2：" + row2.toString());

					sumobj1.clear();
					sumobj2.clear();
				}
			}
			pqunorgid = qunorgid;

			sqlstr = "SELECT disday,   " +
					"  IFNULL(SUM(CASE WHEN recruit_id IS NOT NULL THEN 1 ELSE 0 END),0) regnum, " +
					"  IFNULL(SUM(CASE WHEN quachk=1 and chkok=1 THEN 1 ELSE 0 END),0) sendcardnum " +
					"FROM  " +
					"(SELECT rq.compar_result,rf.recruit_id,rf.quachk,rf.chkok,DATE_FORMAT(rq.createtime,'%Y-%m-%d') disday " +
					"FROM hr_recruit_quachk rq , hr_recruit_form rf, hr_employee e " +
					"WHERE rf.er_id=e.er_id AND rf.recruit_quachk_id=rq.recruit_quachk_id  " +
					"AND rq.createtime>='" + dt1 + "' AND rq.createtime<'" + dt2 + "' " +
					"AND e.idpath LIKE '" + jorg.getString("idpath") + "%' " +
					") tb " +
					"GROUP BY disday";
			JSONArray datas = DBPools.defaultPool().opensql2json_O(sqlstr);// 数据
			setDatas(rows, datas, jorg, dtbg, cal, yyyy, mm, md, sumobj1, sumobj2);
		}
		if (temjorg != null) {
			sumobj1.put("type", "制造群");
			sumobj1.put("isoktitle", "入系统");
			JSONObject row1 = JSONObject.fromObject(temjorg);
			putAllJOFields(row1, sumobj1);
			row1.put("itemname", "小计");
			rows.add(row1);
			System.out.println("小计1：" + row1.toString());

			sumobj2.put("type", "制造群");
			sumobj2.put("isoktitle", "发厂牌");
			JSONObject row2 = JSONObject.fromObject(temjorg);
			putAllJOFields(row2, sumobj2);
			row2.put("itemname", "小计");
			rows.add(row2);
			System.out.println("小计2：" + row2.toString());
		}

	}

	private void putAllJOFields(JSONObject djo, JSONObject sjo) {
		Iterator<String> it = sjo.keys();
		while (it.hasNext()) {
			// 获得key
			String key = it.next();
			djo.put(key, sjo.getString(key));
		}
	}

	private void setDatas(JSONArray rows, JSONArray datas, JSONObject jorg, Date dtbg, Calendar cal, int yyyy, int mm, int md, JSONObject sumobj1, JSONObject sumobj2) {
		JSONObject row1 = JSONObject.fromObject(jorg.toString());
		putrowdata(row1, datas, dtbg, cal, yyyy, mm, md, "regnum", sumobj1);
		row1.put("type", "制造群");
		row1.put("isoktitle", "入系统");
		rows.add(row1);

		JSONObject row2 = JSONObject.fromObject(jorg.toString());
		putrowdata(row2, datas, dtbg, cal, yyyy, mm, md, "sendcardnum", sumobj2);
		row2.put("type", "制造群");
		row2.put("isoktitle", "发厂牌");
		rows.add(row2);
	}

	private void putrowdata(JSONObject row, JSONArray datas, Date dtbg, Calendar cal, int yyyy, int mm, int md, String dataname, JSONObject sumobj) {
		// System.out.println(row.toString());
		int widx = 0, wct = 0, mct = 0;
		for (int i = 1; i <= md; i++) {
			String fdname = "fdday" + mm + "_" + i;
			Date curdate = Systemdate.dateDayAdd(dtbg, i - 1);// 判断当前日期是否星期
			cal.setTime(curdate);
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;// 0 星期天 ； 1 星期一；2 星期二；3 星期三
			if (w == 0) {// 合计周
				// System.out.println("value :" + wct);
				widx++;
				if (i == 1) {// 第一天就是星期天
					continue;
				} else if (i == md) {// 最后一天是星期天
					continue;
				} else {
					row.put("w" + widx, wct);
					if (sumobj != null) {
						int s = 0;
						if (sumobj.has("sw" + widx)) {
							s = sumobj.getInt("sw" + widx);
						}
						// System.out.println("fdname:" + "w" + widx + " value :" + (s + wct));
						sumobj.put("w" + widx, s + wct);
					}
				}
				wct = 0;
			} else {
				int ct = Integer.valueOf(getdatavalue(datas, yyyy, mm, i, dataname));
				wct = wct + ct;
				mct = mct + ct;
				row.put(fdname, ct);
				if (sumobj != null) {
					int s = 0;
					if (sumobj.has(fdname)) {
						s = sumobj.getInt(fdname);
					}
					sumobj.put(fdname, s + wct);
				}
			}
		}
		if (sumobj != null) {
			int s = 0;
			if (sumobj.has("month")) {
				s = sumobj.getInt("month");
			}
			sumobj.put("month", s + mct);
		}
		row.put("month", mct);
	}

	/**
	 * @param datas  二维数据
	 * @param mm     月
	 * @param dd     日
	 * @param fdname 取的字段名
	 * @return
	 */
	private String getdatavalue(JSONArray datas, int yyyy, int mm, int dd, String dataname) {
		String sdt = Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(yyyy + "-" + mm + "-" + dd));
		for (int i = 0; i < datas.size(); i++) {
			JSONObject data = datas.getJSONObject(i);
			if (data.has("disday")) {
				if (sdt.equalsIgnoreCase(data.getString("disday"))) {
					return (data.has(dataname)) ? data.getString(dataname) : "0";
				}
			}
		}
		return "0";
	}

	@ACOAction(eventname = "rpt_recruit_quachk", Authentication = true, notes = "应聘人员异常分析")
	public String rpt_recruit_quachk() throws Exception {
		String sqlstr = "SELECT rq.*,DATE_FORMAT(rq.createtime,'%Y-%m-%d') createday,DATE_FORMAT(rq.createtime,'%Y-%m') yearmonth " +
				"FROM hr_recruit_quachk rq WHERE rq.stat=9 and compar_result<>1 ORDER BY createtime DESC";
		return new CReport(sqlstr, null).findReport();
	}

}
